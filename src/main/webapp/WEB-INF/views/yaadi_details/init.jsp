<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">				
		$(document).ready(function() {
			if($("#selectedHouseType").val()=='lowerhouse' 
					&& $("#deviceType").val()=='questions_starred'
					&& $("#currentusergroupType").val()=='section_officer') {
				//$("#view_yaadi").hide();
				//$("#view_suchi").hide();
			}
			
			/* show publish button for unpublished suchi on default answering date populated */
			if($("#deviceType").val()=='questions_stared') {
				if($('#selectedAnsweringDate').val()!=undefined && $('#selectedAnsweringDate').val()!=""
						&& $('#selectedAnsweringDate').val()>0) {					
					populatePublishButtonForSelectedAnsweringDate();					
				}
			}
			/* show publish button for unpublished suchi on selected answering date after change */
			$('#selectedAnsweringDate').change(function() {
				if($('#selectedAnsweringDate').val()!=undefined && $('#selectedAnsweringDate').val()!=""
						&& $('#selectedAnsweringDate').val()<0) {					
					populatePublishButtonForSelectedAnsweringDate();					
				}
			});
			/* publish suchi if unpublished on click of publish button */
			$('#publishButton1').click(function(event, isHighSecurityValidationRequired) {
				//isHighSecurityValidationRequired = false;
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				publishSuchiOnSelectedAnsweringDate();
			});
			
			$("#generate_yaadi").click(function(event, isHighSecurityValidationRequired){
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL="";
				var selectedDeviceType = $("#selectedDeviceType").val();
				if($("#selectedDeviceType").val()==undefined || $("#selectedDeviceType").val()==''){
					if($("#category").val()=='question') {
						selectedDeviceType = $("#selectedQuestionType").val();
					} else if($("#category").val()=='motion') {
						selectedDeviceType = $("#selectedMotionType").val();
					} else if($("#category").val()=='resolution') {
						selectedDeviceType = $("#selectedResolutionType").val();
					}
				}
				var parameters = "houseType=" + $("#selectedHouseType").val()
									   + "&sessionYear=" + $("#selectedSessionYear").val()
									   + "&sessionType=" + $("#selectedSessionType").val()
									   + '&deviceType=' + selectedDeviceType
									   + "&ugparam=" + $("#ugparam").val()
									   + "&role=" + $("#srole").val()
								 	   + "&usergroup=" + $("#currentusergroup").val()
									   + "&usergroupType=" + $("#currentusergroupType").val();
			
				resourceURL = 'yaadi_details/generate_yaadi?' + parameters;
				
				$.get(resourceURL,function(data){
					$("#resultDiv").empty();
					$("#resultDiv").html(data);
					
					$.unblockUI();					
				},'html').fail(function(data){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});					
			});
			
			$("#view_yaadi").click(function(event, isHighSecurityValidationRequired) {
				/* var isYaadiOfAnsweringDateRestrictedForBallotPrivacy = false;
				if($('#deviceType').val()=='questions_starred') {
					var restrictedAnsweringDateIds = "2651,2654,2657,2660,2663,2701,2703,2705,2707,2709".split(",");
					for(var i=0; i<restrictedAnsweringDateIds.length; i++) {
						if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy!=true) {
							if($('#selectedAnsweringDate').val()==restrictedAnsweringDateIds[i]) {
								isYaadiOfAnsweringDateRestrictedForBallotPrivacy = true;
							}
						}						
					}					
				}
				if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy==true) {
					$.prompt("Restricted for Ballot Privacy!");
					return false;
				} */
				$("#resultDiv").empty();				
				var resourceURL="";
				var parameters="";
				var yaadiGenerationAllowed= false
				//Shubham
				if($("#deviceType").val()=='questions_starred'){
				
					
					var yaadiGenerationAllowedFlag = "#yaadiGenerationAllowed_"+$('#selectedAnsweringDate').val();
					
					 if ($("#houseType").val() == "upperhouse" || $("#DIRECT_VIEW_YAADI_ACCESS").val() == "YES" || $(yaadiGenerationAllowedFlag).val()  == "YES")
					{
						yaadiGenerationAllowed = true;
					
					} 
			
					if (yaadiGenerationAllowed == true){
						//check output format set or not
					if($("#outputFormat").val() == "") {
						$.prompt($('#outputFormatNotSetPrompt').val());
						return false;
					}
					//isHighSecurityValidationRequired = false;
					if(isHighSecurityValidationRequired!=false) {
						validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
						return false;
					}
					/* parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()
									 +"&category=" + $("#category").val()
									 + "&outputFormat=" + $("#outputFormat").val(); */	
					 parameters = {
					 				 houseType: $("#selectedHouseType").val(),
									 sessionYear: $("#selectedSessionYear").val(),
									 sessionType: $("#selectedSessionType").val(),
									 questionType: $("#selectedQuestionType").val(),
									 group: $("#selectedGroup").val(),
									 status: $("#selectedStatus").val(),
									 role: $("#srole").val(), 
									 answeringDate: $("#selectedAnsweringDate").val(),
									 category: $("#category").val(),
									 outputFormat: $("#outputFormat").val(),
					 			 };
					
					/* resourceURL = 'question/report/viewYaadi?' + parameters;
					$(this).attr('href', resourceURL); */
					form_submit('question/report/viewYaadi', parameters, 'GET');
				}
					else
					{
					 window.alert("Not allowed for yaadi generation")
					}
					
				} else if($("#deviceType").val()=='questions_unstarred') {
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var parameters = "houseType=" + $("#selectedHouseType").val()
										   + "&sessionYear=" + $("#selectedSessionYear").val()
										   + "&sessionType=" + $("#selectedSessionType").val()
										   + "&ugparam=" + $("#ugparam").val()
										   + "&role=" + $("#srole").val()
									 	   + "&usergroup=" + $("#currentusergroup").val()
										   + "&usergroupType=" + $("#currentusergroupType").val();
					
					$.get('yaadi_details/generateUnstarredYaadiReport/getYaadiNumberAndDate?'
							+ parameters, function(data) {
						$.fancybox.open(data,{autoSize:false,width:400,height:270});
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
			});		
			
			$("#view_suchi").click(function(event, isHighSecurityValidationRequired) {
				/* var isYaadiOfAnsweringDateRestrictedForBallotPrivacy = false;
				if($('#deviceType').val()=='questions_starred') {
					var restrictedAnsweringDateIds = "2651,2654,2657,2660,2663,2701,2703,2705,2707,2709".split(",");
					for(var i=0; i<restrictedAnsweringDateIds.length; i++) {
						if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy!=true) {
							if($('#selectedAnsweringDate').val()==restrictedAnsweringDateIds[i]) {
								isYaadiOfAnsweringDateRestrictedForBallotPrivacy = true;
							}
						}						
					}					
				}
				if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy==true) {
					$.prompt("Restricted for Ballot Privacy!");
					return false;
				} */
				$("#resultDiv").empty();
				var resourceURL="";
				var parameters="";
				var SuchiGenerationAllowed= false
				if($("#deviceType").val()=='questions_starred'){
					

					var SuchiGenerationAllowedFlag = "#yaadiGenerationAllowed_"+$('#selectedAnsweringDate').val();
					
					
					if ($("#houseType").val() == "upperhouse"){
						SuchiGenerationAllowed = true;
					}else if ($("#DIRECT_VIEW_YAADI_ACCESS").val() == "YES") {
						SuchiGenerationAllowed = true;
					}else if($(SuchiGenerationAllowedFlag).val() == "YES") {
						SuchiGenerationAllowed = true;
					}
			
				
					
					if (SuchiGenerationAllowed == true){
					//check output format set or not
					if($("#outputFormat").val() == "") {
						$.prompt($('#outputFormatNotSetPrompt').val());
						return false;
					}
					//isHighSecurityValidationRequired = false;
					if(isHighSecurityValidationRequired!=false) {
						validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
						return false;
					}
					/* parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()
									 +"&category=" + $("#category").val()
									 + "&outputFormat=" + $("#outputFormat").val(); */	
					 parameters = {
					 				 houseType: $("#selectedHouseType").val(),
									 sessionYear: $("#selectedSessionYear").val(),
									 sessionType: $("#selectedSessionType").val(),
									 questionType: $("#selectedQuestionType").val(),
									 group: $("#selectedGroup").val(),
									 status: $("#selectedStatus").val(),
									 role: $("#srole").val(), 
									 answeringDate: $("#selectedAnsweringDate").val(),
									 category: $("#category").val(),
									 outputFormat: $("#outputFormat").val()
					 			 };
					
					/* resourceURL = 'question/report/viewSuchi?' + parameters;
					$(this).attr('href', resourceURL); */
					form_submit('question/report/viewSuchi', parameters, 'GET');}else
					{
						 window.alert("Not allowed for SUchi generation")
						}
					
				} else if($("#deviceType").val()=='questions_unstarred') {
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var parameters = "houseType=" + $("#selectedHouseType").val()
										   + "&sessionYear=" + $("#selectedSessionYear").val()
										   + "&sessionType=" + $("#selectedSessionType").val()
										   + "&ugparam=" + $("#ugparam").val()
										   + "&role=" + $("#srole").val()
									 	   + "&usergroup=" + $("#currentusergroup").val()
										   + "&usergroupType=" + $("#currentusergroupType").val();
					
					$.get('yaadi_details/generateUnstarredSuchiReport/getYaadiNumberAndDate?'
							+ parameters, function(data) {
						$.fancybox.open(data,{autoSize:false,width:400,height:270});
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
			});

			$(".update_devices_status").click(function(event, isHighSecurityValidationRequired) {
				//isHighSecurityValidationRequired = false;
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}			
				$("#resultDiv").empty();
				var resourceURL="";
				var parameters="";
				if($("#deviceType").val()=='questions_starred'){
					var selectedAnsweringDateId = $('#selectedAnsweringDate').val();
					var selectedAnsweringDate = $("#answeringDateMaster option[value='"+selectedAnsweringDateId+"']").text();
					//console.log("selectedAnsweringDate: " + selectedAnsweringDate);
					//console.log("currentDate: " + new Date());
					if(new Date() < new Date(selectedAnsweringDate)) {
						alert("Selected yaadi date is yet to come...");
						return false;
					}					
					parameters =  "houseType=" + $("#selectedHouseType").val()
										+ "&sessionYear=" + $("#selectedSessionYear").val()
										+ "&sessionType=" + $("#selectedSessionType").val()
										+ "&questionType=" + $("#selectedQuestionType").val()
										+ "&ugparam=" + $("#ugparam").val() 
										+ "&status=" + $("#selectedStatus").val() 
										+ "&role=" + $("#srole").val()
										+ "&usergroup=" + $("#currentusergroup").val()
										+ "&usergroupType=" + $("#currentusergroupType").val()
										+"&group="+$("#selectedGroup").val()
										+ "&answeringDate=" + $("#selectedAnsweringDate").val();

					resourceURL = 'question/yaaditodiscussupdate/assistant/init?' + parameters;
					
					$.get(resourceURL,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						
						$.unblockUI();					
					},'html').fail(function(data){
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.");
						}
						scrollTop();
					});
				}
			});
			
			
			
			$(".update_devices_content").click(function(event, isHighSecurityValidationRequired) {
				//isHighSecurityValidationRequired = false;
				 if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				} 
				$("#resultDiv").empty();
				var resourceURL="";
				var parameters="";
				if($("#deviceType").val()=='questions_starred'){
					var selectedAnsweringDateId = $('#selectedAnsweringDate').val();
					var selectedAnsweringDate = $("#answeringDateMaster option[value='"+selectedAnsweringDateId+"']").text();
					//console.log("selectedAnsweringDate: " + selectedAnsweringDate);
					//console.log("currentDate: " + new Date());
					if(new Date() < new Date(selectedAnsweringDate)) {
						alert("Selected yaadi date is yet to come...");
						return false;
					}					
					parameters =  "houseType=" + $("#selectedHouseType").val()
										+ "&sessionYear=" + $("#selectedSessionYear").val()
										+ "&sessionType=" + $("#selectedSessionType").val()
										+ "&questionType=" + $("#selectedQuestionType").val()
										+ "&ugparam=" + $("#ugparam").val() 
										+ "&status=" + $("#selectedStatus").val() 
										+ "&role=" + $("#srole").val()
										+ "&usergroup=" + $("#currentusergroup").val()
										+ "&usergroupType=" + $("#currentusergroupType").val()
										+"&group="+$("#selectedGroup").val()
										+ "&answeringDate=" + $("#selectedAnsweringDate").val();

					resourceURL = 'question/yaadiQuestionContentUpdate/assistant/init?' + parameters;
					$.get(resourceURL,function(data){
						$("#resultDiv").empty();
						$("#resultDiv").html(data);
						
						$.unblockUI();					
					},'html').fail(function(data){
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.");
						}
						scrollTop();
					});
				}
			});
			
			$("#bulk_yaadi_update").click(function(event, isHighSecurityValidationRequired) {
				//isHighSecurityValidationRequired = false;
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				$("#resultDiv").empty();
				var selectedDeviceType = $("#selectedDeviceType").val();
				if($("#selectedDeviceType").val()==undefined || $("#selectedDeviceType").val()==''){
					if($("#category").val()=='question') {
						selectedDeviceType = $("#selectedQuestionType").val();
					} else if($("#category").val()=='motion') {
						selectedDeviceType = $("#selectedMotionType").val();
					} else if($("#category").val()=='resolution') {
						selectedDeviceType = $("#selectedResolutionType").val();
					}
				}
				var parameters = "houseType=" + $("#selectedHouseType").val()
				   + "&sessionYear=" + $("#selectedSessionYear").val()
				   + "&sessionType=" + $("#selectedSessionType").val()
				   + "&deviceType=" + selectedDeviceType
				   + "&ugparam=" + $("#ugparam").val()
				   + "&role=" + $("#srole").val()
			 	   + "&usergroup=" + $("#currentusergroup").val()
				   + "&usergroupType=" + $("#currentusergroupType").val();
				
				$.get('yaadi_details/bulk_yaadi_update?'+parameters,function(data){
					$("#resultDiv").empty();
					$("#resultDiv").html(data);					
					$.unblockUI();					
				},'html').fail(function(data){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});				
			});
			
			$("#view_log").click(function(){
				showLog();
			});
		});
		
		//---------------------------------functions---------------
		function clearErrorMsg(){
			$("#error_msg").empty();
			$("#error_p").empty();
		}
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
						$.fancybox.open(data, {autoSize:false,width:840,height:700});
				},'html');
			}else if($("#category").val()=='motion'){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&group="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val() 
				 + "&answeringDate=" + $("#selectedAnsweringDate").val()
				 +"&category=motion";
				resourceURL = 'ballot/viewlog?' + parameters;
				
				$.get(resourceURL,function(data){
						$.fancybox.open(data, {autoSize:false,width:840,height:700});
				},'html');
			}
		}
		
		function populatePublishButtonForSelectedAnsweringDate() {
			clearErrorMsg();
			var parameters = "answeringDate=" + $("#selectedAnsweringDate").val();						
			$.ajax({url: 'ref/check_if_suchi_published_on_selected_answering_date', data: parameters,
				type: 'GET',
				async: false,
				success: function(data) {	
					if(data==false) {
						$('#publishButton1').show();
					} else {
						$('#publishButton1').hide();
					}
				},
				error: function(data) {
					$.prompt("Some error occurred to find if suchi is published or not!");
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
				}
			});			
		}
		
		function publishSuchiOnSelectedAnsweringDate() {	
			clearErrorMsg();
			var parameters = "answeringDate=" + $("#selectedAnsweringDate").val();						
			$.ajax({url: 'ref/check_if_suchi_published_on_selected_answering_date', data: parameters,
				type: 'GET',
				async: false,
				success: function(data) {
					var promptMsg = "";
					var successMsg = "";
					if(data==false) {
						promptMsg = "Do you really want to publish suchi for the selected answering date now?";
						successMsg = "Suchi for the selected answering date published now!"
					} else if(data==true) {
						$.prompt("Suchi for the selected answering date is already published!");
						return false;
					} else {
						return false;
					} 						
					$.prompt(promptMsg,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				       if(v){				    	 	
				       		$.post('yaadi_details/publish_suchi_on_selected_answering_date?'+parameters, function(data) {
								if(data==1) {
									$('#publishButton1').hide();
									$.prompt(successMsg);
								} else {
									alert("Some error occured.. contact support");
									if($("#ErrorMsg").val()!=''){
										$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
									}else{
										$("#error_p").html("Error occured contact for support.");
									}
								}
							});
				       }
					}});
				}
			});			
		}
	</script>
	<style type="text/css">
		.publishButton {
		  /* background-color: #004A7F; */
		  background-color: #fa3e3e;
		  -webkit-border-radius: 10px;
		  border-radius: 10px;
		  border: none;
		  color: #FFFFFF;
		  cursor: pointer;
		  display: inline-block;
		  font-family: Arial;
		  font-size: 12px;
		  padding: 5px 10px;
		  text-align: center;
		  text-decoration: none;
		}
		@-webkit-keyframes glowing {
		  0% { background-color: #B20000; -webkit-box-shadow: 0 0 3px #B20000; }
		  50% { background-color: #FF0000; -webkit-box-shadow: 0 0 5px #FF0000; }
		  100% { background-color: #B20000; -webkit-box-shadow: 0 0 3px #B20000; }
		}
		
		@-moz-keyframes glowing {
		  0% { background-color: #B20000; -moz-box-shadow: 0 0 3px #B20000; }
		  50% { background-color: #FF0000; -moz-box-shadow: 0 0 5px #FF0000; }
		  100% { background-color: #B20000; -moz-box-shadow: 0 0 3px #B20000; }
		}
		
		@-o-keyframes glowing {
		  0% { background-color: #B20000; box-shadow: 0 0 3px #B20000; }
		  50% { background-color: #FF0000; box-shadow: 0 0 5px #FF0000; }
		  100% { background-color: #B20000; box-shadow: 0 0 3px #B20000; }
		}
		
		@keyframes glowing {
		  0% { background-color: #B20000; box-shadow: 0 0 3px #B20000; }
		  50% { background-color: #FF0000; box-shadow: 0 0 5px #FF0000; }
		  100% { background-color: #B20000; box-shadow: 0 0 3px #B20000; }
		}
		
		.publishButton {
		  -webkit-animation: glowing 1500ms infinite;
		  -moz-animation: glowing 1500ms infinite;
		  -o-animation: glowing 1500ms infinite;
		  animation: glowing 1500ms infinite;
		}
	</style>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;" id="error">${error}</h4>
	</c:if>
	<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">
		<c:if test="${deviceTypeType=='questions_starred'}">
		<a href="#" id="select_answeringdate" class="butSim">
			<spring:message code="yaadidetails.answeringdate" text="Answering Date"/>
		</a>
		<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
			<c:forEach items="${answeringDates}" var="i">			
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
		</select> |
		<c:forEach items="${answeringDates}" var="i">			
			<c:set var="yaadiGenerationAllowed" value="yaadiGenerationAllowed_${i.id}" />
			<input type="hidden" id="yaadiGenerationAllowed_${i.id}" value="${requestScope[yaadiGenerationAllowed]}"/>
		</c:forEach>
		<a href="#" id="select_outputformat" class="butSim">
			<spring:message code="yaadidetails.outputformat" text="Output Format"/>
		</a>
		<select id="outputFormat" name="outputFormat">
			<option value="" selected="selected"><spring:message code="please.select" text="Please Select"/></option>
			<c:forEach items="${outputFormats}" var="i">
				<option value="${i.value}">${i.name}</option>
			</c:forEach>
		</select>
		<hr/>
		<select id="answeringDateMaster" style="display:none;">
			<c:forEach items="${answeringDates}" var="i">
				<option value="${i.id}"><c:out value="${i.value}"></c:out></option>
			</c:forEach>
		</select>
		</c:if>
		<%-- <security:authorize access="hasAnyRole('QIS_ADMIN', 'QIS_CLERK','QIS_ASSISTANT','QIS_SECTION_OFFICER','SMOIS_CLERK','SMOIS_ASSISTANT')"> --%>
		<c:if test="${deviceTypeType=='questions_unstarred'}">
		<a href="#" id="generate_yaadi" class="butSim">
			<spring:message code="yaadidetails.generateYaadi" text="Generate Yaadi"/>
		</a> |
		</c:if>
		<%-- </security:authorize> --%>
		
		<c:choose>
			<c:when test="${deviceTypeType=='questions_starred'}">
				<security:authorize access="!hasAnyRole( 'QIS_GENERAL_CLERK')">
				<a href="#" id="view_yaadi" class="butSim">
					<spring:message code="yaadidetails.viewYaadi" text="Yaadi Report"/>
				</a> |		 
				<a href="#" id="view_suchi" class="butSim">
					<spring:message code="yaadidetails.viewSuchi" text="Suchi Report"/>
				</a>
				</security:authorize>
			</c:when>
			<c:otherwise>
				<a href="#" id="view_yaadi" class="butSim">
					<spring:message code="yaadidetails.viewYaadi" text="Yaadi Report"/>
				</a> |		 
				<a href="#" id="view_suchi" class="butSim">
					<spring:message code="yaadidetails.viewSuchi" text="Suchi Report"/>
				</a>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${deviceTypeType=='questions_stared'}">
		<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
			<button type="button" id="publishButton1" class="publishButton" style="display: none;">Publish!</button>
		</security:authorize>
		</c:if>
		|
		<c:if test="${deviceTypeType=='questions_unstarred'}">
		<a href="#" id="bulk_yaadi_update" class="butSim">
			<spring:message code="yaadidetails.bulkYaadiUpdate" text="Bulk Yaadi Update"/>				
		</a> |
		</c:if>
		<c:if test="${deviceTypeType=='questions_starred'}">
		<security:authorize access="hasAnyRole('QIS_ASSISTANT','QIS_SECTION_OFFICER')">
		<a href="#" id="update_questions_status" class="butSim update_devices_status">
			<spring:message code="yaadidetails.update_questions_status" text="Update Status of Yaadi Questions"/>				
		</a> |
	 	<%-- <a href="#" id="update_questions" class="butSim update_devices_content">
			<spring:message code="yaadidetails.update_questions" text="Update  Yaadi Questions"/>				
		</a> |  --%>
		</security:authorize>
		</c:if>
		<hr/>
		<br/>
		<h3 id="error_msg" style="color: red"></h3>
	</div>
	<div id="resultDiv">
	</div>
	<input id="category" type="hidden" value="${category}" />
	<input id="DIRECT_VIEW_YAADI_ACCESS" type="hidden" value="${DIRECT_VIEW_YAADI_ACCESS}" />
	<input id="ds" type="hidden" value="${ds}" />
	<input id="houseType" type="hidden" value="${houseType}" />
	


	<input id="deviceType" type="hidden" value="${deviceTypeType}" />
	<%-- <input id="houseType" type="hidden" value="${houseType}" /> --%>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="confirmationMsg" value="<spring:message code='generic.confirmationMessage' text='This is an irreversible change.Are you sure you want to continue?'/>"/>
	
</body>
</html>