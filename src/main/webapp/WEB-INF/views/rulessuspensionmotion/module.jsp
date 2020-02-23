<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="rulessuspensionmotion_${houseType}.list" text="List Of Rules Suspension Motions"/></title>
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
				showRulesSuspensionMotionList();				
			});				
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					$.get('ref/rulessuspensionmotion/rulessuspensiondatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()
							+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultRulesSuspensionDate = data[data.length-1][0];
							$('#selectedRuleSuspensionDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultRulesSuspensionDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedRuleSuspensionDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isRuleSuspensionDateSelected').attr('checked', 'checked');
						$('#selectedRuleSuspensionDate').removeAttr('disabled');
						/** Update Motion Types as per Selected House Type **/
						$.get('ref/devicetypesforhousetype?houseType='+$('#selectedHouseType').val()
								+'&deviceType='+$('#currentDeviceType').val(), function(data) {
							var deviceTypeSelectHtmlText = "";
							for(var i=0 ;i<data.length; i++){
								deviceTypeSelectHtmlText += "<option value='" + data[i].id + "'>" + data[i].displayName;
							}
							$("#selectedMotionType").html(deviceTypeSelectHtmlText);
						}).done(function() {
							reloadRulesSuspensionMotionGrid();
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
					$.get('ref/rulessuspensionmotion/rulessuspensiondatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultRulesSuspensionDate = data[data.length-1][0];
							$('#selectedRuleSuspensionDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultRulesSuspensionDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedRuleSuspensionDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isRuleSuspensionDateSelected').attr('checked', 'checked');
						$('#selectedRuleSuspensionDate').removeAttr('disabled');
						reloadRulesSuspensionMotionGrid();
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
					$.get('ref/rulessuspensionmotion/rulesuspensiondatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultRulesSuspensionDate = data[data.length-1][0];
							$('#selectedRuleSuspensionDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultRulesSuspensionDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedRuleSuspensionDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
					}).done(function() {
						$('#isRuleSuspensionDateSelected').attr('checked', 'checked');
						$('#selectedRuleSuspensionDate').removeAttr('disabled');
						reloadRulesSuspensionMotionGrid();
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
			/**** rulessuspensionmotion type changes ****/		
			$("#selectedMotionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadRulesSuspensionMotionGrid();							
				}				
			});	
			/**** status changes then reload grid ****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadRulesSuspensionMotionGrid();
				}
			});
			/**** sub-department changes then reload grid ****/
			$("#selectedSubDepartment").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadRulesSuspensionMotionGrid();
				}
			});
			/**** adjourning date changes then reload grid ****/
			$("#selectedRuleSuspensionDate").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadRulesSuspensionMotionGrid();
				}
			});
			$('#isRuleSuspensionDateSelected').change(function(){	
				if($("#isRuleSuspensionDateSelected").is(":checked")) {
					$('#selectedRuleSuspensionDate').removeAttr('disabled');
					reloadRulesSuspensionMotionGrid();
				} else {
					$('#selectedRuleSuspensionDate').attr('disabled', 'disabled');
					reloadRulesSuspensionMotionGrid();
				}				
			});
			/**** Submission Time Window ****/
			$("#submission_time_window").click(function(event, isHighSecurityValidationRequired){
				if($("#currentusergroupType").val() == 'member'){
					isHighSecurityValidationRequired = false;
				}
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				$(this).attr('href','#');
				setSubmissionTimeWindow();
			});			
			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutup();
			});	
			/**** Bulk Putup Assistant ****/
			$("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			});		
			/**** show rulessuspensionmotion list method is called by default.****/
			showRulesSuspensionMotionList();		
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
		});
		/**** displaying grid ****/					
		function showRulesSuspensionMotionList() {
			var selectedRuleSuspensionDate = "";
			if($("#isRuleSuspensionDateSelected").is(":checked")) {
				selectedRuleSuspensionDate = convertToDbFormat($('#selectedRuleSuspensionDate').val());
			}
			showTabByIdAndUrl('list_tab','rulessuspensionmotion/list?houseType='+$('#selectedHouseType').val()
					+'&motionType='+$("#selectedMotionType").val()+'&sessionYear='
					+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+
					"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()				
					+"&ruleSuspensionDate="+selectedRuleSuspensionDate);
			
			loadSession();
		}	
		function membersRulesSuspensionMotionsView() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&motionType=" + $("#selectedMotionType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_RULESSUSPENSIONMOTIONS_VIEW"
			+ "&reportout=member_rulessuspensionmotions_view";
			showTabByIdAndUrl('details_tab','rulessuspensionmotion/report/generalreport?'+parameters);
		}
		/**** new rulessuspensionmotion ****/
		function newRulesSuspensionMotion() {		
			$("#cancelFn").val("newrulessuspensionmotion");
			//since id of rulessuspensionmotion has not been created so key is set to empty value
			$("#key").val("");	
			showTabByIdAndUrl('details_tab','rulessuspensionmotion/new?'+$("#gridURLParams").val());
			$("#selectionDiv1").hide();	
		}
		/**** edit rulessuspensionmotion ****/		
		function editRulesSuspensionMotion(row) {
			$("#cancelFn").val("editrulessuspensionmotion");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','rulessuspensionmotion/'+row+'/edit?'+$("#gridURLParams").val());
				$("#selectionDiv1").hide();	
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);			
			showTabByIdAndUrl('details_tab', 'rulessuspensionmotion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete rulessuspensionmotion ****/	
		function deleteRulesSuspensionMotion() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('rulessuspensionmotion/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				        	showRulesSuspensionMotionList();
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
		function reloadRulesSuspensionMotionGrid(){
			var gridWidth = $('#grid').jqGrid('getGridParam', 'width');
			var selectedRuleSuspensionDate = "";
			if($("#isRuleSuspensionDateSelected").is(":checked")) {
				selectedRuleSuspensionDate = convertToDbFormat($('#selectedRuleSuspensionDate').val());
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
					+"&ruleSuspensionDate="+selectedRuleSuspensionDate);
			
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");	
			/**** show/hide adjourning date column as per user selection ****/
			if($("#isRuleSuspensionDateSelected").is(":checked")) {
				$("#grid").jqGrid('hideCol', 'formattedRulesSuspensionDate');					
			} else {
				$("#grid").jqGrid('showCol', 'formattedRulesSuspensionDate');					
			}			
			$("#grid").jqGrid('setGridWidth', gridWidth, true);
			
			loadSession();
		}
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'rulessuspensionmotion/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}	
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&motionType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='rulessuspensionmotion/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}	
		/**** Set Submission Time Window ****/
		function setSubmissionTimeWindow() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select rules suspension date for setting submission window!");
				return false;
			} else {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&motionType="+$("#selectedMotionType").val()
				+"&ruleSuspensionDate="+$("#selectedRuleSuspensionDate").val()
				+"&formattedRulesSuspensionDate="+$("#selectedRuleSuspensionDate").text()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val();
				var resourceURL='rulessuspensionmotion/submissionwindow?'+parameters;
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
			showTabByIdAndUrl('details_tab', "rulessuspensionmotion/report/currentstatusreport?device="+ device +"&reportType="+val+"&moId="+moId);
		}
		/**** Bhag 1 Report Generation ****/
		function generateBhag1Report() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select adjourning date of bhag 1 motions");
				return false;
			} else {
				$("#rsmois_bhag_1_report").attr('href',
						'rulessuspensionmotion/report/bhag1?'
						+'ruleSuspensionDate=' + selectedRuleSuspensionDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=RSMOIS_BHAG1_REPORT');
			}			
		}
		/**** Bhag 2 Report Generation ****/
		function generateBhag2Report() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select rules suspension date of bhag 2 motions");
				return false;
			} else {
				$("#amois_bhag_2_report").attr('href',
						'rulessuspensionmotion/report/bhag2?'
						+'ruleSuspensionDate=' + selectedRuleSuspensionDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=RSMOIS_BHAG2_REPORT');
			}
		}
		/**** Statement Report Generation ****/
		function generateStatementReport() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			$("#amois_statement_report").attr('href',
					'rulessuspensionmotion/report/statement?'
					+'ruleSuspensionDate=' + selectedRuleSuspensionDate
					+'&sessionId=' + $("#loadedSession").val());
		}
		/**** Submitted Motions Report Generation ****/
		function generateSubmittedMotionsReport() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select Rule Suspension date of submitted motions");
				return false;
			} else {
				$("#rsmois_submitted_report").attr('href',
						'rulessuspensionmotion/report/submittedmotions?'
						+'ruleSuspensionDate=' + selectedRuleSuspensionDate
						+'&reportQueryName=RSMOIS_SUBMITTED_MOTIONS_REPORT'
						+'&sessionId=' + $("#loadedSession").val());
			}			
		}
		/**** Admitted Motions Report Generation ****/
		function generateAdmittedMotionsReport() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select rules suspension date of admitted motions");
				return false;
			} else {
				$("#amois_admitted_report").attr('href',
						'rulessuspensionmotion/report/admittedmotions?'
						+'ruleSuspensionDate=' + selectedRuleSuspensionDate
						+'&reportQueryName=RSMOIS_ADMITTED_MOTIONS_REPORT');
			}			
		}
		/**** Rejected Motions Report Generation ****/
		function generateRejectedMotionsReport() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			if(selectedRuleSuspensionDate==undefined || selectedRuleSuspensionDate=="") {
				$.prompt("Please select rules suspension date of rejected motions");
				return false;
			} else {
				$("#amois_rejected_report").attr('href',
						'rulessuspensionmotion/report/rejectedmotions?'
						+'ruleSuspensionDate=' + selectedRuleSuspensionDate
						+'&reportQueryName=RSMOIS_REJECTED_MOTIONS_REPORT');
			}			
		}
		/**** Register Report Generation ****/
		function generateRegisterReport() {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			$("#amois_register_report").attr('href',
					'rulessuspensionmotion/report/register?'
					+'ruleSuspensionDate=' + selectedRuleSuspensionDate
					+'&reportQueryName=RSMOIS_REGISTER_REPORT');
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
					
					showTabByIdAndUrl("details_tab","rulessuspensionmotion/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&memberId="+memberId 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=RSMOIS_MEMBER_WISE_REPORT&reportout=rulessuspensionmotionMemberReport");
				}
			});
		}
		
		/**** Decision Report Generation ****/
		function generateDecisionReport(motionId) {
			var selectedRuleSuspensionDate = $('#selectedRuleSuspensionDate').val();
			$("#rsmois_decision_report").attr('href',
					'rulessuspensionmotion/report/decisionreport?'
					+'ruleSuspensionMotionId=' + motionId);
		}
		
		function generateNoticeStatement(motionId){
			$("#rsmois_notice_statement").attr('href',
					'rulessuspensionmotion/report/noticestatement?'
					+'ruleSuspensionMotionId=' + motionId);
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('RSMOIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>			
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="rulessuspensionmotion.houseType" text="House Type"/>
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
				<spring:message code="rulessuspensionmotion.sessionyear" text="Year"/>
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
				<spring:message code="rulessuspensionmotion.sessionType" text="Session Type"/>
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
				<spring:message code="rulessuspensionmotion.motionType" text="Motion Type"/>
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
				access="hasAnyRole('RSMOIS_ADMIN','RSMOIS_ASSISTANT','RSMOIS_UNDER_SECRETARY',
			'RSMOIS_DEPUTY_SECRETARY','RSMOIS_PRINCIPAL_SECRETARY','RSMOIS_SPEAKER','RSMOIS_JOINT_SECRETARY',
			'RSMOIS_SECRETARY','RSMOIS_OFFICER_ON_SPECIAL_DUTY','RSMOIS_DEPUTY_SPEAKER','RSMOIS_CHAIRMAN','RSMOIS_DEPUTY_CHAIRMAN',
			'RSMOIS_SECTION_OFFICER','RSMOIS_UNDER_SECRETARY_COMMITTEE','RSMOIS_ADDITIONAL_SECRETARY','RSMOIS_CLERK')">
			
			<hr>
			<a href="#" id="select_status" class="butSim">
				<spring:message code="rulessuspensionmotion.status" text="Status"/>
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
			
			<div id='rulessuspensionmotionDepartment' style="display:inline;">
			<a href="#" id="select_department" class="butSim"> <spring:message
					code="rulessuspensionmotion.department" text="Department" />
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
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','RSMOIS_TYPIST')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="rulessuspensionmotion.status" text="Status"/>
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
			
			<a href="#" id="select_adjourningdate" class="butSim"><spring:message code="rulessuspensionmotion.selectRulesSuspensionDate" text="Select Rules Suspension Date"/></a>
			<input class="sCheck" type="checkbox" id="isRuleSuspensionDateSelected" name="isRuleSuspensionDateSelected" checked="checked"/>
			<select name="selectedRuleSuspensionDate" id="selectedRuleSuspensionDate" style="width:130px;height: 25px;">	
			<c:forEach items="${sessionDates}" var="i">
				<option value="${i[0]}" ${i[0]==defaultRuleSuspensionDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
			</c:forEach>
			</select>	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','RSMOIS_ASSISTANT','RSMOIS_SECTION_OFFICER')">
			<a href="#" id="submission_time_window" class="butSim">
				<spring:message code="rsmois.submission_time_window" text="Submission Time Window"/>
			</a> |
			</security:authorize>	
			
			<security:authorize access="hasAnyRole('RSMOIS_CLERK', 'RSMOIS_ASSISTANT', 'RSMOIS_SECTION_OFFICER', 'RSMOIS_SECRETARY')">					
				<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
					<spring:message code="rulessuspensionmotion.reports" text="Reports"/>
				</a>
				<div id="assistantReportDiv" style="display: none; border: 1px solid green; border-radius: 6px; margin: 10px 0px 10px 0px; padding: 5px;">
					<a href="javascript:void(0);" id="member_report" class="butSim" >
						<spring:message code="generic.memberWiseReport" text="Member-wise Report"/>
					</a>						
					<select id="members" class="sSelect" style="display: inline; width:100px;">
					</select>|
					<hr>
				</div>
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
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${motionTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		</div> 
		<input type="hidden" id="allSelectedMsg" value="<spring:message code='generic.allOption' text='---- All ----'/>"/>
		<input type="hidden" id="isRulesSuspensionTodayPrompt" value="<spring:message code='rulessuspensionmotion.isRulesSuspensionDateTodayPrompt' text='is Rules Suspension Date today?'/>"/>
		<input type="hidden" id="yes" value="<spring:message code='generic.yes' text='Yes'/>"/>
		<input type="hidden" id="no" value="<spring:message code='generic.no' text='No'/>"/>
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" id="loadedSession" value="" />
</body>
</html>