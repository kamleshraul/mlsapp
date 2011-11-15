package org.mkcl.els.service.impl;

import java.util.List;

import org.mkcl.els.common.vo.MemberInRoleVO;
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
}
