package org.mkcl.els.domain.ballot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.repository.PreBallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name="preballots")
public class PreBallot extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 2436486573245331974L;

	//===============================================
	//
	//=============== ATTRIBUTES ====================
	//
	//===============================================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="preballots_ballot_entries",
			joinColumns={ @JoinColumn(name="preballot_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") })
	private List<BallotEntry> ballotEntries;

	@Temporal(TemporalType.TIMESTAMP)
	private Date preBallotDate;
	
	@Autowired
	private transient PreBallotRepository preBallotRepository;
	
	
	//===============================================
	//
	//=============== CONSTRUCTORS ==================
	//
	//===============================================
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public PreBallot() {
		super();
	}

	/**
	 * Can be used with devices having no group.
	 */
	public PreBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Date preBallotDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setPreBallotDate(preBallotDate);
	}
	
	/**
	 * To be used for devices having group.
	 */
	public PreBallot(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date preBallotDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setPreBallotDate(preBallotDate);
	}
	
	
	//===============================================
	//
	//=============== VIEW METHODS ==================
	//
	//===============================================
	public static List<BallotVO> getBallotVOFromBallotEntries(final List<BallotEntry> ballotEntries, 
			final String locale) {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		for(BallotEntry bE : ballotEntries) {
			
			for(DeviceSequence ds : bE.getDeviceSequences()) {
				BallotVO preBallotVO = new BallotVO();
				
				if(ds.getDevice() instanceof Question) {	
					Question q = (Question) ds.getDevice();
					
					preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
					preBallotVO.setQuestionNumber(q.getNumber());
					preBallotVO.setQuestionSubject(q.getSubject());
				}
				else if(ds.getDevice() instanceof Resolution) {
					Resolution r = (Resolution) ds.getDevice();
					CustomParameter customParameter = CustomParameter.
							findByName(CustomParameter.class, "RESOLUTION_PREBALLOT_MEMBERNAMEFORMAT", "");
					if(customParameter != null){
						preBallotVO.setMemberName(r.getMember().findNameInGivenFormat(customParameter.getValue()));
					}else{
						preBallotVO.setMemberName(r.getMember().findNameInGivenFormat("firstnamelastname"));
					}
					
					preBallotVO.setQuestionNumber(r.getNumber());
					preBallotVO.setQuestionSubject(r.getSubject());
				}
				else if(ds.getDevice() instanceof StandaloneMotion) {
					StandaloneMotion q = (StandaloneMotion) ds.getDevice();
					
					preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
					preBallotVO.setQuestionNumber(q.getNumber());
					preBallotVO.setQuestionSubject(q.getSubject());
				}
				
				preBallotVOs.add(preBallotVO);				
			}
		}
		
		return preBallotVOs;
	}	

	
	//===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
	@Transactional 
	public boolean optimizedRemove() {
		return PreBallot.getRepository().optimizedRemove(this);
	}
	
	@Transactional 
	public boolean optimizedRemoveHDS() {
		return PreBallot.getRepository().optimizedRemoveHDS(this);
	}
	
	public static PreBallot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException{
		return PreBallot.getRepository().find(session, deviceType, answeringDate, locale);
	}
	
	public static PreBallot find(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException{
		return PreBallot.getRepository().find(session, deviceType, group, answeringDate, locale);
	}
	
	public static PreBallot find(final Device device) throws ELSException {
		return PreBallot.getRepository().find(device);
	}

	
	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static PreBallotRepository getRepository() {
		PreBallotRepository repository = new PreBallot().preBallotRepository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"PreBallotRepository has not been injected in Ballot Domain");
		}
		
		return repository;
	}
	
	
	//===============================================
	//
	//=============== GETTERS/SETTERS ===============
	//
	//===============================================
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<BallotEntry> getBallotEntries() {
		return ballotEntries;
	}

	public void setBallotEntries(List<BallotEntry> ballotEntries) {
		this.ballotEntries = ballotEntries;
	}

	public Date getPreBallotDate() {
		return preBallotDate;
	}

	public void setPreBallotDate(Date preBallotDate) {
		this.preBallotDate = preBallotDate;
	}
}
