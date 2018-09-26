/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.NotificationTemplate.java
 * Created On: Sep 22, 2018
 */
package org.mkcl.els.domain.notification;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationTemplate.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Entity
@Table(name="notification_templates")
public class NotificationTemplate extends BaseDomain implements Serializable {

	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The template key. */
    @Column(length=300)
    private String templateKey;
    
    /** The template query. */
    @Column(length=30000)
    private String templateQuery;
    
    /** The title query. */
    @Column(length=10000)
    private String titleQuery;
    
    /** The receivers query. */
    @Column(length=10000)
    private String receiversQuery;
    
    @Autowired
    private transient NotificationTemplateRepository notificationTemplateRepository;
    
    // ---------------------------------Constructors-----------------------------------------------
    /**
     * Instantiates a new PushMessage Template.
     */
    public NotificationTemplate() {
        super();
    }
    
    // ------------------------------------------Domain Methods-----------------------------------
    public static NotificationTemplateRepository getNotificationTemplateRepository() {
    	NotificationTemplateRepository notificationTemplateRepository = new NotificationTemplate().notificationTemplateRepository;
        if (notificationTemplateRepository == null) {
            throw new IllegalStateException(
                    "NotificationTemplateRepository has not been injected in NotificationTemplate Domain");
        }
        return notificationTemplateRepository;
    }
    
    public static String generateNotificationMessage(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
    	return getNotificationTemplateRepository().generateNotificationMessage(notificationTemplate, templateParameters);
    }
    
    public static String generateNotificationTitle(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
    	return getNotificationTemplateRepository().generateNotificationTitle(notificationTemplate, templateParameters);
    }
    
    public static String generateNotificationReceivers(final NotificationTemplate notificationTemplate, final Map<String, String[]> templateParameters) {
    	return getNotificationTemplateRepository().generateNotificationReceivers(notificationTemplate, templateParameters);
    }

    // ------------------------------------------Getters/Setters-----------------------------------
	public String getTemplateKey() {
		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}

	public String getTemplateQuery() {
		return templateQuery;
	}

	public void setTemplateQuery(String templateQuery) {
		this.templateQuery = templateQuery;
	}

	public String getTitleQuery() {
		return titleQuery;
	}

	public void setTitleQuery(String titleQuery) {
		this.titleQuery = titleQuery;
	}

	public String getReceiversQuery() {
		return receiversQuery;
	}

	public void setReceiversQuery(String receiversQuery) {
		this.receiversQuery = receiversQuery;
	}    

}