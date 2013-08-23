<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="sessiontype" text="Session Types"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>

<div class="fields clearfix vidhanmandalImg">
<form:form action="sessiontype" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="sessiontype.sessionType" text="Session type"/>&nbsp;*</label>
			<form:input cssClass="sText" path="sessionType"/>
			<form:errors path="sessionType" cssClass="validationError"/>	
	</p>
	
	<p>
			<label class="small"><spring:message
						code="sessiontype.type" text="Type" />&nbsp;*</label>
			<form:input cssClass="sText" path="type" />
			<form:errors path="type" cssClass="validationError" />
	</p>
	
	
	<div class="fields expand">
		<h2></h2>
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