<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="${urlPattern}" text="Fields"/>
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
<form:form action="${urlPattern}" method="POST" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<p> 
		<label class="small"><spring:message code="${urlPattern}.name" text="Name"/></label>
		<form:input cssClass="sSelect" path="name"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>
	<p> 
		<label class="small"><spring:message code="${urlPattern}.detail" text="Brief Description"/></label>
		<form:input cssClass="sSelect" path="detail"/>
		<form:errors path="detail" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.mandatory" text="Is Mandatory?"/></label>
		<form:checkbox cssClass="sSelect" path="mandatory" value="MANDATORY"/>
		<form:errors path="mandatory" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.visible" text="Is Visible?"/></label>
		<form:checkbox cssClass="sSelect" path="visible" value="VISIBLE"/>
		<form:errors path="visible" cssClass="validationError"/>	
	</p>
	<p> 
		<label class="small"><spring:message code="${urlPattern}.position" text="Position on page"/></label>
		<form:input cssClass="integer sSelect" path="position"/>
		<form:errors path="position" cssClass="validationError"/>	
	</p>
	<p> 
		<label class="small"><spring:message code="${urlPattern}.hint" text="Hint"/></label>
		<form:input cssClass="sSelect" path="hint"/>
		<form:errors path="hint" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.form" text="Form Name"/></label>
		   <form:select path="form" cssClass="sSelect">
		   		<form:option value="MIS.PERSONAL"></form:option>
		   		<form:option value="MIS.OTHER"></form:option>
		   		<form:option value="MIS.CONTACT"></form:option>		   
		   </form:select>
		   <form:errors path="form" cssClass="validationError" />				
		
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