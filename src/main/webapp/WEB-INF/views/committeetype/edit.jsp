<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeeType" text="Committee Type"/>
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
<form:form action="committeetype" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
		<label class="small"><spring:message code="committeetype.houseType" text="House Type" />*</label>
		<form:select path="houseType" items="${houseTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="houseType" cssClass="validationError"/>
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeetype.name" text="Name"/>*</label>
		<form:input path="name" cssClass="sText"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeetype.type" text="Type"/></label>
		<form:input path="type" cssClass="sText"/>
		<form:errors path="type" cssClass="validationError"/>	
	</p>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>