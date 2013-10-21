<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>	
	<table class="uiTable">
		<tr>
			<th><spring:message code="bill.number" text="Number"></spring:message></th>
			<th><spring:message code="bill.subject" text="Subject"></spring:message></th>
		</tr>			
		<c:forEach items="${bills}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<td>${i.title}</td>
			</tr>
		</c:forEach>
	</table>	
</body>
</html>