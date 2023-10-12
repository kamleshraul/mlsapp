package org.mkcl.els.repository;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDateTime;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.common.vo.WorkFlowDetailsVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
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
	
	
	
		public List<SearchVO> getDeviceDetailsForSupportActivity(String param, String session,HouseType ht, DeviceType dt,
				String filter, Integer offset, Locale locale) {

			
			List<Object> Details = new ArrayList<Object>();
			List<SearchVO> ResponseResult = new ArrayList<SearchVO>();			
			House currentHouse = null;
			try {
				currentHouse = House.findCurrentHouse(locale.toString());
			} catch (ELSException e) {

				e.printStackTrace();
			}
			StringBuffer query = new StringBuffer();
			javax.persistence.Query nQuery = null;
			
			//Adding generic Present in All devices
			query.append("SELECT q.id,q.number,q.session.type.sessionType,  q.session.year,q.houseType.name , q.subDepartment.displayName, ");
			
			
			
			if(dt.getDeviceName().equals(ApplicationConstants.CUTMOTION)) {
				query.append(" q.revisedMainTitle ,q.deviceType.name, ");
			}else {
				if(dt.getDeviceName().equals(ApplicationConstants.PROPRIETYPOINT)) {
					query.append(" q.revisedSubject , q.deviceType.name, ");
				}else {
					query.append(" q.revisedSubject , q.type.name, ");
				}
				
			}
			
			/*
			 * 
			 * XXX-> Check if  resolution then add status as per houseType or just Add generic status types
			 *
			 */			
			if(dt.getDeviceName().equals(ApplicationConstants.RESOLUTION)) {
				if(ht.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					query.append(" q.statusLowerHouse.name,q.internalStatusLowerHouse.name,q.recommendationStatusLowerHouse.name,q.actorLowerHouse,q.internalStatusLowerHouse.type  ");
				}else {
					query.append(" q.statusUpperHouse.name,q.internalStatusUpperHouse.name,q.recommendationStatusUpperHouse.name,q.actorUpperHouse,q.internalStatusUpperHouse.type  ");
				}
				query.append(",q.member.title.name,q.member.firstName,q.member.lastName ");
			}else {
				query.append(" q.status.name,q.internalStatus.name,q.recommendationStatus.name,q.actor,q.internalStatus.type , q.primaryMember.title.name ,q.primaryMember.firstName,q.primaryMember.lastName ");
			}
			
			
			/*
			 * if Device is  XXX-> Question  or XXX-> Standalone then Add Group And Answer  And revisedQuestionText
			 *  XXX-> OR <-XXX
			 *  Add Reply field , revisedDetails
			 *  Last 
			 */						
			if(dt.getDeviceName().equals(ApplicationConstants.QUESTION) || dt.getDeviceName().equals(ApplicationConstants.STANDALONE_MOTION)) {
				query.append(" ,q.group.number ");
				if( !dt.getDeviceName().equals(ApplicationConstants.STANDALONE_MOTION) ) {
					query.append(" ,q.revisedQuestionText,q.answer ,q.parent.id  ");
				}else {
					query.append(" ,q.revisedBriefExplanation,q.answer ");
				}				
			}else if(dt.getDeviceName().equals(ApplicationConstants.ADJOURNMEBT_MOTION) 
					   || dt.getDeviceName().equals(ApplicationConstants.MOTION )
						|| dt.getDeviceName().equals(ApplicationConstants.CUTMOTION)
						   || dt.getDeviceName().equals(ApplicationConstants.PROPRIETYPOINT)
						   	 || dt.getDeviceName().equals(ApplicationConstants.RULES_SUSPENSION_MOTION)
						   	   || dt.getDeviceName().equals(ApplicationConstants.SPECIALMENTION_NOTICE) ) {				
				if(dt.getDeviceName().equals(ApplicationConstants.MOTION)) {
					query.append(" ,q.revisedDetails ");
				}
				else if(dt.getDeviceName().equals(ApplicationConstants.PROPRIETYPOINT)) {
					query.append(" ,q.revisedPointsOfPropriety ");
				}
				else {
					query.append(" ,q.revisedNoticeContent ");
				}
				query.append(" ,q.reply ");
			}else {
				query.append(" ,q.revisedNoticeContent ");
			}
						
			/*
			 * 
			 * XXX-> Adding device Type Class And HouseType 
			 * 
			 */
			query.append( " FROM  "+dt.getDeviceName()+"  q  ");
			query.append(" WHERE q.houseType.id = "+ ht.getId() +"");
			
			/*
			 * 
			 * XXX-> Checking if Passed Filter Param is Specific Or ALL 
			 * 
			 */
			
			if (filter.equals(ApplicationConstants.SUPPORT_SEARCH_FILTER_ALL)) {
				
				query.append("  AND  q.number IN (" + param + ")");
				query.append(" ORDER BY q.id DESC ");
				nQuery = this.em().createQuery(query.toString());
				nQuery.setMaxResults(10);
				
				Details = nQuery.getResultList();
				
			}else {
				if(dt.getDeviceName().equals(ApplicationConstants.CUTMOTION) || dt.getDeviceName().equals(ApplicationConstants.PROPRIETYPOINT)) {
					query.append("  AND q.deviceType.id IN (:deviceTypeId)");	
				}else {
					query.append("  AND q.type.id IN (:deviceTypeId)");	
				}
				query.append(
						"  AND q.session.id IN (:sessionId)");
				
				
				if(filter.equals(ApplicationConstants.SUPPORT_SEARCH_FILTER_SPECIFIC)) {
					if(currentHouse != null ) {
						query.append("  AND q.number IN (" + param + ")");
						query.append("ORDER BY q.id DESC");		
						
						nQuery = this.em().createQuery(query.toString());					
						
					}else {
						logger.info("Unable to Find Current House for filter :- ",filter);
					}
				} else if (filter.equals(ApplicationConstants.SUPPORT_SEARCH_FILTER_MEMBER)) {
					query.append("  AND q.primaryMember.id IN (" + param + ")");
					query.append("ORDER BY q.id DESC  ");					
					
					nQuery = this.em().createQuery(query.toString());
					nQuery.setFirstResult(offset);
					nQuery.setMaxResults(10);
					
				}
				
				nQuery.setParameter("deviceTypeId", dt.getId());					
				nQuery.setParameter("sessionId", Long.parseLong(session));
				
			    Details  = nQuery.getResultList();
			    logger.info("Result Generated SuccessFully !!!");
			}
			
			
			 if(Details != null && Details.size()>0) {
				 SearchVO singleSVo = null;
				 for(Object i : Details) {
					singleSVo =new SearchVO();
					 Object[] o=(Object[]) i;
		
		 
					 singleSVo.setId(Long.parseLong(o[0].toString()));
					 if(o[1] != null) {
						 singleSVo.setNumber(o[1].toString());
					 }else{
						 singleSVo.setNumber("-");
					 }
					 
					 singleSVo.setDevice(dt.getDeviceName());
					 singleSVo.setMinistry(dt.getType()); // used ministry because of no empty field t store deviceType
					
					
					 if(o[2]!=null) {
						 singleSVo.setSessionType(o[2].toString());
					 }
					 else {
						 singleSVo.setSessionType("-");
					 }
					 
					 if(o[3] !=null) {
						 singleSVo.setSessionYear(o[3].toString());
					 }
					 else {
						 singleSVo.setSessionYear("-");
					 }
					 
					 if(o[4] !=null) {
						 singleSVo.setHouseType(o[4].toString());
					 }
					 else {
						 singleSVo.setHouseType("-");
					 }
					 
					 if(o[5] !=null) {
						 singleSVo.setSubDepartment(o[5].toString());
					 }
					 else {
						 singleSVo.setSubDepartment("-");
					 }
					 
					 
					 if(o[6] != null ) {
						 if(o[6].toString().isEmpty()) {
							 singleSVo.setSubject("-");
						 }else {
							 singleSVo.setSubject(o[6].toString());
						 }
						 
					 }else {
						 singleSVo.setSubject("-");
					 }
					 
					 if(o[7] !=null) {
						 singleSVo.setDeviceType(o[7].toString());
					 }
					 else {
						 singleSVo.setDeviceType("-");
					 }
					 
					 if(o[8] !=null) {
						 singleSVo.setStatus(o[8].toString());
					 }
					 else {
						 singleSVo.setStatus("-");
					 }
					 
					 if(o[9] !=null) {
						 singleSVo.setInternalStatus(o[9].toString());
					 }
					 else {
						 singleSVo.setInternalStatus("-");
					 }
					 
					 if(o[10] !=null) {
						 singleSVo.setRecommendationStatus(o[10].toString());
					 }
					 else {
						 singleSVo.setRecommendationStatus("-");
					 }
					 
					 if(o[11] != null) {
						 if(o[11].toString().isEmpty()) {
							 singleSVo.setActor("-");
						 }else {
							 singleSVo.setActor(o[11].toString());
						 }
						 
					 }
					 else {
						 singleSVo.setActor("-");
					 }
					 
					 if(o[12] != null) {
						 if(o[12].toString().isEmpty()) {
							 singleSVo.setInternalStatusType("-");
						 }else {
							 singleSVo.setInternalStatusType(o[12].toString());
						 }
					 }else {
						 singleSVo.setInternalStatusType("-");
					 }
					 
					 if(o[13] != null &&  o[14] != null && o[15] !=null) {
						 singleSVo.setPrimaryMember(o[13].toString()+"  "+o[14].toString()+" "+o[15].toString());
					 }else {
						 singleSVo.setPrimaryMember("-");
					 }
					 
					
					 if(dt.getDeviceName().equals(ApplicationConstants.QUESTION) || dt.getDeviceName().equals(ApplicationConstants.STANDALONE_MOTION)) {
						
						 if(o[16] != null) {
							 singleSVo.setGroup(o[16].toString());
						 }else {
							 singleSVo.setGroup("-");
						 }
						 						 
						 if(o[17] != null) {
							 
							 if(!o[17].toString().isEmpty() ) {
								 singleSVo.setRevisedContent(o[17].toString());
							 }else {
								 singleSVo.setRevisedContent("-");
							 }							 
						 }else {
							 singleSVo.setRevisedContent("-");
						 }
						 
						 if(o[18] !=null) {
							 if(!o[18].toString().isEmpty()) {
								 singleSVo.setAnswer(o[18].toString());
							 }else {
								 singleSVo.setAnswer("-");
							 }
						 }else {
							 singleSVo.setAnswer("-");
						 }
						 
						 if(dt.getDeviceName().equals(ApplicationConstants.QUESTION)) {							 
							 if(o[19] != null) {
								 Question parent = Question.findById(Question.class, Long.parseLong(o[19].toString()));
								 Map<String,String> IdAndNumber = new  HashMap<String, String>();
								 IdAndNumber.put(parent.getId().toString(), parent.getNumber().toString());
								 singleSVo.setParent(IdAndNumber);
							 }else {
								 List<Map<String,String>> childs  =findIfParent(singleSVo.getId());
								 singleSVo.setChild(childs);
							 }
						 }

					 }else  {
						 if(o[16] != null ) {
							 if(!o[16].toString().isEmpty() ) {
								 singleSVo.setRevisedContent(o[16].toString());
							 }else {
								 singleSVo.setRevisedContent("-");
							 }
						 }else {
							 singleSVo.setRevisedContent("-");
						 }
						 if(!(dt.getDeviceName().equals(ApplicationConstants.RESOLUTION) || dt.getDeviceName().equals(ApplicationConstants.DISCUSSION_MOTION))) {
							
							 if(o[17] != null ) {
								 singleSVo.setAnswer(o[17].toString());
							 }else {
								 singleSVo.setAnswer("-");
							 }
							}
						
					 }
					 ResponseResult.add(singleSVo);
				 
				 }
			 }


			return ResponseResult;
		}
		
		
		public List<Map<String,String>>  findIfParent(Long qsnId) {
			
			StringBuilder strQuery = new StringBuilder();
			List<Map<String,String>> childDetails = new ArrayList<Map<String,String>>();
			
			strQuery.append("SELECT q.id,q.number FROM questions q WHERE q.parent IN( "+ qsnId+" )");
			javax.persistence.Query nQuery = this.em().createNativeQuery(strQuery.toString());
			List<Object> childs = nQuery.getResultList();
			
			if(childs != null && childs.size()>0) {
				 for(Object i : childs) {
						 Object[] o=(Object[]) i;
						 Map <String,String> IdAndNumber = new  HashMap<String, String>();
						 IdAndNumber.put(o[0].toString(), o[1].toString());
						 childDetails.add(IdAndNumber);
				}
				 return childDetails;
			}
			
			return null;
		}
		
		
		
 
}
