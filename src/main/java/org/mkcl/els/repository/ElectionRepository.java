/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.repository.ElectionRepository
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.vo.ElectionVO;
import org.mkcl.els.domain.Election;
import org.springframework.stereotype.Repository;

/**
 * The Class ElectionRepository.
 *
 * @author vishals
 * @version 1.0.0
 */
@Repository
public class ElectionRepository extends BaseRepository<Election, Long> {

    /**
     * Find by house type.
     *
     * @param houseType the house type
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<ElectionVO> findByHouseType(final String houseType, final String locale) {

        String query = "SELECT e.id,e.name FROM masters_elections AS e JOIN  "
                + "masters_electiontypes AS et JOIN masters_housetype AS h "
                + "WHERE e.electiontype_id=et.id AND et.housetype_id=h.id AND h.type='"
                + houseType
                + "' AND e.locale='" + locale + "'";
        List elections = this.em().createNativeQuery(query).getResultList();
        List<ElectionVO> electionVOs = new ArrayList<ElectionVO>();
        for (Object i : elections) {
            Object[] o = (Object[]) i;
            ElectionVO electionVO = new ElectionVO(Long.parseLong(o[0].toString()),
                    o[1].toString());
            electionVOs.add(electionVO);
        }
        return electionVOs;
    }

}
