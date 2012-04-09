
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="department" text="Departments"/>
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
<form:form action="department" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
	<label class="small"><spring:message code="department.ministry" text="Ministry"/>&nbsp;*</label>
		<form:select path="ministry" items="${ministry}" itemValue="id" itemLabel="name" cssClass="sSelect">
	    </form:select>			
	    <form:errors path="ministry" cssClass="validationError"/>	
	</p>
	<p>
	<label class="small"><spring:message code="department.name" text="Election Type"/>&nbsp;*</label>
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