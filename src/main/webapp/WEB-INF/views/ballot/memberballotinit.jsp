<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			/**** Level 0 Links ****/
			$("#selectedAttendance").change(function(){
			$(".submit").attr("disabled","disabled");
			});
			$("#selectedRound").change(function(){
				$(".submit").attr("disabled","disabled");
			});
			$("#mbGroup").change(function(){
				loadAnsweringDates($(this).val());
			});		
			/**** Level 1 Links ****/		
			$('#attendance').click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				attendance();
			});		
			$('#preballot').click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				preballot();
			});				
			$('#memberballot').click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				memberballot();
			});	
			$('#view_member_ballot_status').click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				viewMemberBallotStatus();
			});	
			/**** Level 2 Links ****/				
			$('#memberballotchoice').click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				memberballotchoice();
			});			
			$("#updateclubbing").click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				updateClubbing();
			});	
			$("#finalballot").click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				finalballot();
			});	
			$("#memberwise_report").click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				memberwiseReport();
			});			
			$("#question_distribution").click(function(){
				$(".link").css("color","#8D8B8B");
				$(this).css("color","blue");
				questionDistribution();
			});						
			/**** on page load ****/
			attendance();											
		});		
		function loadAnsweringDates(group){
			var options="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
			if(group!='-'){
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
			}).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});	
			}else{
				$("#mbAnsweringDate").empty();	
				$("#mbAnsweringDate").html(options);	
			}
		}	
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
			$(".link").css("color","#8D8B8B");
			$("#attendance").css("color","blue");		
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
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val()
							 +"&round="+$("#selectedRound").val()
							 +"&attendance="+$("#selectedAttendance").val()
			 				 +"&noofrounds="+$("#noOfRounds").val()
			 				 +"&group="+$("#mbGroup").val()
			 				 +"&answeringDate="+$("#mbAnsweringDate").val();
			var resourceURL1 = 'ballot/memberballot/memberballotstatusround?'+ parameters;
			$.get(resourceURL1,function(data){
				if(data=='COMPLETE'){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });						
					var resourceURL2 = 'ballot/memberballot/view?'+ parameters;
					$.get(resourceURL2,function(data){							
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
				}else{
					/* $.blockUI({ message: '<img src="./resources/images/Ballot06.gif" />' }); */
					var pa=$("#selectedAttendance").val();
					var img="Ballot";
					if(pa=='true'){
						img+="Present_"+$("#selectedRound").val();
					}else if(pa=='false'){
						img+="Absent_"+$("#selectedRound").val();
					}
					$.blockUI({ message: '<img src="./resources/images/'+ img +'.gif" />' ,centerY: false, 
			            css: { top: '90px'} });	
					var resourceURL2 = 'ballot/memberballot/view?'+ parameters;
					$.get(resourceURL2,function(data){
							setTimeout(function(){
								$("#resultDiv").html(data);
								$.unblockUI();	
							},5000);	
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
			});			
		}	
		
		function viewMemberBallotStatus(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val()
			 				 +"&noofrounds="+$("#noOfRounds").val();
			var resourceURL = 'ballot/memberballot/memberballotstatus?'+ parameters;
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
		function memberballotchoice(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/choices?'+ parameters;
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
		function updateClubbing(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/updateclubbing?'+ parameters;
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
		function finalballot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var group=$("#mbGroup").val();
			var answeringDate=$("#mbAnsweringDate").val();
			if(group!="-"&&answeringDate!="-"){
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val()
			 +"&group="+group+"&answeringDate="+answeringDate;
			var resourceURL = 'ballot/memberballot/final?'+ parameters;
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
			}else{
				$.unblockUI();
				$.prompt($("#selectGroupAnsweringDateMsg").val());		
			}
		}
		function memberwiseReport(){			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/memberwise?'+ parameters;
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
		function questionDistribution(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "session="+$("#session").val()
							 +"&questionType="+$("#questionType").val();
			var resourceURL = 'ballot/memberballot/questiondistribution?'+ parameters;
			$.get(resourceURL,function(data){
			$("#resultDiv").html(data);
			$.unblockUI();				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg")==undefined){
					
				}else{
					if($("#ErrorMsg").val()!=''){
						$.prompt($("#ErrorMsg").val());
					}else{
						$.prompt("Error");
					}
				}
			});			 
		}	
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
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
			<a href="#" id="attendance" class="butSim link">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a> |	
			<a href="#" id="preballot" class="butSim link">
				<spring:message code="memberballot.preballot" text="Pre Ballot"/>
			</a> |
			<a href="#" id="memberballot" class="butSim link">
				<spring:message code="memberballot.memberballot" text="Member Ballot"/>
			</a> |		
			<a href="#" id="view_member_ballot_status" class="butSim link">
				<spring:message code="memberballot.memberballotstatus" text="Member Ballot Status"/>
			</a> |		
			<hr>
			<a href="#" id="memberballotchoice" class="butSim link">
				<spring:message code="memberballot.memberballotchoice" text="Question Choices"/>
			</a> |
			<a href="#" id="updateclubbing" class="butSim link">
				<spring:message code="memberballot.updateclubbing" text="Update Clubbing"/>
			</a> |
			<a href="#" id="finalballot" class="butSim link">
				<spring:message code="memberballot.finalballot" text="Final Ballot"/>
			</a> |			
			<a href="#" id="memberwise_report" class="butSim link">
				<spring:message code="memberballot.memberwisereport" text="Member's Questions Report"/>
			</a> |
			<a href="#" id="question_distribution" class="butSim link">
				<spring:message code="memberballot.questiondistribution" text="Total Questions Report"/>
			</a> |
			<hr>			
</div>
<div id="resultDiv">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
<input type="hidden" id="noOfRounds" name="noOfRounds" value="${noOfRounds}">
<input type="hidden" id="selectGroupAnsweringDateMsg" name="selectGroupAnsweringDateMsg" value="<spring:message code='memberballot.selectanseringdatemsg' text='Please Select Group And Ansering Date.'/>">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>