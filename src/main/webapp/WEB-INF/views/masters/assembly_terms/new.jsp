<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assembly_term.new.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="assembly_terms" method="POST" 
	modelAttribute="assemblyTerm">
	<div class="info">
		<h2><spring:message code="assembly_term.new.heading"/></h2>
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
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>		
	<li>
	<label class="desc"><spring:message code="assembly_term.term"/>&nbsp;*</label>
		<div>
			<form:input cssClass="integer field text medium " path="term"/>
			<form:errors path="term" cssClass="field_error" />	
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