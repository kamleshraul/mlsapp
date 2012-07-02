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

import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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
	 */
	public User findByUserName(final String username,final String locale){
		String query="SELECT u FROM User u JOIN FETCH u.credential c  where u.locale='"+locale+"' AND c.username='"+username+"'";
		try {
			User user=(User) this.em().createQuery(query).getSingleResult();
			return user;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new User();
		}
	}
}
