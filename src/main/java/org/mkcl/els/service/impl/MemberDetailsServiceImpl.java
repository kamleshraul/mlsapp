package org.mkcl.els.service.impl;

import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.repository.MemberDetailsRepository;
import org.mkcl.els.service.IAssemblyRoleService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IMemberRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class MemberDetailsServiceImpl extends GenericServiceImpl<MemberDetails,Long>
implements IMemberDetailsService{
	
	private MemberDetailsRepository memberDetailsRepository;
	
	@Autowired
	private IAssemblyRoleService assemblyRoleService;
	
	@Autowired
	private IMemberRoleService memberRoleService;
	
	@Autowired
	private ICustomParameterService customParameterService;
	
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

	@Override
	public MemberSearchPage searchMemberDetails(String criteria1,String locale) {		
		return memberDetailsRepository.searchMemberDetails(criteria1,locale);
	}


	@Override
	public MemberSearchPage searchMemberDetails(String criteria1
			,String criteria2,String locale) {
		return memberDetailsRepository.searchMemberDetails(criteria1,criteria2,locale);
	}


	@Override
	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, String criteria3,String locale) {		
		return memberDetailsRepository.searchMemberDetails(criteria1,criteria2,criteria3,locale);
	}

	@Override
	public MemberSearchPage searchMemberDetails(String criteria1, int page,
			int rows,String locale) {		
		return memberDetailsRepository.searchMemberDetails(criteria1,page,rows,locale);
	}


	@Override
	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, int page, int rows,String locale) {
		return memberDetailsRepository.searchMemberDetails(criteria1,criteria2,page,rows,locale);
	}


	@Override
	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, String criteria3, int page, int rows,String locale) {		
		return memberDetailsRepository.searchMemberDetails(criteria1,criteria2,criteria3,page,rows,locale);
	}


	@Override
	public Integer maxNoOfTerms(String locale) {
		return memberDetailsRepository.maxNoOfTerms(locale);
	}


	@Override
	public MemberBiographyVO findBiography(long id, String locale) {
		return memberDetailsRepository.findBiography(id,locale);
	}


	@Override
	public Document getPhoto(String tag) {
		return memberDetailsRepository.getPhoto(tag);
	}


	@Override
	@Transactional
	public void createMemberAndDefaultRole(MemberDetails memberPersonalDetails) {
		this.create(memberPersonalDetails);
		//create default role Member for each member
		MemberRole memberRole=new MemberRole();
		memberRole.setMember(memberPersonalDetails);
		memberRole.setRole(assemblyRoleService.findByName(customParameterService.findByName("DEFAULT_MEMBERROLE").getValue()));
		memberRole.setLocale(memberPersonalDetails.getLocale());
		memberRoleService.create(memberRole);

	}


	@Override
	public MemberDetails findByIdAndLocale(Long memberId, String locale) {
		// TODO Auto-generated method stub
		return memberDetailsRepository.findByIdAndLocale(memberId,locale) ;
	}




}
