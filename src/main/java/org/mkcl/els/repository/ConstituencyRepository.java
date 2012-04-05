/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.repository.ConstituencyRepository
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.vo.ConstituencyVO;
import org.mkcl.els.domain.Constituency;
import org.springframework.stereotype.Repository;


/**
 * The Class ConstituencyRepository.
 *
 * @author vishals
 * @version 1.0.0
 */
@Repository
public class ConstituencyRepository extends BaseRepository<Constituency, Long> {

    /**
     * Find by default state and house type.
     *
     * @param defaultState the default state
     * @param houseType the house type
     * @param locale the locale
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the list
     */
    @SuppressWarnings("rawtypes")
    public List<ConstituencyVO> findByDefaultStateAndHouseType(
            final String defaultState,
            final String houseType,
            final String locale,
            final String sortBy,
            final String sortOrder) {

        String query = "SELECT c.display_name,c.id FROM masters_constituencies AS c "
                + "JOIN masters_housetype "
                + "AS h WHERE h.type='" + houseType + "' AND c.id IN("
                + "SELECT constituency_id FROM associations_constituency_district AS a "
                + "WHERE district_id IN ("
                + "SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di "
                + "JOIN masters_states AS s "
                + "WHERE s.locale='" + locale + "' AND s.name='" + defaultState
                + "' AND d.division_id=di.id AND di.state_id=s.id))"
                + " ORDER BY c.name";
        List constituencies = this.em().createNativeQuery(query).getResultList();
        List<ConstituencyVO> constituencyVOs = new ArrayList<ConstituencyVO>();
        for (Object i : constituencies) {
            Object[] o = (Object[]) i;
            ConstituencyVO constituencyVO = new ConstituencyVO(Long.parseLong(o[1].toString()),
                    o[0].toString());
            constituencyVOs.add(constituencyVO);
        }
        return constituencyVOs;
    }

}
