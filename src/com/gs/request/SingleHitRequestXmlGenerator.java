package com.gs.request;

public class SingleHitRequestXmlGenerator {

	public String generateReqXml(SingleHitRequestBean requestObj) {
				
		   		 String xmlRequest = "<REQUEST REQUEST_ID=\""+requestObj.getRequestId()+"\">";
		   xmlRequest = xmlRequest +  "<REQUEST_PARAMETERS>";
		   xmlRequest = xmlRequest +    "<REPORT_PARAMETERS REPORT_ID=\""+requestObj.getReportId()+"\" SUBJECT_TYPE=\""+requestObj.getSubjectType()+"\" RESPONSE_TYPE=\""+requestObj.getResponseType()+"\"/>";
		   xmlRequest = xmlRequest +    "<INQUIRY_REASON CODE=\""+requestObj.getReasonCode()+"\"/>";
		   if(requestObj.getIsApplicationProvided().equals("TRUE")) {
			   xmlRequest = xmlRequest +    "<APPLICATION PRODUCT=\""+requestObj.getProduct()+"\" NUMBER=\""+requestObj.getNumber()+"\" DATE=\""+requestObj.getDate()+"\" AMOUNT=\""+requestObj.getAmount()+"\" CURRENCY=\""+requestObj.getCurrency()+"\"/>";			   
		   }
		   xmlRequest = xmlRequest +  "</REQUEST_PARAMETERS>";
		   xmlRequest = xmlRequest +  "<SEARCH_PARAMETERS>";
		   xmlRequest = xmlRequest +    "<NAME><![CDATA["+requestObj.getName()+"]]></NAME>";
		   xmlRequest = xmlRequest +    "<ACCOUNT_NUMBER>"+requestObj.getAccountNumber()+"</ACCOUNT_NUMBER>";
		   xmlRequest = xmlRequest +    "<DATA_PROVIDER_BRANCH_ID>"+requestObj.getDataProviderBrnId()+"</DATA_PROVIDER_BRANCH_ID>";
		   if(requestObj.getSubjectType().equals("1")) {
			   xmlRequest = xmlRequest +    "<CITIZENSHIP>"+requestObj.getCitizenship()+"</CITIZENSHIP>";
		   }
		   xmlRequest = xmlRequest +    "<IDENTIFIERS>";		   
		   if(requestObj.getSubjectType().equals("1")) {
			   xmlRequest = xmlRequest +     "<NIC_NO>"+requestObj.getNic()+"</NIC_NO>";
			   xmlRequest = xmlRequest +     "<PASSPORT_NO>"+requestObj.getPassportNumber()+"</PASSPORT_NO>";
		   }else if (requestObj.getSubjectType().equals("0")){
			   xmlRequest = xmlRequest +     "<BUSINESS_REG_NO>"+requestObj.getBrNumber()+"</BUSINESS_REG_NO>";  
		   }
		   xmlRequest = xmlRequest +    "</IDENTIFIERS>";  
		   xmlRequest = xmlRequest +    "<SURROGATES>";		   
		   if(requestObj.getSubjectType().equals("1")) {
			   xmlRequest = xmlRequest +      "<GENDER>"+requestObj.getGender()+"</GENDER>";
			   xmlRequest = xmlRequest +      "<DOB>"+requestObj.getDob()+"</DOB>";	 			   
		   }
		   xmlRequest = xmlRequest +    "</SURROGATES>";
		   xmlRequest = xmlRequest +  "</SEARCH_PARAMETERS>";
		   xmlRequest = xmlRequest + "</REQUEST>";

		   return xmlRequest;
	}
}
