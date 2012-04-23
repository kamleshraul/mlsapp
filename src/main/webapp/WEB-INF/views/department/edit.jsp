
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="department" text="Departments"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		if("${parent}"==""){
			$('#isCategory').val("off");
			$('.parent').hide();
		}
		else
			{
			$('#isCategory').attr("checked","on");
			$('.parent').show();
			} 
	});		
	$(':checkbox').click(function(){
		if($('#isCategory').is(':checked'))
			$('.parent').show();
		else
			{
			$('.parent').hide();
			}
	});
	$('#submit').click(function(){
		if($('#isCategory').is(':checked')){
		}
		else
			$('#parent1').prop('selectedIndex',-1);
	})
</script>
</head>
<body>

<div class="fields clearfix">
<form:form action="department" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>		
		<p> 
			<label class="small"><spring:message code="department.name" text="Department"/></label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError" />	
		</p>	 
		<p>
			<label class="small"><spring:message code="department.isCategory" text="isCategory" /></label>
			<input type="checkbox"  name="isCategory" id="isCategory"/>												
		</p>
		
		<p class="parent"> 
			<label class="small"><spring:message code="department.parentId" text="Parent Department"/></label>
			<form:select id="parent1" path="parentId" items="${parentDepartment}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="parentId" cssClass="validationError"/>	
		</p>
					
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>
</body>
</html>