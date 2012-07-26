/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.GroupTest.java
 * Created On: Jul 25, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.SessionType;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class GroupTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class GroupTest extends AbstractTest {

	/**
	 * Test find by house type session type year.
	 */
	@Test
	@Transactional
	public void testFindByHouseTypeSessionTypeYear() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		List<Group> groups= Group.findByHouseTypeSessionTypeYear(houseType, sessionType, group.getYear());
		Assert.assertEquals(true,groups.size()>0);
	}

	/**
	 * Test find answering dates.
	 * @throws ParseException 
	 */
	@Test
	@Transactional
	public void testFindAnsweringDates() throws ParseException {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.setLocale("mr_IN");
		houseType.persist();
		
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.setLocale("mr_IN");
		ministry.persist();
		
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
	
		QuestionDates questionDate= new QuestionDates();
		questionDate.setLocale("mr_IN");
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		questionDate.setAnsweringDate(df.parse("12/10/2011"));
		questionDate.setFinalSubmissionDate(df.parse("12/09/2011"));
		questionDate.persist();
		
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.setLocale("mr_IN");
		sessionType.persist();
		
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.setLocale("mr_IN");
		group.persist();
		List<String> dates= Group.findAnsweringDates(group.getId());
		Assert.assertEquals(true,dates.size()>0);
	}

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		Assert.assertNotNull("Saved Group Data ", group);
	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		group.setNumber(13);
		group.merge();
		Assert.assertNotNull("Updated Group Data ", group);
	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		group.remove();
		Assert.assertNotNull("Deleted Group Data ", group);
	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		Group group1=Group.findByFieldName(Group.class, "number", 2, group.getLocale());
		Assert.assertNotNull("Finding Group Data ", group1);
	}

	/**
	 * Test find by field name class.
	 */
	@Test
	@Transactional
	public void testFindByFieldNameClass() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		Assert.assertNotNull("Finding Group Data ", group);
	}

	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		HouseType houseType=new HouseType("testhouse","test");
		houseType.persist();
		Ministry ministry=new Ministry("testMinistry",false,null);
		ministry.persist();
		List<Ministry> ministries= Ministry.findAll(Ministry.class, "name", "desc", ministry.getLocale());
		QuestionDates questionDate= new QuestionDates();
		questionDate.persist();
		questionDate.setAnsweringDate(new Date(12-10-2012));
		questionDate.setFinalSubmissionDate(new Date(15-11-2012));
		List<QuestionDates> questionDates=QuestionDates.findAll(QuestionDates.class, "answeringDate", "desc",questionDate.getLocale());
		SessionType sessionType=new SessionType("test type");
		sessionType.persist();
		Group group=new Group();
		group.setHouseType(houseType);
		group.setMinistries(ministries);
		group.setNumber(2);
		group.setQuestionDates(questionDates);
		group.setSessionType(sessionType);
		group.setYear(2012);
		group.persist();
		List<Group> groups=Group.findAll(Group.class, "year", "desc",group.getLocale());
		Assert.assertEquals(true,groups.size()>0);
	}

}
