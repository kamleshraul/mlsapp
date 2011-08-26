<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
</head>
<body>
<form:form cssClass="wufoo" action="menus" method="POST" modelAttribute="menuItem">
	<div class="info">
		<h2>Menu [Id:New]</h2>
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
		<label class="desc">Locale&nbsp;*</label>
			<div>
				<form:select cssClass="field select addr" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
		<li>
		<label class="desc">Text Key&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="textKey"/><form:errors path="textKey" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Text&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="text"/><form:errors path="text" cssClass="field_error" />
			</div>
		</li>
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="Submit" />
		</li>
	</ul>		
</form:form>
</body>
</html>