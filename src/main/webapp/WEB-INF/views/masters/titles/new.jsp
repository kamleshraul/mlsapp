<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="titles.new.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="titles" method="POST" 
	modelAttribute="title">
	<div class="info">
		<h2><spring:message code="title.new.heading"/></h2>
		<div style="background-color:#C1CDCD; ;padding: 3px">
			<spring:message code="generic.mandatory.label"/>
		</div>
	</div>
	<ul>
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
	<label class="desc"><spring:message code="title.name"/>&nbsp;*</label>
		<div>
			<form:input cssClass="field text medium " path="name"/>
			<form:errors path="name" cssClass="field_error" />	
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