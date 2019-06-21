<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<table class="uiTable">
		<tr>
			<th><spring:message code="specialmentionnotice.number" text="Number"></spring:message></th>
			<th><spring:message code="specialmentionnotice.member" text="Member"></spring:message></th>
			<th><spring:message code="specialmentionnotice.subject" text="Subject"></spring:message></th>
			<th><spring:message code="specialmentionnotice.putupfor" text="Put Up For"></spring:message></th>
			<th><spring:message code="specialmentionnotice.sendto" text="Send To"></spring:message></th>			
		</tr>			
		<c:forEach items="${resolutions}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<td>${i.member.getFullname()}</td>
				<td>${i.subject}</td>
				<c:if test="${hType=='lowerhouse'}">
					<td>${i.internalStatusLowerHouse.name}</td>
				</c:if>
				<c:if test="${hType=='upperhouse'}">
					<td>${i.internalStatusUpperHouse.name}</td>
				</c:if>
				<td>${i.localizedActorName}</td>
			</tr>
		</c:forEach>
	</table>	
</body>
</html>