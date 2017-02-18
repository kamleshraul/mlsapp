package org.mkcl.els.common.vo;

import java.util.List;

public class FinalBallotVO {

	private Long memberId;
	
	private List<BallotEntryVO> ballotEntryVOs;

	public FinalBallotVO() {
		super();
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public List<BallotEntryVO> getBallotEntryVOs() {
		return ballotEntryVOs;
	}

	public void setFinalBallotEntryVOs(List<BallotEntryVO> ballotEntryVOs) {
		this.ballotEntryVOs = ballotEntryVOs;
	}
	
}
