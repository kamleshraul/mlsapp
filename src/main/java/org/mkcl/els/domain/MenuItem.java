/*
******************************************************************
File: org.mkcl.els.domain.MenuItem.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class MenuItem.
 *
 * @author vishals
 * @version v1.0.0
 */
@Entity
@Table(name="menus")
public class MenuItem implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// Attributes --------------------------------------------------------------------------------------------------------------------
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** The key. */
	@Column(length=30)
	private String textKey;

	/** The text. */
	@Column(length=50)
	private String text;

	/** The url. */
	@Column(length=1000)
	private String url;

	/** The params. */
	@Column(length=2000)
	private String params;

	/** The parent. */
	@ManyToOne()
    @JoinColumn(name = "parent")
    private MenuItem parent;

    /** The position. */
    private int position;
	
    /** The locale. */
    @Column(length=50)
    private String locale;
    
    // Constructors --------------------------------------------------------------------------------------------------------------------
    
    /**
     * Instantiates a new menu item.
     */
    public MenuItem(){
    	
    }
    
    /**
     * Instantiates a new menu item.
     *
     * @param textKey the text key
     * @param text the text
     * @param url the url
     * @param params the params
     * @param position the position
     */
    public MenuItem(String textKey, String text, String url,
			String params, int position) {
		super();
		this.textKey = textKey;
		this.text = text;
		this.url = url;
		this.params = params;
		this.setPosition(position);
	}
    
    
    /**
     * Instantiates a new menu item with parent.
     *
     * @param textKey the text key
     * @param text the text
     * @param url the url
     * @param params the params
     * @param position the position
     * @param parent the parent
     */
    public MenuItem(String textKey, String text, String url,
			String params, int position, MenuItem parent) {
		super();
		this.textKey = textKey;
		this.text = text;
		this.url = url;
		this.params = params;
		this.setPosition(position);
		this.parent = parent;
	}
    
    
	/**
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
	 */
	public MenuItem(Long id, String textKey, String text, String url,
			String params, MenuItem parent, int position, String locale) {
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
    
    // Getters/Setters --------------------------------------------------------------------------------------------------------------------



	/**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

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
	public void setTextKey(String textKey) {
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
	public void setText(String text) {
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
	public void setUrl(String url) {
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
	public void setParams(String params) {
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
	public void setParent(MenuItem parent) {
		this.parent = parent;
	}


	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	

}
