<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		function clearErrorMsg(){
			$("#error_msg").empty();
		}		
		$(document).ready(function() {
			$("#pre_ballot").click(function(){
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL;
				if($("#category").val()=='question'){
					var parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=question";
					resourceURL = 'ballot/preballot?' + parameters;
				}else if($("#category").val()=='resolution'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=question";
					
					resourceURL = 'ballot/preballot?' + parameters;
				}else if($("#category").val()=='bill'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=bill";
					
					resourceURL = 'ballot/preballot?' + parameters;
				}
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
					
			});	
				
			$("#create_ballot").click(function() {
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL;
				if($("#category").val()=='question'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#ugparam").val()
					 //+"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=question";
					resourceURL = 'ballot/create?' + parameters;
				}else if($("#category").val()=='resolution'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=resolution";
					
					resourceURL = 'ballot/create?' + parameters;
				}else if($("#category").val()=='bill'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=bill";
					
					resourceURL = 'ballot/create?' + parameters;
				}
				$.get(resourceURL, function(data) {
					var displayMessage = data;
					if(data == "CREATED" || data == "ALREADY_EXISTS") {
						var newResourceURL = 'ballot/view?' + parameters;
						$.get(newResourceURL,function(data){
							//if($("#selectedDeviceType").val().match("/^questions/")){
								$("#ballotResultDiv").empty();
								$("#ballotResultDiv").html(data);
							//}else{
								//$("#ballotResultDiv").empty();
								//$("#ballotResultDiv").append(data);
							//}
							if(data){
								$("#submitDiv").show();//.attr('display','block');
							}
							$.unblockUI();					
						},'html');
					}
					else {
						displayMessage = "Error Occurred while creating Ballot";
						$.unblockUI();
						$.fancybox.open(displayMessage);
					}
				}).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
			});		
			$("#view_ballot").click(function(){
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL;
				var parameters;
				if($("#category").val()=='question'){
					parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#ugparam").val()
									 //+"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()
									 +"&category=question";
					resourceURL = 'ballot/view?' + parameters;
				}else if($("#category").val()=='resolution'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=question";
					
					resourceURL = 'ballot/view?' + parameters;
				}else if($("#category").val()=='bill'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=bill";
					
					resourceURL = 'ballot/view?' + parameters;
				}
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					if(data){
						$.unblockUI();
					}
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
					
			});			
			$("#update_yaadi").click(function(){
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedQuestionType").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&role="+$("#srole").val() 
				 +"&answeringDate=" + $("#selectedAnsweringDate").val();
				
				var resourceURL = 'ballot/yaadi/updatebyyaadi?' + parameters;
				
				$.get(resourceURL, function(data){
					if(data){
						$("#ballotResultDiv").html(data);
					}
				});
			});
			
			$("#view_yaadi").click(function(){
				var resourceURL="";
				var parameters="";
				if($("#category").val()=='question'){
					parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()									 
									 + "&outputFormat=" + $("#outputFormat").val();
					resourceURL = 'question/report/viewYaadi?' + parameters;
				}else if($("#category").val()=='resolution'){
					parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&deviceType="+$("#selectedDeviceType").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 +"&answeringDate=" + $("#selectedAnsweringDate").val()								 
								 + "&outputFormat=" + $("#outputFormat").val();					
					resourceURL = 'resolution/viewYaadi?' + parameters;
				}else if($("#category").val()=='bill'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()								 
					 + "&outputFormat=" + $("#outputFormat").val();					
					resourceURL = 'bill/viewYaadi?' + parameters;
				}
				$(this).attr('href', resourceURL);			
				//check output format set or not
				if($("#outputFormat").val() == "") {
					$.prompt($('#outputFormatNotSetPrompt').val());
					return false;
				}
			});			
			$("#view_suchi").click(function(){
				var resourceURL="";
				var parameters="";
				if($("#category").val()=='question'){
					parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()
									 +"&category=" + $("#category").val()
									 + "&outputFormat=" + $("#outputFormat").val();	
					resourceURL = 'question/report/viewSuchi?' + parameters;
				}else if($("#category").val()=='resolution'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=" + $("#category").val()
					 + "&outputFormat=" + $("#outputFormat").val();	
					
					resourceURL = 'resolution/viewSuchi?' + parameters;
				}else if($("#category").val()=='bill'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=" + $("#category").val()
					 + "&outputFormat=" + $("#outputFormat").val();	
					
					resourceURL = 'bill/viewSuchi?' + parameters;
				}
				$(this).attr('href', resourceURL);
				//check output format set or not
				if($("#outputFormat").val() == "") {
					$.prompt($('#outputFormatNotSetPrompt').val());
					return false;
				}
			});
			$("#give_balloted_resolution_choice").click(function(){
				if($("#category").val()=='resolution'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=resolution";
					
					resourceURL = 'ballot/fillresolutionchoices?' + parameters;
				}
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
			});
			
			$("#patrakbhag2_tab").click(function(){
				var parameters="";
				if($("#category").val()=='question'){
					parameters = "houseType="+$("#selectedHouseType").val()
						 +"&sessionYear="+$("#selectedSessionYear").val()
						 +"&sessionType="+$("#selectedSessionType").val()
						 +"&deviceType="+$("#selectedQuestionType").val()
						 +"&status="+$("#selectedStatus").val()
						 +"&role="+$("#srole").val() 
						 +"&answeringDate=" + $("#selectedAnsweringDate").val();
				}else if($("#category").val()=='resolution'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val();
				}else if($("#category").val()=='bill'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val();
				}
					
				resourceURL = 'ballot/showpatrakbhagdon?' + parameters;
					
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					$.unblockUI();					
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
			});
			
		    /**** Put Up ****/
			$("#startworkflow").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});			
				$.prompt($('#startWorkflowMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?operation=startworkflow',  
			    	            $("form").serialize(),  
			    	            function(data){
			       					$('.tabContent').html(data);
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	   				 	   				
			    	            }).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.");
			    					}
			    					scrollTop();
			    				});
	    	         }
				}});			
		        return false;  
		    });
			
			
			//======================================================
				
			$("#view_log").click(function(){
				showLog();
			});
		});	
		
		//---------------------------------functions---------------
		function showLog(){
			var parameters;
			if($("#category").val()=='question'){
				parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#ugparam").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val()
								 +"&category=question";
				resourceURL = 'ballot/viewlog?' + parameters;
				
				$.get(resourceURL,function(data){
						$.fancybox.open(data);
				},'html');
			}
		}
		
	</script>
	<style type="text/css">
		.o{
			vertical-align: middle;
		}
	</style>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;" id="error">${error}</h4>
