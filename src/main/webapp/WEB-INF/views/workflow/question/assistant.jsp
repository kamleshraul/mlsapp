<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<link type="text/css" rel="stylesheet" href="./resources/css/printerfriendly.css?v=5" media="print" />
	
	<script type="text/javascript">
	/**** detail of clubbed and refernced questions ****/		
	function viewQuestionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var deviceId = $("#deviceTypeMasterIds option[value='"+$("#selectedDeviceType").val()+"']").text();
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
		var parameters="questionType="+deviceId+"&qid="+id
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
	/**** Clubbing ****/
	function clubbingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="id=" + id
					+"&usergroup=" + $("#currentusergroup").val()
			        +"&usergroupType=" + $("#currentusergroupType").val();		
		$.get('clubentity/init?'+params,function(data){
			$.unblockUI();	
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#clubbingResultDiv").html(data);
			$("#clubbingResultDiv").show();
			$("#referencingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToQuestionDiv").show();			
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
	/**** Referencing ****/
	function referencingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="id=" + id
		+"&usergroup=" + $("#currentusergroup").val()
        +"&usergroupType=" + $("#currentusergroupType").val();
		$.get('refentity/init?' + params,function(data){
			$.unblockUI();			
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#referencingResultDiv").html(data);
			$("#referencingResultDiv").show();
			$("#clubbingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToQuestionDiv").show();			
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
	/**** refresh clubbing and referencing ****/
	function refreshEdit(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var deviceId = $("#deviceTypeMasterIds option[value='"+$("#selectedDeviceType").val()+"']").text();
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+deviceId
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='question/'+id+'/edit?'+parameters;
		$('a').removeClass('selected');
		//id refers to the tab name and it is used just to highlight the selected tab
		$('#'+ id).addClass('selected');
		//tabcontent is the content area where result of the url load will be displayed
		$('.tabContent').load(resourceURL);
		$("#referencingResultDiv").hide();
		$("#clubbingResultDiv").hide();
		$("#assistantDiv").show();
		scrollTop();
		$.unblockUI();			
	}
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
		var putUpForDateApproval = '';
		var sendToSectionOfficer = '';
		var clubbingApproved = '';
		var clubbingRejected = '';
		var nameclubbingApproved = '';
		var nameclubbingRejected = '';
		var clubbingPostAdmissionRecommendApprove = '';
		var clubbingPostAdmissionRecommendReject = '';
		var clubbingPostAdmissionApproved = '';
		var clubbingPostAdmissionRejected = '';
		var unclubbingRecommendApprove = '';
		var unclubbingRecommendReject = '';
		var unclubbingApproved = '';
		var unclubbingRejected = '';
		var admitDueToReverseClubbingRecommendApprove = '';
		var admitDueToReverseClubbingApproved = '';
		var recommendRejection = '';
		var finalRejection = '';
		var resendToSectionOfficer = '';
		var sendDiscussionDateToSectionOfficer = '';
		var answerConfirmed = $("#internalStatusMaster option[value='question_unstarred_processed_answerConfirmed']").text();
		var deviceTypeType = $('#selectedQuestionType').val();
		if(deviceTypeType == 'questions_starred') {
			putUpForDateApproval = $("#internalStatusMaster option[value='question_processed_putUpForDateApproval']").text();
			sendToSectionOfficer = $("#internalStatusMaster option[value='question_processed_sendToSectionOfficer']").text();
			clubbingApproved = $("#internalStatusMaster option[value='question_final_clubbing']").text();
			clubbingRejected = $("#internalStatusMaster option[value='question_final_reject_clubbing']").text();
			nameclubbingApproved = $("#internalStatusMaster option[value='question_final_nameclubbing']").text();
			nameclubbingRejected = $("#internalStatusMaster option[value='question_final_reject_nameclubbing']").text();
			clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='question_recommend_clubbingPostAdmission']").text();
			clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='question_recommend_reject_clubbingPostAdmission']").text();
			clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='question_final_clubbingPostAdmission']").text();
			clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='question_final_reject_clubbingPostAdmission']").text();
			unclubbingRecommendApprove = $("#internalStatusMaster option[value='question_recommend_unclubbing']").text();
			unclubbingRecommendReject = $("#internalStatusMaster option[value='question_recommend_reject_unclubbing']").text();
			unclubbingApproved = $("#internalStatusMaster option[value='question_final_unclubbing']").text();
			unclubbingRejected = $("#internalStatusMaster option[value='question_final_reject_unclubbing']").text();
			admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='question_recommend_admitDueToReverseClubbing']").text();
			admitDueToReverseClubbingApproved = $("#internalStatusMaster option[value='question_final_admitDueToReverseClubbing']").text();
			recommendRejection = $("#internalStatusMaster option[value='question_recommend_rejection']").text();
			finalRejection = $("#internalStatusMaster option[value='question_final_rejection']").text();
			resendToSectionOfficer = $("#internalStatusMaster option[value='question_processed_resendRevisedQuestionTextToSectionOfficer']").text();
			
		}else if(deviceTypeType == 'questions_unstarred') {
			putUpForDateApproval = $("#internalStatusMaster option[value='question_unstarred_processed_putUpForDateApproval']").text();
			sendToSectionOfficer = $("#internalStatusMaster option[value='question_unstarred_processed_sendToSectionOfficer']").text();
			clubbingApproved = $("#internalStatusMaster option[value='question_unstarred_final_clubbing']").text();
			clubbingRejected = $("#internalStatusMaster option[value='question_unstarred_final_reject_clubbing']").text();
			nameclubbingApproved = $("#internalStatusMaster option[value='question_unstarred_final_nameclubbing']").text();
			nameclubbingRejected = $("#internalStatusMaster option[value='question_unstarred_final_reject_nameclubbing']").text();
			clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='question_unstarred_recommend_clubbingPostAdmission']").text();
			clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='question_unstarred_recommend_reject_clubbingPostAdmission']").text();
			clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='question_unstarred_final_clubbingPostAdmission']").text();
			clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='question_unstarred_final_reject_clubbingPostAdmission']").text();
			unclubbingRecommendApprove = $("#internalStatusMaster option[value='question_unstarred_recommend_unclubbing']").text();
			unclubbingRecommendReject = $("#internalStatusMaster option[value='question_unstarred_recommend_reject_unclubbing']").text();
			unclubbingApproved = $("#internalStatusMaster option[value='question_unstarred_final_unclubbing']").text();
			unclubbingRejected = $("#internalStatusMaster option[value='question_unstarred_final_reject_unclubbing']").text();
			admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='question_unstarred_recommend_admitDueToReverseClubbing']").text();
			admitDueToReverseClubbingApproved = $("#internalStatusMaster option[value='question_unstarred_final_admitDueToReverseClubbing']").text();
			recommendRejection = $("#internalStatusMaster option[value='question_unstarred_recommend_rejection']").text();
			finalRejection = $("#internalStatusMaster option[value='question_unstarred_final_rejection']").text();
			resendToSectionOfficer = $("#internalStatusMaster option[value='question_unstarred_processed_resendRevisedQuestionTextToSectionOfficer']").text();
		}else if(deviceTypeType == 'questions_shortnotice') {
			putUpForDateApproval = $("#internalStatusMaster option[value='question_shortnotice_processed_putUpForDateApproval']").text();
			sendToSectionOfficer = $("#internalStatusMaster option[value='question_shortnotice_processed_sendToSectionOfficer']").text();
			clubbingApproved = $("#internalStatusMaster option[value='question_shortnotice_final_clubbing']").text();
			clubbingRejected = $("#internalStatusMaster option[value='question_shortnotice_final_reject_clubbing']").text();
			nameclubbingApproved = $("#internalStatusMaster option[value='question_shortnotice_final_nameclubbing']").text();
			nameclubbingRejected = $("#internalStatusMaster option[value='question_shortnotice_final_reject_nameclubbing']").text();
			clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='question_shortnotice_recommend_clubbingPostAdmission']").text();
			clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='question_shortnotice_recommend_reject_clubbingPostAdmission']").text();
			clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='question_shortnotice_final_clubbingPostAdmission']").text();
			clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='question_shortnotice_final_reject_clubbingPostAdmission']").text();
			unclubbingRecommendApprove = $("#internalStatusMaster option[value='question_shortnotice_recommend_unclubbing']").text();
			unclubbingRecommendReject = $("#internalStatusMaster option[value='question_shortnotice_recommend_reject_unclubbing']").text();
			unclubbingApproved = $("#internalStatusMaster option[value='question_shortnotice_final_unclubbing']").text();
			unclubbingRejected = $("#internalStatusMaster option[value='question_shortnotice_final_reject_unclubbing']").text();
			admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='question_shortnotice_recommend_admitDueToReverseClubbing']").text();
			admitDueToReverseClubbingApproved = $("#internalStatusMaster option[value='question_shortnotice_final_admitDueToReverseClubbing']").text();
			recommendRejection = $("#internalStatusMaster option[value='question_shortnotice_recommend_rejection']").text();
			finalRejection = $("#internalStatusMaster option[value='question_shortnotice_final_rejection']").text();
			resendToSectionOfficer = $("#internalStatusMaster option[value='question_shortnotice_processed_resendRevisedQuestionTextToSectionOfficer']").text();
		}else if(deviceTypeType == 'questions_halfhourdiscussion_from_question') {
			putUpForDateApproval = $("#internalStatusMaster option[value='question_halfHourFromQuestion_processed_putUpForDateApproval']").text();
			sendToSectionOfficer = $("#internalStatusMaster option[value='question_halfHourFromQuestion_processed_sendToSectionOfficer']").text();
			clubbingApproved = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_clubbing']").text();
			clubbingRejected = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_reject_clubbing']").text();
			nameclubbingApproved = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_nameclubbing']").text();
			nameclubbingRejected = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_reject_nameclubbing']").text();
			clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_clubbingPostAdmission']").text();
			clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_reject_clubbingPostAdmission']").text();
			clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_clubbingPostAdmission']").text();
			clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_reject_clubbingPostAdmission']").text();
			unclubbingRecommendApprove = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_unclubbing']").text();
			unclubbingRecommendReject = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_reject_unclubbing']").text();
			unclubbingApproved = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_unclubbing']").text();
			unclubbingRejected = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_reject_unclubbing']").text();
			admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_admitDueToReverseClubbing']").text();
			admitDueToReverseClubbingApproved = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_admitDueToReverseClubbing']").text();
			recommendRejection = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_rejection']").text();
			finalRejection = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_rejection']").text();
			resendToSectionOfficer = $("#internalStatusMaster option[value='question_halfHourFromQuestion_processed_resendRevisedQuestionTextToSectionOfficer']").text();
			sendDiscussionDateToSectionOfficer = $("#internalStatusMaster option[value='question_halfHourFromQuestion_processed_sendDiscussionDateIntimationToSectionOfficer']").text();
		}
		var currentRecommendationStatus = $("#recommendationStatus").val();
		
		var valueToSend = "";
		var changedInternalStatus = $("#changeInternalStatus").val();
		if(changedInternalStatus == putUpForDateApproval || 
		   changedInternalStatus == sendToSectionOfficer ||
		   changedInternalStatus == resendToSectionOfficer ||
		   changedInternalStatus == sendDiscussionDateToSectionOfficer) {
			$("#endFlag").val("continue");
			valueToSend = $("#internalStatus").val();
		} 
		else if(value==clubbingApproved || value==clubbingRejected					
				|| value==nameclubbingApproved || value == nameclubbingRejected
				|| value==clubbingPostAdmissionApproved || value==clubbingPostAdmissionRejected
				|| value==unclubbingApproved || value == unclubbingRejected
				|| value==admitDueToReverseClubbingApproved){
			$("#endFlag").val("end");
			if(value!=clubbingPostAdmissionApproved && value!=clubbingPostAdmissionRejected
					&& value!=unclubbingApproved && value!=unclubbingRejected
					&& value!=admitDueToReverseClubbingApproved) {
				$("#internalStatus").val(value);
			}				
			$("#recommendationStatus").val(value);
			$("#actor").empty();
			$("#actorDiv").hide();
			return false;
		} 
		else if(value==answerConfirmed) {
			$("#endFlag").val("end");
			$("#recommendationStatus").val(value);
			$("#level").val("1");	    
			$("#localizedActorName").val("");
			$("#actor").empty();
			$("#actorDiv").hide();
			return false;
		}
		else {
			$("#endFlag").val("continue");
			valueToSend = value;
		}
		var level = $("#originalLevel").val();
		var params="question=" + $("#id").val() + "&status=" + valueToSend + 
		"&usergroup=" + $("#usergroup").val() + "&level=" + level;
		var resourceURL='ref/question/actors?' + params;
		$.post(resourceURL,function(data){
			if(data!=undefined || data!=null || data!=''){
				var actor1="";
				var actCount = 1;
				 
				var length = data.length;
				$("#actor").empty();
				var text="";
				for(var i=0 ; i<data.length ; i++){
					var ugt = data[i].id.split("#")[1];
					
					if(ugt!='member' && data[i].state!='active'){
						text += "<option value='" + data[i].id + "' disabled='disabled'>" + data[i].name + "</option>";
					}else{
						text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";	
						if(actCount == 1){
							actor1=data[i].id;
							actCount++;
						}
					}
				}
				$("#actor").html(text);
				if($("#changeInternalStatus").val() == putUpForDateApproval
						|| $("#changeInternalStatus").val() == sendDiscussionDateToSectionOfficer){
					$("#actorDiv").show();	
				}else{
					$("#actorDiv").hide();
				}
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != putUpForDateApproval && value != sendToSectionOfficer
						&& value != clubbingPostAdmissionRecommendApprove && value != clubbingPostAdmissionRecommendReject
						&& value != unclubbingRecommendApprove && value != unclubbingRecommendReject
						&& value != admitDueToReverseClubbingRecommendApprove
						&& value != resendToSectionOfficer
						&& value != sendDiscussionDateToSectionOfficer){
					$("#internalStatus").val(value);
					$("#actorDiv").show();
				} else {
					$("#internalStatus").val($("#oldInternalStatus").val());
				}
				$("#recommendationStatus").val($("#changeInternalStatus").val());
				
				/**** setting level,localizedActorName ****/
				 //var actor1 = data[0].id;
				 var temp = actor1.split("#");
				 $("#level").val(temp[2]);		    
				 $("#localizedActorName").val(temp[3] + "(" + temp[4] + ")");
				 $("#actorName").val(temp[4]);
				 $("#actorName").css('display','inline');
				
				
			}else{
				$("#actor").empty();
				$("#actorDiv").hide();
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != putUpForDateApproval && value != sendToSectionOfficer && value != resendToSectionOfficer
						&& value != clubbingPostAdmissionRecommendApprove && value != clubbingPostAdmissionRecommendReject
						&& value != unclubbingRecommendApprove && value != unclubbingRecommendReject
						&& value != admitDueToReverseClubbingRecommendApprove
						&& value != sendDiscussionDateToSectionOfficer){
					$("#internalStatus").val(value);
				} else {
					$("#internalStatus").val($("#oldInternalStatus").val());
				}
				$("#recommendationStatus").val($("#changeInternalStatus").val());
			}			
		}).fail(function(){
			if($("#actorDiv").css("display")=='block'){
				$("#actorDiv").hide();
			}
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html("No such flow defined.").css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
		}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}	
	}
	/**** group changed ****/
	function groupChanged(){
		var newgroup=$("#group").val();
		var groupChanged = '';
		var deviceTypeType = $('#selectedQuestionType').val();
		if(deviceTypeType == 'questions_starred') {
			 groupChanged = $("#internalStatusMaster option[value='question_system_groupchanged']").text();
		} else if(deviceTypeType == 'questions_unstarred') {
			 groupChanged = $("#internalStatusMaster option[value='question_unstarred_system_groupchanged']").text();
		} else if(deviceTypeType == 'questions_shortnotice') {
			 groupChanged = $("#internalStatusMaster option[value='question_shortnotice_system_groupchanged']").text();
		} else if(deviceTypeType == 'questions_halfhourdiscussion_from_question') {
			 groupChanged = 
				 $("#internalStatusMaster option[value='question_halfHourFromQuestion_system_groupchanged']").text();
		}
		if(newgroup==''){
		   	$("#changeInternalStatus").val("-");
		    $("#changeInternalStatus option").show();			    
		    $("#changeInternalStatus option[value=']"+groupChanged+"'").hide();
		    $("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		    $.prompt($("#ministryEmptyMsg").val());
		    return false;
		}
	    var oldgroup=$("#oldgroup").val();
		    if(oldgroup!=newgroup){
			  //  $("#changeInternalStatus").val(newStatus);
			    $("#changeInternalStatus option").hide();			    
			    $("#changeInternalStatus option[value=']"+groupChanged+"'").show();
			    $("#internalStatus").val(groupChanged);
			    $("#recommendationStatus").val(groupChanged);
		    }else{
		    	$("#changeInternalStatus").val("-");
			    $("#changeInternalStatus option").show();			    
			    $("#changeInternalStatus option[value=']"+groupChanged+"'").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());			    
		    }  
		    return false;  
	}
	/**** sub departments ****/
	function loadSubDepartments(ministry){
		
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data){
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);			
			}else{
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);				
			}
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
    /**** groups ****/
	function loadGroup(subdepartment){
		if(subdepartment!=''){
			$.get('ref/subdepartment/' + subdepartment + '/group?'+
					'session=' + $("#session").val(),function(data){
				$("#formattedGroup").val(data.name);
				$("#group").val(data.id);			
			}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
		}
	}		
	/**** Load Clarifications ****/
	function loadClarifications(){
		$.get('ref/clarifications',function(data){
			if(data.length>0){
				var text="";
				for( var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#clarificationNeededFrom").empty();
				$("#clarificationNeededFrom").html(text);
				$("#clarificationDiv").show();								
			}else{
				$("#clarificationNeededFrom").empty();
				$("#clarificationDiv").hide();
			}
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	$(document).ready(function(){
		/** Warn if revised question text of parent is changed while its child pending for clubbing approval has some modifications in the same **/
		var isPendingClubbedQuestionSearched = false;
	    var isPendingClubbedQuestionFound = false;
	    var isRevisedQuestionTextDisallowedToEdit = false;
	    var revisedQuestionTextOriginal = $('#revisedQuestionText').val();
	    var clubbedQuestionNumbers = "";
		$('#revisedQuestionText').wysiwyg({ //registered here for keypress event handling
			resizeOptions: {maxWidth: 600},
			controls:{
				fullscreen: {
					visible: true,
					hotkey:{
						"ctrl":1|0,
						"key":122
					},
					exec: function () {
						if ($.wysiwyg.fullscreen) {
							$.wysiwyg.fullscreen.init(this);
						}
					},
					tooltip: "Fullscreen"
				},
				strikeThrough: { visible: true },
				underline: { visible: true },
				subscript: { visible: true },
				superscript: { visible: true },
				insertOrderedList  : { visible : true},
				increaseFontSize:{visible:true},
				decreaseFontSize:{visible:true},
				highlight: {visible:true}			
			},
			events: {
				keydown: function(event) {										
					var idval = $('#revisedQuestionText').attr('id');
			    	if(isPendingClubbedQuestionFound && isRevisedQuestionTextDisallowedToEdit) {
			    		if($('#'+idval).val()!=revisedQuestionTextOriginal) {
		    				$('#'+idval+'-wysiwyg-iframe').contents().find('html').html(revisedQuestionTextOriginal);
		    			}
			    		$.prompt("Questions " + clubbedQuestionNumbers + " Pending in Clubbing Approval Flows and You chose not to edit the revised question text");
			    		return false;
			    	}
			    	if($('#clubbedEntities option').length>0 && !isPendingClubbedQuestionSearched) {
			    		$.get('ref/question/'+$('#id').val()+'/is_clubbedquestion_pendingwith_updatedquestiontext', function(data) {
				    		if(data!=undefined && data.length>0) {
				    			clubbedQuestionNumbers = data;				    			
				    			isPendingClubbedQuestionFound = true;
				    			var promptMessage = "Questions " + clubbedQuestionNumbers + " Pending in Clubbing Flows..<br/>Do you still want to edit the revised question text?";
				    			$.prompt(promptMessage,{
									buttons: {Ok:true, Cancel:false}, callback: function(v){
									if(!v){
										isRevisedQuestionTextDisallowedToEdit = true;
							        	if($('#'+idval).val()!=revisedQuestionTextOriginal) {
						    				$('#'+idval+'-wysiwyg-iframe').contents().find('html').html(revisedQuestionTextOriginal);
						    			}
					    	        }
								}});
				    			return false;
				    		}
				    	});
			    		isPendingClubbedQuestionSearched = true;
			    	}
				}
			},
			plugins: {
				autoload: true,
				i18n: { lang: "mr" }
				//rmFormat: {	rmMsWordMarkup: true }
			}
		});
		
		if($('#clubbedEntities option').length>0) {
			$('#viewLatestRevisedQuestionTextFromClubbedQuestionsDiv').show();
		}
		/**** To show/hide viewLatestRevisedQuestionTextFromClubbedQuestionsDiv to view latest revised question text of clubbed questions starts****/
		$("#clubbedRevisedQuestionTextDiv").hide();
		$("#hideClubRQTDiv").hide();
		$("#viewLatestRevisedQuestionTextFromClubbedQuestionsDiv").click(function(){
			if($("#clubbedRevisedQuestionTextDiv").css('display')=='none'){
				$("#hideClubQTDiv").hide();
				$("#clubbedQuestionTextsDiv").hide();
				
				$("#hideClubRQTDiv").show();
				$('#clubbedRevisedQuestionTextDiv').empty();
				$('#clubbedRevisedQuestionTextDiv').html("<p>"+$('#latestRevisedQuestionTextFromClubbedQuestions').val()+"</p>");
				$("#clubbedRevisedQuestionTextDiv").show();
			}else{
				$("#clubbedRevisedQuestionTextDiv").hide();
				$("#hideClubRQTDiv").hide();
			}
		});
		$("#hideClubRQTDiv").click(function(){
			$(this).hide();
			$('#clubbedRevisedQuestionTextDiv').hide();
		});
		/**** To show/hide viewLatestRevisedQuestionTextFromClubbedQuestionsDiv to view latest revised question text of clubbed questions ends****/
		
		$('#mlsBranchNotifiedOfTransfer').val(null);
		$('#transferToDepartmentAccepted').val(null);
		
		$('#questionsAskedInThisFactualPosition').multiSelect();
		$("#questionsAskedInThisFactualPositionForMember").multiSelect();
		
		/** Show/Hide Rejection Reason for Rejected Question **/
		if($('#internalStatusType').val()=='question_recommend_rejection'
				|| $('#internalStatusType').val()=='question_final_rejection'
				|| $('#internalStatusType').val()=='question_unstarred_recommend_rejection'
				|| $('#internalStatusType').val()=='question_unstarred_final_rejection'
				|| $('#internalStatusType').val()=='question_shortnotice_recommend_rejection'
				|| $('#internalStatusType').val()=='question_shortnotice_final_rejection'
				|| $('#internalStatusType').val()=='question_halfHourFromQuestion_recommend_rejection'
				|| $('#internalStatusType').val()=='question_halfHourFromQuestion_final_rejection'){
			/* if($("#copyOfRejectionReason").val()!=''){
	    		$("#rejectionReason").val($("#copyOfRejectionReason").val());
	    	} */
	    	$("#rejectionReasonP").css("display","block");
	    }else{
	    	$("#rejectionReasonP").css("display","none");
	    	/* $("#copyOfRejectionReason").val($("#rejectionReason").val());
	    	$("#rejectionReason").val(""); */
	   }
		
		/*******Actor changes*************/
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
		/**** Back To Question ****/
		$("#backToQuestion").click(function(){
			$("#clubbingResultDiv").hide();
			$("#referencingResultDiv").hide();
			$("#backToQuestionDiv").hide();
			$("#assistantDiv").show();
			/**** Hide update success/failure message on coming back to question ****/
			$(".toolTip").hide();
		});
		/**** Ministry Changes ****/
		$("#ministry").change(function(){
			$.ajax({
				method: "GET",
				url: "ref/isDepartmentChangeRestricted",
				data: { deviceId: $('#id').val(), usergroupType: $("#currentusergroupType").val() },
				async: false
			}).done(function( isDepartmentChangeRestricted ) {
			    if(isDepartmentChangeRestricted!=undefined && isDepartmentChangeRestricted=="YES") {
			    	$.prompt($('#departmentChangeRestrictedMessage').val());
		    		$('#ministry').val($('#ministrySelected').val());
		    		$('#subDepartment').val($('#subDepartmentSelected').val());
		    		return false;
			    } else {
			    	if($('#ministry').val()!=''){
						$("#formattedGroup").val("");
						$("#group").val("");
						loadSubDepartments($('#ministry').val());
					}else{
						$("#formattedGroup").val("");
						$("#group").val("");				
						$("#department").empty();				
						$("#subDepartment").empty();				
						$("#answeringDate").empty();		
						$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
						$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
						//groupChanged();					
					}
			    }
			});			
		});
		
		$('#subDepartment').change(function(){
			$.ajax({
				method: "GET",
				url: "ref/isDepartmentChangeRestricted",
				data: { deviceId: $('#id').val(), usergroupType: $("#currentusergroupType").val() },
				async: false
			}).done(function( isDepartmentChangeRestricted ) {
			    if(isDepartmentChangeRestricted!=undefined && isDepartmentChangeRestricted=="YES") {
			    	$.prompt($('#departmentChangeRestrictedMessage').val());
		    		$('#ministry').val($('#ministrySelected').val());
		    		$('#subDepartment').val($('#subDepartmentSelected').val());
		    		return false;
			    } else {
			    	loadGroup($('#subDepartment').val());
			    }
			});			
		});
		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('question/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
		});	
		
		/**** Revise subject and text****/
		$("#reviseSubject").click(function(){
			$(".revise1").toggle();
			if($("#revisedSubjectDiv").css("display")=="none"){
				$("#revisedSubject").val("");	
			}else{
				$("#revisedSubject").val($("#subject").val());
			}						
			return false;			
		});	
		
		$("#reviseQuestionText").click(function(){
			//alert("reviseQuestionText clicked!");
			if($('#revisedQuestionText').val()==undefined || $('#revisedQuestionText').val()=="" || $('#revisedQuestionText').val()=="<p></p>"
					|| $('#revisedQuestionText').val()==$('#questionText').val()) {
				$(".revise2").toggle();
				if($("#revisedQuestionTextDiv").css("display")=="none"){
					$("#revisedQuestionText").wysiwyg("setContent","");
				}else{
					$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val());
				}
			} else {
				$.prompt("The revised question text is already set and its editor is open too!");
			}			
			return false;			
		});	
		
		
		/**** Revise reason and brief explanation****/
		$("#reviseReason").click(function(){
			$(".revise3").toggle();
			if($("#revisedReasonDiv").css("display")=="none"){	
				$("#revisedReason").wysiwyg("setContent","");
			}else{
				$("#revisedReason").wysiwyg("setContent",$("#reason").val());
			}						
			return false;			
		});	
		
		$("#reviseBriefExplanation").click(function(){
			$(".revise4").toggle();		
			if($("#revisedBriefExplanationDiv").css("display")=="none"){
				$("#revisedBriefExplanation").wysiwyg("setContent","");
			}else{
				$("#revisedBriefExplanation").wysiwyg("setContent",$("#briefExplanation").val());				
			}				
			return false;			
		});


		if($('#selectedQuestionType').val()=='questions_halfhourdiscussion_from_question' 
				|| $('#selectedQuestionType').val()=='questions_shortnotice'){
		
			if($("#revisedReason").val()!=''){
			    $("#revisedReasonDiv").show();
		    }
			if($('#selectedQuestionType').val()=='questions_halfhourdiscussion_from_question'){
			    if($("#revisedBriefExplanation").val()!=''){
			    	$("#revisedBriefExplanationDiv").show();
			    }
			}
		}
		
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('question/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data,{autoSize: false, width: 800, height:700});
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });
	    /**** Contact Details ****/
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("#supportingMemberIds").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('question/members/contacts?members='+members,function(data){
			    $.fancybox.open(data);
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });	    
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
			   // var statusType=$("#internalStatusMaster option[value='"+value+"']").text();
			    loadActors(value);			    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			}		    
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
		    	    			if($("#ErrorMsg").val()!=''){
		    	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    	    			}else{
		    	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    	    			}
		    	    			scrollTop();
		    	    		});
    	            }
			}});			
	        return false;  
	    });
	    
	    
		/**** On Bulk Edit ****/
		$("#submitBulkEdit").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			
			$('#bulkedit').val('yes');
			$.post($('form').attr('action'),  
	            $("form").serialize(),  
	            function(data){
   					$('.fancybox-inner').html(data);
   					$('html').animate({scrollTop:0}, 'slow');
   				 	$('body').animate({scrollTop:0}, 'slow');	
	            }).fail(function(){
	    			if($("#ErrorMsg").val()!=''){
	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    			}else{
	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    			}
	    			scrollTop();
	    		});
	        return false;  
	    });
		
		/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text starts****/
		$("#clubbedQuestionTextsDiv").hide();
		$("#hideClubQTDiv").hide();
		$("#viewClubbedQuestionTextsDiv").click(function(){
			var parent = ($("#id").val()!=undefined && $("#id").val()!='')? $("#id").val():"";
			
			if(parent!=undefined && parent!=''){	
				
				if($("#clubbedQuestionTextsDiv").css('display')=='none'){
					$("#clubbedRevisedQuestionTextDiv").hide();
					$("#hideClubRQTDiv").hide();
					
					$("#clubbedQuestionTextsDiv").empty();
					$.get('ref/'+parent+'/clubbedquestiontext',function(data){
						
						var text="";
						
						text += "<p>Parent: "+data[0].name+" ("+data[0].displayName+")</p><p>"+data[0].value+"</p><hr />";
						
						for(var i = 1; i < data.length; i++){
							text += "<p>Child "+i+":"+data[i].name+" ("+data[i].displayName+")</p><p>"+data[i].value+"</p><hr />";
						}						
						$("#clubbedQuestionTextsDiv").html(text);
						
					});
					$("#hideClubQTDiv").show();
					$("#clubbedQuestionTextsDiv").show();
				}else{
					$("#clubbedQuestionTextsDiv").hide();
					$("#hideClubQTDiv").hide();
				}
			}
		});
		$("#hideClubQTDiv").click(function(){
			$(this).hide();
			$('#clubbedQuestionTextsDiv').hide();
		});
		/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text end****/
		
	    /**** Right Click Menu ****/
		$(".clubbedRefQuestions").contextMenu({
	        menu: 'contextMenuItems'
	    },
	        function(action, el, pos) {
			var id=$(el).attr("id");
			if(action=='unclubbing'){
				if(id.indexOf("cq")!=-1){
				var questionId=$("#id").val();
				var clubId=id.split("cq")[1];		
				$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId+"&whichDevice=questions_"+"&usergroupType="+$("#currentusergroupType").val(),function(data){
					if(data=='SUCCESS' || data=='UNCLUBBING_SUCCESS'){
						$.prompt("Unclubbing Successful");				
					}else{
						$.prompt("Unclubbing Failed");
					}		
				},'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	
				}else{
					$.prompt("Unclubbing not allowed");
				}			
			}else if(action=='dereferencing'){
				if(id.indexOf("rq")!=-1){					
				var questionId=$("#id").val();
				var refId=id.split("rq")[1];				
				$.post('refentity/dereferencing?pId='+questionId+"&rId="+refId,function(data){
					if(data=='SUCCESS'){
						$.prompt("Dereferencing Successful");				
						}else{
							$.prompt("Dereferencing Failed");
						}							
				},'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	
				}else{
					$.prompt("Referencing not allowed");					
				}			
			}
	    });			    
	    /**** On Page Load ****/
	    if($("#ministrySelected").val()==''){
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
		}
	    
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}
		
		if($("#answeringDateSelected").val()==''){
		$("#answeringDate").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
		$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}

		if($('#selectedQuestionType').val()!='questions_halfhourdiscussion_from_question'){
			if($("#revisedSubject").val()!=''){
			    $("#revisedSubjectDiv").show();
		    }
			
			if($("#revisedQuestionText").val()!=''){
	    		$("#revisedQuestionTextDiv").show();
			}		  
		}
	  //--------------vikas dhananjay 20012013--------------------------
		//for viewing the refernced question
		$('#halfhourdiscussion_referred_question').click(function(){
			
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			if(questionNumber!=""){
				
				var sessionId = '${session}';
				var locale='${domain.locale}';
				
				
				var url = 'ref/questionid?strQuestionNumber='+questionNumber+'&strSessionId='+sessionId+'&deviceTypeId='+deviceTypeTemp+'&locale='+locale+'&view=view';
				
				//alert(url);
				
				$.get(url, function(data) {
					if(data.id==0){
						$.prompt('No question found.');
					}else if(data.id==-1){
						$.prompt('Please provide valid question number.');
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid='+data.id +'&questionType=' + deviceTypeTemp,function(data){
							$.fancybox.open(data,{autoSize: false, width: 800, height:700});				
						},'html');
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else{
				$.prompt('Please provide valid question number.');
			}
		});
		//***** On Page Load Internal Status Actors Will be Loaded ****/
		if($('#workflowstatus').val()!='COMPLETED' || ($("#workflowstatus").val()=='COMPLETED' 
				&& ($("#internalStatusType").val()=='question_final_admission' 
					|| $("#internalStatusType").val()=='question_unstarred_final_admission'
					|| $("#internalStatusType").val()=='question_halfHourFromQuestion_final_admission'))){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value$='"+statusType[statusType.length-1]+"']").text();
			$("#changeInternalStatus").val(id);
			$("#changeInternalStatus").change();
			//loadActors($("#changeInternalStatus").val());
		}
		
		$('#subDepartment').change(function(){
			loadGroup($(this).val());
		});
		
		$('#isAllowedInYaadi').click(function() {
			if($(this).is(':checked')) {
				$(this).val("true");
			} else {
				$(this).val("false");
			}
		});
		
		$("#remarks").change(function(){
			var recommendRejection = '';
			var finalRejection = '';
			var deviceTypeType = $('#selectedQuestionType').val();
			if(deviceTypeType == 'questions_starred'){
				recommendRejection = $("#internalStatusMaster option[value='question_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_final_rejection']").text();
			}else if(deviceTypeType == 'questions_unstarred') {
				recommendRejection = $("#internalStatusMaster option[value='question_unstarred_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_unstarred_final_rejection']").text();
			}else if(deviceTypeType == 'questions_shortnotice') {
				recommendRejection = $("#internalStatusMaster option[value='question_shortnotice_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_shortnotice_final_rejection']").text();
			}else if(deviceTypeType == 'questions_halfhourdiscussion_from_question') {
				recommendRejection = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_shortnotice_final_rejection']").text();
			}
			
			if($("#internalStatus").val()==recommendRejection || $("#internalStatus").val()==finalRejection){
				if($("#remarks").val()!=''){
					if($("#rejectionReason").val()!= $("#remarks").val()){
						$("#rejectionReason").val($("#remarks").val());
					}
				}
			} 
		});
		
		
		/********Submit Click*********/
		$('#submit').click(function(){
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			
			if($("#selectedQuestionType").val()=='questions_starred'){
				//alert($("#questionsAskedInThisFactualPosition").val());
				if($("#questionsAskedInThisFactualPosition").val() != null && $("#questionsAskedInThisFactualPosition").val()!=undefined) {
					var questionsAskedInThisFactualPosition = $("#questionsAskedInThisFactualPosition").val();
					questionsAskedInThisFactualPosition = questionsAskedInThisFactualPosition.join("##");
					$('#questionsAskedInFactualPosition').val(questionsAskedInThisFactualPosition);
				}
				
				if($("#questionsAskedInThisFactualPositionForMember").val() != null && $("#questionsAskedInThisFactualPositionForMember").val()!=undefined) {
					var questionsAskedInThisFactualPositionForMember = $("#questionsAskedInThisFactualPositionForMember").val();
					questionsAskedInThisFactualPositionForMember = questionsAskedInThisFactualPositionForMember.join("##");
					$('#questionsAskedInFactualPositionForMember').val(questionsAskedInThisFactualPositionForMember);
				}
			}

			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.post($('form').attr('action'), $("form").serialize(),
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
    	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    	    			}
    	    			scrollTop();
    	    		});
								
		});
	});
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
        #clubbedQuestionTextsDiv {
        	background: none repeat-x scroll 0 0 #FFF;
		    box-shadow: 0 2px 5px #888888;
		    max-height: 260px;
		    right: 0;
		    position: fixed;
		    top: 10px;
		    width: 300px;
		    z-index: 10000;
		    overflow: auto;
		    border-radius: 10px;
	    }
    </style>
