package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
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
	
	@SuppressWarnings("unchecked")
	public List<Credential> findAllActiveByUserGroupType(final String userGroupType, final Date onDate, final String locale) {
		List<Credential> activeCredentialsByUserGroupType = new ArrayList<Credential>();
		
		String strQuery = "SELECT cr.* FROM usergroups ug" +
				" INNER JOIN credentials cr ON (cr.id=ug.credential)" +
				" INNER JOIN usergroups_types ugt ON (ugt.id=ug.user_group_type)" +
				" WHERE ugt.type=:userGroupType" +
				" AND ug.active_from<=:onDate" +
				" AND (ug.active_to>=:onDate || ug.active_to IS NULL)" +
				" AND cr.enabled";
		
		TypedQuery<Credential> query = (TypedQuery<Credential>) this.em().createNativeQuery(strQuery.toString(), Credential.class);
		query.setParameter("userGroupType", userGroupType);
		query.setParameter("onDate", onDate);
		activeCredentialsByUserGroupType = query.getResultList();
		
		return activeCredentialsByUserGroupType;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllActiveUsernamesByUserGroupType(final String userGroupType, final Date onDate, final String locale) {
		List<String> activeCredentialsByUserGroupType = new ArrayList<String>();
		
		String strQuery = "SELECT DISTINCT cr.username FROM usergroups ug" +
				" INNER JOIN credentials cr ON (cr.id=ug.credential)" +
				" INNER JOIN usergroups_types ugt ON (ugt.id=ug.user_group_type)" +
				" WHERE ugt.type=:userGroupType" +
				" AND ug.active_from<=:onDate" +
				" AND (ug.active_to>=:onDate || ug.active_to IS NULL)" +
				" AND cr.enabled";
		
		Query query = this.em().createNativeQuery(strQuery.toString());
		query.setParameter("userGroupType", userGroupType);
		query.setParameter("onDate", onDate);
		activeCredentialsByUserGroupType = query.getResultList();
		
		return activeCredentialsByUserGroupType;
	}
	
}
