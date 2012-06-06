
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="departmentdetail" text="Department Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
	$('#submit').click(function(){
		if($('#isExpired').is(':checked'))
	   	{
			$('#isExpired').val(true);		   	    
		}
		else
	   	{ 				
			$('#isExpired').val(false);				
	   	};
	});
</script>
</head>
<body>

<div class="fields clearfix">
<form:form action="departmentdetail" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>		 
		<p> 
			<label class="small"><spring:message code="departmentdetail.department" text="Department"/></label>
			<form:select path="department" items="${department}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="department" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="departmentdetail.name" text="Department Detail"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError" />	
		</p>	
		<p>
			<label class="small"><spring:message code="departmentdetail.isExpired" text="Is Expired?" /></label>
			<form:checkbox cssClass="sCheck" path="isExpired" id="isExpired"/>
						<form:errors path="isExpired" cssClass="validationError" />
		</p>
		<p>
			<label class="labelcentered"><spring:message code="departmentdetail.remarks" text="Remarks" /></label>
				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />
		</p>			
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>