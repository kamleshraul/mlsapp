<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {			
			$('#attendance').click(function(){
				attendance();
			});							
			$('#memberballot').click(function(){
				memberballot();
			});				
			
			
			$("#assignNumber").click(function(){
				assignNumberToBallotedMember();
			});
			/**** attendance changes then disable submit otherwise whole configuration gets submitted
			as changed value which is wrong ****/
			$("#selectedAttendance").change(function(){
			$(".submit").attr("disabled","disabled");
			});				
			/**** on page load ****/
			attendance();									
		});			
		function attendance(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var attendance=$("#selectedAttendance").val();
			var parameters = "session="+$("#session").val()
							 +"&deviceType="+$("#deviceType").val()
							 +"&attendance="+$("#selectedAttendance").val();
			var resourceURL = 'ballot/attendance?'+ parameters;
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			if(attendance=='true'){
				$("#presentTable").show();
				$("#absentTable").empty();
				$("#absentTable").hide();				
				}else{
					$("#absentTable").show();
					$("#presentTable").empty();
					$("#presentTable").hide();
			}	
			$.unblockUI();				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});			
		}			
		function memberballot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&deviceType="+$("#deviceType").val()
							 +"&attendance="+$("#selectedAttendance").val();
			var resourceURL = 'ballot/create/view?'+ parameters+"&type=member_ballot";
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			$.unblockUI();				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
		}	
		
		function assignNumberToBallotedMember(){
			var parameters = "session="+$("#session").val()
			 +"&deviceType="+$("#deviceType").val()
			 +"&attendance="+$("#selectedAttendance").val();
			var resourceURL = 'motion/assignnumber?'+ parameters;
			$.get(resourceURL, function(data){
				$.prompt(data);
			},'html');
		}
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent">				
			<a href="#" id="attendanceLabel" class="butSim">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a>		
			<select name="selectedAttendance" id="selectedAttendance" style="width:100px;">			
			<option value="true"><spring:message code="memberballot.presentee" text="Present"></spring:message></option>			
			<option value="false"><spring:message code="memberballot.absentee" text="Absent"></spring:message></option>			
			</select> |					
			<hr>			
			<a href="#" id="attendance" class="butSim">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a> |				
			<a href="#" id="memberballot" class="butSim">
				<spring:message code="memberballot.memberballot" text="Ballot"/>
			</a> |			
			<a href="#" id="assignNumber" class="butSim">
				<spring:message code="memberballot.assignNumber" text="Assign Number"/>
			</a> |
			<hr>				
</div>

<div id="resultDiv">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>