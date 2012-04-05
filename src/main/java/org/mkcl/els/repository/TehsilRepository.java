/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.repository.TehsilRepository
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.domain.Reference;
import org.mkcl.els.domain.Tehsil;
import org.springframework.stereotype.Repository;

/**
 * The Class TehsilRepository.
 *
 * @author vishals
 * @version 1.0.0
 */
@Repository
public class TehsilRepository extends BaseRepository<Tehsil, Long> {

    /**
     * Find tehsils ref by district id.
     *
     * @param districtId the district id
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings("rawtypes")
    public List<Reference> findTehsilsRefByDistrictId(final Long districtId,
            final String sortBy, final String sortOrder, final String locale) {
        String query = "SELECT t.id,t.name FROM Tehsil t WHERE t.district.id=" + districtId
                + " AND t.locale='" + locale + "' ORDER BY t." + sortBy + " " + sortOrder;
        List tehsils = this.em().createQuery(query).getResultList();
        List<Reference> tehsilsRef = new ArrayList<Reference>();
        for (Object i : tehsils) {
            Object[] o = (Object[]) i;
            Reference reference = new Reference(o[0].toString(), o[1].toString());
            tehsilsRef.add(reference);
        }
        return tehsilsRef;
    }

}
