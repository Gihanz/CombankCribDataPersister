package com.gs.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gs.response.MultiHit;
import com.gs.response.SearchResultItem;
import com.gs.response.SearchResults;

public class ConRspOperations {
		
	public static Logger log = Logger.getLogger(ConRspOperations.class);
	Timestamp created = new Timestamp(System.currentTimeMillis());
	XPath xPath = XPathFactory.newInstance().newXPath();
	
		//================ Setting Demographic Details ================//
		public void insrtDemographicDetails(Document doc, String s_consumerDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			
			String compiledQuery = "INSERT INTO CRIB_CON_DEMOGRAPHIC_DETAILS (REQUEST_DETAIL_ID, NAME, DOB, GENDER, SPOUSE_NAME, CITIZENSHIP, MARITAL_STATUS, TELEPHONE_AREA_CODE, TELEPHONE_NUMBER, MOBILE_NUMBER, EMAIL_ID, RUID, NIC_NUMBER, NIC_RUID, PASSPORT_NUMBER, PAS_RUID, RTO_RUID, BLOCK_FLAG, IS_ACTIVE, CREATED, CREATED_BY) " +
     			   				   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			NodeList nodeList = (NodeList) xPath.compile(s_consumerDetails).evaluate(doc, XPathConstants.NODESET);

			Element consumerDetails = (Element) nodeList.item(0);
			String name              =  consumerDetails.getElementsByTagName("NAME").item(0).getTextContent();
			String dob               = (consumerDetails.getElementsByTagName("DATE_OF_BIRTH").getLength() > 0) ? consumerDetails.getElementsByTagName("DATE_OF_BIRTH").item(0).getTextContent() : null;
			String gender            = (consumerDetails.getElementsByTagName("GENDER").getLength() > 0) ? consumerDetails.getElementsByTagName("GENDER").item(0).getTextContent() : "-";
			String spouseName        = (consumerDetails.getElementsByTagName("SPOUSE_NAME").getLength() > 0) ? consumerDetails.getElementsByTagName("SPOUSE_NAME").item(0).getTextContent() : "-";
			String citizenship       = (consumerDetails.getElementsByTagName("CITIZENSHIP").getLength() > 0) ? consumerDetails.getElementsByTagName("CITIZENSHIP").item(0).getTextContent() : "-";
			String maritalStatus     = (consumerDetails.getElementsByTagName("MARITAL_STATUS").getLength() > 0) ? consumerDetails.getElementsByTagName("MARITAL_STATUS").item(0).getTextContent() : "-";
			String telephoneAreaCode = (consumerDetails.getElementsByTagName("TELEPHONE_AREA_CODE").getLength() > 0) ? consumerDetails.getElementsByTagName("TELEPHONE_AREA_CODE").item(0).getTextContent() : "-";
			String telephoneNumber   = (consumerDetails.getElementsByTagName("PHONE_NUMBER").getLength() > 0) ? consumerDetails.getElementsByTagName("PHONE_NUMBER").item(0).getTextContent() : "-";
			String mobileNumber      = (consumerDetails.getElementsByTagName("MOBILE_NUMBER").getLength() > 0) ? consumerDetails.getElementsByTagName("MOBILE_NUMBER").item(0).getTextContent() : "-";
			String email             = (consumerDetails.getElementsByTagName("EMAIL_ID").getLength() > 0) ? consumerDetails.getElementsByTagName("EMAIL_ID").item(0).getTextContent() : "-";
			String ruid              = (consumerDetails.getElementsByTagName("RUID").getLength() > 0) ? consumerDetails.getElementsByTagName("RUID").item(0).getTextContent() : null;
			String nicNumber         = (consumerDetails.getElementsByTagName("NIC").getLength() > 0) ? consumerDetails.getElementsByTagName("NIC").item(0).getTextContent().trim() : "-";
			String nicRuid           = (consumerDetails.getElementsByTagName("NIC_RUID").getLength() > 0) ? consumerDetails.getElementsByTagName("NIC_RUID").item(0).getTextContent() : null;
			String passportNumber    = (consumerDetails.getElementsByTagName("PAS").getLength() > 0) ? consumerDetails.getElementsByTagName("PAS").item(0).getTextContent().trim() : "-";
			String passportRuid      = (consumerDetails.getElementsByTagName("PAS_RUID").getLength() > 0) ? consumerDetails.getElementsByTagName("PAS_RUID").item(0).getTextContent() : null;
			String rtoRuid           = (consumerDetails.getElementsByTagName("RTO_RUID").getLength() > 0) ? consumerDetails.getElementsByTagName("RTO_RUID").item(0).getTextContent() : null;
			String blockFlag         = (consumerDetails.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? consumerDetails.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
									
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			pstmt.setInt(1, requestDetailId);
			pstmt.setString(2, name);
			pstmt.setObject(3, formatDates(dob));
			pstmt.setString(4, gender);
			pstmt.setString(5, spouseName);
			pstmt.setString(6, citizenship);
			pstmt.setString(7, maritalStatus);
			pstmt.setString(8, telephoneAreaCode);
			pstmt.setString(9, telephoneNumber);
			pstmt.setString(10, mobileNumber);
			pstmt.setString(11, email);
			pstmt.setString(12, formatInts(ruid));
			pstmt.setString(13, nicNumber);
			pstmt.setString(14, formatInts(nicRuid));
			pstmt.setString(15, passportNumber);
			pstmt.setString(16, formatInts(passportRuid));
			pstmt.setString(17, formatInts(rtoRuid));
			pstmt.setString(18, formatInts(blockFlag));
			pstmt.setInt(19, 1);
			pstmt.setTimestamp(20, created);
			pstmt.setString(21, username);	
			    
			pstmt.execute();
			pstmt.close();			    
		}
		
		//================ Setting Identification Details ================//
		public void insrtIdentificationDetails(Document doc, String b_identificationDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
							
			String compiledQuery = "INSERT INTO CRIB_CON_IDENTIFICATION_DETAILS (REQUEST_DETAIL_ID, SOURCE_ID, ID_VALUE, ID_DISPLAY_NAME, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
							       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
					
			NodeList nodeList = (NodeList) xPath.compile(b_identificationDetails).evaluate(doc, XPathConstants.NODESET);
							
			for(int x=0; x<nodeList.getLength(); x++) {
									
				Element identificationDetails = (Element) nodeList.item(x);
				if(identificationDetails.hasChildNodes()) {	
					String sourceId      = (identificationDetails.getElementsByTagName("SOURCE_ID").getLength() > 0) ? identificationDetails.getElementsByTagName("SOURCE_ID").item(0).getTextContent() : "-";
					String idValue       = (identificationDetails.getElementsByTagName("ID_VALUE").getLength() > 0) ? identificationDetails.getElementsByTagName("ID_VALUE").item(0).getTextContent() : "-";
					String idDisplayName = (identificationDetails.getElementsByTagName("ID_DISPLAY_NAME").getLength() > 0) ? identificationDetails.getElementsByTagName("ID_DISPLAY_NAME").item(0).getTextContent() : "-";
					String ruid          = (identificationDetails.getElementsByTagName("RUID").getLength() > 0) ? identificationDetails.getElementsByTagName("RUID").item(0).getTextContent() : null;			
										
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, sourceId);
					pstmt.setString(3, idValue);
					pstmt.setString(4, idDisplayName);
					pstmt.setString(5, formatInts(ruid));			
					pstmt.setInt(6, 1);
					pstmt.setTimestamp(7, created);
					pstmt.setString(8, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		} 
		
		//================ Setting Mailing Details ================//
		public void insrtMailingAddresses(Document doc, String s_mailingAddresses, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			
			String compiledQuery = "INSERT INTO CRIB_CON_MAILING_ADDRESSES (REQUEST_DETAIL_ID, SNO, REPORTED_DATE, ADDRESS, BLOCK_FLAG, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
	                			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		    PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
		    
		    NodeList nodeList = (NodeList) xPath.compile(s_mailingAddresses).evaluate(doc, XPathConstants.NODESET);
				
			for(int x=0; x<nodeList.getLength(); x++) {
				
				Element mailingAddresses = (Element) nodeList.item(x);
				if(mailingAddresses.hasChildNodes()) {	
					String sNo          = (mailingAddresses.getElementsByTagName("SERIAL_NO").getLength() > 0) ? mailingAddresses.getElementsByTagName("SERIAL_NO").item(0).getTextContent() : null;
					String reportedDate = (mailingAddresses.getElementsByTagName("LAST_REPORTED_DATE").getLength() > 0) ? mailingAddresses.getElementsByTagName("LAST_REPORTED_DATE").item(0).getTextContent() : null;
					String address      = (mailingAddresses.getElementsByTagName("ADDRESS_VALUE").getLength() > 0) ? mailingAddresses.getElementsByTagName("ADDRESS_VALUE").item(0).getTextContent().replace("'", "`") : "-";
					String blockFlag    = (mailingAddresses.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? mailingAddresses.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					String ruid         = (mailingAddresses.getElementsByTagName("RUID").getLength() > 0) ? mailingAddresses.getElementsByTagName("RUID").item(0).getTextContent() : null;

					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setObject(3, formatDates(reportedDate));
					pstmt.setString(4, address);
					pstmt.setString(5, formatInts(blockFlag));
					pstmt.setString(6, formatInts(ruid));				
					pstmt.setInt(7, 1);
					pstmt.setTimestamp(8, created);
					pstmt.setString(9, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}    
				
		//================ Setting Permanent Details ================//
		public void insrtPermanentAddresses(Document doc, String s_permanentAddresses, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
		    
			String compiledQuery = "INSERT INTO CRIB_CON_PERMANENT_ADDRESSES (REQUEST_DETAIL_ID, SNO, REPORTED_DATE, ADDRESS, BLOCK_FLAG, IS_ACTIVE, CREATED, CREATED_BY) " +
     			   				   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_permanentAddresses).evaluate(doc, XPathConstants.NODESET);
				
			for(int x=0; x<nodeList.getLength(); x++) {
			  			
			  	Element permanentAddresses = (Element) nodeList.item(x);
			  	if(permanentAddresses.hasChildNodes()) {		
				  	String sNo          = (permanentAddresses.getElementsByTagName("SERIAL_NO").getLength() > 0) ? permanentAddresses.getElementsByTagName("SERIAL_NO").item(0).getTextContent() : null;
				  	String reportedDate = (permanentAddresses.getElementsByTagName("LAST_REPORTED_DATE").getLength() > 0) ? permanentAddresses.getElementsByTagName("LAST_REPORTED_DATE").item(0).getTextContent() : null;
				  	String address      = (permanentAddresses.getElementsByTagName("ADDRESS_VALUE").getLength() > 0) ? permanentAddresses.getElementsByTagName("ADDRESS_VALUE").item(0).getTextContent().replace("'", "`") : "-";
				  	String blockFlag    = (permanentAddresses.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? permanentAddresses.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
				  		
				  	pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setObject(3, formatDates(reportedDate));
					pstmt.setString(4, address);
					pstmt.setObject(5, formatInts(blockFlag));
					pstmt.setInt(6, 1);
					pstmt.setTimestamp(7, created);
					pstmt.setString(8, username);
					pstmt.addBatch();
			  	}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Reported Names ================//
		public void insrtReportedNames(Document doc, String s_reportedNames, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_REPORTED_NAMES (REQUEST_DETAIL_ID, SNO, NAME, REPORTED_INSTITUTION, REPORTED_DATE, BLOCK_FLAG, IS_ACTIVE, CREATED, CREATED_BY) " +
	   				   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_reportedNames).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element reportedNames = (Element) nodeList.item(x);
				if(reportedNames.hasChildNodes()) {			
					String sNo                 = (reportedNames.getElementsByTagName("SNO").getLength() > 0) ? reportedNames.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String name                = (reportedNames.getElementsByTagName("NAME").getLength() > 0) ? reportedNames.getElementsByTagName("NAME").item(0).getTextContent() : "-";
					String reportedInstitution = (reportedNames.getElementsByTagName("INSTITUTION_NAME").getLength() > 0) ? reportedNames.getElementsByTagName("INSTITUTION_NAME").item(0).getTextContent().replace("'", "`") : "-";				
					String reportedDate        = (reportedNames.getElementsByTagName("LAST_REPORTED_DATE").getLength() > 0) ? reportedNames.getElementsByTagName("LAST_REPORTED_DATE").item(0).getTextContent() : null;
					String blockFlag           = (reportedNames.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? reportedNames.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setString(3, name);
					pstmt.setString(4, reportedInstitution);				
					pstmt.setObject(5, formatDates(reportedDate));
					pstmt.setString(6, formatInts(blockFlag));
					pstmt.setInt(7, 1);
					pstmt.setTimestamp(8, created);
					pstmt.setString(9, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
			
		//================ Setting Employment Details ================//
		public void insrtEmploymentDetails(Document doc, String s_employmentDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_EMPLOYMENT_DETAILS (REQUEST_DETAIL_ID, EMPLOYMENT, PROFESSION, EMPLOYER_NAME, BUSINESS_NAME, BR_NUMBER, REPORTED_DATE, RUID, BLOCK_FLAG, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_employmentDetails).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element employmentDetails = (Element) nodeList.item(x);
				if(employmentDetails.hasChildNodes()) {	
					String employment   = (employmentDetails.getElementsByTagName("EMPLOYMENT").getLength() > 0) ? employmentDetails.getElementsByTagName("EMPLOYMENT").item(0).getTextContent() : "-";
					String profession   = (employmentDetails.getElementsByTagName("PROFESSION").getLength() > 0) ? employmentDetails.getElementsByTagName("PROFESSION").item(0).getTextContent() : "-";
					String employerName = (employmentDetails.getElementsByTagName("EMPLOYER_NAME").getLength() > 0) ? employmentDetails.getElementsByTagName("EMPLOYER_NAME").item(0).getTextContent() : "-";
					String businessName = (employmentDetails.getElementsByTagName("BUSINESS_ENTITY_NAME").getLength() > 0) ? employmentDetails.getElementsByTagName("BUSINESS_ENTITY_NAME").item(0).getTextContent().replace("'", "`") : "-";
					String brNumber     = (employmentDetails.getElementsByTagName("BUSINESS_REGISTRATION_NUMBER").getLength() > 0) ? employmentDetails.getElementsByTagName("BUSINESS_REGISTRATION_NUMBER").item(0).getTextContent() : "-";
					String reportedDate = (employmentDetails.getElementsByTagName("LAST_REPORTED_DATE").getLength() > 0) ? employmentDetails.getElementsByTagName("LAST_REPORTED_DATE").item(0).getTextContent() : null;		
					String ruid         = (employmentDetails.getElementsByTagName("RUID").getLength() > 0) ? employmentDetails.getElementsByTagName("RUID").item(0).getTextContent() : null;
					String blockFlag    = (employmentDetails.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? employmentDetails.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, employment);
					pstmt.setString(3, profession);
					pstmt.setString(4, employerName);
					pstmt.setString(5, businessName);
					pstmt.setString(6, brNumber);
					pstmt.setObject(7, formatDates(reportedDate));
					pstmt.setString(8, formatInts(ruid));
					pstmt.setString(9, formatInts(blockFlag));
					pstmt.setInt(10, 1);
					pstmt.setTimestamp(11, created);
					pstmt.setString(12, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Relationship Details ================//
		public void insrtRelationshipDetails(Document doc, String s_relationships, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_RELATIONSHIP_DETAILS (REQUEST_DETAIL_ID, SNO, ENTITY_ID, ENTITY_NAME, RELATIONSHIP_TYPE, NATURE_OF_RELATIONSHIP, ADDRESS_VALUE, PHONE_NO, FAX_NO, CITY, POSTAL_CODE, DISTRICT, PROVINCE, RELATION_RUID, RELATION_TYPE_ID, RELATION_NATURE_ID, BLOCK_FLAG, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_relationships).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element relationships = (Element) nodeList.item(x);
				if(relationships.hasChildNodes()) {		
					String sNo                  = (relationships.getElementsByTagName("SNO").getLength() > 0) ? relationships.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String entityId             = (relationships.getElementsByTagName("ID_VALUE").getLength() > 0) ? relationships.getElementsByTagName("ID_VALUE").item(0).getTextContent() : "-";
					String entityName           = (relationships.getElementsByTagName("NAME").getLength() > 0) ? relationships.getElementsByTagName("NAME").item(0).getTextContent() : "-";
					String relationshipType     = (relationships.getElementsByTagName("RELATIONSHIP_TYPE").getLength() > 0) ? relationships.getElementsByTagName("RELATIONSHIP_TYPE").item(0).getTextContent() : "-";
					String natureOfRelationship = (relationships.getElementsByTagName("NATURE").getLength() > 0) ? relationships.getElementsByTagName("NATURE").item(0).getTextContent() : "-";
					String address              = (relationships.getElementsByTagName("ADDRESS_VALUE").getLength() > 0) ? relationships.getElementsByTagName("ADDRESS_VALUE").item(0).getTextContent() : "-";
					String phone                = (relationships.getElementsByTagName("PHONE_NUMBER").getLength() > 0) ? relationships.getElementsByTagName("PHONE_NUMBER").item(0).getTextContent() : "-";
					String fax                  = (relationships.getElementsByTagName("FAX_NUMBER").getLength() > 0) ? relationships.getElementsByTagName("FAX_NUMBER").item(0).getTextContent() : "-";
					String city                 = (relationships.getElementsByTagName("CITY").getLength() > 0) ? relationships.getElementsByTagName("CITY").item(0).getTextContent() : "-";
					String postalCode           = (relationships.getElementsByTagName("POSTAL_CODE").getLength() > 0) ? relationships.getElementsByTagName("POSTAL_CODE").item(0).getTextContent() : "-";
					String district             = (relationships.getElementsByTagName("DISTRICT").getLength() > 0) ? relationships.getElementsByTagName("DISTRICT").item(0).getTextContent() : "-";
					String province             = (relationships.getElementsByTagName("PROVINCE").getLength() > 0) ? relationships.getElementsByTagName("PROVINCE").item(0).getTextContent() : "-";
					String relationRuid         = (relationships.getElementsByTagName("RELATION_RUID").getLength() > 0) ? relationships.getElementsByTagName("RELATION_RUID").item(0).getTextContent() : null;
					String relationTypeId       = (relationships.getElementsByTagName("RELATION_TYPE_ID").getLength() > 0) ? relationships.getElementsByTagName("RELATION_TYPE_ID").item(0).getTextContent() : "";
					String relationNatureId     = (relationships.getElementsByTagName("RELATION_NATURE_ID").getLength() > 0) ? relationships.getElementsByTagName("RELATION_NATURE_ID").item(0).getTextContent() : "";
					String blockFlag            = (relationships.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? relationships.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					String ruid                 = (relationships.getElementsByTagName("RUID").getLength() > 0) ? relationships.getElementsByTagName("RUID").item(0).getTextContent() : null;
					 					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setString(3, entityId);
					pstmt.setString(4, entityName);
					pstmt.setString(5, relationshipType);
					pstmt.setString(6, natureOfRelationship);
					pstmt.setString(7, address);
					pstmt.setString(8, phone);
					pstmt.setString(9, fax);
					pstmt.setString(10, city);
					pstmt.setString(11, postalCode);
					pstmt.setString(12, district);
					pstmt.setString(13, province);
					pstmt.setString(14, formatInts(relationRuid));
					pstmt.setString(15, relationTypeId);
					pstmt.setString(16, relationNatureId);
					pstmt.setString(17, formatInts(blockFlag));
					pstmt.setString(18, formatInts(ruid));	
					pstmt.setInt(19, 1);
					pstmt.setTimestamp(20, created);
					pstmt.setString(21, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Relationship Address Details ================//
		public void insrtRelationshipAddressDetails(Document doc, String s_relationshipAddresses, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
					    
			String compiledQuery = "INSERT INTO CRIB_CON_RELATIONSHIP_ADDRESS_DETAILS (REQUEST_DETAIL_ID, SNO, RELATION_RUID, ADDRESS, PROVINCE, DISTRICT, CITY, PHONE_NUMBER, FAX_NUMBER, POSTAL_CODE, IS_ACTIVE, CREATED, CREATED_BY) " +
							   	   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
					
			NodeList nodeList = (NodeList) xPath.compile(s_relationshipAddresses).evaluate(doc, XPathConstants.NODESET);
			
			for(int x=0; x<nodeList.getLength(); x++) {
						  			
				Element relationshipAddresses = (Element) nodeList.item(x);
				if(relationshipAddresses.hasChildNodes()) {	
					String sNo          = (relationshipAddresses.getElementsByTagName("SNO").getLength() > 0) ? relationshipAddresses.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String relationRuid = (relationshipAddresses.getElementsByTagName("RELATION_RUID").getLength() > 0) ? relationshipAddresses.getElementsByTagName("RELATION_RUID").item(0).getTextContent() : null;
					String address      = (relationshipAddresses.getElementsByTagName("ADDRESS_VALUE").getLength() > 0) ? relationshipAddresses.getElementsByTagName("ADDRESS_VALUE").item(0).getTextContent() : "-";
					String province     = (relationshipAddresses.getElementsByTagName("PROVINCE").getLength() > 0) ? relationshipAddresses.getElementsByTagName("PROVINCE").item(0).getTextContent() : "-";
					String district     = (relationshipAddresses.getElementsByTagName("DISTRICT").getLength() > 0) ? relationshipAddresses.getElementsByTagName("DISTRICT").item(0).getTextContent() : "-";
					String city         = (relationshipAddresses.getElementsByTagName("CITY").getLength() > 0) ? relationshipAddresses.getElementsByTagName("CITY").item(0).getTextContent() : "-";
					String phone        = (relationshipAddresses.getElementsByTagName("PHONE_NUMBER").getLength() > 0) ? relationshipAddresses.getElementsByTagName("PHONE_NUMBER").item(0).getTextContent() : "-";
					String fax          = (relationshipAddresses.getElementsByTagName("FAX_NUMBER").getLength() > 0) ? relationshipAddresses.getElementsByTagName("FAX_NUMBER").item(0).getTextContent() : "-";					
					String postalCode   = (relationshipAddresses.getElementsByTagName("POSTAL_CODE").getLength() > 0) ? relationshipAddresses.getElementsByTagName("POSTAL_CODE").item(0).getTextContent() : "-";
												 					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setString(3, formatInts(relationRuid));	
					pstmt.setString(4, address);
					pstmt.setString(5, province);
					pstmt.setString(6, district);
					pstmt.setString(7, city);
					pstmt.setString(8, phone);
					pstmt.setString(9, fax);
					pstmt.setString(10, postalCode);
					pstmt.setInt(11, 1);
					pstmt.setTimestamp(12, created);
					pstmt.setString(13, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Settled Credit Facilities Details ================//
		public void insrtSettledCFDetails(Document doc, String s_settledCFDetils, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_SETTLED_CREDIT_FACILITIES_DETAILS (REQUEST_DETAIL_ID, CURRENCY, CF_TYPE, NO_OF_CREDIT_FACILITIES_AS_BORROWER, AMOUNT_GRANTED_AS_BORROWER, NO_OF_CREDIT_FACILITIES_AS_GUARANTOR, AMOUNT_GRANTED_AS_GUARANTOR, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_settledCFDetils).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element settledCFDetails = (Element) nodeList.item(x);
				if(settledCFDetails.hasChildNodes()) {	
					String currency              = (settledCFDetails.getElementsByTagName("Currency").getLength() > 0) ? settledCFDetails.getElementsByTagName("Currency").item(0).getTextContent() : "-";
					String cfType                = (settledCFDetails.getElementsByTagName("CF_Type").getLength() > 0) ? settledCFDetails.getElementsByTagName("CF_Type").item(0).getTextContent() : "-";
					String noOfCfasBorrower      = (settledCFDetails.getElementsByTagName("Total_no_of_credit_facilities_BRW").getLength() > 0) ? settledCFDetails.getElementsByTagName("Total_no_of_credit_facilities_BRW").item(0).getTextContent() : null;
					String amtGrantedasBorrower  = (settledCFDetails.getElementsByTagName("Total_Amt_Granted_BRW").getLength() > 0) ? settledCFDetails.getElementsByTagName("Total_Amt_Granted_BRW").item(0).getTextContent() : null;
					String noOfCfasGuarantor     = (settledCFDetails.getElementsByTagName("Total_no_of_credit_facilities_GRT").getLength() > 0) ? settledCFDetails.getElementsByTagName("Total_no_of_credit_facilities_GRT").item(0).getTextContent() : null;
					String amtGrantedasGuarantor = (settledCFDetails.getElementsByTagName("Total_Amt_Granted_GRT").getLength() > 0) ? settledCFDetails.getElementsByTagName("Total_Amt_Granted_GRT").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, currency);
					pstmt.setString(3, cfType);
					pstmt.setString(4, formatInts(noOfCfasBorrower));
					pstmt.setObject(5, formatAmounts(amtGrantedasBorrower));
					pstmt.setString(6, formatInts(noOfCfasGuarantor));
					pstmt.setObject(7, formatAmounts(amtGrantedasGuarantor));
					pstmt.setInt(8, 1);
					pstmt.setTimestamp(9, created);
					pstmt.setString(10, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}

		//================ Setting Settled Credit Facilities Summary ================//
		public void insrtSettledCFSummary(Document doc, String s_settledCFSummary, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_SETTLED_CREDIT_FACILITIES_SUMMARY (REQUEST_DETAIL_ID, CURRENCY, OWNERSHIP, NO_OF_CREDIT_FACILITIES_1, AMOUNT_GRANTED_1, NO_OF_CREDIT_FACILITIES_2, AMOUNT_GRANTED_2, NO_OF_CREDIT_FACILITIES_3, AMOUNT_GRANTED_3, NO_OF_CREDIT_FACILITIES_4, AMOUNT_GRANTED_4, NO_OF_CREDIT_FACILITIES_5, AMOUNT_GRANTED_5, RP, YEAR1, YEAR2, YEAR3, YEAR4, YEAR5, IS_COLORING_NEEDED, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_settledCFSummary).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element settledCFSummary = (Element) nodeList.item(x);
				if(settledCFSummary.hasChildNodes()) {	
					String currency     = (settledCFSummary.getElementsByTagName("Currency").getLength() > 0) ? settledCFSummary.getElementsByTagName("Currency").item(0).getTextContent() : "-";
					String ownership    = (settledCFSummary.getElementsByTagName("Ownership").getLength() > 0) ? settledCFSummary.getElementsByTagName("Ownership").item(0).getTextContent() : "-";
					String noOfCF_1     = (settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr1").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr1").item(0).getTextContent() : null;
					String amtGranted_1 = (settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr1").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr1").item(0).getTextContent() : null;
					String noOfCF_2     = (settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr2").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr2").item(0).getTextContent() : null;
					String amtGranted_2 = (settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr2").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr2").item(0).getTextContent() : null;
					String noOfCF_3     = (settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr3").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr3").item(0).getTextContent() : null;
					String amtGranted_3 = (settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr3").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr3").item(0).getTextContent() : null;
					String noOfCF_4     = (settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr4").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr4").item(0).getTextContent() : null;
					String amtGranted_4 = (settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr4").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr4").item(0).getTextContent() : null;
					String noOfCF_5     = (settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr5").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_no_of_credit_facilities_yr5").item(0).getTextContent() : null;
					String amtGranted_5 = (settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr5").getLength() > 0) ? settledCFSummary.getElementsByTagName("Total_Amt_Granted_yr5").item(0).getTextContent() : null;
					String rp           = (settledCFSummary.getElementsByTagName("RP").getLength() > 0) ? settledCFSummary.getElementsByTagName("RP").item(0).getTextContent() : "-";
					String year1        = (settledCFSummary.getElementsByTagName("STYR1").getLength() > 0) ? settledCFSummary.getElementsByTagName("STYR1").item(0).getTextContent() : "-";
					String year2        = (settledCFSummary.getElementsByTagName("STYR2").getLength() > 0) ? settledCFSummary.getElementsByTagName("STYR2").item(0).getTextContent() : "-";
					String year3        = (settledCFSummary.getElementsByTagName("STYR3").getLength() > 0) ? settledCFSummary.getElementsByTagName("STYR3").item(0).getTextContent() : "-";
					String year4        = (settledCFSummary.getElementsByTagName("STYR4").getLength() > 0) ? settledCFSummary.getElementsByTagName("STYR4").item(0).getTextContent() : "-";
					String year5        = (settledCFSummary.getElementsByTagName("STYR5").getLength() > 0) ? settledCFSummary.getElementsByTagName("STYR5").item(0).getTextContent() : "-";
					String isColorNeed  = (settledCFSummary.getElementsByTagName("IsColoringNeeded").getLength() > 0) ? settledCFSummary.getElementsByTagName("IsColoringNeeded").item(0).getTextContent() : "-";
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, currency);
					pstmt.setString(3, ownership);
					pstmt.setString(4, formatInts(noOfCF_1));
					pstmt.setObject(5, formatAmounts(amtGranted_1));
					pstmt.setString(6, formatInts(noOfCF_2));
					pstmt.setObject(7, formatAmounts(amtGranted_2));
					pstmt.setString(8, formatInts(noOfCF_3));
					pstmt.setObject(9, formatAmounts(amtGranted_3));
					pstmt.setString(10, formatInts(noOfCF_4));
					pstmt.setObject(11, formatAmounts(amtGranted_4));
					pstmt.setString(12, formatInts(noOfCF_5));
					pstmt.setObject(13, formatAmounts(amtGranted_5));
					pstmt.setString(14, rp);
					pstmt.setString(15, year1);
					pstmt.setString(16, year2);
					pstmt.setString(17, year3);
					pstmt.setString(18, year4);
					pstmt.setString(19, year5);
					pstmt.setString(20, isColorNeed);
					pstmt.setInt(21, 1);
					pstmt.setTimestamp(22, created);
					pstmt.setString(23, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Lending Institutions Inquiries ================//
		public void insrtLendingInstInquiries(Document doc, String s_lendingInstInquiries, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_LENDING_INSTUTIONS_INQUIRIES (REQUEST_DETAIL_ID, SNO, INSTITUION_CATEGORY, INQUIRY_DATE, REASON_ID, REASON, INSTITUTION_NAME, PRODUCT_NAME, CURRENCY, AMOUNT, CF_TYPE, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_lendingInstInquiries).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element lendingInstInquiries = (Element) nodeList.item(x);
				if(lendingInstInquiries.hasChildNodes()) {	
					String sNo         = (lendingInstInquiries.getElementsByTagName("SLNO").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("SLNO").item(0).getTextContent() : null;
					String instCategory= (lendingInstInquiries.getElementsByTagName("INSTITUTION_TYPE").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("INSTITUTION_TYPE").item(0).getTextContent() : "-";
					String inquiryDate = (lendingInstInquiries.getElementsByTagName("INQUIRY_DATE").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("INQUIRY_DATE").item(0).getTextContent() : null;
					String reasonId    = (lendingInstInquiries.getElementsByTagName("REASON_ID").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("REASON_ID").item(0).getTextContent() : null;
					String reason      = (lendingInstInquiries.getElementsByTagName("REASON").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("REASON").item(0).getTextContent() : "-";
					String instName    = (lendingInstInquiries.getElementsByTagName("INSTITUTION_TYPE").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("INSTITUTION_TYPE").item(0).getTextContent() : "-";
					String product     = (lendingInstInquiries.getElementsByTagName("PRODUCT_NAME").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("PRODUCT_NAME").item(0).getTextContent() : "-";
					String currency    = (lendingInstInquiries.getElementsByTagName("CURRENCY").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("CURRENCY").item(0).getTextContent() : "-";
					String amount      = (lendingInstInquiries.getElementsByTagName("AMOUNT").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("AMOUNT").item(0).getTextContent() : null;
					String cfType      = (lendingInstInquiries.getElementsByTagName("CREDIT_FACILITY_TYPE").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("CREDIT_FACILITY_TYPE").item(0).getTextContent() : "-";
					String ruid        = (lendingInstInquiries.getElementsByTagName("RUID").getLength() > 0) ? lendingInstInquiries.getElementsByTagName("RUID").item(0).getTextContent() : null;
							  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setString(3, instCategory);
					pstmt.setObject(4, formatDates(inquiryDate));
					pstmt.setString(5, formatInts(reasonId));
					pstmt.setString(6, reason);					
					pstmt.setString(7, instName);					
					pstmt.setString(8, product);
					pstmt.setString(9, currency);
					pstmt.setObject(10, formatAmounts(amount));
					pstmt.setString(11, cfType);
					pstmt.setString(12, formatInts(ruid));
					pstmt.setInt(13, 1);
					pstmt.setTimestamp(14, created);
					pstmt.setString(15, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Inquiries By Subject ================//
		public void insrtInquiriesBySubject(Document doc, String s_inqBySubject, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_INQUIRIES_BY_SUBJECT (REQUEST_DETAIL_ID, SNO, INQUIRY_DATE, REASON, REASON_ID, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_inqBySubject).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element inqBySubject = (Element) nodeList.item(x);
				if(inqBySubject.hasChildNodes()) {	
					String sNo         = (inqBySubject.getElementsByTagName("SLNO").getLength() > 0) ? inqBySubject.getElementsByTagName("SLNO").item(0).getTextContent() : null;
					String inquiryDate = (inqBySubject.getElementsByTagName("INQUIRY_DATE").getLength() > 0) ? inqBySubject.getElementsByTagName("INQUIRY_DATE").item(0).getTextContent() : null;
					String reason      = (inqBySubject.getElementsByTagName("REASON").getLength() > 0) ? inqBySubject.getElementsByTagName("REASON").item(0).getTextContent() : "-";
					String reasonId    = (inqBySubject.getElementsByTagName("REASON_ID").getLength() > 0) ? inqBySubject.getElementsByTagName("REASON_ID").item(0).getTextContent() : null;
					
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));						
					pstmt.setObject(3, formatDates(inquiryDate));
					pstmt.setString(4, reason);
					pstmt.setString(5, formatInts(reasonId));
					pstmt.setInt(6, 1);
					pstmt.setTimestamp(7, created);
					pstmt.setString(8, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Credit Facility ================//
		public void insrtCreditFacility(Document doc, String s_creditFacility, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
					    
			String compiledQuery = "INSERT INTO CRIB_CON_CREDIT_FACILITY (REQUEST_DETAIL_ID, RELATION_ID, SERIAL_NUMBER, IS_ACTIVE, CREATED, CREATED_BY) " +
							   	   "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
					
			NodeList nodeList = (NodeList) xPath.compile(s_creditFacility).evaluate(doc, XPathConstants.NODESET);
			
			for(int x=0; x<nodeList.getLength(); x++) {
						  			
				Element creditFacility = (Element) nodeList.item(x);
				if(creditFacility.hasChildNodes()) {	
					String relationId   = (creditFacility.getElementsByTagName("relation_id").getLength() > 0) ? creditFacility.getElementsByTagName("relation_id").item(0).getTextContent() : null;
					String serialNumber = (creditFacility.getElementsByTagName("SerialNumber").getLength() > 0) ? creditFacility.getElementsByTagName("SerialNumber").item(0).getTextContent() : null;					
							
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(relationId));						
					pstmt.setString(3, formatInts(serialNumber));
					pstmt.setInt(4, 1);
					pstmt.setTimestamp(5, created);
					pstmt.setString(6, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Credit Facility Details ================//		
		public void insrtCreditFacilityDetails(Document doc, String s_cFDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			
			String compiledQuery = "INSERT INTO CRIB_CON_CREDIT_FACILITY_DETAILS (REQUEST_DETAIL_ID, SNO, INSTITUTION_CATEGORY, INSTITUTION_BRANCH, CF_TYPE, CF_STATUS, OWNERSHIP, CURRENCY, AMOUNT_GRANTED, CURRENT_BALANCE, ARREARS_AMOUNT, INSTALLMENT_AMOUNT, AMOUNT_WRITTEN_OFF, REPORTED_DATE, FIRST_DISBURSE_DATE, LATEST_PAYMENT_DATE, RESTRUCTURING_DATE, END_DATE, REPAY_TYPE, PURPOSE, COVERAGE, RELATION_ID, ACCOUNT_STATUS, DISPUTE, BUREAU_GUARANTEE_COVERAGE, BUREAU_SECURITY_COVERAGE, INTEREST_OUTSTANDING, MAX_NUM_DAYS_DUE, LOAN_TYPE, SANCTION_DATE, OWNERSHIP_INDICATOR, REPAYMENT_TYPE, PRIORITY, PRIORITY2, LEGAL_ACTION, NUMBER_OF_INSTALLMENTS, PRIMARY_ROOT_ID, ACTIVE_ROOT_ID, RUID, PROVIDER_BRANCH, PROVIDER_SOURCE, CATEGORY_DESC, SI_INST_NAME, SI_BRNH_NAME, DISPUTE_ID, RANK, ROWNUM, SECUR_TYPE, BLOCK_FLAG, IS_ACTIVE, CREATED, CREATED_BY) " +
	                			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		    PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
		    
		    NodeList nodeList = (NodeList) xPath.compile(s_cFDetails).evaluate(doc, XPathConstants.NODESET);
		    	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element cFDetails = (Element) nodeList.item(x);
				if(cFDetails.hasChildNodes()) {	
					String sNo               = (cFDetails.getElementsByTagName("SNO").getLength() > 0) ? cFDetails.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String institutionCatg   = (cFDetails.getElementsByTagName("INS_CATEGORY").getLength() > 0) ? cFDetails.getElementsByTagName("INS_CATEGORY").item(0).getTextContent() : "-";
					String institutionBranch = (cFDetails.getElementsByTagName("BRANCH_NAME").getLength() > 0) ? cFDetails.getElementsByTagName("BRANCH_NAME").item(0).getTextContent() : "-";
					String cfType            = (cFDetails.getElementsByTagName("CREDIT_FACILITY_TYPE").getLength() > 0) ? cFDetails.getElementsByTagName("CREDIT_FACILITY_TYPE").item(0).getTextContent() : "-";
					String cfStatus          = (cFDetails.getElementsByTagName("CREDIT_FACILITY_STATUS").getLength() > 0) ? cFDetails.getElementsByTagName("CREDIT_FACILITY_STATUS").item(0).getTextContent() : "-";
					String ownership         = (cFDetails.getElementsByTagName("OWNER_SHIP_INDICATOR").getLength() > 0) ? cFDetails.getElementsByTagName("OWNER_SHIP_INDICATOR").item(0).getTextContent() : "-";
					String currency          = (cFDetails.getElementsByTagName("CURRENCY").getLength() > 0) ? cFDetails.getElementsByTagName("CURRENCY").item(0).getTextContent() : "-";
					String amtGranted        = (cFDetails.getElementsByTagName("SANCTIONED_AMOUNT").getLength() > 0) ? cFDetails.getElementsByTagName("SANCTIONED_AMOUNT").item(0).getTextContent() : null;
					String currentBalance    = (cFDetails.getElementsByTagName("CURRENT_BALANCE").getLength() > 0) ? cFDetails.getElementsByTagName("CURRENT_BALANCE").item(0).getTextContent() : null;
					String arrearsAmt        = (cFDetails.getElementsByTagName("OVERDUE_AMOUNT").getLength() > 0) ? cFDetails.getElementsByTagName("OVERDUE_AMOUNT").item(0).getTextContent() : null;
					String installmentAmt    = (cFDetails.getElementsByTagName("INSTALLMENT_AMOUNT").getLength() > 0) ? cFDetails.getElementsByTagName("INSTALLMENT_AMOUNT").item(0).getTextContent() : null;
					String amtWrittenOff     = (cFDetails.getElementsByTagName("WRITE_OFF_AMOUNT").getLength() > 0) ? cFDetails.getElementsByTagName("WRITE_OFF_AMOUNT").item(0).getTextContent() : null;
					String reportedDate      = (cFDetails.getElementsByTagName("REPORTED_DATE").getLength() > 0) ? cFDetails.getElementsByTagName("REPORTED_DATE").item(0).getTextContent() : null;
					String firstDisburseDate = (cFDetails.getElementsByTagName("FIRST_DISBURSE_DATE").getLength() > 0) ? cFDetails.getElementsByTagName("FIRST_DISBURSE_DATE").item(0).getTextContent() : null;
					String lastPaymentDate   = (cFDetails.getElementsByTagName("DATE_LATEST_PAY_RECEIVED").getLength() > 0) ? cFDetails.getElementsByTagName("DATE_LATEST_PAY_RECEIVED").item(0).getTextContent() : null;
					String restructuringDate = (cFDetails.getElementsByTagName("RESTRUCTURING_DATE").getLength() > 0) ? cFDetails.getElementsByTagName("RESTRUCTURING_DATE").item(0).getTextContent() : null;
					String endDate           = (cFDetails.getElementsByTagName("DATE_ACC_CLOSE").getLength() > 0) ? cFDetails.getElementsByTagName("DATE_ACC_CLOSE").item(0).getTextContent() : null;
					String repayType         = (cFDetails.getElementsByTagName("REPAY_TYPE").getLength() > 0) ? cFDetails.getElementsByTagName("REPAY_TYPE").item(0).getTextContent() : "-";
					String purpose           = (cFDetails.getElementsByTagName("BUREAU_CREDIT_FAC_PURPOSE").getLength() > 0) ? cFDetails.getElementsByTagName("BUREAU_CREDIT_FAC_PURPOSE").item(0).getTextContent() : "-";
					String coverage          = (cFDetails.getElementsByTagName("COVERAGE").getLength() > 0) ? cFDetails.getElementsByTagName("COVERAGE").item(0).getTextContent() : "-";
					
					String relationId         = (cFDetails.getElementsByTagName("RELATION_ID").getLength() > 0) ? cFDetails.getElementsByTagName("RELATION_ID").item(0).getTextContent() : null;
					String accountStatus      = (cFDetails.getElementsByTagName("ACCOUNT_STATUS").getLength() > 0) ? cFDetails.getElementsByTagName("ACCOUNT_STATUS").item(0).getTextContent() : "-";
					String dispute            = (cFDetails.getElementsByTagName("DISPUTE").getLength() > 0) ? cFDetails.getElementsByTagName("DISPUTE").item(0).getTextContent() : "-";
					String guaranteeCoverage  = (cFDetails.getElementsByTagName("BUREAU_GUARANTEE_COVERAGE").getLength() > 0) ? cFDetails.getElementsByTagName("BUREAU_GUARANTEE_COVERAGE").item(0).getTextContent() : "-";
					String securityCoverage   = (cFDetails.getElementsByTagName("BUREAU_SECURITY_COVERAGE").getLength() > 0) ? cFDetails.getElementsByTagName("BUREAU_SECURITY_COVERAGE").item(0).getTextContent() : "-";
					String interestOutstnding = (cFDetails.getElementsByTagName("INTEREST_OUTSTANDING").getLength() > 0) ? cFDetails.getElementsByTagName("INTEREST_OUTSTANDING").item(0).getTextContent() : null;
					String numDaysDue         = (cFDetails.getElementsByTagName("MAX_NUM_DAYS_DUE").getLength() > 0) ? cFDetails.getElementsByTagName("MAX_NUM_DAYS_DUE").item(0).getTextContent() : null;
					String loanType           = (cFDetails.getElementsByTagName("LOAN_TYPE").getLength() > 0) ? cFDetails.getElementsByTagName("LOAN_TYPE").item(0).getTextContent() : "-";
					String sanctionDate       = (cFDetails.getElementsByTagName("SANCTION_DATE").getLength() > 0) ? cFDetails.getElementsByTagName("SANCTION_DATE").item(0).getTextContent() : null;
					String ownershipIndicator = (cFDetails.getElementsByTagName("OWNERSHIP_INDICATOR").getLength() > 0) ? cFDetails.getElementsByTagName("OWNERSHIP_INDICATOR").item(0).getTextContent() : "-";
					String repaymentType      = (cFDetails.getElementsByTagName("REPAYMENT_TYPE").getLength() > 0) ? cFDetails.getElementsByTagName("REPAYMENT_TYPE").item(0).getTextContent() : "-";
					String priority           = (cFDetails.getElementsByTagName("PRIORITY").getLength() > 0) ? cFDetails.getElementsByTagName("PRIORITY").item(0).getTextContent() : null;
					String priority2          = (cFDetails.getElementsByTagName("PRIORITY2").getLength() > 0) ? cFDetails.getElementsByTagName("PRIORITY2").item(0).getTextContent() : null;
					String legalAction        = (cFDetails.getElementsByTagName("LEGAL_ACTION").getLength() > 0) ? cFDetails.getElementsByTagName("LEGAL_ACTION").item(0).getTextContent() : "-";
					String numOfInstallments  = (cFDetails.getElementsByTagName("NUMBER_OF_INSTALLMENTS").getLength() > 0) ? cFDetails.getElementsByTagName("NUMBER_OF_INSTALLMENTS").item(0).getTextContent() : null;
					String primaryRoot        = (cFDetails.getElementsByTagName("PRIMARY_ROOT_ID").getLength() > 0) ? cFDetails.getElementsByTagName("PRIMARY_ROOT_ID").item(0).getTextContent() : null;
					String activeRoot         = (cFDetails.getElementsByTagName("ACTIVE_ROOT_ID").getLength() > 0) ? cFDetails.getElementsByTagName("ACTIVE_ROOT_ID").item(0).getTextContent() : null;
					String ruId               = (cFDetails.getElementsByTagName("RUID").getLength() > 0) ? cFDetails.getElementsByTagName("RUID").item(0).getTextContent() : null;
					String providerBranch     = (cFDetails.getElementsByTagName("PROVIDER_BRANCH").getLength() > 0) ? cFDetails.getElementsByTagName("PROVIDER_BRANCH").item(0).getTextContent() : "-";
					String providerSource     = (cFDetails.getElementsByTagName("PROVIDER_SOURCE").getLength() > 0) ? cFDetails.getElementsByTagName("PROVIDER_SOURCE").item(0).getTextContent() : "-";
					String categoryDesc       = (cFDetails.getElementsByTagName("CATEGORY_DESC").getLength() > 0) ? cFDetails.getElementsByTagName("CATEGORY_DESC").item(0).getTextContent() : "-";
					String siInstName         = (cFDetails.getElementsByTagName("SI_INST_NAME").getLength() > 0) ? cFDetails.getElementsByTagName("SI_INST_NAME").item(0).getTextContent() : "-";
					String siBranchName       = (cFDetails.getElementsByTagName("SI_BRNH_NAME").getLength() > 0) ? cFDetails.getElementsByTagName("SI_BRNH_NAME").item(0).getTextContent() : "-";
					String disputeId          = (cFDetails.getElementsByTagName("DISPUTE_ID").getLength() > 0) ? cFDetails.getElementsByTagName("DISPUTE_ID").item(0).getTextContent() : "-";
					String rank               = (cFDetails.getElementsByTagName("RANK").getLength() > 0) ? cFDetails.getElementsByTagName("RANK").item(0).getTextContent() : null;
					String rowNum             = (cFDetails.getElementsByTagName("ROWNUM").getLength() > 0) ? cFDetails.getElementsByTagName("ROWNUM").item(0).getTextContent() : null;
					String securType          = (cFDetails.getElementsByTagName("SECUR_TYPE").getLength() > 0) ? cFDetails.getElementsByTagName("SECUR_TYPE").item(0).getTextContent() : "-";
					String blockFlag          = (cFDetails.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? cFDetails.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sNo));
					pstmt.setString(3, institutionCatg);
					pstmt.setString(4, institutionBranch);
					pstmt.setString(5, cfType);
					pstmt.setString(6, cfStatus);
					pstmt.setString(7, ownership);
					pstmt.setString(8, currency);
					pstmt.setObject(9, formatAmounts(amtGranted));
					pstmt.setObject(10, formatAmounts(currentBalance));
					pstmt.setObject(11, formatAmounts(arrearsAmt));
					pstmt.setObject(12, formatAmounts(installmentAmt));
					pstmt.setObject(13, formatAmounts(amtWrittenOff));
					pstmt.setObject(14, formatDates(reportedDate));
					pstmt.setObject(15, formatDates(firstDisburseDate));
					pstmt.setObject(16, formatDates(lastPaymentDate));
					pstmt.setObject(17, formatDates(restructuringDate));
					pstmt.setObject(18, formatDates(endDate));
					pstmt.setString(19, repayType);
					pstmt.setString(20, purpose);
					pstmt.setString(21, coverage);

					pstmt.setString(22, formatInts(relationId));
				    pstmt.setString(23, accountStatus);
				    pstmt.setString(24, dispute);
				    pstmt.setString(25, guaranteeCoverage);
				    pstmt.setString(26, securityCoverage);
				    pstmt.setObject(27, formatAmounts(interestOutstnding));
				    pstmt.setString(28, formatInts(numDaysDue));
				    pstmt.setString(29, loanType);
				    pstmt.setObject(30, formatDates(sanctionDate));
				    pstmt.setString(31, ownershipIndicator);
				    pstmt.setString(32, repaymentType);
				    pstmt.setString(33, formatInts(priority));
				    pstmt.setString(34, formatInts(priority2));
				    pstmt.setString(35, legalAction);
				    pstmt.setString(36, formatInts(numOfInstallments));
				    pstmt.setString(37, formatInts(primaryRoot));
				    pstmt.setString(38, formatInts(activeRoot));
				    pstmt.setString(39, formatInts(ruId));
				    pstmt.setString(40, providerBranch);
				    pstmt.setString(41, providerSource);
				    pstmt.setString(42, categoryDesc);
				    pstmt.setString(43, siInstName);
				    pstmt.setString(44, siBranchName);
				    pstmt.setString(45, disputeId);
				    pstmt.setString(46, formatInts(rank));
				    pstmt.setString(47, formatInts(rowNum));
				    pstmt.setString(48, securType);
				    pstmt.setString(49, formatInts(blockFlag));
				    pstmt.setInt(50, 1);
				    pstmt.setTimestamp(51, created);
				    pstmt.setString(52, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Last 24 Months ================//
		public void insrtLast24Months(Document doc, String s_last24Months, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			  
			String compiledQuery = "INSERT INTO CRIB_CON_LAST_24_MONTHS (REQUEST_DETAIL_ID, RELATION_ID, SNO, ROWNUM, ACTIVE_ROOT_ID, TO_MONTH_YEAR, FROM_MONTH_YEAR, BUREAU_CURRENCY, BUREAU_ACC_STATUS, RANK, IS_ACTIVE, CREATED, CREATED_BY) " +
				   				   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_last24Months).evaluate(doc, XPathConstants.NODESET);

			for(int x=0; x<nodeList.getLength(); x++) {
				
				Element last24Months = (Element) nodeList.item(x);
				if(last24Months.hasChildNodes()) {	
					String relationId      = (last24Months.getElementsByTagName("RELATION_ID").getLength() > 0) ? last24Months.getElementsByTagName("RELATION_ID").item(0).getTextContent() : null;
					String sNo             = (last24Months.getElementsByTagName("SNO").getLength() > 0) ? last24Months.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String rowNum          = (last24Months.getElementsByTagName("ROWNUM").getLength() > 0) ? last24Months.getElementsByTagName("ROWNUM").item(0).getTextContent() : null;
					String activeRootId    = (last24Months.getElementsByTagName("ACTIVE_ROOT_ID").getLength() > 0) ? last24Months.getElementsByTagName("ACTIVE_ROOT_ID").item(0).getTextContent() : null;
					String toMonthYear     = (last24Months.getElementsByTagName("TO_MONTH_YEAR").getLength() > 0) ? last24Months.getElementsByTagName("TO_MONTH_YEAR").item(0).getTextContent() : "-";
					String fromMonthYear   = (last24Months.getElementsByTagName("FROM_MONTH_YEAR").getLength() > 0) ? last24Months.getElementsByTagName("FROM_MONTH_YEAR").item(0).getTextContent() : "-";
					String bureauCurrency  = (last24Months.getElementsByTagName("BUREAU_CURRENCY").getLength() > 0) ? last24Months.getElementsByTagName("BUREAU_CURRENCY").item(0).getTextContent() : "-";
					String bureauAccStatus = (last24Months.getElementsByTagName("BUREAU_ACC_STATUS").getLength() > 0) ? last24Months.getElementsByTagName("BUREAU_ACC_STATUS").item(0).getTextContent() : "-";
					String rank            = (last24Months.getElementsByTagName("RANK").getLength() > 0) ? last24Months.getElementsByTagName("RANK").item(0).getTextContent() : null;
											
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(relationId));
					pstmt.setString(3, formatInts(sNo));
					pstmt.setString(4, formatInts(rowNum));
					pstmt.setString(5, formatInts(activeRootId));
					pstmt.setString(6, toMonthYear);
					pstmt.setString(7, fromMonthYear);
					pstmt.setString(8, bureauCurrency);
					pstmt.setString(9, bureauAccStatus);
					pstmt.setString(10, formatInts(rank));
				    pstmt.setInt(11, 1);
				    pstmt.setTimestamp(12, created);
				    pstmt.setString(13, username);
				    pstmt.addBatch();				    
				}			
			}
			pstmt.executeBatch();
		    pstmt.close();
		}
		
		//================ Setting Credit Facility For Last 24 Months ================//
		public void insrtCFForLast24Months(Document doc, String s_cfFor24Months, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    			
			String compiledQuery = "INSERT INTO CRIB_CON_CREDIT_FACILITY_DETAILS_LAST_24_MONTHS (REQUEST_DETAIL_ID, ACTIVE_ROOT_ID, TO_MONTH_YEAR, FROM_MONTH_YEAR, MONTH, CURRENT_BALANCE, AMOUNT_OVERDUE, ASSET_CLASSIFICATION, MAXIMUM_NUMBER_OF_DAYS_OVERDUE, IS_ACTIVE, CREATED, CREATED_BY) " +
	                		 	   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		    PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
		    
		    NodeList nodeList = (NodeList) xPath.compile(s_cfFor24Months).evaluate(doc, XPathConstants.NODESET);
		    	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element cfFor24Months = (Element) nodeList.item(x);
				if(cfFor24Months.hasChildNodes()) {	
					String activeRootId        = (cfFor24Months.getElementsByTagName("ACTIVE_ROOT_ID").getLength() > 0) ? cfFor24Months.getElementsByTagName("ACTIVE_ROOT_ID").item(0).getTextContent() : null;
					String toMonthYear         = (cfFor24Months.getElementsByTagName("to_month_year").getLength() > 0) ? cfFor24Months.getElementsByTagName("to_month_year").item(0).getTextContent() : "-";
					String fromMonthYear       = (cfFor24Months.getElementsByTagName("from_month_year").getLength() > 0) ? cfFor24Months.getElementsByTagName("from_month_year").item(0).getTextContent() : "-";
					String month               = (cfFor24Months.getElementsByTagName("MONTH").getLength() > 0) ? cfFor24Months.getElementsByTagName("MONTH").item(0).getTextContent() : "-";
					String currentBalance      = (cfFor24Months.getElementsByTagName("CURRENT_BALANCE").getLength() > 0) ? cfFor24Months.getElementsByTagName("CURRENT_BALANCE").item(0).getTextContent() : null;
					String amountOverdue       = (cfFor24Months.getElementsByTagName("AMOUNT_OVERDUE").getLength() > 0) ? cfFor24Months.getElementsByTagName("AMOUNT_OVERDUE").item(0).getTextContent() : null;
					String assetClassification = (cfFor24Months.getElementsByTagName("ASSET_CLASSIFICATION").getLength() > 0) ? cfFor24Months.getElementsByTagName("ASSET_CLASSIFICATION").item(0).getTextContent() : "-";
					String maxDaysOverdue      = (cfFor24Months.getElementsByTagName("MAXIMUM_NUMBER_OF_DAYS_OVERDUE").getLength() > 0) ? cfFor24Months.getElementsByTagName("MAXIMUM_NUMBER_OF_DAYS_OVERDUE").item(0).getTextContent() : "";
						
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(activeRootId));
					pstmt.setString(3, toMonthYear);
					pstmt.setString(4, fromMonthYear);
					pstmt.setString(5, month);
					pstmt.setString(6, formatAmounts(currentBalance));
					pstmt.setString(7, formatAmounts(amountOverdue));
					pstmt.setString(8, assetClassification);
					pstmt.setString(9, maxDaysOverdue);
					pstmt.setInt(10, 1);
					pstmt.setTimestamp(11, created);
					pstmt.setString(12, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Dispute Details ================//
		public void insrtDisputeDetails(Document doc, String s_disputeDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_DISPUTE_DETAILS (REQUEST_DETAIL_ID, DESCRIPTION, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_disputeDetails).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element disputeDetails = (Element) nodeList.item(x);
				if(disputeDetails.hasChildNodes()) {	
					String description = (disputeDetails.getElementsByTagName("DISPUTE_DETAILS").getLength() > 0) ? disputeDetails.getElementsByTagName("DISPUTE_DETAILS").item(0).getTextContent() : "-";
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, description);
					pstmt.setInt(3, 1);
					pstmt.setTimestamp(4, created);
					pstmt.setString(5, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Potential And Current Liabilities Header ================//
		public void insrtPotAndCurrLiabilitiesHeader(Document doc, String s_potAndCurrLiabilitiesHeader, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
							    
			String compiledQuery = "INSERT INTO CRIB_CON_POTENTIAL_AND_CURRENT_LIABILITIES_HEADER (REQUEST_DETAIL_ID, BUREAU_CURRENCY, PRIORITY, MONTHYEAR, IS_ACTIVE, CREATED, CREATED_BY) " +
								   "VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
							
			NodeList nodeList = (NodeList) xPath.compile(s_potAndCurrLiabilitiesHeader).evaluate(doc, XPathConstants.NODESET);
					
			for(int x=0; x<nodeList.getLength(); x++) {
								  			
				Element potAndCurrLiabilitiesHeader = (Element) nodeList.item(x);
				if(potAndCurrLiabilitiesHeader.hasChildNodes()) {	
					String currency  = (potAndCurrLiabilitiesHeader.getElementsByTagName("BUREAU_CURRENCY").getLength() > 0) ? potAndCurrLiabilitiesHeader.getElementsByTagName("BUREAU_CURRENCY").item(0).getTextContent() : "-";
					String priority  = (potAndCurrLiabilitiesHeader.getElementsByTagName("PRIORITY").getLength() > 0) ? potAndCurrLiabilitiesHeader.getElementsByTagName("PRIORITY").item(0).getTextContent() : null;	
					String monthYear = (potAndCurrLiabilitiesHeader.getElementsByTagName("MONTHYEAR").getLength() > 0) ? potAndCurrLiabilitiesHeader.getElementsByTagName("MONTHYEAR").item(0).getTextContent() : "-";
									
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, currency);						
					pstmt.setString(3, formatInts(priority));
					pstmt.setString(4, monthYear);
					pstmt.setInt(5, 1);
					pstmt.setTimestamp(6, created);
					pstmt.setString(7, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Potential And Current Liabilities ================//
		public void insrtPotAndCurrLiabilities(Document doc, String s_potAndCurrLiabilities, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_POTENTIAL_AND_CURRENT_LIABILITIES (REQUEST_DETAIL_ID, BUREAU_CURRENCY, OWNERSHIP, NO_OF_CREDIT_FACILITIES, TOTAL_AMOUNT_GRANTED, TOTAL_OUTSTANDING, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_potAndCurrLiabilities).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element potAndCurrLiabilities = (Element) nodeList.item(x);
				if(potAndCurrLiabilities.hasChildNodes()) {	
					String currency         = (potAndCurrLiabilities.getElementsByTagName("BUREAU_CURRENCY").getLength() > 0) ? potAndCurrLiabilities.getElementsByTagName("BUREAU_CURRENCY").item(0).getTextContent() : "-";
					String ownership        = (potAndCurrLiabilities.getElementsByTagName("OWNERSHIP_TYPE").getLength() > 0) ? potAndCurrLiabilities.getElementsByTagName("OWNERSHIP_TYPE").item(0).getTextContent() : "-";
					String numOfacilities   = (potAndCurrLiabilities.getElementsByTagName("TOTAL_NO_OF_CREDITFACILITIES").getLength() > 0) ? potAndCurrLiabilities.getElementsByTagName("TOTAL_NO_OF_CREDITFACILITIES").item(0).getTextContent() : null;
					String grantedAmt       = (potAndCurrLiabilities.getElementsByTagName("SANCTIONED_AMOUNT").getLength() > 0) ? potAndCurrLiabilities.getElementsByTagName("SANCTIONED_AMOUNT").item(0).getTextContent() : null;
					String totalOutstanding = (potAndCurrLiabilities.getElementsByTagName("TOTAL_OUTSTANDING").getLength() > 0) ? potAndCurrLiabilities.getElementsByTagName("TOTAL_OUTSTANDING").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, currency);
					pstmt.setString(3, ownership);
					pstmt.setString(4, formatInts(numOfacilities));
					pstmt.setObject(5, formatAmounts(grantedAmt));
					pstmt.setObject(6, formatAmounts(totalOutstanding));
					pstmt.setInt(7, 1);
					pstmt.setTimestamp(8, created);
					pstmt.setString(9, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Credit Facilities Of Glance Status ================//
		public void insrtCFOfGlanceStatus(Document doc, String s_cFOfGlanceStatus, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_CREDIT_FACILITY_OF_GLANCE_STATUS (REQUEST_DETAIL_ID, CATALOGUE_CODE, CATALOGUE_VAL_ENGLISH, MONTHYEAR, STATUS, ARREARS_DAYS_0, ARREARS_DAYS_1_30, ARREARS_DAYS_31_60, ARREARS_DAYS_61_90, ARREARS_DAYS_90, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_cFOfGlanceStatus).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element cFOfGlanceStatus = (Element) nodeList.item(x);
				if(cFOfGlanceStatus.hasChildNodes()) {
					String catalogueCode    = (cFOfGlanceStatus.getElementsByTagName("CATALOGUE_CODE").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("CATALOGUE_CODE").item(0).getTextContent() : "-";
					String catalogueVal     = (cFOfGlanceStatus.getElementsByTagName("CATALOGUE_VAL_ENGLISH").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("CATALOGUE_VAL_ENGLISH").item(0).getTextContent() : "-";
					String monthYear        = (cFOfGlanceStatus.getElementsByTagName("MONTHYEAR").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("MONTHYEAR").item(0).getTextContent() : "-";
					String status           = (cFOfGlanceStatus.getElementsByTagName("BUREAU_ACC_STATUS").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("BUREAU_ACC_STATUS").item(0).getTextContent() : "-";
					String arreasDays0      = (cFOfGlanceStatus.getElementsByTagName("ZERO").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("ZERO").item(0).getTextContent() : null;
					String arreasDays1_30   = (cFOfGlanceStatus.getElementsByTagName("ONETO30").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("ONETO30").item(0).getTextContent() : null;
					String arreasDays31_60  = (cFOfGlanceStatus.getElementsByTagName("THIRTYONETO60").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("THIRTYONETO60").item(0).getTextContent() : null;
					String arreasDays61_90  = (cFOfGlanceStatus.getElementsByTagName("SIXTYONETO90").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("SIXTYONETO90").item(0).getTextContent() : null;
					String arreasDaysOver90 = (cFOfGlanceStatus.getElementsByTagName("NINETY").getLength() > 0) ? cFOfGlanceStatus.getElementsByTagName("NINETY").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, catalogueCode);
					pstmt.setString(3, catalogueVal);
					pstmt.setString(4, monthYear);
					pstmt.setString(5, status);
					pstmt.setObject(6, formatInts(arreasDays0));
					pstmt.setObject(7, formatInts(arreasDays1_30));
					pstmt.setObject(8, formatInts(arreasDays31_60));
					pstmt.setObject(9, formatInts(arreasDays61_90));
					pstmt.setObject(10, formatInts(arreasDaysOver90));
					pstmt.setInt(11, 1);
					pstmt.setTimestamp(12, created);
					pstmt.setString(13, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Dishonoured Cheque Summary ================//
		public void insrtDishonChequeSummary(Document doc, String s_dishonChequeSummary, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_DISHONOURED_CHEQUE_SUMMARY (REQUEST_DETAIL_ID, NUMBER_OF_CHEQUES, CHEQUE_VALUE, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_dishonChequeSummary).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element dishonChequeSummary = (Element) nodeList.item(x);
				if(dishonChequeSummary.hasChildNodes()) {	
					String numberOfCheques = (dishonChequeSummary.getElementsByTagName("NO_OF_DC").getLength() > 0) ? dishonChequeSummary.getElementsByTagName("NO_OF_DC").item(0).getTextContent() : null;
					String chequeValue     = (dishonChequeSummary.getElementsByTagName("TOT_AMT_OF_DC").getLength() > 0) ? dishonChequeSummary.getElementsByTagName("TOT_AMT_OF_DC").item(0).getTextContent() : null;
					String ruid            = (dishonChequeSummary.getElementsByTagName("RUID").getLength() > 0) ? dishonChequeSummary.getElementsByTagName("RUID").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setObject(2, formatInts(numberOfCheques));
					pstmt.setObject(3, formatAmounts(chequeValue));
					pstmt.setObject(4, formatInts(ruid));
					pstmt.setInt(5, 1);
					pstmt.setTimestamp(6, created);
					pstmt.setString(7, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Dishonoured Cheque Details ================//
		public void insrtDishonChequeDetails(Document doc, String s_dishonChequeDetails, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException, ParseException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_DISHONOURED_CHEQUE_DETAILS (REQUEST_DETAIL_ID, SNO, INSTITUTION_AND_BRANCH, CHEQUE_NUMBER, CHEQUE_AMOUNT, DATE_DISHONOURED, REASON, ACCOUNT_NUMBER, BLOCK_FLAG, RUID, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_dishonChequeDetails).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element dishonChequeDetails = (Element) nodeList.item(x);
				if(dishonChequeDetails.hasChildNodes()) {
					String sno 			= (dishonChequeDetails.getElementsByTagName("SNO").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("SNO").item(0).getTextContent() : null;
					String instAndBranch = (dishonChequeDetails.getElementsByTagName("INSTBRANCH").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("INSTBRANCH").item(0).getTextContent() : "-";
					String chequeNumber = (dishonChequeDetails.getElementsByTagName("CHEQUE_NUMBER").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("CHEQUE_NUMBER").item(0).getTextContent() : "-";
					String chequeAmount = (dishonChequeDetails.getElementsByTagName("CHEQUE_AMOUNT").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("CHEQUE_AMOUNT").item(0).getTextContent() : null;
					String disHonDate   = (dishonChequeDetails.getElementsByTagName("DATE_DISHONOURED").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("DATE_DISHONOURED").item(0).getTextContent() : null;
					String reason       = (dishonChequeDetails.getElementsByTagName("REASON_FOR_DISHONOUR").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("REASON_FOR_DISHONOUR").item(0).getTextContent() : "-";
					String accountNo 	= (dishonChequeDetails.getElementsByTagName("ACCOUNT_NUMBER").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("ACCOUNT_NUMBER").item(0).getTextContent() : "-";
					String blockFlag    = (dishonChequeDetails.getElementsByTagName("BLOCK_FLAG").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("BLOCK_FLAG").item(0).getTextContent() : null;
					String ruid         = (dishonChequeDetails.getElementsByTagName("RUID").getLength() > 0) ? dishonChequeDetails.getElementsByTagName("RUID").item(0).getTextContent() : null;
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sno));
					pstmt.setString(3, instAndBranch);
					pstmt.setString(4, chequeNumber);
					pstmt.setObject(5, formatAmounts(chequeAmount));
					pstmt.setObject(6, formatDates(disHonDate));									
					pstmt.setString(7, reason);
					pstmt.setString(8, accountNo);
					pstmt.setString(9, formatInts(blockFlag));
					pstmt.setString(10, formatInts(ruid));
					pstmt.setInt(11, 1);
					pstmt.setTimestamp(12, created);
					pstmt.setString(13, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Catalogue Description ================//
		public void insrtCatalogueDescription(Document doc, String s_catalogueDescription, int requestDetailId, String username, Connection conn) throws SQLException, XPathExpressionException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_CATALOGUE_DESCRIPTION (REQUEST_DETAIL_ID, SNO, CATG_LABEL, CATG_VALUE, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
			
			NodeList nodeList = (NodeList) xPath.compile(s_catalogueDescription).evaluate(doc, XPathConstants.NODESET);
	
			for(int x=0; x<nodeList.getLength(); x++) {
				  			
				Element catalogueDescription = (Element) nodeList.item(x);
				if(catalogueDescription.hasChildNodes()) {	
					String sno 	 = (catalogueDescription.getElementsByTagName("SerialNumber").getLength() > 0) ? catalogueDescription.getElementsByTagName("SerialNumber").item(0).getTextContent() : null;
					String label = (catalogueDescription.getElementsByTagName("Label").getLength() > 0) ? catalogueDescription.getElementsByTagName("Label").item(0).getTextContent() : "-";
					String value = (catalogueDescription.getElementsByTagName("Values").getLength() > 0) ? catalogueDescription.getElementsByTagName("Values").item(0).getTextContent().replace("'", "`") : "-";
					  	
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, formatInts(sno));
					pstmt.setString(3, label);
					pstmt.setString(4, value);
					pstmt.setInt(5, 1);
					pstmt.setTimestamp(6, created);
					pstmt.setString(7, username);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Search Results ================//
		public void insrtSearchResults(Document doc, int requestDetailId, String username, Connection conn) throws SQLException {
			    
			String compiledQuery = "INSERT INTO CRIB_CON_SEARCH_RESULTS (REQUEST_DETAIL_ID, NAME, BUREAU_ID, IDENTIFIER_ID_SOURCE, IDENTIFIER_VALUE, IDENTIFIER_MATCHED, SURROGATE_ID, SURROGATE_MATCHED, IS_ACTIVE, CREATED, CREATED_BY) " +
					   			   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
	
			for(int x=0; x<doc.getElementsByTagName("SEARCH-RESULT-ITEM").getLength(); x++) {
					
				Element searchResultItem = (Element) doc.getElementsByTagName("SEARCH-RESULT-ITEM").item(x);
				String name = searchResultItem.getAttribute("NAME");
				String bureauId = searchResultItem.getAttribute("BUREAU-ID");
						
				if(searchResultItem.hasChildNodes()) {
					Element identifier = (Element) searchResultItem.getElementsByTagName("IDENTIFIER").item(0);
					String identifierSource = identifier.getAttribute("IDSOURCE");
					String identifierValue = identifier.getAttribute("IDVALUE");
					String identifierMatched = identifier.getAttribute("MATCHED");	
					Element surrogate = (Element) searchResultItem.getElementsByTagName("SURROGATE").item(0);
					String surrogateId = surrogate.getAttribute("ID");
					String surrogateMatched = surrogate.getAttribute("MATCHED");
						
					pstmt.setInt(1, requestDetailId);
					pstmt.setString(2, name);
					pstmt.setString(3, bureauId);
					pstmt.setString(4, identifierSource);
					pstmt.setString(5, identifierValue);
					pstmt.setString(6, identifierMatched);
					pstmt.setString(7, surrogateId);
					pstmt.setString(8, surrogateMatched);
					pstmt.setInt(9, 1);
					pstmt.setTimestamp(10, created);
					pstmt.setString(11, username);
					pstmt.addBatch();
				}		
			}
			pstmt.executeBatch();
			pstmt.close();
		}
		
		//================ Setting Error ================//
		public void insrtRequestErrors(Document doc, int requestDetailId, String username, Connection conn) throws SQLException {
					
			String compiledQuery = "INSERT INTO CRIB_REQUEST_ERROR (REQUEST_DETAIL_ID, ERROR_CODE, ERROR_DESCRIPTION, IS_ACTIVE, CREATED, CREATED_BY) " +
			                	   "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(compiledQuery);
						
			for(int x=0; x<doc.getElementsByTagName("ERROR").getLength(); x++) {
						
				String errorCode = (doc.getElementsByTagName("ERROR-CODE").getLength() > 0) ? doc.getElementsByTagName("ERROR-CODE").item(x).getTextContent() : "-";
				String errorDescription = (doc.getElementsByTagName("ERROR").getLength() > 0) ? doc.getElementsByTagName("ERROR").item(x).getTextContent() : "-";

				pstmt.setInt(1, requestDetailId);
				pstmt.setString(2, errorCode);
				pstmt.setString(3, errorDescription);
				pstmt.setInt(4, 1);
				pstmt.setTimestamp(5, created);
				pstmt.setString(6, username);
				pstmt.addBatch();				
			}
			pstmt.executeBatch();
			pstmt.close();
		}
				
		//================ Get SEARCH-RESULT-LIST ================//
		public SearchResults getSearchResults(Document doc) {
			
			SearchResults searchResults = new SearchResults();

			List<SearchResultItem> searchResultItemList = new ArrayList<SearchResultItem>();
			for(int x=0; x<doc.getElementsByTagName("SEARCH-RESULT-ITEM").getLength(); x++) {
				
				SearchResultItem srchRsltItem = new SearchResultItem();
				Element searchResultItem = (Element) doc.getElementsByTagName("SEARCH-RESULT-ITEM").item(x);
				srchRsltItem.setName(searchResultItem.getAttribute("NAME"));
				srchRsltItem.setBureauId(searchResultItem.getAttribute("BUREAU-ID"));	
					
				if(searchResultItem.hasChildNodes()) {
					Element identifier = (Element) searchResultItem.getElementsByTagName("IDENTIFIER").item(0);
					srchRsltItem.setIdentifierSource(identifier.getAttribute("IDSOURCE"));
					srchRsltItem.setIdentifierValue(identifier.getAttribute("IDVALUE"));
					srchRsltItem.setIdentifierMatched(identifier.getAttribute("MATCHED"));	
					Element surrogate = (Element) searchResultItem.getElementsByTagName("SURROGATE").item(0);
					srchRsltItem.setSurrogate(surrogate.getAttribute("ID"));
					srchRsltItem.setSurrogateMatched(surrogate.getAttribute("MATCHED"));					
				}
				searchResultItemList.add(srchRsltItem);		
			}
			searchResults.setSearchResultItem(searchResultItemList);	
		
			return searchResults;
		}
			
		//================ Get MultiHit response ================//
		public MultiHit getMultiHitResponse(Document doc) {
			
			MultiHit multiHit = new MultiHit();
			multiHit.setReferenceNumber(doc.getDocumentElement().getAttribute("REFERENCE-NO"));

			List<SearchResultItem> searchResultItemList = new ArrayList<SearchResultItem>();
			for(int x=0; x<doc.getElementsByTagName("SEARCH-RESULT-ITEM").getLength(); x++) {
				
				SearchResultItem srchRsltItem = new SearchResultItem();
				Element searchResultItem = (Element) doc.getElementsByTagName("SEARCH-RESULT-ITEM").item(x);
				srchRsltItem.setName(searchResultItem.getAttribute("NAME"));
				srchRsltItem.setBureauId(searchResultItem.getAttribute("BUREAU-ID"));	
					
				if(searchResultItem.hasChildNodes()) {
					Element identifier = (Element) searchResultItem.getElementsByTagName("IDENTIFIER").item(0);
					srchRsltItem.setIdentifierSource(identifier.getAttribute("IDSOURCE"));
					srchRsltItem.setIdentifierValue(identifier.getAttribute("IDVALUE"));
					srchRsltItem.setIdentifierMatched(identifier.getAttribute("MATCHED"));	
					Element surrogate = (Element) searchResultItem.getElementsByTagName("SURROGATE").item(0);
					srchRsltItem.setSurrogate(surrogate.getAttribute("ID"));
					srchRsltItem.setSurrogateMatched(surrogate.getAttribute("MATCHED"));					
				}
				searchResultItemList.add(srchRsltItem);		
			}
			multiHit.setSearchResultItem(searchResultItemList);	
		
			return multiHit;
		}
	
		// Format amounts to insert DB
		public String formatAmounts(String amount) {
			String amt = null;
            if(amount != null && amount.trim().length() != 0){
            	amt = amount.replaceAll(",", "").trim();           	    	
            }   
			return amt;
		}
		
		// Format dates to insert DB
		public String formatDates(String dateVal) {
			String date = null;
            if(dateVal != null && dateVal.trim().length() != 0){
            	
            	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            	try {
            		Date dateMM = sdf.parse(dateVal);
            		sdf.setLenient(false);            		
            		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                    date = newFormat.format(dateMM);
            	} catch (ParseException e) {
            		date = dateVal.split("\\+")[0].replaceAll("T", " ");            		
                }                  	
            }   
			return date;
		}
		
		// Format ints to insert DB
		public String formatInts(String number) {
			String no = null;
			if(number != null && number.trim().length() != 0){
				no = number.trim();           	    	
			}   
			return no;
		}

}
