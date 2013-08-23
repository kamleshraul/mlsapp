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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${status=='true'}">
<label class="small"><spring:message code="question.referSuccess" text="Referencing successful"></spring:message></label>
</c:when>
<c:when test="${status=='false'}">
<label class="small"><spring:message code="question.referFailed" text="Referencing failed"></spring:message></label>
</c:when>
<c:when test="${status=='true'}">
<label class="small"><spring:message code="question.dereferSuccess" text="Dereferencing successful"></spring:message></label>
</c:when>
<c:when test="${status=='false'}">
<label class="small"><spring:message code="question.dereferFailed" text="Dereferencing failed"></spring:message></label>
</c:when>
<c:otherwise>
</c:otherwise>
</c:choose>
</body>
</html>