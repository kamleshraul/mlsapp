
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="wf" text="Workflow"/>
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

<div class="fields clearfix vidhanmandalImg">
<form:form action="wf" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<p> 
			<label class="small"><spring:message code="wf.deviceTypes" text="Device Type"/></label>
			<form:select path="deviceType" items="${deviceTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="deviceType" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="wf.name" text="Name"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError" />	
		</p>
		<p> 
			<label class="small"><spring:message code="wf.type" text="Type"/></label>
			<form:input cssClass="sText" path="type"/>
			<form:errors path="type" cssClass="validationError" />	
		</p>	
	<div class="fields expand">
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>