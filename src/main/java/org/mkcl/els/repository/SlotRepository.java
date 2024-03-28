package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Adjournment;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class SlotRepository extends BaseRepository<Slot, Serializable>{

	@SuppressWarnings("unchecked")
	public Slot lastGeneratedSlot(final Roster roster) {
		//add in query only slots that have delete flag=false
		String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.blnDeleted=false" +
				" ORDER BY s.endTime "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("roster",roster.getId());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Slot lastAdjournedSlot(final Roster roster,final Adjournment adjournment) {
		//add in query only slots that have delete flag as false
		String strQuery="SELECT s.* FROM slots s "
				+ " WHERE s.roster=:roster AND s.start_time>=:startTime" 
				+ " AND s.end_time<=:endTime "
				+ " AND s.bln_deleted=true ORDER BY s.end_time " +
				 ApplicationConstants.DESC + " ,s.id DESC"  ;
		Query query=this.em().createNativeQuery(strQuery,Slot.class);
		query.setParameter("roster",roster.getId());
		query.setParameter("startTime", adjournment.getStartTime());
		query.setParameter("endTime", adjournment.getEndTime());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public HouseType getHouseType(final Slot slot) {
		try {
			String strQuery="SELECT ht FROM Slot sl JOIN sl.roster r JOIN r.session s JOIN s.house h Join h.type ht " +
							" WHERE sl.id=:slot";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("slot",slot.getId());
			return (HouseType) query.getSingleResult();
		} catch (Exception e) {
			logger.error("HOUSE TYPE NOT FOUND",e);
			return null;
		}
	}

	public Language getLanguage(final Slot slot) {
		try {
			String strQuery="SELECT l FROM Slot s JOIN s.roster r JOIN r.language l WHERE s.id=:slot";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("slot",slot.getId());
			return (Language) query.getSingleResult();
		} catch (Exception e) {
			logger.error("LANGUAGE NOT FOUND",e);
			return null;
		}
	}

	public User getUser(final Slot slot) {
		try {
			String strQuery="SELECT u FROM Slot s JOIN s.reporter r JOIN r.user u WHERE s.id=:slot";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("slot",slot.getId());
			return (User) query.getSingleResult();
		} catch (Exception e) {
			logger.error("USER NOT FOUND",e);
			return null;
		}
	}

	public Slot findByEndTime(final Roster roster,final Date endTime) {
		try {
			String strQuery="SELECT s FROM Slot s  WHERE s.roster.id=:roster s.endTime=:endTime AND s.blnDeleted=false";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",roster.getId());
			query.setParameter("endTime",endTime);
			return (Slot) query.getSingleResult();
		} catch (Exception e) {
			logger.error("SLOT NOT FOUND",e);
			return null;
		}
	}
	
	public Slot findByStartTime(final Roster roster,final Date startTime) {
		try {
			String strQuery="SELECT s FROM Slot s WHERE  s.roster.id=:roster AND s.startTime=:startTime AND s.blnDeleted=false";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",roster.getId());
			query.setParameter("startTime",startTime);
			return (Slot) query.getSingleResult();
		} catch (Exception e) {
			logger.error("SLOT NOT FOUND",e);
			return null;
		}
	}
	
	public List<Slot> findSlotsByLanguageContainingSlotTime(final Language language,final Slot slot) {
		String strQuery="SELECT s FROM Slot s JOIN s.reporter r JOIN r.user u" +
				" WHERE s.startTime<=:endTime AND s.endTime>=:startTime" +
				" AND u.language=:language AND s.blnDeleted=false";
		if(slot.getRoster().getSession() != null){
			strQuery= strQuery + " AND s.roster.session.id=:sessionId";
		}else if(slot.getRoster().getCommitteeMeeting() != null){
			strQuery= strQuery + " AND s.roster.committeeMeeting.id=:committeeMeetingId";
		}
				
		Query query=this.em().createQuery(strQuery);
		query.setParameter("endTime", slot.getEndTime());
		query.setParameter("startTime", slot.getStartTime());
		query.setParameter("language", language.getName());
		if(slot.getRoster().getSession() != null){
			query.setParameter("sessionId", slot.getRoster().getSession().getId());
		}else if(slot.getRoster().getCommitteeMeeting() != null){
			query.setParameter("committeeMeetingId", slot.getRoster().getCommitteeMeeting().getId());
		}
		List<Slot> result=query.getResultList();
		return result;
	}

	public List<User> findDifferentLanguageUsersBySlot(Slot s) {
		String strQuery="SELECT u FROM Slot s "
				+ " JOIN s.reporter r "
				+ " JOIN r.user u"
				+ " JOIN s.roster ro" 
				+ " WHERE s.startTime<:endTime AND s.endTime>:startTime AND s.blnDeleted=false"
				+ " AND (ro.session.id=:id OR ro.committeeMeeting.id=:id)"
				+ " ORDER BY ro.language";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("endTime", s.getEndTime());
		query.setParameter("startTime", s.getStartTime());
		Roster r = s.getRoster();
		if(r.getSession()!= null){
			query.setParameter("id", r.getSession().getId());
		}else if(r.getCommitteeMeeting() != null){
			query.setParameter("id", r.getCommitteeMeeting().getId());
		}
		
		List<User> result=query.getResultList();
		return result;
	}

	public List<Slot> findSlotsBySessionAndLanguage(Session session,
			Language language) {
		String strQuery="SELECT s FROM Slot s JOIN s.roster r " +
				" WHERE r.session=:session AND r.language=:language AND s.blnDeleted=false";
				
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session",session);
		query.setParameter("language", language);
		List<Slot> result=query.getResultList();
		return result;
	}

	public List<Slot> findSlotsByReporterAndRoster(Roster roster,
			Reporter reporter) {
		String strQuery="SELECT DISTINCT s FROM Slot s JOIN s.roster r " +
				" WHERE r.id=:rosterId AND s.reporter=:reporter AND s.blnDeleted=false";
				
		Query query=this.em().createQuery(strQuery);
		query.setParameter("rosterId",roster.getId());
		query.setParameter("reporter", reporter);
		List<Slot> result=query.getResultList();
		return result;
	}

	public List<Slot> findSlotsByMemberAndRoster(Roster roster, Member member) {
		String strQuery="SELECT DISTINCT s FROM Part p JOIN p.proceeding proc JOIN proc.slot s " +
				" WHERE s.roster=:roster AND (p.primaryMember=:member OR p.substituteMember=:member)";
				
		Query query=this.em().createQuery(strQuery);
		query.setParameter("roster",roster);
		query.setParameter("member", member);
		List<Slot> result=query.getResultList();
		return result;
	}

	public Slot lastOriginalSlot(Roster roster) {
		String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.blnDeleted=true" +
				" AND s.startTime=:startTime ORDER BY id "+ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("roster",roster.getId());
		query.setParameter("startTime", roster.getReporterChangedFrom());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public Slot firstAdjournedSlot(Roster roster, Adjournment adjournment) {
		String strQuery="SELECT s.* FROM slots s "
				+ " WHERE s.roster=:roster "
				+ " AND s.start_time>=:startTime" 
				+ " AND s.end_time<=:endTime "
				+ " AND s.bln_deleted=true ORDER BY s.end_time " +
				 ApplicationConstants.ASC + ",s.id DESC " ;
		Query query=this.em().createNativeQuery(strQuery, Slot.class);
		query.setParameter("roster",roster.getId());
		query.setParameter("startTime", adjournment.getStartTime());
		query.setParameter("endTime", adjournment.getEndTime());
		//query.setParameter("isDeleted", false);
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public Slot findPreviousSlot(Slot slot) {
		String strQuery = " SELECT s FROM Slot s WHERE s.roster.id=:rosterId" +
				" AND s.endTime<=:startTime"+ 
				" AND s.blnDeleted=false" +
				" ORDER BY s.endTime "+ ApplicationConstants.DESC;
		Query query = this.em().createQuery(strQuery);
		query.setParameter("rosterId", slot.getRoster().getId());
		query.setParameter("startTime", slot.getStartTime());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public List<Slot> findActiveSlots(Roster roster2) {
		List<Slot> slots = new ArrayList<Slot>();
		String strQuery = " SELECT s FROM Slot s"
				+ " WHERE s.roster.id=:rosterId" 
				+ " AND s.blnDeleted=false" 
				+ " ORDER BY s.startTime ";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("rosterId", roster2.getId());
		slots=query.getResultList();
		return slots;
		
	}
	
	public Slot findNextSlot(Slot slot) {
		String strQuery = " SELECT s FROM Slot s WHERE s.roster.id=:rosterId" +
				" AND s.startTime>=:endTime"+ 
				" AND s.blnDeleted=false" +
				" ORDER BY s.endTime "+ ApplicationConstants.ASC;
		Query query = this.em().createQuery(strQuery);
		query.setParameter("rosterId", slot.getRoster().getId());
		query.setParameter("endTime", slot.getEndTime());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public Slot slotPreviousToAdjournedSlot(Roster roster, Adjournment adjournment) {
		String strQuery = "SELECT s.* FROM slots s"
				+ " WHERE s.roster=:rosterId"
				+ " AND s.end_time<=:adjournmentStartTime"
				+ " AND bln_deleted=false"
				+ " ORDER BY s.end_time " + ApplicationConstants.DESC;
		
		Query query = this.em().createNativeQuery(strQuery, Slot.class);
		query.setParameter("rosterId", roster.getId());
		query.setParameter("adjournmentStartTime",adjournment.getEndTime());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots = query.getResultList();
		if(slots != null && !slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public Slot slotPreviousToReporterChangeTime(Roster roster) {
		String strQuery = "SELECT s.* FROM slots s"
				+ " WHERE s.roster=:rosterId"
				+ " AND s.end_time<=:reporterChangeTime"
				+ " AND bln_deleted=false"
				+ " ORDER BY s.end_time " + ApplicationConstants.DESC;
		
		Query query = this.em().createNativeQuery(strQuery, Slot.class);
		query.setParameter("rosterId", roster.getId());
		query.setParameter("reporterChangeTime",roster.getReporterChangedFrom());
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots = query.getResultList();
		if(slots != null && !slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}

	public Slot lastActiveSlotAfterAdjournemnt(Roster roster, Date endTime) {
		String strQuery="SELECT s.* FROM slots s "
				+ " WHERE s.roster=:roster "				 
				+ " AND s.end_time>=:endTime "
				+ " AND s.bln_deleted=false ORDER BY s.end_time " +
				 ApplicationConstants.DESC + ",s.id DESC " ;
		Query query=this.em().createNativeQuery(strQuery, Slot.class);
		query.setParameter("roster",roster.getId());
		query.setParameter("endTime",endTime);
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Slot> slots=query.getResultList();
		
		if(slots!=null&&!slots.isEmpty()){
			return slots.get(0);
		}
		return null;
	}
	
	public List<Slot> getFirstAndLastSlotForGivenRoster(Long rosterId){
		
		StringBuilder strQuery = new StringBuilder();
		List<Slot> slots = new ArrayList<>();
		
		strQuery.append(" (SELECT * FROM slots  WHERE roster = "+rosterId+"  LIMIT 1) ");
		strQuery.append(" UNION ");
		strQuery.append(" (SELECT * FROM slots  WHERE roster = "+rosterId+"  ORDER BY id DESC LIMIT 1 )");
		Query query=this.em().createNativeQuery(strQuery.toString(), Slot.class);
		slots=query.getResultList();
		
		return slots;  // returns Empty Array if their is no  element fetched From DB
	}

}
