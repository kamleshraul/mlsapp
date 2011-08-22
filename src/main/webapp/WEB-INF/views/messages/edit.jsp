<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
	</head>
	<body>
		<form:form cssClass="wufoo" action="messages" method="PUT" modelAttribute="messageResource">
			<div class="info">
				<h2>Message Resource [Id:${messageResource.id}]</h2>
				<div style="background-color: yellow;padding: 3px">Note: Fields marked * are mandatory</div>
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
				<label class="desc">Code&nbsp;*</label>
					<div>
						<form:input cssClass="field text medium" path="code"/><form:errors path="code" cssClass="field_error" />	
					</div>
				</li>
				<li>
				<label class="desc">Text&nbsp;*</label>
					<div>
						<form:input cssClass="field text medium" path="value"/><form:errors path="value" cssClass="field_error" />
					</div>
				</li>
				<li>
				<label class="desc">Description</label>
					<div>
						<form:textarea cssClass="field textarea small" path="description" cols="28" rows="5"/>
					</div>
				</li>
				<li class="buttons">
					<input id="saveForm" class="btTxt" type="submit" value="Submit" />
				</li>
				<form:hidden path="version"/>
			</ul>
		</form:form>
	</body>
</html>