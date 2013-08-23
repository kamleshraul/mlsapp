<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="locale" text="Supported Locales"/>
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
<form:form  action="locale" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>			 
		<p> 
			<label class="small"><spring:message code="locale.language" text="Language"/></label>
			<form:input cssClass="sSelect" path="language"/>
			<form:errors path="language" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="locale.country" text="Country"/></label>
			<form:input cssClass="sSelect" path="country"/>
			<form:errors path="country" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="locale.variant" text="Variant"/></label>
			<form:input cssClass="sSelect" path="variant"/>
			<form:errors path="variant" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="locale.displayName" text="Display Text"/></label>
			<form:input cssClass="sSelect" path="displayName"/>
			<form:errors path="displayName" cssClass="validationError"/>	
		</p>			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>	
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>