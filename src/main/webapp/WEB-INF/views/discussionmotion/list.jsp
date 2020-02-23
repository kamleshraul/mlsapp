<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();							
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&discussionMotionType="+$("#selectedDiscussionMotionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()
				);
			/**** show/hide unstarred yaadi link as per selected devicetype ****/
			var currentDeviceType = $("#deviceTypeMaster option[value='"+ $("#selectedDiscussionMotionType").val() + "']").text();
			
			/**** new motion ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newDiscussionMotion();
			});
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editDiscussionMotion();
			});
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteDiscussionMotion();
			});		
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
			/**** Generate Intimation Letter ****/			
			$("#generateIntimationLetter").click(function(){
				$(this).attr('href','#');
				generateIntimationLetter();				
			});
			
			$("#admission_report").click(function(){
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showAdmissionReport(selectedId[0]);
				}
			});
			
			//------stats reports as html-----------------------starts----------------
			$("#generateCurrentStatusReport").click(function(){
				var selectedDiscussionMotionId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				
				if(selectedDiscussionMotionId.length>=1){
					showCurrentStatusReport('multiple',selectedDiscussionMotionId);
				}else{
					showCurrentStatusReport('all','');
				}
			});
			
						
			//---ADDED BY ANAND------------------
			
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());		
			
			$("#selectedQuestionType").change(function(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&discussionMotionType="+$(this).val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				$('#gridURLParams_ForNew').val($('#gridURLParams').val());				
			});
			
			$("#departmentIntimation_report").click(function(){
				
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showDepartmentIntimationReport(selectedId[0]);
					
				}
			});

			$("#houseitem_report").click(function(){
			
				var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
				if(selectedId!=undefined && selectedId.length>=1){
					showHouseItemReport(selectedId[0]);
					
				}
			});

		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'discussionmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}	
		$("#admission_report").click(function(){
			
			var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedId!=undefined && selectedId.length>=1){
				showAdmissionReport(selectedId[0]);
				
			}
		});
		

		
		function showAdmissionReport(id){
			
			
			$("#admission_report").attr('href',
					'discussionmotion/report/commonadmissionreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedDiscussionMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_ADMISSION_LETTER'
							+ '&templateName=discussionmotion_admission_report'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=admissionLetter');
		
		}
		
		
		function showDepartmentIntimationReport(id){
			$("#departmentIntimation_report").attr('href',
					'discussionmotion/report/commonadmissionreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedDiscussionMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_DEPARTMENTINTIMATION_LETTER'
							+ '&templateName=department_intimationletter'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=departmentintimationletter');
		
			}
		
		function showHouseItemReport(id){
			$("#houseitem_report").attr('href',
					'discussionmotion/report/houseitemreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_HOUSEITEM_LETTER'
							+ '&templateName=discussionmotion_houseitem_letter'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=departmentintimationletter');
		
			}
		
		$("#memberofoppositionreport").click(function(){
			
			var selectedId = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedId!=undefined && selectedId.length>=1){
				showmemberofoppositionreportReport(selectedId[0]);
				
			}
		});
		
		function showmemberofoppositionreportReport(id){
			
			
			$("#admission_report").attr('href',
					'discussionmotion/report/memberofoppositionreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_ADMISSION_LETTER'
							+ '&templateName=discussionmotion_admission_report'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=admissionLetter');
		
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','DMOIS_TYPIST')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','DMOIS_TYPIST','DMOIS_TYPIST')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a>|
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> 			
		
			<security:authorize access="hasAnyRole('DMOIS_ASSISTANT','DMOIS_SECTION_OFFICER','DMOIS_CLERK')">
				<hr>
							
				<a href="javascript:void(0);" id="admission_report" class="butSim" >
					<c:choose>
						<c:when test="${houseType=='lowerhouse'}">
							<spring:message code="generic.admissionLetter" text="Admission Letter"/>
						</c:when>
						<c:when test="${houseType=='upperhouse'}">
							<spring:message code="generic.admissionLetter" text="Admission Letter"/>
						</c:when>
					</c:choose>
				</a>
				|
				<a href="javascript:void(0);" id="departmentIntimation_report" class="butSim" >
					<c:choose>
						<c:when test="${houseType=='lowerhouse'}">
							<spring:message code="generic.departmentIntimation_report" text="Department Intimation Letter"/>
						</c:when>
						<c:when test="${houseType=='upperhouse'}">
							<spring:message code="generic.departmentIntimation_report" text="Department Intimation Letter"/>
						</c:when>
					</c:choose>
				</a>
				|
				 <a href="#" id="generateCurrentStatusReport" class="butSim">
					<spring:message code="discussionmotion.generateCurrentStatusReport" text="Generate Current Status Report"/>
				</a> 
				|
						
					
								<a href="javascript:void(0);" id="houseitem_report" class="butSim" >
							<c:choose>
								<c:when test="${houseType=='lowerhouse'}">
									<spring:message code="generic.houseitem_report" text="House Item"/>
								</c:when>
								<c:when test="${houseType=='upperhouse'}">
									<spring:message code="generic.houseitem_report" text="House Item"/>
								</c:when>
							</c:choose>
						</a>
				<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">	
							
				<a href="javascript:void(0);" id="memberofoppositionreport" class="butSim" >
					<c:choose>
						<c:when test="${houseType=='lowerhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Opposition"/>
						</c:when>
						<c:when test="${houseType=='upperhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Opposition"/>
						</c:when>
					</c:choose>
				</a>
					|
						
							
				<a href="javascript:void(0);" id="partyreport" class="butSim" >
					<c:choose>
						<c:when test="${houseType=='lowerhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Parties"/>
						</c:when>
						<c:when test="${houseType=='upperhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Parties"/>
						</c:when>
					</c:choose>
				</a>
					|
						
							
				<a href="javascript:void(0);" id="departmentreport" class="butSim" >
					<c:choose>
						<c:when test="${houseType=='lowerhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Department"/>
						</c:when>
						<c:when test="${houseType=='upperhouse'}">
							<spring:message code="generic.admissionLetter" text="Letter to Department"/>
						</c:when>
					</c:choose>
				</a>
								
								|
						
							
								<a href="javascript:void(0);" id="citationreport" class="butSim" >
							<c:choose>
								<c:when test="${houseType=='lowerhouse'}">
									<spring:message code="generic.admissionLetter" text="citation"/>
								</c:when>
								<c:when test="${houseType=='upperhouse'}">
									<spring:message code="generic.admissionLetter" text="citation"/>
								</c:when>
							</c:choose>
						</a>
						</c:if>
				<hr> 
		
			
			</security:authorize>
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