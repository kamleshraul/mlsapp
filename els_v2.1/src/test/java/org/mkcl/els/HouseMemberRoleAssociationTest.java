package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.transaction.annotation.Transactional;

public class HouseMemberRoleAssociationTest extends AbstractTest{

	@Test
	@Transactional
	public void testFindByMemberIdAndId() {
		Date fromDate=new Date(12-10-2012);
		Date toDate=new Date(12-11-2012);
		HouseType houseType= new HouseType("type", "name");
		houseType.persist();
		Member member=new Member();
		member.persist();
		MemberRole role=new MemberRole("testname", 1, houseType);
		role.persist();
		House house=new House("test",2,houseType,new Date(12-10-2012));
		house.persist();
		HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
		hmra.setRecordIndex(1);
		hmra.persist();
		HouseMemberRoleAssociation hmra1=HouseMemberRoleAssociation.findByMemberIdAndId(member.getId(),hmra.getRecordIndex());
		Assert.assertNotNull("findina housememberrole associtation Data ", hmra1);
	}

	@Test
	@Transactional
	public void testPersist() {
		Date fromDate=new Date(12-10-2012);
		Date toDate=new Date(12-11-2012);
		HouseType houseType= new HouseType("type", "name");
		houseType.persist();
		Member member=new Member();
		member.persist();
		MemberRole role=new MemberRole("testname", 1, houseType);
		role.persist();
		House house=new House("test",2,houseType,new Date(12-10-2012));
		house.persist();
		HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
		hmra.setRecordIndex(1);
		hmra.persist();
		Assert.assertNotNull("saving housememberrole associtation Data ", hmra);
	}

	@Test
	@Transactional
	public void testMerge() {
		Date fromDate=new Date(12-10-2012);
		Date toDate=new Date(12-11-2012);
		HouseType houseType= new HouseType("type", "name");
		houseType.persist();
		Member member=new Member();
		member.persist();
		MemberRole role=new MemberRole("testname", 1, houseType);
		role.persist();
		House house=new House("test",2,houseType,new Date(12-10-2012));
		house.persist();
		HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
		hmra.setRecordIndex(1);
		hmra.persist();
		hmra.setRemarks("test");
		hmra.merge();
		Assert.assertNotNull("Updating housememberrole associtation Data ", hmra);
	}

	@Test
	@Transactional
	public void testRemove() {
		Date fromDate=new Date(12-10-2012);
		Date toDate=new Date(12-11-2012);
		HouseType houseType= new HouseType("type", "name");
		houseType.persist();
		Member member=new Member();
		member.persist();
		MemberRole role=new MemberRole("testname", 1, houseType);
		role.persist();
		House house=new House("test",2,houseType,new Date(12-10-2012));
		house.persist();
		HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
		hmra.setRecordIndex(1);
		hmra.persist();
		hmra.remove();
		Assert.assertNotNull("Removing housememberrole associtation Data ", hmra);
	}

	@Test
	@Transactional
	public void testFindHighestRecordIndex() {
		try {
			Date fromDate=new Date(12-10-2012);
			Date toDate=new Date(12-11-2012);
			HouseType houseType= new HouseType("type", "name");
			houseType.persist();
			Member member=new Member();
			member.persist();
			MemberRole role=new MemberRole("testname", 1, houseType);
			role.persist();
			House house=new House("test",2,houseType,new Date(12-10-2012));
			house.persist();
			HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
			hmra.setRecordIndex(1);
			hmra.persist();
			Integer recordIndex=HouseMemberRoleAssociation.findHighestRecordIndex(member.getId());
			Assert.assertNotNull("finding record index Data ", recordIndex);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFindByMemberIdRolePriorityHouseId() {
		try {
			Date fromDate=new Date(12-10-2012);
			Date toDate=new Date(12-11-2012);
			HouseType houseType= new HouseType("type", "name");
			houseType.setLocale("mr_IN");
			houseType.persist();
			Member member=new Member();
			member.setLocale("mr_IN");
			member.persist();
			MemberRole role=new MemberRole("testname", 1, houseType);
			role.setLocale("mr_IN");
			role.persist();
			House house=new House("test",2,houseType,new Date(12-10-2012));
			house.setLocale("mr_IN");
			house.persist();
			HouseMemberRoleAssociation hmra=new HouseMemberRoleAssociation(fromDate, toDate, member, role, house, house.getLocale());
			hmra.setRecordIndex(1);
			hmra.persist();
			List<HouseMemberRoleAssociation> hmras= HouseMemberRoleAssociation.findByMemberIdRolePriorityHouseId(member.getId(), role.getPriority(), house.getId(), hmra.getLocale());
			Assert.assertEquals(true,hmras.size()>0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		
}
