<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="message"	text="Edit Message Resource" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
	});
</script>
</head>
<body>
<div class="fields clearfix vidhanmandalImg">
		<form:form action="message" method="PUT"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.edit.heading" text="Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:${domain.id}]
			</h2>			
			<c:set var="codeErrors">
				<form:errors path="code" />
			</c:set>
			<p <c:if test="${not empty codeErrors}">class="error"</c:if>>
				<label class="small"><spring:message
						code="message.code" text="Code" />&nbsp;*</label>
				<form:input cssClass="sText large" path="code" />
				<span><form:errors path="code" /></span>
			</p>
			<c:set var="valueErrors">
				<form:errors path="value" />
			</c:set>
			<p <c:if test="${not empty valueErrors}">class="error"</c:if>>
				<label class="small"><spring:message
						code="message.text" text="Text" />&nbsp;*</label>
				<form:input cssClass="sText large" path="value" />
				<span><form:errors path="value" /></span>
			</p>
			<p>
				<label class="small">
				<spring:message code="message.description" text="Description"/>
				</label>					
				<form:textarea cssClass="sTextarea" path="description"
					rows="5" cols="30" />
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
						
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>