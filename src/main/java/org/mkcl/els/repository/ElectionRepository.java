/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.ElectionRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Election;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class ElectionRepository extends BaseRepository<Election,Long> {

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
    public List<Election> findByHouseType(final String houseType,final String locale) {
        /*
         * SELECT * FROM masters_elections  AS e JOIN  masters_electiontypes AS et JOIN masters_housetype AS h WHERE e.electiontype_id=et.id
            AND et.housetype_id=h.id AND h.type='lowerhouse' AND e.locale='en_US'
         */
        String query=null;
        if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
            query="SELECT e FROM Election e WHERE e.locale='"+locale+"'";
        }else{
            query="SELECT e FROM Election e  WHERE e.locale='"+locale+"' AND e.electionType.houseType.type='"+houseType+"'";

        }
        return this.em().createQuery(query).getResultList();
    }

}
