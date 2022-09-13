<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.suspension" text="Member Role Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style>
	#submit{
	cursor: pointer;
	}
	#cancel{
	cursor: pointer;
	}
	</style>
	</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>

<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/suspension" method="PUT" modelAttribute="domain">
<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}		
	</h2>
	<h2>
		<spring:message code="member.module.memberSuspensionDetails" text="Member Suspension"/>
	</h2>
	

	<p>
		<label class="small"><spring:message code="member.suspension.startdate" text="Start Date of Suspension"/></label>
		<form:input path="startDateOfSuspension" cssClass="sText datemask"/>
		<form:errors path="startDateOfSuspension" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.suspension.enddate" text="Estimated End Date of Suspension"/></label>
		<form:input path="estimatedEndDateOfSuspension" cssClass="sText datemask"/>
		<form:errors path="estimatedEndDateOfSuspension" cssClass="validationError"/>	
	</p>
		
	<p>
		<label class="small"><spring:message code="member.suspension.actualenddate" text="Actual End date of Suspension"/></label>
		<form:input path="actualEndDateOfSuspension" cssClass="sText datemask"/>
		<form:errors path="actualEndDateOfSuspension" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.suspension.reasonOfSuspension" text="Reason Of Suspension"/></label>
		<form:textarea path="reasonOfSuspension" cssClass="wysiwyg" rows="5" cols="50"/>
		<form:errors path="reasonOfSuspension" cssClass="validationError"/>	
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale" />
	<form:hidden path="id"/>
	<input id="member" name="member" value="${member}" type="hidden">
	
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>