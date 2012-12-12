package org.mkcl.els.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class DbLoggerRepsoitory {
	@PersistenceContext EntityManager em;;
	public void log(final String processVariablesAsSingleString,final String processId,final String storedProcedureName){
		em.createNativeQuery("call "+storedProcedureName+"(?,?)").setParameter(1,processVariablesAsSingleString).setParameter(2,processId).executeUpdate();
	}
}
