<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="adjournmentmotion.supportingmember"
	text="Supporting Members and Their Decisions " /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<c:choose>
<c:when test="${!(empty supportingMembers) }">
<table class="uiTable">
<tr>
<th><spring:message code="adjournmentmotion.supportingMembers" text="Supporting Member"></spring:message></th>
<th><spring:message code="adjournmentmotion.status" text="Status"></spring:message></th>
</tr>
<c:forEach items="${supportingMembers}" var="i">
<tr>
<td>${i.member.getFullname()}</td>
<td>${i.decisionStatus.name}</td>
</tr>
</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="adjournmentmotion.nosupportingmembers" text="This adjournment motion doesnot have any supporting members"></spring:message>
</c:otherwise>
</c:choose>

</body>
</html>