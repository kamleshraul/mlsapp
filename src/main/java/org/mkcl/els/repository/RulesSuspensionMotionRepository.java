package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.AdjournmentMotionDraft;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.RulesSuspensionMotionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

@Repository
public class RulesSuspensionMotionRepository extends BaseRepository<RulesSuspensionMotion, Serializable>{

	public Integer assignMotionNo(HouseType houseType, Date ruleSuspensionDate, String locale) {
		String strQuery = "SELECT m FROM RulesSuspensionMotion m " +
				"WHERE m.ruleSuspensionDate=:ruleSuspensionDate " +
				"AND m.houseType.id=:houseTypeId " +
				"AND m.locale=:locale " +
				"ORDER BY m.number DESC";
		TypedQuery<RulesSuspensionMotion> query = this.em().createQuery(strQuery, RulesSuspensionMotion.class);
		query.setParameter("ruleSuspensionDate", ruleSuspensionDate);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<RulesSuspensionMotion> rulesSuspensionMotions = query.getResultList();
		if(rulesSuspensionMotions==null || rulesSuspensionMotions.isEmpty()) {
			return 0;
		} else {
			return rulesSuspensionMotions.get(0).getNumber()==null? 0 : rulesSuspensionMotions.get(0).getNumber();
		}	
	}

	public Boolean isDuplicateNumberExist(Date ruleSuspensionDate, Integer number, Long id, String locale) {
		String strQuery = "SELECT m FROM RulesSuspensionMotion m" +
				" WHERE" +
				" m.ruleSuspensionDate=:ruleSuspensionDate" +
				" AND m.number=:number" +
				" AND m.id<>:motionId" +
				" AND m.locale=:locale";
		TypedQuery<RulesSuspensionMotion> query = this.em().createQuery(strQuery, RulesSuspensionMotion.class);
		query.setParameter("ruleSuspensionDate", ruleSuspensionDate);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("motionId", id);
		} else {
			query.setParameter("motionId", new Long("0"));
		}
		List<RulesSuspensionMotion> rulesSuspensionMotions = query.getResultList();
		if(rulesSuspensionMotions!=null && !rulesSuspensionMotions.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}

	public List<RulesSuspensionMotion> findAllReadyForSubmissionByMember(Session session, Member primaryMember,
			DeviceType motionType, Integer itemsCount, String locale) throws ELSException {
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.RULESSUSPENSIONMOTION_COMPLETE, locale);
			String query="SELECT m FROM RulesSuspensionMotion m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<RulesSuspensionMotion> m=this.em().createQuery(query, RulesSuspensionMotion.class);
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

	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(RulesSuspensionMotion rulesSuspensionMotion,
			String sortOrder) {
		String strQuery = "SELECT ce FROM RulesSuspensionMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:rulesSuspensionMotionId ORDER BY ce.rulesSuspensionMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("rulesSuspensionMotionId", rulesSuspensionMotion.getId());
		return query.getResultList();
	}

	public List<RevisionHistoryVO> getRevisions(Long rulesSuspensionMotionId, String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.RULESSUSPENSIONMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("rulesSuspensionMotionId",rulesSuspensionMotionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> rulesSuspensionMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO rulesSuspensionMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				rulesSuspensionMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				rulesSuspensionMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			rulesSuspensionMotionRevisionVO.setEditedBY(o[1].toString());
			rulesSuspensionMotionRevisionVO.setEditedOn(o[2].toString());
			rulesSuspensionMotionRevisionVO.setStatus(o[3].toString());
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
					rulesSuspensionMotionRevisionVO.setDetails(noticeContent);
				}else{
					rulesSuspensionMotionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				rulesSuspensionMotionRevisionVO.setDetails(o[4].toString());
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
					rulesSuspensionMotionRevisionVO.setSubject(subject);
				}else{
					rulesSuspensionMotionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				rulesSuspensionMotionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				rulesSuspensionMotionRevisionVO.setRemarks(o[6].toString());
			}			
			rulesSuspensionMotionRevisionVOs.add(rulesSuspensionMotionRevisionVO);
		}
		return rulesSuspensionMotionRevisionVOs;
	}

	public RulesSuspensionMotionDraft findPreviousDraft(Long id) {
		String query = "SELECT md" +
				" FROM RulesSuspensionMotion m JOIN m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
			TypedQuery<RulesSuspensionMotionDraft> tQuery = 
				this.em().createQuery(query, RulesSuspensionMotionDraft.class);
			tQuery.setParameter("mid", id);
			tQuery.setMaxResults(1);
			RulesSuspensionMotionDraft draft = tQuery.getSingleResult();
			return draft;
		}catch(Exception e){
			return null;
		}
	}

	public List<ClubbedEntity> findClubbedEntitiesByPosition(RulesSuspensionMotion rulesSuspensionMotion) {
		String strQuery = "SELECT ce FROM RulesSuspensionMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:rulesSuspensionMotionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("rulesSuspensionMotionId", rulesSuspensionMotion.getId());
		return query.getResultList();
	}

}
