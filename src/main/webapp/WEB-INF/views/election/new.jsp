<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="election" text="Election"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">

	</script>
</head>
<body>
<div class="fields clearfix">
<form:form  action="election" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id">
		</spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="election.electionType" text="Election Type"/></label>
		<form:select path="electionType" items="${electionTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="electionType" cssClass="validationError"/>		
	</p>
	<p> 
		<label class="small"><spring:message code="election.name" text="Election"/></label>
		<form:input cssClass="sSelect" path="name"/>
		<form:errors path="name" cssClass="validationError" />	
	</p>
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="datemask sText"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="datemask sText"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<form:hidden path="id" />
	<form:hidden path="locale" />
	<form:hidden path="version" />
</form:form>
</div>
</body>
</html>