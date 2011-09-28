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
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
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
		<label class="desc"><spring:message code="assembly.assemblyNumber"/>&nbsp;*</label>
			<div>
				<form:select path="assemblyNumber" items="${assemblyNumbers}" itemValue="id" itemLabel="assemblyNo" >
	            </form:select>			
	        </div>
		</li>		
		<li>
		<label class="desc"><spring:message code="assembly.strength"/>&nbsp;*</label>
			<div>
				<form:input cssClass="integer field text medium" path="strength" size="50"/><form:errors path="strength" cssClass="field_error" />	
			</div>
		</li>	
		<li>
		<label class="desc"><spring:message code="assembly.term"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="term" size="50"/><form:errors path="term" cssClass="field_error" />	
			</div>
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.budgetSession"/>&nbsp;</label>
		<div>
				<form:checkbox cssClass="field text medium" path="budgetSession" value="true" /><form:errors path="budgetSession" cssClass="field_error" />
		</div>	
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.monsoonSession"/>&nbsp;</label>
		<div>
				<form:checkbox cssClass="field text medium" path="monsoonSession" value="true" /><form:errors path="monsoonSession" cssClass="field_error" />
		</div>	
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.winterSession"/>&nbsp;</label>
		<div>
				<form:checkbox cssClass="field text medium" path="winterSession" value="true" /><form:errors path="winterSession" cssClass="field_error" />
		</div>	
		</li>	
		<li>	
		<label class="desc"><spring:message code="assembly.specialSession"/>&nbsp;</label>
		<div>
				<form:checkbox cssClass="field text medium" path="specialSession" value="true" /><form:errors path="specialSession" cssClass="field_error" />
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