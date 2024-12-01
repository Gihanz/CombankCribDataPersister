package com.gs.bulk;

import java.beans.BeanDescriptor;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CribBulkExtractBeanInfo extends SimpleBeanInfo {
	private final static Class beanClass = CribBulkExtract.class;

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
						
			// public String cribBulkInvoke(int requestDetailId, String requestJson, String xmlLocation, String username, String encryptionKey)
			method = beanClass.getMethod("cribBulkInvoke", java.lang.Integer.TYPE, String.class, String.class, String.class, String.class, String.class);
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
			parm.setShortDescription("xmlLocation");
			parm.setDisplayName("xmlLocation");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("username");
			parm.setDisplayName("username");
			parmDescriptors.add(parm);
			// parm
			parm = new ParameterDescriptor();
			parm.setShortDescription("encryptionKey");
			parm.setDisplayName("encryptionKey");
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