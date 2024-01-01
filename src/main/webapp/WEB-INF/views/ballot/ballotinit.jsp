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
			if($("#category").val()=='question') {			
				if($("#selectedHouseType").val()=='lowerhouse') {
					if($("#srole").val()=='QIS_SECTION_OFFICER' 
							|| $("#srole").val()=='QIS_UNDER_SECRETARY'
							|| $("#srole").val()=='QIS_UNDER_SECRETARY_COMMITTEE'
							|| $("#srole").val()=='QIS_JOINT_SECRETARY') {
						$('#yaadiDiv').show();
					} else {
						$('#yaadiDiv').hide();
					}
				} else if($("#selectedHouseType").val()=='upperhouse') {
					if($("#srole").val()=='QIS_SECTION_OFFICER' || $("#srole").val()=='QIS_PRINCIPAL_SECRETARY') {
						$('#yaadiDiv').show();
					} else {
						$('#yaadiDiv').hide();
					}
				}
			} else {
				$('#yaadiDiv').show();
			}		
			
			$("#pre_ballot").click(function(event, isHighSecurityValidationRequired){	
				if($('#highSecurityPasswordEnabled').val()=='no') {
					isHighSecurityValidationRequired = false;
				} else {
					if(highSecurityPasswordEntered) {
						isHighSecurityValidationRequired = false;
					}
				}
				if(isHighSecurityValidationRequired!=false) {					
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");					
					return false;
				}
				//highSecurityPasswordEntered = true;
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL="";
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
				}else if($("#category").val()=='motion'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=motion";
					resourceURL = 'ballot/preballot?' + parameters;
				}else if($("#category").val()=='proprietypoint'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=proprietypoint";
					resourceURL = 'ballot/preballot?' + parameters;
				}
				$.get(resourceURL,function(data){
					/* var dt = $("#selectedQuestionType").val();
					var deviceType = $("#deviceTypeMaster option[value='"+ dt + "']").text().trim();
					
					if((deviceType=='questions_halfhourdiscussion_from_question' && $("#selectedHouseType").val()=='upperhouse')
							|| (deviceType=='motions_standalonemotion_halfhourdiscussion' && $("#selectedHouseType").val()=='upperhouse')){
						
						var parameters = "houseType="+$("#selectedHouseType").val()
						 +"&sessionYear="+$("#selectedSessionYear").val()
						 +"&sessionType="+$("#selectedSessionType").val()
						 +"&questionType="+$("#selectedQuestionType").val()
						 +"&answeringDate=" + $("#selectedAnsweringDate").val();
						
						$.get('question/report/generalreport?' + parameters+"&locale="+$("#moduleLocale").val()
								+"&report=HDS_COUNCIL_PREBALLOT_REPORT_GEN&reportout=hdscouncilpreballotreportgen",function(datan){
							$("#ballotResultDiv").empty();
							$("#ballotResultDiv").html(datan);
						});
					}else{
						$("#ballotResultDiv").empty();
						$("#ballotResultDiv").html(data);
					} */
					
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					
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
				
			$('#view_preballot').click(function(event, isHighSecurityValidationRequired){	
				if($('#highSecurityPasswordEnabled').val()=='no') {
					isHighSecurityValidationRequired = false;
				} else {
					if(highSecurityPasswordEntered) {
						isHighSecurityValidationRequired = false;
					}
				}
				if(isHighSecurityValidationRequired!=false) {					
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");					
					return false;
				}
				//highSecurityPasswordEntered = true;
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
					resourceURL = 'ballot/viewpreballot?' + parameters;
				}else if($("#category").val()=='resolution'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=question";
					
					resourceURL = 'ballot/viewpreballot?' + parameters;
				}else if($("#category").val()=='bill'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 +"&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=bill";
					
					resourceURL = 'ballot/viewpreballot?' + parameters;
				}else if($("#category").val()=='motion'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=motion";
					resourceURL = 'ballot/viewpreballot?' + parameters;
				}else if($("#category").val()=='proprietypoint'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=proprietypoint";
					resourceURL = 'ballot/viewpreballot?' + parameters;
				}
				$.get(resourceURL,function(data){
					
					/* var dt = $("#selectedQuestionType").val();
					var deviceType = $("#deviceTypeMaster option[value='"+ dt + "']").text().trim();
					
					if((deviceType=='questions_halfhourdiscussion_from_question' && $("#selectedHouseType").val()=='upperhouse')
							|| (deviceType=='motions_standalonemotion_halfhourdiscussion' && $("#selectedHouseType").val()=='upperhouse')){
						
						var parameters = "houseType="+$("#selectedHouseType").val()
						 +"&sessionYear="+$("#selectedSessionYear").val()
						 +"&sessionType="+$("#selectedSessionType").val()
						 +"&questionType="+$("#selectedQuestionType").val()
						 +"&answeringDate=" + $("#selectedAnsweringDate").val();
						var url="";
						if(deviceType=='questions_halfhourdiscussion_from_question'){
							url+="&locale="+$("#moduleLocale").val()
							+"&report=HDQ_COUNCIL_PREBALLOT_REPORT_GEN&reportout=hdqcouncilpreballotreportgen";
						}else{
							url+="&locale="+$("#moduleLocale").val()
							+"&report=HDS_COUNCIL_PREBALLOT_REPORT_GEN&reportout=hdscouncilpreballotreportgen";
						}
						
						$.get('question/report/generalreport?' + parameters+url,function(datan){
							$("#ballotResultDiv").empty();
							$("#ballotResultDiv").html(datan);
						});
					}else{
						$("#ballotResultDiv").empty();
						$("#ballotResultDiv").html(data);
					} */
					
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					
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
			
			$("#create_ballot").click(function(event, isHighSecurityValidationRequired){	
				if($('#highSecurityPasswordEnabled').val()=='no') {
					isHighSecurityValidationRequired = false;
				} else {
					if(highSecurityPasswordEntered) {
						isHighSecurityValidationRequired = false;
					}
				}
				if(isHighSecurityValidationRequired!=false) {
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");
					return false;
				}
				//highSecurityPasswordEntered = true;
				var resourceURL='';
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
				}else if($("#category").val()=='motion'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=motion";
					resourceURL = 'ballot/create?' + parameters;
				}else if($("#category").val()=='proprietypoint'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=proprietypoint";
					resourceURL = 'ballot/create?' + parameters;
				}
				
				$.prompt($('#ballotConfirmationMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
				   		if(v){
				   			clearErrorMsg();
							$.blockUI({ message: '<img src="./resources/images/Ballot06.gif" />' });
							$.get(resourceURL, function(data) {
								var displayMessage = data;
								if(data == "CREATED" || data == "ALREADY_EXISTS") {
									var newResourceURL = 'ballot/view?' + parameters;
									$.get(newResourceURL,function(data){
										setTimeout(function(){
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
										},30000);															
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
				   		}     						
					}
				});	
				return false;
			});
			
			$("#view_ballot").click(function(event, isHighSecurityValidationRequired){	
				if($('#highSecurityPasswordEnabled').val()=='no') {
					isHighSecurityValidationRequired = false;
				} else {
					if(highSecurityPasswordEntered) {
						isHighSecurityValidationRequired = false;
					}
				}
				if(isHighSecurityValidationRequired!=false) {					
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");					
					return false;
				}
				clearErrorMsg();
				//highSecurityPasswordEntered = true;
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
				}else if($("#category").val()=='motion'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#ugparam").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=motion";
					resourceURL = 'ballot/view?' + parameters;
				}else if($("#category").val()=='proprietypoint'){
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=proprietypoint";
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
			
			$('#view_unballoted').click(function(event, isHighSecurityValidationRequired){	
				if($('#highSecurityPasswordEnabled').val()=='no') {
					isHighSecurityValidationRequired = false;
				} else {
					if(highSecurityPasswordEntered) {
						isHighSecurityValidationRequired = false;
					}
				}
				if(isHighSecurityValidationRequired!=false) {					
					validateHighSecurityPassword(isHighSecurityValidationRequired, $(this).attr('id'), "click");					
					return false;
				}
				//highSecurityPasswordEntered = true;
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL = "";
				if($("#category").val()=='proprietypoint'){
					var parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&deviceType="+$("#selectedDeviceType").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=proprietypoint";
					resourceURL = 'ballot/viewunballoted?' + parameters;
				}
				if(resourceURL!="") {
					$.get(resourceURL,function(data){					
						$("#ballotResultDiv").empty();
						$("#ballotResultDiv").html(data);
						
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
				else {
					$.prompt("Not allowed for selected device type!");
					return false;
				}
				
			});
			
			$("#update_yaadi").click(function(){
				$.prompt("Please call administrator to open this link on the day before generating yaadi! ");
				return false;
				/*var selectedAnsweringDateId = $('#selectedAnsweringDate').val();
				var selectedAnsweringDate = $("#answeringDateMaster option[value='"+selectedAnsweringDateId+"']").text();
				//console.log("selectedAnsweringDate: " + selectedAnsweringDate);
				//console.log("currentDate: " + new Date());
				if(new Date() < new Date(selectedAnsweringDate)) {
					alert("Selected yaadi date is yet to come...");
					return false;
				}
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
				});*/
			});
			
			$("#view_yaadi").click(function(){
				/* var isYaadiOfAnsweringDateRestrictedForBallotPrivacy = false;
				if($('#deviceType').val()=='questions_starred') {
					var restrictedAnsweringDateIds = "2620,2623,2626,2631,2634,2602,2606,2609,2613,2616".split(",");
					for(var i=0; i<restrictedAnsweringDateIds.length; i++) {
						if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy!=true) {
							if($('#selectedAnsweringDate').val()==restrictedAnsweringDateIds[i]) {
								isYaadiOfAnsweringDateRestrictedForBallotPrivacy = true;
							}
						}						
					}					
				}
				if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy==true) {
					$.prompt("Restricted for Today's Ballot Privacy!");
					return false;
				} */
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
				/* var isYaadiOfAnsweringDateRestrictedForBallotPrivacy = false;
				if($('#deviceType').val()=='questions_starred') {
					var restrictedAnsweringDateIds = "2620,2623,2626,2631,2634,2602,2606,2609,2613,2616".split(",");
					for(var i=0; i<restrictedAnsweringDateIds.length; i++) {
						if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy!=true) {
							if($('#selectedAnsweringDate').val()==restrictedAnsweringDateIds[i]) {
								isYaadiOfAnsweringDateRestrictedForBallotPrivacy = true;
							}
						}						
					}					
				}
				if(isYaadiOfAnsweringDateRestrictedForBallotPrivacy==true) {
					$.prompt("Restricted for Today's Ballot Privacy!");
					return false;
				} */
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
			
			
			/***Preview Preballot**/
			$('#preview_preballot').click(function(){
				clearErrorMsg();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL='';
				/* if($("#category").val()=='question'){ */
					var parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&questionType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val()+"&category=question";
					resourceURL = 'ballot/previewpreballot?' + parameters;
				/* } */
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
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
			
			$("#update_ballot_hdq").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&group="+$("#ugparam").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val() 
				 + "&answeringDate=" + $("#selectedAnsweringDate").val()
				 +"&category=question";
				
				resourceURL = 'ballot/updateHDQ?' + parameters;
				
				$.get(resourceURL, function(data){
					if(data){
						$("#ballotResultDiv").empty();
						$("#ballotResultDiv").html(data);
						$.unblockUI();
					}
				}).fail(function(){
					$.unblockUI();
				});
				
			});
			
			 $("#update_ballot_hds").click(function(){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					
					parameters = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#ugparam").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 +"&category=question";
					
					resourceURL = 'ballot/updateHDS?' + parameters;
					
					$.get(resourceURL, function(data){
						if(data){
							$("#ballotResultDiv").empty();
							$("#ballotResultDiv").html(data);
							$.unblockUI();
						}
					}).fail(function(){
						$.unblockUI();
					});
					
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
		<c:when test="${not empty answeringDates}">
			<c:choose>
				<c:when test="${deviceTypeType=='resolutions_nonofficial'
									or deviceTypeType=='bills_nonofficial' 
									or deviceTypeType == 'questions_halfhourdiscussion_from_question' 
									or deviceTypeType == 'motions_standalonemotion_halfhourdiscussion'
									or deviceTypeType == 'proprietypoint' }">
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
					<select id="answeringDateMaster" style="display:none;">
						<c:forEach items="${answeringDates}" var="i">
							<option value="${i.id}"><c:out value="${i.value}"></c:out></option>
						</c:forEach>
					</select>
				</c:when>
				<c:when test="${deviceTypeType == 'questions_halfhourdiscussion_from_question' 
								or deviceTypeType == 'motions_standalonemotion_halfhourdiscussion' 
								or deviceTypeType=='resolutions_nonofficial'
								or deviceTypeType=='bills_nonofficial'
								or deviceTypeType == 'proprietypoint'}">
					<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
					<c:forEach items="${answeringDates}" var="i">			
						<option value="${i.value}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach> 
					</select>
				</c:when>
			</c:choose>
			| 
			<security:authorize access="hasAnyRole('QIS_ADMIN', 'QIS_UNDER_SECRETARY', 'QIS_DEPUTY_SECRETARY', 'QIS_PRINCIPAL_SECRETARY', 
			'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_UNDER_SECRETARY_COMMITTEE', 'QIS_JOINT_SECRETARY',
			'QIS_ADDITIONAL_SECRETARY', 'SMOIS_UNDER_SECRETARY_COMMITTEE','SMOIS_UNDER_SECRETARY', 'SMOIS_DEPUTY_SECRETARY',
				'SMOIS_JOINT_SECRETARY', 'SMOIS_SECRETARY', 'SMOIS_OFFICER_ON_SPECIAL_DUTY', 'SMOIS_PRINCIPAL_SECRETARY', 'BIS_ASSISTANT', 'BIS_PRINCIPAL_SECRETARY',
			 'PROIS_SECTION_OFFICER','PROIS_DEPUTY_SECRETARY',
				'ROIS_UNDERSECRETARY', 'ROIS_UNDER_SECRETARY_COMMITTEE', 'ROIS_SECRETARY', 'ROIS_SECTION_OFFICER','ROIS_DEPUTYSECRETARY'
			 )">
			<a href="#" id="pre_ballot" class="butSim">
				<spring:message code="ballotinitial.preballot" text="Pre Ballot"/>
			</a>
			|
			</security:authorize>
			<a href="#" id="view_preballot" class="butSim">
				<spring:message code="ballotinitial.viewpreballot" text="View Preballot"/>
			</a>
			|
			<security:authorize access="hasAnyRole('QIS_ADMIN', 'QIS_CLERK','QIS_ASSISTANT','QIS_SECTION_OFFICER','SMOIS_CLERK','SMOIS_ASSISTANT','PROIS_CLERK','PROIS_ASSISTANT')">
			<a href="#" id="preview_preballot" class="butSim">
				<spring:message code="ballotinitial.previewpreballot" text="Preview PreBallot"/>
			</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_ADMIN', 'QIS_UNDER_SECRETARY', 'QIS_DEPUTY_SECRETARY',  
			'QIS_OFFICER_ON_SPECIAL_DUTY', 'QIS_UNDER_SECRETARY_COMMITTEE', 'QIS_JOINT_SECRETARY',
			'QIS_ADDITIONAL_SECRETARY', 'SMOIS_UNDER_SECRETARY_COMMITTEE','SMOIS_UNDER_SECRETARY', 'SMOIS_DEPUTY_SECRETARY',
			'SMOIS_JOINT_SECRETARY', 'SMOIS_SECRETARY', 'BIS_ASSISTANT', 
			'PROIS_SECTION_OFFICER','PROIS_DEPUTY_SECRETARY',
			 'ROIS_UNDERSECRETARY', 'ROIS_UNDER_SECRETARY_COMMITTEE', 'ROIS_SECRETARY', 'ROIS_SECTION_OFFICER','ROIS_DEPUTYSECRETARY')">
			<a href="#" id="create_ballot" class="butSim">
				<spring:message code="ballotinitial.createballot" text="Create Ballot"/>
			</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_ADMIN', 'QIS_UNDER_SECRETARY', 'QIS_DEPUTY_SECRETARY',  'QIS_UNDER_SECRETARY_COMMITTEE', 'QIS_JOINT_SECRETARY',
			 'SMOIS_UNDER_SECRETARY_COMMITTEE','SMOIS_UNDER_SECRETARY','SMOIS_DEPUTY_SECRETARY','SMOIS_JOINT_SECRETARY', 'SMOIS_SECRETARY', 'SMOIS_OFFICER_ON_SPECIAL_DUTY', 
			 'PROIS_SECTION_OFFICER','PROIS_UNDER_SECRETARY_COMMITTEE','PROIS_UNDER_SECRETARY','PROIS_DEPUTY_SECRETARY','PROIS_PRINCIPAL_SECRETARY',
			  'BIS_ASSISTANT', 'ROIS_UNDERSECRETARY', 'ROIS_UNDER_SECRETARY_COMMITTEE', 'ROIS_SECRETARY', 'ROIS_SECTION_OFFICER','ROIS_ASSISTANT','ROIS_DEPUTYSECRETARY','QIS_PRINCIPAL_SECRETARY')">
				<a href="#" id="view_ballot" class="butSim">
					<spring:message code="ballotinitial.viewballot" text="View Ballot"/>
				</a> |
			</security:authorize>
			<security:authorize access="hasAnyRole('PROIS_SECTION_OFFICER','PROIS_DEPUTY_SECRETARY')">
			<a href="#" id="view_unballoted" class="butSim">
				<spring:message code="ballotinitial.viewunballoted" text="View Un-Balloted List"/>
			</a>
			|
			</security:authorize>
			<security:authorize access="hasAnyRole('QIS_SECTION_OFFICER')">
				<c:if test="${deviceTypeType=='questions_halfhourdiscussion_from_question' and balHouseType=='lowerhouse'}">
					<a href="#" id="update_ballot_hdq" class="butSim">
						<spring:message code="ballotinitial.updatehdq" text="Update HalfHour in Ballot"/>
					</a>
				</c:if>
			</security:authorize>
			<security:authorize access="hasAnyRole('SMOIS_SECTION_OFFICER')">
				<c:if test="${deviceTypeType=='motions_standalonemotion_halfhourdiscussion' and balHouseType=='lowerhouse'}">
					<a href="#" id="update_ballot_hds" class="butSim">
						<spring:message code="ballotinitial.updatehdq" text="Update HalfHour in Ballot"/>
					</a>
				</c:if>
			</security:authorize>
			<a href="#" id="view_log" class="butSim">
				<spring:message code="ballotinitial.viewlog" text="View Log"/>
			</a>
			<c:if test="${deviceTypeType != 'bills_nonofficial' and not(fn:contains(deviceTypeType, 'resolutions_')) 
							and not(fn:contains(deviceTypeType, 'halfhour')) and deviceTypeType ne 'proprietypoint'}"> |
				<span id="yaadiDiv">
				<a href="#" id="update_yaadi" class="butSim">
					<spring:message code="ballotinitial.updateyaadi" text="Update Questions in Yaadi"/>
				</a> |
				<%-- <a href="#" id="view_yaadi" class="butSim">
					<spring:message code="ballotinitial.viewyaadi" text="View Yaadi"/>
				</a> | 
				<a href="#" id="view_suchi" class="butSim">
					<spring:message code="ballotinitial.viewsuchi" text="View Suchi"/>
				</a> --%>				
				<%-- <c:if test="${not empty outputFormats}">				
					<select id="outputFormat" name="outputFormat">
						<option value="" selected="selected">Please Select Output Format</option>
						<c:forEach items="${outputFormats}" var="i">
							<option value="${i.value}">${i.name}</option>
						</c:forEach>
					</select>				
				</c:if> --%>		
				</span>		
				<hr/>			
			</c:if>
			<c:if test="${deviceTypeType =='resolutions_nonofficial'}">|
				<%-- <c:if test="${houseType=='upperhouse'}"> --%>
				<a href="#" id="give_balloted_resolution_choice" class="butSim">
					<spring:message code="ballotinitial.ballotchoice" text="Give Choice"/>
				</a>
				<%-- </c:if> --%>
				<c:if test="${houseType=='lowerhouse'}">
					<a id="patrakbhag2_tab" href="#" class="tab">
				   		<spring:message code="resolution.patrakbhag2" text="Post Ballot Report"></spring:message>
					</a>		
				</c:if>
			</c:if>
			
			<br />
			<h3 id="error_msg" style="color: red"></h3>
		</c:when>
		<c:otherwise>
			<h3>No Discussion Dates Available for Given Session</h3>
		</c:otherwise>
	</c:choose>		
</div>
<div id="ballotResultDiv">
</div>
<input type="hidden" id="ballotViewFailureMsg" value="<spring:message code='ballot.view.failure' text='Can not view ballot.'></spring:message>">
<input id="category" type="hidden" value="${category}" />
<input id="deviceType" type="hidden" value="${deviceTypeType}" />
<input id="houseType" type="hidden" value="${houseType}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="ballotConfirmationMsg" value="<spring:message code='ballot.confirmationMessage' text='This is an irreversible change.Are you sure you want to continue?'/>"/>
<input type="hidden" id="balHouseType" value="${balHouseType}" />
<input type="hidden" id="highSecurityPasswordEnabled" value="${highSecurityPasswordEnabled}" />

</body>
</html>