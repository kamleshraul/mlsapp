<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="prashnavali" text="Prashnavali"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	</script>
</head>
<body>
<div class="fields clearfix">
<form>
	<h2><spring:message code="generic.view.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${id}]
	</h2>
	
	<p>
		<label class="small"><spring:message code="prashnavali.name" text="Prashnavali Name" /></label>
		<input type="text" id="prashnavaliName" name="prashnavaliName" value="${prashnavaliName}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="prashnavali.participantName" text="Participant Name" /></label>
		<input type="text" id="createDate" name="createDate" value="${createDate}" class="sText" readonly="readonly"/>
	</p>
	
	<!-- Table displaying questions -->
	<c:if test="${not empty questions}">
		<label class="small"><spring:message code="prashnavali.questions" text="Prashnavali"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="prashnavali.question" text="Question"/></th>
				<th><spring:message code="prashnavali.question" text="Answer"/></th>
			</tr>
			<c:set var="qCount" value="1"></c:set>
			<c:forEach items="${questions}" var="q">
				<tr>
					<td>${q.question}</td>
					<td>${q.answer}</td>
				</tr>
				<c:set var="qCount" value="${qCount + 1}"></c:set>
			</c:forEach>
		</table>
	</c:if>	
	
</form>
</div>
</body>
</html>