</head> 

<body>
<p id="error_p">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<p style="color: #FF0000;">${error}</p>
</c:if>
<div class="fields clearfix watermark">
<div id="reportDiv">
<div id="assistantDiv">
<form:form action="workflow/question" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${workflowstatus=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>
	<br>
	${formattedQuestionType}: ${formattedNumber}		
	</c:when>
	<c:otherwise>
	${formattedQuestionType}: ${formattedNumber}		
	</c:otherwise>
	</c:choose>
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.type" text="Type"/>*</label>
		<input id="formattedQuestionType" name="formattedQuestionType" value="${formattedQuestionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${questionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
		<c:choose>
			<c:when test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
				<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
			</c:when>
			<c:otherwise>
				<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
			</c:otherwise>
		</c:choose>
	<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
	<input id="number" name="number" value="${domain.number}" type="hidden">
	<form:errors path="number" cssClass="validationError"/>
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		
		<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
		<input class="sInteger integer" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
		<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>	
		
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_starred'}">
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
		<input name="priority" id="priority"  type="hidden" value="${priority }">	
		<form:errors path="priority" cssClass="validationError"/>
	</c:if>
	</p>
		
	<p>		
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	
	<c:if test="${selectedQuestionType=='questions_starred'}">
		<c:if test="${not (formattedAnsweringDate==null and (empty formattedAnsweringDate))}">
			<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
			<input id="formattedAnsweringDate" name="formattedAnsweringDate" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
		</c:if>
		<input id="answeringDate" name="answeringDate" type="hidden"  value="${answeringDate}">
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<c:if test="${not (discussionDateSelected==null and (empty discussionDateSelected))}">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<input id="formattedDiscussionDate"value="${formattedDiscussionDateSelected }" class="sText" readonly="readonly">
			<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" type="hidden">
			<form:errors path="discussionDate" cssClass="validationError"/>
		</c:if>
	</c:if>
	</p>
	
	<c:choose>
	<c:when test="${selectedQuestionType=='questions_starred'}">
		<p>
		<c:if test="${not empty formattedChartAnsweringDate}">
			<label class="small"><spring:message code="question.chartAnsweringDate" text="Chart Answering Date"/></label>
			<input id="formattedChartAnsweringDate" name="formattedChartAnsweringDate" value="${formattedChartAnsweringDate}" class="sText" readonly="readonly">
		</c:if>	
		<input id="chartAnsweringDate" name="chartAnsweringDate" type="hidden"  value="${chartAnsweringDate}">
		</p>
	</c:when>
	<c:when test="${selectedQuestionType=='questions_unstarred'}">
		<c:if test="${not empty formattedChartAnsweringDate}">
			<p>
				<label class="small"><spring:message code="question.chartAnsweringDate" text="Chart Answering Date"/></label>
				<input id="formattedChartAnsweringDate" name="formattedChartAnsweringDate" value="${formattedChartAnsweringDate}" class="sText" readonly="readonly">
				<input id="chartAnsweringDate" name="chartAnsweringDate" type="hidden"  value="${chartAnsweringDate}">
			</p>
		</c:if>
	</c:when>
	</c:choose>
	
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect" style="width:270px;">
			<c:forEach items="${ministries }" var="i">
				<c:choose>
					<c:when test="${i.id==ministrySelected }">
						<option value="${i.id }" selected="selected">${i.dropdownDisplayName}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.dropdownDisplayName}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		<form:errors path="ministry" cssClass="validationError"/>	
		
		<label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
		<input type="hidden" id="group" name="group" value="${group }">
		<form:errors path="group" cssClass="validationError"/>
		
	</p>	
	
	
	<p>	
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<select name="subDepartment" id="subDepartment" class="sSelect" style="width:270px;" >
			<c:forEach items="${subDepartments }" var="i">
				<c:choose>
					<c:when test="${i.id==subDepartmentSelected }">
						<option value="${i.id }" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		<form:errors path="subDepartment" cssClass="validationError"/>	
	</p>	
	<p>		
		<label class="small"><spring:message code="question.processed" text="Processed By Clerk?"/></label>
		<form:checkbox path="processed"/>
	</p>
	<p>
	<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
	<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty selectedSupportingMembersIds)}">
		<select  name="supportingMembers" id="supportingMembers" multiple="multiple" style="display:none;">
		<c:forEach items="${selectedSupportingMembersIds}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	<c:if test="${!(empty supportingMembers)}">
		<select  name="supportingMembersIds" id="supportingMembersIds" multiple="multiple" style="display:none;">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	
	<p>
		<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="question.clubbing" text="Clubbing"></spring:message></a>
		<c:if test="${selectedQuestionType!='questions_halfhourdiscussion_from_question'}">
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="question.referencing" text="Referencing"></spring:message></a>
		</c:if>
		<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="question.refresh" text="Refresh"></spring:message></a>	
	</p>	
		
	<p>
		<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewQuestionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>	
	<p>
		<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedQuestions) }">
				<c:forEach items="${clubbedQuestions }" var="i">
					<a href="#" id="cq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
				<a href="javascript:void(0);" id="viewClubbedQuestionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;margin-left: 5px;font-size: 18px;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
		<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${clubbedQuestions }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
		</p>
		
	<p>
		<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedQuestions) }">
				<c:forEach items="${referencedQuestions }" var="i">
					<c:choose>
						<c:when test="${i.state=='questions_unstarred'}">
							<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;">
								${i.name}, <spring:message code='device.unstarred' text='Unstarred'/> ${i.remark}
							</a>
						</c:when>
						<c:otherwise>
							<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;">
								${i.name}, ${i.remark}
							</a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedQuestions }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</p>
	
	
	<p>	
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
		<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
		<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	
	<c:if test="${selectedQuestionType=='questions_starred' or selectedQuestionType=='questions_unstarred'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.reference" text="Reference Text"/>*</label>
			<form:textarea path="questionreferenceText" cssClass="wysiwyg"></form:textarea>
			<form:errors path="questionreferenceText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
	
	
	<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question' }">
	<p>
		<c:choose>
			<c:when test="${selectedQuestionType=='questions_shortnotice'}">
				<label class="wysiwyglabel"><spring:message code="question.shortnoticeReason" text="Reason"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
			</c:when>
			<c:otherwise>
				<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
			</c:otherwise>
		</c:choose>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>	
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<p>
		<c:if test="${selectedQuestionType!='questions_halfhourdiscussion_from_question'}">
			<a href="javascript:void(0);" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
			<a href="javascript:void(0);" style="" id="reviseQuestionText"><spring:message code="question.reviseQuestionText" text="Revise Question Text"></spring:message></a>
		</c:if>
		
	
		<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			<c:choose>
				<c:when test="${selectedQuestionType=='questions_shortnotice'}">
					<a href="#" id="reviseReason" style="margin-left: 20px;"><spring:message code="question.revise.common.reason" text="Revise Reason"></spring:message></a>
				</c:when>
				<c:otherwise>
					<a href="#" id="reviseReason" style="margin-left: 20px;"><spring:message code="question.revise.common.reason" text="Revise Reason"></spring:message></a>
				</c:otherwise>
			</c:choose>					
			<c:if test="${selectedQuestionType!='questions_shortnotice'}">	
				<a href="#" id="reviseBriefExplanation" style="margin: 0px 20px 10px 10px;"><spring:message code="question.reviseBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
			</c:if>
		</c:if>
	</p>
	
	<p style="display:none;" class="revise4" id="revisedBriefExplanationDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
	<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedQuestionTextDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
	<a href="javascript:void(0);" id="viewLatestRevisedQuestionTextFromClubbedQuestionsDiv" style="border: 1px solid #000000;background-color: #657A8F;border-radius: 5px;color: #FFFFFF;text-decoration: none;float: right;margin-top: -200px;margin-right: 200px;display:none;"><spring:message code="question.clubbed.revised_texts" text="RC"></spring:message></a>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise3" id="revisedReasonDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedReason" text="Revised Reason"/></label>
	<form:textarea path="revisedReason" rows="2" cols="50" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedReason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:choose>
		<c:when  test="${!(empty domain.rejectionReason)}">
			<p id="rejectionReasonP">
				<%-- <label class="centerlabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" rows="2" cols="50"></form:textarea> --%>					
				<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:when>
		<c:otherwise>
			<p id="rejectionReasonP">
				<%-- <label class="centerlabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" rows="2" cols="50"></form:textarea> --%>					
				<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${(fn:endsWith(internalStatusType, 'final_clarificationNeededFromDepartment')
		|| fn:endsWith(internalStatusType, 'final_clarificationNeededFromMemberAndDepartment'))}">
		<p>
			<c:choose>
				<c:when test="${houseTypeType == 'lowerhouse'}">
					<label class="wysiwyglabel"><spring:message code="hds.questionsAskedInFactualPosition" text="Questions To Be Asked In Factual Position"/></label>
					<form:textarea path="questionsAskedInFactualPosition" id="questionsAskedInFactualPosition" cssClass="wysiwyg"/>
				</c:when>
				<c:otherwise>
					<label class="small"><spring:message code="hds.questionsAskedInFactualPosition" text="Questions To Be Asked In Factual Position"/></label>
					<select name="questionsAskedInThisFactualPosition" id="questionsAskedInThisFactualPosition" class="sSelectMultiple" size="5" multiple="multiple">
						<c:forEach items="${questionsToBeAskedInFactualPosition}" var="i">
							<c:choose>
								<c:when test="${i.isSelected=='true'}">
									<option value="${i.value}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.value}" >${i.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					<form:hidden path="questionsAskedInFactualPosition"/>
				</c:otherwise>
			</c:choose>
			<form:errors path="questionsAskedInFactualPosition" cssClass="validationError"/>	
		</p>	
	</c:if>
	<c:if test="${(fn:endsWith(internalStatusType, 'final_clarificationNeededFromMember')
		||
		fn:endsWith(internalStatusType, 'final_clarificationNeededFromMemberAndDepartment'))}">
		<p>
			<c:choose>
				<c:when test="${houseTypeType == 'lowerhouse'}">
					<label class="wysiwyglabel"><spring:message code="hds.questionsAskedInFactualPositionForMember" text="Questions To Be Asked In Factual Position for Member"/></label>
					<form:textarea path="questionsAskedInFactualPositionForMember" id="questionsAskedInFactualPositionForMember" cssClass="wysiwyg"/>
				</c:when>
				<c:otherwise>
					<label class="small"><spring:message code="hds.questionsAskedInFactualPosition" text="Questions To Be Asked In Factual Position for Member"/></label>
					<select name="questionsAskedInThisFactualPositionForMember" id="questionsAskedInThisFactualPositionForMember" class="sSelectMultiple" size="5" multiple="multiple">
						<c:forEach items="${questionsToBeAskedInFactualPositionForMember}" var="i">
							<c:choose>
								<c:when test="${i.isSelected=='true'}">
									<option value="${i.value}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.value}" >${i.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					<form:hidden path="questionsAskedInFactualPositionForMember"/>
				</c:otherwise>
			</c:choose>
			<form:errors path="questionsAskedInFactualPositionForMember" cssClass="validationError"/>	
		</p>	
	</c:if>
	
	<p style="text-align: right; width: 720px;">
		<a href="#" id="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	<table class="uiTable" style="margin-left:165px;">
		<thead>
			<tr>
				<th>
					<spring:message code="qis.latestrevisions.user" text="Usergroup"></spring:message>
				</th>
				<th>
					<spring:message code="qis.latestrevisions.decision" text="Decision"></spring:message>
				</th>
				<th>
					<spring:message code="qis.latestrevisions.remarks" text="Remarks"></spring:message>
				</th>
			</tr>
		</thead>
		<tbody>	
			<c:set var="startingActor" value="${startingActor}"></c:set>
			<c:set var="count" value="0"></c:set>
			<c:set var="startingActorCount" value="0"></c:set>
			<c:forEach items="${latestRevisions}" var="i">	
				<c:choose>
					<c:when test="${i[0]==startingActor}">	
						<c:set var="startingActorCount" value="${count}"></c:set>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<c:set var="count" value="0"></c:set>
			<c:forEach items="${latestRevisions }" var="i">
				<c:choose>
					<c:when test="${count>= startingActorCount}">
						<tr>
							<td>
							${i[0]}<br>${i[1]}
							</td>
							<td>
							<c:choose>
								<c:when test="${fn:endsWith(i[12],'recommend_sendback')
										|| fn:endsWith(i[12],'recommend_discuss')}">
									${i[3]}
								</c:when>
								<c:otherwise>${i[2]}</c:otherwise>
							</c:choose>							
							</td>
							<td style="max-width:400px;">
							${i[4]}
							</td>
						</tr>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:otherwise>
				</c:choose>
			</c:forEach>	
			<c:if test="${workflowstatus != 'COMPLETED' 
						or (workflowstatus == 'COMPLETED' 
						and (internalStatusType=='question_final_admission' or internalStatusType=='question_unstarred_final_admission'  or internalStatusType=='question_halfHourFromQuestion_final_admission'))}">
				<tr>
					<td>
						${userName}<br>
						${userGroupName}
					</td>
					<td>
						<select id="changeInternalStatus" class="sSelect">
							<c:forEach items="${internalStatuses}" var="i">
								<c:choose>
									<c:when test="${i.type=='question_system_groupchanged' }">
										<option value="${i.id}" style="display: none;"><c:out value="${i.name}"></c:out></option>	
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${i.id==internalStatus }">
												<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
											</c:when>
											<c:otherwise>
												<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</td>
					<td>
						<form:textarea path="remarks" rows="4" style="width: 250px;"></form:textarea>
					</td>
				</tr>
			</c:if>	
		</tbody>
	</table>
	<c:if test="${workflowstatus != 'COMPLETED'}">
		<p>
			 <a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
		</p>
	</c:if>
	
	<c:if test="${workflowstatus != 'COMPLETED' 
						or (workflowstatus == 'COMPLETED' 
						and (internalStatusType=='question_final_admission' 
							or internalStatusType=='question_unstarred_final_admission'
							or internalStatusType=='question_halfHourFromQuestion_final_admission'))}">
	<p>
	<c:if test="${selectedQuestionType == 'questions_shortnotice' and domain.dateOfAnsweringByMinister != null}">
		<p>
		<label class="small"><spring:message code="question.dateOfAnsweringByMinister" text="Answering Date"/></label>
		<form:input path="dateOfAnsweringByMinister" cssClass="datemask sText" readonly="true"/>
		<form:errors path="dateOfAnsweringByMinister" cssClass="validationError"/>
		</p>
	</c:if>
	
	<c:choose>
		<c:when test="${(not empty domain.answer) and (fn:contains(allowedDeviceTypesForAnswerRelatedDates, selectedQuestionType))}">
			<p>
				<label class="small"><spring:message code="question.answerRequestedDate" text="Answer Requested Date"/></label>
				<input id="answerRequestedDate" name="setAnswerRequestedDate" class="datetimemask sText" value="${formattedAnswerRequestedDate}"/>
			</p>
			<p>
				<label class="small"><spring:message code="question.answerReceivedDate" text="Answer Received Date"/></label>
				<input id="answerReceivedDate" name="setAnswerReceivedDate" class="datetimemask sText" value="${formattedAnswerReceivedDate}"/>
			</p>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty formattedAnswerRequestedDate}">
				<input type="hidden" id="answerRequestedDate" name="setAnswerRequestedDate" class="datetimemask sText" value="${formattedAnswerRequestedDate}"/>
			</c:if>
			<c:if test="${not empty formattedAnswerReceivedDate}">
				<input type="hidden" id="answerReceivedDate" name="setAnswerReceivedDate" class="datetimemask sText" value="${formattedAnswerReceivedDate}"/>
			</c:if>
		</c:otherwise>
	</c:choose>
	<c:if test="${(not empty domain.answer)}">
		<p>
			<label class="small"><spring:message code="question.answer" text="Answer"/></label>
			<form:textarea path="answer" cssClass="wysiwyg"/>
		</p>
		<c:if test="${selectedQuestionType=='questions_unstarred' and empty domain.parent and isRemovedFromYaadiDetails=='true'}">
			<p>
				<label class="small"><spring:message code="question.allowInYaadi" text="Allow In Yaadi?"/></label>
				<input class="sCheck" type="checkbox" id="isAllowedInYaadi" name="isAllowedInYaadi">
			</p>
		</c:if>
	</c:if>
		
	<select id="internalStatusMaster" style="display:none;">
	<c:forEach items="${internalStatuses}" var="i">
	<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="internalStatus" cssClass="validationError"/>	
	</p>
	
	<p id="actorDiv" style="display:none;">
		<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
		<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
		<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
	</p>		
	</c:if>
		
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	
	
	
	<c:if test="${!(empty domain.factualPosition)}">
		<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
		<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
		<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>	
	
	<c:if test="${!(empty domain.factualPositionFromMember)}">
		<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPositionFromMember" text="Factual Position From Member"/></label>
		<form:textarea path="factualPositionFromMember" cssClass="wysiwyg"></form:textarea>
		<form:errors path="factualPositionFromMember" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
	
	<c:choose>
		<c:when test="${workflowstatus!='COMPLETED' }">
			<div class="fields">
				<h2></h2>
				<p class="tright">		
				<c:if test="${bulkedit!='yes'}">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					
				</c:if>
				<c:if test="${bulkedit=='yes'}">
					<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
				</c:if>
				</p>
			</div>
		</c:when>
		<c:otherwise>
			<c:if test="${internalStatusType=='question_final_admission'
					|| internalStatusType=='question_unstarred_final_admission'
					|| internalStatusType=='question_halfHourFromQuestion_final_admission'}">
					<div class="fields">
				<h2></h2>
				<p class="tright">		
				<c:if test="${bulkedit!='yes'}">
					<input id="resubmit" type="submit" value="<spring:message code='generic.resubmit' text='Resubmit'/>" class="butDef">
				</c:if>
				<c:if test="${bulkedit=='yes'}">
					<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
				</c:if>
				</p>
			</div>			
			</c:if>
		</c:otherwise>
	</c:choose>
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<input type="hidden" name="originalSubDepartment" id="originalSubDepartment" value="${originalSubDepartment}">
	<input type="hidden" name="originalAnsweringDate" id="originalAnsweringDate" value="${originalAnsweringDate}">
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="bulkSubmitted"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="reasonForLateReply"/>
	<form:hidden path="submittedInBatch1"/>
	<input type="hidden" id="resendQuestionTextStatus" name="resendQuestionTextStatus" value="${resendQuestionTextStatus}" />
	<input type="hidden" id="sendHalfHourForDiscussionDate" name="sendHalfHourForDiscussionDate" value="${sendHalfHourForDiscussionDate}" />
	<c:if test="${domain.ballotStatus!=null}">
		<input type="hidden" name="ballotStatus" id="ballotStatusId" value="${ballotStatusId}"/>		
	</c:if>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">
	<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">	
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">			
	<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
		
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			<input type="hidden" name="halfHourDiscusionFromQuestionReferenceNumber" id="halfHourDiscusionFromQuestionReferenceNumber" value="${referredQuestionNumber}" />
			<input type="hidden" name="referenceDeviceType" id="referenceDeviceType" value="${domain.referenceDeviceType}"/>
			<input type="hidden" name="referenceDeviceMember" id="referenceDeviceMember" value="${domain.referenceDeviceMember}"/>
			<input type="hidden" name="referenceDeviceAnswerDate" id="referenceDeviceAnswerDate" value="${refDeviceAnswerDate}"/>
	</c:if>
	
	<input type="hidden" id="yaadiNumber" name="yaadiNumber" value="${domain.yaadiNumber}"/>
	<input type="hidden" id="yaadiLayingDate" name="yaadiLayingDate" value="${yaadiLayingDate}"/>
	<input type="hidden" id="resendQuestionTextStatus" name="resendQuestionTextStatus" value="${resendQuestionTextStatus}" />
	<input type="hidden" id="lastDateOfAnswerReceiving" name="setLastDateOfAnswerReceiving" class="datemask sText" value="${formattedLastAnswerReceivingDate}"/>
	<input type="hidden" id="answerReceivedMode" name="answerReceivedMode" value="${domain.answerReceivedMode}"/>
	<input type="hidden" id="submissionPriority" name="submissionPriority" value="${domain.submissionPriority}"/>
