/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MenuItem.java
 * Created On: Jan 6, 2012
 *//*
package org.mkcl.els.domain;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mkcl.els.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

*//**
 * The Class MenuItem.
 *
 * @author vishals
 * @version v1.0.0
 *//*
@Configurable
@Entity
@Table(name = "menus")
public class MenuItem  extends BaseDomain implements Serializable {

    *//** The Constant serialVersionUID. *//*
    private static final long serialVersionUID = 1L;

    // ----------------------- Attributes ----------------------- //
    *//** The key. *//*
    @Column(length = 30)
    private String textKey;

    *//** The text. *//*
    @Column(length = 50)
    private String text;

    *//** The url. *//*
    @Column(length = 1000)
    private String url;

    *//** The params. *//*
    @Column(length = 2000)
    private String params;

    *//** The parent. *//*
    @ManyToOne()
    @JoinColumn(name = "parent")
    private MenuItem parent;

    *//** The position. *//*
    private int position;

    *//** The menu item repository. *//*
    @Autowired
    private transient MenuItemRepository menuItemRepository;

    *//** The Constant logger. *//*
    private static final Logger menuLogger = LoggerFactory
            .getLogger(MenuItem.class);

    // -------------------- Constructors -------------------------//
    *//**
     * Instantiates a new menu item.
     *//*
    public MenuItem() {
        super();
    }

    *//**
     * Instantiates a new menu item.
     *
     * @param textKey the text key
     * @param text the text
     * @param url the url
     * @param params the params
     * @param position the position
     *//*
    public MenuItem(final String textKey,
            final String text,
            final String url,
            final String params,
            final int position) {
        super();
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.setPosition(position);
    }

    *//**
     * Instantiates a new menu item with parent.
     *
     * @param textKey the text key
     * @param text the text
     * @param url the url
     * @param params the params
     * @param position the position
     * @param parent the parent
     *//*
    public MenuItem(final String textKey,
            final String text,
            final String url,
            final String params,
            final int position,
            final MenuItem parent) {
        super();
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.setPosition(position);
        this.parent = parent;
    }

    *//**
     * Instantiates a new menu item.
     *
     * @param id the id
     * @param textKey the text key
     * @param text the text
     * @param url the url
     * @param params the params
     * @param parent the parent
     * @param position the position
     * @param locale the locale
     *//*
    public MenuItem(final Long id,
            final String textKey,
            final String text,
            final String url,
            final String params,
            final MenuItem parent,
            final int position,
            final String locale) {
        super();
        this.id = id;
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.parent = parent;
        this.position = position;
        this.locale = locale;
    }

    // -------------------- Domain Methods ---------------------//

    *//**
     * Gets the menu item repository.
     *
     * @return the menu item repository
     *//*
    public static MenuItemRepository getMenuItemRepository() {
        final MenuItemRepository repository = new MenuItem().menuItemRepository;
        if (repository == null) {
            throw new IllegalStateException(
                    "MenuItemRepository has not been injected");
        }
        return repository;
    }

   
    *//**
     * Menu item.
     *
     * @param textKey the text key
     * @return the menu item
     * @author meenalw
     * @since v1.0.0
     *//*
    @Transactional(readOnly = true)
    public static MenuItem findByTextKey(final String textKey,
                                         final String locale) {
        return getMenuItemRepository().findMenuItemByTextKey(textKey, locale);
    }

    *//**
     * List.
     *
     * @param property the property
     * @param locale the locale
     * @param descOrder the desc order
     * @return the list< menu item>
     * @author meenalw
     * @since v1.0.0
     *//*
    @Transactional(readOnly = true)
    public static List<MenuItem> findAllSorted(final String property,
                                               final String locale,
                                               final boolean descOrder) {
        return getMenuItemRepository().findAllSorted(property, locale, descOrder);
    }

