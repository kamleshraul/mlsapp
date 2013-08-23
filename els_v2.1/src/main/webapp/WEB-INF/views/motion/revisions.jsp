<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="motion.revisions" text="Revisions" /></title>
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
					<th><spring:message code="motion.revisedas" text="Revised As"></spring:message></th>
					<th><spring:message code="motion.remark" text="Remarks"></spring:message></th>
					<th><spring:message code="motion.subject" text="Subject"></spring:message></th>
					<th><spring:message code="motion.details" text="Details"></spring:message></th>
				</tr>				
				<c:forEach items="${drafts}" var="i">
					<tr>
						<td>${i.editedAs}<br>(${i.editedBY}-${i.editedOn})<br>${i.status}</td>
						<td>${i.remarks}</td>
						<td>${i.subject}</td>
						<td>${i.details}</td>						
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="motion.norevisions" text="No Revisions Found"></spring:message>
		</c:otherwise>
	</c:choose>
</body>
</html>