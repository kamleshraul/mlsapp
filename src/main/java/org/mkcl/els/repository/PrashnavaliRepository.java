package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.batik.svggen.font.table.Device;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.controller.cis.PrashanavliController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Prashnavali;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

@Repository
public class PrashnavaliRepository extends BaseRepository<Prashnavali, Serializable> {
	public List<Prashnavali> findActivePrashnavalis(final HouseType houseType,
			final Status status, 
			final String locale) {
		
		
		HouseType[] houseTypes = new HouseType[]{houseType};
			
		StringBuffer query = new StringBuffer();
		query.append("SELECT p" +
				" FROM Prashnavali p " +
				" WHERE p.status.priority >=" + status.getPriority() +
					" AND p.locale = '" + locale + "'");
		
		String houseTypesFilter = this.getHouseTypesFilter(houseTypes);
		query.append(houseTypesFilter);
		
		TypedQuery<Prashnavali> tQuery = 
			this.em().createQuery(query.toString(), Prashnavali.class);
		List<Prashnavali> prashnavali = tQuery.getResultList();
		return prashnavali;
	}
	
	private String getHouseTypesFilter(final HouseType[] houseTypes) {
		StringBuffer sb = new StringBuffer();
		
		int n = houseTypes.length;
		if(n > 0) {
			sb.append(" AND (");
			for(int i = 0; i < n; i++) {
				sb.append(" ht.id = " + houseTypes[i].getId());
				if(i < n - 1) {
					sb.append(" OR ");
				}
			}			
			sb.append(")");
		}

		return sb.toString();
	}
}
