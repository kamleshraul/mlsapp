package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="highways")
public class Highway extends BaseDomain{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
    
    @Column(length=1000)
    private String text; 
    
    private String identificationkey;
    	
	/****Constructors****/
    
    public Highway() {
		super();
	}

    public Highway(String name, String text,String identificationkey) {
		super();
		this.name = name;
		this.text = text;
		this.identificationkey=identificationkey;
	}

 
	/****Domain Methods ****/

	
	/****Getters and Setters****/
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIdentificationkey() {
		return identificationkey;
	}

	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}
	
}
