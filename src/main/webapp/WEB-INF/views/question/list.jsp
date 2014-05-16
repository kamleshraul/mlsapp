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
			<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER','HDS_SECTION_OFFICER')">
				<a href="#" id="generateIntimationLetter" class="butSim">
					<spring:message code="question.generateIntimationLetter" text="Generate Intimation Letter"/>
				</a> 				
				<select id="memDeptReport" size="1" style="height: 20px; font-size: 12px; min-width: 50px; vertical-align: middle;">
						<option value="-">-</option>
					 	<option value="member"><spring:message code='generic.usergroup.member' text='member' /></option>
						<option value="department"><spring:message code='generic.usergroup.department' text='department' /></option>
				</select>
				|
			</security:authorize>				
			 <security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<a href="#" id="statreport" class="butSim">
					<spring:message code="question.statreport" text="Generate Statistics Report"/>
				</a> |
			 </security:authorize>
			<a href="#" id="generateCurrentStatusReport" class="butSim">
				<spring:message code="question.generateCurrentStatusReport" text="Generate Current Status Report"/>
			</a> |	
			<security:authorize access="hasAnyRole('QIS_PRINCIPAL_SECRETARY')">	 
				<a href="#" id="generateAdmissionReport" class="butSim">
					<spring:message code="question.generateAdmissionReport" text="Generate Admission Report"/>
				</a> |
			</security:authorize>
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