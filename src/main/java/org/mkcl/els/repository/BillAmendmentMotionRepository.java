package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SectionAmendmentVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class BillAmendmentMotionRepository extends BaseRepository<BillAmendmentMotion, Serializable>{

	public Integer assignBillAmendmentMotionNo(final Bill amendedBill) {		
		String queryString = "SELECT m FROM BillAmendmentMotion m LEFT JOIN m.amendedBill b WHERE b.id=:billId ORDER BY m.number DESC";
		Query query = this.em().createQuery(queryString);
		query.setParameter("billId", amendedBill.getId());
		@SuppressWarnings("unchecked")
		List<BillAmendmentMotion> billAmendmentMotions = query.setFirstResult(0).setMaxResults(1).getResultList();
		if(billAmendmentMotions==null || billAmendmentMotions.isEmpty() || billAmendmentMotions.get(0).getNumber() == null) {
			return 0;
		} else {
			return billAmendmentMotions.get(0).getNumber();
		}		
	}
	
	public Boolean isDuplicateNumberExist(final BillAmendmentMotion billAmendmentMotion) {
		Boolean isDuplicateNumberExist = false;
		
		if(billAmendmentMotion!=null 
				&& billAmendmentMotion.getAmendedBill()!=null
				&& billAmendmentMotion.getNumber()!=null) {
			String queryString = "SELECT m FROM BillAmendmentMotion m"
					+ " LEFT JOIN m.amendedBill b"
					+ " WHERE b.id=:billId"
					+ " AND ((:motionId IS NOT NULL AND m.id<>:motionId) OR (:motionId IS NULL))"
					+ " AND m.number=:motionNumber";
			
			TypedQuery<BillAmendmentMotion> query = this.em().createQuery(queryString, BillAmendmentMotion.class);
			query.setParameter("billId", billAmendmentMotion.getAmendedBill().getId());
			query.setParameter("motionNumber", billAmendmentMotion.getNumber());
			query.setParameter("motionId", billAmendmentMotion.getId());
			List<BillAmendmentMotion> duplicateMotions = query.setMaxResults(1).getResultList();
			if(duplicateMotions!=null && !duplicateMotions.isEmpty()) {
				isDuplicateNumberExist = true;
			}
		}
		
		return isDuplicateNumberExist;
	}
	
	public List<BillAmendmentMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE, locale);
			String query="SELECT m FROM BillAmendmentMotion m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<BillAmendmentMotion> m=this.em().createQuery(query, BillAmendmentMotion.class);
			m.setParameter("sessionId", session.getId());
			m.setParameter("deviceTypeId", motionType.getId());
			m.setParameter("memberId", primaryMember.getId());
			m.setParameter("locale", locale);
			m.setParameter("statusId", status.getId());
			m.setMaxResults(itemsCount);
			return m.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("BillAmendmentMotionRepository_List<BillAmendmentMotion>_findAllByMember", "Cannot get the BillAmendmentMotions ");
			throw elsException;
		}
	}
	
	public int findHighestFileNo(final Session session,final DeviceType motionType,
			final String locale) {
		String strQuery="SELECT m FROM BillAmendmentMotion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.file IS NOT NULL" +
				" ORDER BY m.file";
		
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", session.getId());
		query.setParameter("motionTypeId",motionType.getId());
		query.setParameter("locale",locale);
		@SuppressWarnings("unchecked")
		List<BillAmendmentMotion> billAmendmentMotions= query.getResultList();
		if(billAmendmentMotions==null || billAmendmentMotions.isEmpty()){
			return 0;
		}else{
			 return billAmendmentMotions.get(0).getFile();
		}
	}

	public List<RevisionHistoryVO> findRevisions(final Long billAmendmentMotionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.BILLAMENDMENTMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("billAmendmentMotionId",billAmendmentMotionId);
		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
		List<RevisionHistoryVO> billAmendmentMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO billAmendmentMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				billAmendmentMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.MEMBER, locale);
				billAmendmentMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			billAmendmentMotionRevisionVO.setEditedBY(o[1].toString());
			billAmendmentMotionRevisionVO.setEditedOn(o[2].toString());
			billAmendmentMotionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){				
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					List<SectionAmendmentVO> sectionAmendments = new ArrayList<SectionAmendmentVO>();
					String[] secAmendmentsForLanguage = o[4].toString().split("##");
					String[] secAmendmentsOfNextRevisionForLanguage = o1[4].toString().split("##");
					for(String secAmendmentForLanguage: secAmendmentsForLanguage) {
						String[] secAmendmentData = secAmendmentForLanguage.split("~");
						SectionAmendmentVO sectionAmendment = new SectionAmendmentVO();
						sectionAmendment.setLanguage(secAmendmentData[0]);
						sectionAmendment.setLanguageName(secAmendmentData[1]);
						boolean isInPreviousRevision = false;
						for(String secAmendmentOfNextRevisionForLanguage: secAmendmentsOfNextRevisionForLanguage) {
							String[] secAmendmentDataOfNextRevision = secAmendmentOfNextRevisionForLanguage.split("~");
							if(secAmendmentData[0].equals(secAmendmentDataOfNextRevision[0])) {
								/** amended section number with diff match patch **/
								LinkedList<Diff> diff=d.diff_main(secAmendmentDataOfNextRevision[2], secAmendmentData[2]);
								String amendedSectionNumber=d.diff_prettyHtml(diff);
								sectionAmendment.setAmendedSectionNumber(amendedSectionNumber);
								/** amending content with diff match patch **/
								diff=d.diff_main(secAmendmentDataOfNextRevision[3], secAmendmentData[3]);
								String amendingContent=d.diff_prettyHtml(diff);
								if(amendingContent.contains("&lt;")){
									amendingContent=amendingContent.replaceAll("&lt;", "<");
								}
								if(amendingContent.contains("&gt;")){
									amendingContent=amendingContent.replaceAll("&gt;", ">");
								}
								if(amendingContent.contains("&amp;nbsp;")){
									amendingContent=amendingContent.replaceAll("&amp;nbsp;"," ");
								}
								sectionAmendment.setAmendingContent(amendingContent);
								isInPreviousRevision = true;
							}
						}
						if(!isInPreviousRevision) {
							sectionAmendment.setAmendedSectionNumber(secAmendmentData[2]);
							sectionAmendment.setAmendingContent(secAmendmentData[3]);
						}
						sectionAmendments.add(sectionAmendment);
					}
					billAmendmentMotionRevisionVO.setSectionAmendments(sectionAmendments);
				}
			}else{
				if(!o[4].toString().isEmpty()){
					List<SectionAmendmentVO> sectionAmendments = new ArrayList<SectionAmendmentVO>();
					String[] secAmendmentsForLanguage = o[4].toString().split("##");
					for(String secAmendmentForLanguage: secAmendmentsForLanguage) {
						String[] secAmendmentData = secAmendmentForLanguage.split("~");
						SectionAmendmentVO sectionAmendment = new SectionAmendmentVO();
						sectionAmendment.setLanguage(secAmendmentData[0]);
						sectionAmendment.setLanguageName(secAmendmentData[1]);					
						sectionAmendment.setAmendedSectionNumber(secAmendmentData[2]);
						sectionAmendment.setAmendingContent(secAmendmentData[3]);
						sectionAmendments.add(sectionAmendment);
					}
					billAmendmentMotionRevisionVO.setSectionAmendments(sectionAmendments);
				}
			}
			if(o[5] != null){
				billAmendmentMotionRevisionVO.setRemarks(o[5].toString());
			}			
			billAmendmentMotionRevisionVOs.add(billAmendmentMotionRevisionVO);
		}
		return billAmendmentMotionRevisionVOs;
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final BillAmendmentMotion billAmendmentMotion) {
		String strQuery = "SELECT ce FROM BillAmendmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:billAmendmentMotionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("billAmendmentMotionId", billAmendmentMotion.getId());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final BillAmendmentMotion billAmendmentMotion, final String sortOrder) {
		String strQuery = "SELECT ce FROM BillAmendmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:billAmendmentMotionId ORDER BY ce.adjournmentMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("billAmendmentMotionId", billAmendmentMotion.getId());
		return query.getResultList();
	}
	
}
