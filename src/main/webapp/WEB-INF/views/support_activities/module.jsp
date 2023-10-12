<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>Support Utilities</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<!-- Include Select2 library -->


	<script type="text/javascript">
	//var primaryMemberControlName=$(".autosuggest").attr("id");
	var curroffset=0;
	var page = 1;
	var record=10;
	var lastPage = false;
	//var previousSearchTerm="";
		
	//var controlName=$(".autosuggestmultiple").attr("id");
	var previousSearchCount=record;
		$(document).ready(function() {
			
			loadMemberbySession();
			initControls();
			
			//
			$(".filter").hide();
			$(".member").hide();
			//
			/**** Remove hr from embeddd table ****/
			$("#searchTable td > table hr:last").remove();
			/**** Reset Filters ****/
			$("#reset").click(function(){
				$("#houseTypeForSearch").val($("#defaultSelectedHouseType").val());
				$("#houseTypeForSearch").css("color","");
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				$("#deviceTypeForSearch").val($("#defaultSelectedDeviceType").val());
				$("#deviceTypeForSearch").css("color","");
				$("#sessionYearForSearch").val("-");
				$("#sessionYearForSearch").css("color","");				
				$("#sessionTypeForSearch").val("-");
				$("#searchBy").val("-");
				$(".filter").hide();	
				$(".member").hide();
				$("#sessionTypeForSearch").css("color","");								
				$("#groupForSearch").empty();
				$("#groupForSearch").html(text);	
				$("#groupForSearch").css("color","");								
				$("#answeringDateForSearch").empty();
				$("#answeringDateForSearch").html(text);
				$("#answeringDateForSearch").css("color","");				
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);
				$("#ministryForSearch").css("color","");				
				$("#departmentForSearch").empty();
				$("#departmentForSearch").html(text);
				$("#departmentForSearch").css("color","");				
				$("#subDepartmentForSearch").empty();
				$("#subDepartmentForSearch").html(text);
				$("#subDepartmentForSearch").css("color","");				
				$("#statusForSearch").val("-");	
				$("#statusForSearch").css("color","");
				if($('#whichDevice').val()=='bills_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
				}						
			});
			/******** Filters *********/
			/**** Session Year ****/
			$("#sessionYearForSearch").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#sessionTypeForSearch").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&type!='-'&&houseType!='-'){
					if($("#whichDevice").val().indexOf('questions_')==0){
						loadGrp(houseType,value,type);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupForSearch").empty();
					$("#groupForSearch").html(text);					
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);					
				}
			});
			
			
			/***************/
		
			$("#houseTypeForSearch").change(function(){
				loadMemberbySession();
			})
		
			
			/***************/
			
			/**** Session Type ****/
			$("#sessionTypeForSearch").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#sessionYearForSearch").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&year!='-'&&houseType!='-'){
					if($("#whichDevice").val().indexOf('questions_')==0){
						loadGrp(houseType,year,value);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupForSearch").empty();
					$("#groupForSearch").html(text);					
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);
				}
			});
			/**** Device Type ****/
			$("#deviceTypeForSearch").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");
					$.get('ref/devicetype/'+value+'/statuses_for_support_activities', function(data) {
						//console.log(data);
						var options="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";	
						$('#statusForSearch option').empty();
						for(var i=0;i<data.length;i++){
							options+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$('#statusForSearch').html(options);
					});
				}else{
					$(this).css("color","");					
				}
				
			});
			
			/*----------------------------------------------------*/
			
			$("#searchBy").change(function(){
				var value = $(this).val();
				if(value == 'searchByNumber'){
					$(".filter").show();
					$(".member").hide();
				}else if(value == 'searchByMember'){
					$(".member").show();
					$(".filter").hide();
				}else{
					$(".filter").hide();	
					$(".member").hide();
				}
				
			})
			/*----------------------------------------------------*/

			/**** primary Question's Details ****/
			$("#primary").click(function(){
				viewDetail($("#deviceId").val());
			});
			/**** Back To Search Page****/
			$("#backToSearch").click(function(){
					back();
			});			
			/**** Search Content Changes ****/
			$("#searchvalue").change(function(){
				start=0;				
				$("#clubbingResult").empty();	
				$("#searchTable tbody").empty();
				$("#clubbingDiv").hide();
				previousSearchCount=record;										
			});			
			/**** On clicking search button ****/
			$("#search").click(function(){
				var offset = curroffset;
				search(offset);
			});
			/**** On Page Load ****/
			$("#clubbingDiv").hide();
			/**** Back To Question ****/
			/**** Back To Question ****/
			$("#backToQuestion").click(function(){
				/* $("#reset").click();
				$('#searchvalue').val(""); */
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToQuestionDiv").hide();
				if($("#assistantDiv").length>0){
				$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}else{
					if($('#whichDevice').val()=='questions_') {
						showQuestionList();
					}
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			
			
			$("#searchBy").change(function(){
				var value = $(this).val();
				if(value != '-'){
					if(value == 'searchByNumber'){						
						$("#searchByMemberDiv").css({'display': 'none'});
					}else if(value == 'searchByMember'){
						$("#formattedPrimaryMember").val('');
						$("#primaryMember").val('');
						$("#searchByMemberDiv").css({'display': 'inline-block'});					
					}
				}else{
					$("#searchByMemberDiv").css({'display': 'none'});
				}
			});
			
			if($('#whichDevice').val()=='questions_'){
				loadAllMinistries();
			}			
		});
		
	function changePage(value){
		//console.log(value)
		if(value == 'prev'){
			if(page == 1){
				$.prompt("you are Already on first Page")
			}else{
				search(curroffset-10);
				page--;
				
			}
		}else{
			//console.log(lastPage)
			if(!lastPage){
				search(curroffset+10);
				page++;
			}else{
				$.prompt("you are Already on last Page")
			}
		}
	}
		

		
		/************   ******/
		function loadMemberbySession(){
			$.get('ref/sessionbyhousetype/'+$("#houseTypeForSearch").val()+"/"+$("#sessionYearForSearch").val()+"/"+$("#sessionTypeForSearch").val()
					  ,function(session){
				
				$.get('ref/getMemberForSpecificSession/sessionId/'+session.id,function(data){
					 if(data){
						//console.log(data);
						 $("#selectedMember").empty()
			 		   		var option = "<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			 		   		for (let i = 0; i < data.length; i++) {
			 		   		 option = option +'<option value="'+data[i].id+'"> '+data[i].firstNameAliasLastName+' </option>  '
			 		   		}
			 		   		
			 		   		$("#selectedMember").html(option)
			 		   		
			 		   		//$("#selectedMember").show()
					} 
				})
			})
		}
		
			/* $.get('ref/getMemberForSpecificSession/houseType/'+$("#houseTypeForSearch").val()+"/sessionYear/"+$("#sessionYearForSearch").val()+"/sessionType/"+$("#sessionTypeForSearch").val(),function(data){
				if(data){
					//console.log(data);
					 $("#selectedMember").empty()
		 		   		var option = "<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
		 		   		for (let i = 0; i < data.length; i++) {
		 		   		 option = option +'<option value="'+data[i].id+'"> '+data[i].firstNameAliasLastName+' </option>  '
		 		   		}
		 		   		
		 		   		$("#selectedMember").html(option)
		 		   		
		 		   		//$("#selectedMember").show()
				}
			}).fail(function(){
 				if($("#ErrorMsg").val()!=''){
 					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
 				}else{
 					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
 				}
 				
 			}) */
		
	
	
		/**** On clicking search button ****/		
		function search(offset){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			  start=0;
			 
			var resourceURL = "";
			var postData = "";
			
			$.get('ref/sessionbyhousetype/'+$("#houseTypeForSearch").val()+"/"+$("#sessionYearForSearch").val()+"/"+$("#sessionTypeForSearch").val()
				  ,function(data){
				
				/*
				 
				 Also adding session id 
				*/
				postData={session:data.id};
				
				/*
					Adding Selected DeviceType
				*/
				var deviceType = $("#deviceTypeForSearch").val();						
				if(deviceType!='' && deviceType!='-'){
					postData['deviceType']=deviceType;
				} 
				
				postData['offset']=offset;
				
				
				/*
					Url for COntroller Method (Currently only in future May vary )
				*/
				resourceURL = "admin/getDeviceDetailforSupportActivity"
				
				
				/*
					Adding param in PostData Object 
					eg :- 1.)if SearchByNumber Selected Get Number from Search Text Field
						  2.)if SearchByMmber  Selected Then Get member From DropDown
				*/
				var selectedSearchBy= $("#searchBy").val();
						  
				
				
				if(selectedSearchBy=='searchByNumber'){
					
						postData['param']=$("#searchvalue").val();	
						postData['filter']=  $("#filter").val();  
					
				}else if(selectedSearchBy=='searchByMember'){
						postData['param']= $('#selectedMember').val();	
						postData['filter']=  'Member';
					
				}
				
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeForSearch").val();
				}
				if($("#sessionYearForSearch").length>0){
					postData['sessionYear']=$("#sessionYearForSearch").val();
				}
				if($("#sessionTypeForSearch").length>0){
					postData['sessionType']=$("#sessionTypeForSearch").val();
				}
				
				//console.log(resourceURL +" "+JSON.stringify(postData));
				
				if(selectedSearchBy=='searchByNumber' && $("#searchvalue").val()==''){
					$.prompt("Please Enter Number You Want to Search ")
				} else if(selectedSearchBy=='searchByNumber' && $("#filter").val()=='-'){
					$.prompt("Please Select the filter  ")
				}else{
					  $.post(resourceURL,postData,function(data){

						$("#viewDeviceDiv").html(data)
						curroffset = offset
						$('#pageNumber').html(page);
						$.unblockUI();
							
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
				
			})
			
		}
		
		
		/**** view device details in readonly mode ****/
		function viewDetail(clubId){
			var resourceURL="";
			var deviceTypeParameterName = "";
			if($('#whichDevice'=='questions_')) {
				deviceTypeParameterName = "questionType";
			} else if($('#whichDevice'=='bills_')) {
				deviceTypeParameterName = "deviceType";
			} else if($('#whichDevice'=='motions_')){
				
			}	
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters="houseType="+$("#houseTypeForSearch").val()
			+"&sessionYear="+$("#sessionYearForSearch").val()
			+"&sessionType="+$("#sessionTypeForSearch").val()
			+"&"+deviceTypeParameterName+"="+$("#refDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			if($('#whichDevice').val()=='questions_') {
				resourceURL='question/'+clubId+'/edit?'+parameters;
			}else if($('#whichDevice').val()=='bills_') {
				resourceURL='bill/'+clubId+'/edit?'+parameters;
			}else if($('#whichDevice').val()=='motions_') {
				resourceURL='motion/'+clubId+'/edit?'+parameters;
				
			}
			$.get(resourceURL,function(data){
				$("#clubbingDiv").hide();
				$("#viewQuestion").html(data);
				$("#viewQuestionDiv").show();
				$.unblockUI();				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			$.unblockUI();						
		}
		/**** on clicking back ****/
		function back(){
			$("#clubbingDiv").show();		
			$("#clubbingResult").empty();
			$("#viewQuestion").empty();
			$("#viewQuestionDiv").hide();
		}
	</script>

<style type="text/css">

.filterSelected{
color:blue;
}
.highlightedSearchPattern{
font-weight: bold;
text-decoration: underline;
}
td>.strippedTable{
	width: 350px;
}
</style>
</head>
<body>
	<div class="clearfix content">
		<div class="clearfix tabbar">
			<ul class="tabs">
				<li>
					<a id="search_tab" href="#" class="tab selected">
					   <spring:message code="generic.search" text="Search"></spring:message>
					</a>
				</li>
				<li>
					<a id="details_tab" href="#" class="tab">
					   <spring:message code="generic.details" text="Details"></spring:message>
					</a>
				</li>			
			</ul>
			<div class="tabContent clearfix">
				<p id="error_p" style="display: none;">&nbsp;</p>
				<c:if test="${(error!='') && (error!=null)}">
					<h4 style="color: #FF0000;">${error}</h4>
				</c:if>
				<div id="supportSearch">
					<a href="#" class="butSim">
						<spring:message code="generic.houseType" text="House Type"/>
					</a>		
					<select name="houseTypeForSearch" id="houseTypeForSearch" style="width:100px;height: 25px;">			
						<%-- <option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option> --%>			
						<c:forEach items="${houseTypes}" var="i">
							<c:choose>
								<c:when test="${defaultSelectedHouseType==i.type}">
									<option value="${i.type}" selected="selected">
										<c:out value="${i.name}"></c:out>
									</option>
								</c:when>
								<c:otherwise>
									<option value="${i.type}">
										<c:out value="${i.name}"></c:out>
									</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					
					<a href="#" class="butSim">
						<spring:message code="generic.deviceType" text="Device Type"/>
					</a>		
					<select name="deviceTypeForSearch" id="deviceTypeForSearch" style="width:100px;height: 25px;">			
						<c:forEach items="${deviceTypes}" var="i">
							<c:choose>
								<c:when test="${defaultSelectedDeviceType==i.id}">
									<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
								</c:when>
								<c:otherwise>
									<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select> 
					<select id="deviceTypeForSearchMaster" style="display:none;">
						<c:forEach items="${deviceTypes}" var="i">
							<option value="${i.id}"><c:out value="${i.type}"></c:out></option>			
						</c:forEach>
					</select> |
					
					<a href="#" class="butSim">
						<spring:message code="generic.sessionyear" text="Year"/>
					</a>			
					<select name="sessionYearForSearch" id="sessionYearForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach items="${years}" var="i">
							<c:choose>
								<c:when test="${sessionYear==i.id}">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
								</c:when>
								<c:otherwise>
									<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
								</c:otherwise>
							</c:choose>
						</c:forEach> 
					</select> |	
							
					<a href="#" class="butSim">
						<spring:message code="generic.sessionType" text="Session Type"/>
					</a>			
					<select name="sessionTypeForSearch" id="sessionTypeForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
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
					<%-- <hr>
					<a href="#" class="butSim">
						<spring:message code="question.group" text="Group"/>
					</a>			
					<select name="groupForSearch" id="groupForSearch" style="width:100px;height: 25px;">
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
						<c:forEach items="${groups}" var="i">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>
					</select> |	 --%>		
					<%-- <a href="#" class="butSim">
						<spring:message code="question.answeringDate" text="Answering Date"/>
					</a>			
					<select name="answeringDateForSearch" id="answeringDateForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |				
					<a href="#" class="butSim">
						<spring:message code="generic.ministry" text="Ministry"/>
					</a>			
					<select name="ministryForSearch" id="ministryForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			 --%>
					<%-- <a href="#" class="butSim">
						<spring:message code="generic.department" text="Department"/>
					</a>			
					<select name="departmentForSearch" id="departmentForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> | --%>
					
					<%-- <a href="#" class="butSim">
						<spring:message code="generic.subdepartment" text="Sub Department"/>
					</a> --%>	
<%-- 					<select name="subDepartmentForSearch" id="subDepartmentForSearch" style="width:100px;height: 25px;">
						
						<c:forEach items="${subdepartments}" var="i">
							<option value="${i.id}">${i.displayName}</option>
						</c:forEach>
					</select> --%>
					
					<%-- <select id="selecteDeviceType">
					<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
					  <c:forEach items="${subdepartments}" var="i" varStatus="loop">
					    <option value="${i.id}">
					        ${i.displayName}
					    </option>
					  </c:forEach>
					</select>
					
					 |	
					
					<a href="#" class="butSim"> 
						<spring:message	code="generic.status" text="Status" />
					</a>
					<select name="statusForSearch" id="statusForSearch"
						style="width: 150px; height: 25px;">
						<option value="0">
								<spring:message code='please.select' text='Please Select'/>
						</option>
						<c:forEach items="${statusesForDeviceType}" var="i">
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:forEach>
					</select> |
					<hr>
					<a href="#" class="butSim">
						<spring:message	code="generic.clubbingStatus" text="Clubbing Status"/>
					</a>
					<select name="clubbingStatusForSearch" id="clubbingStatusForSearch" style="height: 25px;">
						<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
						<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
						<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
					</select> |
						 --%>			
					<hr>
					<a href="#" class="butSim">
						<spring:message code="generic.searchType" text="Search By"/>
					</a>			
					<select name="searchBy" id="searchBy" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>	
						<c:forEach items="${searchBy}" var="sb">
							<option value="${sb.value}">${sb.name}</option>
						</c:forEach>
					</select> 	 
					
					<a href="#" class="butSim filter">
						Filter
					</a>			
					<select name="filter" id="filter" class="filter" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>	
						<option value=Specific >Specific</option>
						<option value="All">All</option>
					</select> 
					
					<a href="#" class="butSim member">
						Member
					</a>
					<select id="selectedMember" class="member">
					</select>
					
					<!-- <div id="searchByMemberDiv" style="display: none;">
						<input type="text" class="sText autosuggest" style="width: 150px;" id="formattedPrimaryMember"/>
						<input type="hidden" style="width: 60px;" id="primaryMember"/>
					</div>| -->
					
				</div>	
				
				
				<hr>
				
				
				<div id="searchBoxDiv">
					<table style="padding: 0px; margin: 0px;"> 
						<tr> 
							<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:2px;">
								<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
							</td>
							<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
								<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
							</td>
							<td>
								<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.reset" text="Reset Filters"></spring:message></a>
								<%-- <a href="#" id="backToQuestion" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.back" text="Back"></spring:message></a> --%>
							</td>
						</tr>
					</table>
				</div>
				
				<%-- <p id="clubbingP">
				<c:choose>
					<c:when test="${whichDevice=='questions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${subject}</c:when>
					<c:when test="${whichDevice=='bills_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${title}</c:when>
					<c:when test="${whichDevice=='motions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${subject}</c:when>
				</c:choose>
				<input type="hidden" id="deviceId" value="${id }">
				<input type="hidden" id="deviceNumber" value="${number}">
				</p>
				
				<div id="clubbingDiv">
				
				<div id="clubbingResult" style="margin: 10px;">
				</div> --%>
				
				</div>
				
				
				<div id="viewDeviceDiv" >
					<%-- <a id="backToSearch" href="#" style="display:block;"><spring:message code="clubbing.back" text="Back to search page"></spring:message></a> --%>
				
				</div>
				
			</div>
		</div>
	</div>
	
	<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
	<input type="hidden" id="defaultSelectedHouseType" value="${defaultSelectedHouseType}" />
	<input type="hidden" id="defaultSelectedDeviceType" value="${defaultSelectedDeviceType}" />
	<input id="nothingToSearchMsg" value="<spring:message code='clubbing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
	<input id="noResultsMsg" value="<spring:message code='clubbing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
	<input id="viewDetailMsg" value="<spring:message code='clubbing.viewdetail' text='Detail'></spring:message>" type="hidden">
	<input id="clubMsg" value="<spring:message code='clubbing.club' text='Club'></spring:message>" type="hidden">
	<input id="unclubMsg" value="<spring:message code='clubbing.unclub' text='Unclub'></spring:message>" type="hidden">
	<input id="groupChangeMsg" value="<spring:message code='clubbing.groupchange' text='Change Group'></spring:message>" type="hidden">
	<input id="ministryChangeMsg" value="<spring:message code='clubbing.ministrychange' text='Change Ministry'></spring:message>" type="hidden">
	<input id="departmentChangeMsg" value="<spring:message code='clubbing.departmentchange' text='Change Department'></spring:message>" type="hidden">
	<input id="subDepartmentChangeMsg" value="<spring:message code='clubbing.subDepartmentchange' text='Change Sub Department'></spring:message>" type="hidden">
	<input id="referencingMsg" value="<spring:message code='clubbing.referencing' text='Referencing'></spring:message>" type="hidden">
	<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
	<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
	<input id="houseTypeCommon" value="${houseType}" type="hidden">
	<input id="houseTypeYearSessionTypeEmptyMsg" value="<spring:message code='client.error.generic.yeartypeempty' text='Session Year and Session Type must be selected to continue'/>" type="hidden">
	<input id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="whichDevice" value="${whichDevice}" />
	<input id="refDeviceType" type="hidden" value="${deviceType}" />
	<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
	<input type="hidden" id="subdepartmentValue" value="<spring:message code='question.department' text='subDepartment'/>" />
	<input type="hidden" id="billWithoutNumber" value="<spring:message code='bill.referredBillWithoutNumber' text='Click To See'/>">
	<input id="ballotDateTitle" value="<spring:message code='question.ballotDateTitle' text='Ballot Date'/>" type="hidden">
	<input id="chartDateTitle" value="<spring:message code='question.chartDateTitle' text='Chart Date'/>" type="hidden">
	<input id="parentTitle" value="<spring:message code='question.parentTitle' text='Parent'/>" type="hidden">
	<input id="clubbingTitle" value="<spring:message code='question.clubbingTitle' text='Clubbed Entities'/>" type="hidden">
</body>
</html>