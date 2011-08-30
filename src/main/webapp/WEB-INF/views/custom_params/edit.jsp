<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>Edit - Custom Parameter</title>
</head>
<body>
<form:form cssClass="wufoo" action="custom_params" method="PUT" modelAttribute="customParameter">
	<div class="info">
		<h2>Custom Parameter [Id:${customParameter.id}]</h2>
		<div style="background-color:#C1CDCD; ;padding: 3px">Note: Fields marked * are mandatory</div>
	</div>
	<ul>
		<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error">Please correct the following errors</p>
			</c:if>
		</li>
	
		<li>
		<label class="desc">Id</label>
			<div>
				<form:input cssClass="field text small" path="id" readonly="true" /> 
			</div>
		</li>
		<li>
		<label class="desc">Name&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="name"/><form:errors path="name" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Value&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="value"/><form:errors path="value" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Updateable&nbsp;*</label>
			<div>
				<form:checkbox path="updateable" id="updateable"/><form:errors path="updateable" cssClass="field_error" />
			</div>
		</li>
		<li>
		<label class="desc">Description&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="description" cols="28" rows="3"/>
			</div>
		</li>
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="Submit" />
		</li>
	</ul>		
</form:form>
</body>
</html>