    *//**
     * Gets the menu xml.
     *
     * @param locale the locale
     * @return the menu xml
     *//*
    public static String getMenuXml(final Locale locale) {
        List<MenuItem> items = getMenuItemRepository().findAllSorted("text", locale.toString(), false);
        Element root = new Element("root");
        for (MenuItem item : items) {
            Element row = new Element("menu");
            row.setAttribute(new Attribute("id", item.getId() + ""));
            row.setAttribute(new Attribute("text", item.getText()));
            row.setAttribute(new Attribute("url", item.getUrl()));
            if (item.getParent() != null) {
                row.setAttribute(new Attribute("parent", item.getParent()
                        .getId() + ""));
            }
            root.addContent(row);
        }
        StringWriter writer = new StringWriter();
        XMLOutputter serializer = new XMLOutputter();
        try {
            serializer.output(root, writer);
        } catch (IOException e) {
            menuLogger.error(e.toString());
        }
        return writer.toString();
    }

    *//**
     * Gets the menu xml.
     *
     * @return the menu xml
     *//*
    public static String getMenuXml() {
        List<MenuItem> items = MenuItem.findAll();
        Element root = new Element("root");
        for (MenuItem item : items) {
            Element row = new Element("menu");
            row.setAttribute(new Attribute("id", item.getId() + ""));
            row.setAttribute(new Attribute("text", item.getText()));
            row.setAttribute(new Attribute("url", item.getUrl()));
            if (item.getParent() != null) {
                row.setAttribute(new Attribute("parent", item.getParent()
                        .getId() + ""));
            }
            root.addContent(row);
        }
        StringWriter writer = new StringWriter();
        XMLOutputter serializer = new XMLOutputter();
        try {
            serializer.output(root, writer);
        } catch (IOException e) {
            menuLogger.error(e.toString());
        }
        return writer.toString();
    }

    *//**
     * List.
     *
     * @return the list< menu item>
     * @author meenalw
     * @since v1.0.0
     *//*
    @Transactional(readOnly = true)
    public static List<MenuItem> findAll() {
        return getMenuItemRepository().findAll();
    }

    // ------------------------ Getters/Setters ----------------------------- //

    *//**
     * Gets the id.
     *
     * @return the id
     *//*
    public Long getId() {
        return id;
    }

    *//**
     * Sets the id.
     *
     * @param id the new id
     *//*
    public void setId(final Long id) {
        this.id = id;
    }

    *//**
     * Gets the key.
     *
     * @return the key
     *//*
    public String getTextKey() {
        return textKey;
    }

    *//**
     * Sets the key.
     *
     * @param textKey the new text key
     *//*
    public void setTextKey(final String textKey) {
        this.textKey = textKey;
    }

    *//**
     * Gets the text.
     *
     * @return the text
     *//*
    public String getText() {
        return text;
    }

    *//**
     * Sets the text.
     *
     * @param text the new text
     *//*
    public void setText(final String text) {
        this.text = text;
    }

    *//**
     * Gets the url.
     *
     * @return the url
     *//*
    public String getUrl() {
        return url;
    }

    *//**
     * Sets the url.
     *
     * @param url the new url
     *//*
    public void setUrl(final String url) {
        this.url = url;
    }

    *//**
     * Gets the params.
     *
     * @return the params
     *//*
    public String getParams() {
        return params;
    }

    *//**
     * Sets the params.
     *
     * @param params the new params
     *//*
    public void setParams(final String params) {
        this.params = params;
    }

    *//**
     * Gets the parent.
     *
     * @return the parent
     *//*
    public MenuItem getParent() {
        return parent;
    }

    *//**
     * Sets the parent.
     *
     * @param parent the new parent
     *//*
    public void setParent(final MenuItem parent) {
        this.parent = parent;
    }

    *//**
     * Sets the position.
     *
     * @param position the new position
     *//*
    public void setPosition(final int position) {
        this.position = position;
    }

    *//**
     * Gets the position.
     *
     * @return the position
     *//*
    public int getPosition() {
        return position;
    }

    *//**
     * Gets the locale.
     *
     * @return the locale
     *//*
    public String getLocale() {
        return locale;
    }

    *//**
     * Sets the locale.
     *
     * @param locale the new locale
     *//*
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    *//**
     * Gets the version.
     *
     * @return the version
     *//*
    public Long getVersion() {
        return version;
    }

    *//**
     * Sets the version.
     *
     * @param version the new version
     *//*
    public void setVersion(final Long version) {
        this.version = version;
    }
}
*/


package org.mkcl.els.domain;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mkcl.els.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;


