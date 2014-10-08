/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.QuestionRepository.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionDateDevice;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

/**
 * The Class DiscussionDateDeviceRepository.
 *
 * @author vikasg
 * @since v1.0.0
 */
@Repository
public class DiscussionDateDeviceRepository extends BaseRepository<DiscussionDateDevice, Long> {

	
	public List<DiscussionDateDevice> findBySessionDeviceType(final Session session, final DeviceType deviceType, final String order, final String locale){
		StringBuffer strQuery = new StringBuffer("SELECT m FROM DiscussionDateDevice m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.locale=:locale ORDER BY m.discussionDate " + order);
		
		TypedQuery<DiscussionDateDevice> query = this.em().createQuery(strQuery.toString(), DiscussionDateDevice.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		
		return query.getResultList();
	}
	
	public DiscussionDateDevice findBySessionDeviceTypeDate(final Session session, 
			final DeviceType deviceType, 
			final Date discussionDate, 
			final String locale){
		StringBuffer strQuery = new StringBuffer("SELECT m FROM DiscussionDateDevice m" +
				" WHERE m.session.id=:sessionId" +
				" AND m.deviceType.id=:deviceTypeId" +
				" AND m.discussionDate=:discussionDate" +
				" AND m.locale=:locale");
		
		TypedQuery<DiscussionDateDevice> query = this.em().createQuery(strQuery.toString(), DiscussionDateDevice.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("discussionDate", discussionDate);
		query.setParameter("locale", locale);
		
		List<DiscussionDateDevice> list = query.getResultList();
		
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		
		return null;
	}
	
}
