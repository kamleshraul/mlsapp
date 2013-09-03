package org.mkcl.els.common.vo;

public class DeviceVO {
	//=============== ATTRIBUTES ====================
		/** The id. */
		private Long id;

		/** The number. */
		private Integer number;
		
		/** The formatted number **/
		private String formattedNumber;
		
		/** The serial number **/
		private String serialNumber;
		
		/** The member names **/
		private String memberNames;
		
		/** The subject **/
		private String subject;	
		
		/** The content **/
		private String content;
		
		/** The ministry name **/
		private String ministryName;
		
		/** The submission date **/
		private String submissionDate;
		
		/** The answer **/
		private String answer;
		
		/** The answered by (e.g. minister name who gave answer) **/
		private String answeredBy;
		
		/** The answering date **/
		private String answeringDate;
		
		/** The date Of Answer By Department **/
		private String dateOfAnswerByDepartment;

		/** The status. */
		private String status;
		
		private String localisedStatus;

		private Boolean hasParent;
		
		private String parent;		
		
		private String parentAnsweringDate;		
		
		private String parentYaadiNumber;
		
		private String kids;
		
		private Boolean isFactualRecieved;
		
		public DeviceVO() {
			super();
		}
		
		public DeviceVO(Long id, Integer number, String status) {
			super();
			this.id = id;
			this.number = number;
			this.status = status;
		}

		public DeviceVO(Long id, Integer number, String status,String localisedStatus) {
			super();
			this.id = id;
			this.number = number;
			this.status = status;
			this.localisedStatus=localisedStatus;
		}
		
		public DeviceVO(Long id, Integer number, String status,String localisedStatus, Boolean isFactualRecieved) {
			super();
			this.id = id;
			this.number = number;
			this.status = status;
			this.isFactualRecieved=isFactualRecieved;
			this.localisedStatus=localisedStatus;
		}
		
		

		public DeviceVO(Long id, Integer number, String status,
				Boolean hasParent, String parent, String kids) {
			super();
			this.id = id;
			this.number = number;
			this.status = status;
			this.hasParent = hasParent;
			this.parent = parent;
			this.kids = kids;
		}
		
		public DeviceVO(String formattedNumber, String content) {
			super();
			this.formattedNumber = formattedNumber;
			this.content = content;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Boolean getHasParent() {
			return hasParent;
		}

		public void setHasParent(Boolean hasParent) {
			this.hasParent = hasParent;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getParentAnsweringDate() {
			return parentAnsweringDate;
		}

		public void setParentAnsweringDate(String parentAnsweringDate) {
			this.parentAnsweringDate = parentAnsweringDate;
		}

		public String getParentYaadiNumber() {
			return parentYaadiNumber;
		}

		public void setParentYaadiNumber(String parentYaadiNumber) {
			this.parentYaadiNumber = parentYaadiNumber;
		}

		public String getKids() {
			return kids;
		}

		public void setKids(String kids) {
			this.kids = kids;
		}

		public Boolean getIsFactualRecieved() {
			return isFactualRecieved;
		}

		public void setIsFactualRecieved(Boolean isFactualRecieved) {
			this.isFactualRecieved = isFactualRecieved;
		}

		public String getFormattedNumber() {
			return formattedNumber;
		}

		public void setFormattedNumber(String formattedNumber) {
			this.formattedNumber = formattedNumber;
		}
		
		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public String getMemberNames() {
			return memberNames;
		}

		public void setMemberNames(String memberNames) {
			this.memberNames = memberNames;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getMinistryName() {
			return ministryName;
		}

		public void setMinistryName(String ministryName) {
			this.ministryName = ministryName;
		}

		public String getSubmissionDate() {
			return submissionDate;
		}

		public void setSubmissionDate(String submissionDate) {
			this.submissionDate = submissionDate;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public String getAnsweredBy() {
			return answeredBy;
		}

		public void setAnsweredBy(String answeredBy) {
			this.answeredBy = answeredBy;
		}

		public String getAnsweringDate() {
			return answeringDate;
		}

		public void setAnsweringDate(String answeringDate) {
			this.answeringDate = answeringDate;
		}

		public String getDateOfAnswerByDepartment() {
			return dateOfAnswerByDepartment;
		}

		public void setDateOfAnswerByDepartment(String dateOfAnswerByDepartment) {
			this.dateOfAnswerByDepartment = dateOfAnswerByDepartment;
		}

		public String getLocalisedStatus() {
			return localisedStatus;
		}

		public void setLocalisedStatus(String localisedStatus) {
			this.localisedStatus = localisedStatus;
		}		
		
		
}
