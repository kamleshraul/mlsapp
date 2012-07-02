
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="ministry" text="Ministries"/>
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

<div class="fields clearfix vidhanmandalImg">
<form:form action="ministry" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>		
		<p> 
			<label class="small"><spring:message code="ministry.name" text="Ministry"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError" />	
		</p>	 
		
		<p>
			<label class="small"><spring:message code="ministry.isExpired" text="Is Expired?" /></label>
			<form:checkbox cssClass="sCheck" path="isExpired" id="isExpired"/>
						<form:errors path="isExpired" cssClass="validationError" />
		</p>
		<p>
			<label class="labelcentered"><spring:message code="ministry.remarks" text="Remarks" /></label>
				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />
		</p>
					
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
				
			</p>
		</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>
</body>
</html>