<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assembly.edit.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	</head>
<body>
<form:form cssClass="wufoo" action="assemblies" method="PUT" modelAttribute="assembly">
	<div class="info">
		<h2><spring:message code="assembly.edit.heading"/>[Id:${assembly.id}]</h2>
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	<ul>
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
		<label class="desc"><spring:message code="assembly.assemblystructure"/>&nbsp;*</label>
			<div>
				<form:select path="assemblyStructure" items="${assemblyStructures}" itemValue="id" itemLabel="name" >
	            </form:select>
		   </div>
		</li>
		<li>
		<label class="desc"><spring:message code="assembly.assembly"/>&nbsp;*</label>
			<div>
				<form:input path="assembly" cssClass="field text medium"></form:input>
	           <form:errors path="assembly" cssClass="field_error"></form:errors>			
	        </div>
		</li>		
		<li>
		<label class="desc"><spring:message code="assembly.strength"/>&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text medium" path="strength"/><form:errors path="strength" cssClass="field_error" />	
			</div>
		</li>	
		<li>
		<label class="desc"><spring:message code="assembly.term"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="term"/><form:errors path="term" cssClass="field_error" />	
			</div>
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.budgetSession"/>&nbsp;*</label>
		<div>
				<form:checkbox cssClass="checkbox" path="budgetSession" value="true" /><form:errors path="budgetSession" cssClass="field_error" />
		</div>	
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.monsoonSession"/>&nbsp;*</label>
		<div>
				<form:checkbox cssClass="checkbox" path="monsoonSession" value="true" /><form:errors path="monsoonSession" cssClass="field_error" />
		</div>	
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.winterSession"/>&nbsp;*</label>
		<div>
				<form:checkbox cssClass="checkbox" path="winterSession" value="true" /><form:errors path="winterSession" cssClass="field_error" />
		</div>	
		</li>	
		
		<li>
		<label class="desc"><spring:message code="assembly.startDate"/>&nbsp;*</label>
			<div>
				<form:input cssClass="date field text medium" path="assemblyStartDate"/><form:errors path="assemblyStartDate" cssClass="field_error" />	
			</div>
		</li>	
		
		<li>
		<label class="desc"><spring:message code="assembly.endDate"/></label>
			<div>
				<form:input cssClass="date field text medium" path="assemblyEndDate"/><form:errors path="assemblyEndDate" cssClass="field_error" />	
			</div>
		</li>	
		
		<li>
		<label class="desc"><spring:message code="assembly.dissolvedOn"/></label>
			<div>
				<form:input cssClass="date field text medium" path="assemblyDissolvedOn"/><form:errors path="assemblyDissolvedOn" cssClass="field_error" />	
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
