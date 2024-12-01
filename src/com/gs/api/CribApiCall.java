package com.gs.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.gs.encrypt.CribEncryption;
import com.gs.liveInvoke.LiveRequestInvoker;
import com.gs.liveInvoke.LiveRequestInvokerSoap;
import com.gs.request.MultiHitRequestBean;
import com.gs.request.MultiHitRequestXmlGenerator;
import com.gs.request.RequestBean;
import com.gs.request.SingleHitRequestBean;
import com.gs.request.SingleHitRequestXmlGenerator;
import com.gs.response.ResponseBean;
import com.gs.response.Status;
import com.gs.util.DbOperations;
import com.gs.util.PropertyReader;
import com.gs.util.XMLTagsBean;

public class CribApiCall {
	
	private static Properties prop;
	private static String cribResponseFile;
	public static Logger log = Logger.getLogger(CribApiCall.class);
	
	public CribApiCall(){
		try{
			PropertyReader pr = new PropertyReader();
	    	prop = pr.loadPropertyFile();
	    	
			String pathSep = System.getProperty("file.separator");
	        String logpath = prop.getProperty("LOG4J_FILE_PATH");
	        String activityRoot = prop.getProperty("LOG_PATH");
			String logPropertyFile =logpath+pathSep+"log4j.properties"; 
	
			PropertyConfigurator.configure(logPropertyFile);
			PropertyReader.loadLogConfiguration(logPropertyFile, activityRoot+"/CribApiCall/", "CribApiCall.log");
			
			cribResponseFile = prop.getProperty("CRIB_RESPONSE_FILE");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error CribApiCall : " +e.fillInStackTrace());
			log.info("Error CribApiCall : " +e.fillInStackTrace());
		}	
	}
	
