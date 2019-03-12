package org.mkcl.els.common.vo;

import java.util.Date;

public class DepartmentDashboardVo {
	
		private Long id;
		
		private String subdepartment;
	    
	    private String houseType;
	    
	    private String deviceType;    
	    
	    private String sessionType;
	    
	    private String sessionYear;
	    
	    private Integer totalCount;
	    
	    private Integer pendingCount;
	    
	    private Integer completedCount;
	    
	    private Integer timeoutCount;
	    
	    private Integer assemblyCount;
	    
	    private Integer councilCount;
	    
	    private String deviceNumber;
	    
	    private String assignee;
	    
	    private String assignmentTime;
	    
	    private String subject;

		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}

		public String getSubdepartment() {
			return subdepartment;
		}

		public void setSubdepartment(String subdepartment) {
			this.subdepartment = subdepartment;
		}

		public String getHouseType() {
			return houseType;
		}

		public void setHouseType(String houseType) {
			this.houseType = houseType;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public String getSessionType() {
			return sessionType;
		}

		public void setSessionType(String sessionType) {
			this.sessionType = sessionType;
		}

		public String getSessionYear() {
			return sessionYear;
		}

		public void setSessionYear(String sessionYear) {
			this.sessionYear = sessionYear;
		}

		public Integer getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(Integer totalCount) {
			this.totalCount = totalCount;
		}

		public Integer getPendingCount() {
			return pendingCount;
		}

		public void setPendingCount(Integer pendingCount) {
			this.pendingCount = pendingCount;
		}

		public Integer getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Integer completedCount) {
			this.completedCount = completedCount;
		}

		public Integer getTimeoutCount() {
			return timeoutCount;
		}

		public void setTimeoutCount(Integer timeoutCount) {
			this.timeoutCount = timeoutCount;
		}
		public Integer getAssemblyCount() {
			return assemblyCount;
		}

		public void setAssemblyCount(Integer assemblyCount) {
			this.assemblyCount = assemblyCount;
		}

		public Integer getCouncilCount() {
			return councilCount;
		}

		public void setCouncilCount(Integer councilCount) {
			this.councilCount = councilCount;
		}
		
		public String getDeviceNumber() {
			return deviceNumber;
		}

		public void setDeviceNumber(String deviceNumber) {
			this.deviceNumber = deviceNumber;
		}

		public String getAssignee() {
			return assignee;
		}

		public void setAssignee(String assignee) {
			this.assignee = assignee;
		}

		public String getAssignmentTime() {
			return assignmentTime;
		}

		public void setAssignmentTime(String assignmentTime) {
			this.assignmentTime = assignmentTime;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}    
	
}
