<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assemblyroles.new.title"/></title>
</head>
<body>
<form:form cssClass="wufoo" action="assembly_roles" method="POST" 
	modelAttribute="assemblyRole">
	<div class="info">
		<h2><spring:message code="assemblyroles.new.heading"/></h2>
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
	<label class="desc"><spring:message code="generic.locale"/></label>
		<div>
			<form:select cssClass="field select addr" path="locale"> 
				<form:option value="en">
					<spring:message code="generic.lang.english"/>
				</form:option>
				<form:option value="hi_IN">
					<spring:message code="generic.lang.hindi"/>
				</form:option>
				<form:option value="mr_IN">
					<spring:message code="generic.lang.marathi"/>
				</form:option>
			</form:select>
		</div>
	</li>
		
	<li>
	<label class="desc"><spring:message code="assemblyroles.name"/>&nbsp;*</label>
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