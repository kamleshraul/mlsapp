<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectedDisplayContent").hide();
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
			
			/****Member's Motions View ****/
			$("#member_motions_view").click(function() {
				$("#selectedDisplayContent").show();
				$("#generateMotion").show();
			});
			
			$("#generateMotion").click(function(){
				$("#selectionDiv1").hide();
				memberMotionsView($("#selectedDisplayContent").val());
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
			
			$("#status_report").click(function(e){
				statusWiseReport();
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
		
		/**** To Generate Status Wise Report ****/
		function statusWiseReport(){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","discussionmotion/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&statusId="+selectedStatus
							+"&statusType="+statusType
							+"&locale="+$("#moduleLocale").val()
							+"&report=DMOIS_STATUSWISE_REPORT&reportout=DmoisStatusReport");
				}
			});
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
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$("#admission_report").attr('href',
					'discussionmotion/report/commonadmissionreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedDiscussionMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_ADMISSION_LETTER'
							+ '&templateName=admission_report'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=admissionLetter');
			$.unblockUI();
		}
		
		
		function showDepartmentIntimationReport(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$("#departmentIntimation_report").attr('href',
					'discussionmotion/report/commonadmissionreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedDiscussionMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_DEPARTMENTINTIMATION_LETTER'
							+ '&templateName=department_intimationletter'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=departmentintimationletter');
			$.unblockUI();
		
			}
		
		function showHouseItemReport(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$("#houseitem_report").attr('href',
					'discussionmotion/report/houseitemreport?discussionmotionId=' + id 
							+ '&locale=' + $("#moduleLocale").val()
							+ '&motionType=' + $("#selectedDiscussionMotionType").val()
							+ '&outputFormat=' + $("#defaultReportFormat").val()
							+ '&reportQuery=DISCUSSIONMOTION_HOUSEITEM_LETTER'
							+ '&templateName=discussionmotion_houseitem_letter'
							+ '&houseType=' + $("#selectedHouseType").val()
							+ '&reportName=houseitemreport');
			$.unblockUI();
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
			<a href="#" id="member_motions_view" class="butSim">
					<spring:message code="motion.member_motions_view" text="Member's Motions View"/>
			</a>
			<select name="selectedDisplayContent" id="selectedDisplayContent" style="width:100px;height: 25px;">			
					<option value="subject"><spring:message code="motion.subject" text="Subject"/></option>
					<option value="details"><spring:message code="dashboard.discussionmotion" text="Details"/></option>	
					<option value="brief_explanation"><spring:message code="cutmotion.viewcitation" text="brief_explanation"/></option>			
			</select>
			<a href="#" id="generateMotion" class="butSim">
					Go
			</a>|		
		
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
				<a href="javascript:void(0);" id="status_report" class="butSim">
							<spring:message code="discussionmotion.statusReport" text="Status-wise Report"/>
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