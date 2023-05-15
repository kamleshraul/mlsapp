<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="message"
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix ">
		<form:form action="apitoken" method="POST"
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
				<label class="small"><spring:message code="jwtTokens.ClientName"
						text="Code" />&nbsp;*</label>
				<form:input cssClass="sText medium" path="clientName" />
				<span><form:errors path="clientName" /></span>
			</p>
			
			
			
			<p>
				<label class="small"><spring:message
						code="genric.fromdate" text="From date" />&nbsp;*</label>
				<form:input id="fromDate" cssClass="datemask sText" path="fromDate" />
				<form:errors path="fromDate" cssClass="validationError" />

			</p>
			
			
			<p>
				<label class="small"><spring:message
						code="generic.toDate" text="To date" />&nbsp;*</label>
				<form:input id="toDate" cssClass="datemask sText" path="toDate" />
				<form:errors path="toDate" cssClass="validationError" />

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
			<form:hidden path="id" />
			<form:hidden path="locale" />
			<form:hidden path="version" />
		</form:form>
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>