</form:form>
<input id="oldgroup" name="oldgroup" value="${group}" type="hidden">
<input id="formattedoldgroup" name="formattedoldgroup" value="${formattedGroup}" type="hidden">
<input id="originalLevel" name="originalLevel" value="${domain.level}" type="hidden">
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='question.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
<input id="oldRecommendationStatus" value="${ oldRecommendationStatus}" type="hidden">
<input id="selectedQuestionType" value="${selectedQuestionType}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input type="hidden" id="srole" value="${role}" />
<input id="departmentChangeRestrictedMessage" value="<spring:message code='question.departmentChangeRestrictedMessage' text='Department change not allowed at the moment!'/>" type="hidden">

<ul id="contextMenuItems" >
	<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
	<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
</ul>
</div>
</div>
</div>

<div id="backToQuestionDiv" style="display:none;">
<a href="#" id="backToQuestion"><spring:message code="question.backtoquestion" text="Back To Question"></spring:message></a>
</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<!--To show the questionTexts of the clubbed questions -->
<div id="clubbedQuestionTextsDiv">
	<h1>Assistant Question texts of clubbed questions</h1>
</div>
<div id="hideClubQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>

<!--To show the latest revised questionText of the clubbed questions -->
<div id="clubbedRevisedQuestionTextDiv">
	<h1>Latest Revised Question text of clubbed questions</h1>
</div>
<div id="hideClubRQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
<c:set var="revisedQuestionTextFromClubbedQuestionsEscapingDoubleQuote" value="${fn:replace(latestRevisedQuestionTextFromClubbedQuestions, '\"', '&#34;')}" />
<c:set var='revisedQuestionTextFromClubbedQuestionsEscapingSingleQuote' value='${fn:replace(revisedQuestionTextFromClubbedQuestionsEscapingDoubleQuote, "\'", "&#39;")}' />
<input type="hidden" id="latestRevisedQuestionTextFromClubbedQuestions" value="${revisedQuestionTextFromClubbedQuestionsEscapingSingleQuote}"/>

<div id="referencingresultDiv" style="display:none;">
</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="revisedDraftLevel" name="revisedDraftLevel" value="${revisedDraftInitialLevel}"/>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>"/>
</body>
</html>