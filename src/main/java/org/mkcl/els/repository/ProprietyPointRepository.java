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
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.ProprietyPointDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class ProprietyPointRepository extends BaseRepository<AdjournmentMotion, Serializable> {

	public Integer assignNumber(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strProprietyPointType = type.getType();
		String strQuery = "SELECT m FROM ProprietyPoint m"
				+ " JOIN m.session s"
				+ " JOIN m.deviceType dt"
				+ " WHERE dt.type =:proprietyPointType"
				+ " AND s.id=:sessionId"
				+ " ORDER BY m.number " + ApplicationConstants.DESC;	
		try {
			TypedQuery<ProprietyPoint> query=this.em().createQuery(strQuery, ProprietyPoint.class);
			query.setParameter("proprietyPointType",strProprietyPointType);
			query.setParameter("sessionId",session.getId());
			List<ProprietyPoint> proprietyPoints = query.setFirstResult(0).setMaxResults(1).getResultList();
			
			if(proprietyPoints == null) {
				return 0;
			}else if(proprietyPoints.isEmpty()) {
				return 0;
			}else {
				if(proprietyPoints.get(0).getNumber() == null) {
					return 0;
				}else{
					return proprietyPoints.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Integer assignNumber(final HouseType houseType,
								final Session session,
								final Date proprietyPointDate,
								final String locale) {
		boolean isTodaySessionEndDate = false;
		if(DateUtil.compareDatePartOnly(new Date(), session.getEndDate())==0) {
			isTodaySessionEndDate = true;
		}
		String strQuery = "SELECT MAX(number) FROM propriety_points"
				+ " WHERE session_id=:sessionId"
				+ " AND propriety_point_date=:proprietyPointDate"
				+ " AND (CASE WHEN :isTodaySessionEndDate IS TRUE AND number IS NOT NULL THEN DATE(submission_date)=:sessionEndDate ELSE TRUE END)";
		try {
			Query query=this.em().createNativeQuery(strQuery);
			query.setParameter("sessionId",session.getId());
			query.setParameter("proprietyPointDate", proprietyPointDate);
			query.setParameter("isTodaySessionEndDate", isTodaySessionEndDate);
			query.setParameter("sessionEndDate", session.getEndDate());
			
			Integer maxNumber = (Integer) query.getSingleResult();
			if(maxNumber == null) {
				return 0;
			} else {
				return maxNumber;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public List<ProprietyPoint> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.PROPRIETYPOINT_COMPLETE, locale);
			String query="SELECT m FROM ProprietyPoint m WHERE m.session.id=:sessionId"+
					" AND m.deviceType.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<ProprietyPoint> m=this.em().createQuery(query, ProprietyPoint.class);
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
			elsException.setParameter("ProprietyPointRepository_List<ProprietyPoint>_findAllByMember", "Cannot get the ProprietyPoints ");
			throw elsException;
		}
	}

	public Boolean isDuplicateNumberExist(final Integer number,	final Long id, final String locale) {
		String strQuery = "SELECT m FROM ProprietyPoint m" +
				" WHERE m.number=:number" +
				" AND m.id<>:proprietyPointId" +
				" AND m.locale=:locale";
		TypedQuery<ProprietyPoint> query = this.em().createQuery(strQuery, ProprietyPoint.class);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("proprietyPointId", id);
		} else {
			query.setParameter("proprietyPointId", new Long("0"));
		}
		List<ProprietyPoint> proprietyPoints = query.getResultList();
		if(proprietyPoints!=null && !proprietyPoints.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	public ProprietyPointDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM ProprietyPoint m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<ProprietyPointDraft> tQuery = 
			this.em().createQuery(query, ProprietyPointDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		ProprietyPointDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param adjournmentMotionId the adjournmentMotion id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long proprietyPointId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.PROPRIETYPOINT_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("proprietypointId",proprietyPointId);
		List results = query.getResultList();
		List<RevisionHistoryVO> proprietyPointRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO proprietyPointRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				proprietyPointRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				proprietyPointRevisionVO.setEditedAs(userGroupType.getName());
			}
			proprietyPointRevisionVO.setEditedBY(o[1].toString());
			proprietyPointRevisionVO.setEditedOn(o[2].toString());
			proprietyPointRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String pointsOfPropriety=d.diff_prettyHtml(diff);
					if(pointsOfPropriety.contains("&lt;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&lt;", "<");
					}
					if(pointsOfPropriety.contains("&gt;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&gt;", ">");
					}
					if(pointsOfPropriety.contains("&amp;nbsp;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&amp;nbsp;"," ");
					}
					proprietyPointRevisionVO.setDetails(pointsOfPropriety);
				}else{
					proprietyPointRevisionVO.setDetails(o[4].toString());
				}

			}else{
				proprietyPointRevisionVO.setDetails(o[4].toString());
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
					proprietyPointRevisionVO.setSubject(subject);
				}else{
					proprietyPointRevisionVO.setSubject(o[5].toString());
				}

			}else{
				proprietyPointRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				proprietyPointRevisionVO.setRemarks(o[6].toString());
			}			
			proprietyPointRevisionVOs.add(proprietyPointRevisionVO);
		}
		return proprietyPointRevisionVOs;
	}

}
