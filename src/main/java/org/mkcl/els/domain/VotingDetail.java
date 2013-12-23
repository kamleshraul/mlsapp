package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mkcl.els.repository.VotingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "voting_details")
public class VotingDetail extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String houseType;
	
	private Integer houseRound;
	
	/** The device. */
    private String deviceId;
    
    private String deviceType;
    
    private String votingFor;
	
	private Integer totalNumberOfVoters;
	
	private Integer actualNumberOfVoters;
	
	private Integer votesInFavor;
	
	private Integer votesAgainst;
	
	@Transient
	private Integer votesNeutral;
	
	private String decision;
	
	private Boolean isInDecorum;
	
	/** The voting detail repository. */
    @Autowired
    private transient VotingDetailRepository votingDetailRepository;
	
	// ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new voting detail.
     */
	public VotingDetail() {
	   super();
	}
	
	 //-----------------------------Domain Methods--------------------------------
	/**
     * Gets the voting detail repository.
     *
     * @return the voting detail repository
     */
    private static VotingDetailRepository getVotingDetailRepository() {
    	VotingDetailRepository votingDetailRepository = new VotingDetail().votingDetailRepository;
        if (votingDetailRepository == null) {
            throw new IllegalStateException(
            	"VotingDetailRepository has not been injected in VotingDetail Domain");
        }
        return votingDetailRepository;
    }
	
    public static List<VotingDetail> findByVotingForDeviceInGivenHouse(final Device device, 
			final DeviceType deviceType, final HouseType houseType, final String votingFor) {
    	return getVotingDetailRepository().findByVotingForDeviceInGivenHouse(device, deviceType, houseType, votingFor);
    }
	
	//-----------------------------Getters And Setters--------------------------------
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Integer getHouseRound() {
		return houseRound;
	}

	public void setHouseRound(Integer houseRound) {
		this.houseRound = houseRound;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getVotingFor() {
		return votingFor;
	}

	public void setVotingFor(String votingFor) {
		this.votingFor = votingFor;
	}

	public Integer getTotalNumberOfVoters() {
		return totalNumberOfVoters;
	}

	public void setTotalNumberOfVoters(Integer totalNumberOfVoters) {
		this.totalNumberOfVoters = totalNumberOfVoters;
	}

	public Integer getActualNumberOfVoters() {
		return actualNumberOfVoters;
	}

	public void setActualNumberOfVoters(Integer actualNumberOfVoters) {
		this.actualNumberOfVoters = actualNumberOfVoters;
	}

	public Integer getVotesInFavor() {
		return votesInFavor;
	}

	public void setVotesInFavor(Integer votesInFavor) {
		this.votesInFavor = votesInFavor;
	}

	public Integer getVotesAgainst() {
		return votesAgainst;
	}

	public void setVotesAgainst(Integer votesAgainst) {
		this.votesAgainst = votesAgainst;
	}
	
	public Integer getVotesNeutral() {
		votesNeutral = actualNumberOfVoters - (votesInFavor + votesAgainst);
		return votesNeutral;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public Boolean getIsInDecorum() {
		return isInDecorum;
	}

	public void setIsInDecorum(Boolean isInDecorum) {
		this.isInDecorum = isInDecorum;
	}	
	
	
    
   

}
