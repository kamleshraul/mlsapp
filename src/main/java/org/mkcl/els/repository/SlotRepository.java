package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class SlotRepository extends BaseRepository<Slot, Serializable>{

	@SuppressWarnings("unchecked")
	public Slot lastGeneratedSlot(final Roster roster) {
		String strQuery="SELECT s FROM Slot s WHERE s.roster.id=:roster" +
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
			String strQuery="SELECT s FROM Slot s JOIN s.roster.id=:roster AND s.endTime=:endTime";
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
			String strQuery="SELECT s FROM Slot s JOIN s.roster.id=:roster AND s.startTime=:startTime";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("roster",roster.getId());
			query.setParameter("startTime",startTime);
			return (Slot) query.getSingleResult();
		} catch (Exception e) {
			logger.error("SLOT NOT FOUND",e);
			return null;
		}
	}

}
