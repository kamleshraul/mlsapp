<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="${urlPattern}" text="Member Ministry Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
		});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="${urlPattern}" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="${urlPattern}.ministry" text="Ministry"/></label>
		<form:select path="ministry" items="${ministries}" itemLabel="department" itemValue="id" cssClass="sSelect"/>
		<form:errors path="ministry" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="${urlPattern}.role" text="Status"/></label>
		<form:select path="role" items="${roles}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="role" cssClass="validationError"/>	
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