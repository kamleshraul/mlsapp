<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="airport" text="Airports"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
	</script>
	<script type="text/javascript">
	if($('#states').val()!=undefined){
		$('#states').change(function(){
			$.ajax({
				url:'ref/state'+$('#states').val()+'/districts',
				datatype:'json',
				success:function(data){
					$('#districts option').remove();
					for(var i=0;i<data.length;i++){
						$('#districts').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
					}
				}							
			});
		});	
	}    
	</script>
</head>
<body>
<div class="fields clearfix vidhanmandalImg">
<form:form  action="airport" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>
		<p>
			<label class="small"><spring:message code="airport.state" text="State"/>&nbsp;*</label>
			<select class="sSelect" name="state" id="states">
			<c:forEach items="${states}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
			</select>
		</p>		 
		<p> 
			<label class="small"><spring:message code="airport.district" text="District"/></label>
			<form:select id="districts" path="district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="district" cssClass="validationError"/>	
		</p>
		<p> 
			<label class="small"><spring:message code="airport.name" text="Name"/></label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError" />	
		</p>				
		<div class="fields expand">
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