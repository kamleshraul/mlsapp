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
					<th><spring:message code="part.revisedas" text="Revised By"></spring:message></th>
					<%-- <th><spring:message code="part.revisedOn" text="Revised On"></spring:message></th> --%>
					<th><spring:message code="part.Content" text="Revised Content"></spring:message></th>
				</tr>
				
				<c:forEach items="${drafts}" var="i">
					<tr>
 						<td>${i.editedBY} <br> ${i.editedOn}</td>
						<td>${i.details}</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="proceeding.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>