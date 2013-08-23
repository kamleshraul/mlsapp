package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.transaction.annotation.Transactional;

public class MemberPartyAssociationTest extends AbstractTest{

	@Test
	@Transactional
	public void testFindByMemberIdAndId() {
		HouseType type=new HouseType("type", "name");
		type.persist();
		House house=new House("test", 2, type, new Date(12-10-2012));
		house.persist();
		Member member=new Member();
		member.persist();
		Party party=new Party("name", "test", new Date(12-12-2000));
		party.persist();
		MemberPartyAssociation mpa=new MemberPartyAssociation();
		mpa.setHouse(house);
		mpa.setFromDate(new Date(12-12-2011));
		mpa.setMember(member);
		mpa.setParty(party);
		mpa.setRecordIndex(1);
		mpa.persist();
		MemberPartyAssociation mpa1=MemberPartyAssociation.findByMemberIdAndId(member.getId(), mpa.getRecordIndex());
		Assert.assertNotNull("Finding MemberPartyAssociation Data",mpa1);
	}

	@Test
	@Transactional
	public void testPersist() {
		HouseType type=new HouseType("type", "name");
		type.persist();
		House house=new House("test", 2, type, new Date(12-10-2012));
		house.persist();
		Member member=new Member();
		member.persist();
		Party party=new Party("name", "test", new Date(12-12-2000));
		party.persist();
		MemberPartyAssociation mpa=new MemberPartyAssociation();
		mpa.setHouse(house);
		mpa.setFromDate(new Date(12-12-2011));
		mpa.setMember(member);
		mpa.setParty(party);
		mpa.setRecordIndex(1);
		mpa.persist();
		Assert.assertNotNull("saving MemberPartyAssociation Data",mpa);
	}

	@Test
	@Transactional
	public void testMerge() {
		HouseType type=new HouseType("type", "name");
		type.persist();
		House house=new House("test", 2, type, new Date(12-10-2012));
		house.persist();
		Member member=new Member();
		member.persist();
		Party party=new Party("name", "test", new Date(12-12-2000));
		party.persist();
		MemberPartyAssociation mpa=new MemberPartyAssociation();
		mpa.setHouse(house);
		mpa.setFromDate(new Date(12-12-2011));
		mpa.setMember(member);
		mpa.setParty(party);
		mpa.setRecordIndex(1);
		mpa.persist();
		mpa.setLocale("mr_IN");
		mpa.merge();
		Assert.assertNotNull("Updating MemberPartyAssociation Data",mpa);
	}

	@Test
	@Transactional
	public void testRemove() {
		HouseType type=new HouseType("type", "name");
		type.persist();
		House house=new House("test", 2, type, new Date(12-10-2012));
		house.persist();
		Member member=new Member();
		member.persist();
		Party party=new Party("name", "test", new Date(12-12-2000));
		party.persist();
		MemberPartyAssociation mpa=new MemberPartyAssociation();
		mpa.setHouse(house);
		mpa.setFromDate(new Date(12-12-2011));
		mpa.setMember(member);
		mpa.setParty(party);
		mpa.setRecordIndex(1);
		mpa.persist();
		mpa.remove();
		Assert.assertNotNull("Removing MemberPartyAssociation Data",mpa);
	}

	@Test
	@Transactional
	public void testFindHighestRecordIndex() {
		HouseType type=new HouseType("type", "name");
		type.persist();
		House house=new House("test", 2, type, new Date(12-10-2012));
		house.persist();
		Member member=new Member();
		member.persist();
		Party party=new Party("name", "test", new Date(12-12-2000));
		party.persist();
		MemberPartyAssociation mpa=new MemberPartyAssociation();
		mpa.setHouse(house);
		mpa.setFromDate(new Date(12-12-2011));
		mpa.setMember(member);
		mpa.setParty(party);
		mpa.setRecordIndex(1);
		mpa.persist();
		Integer recordIndex=MemberPartyAssociation.findHighestRecordIndex(member.getId());
		Assert.assertNotNull("Finding MemberPartyAssociation Data",recordIndex);
	}

}
