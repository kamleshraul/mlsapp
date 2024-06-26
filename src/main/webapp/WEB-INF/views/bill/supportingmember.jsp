<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="bill.supportingmember"
	text="Supporting Members and Their Decisions " /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<c:choose>
<c:when test="${!(empty supportingMembers) }">
<table class="uiTable">
<tr>
<th><spring:message code="bill.supportingMembers" text="Supporting Member"></spring:message></th>
<th><spring:message code="bill.status" text="Status"></spring:message></th>
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
<spring:message code="bill.nosupportingmembers" text="This bill doesnot have any supporting members"></spring:message>
</c:otherwise>
</c:choose>

</body>
</html>