@Configurable
@Entity
@Table(name = "menus")
public class MenuItem extends BaseDomain implements Serializable {
    // ----------------------- Attributes ----------------------- //
    private transient static final long serialVersionUID = 1L;  
    @NotEmpty
    @Column(length = 30)
    private String textKey;
    
    @NotEmpty
    @Column(length = 50)
    private String text;
    
    @NotEmpty
    @Column(length = 1000)
    private String url;

    @Column(length = 2000)
    private String params;

    @ManyToOne()
    @JoinColumn(name = "parent")
    private MenuItem parent;
    
    @NotNull
    private Integer position;    

    @Autowired
    private transient MenuItemRepository menuItemRepository;
        
    private transient static final Logger menuLogger = LoggerFactory
            .getLogger(MenuItem.class);

    // -------------------- Constructors -------------------------//
   
    public MenuItem() {
        super();
    }
   
    public MenuItem(final String textKey,
            final String text,
            final String url,
            final String params,
            final Integer position) {
        super();
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.setPosition(position);
    }
   
    public MenuItem(final String textKey,
            final String text,
            final String url,
            final String params,
            final Integer position,
            final MenuItem parent) {
        super();
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.setPosition(position);
        this.parent = parent;
    }
    
    public MenuItem(final String textKey,
            final String text,
            final String url,
            final String params,
            final MenuItem parent,
            final Integer position
            ) {
        super();
        this.textKey = textKey;
        this.text = text;
        this.url = url;
        this.params = params;
        this.parent = parent;
        this.position = position;
    }
    
    public static MenuItemRepository getMenuItemRepository() {
    	MenuItemRepository menuItemRepository = new MenuItem().menuItemRepository;
        if (menuItemRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRepository has not been injected in Assembly Domain");
        }
        return menuItemRepository;
    }

    // -------------------- Domain Methods ---------------------//     
        
    @SuppressWarnings("unchecked")
	public static String getMenuXml() {
		List<MenuItem> items = (List<MenuItem>) getMenuItemRepository().findAll(MenuItem.class,"textKey","asc", "");
        Element root = new Element("root");
        for (MenuItem item : items) {
            Element row = new Element("menu");
            row.setAttribute(new Attribute("id", item.getId() + ""));
            row.setAttribute(new Attribute("text", item.getText()));
            row.setAttribute(new Attribute("url", item.getUrl()));
            if (item.getParent() != null) {
                row.setAttribute(new Attribute("parent", item.getParent()
                        .getId() + ""));
            }
            root.addContent(row);
        }
        StringWriter writer = new StringWriter();
        XMLOutputter serializer = new XMLOutputter();
        try {
            serializer.output(root, writer);
        } catch (IOException e) {
            menuLogger.error(e.toString());
        }
        return writer.toString();
    }
    @SuppressWarnings("unchecked")
	public static String getMenuXml(String locale) {
		List<MenuItem> items = (List<MenuItem>) getMenuItemRepository().findAll(MenuItem.class, "textKey", "asc", locale);
		Element root = new Element("root");
        for (MenuItem item : items) {
            Element row = new Element("menu");
            row.setAttribute(new Attribute("id", item.getId() + ""));
            row.setAttribute(new Attribute("text", item.getText()));
            row.setAttribute(new Attribute("url", item.getUrl()));
            if (item.getParent() != null) {
                row.setAttribute(new Attribute("parent", item.getParent()
                        .getId() + ""));
            }
            root.addContent(row);
        }
        StringWriter writer = new StringWriter();
        XMLOutputter serializer = new XMLOutputter();
        try {
            serializer.output(root, writer);
        } catch (IOException e) {
            menuLogger.error(e.toString());
        }
        return writer.toString();
    }
   
    // ------------------------ Getters/Setters ----------------------------- //
   
    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getTextKey() {
        return textKey;
    }

    /**
     * Sets the key.
     *
     * @param textKey the new text key
     */
    public void setTextKey(final String textKey) {
        this.textKey = textKey;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text the new text
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public String getParams() {
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the new params
     */
    public void setParams(final String params) {
        this.params = params;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public MenuItem getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(final MenuItem parent) {
        this.parent = parent;
    }

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
   
}
