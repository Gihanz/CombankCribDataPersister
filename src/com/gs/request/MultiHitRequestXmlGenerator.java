package com.gs.request;

public class MultiHitRequestXmlGenerator {
	
	public String generateReqXml(MultiHitRequestBean requestObj) {
		
		  		 String xmlRequest = "<REQUEST REQUEST_ID=\""+requestObj.getRequestId()+"\">";
		  xmlRequest = xmlRequest +  "<REQUEST_PARAMETERS>";
		  xmlRequest = xmlRequest +    "<REPORT_PARAMETERS REPORT_ID=\""+requestObj.getReportId()+"\" SUBJECT_TYPE=\""+requestObj.getSubjectType()+"\" RESPONSE_TYPE=\""+requestObj.getResponseType()+"\"/>";
		  xmlRequest = xmlRequest +    "<INQUIRY_REASON CODE=\""+requestObj.getReasonCode()+"\"/>";
		  if(requestObj.getIsApplicationProvided().equals("TRUE")) {
			   xmlRequest = xmlRequest +    "<APPLICATION PRODUCT=\""+requestObj.getProduct()+"\" NUMBER=\""+requestObj.getNumber()+"\" DATE=\""+requestObj.getDate()+"\" AMOUNT=\""+requestObj.getAmount()+"\" CURRENCY=\""+requestObj.getCurrency()+"\"/>";			   
		  }
		  xmlRequest = xmlRequest +    "<REQUEST_REFERENCE REFERENCE-NO=\""+requestObj.getReferenceNumber()+"\" BUREAU-ID=\""+requestObj.getBureauId()+"\"></REQUEST_REFERENCE>";
		  xmlRequest = xmlRequest +  "</REQUEST_PARAMETERS>";
		  xmlRequest = xmlRequest + "</REQUEST>";

  		  return xmlRequest;
	}

}
