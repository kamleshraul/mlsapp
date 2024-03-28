package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name ="editor_parts")
public class EditorPart extends BaseDomain implements Serializable{
	
	
	 /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;
    
    
    /*
	 *
	 *-----> Fields <--------
	 * 
	 */
    
    
	private Integer orderNo;
  
	private String mainHeading;
	
	private String pageHeading;
	
	@Column(length=30000)
	private String proceedingContent;
	
	@Column(length=30000)
	private String revisedContent;
	
	
	@ManyToOne
	private User editor;
	
	
	@ManyToOne
	private Roster roster;
	
	
	/*
	 *
	 *--------> Constructor <--------
	 * 
	 */

	
	public EditorPart() {
		super();
		
	}

	
	/*
	 *
	 *--------> Getter And Setters  <--------
	 * 
	 */

	public Integer getOrderNo() {
		return orderNo;
	}



	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public String getMainHeading() {
		return mainHeading;
	}

	public void setMainHeading(String mainHeading) {
		this.mainHeading = mainHeading;
	}

	public String getPageHeading() {
		return pageHeading;
	}

	public void setPageHeading(String pageHeading) {
		this.pageHeading = pageHeading;
	}

	public String getProceedingContent() {
		return proceedingContent;
	}

	public void setProceedingContent(String proceedingContent) {
		this.proceedingContent = proceedingContent;
	}

	public String getRevisedContent() {
		return revisedContent;
	}

	public void setRevisedContent(String revisedContent) {
		this.revisedContent = revisedContent;
	}

	public User getEditor() {
		return editor;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	public Roster getRoster() {
		return roster;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}
	
	
	
	
}
