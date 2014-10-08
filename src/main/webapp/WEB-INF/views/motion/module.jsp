<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="motion.list" text="List Of Motions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			/**** On Page Load ****/
			var currentDeviceType = $("#currentDeviceType").val();
			var currentHouseType = $("#currentHouseType").val();				
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
				showMotionList();
			});			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){	
					reloadMotionGrid();									
				}	
			});	
			/**** session year changes then reload grid****/			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadMotionGrid();								
				}			
			});
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadMotionGrid();							
				}			
			});
			/**** motion type changes then reload grid****/			
			$("#selectedMotionType").change(function(){
				var value = $(this).val();
				var text = $("#deviceTypeMaster option[value='"+value+"']").text();				
				if(value != ""){				
					reloadMotionGrid();
				}
				
			});	
			/**** status changes then reload grid****/			
			$("#selectedStatus").change(function(){
				var value=$(this).val();
				if(value!=""){				
					reloadMotionGrid();
					$("#selectedFileCount").val("-");
				}
			});	
			/**** Ballot ****/
			$('#ballot_tab').click(function(){
				$("#selectionDiv1").hide();
				viewBallot();
			});	
			/**** Bulk Putup ****/
			$("#bulkputup_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutup();
			});	
			/**** Bulk Putup ****/
			$("#bulkputupassistant_tab").click(function(){
				$("#selectionDiv1").hide();
				bulkPutupAssistant();
			});		
			/**** show motion list method is called by default.****/
			showMotionList();				
		});
		/**** displaying grid ****/					
		function showMotionList() {
				showTabByIdAndUrl('list_tab','motion/list?houseType='+$('#selectedHouseType').val()
						+'&motionType='+$("#selectedMotionType").val()+'&sessionYear='
						+$("#selectedSessionYear").val()+'&sessionType='+$("#selectedSessionType").val()+
						"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()						
						);
		}
		/**** new motion ****/
		function newMotion() {
			$("#cancelFn").val("newMotion");
			//since id of motion has not been created so key is set to empty value
			$("#key").val("");				
			showTabByIdAndUrl('details_tab','motion/new?'+$("#gridURLParams").val());
		}
		/**** edit motion ****/		
		function editMotion(row) {
			$("#cancelFn").val("editMotion");			
			row=$('#key').val();			
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());				
				return false;
			}else{
				showTabByIdAndUrl('details_tab','motion/'+row+'/edit?'+$("#gridURLParams").val());
			}			
		}	
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'motion/'+rowid+'/edit?'+$("#gridURLParams").val());
		}	
		/**** delete motion ****/	
		function deleteMotion() {
			var row=$("#key").val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('motion/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				        	showMotionList();
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
		function reloadMotionGrid(){
				$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						+"&sessionYear="+$("#selectedSessionYear").val()+
						"&sessionType="+$("#selectedSessionType").val()+
						"&motionType="+$("#selectedMotionType").val()
						+"&ugparam="+$("#ugparam").val()
						+"&status="+$("#selectedStatus").val()
						+"&role="+$("#srole").val()
						+"&usergroup="+$("#currentusergroup").val()
						+"&usergroupType="+$("#currentusergroupType").val()
						);
				var oldURL=$("#grid").getGridParam("url");
				var baseURL=oldURL.split("?")[0];
				newURL=baseURL+"?"+$("#gridURLParams").val();
				$("#grid").setGridParam({"url":newURL});
				$("#grid").trigger("reloadGrid");							
		}
		/**** Ballot Tab ****/
		function viewBallot() {
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedMotionType").val()
				 +"&ugparam="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val()
				 +"&usergroup="+$("#currentusergroup").val()
				 +"&usergroupType="+$("#currentusergroupType").val();
			}			
			var resourceURL = 'ballot/motion/init?' + parameters +"&deviceType="+$("#selectedMotionType").val();
			showTabByIdAndUrl('ballot_tab', resourceURL);
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
			var resourceURL = 'motion/bulksubmission?' + parameters +"&itemscount="+$("#selectedItemsCount").val();
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
				 +"&file="+$("#selectedFileCount").val()
				 +"&itemscount="+$("#selectedItemsCount").val();	
				 var resource='motion/bulksubmission/assistant/int';
				 var resourceURL=resource+"?"+parameters;	
				showTabByIdAndUrl('bulkputupassistant_tab', resourceURL);				
		}	
		
		function showDiscussionSelection(){
			var parameters = "houseType="+$("#selectedHouseType").val()
			 +"&sessionYear="+$("#selectedSessionYear").val()
			 +"&sessionType="+$("#selectedSessionType").val()
			 +"&motionType="+$("#selectedMotionType").val()
			 +"&ugparam="+$("#ugparam").val()
			 +"&status="+$("#selectedStatus").val()
			 +"&role="+$("#srole").val()
			 +"&usergroup="+$("#currentusergroup").val()
			 +"&usergroupType="+$("#currentusergroupType").val();
				
			 var resource = 'motion/discussionselection';
			 var resourceURL = resource+"?"+parameters;	
			 $("#selectionDiv").hide();
			 $("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', resourceURL);
		}
		
		function showJodPatra(){
			var url = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
			
			$.get(url,function(data){
				if(data){
					showTabByIdAndUrl('details_tab','motion/report/motion/genreport?sessionId='+data.id+"&deviceTypeId="+$("#selectedMotionType").val()+"&statusId=" + $("#selectedStatus").val() + "&locale="+$("#moduleLocale").val()+"&report=MOTION_JODPATRA_REPORT&reportout=motionjodpatra");
				}
			});
			var resourceURL = resource+"?"+parameters;	
			$("#selectionDiv").hide();
			$("#selectionDiv1").hide();
			showTabByIdAndUrl('details_tab', resourceURL);
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
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">			
			<li>
				<a id="bulkputup_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">			
			<li>
				<a id="bulkputupassistant_tab" href="#" class="tab">
				   <spring:message code="generic.bulkputup" text="Bulk Putup">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT', 'MOIS_UNDER_SECRETARY',
				'MOIS_DEPUTY_SECRETARY', 'MOIS_PRINCIPAL_SECRETARY', 'MOIS_SPEAKER', 'MOIS_JOINT_SECRETARY',
				'MOIS_SECRETARY', 'MOIS_OFFICER_ON_SPECIAL_DUTY', 'MOIS_DEPUTY_SPEAKER', 'MOIS_CHAIRMAN',
				'MOIS_DEPUTY_CHAIRMAN', 'MOIS_SECTION_OFFICER', 'MOIS_UNDER_SECRETARY_COMMITTEE',
				'SUPER_ADMIN')">
					<c:if test="${houseType=='lowerhouse'}">
					<li>
					<a id="ballot_tab" href="#" class="tab">
				   		<spring:message code="motion.memberballot" text="Ballot"></spring:message>
					</a>
					</li>			
					</c:if>				
			</security:authorize>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
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
				<spring:message code="question.sessionyear" text="Year"/>
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
				<spring:message code="question.sessionType" text="Session Type"/>
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
						
			<a href="#" id="select_motionType" class="butSim">
				<spring:message code="question.questionType" text="Motion Type"/>
			</a>
			<select name="selectedMotionType" id="selectedMotionType" style="width:100px;height: 25px;">			
			<c:forEach items="${motionTypes}" var="i">
			<c:choose>
			<c:when test="${motionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> 
			<select id="deviceTypeMaster" style="display:none;">
			<c:forEach items="${motionTypes }" var="i">
			<option value="${i.id }">${i.type }</option>
			</c:forEach>			
			</select>|		
			
			<a href="#" id="select_status" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>
			<select name="selectedStatus" id="selectedStatus" style="width:100px;height: 25px;">			
			<c:forEach items="${status}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach>
			</select> |	
					
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
			<hr>		
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="motion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
			<option value="30">30</option>
			<option value="25">25</option>
			<option value="20">20</option>
			<option value="15">15</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			</security:authorize>	
			<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
			<hr>			
			<a href="#" id="select_itemcount" class="butSim">
				<spring:message code="motion.itemcount" text="No. of Motions(Bulk Putup)"/>
			</a>
			<select name="selectedItemsCount" id="selectedItemsCount" style="width:100px;height: 25px;">			
			<option value="100">100</option>
			<option value="75">75</option>
			<option value="50">50</option>
			<option value="25">25</option>
			<option value="10">10</option>
			<option value="5">05</option>		
			</select>|	
			<a href="#" id="select_filecount" class="butSim">
				<spring:message code="motion.filecount" text="Select File(Bulk Putup)"/>
			</a>
			<select name="selectedFileCount" id="selectedFileCount" style="width:100px;height: 25px;">			
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
			<c:if test="${highestFileNo>0 }">
			<c:forEach var="i" begin="1" step="1" end="${highestFileNo}">
			<option value="${i}">${i}</option>
			</c:forEach>
			</c:if>						
			</select>|	
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
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
</body>
</html>