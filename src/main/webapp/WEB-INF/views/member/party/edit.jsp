<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.party" text="Member Party Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
		});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/party" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="member.party.party" text="Party"/></label>
		<form:select path="party" items="${parties}" itemLabel="department" itemValue="id" cssClass="sSelect"/>
		<form:errors path="party" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="generic.fromdate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="sText datemask"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.todate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.party.sitting" text="Is Sitting"/></label>
		<form:checkbox path="sitting" cssClass="sOption"/>
		<form:errors path="sitting" cssClass="validationError"/>	
	</p>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="recordIndex"/>
	<input id="member" name="member" value="${member}" type="hidden">
</form:form>
</div>
</body>
</html>