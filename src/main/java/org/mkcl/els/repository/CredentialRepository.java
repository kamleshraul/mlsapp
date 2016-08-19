package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.Credential;
import org.springframework.stereotype.Repository;

@Repository
public class CredentialRepository extends BaseRepository<Credential, Serializable>{

	public List<Credential> findAllCredentialsByRole(final String role){
		StringBuffer strQuery = new StringBuffer("SELECT c FROM Credential c" +
				" JOIN FETCH c.roles r" +
				" WHERE r.type=:roleType");
		TypedQuery<Credential> query = this.em().createQuery(strQuery.toString(), Credential.class);
		
		query.setParameter("roleType", role);
		List<Credential> credentials = query.getResultList();
	
		return credentials;
	}	
	
}
