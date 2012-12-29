<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			$(".toolTip").hide();					
			//here we are trying to add date mask in grid search when field names
			//ends with Date
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});			
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();
				$("#selectionDiv2").show();
				$("#selectionDiv3").show();				
				showQuestionList();
			});	
			$('#details_tab').click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();				
				editQuestion();
			});
			$('#chart_tab').click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();		
				viewChart();
			});	
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();					
				viewBallot();
			});
			/*$('#attendance_tab').click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();					
				markAttendance("presentees");
			});*/	
			$('#mark_attendance').click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();					
				markAttendance("presentees");
			});		
			$("#view_chart").click(function() {
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();					
				viewChart();
			});	
				
			$("#create_chart").click(function() {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = $("#gridURLParams").val() + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'question/chart/create?' + parameters;
				$.get(resourceURL, function(data) {
					var displayMessage = data;
					if(data == "CREATED") {
						displayMessage = "Chart is successfully created.";
					}
					else if(data == "ALREADY_EXISTS") {
						displayMessage = "Chart already exists.";
					}
					else if(data == "PREVIOUS_CHART_IS_NOT_PROCESSED") {
						displayMessage = "Previos Chart is not Processed. Kindly process it before creating a new Chart.";
					}
					$.unblockUI();
					$.fancybox.open(displayMessage);
				});
			}); 
			
			$("#view_ballot").click(function() {
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();					
				viewBallot();
			});	
				
			$("#create_ballot").click(function() {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = $("#gridURLParams").val() + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'question/ballot/create?' + parameters;
				$.get(resourceURL, function(data) {
					var displayMessage = data;
					if(data == "CREATED") {
						displayMessage = "Ballot is successfully created.";
					}
					else if(data == "ALREADY_EXISTS") {
						displayMessage = "Ballot already exists.";
					}
					$.unblockUI();
					$.fancybox.open(displayMessage);
				});
				
			});		
			$("#createMemberballot").click(function(){
				createMemberBallot();
			});	
			$("#viewMemberballot").click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();	
				viewMemberBallot();
			});		
			//If house type changes then we need to change the value of selected house type,grid url param
			// and reload the grid			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){
					if($("#selectedGroup").length>0){
					loadSessionGroups();					
					}else{
					reloadQuestionGrid();
					}
				}	
			});	
			//If session year changes then we need to change the value of selected session year,grid url param
			// and reload the grid		
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){
					if($("#selectedGroup").length>0){
					loadSessionGroups();					
					}else{
					reloadQuestionGrid();
					}
				}			
			});
			//If session type changes then we need to change the value of selected session type,grid url param
			// and reload the grid	
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){
					if($("#selectedGroup").length>0){
					loadSessionGroups();					
					}else{
					reloadQuestionGrid();
					}
				}	
			});
			//If question type changes then we need to change the value of selected question type,grid url param
			// and reload the grid	
			$("#selectedQuestionType").change(function(){
				var value=$(this).val();
				if(value!=""){				
				reloadQuestionGrid();
				}
			});	

			$("#selectedGroup").change(function(){
				var value=$(this).val();
				if(value!=""){				
					loadAnsweringDates(value)
				}
			});	

			$("#selectPreBallot").change(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();	
				var val=$(this).val();
				if(val!='-'){
					preBallot(val);
				}else{
					$("#selectionDiv1").show();
					$("#selectionDiv2").show();
					$("#selectionDiv3").show();	
				}				
			});	
			/*$("#preballot_tab").click(function(){
				$("#selectionDiv1").hide();
				$("#selectionDiv2").hide();
				$("#selectionDiv3").hide();	
				var val=$("#selectPreBallot").val();
				if(val!='-'){
					preBallot(val);
				}else{
					$("#selectionDiv1").show();
					$("#selectionDiv2").show();
					$("#selectionDiv3").show();	
				}	
			});*/
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newQuestion();
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showQuestionList();
				}
				if(e.which==79 && e.ctrlKey){
					editQuestion($('#key').val());
				}
				if(e.which==8 && e.ctrlKey){
					deleteQuestion($('#key').val());
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			//show question list method is called by default.
			showQuestionList();	
		});
				
		function showQuestionList() {
			//If no session entry has been created then just house type and question type is passed else all four parameters are
			//passed.
			var sessionYear=$("#sessionYear").val();
			if(sessionYear==""){						
				showTabByIdAndUrl('list_tab','question/list?houseType='+$('#selectedHouseType').val()+'&questionType='+$("#selectedQuestionType").val()+'&usergroup='+$("#usergroup").val()+'&userrole='+$("#userrole").val()+'&group='+$("#selectedGroup").val());
			}else{
				showTabByIdAndUrl('list_tab','question/list?houseType='+$('#selectedHouseType').val()+'&questionType='+$("#selectedQuestionType").val()+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroup='+$("#usergroup").val()+'&userrole='+$("#userrole").val()+'&group='+$("#selectedGroup").val());				
			}							
		}	
		function newQuestion() {
			$("#cancelFn").val("newQuestion");
			//since id of question has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','question/new?'+$("#gridURLParams").val());
		}
		function editQuestion() {
			$("#cancelFn").val("editQuestion");						
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
			showTabByIdAndUrl('details_tab','question/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		function editQuestion(row) {
			$("#cancelFn").val("editQuestion");						
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
			showTabByIdAndUrl('details_tab','question/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}			
		
		function deleteQuestion() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('question/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showQuestionList();
				        });
			        }
				}});
			}
		}

		function loadSessionGroups(){
			$.get("ref/groups?houseType="+$("#selectedHouseType").val()+"&year="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val(),function(data){
				var groupsAllowed=$("#groupsAllowed").val();
				if(data.length>0){
					$("#selectedGroup").empty();
					$("#selectedAnsweringDate").empty();
					console.log(data.length);					
					for(var i=0;i<data.length;i++){
						if(groupsAllowed.indexOf(data[i].name)!=-1){						
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>"
						}
					}
					$("#selectedGroup").html(text);
					$("#selectedAnsweringDate").html(text);
					$("#ugparam").val(data[0].id);					
					loadSessionAnsweringDates(data[0].id);	
				}else{
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";					
					$("#selectedGroup").empty();
					$("#selectedAnsweringDate").empty();
					$("#selectedGroup").html(text);
					$("#selectedAnsweringDate").html(text);					
				}
			});
		}

		function loadSessionAnsweringDates(group){
			$.get("ref/group/"+group+"/answeringdates",function(data){
				if(data.length>0){
					$("#selectedAnsweringDate").empty();
					for(var i=0;i<data.length;i++){
						if(i==0){
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>"
						}else{
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>"							
						}
					}
					$("#selectedAnsweringDate").html(text);
					reloadQuestionGrid();
				}else{
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";					
					$("#selectedGroup").empty();
					$("#selectedAnsweringDate").empty();
					$("#selectedGroup").html(text);
					$("#selectedAnsweringDate").html(text);					
				}
			});
		}	

		function reloadQuestionGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val()+"&ugparam="+$("#ugparam").val());			
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}

		function viewChart() {
			var parameters = $("#gridURLParams").val() + "&answeringDate=" + $("#selectedAnsweringDate").val();
			var resourceURL = 'question/chart/view?' + parameters;
			showTabByIdAndUrl('chart_tab', resourceURL);
		}	
		function viewBallot() {
			var parameters = $("#gridURLParams").val() + "&answeringDate=" + $("#selectedAnsweringDate").val();
			var resourceURL = 'question/ballot/view?' + parameters;
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}	
		function markAttendance(operation){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = $("#gridURLParams").val()+"&operation="+operation;
			if(parameters==undefined){
				parameters="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val()+"&operation="+operation;
			}
			if(parameters==undefined){
				parameters="houseType="+$("#houseType").val()+"&sessionYear="+$("#sessionYear").val()+"&sessionType="+$("#sessionType").val()+"&questionType="+$("#questionType").val()+"&operation="+operation;
			}
			var resourceURL = 'question/attendance?' + parameters;
			$.get(resourceURL,function(data){
			$('a').removeClass('selected');
			$('#attendance_tab').addClass('selected');
			$('.tabContent').html(data);
			$.unblockUI();				
			},'html');			
		}	
		function preBallot(attendance){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = $("#gridURLParams").val()+"&attendance="+attendance;
			if(parameters==undefined){
				parameters="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val()+"&attendance="+attendance;
			}
			var resourceURL = 'question/preballot?' + parameters;
			$.get(resourceURL,function(data){
			$('a').removeClass('selected');
			$('#preballot_tab').addClass('selected');
			$('.tabContent').html(data);
			$.unblockUI();				
			},'html');			
		}	

		function createMemberBallot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val();
			}
			var attendance=$("#selectAttendanceType").val();
			var round=$("#selectRound").val();
			if(attendance!='-'&&round!='-'){
				parameters=parameters+'&attendance='+attendance+'&round='+round;
				var resourceURL = 'question/memberballot?' + parameters;
				$.post(resourceURL,function(data){
					if(data=='success'){
						$.unblockUI();							
						$.prompt($("#ballotSuccessMsg").val());
					}else if(data=='alreadycreated'){
						$.unblockUI();						
						$.prompt($("#ballotAlreadyCreatedMsg").val());
					}else{
						$.unblockUI();						
						$.prompt($("#ballotFailedMsg").val());
					}		
				},'html');
			}else{
				$.unblockUI();				
				$.prompt($("#selectAttendanceRoundMsg").val());
			}						
		}
		function viewMemberBallot(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val();
			}
			var attendance=$("#selectAttendanceType").val();
			var round=$("#selectRound").val();
			if(attendance!='-'&&round!='-'){
				parameters=parameters+'&attendance='+attendance+'&round='+round;
				var resourceURL = 'question/memberballot?' + parameters;
				$.get(resourceURL,function(data){
					$('a').removeClass('selected');
					$('#memberballot_tab').addClass('selected');
					$('.tabContent').html(data);
					$.unblockUI();					
				},'html');
			}else{
				$.unblockUI();				
				$.prompt($("#selectAttendanceRoundMsg").val());
			}						
		}				
	</script>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details">
				   </spring:message>
				</a>
			</li>
			<c:if test="${usergroupType=='assistant'}">
			<li>
				<a id="chart_tab" href="#" class="tab">
				   <spring:message code="question.chart" text="Chart"></spring:message>
				</a>
			</li>
			<li>
				<a id="ballot_tab" href="#" class="tab">
				   <spring:message code="question.ballot" text="Ballot"></spring:message>
				</a>
			</li>
			</c:if>
			<c:if test="${usergroupType!='member'}">
			<c:if test="${userrole!='CLERK' }">
			<c:if test="${questionTypeType!='questions_unstarred'&& questionTypeType!='questions_shortnotice'&& questionTypeType!='questions_halfhourdiscussion'}">
			<c:if test="${houseType=='upperhouse'}">
			<li>
				<a id="attendance_tab" href="#" class="tab">
				   <spring:message code="question.attendance" text="Attendance"></spring:message>
				</a>				
			</li>
			<li>
				<a id="preballot_tab" href="#" class="tab">
				   <spring:message code="question.preballot" text="Pre-Ballot"></spring:message>
				</a>				
			</li>
			<li>
				<a id="memberballot_tab" href="#" class="tab">
				   <spring:message code="question.memberballot" text="Member Ballot"></spring:message>
				</a>				
			</li>				
			</c:if>
			</c:if>
			</c:if>
			</c:if>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseTypeSelected==i.type}">
			<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |					
			<a href="#" id="select_session_year" class="butSim">
				<spring:message code="question.sessionyear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:100px;height: 25px;">				
			<c:forEach var="i" items="${years}">
			<c:choose>
			<c:when test="${i==sessionYear }">
			<option value="${i}" selected="selected"><c:out value="${i}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i}" ><c:out value="${i}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach> 
			</select> |						
			<a href="#" id="select_sessionType" class="butSim">
				<spring:message code="question.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">				
			<c:forEach items="${sessionTypes}" var="i">
			<c:choose>
			<c:when test="${sessionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>			
			</c:otherwise>
			</c:choose>			
			</c:forEach> 
			</select> |				
			<a href="#" id="select_questionType" class="butSim">
				<spring:message code="question.questionType" text="Question Type"/>
			</a>
			<select name="selectedQuestionType" id="selectedQuestionType" style="width:100px;height: 25px;">			
			<c:forEach items="${questionTypes}" var="i">
			<c:choose>
			<c:when test="${questionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |						
		</div>
				
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">		
		<c:if test="${usergroupType!='member'}">
		<c:if test="${userrole!='CLERK' }">
		<c:if test="${questionTypeType!='questions_unstarred'&& questionTypeType!='questions_shortnotice'}">
			<a href="#" id="select_group" class="butSim">
				<spring:message code="question.group" text="Group"/>
			</a>
			<select name="selectedGroup" id="selectedGroup" style="width:100px;height: 25px;">				
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.number}"></c:out></option>	
			</c:forEach> 
			</select> | 
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="question.answeringdate" text="Answering Date"/>
			</a>
			<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
			<c:forEach items="${answeringDates}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> | 			
			<a href="#" id="create_chart" class="butSim">
				<spring:message code="question.createChart" text="Create Chart"/>
			</a> |
			<a href="#" id="view_chart" class="butSim">
				<spring:message code="question.viewChart" text="View Chart"/>
			</a> |
			<a href="#" id="create_ballot" class="butSim">
				<spring:message code="question.createBallot" text="Create Ballot"/>
			</a> |
			<a href="#" id="view_ballot" class="butSim">
				<spring:message code="question.viewBallot" text="View Ballot"/>
			</a> 
			</c:if>
			</c:if>
			</c:if>	
		</div>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv3">		
		<c:if test="${usergroupType!='member'}">
		<c:if test="${userrole!='CLERK' }">
		<c:if test="${questionTypeType!='questions_unstarred'&& questionTypeType!='questions_shortnotice'&& questionTypeType!='questions_halfhourdiscussion'}">
			<c:if test="${houseType=='upperhouse'}">
			<a href="#" id="mark_attendance" class="butSim">
				<spring:message code="question.attendance" text="Attendance"/>
			</a> |
			<select name="selectPreBallot" id="selectPreBallot" style="width:100px;height: 25px;">
			<option value='-'><spring:message code='please.select' text='Please Select'/></option>				
			<option value="present"><spring:message code='attendance.present' text='Present'/></option>	
			<option value="absent"><spring:message code='attendance.absent' text='Absent'/></option>			
			</select>	
			<a href="#" id="preballot" class="butSim">
				<spring:message code="question.preballot" text="Pre-Ballot"/>
			</a> |
			<select name="selectAttendanceType" id="selectAttendanceType" style="width:100px;height: 25px;">
			<option value='-'><spring:message code='please.select' text='Please Select'/></option>				
			<option value="true"><spring:message code='attendance.present' text='Present'/></option>	
			<option value="false"><spring:message code='attendance.absent' text='Absent'/></option>			
			</select>
			<select name="selectRound" id="selectRound" style="width:100px;height: 25px;">
			<option value='-'><spring:message code='please.select' text='Please Select'/></option>				
			<option value="1"><c:out value="1"></c:out></option>	
			<option value="2"><c:out value="2"></c:out></option>
			<option value="3"><c:out value="3"></c:out></option>	
			<option value="4"><c:out value="4"></c:out></option>
			<option value="5"><c:out value="5"></c:out></option>				
			</select>	
			<a href="#" id="createMemberballot" class="butSim">
				<spring:message code="question.createMemberballot" text="Create Member Ballot"/>
			</a> 
			<a href="#" id="viewMemberballot" class="butSim">
				<spring:message code="question.viewMemberballot" text="View Member Ballot"/>
			</a> 			
			</c:if>		
			</c:if>
			</c:if>
			</c:if>	
		</div>
		
		
		<div class="tabContent clearfix">
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" name="usergroup" id="usergroup" value="${usergroup}">	
		<input type="hidden" name="usergroupType" id="usergroupType" value="${usergroupType}">	
		<input type="hidden" name="userrole" id="userrole" value="${userrole}">	
		<input type="hidden" name="groupsAllowed" id="groupsAllowed" value="${groupsAllowed}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>">			
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>">			
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>">			
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>">			
		
		</div> 		
</body>
</html>