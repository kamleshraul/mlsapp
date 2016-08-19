<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.bulksubmission" text="Bulk Submissions" /></title>
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
	<table class="uiTable">
		<tr>
			<th><spring:message code="question.number" text="Number"></spring:message></th>
			<th><spring:message code="question.member" text="Member"></spring:message></th>
			<th><spring:message code="question.subject" text="Subject"></spring:message></th>
			<th><spring:message code="question.putupfor" text="Put Up For"></spring:message></th>
			<th><spring:message code="question.sendto" text="Send To"></spring:message></th>			
		</tr>			
		<c:forEach items="${questions}" var="i">
			<tr>
				<td>${i.formatNumber()}</td>
				<td>${i.primaryMember.getFullname()}</td>
				<td>${i.subject}</td>
				<td>${i.internalStatus.name}</td>
				<td>${i.localizedActorName}</td>
			</tr>
		</c:forEach>
	</table>	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>