<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="${urlPattern}" text="Custom Parameters"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form  action="${urlPattern}" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
		<p> 
			<label class="small"><spring:message code="${urlPattern}.name" text="Name"/></label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="${urlPattern}.value" text="Value"/></label>
			<form:input cssClass="sSelect" path="value"/>
			<form:errors path="value" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="${urlPattern}.updateable" text="Updateable?"/></label>
			<form:checkbox cssClass="sSelect" path="updateable"/>
			<form:errors path="updateable" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="${urlPattern}.description" text="Description"/></label>
			<form:textarea cssClass="sSelect" path="description" rows="5" cols="50"/>
			<form:errors path="description" cssClass="validationError"/>	
		</p>			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>	
</body>
</html>