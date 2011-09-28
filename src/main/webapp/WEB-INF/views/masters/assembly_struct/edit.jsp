<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assemblystruct.edit.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="assembly_struct" method="PUT" 
	modelAttribute="assemblyStructure">
	<div class="info">
		<h2><spring:message code="assemblystruct.edit.heading"/> [Id:${assemblyStructure.id}]</h2>
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
			<c:if test="${isvalid eq true}">
			<p class="field_error"><spring:message code="generic.update_success.label"/></p>
		</c:if>
		
	</li>
	
	<li>
	<label class="desc"><spring:message code="generic.id"/></label>
		<div>
			<form:input cssClass="field text small" path="id" readonly="true" /> 
		</div>
	</li>
	
	<li>
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
	
	
	<li>
	<label class="desc"><spring:message code="assemblystruct.name"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium" path="name"/>
			<form:errors path="name" cssClass="field_error" />	
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