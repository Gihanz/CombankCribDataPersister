package com.gs.bulk;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.gs.request.RequestBean;
import com.gs.response.ResponseBean;
import com.gs.response.Status;
import com.gs.util.DbOperations;
import com.gs.util.PropertyReader;
import com.gs.util.XMLTagsBean;

public class CribBulkExtract {
	
	private static Properties prop;
	public static Logger log = Logger.getLogger(CribBulkExtract.class);
	
	public CribBulkExtract(){
		try{
			PropertyReader pr = new PropertyReader();
	    	prop = pr.loadPropertyFile();
	    	
			String pathSep = System.getProperty("file.separator");
	        String logpath = prop.getProperty("LOG4J_FILE_PATH");
	        String activityRoot = prop.getProperty("LOG_PATH");
			String logPropertyFile =logpath+pathSep+"log4j.properties"; 
	
			PropertyConfigurator.configure(logPropertyFile);
			PropertyReader.loadLogConfiguration(logPropertyFile, activityRoot+"/CribBulkExtract/", "CribBulkExtract.log");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error CribBulkExtract : " +e.fillInStackTrace());
			log.info("Error CribBulkExtract : " +e.fillInStackTrace());
		}	
	}
	
	public String cribBulkInvoke(int requestDetailId, String requestJson, String xmlLocation, String username, String encryptionKey, String xmlTagsJson) {
		
		DbOperations dbOperations = null;
		Connection conn = null;
		RequestBean requestObj = null;
		String responseType = null;
		List<ResponseBean> responseBeanList = new ArrayList<ResponseBean>();
		
		String bureauRequestId = null;
		
		Savepoint savePoint = null;
					
		try {
			
			dbOperations = new DbOperations();
			conn = dbOperations.getConnection(encryptionKey);
			ResponseBean responseBean = new ResponseBean();
			
			if(conn!=null) {
					
				requestObj = new Gson().fromJson(requestJson, RequestBean.class);
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc= dBuilder.parse(xmlLocation);
				doc.getDocumentElement().normalize();
				log.info("Response Xml loaded successfully.");
	
	// ============== If Consumer ==============//		
				if(requestObj.getSubjectType().equals("1")) {
					
					ConRspBulkOperations conRsOp = new ConRspBulkOperations();
								
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
						conRsOp.insrtRelationshipAddressDetails(doc, xmlTagsObj.getRelationshipAddresses(), requestDetailId, username, conn);
						conRsOp.insrtRelationshipDetails(doc, xmlTagsObj.getRelationships(), requestDetailId, username, conn);
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
					
					CorpRspBulkOperations corpRsOp = new CorpRspBulkOperations();
									
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
						isCribAvailable = "1";
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
			
	//================ Setting Request Details ================//
	private void updateRequestDetailsPstmt(int requestDetailId, String bureauReferenceNo, String isCribAvailable, String isHit, Status status, String errorCode, String error, String perHitCost, Connection conn) throws SQLException {
  		log.info("updateRequestDetailsPstmt >>>>>>>>");
  		log.info("bureauReferenceNo: "+bureauReferenceNo+"-");
		log.info("isCribAvailable: "+isCribAvailable+"-");
		log.info("isHit: "+isHit+"-");
		log.info("statusCode: "+status.getStatusCode()+"-");
		log.info("StatusDescription: "+status.getStatus()+"-");
		log.info("status: "+"COMPLETED"+"-");
		log.info("errorCode: "+errorCode+"-");
		log.info("error: "+error+"-");
		log.info("sourceName: "+"OFFLINE"+"-");
		log.info("reportPath: "+"N/A"+"-");
		log.info("perHitCost: "+perHitCost+"-");
		log.info("reportId: "+requestDetailId+"-");
		
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
		pstmt.setString(9, "OFFLINE");
		pstmt.setString(10, "N/A");
	    pstmt.setString(11, perHitCost);
	    pstmt.setInt(12, requestDetailId);
	    pstmt.setInt(13, 1);
	    pstmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
	    pstmt.setString(15, "OFFLINE");
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
			pstmt.setString(9, "OFFLINE");
			pstmt.setString(10, "N/A");
		    pstmt.setString(11, perHitCost);
		    pstmt.setInt(12, 1);
		    pstmt.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
		    pstmt.setString(14, "OFFLINE");
		    pstmt.setInt(15, requestDetailId);
					    
			pstmt.execute();
			conn.commit();
			pstmt.close();
				
		}catch(SQLException se){
			se.printStackTrace();
			log.info("Error : " +se.fillInStackTrace());
		}
	}
		
	public static void main(String[] args) {
		
		CribBulkExtract cribBulkExtract = new CribBulkExtract();
		String requestJson = "{'perHitCost':{'hit':'125', 'noHit':'75', 'multiHit':'75', 'error':'0'},'subjectType':'0'}";
		//String conXmlTagsJson = "{'consumerDetails' : 'DATAPACKET/BODY/CONSUMER_PROFILE_VER3/CONSUMER_DETAILS_VER3', 'identificationDetails' : 'DATAPACKET/BODY/CONSUMER_PROFILE_VER3/CONSUMER_DETAILS_VER3/IDENTIFICATION_DETAILS_VER3', 'mailingAddresses' : 'DATAPACKET/BODY/CONSUMER_ADDRESS_VER3/MAILING_ADDRESS_VER3', 'permanentAddresses' : 'DATAPACKET/BODY/CONSUMER_ADDRESS_VER3/PERMANENT_ADDRESS_VER3', 'reportedNames' : 'DATAPACKET/BODY/CONSUMER_NORMAL_NAMES_VER3/NAMES_VER3', 'employmentDetails' : 'DATAPACKET/BODY/CONSUMER_EMPLOYMENT_VER3/EMPLOYMENT_DETAILS_VER3', 'relationships' : 'DATAPACKET/BODY/COMMERCIAL_RELATIONSHIPS', 'relationshipAddresses' : 'DATAPACKET/BODY/CONSUMER_RELATIONSHIPS_VER4/RELATIONSHIP_DETAILS_VER4', 'settledCFDetails' : 'CONSUMER_SETTLED_CREDIT_SUMMARY_DETAILS_VER1', 'settledCFSummary' : 'CONSUMER_SETTLED_CREDIT_SUMMARY_VER3', 'lendingInstInquiries' : 'DATAPACKET/BODY/COMMERCIAL_INQUIRY/INQUIRY', 'inqBySubject' : 'INQUIRY_DETAILS_VER3', 'creditFacility' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER3/MASTER_CREDIT_DETAILS_VER3', 'cFDetails' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER3/MASTER_CREDIT_DETAILS_VER3/CREDIT_DETAILS_VER3', 'last24Months' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER3/MASTER_CREDIT_DETAILS_VER3/MONTH_YEAR_VER3', 'cfFor24Months' : 'DATAPACKET/BODY/CONSUMER_CREDIT_FACILITY_VER3/MASTER_CREDIT_DETAILS_VER3/MONTH_YEAR_VER3/CONSUMER_HISTORY_SUMMARY_VER3', 'disputeDetails' : 'CONSUMER_CF_DISPUTE_VER3', 'potAndCurrLiabHeader' : 'DATAPACKET/BODY/CREDIT_SUMMARY_VER4/CURRENCY_VER4', 'potAndCurrLiabilities' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_SUMMARY/CURRENCY/SUMMARY', 'cFOfGlanceStatus' : 'CONSUMER_CREDIT_FACILITY_STATUS_VER3', 'dishonChequeSummary' : 'DISHONOURED_CHEQUE_SUMM_VER3', 'dishonChequeDetails' : 'DC_DETAILS_VER3', 'econActivityHistory' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE/COMMERCIAL_DETAILS/ECONOMIC_ACTIVITY', 'catalogueDescription' : 'CONSUMER_CREDIT_DETAILS_CATALOGUE_VER3'}";
		String corpXmlTagsJson = "{'consumerDetails' : 'CONSUMER_DETAILS_VER3', 'commercialDetails' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE/COMMERCIAL_DETAILS', 'identificationDetails' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE/COMMERCIAL_DETAILS/IDENTIFICATION_DETAILS', 'mailingAddresses' : 'DATAPACKET/BODY/COMMERCIAL_ADDRESS/MAILING_ADDRESS', 'permanentAddresses' : 'DATAPACKET/BODY/COMMERCIAL_ADDRESS/PERMANENT_ADDRESS', 'reportedNames' : 'DATAPACKET/BODY/COMMERCIAL_NORMAL_NAMES/NAMES', 'employmentDetails' : 'DATAPACKET/BODY/CONSUMER_EMPLOYMENT_VER3/EMPLOYMENT_DETAILS_VER3', 'relationships' : 'DATAPACKET/BODY/COMMERCIAL_RELATIONSHIPS', 'settledCFDetails' : 'DATAPACKET/BODY/CONSUMER_SETTLED_CREDIT_SUMMARY_DETAILS_VER1', 'settledCFSummary' : 'DATAPACKET/BODY/hh', 'lendingInstInquiries' : 'DATAPACKET/BODY/COMMERCIAL_INQUIRY/INQUIRY_DETAILS', 'inqBySubject' : 'DATAPACKET/BODY/COMMERCIAL_INQUIRY/SELF_INQUIRY_DETAILS', 'cFDetails' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY/CREDIT_DETAILS', 'last24Months' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY/CREDIT_DETAILS', 'cfFor24Months' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY/CREDIT_DETAILS', 'disputeDetails' : 'DATAPACKET/BODY/COMMERCIAL_CF_DISPUTE', 'potAndCurrLiabilities' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_SUMMARY/CURRENCY/SUMMARY', 'cFOfGlanceStatus' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_STATUS/COMMERCIAL_CREDIT_FACILITY_STATUS', 'dishonChequeSummary' : 'DATAPACKET/BODY/hh', 'relationshipAddresses' : 'DATAPACKET/BODY/hh', 'creditFacility' : 'DATAPACKET/BODY/hh', 'potAndCurrLiabHeader' : 'DATAPACKET/BODY/hh', 'dishonChequeDetails' : 'DATAPACKET/BODY/hh', 'econActivityHistory' : 'DATAPACKET/BODY/COMMERCIAL_PROFILE/COMMERCIAL_DETAILS/ECONOMIC_ACTIVITY', 'catalogueDescription' : 'DATAPACKET/BODY/COMMERCIAL_CREDIT_FACILITY_CATALOGUE/COMMERCIAL_CREDIT_DETAILS_CATALOGUE'}";
				
		System.out.println(cribBulkExtract.cribBulkInvoke(10000000, requestJson, "C:/Users/GIHAN/Downloads/CorpXMLs/W-0071426123-2021.xml", "gihanli", "asdf-9kjh-qwe56", corpXmlTagsJson));	
	}
	
}
