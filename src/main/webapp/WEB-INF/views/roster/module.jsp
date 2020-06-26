<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.list" text="List Of Rosters"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/*Tooltip*/
			$(".toolTip").hide();					
			/**** here we are trying to add date mask in grid search when field names ends with date ****/
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});					
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadRosterGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadRosterGrid();								
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadRosterGrid();							
				}			
			});	
			
			$("#selectedtCommitteeType").change(function(){
				$.get('ref/committeename?committeeTypeId='+$(this).val(),function(data){
					if(data.length>0){
						var text="<option value=''>"+$("#pleaseSelect").val()+"</option>";
						for( var i=0;i<data.length;i++){
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$("#selectedCommitteeName").empty();
						$("#selectedCommitteeName").html(text);
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			});
			
			$("#selectedCommitteeName").change(function(){
				$.get('ref/committeemeeting?committeeNameId='+$(this).val(),function(data){
					if(data.length>0){
						var text="<option value=''>"+$("#pleaseSelect").val()+"</option>";
						for( var i=0;i<data.length;i++){
							text+="<option title='"+data[i].value+"' value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$("#selectedCommitteeMeeting").empty();
						$("#selectedCommitteeMeeting").html(text);
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			});
			
			$("#selectedCommitteeMeeting").change(function(){
				reloadRosterGrid();
			});
			/**** List Tab ****/
			$('#list_tab').click(function(){
				showRosterList(); 	
			}); 
			/***** Details Tab ****/
			$('#details_tab').click(function(){
				editRoster(); 	
			}); 	
			/**** Slot Tab ****/
			$('#slot_tab').click(function(){
				listSlot(); 	
			}); 
			/**** Adjournment Tab ****/
			$('#adjournment_tab').click(function(){
				listAdjournment(); 	
			});	
			
			$('#selectedModule').change(function(){
				var moduleValue = $(this).val();
				if(moduleValue=='COMMITTEE'){
					$('#committeeDiv').css("display","inline");
					$('#sessionDiv').css("display","none");
				}else{
					$('#committeeDiv').css("display","none");
					$('#sessionDiv').css("display","inline");
				}
				reloadRosterGrid();
			});
			
			$("#selectedLanguage").change(function(){
				reloadRosterGrid();
			});
			/**** show roster list method is called by default.****/
			showRosterList();	
			
		});
		/**** displaying grid ****/					
		function showRosterList() {
				$("#selectionDiv1").show();	
				
				if($('#selectedModule').val()=="COMMITTEE"){
					showTabByIdAndUrl('list_tab',"roster/list?houseType=0" 
							+"&sessionYear=0"
							+"&sessionType=0"
							+"&language="+$("#selectedLanguage").val()
							+"&committeeMeeting="+$("#selectedCommitteeMeeting").val()
							);
				}else{
					showTabByIdAndUrl('list_tab','roster/list?houseType='+$('#selectedHouseType').val()
							+"&sessionYear="+$("#selectedSessionYear").val()
							+"&sessionType="+$("#selectedSessionType").val()
							+"&language="+$("#selectedLanguage").val()
							+"&committeeMeeting=0");
				}
				
		}
		/**** new roster ****/
		function newRoster() {
			$("#selectionDiv1").hide();			
			$("#cancelFn").val("newRoster");
			//since id of roster has not been created so key is set to empty value
			$("#key").val("");
			if($('#selectedModule').val()=="COMMITTEE"){
				showTabByIdAndUrl('details_tab','roster/new?houseType='+$('#selectedHouseType').val()
						+"&sessionYear=0"
						+"&sessionType=0"
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting="+$("#selectedCommitteeMeeting").val());
			}else{
				showTabByIdAndUrl('details_tab','roster/new?houseType='+$('#selectedHouseType').val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting=0");
			}
			
		}
		/**** edit roster ****/		
		function editRoster() {
			$("#cancelFn").val("editRoster");			
			row=$('#key').val();			
			if(row==null||row==''){				
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				$("#selectionDiv1").hide();			
				showTabByIdAndUrl('details_tab','roster/'+row+'/edit?'+$("#gridURLParams").val());
			}						
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();				
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'roster/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete roster ****/	
		function deleteRoster() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('roster/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				        	showRosterList();
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
		/**** reload grid ****/
		function reloadRosterGrid(){
			
			if($('#selectedModule').val()=="COMMITTEE"){
				$("#gridURLParams").val("houseType=0"
						+"&sessionYear=0"
						+"&sessionType=0"
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting="+$("#selectedCommitteeMeeting").val()
						);
			}else{
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting=0"
						);
			}
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");							
		}	
		/**** list slot ****/
		function listSlot() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$("#selectionDiv1").hide();				
				showTabByIdAndUrl('slot_tab','roster/slot/list');
			}
		}
		/**** list adjournment ****/
		function listAdjournment() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$("#selectionDiv1").hide();					
				showTabByIdAndUrl('adjournment_tab','roster/adjournment/list');
			}
		}	
		
		function viewTotalWorkRep() {

				showTabByIdAndUrl('totalWorkRep_tab','roster/roster_totalWorkRep?houseType='+$('#selectedHouseType').val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+"&language="+$("#selectedLanguage").val()
						+"&committeeMeeting=0");

		}
		
		function viewAdhawa() {

			$("#adhawa").attr('href','roster/viewAdhawa?houseType='+$('#selectedHouseType').val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&languageId="+$("#selectedLanguage").val()
					+ '&outputFormat=' + $("#defaultReportFormat").val()
					+ '&reportQuery=RIS_ADHAWA_REPORT'
						+ '&templateName=template_ris_adhawa_'+$('#selectedHouseType').val()
						+ '&reportName=adhawaReport'
						+ '&locale='+$("#authlocale").val()
					+"&committeeMeeting=0");

	}
		
		function viewRoster() {
			
			$("#selectionDiv1").hide();					
			row=$('#key').val();			
			if(row==null||row==''){				
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				$("#selectionDiv1").hide();	
				showTabByIdAndUrl('slot_tab','roster/'+row+'/roster_rep?'+$("#gridURLParams").val());
			}/* 
			
			showTabByIdAndUrl('slot_tab','roster/roster_rep'); */
		}
		
		function publishRoster(rosterId) {
			//row=$('#key').val();			
			if(rosterId==null||rosterId==''){				
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				$.prompt($('#sendForPublishMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
						$.post("roster/"+rosterId+"/publish",function(data){
							if(data){
								$.prompt($("#successMessage").val());
								$.unblockUI();
							}else{
								$.prompt($("#failureMessage").val());
							}
						});	
			        }
				}});
				return false;
			}
		}
	</script>
</head>
<body>
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
			<li>
				<a id="slot_tab" href="#" class="tab">
				   <spring:message code="roster.slot" text="Slots">
				   </spring:message>
				</a>
			</li>
			<li>
				<a id="adjournment_tab" href="#" class="tab">
				   <spring:message code="roster.adjournment" text="Adjournments">
				   </spring:message>
				</a>
			</li>		
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">
				
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="roster.houseType" text="House Type"/>
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
				
			<div id='moduleFilter' style='display:inline;'>
				<a href="#" id="moduletypeLabel" class="butSim">
					<spring:message code="mytask.module" text="Module"/>
				</a>
				<select name="selectedModule" id="selectedModule" style="width:100px;height: 25px;">			
					<option value=""><spring:message code='please.select' text='----Please Select----'></spring:message></option>
					<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
				</select> |
			</div>
			
			<div id="sessionDiv" style="display:inline;">				
				<a href="#" id="select_session_year" class="butSim">
					<spring:message code="roster.sessionyear" text="Year"/>
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
					<spring:message code="roster.sessionType" text="Session Type"/>
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
			</div>	
			
			<div id="committeeDiv" style="display:none;">
				<a href="#" id="select_committee_type" class="butSim">
					<spring:message code="roster.committeeType" text="Committee Type"/>
				</a>
				<select name="selectedtCommitteeType" id="selectedtCommitteeType" style="width:100px;height: 25px;">				
					<option value=""><spring:message code='please.select' text='----Please Select----'></spring:message></option>
					<c:forEach var="i" items="${committeeTypes}">
						<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
					</c:forEach> 
				</select> |	
									
				<a href="#" id="select_committeeName" class="butSim">
					<spring:message code="roster.committeeName" text="Committee Name"/>
				</a>
				<select name="selectedCommitteeName" id="selectedCommitteeName" style="width:100px;height: 25px;">				
					<option value=""><spring:message code='please.select' text='----Please Select----'></spring:message></option>
				</select> |	
				<br>
				<br>
				<a href="#" id="select_committeeMeeting" class="butSim">
					<spring:message code="roster.committeeMeeting" text="Committee Meeting"/>
				</a>
				<select name="selectedCommitteeMeeting" id="selectedCommitteeMeeting" style="width:100px;height: 25px;">				
					<option value=""><spring:message code='please.select' text='----Please Select----'></spring:message></option>
				</select> |	
			</div>
			
			<a href="#" id="select_language" class="butSim">
				<spring:message code="roster.language" text="Language"/>
			</a>
			<select name="selectedLanguage" id="selectedLanguage" style="width:100px;height: 25px;">				
				<c:forEach items="${languages}" var="i">
					<c:choose>
						<c:when test="${language==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>				
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>			
				</c:forEach> 
			</select> |							
			<hr>							
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">				
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="sendForPublishMsg" name="sendForPublishMsg" value="<spring:message code='generic.confirmPublishMessage' text='Do you want to publish proceedings for selected Roster '></spring:message>" disabled="disabled">
		<input type="hidden" id="successMessage" name="successMessage" value="<spring:message code='roster.publishSuccessMessage' text='The Proceeding is Successfully published '></spring:message>" disabled="disabled">	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="failureMessage" name="failureMessage" value="<spring:message code='roster.publishFailureMessage' text='There is some problem in publishing, kindly try after sometime '></spring:message>" disabled="disabled">
		<input type="hidden" id="defaultReportFormat" value="<spring:message code='motion.report.defaultFormat' text='PDF' />" />
		
		</div> 		
</body>
</html>