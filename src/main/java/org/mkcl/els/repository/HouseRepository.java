/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.repository.HouseRepository
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.repository;

import java.util.Date;

import org.mkcl.els.domain.House;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class HouseRepository.
 *
 * @author vishals
 * @version 1.0.0
 */
@Repository
public class HouseRepository extends BaseRepository<House, Long> {

    /**
     * Find current house.
     *
     * @param locale the locale
     * @return the house
     */
    public House findCurrentHouse(final String locale) {
        Search search = new Search();
        search.addFilterEqual("locale", locale);
        Date currentDate = new Date();
        search.addFilterGreaterOrEqual("lastDate", currentDate);
        search.addFilterLessOrEqual("firstDate", currentDate);
        return this.searchUnique(search);
    }

    /**
     * Find house by to from date.
     *
     * @param fromDate the from date
     * @param toDate the to date
     * @param locale the locale
     * @return the house
     */
    public House findHouseByToFromDate(
            final Date fromDate,
            final Date toDate,
            final String locale) {
        Search search = new Search();
        search.addFilterEqual("locale", locale);
        search.addFilterGreaterOrEqual("lastDate", toDate);
        search.addFilterLessOrEqual("firstDate", fromDate);
        return this.searchUnique(search);
    }
}
