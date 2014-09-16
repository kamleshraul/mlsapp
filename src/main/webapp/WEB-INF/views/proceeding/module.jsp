<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proceeding.list" text="List Of Proceedings"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			 $(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					e.preventDefault();
					//console.log("partCount="+partCount);
					$('#addPart'+$('#partCount').val()).trigger('click');
					e.stopPropagation();
					//return false;
				}
				if(e.which==83 && e.ctrlKey){
					e.preventDefault();
					$('#savePart'+$('#partCount').val()).trigger('click');
					e.stopPropagation();
				}
				
			}); 

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
					loadRosterDayFromSessions();
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
			/**** List Tab ****/
			$('#list_tab').click(function(){
				showProceedingList(); 	
			}); 
			/***** Details Tab ****/
			$('#details_tab').click(function(){
				editProceeding(); 	
			}); 
			
			loadRosterDayFromSessions();			
			/**** show roster list method is called by default.****/
			showProceedingList();				
		});
		/**** displaying grid ****/					
		
		function showProceedingList() {
			$("#selectionDiv1").show();
			showTabByIdAndUrl('list_tab', 'proceeding/list?houseType='
					+ $('#selectedHouseType').val() + '&sessionyear='
					+ $("#selectedSessionYear").val() + '&sessionType='
					+ $("#selectedSessionType").val() + '&language='
					+ $("#selectedLanguage").val() + '&day='
					+ $("#selectedDay").val() + '&ugparam='
					+ $("#ugparam").val());
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
			$("#gridURLParams").val(
					"houseType=" + $("#selectedHouseType").val()
							+ "&sessionYear=" + $("#selectedSessionYear").val()
							+ "&sessionType=" + $("#selectedSessionType").val()
							+ '&language=' + $("#selectedLanguage").val()
							+ '&day=' + $("#selectedDay").val() + '&ugparam='
							+ $("#ugparam").val());
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
					$("#selectedDay").trigger("change");
				});
			}
			//		
		}
		
		function rosterWiseReport(){
			var params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val();
			showTabByIdAndUrl('details_tab', 'proceeding/rosterwisereport?'+params);
		}
		
		function reporterWiseReport(){
			var params="houseType=" + $('#selectedHouseType').val()
				+ '&sessionYear=' + $("#selectedSessionYear").val()
				+ '&sessionType=' + $("#selectedSessionType").val()
				+ '&language=' + $("#selectedLanguage").val()
				+ '&day=' +$('#selectedDay').val()
				+ '&user='+$('#ugparam').val();
			showTabByIdAndUrl('details_tab', 'proceeding/reporterwisereport?'+params);
		}
		
		function showPartList(key){
			showTabByIdAndUrl('part_tab', 'proceeding/part/list?proceeding='+key);
		}
		
		function proceedingwiseReport(){
			var params="proceeding="+$('#key').val()+
			'&language=' + $("#selectedLanguage").val();
			showTabByIdAndUrl('details_tab', 'proceeding/part/proceedingwiseReport?'+params);
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
		<input type="hidden" name="pleaseSelect" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">	
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		</div> 		
</body>
</html>