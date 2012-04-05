/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.repository.MemberRoleRepository
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.repository;

import org.mkcl.els.domain.MemberRole;
import org.springframework.stereotype.Repository;

/**
 * The Class MemberRoleRepository.
 *
 * @author vishals
 * @version 1.0.0
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
}
