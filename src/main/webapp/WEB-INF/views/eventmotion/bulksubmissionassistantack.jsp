<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="eventmotion.bulksubmission" text="Bulk Submissions" /></title>
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
			<th><spring:message code="eventmotion.number" text="Number"></spring:message></th>
			<th><spring:message code="eventmotion.member" text="Member"></spring:message></th>
			<th><spring:message code="eventmotion.subject" text="Event Title"></spring:message></th>
			<th><spring:message code="eventmotion.putupfor" text="Put Up For"></spring:message></th>
			<th><spring:message code="eventmotion.sendto" text="Send To"></spring:message></th>			
		</tr>			
		<c:forEach items="${motions}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<td>
					<c:choose>
						<c:when test="${i.member!=null}">
							${i.member.getFullname()}
						</c:when>
						<c:when test="${not(empty i.exMember)}">
							${i.exMember}
						</c:when>
					</c:choose>
				</td>
				<td>${i.eventTitle}</td>
				<td>${i.internalStatus.name}</td>
				<td>${i.localizedActorName}</td>
			</tr>
		</c:forEach>
	</table>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
</body>
</html>