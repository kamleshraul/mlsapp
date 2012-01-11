<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>

<html>
<head>
	<title>
		Motion Information System
	</title>
</head>
<body>
<div class="commandbar">
		<div class="commandbarContent">
			<a  href="#" class="mis" id="member_personal_details">Assembly Details</a> |
			<a  href="#" class="mis" id="member_contact_details">Motion Details</a> 
		</div>
</div>	
<form:form  action="motion_information" method="POST" >
	<div class="info">
		 <h2>Motion Information System </h2>		
	</div>
	<div id="positionContentDiv">
	<ul>	
					
		<li>
		<label class="desc">सूचना प्रकार&nbsp;*</label>
			<div>
			<select>
				<option value="Adjournment Motion">स्थगन प्रस्ताव</option>
				<option value="Calling Attention">लक्षवेधी सूचना</option>
				<option value="Half an hour discussion">अर्धा-तास चर्चा</option>
				<option value="Short Duration Discussion">अल्पकालीन चर्चा</option>
			</select>
			</div>
		</li>
			
		<li  class="name ${fields.title.visible}" id="${fields.title.position}">
		<span>
		<label>वर्ष&nbsp;</label>
		<form:input cssClass="field text" path="year"/>
		</span>
		<span>
		<label>सभा &nbsp;</label>
			<div>
			<select>
				<option value="">उन्हाळी</option>
				<option value=""></option>
				<option value=""></option>
				<option value=""></option>
			</select>
			</div>
		</span>
				
		<span>
		<label><spring:message code="member_personal_details.lastName" text="Last Name"/><span><spring:message code="${fields.lastName.hint}" text=""/></span>&nbsp;<c:if test="${fields.lastName.mandatory=='MANDATORY'}">*</c:if></label>
		<form:input cssClass="field text ${fields.lastName.mandatory}" path="lastName"/><form:errors path="lastName" cssClass="field_error" /><span id="lastNameError"></span>	
		</span>
		</li>
		
		</ul>
		</div>
	<input id="saveForm" class="btTxt" type="submit" value="Submit" />
	
</form:form>

</body>
</html>