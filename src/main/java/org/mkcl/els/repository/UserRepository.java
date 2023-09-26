/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.UserRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * The Class UserRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class UserRepository extends BaseRepository<User,Long>{

	/** The logger. */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Find by user name.
	 *
	 * @param username the username
	 * @param locale the locale
	 * @return the user
	 * @throws ELSException 
	 */
	public User findByUserName(final String username,final String locale) throws ELSException{
		String strQuery="SELECT u FROM User u" +
				" JOIN FETCH u.credential c" +
				" where u.locale=:locale" +
				" AND c.username=:username";
		try {
			Query query=this.em().createQuery(strQuery);
			query.setParameter("locale", locale);
			query.setParameter("username", username);
			User user=(User) query.getSingleResult();
			return user;
		} catch(EntityNotFoundException ex){
			logger.error(ex.getMessage());
			return new User();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserRepository_User_findByUserName", "User Not found");
			throw elsException;
		}
	}
	@Transactional(readOnly=false)
	public void assignMemberId(final Long memberId,final Long userId) throws ELSException{
		try{
			String strquery="UPDATE User u SET u.id=:memberId WHERE u.id=:userId";
			Query query=this.em().createQuery(strquery);
			query.setParameter("memberId", memberId);
			query.setParameter("userId", userId);
			query.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserRepository_void_assignMemberId", "Failed to assign Member Id to user");
			throw elsException;
		}
		
	}
	public User find(final Member member) throws ELSException {
//		StringBuffer buffer=new StringBuffer();
//		buffer.append("SELECT u FROM User u WHERE ");
//		if(!member.getFirstName().isEmpty()){
//			buffer.append("u.firstName=:firstName");
//		}
//		if(!member.getMiddleName().isEmpty()){
//			buffer.append(" AND u.middleName=:middleName");
//		}
//		if(!member.getLastName().isEmpty()){
//			buffer.append(" AND u.lastName=:lastName");
//		}
//		if(member.getBirthDate()!=null){
//			buffer.append(" AND u.birthDate=:birthDate");
//		}
//		buffer.append(" ORDER BY u.lastName");
//		
//		Query query=this.em().createQuery(buffer.toString());
//		if(!member.getFirstName().isEmpty()){
//			query.setParameter("firstName", member.getFirstName());
//		}
//		if(!member.getMiddleName().isEmpty()){
//			query.setParameter("middleName", member.getMiddleName());
//		}
//		if(!member.getLastName().isEmpty()){
//			query.setParameter("lastName", member.getLastName());
//		}
//		if(member.getBirthDate()!=null){
//			query.setParameter("birthDate",member.getBirthDate());
//		}		
		String dbBirthDate=FormaterUtil.formatDateToString(member.getBirthDate(), ApplicationConstants.DB_DATEFORMAT,"en_US");
		
		String strQuery="SELECT u.id FROM users as u WHERE u.first_name='"+member.getFirstName()+"' AND u.middle_name='"+
				member.getMiddleName()+ "' AND u.last_name='"+  member.getLastName()+"' AND u.birth_date='"+dbBirthDate+"'";
		Query query=this.em().createNativeQuery(strQuery);		
		try{
			Long id=new Long(0);
			Object results= query.getSingleResult();
			if(results!=null){
				BigInteger o=(BigInteger) results;				
				id=Long.parseLong(o.toString());	
				User user=User.findById(User.class, id);
				return user;
			}else{
				return new User();
			}			
		}catch(EntityNotFoundException ex){
			logger.error(ex.getMessage());
			return new User();
		}catch(Exception e){			
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserRepository_User_findbyNameBirthDate", "User Not found");
			throw elsException;
		}
		
	}
	
	public User findbyNameBirthDate(final String firstName,final String middleName,final String lastName,
			final Date birthDate) throws ELSException {		
		return this.findByNameBirthDate(firstName, middleName, lastName, birthDate, ApplicationLocale.findDefaultLocale());
	}
	
	public User findByNameBirthDate(final String firstName,final String middleName,final String lastName,
			final Date birthDate, final String locale) throws ELSException {
		
		/**** Previous Code ****/
//		String strQuery="SELECT u FROM User u "
//				+ "WHERE ("
//				+ "(u.firstName=:firstName AND u.middleName=:middleName AND u.lastName=:lastName) "
//				+ " OR "
//				+ "(u.firstName=:firstName AND u.lastName=:lastName) "
//				+ " OR "
//				+ "(u.middleName=:middleName AND u.lastName=:lastName) "
//				+ " OR "
//				+ "(u.firstName=:firstName AND u.middleName=:middleName) "
//				+ ")"
//				+ "AND u.birthDate=:birthDate";
//		Query query=this.em().createQuery(strQuery);
//		query.setParameter("firstName", firstName);
//		query.setParameter("middleName",middleName);
//		query.setParameter("lastName",lastName);
//		query.setParameter("birthDate",birthDate);
//		try{
//			User user= (User) query.getSingleResult();
//			if(user!=null){			
//				return user;
//			}else{
//				return new User();
//			}
//		}catch(EntityNotFoundException ex){
//			logger.error(ex.getMessage());
//			return new User();
//		}catch(Exception e){
//			e.printStackTrace();
//			logger.error(e.getMessage());
//			ELSException elsException=new ELSException();
//			elsException.setParameter("UserRepository_User_findbyNameBirthDate", "User Not found");
//			throw elsException;
//		}
		
		/**** Updated Code ****/
		User user = null;
		List<User> possibleUsers = null;
		Map<String, String> userNameParameters = new HashMap<String, String>();
		
		//Combo 1: firstName + middleName + lastName + birthDate
		try {
			userNameParameters.put("firstName", firstName);
			userNameParameters.put("middleName", middleName);
			userNameParameters.put("lastName", lastName);
//			System.out.println("combo 1:");
//			for (Map.Entry<String, String> entry : userNameParameters.entrySet()) {
//			    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//			}
			possibleUsers = User.findAllByFieldNames(User.class, userNameParameters, "id", ApplicationConstants.ASC, locale);
			if(possibleUsers!=null && !possibleUsers.isEmpty()) {
				for(User m: possibleUsers) {
					if(m.getBirthDate().equals(birthDate)) {
						user = m;
						possibleUsers = null;
						userNameParameters = null;
						return user;
					} else {
						throw new ELSException("user_not_found", "user_not_found");
					}
				}
			} else {
				throw new ELSException("user_not_found", "user_not_found");
			}
		} catch(ELSException eCombo1) {
			if(eCombo1.getParameter()!=null && eCombo1.getParameter().equals("user_not_found")) {
				//Combo 2: firstName + lastName + birthDate
				try {
					userNameParameters.remove("middleName");	
//					System.out.println("combo 2:");
//					for (Map.Entry<String, String> entry : userNameParameters.entrySet()) {
//					    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//					}
					possibleUsers = User.findAllByFieldNames(User.class, userNameParameters, "id", ApplicationConstants.ASC, locale);
					if(possibleUsers!=null && !possibleUsers.isEmpty()) {
						for(User m: possibleUsers) {
							if(m.getBirthDate().equals(birthDate)) {
								user = m;
								possibleUsers = null;
								userNameParameters = null;
								return user;
							} else {
								throw new ELSException("user_not_found", "user_not_found");
							}
						}
					} else {
						throw new ELSException("user_not_found", "user_not_found");
					}
				} catch(ELSException eCombo2) {
					if(eCombo2.getParameter()!=null && eCombo2.getParameter().equals("user_not_found")) {
						//Combo 3: middleName + lastName + birthDate
						try {
							userNameParameters.remove("firstName");
							userNameParameters.put("middleName", middleName);			
//							System.out.println("combo 3:");
//							for (Map.Entry<String, String> entry : userNameParameters.entrySet()) {
//							    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//							}
							possibleUsers = User.findAllByFieldNames(User.class, userNameParameters, "id", ApplicationConstants.ASC, locale);
							if(possibleUsers!=null && !possibleUsers.isEmpty()) {
								for(User m: possibleUsers) {
									if(m.getBirthDate().equals(birthDate)) {
										user = m;
										possibleUsers = null;
										userNameParameters = null;
										return user;
									} else {
										throw new ELSException("user_not_found", "user_not_found");
									}
								}
							} else {
								throw new ELSException("user_not_found", "user_not_found");
							}
						} catch(ELSException eCombo3) {
							if(eCombo3.getParameter()!=null && eCombo3.getParameter().equals("user_not_found")) {
								//Combo 4: firstName + middleName + birthDate
								userNameParameters.remove("lastName");
								userNameParameters.put("firstName", firstName);
//								System.out.println("combo 4:");
//								for (Map.Entry<String, String> entry : userNameParameters.entrySet()) {
//								    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//								}
								possibleUsers = User.findAllByFieldNames(User.class, userNameParameters, "id", ApplicationConstants.ASC, locale);
								if(possibleUsers!=null && !possibleUsers.isEmpty()) {
									for(User m: possibleUsers) {
										if(m.getBirthDate().equals(birthDate)) {
											user = m;
											possibleUsers = null;
											userNameParameters = null;
											return user;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//if user is not found, return blank instance
		possibleUsers = null;
		userNameParameters = null;
		return new User();
	}
	
	public List<User> findByRole(final boolean roleStartingWith,
			final String roles,
			final String language,
			final String locale) throws ELSException{
		StringBuffer buffer=new StringBuffer();
		try{
			buffer.append("SELECT u FROM User u" +
					" JOIN u.credential c" +
					" JOIN c.roles r" +
					" WHERE u.locale='"+locale+"' AND c.enabled=true ");
			if(roleStartingWith){
				buffer.append(" AND r.name LIKE '"+roles+"%'");
			}else{
				String[] roleArr=roles.split(",");
				if(roleArr.length>0){
					buffer.append(" AND (");
					for(String i:roleArr){
						buffer.append("r.name='"+i+"' OR ");
					}
					buffer.delete(buffer.length()-3,buffer.length()-1);
					buffer.append(")");
				}
			}
			buffer.append(" ORDER BY u.lastName");
			return this.em().createQuery(buffer.toString(), User.class).getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserRepository_List<User>_findByRole", "User Not found");
			throw elsException;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findByRole(final boolean roleStartingWith,
			final String roles,
			final String language,
			final String orderBy,
			final String sortOrder,
			final String locale,
			final String houseType) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT u FROM User u JOIN u.credential c JOIN c.roles r WHERE u.locale='"+locale+"' AND c.enabled=true ");
		if(houseType!=null&&!houseType.isEmpty()){
			buffer.append(" AND u.houseType.type=:houseType ");
		}
		if(language!=null&&!language.isEmpty()){
			buffer.append(" AND u.language=:language ");
		}
		if(roleStartingWith){
			buffer.append(" AND r.name LIKE '"+roles+"%'");
		}else{
			String[] roleArr=roles.split(",");
			if(roleArr.length>0){
				buffer.append(" AND (");
				for(String i:roleArr){
					buffer.append("r.name='"+i+"' OR ");
				}
				buffer.delete(buffer.length()-3,buffer.length()-1);
				buffer.append(")");
			}
		}
		if(orderBy!=null&&!orderBy.isEmpty()){
			buffer.append(" ORDER BY ");
			String[] orderByParts=orderBy.split(",");
			String[] sortOrderParts=sortOrder.split(",");
			for(int i=0;i<orderByParts.length;i++){
				buffer.append("u."+orderByParts[i]+" "+sortOrderParts[i]+",");
			}
			buffer.deleteCharAt(buffer.length()-1);
		}
		Query query=this.em().createQuery(buffer.toString());
		if(buffer.toString().contains(":houseType")){
			query.setParameter("houseType",houseType);
		}
		if(buffer.toString().contains(":language")){
			query.setParameter("language",language);
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findByRole(final boolean roleStartingWith,
			final String roles,
			final String locale) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT u FROM User u JOIN u.credential c JOIN c.roles r WHERE u.locale='"+locale+"' AND c.enabled=true ");
		if(roleStartingWith){
			buffer.append(" AND r.name LIKE '"+roles+"%'");
		}else{
			String[] roleArr=roles.split(",");
			if(roleArr.length>0){
				buffer.append(" AND (");
				for(String i:roleArr){
					buffer.append("r.name='"+i+"' OR ");
				}
				buffer.delete(buffer.length()-3,buffer.length()-1);
				buffer.append(")");
			}
		}
		buffer.append(" ORDER BY u.lastName");
		return this.em().createQuery(buffer.toString()).getResultList();
	}

	public String findFullNameByUserName(final String username,final String locale) throws ELSException{
		String strQuery="SELECT CONCAT(" +
											" (CASE WHEN u.title IS NOT NULL AND u.title!='' AND u.first_name IS NOT NULL AND u.first_name!='' THEN CONCAT(u.title, ' ', u.first_name) ELSE '' END)," +
											" (CASE WHEN (u.title IS NULL OR u.title='') AND u.first_name IS NOT NULL AND u.first_name!='' THEN u.first_name ELSE '' END)," +
											" (CASE WHEN u.first_name IS NOT NULL AND u.first_name!='' AND u.middle_name IS NOT NULL AND u.middle_name!='' THEN CONCAT(' ', u.middle_name) ELSE '' END)," +
											" (CASE WHEN u.last_name IS NOT NULL AND u.last_name!='' THEN CONCAT(' ', u.last_name) ELSE '' END)" +
								") AS userFullName FROM users u" +
				" INNER JOIN credentials c ON (c.id=u.credential_id)" +
				" where u.locale=:locale" +
				" AND c.username=:username";
		try {
			Query query=this.em().createNativeQuery(strQuery);
			query.setParameter("locale", locale);
			query.setParameter("username", username);
			String userFullName = (String) query.getSingleResult();
			return userFullName;
		} catch(EntityNotFoundException ex){
			logger.error(ex.getMessage());
			return "";
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserRepository_User_findFullNameByUserName", "User Not found");
			throw elsException;
		}
	}
}
