package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.AdjournmentMotionDraft;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class AdjournmentMotionRepository extends BaseRepository<AdjournmentMotion, Serializable> {

	public Integer assignMotionNo(final HouseType houseType, final Date adjourningDate, final String locale) {
		String strQuery = "SELECT m FROM AdjournmentMotion m " +
				"WHERE m.adjourningDate=:adjourningDate " +
				"AND m.houseType.id=:houseTypeId " +
				"AND m.locale=:locale " +
				"ORDER BY m.number DESC";
		TypedQuery<AdjournmentMotion> query = this.em().createQuery(strQuery, AdjournmentMotion.class);
		query.setParameter("adjourningDate", adjourningDate);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<AdjournmentMotion> adjournmentMotions = query.getResultList();
		if(adjournmentMotions==null || adjournmentMotions.isEmpty()) {
			return 0;
		} else {
			return adjournmentMotions.get(0).getNumber()==null? 0 : adjournmentMotions.get(0).getNumber();
		}		
	}
	
	public List<AdjournmentMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.ADJOURNMENTMOTION_COMPLETE, locale);
			String query="SELECT m FROM AdjournmentMotion m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<AdjournmentMotion> m=this.em().createQuery(query, AdjournmentMotion.class);
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
			elsException.setParameter("AdjournmentMotionRepository_List<AdjournmentMotion>_findAllByMember", "Cannot get the AdjournmentMotions ");
			throw elsException;
		}
	}

	public Boolean isDuplicateNumberExist(final Date adjourningDate, 
			final Integer number,
			final String locale) {
		// TODO Auto-generated method stub
		String strQuery = "SELECT m FROM AdjournmentMotion m" +
				" WHERE" +
				" m.adjourningDate=:adjourningDate" +
				" AND m.number=:number" +
				" AND m.locale=:locale";
		TypedQuery<AdjournmentMotion> query = this.em().createQuery(strQuery, AdjournmentMotion.class);
		query.setParameter("adjourningDate", adjourningDate);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		List<AdjournmentMotion> adjournmentMotions = query.getResultList();
		if(adjournmentMotions!=null && !adjournmentMotions.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	public AdjournmentMotionDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM AdjournmentMotion m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<AdjournmentMotionDraft> tQuery = 
			this.em().createQuery(query, AdjournmentMotionDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		AdjournmentMotionDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final AdjournmentMotion adjournmentMotion) {
		String strQuery = "SELECT ce FROM AdjournmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:adjournmentMotionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("adjournmentMotionId", adjournmentMotion.getId());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final AdjournmentMotion adjournmentMotion, final String sortOrder) {
		String strQuery = "SELECT ce FROM AdjournmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:adjournmentMotionId ORDER BY ce.adjournmentMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("adjournmentMotionId", adjournmentMotion.getId());
		return query.getResultList();
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param adjournmentMotionId the adjournmentMotion id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long adjournmentMotionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.ADJOURNMENTMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("adjournmentmotionId",adjournmentMotionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> adjournmentMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO adjournmentMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				adjournmentMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				adjournmentMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			adjournmentMotionRevisionVO.setEditedBY(o[1].toString());
			adjournmentMotionRevisionVO.setEditedOn(o[2].toString());
			adjournmentMotionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String noticeContent=d.diff_prettyHtml(diff);
					if(noticeContent.contains("&lt;")){
						noticeContent=noticeContent.replaceAll("&lt;", "<");
					}
					if(noticeContent.contains("&gt;")){
						noticeContent=noticeContent.replaceAll("&gt;", ">");
					}
					if(noticeContent.contains("&amp;nbsp;")){
						noticeContent=noticeContent.replaceAll("&amp;nbsp;"," ");
					}
					adjournmentMotionRevisionVO.setDetails(noticeContent);
				}else{
					adjournmentMotionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				adjournmentMotionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty() && !o1[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String subject=d.diff_prettyHtml(diff);
					if(subject.contains("&lt;")){
						subject=subject.replaceAll("&lt;", "<");
					}
					if(subject.contains("&gt;")){
						subject=subject.replaceAll("&gt;", ">");
					}
					if(subject.contains("&amp;nbsp;")){
						subject=subject.replaceAll("&amp;nbsp;"," ");
					}
					adjournmentMotionRevisionVO.setSubject(subject);
				}else{
					adjournmentMotionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				adjournmentMotionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				adjournmentMotionRevisionVO.setRemarks(o[6].toString());
			}			
			adjournmentMotionRevisionVOs.add(adjournmentMotionRevisionVO);
		}
		return adjournmentMotionRevisionVOs;
	}

}
