package org.mkcl.els.repository;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.notification.NotificationTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationTemplateRepository extends BaseRepository<NotificationTemplate, Serializable>{

	public String generateNotificationMessage(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
		if(templateParameters!=null&&!templateParameters.isEmpty()){			
			String queryString = notificationTemplate.getTemplateQuery();
			return this.generateTextFromQuery(queryString, templateParameters);
		}		
		return "";
	}
	
	public String generateNotificationTitle(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
		if(templateParameters!=null&&!templateParameters.isEmpty()){			
			String queryString = notificationTemplate.getTitleQuery();
			return this.generateTextFromQuery(queryString, templateParameters);
		}		
		return "";
	}
	
	public String generateNotificationReceivers(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
		if(templateParameters!=null&&!templateParameters.isEmpty()){			
			String queryString = notificationTemplate.getReceiversQuery();
			return this.generateTextFromQuery(queryString, templateParameters);
		}		
		return "";
	}
	
	@SuppressWarnings("rawtypes")
	private String generateTextFromQuery(final String queryString, final Map<String, String[]> templateParameters) {
		//To handle the parameter setting of IN clause value
		int indexOfIN = queryString.indexOf(" IN "); 
		//int index = ((indexOfIN==-1)? ((queryString.indexOf(" IN ")==-1)? -1:queryString.indexOf(" IN ")): indexOfIN);
		String inParameter = "";
		if(indexOfIN != -1) {
			inParameter = queryString.substring(indexOfIN+" IN ".length()).trim();
			inParameter = inParameter.substring(inParameter.indexOf(":")+1,inParameter.indexOf(")"));
		}
		Query persistenceQuery=this.em().createNativeQuery(queryString);
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
		for (Parameter i : selectQueryParameters) {
			if(templateParameters.get(i.getName())==null) {
				System.out.println(i.getName());
			}
			String param=templateParameters.get(i.getName())[0];
			String decodedParam=param;
			if(templateParameters.get(i.getName()).length>1 && templateParameters.get(i.getName())[1].equals("requires_decoding")) {
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}			            	
			if(decodedParam.equals("true") || decodedParam.equals("false")){
				persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
			}else{
				if(!inParameter.isEmpty() && i.getName().equals(inParameter)){
					List<String> values = new ArrayList<String>();
					
					for(String val : decodedParam.split(",")){
						values.add(val.trim());
					}
					persistenceQuery.setParameter(i.getName(),values);
				}else{
					persistenceQuery.setParameter(i.getName(),decodedParam);
				}
			}
		}
		List results=persistenceQuery.getResultList();
		
		if(results!=null && !results.isEmpty() && results.get(0)!=null && results.get(0).getClass().equals(String.class)) {
			return results.get(0).toString();
		} else {		
			return "";
		}
	}
}
