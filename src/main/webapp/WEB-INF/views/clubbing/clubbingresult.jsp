<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.citation"
	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">

</script>
</head>
<body>
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
</body>
</html>