<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="field.new.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="fields" method="PUT" modelAttribute="field">
	<div class="info">
		<h2><spring:message code="field.new.heading"/></h2>
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul>
		<li>
		<label class="desc">Id</label>
			<div>
				<form:input cssClass="field text medium" path="id" readonly="true" /> 
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
		<label class="desc"><spring:message code="field.name"/>&nbsp;*</label>
			<div>
		   <form:input path="name" cssClass="field text medium" ></form:input><form:errors path="name" cssClass="field_error" />			
		    </div>
		</li>	
		
		<li>
		<label class="desc"><spring:message code="field.detail"/>&nbsp;*</label>
			<div>
		   <form:input path="detail" cssClass="field text medium"></form:input><form:errors path="detail" cssClass="field_error" />			
		    </div>
		</li>	
			
		<li>
		<label class="desc"><spring:message code="field.mandatory"/>&nbsp;*</label>
			<div>
				<form:checkbox cssClass="checkbox" path="mandatory" value="MANDATORY"/><form:errors path="mandatory" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="field.visible"/>&nbsp;*</label>
			<div>
				<form:checkbox cssClass="checkbox" path="visible" value="VISIBLE"/><form:errors path="visible" cssClass="field_error" />	
			</div>
		</li>	
		
		<li>
		<label class="desc"><spring:message code="field.position"/>&nbsp;*</label>
			<div>
				<form:input cssClass="integer" path="position" /><form:errors path="position" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="field.form"/>&nbsp;*</label>
			<div>
		   <form:input path="form" cssClass="field text medium"></form:input><form:errors path="form" cssClass="field_error" />				
		    </div>
		</li>	
				
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" />
		</li>
		<form:hidden path="id"/>
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>