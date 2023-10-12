/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Query.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.domain;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable
@Entity
@Table(name="queries")
public class Query extends BaseDomain{

    @Column(length=30000)
    private String query;

    @Column(length=100)
    private String keyField;
    
    @Autowired
    private transient QueryRepository queryRepository;
    
    public Query() {
        super();
    }
    
    public static QueryRepository getQueryRepository() {
    	QueryRepository queryRepository = new Query().queryRepository;
        if (queryRepository == null) {
            throw new IllegalStateException(
                    "QueryRepository has not been injected in Query Domain");
        }
        return queryRepository;
    }

    public Query(final String keyField, final String query) {
        super();
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(final String keyField) {
        this.keyField = keyField;
    }

	@SuppressWarnings("rawtypes")
	public static List findReport(final String report, final Map<String, String[]> requestMap) {
		return getQueryRepository().findReport(report, requestMap);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findReport(final String report,final Map<String, String[]> requestMap, final Boolean handleIN) {
		return getQueryRepository().findReport(report, requestMap, handleIN);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findResultListOfGivenClass(final String report, final Map<String, String[]> requestMap, final Class className) {
		return getQueryRepository().findResultListOfGivenClass(report, requestMap, className);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findResultListOfGivenClass(final String report, final Map<String, String[]> requestMap, final Class className, final Boolean handleIN) {
		return getQueryRepository().findResultListOfGivenClass(report, requestMap, className, handleIN);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findMISPartyDistrictReport(final String report, final Map<String, String[]> requestMap) {
		return getQueryRepository().findMISPartyDistrictReport(report, requestMap);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findReport(final String report,final Map<String, String[]> requestMap, Integer start, Integer end) {
		return getQueryRepository().findReport(report, requestMap, start, end);
	}
	
	@SuppressWarnings("rawtypes")
	public static List findReportWithIn(final String report,final Map<String, String[]> parameterMap) {
		return getQueryRepository().findReportWithIn(report, parameterMap);
	}
	
	public static String generateDynamicInnerQueryText(final String innerQuery,final Map<String, String[]> requestMap) {
		return getQueryRepository().generateDynamicInnerQueryText(innerQuery, requestMap);
	}
	public static List<SearchVO> getDeviceDetailsforSupportActivity(String param, String session,HouseType houseType, DeviceType deviceType,
			String filter,Integer offset , Locale locale ) {
		return getQueryRepository().getDeviceDetailsForSupportActivity(param, session,houseType, deviceType, filter,offset, locale);
	}
}
