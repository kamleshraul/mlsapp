<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.revisions"
	text="Revisions" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<c:choose>
<c:when test="${!(empty drafts) }">
<table class="uiTable">
<tr>
<th><spring:message code="question.revisedas" text="Revised As"></spring:message></th>
<th><spring:message code="question.decision" text="Decision"></spring:message></th>
<th><spring:message code="question.remark" text="Remarks"></spring:message></th>
<th><spring:message code="question.subject" text="Subject"></spring:message></th>
<th><spring:message code="question.question" text="Question"></spring:message></th>
<th><spring:message code="question.revisedby" text="Revised By"></spring:message></th>
<th><spring:message code="question.revisedon" text="Revised On"></spring:message></th>
</tr>
<c:forEach items="${drafts}" var="i">
<tr>
<td>${i.editedAs}</td>
<td>${i.status}</td>
<td>${i.remarks}</td>
<td>${i.subject}</td>
<td>${i.question}</td>
<td>${i.editedBY}</td>
<td>${i.editedOn}</td>
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