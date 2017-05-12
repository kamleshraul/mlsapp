package org.mkcl.els.repository;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Citizen;
import org.mkcl.els.domain.CitizenQuestion;
import org.springframework.stereotype.Repository;

@Repository
public class CitizenRepository extends BaseRepository<Citizen, Long> {

	public Citizen AddCitizen(final String name,
			final String mobile,final String email,final String locale) throws ELSException{
			try{
		Citizen c=new Citizen();
		c.setLocale(locale);
		c.setMobile(mobile);
		c.setName(name);
		c.setEmail(email);
		Citizen cId = (Citizen) c.persist();
		
		return cId;
	} catch (Exception e) {
		logger.error("Entity Not Found",e);
		return null;
	}
		
		
	
	}
}