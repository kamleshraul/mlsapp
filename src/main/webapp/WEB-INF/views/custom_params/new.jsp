<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="customparams.new.title"/></title>
</head>
<body>
<form:form cssClass="wufoo" action="custom_params" method="POST" 
	modelAttribute="customParameter">
	<div class="info">
		<h2><spring:message code="customparams.new.heading"/></h2>
		<div style="background-color:#C1CDCD; ;padding: 3px">
			<spring:message code="generic.mandatory.label"/>
		</div>
	</div>
	<ul>
	<li class="section first">
		<c:if test="${isvalid eq false}">
			<p class="field_error">
				<spring:message code="generic.error.label"/>
			</p>
		</c:if>
	</li>
	
	<li>
	<label class="desc"><spring:message code="generic.id"/></label>
		<div>
			<form:input cssClass="field text small" path="id" readonly="true" /> 
		</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="customparams.name"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium" path="name"/>
			<form:errors path="name" cssClass="field_error" />	
	</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="customparams.value"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium" path="value"/>
			<form:errors path="value" cssClass="field_error" />	
		</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="customparams.updateable"/></label>
		<div>
			<form:checkbox path="updateable" id="updateable"/>
			<form:errors path="updateable" cssClass="field_error" />
		</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="customparams.description"/></label>
		<div>
			<form:textarea cssClass="field textarea small" path="description" cols="28" rows="3"/>
		</div>
	</li>
	<li class="buttons">
		<input id="saveForm" class="btTxt" type="submit" 
			value="<spring:message code="generic.submit"/>" />
	</li>
		
	<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>