package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.GovernorSpeechNotice;
import org.mkcl.els.domain.GovernorSpeechNoticeDraft;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.SpecialMentionNoticeDraft;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;


@Repository
public class GovernorSpeechNoticeRepository extends BaseRepository<GovernorSpeechNotice, Serializable> {

	public GovernorSpeechNoticeDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM GovernorSpeechNotice m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<GovernorSpeechNoticeDraft> tQuery = 
			this.em().createQuery(query, GovernorSpeechNoticeDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		GovernorSpeechNoticeDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	public Boolean isDuplicateNumberExist(final Integer number,
			final Long id,
			final String locale) {
		
		String strQuery = "SELECT smn FROM GovernorSpeechNotice smn" +
				" WHERE" +
				" smn.number=:number" +
				" AND smn.id<>:motionId" +
				" AND smn.locale=:locale";
		TypedQuery<GovernorSpeechNotice> query = this.em().createQuery(strQuery, GovernorSpeechNotice.class);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("motionId", id);
		} else {
			query.setParameter("motionId", new Long("0"));
		}
		List<GovernorSpeechNotice> governorSpeechNotices = query.getResultList();
		if(governorSpeechNotices!=null && !governorSpeechNotices.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	public Integer assignMotionNo(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strNoticeType = type.getType();
		String strQuery = "SELECT m FROM GovernorSpeechNotice m "
				+ "JOIN m.session s JOIN m.type dt" +
		" WHERE dt.type =:noticeType AND s.id=:sessionId"+
		" ORDER BY m.number " +ApplicationConstants.DESC;	
		try {
			TypedQuery<GovernorSpeechNotice> query=this.em().createQuery(strQuery, GovernorSpeechNotice.class);
			query.setParameter("noticeType",strNoticeType);
			query.setParameter("sessionId",session.getId());
			List<GovernorSpeechNotice> notices = query.setFirstResult(0).
			setMaxResults(1).getResultList();
			if(notices == null) {
				return 0;
			}
			else if(notices.isEmpty()) {
				return 0;
			}
			else {
				if(notices.get(0).getNumber() == null) {
					return 0;
				}else{
					return notices.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Integer findCountOfNoticesBySpecificMemberAndSecificSession(final Session session, final String createdBy, final String locale) {
		
		Integer count = null;
		String strQuery = "SELECT n from GovernorSpeechNotice n " + "JOIN n.session s " 
		+ "WHERE s.id = :sessionId AND n.createdBy = :createdBy "
		+ "ORDER BY n.number";
		
		try {
		
			TypedQuery<GovernorSpeechNotice> query = this.em().createQuery(strQuery, GovernorSpeechNotice.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("createdBy", createdBy);
			List<GovernorSpeechNotice> notices = query.getResultList();
			
			if(notices.size() != 0) {
				count = notices.size();
			} 
			
			return count;
		
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
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
	public List<RevisionHistoryVO> getRevisions(final Long governorSpeechNoticeId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.GOVERNORSPEECHNOTICE_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("governorSpeechNoticeId",governorSpeechNoticeId);
		List results = query.getResultList();
		List<RevisionHistoryVO> governorSpeechNoticeRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO governorSpeechNoticeRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				governorSpeechNoticeRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				governorSpeechNoticeRevisionVO.setEditedAs(userGroupType.getName());
			}
			governorSpeechNoticeRevisionVO.setEditedBY(o[1].toString());
			governorSpeechNoticeRevisionVO.setEditedOn(o[2].toString());
			governorSpeechNoticeRevisionVO.setStatus(o[3].toString());
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
					governorSpeechNoticeRevisionVO.setDetails(noticeContent);
				}else{
					governorSpeechNoticeRevisionVO.setDetails(o[4].toString());
				}

			}else{
				governorSpeechNoticeRevisionVO.setDetails(o[4].toString());
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
					governorSpeechNoticeRevisionVO.setSubject(subject);
				}else{
					governorSpeechNoticeRevisionVO.setSubject(o[5].toString());
				}

			}else{
				governorSpeechNoticeRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				governorSpeechNoticeRevisionVO.setRemarks(o[6].toString());
			}			
			governorSpeechNoticeRevisionVOs.add(governorSpeechNoticeRevisionVO);
		}
		return governorSpeechNoticeRevisionVOs;
	}
}
