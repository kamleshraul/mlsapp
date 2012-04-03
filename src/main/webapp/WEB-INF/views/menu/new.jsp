<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="menu" text="Menus"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();		
	});		
</script>
</head>
<body>
<div class="fields clearfix" style="width:680px;">
<form:form action="menu" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
	<p>
	<label class="small"><spring:message code="menu.parent" text="Parent Menu"/></label>
				<form:input cssClass="sSelect" path="parent.text" readonly="true" /><form:errors path="parent.text" cssClass="validationError" />
				<form:hidden path="parent.id"/>	
				<form:hidden path="parent.version"/>
				<form:hidden path="parent.locale"/>
	</p>		
	<p>
		<label class="small"><spring:message code="menu.textkey" text="Text Key"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="textKey"/>
			<form:errors path="textKey" cssClass="validationError" />	
	</p>
	<p>
		<label class="small"><spring:message code="menu.text" text="Text"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="text"/><form:errors path="text" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="menu.position" text="Position"/>&nbsp;*</label>
			<form:input cssClass="integer sSelect" path="position"/><form:errors path="position" cssClass="validationError" />	
	</p>
	<p>
		<label class="small"><spring:message code="menu.url" text="Url"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="url"/><form:errors path="url" cssClass="validationError" />	
	</p>
	
	<div class="fields" >
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale" />	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>