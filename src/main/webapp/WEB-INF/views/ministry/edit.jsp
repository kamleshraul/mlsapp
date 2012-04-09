<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="ministry" text="Ministries"/>
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
<form:form  action="ministry" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>			 
		<p> 
			<label class="small"><spring:message code="ministry.name" text="Department"/></label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="ministry.alias" text="Alias"/></label>
			<form:input cssClass="sSelect" path="alias"/>
			<form:errors path="alias" cssClass="validationError"/>
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