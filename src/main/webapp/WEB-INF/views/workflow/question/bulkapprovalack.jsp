<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>	
	<h4 id="error_p">&nbsp;</h4>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<table class="uiTable">
		<tr>
			<th><spring:message code="question.number" text="Number"></spring:message></th>
			<th><spring:message code="question.member" text="Member"></spring:message></th>
			<th><spring:message code="question.subject" text="Subject"></spring:message></th>
			<c:choose>
			<c:when test="${workflowSubType=='request_to_supporting_member' }">
			<th><spring:message code="question.decision" text="Decision"></spring:message></th>			
			</c:when>
			<c:otherwise>
			<th><spring:message code="question.putupfor" text="Put Up For"></spring:message></th>
			<th><spring:message code="question.sendto" text="Send To"></spring:message></th>			
			</c:otherwise>
			</c:choose>
		</tr>
		<c:choose>
		<c:when test="${workflowSubType=='request_to_supporting_member' }">
		<c:forEach items="${supportingMembers}" var="i">
			<tr>
				<td>-</td>
				<td>${i.member.getFullname()}</td>
				<td>${i.approvedSubject}</td>
				<td>${i.decisionStatus.name}</td>
			</tr>
		</c:forEach>
		</c:when>
		<c:otherwise>
		<c:forEach items="${motions}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<td>${i.primaryMember.getFullname()}</td>
				<td>${i.subject}</td>
				<td>${i.internalStatus.name}</td>
				<td>${i.localizedActorName}</td>
			</tr>
		</c:forEach>
		</c:otherwise>
		</c:choose>				
	</table>	
</body>
</html>