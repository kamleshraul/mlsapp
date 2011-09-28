package org.mkcl.els.service;

import org.mkcl.els.domain.MemberDetails;

public interface IMemberDetailsService extends IGenericService<MemberDetails ,Long>{

	public int updateMemberPersonalDetails(MemberDetails memberPersonalDetails);
		
	public int updateMemberContactDetails(MemberDetails memberContactDetails);
	
	public int updateMemberOtherDetails(MemberDetails memberOtherDetails);


}
