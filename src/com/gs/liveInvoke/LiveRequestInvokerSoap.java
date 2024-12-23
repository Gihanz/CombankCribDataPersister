package com.gs.liveInvoke;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.10
 * 2021-01-14T21:47:24.325+05:30
 * Generated source version: 2.7.10
 * 
 */
@WebService(targetNamespace = "http://tempuri.org/", name = "LiveRequestInvokerSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface LiveRequestInvokerSoap {

    @WebMethod(operationName = "PostRequest", action = "http://tempuri.org/PostRequest")
    @RequestWrapper(localName = "PostRequest", targetNamespace = "http://tempuri.org/", className = "org.tempuri.PostRequest")
    @ResponseWrapper(localName = "PostRequestResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.PostRequestResponse")
    @WebResult(name = "PostRequestResult", targetNamespace = "http://tempuri.org/")
    public java.lang.String postRequest(
        @WebParam(name = "strRequest", targetNamespace = "http://tempuri.org/")
        java.lang.String strRequest,
        @WebParam(name = "strUserID", targetNamespace = "http://tempuri.org/")
        java.lang.String strUserID,
        @WebParam(name = "strPassword", targetNamespace = "http://tempuri.org/")
        java.lang.String strPassword
    );
}
