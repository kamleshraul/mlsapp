<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>New - Grid</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="grid" method="POST" modelAttribute="grid">
	<div class="info">
		<h2>Grid [Id:New]</h2>
		<div style="background-color:#C1CDCD; ;padding: 3px">Note: Fields marked * are mandatory</div>
	</div>
	<ul>
		<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error">Please correct the following errors</p>
			</c:if>
		</li>
	
		
		<li>
		<label class="desc">Name&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="name"/><form:errors path="name" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Title&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="title"/><form:errors path="title" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Column Names&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="colNames"/><form:errors path="colNames" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Column Model&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="colModel"/><form:errors path="colModel" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Page Size&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text small" path="pageSize"/><form:errors path="pageSize" cssClass="field_error" />
			</div>
		</li>
		<li>
		<label class="desc">Sort Field&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="sortField"/><form:errors path="sortField" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Sort Order&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="sortOrder"/><form:errors path="sortOrder" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Query&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="query"/><form:errors path="query" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Count Query&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="countQuery"/><form:errors path="countQuery" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Width&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text small" path="width"/><form:errors path="width" cssClass="field_error" />
			</div>
		</li>
		<li>
		<label class="desc">Height&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text small" path="height"/><form:errors path="height" cssClass="field_error" />
			</div>
		</li>
		<li>
		<label class="desc">Detail View&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="detailView"/><form:errors path="detailView" cssClass="field_error" />	
			</div>
		</li>
		<li>
		<label class="desc">Localized&nbsp;*</label>
			<div>
				<form:checkbox path="localized" id="localized"/><form:errors path="localized" cssClass="field_error" />
			</div>
		</li>
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="Submit" />
		</li>
		<form:hidden path="id"/>
		
		<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>