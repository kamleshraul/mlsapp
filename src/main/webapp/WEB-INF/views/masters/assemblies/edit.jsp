<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="assembly.edit.title" text="Edit Assembly"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
	</head>
<body>
<div class="fields clearfix">
<form:form action="${urlPattern}" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
		<p>
		<label class="small"><spring:message code="${urlPattern}.assemblystructure" text="Structure"/>&nbsp;*</label>
			
				<form:select cssClass="sSelect" path="assemblyStructure" items="${assemblyStructures}" itemValue="id" itemLabel="name" >
	            </form:select>
		   
		</p>
		<p>
		<label class="small"><spring:message code="${urlPattern}.assembly" text="Assembly"/>&nbsp;*</label>
			
				<form:input path="assembly" cssClass="sText"></form:input>
	           <form:errors path="assembly" cssClass="validationError"></form:errors>			
	        
		</p>
		<p>
		<label class="small"><spring:message code="${urlPattern}.assemblyNumber" text="Assembly Number"/>&nbsp;*</label>
			
				<form:input path="assemblyNumber" cssClass="sText"></form:input>
				<label class="small"><spring:message
						code="${urlPattern}.assemblyNumberExample" text="e.g. Eleventh" /></label>
	           <form:errors path="assemblyNumber" cssClass="validationError"></form:errors>			
	        
		</p>			
		<p>
		<label class="small"><spring:message code="${urlPattern}.strength" text="Strength"/>&nbsp;*</label>
			
				<form:input cssClass="integer sText" path="strength"/><form:errors path="strength" cssClass="validationError" />	
			
		</p>	
		<p>
		<label class="small"><spring:message code="${urlPattern}.term" text="Term"/>&nbsp;*</label>
			
				<form:input cssClass="sText" path="term"/><form:errors path="term" cssClass="validationError" />	
			
		</p>	
		<p>	
		<label class="small"><spring:message code="${urlPattern}.budgetSession" text="Budget Session?"/>&nbsp;</label>
		
				<form:checkbox cssClass="checkbox" path="budgetSession" value="true" /><form:errors path="budgetSession" cssClass="validationError" />
			
		</p>	
		<p>	
		<label class="small"><spring:message code="${urlPattern}.monsoonSession" text="Monsoon Session?"/>&nbsp;</label>
		
				<form:checkbox cssClass="checkbox" path="monsoonSession" value="true" /><form:errors path="monsoonSession" cssClass="validationError" />
			
		</p>	
		<p>	
		<label class="small"><spring:message code="${urlPattern}.winterSession" text="Winter Session?"/>&nbsp;</label>
		
				<form:checkbox cssClass="checkbox" path="winterSession" value="true" /><form:errors path="winterSession" cssClass="validationError" />
			
		</p>
		
		<p>	
		<label class="small"><spring:message code="${urlPattern}.current" text="Is Current Assembly?"/>&nbsp;</label>
		
				<form:checkbox cssClass="checkbox" path="currentAssembly" value="true" /><form:errors path="currentAssembly" cssClass="validationError" />
			
		</p>	
		
		<p>	
		<label class="small"><spring:message code="assembly.specialSession" text="Special Session?"/>&nbsp;</label>
		
				<form:checkbox cssClass="checkbox" path="specialSession" value="true" /><form:errors path="specialSession" cssClass="validationError" />
			
		</p>
		
		<p>
		<label class="small"><spring:message code="${urlPattern}.startDate" text="Start Date"/>&nbsp;*</label>
			
				<form:input cssClass="date sText" path="assemblyStartDate"/><form:errors path="assemblyStartDate" cssClass="validationError" />	
			
		</p>	
		
		<p>
		<label class="small"><spring:message code="${urlPattern}.endDate" text="End Date"/></label>
			
				<form:input cssClass="date sText" path="assemblyEndDate"/><form:errors path="assemblyEndDate" cssClass="validationError" />	
			
		</p>	
		
		<p>
		<label class="small"><spring:message code="${urlPattern}.dissolvedOn" text="Dissolved On"/></label>
			
				<form:input cssClass="date sText" path="assemblyDissolvedOn"/><form:errors path="assemblyDissolvedOn" cssClass="validationError" />	
			
		</p>				
		<p>
		<label class="small"><spring:message code="assembly.electionDateOfHonbleSpeaker" text="Election Date Of Honourable Speaker"/></label>
			
				<form:input cssClass="date sText" path="electionDateofHonbleSpeaker"/><form:errors path="electionDateofHonbleSpeaker" cssClass="validationError" />	
			
		</p>	
		
		<p>
		<label class="small"><spring:message code="assembly.governersAddressDate" text="Governers Address Date"/></label>
			
				<form:input cssClass="date sText" path="governersAddressDate"/><form:errors path="governersAddressDate" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="assembly.remarks" text="Remarks"/></label>
			
				<form:textarea cssClass="sTextarea" path="remarks"/><form:errors path="remarks" cssClass="validationError" />	
			
		</p>	
		<p class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" />
		</p>
		
		<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
		
</form:form>
</div>
</body>
</html>
