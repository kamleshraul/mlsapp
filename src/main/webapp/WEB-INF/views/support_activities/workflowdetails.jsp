<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.revisions" text="Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		.uiTable{width:380px;}
		td{min-width:100px; max-width:200px;min-height:50px;}
		th{min-width:100px; max-width:200px;min-height:50px;}
	</style>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty WorkFlowDetailsVO) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="memberwise.status" text=" Status "/></th>
					<th><spring:message code="generic.serialnumber" text=" number"/></th>
					<th><spring:message code="mytask.assignee" text=" Assignee "/></th>
					<th><spring:message code="generic.Level" text=" Level "/></th>
					<th><spring:message code="generic.usergroup" text=" usergroup "/></th>
					<th>Assignment Time</th>
					<th> Completion Time </th>
					<th><spring:message code="generic.InternalStatus" text="Internal Status"/></th>
					<th><spring:message code="generic.RecommendStatus" text="RecommendStatus"/></th>
					<th> Workflow Sub Type </th>
				</tr>
				
				<c:forEach items="${WorkFlowDetailsVO}" var="i">
					<tr>
						<td>${i.status}</td>
						<td>${i.deviceNumber}</td>
						<td>${i.assignee}</td>
						<td>${i.assigneeLevel}</td>
 						<td>${i.assigneeUserGroupName }</td>	
 						<td>${i.assignmentTime }</td>
 						<td>${i.completionTime }</td>
 						<td>${i.internalStatus }</td>
 						<td>${i.recommendationStatus }</td>
 						<td >${i.workflowSubType }</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			No Workflow Details Found
		</c:otherwise>
	</c:choose>
</body>
</html>