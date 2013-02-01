<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
th,td{
text-align: center;
}
</style>
</head>

<body>
<c:choose>
<c:when test="${!(empty report) }">
<h2>${report.member}</h2>
<c:if test="${!(empty report.memberBallotMemberWiseCountVOs)}">
<c:forEach items="${report.memberBallotMemberWiseCountVOs }" var="i">
<strong>${i.statusType}-${i.count}</strong><br>
</c:forEach>
<hr>
<table class="uiTable">
<tr>
<th><spring:message code="memberwise.sno" text="S.No"></spring:message></th>
<th><spring:message code="memberwise.number" text="Question Number"></spring:message></th>
<th><spring:message code="memberwise.subject" text="Subject"></spring:message></th>
<th><spring:message code="memberwise.status" text="Status"></spring:message></th>
<th><spring:message code="memberwise.group" text="Group"></spring:message></th>
</tr>
<c:if test="${!(empty report.memberBallotMemberWiseQuestionVOs)}">
<c:forEach items="${report.memberBallotMemberWiseQuestionVOs }"  var="i">
<tr>
<td>${i.sno}</td>
<td>${i.questionNumber}</td>
<td>${i.questionSubject}</td>
<td>${i.statusType }</td>
<td>${i.groupFormattedNumber}</td>
</tr>
</c:forEach>
</c:if>
</table>
</c:if>
</c:when>
<c:otherwise>
<h2><spring:message code="memberwise.sno" text="S.No"></spring:message></h2>
</c:otherwise>
</c:choose>
</body>
</html>