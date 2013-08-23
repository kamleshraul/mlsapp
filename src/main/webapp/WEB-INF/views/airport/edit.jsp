
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
			/* $.ajax({
				url:'ref/'+$('#states').val()+'/districts',
				datatype:'json',
				success:function(data){
					$('#districts option').remove();
					for(var i=0;i<data.length;i++){
						$('#districts').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
					}
				}							
			}); */
			$.ajax({
				url:'ref/state'+$('#states').val()+'/districts',
				datatype:'json',
				success:function(data){				
				$('#districts option').empty();
				var options="";
				for(var i=0;i<data.length;i++){
					options+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$('#districts').html(options);
				$('#districts').sexyselect('destroy');
				$('#districts').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true});
			}							
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
		});	
	}        
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix vidhanmandalImg">
<form:form action="airport" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
	<p>
	<label class="small"><spring:message code="airport.state" text="State"/>&nbsp;*</label>
			<select class="sSelect" name="state" id="states">
			<c:forEach items="${states}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
			</select>
			<%-- <form:errors path="state" cssClass="validationError"/> --%>
	</p>  
	<p>
	<label class="small"><spring:message code="airport.district" text="District"/>&nbsp;*</label>
	        <form:select id="districts" path="district" items="${districts}" itemValue="id" itemLabel="name" cssClass="sSelect"></form:select>	
	        <form:errors path="district" cssClass="validationError"/>	
	</p>
	<p>
	<label class="small"><spring:message code="airport.name" text="Name"/>&nbsp;*</label>
			<form:input cssClass="sText" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
	</p>
	<div class="fields expand">
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/> 
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>