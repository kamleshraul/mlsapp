package org.mkcl.els.service;

import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.MemberDetails;

public interface IMemberDetailsService extends IGenericService<MemberDetails ,Long>{

	public int updateMemberPersonalDetails(MemberDetails memberPersonalDetails);
		
	public int updateMemberContactDetails(MemberDetails memberContactDetails);
	
	public int updateMemberOtherDetails(MemberDetails memberOtherDetails);
	
	public MemberSearchPage searchMemberDetails(String criteria1,String locale);

	public MemberSearchPage searchMemberDetails(String criteria1, String criteria2,String locale);

	public MemberSearchPage searchMemberDetails(String criteria1,String criteria2,String criteria3,String locale);
	
	public MemberSearchPage searchMemberDetails(String criteria1,int page,int rows,String locale);

	public MemberSearchPage searchMemberDetails(String criteria1,String criteria2,int page,int rows,String locale);

	public MemberSearchPage searchMemberDetails(String criteria1,String criteria2,String criteria3,int page,int rows,String locale);

	public Integer maxNoOfTerms(String locale);

	public MemberBiographyVO findBiography(long id, String locale);

	public Document getPhoto(String tag);

}
