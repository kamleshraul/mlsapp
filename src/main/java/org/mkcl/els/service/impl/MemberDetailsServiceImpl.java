package org.mkcl.els.service.impl;


import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.repository.MemberDetailsRepository;
import org.mkcl.els.service.IMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberDetailsServiceImpl extends GenericServiceImpl<MemberDetails,Long>
implements IMemberDetailsService{

	private MemberDetailsRepository memberDetailsRepository;
	
	@Autowired
	public void setMemberDetailsRepository(
			MemberDetailsRepository memberDetailsRepository) {
		this.dao = memberDetailsRepository;
		this.memberDetailsRepository = memberDetailsRepository;
	}
	
	
	@Override
	@Transactional
	public int updateMemberPersonalDetails(
			MemberDetails memberPersonalDetails) {
		return memberDetailsRepository.updateMemberPersonalDetails(memberPersonalDetails);
	}


	@Override
	@Transactional
	public int updateMemberContactDetails(MemberDetails memberContactDetails) {
		return memberDetailsRepository.updateMemberContactDetails(memberContactDetails);
	}


	@Override
	@Transactional
	public int updateMemberOtherDetails(MemberDetails memberOtherDetails) {
		return memberDetailsRepository.updateMemberOtherDetails(memberOtherDetails);
	}

}
