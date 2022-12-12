<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proprietypoint.list" text="List Of Propriety Points"/></title>
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
						loadParties();
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
		function loadParties(){
			memberArray = [];
			$.get('ref/allparties/'+$("#loadedSession").val(), function(data){
				if(data.length>0){
					var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					for(var i = 0; i < data.length; i++){
						memberArray.push(data[i].name);
						text+="<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$("#parties").empty();
					$("#parties").html(text);
				}
			});
		}
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			/* var currentHouseType = $("#currentHouseType").val();
			if(currentHouseType=='upperhouse') {
				$('.proprietypointdate_display').show();
			} else {
				$('.proprietypointdate_display').hide();
			} */
			$('.proprietypointdate_display').show();
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
				showProprietyPointList();				
			});				
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				
				/* if(value=='lowerhouse'){	
					$('.proprietypointdate_display').hide();
					reloadProprietyPointGrid();	
					
				} else if(value=='upperhouse'){
					$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultProprietyPointDate = data[data.length-1][0];
							$('#selectedProprietyPointDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultProprietyPointDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedProprietyPointDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
						
					}).done(function() {
						$('#isProprietyPointDateSelected').attr('checked', 'checked');
						$('#selectedProprietyPointDate').removeAttr('disabled');
						$('.proprietypointdate_display').show();
						//reloadProprietyPointGrid();
						showProprietyPointList();
						
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});
				} */
				$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
						+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
					if(data.length>1) {
						var defaultProprietyPointDate = data[data.length-1][0];
						$('#selectedProprietyPointDate').empty();
						var htmlText = "";
						for(var i=0; i<data.length-1; i++) {
							htmlText += "<option value='"+data[i][0]+"'";
							if(data[i][0]==defaultProprietyPointDate) {
								htmlText += "selected='selected'";
							}
							htmlText += ">"+data[i][1]+"</option>";									
						}	
						$('#selectedProprietyPointDate').html(htmlText);
					} else {
						$.prompt("some error..please contact administrator");
					}
					
				}).done(function() {
					$('#isProprietyPointDateSelected').attr('checked', 'checked');
					$('#selectedProprietyPointDate').removeAttr('disabled');
					$('.proprietypointdate_display').show();
					//reloadProprietyPointGrid();
					showProprietyPointList();
					
				}).fail(function() {
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					//resetControls();
					scrollTop();
				});
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				
				/* if($('#selectedHouseType').val()=='lowerhouse') {	
					reloadProprietyPointGrid();	
					
				} else if($('#selectedHouseType').val()=='upperhouse') {
					$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultProprietyPointDate = data[data.length-1][0];
							$('#selectedProprietyPointDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultProprietyPointDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedProprietyPointDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
						
					}).done(function() {
						$('#isProprietyPointDateSelected').attr('checked', 'checked');
						$('#selectedProprietyPointDate').removeAttr('disabled');
						//reloadProprietyPointGrid();
						showProprietyPointList();
						
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});
				} */
				$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
						+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
					if(data.length>1) {
						var defaultProprietyPointDate = data[data.length-1][0];
						$('#selectedProprietyPointDate').empty();
						var htmlText = "";
						for(var i=0; i<data.length-1; i++) {
							htmlText += "<option value='"+data[i][0]+"'";
							if(data[i][0]==defaultProprietyPointDate) {
								htmlText += "selected='selected'";
							}
							htmlText += ">"+data[i][1]+"</option>";									
						}	
						$('#selectedProprietyPointDate').html(htmlText);
					} else {
						$.prompt("some error..please contact administrator");
					}
					
				}).done(function() {
					$('#isProprietyPointDateSelected').attr('checked', 'checked');
					$('#selectedProprietyPointDate').removeAttr('disabled');
					//reloadProprietyPointGrid();
					showProprietyPointList();
					
				}).fail(function() {
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					//resetControls();
					scrollTop();
				})
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				
				/* if($('#selectedHouseType').val()=='lowerhouse') {	
					reloadProprietyPointGrid();	
					
				} else if($('#selectedHouseType').val()=='upperhouse') {
					$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
							+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
						if(data.length>1) {
							var defaultProprietyPointDate = data[data.length-1][0];
							$('#selectedProprietyPointDate').empty();
							var htmlText = "";
							for(var i=0; i<data.length-1; i++) {
								htmlText += "<option value='"+data[i][0]+"'";
								if(data[i][0]==defaultProprietyPointDate) {
									htmlText += "selected='selected'";
								}
								htmlText += ">"+data[i][1]+"</option>";									
							}	
							$('#selectedProprietyPointDate').html(htmlText);
						} else {
							$.prompt("some error..please contact administrator");
						}
						
					}).done(function() {
						$('#isProprietyPointDateSelected').attr('checked', 'checked');
						$('#selectedProprietyPointDate').removeAttr('disabled');
						//reloadProprietyPointGrid();
						showProprietyPointList();
						
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});
				} */	
				$.get('ref/proprietypoint/proprietypointdatesforsession?houseType='+$('#selectedHouseType').val()
						+'&sessionYear='+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+'&usergroupType='+$("#currentusergroupType").val(), function(data) {
					if(data.length>1) {
						var defaultProprietyPointDate = data[data.length-1][0];
						$('#selectedProprietyPointDate').empty();
						var htmlText = "";
						for(var i=0; i<data.length-1; i++) {
							htmlText += "<option value='"+data[i][0]+"'";
							if(data[i][0]==defaultProprietyPointDate) {
								htmlText += "selected='selected'";
							}
							htmlText += ">"+data[i][1]+"</option>";									
						}	
						$('#selectedProprietyPointDate').html(htmlText);
					} else {
						$.prompt("some error..please contact administrator");
					}
					
				}).done(function() {
					$('#isProprietyPointDateSelected').attr('checked', 'checked');
					$('#selectedProprietyPointDate').removeAttr('disabled');
					//reloadProprietyPointGrid();
					showProprietyPointList();
					
				}).fail(function() {
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					//resetControls();
					scrollTop();
				});
			});
			/**** proprietypoint type changes ****/		
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadProprietyPointGrid();							
				}				
			});	
			/**** status changes then reload grid ****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadProprietyPointGrid();
					$("#selectedFileCount").val("-");
				}
			});
			/**** sub-department changes then reload grid ****/
			$("#selectedSubDepartment").change(function() {
				var value = $(this).val();
				if (value != "") {
					reloadProprietyPointGrid();
					$("#selectedFileCount").val("-");
				}
			});			
			/**** Search Tab ****/
			$('#search_tab').click(function() {
				$("#selectionDiv1").hide();
				searchInt();
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
			
			/**** propriety point date changes then reload grid ****/
			$("#selectedProprietyPointDate").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadProprietyPointGrid();
					$("#selectedFileCount").val("-");
				}
			});
			$('#isProprietyPointDateSelected').change(function(){	
				if($("#isProprietyPointDateSelected").is(":checked")) {
					$('#selectedProprietyPointDate').removeAttr('disabled');
					//reloadProprietyPointGrid();
					showProprietyPointList();
					$("#selectedFileCount").val("-");
				} else {
					$('#selectedProprietyPointDate').attr('disabled', 'disabled');
					//reloadProprietyPointGrid();
					showProprietyPointList();
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
			/**** show proprietypoint list method is called by default.****/
			showProprietyPointList();	
			/**** Toggle Reports Div ****/
			$("#reports_link").click(function(e){
				$("#assistantReportDiv").toggle("slow");
			});
			$("#members").change(function(){
				var val = $(this).val();
				/* if(val!="" && val!='-'){
					memberWiseReport($(this).val());
				} */
				memberWiseReport($(this).val());
			});
			$("#department_report").click(function(e){
				var dept = $("#selectedSubDepartment").val();
				if(dept!="" && dept!='0'){
					departmentWiseReport(dept);	
				}
			});
			$("#party_report").click(function(){
				var val = $('#parties').val();
				if(val!="" && val!='-'){
					partyWiseReport(val);
				} else {
					$.prompt("Please select a party for this report!");
				}
			});
			$("#party_notices_report").click(function(){
				var val = $('#parties').val();
				if(val!="" && val!='-'){
					partyWiseNoticesReport(val);
				} else {
					$.prompt("Please select a party for this report!");
				}
			});
		});
		/**** displaying grid ****/					
		function showProprietyPointList() {
			var selectedProprietyPointDate = "";
			/* if($("#selectedHouseType").val()=='upperhouse' && $("#isProprietyPointDateSelected").is(":checked")) {
				selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
			} */
			if($("#isProprietyPointDateSelected").is(":checked")) {
				selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
			}
			showTabByIdAndUrl('list_tab','proprietypoint/list?houseType='+$('#selectedHouseType').val()
					+'&deviceType='+$("#selectedDeviceType").val()
					+'&sessionYear='+$("#selectedSessionYear").val()
					+'&sessionType='+$("#selectedSessionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()				
					+"&proprietyPointDate="+selectedProprietyPointDate			
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
				        "&deviceType="+$("#selectedDeviceType").val();		
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
		function memberProprietyPointsView() {
			var parameters = "houseType=" + $("#selectedHouseType").val()
			+ "&sessionYear=" + $("#selectedSessionYear").val()
			+ "&sessionType=" + $("#selectedSessionType").val()
			+ "&deviceType=" + $("#selectedDeviceType").val()
			+ "&createdBy=" + $("#ugparam").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=MEMBER_PROPRIETYPOINTS_VIEW"
			+ "&reportout=member_proprietypoints_view";
			showTabByIdAndUrl('details_tab','proprietypoint/report/generalreport?'+parameters);
		}
		function submittedProprietyPointsView() {
			var parameters = "sessionId=" + $("#loadedSession").val()
			+"&locale="+$("#moduleLocale").val()
			+ "&report=SUBMITTED__PROPRIETYPOINTS_VIEW"
			+ "&reportout=submitted_proprietypoints_view";
			showTabByIdAndUrl('details_tab','proprietypoint/report/generalreport?'+parameters);
		}
		/**** new proprietypoint ****/
		function newProprietyPoint() {		
			$("#cancelFn").val("newProprietyPoint");
			//since id of proprietypoint has not been created so key is set to empty value
			$("#key").val("");	
			showTabByIdAndUrl('details_tab','proprietypoint/new?'+$("#gridURLParams").val());
			$("#selectionDiv1").hide();	
		}
		/**** edit proprietypoint ****/		
		function editProprietyPoint(row) {
			$("#cancelFn").val("editProprietyPoint");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','proprietypoint/'+row+'/edit?'+$("#gridURLParams").val());
				$("#selectionDiv1").hide();	
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);			
			showTabByIdAndUrl('details_tab', 'proprietypoint/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete proprietypoint ****/	
		function deleteProprietyPoint() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				deviceNumber = $("#grid").jqGrid ('getCell', row, 'number');
			    if(deviceNumber!='-') {
			    	$.prompt($('#submittedParliamentaryDevicesCannotBeDeletedPrompt').val());
					return;
			    } else {
					$.prompt($('#confirmDeleteMessage').val()+ row,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
					        $.delete_('proprietypoint/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					        	showProprietyPointList();
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
		function reloadProprietyPointGrid(){
			var selectedProprietyPointDate = "";
			/* if($("#selectedHouseType").val()=='upperhouse' && $("#isProprietyPointDateSelected").is(":checked")) {
				selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
			} */
			if($("#isProprietyPointDateSelected").is(":checked")) {
				selectedProprietyPointDate = convertToDbFormat($('#selectedProprietyPointDate').val());
			}
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()+
					"&sessionType="+$("#selectedSessionType").val()+
					"&deviceType="+$("#selectedDeviceType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()					
					+"&proprietyPointDate="+selectedProprietyPointDate	
					+"&subDepartment="+$("#selectedSubDepartment").val()
			);
			console.log($("#gridURLParams").val());
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");
			
			loadSession();
		}		
		/**** Set Submission Time Window ****/
	 	function setSubmissionTimeWindow() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			if(selectedProprietyPointDate==undefined || selectedProprietyPointDate=="") {
				$.prompt("Please select proprietypoint date for setting submission window!");
				return false;
			} else {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&deviceType="+$("#selectedDeviceType").val()
				+"&proprietyPointDate="+$("#selectedProprietyPointDate").val()
				+"&formattedProprietyPointDate="+$("#selectedProprietyPointDate").text()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val();
				var resourceURL='proprietypoint/submissionwindow?'+parameters;
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
		/**** Bulk putup(Member)****/
		function bulkPutup(){
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'proprietypoint/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
			showTabByIdAndUrl('bulkputup_tab', resourceURL);
		}	
		/**** Bulk putup(Assistant)****/
		function bulkPutupAssistant(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val()
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='proprietypoint/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}
		/**** Current Status Report Generation ****/
		function showCurrentStatusReport(val, deviceId){
			$("#selectionDiv1").hide();
			var device = $("#deviceTypeMaster option[value='"+$("#selectedDeviceType").val()+"']").text().split("_")[0];
			showTabByIdAndUrl('details_tab', "proprietypoint/report/currentstatusreport?device="+ device +"&reportType="+val+"&deviceId="+deviceId);
		}
		/**** To Be Admitted Report Generation ****/
		function generateToBeAdmittedReport() {
			$("#prois_tobeadmitted_report").attr('href',
					'proprietypoint/report/tobeadmitted?'
					+'sessionId=' + $("#loadedSession").val()
					+'&status=' + $("#selectedStatus").val()
					+'&locale=' + $("#moduleLocale").val()
					+'&reportQueryName=PROIS_TOBEADMITTED_REPORT');
		}
		/**** To Be Rejected Report Generation ****/
		function generateToBeRejectedReport() {
			$("#prois_toberejected_report").attr('href',
					'proprietypoint/report/toberejected?'
					+'sessionId=' + $("#loadedSession").val()
					+'&reportQueryName=PROIS_TOBEREJECTED_REPORT');
		}
		/**** Submitted Devices Report Generation ****/
		function generateSubmittedDevicesReport() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			if(selectedProprietyPointDate==undefined || selectedProprietyPointDate=="") {
				$.prompt("Please select propriety point date of submitted motions");
				return false;
			} else {
				$("#prois_submitted_report").attr('href',
						'proprietypoint/report/submitteddevices?'
						+'proprietyPointDate=' + selectedProprietyPointDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=PROIS_SUBMITTED_DEVICES_REPORT');
			}			
		}
		/**** Admitted Devices Report Generation ****/
		function generateAdmittedDevicesReport() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			if(selectedProprietyPointDate==undefined || selectedProprietyPointDate=="") {
				$.prompt("Please select propriety point date of admitted motions");
				return false;
			} else {
				$("#prois_admitted_report").attr('href',
						'proprietypoint/report/admitteddevices?'
						+'proprietyPointDate=' + selectedProprietyPointDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=PROIS_ADMITTED_DEVICES_REPORT');
			}			
		}
		/**** Rejected Devices Report Generation ****/
		function generateRejectedDevicesReport() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			if(selectedProprietyPointDate==undefined || selectedProprietyPointDate=="") {
				$.prompt("Please select propriety point date of rejected motions");
				return false;
			} else {
				$("#prois_rejected_report").attr('href',
						'proprietypoint/report/rejecteddevices?'
						+'proprietyPointDate=' + selectedProprietyPointDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=PROIS_REJECTED_DEVICES_REPORT');
			}			
		}
		/**** Register of Admitted Devices Report Generation ****/
		function generateRegisterReport() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			$("#prois_register_report").attr('href',
					'proprietypoint/report/register?'
					+'proprietyPointDate=' + selectedProprietyPointDate
					+'&sessionId=' + $("#loadedSession").val()
					+'&reportQueryName=PROIS_REGISTER_REPORT');
		}
		/**** Admitted Devices for Reporting Branch Report Generation ****/
		function generateAdmittedDevicesReportForReporting() {
			var selectedProprietyPointDate = $('#selectedProprietyPointDate').val();
			if(selectedProprietyPointDate==undefined || selectedProprietyPointDate=="") {
				$.prompt("Please select propriety point date of admitted motions");
				return false;
			} else {
				$("#prois_admitted_reportingbranch").attr('href',
						'proprietypoint/report/admitteddevices?'
						+'proprietyPointDate=' + selectedProprietyPointDate
						+'&sessionId=' + $("#loadedSession").val()
						+'&reportQueryName=PROIS_ADMITTED_DEVICES_REPORT_REPORTING_BRANCH');
			}			
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
					
					showTabByIdAndUrl("details_tab","proprietypoint/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedDeviceType").val()
							+"&memberId="+memberId 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=PROIS_MEMBER_WISE_REPORT&reportout=proprietyPointMemberReport");
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
					
					showTabByIdAndUrl("details_tab","proprietypoint/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedMotionType").val()
							+"&subd="+dept 
							+"&locale="+$("#moduleLocale").val()
							+"&report=PROIS_DEPARTMENT_WISE_REPORT&reportout=proprietyPointDepartmentReport");
				}
			});
		}
		/**** Partywise Members Report Generation ****/
		function partyWiseReport(party){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","proprietypoint/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedDeviceType").val()
							+"&partyId="+party 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=PROIS_PARTY_WISE_REPORT&reportout=proprietyPointPartyReport");
				}
			});
		}
		/**** Partywise Notices Report Generation ****/
		function partyWiseNoticesReport(party){
			var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
			+ "/" + $("#selectedSessionYear").val()
			+ "/" + $("#selectedSessionType").val();
			$.get(url,function(data){
				if(data){
					
					var selectedStatus = $("#selectedStatus").val();
					var statusType = $("#statusMaster option[value='" + selectedStatus + "']").text().trim();
					
					showTabByIdAndUrl("details_tab","proprietypoint/report/generalreport?"
							+"sessionId="+data.id
							+"&deviceTypeId="+$("#selectedDeviceType").val()
							+"&partyId="+party 
							+"&locale="+$("#moduleLocale").val()
							+"&statusId="+selectedStatus
							+"&report=PROIS_PARTY_WISE_NOTICES_REPORT&reportout=proprietyPointPartyNoticesReport");
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
			<security:authorize access="hasAnyRole('PROIS_CLERK','PROIS_ASSISTANT', 'PROIS_UNDER_SECRETARY',
			'PROIS_DEPUTY_SECRETARY','PROIS_PRINCIPAL_SECRETARY','PROIS_SPEAKER', 'PROIS_JOINT_SECRETARY',
			'PROIS_SECRETARY', 'PROIS_OFFICER_ON_SPECIAL_DUTY', 'PROIS_DEPUTY_SPEAKER', 'PROIS_CHAIRMAN',
			'PROIS_DEPUTY_CHAIRMAN', 'PROIS_SECTION_OFFICER', 'PROIS_UNDER_SECRETARY_COMMITTEE',
			'SUPER_ADMIN','PROIS_ADDITIONAL_SECRETARY')">
			<li>
				<a id="search_tab" href="#" class="tab"><spring:message code="question.searchT" text="Search"></spring:message></a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('PROIS_ASSISTANT')">			
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
				<spring:message code="proprietypoint.houseType" text="House Type"/>
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
				<spring:message code="proprietypoint.sessionyear" text="Year"/>
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
				<spring:message code="proprietypoint.sessionType" text="Session Type"/>
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
						
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="proprietypoint.deviceType" text="Device Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width: 100px; height: 25px;">
				<c:forEach items="${deviceTypes}" var="i">
					<c:choose>
						<c:when test="${deviceType==i.id}">
							<option value="${i.id}" selected="selected">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display: none;">
				<c:forEach items="${deviceTypes }" var="i">
					<option value="${i.id}">${i.type}</option>
				</c:forEach>
			</select>|		
			
			<security:authorize
				access="hasAnyRole('PROIS_ADMIN','PROIS_ASSISTANT','PROIS_UNDER_SECRETARY',
			'PROIS_DEPUTY_SECRETARY','PROIS_PRINCIPAL_SECRETARY','PROIS_SPEAKER','PROIS_JOINT_SECRETARY',
			'PROIS_SECRETARY','PROIS_OFFICER_ON_SPECIAL_DUTY','PROIS_DEPUTY_SPEAKER','PROIS_CHAIRMAN','PROIS_DEPUTY_CHAIRMAN',
			'PROIS_SECTION_OFFICER','PROIS_UNDER_SECRETARY_COMMITTEE','PROIS_ADDITIONAL_SECRETARY','PROIS_CLERK')">
			
			<hr>
			<a href="#" id="select_status" class="butSim">
				<spring:message code="proprietypoint.status" text="Status"/>
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
			
			<div id='proprietypointDepartment' style="display:inline;">
			<a href="#" id="select_department" class="butSim"> <spring:message
					code="proprietypoint.department" text="Department" />
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
			
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_TYPIST')">
			<a href="#" id="select_status" class="butSim">
				<spring:message code="proprietypoint.status" text="Status"/>
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
				
			<%-- <security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','PROIS_ASSISTANT')">	
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="proprietypoint.itemcount" text="No. of Motions(Bulk Putup)"/>
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
			
			<hr class="proprietypointdate_display">
			
			<a href="#" id="select_proprietypointdate" class="butSim proprietypointdate_display"><spring:message code="proprietypointdate.selectProprietyPointdate" text="Select Propriety Point Date"/></a>
			<input class="sCheck proprietypointdate_display" type="checkbox" id="isProprietyPointDateSelected" name="isProprietyPointDateSelected" checked="checked"/>
			<select name="selectedProprietyPointDate" id="selectedProprietyPointDate" class="proprietypointdate_display" style="width:130px;height: 25px;">	
			<c:forEach items="${sessionDates}" var="i">
				<option value="${i[0]}" ${i[0]==defaultProprietyPointDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
			</c:forEach>
			</select>
			
			<security:authorize access="hasAnyRole('PROIS_CLERK', 'PROIS_ASSISTANT', 'PROIS_SECTION_OFFICER', 'PROIS_UNDER_SECRETARY', 'PROIS_SECRETARY', 'PROIS_PRINCIPAL_SECRETARY')">	
				<a href="javascript:void(0);" id="reports_link" class="butSim" style="float: right;">
					<spring:message code="proprietypoint.reports" text="Reports"/>
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
					<a href="javascript:void(0);" id="party_report" class="butSim" >
						<spring:message code="generic.partyWiseReport" text="Party-wise Members Report"/>
					</a>						
					<select id="parties" class="sSelect" style="display: inline; width:100px;">
					</select>|
					<a href="javascript:void(0);" id="party_notices_report" class="butSim" >
						<spring:message code="generic.partyWiseReport" text="Party-wise Notices Report"/>
					</a>|<br>
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
		<input type="hidden" name="currentDeviceType" id="currentDeviceType" value="${deviceTypeType}">		
		<input type="hidden" name="currentHouseType" id="currentHouseType" value="${houseType}">		
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="allowedGroups" name="allowedGroups" value="${allowedGroups }">
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" /> 
		</div> 
		<input type="hidden" id="allSelectedMsg" value="<spring:message code='generic.allOption' text='---- All ----'/>"/>
		<input type="hidden" id="yes" value="<spring:message code='generic.yes' text='Yes'/>"/>
		<input type="hidden" id="no" value="<spring:message code='generic.no' text='No'/>"/>
		<input type="hidden" id="gridURLParams_ForNew" name="gridURLParams_ForNew" />
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
		<input type="hidden" id="loadedSession" value="" />
</body>
</html>