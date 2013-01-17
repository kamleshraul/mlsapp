<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$("#mbRound").change(function(){
				if($(this).val()!='-'){
					if($("#mbAttendance").val()!='-'){
						var parameters="round="+$("#mbRound").val()+"&attendance="+$("#mbAttendance").val()+
							"&group="+$("#mbGroup").val()+"&answeringDate="+$("#mbAnsweringDate").val()+
							"&houseType="+$("#mbhouseType").val()+"&sessionType="+$("#mbsessionType").val()+
							"&sessionYear="+$("#mbsessionYear").val()+"&questionType="+$("#mbquestionType").val();
						var resource="question/memberballotresult";
						$.get(resource+'?'+parameters,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						},'html');
					}else{
						$.prompt($("#selectAttendanceMsg").val());
						$("#resultDiv").empty();					
					}
				}else{
						$("#resultDiv").empty();					
				}
			});
			$("#mbAttendance").change(function(){
				if($(this).val()!='-'){
					if($("#mbRound").val()!='-'){
						var parameters="round="+$("#mbRound").val()+"&attendance="+$("#mbAttendance").val()+
							"&group="+$("#mbGroup").val()+"&answeringDate="+$("#mbAnsweringDate").val()+
							"&houseType="+$("#mbhouseType").val()+"&sessionType="+$("#mbsessionType").val()+
							"&sessionYear="+$("#mbsessionYear").val()+"&questionType="+$("#mbquestionType").val();
						var resource="question/memberballotresult";
						$.get(resource+'?'+parameters,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						},'html');
					}else{
						$.prompt($("#selectRoundMsg").val());
						$("#resultDiv").empty();					
					}
				}else{
						$("#resultDiv").empty();					
				}
			});	
			$("#mbGroup").change(function(){
				if($(this).val()!='-'){
					if($("#mbRound").val()!='-'&&$("#mbAttendance").val()!='-'){
						var parameters="round="+$("#mbRound").val()+"&attendance="+$("#mbAttendance").val()+
							"&group="+$("#mbGroup").val()+"&answeringDate="+$("#mbAnsweringDate").val()+
							"&houseType="+$("#mbhouseType").val()+"&sessionType="+$("#mbsessionType").val()+
							"&sessionYear="+$("#mbsessionYear").val()+"&questionType="+$("#mbquestionType").val();
						var resource="question/memberballotresult";
						$.get(resource+'?'+parameters,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						loadAnsweringDates($("#mbGroup").val());
						},'html');
					}else{
						$.prompt($("#selectRoundAttendanceMsg").val());
						$("#resultDiv").empty();					
					}
				}else{
						$("#resultDiv").empty();					
				}
			});	
			$("#mbAnsweringDate").change(function(){
				if($(this).val()!='-'){
					if($("#mbRound").val()!='-'&&$("#mbAttendance").val()!='-'){
						var parameters="round="+$("#mbRound").val()+"&attendance="+$("#mbAttendance").val()+
							"&group="+$("#mbGroup").val()+"&answeringDate="+$("#mbAnsweringDate").val()+
							"&houseType="+$("#mbhouseType").val()+"&sessionType="+$("#mbsessionType").val()+
							"&sessionYear="+$("#mbsessionYear").val()+"&questionType="+$("#mbquestionType").val();
						var resource="question/memberballotresult";
						$.get(resource+'?'+parameters,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						},'html');
					}else{
						$.prompt($("#selectRoundAttendanceMsg").val());
						$("#resultDiv").empty();					
					}
				}else{
						$("#resultDiv").empty();					
				}
			});			
		});
		function loadAnsweringDates(group){
			var options="<option value='-'>"+$("#pleaseSelect").val()+"</option>";	
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
		}
	</script>
</head>

<body>
<div class="commandbarContent" style="margin: 10px;" id="choiceDiv">
			<a href="#" id="select_round" class="butSim">
				<spring:message code="memberballot.round" text="Round"/>
			</a>
			<select name="mbRound" id="mbRound" style="width:100px;height: 25px;">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>		
			<c:forEach items="${rounds}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
			</c:forEach> 
			</select> |	
			<a href="#" id="select_attendance" class="butSim">
				<spring:message code="memberballot.attendance" text="Attendance"/>
			</a>
			<select name="mbAttendance" id="mbAttendance" style="width:100px;height: 25px;">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>										
			<option value="true"><spring:message code='attendance.present' text='Present'/></option>	
			<option value="false"><spring:message code='attendance.absent' text='Absent'/></option>			 
			</select> |
			<a href="#" id="select_group" class="butSim">
				<spring:message code="memberballot.group" text="Group"/>
			</a>
			<select name="mbGroup" id="mbGroup" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.formatNumber()}"></c:out></option>	
			</c:forEach> 
			</select> |
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="memberballot.answeringdate" text="Answering Date"/>
			</a>
			<select name="mbAnsweringDate" id="mbAnsweringDate" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>			
			</select> 
</div>
<div id="resultDiv">
</div>
<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">
<input type="hidden" name="selectAttendanceMsg" id="selectAttendanceMsg" value="<spring:message code='memberballot.selectattendance' text='Please Select Attendance First'/>">
<input type="hidden" name="selectRoundMsg" id="selectRoundMsg" value="<spring:message code='memberballot.selectround' text='Please Select Round First'/>">
<input type="hidden" name="selectRoundAttendanceMsg" id="selectRoundAttendanceMsg" value="<spring:message code='memberballot.selectroundattendance' text='Please Select Round and Attendance First'/>">
<input type="hidden" id="mbhouseType" value="${houseType}">	
<input type="hidden" id="mbsessionType" value="${sessionType}">	
<input type="hidden" id="mbsessionYear" value="${sessionYear}">	
<input type="hidden" id="mbquestionType" value="${questionType}">	
</body>
</html>