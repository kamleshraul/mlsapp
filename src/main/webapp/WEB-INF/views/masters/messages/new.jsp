<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="messageResource.new.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="messages" method="POST" 
	modelAttribute="messageResource">
	<div class="info">
		<h2><spring:message code="messageResource.new.heading"/></h2>
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
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en"><spring:message code="generic.lang.english"/></form:option>
					<form:option value="hi_IN"><spring:message code="generic.lang.hindi"/></form:option>
					<form:option value="mr_IN"><spring:message code="generic.lang.marathi"/></form:option>
				</form:select>
			</div>
		</li>
	
	<li>
	<label class="desc"><spring:message code="messageResource.code"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium" path="code"/>
			<form:errors path="code" cssClass="field_error" />	
		</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="messageResource.text"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium" path="value"/>
			<form:errors path="value" cssClass="field_error" />
		</div>
	</li>
	
	<li>
	<label class="desc"><spring:message code="messageResource.description"/></label>
		<div>
			<form:textarea cssClass="field textarea small" path="description" cols="28" rows="3"/>
		</div>
	</li>
	
	<li class="buttons">
		<input id="saveForm" class="btTxt" type="submit" 
			value="<spring:message code="generic.submit"/>" />
	</li>
	<form:hidden path="id"/>
	
	<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>