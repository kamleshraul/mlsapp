<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();
			if(currentDeviceType == 'questions_starred' && currentHouseType == 'upperhouse'){
				$("#memberballot_tab").show();
				$("#ballot_tab").hide();
			}
			else if((currentDeviceType == 'questions_starred' && currentHouseType == 'lowerhouse')
					|| currentDeviceType == 'questions_halfhourdiscussion_from_question') {
				$("#memberballot_tab").hide();
				$("#ballot_tab").show();
			}
			else {
				$("#memberballot_tab").hide();
				$("#ballot_tab").hide();
			}		
			/*Tooltip*/
			$(".toolTip").hide();					
			/**** here we are trying to add date mask in grid search when field names ends with date ****/
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});	
			/**** displaying grid ****/		
			$('#list_tab').click(function(){
				$("#selectionDiv1").show();								
				showQuestionList();
			});			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					loadGroupsFromSessions();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					loadGroupsFromSessions();								
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					loadGroupsFromSessions();							
				}			
			});
			/**** question type changes then reload grid****/			
			$("#selectedQuestionType").change(function(){
				var value = $(this).val();
				var text = $("#deviceTypeMaster option[value='"+value+"']").text();
				if(text == 'questions_starred' && currentHouseType == 'upperhouse'){
					$("#memberballot_tab").show();
					$("#ballot_tab").hide();
				}
				else if((text == 'questions_starred' && currentHouseType == 'lowerhouse')
						|| text == 'questions_halfhourdiscussion_from_question') {
					$("#memberballot_tab").hide();
					$("#ballot_tab").show();
				}
				else {
					$("#memberballot_tab").hide();
					$("#ballot_tab").hide();
				}		
				if(value != ""){				
					reloadQuestionGrid();
				}
				
			});	
			/**** status changes then reload grid****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
				reloadQuestionGrid();
				}
			});
			/**** group changes then reload grid ****/
			$("#selectedGroup").change(function(){
				var value=$(this).val();
				if(value!=""){
			    $("#ugparam").val(value);	
				reloadQuestionGrid();
				}
			});		
			/**** Chart Tab ****/
			$('#chart_tab').click(function(){
				$("#selectionDiv1").hide();
				viewChart();
			});		
			/**** Ballot Tab ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
			});		
			/**** Rotation Order Tab ****/
			$('#rotationorder_tab').click(function(){
				$("#selectionDiv1").hide();
				viewRotationOrder();
			});		
			/**** Member Ballot Tab ****/
			$('#memberballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewMemberBallot();
			});		
			/**** show question list method is called by default.****/
			showQuestionList();				
		});
		function loadGroupsFromSessions(){
			if($("#selectedGroup").length>0){
			params="houseType="+$('#selectedHouseType').val()
			+'&sessionYear='+$("#selectedSessionYear").val()
			+'&sessionType='+$("#selectedSessionType").val()
			+'&allowedgroups='+$("#allowedGroups").val();
			$.get('ref/allowedgroups?'+params,function(data){
				if(data.length>0){
					var text="";					
					for(var i=0;i<data.length;i++){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#selectedGroup").empty();
					$("#selectedGroup").html(text);
				}else{
					$("#selectedGroup").empty();
				}
			});
			}
			reloadQuestionGrid();			
		}

		/**** displaying grid ****/					
		function showQuestionList() {
				showTabByIdAndUrl('list_tab','question/list?houseType='+$('#selectedHouseType').val()
						+'&questionType='+$("#selectedQuestionType").val()+'&sessionYear='
						+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+
						"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()						
						);
		}
		/**** new question ****/
		function newQuestion() {
			$("#cancelFn").val("newQuestion");
			//since id of question has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','question/new?'+$("#gridURLParams").val());
		}
		/**** edit question ****/		
		/* function editQuestion() {
			$("#cancelFn").val("editQuestion");						
			var row=$('#key').val();
			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}else{
				showTabByIdAndUrl('details_tab','question/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		} */	
		function editQuestion(row) {
			$("#cancelFn").val("editQuestion");
			
			row=$('#key').val();
			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','question/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete question ****/	
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
		/**** reload grid ****/
		function reloadQuestionGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+
						"&questionType="+$("#selectedQuestionType").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}
		/**** Chart Tab ****/
		function viewChart() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			var parameters = parameters + "&group=" + $("#selectedGroup").val();
			var resourceURL = 'chart/init?' + parameters;
			showTabByIdAndUrl('chart_tab', resourceURL);
		}
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			var parameters = parameters + "&group=" + $("#selectedGroup").val();
			var resourceURL = 'ballot/init?' + parameters;
			showTabByIdAndUrl('ballot_tab', resourceURL);
		}	
		/**** Rotation Order Tab ****/
		function viewRotationOrder() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			var parameters = parameters + "&group=" + $("#selectedGroup").val();
			var resourceURL = 'rotationorder/init?' + parameters;
			showTabByIdAndUrl('rotationorder_tab', resourceURL);
		}	
		/**** Member Ballot Tab ****/
		function viewMemberBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}
			parameters+= "&group=" + $("#selectedGroup").val()
					+"&round=1";
			var resourceURL = 'ballot/memberballot/init?' + parameters;
			showTabByIdAndUrl('memberballot_tab', resourceURL);
		}	
		
	</script>
