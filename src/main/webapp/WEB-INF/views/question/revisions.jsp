<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.revisions" text="Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		.uiTable{width:780px;}
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty drafts) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="question.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="question.remark" text="Remarks"></spring:message></th>
					<c:if test="${selectedDeviceType!='questions_halfhourdiscussion_from_question'}">
						<th><spring:message code="question.subject" text="Subject"></spring:message></th>
						<th><spring:message code="question.question" text="Question"></spring:message></th>
					</c:if>
					
					<c:if test="${selectedDeviceType=='questions_halfhourdiscussion_from_question'}">
						<th><spring:message code="question.reason" text="Reason"></spring:message></th>
						<th><spring:message code="question.briefexplanation" text="Brief Explanantion"></spring:message></th>
					</c:if>
				</tr>
				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<%-- <td>${i.editedAs}</td>
						<td>${i.status}</td>
						<td>${i.remarks}</td>
						<td>${i.editedOn}</td>
 --%>
 						<td>${i.editedAs}<br>(${i.editedBY}-${i.editedOn})<br>${i.status}</td>
 						<td>${i.remarks}</td>
						<c:if test="${selectedDeviceType!='questions_halfhourdiscussion_from_question'}">
							<td>${i.subject}</td>
							<td>${i.details}</td>
						</c:if>
						
						<c:if test="${selectedDeviceType=='questions_halfhourdiscussion_from_question'}">
							<td>${i.reason}</td>
							<td>${i.briefExplanation}</td>
						</c:if>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="question.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>