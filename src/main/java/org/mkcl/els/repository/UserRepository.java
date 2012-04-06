package org.mkcl.els.repository;

import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends BaseRepository<User,Long>{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

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
