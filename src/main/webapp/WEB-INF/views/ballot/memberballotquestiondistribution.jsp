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
<c:when test="${!(empty questionDistributions) }">
<table class="uiTable">
<tr>
<th><spring:message code="memberdistribution.sno" text="S.No"></spring:message></th>
<th><spring:message code="memberdistribution.member" text="Member"></spring:message></th>
<th><spring:message code="memberdistribution.admitted" text="Admitted"></spring:message></th>
<th><spring:message code="memberdistribution.unstarredadmitted" text="Unstarred Admitted"></spring:message></th>
<th><spring:message code="memberdistribution.clarificationneeded" text="Clarification From Member/Fact Finding"></spring:message></th>
<th><spring:message code="memberdistribution.rejection" text="Rejection"></spring:message></th>
</tr>
<c:forEach items="${questionDistributions}"  var="i">
<tr>
<td>${i.sno}</td>
<td>${i.member}</td>
<c:if test="${!(empty questionDistributions.distributions ) }">
<c:forEach items="${questionDistributions.distributions}" var="j">
<c:choose>
<c:when test="${!(empty j.count)}">
${j.count}
</c:when>
<c:otherwise>
-
</c:otherwise>
</c:choose>
</c:forEach>
</c:if>
</tr>
</c:forEach>
</table>
</c:when>
<c:otherwise>
<h2><spring:message code="memberdistribution.noresultsfound" text="No Results Found"></spring:message></h2>
</c:otherwise>
</c:choose>
</body>
</html>