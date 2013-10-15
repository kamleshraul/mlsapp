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
			
			/**** List Tab ****/
			$('#list_tab').click(function(){
				showRosterList(); 	
			}); 
			/***** Details Tab ****/
			$('#details_tab').click(function(){
				editRoster(); 	
			}); 	
				
			/**** show roster list method is called by default.****/
			showRosterList();				
		});
		/**** displaying grid ****/					
		function showRosterList() {
				$("#selectionDiv1").show();				
				showTabByIdAndUrl('list_tab','editing/list?houseType='+$('#selectedHouseType').val()
						+$("#selectedSessionYear").val()
						+'&sessionType='+$("#selectedSessionType").val()
						+'&language='+$("#selectedLanguage").val()
						+'&day='+$("#selectedDay").val()
						);
		}
		
		function showUneditedProceeding(){
			$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val();
			
			showTabByIdAndUrl('details_tab', 'proceeding/rosterwisereport?'+params);
		}
		
		function showCompiledProceeding(){
			$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&action=compile'
			+ '&reedit=false';
			
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		function showEditedProceeding(){
			$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&action=edited'
			+ '&reedit=false';
			
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		function showEditProceeding(){
			$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&action=edit'
			+ '&reedit=false';
			
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}		
		
		function showReEditProceeding(){
			
			$("#selectionDiv1").hide();
			var params="houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' +$('#selectedDay').val()
			+ '&action=edit'
			+ '&reedit=true';
			
			showTabByIdAndUrl('details_tab', 'editing/compiledreport?'+params);
		}
		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();				
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
			</select> |								
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