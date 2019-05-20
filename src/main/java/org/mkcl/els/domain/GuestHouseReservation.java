package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.GuestHouseReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
@Configurable
@Entity
@Table(name = "guesthousereservation")
public class GuestHouseReservation extends BaseDomain implements Serializable {
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
	/** The date in. */
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	
	/** The date out. */
	@Temporal(TemporalType.DATE)
	private Date toDate;
	
    /** The number Of Room. */
	@Column
	private Integer roomNumber = 0;
	
    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    
    /** The creation date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /** The created by. */
    @Column(length=1000)
    private String createdBy;
    
    /** The received mode (ONLINE/OFFLINE). */
    @Column(length=50)
    private String mode;
    
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;
    
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="guestHouse_id")
    private GuestHouse guestHouse;


	public Date getFromDate() {
		return fromDate;
	}


	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}


	public Date getToDate() {
		return toDate;
	}


	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}


	public Integer getRoomNumber() {
		return roomNumber;
	}


	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}


	public Member getMember() {
		return member;
	}


	public void setMember(Member member) {
		this.member = member;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public GuestHouse getGuestHouse() {
		return guestHouse;
	}


	public void setGuestHouse(GuestHouse guestHouse) {
		this.guestHouse = guestHouse;
	}
	/** The GuestHouseReservation repository. */
	@Autowired
	private transient GuestHouseReservationRepository guestHouseReservationRepository;
	
	/**
	 * Gets the GuestHouseReservation repository.
	 *
	 * @return the GuestHouseReservation repository
	 */
	public static GuestHouseReservationRepository getGuestHouseReservationRepository() {
		GuestHouseReservationRepository guestHouseReservationRepository = new GuestHouseReservation().guestHouseReservationRepository;
		if (guestHouseReservationRepository == null) {
			throw new IllegalStateException(
					"MemberRepository has not been injected in Member Domain");
		}
		return guestHouseReservationRepository;
	}

	public List<GuestHouseReservation> findBookedRoomsByGuestHouse(final GuestHouse guestHouse,
			final Date fromDate,
			final Date toDate,
			final String locale){
		try {
			return getGuestHouseReservationRepository().findBookedRoomsByGuestHouse(guestHouse, fromDate, toDate, locale);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


}
