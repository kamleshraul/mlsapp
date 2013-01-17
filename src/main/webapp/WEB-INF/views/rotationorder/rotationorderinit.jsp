<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {		
			$("#aadwachart").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'rotationorder/aadwachart?' + parameters;
				$.get(resourceURL,function(data){
					$("#rotationOrderResultDiv").empty();
					$("#rotationOrderResultDiv").html(data);
					$.unblockUI();					
				},'html');
					
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
				var resourceURL = 'rotationorder/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#rotationOrderResultDiv").empty();
					$("#rotationOrderResultDiv").html(data);
					$.unblockUI();					
				},'html');
					
			});	
		});		
	</script>
</head>

<body>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">		
			<a href="#" id="rotationorder" class="butSim">
				<spring:message code="rotationorderinitial.rotationorder" text="Rotation Order"/>
			</a> |
			<a href="#" id="aadwachart" class="butSim">
				<spring:message code="rotationorderinitial.aadwachart" text="Aadwa Chart"/>
			</a>
</div>
<div id="rotationOrderResultDiv">
</div>
</body>
</html>