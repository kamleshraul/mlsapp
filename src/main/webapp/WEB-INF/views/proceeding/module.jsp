<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proceeding.list" text="List Of Proceedings"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	 
			document.onkeydown = function(e){
				
				//var myVar = '';
			    //keycode for F5 function
				if(e.keyCode===116){
					return false;
				}
			    
				if (e.ctrlKey || e.metaKey) {
			        switch (String.fromCharCode(e.which).toLowerCase()) {
			        case 's':
			            e.preventDefault();
			            break;
			        case 'r':
			        	e.preventDefault();
			        	break;
			        case 'w':
			        	e.preventDefault();
			        	break;
			        }
			        	
			    }
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
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					loadRosterDayFromSessions();
					reloadProceedingGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					loadRosterDayFromSessions();
					reloadProceedingGrid();								
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					loadRosterDayFromSessions();
					reloadProceedingGrid();							
				}			
			});	
			/**** language changes then reload grid****/
			$("#selectedLanguage").change(function(){
				var value=$(this).val();
				if(value!=""){	
					if($('#selectedModule').val()=='COMMITTEE'){
						loadRosterDayFromCommitteeMeeting();
					}else{
						loadRosterDayFromSessions();
					}
					reloadProceedingGrid();							
				}			
			});	
			/**** Day changes then reload grid****/
			$("#selectedDay").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadProceedingGrid();							
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
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
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
				loadRosterDayFromCommitteeMeeting();
				reloadProceedingGrid();
			});
			
			$('#selectedModule').change(function(){
				var moduleValue = $(this).val();
				if(moduleValue=='COMMITTEE'){
					$('#committeeDiv').css("display","inline");
					$('#sessionDiv').css("display","none");
					loadRosterDayFromCommitteeMeeting();
				}else{
					$('#committeeDiv').css("display","none");
					$('#sessionDiv').css("display","inline");
					loadRosterDayFromSessions();
				}
				reloadProceedingGrid();
			});
			/**** List Tab ****/
			$('#list_tab').click(function(){
				var currentPartId = $("#partId1").val();
				var currentPartContent = $("#proceedingReportDiv").html();
				if(currentPartContent != null && currentPartContent != ''){
					var currentPartContentArr = currentPartContent.split("</table>");
					var mainContent = currentPartContentArr[1];
					//Assumption : If user starts typing the size of the content without headers will have length >34
					if((mainContent.length>34) ||
							(currentPartId != null && currentPartId!='')){
						$.prompt($('#listNavigationConfirmationMessage').val(),{
							buttons: {Ok:true, Cancel:false}, callback: function(v){
					        if(v){
					        	updatePart();
					        	showProceedingList(); 
					        }
						}});
						return false;
					}else{
						showProceedingList(); 
					}
				}else{
					showProceedingList(); 
				}
			}); 
			/***** Details Tab ****/
			$('#details_tab').click(function(){
				editProceeding(); 	
			}); 
			if($('#selectedModule').val()=='COMMITTEE'){
				loadRosterDayFromCommitteeMeeting();
			}else{
				loadRosterDayFromSessions();
			}
						
			/**** show roster list method is called by default.****/
			showProceedingList();				
		});
		/**** displaying grid ****/					
		
		function showProceedingList() {
			$("#selectionDiv1").show();
			if($('#selectedModule').val()=="COMMITTEE"){
				showTabByIdAndUrl('list_tab', 'proceeding/list?houseType=0'  
						+ '&sessionyear=0'
						+ '&sessionType=0'
						+ '&language='+ $("#selectedLanguage").val() 
						+ '&day='+ $("#selectedDay").val() 
						+ '&ugparam='+ $("#ugparam").val()
						+ '&committeeMeeting=' +$("#selectedCommitteeMeeting").val()
						+ '&roleType='+$("#roleType").val());
			}else{
				showTabByIdAndUrl('list_tab', 'proceeding/list?houseType=' + $('#selectedHouseType').val() 
						+ '&sessionyear=' + $("#selectedSessionYear").val() 
						+ '&sessionType=' + $("#selectedSessionType").val() 
						+ '&language=' + $("#selectedLanguage").val() 
						+ '&day=' + $("#selectedDay").val() 
						+ '&ugparam=' + $("#ugparam").val()
						+ '&committeeMeeting=0'
						+ '&roleType='+$("#roleType").val());
			}
			
		}

		/**** edit Proceeding ****/
		function editProceeding() {
			$("#cancelFn").val("editProceeding");
			row = $('#key').val();
			if (row == null || row == '') {
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			} else {
				$("#selectionDiv1").hide();
				showTabByIdAndUrl('details_tab', 'proceeding/' + row + '/edit?'
						+ $("#gridURLParams").val());
			}
		}
		
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();
			$("#cancelFn").val("rowDblClickHandler");
			$('#key').val(rowid);
			/* showTabByIdAndUrl('details_tab', 'proceeding/' + rowid + '/uploadproceeding?'
					+ $("#gridURLParams").val()); */
			showTabByIdAndUrl('details_tab', 'proceeding/' + rowid + '/edit?'
					+ $("#gridURLParams").val());
		}
		/**** delete roster ****/
		function deleteProceeding() {
			var row = $("#key").val();
			if (row == null || row == '') {
				$.prompt($('#selectRowFirstMessage').val());
				return;
			} else {
				$.prompt($('#confirmDeleteMessage').val() + row, {
					buttons : {
						Ok : true,
						Cancel : false
					},
					callback : function(v) {
						if (v) {
							$.delete_('proceeding/' + row + '/delete', null,
									function(data, textStatus, XMLHttpRequest) {
										showProceedingList();
									});
						}
					}
				});
			}
		}
		/**** reload grid ****/
		function reloadProceedingGrid() {
			
			if($('#selectedModule').val()=="COMMITTEE"){
				$("#gridURLParams").val("houseType=0" 
								+ "&sessionYear=0"
								+ "&sessionType=0"
								+ '&language=' + $("#selectedLanguage").val()
								+ '&day=' + $("#selectedDay").val() 
								+ "&committeeMeeting="+ $("#selectedCommitteeMeeting").val()
								+ '&ugparam='+ $("#ugparam").val()
								+ '&roleType='+$("#roleType").val());
			}else{
				$("#gridURLParams").val("houseType=" + $("#selectedHouseType").val()
								+ "&sessionYear=" + $("#selectedSessionYear").val()
								+ "&sessionType=" + $("#selectedSessionType").val()
								+ '&language=' + $("#selectedLanguage").val()
								+ '&day=' + $("#selectedDay").val()
								+ '&ugparam=' + $("#ugparam").val()
								+ "&committeeMeeting=0"
								+ '&roleType='+$("#roleType").val());
			}
			
			var oldURL = $("#grid").getGridParam("url");
			var baseURL = oldURL.split("?")[0];
			newURL = baseURL + "?" + $("#gridURLParams").val();
			$("#grid").setGridParam({
				"url" : newURL
			});
			$("#grid").trigger("reloadGrid");
		}

		function loadRosterDayFromSessions() {
			if ($("#selectedDay").length > 0) {
				params = "houseType=" + $('#selectedHouseType').val()
						+ '&sessionYear=' + $("#selectedSessionYear").val()
						+ '&sessionType=' + $("#selectedSessionType").val()
						+ '&language=' + $("#selectedLanguage").val();
				$.get('ref/rosterdays?' + params, function(data) {
					if (data.length > 0) {
						var text = "";
						var length = data.length - 1;
						for (var i = length; i>=0; i--) {
							text += "<option value='"+data[i].number+"'>" + data[i].name
									+ "</option>";
						}
						$("#selectedDay").empty();
						$("#selectedDay").html(text);
					} else {
						$("#selectedDay").empty();
					}
				}).done(function(){
					$("#selectedDay").trigger("change");
				});
			}
			//		
		}
		
		function loadRosterDayFromCommitteeMeeting() {
			if ($("#selectedDay").length > 0) {
				params = "committeeMeeting="+ $("#selectedCommitteeMeeting").val()
						+ '&language=' + $("#selectedLanguage").val();
				$.get('ref/rosterdaysfromcommitteemeeting?' + params, function(data) {
					if (data.length > 0) {
						var text = "";
						for ( var i = 0; i < data.length; i++) {
							text += "<option value='"+data[i]+"'>" + data[i]
									+ "</option>";
						}
						$("#selectedDay").empty();
						$("#selectedDay").html(text);
					} else {
						$("#selectedDay").empty();
					}
				}).done(function(){
					reloadProceedingGrid();
				});
			}
			//		
		}
		
		function rosterWiseReport(){
			if($('#selectedModule').val()=="COMMITTEE"){
				var params="committeeMeeting=" + $("#selectedCommitteeMeeting").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val();
				showTabByIdAndUrl('details_tab', 'proceeding/rosterwisereport?'+params);
			}else{
				var params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val();
				showTabByIdAndUrl('details_tab', 'proceeding/rosterwisereport?'+params);
			}
		}
		
		function reporterWiseReport(){
			if($('#selectedModule').val()=="COMMITTEE"){
				var params="committeeMeeting=" + $("#selectedCommitteeMeeting").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&user='+$('#ugparam').val();
				showTabByIdAndUrl('details_tab', 'proceeding/reporterwisereport?'+params);
			}else{
				var params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&user='+$('#ugparam').val();
				showTabByIdAndUrl('details_tab', 'proceeding/reporterwisereport?'+params);
			}
		}
		
		function showPartList(key){
			showTabByIdAndUrl('part_tab', 'proceeding/part/list?proceeding='+key);
		}
		
		function proceedingwiseReport(){
			var params="proceeding="+$('#key').val()+
			'&language=' + $("#selectedLanguage").val();
			showTabByIdAndUrl('details_tab', 'proceeding/part/proceedingwiseReport?'+params);
		}

		function completeProceeding(proceedingId) {
			 //row=$('#key').val();			
			if(proceedingId == null||proceedingId ==''){				
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				$.prompt($('#sendForPublishMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
						$.post("proceeding/" + proceedingId + "/complete",function(data){
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
				<a id="bookmarks_tab" href="#" class="tab">
				   <spring:message code="generic.bookmarks" text="Bookmark">
				   </spring:message>
				</a>
			</li>
			<li style='display:none;'>
				<a id="part_tab" href="#" class="tab">
				   <spring:message code="generic.part" text="part">
				   </spring:message>
				</a>
			</li>	
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">
				
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="proceeding.houseType" text="House Type"/>
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
					<spring:message code="proceeding.sessionyear" text="Year"/>
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
					<spring:message code="proceeding.sessionType" text="Session Type"/>
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
				<!-- <br> -->
				<a href="#" id="select_committeeMeeting" class="butSim">
					<spring:message code="roster.committeeMeeting" text="Committee Meeting"/>
				</a>
				<select name="selectedCommitteeMeeting" id="selectedCommitteeMeeting" style="width:100px;height: 25px;">				
					<option value=""><spring:message code='please.select' text='----Please Select----'></spring:message></option>
					<c:forEach var="i" items="${committeeMeetings}">
						<option value="${i.id}" ><c:out value="${i.meetingDate}"></c:out></option>			
					</c:forEach>
				</select> |	
			</div>
			<a href="#" id="select_language" class="butSim">
				<spring:message code="proceeding.language" text="Language"/>
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
						
			<a href="#" id="select_day" class="butSim">
				<spring:message code="proceeding.day" text="Day"/>
			</a>
			<select name="selectedDay" id="selectedDay" style="width:100px;height: 25px;">				
			<c:forEach items="${days}" var="i">
			<c:choose>
			<c:when test="${day==i}">
			<option value="${i}" selected="selected"><c:out value="${i}"></c:out></option>				
			</c:when>
			<c:otherwise>
			<option value="${i}"><c:out value="${i}"></c:out></option>			
			</c:otherwise>
			</c:choose>			
			</c:forEach> 
			</select> |					
			<hr>							
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">	
		<input type="hidden" id="ugparam" name="ugparam" value="${ugparam}">	
		<input type="hidden" id="slotId" name="slotId"/>
		<input type="hidden" id="roleType" name="roleType" value="${roleType}"/>	
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="sendForPublishMsg" name="sendForPublishMsg" value="<spring:message code='generic.confirmPublishMessage' text='Do you want to Complete Proceeding<!--  --> '></spring:message>" disabled="disabled">
		<input type="hidden" id="successMessage" name="successMessage" value="<spring:message code='roster.publishSuccessMessage' text='The Proceeding is Successfully Completed '></spring:message>" disabled="disabled">	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="failureMessage" name="failureMessage" value="<spring:message code='roster.publishFailureMessage' text='There is some problem in Completing proceeding kindly try after sometime '></spring:message>" disabled="disabled">
		</div> 		
</body>
</html>