</c:if>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">

		<c:choose>
			<c:when test="${deviceTypeType=='resolutions_nonofficial'
								or deviceTypeType=='bills_nonofficial' 
								or deviceTypeType == 'questions_halfhourdiscussion_from_question' 
								or deviceTypeType == 'questions_halfhourdiscussion_standalone' }">
				<a href="#" id="select_discussiondatedate" class="butSim">
					<spring:message code="ballotinitial.discussiondate" text="Discussion Date"/>
				</a>
			</c:when>
			<c:otherwise>
				<a href="#" id="select_answeringdate" class="butSim">
					<spring:message code="ballotinitial.answeringdate" text="Answering Date"/>
				</a>
			</c:otherwise>
		</c:choose>
			<c:choose>
				<c:when test="${deviceTypeType == 'questions_starred'}">
					<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
					<c:forEach items="${answeringDates}" var="i">			
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach> 
					</select>
				</c:when>
				<c:when test="${deviceTypeType == 'questions_halfhourdiscussion_from_question' 
								or deviceTypeType == 'questions_halfhourdiscussion_standalone' 
								or deviceTypeType=='resolutions_nonofficial'
								or deviceTypeType=='bills_nonofficial'}">
					<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
					<c:forEach items="${answeringDates}" var="i">			
						<option value="${i.value}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach> 
					</select>
				</c:when>
			</c:choose>
			| 
			<a href="#" id="pre_ballot" class="butSim">
				<spring:message code="ballotinitial.preballot" text="Pre Ballot"/>
			</a>
			|
			<a href="#" id="create_ballot" class="butSim">
				<spring:message code="ballotinitial.createballot" text="Create Ballot"/>
			</a> |
			<a href="#" id="view_ballot" class="butSim">
				<spring:message code="ballotinitial.viewballot" text="View Ballot"/>
			</a>
			<a href="#" id="view_log" class="butSim">
				<spring:message code="ballotinitial.viewlog" text="View Log"/>
			</a>
			<c:if test="${deviceTypeType != 'bills_nonofficial' and not(fn:contains(deviceTypeType, 'resolutions_')) and not(fn:contains(deviceTypeType, 'halfhour'))}"> |
				 <a href="#" id="update_yaadi" class="butSim">
					<spring:message code="ballotinitial.updateyaadi" text="Update Questions in Yaadi"/>
				</a> |
				<a href="#" id="view_yaadi" class="butSim">
					<spring:message code="ballotinitial.viewyaadi" text="View Yaadi"/>
				</a> | 
				<a href="#" id="view_suchi" class="butSim">
					<spring:message code="ballotinitial.viewsuchi" text="View Suchi"/>
				</a>				
				<c:if test="${not empty outputFormats}">				
					<select id="outputFormat" name="outputFormat">
						<option value="" selected="selected">Please Select Output Format</option>
						<c:forEach items="${outputFormats}" var="i">
							<option value="${i.value}">${i.name}</option>
						</c:forEach>
					</select>				
				</c:if>
				<hr/>			
			</c:if>
			<c:if test="${deviceTypeType =='resolutions_nonofficial'}">|
				<%-- <c:if test="${houseType=='upperhouse'}"> --%>
				<a href="#" id="give_balloted_resolution_choice" class="butSim">
					<spring:message code="ballotinitial.ballotchoice" text="Give Choice"/>
				</a>
				<%-- </c:if> --%>
				<%-- <c:if test="${houseType=='lowerhouse'}">
					<a id="patrakbhag2_tab" href="#" class="tab">
				   		<spring:message code="resolution.patrakbhag2" text="Post Ballot Report"></spring:message>
					</a>		
				</c:if> --%>
			</c:if>
			
			<br />
			<h3 id="error_msg" style="color: red"></h3>
</div>
<div id="ballotResultDiv">
</div>
<input type="hidden" id="ballotViewFailureMsg" value="<spring:message code='ballot.view.failure' text='Can not view ballot.'></spring:message>">
<input id="category" type="hidden" value="${category}" />
<input id="deviceType" type="hidden" value="${deviceTypeType}" />
<input id="houseType" type="hidden" value="${houseType}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>