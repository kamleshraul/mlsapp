<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><spring:message code="specialmentionnotice" text="Special Mention Notice"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		/**** Load Session ****/
		function loadSession(){
			$.get("ref/sessionbyhousetype/" + $("#selectedHouseType").val()
				+ "/" + $("#selectedSessionYear").val() + "/" + $("#selectedSessionType").val(),
				function(data){
					if(data){
						$("#loadedSession").val(data.id);
						loadMembers();
						//loadParties();
					}
				});
		}
		function loadMembers(){
			memberArray = [];
			$.get('ref/alleligiblemembers?session='+$("#loadedSession").val(), function(data){
				if(data.length>0){
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					for(var i = 0; i < data.length; i++){
						memberArray.push(data[i].name);
						text+="<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#members").empty();
					$("#members").html(text);
				}
			});
		}
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();
			$("#bulkputup_tab").hide();
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
				showSpecialMentionNoticeList();				
			});			
			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					$.get('ref/specialmentionnotice/specialmentionnoticedatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultSpecialMentionNoticeDate = data[data.length-1][0];
							$('#selectedSpecialMentionNoticeDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultSpecialMentionNoticeDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedSpecialMentionNoticeDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isSpecialMentionNoticeDateSelected').attr('checked', 'checked');
						$('#selectedSpecialMentionNoticeDate').removeAttr('disabled');
						/** Update Motion Types as per Selected House Type **/
						
						$.get('ref/devicetypesforhousetype?houseType='+$('#selectedHouseType').val()
								+'&deviceType='+$('#currentDeviceType').val(), function(data) {
							var deviceTypeSelectHtmlText = "";
							for(var i=0 ;i<data.length; i++){
								deviceTypeSelectHtmlText += "<option value='" + data[i].id + "'>" + data[i].displayName;
							}
							$("#selectedMotionType").html(deviceTypeSelectHtmlText);
						}).done(function() {
							reloadSpecialMentionNoticeGrid();
						}).fail(function() {
							console.log("3.error");
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							//resetControls();
							scrollTop();
						});						
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});								
				}	
			});		
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){	
					$.get('ref/specialmentionnotice/specialmentionnoticedatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultSpecialMentionNoticeDate = data[data.length-1][0];
							$('#selectedSpecialMentionNoticeDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultSpecialMentionNoticeDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedSpecialMentionNoticeDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isSpecialMentionNoticeDateSelected').attr('checked', 'checked');
						$('#selectedSpecialMentionNoticeDate').removeAttr('disabled');
						reloadSpecialMentionNoticeGrid();
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});						
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					$.get('ref/specialmentionnotice/specialmentionnoticedatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultSpecialMentionNoticeDate = data[data.length-1][0];
							$('#selectedSpecialMentionNoticeDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultSpecialMentionNoticeDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedSpecialMentionNoticeDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isSpecialMentionNoticeDateSelected').attr('checked', 'checked');
						$('#selectedSpecialMentionNoticeDate').removeAttr('disabled');
						reloadSpecialMentionNoticeGrid();
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});										
				}			
			});
			
			/**** adjournmentmotion type changes ****/		
			$("#selectedMotionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadSpecialMentionNoticeGrid();							
				}				
			});	
			/**** status changes then reload grid ****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadSpecialMentionNoticeGrid();
					$("#selectedFileCount").val("-");
				}
			});
			/**** sub-department changes then reload grid ****/
			$("#selectedSubDepartment").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadSpecialMentionNoticeGrid();
					$("#selectedFileCount").val("-");
				}
			});
			
			/**** Search Tab ****/
			$('#search_tab').click(function() {
				$("#selectionDiv1").hide();
				searchInt();
			});
			
			/**** special mention notice date changes then reload grid ****/
			$("#selectedSpecialMentionNoticeDate").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadSpecialMentionNoticeGrid();
					$("#selectedFileCount").val("-");
				}
			});
			$('#isSpecialMentionNoticeDateSelected').change(function(){	
				if($("#isSpecialMentionNoticeDateSelected").is(":checked")) {
					$('#selectedSpecialMentionNoticeDate').removeAttr('disabled');
					reloadSpecialMentionNoticeGrid();
					$("#selectedFileCount").val("-");
				} else {
					$('#selectedSpecialMentionNoticeDate').attr('disabled', 'disabled');
					reloadSpecialMentionNoticeGrid();
					$("#selectedFileCount").val("-");
				}				
			});
			/**** Submission Time Window ****/
			$("#submission_time_window").click(function(event, isHighSecurityValidationRequired){			
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				$(this).attr('href','#');
				setSubmissionTimeWindow();
			});		
		
			/**** show specialmentionnotice list method is called by default.****/
			showSpecialMentionNoticeList();	
			
			/**** Toggle Reports Div ****/
			$("#reports_link").click(function(e){
				$("#assistantReportDiv").toggle("slow");
			});
			$("#members").change(function(){
				var val = $(this).val();
				if(val!="" && val!='-'){
					memberWiseReport($(this).val());
				}
			});
			$("#department_report").click(function(e){
				var dept = $("#selectedSubDepartment").val();
				if(dept!="" && dept!='0'){
					departmentWiseReport(dept);	
				}
			});
		});
			
		/**** displaying grid ****/					
		function showSpecialMentionNoticeList() {
			var selectedSpecialMentionNoticeDate = "";
			if($("#isSpecialMentionNoticeDateSelected").is(":checked")) {
				selectedSpecialMentionNoticeDate = convertToDbFormat($('#selectedSpecialMentionNoticeDate').val());
			}
			showTabByIdAndUrl('list_tab','specialmentionnotice/list?houseType='+$('#selectedHouseType').val()
								+'&motionType='+$("#selectedMotionType").val()+'&sessionYear='
								+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()
								+"&ugparam="+$("#ugparam").val()
								+"&status="+$("#selectedStatus").val()
								+"&role="+$("#srole").val()
								+"&usergroup="+$("#currentusergroup").val()
								+"&usergroupType="+$("#currentusergroupType").val()				
								+"&specialMentionNoticeDate="+selectedSpecialMentionNoticeDate
								+"&subDepartment="+$("#selectedSubDepartment").val()
							);
			
			loadSession();
		}
		
		/**** Search Facility ****/
		function searchInt(id){
			//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="searchfacility=yes&usergroup="+$("#currentusergroup").val() +
				        "&usergroupType="+$("#currentusergroupType").val() +
				        "&houseType="+$("#selectedHouseType").val() +
				        "&sessionType="+$("#selectedSessionType").val() +
				        "&sessionYear="+$("#selectedSessionYear").val() +
				        "&deviceType="+$("#selectedMotionType").val();		
			/* $.get('clubentity/init?'+params,function(data){
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				if(data){
					$.unblockUI();
				}
				$("#clubbingResultDiv").html(data);
				$("#clubbingResultDiv").show();
				$("#referencingResultDiv").hide();
				$("#assistantDiv").hide();
				$("#backToQuestionDiv").show();			
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			}); */
			showTabByIdAndUrl('search_tab','devicesearch/init?'+params);
		}
		
		function memberSpecialMentionNoticesView() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&motionType=" + $("#selectedMotionType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_SPECIALMENTIONNOTICES_VIEW"
			+ "&reportout=member_specialmentionnotice_view";
			showTabByIdAndUrl('details_tab','specialmentionnotice/report/generalreport?'+parameters);
		}
		
		/**** new special mention notice ****/
		function newSpecialMentionNotice() {		
			$("#cancelFn").val("newSpecialMentionNotice");
			//since id of adjournmentmotion has not been created so key is set to empty value
			$("#key").val("");	
			showTabByIdAndUrl('details_tab','specialmentionnotice/new?'+$("#gridURLParams").val());
			$("#selectionDiv1").hide();	
		}
		/**** edit special mention notice  ****/		
		function editSpecialMentionNotice(row) {
			$("#cancelFn").val("editSpecialMentionNotice");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','specialmentionnotice/'+row+'/edit?'+$("#gridURLParams").val());
				$("#selectionDiv1").hide();	
			}			
		}
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);			
			showTabByIdAndUrl('details_tab', 'specialmentionnotice/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete special mention notice  ****/	
		function deleteSpecialMentionNotice() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('specialmentionnotice/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				        	showSpecialMentionNoticeList();
				        }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
			        }
				}});
			}
		}	
		
		function convertToDbFormat(date){
			if(date!="") {
				var splitResult=date.split("/");
				if(splitResult.length==3){
					return splitResult[2]+"-"+splitResult[1]+"-"+splitResult[0];
				}else{
					return "Invalid Date";
				}
			} else {
				return "";
			}		
		}
		
		/**** reload grid ****/
		function reloadSpecialMentionNoticeGrid(){
			var gridWidth = $('#grid').jqGrid('getGridParam', 'width');
			var selectedSpecialMentionNoticeDate = "";
			if($("#isSpecialMentionNoticeDateSelected").is(":checked")) {
				selectedSpecialMentionNoticeDate = convertToDbFormat($('#selectedSpecialMentionNoticeDate').val());
			}
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()+
					"&sessionType="+$("#selectedSessionType").val()+
					"&motionType="+$("#selectedMotionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()		
					+"&specialMentionNoticeDate="+selectedSpecialMentionNoticeDate
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			console.log($("#gridURLParams").val());
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");	
			/**** show/hide special mention notice  date column as per user selection ****/
			if($("#isSpecialMentionNoticeDateSelected").is(":checked")) {
				$("#grid").jqGrid('hideCol', 'formattedSpecialMentionNoticeDate');					
			} else {
				$("#grid").jqGrid('showCol', 'formattedSpecialMentionNoticeDate');					
			}			
			$("#grid").jqGrid('setGridWidth', gridWidth, true);
			
			loadSession();
		}
		
		/**** Set Submission Time Window ****/
	 	function setSubmissionTimeWindow() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			if(selectedSpecialMentionNoticeDate==undefined || selectedSpecialMentionNoticeDate=="") {
				$.prompt("Please select adjourning date for setting submission window!");
				return false;
			} else {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&motionType="+$("#selectedMotionType").val()
				+"&specialMentionNticeDate="+$("#selectedSpecialMentionNoticeDate").val()
				+"&formattedSpecialMentionNoticeDate="+$("#selectedSpecialMentionNoticeDate").text()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val();
				var resourceURL='specialmentionnotice/submissionwindow?'+parameters;
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:360,height:270});
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}			
		}
		
		/**** Current Status Report Generation ****/
		function showCurrentStatusReport(val, moId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"+$("#selectedMotionType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', "specialmentionnotice/report/currentstatusreport?device="+ device +"&reportType="+val+"&moId="+moId);
		}
		/**** Submitted Motions Report Generation ****/
		function generateSubmittedMotionsReport() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			if(selectedSpecialMentionNoticeDate==undefined || selectedSpecialMentionNoticeDate=="") {
				$.prompt("Please select adjourning date of submitted motions");
				return false;
			} else {
				$("#smis_submitted_report").attr('href',
						'specialmentionnotice/report/submittedmotions?'
						+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=SMIS_SUBMITTED_MOTIONS_REPORT');
			}			
		}
		/**** Admitted Motions Report Generation ****/
		function generateAdmittedMotionsReport() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			if(selectedSpecialMentionNoticeDate==undefined || selectedSpecialMentionNoticeDate=="") {
				$.prompt("Please select special mention notice  date of admitted motions");
				return false;
			} else {
				$("#smis_admitted_report").attr('href',
						'specialmentionnotice/report/admittedmotions?'
						+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=SMIS_ADMITTED_MOTIONS_REPORT');
			}			
		}
		/**** Admitted Motions Report Generation ****/
		function generateAdmittedMotionsReportForReporting() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			if(selectedSpecialMentionNoticeDate==undefined || selectedSpecialMentionNoticeDate=="") {
				$.prompt("Please select special mention notice  date of admitted motions");
				return false;
			} else {
				$("#smis_admitted_reportingbranch").attr('href',
						'specialmentionnotice/report/admittedmotions?'
						+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=SMIS_ADMITTED_MOTIONS_REPORT_REPORTING_BRANCH');
			}			
		}
		/**** Rejected Motions Report Generation ****/
		function generateRejectedMotionsReport() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			if(selectedSpecialMentionNoticeDate==undefined || selectedSpecialMentionNoticeDate=="") {
				$.prompt("Please select special mention notice  date of rejected motions");
				return false;
			} else {
				$("#smis_rejected_report").attr('href',
						'specialmentionnotice/report/rejectedmotions?'
						+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=SMIS_REJECTED_MOTIONS_REPORT');
			}			
		}
		/**** Register Report Generation ****/
		function generateRegisterReport() {
			var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
			$("#smis_register_report").attr('href',
					'specialmentionnotice/report/register?'
					+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
					+'&sessionId=' + $("#loadedSession").val()
					+'&reportQueryName=SMIS_REGISTER_REPORT');
		}
		/**** Memberwise Devices Report Generation ****/
		function memberWiseReport(memberId){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","specialmentionnotice/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&memberId="+memberId 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=SMIS_MEMBER_WISE_REPORT&reportout=specialMentionNoticeMemberReport");
				}
			});
		}
		/**** Departmentwise Devices Report Generation ****/
		function departmentWiseReport(dept){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					showTabByIdAndUrl("details_tab","specialmentionnotice/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&subd="+dept 
							+"&locale="+$("#moduleLocale").val()
							+"&report=SMIS_DEPARTMENT_WISE_REPORT&reportout=specialMentionNoticeDepartmentReport");
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
			<security:authorize access="hasAnyRole('SMIS_CLERK','SMIS_ASSISTANT', 'SMIS_UNDER_SECRETARY',
			'SMIS_DEPUTY_SECRETARY','SMIS_PRINCIPAL_SECRETARY','SMIS_SPEAKER', 'SMIS_JOINT_SECRETARY',
			'SMIS_SECRETARY', 'SMIS_OFFICER_ON_SPECIAL_DUTY', 'SMIS_DEPUTY_SPEAKER', 'SMIS_CHAIRMAN',
			'SMIS_DEPUTY_CHAIRMAN', 'SMIS_SECTION_OFFICER', 'SMIS_UNDER_SECRETARY_COMMITTEE',
			'SUPER_ADMIN','SMIS_ADDITIONAL_SECRETARY')">
			<li>
				<a id="search_tab" href="#" class="tab"><spring:message code="question.searchT" text="Search"></spring:message></a>
			</li>
			</security:authorize>
		<%-- 	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('SMIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>	 --%>		
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="adjournmentmotion.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:99px;height: 25px;">			
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
				<spring:message code="adjournmentmotion.sessionyear" text="Year"/>
			</a>
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:99px;height: 25px;">				
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
				<spring:message code="adjournmentmotion.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:99px;height: 25px;">				
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
						
			<a href="#" id="select_motionType" class="butSim">
				<spring:message code="adjournmentmotion.motionType" text="Motion Type"/>
			</a>
			<select name="selectedMotionType" id="selectedMotionType" style="width:99px;height: 25px;">	
			<c:forEach items="${motionTypeVOs}" var="i">
			<c:choose>
			<c:when test="${motionType==i.id}">			
			<option value="${i.id}" selected="selected"><c:out value="${i.displayName}"></c:out></option>
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>	
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${motionTypes }" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>			
			</select>|		
			
			<security:authorize
				access="hasAnyRole('SMIS_ADMIN','SMIS_ASSISTANT','SMIS_UNDER_SECRETARY',
			'SMIS_DEPUTY_SECRETARY','SMIS_PRINCIPAL_SECRETARY','SMIS_SPEAKER','SMIS_JOINT_SECRETARY',
			'SMIS_SECRETARY','SMIS_OFFICER_ON_SPECIAL_DUTY','SMIS_DEPUTY_SPEAKER','AMOIS_CHAIRMAN','SMIS_DEPUTY_CHAIRMAN',
			'SMIS_SECTION_OFFICER','SMIS_UNDER_SECRETARY_COMMITTEE','SMIS_ADDITIONAL_SECRETARY','SMIS_CLERK')">
			
			<hr>
			<a href="#" id="select_status" class="butSim">
				<spring:message code="adjournmentmotion.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:250px;height: 25px;">		
			<option value="0" selected="selected">--<spring:message code="please.select" text="Please Select"/>--</option>	
			<c:forEach items="${status}" var="i">
			<c:choose>
			<c:when test="${selectedStatusId==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>	
			</c:forEach>
			</select> |
			
			<div id='specialMentionNoticeDepartment' style="display:inline;">
			<a href="#" id="select_department" class="butSim"> <spring:message
					code="adjournmentmotion.department" text="Department" />
			</a>
			<select name="selectedSubDepartment" id="selectedSubDepartment"
				style="width: 250px; height: 25px;">
				<option value="0" selected="selected"><spring:message code="please.select"></spring:message></option>
				<c:forEach items="${subDepartments}" var="i">
					<option value="${i.id}">
						<c:out value="${i.name}"></c:out>
					</option>
				</c:forEach>
			</select>|
			</div>
			</security:authorize>
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMIS_TYPIST')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="adjournmentmotion.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:99px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<c:choose>
			<c:when test="${selectedStatusId==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>	
			</c:forEach>
			</select> |
			</security:authorize>

			<hr>
			
			<a href="#" id="select_specialmentionnoticedate" class="butSim"><spring:message code="specialmentionnotice.selectspecialmentionnoticedate" text="Select Special Mention Notice Date"/></a>
			<input class="sCheck" type="checkbox" id="isSpecialMentionNoticeDateSelected" name="isSpecialMentionNoticeDateSelected" checked="checked"/>
			<select name="selectedSpecialMentionNoticeDate" id="selectedSpecialMentionNoticeDate" style="width:130px;height: 25px;">	
			<c:forEach items="${sessionDates}" var="i">
				<option value="${i[0]}" ${i[0]==defaultSpecialMentionNoticeDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
			</c:forEach>
			</select>	
		<%-- 	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMIS_ASSISTANT','SMIS_SECTION_OFFICER')">
			<a href="#" id="submission_time_window" class="butSim">
				<spring:message code="amois.submission_time_window" text="Submission Time Window"/>
			</a> |
			</security:authorize>	 --%>
			
	 		<security:authorize access="hasAnyRole('SMIS_CLERK', 'SMIS_ASSISTANT', 'SMIS_SECTION_OFFICER', 'SMIS_SECRETARY')">					
				<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
					<spring:message code="adjournmentmotion.reports" text="Reports"/>
				</a>
				<div id="assistantReportDiv" style="display: none; border: 1px solid green; border-radius: 6px; margin: 10px 0px 10px 0px; padding: 5px;">
					<a href="javascript:void(0);" id="member_report" class="butSim" >
						<spring:message code="generic.memberWiseReport" text="Member-wise Report"/>
					</a>						
					<select id="members" class="sSelect" style="display: inline; width:100px;">
					</select>|
					<a href="javascript:void(0);" id="department_report" class="butSim" >
						<spring:message code="generic.departmentWiseReport" text="Department-wise Report"/>
					</a>|
					<%-- <a href="javascript:void(0);" id="party_report" class="butSim" >
						<spring:message code="generic.partyWiseReport" text="Party-wise Report"/>
					</a>						
					<select id="parties" class="sSelect" style="display: inline; width:100px;">
					</select>|<br> --%>
					<hr>
				</div>
			</security:authorize>	 
				
			<%-- <security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','SMIS_ASSISTANT')">	
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="adjournmentmotion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:99px;height: 25px;">			
			<option value="30">30</option>
			<option value="25">25</option>
			<option value="20">20</option>
			<option value="15">15</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			</security:authorize> --%>				
			
			<hr>										
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">				
		<input type="hidden" name="ugparam" id="ugparam" value="${ugparam }">
		<input type="hidden" name="srole" id="srole" value="${role }">		
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">		
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${motionTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		</div> 
		<input type="hidden" id="allSelectedMsg" value="<spring:message code='generic.allOption' text='---- All ----'/>"/>
		<input type="hidden" id="isAdjourningDateTodayPrompt" value="<spring:message code='adjournmentmotion.isAdjourningDateTodayPrompt' text='is adjourning date today?'/>"/>
		<input type="hidden" id="yes" value="<spring:message code='generic.yes' text='Yes'/>"/>
		<input type="hidden" id="no" value="<spring:message code='generic.no' text='No'/>"/>
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" id="loadedSession" value="" />
	

</body>
</html>