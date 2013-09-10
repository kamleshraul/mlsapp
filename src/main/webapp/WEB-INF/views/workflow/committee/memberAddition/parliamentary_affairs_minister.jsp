<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="workflow/committee" method="PUT" modelAttribute="committeeVOs">
	<%@ include file="/common/info.jsp" %>
	
	<h2>
		<spring:message code="committee.requestToParliamentaryMinister" text="Request to Parliamentary Minister"/>
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<table class="uiTable" border="1">
		<tr>
			<th rowspan="2"><spring:message code="committee.committees" text="Committees"/></th>
			<th rowspan="2"><spring:message code="committee.maximumMembers" text="Maximum Members"/></th>
			<!-- 1 is added to incorporate a "Total" column -->
			<th colspan="${fn:length(rulingParties) + 1}"><spring:message code="committee.rulingParty" text="Ruling Party"/></th>
			<!-- 1 is added to incorporate a "Total" column -->
			<th colspan="${fn:length(oppositionParties) + 1}"><spring:message code="committee.oppositionParty" text="Opposition Party"/></th>
			<th rowspan="2"><spring:message code="committee.members" text="Members"/></th>
			<th rowspan="2"><spring:message code="committee.chairman" text="Chairman"/></th>
		</tr>
		
		<tr>
			<c:forEach items="${rulingParties}" var="i">
				<th>${i.shortName}</th>
			</c:forEach>
			<th><spring:message code="generic.total" text="Total"/></th>
			
			<c:forEach items="${oppositionParties}" var="i">
				<th>${i.shortName}</th>
			</c:forEach>
			<th><spring:message code="generic.total" text="Total"/></th>
		</tr>
		
		<tr>
			
		</tr>
	</table>
		
</form:form>
</div>
</body>
</html>