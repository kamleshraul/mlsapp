<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.citation" text="Citations" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">

	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${status=='true'}">
			<label class="small"><spring:message code="question.clubbedSuccess" text="Clubbing Successful"></spring:message></label>
		</c:when>
		
		<c:when test="${status=='false'}">
			<label class="small"><spring:message code="question.clubbedFailed" text="Clubbing Failed"></spring:message></label>
		</c:when>
		
		<c:when test="${clubbingstatus=='true'}">
			<label class="small"><spring:message code="question.unclubbedSuccess" text="Unclubbing Successful"></spring:message></label>
		</c:when>
		
		<c:when test="${clubbingstatus=='false'}">
			<label class="small"><spring:message code="question.unclubbedSuccess" text="Unclubbing Failed"></spring:message></label>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>