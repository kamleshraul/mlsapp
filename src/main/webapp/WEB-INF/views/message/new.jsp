<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="${urlPattern}"
		text="Add Message Resource" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
	});
</script>
</head>
<body>
	<div class="fields clearfix">
		<form:form action="${urlPattern}" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<c:set var="codeErrors">
				<form:errors path="code" />
			</c:set>
			<p <c:if test="${not empty codeErrors}">class="error"</c:if>>
				<label class="small"><spring:message code="${urlPattern}.code"
						text="Code" />&nbsp;*</label>
				<form:input cssClass="sText large" path="code" />
				<span><form:errors path="code" /></span>
			</p>
			<c:set var="valueErrors">
				<form:errors path="value" />
			</c:set>
			<p <c:if test="${not empty valueErrors}">class="error"</c:if>>
				<label class="small"><spring:message code="${urlPattern}.text"
						text="Text" />&nbsp;*</label>
				<form:input cssClass="sText large" path="value" />
				<span><form:errors path="value" /></span>
			</p>
			<div class="fields">
				<h2>
					<spring:message code="${urlPattern}.description" text="Description" />
				</h2>
				<form:textarea cssClass="wysiwyg sTextarea" path="description"
					rows="10" cols="78" />
			</div>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="locale" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>