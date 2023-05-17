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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix ">
		<form:form action="message" method="PUT"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.edit.heading" text="Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:${domain.id}]
			</h2>	
			<form:errors path="version" cssClass="validationError" />
			
			<p>
				<label class="small"><spring:message code="jwtTokens.ClientName"
						text="Code" />&nbsp;*</label>
				<form:input cssClass="sText medium" path="subUrl" />
				<span><form:errors path="subUrl" /></span>
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
			
			<p>
				<label class="small"><spring:message
						code="generic.Token" text="Token " />&nbsp;*</label>
				
				<form:textarea  id="token" cssClass=" sText large" path="token" rows="4" cols="50" readonly="true" style="width:500px; height:100px" />
				<form:errors path="token" cssClass="validationError" />

			</p>		
		
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>