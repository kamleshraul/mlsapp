/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.TehsilRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class TehsilRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class TehsilRepository extends BaseRepository<Tehsil,Long> {

	/**
	 * Find tehsils ref by district id.
	 *
	 * @param districtId the district id
	 * @param sortBy the sort by
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 * @throws ELSException the eLS exception
	 * @author anandk
	 * @since v1.0.0
	 */
	public List<Reference> findTehsilsRefByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		String strQuery="SELECT t FROM Tehsil t" +
				" WHERE t.district.id=:districtId" +
				" AND t.locale=:locale" +
				" ORDER BY t."+sortBy+" "+sortOrder;
		try{
			TypedQuery<Tehsil> query=this.em().createQuery(strQuery, Tehsil.class);
			query.setParameter("districtId", districtId);
			query.setParameter("locale", locale);
			List<Tehsil> tehsils=query.getResultList();
			List<Reference> tehsilsRef=new ArrayList<Reference>();
			for(Tehsil i:tehsils){
				Reference reference=new Reference(String.valueOf(i.getId()),i.getName());
				tehsilsRef.add(reference);
			}
			return tehsilsRef;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("TehsilRepository_List<Reference>_findTehsilsRefByDistrictId", "Tehsil Not found");
			throw elsException;
		}

	}

	/**
	 * Find by state.
	 *
	 * @param stateId the state id
	 * @param locale the locale
	 * @return the list< tehsil>
	 * @throws ELSException the eLS exception
	 * @author anandk
	 * @since v1.0.0
	 */
	public List<Tehsil> findByState(final Long stateId, final String locale) throws ELSException {
		String strQuery="SELECT t FROM Tehsil t" +
				" JOIN t.district d" +
				" JOIN d.division.state s" +
				" WHERE s.id=:stateId" +
				" AND t.locale=:locale" +
				" ORDER BY t.name asc";
		try{
			TypedQuery<Tehsil> query=this.em().createQuery(strQuery, Tehsil.class);
			query.setParameter("stateId", stateId);
			query.setParameter("locale", locale);
			return query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("TehsilRepository_List<Tehsil>_findByState", "Tehsil Not found");
			throw elsException;
		}
	}
}
