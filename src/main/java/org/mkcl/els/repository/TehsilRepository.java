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

import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Repository;

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
	 */
	@SuppressWarnings("unchecked")
    public List<Reference> findTehsilsRefByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) {
		String query="SELECT t FROM Tehsil t WHERE t.district.id="+districtId+" AND t.locale='"+locale+"' ORDER BY t."+sortBy+" "+sortOrder;
		List<Tehsil> tehsils=this.em().createQuery(query).getResultList();
		List<Reference> tehsilsRef=new ArrayList<Reference>();
		for(Tehsil i:tehsils){
			Reference reference=new Reference(String.valueOf(i.getId()),i.getName());
			tehsilsRef.add(reference);
		}
		return tehsilsRef;
	}

	@SuppressWarnings("unchecked")
	public List<Tehsil> findByState(final Long stateId, final String locale) {
		String query="SELECT t FROM Tehsil t JOIN t.district d JOIN d.division.state s WHERE s.id="+stateId+" AND t.locale='"+locale+"' ORDER BY t.name asc";
		return this.em().createQuery(query).getResultList();
		
	}
	
	

}
