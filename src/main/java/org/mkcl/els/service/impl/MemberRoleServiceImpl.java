package org.mkcl.els.service.impl;

import java.util.List;
import java.util.Locale;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.repository.MemberRoleRepository;
import org.mkcl.els.service.IMemberRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trg.search.Search;

@Service
public class MemberRoleServiceImpl extends GenericServiceImpl<MemberRole,Long>
implements IMemberRoleService{
	private MemberRoleRepository memberRoleRepository;
	@Autowired
	public void setMemberRoleRepository(
			MemberRoleRepository memberRoleRepository) {
		this.dao = memberRoleRepository;
		this.memberRoleRepository = memberRoleRepository;
	}
	@Override
	public void createMemberRole(long role, Long memberId,
			MemberRole memberRole) {
		memberRoleRepository.createMemberRole(role,memberId,memberRole);
	}
	@Override
	public List<MemberRole> findByMemberId(Long memberId) {
		return memberRoleRepository.findByMemberId(memberId);
	}
	@Override
	public List<MemberInRoleVO> findUnassignedMembers(Long roleId) {
		return memberRoleRepository.findUnassignedMembers(roleId);
	}
	@Override
	public List<MemberInRoleVO> findAssignedMembers(Long roleId) {
		return memberRoleRepository.findAssignedMembers(roleId);
	}
	@Override
	public List<MemberRole> findByRoleId(Long roleId) {
		return memberRoleRepository.findByRoleId(roleId);
	}
	@Override
	public GridData getAssignedUnassignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		return memberRoleRepository.getAssignedUnassignedMembers(roleId, rows,page,sidx, order,sQl,locale);
	}
	@Override
	public GridData getAssignedUnassignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {
		return memberRoleRepository.getAssignedUnassignedMembers(roleId,rows,page,sidx,order,locale);
	}
	@Override
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, String sQl, Locale locale) {
		return memberRoleRepository.getUnAssignedMembers(roleId, rows,page,sidx, order,sQl,locale);
	}
	@Override
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, Locale locale) {
		return memberRoleRepository.getUnAssignedMembers(roleId,rows,page,sidx,order,locale);
	}
	@Override
	public GridData getAssignedUnassignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		return memberRoleRepository.getAssignedUnassignedRoles(memberId, rows,page,sidx, order,sQl,locale);
	}
	@Override
	public GridData getAssignedUnassignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {
		return memberRoleRepository.getAssignedUnassignedRoles(memberId,rows,page,sidx,order,locale);
	}
	@Override
	public MemberRole checkForDuplicateMemberRole(MemberRole memberRole) {
		return (MemberRole)memberRoleRepository.checkForDuplicateMemberRole(memberRole);
	}
	@Override
	public boolean isMember(MemberDetails memberDetails, Assembly assembly,String fromdate,String todate) {
		return memberRoleRepository.isMember(memberDetails, assembly,fromdate,todate);
	}
	@Override
	public List<AssemblyRole> getUnassignedRoles(MemberDetails memberDetails,
			Assembly assembly, String locale) {
		//for current assembly
		return memberRoleRepository.getUnassignedRoles(memberDetails,assembly, locale);
	}

}
