package org.mkcl.els;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Designation;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberDepartment;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.transaction.annotation.Transactional;

public class MemberMinisterTest extends AbstractTest{

	@Test
	@Transactional
	public void testFindAssignedDepartments() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.setLocale("mr_IN");
		mm.persist();
		
		CustomParameter customParameter=new CustomParameter("DB_DATEFORMAT", "yyyy-MM-dd", true, "");
		customParameter.persist();
		List<Department> departments=MemberMinister.findAssignedDepartments(ministry, ministry.getLocale());
		Assert.assertEquals(true,departments.size()>0);
	}

	@Test
	@Transactional
	public void testFindAssignedSubDepartments() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		SubDepartment subDepartment=new SubDepartment("test",department,"",false);
		subDepartment.setLocale("mr_IN");
		subDepartment.persist();
		
		List<SubDepartment> subDepartments=SubDepartment.findAll(SubDepartment.class, "name", "desc", subDepartment.getLocale());
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.setSubDepartments(subDepartments);
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.setLocale("mr_IN");
		mm.persist();
		
		CustomParameter customParameter=new CustomParameter("DB_DATEFORMAT", "yyyy-MM-dd", true, "");
		customParameter.persist();
		List<SubDepartment> subDepartments1=MemberMinister.findAssignedSubDepartments(ministry, department, mm.getLocale());
		Assert.assertEquals(true,subDepartments1.size()>0);
	}

	@Test
	@Transactional
	public void testPersist() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		Assert.assertNotNull("Saved memberDepartment Data ", mm);
	}

	@Test
	@Transactional
	public void testMerge() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		mm.setLocale("en_US");
		Assert.assertNotNull("Updated memberDepartment Data ", mm);
	}

	@Test
	@Transactional
	public void testRemove() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		mm.remove();
		Assert.assertNotNull("Removed memberDepartment Data ", mm);
	}

	@Test
	@Transactional
	public void testFindById() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		
		MemberMinister memberMinister= MemberMinister.findById(MemberMinister.class, mm.getId());
		Assert.assertNotNull("Finding memberDepartment Data ", memberMinister);
	}

	@Test
	@Transactional
	public void testFindByFieldName() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		
		MemberMinister memberMinister= MemberMinister.findByFieldName(MemberMinister.class, "house", mm.getHouse(), mm.getLocale());
		Assert.assertNotNull("Finding memberDepartment Data ", memberMinister);
	}

	@Test
	@Transactional
	public void testFindAll() throws ParseException {
		HouseType type=new HouseType("type", "name");
		type.setLocale("mr_IN");
		type.persist();
		
		Designation designation=new Designation("testname");
		designation.setLocale("mr_IN");
		designation.persist();
		
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		House house=new House("testname", 1, type, df.parse("12/01/2012"));
		house.setLocale("mr_IN");
		house.persist();
		
		Member member=new Member();
		member.setLocale("mr_IN");
		member.persist();
		
		Department department=new Department("test", false, "");
		department.setLocale("mr_IN");
		department.persist();
		
		MemberDepartment memberDepartment= new MemberDepartment();
		memberDepartment.setDepartment(department);
		memberDepartment.setLocale("mr_IN");
		memberDepartment.persist();
		
		List<MemberDepartment> memberDepartments= MemberDepartment.findAll(MemberDepartment.class, "department", "desc", memberDepartment.getLocale());
		
		Ministry ministry=new Ministry("test", false, "");
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		MemberMinister mm= new MemberMinister();
		mm.setDesignation(designation);
		mm.setHouse(house);
		mm.setMember(member);
		mm.setMemberDepartments(memberDepartments);
		mm.setMinistry(ministry);
		mm.persist();
		
		List<MemberMinister> memberMinisters= MemberMinister.findAll(MemberMinister.class, "house", "desc", mm.getLocale());
		Assert.assertEquals(true,memberMinisters.size()>0);
	}

}
