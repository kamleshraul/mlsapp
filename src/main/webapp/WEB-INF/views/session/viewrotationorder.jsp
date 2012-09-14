<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.list" text="Edit Session"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
	});
</script>
</head>
<body> 
<div class="commandbar">
</div>
<div class="fields clearfix vidhanmandalImg">
<form:form  method="GET" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
		<table border="0">
			<tr>
				<td>
					<label class="small"><spring:message code="session.houseType" text="House Type"/></label> : ${domain.house.type.name}
				</td>
				<td>
					<label class="small"><spring:message code="session.sessionType" text="Session Type"/></label> : ${domain.type.sessionType}
				</td>
				<td>
					<label class="small"><spring:message code="session.sessionPlace" text="Session Place"/></label> : ${domain.place.place}
				</td>
			</tr>
			<tr>
				<td>
					<label class="small"><spring:message code="session.number" text="Session Number"/></label> : ${domain.number}
				</td>
				<td>
					<label class="small"><spring:message code="session.startDate" text="Session Start Date"/></label> : ${domain.startDate}
				</td>
				<td>
					<label class="small"><spring:message code="session.endDate" text="Session End Date"/></label> :  ${domain.endDate} 
				</td>
			</tr>
		</table>		
		
</form:form>
</div>
</body>
</html>
