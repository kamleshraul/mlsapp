package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.GuestHouse;
import org.mkcl.els.domain.GuestHouseReservation;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class GuestHouseReservationRepository extends BaseRepository<GuestHouseReservation, Long>{
	
	public List<GuestHouseReservation> findBookedRoomsByGuestHouse(final GuestHouse guestHouse,
			final Date fromDate,
			final Date toDate,
			final String locale) throws ELSException{
		try{
			String query="SELECT m FROM GuestHouseReservation m WHERE m.guestHouse.id=:guestHouseId"+
					" AND ((:fromDate between m.fromDate and m.toDate)"+
		    		" OR (:toDate between m.fromDate and m.toDate)"+
		    		" OR (m.fromDate between :fromDate and :toDate)"+
		    		" OR (m.toDate between :fromDate and :toDate))"+
		    		" AND m.locale=:locale"+
					" ORDER BY m.id ASC";
			TypedQuery<GuestHouseReservation> m=this.em().createQuery(query, GuestHouseReservation.class);
			m.setParameter("fromDate", fromDate);
			m.setParameter("toDate", toDate);
			m.setParameter("guestHouseId", guestHouse.getId());
			m.setParameter("locale", locale);
			return m.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("GuestHouseReservationRepository_List<GuestHouseReservation>_findAllByGuestHouse", "Cannot get the GuestHouseReservation ");
			throw elsException;
		}
	}
	
	public List<GuestHouseReservation> findBookedRoomsByGuestHouseMember(final GuestHouse guestHouse,final Member member,
			final Date fromDate,
			final Date toDate,
			final String locale) throws ELSException{
		try{
			String query="SELECT m FROM GuestHouseReservation m WHERE m.guestHouse.id=:guestHouseId"+
					" AND m.member.id=:memberId"+
					" AND ((:fromDate between m.fromDate and m.toDate)"+
		    		" OR (:toDate between m.fromDate and m.toDate)"+
		    		" OR (m.fromDate between :fromDate and :toDate)"+
		    		" OR (m.toDate between :fromDate and :toDate))"+
		    		" AND m.locale=:locale"+
					" ORDER BY m.id ASC";
			TypedQuery<GuestHouseReservation> m=this.em().createQuery(query, GuestHouseReservation.class);
			m.setParameter("fromDate", guestHouse.getFromDate());
			m.setParameter("toDate", guestHouse.getToDate());
			m.setParameter("memberId", member.getId());
			m.setParameter("guestHouseId", guestHouse.getId());
			m.setParameter("locale", locale);
			return m.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("GuestHouseReservationRepository_List<GuestHouseReservation>_findAllByMember", "Cannot get the GuestHouseReservation ");
			throw elsException;
		}
	}

}
