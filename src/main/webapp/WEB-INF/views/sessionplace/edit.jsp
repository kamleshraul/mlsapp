<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="sessionplace" text="Session Place"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
</head>
<body>

<div class="fields clearfix">
<form:form action="sessionplace" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="sessionplace.place" text="Session Place"/>&nbsp;*</label>
			<form:input cssClass="sText" path="place"/>
			<form:errors path="place" cssClass="validationError"/>	
	</p>
	
	
	<div class="fields">
		<h2></h2>
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