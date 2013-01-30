<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			/**** attendance ****/		
			$('#attendance').click(function(){
				attendance();
			});		
			$('#preballot').click(function(){
				preballot();
			});				
			$('#memberballot').click(function(){
				memberballot();
			});				
			$('#memberballotchoice').click(function(){
				memberballotchoice();
			});			
			$("#updateclubbing").click(function(){
				updateClubbing();
			});	
			/**** attendance changes then disable submit otherwise whole configuration gets submitted
			as changed value which is wrong ****/
			$("#selectedAttendance").change(function(){
			$(".submit").attr("disabled","disabled");
			});
			$("#selectedRound").change(function(){
				$(".submit").attr("disabled","disabled");
			});
			$("#mbGroup").change(function(){
				loadAnsweringDates($(this).val());
			});			
			/**** on page load ****/
			attendance();									
		});		
		function attendance(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var attendance=$("#selectedAttendance").val();
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val()
							 +"&round="+$("#selectedRound").val()
							 +"&attendance="+$("#selectedAttendance").val()
			 				 +"&noofrounds="+$("#noOfRounds").val();
			var resourceURL = 'ballot/memberballot/attendance?'+ parameters;
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
			},'html');			
		}
		function preballot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val()
							 +"&round="+$("#selectedRound").val()
							 +"&attendance="+$("#selectedAttendance").val()
			 				 +"&noofrounds="+$("#noOfRounds").val();
			var resourceURL = 'ballot/memberballot/preballot?'+ parameters;
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			$.unblockUI();				
			},'html');			
		}	
		function loadAnsweringDates(group){
			var options="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		if(group!='-'){
			$.get('ref/group/'+group+'/answeringdates',function(data){
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						options=options+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#mbAnsweringDate").empty();
					$("#mbAnsweringDate").html(options);
				}else{
					$("#mbAnsweringDate").empty();	
					$("#mbAnsweringDate").html(options);								
				}
			});	
			}else{
				$("#mbAnsweringDate").empty();	
				$("#mbAnsweringDate").html(options);	
			}
		}
		function memberballot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val()
							 +"&round="+$("#selectedRound").val()
							 +"&attendance="+$("#selectedAttendance").val()
			 				 +"&noofrounds="+$("#noOfRounds").val()
			 				 +"&group="+$("#mbGroup").val()
			 				 +"&answeringDate="+$("#mbAnsweringDate").val();
			var resourceURL = 'ballot/memberballot/view?'+ parameters;
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			$.unblockUI();				
			},'html');
		}
		function memberballotchoice(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/choices?'+ parameters;
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			$.unblockUI();				
			},'html');
		}
		function uodateClubbing(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/updateclubbing?'+ parameters;
			$.get(resourceURL,function(data){
				$("#resultDiv").html(data);
				$.unblockUI();				
			},'html');
		}
	</script>
</head>

<body>
<div class="commandbarContent">	
			<a href="#" id="roundLabel" class="butSim">
				<spring:message code="memberballot.round" text="Round"/>
			</a>		
			<select name="selectedRound" id="selectedRound" style="width:100px;">			
			<c:forEach items="${rounds}" var="i">
			<c:choose>
			<c:when test="${selectedRound==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |
			<a href="#" id="attendanceLabel" class="butSim">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a>		
			<select name="selectedAttendance" id="selectedAttendance" style="width:100px;">			
			<option value="true"><spring:message code="memberballot.presentee" text="Present"></spring:message></option>			
			<option value="false"><spring:message code="memberballot.absentee" text="Absent"></spring:message></option>			
			</select> |
			<a href="#" id="groupLabel" class="butSim">
				<spring:message code="memberballot.group" text="Group"/>
			</a>
			<select name="mbGroup" id="mbGroup" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> |
			<a href="#" id="answeringdateLabel" class="butSim">
				<spring:message code="memberballot.answeringdate" text="Answering Date"/>
			</a>
			<select name="mbAnsweringDate" id="mbAnsweringDate" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select> |
			<hr>
			<a href="#" id="attendance" class="butSim">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a> |	
			<a href="#" id="preballot" class="butSim">
				<spring:message code="memberballot.preballot" text="Pre Ballot"/>
			</a> |
			<a href="#" id="memberballot" class="butSim">
				<spring:message code="memberballot.memberballot" text="Member Ballot"/>
			</a> |
			<a href="#" id="memberballotchoice" class="butSim">
				<spring:message code="memberballot.memberballotchoice" text="Question Choices"/>
			</a> |
			<a href="#" id="updateclubbing" class="butSim">
				<spring:message code="memberballot.updateclubbing" text="Update Clubbing"/>
			</a> |
			<a href="#" id="finalballot" class="butSim">
				<spring:message code="memberballot.finalballot" text="Final Ballot"/>
			</a> |
			<hr>				
</div>

<div id="resultDiv">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
<input type="hidden" id="noOfRounds" name="noOfRounds" value="${noOfRounds}">

</body>
</html>