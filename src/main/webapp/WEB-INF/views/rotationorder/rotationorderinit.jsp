<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#aadwachart").click(function(){								
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val()
								 + "&outputFormat=" + $("#outputFormat").val();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL = 'rotationorder/aadwachart?' + parameters;
				$.get(resourceURL,function(data){
					$("#rotationOrderResultDiv").empty();
					$("#rotationOrderResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});																	
			});		
			
			$("#rotationorder").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'rotationorder/viewrotationorder?' + parameters;
				$.get(resourceURL,function(data){
					$("#rotationOrderResultDiv").empty();
					$("#rotationOrderResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});				
			});		
			
			$("#ministryreport").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'rotationorder/viewministryreport?' + parameters;
				$.get(resourceURL,function(data){
					$("#rotationOrderResultDiv").empty();
					$("#rotationOrderResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			});
		});		
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">		
			<a href="#" id="rotationorder" class="butSim">
				<spring:message code="rotationorderinitial.rotationorder" text="Rotation Order"/>
			</a> |
			<a href="#" id="aadwachart" class="butSim">
				<spring:message code="rotationorderinitial.aadwachart" text="Aadwa Chart"/>
			</a> |
			<a href="#" id="ministryreport" class="butSim">
				<spring:message code="rotationorderinitial.ministryreport" text="Ministry Department Report"/>
			</a> 
</div>
<div id="rotationOrderResultDiv">
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>