/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.SessionTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class SessionTest extends AbstractTest{

	/**
	 * Test persist.
	 */
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
		Session session=new Session();
		session.setNumber(4);
		session.setHouse(house);
		session.setStartDate(startDate);
		session.setEndDate(endDate);
		session.setPlace(place);
		session.setType(type);
		session.persist();
		Assert.assertNotNull("saving session data ",session);

	}

	/**
	 * Test merge.
	 */
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
		Session session=new Session();
		session.setHouse(house);
		session.setStartDate(startDate);
		session.setEndDate(endDate);
		session.setPlace(place);
		session.setType(type);
		session.persist();
		session.setNumber(3);
		session.merge();
		Assert.assertNotNull("updating session data ",session);

	}

	/**
	 * Test remove.
	 */
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
		Session session=new Session();
		session.setHouse(house);
		session.setStartDate(startDate);
		session.setEndDate(endDate);
		session.setPlace(place);
		session.setType(type);
		session.persist();
		session.remove();
		Assert.assertNotNull("removing session data ",session);

	}

	/**
	 * Test find by id.
	 */
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
		Session session=new Session();
		session.setHouse(house);
		session.setStartDate(startDate);
		session.setEndDate(endDate);
		session.setPlace(place);
		session.setType(type);
		session.persist();
		Session s1=Session.findById(Session.class, session.getId());
		Assert.assertNotNull("removing session data ",s1);

	}

	
	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
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
		Session session=new Session();
		session.setHouse(house);
		session.setStartDate(startDate);
		session.setEndDate(endDate);
		session.setPlace(place);
		session.setType(type);
		session.persist();
		List<Session> sessions=Session.findAll(Session.class, "number", "desc", session.getLocale());
		Assert.assertNotNull("Finding session data ",sessions);

	}
	
	/**
	 * Test find latest session.
	 */
	@Transactional
	@Test
	public void testFindLatestSession(){
		try {
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
			Session session=new Session();
			session.setHouse(house);
			session.setStartDate(startDate);
			session.setEndDate(endDate);
			session.setPlace(place);
			session.setType(type);
			session.setYear(1988);
			session.persist();
			Session session1=Session.findLatestSession(housetype, session.getYear());
			Assert.assertNotNull("finding session data ",session1);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test find session by house and year.
	 */
	public void testFindSessionByHouseAndYear(){
		try {
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
			Session session=new Session();
			session.setHouse(house);
			session.setStartDate(startDate);
			session.setEndDate(endDate);
			session.setPlace(place);
			session.setType(type);
			session.setYear(1988);
			session.persist();
			List<Session> sessions= Session.findSessionsByHouseTypeAndYear(house.getType(), session.getYear());
			Assert.assertEquals(true,	sessions.size()>0);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test find session by house session typeyear.
	 */
	@Transactional
	@Test
	public void testFindSessionByHouseSessionTypeyear(){
		try {
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
			Session session=new Session();
			session.setHouse(house);
			session.setStartDate(startDate);
			session.setEndDate(endDate);
			session.setPlace(place);
			session.setType(type);
			session.setYear(1988);
			session.persist();
			Session session1=Session.findSessionByHouseSessionTypeYear(house, type, session.getYear());
			Assert.assertNotNull("finding session data ",session1);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test find session by house type session type year.
	 */
	@Transactional
	@Test
	public void testFindSessionByHouseTypeSessionTypeYear(){
		try {
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
			Session session=new Session();
			session.setHouse(house);
			session.setStartDate(startDate);
			session.setEndDate(endDate);
			session.setPlace(place);
			session.setType(type);
			session.setYear(1988);
			session.persist();
			Session session1=Session.findSessionByHouseTypeSessionTypeYear(housetype, type, session.getYear());
			Assert.assertNotNull("finding session data ",session1);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Transactional
	@Test
	public void testgetParametersSetForDeviceType(){
		try{
			assertNotNull(Session.getParametersSetForDeviceType(Long.valueOf(50), "questions_starred"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
