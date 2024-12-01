package com.gs.api;

import java.beans.BeanDescriptor;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CribApiCallBeanInfo extends SimpleBeanInfo {
	private final static Class beanClass = CribApiCall.class;

	@Override
	public BeanDescriptor getBeanDescriptor() {
	    BeanDescriptor bd = new BeanDescriptor(beanClass);
	    return bd;
	}
	
	@Override
	public MethodDescriptor[] getMethodDescriptors() {
		try {
			Method method = null;
			ParameterDescriptor parm = null;
			List<ParameterDescriptor> parmDescriptors = null; ParameterDescriptor[] parms = null;
			MethodDescriptor methodDescriptor = null;
			List<MethodDescriptor> methodDescriptors = null; MethodDescriptor[] methods = null;
			methodDescriptors = new ArrayList<MethodDescriptor>();
						
			// public String cribApiInvoke(int requestDetailId, String requestJson, String requestType, String username, String apiUsername, String apiPassword, String encryptionKey, String decryptionKey)
			method = beanClass.getMethod("cribApiInvoke", java.lang.Integer.TYPE, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class);
			parmDescriptors = new ArrayList<ParameterDescriptor>();
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("requestDetailId");
			parm.setDisplayName("requestDetailId");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("requestJson");
			parm.setDisplayName("requestJson");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("requestType");
			parm.setDisplayName("requestType");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("username");
			parm.setDisplayName("username");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("apiUsername");
			parm.setDisplayName("apiUsername");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("apiPassword");
			parm.setDisplayName("apiPassword");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("encryptionKey");
			parm.setDisplayName("encryptionKey");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("decryptionKey");
			parm.setDisplayName("decryptionKey");
			parmDescriptors.add(parm);	
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("xmlTagsJson");
			parm.setDisplayName("xmlTagsJson");
			parmDescriptors.add(parm);	
			// methodDescriptor
			parms = new ParameterDescriptor[parmDescriptors.size()];
			parmDescriptors.toArray(parms);
			methodDescriptor = new MethodDescriptor(method, parms);
			methodDescriptors.add(methodDescriptor);

			// return
			methods = new MethodDescriptor[methodDescriptors.size()];
			methodDescriptors.toArray(methods);
			return methods;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return super.getMethodDescriptors();
	}
}
