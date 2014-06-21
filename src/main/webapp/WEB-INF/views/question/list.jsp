<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&questionType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
					+"&subDepartment="+$('#selectedSubDepartment').val()
					);
			
			$("#member_statistics").click(function(){
				memberStatistics();
			});
			/**** new question ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newQuestion();
			});
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editQuestion();
			});
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteQuestion();
			});		
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
			$("#showdemo").click(function(){
				showDemo();
			});
			
			$("#statreport").click(function(){
				statReport();
			});
			
			//---ADDED BY VIKAS------------------
			
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedQuestionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&questionType="+$(this).val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());				
				var standAlone = $("#deviceTypeMaster option[value='"+$(this).val()+"']").text();
				if(standAlone=='questions_halfhourdiscussion_standalone'){
					$("#new_record").html("<spring:message code='question.newStandAlone' text='New'/>");
				}
			});
						
			/**** Generate Intimation Letter ****/			
			$("#generateIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateIntimationLetter();				
			});
			
			/**** Generate Clubbed Intimation Letter ****/			
			$("#generateClubbedIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateClubbedIntimationLetter();				
			});
			
			/**** Generate Member's Questions Report ****/
			$("#memberwise_questions_report").click(function(){
				$("#selectionDiv1").hide();
				memberwiseQuestionsReport();
			});			
			
			/**** Questions Bulletein Report ****/
			$("#group_bulletein_report").click(function(){				
				$(this).attr('href','#');
				groupBulleteinReport();
			});
			
			/**** Questions Bulletein Report ****/
			$("#bulletein_report").click(function(){				
				$(this).attr('href','#');
				bulleteinReport();
			});
			
			/**** Departmentwise Questions Report ****/
			$("#departmentwise_report").click(function(){				
				$("#selectionDiv1").hide();
				departmentwiseQuestionsReport();
			});
			
			/**** Sankshipt Ahwal Report ****/
			$("#ahwal_report").click(function(){				
				//$("#selectionDiv1").hide();
				sankshiptAhwalReport();
			});
			
			//------stats reports as html-----------------------starts----------------
			$("#generateCurrentStatusReport").click(function(){
				var selectedQuestionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedQuestionId.length>=1){
					showCurrentStatusReport('multiple',selectedQuestionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
			$("#generateAdmissionReport").click(function(){
				$("#selectionDiv1").hide();
				showAdmissionReport();
			});
			
			$("#send_message").click(function(){
				sendMessage();
			});
			
			$("#hdDays").hide();
			
			$("#showHDDayWiseReport").click(function(){
				$("#hdDays").toggle();
			});
			
			$("#showhddaywisereport").click(function(){
				$("#selectionDiv1").hide();
				showHDDaywisereport();
			});
			
			$("#showHDStatAndAdmissionReport").click(function(){
				$("#selectionDiv1").hide();
				showHDStatAndAdmissionreport();
			});
			
			$("#showHDGeneralReport").click(function(){
				$("#selectionDiv1").hide();
				showHDGeneralreport();
			});
			
			var selectedDeviceType = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
			if(selectedDeviceType.indexOf("questions_halfhourdiscussion_")==-1){
				$("#hdReportsDiv").hide();
			}else{
				$("#hdReportsDiv").show();
			}
			//------stats reports as html-----------------------ends----------------
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}			
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST','HDS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="question.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="question.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST','HDS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="question.delete" text="Delete"/>
			</a> |			
			<a href="#" id="submitQuestion" class="butSim" style="display: none;">
				<spring:message code="generic.submitquestion" text="submit"/>
			</a> 
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="question.search" text="Search"/>
			</a> |
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<a href="#" id="statreport" class="butSim">
					<spring:message code="question.statreport" text="Generate Statistics Report"/>
				</a> |
			</security:authorize>
			 <security:authorize access="!hasAnyRole('QIS_TYPIST', 'QIS_CLERK','HDS_TYPIST','HDS_CLERK')">
				<a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> |
			</security:authorize>	
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY')">	 
				<a href="#" id="generateAdmissionReport" class="butSim">
					<spring:message code="question.generateAdmissionReport" text="Generate Admission Report"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_TYPIST', 'QIS_CLERK', 'QIS_ASSISTANT', 'HDS_TYPIST','HDS_CLERK', 'HDS_ASSISTANT')">
				<div id="hdReportsDiv" style="display: inline;">
					<a href="#" id="showHDDayWiseReport" class="butSim">
						<spring:message code="question.hdDayWiseReport" text="HD Day wise Report"/>
					</a> <div id="hdDays" style="display: inline; width: 200px;">
						<input type="text" value="0" id="hdDaysForReport" style="width: 50px; border: 1px solid black; border-radius: 2px;"/>
						<a href="javascript:void(0);" id="showhddaywisereport" >Go</a>
					</div>|
					<a href="#" id="showHDStatAndAdmissionReport" class="butSim">
						<spring:message code="question.hdStatAndAdmissionReport" text="HD Stat and Admission Report"/>
					</a> |
					<a href="#" id="showHDGeneralReport" class="butSim">
						<spring:message code="question.hdGeneralReport" text="HD General Report"/>
					</a> |
				</div>
			</security:authorize>		
			<security:authorize access="!hasAnyRole('QIS_TYPIST','QIS_CLERK','HDS_TYPIST','HDS_CLERK')">	 
				<a href="#" id="send_message" class="butSim">
					<spring:message code="question.sendMessage" text="Send Message"/>
				</a> |
			</security:authorize>			
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','HDS_SECTION_OFFICER')">
				<hr>
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="intimationLetterFilter" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='question.intimationletter.member' text='member' /></option>
						<option value="department"><spring:message code='question.intimationletter.department' text='department' /></option>
						<option value="prestatus"><spring:message code='question.intimationletter.prestatus' text='pre-status' /></option>
						<option value="discussiondate"><spring:message code='question.intimationletter.discussiondate' text='discussion date' /></option>
				</select> | 
				<a href="#" id="generateClubbedIntimationLetter" class="butSim">
					<spring:message code="question.generateClubbedIntimationLetter" text="Generate Clubbed Intimation Letter"/>
				</a> | 				
			</security:authorize>	
			<hr> 
			<a href="#" id="memberwise_questions_report" class="butSim link">
				<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
			</a> |			
			<a href="#" id="group_bulletein_report" class="butSim link">
				<spring:message code="question.group_bulletein_report" text="Group Bulletein Report"/>
			</a> |
			<a href="#" id="bulletein_report" class="butSim link">
				<spring:message code="question.bulletein_report" text="Bulletein Report"/>
			</a> |
			<a href="#" id="departmentwise_report" class="butSim link">
				<spring:message code="question.departmentwise_report" text="Department's Questions Report"/>
			</a> |
			<a href="#" id="ahwal_report" class="butSim link">
				<spring:message code="question.ahwal_report" text="Sankshipt Ahwal Report"/>
			</a> |
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>