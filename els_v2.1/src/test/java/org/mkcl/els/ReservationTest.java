/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.ReservationTest.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.Reservation;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class ReservationTest.
 *
 * @author Anand
 * @since v1.0.0
 */
public class ReservationTest extends AbstractTest{

	/**
	 * Test persist.
	 */
	@Test
	@Transactional
	public void testPersist() {
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
	    Assert.assertNotNull("Saved reservation Data ", reservation);

	}

	/**
	 * Test merge.
	 */
	@Test
	@Transactional
	public void testMerge() {
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
		reservation.setName("new Reservation");
		reservation.merge();
	    Assert.assertNotNull("Updated reservation Data ", reservation);

	}

	/**
	 * Test remove.
	 */
	@Test
	@Transactional
	public void testRemove() {
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
		reservation.remove();
	    Assert.assertNotNull("Deleted reservation Data ", reservation);

	}

	/**
	 * Test find by id.
	 */
	@Test
	@Transactional
	public void testFindById() {
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
		Reservation reservation1=Reservation.findById(Reservation.class,reservation.getId() );
	    Assert.assertNotNull("Finding reservation Data by Id ", reservation1);

	}

	/**
	 * Test find by field name.
	 */
	@Test
	@Transactional
	public void testFindByFieldName() {
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
		Reservation reservation1=Reservation.findByFieldName(Reservation.class, "name", "TestReservation", reservation.getLocale());
	    Assert.assertNotNull("Finding reservation Data by Fieled name ", reservation1);

	}
	
	/**
	 * Test find all.
	 */
	@Test
	@Transactional
	public void testFindAll(){
		Reservation reservation=new Reservation("TestReservation","tr");
		reservation.persist();
		List<Reservation> reservation1=Reservation.findAll(Reservation.class, "name", "desc",reservation.getLocale());
	    Assert.assertNotNull("Finding reservation Data by Fieled name ", reservation1);

	}
	

}
