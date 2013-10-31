package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ActSearchVO;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.OrdinanceSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillDraft;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;
import com.trg.search.SearchResult;

@Repository
public class BillRepository extends BaseRepository<Bill, Serializable>{

	public List<Bill> findAllByMember(final Session session,final Member primaryMember,
			final DeviceType deviceType,final Integer itemsCount,final String locale) {
		Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.BILL_COMPLETE, locale);
		Search search=new Search();
		search.addFilterEqual("session",session);
		search.addFilterEqual("primaryMember",primaryMember);
		search.addFilterEqual("type",deviceType);
		search.addFilterEqual("locale",locale);
		search.addFilterEqual("status",status);
		search.addSort("id",true);
		search.setMaxResults(itemsCount);
		return this.search(search);
	}
	
//	@SuppressWarnings("rawtypes")
//	public List<RevisionHistoryVO> getRevisions(final Long billId, final String locale) {		
//		String query = "SELECT rs.usergroup, rs.fullname, rs.editedon, rs.status," +
//				" rs.content, rs.title, rs.remark" +
//				" FROM ("+
//					" SELECT bd.edited_as as usergroup," +
//					" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
//					" as fullname, bd.edited_on as editedon, s.name as status," +
//					" bd.content as content, bd.title as title, bd.remarks as remark" +					
//					" FROM bills as b JOIN bills_drafts_association as bda "+
//					" JOIN bill_drafts as bd JOIN users as u JOIN credentials as c JOIN"+
//					" status as s" +
//					" WHERE b.id = bda.question_id " +
//					" AND bda.bill_draft_id = bd.id" +
//					" AND bd.recommendationstatus_id = s.id" +
//					" AND u.credential_id = c.id " +
//					" AND c.username = bd.edited_by" +
//					" AND b.id = " + billId + 
//					" ORDER BY bd.edited_on desc ) as rs";
//		
//		List results = this.em().createNativeQuery(query).getResultList();
//		List<RevisionHistoryVO> billRevisionVOs = new ArrayList<RevisionHistoryVO>();
//		for(Object i:results) {
//			Object[] o = (Object[]) i;
//			RevisionHistoryVO billRevisionVO = new RevisionHistoryVO();
//			if(o[0] != null) {
//				billRevisionVO.setEditedAs(o[0].toString());
//			}
//			else {
//				UserGroupType userGroupType = 
//					UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
//				billRevisionVO.setEditedAs(userGroupType.getName());
//			}
//			billRevisionVO.setEditedBY(o[1].toString());
//			billRevisionVO.setEditedOn(o[2].toString());
//			billRevisionVO.setStatus(o[3].toString());
//			billRevisionVO.setDetails(o[4].toString());
//			billRevisionVO.setSubject(o[5].toString());
//			if(o[6] != null){
//				billRevisionVO.setRemarks(o[6].toString());
//			}
//			billRevisionVOs.add(billRevisionVO);
//		}
//		return billRevisionVOs;
//	}
	
	public String getLatestRemarksOfActor(final Long billId, final String userGroupTypeName, final String username, final String locale) {
		String remarks = "";
		String queryString = "SELECT bd.remarks" +
							 " from bills as b JOIN bills_drafts_association as bda JOIN bill_drafts as bd" +
							 " WHERE b.id = bda.bill_id " +
							 " AND bda.bill_draft_id = bd.id" +
							 " AND b.id=:billId" +
							 " AND bd.edited_as=:userGroupTypeName" +
							 " AND bd.edited_by=:username" +
							 " ORDER BY bd.edited_on DESC";
		Query query = this.em().createNativeQuery(queryString);
		query.setParameter("userGroupTypeName", userGroupTypeName);
		query.setParameter("billId", billId);
		query.setParameter("username", username);
		query.setMaxResults(1);
		List resultList = query.getResultList();
		if(resultList!=null&&!resultList.isEmpty()) {
			remarks = (String) query.getResultList().get(0);
		}		
		return remarks;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Object[]> getRevisions(final Long billId, final String thingToBeRevised, final String locale) {	
		List<Object[]> revisions = new ArrayList<Object[]>();
		String query = "";
		if(thingToBeRevised.equals("titles")
					|| thingToBeRevised.equals("contentDrafts")
					|| thingToBeRevised.equals("statementOfObjectAndReasonDrafts")
					|| thingToBeRevised.equals("financialMemorandumDrafts")
					|| thingToBeRevised.equals("statutoryMemorandumDrafts")
					|| thingToBeRevised.equals("annexuresForAmendingBill")) {
			
			if(thingToBeRevised.equals("titles")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +							
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +							
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +							
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			} else if(thingToBeRevised.equals("contentDrafts")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.content, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +
							" (CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END) AS content, " +
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_contentdrafts` AS bdc ON (bdc.bill_draft_id = bd.id) " +
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +
							" LEFT JOIN `text_drafts` AS tdc ON (tdc.id = bdc.content_draft_id) " +
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id OR lang.id = tdc.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			} else if(thingToBeRevised.equals("statementOfObjectAndReasonDrafts")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.content, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +
							" (CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END) AS content, " +
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_statementofobjectandreasondrafts` AS bdc ON (bdc.bill_draft_id = bd.id) " +
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +
							" LEFT JOIN `text_drafts` AS tdc ON (tdc.id = bdc.statement_of_object_and_reason_draft_id) " +
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id OR lang.id = tdc.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			} else if(thingToBeRevised.equals("financialMemorandumDrafts")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.content, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +
							" (CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END) AS content, " +
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_financialmemorandumdrafts` AS bdc ON (bdc.bill_draft_id = bd.id) " +
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +
							" LEFT JOIN `text_drafts` AS tdc ON (tdc.id = bdc.financial_memorandum_draft_id) " +
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id OR lang.id = tdc.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			} else if(thingToBeRevised.equals("statutoryMemorandumDrafts")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.content, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +
							" (CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END) AS content, " +
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_statutorymemorandumdrafts` AS bdc ON (bdc.bill_draft_id = bd.id) " +
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +
							" LEFT JOIN `text_drafts` AS tdc ON (tdc.id = bdc.statutory_memorandum_draft_id) " +
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id OR lang.id = tdc.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			} else if(thingToBeRevised.equals("annexuresForAmendingBill")) {
				query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
						" rs.title, rs.content, rs.language_type, rs.language_name, rs.remark" +
						" FROM ("+
							" SELECT bd.edited_as as usergroup," +
							" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
							" as fullname, bd.edited_on as editedon, s.name as status," +
							" (CASE WHEN (lang.id=tdt.language_id) THEN tdt.text ELSE NULL END) AS title, " +
							" (CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END) AS content, " +
							" lang.type AS language_type, lang.name AS language_name, bd.remarks as remark" +					
							" FROM bills as b " +
							" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
							" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_titles` AS bdt ON (bdt.bill_draft_id = bd.id) " +
							" LEFT JOIN `billdrafts_annexuresforamendingbill` AS bdc ON (bdc.bill_draft_id = bd.id) " +
							" LEFT JOIN `text_drafts` AS tdt ON (tdt.id = bdt.title_id) " +
							" LEFT JOIN `text_drafts` AS tdc ON (tdc.id = bdc.annexure_for_amending_bill_draft_id) " +
							" LEFT JOIN languages AS lang ON (lang.id = tdt.language_id OR lang.id = tdc.language_id) " +
							" JOIN users as u " +
							" JOIN credentials AS c ON (u.credential_id = c.id) "+
							" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
							" WHERE c.username = bd.edited_by" +
							" AND b.id = " + billId + 
							" ORDER BY bd.edited_on DESC, lang.id DESC ) as rs";
			}
			List results = this.em().createNativeQuery(query).getResultList();						
			String editedOn = "";		
			Object[] revision = new Object[12];
			for(Object i:results) {
				Object[] o = (Object[]) i;
				String language = "";
				if(!thingToBeRevised.equals("titles")) {
					if(o[7]!=null) {
						language = o[7].toString();
					}
				} else {
					if(o[6]!=null) {
						language = o[6].toString();
					}				
				}			
				if(o[1] == null) {
					UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
					o[1] = userGroupType.getName();
				}			
				if(!editedOn.equals(o[3].toString())) {
					if(!revisions.isEmpty()) {						
						revision = new Object[12];	
					}
					revisions.add(revision);
					
					revision[0] = o[0];
					revision[1] = o[1];
					revision[2] = o[2];
					revision[3] = o[3];
					revision[4] = o[4];		
					if(!thingToBeRevised.equals("titles")) {
						revision[11] = o[9];
					} else {				
						revision[8] = o[8];
					}
					
					if(language.equals("marathi")) {
						if(o[5]!=null) {
							revision[5] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[8] = o[6];
							}
						}												
					} else if(language.equals("hindi")) {
						if(o[5]!=null) {
							revision[6] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[9] = o[6];
							}
						}						
					} else if(language.equals("english")) {
						if(o[5]!=null) {
							revision[7] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[10] = o[6];
							}
						}						
					}
					
					editedOn = o[3].toString();
				} else {
					if(language.equals("marathi")) {
						if(o[5]!=null) {
							revision[5] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[8] = o[6];
							}
						}						
					} else if(language.equals("hindi")) {
						if(o[5]!=null) {
							revision[6] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[9] = o[6];
							}
						}						
					} else if(language.equals("english")) {
						if(o[5]!=null) {
							revision[7] = o[5];
						}
						if(!thingToBeRevised.equals("titles")) {
							if(o[6]!=null) {
								revision[10] = o[6];
							}
						}						
					}					
				}
			}
			for(Object[] arr: revisions) {
				for(int j=0; j<12; j++){
					System.out.print("revision["+j+"]="+arr[j]);
				}
				System.out.println("\n");
			}			
		} else if(thingToBeRevised.equals("checklist")) {			
			query = "SELECT '" + thingToBeRevised + "', rs.usergroup, rs.fullname, rs.editedon, rs.status," +
					" rs.checkKey, rs.checkValue, rs.remark" +
					" FROM ("+
						" SELECT bd.edited_as as usergroup," +
						" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
						" as fullname, bd.edited_on as editedon, s.name as status," +
						" bdch.checklist_key as checkKey, bdch.checklist_value as checkValue, bd.remarks as remark" +					
						" FROM bills as b " +
						" JOIN bills_drafts_association AS bda ON (b.id = bda.bill_id) "+
						" JOIN bill_drafts AS bd ON (bda.bill_draft_id = bd.id) " +
						" LEFT JOIN `billdraft_checklist` AS bdch ON (bdch.bill_draft = bd.id) " +
						" JOIN users as u " +
						" JOIN credentials AS c ON (u.credential_id = c.id) "+
						" JOIN STATUS AS s ON (bd.recommendationstatus_id = s.id) " +
						" WHERE c.username = bd.edited_by" +
						" AND b.id = " + billId + 						
						" AND bdch.checklist_key IS NOT NULL" + 
						" ORDER BY" +
						" CASE bdch.checklist_key" +
						" WHEN 'isRecommendedAsPerConstitutionArticle_207_1' THEN 1" + 
						" WHEN 'sectionsForRecommendationAsPerConstitutionArticle_207_1' THEN 2" + 
						" WHEN 'isRecommendedAsPerConstitutionArticle_207_3' THEN 3" + 
						" WHEN 'sectionsForRecommendationAsPerConstitutionArticle_207_3' THEN 4" + 
						" WHEN 'isRecommendedAsPerConstitutionArticle_304_b' THEN 5" + 
						" WHEN 'sectionsForRecommendationAsPerConstitutionArticle_304_b' THEN 6" + 
						" WHEN 'isInScopeOfStateLegislature' THEN 7" + 
						" WHEN 'issuesInRelatedScheduleForScopeOfStateLegislature' THEN 8" + 
						" WHEN 'isMoneyBill' THEN 9" + 
						" WHEN 'sectionsForBeingMoneyBill' THEN 10" + 
						" WHEN 'isFinancialBillAsPerConstitutionArticle_207_1' THEN 11" + 
						" WHEN 'sectionsForFinancialBillAsPerConstitutionArticle_207_1' THEN 12" + 
						" WHEN 'areAmendmentsForAmendingBillAsPerScopeOfOriginalAct' THEN 13" + 
						" WHEN 'isStatutoryMemorandumMandatory' THEN 14" + 
						" WHEN 'isStatutoryMemorandumAsPerRules' THEN 15" + 
						" WHEN 'sectionsForStatutoryMemorandum' THEN 16" + 
						" WHEN 'isFinancialMemorandumMandatory' THEN 17" + 
						" WHEN 'isFinancialMemorandumAsPerRules' THEN 18" + 
						" WHEN 'sectionsForFinancialMemorandum' THEN 19" + 
						" WHEN 'isStatementOfObjectAndReasonComplete' THEN 20" + 
						" WHEN 'isLawAndJudiciaryDepartmentInAgreementWithOpinions' THEN 21" + 
						" WHEN 'isRecommendedOnSubjectMatterBySubLegislationCommittee' THEN 22" + 
						" END" + 
						" , bd.edited_on ASC) as rs";
			
			revisions = this.em().createNativeQuery(query).getResultList();	
			for(Object[] i: revisions) {
				if(i[6]!=null) {
					if(i[6].equals("yes")) {
						MessageResource displayValueMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.yes", locale);
						if(displayValueMessage!=null) {
							i[6]=displayValueMessage.getValue();
						}
					} else if(i[6].equals("no")) {
						MessageResource displayValueMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.no", locale);
						if(displayValueMessage!=null) {
							i[6]=displayValueMessage.getValue();
						}
					}
				}
				System.out.println(i[5] + ": " + i[6]);
			}
		}	
		return revisions;
	}

	public Integer assignBillNo(final String year, final HouseType houseType, final String locale) {
		String queryString="SELECT bill.number from bills bill LEFT JOIN devicetypes dt ON(bill.devicetype_id=dt.id)" +					
				" WHERE housetype_id="+houseType.getId() +
				" AND " +
				" CASE " +
				" WHEN dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + "' THEN year(bill.admission_date)="+year +
				" WHEN dt.type='"+ApplicationConstants.GOVERNMENT_BILL + "' THEN year(bill.submission_date)="+year +
				" END " +
				" ORDER BY bill.number DESC";		
		List results = this.em().createNativeQuery(queryString).setFirstResult(0).setMaxResults(1).getResultList();
		if(results == null) {
			return 0;
		}
		else if(results.isEmpty()) {
			return 0;
		}
		else {
			if(results.get(0) == null) {
				return 0;
			}else{
				return (Integer) results.get(0);
			}
		}		
	}
	
	/**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 * @throws ELSException 
	 */
	public List<Bill> findBillsForItroduction(final Session session,
			final DeviceType deviceType,
			final Status[] internalStatuses,
			final Status admitted,
			final Boolean useIntroductionDate, 
			final String sortOrder,
			final String locale) throws ELSException {		
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT * FROM bills b LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId " +
				" AND b.session_id=:sessionId " +
				" AND (ista.type <> 'bill_final_lapsed')" +
				((useIntroductionDate==true)? "":" AND b.expected_introduction_date IS NOT NULL"));

		try{
				
			query.append(" AND b.locale=:locale");
	
			query.append(this.getStatusFilters(internalStatuses));
	
			query.append(" AND b.status_id=:admittedStatus");
			
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY b.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY b.number DESC");
			}
	
			Query tQuery = this.em().createNativeQuery(query.toString(), Bill.class);
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("admittedStatus", admitted.getId());
			tQuery.setParameter("locale", locale);
			List<Bill> bills = tQuery.getResultList();
			return bills;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Bill>_find", "Cannot get Bill");
			throw elsException;
        }
	}
	/**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 * @throws ELSException 
	 */
	public List<Bill> find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean forBalloting,
			final String sortOrder,
			final String locale) throws ELSException {
		
		StringBuffer sb = new StringBuffer();
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT * FROM bills b" +
				" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id)" +
				" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id)" +
				" LEFT JOIN sessions as s ON (b.session_id=s.id) " +
				" LEFT JOIN members as m ON (b.member_id=m.id) " +
				" LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId " +
				" AND CASE " +
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_lowerhouse" +
				" ) THEN s.id IN (" + sb + ") " +					
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_upperhouse" +
				" ) THEN m.id IN (" + sb + ") END" +
				" AND (ista.type <> 'bill_final_lapsed')" +
				" AND b.expected_discussion_date IS NULL");

		try{
			if(forBalloting.booleanValue()){
				query.append(" AND b.ballotstatus_id IS NULL");
			}
	
			query.append(" AND b.locale=:locale");
	
			query.append(this.getStatusFilters(internalStatuses));
	
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY b.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY b.number DESC");
			}
	
			Query tQuery = this.em().createNativeQuery(query.toString(), Bill.class);
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
			tQuery.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
			tQuery.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
			tQuery.setParameter("locale", locale);
			List<Bill> bills = tQuery.getResultList();
			return bills;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Bill>_find", "Cannot get Bill");
			throw elsException;
        }
	}
	
	public List<Bill> findForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Status[] recommendationStatuses,
			final Boolean forBalloting,
			final String sortOrder,
			final String locale) throws ELSException {
		
		StringBuffer sb = new StringBuffer();
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT * FROM bills b" +
				" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id)" +
				" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id)" +
				" LEFT JOIN sessions as s ON (b.session_id=s.id) " +
				" LEFT JOIN members as m ON (b.member_id=m.id) " +
				" LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId " +
				" AND CASE " +
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_lowerhouse" +
				" ) THEN s.id IN (" + sb + ") " +					
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_upperhouse" +
				" ) THEN m.id IN (" + sb + ") END" +
				" AND (ista.type <> 'bill_final_lapsed')" +
				" AND b.expected_discussion_date IS NULL");

		try{
			if(forBalloting.booleanValue()){
				query.append(" AND b.ballotstatus_id IS NULL");
			}
	
			query.append(" AND b.locale=:locale");
	
			query.append(this.getInternalStatusFilters(internalStatuses));
			
			query.append(this.getRecommendationStatusFilters(recommendationStatuses));
	
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY b.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY b.number DESC");
			}
	
			Query tQuery = this.em().createNativeQuery(query.toString(), Bill.class);
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
			tQuery.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
			tQuery.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
			tQuery.setParameter("locale", locale);
			List<Bill> bills = tQuery.getResultList();
			return bills;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Bill>_find", "Cannot get Bill");
			throw elsException;
        }
	}
	
	private String getStatusFilters(final Status[] internalStatuses) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" b.internalstatus_id = " + internalStatuses[i].getId());
			if(i < n - 1) {
				sb.append(" OR");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Gets the status filters.
	 *
	 * @param internalStatuses the internal statuses
	 * @return the status filters
	 */
	private String getInternalStatusFilters(final Status[] internalStatuses) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" b.internalstatus_id = " + internalStatuses[i].getId());
			if(i < n - 1) {
				sb.append(" OR");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	private String getRecommendationStatusFilters(final Status[] recommendationStatuses) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = recommendationStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" b.recommendationstatus_id = " + recommendationStatuses[i].getId());
			if(i < n - 1) {
				sb.append(" OR");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public List<Member> findMembersAll(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean isPreBallot,
			final String sortOrder,
			final String locale) throws ELSException {
		
		StringBuffer sb = new StringBuffer();
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		StringBuffer query = new StringBuffer("SELECT * FROM members m" +			
				" WHERE m.id IN (SELECT DISTINCT(b.member_id) FROM bills b" +
				" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id) " +
				" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id) " +
				" LEFT JOIN sessions as bs ON (b.session_id=bs.id) " +
				" LEFT JOIN members as bm ON (b.member_id=bm.id) " +
				" LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId "+						
				" AND CASE " +
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_lowerhouse" +
				" ) THEN bs.id IN (" + sb + ") " +					
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_upperhouse" +
				" ) THEN bm.id IN (" + sb + ") END" +
				" AND (ista.type <> 'bill_final_lapsed')" +
				" AND b.locale=:locale)");
		try{
		if(!isPreBallot.booleanValue()){
			query.append(" AND b.expected_discussion_date IS NULL");
		}
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY b.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY b.number DESC");
		}
		query.append(")");
		Query tQuery = this.em().createNativeQuery(query.toString(), Member.class);		
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
		tQuery.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
		tQuery.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
		tQuery.setParameter("locale", locale);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Member>_findMembersAll", "Cannot get Members");
			throw elsException;
        }
		
	}
	
	public List<Member> findMembersAllForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Status[] recommendationStatuses,
			final Boolean isPreBallot,
			final String sortOrder,
			final String locale) throws ELSException {
		
		StringBuffer sb = new StringBuffer();
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		StringBuffer query = new StringBuffer("SELECT * FROM members m" +			
				" WHERE m.id IN (SELECT DISTINCT(b.member_id) FROM bills b" +
				" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id) " +
				" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id) " +
				" LEFT JOIN sessions as bs ON (b.session_id=bs.id) " +
				" LEFT JOIN members as bm ON (b.member_id=bm.id) " +
				" LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId "+						
				" AND CASE " +
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_lowerhouse" +
				" ) THEN bs.id IN (" + sb + ") " +					
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_upperhouse" +
				" ) THEN bm.id IN (" + sb + ") END" +
				" AND (ista.type <> 'bill_final_lapsed')" +
				" AND b.locale=:locale");
		try{
		if(!isPreBallot.booleanValue()){
			query.append(" AND b.expected_discussion_date IS NULL");
		}
		query.append(this.getInternalStatusFilters(internalStatuses));
		query.append(this.getRecommendationStatusFilters(recommendationStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY b.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY b.number DESC");
		}
		query.append(")");
		Query tQuery = this.em().createNativeQuery(query.toString(), Member.class);		
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
		tQuery.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
		tQuery.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
		tQuery.setParameter("locale", locale);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Member>_findMembersAll", "Cannot get Members");
			throw elsException;
        }
		
	}
	
	public Bill findBillForMemberOfUniqueSubject(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Long memberID, 
			final List<String> subjects, 
			final String locale) throws ELSException{

		Bill bill = null;
		try{
			//String discussionDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
			Status internalStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, locale);
			Status ballotStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_BALLOTED, locale);
	
			StringBuffer sb = new StringBuffer();
			if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
				List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
				System.out.println(checkSessions.size());					
				int index = 1;
				for(Session ss: checkSessions){
					sb.append(ss.getId());
					if(index < checkSessions.size()){
						sb.append(",");
					}else{
						break;
					}
					index++;
				}			
				checkSessions = null;
			} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
				List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
				System.out.println(checkMembers.size());
				int index = 1;
				for(Member m: checkMembers){
					sb.append(m.getId());
					if(index < checkMembers.size()){
						sb.append(",");
					}else{
						break;
					}
					index++;
				}			
				checkMembers = null;
			}
			
			StringBuffer strQuery = new StringBuffer("SELECT * FROM bills b" +
					" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id) " +
					" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id) " +
					" LEFT JOIN sessions as s ON (b.session_id=s.id) " +
					" LEFT JOIN members as m ON (b.member_id=m.id) " +
					" LEFT JOIN status rsta ON (rsta.id=b.recommendationstatus_id) " +
					" WHERE " +
					" CASE " +
					" WHEN (dt.type=:deviceType_nonofficial" + 
					" AND ht.type=:houseType_lowerhouse" +
					" ) THEN s.id IN (" + sb + ") " +					
					" WHEN (dt.type=:deviceType_nonofficial" + 
					" AND ht.type=:houseType_upperhouse" +
					" ) THEN m.id IN (" + sb + ") END" +					
					" AND b.expected_discussion_date IS NULL"+
					" AND b.member_id=:memberId AND b.number IS NOT NULL");
	
			strQuery.append(" AND b.internalstatus_id=:internalStatusId");
			
			strQuery.append(" AND (rsta.type='bill_processed_introduced' OR (rsta.type LIKE 'bill_processed_passed%' AND rsta.type LIKE '%firsthouse'))");
	
			strQuery.append(" AND (b.ballotstatus_id!=:ballotStatusId OR b.ballotstatus_id IS NULL) ORDER BY b.expected_discussion_date ASC");
	
			Query query = this.em().createNativeQuery(strQuery.toString(), Bill.class);
			query.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
			query.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
			query.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
			query.setParameter("memberId", memberID);
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("ballotStatusId", ballotStatus.getId());
			List<Bill> bills = query.getResultList();
			bill = randomBill(bills);
			return bill;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_Bill_findBillForMemberOfUniqueSubject", "Cannot get Bill");
			throw elsException;
        }
	}
	
	private Bill randomBill(final List<Bill> bills){
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		List<Bill> randomBillList = randomizeBills(bills);
		Bill bill = null;

		if(randomBillList.size() > 0){
			bill = randomBillList.get(Math.abs(rnd.nextInt() % bills.size())); 
		}
		return bill;		
	}
	
	private List<Bill> randomizeBills(final List<Bill> bills) {
		List<Bill> newBills = new ArrayList<Bill>();
		newBills.addAll(bills);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newBills, rnd);
		return newBills;
	}

	public List<Bill> findPendingBillsBeforeBalloting(final Session session,
			final DeviceType deviceType, final Date discussionDate, final String locale) throws ELSException {
		
//		CustomParameter dbDateFormat =
//				CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
//		String discussionDateForBallot = FormaterUtil.formatDateToString(discussionDate, dbDateFormat.getValue());
		
		StringBuffer sb = new StringBuffer();
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(session.getHouse(), session.getHouse().getFirstDate(), session.getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(session.getHouse(), locale);
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}	
		
		Status ballotStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_BALLOTED, locale);
		
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT * FROM bills b" +
				" LEFT JOIN devicetypes as dt ON (b.devicetype_id=dt.id)" +
				" LEFT JOIN housetypes as ht ON (b.housetype_id=ht.id)" +
				" LEFT JOIN housetypes as cht ON (b.current_housetype_id=cht.id)" +
				" LEFT JOIN sessions as s ON (b.session_id=s.id) " +
				" LEFT JOIN members as m ON (b.member_id=m.id) " +
				" LEFT JOIN status ista ON (ista.id=b.internalstatus_id) " +
				" LEFT JOIN status rsta ON (rsta.id=b.recommendationstatus_id) " +
				" WHERE b.devicetype_id=:deviceTypeId " +
				" AND CASE " +
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_lowerhouse" +
				" ) THEN s.id IN (" + sb + ") " +					
				" WHEN (dt.type=:deviceType_nonofficial" + 
				" AND ht.type=:houseType_upperhouse" +
				" ) THEN m.id IN (" + sb + ") END" +
				" AND (ista.type <> 'bill_final_lapsed')" +
				" AND b.ballotstatus_id=:ballotStatusId" +
				" AND b.current_housetype_id=:currentHouseTypeId" +
				" AND b.expected_discussion_date < :discussionDate");				

		try{
			query.append(" AND b.locale=:locale");	
			//discussion statuses for which bill may be pending
			Status toBeDiscussed = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED, locale);
			StringBuffer ds = new StringBuffer();
			ds.append(" AND(");
			ds.append(" rsta.priority >= " + toBeDiscussed.getPriority());
			ds.append(")");
			query.append(ds);
			Query tQuery = this.em().createNativeQuery(query.toString(), Bill.class);
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("deviceType_nonofficial", ApplicationConstants.NONOFFICIAL_BILL);
			tQuery.setParameter("houseType_lowerhouse", ApplicationConstants.LOWER_HOUSE);
			tQuery.setParameter("houseType_upperhouse", ApplicationConstants.UPPER_HOUSE);
			tQuery.setParameter("ballotStatusId", ballotStatus.getId());
			tQuery.setParameter("currentHouseTypeId", session.getHouse().getType().getId());
			tQuery.setParameter("discussionDate", discussionDate);
			tQuery.setParameter("locale", locale);
			List<Bill> bills = tQuery.getResultList();
			return bills;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillRepository_List<Bill>_find", "Cannot get Bill");
			throw elsException;
        }		
	}

	public List<ActSearchVO> fullTextSearchActForReferring(final String param,
			final String actYear, final String actDefaultLanguage, final String start, final String noOfRecords) {
		List<ActSearchVO> actSearchVOs = new ArrayList<ActSearchVO>();
		
		String matchQuery = "";				
		if(!param.contains("+")&&!param.contains("-")){
			matchQuery=" WHERE ((match(titleDraft.text) against('"+param+"' in natural language mode))" +					
					" || titleDraft.text LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			matchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			matchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))";
		}else if(param.contains("+")||param.contains("-")){
			matchQuery=" WHERE ((match(titleDraft.text) against('"+param+"' in boolean mode))";
		}
		
		String selectQuery = "SELECT a.id as actId, a.number as actNumber, a.year as actYear, " +
				"a.file_english as actFileEnglish, a.file_hindi as actFileHindi, a.file_marathi as actFileMarathi, " +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS actTitle, " +
				" lang.type AS languageType " +
				" FROM acts as a " +	
				" LEFT JOIN `acts_titles` AS at ON (at.act_id = a.id) " +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = at.title_id) " +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id) ";
		
		String orderByQuery=" ORDER BY a.id "+ApplicationConstants.DESC + ", lang.id "+ApplicationConstants.ASC;
				
		String finalQuery = "";		
		if(!actYear.equals("-")) {
			finalQuery = "SELECT rs.actId, rs.actNumber, rs.actYear, rs.actTitle, rs.languageType, " +
					" rs.actFileEnglish, rs.actFileHindi, rs.actFileMarathi " +					
					" FROM ("+selectQuery + matchQuery + " AND a.year = " + actYear + orderByQuery+") as rs LIMIT "+start+","+noOfRecords;
		} else {
			finalQuery = "SELECT rs.actId, rs.actNumber, rs.actYear, rs.actTitle, rs.languageType, " +
					" rs.actFileEnglish, rs.actFileHindi, rs.actFileMarathi " +					
					" FROM ("+selectQuery + matchQuery + orderByQuery+") as rs LIMIT "+start+","+noOfRecords;
		}
		
		List resultList = this.em().createNativeQuery(finalQuery).getResultList();
		String actId = "";
		ActSearchVO actSearchVO = new ActSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				String language = "";
				if(o[4]!=null) {
					language = o[4].toString();
				}
				if(!actId.equals(o[0].toString())) {
					if(!actSearchVOs.isEmpty()) {
						actSearchVO = new ActSearchVO();
					}
					actSearchVOs.add(actSearchVO);
					if(o[0] != null){
						actSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						actSearchVO.setNumber(o[1].toString());
					}
					if(o[2] != null){
						actSearchVO.setYear(o[2].toString());
					}
					if(o[5] != null){
						actSearchVO.setFileEnglish(o[5].toString());
					}if(o[6] != null){
						actSearchVO.setFileHindi(o[6].toString());
					}if(o[7] != null){
						actSearchVO.setFileMarathi(o[7].toString());
					}
					if(language.equals(actDefaultLanguage)) {
						if(o[3] != null){
							actSearchVO.setTitle(o[3].toString());
						}
					}
					actId = o[0].toString();
				} else {
					if(language.equals(actDefaultLanguage)) {
						if(o[3] != null){
							actSearchVO.setTitle(o[3].toString());
						}
					}
				}
			}
		}
		return actSearchVOs;
	}
	
	
	/****Find Ordinances for refering ****/
	public List<OrdinanceSearchVO> fullTextSearchOrdinanceForReferring(final String param,
			final String ordYear, 
			final String ordDefaultLanguage, 
			final String start, final String noOfRecords) {
		List<OrdinanceSearchVO> ordSearchVOs = new ArrayList<OrdinanceSearchVO>();
		
		String matchQuery = "";				
		if(!param.contains("+")&&!param.contains("-")){
			matchQuery=" WHERE ((match(titleDraft.text) against('"+param+"' in natural language mode))" +					
					" || titleDraft.text LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			matchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			matchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))";
		}else if(param.contains("+")||param.contains("-")){
			matchQuery=" WHERE ((match(titleDraft.text) against('"+param+"' in boolean mode))";
		}
		
		String selectQuery = "SELECT o.id as ordId, o.number as ordNumber, o.year as ordYear, " +
				"o.file_english as ordFileEnglish, o.file_hindi as ordFileHindi, o.file_marathi as ordFileMarathi, " +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS ordTitle, " +
				" lang.type AS languageType " +
				" FROM ordinances as o " +	
				" LEFT JOIN `ordinances_titles` AS ot ON (ot.ordinance_id = o.id) " +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = ot.title_id) " +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id) ";
		
		String orderByQuery=" ORDER BY o.id "+ApplicationConstants.DESC + ", lang.id "+ApplicationConstants.ASC;
				
		String finalQuery = "";		
		if(!ordYear.equals("-")) {
			finalQuery = "SELECT rs.ordId, rs.ordNumber, rs.ordYear, rs.ordTitle, rs.languageType, " +
					" rs.ordFileEnglish, rs.ordFileHindi, rs.ordFileMarathi " +					
					" FROM ("+selectQuery + matchQuery + " AND o.year = " + ordYear + orderByQuery+") as rs LIMIT "+start+","+noOfRecords;
		} else {
			finalQuery = "SELECT rs.ordId, rs.ordNumber, rs.ordYear, rs.ordTitle, rs.languageType, " +
					" rs.ordFileEnglish, rs.ordFileHindi, rs.ordFileMarathi " +					
					" FROM ("+selectQuery + matchQuery + orderByQuery+") as rs LIMIT "+start+","+noOfRecords;
		}
		
		List resultList = this.em().createNativeQuery(finalQuery).getResultList();
		String actId = "";
		OrdinanceSearchVO ordSearchVO = new OrdinanceSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				String language = "";
				if(o[4]!=null) {
					language = o[4].toString();
				}
				if(!actId.equals(o[0].toString())) {
					if(!ordSearchVOs.isEmpty()) {
						ordSearchVO = new OrdinanceSearchVO();
					}
					ordSearchVOs.add(ordSearchVO);
					if(o[0] != null){
						ordSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						ordSearchVO.setNumber(o[1].toString());
					}
					if(o[2] != null){
						ordSearchVO.setYear(o[2].toString());
					}
					if(o[5] != null){
						ordSearchVO.setFileEnglish(o[5].toString());
					}if(o[6] != null){
						ordSearchVO.setFileHindi(o[6].toString());
					}if(o[7] != null){
						ordSearchVO.setFileMarathi(o[7].toString());
					}
					if(language.equals(ordDefaultLanguage)) {
						if(o[3] != null){
							ordSearchVO.setTitle(o[3].toString());
						}
					}
					actId = o[0].toString();
				} else {
					if(language.equals(ordDefaultLanguage)) {
						if(o[3] != null){
							ordSearchVO.setTitle(o[3].toString());
						}
					}
				}
			}
		}
		return ordSearchVOs;
	}

	public List<Object> findBillDataForDocketReport(final String billId, final String language) {
		List<Object> billData = new ArrayList<Object>();
		String queryString = "SELECT b.id AS billId, b.number AS billNumber, " +
				"CASE " +
				"	WHEN dt.type='bills_nonofficial' THEN YEAR(b.admission_date) " +
				"	WHEN dt.type='bills_government' THEN YEAR(b.submission_date) " +
				"END AS billyear, " +
				"CONCAT(t.name, ' ', m.first_name, ' ', m.last_name)AS memberName, " +
				"mi.name AS billMinistry, " +
				"sd.name AS billSubDepartment, " +
				"CASE WHEN (title_lang.id=tdt.language_id) THEN tdt.text ELSE NULL END AS billTitle, " +
				"CASE WHEN (lang.id=tdc.language_id) THEN tdc.text ELSE NULL END AS billContent, " +
				"CASE WHEN (lang.id=tdsor.language_id) THEN tdsor.text ELSE NULL END AS billSOR, " +
				"CASE WHEN (lang.id=tdfm.language_id) THEN tdfm.text ELSE NULL END AS billFM, " +
				"CASE WHEN (lang.id=tdsm.language_id) THEN tdsm.text ELSE NULL END AS billSM, " +
				"CASE WHEN (bt.type='amending'  AND lang.id=tdan.language_id) THEN tdan.text ELSE NULL END AS billAnnexure, " +
				"title_lang.type AS billTitleLanguage, " +
				"b.`opinion_sought_from_law_andjd` AS billOpinion, " +
				"b.`recommendation_from_governor` AS billRecommendationFromGovernor, " +
				"b.`recommendation_from_president` AS billRecommendationFromPresident, " +				
				"lang.type AS billLanguage, " +
				"ht.name as billHouseType, " +
				"bt.type as billType " +
				"FROM bills b " +
				"LEFT JOIN housetypes ht ON (ht.id = b.housetype_id) " +
				"LEFT JOIN devicetypes dt ON (dt.id = b.devicetype_id) " +
				"LEFT JOIN billtypes bt ON (bt.id = b.billtype_id) " +
				"LEFT JOIN members m ON (m.id = b.member_id ) " +
				"LEFT JOIN titles t ON (t.id = m.title_id ) " +
				"LEFT JOIN ministries mi ON (mi.id = b.ministry_id ) " +
				"LEFT JOIN subdepartments sd ON (sd.id = b.subdepartment_id) " +
				"LEFT JOIN `bills_revisedtitles` AS brt ON (brt.bill_id = b.id) " +
				"LEFT JOIN `bills_revisedcontentdrafts` AS brc ON (brc.bill_id = b.id) " +
				"LEFT JOIN `bills_revisedstatementofobjectandreasondrafts` AS brsor ON (brsor.bill_id = b.id) " +
				"LEFT JOIN `bills_revisedfinancialmemorandumdrafts` AS brfm ON (brfm.bill_id = b.id) " +
				"LEFT JOIN `bills_revisedstatutorymemorandumdrafts` AS brsm ON (brsm.bill_id = b.id) " +
				"LEFT JOIN `bills_annexuresforamendingbill`  AS bran ON (bran.bill_id = b.id) " +
				"LEFT JOIN `text_drafts` AS tdt ON (tdt.id = brt.revised_title_id) " +
				"LEFT JOIN `text_drafts` AS tdc ON (tdc.id = brc.revised_content_draft_id) " +
				"LEFT JOIN `text_drafts` AS tdsor ON (tdsor.id = brsor.revised_statement_of_object_and_reason_draft_id) " +
				"LEFT JOIN `text_drafts` AS tdfm ON (tdfm.id = brfm.`revised_financial_memorandum_draft_id`) " +
				"LEFT JOIN `text_drafts` AS tdsm ON (tdsm.id = brsm.`revised_statutory_memorandum_draft_id`) " +
				"LEFT JOIN `text_drafts` AS tdan ON (tdan.id = bran.`annexure_for_amending_bill_id`) " +
				"LEFT JOIN languages AS lang ON (lang.type='"+language+"') " +
				"LEFT JOIN languages AS title_lang ON (title_lang.id=tdt.language_id) " +
				"WHERE b.id="+billId;
		billData = this.em().createNativeQuery(queryString).getResultList();
		Object[] result = new Object[20];
		for(Object i:billData) {
			Object[] o = (Object[]) i;
			String titleLanguage = (String) o[12];
			if(o[0]!=null) {
				result[0] = o[0].toString();
			} 
			if(o[1]!=null) {
				result[1] = o[1].toString();
			} 
			if(o[2]!=null) {
				result[2] = o[2].toString();				
			} 
			if(o[3]!=null) {
				result[3] = o[3].toString();
			} 
			if(o[4]!=null) {
				result[4] = o[4].toString();
			} 
			if(o[5]!=null) {
				result[5] = o[5].toString();
			} 
			if(o[6]!=null) {
				if(titleLanguage.equals(language)) {
					result[6]=o[6].toString();
				} else if(titleLanguage.equals("english")) {
					result[7]=o[6].toString();
				}
			} 
			if(o[7]!=null) {
				result[8] = o[7].toString();
			} 
			if(o[8]!=null) {
				result[9] = o[8].toString();
			} 
			if(o[9]!=null) {
				result[10] = o[9].toString();
			} 
			if(o[10]!=null) {
				result[11] = o[10].toString();
			} 
			if(o[11]!=null) {
				result[12] = o[11].toString();
			} 
			if(o[12]!=null) {
				result[13] = o[12].toString();
			} 
			if(o[13]!=null) {
				result[14] = o[13].toString();
			} 
			if(o[14]!=null) {
				result[15] = o[14].toString();
			} 
			if(o[15]!=null) {
				result[16] = o[15].toString();
			}
			if(o[16]!=null) {
				result[17] = o[16].toString();
			}
			if(o[17]!=null) {
				result[18] = o[17].toString();
			}
			if(o[18]!=null) {
				result[19] = o[18].toString();
			}
		}
		for(int i=0; i<result.length; i++) {
			if(result[i]==null) {
				result[i]="";
			}
		}
		billData = Arrays.asList(result);
		return billData;
	}

	public Date findIntroductionDate(final Bill bill) {
		Date introductionDate = null;
		Status admittedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, bill.getLocale());
		Status underConsiderationStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, bill.getLocale());
		Status introducedStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_INTRODUCED, bill.getLocale());
		String query = "SELECT bd.status_date " +
				" FROM bills_drafts_association bda INNER JOIN bill_drafts bd ON (bda.bill_draft_id=bd.id)" +
				" WHERE bda.bill_id="+bill.getId()+" AND bd.status_id="+admittedStatus.getId() + 
				" AND bd.internalstatus_id="+underConsiderationStatus.getId() +				
				" AND bd.recommendationstatus_id="+introducedStatus.getId() +				
				" ORDER BY bd.id asc";
		List result = this.em().createNativeQuery(query).setMaxResults(1).getResultList();
		if(result!=null) {
			if(!result.isEmpty()) {
				introductionDate = (Date) result.get(0);
			}
		}
		return introductionDate;		
	}
	
	@SuppressWarnings("unchecked")
	public BillDraft findStatusDate(final Bill bill, final Status recommendationStatus) {
		//Date statusDate = null;
		//Status recommendationStatus = Status.findByType(recommendationStatusType, bill.getLocale());
		String strQuery="SELECT bd FROM Bill m JOIN m.drafts bd WHERE bd.recommendationStatus.id=:recommendationStatus"+
		" AND m.id=:bill ORDER BY bd.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("recommendationStatus",recommendationStatus.getId());
		query.setParameter("bill",bill.getId());
		List<BillDraft> drafts=query.getResultList();
		if(drafts!=null&&!drafts.isEmpty()){
			return drafts.get(0);
		}
		return null;		
//		String query = "SELECT bd.status_date " +
//				" FROM bills_drafts_association bda INNER JOIN bill_drafts bd ON (bda.bill_draft_id=bd.id)" +
//				" WHERE bda.bill_id="+bill.getId()+
//				" AND bd.recommendationstatus_id="+recommendationStatus.getId() +				
//				" ORDER BY bd.id asc";
//		List result = this.em().createNativeQuery(query).setMaxResults(1).getResultList();
//		if(result!=null) {
//			if(!result.isEmpty()) {
//				statusDate = (Date) result.get(0);
//			}
//		}
//		return statusDate;		
	}
	
	public Date findDiscussionDate(final Bill bill, final String currentPosition) {
		Date discussionDate = null;
		Status admittedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, bill.getLocale());
		Status underConsiderationStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, bill.getLocale());
		Status consideredStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_CONSIDERED + currentPosition, bill.getLocale());
		String query = "SELECT bd.status_date " +
				" FROM bills_drafts_association bda INNER JOIN bill_drafts bd ON (bda.bill_draft_id=bd.id)" +
				" WHERE bda.bill_id="+bill.getId()+" AND bd.status_id="+admittedStatus.getId() + 
				" AND bd.internalstatus_id="+underConsiderationStatus.getId() +
				" AND bd.recommendationstatus_id="+consideredStatus.getId() +				
				" ORDER BY bd.id asc";
		List result = this.em().createNativeQuery(query).setMaxResults(1).getResultList();
		if(result!=null) {
			if(!result.isEmpty()) {
				discussionDate = (Date) result.get(0);
			}
		}
		return discussionDate;		
	}
	
	public Date findExpectedDiscussionDate(final Bill bill, final String currentPosition) {
		Date discussionDate = null;
		Status admittedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, bill.getLocale());
		Status underConsiderationStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, bill.getLocale());
		Status toBeDiscussedStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED + currentPosition, bill.getLocale());
		String query = "SELECT bd.expected_status_date " +
				" FROM bills_drafts_association bda INNER JOIN bill_drafts bd ON (bda.bill_draft_id=bd.id)" +
				" WHERE bda.bill_id="+bill.getId()+" AND bd.status_id="+admittedStatus.getId() + 
				" AND bd.internalstatus_id="+underConsiderationStatus.getId() +
				" AND bd.recommendationstatus_id="+toBeDiscussedStatus.getId() +				
				" ORDER BY bd.id asc";
		List result = this.em().createNativeQuery(query).setMaxResults(1).getResultList();
		if(result!=null) {
			if(!result.isEmpty()) {
				discussionDate = (Date) result.get(0);
			}
		}
		return discussionDate;		
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Bill bill) {
		String query = "SELECT ce " +
				" FROM Bill m JOIN m.clubbedEntities ce" +
				" WHERE m.id = " + bill.getId() +
				" ORDER BY ce.position " + ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesInAscendingOrder(final Bill bill, final String sortOrder, final String locale) {
		String query = "SELECT m " +
				" FROM Bill b JOIN b.clubbedEntities m" +
				" WHERE b.id = " + bill.getId() + 
				" ORDER BY m.bill.submissionDate " + sortOrder;
		return this.em().createQuery(query).getResultList();
	}
	
	public Reference findCurrentFile(Bill domain) {
		Search search=new Search();
		search.addFilterEqual("session",domain.getSession());
		search.addFilterEqual("type",domain.getType());
		search.addFilterEqual("locale",domain.getLocale());
		search.addSort("file",true);
		search.addSort("fileIndex",true);
		search.addFilterNotNull("file");
		//search.addFilterEqual("fileSent",false);
		List<Bill> bills=this.search(search);
		Bill bill=null;
		if(bills!=null&&!bills.isEmpty()){
			bill=bills.get(0);
		}
		if(bill==null){
			return new Reference(String.valueOf(1),String.valueOf(1));
		}else if(bill.getFile()==null){
			return new Reference(String.valueOf(1),String.valueOf(1));
		}else if(bill.getFile()!=null&&bill.getFileIndex()==null){
			return new Reference(String.valueOf(bill.getFile()),String.valueOf(1));
		}else{
			CustomParameter customParameter=CustomParameter.
			findByName(CustomParameter.class,"FILE_"+domain.getType().getType().toUpperCase(), "");
			int fileSize=Integer.parseInt(customParameter.getValue());
			Search search1=new Search();
			search1.addFilterEqual("session",bill.getSession());
			search1.addFilterEqual("type",bill.getType());
			search1.addFilterEqual("locale",bill.getLocale());
			search1.addFilterEqual("file",bill.getFile());
			search1.addSort("fileIndex",true);
			search1.addFilterNotNull("file");
			SearchResult<Bill> result=this.searchAndCount(search1);
			Integer count=result.getTotalCount();
			if(count==fileSize){
				return new Reference(String.valueOf(bill.getFile()+1),String.valueOf(1));
			}else{
				return new Reference(String.valueOf(bill.getFile()),String.valueOf(bill.getFileIndex()+1));
			}
		}
	}
	
	public List<Bill> findBillsByPriority(final Session session, 
								final DeviceType deviceType,
								final Status status,
								final Boolean useDiscussionDate,
								final String orderField,
								final String sortOrder,
								final String locale){
		
		StringBuffer strQuery = new StringBuffer("SELECT b from Bill b" +
					" WHERE b.type.id=:deviceTypeId" +
					" AND b.session.id=:sessionId" +
					" AND b.internalStatus.type <> 'bill_final_lapsed' " +
					" AND (b.recommendationStatus.type=:recStatus " +
					" OR b.recommendationStatus.type LIKE 'bill_processed_passed%firsthouse%'" +
					" OR b.recommendationStatus.type LIKE 'bill_processed_toBeDiscussed%')" +
					" AND b.locale=:locale" + 				
					((useDiscussionDate==true)? "":" AND b.expectedDiscussionDate IS NOT NULL") +
					" ORDER BY b.expectedIntroductionDate " + sortOrder);
					

		Query query = this.em().createQuery(strQuery.toString(), Bill.class);
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("recStatus", ApplicationConstants.BILL_PROCESSED_INTRODUCED);
		query.setParameter("locale", locale);
		List<Bill> bills = query.getResultList();
				
		return bills;
	}
	
 	public List<Bill> findBillsEligibleForDiscussionPriority(final Session session, 
			final DeviceType deviceType,
			final Boolean useDiscussionDate,
			final String orderField,
			final String sortOrder,
			final String locale){
 		
 			String query = "SELECT b FROM Bill b" +
					" WHERE b.type.id=:deviceTypeId" +
					" AND b.session.id=:sessionId" +
					" AND b.internalStatus.type <> 'bill_final_lapsed' " +
					" AND (b.recommendationStatus.type=:recStatus OR b.recommendationStatus.type LIKE 'bill_processed_passed%firsthouse')" +
					" AND b.locale=:locale" + 
					((useDiscussionDate==true)? "":" AND b.expectedDiscussionDate IS NOT NULL") +
					" ORDER BY b.expectedIntroductionDate " + sortOrder;
 			
 			TypedQuery<Bill> jpQuery = this.em().createQuery(query, Bill.class);
 			jpQuery.setParameter("recStatus", ApplicationConstants.BILL_PROCESSED_INTRODUCED);
 			jpQuery.setParameter("locale", locale);
 			List<Bill> bills = jpQuery.getResultList();
 			return bills;			
 		
 	}

	public Boolean isAnyBillSubmittedEarierThanCurrentBillToBePutup(Bill bill) {
		String queryString = "SELECT b FROM Bill b" +
				" WHERE b.internalStatus.type IN :eligibleStatuses" +
				" AND b.submissionDate < :billSubmissionDate";
		Query query = this.em().createQuery(queryString);		
		List<String> eligibleStatuses = new ArrayList<String>();
		eligibleStatuses.add(ApplicationConstants.BILL_SUBMIT);
		eligibleStatuses.add(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED);
		eligibleStatuses.add(ApplicationConstants.BILL_SYSTEM_TO_BE_PUTUP);
		query.setParameter("eligibleStatuses", eligibleStatuses);
		query.setParameter("billSubmissionDate", bill.getSubmissionDate());
		List result = query.getResultList();
		if(result!=null) {
			if(!result.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public BillDraft findDraftByRecommendationStatus(final Bill bill, final Status recommendationStatus) {
		String strQuery="SELECT bd FROM Bill m JOIN m.drafts bd WHERE bd.recommendationStatus.id=:recommendationStatus"+
				" AND m.id=:bill ORDER BY bd.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("recommendationStatus",recommendationStatus.getId());
		query.setParameter("bill",bill.getId());
		List<BillDraft> drafts=query.getResultList();
		if(drafts!=null&&!drafts.isEmpty()){
			return drafts.get(0);
		}
		return null;
	}
	
	public BillDraft findDraftByRecommendationStatusAndHouseRound(final Bill bill, final Status recommendationStatus, final Integer houseRound) {
		String strQuery="SELECT bd FROM Bill m JOIN m.drafts bd WHERE bd.recommendationStatus.id=:recommendationStatus"+
				" AND bd.houseRound=:houseRound AND m.id=:bill ORDER BY bd.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("recommendationStatus",recommendationStatus.getId());
		query.setParameter("houseRound",houseRound);
		query.setParameter("bill",bill.getId());		
		List<BillDraft> drafts=query.getResultList();
		if(drafts!=null&&!drafts.isEmpty()){
			return drafts.get(0);
		}
		return null;
	}
	
	public List<BillDraft> findStatusUpdationDraftsForGivenHouse(final Bill bill, final HouseType houseType) {
		Status statusForBeginningStatusUpdation = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, bill.getLocale());
		String strQuery="SELECT bd FROM Bill m JOIN m.drafts bd JOIN bd.houseType" +
				" WHERE bd.internalStatus.priority>=:statusUpdationPriority"+
				" AND bd.houseType.id=:houseType"+
				" AND m.id=:bill ORDER BY bd.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("statusUpdationPriority",statusForBeginningStatusUpdation.getPriority());
		query.setParameter("bill",bill.getId());
		query.setParameter("houseType",houseType.getId());
		return query.getResultList();
	}	
	
	public Bill findByNumberAndYear(final int billNumber, int billYear, final String locale) {
		Bill bill = null;
//		String queryString="SELECT bill from Bill bill JOIN DeviceType dt" +					
//				" WHERE bill.number=:billNumber" +
//				" AND " +
//				" CASE " +
//				" WHEN dt.type=:nb THEN year(bill.admissionDate)=:billYear" +
//				" WHEN dt.type=:gb THEN year(bill.submissionDate)=:billYear" +
//				" END " +
//				" AND bill.locale=:locale" +
//				" ORDER BY bill.number DESC";	
//		Query query = this.em().createQuery(queryString);
//		query.setParameter("billNumber", billNumber);
//		query.setParameter("billYear", billYear);
//		query.setParameter("nb", ApplicationConstants.NONOFFICIAL_BILL);
//		query.setParameter("gb", ApplicationConstants.GOVERNMENT_BILL);
//		query.setParameter("locale", locale);
//		try {
//			List<Bill> bills = query.getResultList();
//			return (Bill) query.getSingleResult();
//		} catch(Exception e) {
//			return null;
//		}
		String queryString="SELECT bill.id from bills bill LEFT JOIN devicetypes dt ON(bill.devicetype_id=dt.id)" +					
				" WHERE bill.number=:billNumber" +
				" AND " +
				" CASE " +
				" WHEN dt.type=:nb THEN year(bill.admission_date)=:billYear" +
				" WHEN dt.type=:gb THEN year(bill.submission_date)=:billYear" +
				" END " +
				" AND bill.locale=:locale" +
				" ORDER BY bill.number DESC";	
		Query query = this.em().createNativeQuery(queryString);
		query.setParameter("billNumber", billNumber);
		query.setParameter("billYear", billYear);
		query.setParameter("nb", ApplicationConstants.NONOFFICIAL_BILL);
		query.setParameter("gb", ApplicationConstants.GOVERNMENT_BILL);
		query.setParameter("locale", locale);
		try {
			Object billIdObj =  query.getSingleResult();
			if(billIdObj!=null) {
				bill =  Bill.findById(Bill.class, Long.parseLong(billIdObj.toString()));
			}			
		} catch(Exception e) {
			return bill;
		}
		return bill;
	}
}
