<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="generic.supportingmember" text="Supporting Members and Their Decisions " /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty supportingMembers) }">
			<table class="uiTable">
				<tr>
					<th><spring:message code="generic.supportingMembers" text="Supporting Member"></spring:message></th>
					<th><spring:message code="generic.status" text="Status"></spring:message></th>
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
			<spring:message code="generic.nosupportingmembers" text="This notice doesnot have any supporting members"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>