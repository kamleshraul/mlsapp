package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="cutmotiondepartmentdatepriority")
@JsonIgnoreProperties({"department","subDepartment","discussionDate", "submissionEndDate"})
public class CutMotionDepartmentDatePriority extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**** Department ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="department_id")
	private Department department;
	
	/**** SubDepartment ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subdepartment_id")
	private SubDepartment subDepartment;
	
	/**** discussionDate ****/
	@Temporal(TemporalType.DATE)
	private Date discussionDate;
	
	/**** submissionEndDate ****/
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionEndDate;	
	
	private Integer priority;
	
	/**
     * To keep the referring cutmotiondate in order to preserve its all cutmotiondepartmentdatepriority details
     */
    @Column(length=45, name="cutmotiondate_id")    
    private String cutMotionDateId;

	public CutMotionDepartmentDatePriority() {
		super();
	}

	public CutMotionDepartmentDatePriority(Department department,
			SubDepartment subDepartment, Date discussionDate,
			Date submissionEndDate) {
		super();
		this.department = department;
		this.subDepartment = subDepartment;
		this.discussionDate = discussionDate;
		this.submissionEndDate = submissionEndDate;
	}
	
	public static List<CutMotionDepartmentDatePriority> sortByDiscussionDateAndPriority(final List<CutMotionDepartmentDatePriority> departmentDates) {
		List<CutMotionDepartmentDatePriority> sortedDepartmentDates = null;
		if(departmentDates!=null) {
			sortedDepartmentDates = new ArrayList<CutMotionDepartmentDatePriority>();
			sortedDepartmentDates.addAll(departmentDates);
			if(sortedDepartmentDates.size()>1) { //sort if list is atleast having 2 elements otherwise return as it is
				Comparator<CutMotionDepartmentDatePriority> c = new Comparator<CutMotionDepartmentDatePriority>() {
					@Override
					public int compare(final CutMotionDepartmentDatePriority departmentDate1, final CutMotionDepartmentDatePriority departmentDate2) {
						//=========check null objects===================
						if (departmentDate1 == null) {
					        return (departmentDate2 == null) ? 0 : -1;
					    }
					    if (departmentDate2 == null) {
					        return 1;
					    }		
					    //==============================================
					    
					    //=========check null discussion dates==========
					    if (departmentDate1.getDiscussionDate() == null) {
					        return (departmentDate2.getDiscussionDate() == null) ? 0 : -1;
					    }
					    if (departmentDate2.getDiscussionDate() == null) {
					        return 1;
					    }
					    //==============================================
					    
					    //=========sort by discussion dates=============
					    int discussionDateSortingResult = departmentDate1.getDiscussionDate().compareTo(departmentDate2.getDiscussionDate());
				        if (discussionDateSortingResult != 0) {
				            return discussionDateSortingResult;
				        }
				        //==============================================		
				        
				        //=========check null priorities================
					    if (departmentDate1.getPriority() == null) {
					        return (departmentDate2.getPriority() == null) ? 0 : -1;
					    }
					    if (departmentDate2.getPriority() == null) {
					        return 1;
					    }
					    //==============================================
					    
				        //=========sort further by priorities===========				    
						return departmentDate1.getPriority().compareTo(departmentDate2.getPriority());
					}
				};
				Collections.sort(sortedDepartmentDates, c);
			}
		}
		return sortedDepartmentDates;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Date getSubmissionEndDate() {
		return submissionEndDate;
	}

	public void setSubmissionEndDate(Date submissionEndDate) {
		this.submissionEndDate = submissionEndDate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCutMotionDateId() {
		return cutMotionDateId;
	}

	public void setCutMotionDateId(String cutMotionDateId) {
		this.cutMotionDateId = cutMotionDateId;
	}
}
