<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assembly_term.edit.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="assembly_terms" method="PUT" 
	modelAttribute="assemblyTerm">
	<div class="info">
		<h2><spring:message code="assembly_term.edit.heading"/>[Id:${assemblyTerm.id}]</h2>
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
		
	<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>