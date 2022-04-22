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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;

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
    
    private String type;
    
    private String displayName;
    
    private Integer order;
    
    private String formattedOrder;
    
    private Boolean isSelected;
    
    private Date sessionDate;
    
    
    //=============== CONSTRUCTORS ==================
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

	public MasterVO(final Long id, final String name, final String value) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public MasterVO() {
        super();
    }
	
	
	//=============== UTILITY METHODS ===============
	public static List<MasterVO> sortByOrder(final List<MasterVO> masterVOs, final String sortOrder) {
		List<MasterVO> newMasterVOs = new ArrayList<MasterVO>();
		newMasterVOs.addAll(masterVOs);	
		Collections.sort(newMasterVOs, new Comparator<MasterVO>() {
	        @Override 
	        public int compare(MasterVO vo1, MasterVO vo2) {
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

	
	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	@Override
	public boolean equals(Object mv) {
		boolean retVal = false;
		if(mv instanceof MasterVO){
			MasterVO m = (MasterVO) mv;
//			return (this.getName().equals(m.getName()) && this.getValue().equals(m.getValue()));
			String thisName = this.getName();
			String mName = m.getName();
			String thisValue = this.getValue();
			String mValue = m.getValue();
			
			if(this.getName()==null) {
				thisName = "";
			}
			if(m.getName()==null) {
				mName = "";
			}
			if(this.getValue()==null) {
				thisValue = "";
			}
			if(m.getValue()==null) {
				mValue = "";
			}
			
			return (thisName.equals(mName) && thisValue.equals(mValue));
		}else{
			retVal = super.equals(mv);
		}
		
		return retVal;
	}
	
	public static List<MasterVO> sort(final List<MasterVO> deviceVOs, final String sortField, final String sortOrder) {
		List<MasterVO> sortedMasterVOs = null;
		if(deviceVOs!=null) {
			sortedMasterVOs = new ArrayList<MasterVO>();
			if(!deviceVOs.isEmpty()) {
				sortedMasterVOs.addAll(deviceVOs);
				if(sortField!=null && !sortField.isEmpty()) {
					if(sortField.equals("number")) {
						Comparator<MasterVO> c = new Comparator<MasterVO>() {
							@Override
							public int compare(final MasterVO dv1, final MasterVO dv2) {
								if(sortOrder.equals(ApplicationConstants.DESC)) {
									return dv2.getNumber().compareTo(dv1.getNumber());
								} else {
									return dv1.getNumber().compareTo(dv2.getNumber());
								}
							}
						};
						Collections.sort(sortedMasterVOs, c);					
					}
				}
			}			
		}			
		return sortedMasterVOs;
	}
	
}
