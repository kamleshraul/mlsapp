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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
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
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT u FROM User u WHERE ");
		if(!member.getFirstName().isEmpty()){
			buffer.append(" u.firstName=:firstName");
		}
		if(!member.getMiddleName().isEmpty()){
			buffer.append(" AND u.middleName=:middleName");
		}
		if(!member.getLastName().isEmpty()){
			buffer.append(" AND u.lastName=:lastName");
		}
		if(member.getBirthDate()!=null){
			buffer.append(" AND u.birthDate=:birthDate");
		}
		buffer.append(" ORDER BY u.lastName");
		
		Query query=this.em().createQuery(buffer.toString());
		if(!member.getFirstName().isEmpty()){
			query.setParameter("firstName", member.getFirstName());
		}
		if(!member.getMiddleName().isEmpty()){
			query.setParameter("middleName", member.getMiddleName());
		}
		if(!member.getLastName().isEmpty()){
			query.setParameter("lastName", member.getLastName());
		}
		if(member.getBirthDate()!=null){
			query.setParameter("birthDate",member.getBirthDate());
		}
		try{
			User user= (User) query.getSingleResult();
			if(user!=null){			
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
		String strQuery="SELECT u FROM User u WHERE u.firstName=:firstName AND u.middleName=:middleName"+
				" AND u.lastName=:lastName AND u.birthDate=:birthDate";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("firstName", firstName);
		query.setParameter("middleName",middleName);
		query.setParameter("lastName",lastName);
		query.setParameter("birthDate",birthDate);
		try{
			User user= (User) query.getSingleResult();
			if(user!=null){			
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

	public List<User> findByRole(final boolean roleStartingWith,final String roles,
			final String language,final String locale) throws ELSException{
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
}
