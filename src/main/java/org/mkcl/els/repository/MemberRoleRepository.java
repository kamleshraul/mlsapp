/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberRoleRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MemberRoleRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberRoleRepository extends BaseRepository<MemberRole, Long> {

    /**
     * Find by name house type locale.
     *
     * @param roleName the role name
     * @param houseTypeId the house type id
     * @param locale the locale
     * @return the member role
     */
    public MemberRole findByNameHouseTypeLocale(final String roleName,
            final Long houseTypeId, final String locale) {
    	MemberRole mRole = null;
    	try{
	        String strQuery = "SELECT m FROM MemberRole m WHERE m.type=:roleName" +
	                 " AND m.houseType.id=:houseTypeId AND m.locale=:locale";
	        Query query=this.em().createQuery(strQuery);
	        query.setParameter("roleName", roleName);
	        query.setParameter("houseTypeId", houseTypeId);
	        query.setParameter("locale", locale);
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	        
        return mRole;
    }

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 */
	
	public List<MemberRole> findByHouseType(final String houseType, final String locale) {
		
		List<MemberRole> mRoles = new ArrayList<MemberRole>();
		try{
			String strquery=null;
			if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				strquery="SELECT m FROM MemberRole m WHERE m.locale=:locale ORDER BY m.name DESC";
			}else{
				strquery="SELECT m FROM MemberRole m WHERE m.locale=:locale AND m.houseType.type=:houseType ORDER BY m.name DESC";
			}
			TypedQuery<MemberRole> query=this.em().createQuery(strquery, MemberRole.class);
			query.setParameter("locale", locale);
			if(!houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				query.setParameter("houseType", houseType);
			}
			mRoles = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return mRoles;
	}
	
	public MemberRole find(final HouseType houseType,final String type,final String locale) {
		MemberRole mRole = null;
		try{
			String strquery="SELECT mr FROM MemberRole mr WHERE mr.houseType=:houseType AND mr.type=:type"+
					" AND mr.locale=:locale";
			Query query=this.em().createQuery(strquery);
			query.setParameter("houseType", houseType);
			query.setParameter("type", type);
			query.setParameter("locale", locale);
			mRole =(MemberRole) query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return mRole;
	}
}
