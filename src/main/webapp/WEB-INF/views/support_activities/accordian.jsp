<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

		 
		<script type="text/javascript">
			$(document).ready(function() {	
				$('#pageCursorDiv').hide()
				if( $('#count').val()  < 10){
					
					lastPage = true;
				}else{
					lastPage = false
				}
				
				if($('#pageCursor').val() == 'true'){
					$('#pageCursorDiv').show()
				}
				
				$( "#accordion" ).accordion({
					collapsible: true,
					icons: false
				});
				
				
				
			});
		
			function viewQuestionDetail(id){
				
				var deviceTypesId = {
						questions_starred:4,
						questions_unstarred:5,
						questions_shortnotice:7,
						questions_halfhourdiscussion_from_question:49,
						
				}
				
				
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var deviceType = $("#deviceTypeForSearch").val();
				//console.log(deviceType);
				deviceTypeId = deviceTypesId[deviceType];
				//console.log(deviceTypeId);
				/* var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&questionType="+deviceId
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val()
				+"&edit=false";
				var resourceURL='question/'+id+'/edit?'+parameters; */
				var parameters="questionType="+deviceTypeId+"&qid="+id
				var resourceURL='question/viewquestion?'+parameters;
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:750,height:700});
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
			
		function posterActivity(id,deviceType,status){
				//console.log(id);
				//console.log(deviceType);
				
				//console.log('posterUtility/posterActivityForSupport/id/'+id+'/deviceType/'+deviceType+'')
				
				var houseType = $('#houseTypeForSearch').val();
				
				  $.get('posterUtility/posterActivityForSupport/id/'+id+'/deviceType/'+deviceType+'/status/'+status+'/houseType/'+houseType,function(data){
					    $.fancybox.open(data,{autoSize: false, width: 400, height:300});		    
				   }).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						
					});  
		}
			
			
		function workflowDetails(id){
			//console.log(id);
			
			 $.get('ref/getWorkFlowDetails/'+id,function(data){
				    $.fancybox.open(data,{autoSize: false, width: 900, height:700});		    
			   }).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					
				}); 
			
		}	
		
		
			
		function childRevision(id,device){
				
				var deviceTypes = {
						Question:'question',
						AdjournmentMotion:'adjournmentmotion',
						CutMotion:'cutmotion',
						DiscussionMotion:'discussionmotion',
						Motion:'motion',
						ProprietyPoint:'proprietypoint',
						Resolution:'resolution',
						RulesSuspensionMotion:'rulessuspensionmotion',
						SpecialMentionNotice:'specialmentionnotice',
						StandaloneMotion:'standalonemotion'
				}
				
				//console.log(id)
				//console.log(device)
				var deviceType = '';
				deviceType = deviceTypes[device];
				//console.log(deviceType)
				
				
				if(deviceType != ''){
					 $.get(deviceType+'/revisions/'+id,function(data){
						    $.fancybox.open(data,{autoSize: false, width: 800, height:700});		    
					   }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							
						}); 
				}
			  
				 
			   return false; 
			}
			
		</script>		 
	</head>	
	<body>	
		<c:choose>
				
	<c:when test="${DevicesDetails != null and not (empty DevicesDetails)}">
	
	
	<c:if test="${responseDeviceType eq 'Question'}">
		<br>
			<div style=" width:200px; border-style: groove;">
			  	 parent :-<img src='./resources/images/P.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'> 
			  	  |
			  	child :- <img src='./resources/images/clubbed.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
			</div>
		<br>
	</c:if>
	<div id="accordion">
	
	<c:forEach items="${DevicesDetails}" var="r" varStatus="counter">
		<h2 >
			<p style="padding-left: 10px;	padding-top: 10px; padding-bottom: 10px; margin:0px;">
				<spring:message code="generic.serialnumber" text="Device number"/> :-${r.number}  
				|
				<spring:message code="electiontype.houseType" text="House"/> :-${r.houseType} ${r.sessionType } ${r.sessionYear }
				|
				<spring:message code="mis.report.cred.name" text="Member Name"/> :-${r.primaryMember} 
				|
				<spring:message code="proprietypoint.deviceType" text="DeviceType"/> :-${r.deviceType} 	
				|
				<c:if test="${not empty r.parent }">
				
				<img src='./resources/images/clubbed.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
				
			
				</c:if>
				
				<c:if test="${not empty r.child }">
				
				<img src='./resources/images/P.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
						
				</c:if>
				
				
			</p>	
		</h2>
		  <div>
		    <table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th style=" width: 10px;"><spring:message code="memberwise.subject" text="Subject"/></th>
								<th style="text-align: center; font-size: 12px; width: 90px;">
								<spring:message code="question.QuestionText" text="Content"/>
								 /
								 <spring:message code="qis.memberwisequestions.answer" text="Content"/>
								 </th>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="memberwise.status" text="Status"/></th>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="generic.details" text="Status"/></th>
								<th style="text-align: center; font-size: 12px; width: 30px;"> Action </th>
							</tr>
							<tbody>
							<tr class="page-break-after-forced">
								<td style="width: 10px;">${r.subject}</td>
								<td style="text-align: center; font-size: 12px; width: 90px;">
								${r.revisedContent}
								
								
								<c:if test="${r.answer != null}">
								  <div style=" border-style: groove;">
									${r.answer }
								  </div>
								</c:if>
								
								
								</td>
								<td style="text-align: center; font-size: 12px; width: 20px;">
									<div style=" border-style: groove;">
										<spring:message code="generic.InternalStatus" text="Status"/> :-
										<b>${r.internalStatus }</b>
									</div >
								<br>
									<div style=" border-style: groove;">
										<spring:message code="generic.RecommendStatus" text="Status"/> :-
										<b>${r.recommendationStatus }</b>
									</div >
								</td>
								<td style="text-align: center; font-size: 12px; width: 20px;">
									<div style=" border-style: groove;">
										<spring:message code="generic.department" text="Department"/> :-
										<b>${r.subDepartment }</b>
									</div >
								<br>
									<div style=" border-style: groove;">
										<spring:message code="question.actor" text="actor"/> :-
										<br>
										 <%-- <b>${r.actor }</b>
										 <br> --%>
										  <c:set var="inputString" value="${r.actor }" />
										  <c:set var="parts" value="${fn:split( inputString , '#')}" />
										 	
										  <spring:message code="user_lbl_username" text="actor"/> :-<b><c:out value="${parts[0]}" /></b> <br />
										  UserGroup  :<b><c:out value="${parts[1]}" /></b><br />
										  level : <b><c:out value="${parts[2]}" /></b><br />	
										  Full Name :<b><c:out value=" ${parts[4]}" /></b><br />	
									</div >
								<br>
									<c:if test="${r.device eq 'Question' }">
										<div style=" border-style: groove;">
										<spring:message code="memberwise.group" text="Group"/> :-
										<b>${r.group }</b>
										</div >
										
										<br>
										<c:if test="${ not empty r.parent}">
										
										 <div style=" border-style: groove;">
											Parent :
											<c:forEach items="${r.parent}" var="entry">									   
									           <a href="#" id="p${entry.key}" onclick="viewQuestionDetail(${entry.key});"><c:out value="${entry.value}"></c:out></a>
									        </c:forEach>
											
										 </div >
										</c:if>
										
										<c:if test="${ not empty r.child}">
										
										
										 <div style=" border-style: groove;">
											Childs :-
											<br>
											<c:forEach items="${r.child}" var="map">
								                <c:forEach items="${map}" var="entry">								                
								                 <a href="#" id="p${entry.key}" onclick="viewQuestionDetail(${entry.key});"><c:out value="${entry.value}"></c:out></a>,							                	
								                </c:forEach>
								                <br>
							           		 </c:forEach>
										 </div >
										</c:if> 
										
										
										<%-- <div style=" border-style: groove;">
										<spring:message code="memberwise.group" text="Chart Answering Date"/> :-
										<b>${r.chartAnsweringDate }</b>
										</div > --%>
									</c:if>
								</td>
									
								<td>
									<div style="margin-top: 10px; text-align: center;">
										<a href='#' style='font-weight:bold; align-items: center;'   onclick='childRevision(${r.id},"${r.device }")'> 
		 									<img src='./resources/images/referenced.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
		 									<br>
		 									View revision
		 								</a> 
									</div>
									<br>
									<div style="margin-top: 10px; text-align: center;">
										<a href='#' style='font-weight:bold; align-items: center;'   onclick='workflowDetails(${r.id})'> 
		 									<img src='./resources/images/IcoPageHeading.jpg' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
		 									<br>
		 									View WorkFlow Detail
		 								</a> 
									</div>
									<br>
									<div style="margin-top: 10px; text-align: center;">
										<a href='#' style='font-weight:bold; align-items: center;'   onclick='posterActivity(${r.id},"${r.ministry}","${r.internalStatusType}")'> 
		 									<img src='./resources/images/Revise.jpg' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
		 									<br>
		 									 Poster 
		 								</a> 
									</div>
									
								</td>
							</tr>
							</tbody>
							
						</thead>
			</table>
		  </div>
	</c:forEach>
	  
	</div>
	<div id="pageCursorDiv" class="tright">
		<a href='#' style='font-weight:bold; align-items: center; '   onclick='changePage("prev")'> 		  	
			<img src='./resources/images/IcoBack.jpg' style='display:inline-block ' title='Revision' width='30px' height='30px' align='justify'>
		 </a> 
			<span id="pageNumber"  style =" border-style: groove;" ></span>
		<a href='#' style='font-weight:bold; align-items: center;'   onclick='changePage("next")'> 
				<img src='./resources/images/IcoNext.jpg' style='display:inline-block' title='Revision' width='30px' height='30px' align='justify'>
		 </a> 
	 </div>
	</c:when>
	<c:otherwise>
				<spring:message code="member_questions_view.nodatafound" text="No Data Found"/>
			</c:otherwise>
		</c:choose>
	<!-- <div id="accordion">

	one Accordian  
	  <h2>Section 1</h2>
	  <div>
	    <p>Mauris mauris ante, blandit et, ultrices a, suscipit eget.
	    Integer ut neque. Vivamus nisi metus, molestie vel, gravida in,
	    condimentum sit amet, nunc. Nam a nibh. Donec suscipit eros.
	    Nam mi. Proin viverra leo ut odio.</p>
	  </div>
	 
	 
	</div> -->
	<input id="pageCursor" name="pageCursor" value="${pageCursor}" type="hidden">
	<input id="count" name="count" value="${count}" type="hidden">
	<input id="housTypeId" name="housTypeId" value="${houseTypeId}" type="hidden">
	</body>
</html>