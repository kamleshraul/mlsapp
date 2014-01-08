<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="language" text="Languages"/>
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form action="language" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
		<label class="small"><spring:message code="language.name" text="Name" />&nbsp;*</label>
		<form:input cssClass="sText " path="name" />
		<form:errors path="name" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="language.type" text="Type" />&nbsp;*</label>
		<form:input cssClass="sText " path="type" />
		<form:errors path="type" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="language.priority" text="Priority" />&nbsp;*</label>
		<form:input cssClass="sText Integer" path="priority" />
		<form:errors path="priority" cssClass="validationError" />
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
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>