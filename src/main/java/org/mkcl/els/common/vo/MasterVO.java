/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MasterVO.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class MasterVO.
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MasterVO {

	private Long id;

    /** The name. */
    private String name;
    
    private String value;
    
    private Integer number;
    
    private String formattedNumber;
    
    private Integer order;
    
    private Boolean isSelected;

	public MasterVO(Integer number,String name) {
		super();
		this.name = name;
		this.number = number;
	}

	public MasterVO(final Long id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public MasterVO() {
        super();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setNumber(final Integer number) {
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(final Boolean isSelected) {
		this.isSelected = isSelected;
	}
}