</head>
<body>
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
			<c:if test="${questionTypeType == 'questions_starred'}">
			<security:authorize access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
			<li>
				<a id="rotationorder_tab" href="#" class="tab">
				   <spring:message code="question.rotationorder" text="Rotation Order"></spring:message>
				</a>
			</li>
			</security:authorize>
			
			<security:authorize access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_SECTION_OFFICER', 
				'QIS_UNDER_SECRETARY_COMMITTEE','SUPER_ADMIN')">
			<li>
				<a id="chart_tab" href="#" class="tab">
				   <spring:message code="question.chart" text="Chart"></spring:message>
				</a>
			</li>
			</security:authorize>
			</c:if>
			
			<security:authorize access="hasAnyRole('QIS_ASSISTANT', 'QIS_UNDER_SECRETARY',
				'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 'QIS_SPEAKER', 'QIS_JOINT_SECRETARY',
				'QIS_SECRETARY', 'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_DEPUTY_SPEAKER', 'QIS_CHAIRMAN',
				'QIS_DEPUTY_CHAIRMAN', 'QIS_SECTION_OFFICER', 'QIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<c:if test="${houseType=='upperhouse'}">
					<li>
					<a id="memberballot_tab" href="#" class="tab">
				   		<spring:message code="question.memberballot" text="Ballot"></spring:message>
					</a>
					</li>			
					</c:if>
				
					<li>
					<a id="ballot_tab" href="#" class="tab">
				   		<spring:message code="question.ballot" text="Ballot"></spring:message>
					</a>
					</li>
			</security:authorize>
		</ul>
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseType==i.type}">
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
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${questionTypes }" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>
			
			</select>|			
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_UNDER_SECRETARY',
			'QIS_DEPUTY_SECRETARY','QIS_PRINCIPAL_SECRETARY','QIS_SPEAKER','QIS_JOINT_SECRETARY',
			'QIS_SECRETARY','QIS_OFFICER_ON_SPECIAL_DUTY','QIS_DEPUTY_SPEAKER','QIS_CHAIRMAN','QIS_DEPUTY_CHAIRMAN',
			'QIS_SECTION_OFFICER','QIS_UNDER_SECRETARY_COMMITTEE')">
			<hr>
			<a href="#" id="select_group" class="butSim">
				<spring:message code="question.group" text="Group"/>
			</a>
			<select name="selectedGroup" id="selectedGroup" style="width:100px;height: 25px;">				
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.formatNumber()}"></c:out></option>	
			</c:forEach> 
			</select> |			
			<a href="#" id="select_status" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:250px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach>
			</select> |			 
			</security:authorize>
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_CLERK')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:100px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach>
			</select> |
			<select name="selectedGroup" id="selectedGroup" style="width:100px;height: 25px;display:none;">			 
			</select>			 
			</security:authorize>						
			<hr>						
		</div>		
				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">		
		
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${questionTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">
		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="ballotSuccessMsg" value="<spring:message code='ballot.success' text='Member Ballot Created Succesfully'/>">			
		<input type="hidden" id="ballotAlreadyCreatedMsg" value="<spring:message code='ballot.success' text='Member Ballot Already Created'/>">			
		<input type="hidden" id="ballotFailedMsg" value="<spring:message code='ballot.failed' text='Member Ballot Couldnot be Created.Try Again'/>">			
		<input type="hidden" id="selectAttendanceRoundMsg" value="<spring:message code='ballot.selectattendanceround' text='Please Select Attendance Type And Round First'/>">			
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		<input type="hidden" id="chartAnsweringDate" name="chartAnsweringDate" value="-">
		</div> 		
</body>
</html>