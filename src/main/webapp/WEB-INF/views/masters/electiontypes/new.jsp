<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="${urlPattern}" text="Districts"/>
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
<form:form  action="${urlPattern}" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
		<p> 
			<label class="small"><spring:message code="${urlPattern}.houseType" text="Assembly Council Type"/></label>
			<form:select path="houseType" items="${assemblycounciltype}" itemValue="id" itemLabel="type" cssClass="sSelect"></form:select>	
	        <form:errors path="houseType" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="${urlPattern}.electionType" text="Election Type"/></label>
			<form:input cssClass="sSelect" path="electionType"/>
			<form:errors path="electionType" cssClass="validationError" />	
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