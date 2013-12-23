<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmission" text="Bulk Submissions" /></title>
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
	<table class="uiTable" style="width: 100%">
		<tr>
			<th><spring:message code="resolution.number" text="Number"></spring:message></th>
			<th><spring:message code="resolution.subject" text="Subject"></spring:message></th>
		</tr>			
		<c:forEach items="${resolutions}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<c:choose>
					<c:when test="${i.subject==null or i.subject=='' }">
						<td>${i.noticeContent}</td>
					</c:when>
					<c:otherwise>
						<td>${i.subject}</td>
					</c:otherwise>
				</c:choose>
				
			</tr>
		</c:forEach>
	</table>	
</body>
</html>