package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.PartyType;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class CommitteeRepository extends BaseRepository<Committee, Long> {

	public Committee find(final CommitteeName committeeName,
			final Date formationDate,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("committeeName", committeeName);
		search.addFilterEqual("formationDate", formationDate);
		search.addFilterEqual("locale", locale);
		Committee committee = this.searchUnique(search);
		return committee;
	}
	
	public Committee findActiveCommittee(final CommitteeName committeeName,
			final Status status,
			final Date onDate, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("committeeName", committeeName);
		search.addFilterGreaterOrEqual("status.priority", status.getPriority());
		search.addFilterLessOrEqual("formationDate", onDate);
		search.addFilterGreaterOrEqual("dissolutionDate", onDate);
		search.addFilterEqual("locale", locale);
		Committee committee = this.searchUnique(search);
		return committee;
	}
	
	public List<Committee> findActiveCommittees(final Status status,
			final Date onDate, 
			final String locale) {
		Search search = new Search();
		search.addFilterGreaterOrEqual("status.priority", status.getPriority());
		search.addFilterLessOrEqual("formationDate", onDate);
		search.addFilterGreaterOrEqual("dissolutionDate", onDate);
		search.addFilterEqual("locale", locale);
		List<Committee> committees = this.search(search);
		return committees;
	}
	
	public List<Committee> findActiveCommittees(final HouseType houseType,
			final Status status, 
			final Boolean isIncludeBothHouseType, 
			final Date onDate,
			final String locale) {
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_DATEFORMAT", "");
		String strOnDate = FormaterUtil.formatDateToString(
				onDate, parameter.getValue());
		
		HouseType[] houseTypes = new HouseType[]{houseType};
		if(isIncludeBothHouseType) {
			HouseType bothHouseType = 
				HouseType.findByType(ApplicationConstants.BOTH_HOUSE, locale);
			houseTypes = new HouseType[]{houseType, bothHouseType};
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT c" +
				" FROM Committee c JOIN c.committeeName cn" +
				" JOIN cn.committeeType ct JOIN ct.houseType ht" +
				" WHERE c.status.priority >=" + status.getPriority() +
				" AND c.formationDate <= '" + strOnDate + "'" +
				" AND c.dissolutionDate >= '" + strOnDate + "'" +
				" AND c.locale = '" + locale + "'");
		
		String houseTypesFilter = this.getHouseTypesFilter(houseTypes);
		query.append(houseTypesFilter);
		
		TypedQuery<Committee> tQuery = 
			this.em().createQuery(query.toString(), Committee.class);
		List<Committee> committees = tQuery.getResultList();
		return committees;
	}
	
	public List<Committee> findCommitteesToBeProcessed(
			final HouseType houseType,
			final PartyType partyType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		Date currentDate = new Date();
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_DATEFORMAT", "");
		String strCurrentDate = FormaterUtil.formatDateToString(
				currentDate, parameter.getValue());
		
		Status status = 
			Status.findByType(ApplicationConstants.COMMITTEE_CREATED, locale);
		
		HouseType[] houseTypes = new HouseType[]{houseType};
		if(isIncludeBothHouseType) {
			HouseType bothHouseType = 
				HouseType.findByType(ApplicationConstants.BOTH_HOUSE, locale);
			houseTypes = new HouseType[]{houseType, bothHouseType};
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT c" +
				" FROM Committee c JOIN c.committeeName cn" +
				" JOIN cn.committeeType ct JOIN ct.houseType ht" +
				" WHERE c.status.id =" + status.getId() +
				" AND c.formationDate <= '" + strCurrentDate + "'" +
				" AND c.dissolutionDate >= '" + strCurrentDate + "'" +
				" AND c.locale = '" + locale + "'");
		
		String houseTypeType = houseType.getType();
		String partyTypeType = partyType.getType();
		if(partyTypeType.equals(ApplicationConstants.RULING_PARTY)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				query.append(" AND c.internalStatusPAMLH.id = " + null);
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				query.append(" AND c.internalStatusPAMUH.id = " + null);
			}
		}
		else if (partyTypeType.equals(ApplicationConstants.OPPOSITION_PARTY)) {
			if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
				query.append(" AND c.internalStatusLOPLH.id = " + null);
			}
			else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
				query.append(" AND c.internalStatusLOPUH.id = " + null);
			}
		}
		
		String houseTypesFilter = this.getHouseTypesFilter(houseTypes);
		query.append(houseTypesFilter);
		
		TypedQuery<Committee> tQuery = 
			this.em().createQuery(query.toString(), Committee.class);
		List<Committee> committees = tQuery.getResultList();
		return committees;
	}
	
	public List<Committee> findCommitteesForInvitedMembersToBeAdded(
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		Date currentDate = new Date();
		CustomParameter parameter = 
			CustomParameter.findByName(CustomParameter.class, 
					"DB_DATEFORMAT", "");
		String strCurrentDate = FormaterUtil.formatDateToString(
				currentDate, parameter.getValue());
				
		Status status = Status.findByType(
				ApplicationConstants.COMMITTEE_MEMBERS_ADDED, locale);
		
		HouseType[] houseTypes = new HouseType[]{houseType};
		if(isIncludeBothHouseType) {
			HouseType bothHouseType = 
				HouseType.findByType(ApplicationConstants.BOTH_HOUSE, locale);
			houseTypes = new HouseType[]{houseType, bothHouseType};
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT c" +
				" FROM Committee c JOIN c.committeeName cn" +
				" JOIN cn.committeeType ct JOIN ct.houseType ht" +
				" WHERE c.status.id =" + status.getId() +
				" AND c.formationDate <= '" + strCurrentDate + "'" +
				" AND c.dissolutionDate >= '" + strCurrentDate + "'" +
				" AND c.locale = '" + locale + "'");
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			query.append(" AND c.internalStatusIMLH.id = " + null);
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			query.append(" AND c.internalStatusIMUH.id = " + null);
		}
		
		String houseTypesFilter = this.getHouseTypesFilter(houseTypes);
		query.append(houseTypesFilter);
		
		TypedQuery<Committee> tQuery = 
			this.em().createQuery(query.toString(), Committee.class);
		List<Committee> committees = tQuery.getResultList();
		return committees;
	}

	public List<Committee> findByCommitteeNames(final String[] committeeNames,
			final HouseType[] houseTypes, 
			final String locale) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT c" +
				" FROM Committee c JOIN c.committeeName cn" +
				" JOIN cn.committeeType ct JOIN ct.houseType ht" +
				" WHERE c.locale = '" + locale + "'");
		
		String committeeNamesFilter = 
			this.getCommitteeNamesFilter(committeeNames);
		query.append(committeeNamesFilter);
		
		String houseTypesFilter = this.getHouseTypesFilter(houseTypes);
		query.append(houseTypesFilter);
		
		TypedQuery<Committee> tQuery = 
			this.em().createQuery(query.toString(), Committee.class);
		List<Committee> committees = tQuery.getResultList();
		return committees;
	}
	
	//=============== INTERNAL METHODS =========
	private String getCommitteeNamesFilter(final String[] committeeNames) {
		StringBuffer sb = new StringBuffer();
		
		int n = committeeNames.length;
		if(n > 0) {
			sb.append(" AND (");
			for(int i = 0; i < n; i++) {
				sb.append(" cn.name = '" + committeeNames[i] + "'");
				if(i < n - 1) {
					sb.append(" OR ");
				}
			}			
			sb.append(")");
		}

		return sb.toString();
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