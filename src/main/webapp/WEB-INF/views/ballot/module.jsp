<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="ballot.list" text="List Of Ballots"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//var fancybox1=$("#view_member_ballot").fancybox({'autoSize':false,'width':600,'height':700,'type':'ajax'});
			//we will allow a user to view questions on the basis of housetype,session year,session type 
			//and question type.Here we are deciding the default selected house type.If house type of 
			//auth user is both house then lowerhouse will be deafult otherwise it will be same as house type
			//of auth user
			var houseType=$("#houseTypeOfAuthUser").val();
			if(houseType=="bothhouse"){
				$("#selectedHouseType").val("lowerhouse");
			}else if(houseType=="lowerhouse"||houseType=="upperhouse"){
				$("#selectedHouseType").val(houseType);				
			}
			//here we are trying to add date mask in grid search when field names
			//ends with Date
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});			
			$('#list_tab').click(function(){
				showQuestionList();
			});				
			//If house type changes then we need to change the value of selected house type,grid url param
			// and reload the grid			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){					
							$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&ugparam="+$("#ugparam").val());			
							var oldURL=$("#grid").getGridParam("url");
							var baseURL=oldURL.split("?")[0];
							newURL=baseURL+"?"+$("#gridURLParams").val();
							$("#grid").setGridParam({"url":newURL});
							$("#grid").trigger("reloadGrid");							
				}				
			});	
			//If session year changes then we need to change the value of selected session year,grid url param
			// and reload the grid		
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&ugparam="+$("#ugparam").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}			
			});
			//If session type changes then we need to change the value of selected session type,grid url param
			// and reload the grid	
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&ugparam="+$("#ugparam").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}					
			});
			//If question type changes then we need to change the value of selected question type,grid url param
			// and reload the grid	
			$("#selectedDeviceType").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&ugparam="+$("#ugparam").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}
			});		
			$("#create_member_ballot").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
				var params="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&round="+$("#selectedRound").val();
				$.post('ballot/memberballot?'+params,function(data){
					$.unblockUI();					
					if(data!=null){
						if(data!=""){
							if(data=='success'){
								alert("Member Ballot created for round:"+$("#selectedRound").val());
							}else if(data=='failure'){
								alert("Member Ballot already created for round:"+$("#selectedRound").val());
							}else{
								alert("Member Ballot already created for round:"+$("#selectedRound").val());
							}
						}else{
							alert("Member Ballot for round:"+$("#selectedRound").val()+"failed to create at this time.Try again");
						}
					}else{
						alert("Member Ballot for round:"+$("#selectedRound").val()+"failed to create at this time.Try again");
					}
				});
				return false;
			});		
			$("#view_member_ballot").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
				var params="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val()+"&round="+$("#selectedRound").val();
				$.get('ballot/memberballot?'+params,function(data){
					$.unblockUI();					
					$.fancybox.open(data,{autoSize:false,width:600,height:600});
				},'html');
				return false;
			});	
			$("#view_prioritylist").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
				var params="houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&deviceType="+$("#selectedDeviceType").val();
				$("#basics").hide();
				$("#rounds").hide();
				$("#ballots").hide();
				showTabByIdAndUrl('details_tab','ballot/prioritylist?'+params);	
				$.unblockUI();				
				return false;			
			});				
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newQuestion();
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showQuestionList();
				}
				if(e.which==79 && e.ctrlKey){
					editQuestion($('#key').val());
				}
				if(e.which==8 && e.ctrlKey){
					deleteQuestion($('#key').val());
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});	
		});		
	</script>
</head>
<body>
	<!-- .section -->
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
		
		<div class="commandbarContent" style="margin-top: 10px;" id="basics">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
			<c:choose>
			<c:when test="${houseTypeSelected==i.type}">
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
			<a href="#" id="select_questionType" class="butSim">
				<spring:message code="ballot.deviceType" text="Device Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:100px;height: 25px;">			
			<c:forEach items="${deviceTypes}" var="i">
			<c:choose>
			<c:when test="${deviceType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select> |
			<a href="#" id="view_prioritylist" class="butSim">
				<spring:message code="ballot.prioritylist" text="Priority List"/>
			</a> 			
		</div>
			
		<div class="commandbarContent" style="margin-top: 10px;" id="rounds">	
			<c:if test="${!(empty noOfRounds)}">
			<a href="#" id="select_round" class="butSim">
				<spring:message code="ballot.round" text="Round"/>
			</a>
			<select name="selectedRound" id="selectedRound" style="width:100px;height: 25px;">				
			<c:forEach begin="1" end="${noOfRounds}" step="1" var="i">			
			<option value="${i}"><c:out value="${i}"></c:out></option>	
			</c:forEach> 
			</select> |	
			<a href="#" id="create_member_ballot" class="butSim">
				<spring:message code="ballot.creatememberballot" text="Create Member Ballot"/>
			</a> |			
			<a href="#" id="view_member_ballot" class="butSim">
				<spring:message code="ballot.viewmemberballot" text="View Member Ballot"/>
			</a> |					
			</c:if>							
		</div>	
			
		<div class="commandbarContent" style="margin-top: 10px;" id="ballots">				
			<c:if test="${!(empty answeringDates)}">
			<a href="#" id="select_group" class="butSim">
				<spring:message code="ballot.group" text="Group"/>
			</a>
			<select name="selectedGroup" id="selectedGroup" style="width:100px;height: 25px;">				
			<c:forEach begin="1" end="${groups}" step="1" var="i">			
			<option value="${i}"><c:out value="${i}"></c:out></option>	
			</c:forEach> 
			</select> |	
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="question.answeringdate" text="Answering Date"/>
			</a>
			<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
			<c:forEach items="${answeringDates}" var="i">			
			<option value="${i}"><c:out value="${i}"></c:out></option>	
			</c:forEach> 
			</select> |</c:if> 		
		</div>	
		
		<div class="tabContent clearfix">
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" name="houseTypeOfAuthUser" id="houseTypeOfAuthUser" value="${houseType}">
		</div> 		
</body>
</html>