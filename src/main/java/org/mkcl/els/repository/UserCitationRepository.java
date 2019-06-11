package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.UserCitation;
import org.springframework.stereotype.Repository;

@Repository
public class UserCitationRepository extends BaseRepository<UserCitation, Serializable>{

	public List<UserCitation> findByDeviceTypeAndCredential(DeviceType deviceType, Credential credential) {
		String strQuery = "SELECT uc"
				+ " FROM UserCitation uc"
				+ " WHERE uc.deviceType=:deviceType"
				+ " AND uc.credential=:credential";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("deviceType", deviceType);
		query.setParameter("credential", credential);
		List<UserCitation> userCitations = query.getResultList();
		return userCitations;
	}

}
