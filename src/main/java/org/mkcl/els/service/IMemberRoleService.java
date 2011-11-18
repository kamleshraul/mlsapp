package org.mkcl.els.service;

import java.util.List;
import java.util.Locale;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.MemberRole;

public interface IMemberRoleService extends IGenericService<MemberRole ,Long>{

	void createMemberRole(long role, Long memberId, MemberRole memberRole);

	List<MemberRole> findByMemberId(Long memberId);

	List<MemberInRoleVO> findUnassignedMembers(Long roleId);

	List<MemberInRoleVO> findAssignedMembers(Long roleId);

	List<MemberRole> findByRoleId(Long roleId);

	GridData getAssignedMembers(Long roleId, Integer rows, Integer page, String sidx,
			String order, String sQl, Locale locale);

	GridData getAssignedMembers(Long roleId, Integer rows, Integer page, String sidx,
			String order, Locale locale);

	GridData getUnAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale);

	GridData getUnAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, Locale locale);

}
