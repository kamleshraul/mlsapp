<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.department" text="Member Department Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
		});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/department" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		${domain.member.title.name} ${domain.member.firstName} ${domain.member.middleName} ${domain.member.lastName}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="member.department.department" text="Department"/></label>
		<form:select path="department" items="${departments}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="department" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="sText datemask"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.department.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="sTextarea"/>
		<form:errors path="remarks" cssClass="validationError"/>	
	</p>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="recordIndex"/>
	<input id="member" name="member" value="${member}" type="hidden">
</form:form>
</div>
</body>
</html>