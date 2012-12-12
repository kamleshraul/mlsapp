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

import java.util.List;

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
        String query = "SELECT m FROM MemberRole m WHERE m.name='" + roleName
                + "' AND m.houseType.id=" + houseTypeId + " AND m.locale='"
                + locale + "'";
        return (MemberRole) this.em().createQuery(query).getSingleResult();
    }

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<MemberRole> findByHouseType(final String houseType, final String locale) {
		String query=null;
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			query="SELECT m FROM MemberRole m WHERE m.locale='"+locale+"' ORDER BY m.name DESC";
		}else{
			query="SELECT m FROM MemberRole m WHERE m.locale='"+locale+"' AND m.houseType.type='"+houseType+"' ORDER BY m.name DESC";
		}
		return this.em().createQuery(query).getResultList();
	}
	
	public MemberRole find(HouseType houseType, String type, String locale) {
		Search search = new Search();
		search.addFilterEqual("houseType", houseType);
		search.addFilterEqual("type", type);
		search.addFilterEqual("locale", locale);
		MemberRole role = this.searchUnique(search);
		return role;
	}
}
