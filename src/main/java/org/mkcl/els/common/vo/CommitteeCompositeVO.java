package org.mkcl.els.common.vo;

import java.util.List;

public class CommitteeCompositeVO {

	//=============== ATTRIBUTES ====================
	private List<PartyVO> rulingParties;
	
	private List<PartyVO> oppositionParties;
	
	private List<CommitteeVO> committeeVOs;
	
	//=============== CONSTRUCTORS ==================
	public CommitteeCompositeVO() {
		super();
	}

	//=============== GETTERS SETTERS ===============
	public List<PartyVO> getRulingParties() {
		return rulingParties;
	}

	public void setRulingParties(final List<PartyVO> rulingParties) {
		this.rulingParties = rulingParties;
	}

	public List<PartyVO> getOppositionParties() {
		return oppositionParties;
	}

	public void setOppositionParties(final List<PartyVO> oppositionParties) {
		this.oppositionParties = oppositionParties;
	}

	public List<CommitteeVO> getCommitteeVOs() {
		return committeeVOs;
	}

	public void setCommitteeVOs(final List<CommitteeVO> committeeVOs) {
		this.committeeVOs = committeeVOs;
	}
}