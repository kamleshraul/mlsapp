package org.mkcl.els.repository;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Repository;

@Repository
public class QueryRepository extends BaseRepository<Query, Serializable>{

	@SuppressWarnings("rawtypes")
	public List findReport(final String report,final Map<String, String[]> requestMap) {
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			Query query = Query.findByFieldName(Query.class, "keyField", report, locale);
			String queryString = query.getQuery();
			/**** in case selected fields of query need to be dynamically taken (e.g. in extended grid report) ****/
			/** for fields **/
			if(requestMap.get("field_select_query")!=null 
					&& requestMap.get("field_select_query")[0]!=null
					&& queryString.contains("field_select_query")) {
				String field_select_query = requestMap.get("field_select_query")[0];
//				CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//				if(deploymentServerCP.getValue().equals("TOMCAT")){
//					try {
//						field_select_query = new String(field_select_query.getBytes("ISO-8859-1"),"UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}		
//				}
				queryString = queryString.replace("field_select_query", field_select_query);
			}
			/** for headers **/
			if(requestMap.get("field_header_select_query")!=null 
					&& requestMap.get("field_header_select_query")[0]!=null
					&& queryString.contains("field_header_select_query")) {
				String field_header_select_query = requestMap.get("field_header_select_query")[0];
//				CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//				if(deploymentServerCP.getValue().equals("TOMCAT")){
//					try {
//						field_header_select_query = new String(field_header_select_query.getBytes("ISO-8859-1"),"UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}		
//				}
				queryString = queryString.replace("field_header_select_query", field_header_select_query);
			}
			//====================================================================================================
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(queryString);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
			for (Parameter i : selectQueryParameters) {
				if(requestMap.get(i.getName())==null) {
					System.out.println(i.getName());
				}
				String param=requestMap.get(i.getName())[0];
				String decodedParam=param;
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}            	
				if(decodedParam.equals("true") || decodedParam.equals("false")){
					persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
				}else{
					persistenceQuery.setParameter(i.getName(),decodedParam);
				}
			}
			List results=persistenceQuery.getResultList();
			
			/** handling for serial number generation **/
			if(results!=null && !results.isEmpty() && results.get(0)!=null && results.get(0).getClass().equals(Object[].class)) {
				Object[] firstResult = (Object[]) results.get(0);
				if(firstResult!=null && firstResult[0]!=null && firstResult[0].toString().contains("serialNumber")) {
					int rowIndex=0;
					for(Object r: results) {
						Object[] row = (Object[]) r;
						String serialNumber = FormaterUtil.formatNumberNoGrouping(rowIndex+1, locale);
						row[0] = row[0].toString().replace("serialNumber", serialNumber);
						rowIndex++;
					}
				}
			}			
			
