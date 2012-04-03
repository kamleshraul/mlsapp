
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="division" text="Divisions"/>
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
<form:form action="division" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
	<label class="small"><spring:message code="division.state" text="State"/>&nbsp;*</label>
		<form:select path="state" items="${states}" itemValue="id" itemLabel="name" cssClass="sSelect">
	    </form:select>			
	    <form:errors path="state" cssClass="validationError"/>	
	</p>
	<p>
	<label class="small"><spring:message code="division.name" text="Name"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
	</p>
	<div class="fields">
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>