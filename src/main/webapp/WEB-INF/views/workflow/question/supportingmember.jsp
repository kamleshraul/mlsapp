<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="workflow/question/supportingmember" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="supportingmember.heading" text="Approve request to add you as supporting member"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p style="display:none;">
		<label class="small"><spring:message code="question.houseType" text="House Type"/></label>		
		<input type="text" class="sText" id="houseType" name="houseType" value="${houseTypeName}" readonly="readonly">
	
		<label class="small"><spring:message code="question.year" text="Year"/>*</label>
		<input type="text" class="sText" id="year" name="year" value="${year}" readonly="readonly">
	
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
		<input type="text" class="sText" id="sessionType" name="sessionType" value="${sessionType}" readonly="readonly">
		
		<label class="small"><spring:message code="question.type" text="Type"/>*</label>
		<input type="text" class="sText" id="questionType" name="questionType" value="${questionType}" readonly="readonly">
	</p>	
		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="primaryMember" class="sText" type="text"  value="${primaryMemberName}" readonly="readonly" style="height: 28px;">
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="supportingMembers"  class="sTextarea" readonly="readonly" rows="2" cols="50">${supportingMembersName}</textarea>
	</p>
	
	<p>
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
		<form:textarea path="approvedSubject" readonly="true" rows="2" cols="50"></form:textarea>
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
		<form:textarea path="approvedQuestionText" cssClass="wysiwyg" readonly="true"></form:textarea>
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<input id="priority" class="sText" type="text"  value="${priority}" readonly="readonly" style="height: 28px;">
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.decisionstatus" text="Decision?"/>*</label>
		<form:select path="decisionStatus" cssClass="sSelect" items="${decisionStatus}" itemLabel="name" itemValue="id"/>
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="supportingmember.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError"></form:errors>
	</p>	
	<c:if test="${type!='success'}">
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	</c:if>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<form:hidden path="approvalDate"/>
	<form:hidden path="requestReceivedOn"/>	
	<input type="hidden" id="member" name="member" value="${member}">
	<input type="hidden" id="question" name="question" value="${question}">
	<input type="hidden" id="task" name="task" value="${task}">	
	<%--21012013 --%>
	<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
	<input type="hidden" name="discussionDate" id="discussionDate" value="${discussionDateSelected}" />	
</form:form>
</div>
</body>
</html>