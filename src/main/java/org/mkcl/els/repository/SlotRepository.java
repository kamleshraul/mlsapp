package org.mkcl.els.repository;

import java.io.Serializable;
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
		String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
				" AND s.endTime<=:endTime AND s.blnDeleted=true ORDER BY s.endTime " +
				 ApplicationConstants.DESC + " ,s.id DESC"  ;
		Query query=this.em().createQuery(strQuery);
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
			String strQuery="SELECT s FROM Slot s JOIN s.roster.id=:roster AND s.endTime=:endTime AND s.blnDeleted=false";
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
			String strQuery="SELECT s FROM Slot s JOIN s.roster.id=:roster AND s.startTime=:startTime AND s.blnDeleted=false";
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
				" WHERE s.startTime<:endTime AND s.endTime>:startTime" +
				" AND u.language=:language AND s.blnDeleted=false" ;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("endTime", slot.getEndTime());
		query.setParameter("startTime", slot.getStartTime());
		query.setParameter("language", language.getName());
		List<Slot> result=query.getResultList();
		return result;
	}

	public List<User> findDifferentLanguageUsersBySlot(Slot s) {
		String strQuery="SELECT u FROM Slot s JOIN s.reporter r JOIN r.user u" +
				" WHERE s.startTime<:endTime AND s.endTime>:startTime AND s.blnDeleted=false";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("endTime", s.getEndTime());
		query.setParameter("startTime", s.getStartTime());
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
		String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster AND s.startTime>=:startTime" +
				" AND s.endTime<=:endTime AND s.blnDeleted=TRUE ORDER BY s.endTime " +
				 ApplicationConstants.ASC + ",s.id DESC " ;
		Query query=this.em().createQuery(strQuery);
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

}
