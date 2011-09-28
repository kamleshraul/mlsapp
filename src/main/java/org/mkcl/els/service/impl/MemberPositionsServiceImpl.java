package org.mkcl.els.service.impl;

import org.mkcl.els.domain.MemberPositionsDetails;
import org.mkcl.els.repository.MemberDetailsRepository;
import org.mkcl.els.repository.MemberPositionRepository;
import org.mkcl.els.service.IMemberPositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberPositionsServiceImpl extends GenericServiceImpl<MemberPositionsDetails,Long>
implements IMemberPositionsService{

	private MemberPositionRepository memberPositionRepository;	
	@Autowired
	public void setMemberPositionRepository(
			MemberPositionRepository memberPositionRepository) {
		this.dao = memberPositionRepository;
		this.memberPositionRepository = memberPositionRepository;
	}
	
}
