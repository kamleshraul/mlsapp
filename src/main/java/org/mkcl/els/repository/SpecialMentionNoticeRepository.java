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
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.SpecialMentionNoticeDraft;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

@Repository
public class SpecialMentionNoticeRepository  extends BaseRepository<SpecialMentionNotice, Serializable> {
	public Integer assignMotionNo(final HouseType houseType, final Date specialMentionNoticeDate, final String locale) {
		String strQuery = "SELECT m FROM SpecialMentionNotice m " +
				"WHERE m.specialMentionNoticeDate=:specialMentionNoticeDate " +
				"AND m.houseType.id=:houseTypeId " +
				"AND m.locale=:locale " +
				"ORDER BY m.number DESC";
		TypedQuery<SpecialMentionNotice> query = this.em().createQuery(strQuery, SpecialMentionNotice.class);
		query.setParameter("specialMentionNoticeDate", specialMentionNoticeDate);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<SpecialMentionNotice> specialMentionNotices = query.getResultList();
		if(specialMentionNotices==null || specialMentionNotices.isEmpty()) {
			return 0;
		} else {
			return specialMentionNotices.get(0).getNumber()==null? 0 : specialMentionNotices.get(0).getNumber();
		}		
	}
	
	public List<SpecialMentionNotice> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SPECIALMENTIONNOTICE_COMPLETE, locale);
			String query="SELECT m FROM SpecialMentionNotice m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<SpecialMentionNotice> m=this.em().createQuery(query, SpecialMentionNotice.class);
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
			elsException.setParameter("SpecialMentionNoticeRepository_List<SpecialMentionNotice>_findAllByMember", "Cannot get the SpecialMentionNotices ");
			throw elsException;
		}
	}
	
	public Boolean isDuplicateNumberExist(final Date specialMentionNoticeDate, 
			final Integer number,
			final Long id,
			final String locale) {
		// TODO Auto-generated method stub
		String strQuery = "SELECT smn FROM SpecialMentionNotice smn" +
				" WHERE" +
				" smn.specialMentionNoticeDate=:specialMentionNoticeDate" +
				" AND smn.number=:number" +
				" AND smn.id<>:motionId" +
				" AND smn.locale=:locale";
		TypedQuery<SpecialMentionNotice> query = this.em().createQuery(strQuery, SpecialMentionNotice.class);
		query.setParameter("specialMentionNoticeDate", specialMentionNoticeDate);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("motionId", id);
		} else {
			query.setParameter("motionId", new Long("0"));
		}
		List<SpecialMentionNotice> specialMentionNotices = query.getResultList();
		if(specialMentionNotices!=null && !specialMentionNotices.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final SpecialMentionNotice specialMentionNotice) {
		String strQuery = "SELECT ce FROM SpecialMentionNotice m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:specialMentionNoticeId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("specialMentionNoticeId", specialMentionNotice.getId());
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final SpecialMentionNotice specialMentionNotice, final String sortOrder) {
		String strQuery = "SELECT ce FROM SpecialMentionNotice m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:specialMentionNoticeId ORDER BY ce.specialMentionNotice.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("specialMentionNoticeId", specialMentionNotice.getId());
		return query.getResultList();
	}
	
	public SpecialMentionNoticeDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM SpecialMentionNotice m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<SpecialMentionNoticeDraft> tQuery = 
			this.em().createQuery(query, SpecialMentionNoticeDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		SpecialMentionNoticeDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param specialMentionNoticeId the specialMentionNotice id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long specialMentionNoticeId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.SPECIALMENTIONNOTICEN_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("specialMentionNoticeId",specialMentionNoticeId);
		List results = query.getResultList();
		List<RevisionHistoryVO> specialMentionNoticeRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO specialMentionNoticeRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				specialMentionNoticeRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				specialMentionNoticeRevisionVO.setEditedAs(userGroupType.getName());
			}
			specialMentionNoticeRevisionVO.setEditedBY(o[1].toString());
			specialMentionNoticeRevisionVO.setEditedOn(o[2].toString());
			specialMentionNoticeRevisionVO.setStatus(o[3].toString());
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
					specialMentionNoticeRevisionVO.setDetails(noticeContent);
				}else{
					specialMentionNoticeRevisionVO.setDetails(o[4].toString());
				}

			}else{
				specialMentionNoticeRevisionVO.setDetails(o[4].toString());
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
					specialMentionNoticeRevisionVO.setSubject(subject);
				}else{
					specialMentionNoticeRevisionVO.setSubject(o[5].toString());
				}

			}else{
				specialMentionNoticeRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				specialMentionNoticeRevisionVO.setRemarks(o[6].toString());
			}			
			specialMentionNoticeRevisionVOs.add(specialMentionNoticeRevisionVO);
		}
		return specialMentionNoticeRevisionVOs;
	}

}
