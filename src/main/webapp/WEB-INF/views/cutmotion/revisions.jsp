<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.revisions" text="Revisions" /></title>
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
	<c:choose>
		<c:when test="${!(empty drafts) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="cutmotion.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="cutmotion.decision" text="Decision"></spring:message></th>
					<th><spring:message code="cutmotion.remark" text="Remarks"></spring:message></th>
					<th><spring:message code="cutmotion.revisedon" text="Revised On"></spring:message></th>
					<th><spring:message code="cutmotion.mainTitle" text="Main Title"></spring:message></th>
					<th><spring:message code="cutmotion.noticeContent" text="Content"></spring:message></th>
					<th><spring:message code="cutmotion.revisedby" text="Revised By"></spring:message></th>
				</tr>
				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<td>${i.editedAs}</td>
						<td>${i.status}</td>
						<td>${i.remarks}</td>
						<td>${i.editedOn}</td>
						<td>${i.subject}</td>
						<td>${i.details}</td>
						<td>${i.editedBY}</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="cutmotion.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>