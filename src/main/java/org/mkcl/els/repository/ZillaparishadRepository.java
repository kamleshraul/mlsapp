/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.ZillaparishadRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Zillaparishad;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class ZillaparishadRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class ZillaparishadRepository extends BaseRepository<Zillaparishad,Long> {

	/**
	 * Find Zillaparishads ref by district id.
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
	public List<Reference> findZillaparishadsRefByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		String strQuery="SELECT t FROM Zillaparishad t" +
				" WHERE t.district.id=:districtId" +
				" AND t.locale=:locale" +
				" ORDER BY t."+sortBy+" "+sortOrder;
		try{
			TypedQuery<Zillaparishad> query=this.em().createQuery(strQuery, Zillaparishad.class);
			query.setParameter("districtId", districtId);
			query.setParameter("locale", locale);
			List<Zillaparishad> zillaparishads=query.getResultList();
			List<Reference> zillaparishadsRef=new ArrayList<Reference>();
			for(Zillaparishad i:zillaparishads){
				Reference reference=new Reference(String.valueOf(i.getId()),i.getName());
				zillaparishadsRef.add(reference);
			}
			return zillaparishadsRef;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ZillaparishadRepository_List<Reference>_findZillaparishadsRefByDistrictId", "Zillaparishad Not found");
			throw elsException;
		}

	}

	/**
	 * Find by state.
	 *
	 * @param stateId the state id
	 * @param locale the locale
	 * @return the list< Zillaparishad>
	 * @throws ELSException the eLS exception
	 * @author anandk
	 * @since v1.0.0
	 */
	public List<Zillaparishad> findByState(final Long stateId, final String locale) throws ELSException {
		String strQuery="SELECT t FROM Zillaparishad t" +
				" JOIN t.district d" +
				" JOIN d.division.state s" +
				" WHERE s.id=:stateId" +
				" AND t.locale=:locale" +
				" ORDER BY t.name asc";
		try{
			TypedQuery<Zillaparishad> query=this.em().createQuery(strQuery, Zillaparishad.class);
			query.setParameter("stateId", stateId);
			query.setParameter("locale", locale);
			return query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ZillaparishadRepository_List<Zillaparishad>_findByState", "Zillaparishad Not found");
			throw elsException;
		}
	}
}
