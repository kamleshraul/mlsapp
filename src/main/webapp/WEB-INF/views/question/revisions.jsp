<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.revisions"
	text="Revisions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style>
td{min-width:150px; max-width:350px;min-height:30px;}
th{min-width:150px; max-width:350px;min-height:30px;}
</style>
</head>
<body>
<c:choose>
<c:when test="${!(empty drafts) }">
<table class="uiTable">
<tr>
<th><spring:message code="question.revisedas" text="Revised As"></spring:message></th>
<th><spring:message code="question.decision" text="Decision"></spring:message></th>
<th><spring:message code="question.remark" text="Remarks"></spring:message></th>
<th><spring:message code="question.revisedon" text="Revised On"></spring:message></th>
<c:if test="${selectedDeviceType!=questions_halfhourdiscussion_from_question}">
	<th><spring:message code="question.subject" text="Subject"></spring:message></th>
	<th><spring:message code="question.question" text="Question"></spring:message></th>
</c:if>
<c:if test="${selectedDeviceType==questions_halfhourdiscussion_from_question}">
	<th><spring:message code="question.reason" text="Reason"></spring:message></th>
	<th><spring:message code="question.briefexplanation" text="Brief Explanantion"></spring:message></th>
</c:if>
<th><spring:message code="question.revisedby" text="Revised By"></spring:message></th>
</tr>
<c:forEach items="${drafts}" var="i">
<tr>
<td>${i.editedAs}</td>
<td>${i.status}</td>
<td>${i.remarks}</td>
<td>${i.editedOn}</td>
<c:if test="${selectedDeviceType!=questions_halfhourdiscussion_from_question}">
<td>${i.subject}</td>
<td>${i.question}</td>
</c:if>
<c:if test="${selectedDeviceType==questions_halfhourdiscussion_from_question}">
<td>${i.reason}</td>
<td>${i.briefExplanation}</td>
</c:if>
<td>${i.editedBY}</td>
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