			return results;
		}		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public List findReport(final String report,final Map<String, String[]> requestMap, final Boolean handleIN) {
		if(handleIN){
			if(requestMap!=null&&!requestMap.isEmpty()){
				String locale=requestMap.get("locale")[0];
				Query query = Query.findByFieldName(Query.class, "keyField", report, locale);
				String queryString = query.getQuery();
				/**** in case selected fields of query need to be dynamically taken (e.g. in extended grid report) ****/
				/** for fields **/
				if(requestMap.get("field_select_query")!=null 
						&& requestMap.get("field_select_query")[0]!=null
						&& queryString.contains("field_select_query")) {
					String field_select_query = requestMap.get("field_select_query")[0];
//					CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//					if(deploymentServerCP.getValue().equals("TOMCAT")){
//						try {
//							field_select_query = new String(field_select_query.getBytes("ISO-8859-1"),"UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						}		
//					}
					queryString = queryString.replace("field_select_query", field_select_query);
				}
				/** for headers **/
				if(requestMap.get("field_header_select_query")!=null 
						&& requestMap.get("field_header_select_query")[0]!=null
						&& queryString.contains("field_header_select_query")) {
					String field_header_select_query = requestMap.get("field_header_select_query")[0];
//					CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//					if(deploymentServerCP.getValue().equals("TOMCAT")){
//						try {
//							field_header_select_query = new String(field_header_select_query.getBytes("ISO-8859-1"),"UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						}		
//					}
					queryString = queryString.replace("field_header_select_query", field_header_select_query);
				}
				//====================================================================================================
				//To handle the parameter setting in IN clause value
				int indexOfIN = queryString.indexOf(" IN "); 
				int index = ((indexOfIN==-1)? ((queryString.indexOf("in")==-1)? -1:queryString.indexOf("in")): indexOfIN);
				String inParameter = queryString.substring(index+"IN".length()).trim();
				inParameter = inParameter.substring(inParameter.indexOf(":")+1,inParameter.indexOf(")"));
				
				javax.persistence.Query persistenceQuery=this.em().createNativeQuery(queryString);
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
				Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
				for (Parameter i : selectQueryParameters) {
					if(requestMap.get(i.getName())==null) {
						System.out.println(i.getName());
					}
					String param=requestMap.get(i.getName())[0];
					String decodedParam=param;
					if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
						try {
							decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}            	
					if(decodedParam.equals("true") || decodedParam.equals("false")){
						persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
					}else{
						if(i.getName().equals(inParameter)){
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
				
				/** handling for serial number generation **/
				if(results!=null && !results.isEmpty() && results.get(0)!=null && results.get(0).getClass().equals(Object[].class)) {
					Object[] firstResult = (Object[]) results.get(0);
					if(firstResult!=null && firstResult[0]!=null && firstResult[0].toString().contains("serialNumber")) {
						int rowIndex=0;
						for(Object r: results) {
							Object[] row = (Object[]) r;
							String serialNumber = FormaterUtil.formatNumberNoGrouping(rowIndex+1, locale);
							row[0] = row[0].toString().replace("serialNumber", serialNumber);
							rowIndex++;
						}
					}
				}
				
				return results;
			}
		}else{
			return findReport(report, requestMap);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List findResultListOfGivenClass(final String report,final Map<String, String[]> requestMap, Class className) {
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			Query query = Query.findByFieldName(Query.class, "keyField", report, locale);
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery(), className);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
			for (Parameter i : selectQueryParameters) {
				String param=requestMap.get(i.getName())[0];
				String decodedParam=param;
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}            	
				if(decodedParam.equals("true") || decodedParam.equals("false")){
					persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
				}else{
					persistenceQuery.setParameter(i.getName(),decodedParam);
				}
			}
			List results=persistenceQuery.getResultList();
			return results;
		}		
		return null;
	} 
	
	@SuppressWarnings("rawtypes")
	public List findResultListOfGivenClass(final String report,final Map<String, String[]> requestMap, Class className, final Boolean handleIN) {
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			Query query = Query.findByFieldName(Query.class, "keyField", report, locale);
			
			//To handle the parameter setting in IN clause value
			int indexOfIN = query.getQuery().indexOf(" IN "); 
			int index = ((indexOfIN==-1)? ((query.getQuery().indexOf("in")==-1)? -1:query.getQuery().indexOf("in")): indexOfIN);
			String inParameter = query.getQuery().substring(index+"IN".length()).trim();
			inParameter = inParameter.substring(inParameter.indexOf(":")+1,inParameter.indexOf(")"));
			
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery(), className);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
			for (Parameter i : selectQueryParameters) {
				String param=requestMap.get(i.getName())[0];
				String decodedParam=param;
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}            	
				if(decodedParam.equals("true") || decodedParam.equals("false")){
					persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
				}else{
					if(i.getName().equals(inParameter)){
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
			return results;
		}		
		return null;
	}	
	
	@SuppressWarnings("rawtypes")
	public List findMISPartyDistrictReport(final String report,final Map<String, String[]> requestMap){
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			List<District> districts=District.findAll(District.class,"name",ApplicationConstants.ASC, locale);
			List<Gender> genders=Gender.findAll(Gender.class,"name",ApplicationConstants.ASC, locale);
			String selectClause1="SELECT p.name AS group_party_wise,\n"+
				"COUNT(p.name) AS totalmembers_in_each_group";
			StringBuffer genderClause=new StringBuffer();
			for(Gender i:genders){
				if(i.getType().equals("male")){
					genderClause.append("SUM(CASE WHEN g.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS totalmalecount,\n" );
				}else{
					genderClause.append("SUM(CASE WHEN g.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS totalfemalecount,\n" );
				}
			}
			genderClause.deleteCharAt(genderClause.length()-1);
			genderClause.deleteCharAt(genderClause.length()-1);
			StringBuffer districtClause=new StringBuffer();
			for(District i:districts){
				String[] parts=i.getName().split(" ");
				if(parts.length==1){
					districtClause.append("SUM(CASE WHEN d.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS '"+i.getName()+"',\n" );
				}else if(parts.length==2){
					districtClause.append("SUM(CASE WHEN d.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS '"+parts[0]+parts[1]+"',\n" );
				}else if(parts.length==3){
					districtClause.append("SUM(CASE WHEN d.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS '"+parts[0]+parts[1]+parts[2]+"',\n" );
				}
			}
			districtClause.deleteCharAt(districtClause.length()-1);
			districtClause.deleteCharAt(districtClause.length()-1);
			String fromClause="FROM members AS m\n"+
			"JOIN genders AS g\n"+
			"JOIN members_houses_roles AS mhr\n"+
			"JOIN houses AS h\n"+
			"JOIN memberroles AS mr\n"+
			"JOIN members_parties AS mp\n"+
			"JOIN parties AS p\n"+
			"JOIN constituencies AS c\n"+
			"LEFT JOIN reservations AS r ON(c.reservation_id = r.id)\n"+
			"LEFT JOIN constituencies_districts AS cd ON(cd.constituency_id=c.id)\n"+ 
			"LEFT JOIN districts AS d ON(cd.district_id=d.id)\n"+
			"LEFT JOIN titles AS t ON(m.title_id = t.id)\n"+ 
			"WHERE g.id=m.gender_id\n"+
			"AND mhr.member = m.id\n"+
			"AND mhr.house_id=h.id\n"+
			"AND mhr.role=mr.id\n"+
			"AND mp.member=m.id\n"+
			"AND mp.party = p.id\n"+
			"AND mhr.constituency_id=c.id\n"+
			"AND mr.priority=0\n"+
			"AND c.is_retired=FALSE\n"+
			"AND m.death_date IS NULL\n"+
			"AND (mhr.to_date IS NULL OR mhr.to_date >=CURDATE())\n"+
			"AND mhr.from_date <=CURDATE()\n"+
			"AND (mp.to_date IS NULL OR mp.to_date >=CURDATE())\n"+
			"AND mp.from_date <= CURDATE()\n"+ 
			"AND h.id=:house\n"+
			"AND m.locale=:locale\n"+
			"GROUP BY p.name\n";
			String query1=selectClause1+",\n"+genderClause.toString()+",\n"+districtClause.toString()+"\n"+fromClause;
			String selectClause2="SELECT 'Total',\n"+
			"SUM(dt.totalmembers_in_each_group),\n"+
			"SUM(dt.totalmalecount),\n"+
			"SUM(dt.totalfemalecount)";
			StringBuffer districtClause2=new StringBuffer();
			for(District i:districts){
				String[] parts=i.getName().split(" ");
				if(parts.length==1){
					districtClause2.append("SUM(dt."+i.getName()+"),\n" );
				}else if(parts.length==2){
					districtClause2.append("SUM(dt."+parts[0]+parts[1]+"),\n" );
				}else if(parts.length==3){
					districtClause2.append("SUM(dt."+parts[0]+parts[1]+parts[2]+"),\n" );
				}
			}
			districtClause2.deleteCharAt(districtClause2.length()-1);
			districtClause2.deleteCharAt(districtClause2.length()-1);
			String query2=selectClause2+",\n"+districtClause2+"\nFROM\n(\n"+query1+") as dt";
			String finalQuery=query1+"\nUNION\n"+query2;
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(finalQuery);
			persistenceQuery.setParameter("house",requestMap.get("house")[0]);
			persistenceQuery.setParameter("locale",locale);
			List results=persistenceQuery.getResultList();
			return results;
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public List findReport(final String report,final Map<String, String[]> requestMap, Integer start, Integer end) {
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			Query query = Query.findByFieldName(Query.class, "keyField", report, locale);
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
			for (Parameter i : selectQueryParameters) {
				String param=requestMap.get(i.getName())[0];
				String decodedParam=param;
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}            	
				if(decodedParam.equals("true") || decodedParam.equals("false")){
					persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
				}else{
					persistenceQuery.setParameter(i.getName(),decodedParam);
				}
			}
			persistenceQuery.setFirstResult(start.intValue()).setMaxResults(end.intValue());
			List results=persistenceQuery.getResultList();
			return results;
		}		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public List findReportWithIn(final String report,final Map<String, String[]> parameterMap) {
		try{
			if(parameterMap!=null&&!parameterMap.isEmpty()){
				
				Query query = Query.findByFieldName(Query.class, "keyField", report, "");
				String actualQuery = query.getQuery();
				for(Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
					if(actualQuery.contains(":" + entry.getKey())){
						actualQuery = actualQuery.replaceAll(":"+entry.getKey(), entry.getValue()[0]);
					}
				}
				javax.persistence.Query pQuery = em().createNativeQuery(actualQuery);
				List results = pQuery.getResultList();
				
				return results;
			}		
		}catch(Exception e){
			
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public String generateDynamicInnerQueryText(final String innerQuery,final Map<String, String[]> requestMap) {
		if(requestMap!=null&&!requestMap.isEmpty()){
			String locale=requestMap.get("locale")[0];
			Query query = Query.findByFieldName(Query.class, "keyField", innerQuery, locale);
			String queryString = query.getQuery();
			javax.persistence.Query persistenceQuery=this.em().createNativeQuery(queryString);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
			Set<Parameter<?>> selectQueryParameters = persistenceQuery.getParameters();
			for (Parameter i : selectQueryParameters) {
				if(requestMap.get(i.getName())==null) {
					System.out.println(i.getName());
				}
				String param=requestMap.get(i.getName())[0];
				String decodedParam=param;
				if(customParameter!=null&&customParameter.getValue().equals("TOMCAT")){							
					try {
						decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}            	
				if(decodedParam.equals("true") || decodedParam.equals("false")){
					persistenceQuery.setParameter(i.getName(),((decodedParam.equals("true"))? true: false));
				}else{
					persistenceQuery.setParameter(i.getName(),decodedParam);
				}
			}
			List results=persistenceQuery.getResultList();
			
			if(results!=null && !results.isEmpty() && results.get(0)!=null && results.get(0).getClass().equals(String.class)) {
				return results.get(0).toString();
			} else {		
				return "";
			}
		}		
		return "";
	}
}
