<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="sessiontype" text="Session Types" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</script>
</head>
<body>

	<div class="fields clearfix">
		<form:form action="sessiontype" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<form:errors path="version" cssClass="validationError" />
			<p>
				<label class="small"><spring:message
						code="sessiontype.sessionType" text="Session type" />&nbsp;*</label>
				<form:input cssClass="sText" path="sessionType" />
				<form:errors path="sessionType" cssClass="validationError" />
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef"> 
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>