	public String cribApiInvoke(int requestDetailId, String requestJson, String requestType, String username, String apiUsername, String apiPassword, String encryptionKey, String decryptionKey, String xmlTagsJson) {
		
		DbOperations dbOperations = null;
		Connection conn = null;
		RequestBean requestObj = null;
		String requestXml = null;
		String responseType = null;
		List<ResponseBean> responseBeanList = new ArrayList<ResponseBean>();
		
		String bureauRequestId = null;
		
		Savepoint savePoint = null;
					
		try {
			
			dbOperations = new DbOperations();
			conn = dbOperations.getConnection(encryptionKey);
			ResponseBean responseBean = new ResponseBean();
			
			if(conn!=null) {
			
				if(requestType.equalsIgnoreCase("M")) {
					MultiHitRequestBean multiHitRequestObj = new Gson().fromJson(requestJson, MultiHitRequestBean.class);	
					MultiHitRequestXmlGenerator reqXmlGen = new MultiHitRequestXmlGenerator();
					requestXml = reqXmlGen.generateReqXml(multiHitRequestObj);
				}else {
					SingleHitRequestBean singleHitRequestObj = new Gson().fromJson(requestJson, SingleHitRequestBean.class);	
					SingleHitRequestXmlGenerator reqXmlGen = new SingleHitRequestXmlGenerator();
					requestXml = reqXmlGen.generateReqXml(singleHitRequestObj);
				}
				requestObj = new Gson().fromJson(requestJson, RequestBean.class);
				log.info("RequestXml : "+requestXml);
				
				LiveRequestInvoker client = new LiveRequestInvoker();
				LiveRequestInvokerSoap soapClient = client.getLiveRequestInvokerSoap();
				
				CribEncryption cribEncryptor = new CribEncryption();
				String sRequestXml = cribEncryptor.encryptorSHA(requestXml, encryptionKey);
				String sUserName = cribEncryptor.encryptorSHA(apiUsername, encryptionKey);
				String sPassword = cribEncryptor.encryptorSHA(apiPassword, encryptionKey);
	        
				// Sending SOAP Request to CRIB API.
				log.info("Sending SOAP request to H2H CRIB API...");
				String sResponseXml = soapClient.postRequest(sRequestXml,sUserName,sPassword);
				log.info("Response received from H2H CRIB API.");
				//log.info("Encrypted ResponseXml : "+sResponseXml);
				//createCribResponseFile(sResponseXml, requestObj.getRequestId());
				dbOperations.insertH2HResponseToDB(sResponseXml, requestDetailId, username, conn);
				String responseXml = cribEncryptor.decryptorSHA(sResponseXml, decryptionKey);
	
	// ============== If Consumer ==============//		
				if(requestObj.getSubjectType().equals("1")) {
					
					ConRspOperations conRsOp = new ConRspOperations();
					Document doc = convertStringToXMLDocument(responseXml);
								
					responseType = getResposeType(doc);
					if(responseType.equals("singleHit")) {		
						
						XMLTagsBean xmlTagsObj = new Gson().fromJson(xmlTagsJson, XMLTagsBean.class);
						
						savePoint = conn.setSavepoint();
						conRsOp.insrtDemographicDetails(doc, xmlTagsObj.getConsumerDetails(), requestDetailId, username, conn);					
						conRsOp.insrtIdentificationDetails(doc, xmlTagsObj.getIdentificationDetails(), requestDetailId, username, conn);
						conRsOp.insrtMailingAddresses(doc, xmlTagsObj.getMailingAddresses(), requestDetailId, username, conn);
						conRsOp.insrtPermanentAddresses(doc, xmlTagsObj.getPermanentAddresses(), requestDetailId, username, conn);
						conRsOp.insrtReportedNames(doc, xmlTagsObj.getReportedNames(), requestDetailId, username, conn);
						conRsOp.insrtEmploymentDetails(doc, xmlTagsObj.getEmploymentDetails(), requestDetailId, username, conn);
						conRsOp.insrtRelationshipDetails(doc, xmlTagsObj.getRelationships(), requestDetailId, username, conn);
						conRsOp.insrtRelationshipAddressDetails(doc, xmlTagsObj.getRelationshipAddresses(), requestDetailId, username, conn);
						conRsOp.insrtSettledCFDetails(doc, xmlTagsObj.getSettledCFDetails(), requestDetailId, username, conn);					
						conRsOp.insrtSettledCFSummary(doc, xmlTagsObj.getSettledCFSummary(), requestDetailId, username, conn);
						conRsOp.insrtLendingInstInquiries(doc, xmlTagsObj.getLendingInstInquiries(), requestDetailId, username, conn);
						conRsOp.insrtInquiriesBySubject(doc, xmlTagsObj.getInqBySubject(), requestDetailId, username, conn);
						conRsOp.insrtCreditFacility(doc, xmlTagsObj.getCreditFacility(), requestDetailId, username, conn);
						conRsOp.insrtCreditFacilityDetails(doc, xmlTagsObj.getcFDetails(), requestDetailId, username, conn);
						conRsOp.insrtLast24Months(doc, xmlTagsObj.getLast24Months(), requestDetailId, username, conn);
						conRsOp.insrtCFForLast24Months(doc, xmlTagsObj.getCfFor24Months(), requestDetailId, username, conn);
						conRsOp.insrtDisputeDetails(doc, xmlTagsObj.getDisputeDetails(), requestDetailId, username, conn);
						conRsOp.insrtPotAndCurrLiabilitiesHeader(doc, xmlTagsObj.getPotAndCurrLiabHeader(), requestDetailId, username, conn);
						conRsOp.insrtPotAndCurrLiabilities(doc, xmlTagsObj.getPotAndCurrLiabilities(), requestDetailId, username, conn);
						conRsOp.insrtCFOfGlanceStatus(doc, xmlTagsObj.getcFOfGlanceStatus(), requestDetailId, username, conn);
						conRsOp.insrtDishonChequeSummary(doc, xmlTagsObj.getDishonChequeSummary(), requestDetailId, username, conn);
						conRsOp.insrtDishonChequeDetails(doc, xmlTagsObj.getDishonChequeDetails(), requestDetailId, username, conn);
						conRsOp.insrtCatalogueDescription(doc, xmlTagsObj.getCatalogueDescription(), requestDetailId, username, conn);
						conRsOp.insrtSearchResults(doc, requestDetailId, username, conn);						
																
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "1", "1", Status.SINGLE_HIT, "-", "-", requestObj.getPerHitCost().getHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getHit());
						responseBean.setStatusCode(Status.SINGLE_HIT.getStatusCode());
						responseBean.setStatus(Status.SINGLE_HIT.getStatus());
						responseBean.setSearchResults(conRsOp.getSearchResults(doc));
						responseBeanList.add(responseBean);
						
					}else if(responseType.equals("multiHit")) {

						bureauRequestId = doc.getDocumentElement().getAttribute("REFERENCE-NO");
						conRsOp.insrtSearchResults(doc, requestDetailId, username, conn);						
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "1", Status.MULTI_HIT, "-", "-", requestObj.getPerHitCost().getMultiHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getMultiHit());
						responseBean.setStatusCode(Status.MULTI_HIT.getStatusCode());
						responseBean.setStatus(Status.MULTI_HIT.getStatus());
						responseBean.setMultiHit(conRsOp.getMultiHitResponse(doc));
						responseBeanList.add(responseBean);
						
					}else if(responseType.equals("noHit")) {
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "0", Status.NO_HIT, "-", "-", requestObj.getPerHitCost().getNoHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getNoHit());
						responseBean.setStatusCode(Status.NO_HIT.getStatusCode());
						responseBean.setStatus(Status.NO_HIT.getStatus());
						responseBeanList.add(responseBean);
					}else {	
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						String errorCode = (doc.getElementsByTagName("ERROR-CODE").getLength() > 0) ? doc.getElementsByTagName("ERROR-CODE").item(0).getTextContent() : "-";
						String error = (doc.getElementsByTagName("ERROR").getLength() > 0) ? doc.getElementsByTagName("ERROR").item(0).getTextContent() : "-";
						
						conRsOp.insrtRequestErrors(doc, requestDetailId, username, conn);
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "0", Status.ERROR_HIT, errorCode, error, "0", conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setErrorCode(errorCode);
						responseBean.setError(error);
						responseBean.setStatusCode(Status.ERROR_HIT.getStatusCode());
						responseBean.setStatus(Status.ERROR_HIT.getStatus());
						responseBeanList.add(responseBean);
					}	
									
	// ============== If Corporate ==============//		
				}else if(requestObj.getSubjectType().equals("0")) {
					
					CorpRspOperations corpRsOp = new CorpRspOperations();
					Document doc = convertStringToXMLDocument(responseXml);
									
					responseType = getResposeType(doc);
					if(responseType.equals("singleHit")) {
						
						XMLTagsBean xmlTagsObj = new Gson().fromJson(xmlTagsJson, XMLTagsBean.class);
						
						savePoint = conn.setSavepoint();
						corpRsOp.insrtFirmographicDetails(doc, xmlTagsObj.getCommercialDetails(), requestDetailId, username, conn);
						corpRsOp.insrtIdentificationDetails(doc, xmlTagsObj.getIdentificationDetails(), requestDetailId, username, conn);
						corpRsOp.insrtMailingAddresses(doc, xmlTagsObj.getMailingAddresses(), requestDetailId, username, conn);
						corpRsOp.insrtPermanentAddresses(doc, xmlTagsObj.getPermanentAddresses(), requestDetailId, username, conn);
						corpRsOp.insrtReportedNames(doc, xmlTagsObj.getReportedNames(), requestDetailId, username, conn);
						corpRsOp.insrtRelationshipDetails(doc, xmlTagsObj.getRelationships(), requestDetailId, username, conn);
						corpRsOp.insrtRelationshipAddressDetails(doc, xmlTagsObj.getRelationshipAddresses(), requestDetailId, username, conn);
						corpRsOp.insrtSettledCFDetails(doc, xmlTagsObj.getSettledCFDetails(), requestDetailId, username, conn);
						corpRsOp.insrtSettledCFSummary(doc, xmlTagsObj.getSettledCFSummary(), requestDetailId, username, conn);
						corpRsOp.insrtLendingInstInquiries(doc, xmlTagsObj.getLendingInstInquiries(), requestDetailId, username, conn);
						corpRsOp.insrtInquiriesBySubject(doc, xmlTagsObj.getInqBySubject(), requestDetailId, username, conn);
						corpRsOp.insrtCreditFacility(doc, xmlTagsObj.getCreditFacility(), requestDetailId, username, conn);
						corpRsOp.insrtCreditFacilityDetails(doc, xmlTagsObj.getcFDetails(), requestDetailId, username, conn);
						corpRsOp.insrtLast24Months(doc, xmlTagsObj.getLast24Months(), requestDetailId, username, conn);
						corpRsOp.insrtCFForLast24Months(doc, xmlTagsObj.getCfFor24Months(), requestDetailId, username, conn);
						corpRsOp.insrtDisputeDetails(doc, xmlTagsObj.getDisputeDetails(), requestDetailId, username, conn);
						corpRsOp.insrtPotAndCurrLiabilitiesHeader(doc, xmlTagsObj.getPotAndCurrLiabHeader(), requestDetailId, username, conn);
						corpRsOp.insrtPotAndCurrLiabilities(doc, xmlTagsObj.getPotAndCurrLiabilities(), requestDetailId, username, conn);
						corpRsOp.insrtCFOfGlanceStatus(doc, xmlTagsObj.getcFOfGlanceStatus(), requestDetailId, username, conn);
						corpRsOp.insrtDishonChequeSummary(doc, xmlTagsObj.getDishonChequeSummary(), requestDetailId, username, conn);
						corpRsOp.insrtDishonChequeDetails(doc, xmlTagsObj.getDishonChequeDetails(), requestDetailId, username, conn);
						corpRsOp.insrtEconActivityHistory(doc, xmlTagsObj.getEconActivityHistory(), requestDetailId, username, conn);
						corpRsOp.insrtCatalogueDescription(doc, xmlTagsObj.getCatalogueDescription(), requestDetailId, username, conn);
						corpRsOp.insrtSearchResults(doc, requestDetailId, username, conn);
						
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "1", "1", Status.SINGLE_HIT, "-", "-", requestObj.getPerHitCost().getHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getHit());
						responseBean.setStatusCode(Status.SINGLE_HIT.getStatusCode());
						responseBean.setStatus(Status.SINGLE_HIT.getStatus());
						responseBean.setSearchResults(corpRsOp.getSearchResults(doc));
						responseBeanList.add(responseBean);
						
					}else if(responseType.equals("multiHit")) {
						bureauRequestId = doc.getDocumentElement().getAttribute("REFERENCE-NO");
						corpRsOp.insrtSearchResults(doc, requestDetailId, username, conn);
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "1", Status.MULTI_HIT, "-", "-", requestObj.getPerHitCost().getMultiHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getMultiHit());
						responseBean.setStatusCode(Status.MULTI_HIT.getStatusCode());
						responseBean.setStatus(Status.MULTI_HIT.getStatus());
						responseBean.setMultiHit(corpRsOp.getMultiHitResponse(doc));
						responseBeanList.add(responseBean);						
						
					}else if(responseType.equals("noHit")) {
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "0", Status.NO_HIT, "-", "-", requestObj.getPerHitCost().getNoHit(), conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setPerHitCost(requestObj.getPerHitCost().getNoHit());
						responseBean.setStatusCode(Status.NO_HIT.getStatusCode());
						responseBean.setStatus(Status.NO_HIT.getStatus());
						responseBeanList.add(responseBean);
					}else {	
						bureauRequestId = doc.getDocumentElement().getAttribute("REQUEST-ID");
						String errorCode = (doc.getElementsByTagName("ERROR-CODE").getLength() > 0) ? doc.getElementsByTagName("ERROR-CODE").item(0).getTextContent() : "-";
						String error = (doc.getElementsByTagName("ERROR").getLength() > 0) ? doc.getElementsByTagName("ERROR").item(0).getTextContent() : "-";
						
						corpRsOp.insrtRequestErrors(doc, requestDetailId, username, conn);
						updateRequestDetailsPstmt(requestDetailId, bureauRequestId, "0", "0", Status.ERROR_HIT, errorCode, error, "0", conn);
						conn.commit();
						
						// Setting ResponseBean
						responseBean.setErrorCode(errorCode);
						responseBean.setError(error);
						responseBean.setStatusCode(Status.ERROR_HIT.getStatusCode());
						responseBean.setStatus(Status.ERROR_HIT.getStatus());
						responseBeanList.add(responseBean);
					}	
					
				}
				log.info("#--------------------------------- Request : "+requestDetailId+" completed ---------------------------------#");
				
			}else {
	
				// Setting ResponseBean
				responseBean.setStatusCode(Status.APPLICATION_ERROR.getStatusCode());
				responseBean.setStatus(Status.APPLICATION_ERROR.getStatus());
				responseBean.setErrorCode(Status.APPLICATION_ERROR.getStatusCode());
				responseBean.setError("Unable to connect database.");
				responseBeanList.add(responseBean);	
				
			}
			
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			e.printStackTrace();
			log.info("Exception occured : " +sw.toString());
			
			String perHitCost = "0";
			Status status = Status.API_ERROR;	
			
			if(conn!=null) {
				if(savePoint!=null){
					try {
						System.out.println("Preparing roll back...");
						log.info("Preparing roll back...");	
						conn.rollback(savePoint);
						log.info("Roll back completed.");	
					} catch (SQLException e1) {
						e.printStackTrace();
						System.out.println("Rolling back failed : " +e.fillInStackTrace());
						log.info("Rolling back failed : " +e.fillInStackTrace());				
					}			
				}
				
				String isHit = "0";
				String isCribAvailable = "0";
								
				if(responseType != null) {
					if(responseType.equals("singleHit")) {
						isHit = "1";	
						//isCribAvailable = "1";
						perHitCost = requestObj.getPerHitCost().getHit();
					}else if(responseType.equals("multiHit")) {
						isHit = "1";	
						perHitCost = requestObj.getPerHitCost().getMultiHit();
					}else if(responseType.equals("noHit")) {
						isHit = "1";	
						perHitCost = requestObj.getPerHitCost().getNoHit();
					}
					
					status = Status.APPLICATION_ERROR;						
				}
				insertException(requestDetailId, bureauRequestId, isCribAvailable, isHit, status, status.getStatusCode(), e.fillInStackTrace().toString(), perHitCost, conn);				
			}
			
			ResponseBean responseBean = new ResponseBean();
			responseBean.setPerHitCost(perHitCost);
			responseBean.setStatusCode(status.getStatusCode());
			responseBean.setStatus(status.getStatus());
			responseBean.setErrorCode(status.getStatusCode());
			responseBean.setError("Exception occured : " +e.fillInStackTrace());
			responseBeanList.add(responseBean);		
					
		}finally {
			if(dbOperations != null){
				// Disconnecting from DB.
				dbOperations.closeConnection(conn);
			}			
		}
		
		return new Gson().toJson(responseBeanList);
	}
	
	// Convert XML string to XML document.
	private static Document convertStringToXMLDocument(String xmlString) {
		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try{
            builder = factory.newDocumentBuilder();
            xmlString = sanitizeXmlChars(xmlString);
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            doc.getDocumentElement().normalize();
            return doc;
        }catch(Exception e){
            e.printStackTrace();
            log.info("Error convertStringToXMLDocument : Error occured while converting H2H response String to XML document");
            log.info("Error convertStringToXMLDocument : " +e.fillInStackTrace());
        }
        return null;
    }
	
	// Clear XML (Remove invalid characters)
	private static String sanitizeXmlChars(String xmlString) {
		
	    if (xmlString == null) {
	    	return null;
	    }else if(xmlString.equals("")) {
	    	return "";
	    }else {
	    	Pattern xmlInvalidChars = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\x{10000}-\\x{10FFFF}]");
	    	return xmlInvalidChars.matcher(xmlString).replaceAll("").replaceAll("&(?!amp;)", "&amp;");
	    }
	}
	
	// Return response type
	private String getResposeType(Document doc) {
		
		String responseType = null;			
		String responseTypeCode = doc.getElementsByTagName("RESPONSE-TYPE").item(0).getAttributes().item(0).getTextContent();
		System.out.println("ResponseTypeCode: "+responseTypeCode);

		if(responseTypeCode.equals("1")){
			responseType = "singleHit";
		}else if(responseTypeCode.equals("2")){
			responseType = "noHit";
		}else if(responseTypeCode.equals("3")){
			responseType = "multiHit";
		}else{
			responseType = "error";
		}
        
        System.out.println("ResponseType: "+responseType);
        log.info("ResponseType: "+responseType);
		return responseType;
	}
			
	//================ Updating Request Details ================//
	private void updateRequestDetailsPstmt(int requestDetailId, String bureauReferenceNo, String isCribAvailable, String isHit, Status status, String errorCode, String error, String perHitCost, Connection conn) throws SQLException {
  		
		String compiledQuery = "UPDATE CRIB_REQUEST_DETAIL SET BUREAU_REFERENCE_NO=?, IS_CRIB_AVAILABLE=?, IS_HIT=?, STATUS_CODE=?, STATUS_DESCRIPTION=?, STATUS=?, ERROR_CODE=?, ERROR=?, SOURCE_NAME=?, REPORT_PATH=?, PER_HIT_COST=?, REPORT_ID=?, IS_ACTIVE=?, MODIFIED=?, MODIFIED_BY=? WHERE ID=?";
		PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
		pstmt.setString(1, bureauReferenceNo);
		pstmt.setString(2, isCribAvailable);
		pstmt.setString(3, isHit);
		pstmt.setString(4, status.getStatusCode());
		pstmt.setString(5, status.getStatus());
		pstmt.setString(6, "CRIB_COMPLETED");
		pstmt.setString(7, errorCode);
		pstmt.setString(8, error);
		pstmt.setString(9, "API");
		pstmt.setString(10, "N/A");
	    pstmt.setString(11, perHitCost);	
	    pstmt.setInt(12, requestDetailId);
	    pstmt.setInt(13, 1);
	    pstmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
	    pstmt.setString(15, "API");
	    pstmt.setInt(16, requestDetailId);
		    
	    pstmt.execute();
		pstmt.close();
	}
	
	//================ Insert Exception to DB ================//
	private void insertException(int requestDetailId, String bureauReferenceNo, String isCribAvailable, String isHit, Status status, String errorCode, String error, String perHitCost, Connection conn) {
	  		
		String compiledQuery = "UPDATE CRIB_REQUEST_DETAIL SET BUREAU_REFERENCE_NO=?, IS_CRIB_AVAILABLE=?, IS_HIT=?, STATUS_CODE=?, STATUS_DESCRIPTION=?, STATUS=?, ERROR_CODE=?, ERROR=?, SOURCE_NAME=?, REPORT_PATH=?, PER_HIT_COST=?, IS_ACTIVE=?, MODIFIED=?, MODIFIED_BY=? WHERE ID=?";

		try{
			System.out.println("Error added");
			
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
				
			pstmt.setString(1, bureauReferenceNo);
			pstmt.setString(2, isCribAvailable);
			pstmt.setString(3, isHit);
			pstmt.setString(4, status.getStatusCode());
			pstmt.setString(5, status.getStatus());
			pstmt.setString(6, "CRIB_COMPLETED");
			pstmt.setString(7, errorCode);
			pstmt.setString(8, error);
			pstmt.setString(9, "API");
			pstmt.setString(10, "N/A");
		    pstmt.setString(11, perHitCost);	
		    pstmt.setInt(12, 1);
		    pstmt.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
		    pstmt.setString(14, "API");
		    pstmt.setInt(15, requestDetailId);
				    
		    pstmt.execute();
		    conn.commit();
			pstmt.close();
			
		}catch(SQLException se){
			se.printStackTrace();
			log.info("Error : " +se.fillInStackTrace());
		}
	}
	
	private void createCribResponseFile(String response, String requestId) {
		try {
			Date today = new Date();
			int day = today.getDate();
			int month = today.getMonth()+1;
			int year = today.getYear()+1900;
			String writeFolder = cribResponseFile+"//"+year+"//"+String.format("%02d", month)+"//"+String.format("%02d", day)+"//";
			File dir = new File(writeFolder);
			if (!dir.exists()) {
			    dir.mkdirs();
			}
			
			RandomAccessFile stream = new RandomAccessFile(writeFolder+requestId+".txt", "rw");
		    FileChannel channel = stream.getChannel();
		    byte[] strBytes = response.getBytes();
		    ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
		    buffer.put(strBytes);
		    buffer.flip();
		    channel.write(buffer);
		    stream.close();
		    channel.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
            log.info("Error createCribResponseFile : " +e.fillInStackTrace());
	    }
		
	}
	
	public static void main(String[] args) {
		
		CribApiCall cribApiCall = new CribApiCall();	
		String requestJson = "{'perHitCost':{'hit':'125', 'noHit':'75', 'multiHit':'75', 'error':'0'},'isApplicationProvided':'TRUE','requestId':'123','reportId':'123','subjectType':'0','responseType':'0','reasonCode':'60','product':'060','number':'s123','date':'23/Jan/1997','amount':'100','currency':'LKR','name':'saman','accountNumber':'sad','dataProviderBrnId':'branch','citizenship':'001','nic':'680270058X','passportNumber':'','brNumber':'','gender':'001','dob':'23/Jan/1997'}";
		//String conXmlTagsJson = "{'consumerDetails' : 'DATAPACKET/BODY/CONSUMER_PROFILE_VER4/CONSUMER_DETAILS_VER4', 'identificationDetails' : 'DATAPACKET/BODY/CONSUMER_PROFILE_VER4/CONSUMER_DETAILS_VER4/IDENTIFICATION_DETAILS_VER4', 'mailingAddresses' : 'DATAPACKET/BODY/CONSUMER_ADDRESS_VER4/MAILING_ADDRESS_VER4', 'permanentAddresses' : 'DATAPACKET/BODY/CONSUMER_ADDRESS_VER4/PERMANENT_ADDRESS_VER4', 'reportedNames' : 'DATAPACKET/BODY/CONSUMER_NORMAL_NAMES_VER4/NAMES_VER4', 'employmentDetails' : 'DATAPACKET/BODY/CONSUMER_EMPLOYMENT_VER4/EMPLOYMENT_DETAILS_VER4', 'relationships' : 'DATAPACKET/BODY/CONSUMER_RELATIONSHIPS_VER4/RELATIONSHIP_DETAILS_VER4', 'settledCFDetails' : 'DATAPACKET/BODY/CONSUMER_SETTLED_CREDIT_SUMMARY_DETAILS_VER1/CONS_SETTLED_SUMMARY_DETAILS_VER1', 'settledCFSummary' : 'DATAPACKET/BODY/CONSUMER_SETTLED_CREDIT_SUMMARY_VER4/CONS_SETTLED_SUMMARY_VER4', 'lendingInstInquiries' : 'DATAPACKET/BODY/CONSUMER_INQUIRY_VER4/INQUIRY_DETAILS_VER4', 'inqBySubject' : 'DATAPACKET/BODY/CONSUMER_INQUIRY_VER4/INQUIRY_DETAILS_VER4', 'cFDetails' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/CREDIT_DETAILS_VER4', 'last24Months' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/MONTH_YEAR_VER4', 'cfFor24Months' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/MONTH_YEAR_VER4/CONSUMER_HISTORY_SUMMARY_VER4', 'disputeDetails' : 'DATAPACKET/BODY/CONSUMER_CF_DISPUTE_VER4', 'potAndCurrLiabilities' : 'DATAPACKET/BODY/CREDIT_SUMMARY_VER4/CURRENCY_VER4/SUMMARY_VER4', 'cFOfGlanceStatus' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_STATUS_VER4/CONSUMER_CREDIT_FACILITY_STATUS_VER4', 'dishonChequeSummary' : 'DATAPACKET/BODY/CONSUMER_DC_SUMMARY_VER4/DISHONOURED_CHEQUE_SUMM_VER4', 'relationshipAddresses' : 'DATAPACKET/BODY/hh', 'creditFacility' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4', 'potAndCurrLiabHeader' : 'DATAPACKET/BODY/CREDIT_SUMMARY_VER4/CURRENCY_VER4', 'dishonChequeDetails' : 'DATAPACKET/BODY/CONSUMER_DC_DETAILS_VER4/DC_DETAILS_VER4', 'catalogueDescription' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_CATALOGUE_VER4/CONSUMER_CREDIT_DETAILS_CATALOGUE_VER4'}";
		String corpXmlTagsJson = "{'commercialDetails' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE_VER4/COMMERCIAL_DETAILS_VER4', 'identificationDetails' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE_VER4/COMMERCIAL_DETAILS_VER4/IDENTIFICATION_DETAILS_VER4', 'mailingAddresses' : 'DATAPACKET/BODY/COMMERCIAL_ADDRESS_VER4/MAILING_ADDRESS_VER4', 'permanentAddresses' : 'DATAPACKET/BODY/COMMERCIAL_ADDRESS_VER4/PERMANENT_ADDRESS_VER4', 'reportedNames' : 'DATAPACKET/BODY/COMMERCIAL_NORMAL_NAMES_VER4/NAMES_VER4', 'employmentDetails' : 'DATAPACKET/BODY/hh', 'relationships' : 'DATAPACKET/BODY/COMMERCIAL_RELATIONSHIP_VER4/RELATIONSHIP', 'settledCFDetails' : 'DATAPACKET/BODY/COMMERCIAL_SETTLED_CREDIT_SUMMARY_DETAILS_VER1/COMM_SETTLED_SUMMARY_DETAILS_VER1', 'settledCFSummary' : 'DATAPACKET/BODY/COMMERCIAL_SETTLED_CREDIT_SUMMARY_VER4/COMM_SETTLED_SUMMARY_VER4', 'lendingInstInquiries' : 'DATAPACKET/BODY/COMMERCIAL_INQUIRY_VER4/INQUIRY_DETAILS_VER4', 'inqBySubject' : 'DATAPACKET/BODY/hh', 'cFDetails' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/CREDIT_DETAILS_VER4', 'last24Months' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/MONTH_YEAR_VER4', 'cfFor24Months' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4/MONTH_YEAR_VER4/COMMERCIAL_HISTORY_SUMMARY_VER4', 'disputeDetails' : 'DATAPACKET/BODY/COMMERCIAL_CF_DISPUTE_VER4/DISPUTE_DETAILS', 'potAndCurrLiabilities' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_SUMMARY_VER4/CURRENCY_VER4/SUMMARY_VER4', 'cFOfGlanceStatus' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_STATUS_VER4/COMMERCIAL_CREDIT_FACILITY_STATUS_VER4', 'dishonChequeSummary' : 'DATAPACKET/BODY/COMMERCIAL_DC_SUMMARY_VER4/DISHONOURED_CHEQUE_SUMM_VER4', 'relationshipAddresses' : 'DATAPACKET/BODY/hh', 'creditFacility' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_VER8/MASTER_CREDIT_DETAILS_VER4', 'potAndCurrLiabHeader' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_SUMMARY_VER4/CURRENCY_VER4', 'dishonChequeDetails' : 'DATAPACKET/BODY/COMMERCIAL_DC_DETAILS_VER4/DC_DETAILS_VER4', 'econActivityHistory' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE_VER4/COMMERCIAL_DETAILS_VER4/IDENTIFICATION_DETAILS_VER4', 'catalogueDescription' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_CATALOGUE_VER4/COMMERCIAL_CREDIT_DETAILS_CATALOGUE_VER4'}";
		
		System.out.println(cribApiCall.cribApiInvoke(10000061, requestJson, "M", "gihanli", "gihan", "nable@123", "asdf-9kjh-qwe56", "sblw-3hn8-soy19", corpXmlTagsJson));	
	}
	
}
