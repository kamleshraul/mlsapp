<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
</head>
<body>
<form:form cssClass="wufoo" action="menus" method="PUT" modelAttribute="menuItem">
	<div class="info">
		<h2><spring:message code="menus.edit.heading"/> [Id:${menuItem.id}]</h2>
		<div style="background-color:#C1CDCD; ;padding: 3px">
		<spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul>
		<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error"><spring:message code="generic.error.label"/></p>
			</c:if>
		</li>
	
		<li>
		<label class="desc"><spring:message code="menus.id"/></label>
			<div>
				<form:input cssClass="field text small" path="id" readonly="true" /> 
			</div>
		</li>
		<li>
		<label class="parent"><spring:message code="menus.parentMenu"/></label>
			<div>
				<form:input cssClass="field text medium" path="parent.text" readonly="true" /><form:errors path="parent.text" cssClass="field_error" />
				<form:hidden path="parent.id"/>	
				<form:hidden path="parent.version"/>
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="menus.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="menus.textKey"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="textKey"/><form:errors path="textKey" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="menus.text"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="text"/><form:errors path="text" cssClass="field_error" />
			</div>
		</li>
		<li>
			<label class="position"><spring:message code="menus.position"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="position"/><form:errors path="position" cssClass="field_error" />	
			</div>
		</li>
		<li>
			<label class="desc"><spring:message code="menus.url"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="url"/><form:errors path="url" cssClass="field_error" />	
			</div>
		</li>
		
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>