package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.transaction.annotation.Transactional;

public class SessionTest extends AbstractTest{

	@Test
	@Transactional
	public void testPersist() {
		HouseType housetype=new HouseType("testhousetype","testhouse");
		housetype.persist();
		Date d=new Date(1988-20-12);
		Date startDate=new Date(1988-20-12);
		Date endDate=new Date(1988-18-11);
		SessionPlace place=new SessionPlace("testPlace");
		place.persist();
		SessionType type=new SessionType("testsession");
		type.persist();
		House house=new House("testhouse",2,housetype,d);
		house.persist();
		Session session=new Session(1, startDate, endDate, type, place,1988, house);
		session.persist();
		Assert.assertNotNull("saving session data ",session);

	}

	@Test
	@Transactional
	public void testMerge() {
		HouseType housetype=new HouseType("testhousetype","testhouse");
		housetype.persist();
		Date d=new Date(1988-20-12);
		Date startDate=new Date(1988-20-12);
		Date endDate=new Date(1988-18-11);
		SessionPlace place=new SessionPlace("testPlace");
		place.persist();
		SessionType type=new SessionType("testsession");
		type.persist();
		House house=new House("testhouse",2,housetype,d);
		house.persist();
		Session session=new Session(1, startDate, endDate, type, place,1988, house);
		session.persist();
		session.setNumber(3);
		session.merge();
		Assert.assertNotNull("updating session data ",session);

	}

	@Test
	@Transactional
	public void testRemove() {
		HouseType housetype=new HouseType("testhousetype","testhouse");
		housetype.persist();
		Date d=new Date(1988-20-12);
		Date startDate=new Date(1988-20-12);
		Date endDate=new Date(1988-18-11);
		SessionPlace place=new SessionPlace("testPlace");
		place.persist();
		SessionType type=new SessionType("testsession");
		type.persist();
		House house=new House("testhouse",2,housetype,d);
		house.persist();
		Session session=new Session(1, startDate, endDate, type, place,1988, house);
		session.persist();
		session.remove();
		Assert.assertNotNull("removing session data ",session);

	}

	@Test
	@Transactional
	public void testFindById() {
		HouseType housetype=new HouseType("testhousetype","testhouse");
		housetype.persist();
		Date d=new Date(1988-20-12);
		Date startDate=new Date(1988-20-12);
		Date endDate=new Date(1988-18-11);
		SessionPlace place=new SessionPlace("testPlace");
		place.persist();
		SessionType type=new SessionType("testsession");
		type.persist();
		House house=new House("testhouse",2,housetype,d);
		house.persist();
		Session session=new Session(1, startDate, endDate, type, place,1988, house);
		session.persist();
		Session s1=Session.findById(Session.class, session.getId());
		Assert.assertNotNull("removing session data ",s1);

	}

	
	@Test
	@Transactional
	public void testFindAll() {
		HouseType housetype=new HouseType("testhousetype","testhouses");
		housetype.persist();
		Date d=new Date(1988-20-12);
		Date startDate=new Date(1988-20-12);
		Date endDate=new Date(1988-18-11);
		SessionPlace place=new SessionPlace("testPlace");
		place.persist();
		SessionType type=new SessionType("testsession");
		type.persist();
		House house=new House("testhouse",2,housetype,d);
		house.persist();
		Session session=new Session(1, startDate, endDate, type, place,1988, house);
		session.persist();
		List<Session> sessions=Session.findAll(Session.class, "number", "desc", session.getLocale());
		Assert.assertNotNull("Finding session data ",sessions);

	}

}
