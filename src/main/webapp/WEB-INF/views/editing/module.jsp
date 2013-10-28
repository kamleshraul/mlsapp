<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.list" text="List Of Rosters"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		function hideEditingFilters(){
			$("#memberDiv").hide();
			$("#memberReportTypeDiv").hide();
			$("#pageHeadingDiv").hide();
			$("#selectedReportType").val(0);
			$("#compileDiv").show();
		}
		$(document).ready(function(){
			/****Hide editing filters****/
			hideEditingFilters();
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
			/**** language changes then reload grid****/
			$("#selectedLanguage").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadRosterGrid();							
				}			
			});	
			/**** Day changed reload the grid ****/
			/**** language changes then reload grid****/
			$("#selectedDay").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadRosterGrid();							
				}			
			});	
			
			$("#selectedMember").change(function(e){
				$("#selectedMemberReport").val(0);
				$("#pageHeadingDiv").hide();
				if($(this).val()!='-'){
					$("#memberReportTypeDiv").show();
				}else{
					$("#memberReportTypeDiv").hide();
				}
			});
			
			/****Editor functions****/
			$("#unedited_copy").click(function(){
				showUneditedProceeding();
			});
			$("#compiled_copy").click(function(){
				showProceedingInGeneral("compile", "false");
			});
			$("#edited_copy").click(function(){
				showProceedingInGeneral("edited", "false");
			});
			
			$("#edit_copy").click(function(){
				showProceedingInGeneral("edit", "false");
			});
			/****Editor Functions****/
			
			/**** List Tab ****/
			$('#list_tab').click(function(){
				showRosterList(); 	
				hideEditingFilters();
			}); 
			/***** Details Tab ****/
			$('#details_tab').click(function(){
				editRoster(); 	
			}); 	
				
			/**** show roster list method is called by default.****/
			showRosterList();	
			
			/********/
			$("#selectedReportType").change(function(e){
				var value=$(this).val();
				if(value!='-'){
					if(value=='member'){
						loadMember();
						$("#pageHeadingDiv").hide();
						$("#memberDiv").show();
						$("#compileDiv").hide();
					}else if(value=='pageheading'){
						$("#memberDiv").hide();
						$("#memberReportTypeDiv").hide();
						$("#pageHeadingDiv").show();	
						$("#compileDiv").show();
						loadPageHeading();
					}
				}else{
					$("#pageHeadingDiv").hide();
					$("#memberDiv").hide();
					$("#memberReportTypeDiv").hide();
					$("#compileDiv").show();
				}
			});
			
			$("#selectedMemberReport").change(function(){
				var value=$(this).val();
				if(value!='-'){
					if(value=='member' || value=='asmember'){
						$("#pageHeadingDiv").hide();
					}else if(value=='pageheading'){
						loadMemberPageHeading();
						$("#pageHeadingDiv").show();
					}
				}else{
					$("#pageHeadingDiv").hide();
				}
			});
		});
		/**** displaying grid ****/					
		function showRosterList() {
				//$("#selectionDiv1").show();				
				showTabByIdAndUrl('list_tab','editing/list?houseType='+$('#selectedHouseType').val()
						+$("#selectedSessionYear").val()
						+'&sessionType='+$("#selectedSessionType").val()
						+'&language='+$("#selectedLanguage").val()
						+'&day='+$("#selectedDay").val()
						);
		}
		
		function showUneditedProceeding(){
			//$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+'&reportType=other';
			
			//$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'proceeding/rosterwisereport?'+params);
		}
		
		function showCompiledProceeding(){
			//$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&action=compile'
			+ '&reedit=false'
			+ '&reportType=other';
			
			//$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		function showEditedProceeding(){

			var params="";
			if($("#selectedMember").val()=='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edited'
				+ '&reedit=false'
				+ '&reportType=other';
			}else if($("#selectedMember").val()!='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edited'
				+ '&reedit=false'
				+ '&reportType=member'
				+ '&member='+ $("#selectedMember").val();
			}
			
			//$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		function showEditProceeding(){
			
			var params="";
			if($("#selectedMember").val()=='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edit'
				+ '&reportType=other'
				+ '&reedit=false';
			}else if($("#selectedMember").val()!='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edit'
				+ '&reedit=false'
				+ '&reportType=member'
				+ '&member='+ $("#selectedMember").val();
			}
			console.log(params);
			//$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}		
		
		function showReEditProceeding(){
			var params="";
			if($("#selectedMember").val()=='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edit'
				+ '&reportType=other'
				+ '&reedit=true';
			}else if($("#selectedMember").val()!='-'){
				params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&action=edit'
				+ '&reportType=member'
				+ '&reedit=true'
				+ '&member='+ $("#selectedMember").val();
			}
			//$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		function showProceedingInGeneral(command,reedit){
			params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val()
			+ '&action=' + command
			+ '&reportType=' + $("#selectedReportType").val()
			+ '&reedit=' + reedit
			+ '&member='+ $("#selectedMember").val()
			+ '&memberReportType=' + $("#selectedMemberReport").val()
			+ '&pageheader=' + $("#selectedPageheader").val();
			
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			//$("#selectionDiv1").hide();				
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			//showTabByIdAndUrl('details_tab', 'roster/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		
		/**** reload grid ****/
		function reloadRosterGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()
						+"&sessionType="+$("#selectedSessionType").val()
						+'&language='+$("#selectedLanguage").val()
						+'&day='+$("#selectedDay").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}
		
		function loadMember(){
			var params="houseType=" + $('#selectedHouseType').val()
						+ '&sessionYear=' + $("#selectedSessionYear").val()
						+ '&sessionType=' + $("#selectedSessionType").val()
						+ '&language=' + $("#selectedLanguage").val()
						+ '&day=' +$('#selectedDay').val();
			
			$.get('ref/partmembers?'+params,function(data){
				if(data.length>0){
					var text="<option value='-' selected='selected'>"+$("#pleaseSelect").val()+"</option>";
					var i;
					for(i=0; i < data.length; i++){
						text +="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#selectedMember").empty();
					$("#selectedMember").html(text);
					
					loadMembersReportTypes();
				}
			}).fail(function(){
				
			});			
		}
		
		function loadMembersReportTypes(){
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val();

			$.get('ref/memberreporttype?'+params,function(data){
				if(data.length>0){
					var text="<option value='-' selected='selected'>"+$("#pleaseSelect").val()+"</option>";
					var i;
					for(i=0; i < data.length; i++){
						text +="<option value='"+data[i].value+"'>"+data[i].name+"</option>";
					}
					$("#selectedMemberReport").empty();
					$("#selectedMemberReport").html(text);
				}
			}).fail(function(){
				
			});		
		}
		
		function loadPageHeading(){
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val();

			$.get('ref/reportpageheading?'+params,function(data){
				if(data.length>0){
					var text="<option value='-' selected='selected'>"+$("#pleaseSelect").val()+"</option>";
					var i;
					for(i=0; i < data.length; i++){
						if(data[i].name!=''){
							text +="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
					}
					$("#selectedPageheader").empty();
					$("#selectedPageheader").html(text);
				}
			}).fail(function(){
				
			});	
		}
		
		function loadMemberPageHeading(){
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&member='+ $("#selectedMember").val();

			$.get('ref/memberreportpageheading?'+params,function(data){
				var text="<option value='-' selected='selected'>"+$("#pleaseSelect").val()+"</option>";
				if(data.length>0){					
					var i;
					for(i=0; i < data.length; i++){
						if(data[i].name!=''){
							text +="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
					}
					$("#selectedPageheader").empty();
					$("#selectedPageheader").html(text);
				}else{
					$("#selectedPageheader").html(text);
				}
			}).fail(function(){
				
			});	
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
			<a href="#" id="select_day" class="butSim">
				<spring:message code="roster.day" text="Day"/>
			</a>
			<select name="selectedDay" id="selectedDay" style="width:100px;height: 25px;">
				<!--<option value="-" selected="selected"><spring:message code="please.select" text="Please Select" /></option> -->					
				<c:forEach items="${days}" var="i">
					<option value="${i.number}"><c:out value="${i.value}"></c:out></option>
				</c:forEach> 
			</select> 
			<div>
				<security:authorize access="hasAnyRole('EDIS_EDITOR','EDIS_CHIEF_EDITOR')">
					<div>
						<br />
						<a href="#" id="select_reporttype" class="butSim">
							<spring:message code="editing.reporttype" text="Copy Type"/>
						</a>
						<select name="selectedReportType" id="selectedReportType" style="width:100px;height: 25px;">
							<option value="-" selected="selected"><spring:message code="please.select" text="Please Select" /></option>
							<c:forEach items="${reportTypes}" var="i">
								<option value="${i.value}">${i.name}</option>
							</c:forEach>
						</select> |
						<div style="display: inline;" id="memberDiv">
							<a href="#" id="select_member" class="butSim">
								<spring:message code="editing.member" text="Member"/>
							</a>
							<select name="selectedMember" id="selectedMember" style="width:100px;height: 25px;">
								<option value="-" selected="selected"><spring:message code="please.select" text="Please Select" /></option>
							</select> |
						</div>
						<div style="display: inline;" id="memberReportTypeDiv">
							<a href="#" id="select_memberreport" class="butSim">
								<spring:message code="editing.member.report" text="Member's Report"/>
							</a>
							<select name="selectedMemberReport" id="selectedMemberReport" style="width:100px;height: 25px;">
								<option value="-" selected="selected"><spring:message code="please.select" text="Please Select" /></option>
							</select> |
						</div>
						<div id="pageHeadingDiv" style="display: inline;">
							<a href="#" id="select_pageheader" class="butSim">
								<spring:message code="editing.pageheader" text="Page Header"/>
							</a>
							<select name="selectedPageheader" id="selectedPageheader" style="width:100px;height: 25px;">
								<option value="-" selected="selected"><spring:message code="please.select" text="Please Select" /></option>
							</select> |
						</div>
					</div>		
					<br />
					<a href="#" id="unedited_copy" class="butSim">
						<spring:message code="editor.unedited" text="Unedited Copy"/>
					</a><div style="display: inline;" id="compileDiv">|
					<a href="#" id="compiled_copy" class="butSim">
						<spring:message code="editor.compiled" text="Compiled Copy"/>
					</a></div>|
					<a href="#" id="edited_copy" class="butSim">
						<spring:message code="editor.edited" text="Edited Copy"/>
					</a> |			
					<a href="#" id="edit_copy" class="butSim">
						<spring:message code="editor.edit" text="Editing"/>
					</a> |
				</security:authorize>		
			</div>
			<hr>							
		</div>				
		
		<div class="tabContent">
		</div>
		
		<input type="hidden" id="key" name="key">
		<input type="hidden" name="srole" id="srole" value="${role}">				
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		</div> 		
</body>
</html>