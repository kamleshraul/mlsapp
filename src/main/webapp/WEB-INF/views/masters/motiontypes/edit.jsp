<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="master.locale" text="Supported Locales"/>
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
<form:form  action="masters_motiontype" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>			 
		<p> 
			<label class="small"><spring:message code="masters.locale.language" text="Language"/></label>
			<form:input cssClass="sSelect" path="language"/>
			<form:errors path="language" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="masters.locale.country" text="Country"/></label>
			<form:input cssClass="sSelect" path="country"/>
			<form:errors path="country" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="masters.locale.variant" text="Variant"/></label>
			<form:input cssClass="sSelect" path="variant"/>
			<form:errors path="variant" cssClass="validationError"/>	
		</p>		
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>	
</form:form>
</div>	
</body>
</html>