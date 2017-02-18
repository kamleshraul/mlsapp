/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.TestVO.java
 * Created On: July 30, 2016
 */
package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;

public class TestVO {

	private Long id;

    private String name;
    
    private String value;
    
    private String content;
    
    private Integer number;    
    
    private String formattedNumber;
    
    private String type;
    
    private String displayName;
    
    private Integer order;
    
    private String formattedOrder;
    
    private Boolean isSelected;
    
    
    //=============== CONSTRUCTORS ==================
	public TestVO(Integer number,String name) {
		super();
		this.name = name;
		this.number = number;
	}

	public TestVO(final Long id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public TestVO() {
        super();
    }
	
	
	//=============== UTILITY METHODS ===============
	public static List<TestVO> sortByOrder(final List<TestVO> masterVOs, final String sortOrder) {
		List<TestVO> newMasterVOs = new ArrayList<TestVO>();
		newMasterVOs.addAll(masterVOs);	
		Collections.sort(newMasterVOs, new Comparator<TestVO>() {
	        @Override 
	        public int compare(TestVO vo1, TestVO vo2) {
	        	if(sortOrder!=null && sortOrder.equals(ApplicationConstants.DESC)) {
	        		return vo2.getOrder().compareTo(vo1.getOrder());
	        	} else {
	        		return vo1.getOrder().compareTo(vo2.getOrder());
	        	}	           
	        }
	    });
		return newMasterVOs;
	}
	

	//=============== GETTERS/SETTERS ===============
    public String getName() {
        return name;
    }

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getFormattedOrder() {
		return formattedOrder;
	}

	public void setFormattedOrder(String formattedOrder) {
		this.formattedOrder = formattedOrder;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(final Boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public boolean equals(Object mv) {
		boolean retVal = false;
		if(mv instanceof TestVO){
			TestVO m = (TestVO) mv;
			return (this.getName().equals(m.getName()) && this.getValue().equals(m.getValue()));
		}else{
			retVal = super.equals(mv);
		}
		
		return retVal;
	}
	
}
