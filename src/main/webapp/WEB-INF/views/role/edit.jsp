<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="role" text="User Role"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);
	});		
</script>
</head>
<body>

<div class="fields clearfix">
<form:form action="role" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="role.name" text="User Role"/>&nbsp;*</label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
	</p>
	<p>
				<label class="small"><spring:message
						code="role.type" text="Role" />&nbsp;*</label>
				<form:input cssClass="sText " path="type" />
				<form:errors path="type" cssClass="validationError" />
			</p>
	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<input type="hidden" id="key" name="key">
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>