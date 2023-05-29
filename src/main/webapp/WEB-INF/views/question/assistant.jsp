<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

	<script type="text/javascript">
	/**** detail of clubbed and refernced questions ****/		
	function viewQuestionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+$("#selectedQuestionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='question/'+id+'/edit?'+parameters;
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
		var params="id="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val();		
		$.get('clubentity/init?'+params,function(data){
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			if(data){
				$.unblockUI();
			}
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
		var params="id="+id
		+"&houseType="+$("#houseTypeType").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val()
        +"&deviceType="+$("#questionTypeType").val();
		$.get('refentity/init?'+params,function(data){
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
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+$("#selectedQuestionType").val()
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
		$('.tabContent').load(resourceURL,function(data){
			scrollTop();
			$.unblockUI();
		});
		$("#referencingResultDiv").hide();
		$("#clubbingResultDiv").hide();
		$("#assistantDiv").show();			
	}
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
			var params="question="+$("#id").val()+"&status="+value+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
			var resourceURL='ref/question/actors?'+params;
			var sendback = '';
			var discuss = '';
			var clubbingPostAdmission = '';
			var clubbingWithUnstarredFromPreviousSession = '';
			var unclubbing = '';
			var admitDueToReverseClubbing = '';
			var recommendRejection = '';
			var finalRejection = '';
			var questionSupplmentarySendToSectionOfficer = $("#internalStatusMaster option[value='question_processed_sendSupplementaryQuestionToSectionOfficer']").text();
			var recommendAnswerConfirmation = $("#internalStatusMaster option[value='question_unstarred_processed_recommendAnswerForConfirmation']").text();
		    var deviceTypeType = $('#questionTypeType').val();
		    
		    if(deviceTypeType == 'questions_starred'){
				sendback = $("#internalStatusMaster option[value='question_recommend_sendback']").text();			
				discuss = $("#internalStatusMaster option[value='question_recommend_discuss']").text();		
				clubbingPostAdmission = $("#internalStatusMaster option[value='question_recommend_clubbingPostAdmission']").text();
				clubbingWithUnstarredFromPreviousSession = $("#internalStatusMaster option[value='question_recommend_clubbingWithUnstarredFromPreviousSession']").text();
				unclubbing = $("#internalStatusMaster option[value='question_recommend_unclubbing']").text();
				admitDueToReverseClubbing = $("#internalStatusMaster option[value='question_recommend_admitDueToReverseClubbing']").text();
				recommendRejection = $("#internalStatusMaster option[value='question_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_final_rejection']").text();				
			
		    }else if(deviceTypeType == 'questions_unstarred') {
				sendback = $("#internalStatusMaster option[value='question_unstarred_recommend_sendback']").text();			
				discuss = $("#internalStatusMaster option[value='question_unstarred_recommend_discuss']").text();		
				clubbingPostAdmission = $("#internalStatusMaster option[value='question_unstarred_recommend_clubbingPostAdmission']").text();
				clubbingWithUnstarredFromPreviousSession = $("#internalStatusMaster option[value='question_unstarred_recommend_clubbingWithUnstarredFromPreviousSession']").text();
				unclubbing = $("#internalStatusMaster option[value='question_unstarred_recommend_unclubbing']").text();
				admitDueToReverseClubbing = $("#internalStatusMaster option[value='question_unstarred_recommend_admitDueToReverseClubbing']").text();	
				recommendRejection = $("#internalStatusMaster option[value='question_unstarred_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_unstarred_final_rejection']").text();				
			
			}else if(deviceTypeType == 'questions_shortnotice') {
				sendback = $("#internalStatusMaster option[value='question_shortnotice_recommend_sendback']").text();			
				discuss = $("#internalStatusMaster option[value='question_shortnotice_recommend_discuss']").text();		
				clubbingPostAdmission = $("#internalStatusMaster option[value='question_shortnotice_recommend_clubbingPostAdmission']").text();
				clubbingWithUnstarredFromPreviousSession = $("#internalStatusMaster option[value='question_shortnotice_recommend_clubbingWithUnstarredFromPreviousSession']").text();
				unclubbing = $("#internalStatusMaster option[value='question_shortnotice_recommend_unclubbing']").text();
				admitDueToReverseClubbing = $("#internalStatusMaster option[value='question_shortnotice_recommend_admitDueToReverseClubbing']").text();
				recommendRejection = $("#internalStatusMaster option[value='question_shortnotice_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_shortnotice_final_rejection']").text();
			
			}else if(deviceTypeType == 'questions_halfhourdiscussion_from_question') {
				sendback = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_sendback']").text();			
				discuss = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_discuss']").text();		
				clubbingPostAdmission = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_clubbingPostAdmission']").text();
				unclubbing = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_unclubbing']").text();
				admitDueToReverseClubbing = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_admitDueToReverseClubbing']").text();
				recommendRejection = $("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_rejection']").text();
				finalRejection = $("#internalStatusMaster option[value='question_halfHourFromQuestion_final_rejection']").text();
			}
		    
		     
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					 var actor1="";
					 var actCount = 1;
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						if(data[i].state!="active"){
							text+="<option value='"+data[i].id+"' disabled='disabled'>"+data[i].name+"</option>";
						}else{
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
							if(actCount == 1){
								actor1=data[i].id;
								actCount++;
							}
						}
					}
					$("#actor").html(text);
					$("#actorDiv").show();				
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback && value!=discuss
							&& value!=clubbingPostAdmission 
							&& value!=clubbingWithUnstarredFromPreviousSession
							&& value!=unclubbing
							&& value!=admitDueToReverseClubbing
							&& value!=questionSupplmentarySendToSectionOfficer
							&& value!=recommendAnswerConfirmation){
						$("#internalStatus").val(value);
					}
					$("#recommendationStatus").val(value);			
					/**** setting level,localizedActorName ****/
					 var temp=actor1.split("#");
					 $("#level").val(temp[2]);		    
					 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
					 $('#actorName').val(temp[4]);
					 $('#actorName').css('display','inline');
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback && value!=discuss
							&& value!=clubbingPostAdmission 
							&& value!=clubbingWithUnstarredFromPreviousSession
							&& value!=unclubbing
							&& value!=admitDueToReverseClubbing
							&& value!=questionSupplmentarySendToSectionOfficer){
						$("#internalStatus").val(value);
					}
					$("#recommendationStatus").val(value);
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
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
		
		 var valueType = '';
		 if(value!=sendback && value!=discuss
					&& value!=clubbingPostAdmission 
					&& value!=clubbingWithUnstarredFromPreviousSession
					&& value!=unclubbing
					&& value!=admitDueToReverseClubbing
					&& value!=questionSupplmentarySendToSectionOfficer){
				valueType = value;
			}else{
				valueType = $("#internalStatus").val();
			}
			if(valueType == recommendRejection || (valueType!='' && valueType == finalRejection)){
		    	if($("#copyOfRejectionReason").val()!=''){
		    		$("#rejectionReason").val($("#copyOfRejectionReason").val());
		    	}
		    	$("#rejectionReasonP").css("display","block");
		    }else{
		    	$("#rejectionReasonP").css("display","none");
		    	$("#copyOfRejectionReason").val($("#rejectionReason").val());
		    	$("#rejectionReason").val("");
		   }
		
	}
	/**** group changed ****/
	function groupChanged(){
		var newgroup=$("#group").val();
		if(newgroup==''){
		    var groupChanged=$("#internalStatusMaster option[value='question_system_groupchanged']").text();			
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
			    var groupChanged=$("#internalStatusMaster option[value='question_system_groupchanged']").text();
			    $("#changeInternalStatus").val(newStatus);
			    $("#changeInternalStatus option").hide();			    
			    $("#changeInternalStatus option[value=']"+groupChanged+"'").show();
			    $("#internalStatus").val(groupChanged);
			    $("#recommendationStatus").val(groupChanged);
		    }else{
		    	var groupChanged=$("#internalStatusMaster option[value='question_system_groupchanged']").text();
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
		
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
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
		$.unblockUI();
	}
	
    /**** groups ****/
	function loadGroup(subdepartment){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(subdepartment!=''){
			$.get('ref/subdepartment/' + subdepartment + '/group?'+
					'session=' + $("#session").val(),function(data){
				$("#formattedGroup").val(data.name);
				$("#group").val(data.id);			
				//loadAnsweringDates(data.id);			
			}).fail(function(){
				if($("#ErrorMsg").val() != ''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}else{
			$("#formattedGroup").val("");
			$("#group").val("");
		}
		$.unblockUI();
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
	
	function removeFormattingFromDetails(callBack){		
		var detailsBox=$('textarea#questionText');
		if(detailsBox!==undefined && detailsBox!==null){				
			var motionDetailText=$.wysiwyg.getContent(detailsBox);			
			if(motionDetailText!==undefined && motionDetailText!==null && motionDetailText!==''){
				cleanText=cleanFormatting(motionDetailText);
				$.wysiwyg.setContent(detailsBox,cleanText);				
			}
		}
		
		callBack();
	}
	
	$(document).ready(function(){
		/** Warn if revised question text of parent is changed while its child pending for clubbing approval has some modifications in the same **/
		var isPendingClubbedQuestionSearched = false;
	    var isPendingClubbedQuestionFound = false;
	    var isRevisedQuestionTextDisallowedToEdit = false;
	    var revisedQuestionTextOriginal = $('#revisedQuestionText').val();
	    var clubbedQuestionNumbers = "";
		if($('#revisedQuestionText').val()!=undefined && $('#revisedQuestionText').val()!="" && $('#revisedQuestionText').val()!="<p></p>") {
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
		}
		
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
			//$("#backToQuestionDiv").hide();
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
						$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
						$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
						$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
						//groupChanged();					
					}
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
					//find if question text by member has invalid formatting text and notify the clerk/assistant
					if($('#questionText').val().toLowerCase().indexOf("mso") >= 0 || $('#questionText').val().toLowerCase().indexOf("w:") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;					
						
					} else if($('#questionText').val().toLowerCase().indexOf("o:p") >= 0){
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());	
						return false;
						
					} if($('#questionText').val().toLowerCase().indexOf("ol style=") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());	
						return false;
					
					} else if($('#questionText').val().toLowerCase().indexOf("&lt;ol&gt;&lt;/ol&gt;") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());	
						return false;
					
					} else if($('#questionText').val().toLowerCase().indexOf("<ol></ol>") >= 0){
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());	
						return false;
					
					} else if($('#questionText').val().toLowerCase().indexOf("br style=") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;
					
					} else if($('#questionText').val().toLowerCase().indexOf("-webkit-text-stroke-widt") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;
						
					} else if($('#questionText').val().toLowerCase().indexOf("font-size: small") >= 0){	
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;
						
					} else {
						removeFormattingFromDetails(function() {});
						$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val()); //as question text is valid formatted
						
					}					
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
		    var supportingMembers=$("select[name='selectedSupportingMembers']").val();
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
		    	loadActors(value);	
			    $("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");		    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#startworkflow").attr("disabled","disabled");
			    $("#submit").removeAttr("disabled");
			}		    
	    });
	    /**** On page Load ****/
	    $("#startworkflow").attr("disabled","disabled");
		$("#submit").removeAttr("disabled");
	    /**** Put Up ****/
		$("#startworkflow").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			
			// For Client side validation of Revised QuestionText, Revised Subject and Rejection Reason
			// Added by Anand
			 var startworkflowAllowed = true;
			 var recommendRejection = $("#internalStatusMaster option[value='question_recommend_rejection']").text();
			 if($("#questionTypeType").val()=="questions_starred"
					|| $("questionTypeType").val()=="questions_unstarred"){
				if($("#revisedSubject").val()==null || $("#revisedSubject").val()==""){
					startworkflowAllowed = false;
					$.prompt("Revised Subject Empty");
				}else if($("#revisedQuestionText").val()==null || $("#revisedQuestionText").val()==""){
					startworkflowAllowed = false
					$.prompt("Revised QuestionText Empty");
				}else if(recommendRejection == $("#internalStatus").val()){
					if($("#rejectionReason").val()==null || $("#rejectionReason").val()==""){
						startworkflowAllowed = false;
						$.prompt("Rejection Reason Empty");
					}
				}
				
			}
			 
			 if(startworkflowAllowed){
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
				    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				    					}
				    					scrollTop();
				    				});
		    	            }
					}});
			 }
	        return false;  
	    });
	    /**** On Update Validations If Any ****/
		$("#submit").click(function(e){
			if($('#questionTypeType').val()=='questions_unstarred') {
				var answer = $('#answer').val();
				if(answer!=undefined && answer!='' && answer!='-' 
						&& answer!='<p>-</p>' && answer!='<p></p>' && answer!='<p></p>-' && answer!='-<p></p>'
						&& answer!='<br><p></p>' && answer!='<p><br></p>' && answer!='-<br><p></p>'
						&& answer!='<p>-<br></p>' && answer!='<p><br>-</p>') {
					if($('#answerRequestedDate').val()=='') {
						$.prompt("Please update answer requested date for the question!");
						return false;
					} else {
						if($('#answerReceivedDate').val()=='') {
							$.prompt("Please update answer received date for the question!");
							return false;
						}
					}
				}
			}			
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
			var parent = $("#key").val();
			if(parent==undefined || parent==''){
				parent = ($("#id").val()!=undefined && $("#id").val()!='')? $("#id").val():"";
			}
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
		$(".clubbedRefQuestions").contextMenu({menu: 'contextMenuItems'},
	        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='unclubbing'){
					if(id.indexOf("cq")!=-1){
						var questionId=$("#id").val();
						var clubId=id.split("cq")[1];		
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId+"&whichDevice=questions_"+"&usergroupType="+$("#currentusergroupType").val(),function(data){
								if(data=='SUCCESS' || data=='UNCLUBBING_SUCCESS'){
									$.prompt("Unclubbing Successful");
								}else{
									$.prompt("Unclubbing Failed");
								}		
								
								$.unblockUI();
							},'html').fail(function(){
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
								}
								$.unblockUI();
								scrollTop();
						});	
					}else{
						$.prompt("Unclubbing not allowed");
					}			
				}else if(action=='dereferencing'){
					if(id.indexOf("rq")!=-1){					
						var questionId=$("#id").val();
						var refId=id.split("rq")[1];	
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post('refentity/dereferencing?pId='+questionId+"&rId="+refId+"&device=" + $("#questionTypeType").val(),function(data){
							if(data=='SUCCESS'){
								$.prompt("Dereferencing Successful");
								/* $.prompt("Dereferencing Successful",{
									buttons: {Ok:true, Cancel:false}, callback: function(v){
								        refreshEdit($("#id").val());
									}
								}); */
							}else{
								$.prompt("Dereferencing Failed");
							}
							$.unblockUI();
							
							refreshEdit($("#id").val());
						},'html').fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							$.blockUI();
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
		if($("#departmentSelected").val()==''){
			$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
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
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
		if(1==1){
		    if($("#revisedQuestionText").val()!=''){
		    	$("#revisedQuestionTextDiv").show();
		    }
		}
	    if($("#revisedReason").val()!=''){
		    $("#revisedReasonDiv").show();
	    }
	    if($("#revisedBriefExplanation").val()!=''){
	    	$("#revisedBriefExplanationDiv").show();
	    }
	    
	    $("#remarks").change(function(){
	    	if($(this).val()!=''){
	    		var recommendRejection = $("#internalStatusMaster option[value='question_recommend_rejection']").text();
			   	if(recommendRejection == $("#internalStatus").val()){
			   		//temporary remove found tags.. later to be replaced with method that removes entire html formatting
			   		var rejectionReason = $(this).val().replace("<br><p></p>","");
			   		rejectionReason = rejectionReason.replace("<p></p>","");
			   		rejectionReason = rejectionReason.replace('<div align="justify">',"");
			   		rejectionReason = rejectionReason.replace('<div align="left">',"");
			   		rejectionReason = rejectionReason.replace("</div>","");			   		
			   		$("#rejectionReason").val(rejectionReason.trim());
			    }
	    	}
	    	
	    });
	    
	  //--------------vikas dhananjay 20012013--------------------------
		//for viewing the refernced question
		$('#halfhourdiscussion_referred_question').click(function(){
			
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			if(questionNumber!=""){
				var sessionId = '${session}';
				var locale='${domain.locale}';
				var url = 'ref/questionid?strQuestionNumber=' + questionNumber
						+ '&strSessionId=' + sessionId 
						+ '&deviceTypeId=' + deviceTypeTemp 
						+'&locale=' + locale 
						+'&view=view';
				$.get(url, function(data) {
					if(data.id == 0){
						$.prompt('No question found.');
					}else if(data.id == -1){
						$.prompt('Please provide valid question number.');
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid=' + data.id 
								+ '&questionType=' + deviceTypeTemp ,function(data){
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
	  
	  
	  /*****AutoSuggest Multiple for supporting members******/
		
		var controlName=$(".autosuggestmultiple").attr("id");
		$("select[name='"+controlName+"']").hide();	
		$( ".autosuggestmultiple" ).change(function(){
			//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
			//for this we iterate through the slect box selected value and check if that value is present in the 
			//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
			//then that value will be removed from the select box.
			var value=$(this).val();
			$("select[name='"+controlName+"'] option:selected").each(function(){
				var optionClass=$(this).attr("class");
				if(value.indexOf(optionClass)==-1){
					$("select[name='"+controlName+"'] option[class='"+optionClass+"']").remove();
				}		
			});	
			$("select[name='"+controlName+"']").hide();				
		});
		//http://api.jqueryui.com/autocomplete/#event-select
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:3,
			source: function( request, response ) {
				$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()
						+'&primaryMemberId='+$('#primaryMember').val(), {
					term: extractLast( request.term )
				}, response ).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			},			
			search: function() {
				var term = extractLast( this.value );
				if ( term.length < 2 ) {
					return false;
				}
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				//what happens when we are selecting a value from drop down
				var terms = $(this).val().split(",");
				//if select box is already present i.e atleast one option is already added
				if($("select[name='"+controlName+"']").length>0){
					if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
					//if option being selected is already present then do nothing
					this.value = $(this).val();					
					$("select[name='"+controlName+"']").hide();						
					}else{
					//if option is not present then add it in select box and autocompletebox
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
					$("select[name='"+controlName+"']").append(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}							
					$("select[name='"+controlName+"']").hide();								
					}
				}else{
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
					textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
					text=text+textoption+"</select>";
					$(this).after(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}	
					$("select[name='"+controlName+"']").hide();									
				}		
				return false;
			}
		});
		
		$("#printIt").click(function(){
			
			var myWindow = window.open('question/'+$("#id").val()+'/edit?editPrint=true&usergroup=' + 
						$("#currentusergroup").val() + '&usergroupType=' + $("#currentusergroupType").val(),
						'_blank','width=700,height=768,scrollbars=1,menubar=yes');
			myWindow.print();
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
		
		$('#isAllowedInYaadi').click(function() {
			if($(this).is(':checked')) {
				$(this).val("true");
			} else {
				$(this).val("false");
			}
			alert("isAllowedInYaadi: " + $(this).val());
		});
	});
	
  	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}
	
	function dereferencingInt(referId){
		var device=$("#questionType").val();
		var deviceId=$("#id").val();		
		$.post('refentity/dereferencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
			if(data=='SUCCESS'){
				$("#referencingResult").empty();
				$("#referencingResult").html(data);
				$("#operation"+referId).empty();
				$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
				}else{
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
				}				
		},'html').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
		return false;
	}	
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
        #clubbedQuestionTextsDiv, #clubbedRevisedQuestionTextDiv {
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${error!=''}">
	<h3 style="color: #FF0000;">${error}</h3>
</c:if>
<div class="fields clearfix watermark">
<a id="printIt" href="javascript:void(0);"><spring:message code="generic.print" text="Print" /></a>
<div id="reportDiv">
	<div id="assistantDiv">
	<form:form action="question" method="PUT" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<h2>
			${formattedQuestionType}: ${formattedNumber}
			<c:choose>
				<c:when test="${not empty yaadiDetailsText}">
					&nbsp;&nbsp;(${yaadiDetailsText})
				</c:when>
				<c:when test="${not empty discussionDetailsText}">
					&nbsp;&nbsp;(${discussionDetailsText})
				</c:when>
				<c:when test="${not empty previousSessionUnstarredParentDetailsText}">
					&nbsp;&nbsp;(${previousSessionUnstarredParentDetailsText})
				</c:when>
				<c:otherwise></c:otherwise>
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
				<c:when test="${fn:contains(selectedQuestionType,'questions_halfhourdiscussion')}">
					<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
				</c:when>
				<c:otherwise>
					<label class="small"><spring:message code="question.number" text="Motion Number"/>*</label>
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
		
		<c:choose>
			<c:when test="${selectedQuestionType=='questions_starred'}">
				<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
				<c:if test="${processingMode.equalsIgnoreCase('upperhouse') && isFirstBatchQuestion.equalsIgnoreCase('YES')}"><c:set var="hideDRDAnsweringDate" value="true"/></c:if>
				
				<select name="answeringDate" id="answeringDate" class="sSelect">
				
					<c:forEach items="${answeringDates }" var="i">
						<c:choose>
							<c:when test="${i.id==answeringDate }">
								<option value="${i.id }" selected="selected">${i.name}</option>
							</c:when>
							<c:otherwise>
								<c:if test="${hideDRDAnsweringDate}">
								<option value="${i.id }" disabled>${i.name}</option>
								</c:if>
								<c:if test="${hideDRDAnsweringDate=='' || !hideDRDAnsweringDate}">
									<option value="${i.id }">${i.name}</option>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</c:when>
		</c:choose>
			
		<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<select name="discussionDate" id="discussionDate" class="sSelect">
				<option value="">--<spring:message code="please.select" text="Select"/>--</option>
				<c:forEach items="${discussionDates}" var="i">
					<c:choose>
						<c:when test="${i.value==discussionDateSelected }">
							<option value="${i.value}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.value}" >${i.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<form:errors path="discussionDate" cssClass="validationError"/>
			
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
			<c:when test="${selectedQuestionType == 'questions_unstarred'}">
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
		<select name="ministry" id="ministry" class="sSelect" style="width: 270px;">
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
		<c:choose>	
			<c:when test="${not (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='lowerhouse')}">
				<label class="small"><spring:message code="question.group" text="Group"/>*</label>
				<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
				<input type="hidden" id="group" name="group" value="${group }">
				<form:errors path="group" cssClass="validationError"/>
			</c:when>	
		</c:choose>
		</p>	
		
		<p>
			<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
			<select name="subDepartment" id="subDepartment" class="sSelect" style="width: 270px;">
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
			<c:choose>
				<c:when test="${usergroupType =='clerk' || usergroupType =='assistant'}">
					<label class="small"><spring:message code="question.processed" text="Processed By Clerk?"/></label>
					<form:checkbox path="processed"/>
				</c:when>
				<c:otherwise>
					<form:hidden path="processed"/>
				</c:otherwise>
			</c:choose>
			<form:errors path="processed" cssClass="validationError"/>	
		</p>	
			
		
		<p>
		<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
		<textarea class="autosuggestmultiple" id="selectedSupportingMembers" rows="2" cols="50">${memberNames}</textarea>
		<c:if test="${!(empty primaryMember)}">
			<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
			<select  name="selectedSupportingMembers" multiple="multiple" style="display: none;">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
			</c:forEach>		
			</select>
		</c:if>	
		</p>
		
		<p>
			<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText">
			<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="./resources/images/contactus.jpg" width="40" height="25"></a>		
		</p>		
		
		<c:set var="isClubbingReferencingAllowed" value="yes"/>		
		<c:if test="${internalStatusType=='question_submit' 
						or internalStatusType=='question_ustarred_submit' 
						or internalStatusType=='question_shortnotice_submit'
						or internalStatusType=='question_halfHourFromQuestion_submit' 
						or (selectedQuestionType=='questions_starred' && internalStatusType=='question_system_assistantprocessed')}">
			<c:set var="isClubbingReferencingAllowed" value="no"/>
		</c:if>
		<c:choose>
		<c:when test="${isClubbingReferencingAllowed=='yes'}">
		<p>
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="question.clubbing" text="Clubbing"></spring:message></a>
				<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="question.referencing" text="Referencing"></spring:message></a>
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
							<a href="#" id="cq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 14px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
						<a href="javascript:void(0);" id="viewClubbedQuestionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
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
		</c:when>
		<c:otherwise>
			<input type="hidden" id="parent" name="parent" value="${parent}">
			<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
				<c:forEach items="${clubbedQuestions }" var="i">
					<option value="${i.id}" selected="selected"></option>
				</c:forEach>
			</select>
			<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
				<c:forEach items="${referencedQuestions }" var="i">
					<option value="${i.id}" selected="selected"></option>
				</c:forEach>
			</select>
		</c:otherwise>
		</c:choose>			
		
		<p>	
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>			
			<c:choose>
				<c:when test="${houseTypeType=='upperhouse'}">
					<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				</c:when>
				<c:otherwise>
					<form:textarea path="subject" rows="2" cols="50" readonly="true"></form:textarea>
				</c:otherwise>
			</c:choose>
			<form:errors path="subject" cssClass="validationError"/>	
		</p>
	
		
		<p>
			<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
			<c:choose>
				<c:when test="${houseTypeType=='upperhouse'}">
					<form:textarea path="questionText" cssClass="wysiwyg"></form:textarea>
				</c:when>
				<c:otherwise>
					<form:textarea path="questionText" cssClass="wysiwyg" readonly="true"></form:textarea>
				</c:otherwise>
			</c:choose>			
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
					</c:when>
					<c:otherwise>
						<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
					</c:otherwise>
				</c:choose>
				<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
				<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>	
		
		<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
				<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
				<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>
		</c:if>
		
		<p>
			<c:if test="${selectedQuestionType!='questions_halfhourdiscussion_from_question'}">
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
				<a href="#" id="reviseQuestionText" style="margin-right: 20px;"><spring:message code="question.reviseQuestionText" text="Revise Question"></spring:message></a>
			</c:if>
			
			<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
				<c:choose>
					<c:when test="${(selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='upperhouse')}">
						<a href="#" id="reviseReason" style="margin-left: 10px;"><spring:message code="question.reviseReason" text="Revise Reason"></spring:message></a>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${selectedQuestionType=='questions_shortnotice'}">
								<a href="#" id="reviseReason" style="margin-right: 20px;"><spring:message code="question.revise.shortnotice.reason" text="Revise Reason"></spring:message></a>
							</c:when>
							<c:otherwise>
								<a href="#" id="reviseReason" style="margin-left: 162px;"><spring:message code="question.revise.halfhour.reason" text="Revise Reason"></spring:message></a>
							</c:otherwise>
						</c:choose>					
					</c:otherwise>
				</c:choose>		
				<c:if test="${selectedQuestionType!='questions_shortnotice'}">	
					<a href="#" id="reviseBriefExplanation" style="margin: 0px 20px 10px 10px;"><spring:message code="question.reviseBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
				</c:if>
			</c:if>
			<a href="#" id="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
			<br />
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
		
		<c:set var="isAllowedToPutupQuestionForApproval" value="NO"/>
		<c:choose>
		<c:when test="${((internalStatusType=='question_system_putup' ||internalStatusType=='question_putup_nameclubbing'
						|| internalStatusType == 'question_putup_rejection' ||internalStatusType=='question_putup_convertToUnstarredAndAdmit'
						|| internalStatusType == 'question_putup_clubbing' || internalStatusType == 'question_putup_nameclubbing' 
						|| recommendationStatusType == 'question_putup_clubbingPostAdmission' || recommendationStatusType == 'question_putup_clubbingWithUnstarredFromPreviousSession'
						|| recommendationStatusType == 'question_putup_unclubbing' || recommendationStatusType == 'question_putup_admitDueToReverseClubbing'
						|| internalStatusType == 'question_unstarred_system_assistantprocessed'
						|| internalStatusType == 'question_unstarred_putup_clubbing' || internalStatusType == 'question_unstarred_putup_nameclubbing'
						|| recommendationStatusType == 'question_unstarred_putup_clubbingPostAdmission' || recommendationStatusType == 'question_unstarred_putup_clubbingWithUnstarredFromPreviousSession'
						|| recommendationStatusType == 'question_unstarred_putup_unclubbing' || recommendationStatusType == 'question_unstarred_putup_admitDueToReverseClubbing'
						|| internalStatusType == 'question_shortnotice_system_assistantprocessed'
						|| internalStatusType == 'question_shortnotice_putup_clubbing' || internalStatusType == 'question_shortnotice_putup_nameclubbing' 
						|| recommendationStatusType == 'question_shortnotice_putup_clubbingPostAdmission' || recommendationStatusType == 'question_shortnotice_putup_clubbingWithUnstarredFromPreviousSession'
						|| recommendationStatusType == 'question_shortnotice_putup_unclubbing' || recommendationStatusType == 'question_shortnotice_putup_admitDueToReverseClubbing'
						|| internalStatusType == 'question_halfHourFromQuestion_system_assistantprocessed' 
						|| internalStatusType == 'question_halfHourFromQuestion_putup_clubbing' || internalStatusType == 'question_halfHourFromQuestion_putup_nameclubbing' 
						|| recommendationStatusType == 'question_halfHourFromQuestion_putup_clubbingPostAdmission' || recommendationStatusType == 'question_halfHourFromQuestion_putup_unclubbing' || recommendationStatusType == 'question_halfHourFromQuestion_putup_admitDueToReverseClubbing')	
				&& (recommendationStatusType ne 'question_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_final_clubbingWithUnstarredFromPreviousSession'
						&& recommendationStatusType ne 'question_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_final_reject_clubbingWithUnstarredFromPreviousSession'
						&& recommendationStatusType ne 'question_unstarred_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_unstarred_final_clubbingWithUnstarredFromPreviousSession'
						&& recommendationStatusType ne 'question_unstarred_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_unstarred_final_reject_clubbingWithUnstarredFromPreviousSession'
						&& recommendationStatusType ne 'question_shortnotice_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_shortnotice_final_clubbingWithUnstarredFromPreviousSession'
						&& recommendationStatusType ne 'question_shortnotice_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_shortnotice_final_reject_clubbingWithUnstarredFromPreviousSession'))
				|| (internalStatusType=='question_final_admission' && recommendationStatusType=='question_final_admission' && parent ne '')
		}">
			<security:authorize access="hasAnyRole('QIS_ASSISTANT')">
				<c:set var="isAllowedToPutupQuestionForApproval" value="YES"/>
			</security:authorize>		
		</c:when>
		<c:when test="${(internalStatusType=='question_unstarred_final_admission' 
						&& recommendationStatusType=='question_unstarred_processed_answerReceived'
						&& empty parent)}">
			<security:authorize access="hasAnyRole('QIS_CLERK')">
				<c:set var="isAllowedToPutupQuestionForApproval" value="YES"/>
			</security:authorize>
		</c:when>
		</c:choose>
		<c:if test="${isAllowedToPutupQuestionForApproval=='YES'}">
			<p>
				<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
				<select id="changeInternalStatus" class="sSelect">
				<option value="-"><spring:message code='please.select' text='Please Select'/></option>
				<c:forEach items="${internalStatuses}" var="i">
					<c:choose>
						<c:when test="${i.id==internalStatusSelected }">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
				
				<select id="internalStatusMaster" style="display:none;">
				<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
				</c:forEach>
				</select>	
				<form:errors path="internalStatus" cssClass="validationError"/>	
			</p>

			<p id="actorDiv" style="display: none;">
				<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
				<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }" />
				<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
			</p>
		</c:if>
			
		<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
		<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
		<c:if test="${fn:contains(internalStatusType, 'question_final')||fn:contains(internalStatusType, 'question_unstarred_final')}">
			<form:hidden path="actor"/>
		</c:if>
		
		<c:if test="${!(empty domain.factualPosition)}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
				<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:if>
		
		<c:if test="${!(empty domain.factualPositionFromMember)}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.factualPositionFromMember" text="Factual Position from Member"/></label>
				<form:textarea path="factualPositionFromMember" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:if>
		
		<c:if test="${fn:contains(internalStatusType, 'question_final_clarificationNeededFromDepartment') || fn:contains(internalStatusType, 'question_unstarred_final_clarificationNeededFromDepartment')}">
			<p>
			<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
			<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>	
		
		<c:if test="${fn:contains(internalStatusType, 'question_final_clarificationNeededFromMember') || fn:contains(internalStatusType, 'question_unstarred_final_clarificationNeededFromMember')}">
			<p>
			<label class="wysiwyglabel"><spring:message code="question.factualPositioFromMember" text="Factual Position From Member"/></label>
			<form:textarea path="factualPositionFromMember" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPositionFromMember" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>	
		
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
				<p id="rejectionReasonP" style="display:none;">
					<%-- <label class="centerlabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
					<form:textarea path="rejectionReason" rows="2" cols="50"></form:textarea> --%>
					<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
					<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
				</p>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${selectedQuestionType=='questions_starred'
		 || selectedQuestionType=='questions_shortnotice'
		 || selectedQuestionType=='questions_unstarred'}">
			<%-- <c:choose>
				<c:when test="${houseTypeType=='upperhouse' && (domain.ballotStatus != null)}">
					<p>
					<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
					<form:textarea path="answer" cssClass="wysiwyg"></form:textarea>
					</p>
					<input type="hidden" id="ballotStatus" name="ballotStatus" value="${ballotStatusId}"/>
				</c:when>
				<c:when test="${houseTypeType=='lowerhouse' &&  (domain.ballotStatus != null)}">
					<p>
					<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
					<form:textarea path="answer" cssClass="wysiwyg"></form:textarea>
					</p>
					<input type="hidden" id="ballotStatus" name="ballotStatus" value="${ballotStatusId}"/>
				</c:when>
			</c:choose> --%>
			<c:if test="${fn:contains(internalStatusType, 'question_final_admission') || fn:contains(internalStatusType, 'question_unstarred_final_admission') || fn:contains(internalStatusType, 'question_shortnotice_final_admission')}">
				<p>
					<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
					<form:textarea path="answer" cssClass="wysiwyg"></form:textarea>
				</p>
				<c:if test="${selectedQuestionType=='questions_unstarred' and empty domain.parent and not empty domain.answer and isRemovedFromYaadiDetails=='true'}">
					<p>
						<label class="small"><spring:message code="question.allowInYaadi" text="Allow In Yaadi?"/></label>
						<input class="sCheck" type="checkbox" id="isAllowedInYaadi" name="isAllowedInYaadi">
					</p>
				</c:if>
			</c:if>
		</c:if>
		
		<c:choose>
			<c:when test="${(fn:contains(internalStatusType, 'final_admission')) and (fn:contains(allowedDeviceTypesForAnswerRelatedDates, selectedQuestionType))}">
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
		
		<p>
		<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
		</p>
		
		<p>
		<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		</p>	
		
		<div class="fields">
			<h2></h2>
			<p class="tright">
			<c:choose>
				<c:when test="${bulkedit!='yes'}">
					<c:if test="${((internalStatusType=='question_submit' || internalStatusType=='question_system_assistantprocessed' || internalStatusType=='question_system_putup'
									|| internalStatusType =='question_system_groupchanged' || internalStatusType=='question_putup_rejection' || internalStatusType=='question_putup_convertToUnstarredAndAdmit'
									|| internalStatusType == 'question_putup_clubbing' || internalStatusType == 'question_putup_nameclubbing' 
									|| recommendationStatusType == 'question_putup_clubbingPostAdmission' || recommendationStatusType == 'question_putup_clubbingWithUnstarredFromPreviousSession'
									|| recommendationStatusType == 'question_putup_unclubbing' || recommendationStatusType == 'question_putup_admitDueToReverseClubbing'
									|| internalStatusType =='question_unstarred_system_assistantprocessed' || internalStatusType == 'question_unstarred_system_groupchanged' || internalStatusType =='question_unstarred_submit'
									|| internalStatusType == 'question_unstarred_putup_clubbing' || internalStatusType == 'question_unstarred_putup_nameclubbing'
									|| recommendationStatusType == 'question_unstarred_putup_clubbingPostAdmission' || recommendationStatusType == 'question_unstarred_putup_clubbingWithUnstarredFromPreviousSession'
									|| recommendationStatusType == 'question_unstarred_putup_unclubbing' || recommendationStatusType == 'question_unstarred_putup_admitDueToReverseClubbing'
									|| internalStatusType =='question_shortnotice_system_assistantprocessed' || internalStatusType=='question_shortnotice_system_groupchanged'|| internalStatusType =='question_shortnotice_submit'
									|| internalStatusType == 'question_shortnotice_putup_clubbing' || internalStatusType == 'question_shortnotice_putup_nameclubbing' 
									|| recommendationStatusType == 'question_shortnotice_putup_clubbingPostAdmission' || recommendationStatusType == 'question_shortnotice_putup_clubbingWithUnstarredFromPreviousSession'
									|| recommendationStatusType == 'question_shortnotice_putup_unclubbing' || recommendationStatusType == 'question_shortnotice_putup_admitDueToReverseClubbing'
									|| internalStatusType =='question_halfHourFromQuestion_system_assistantprocessed' || internalStatusType=='question_halfHourFromQuestion_system_groupchanged' ||internalStatusType=='question_halfHourFromQuestion_submit'
									|| internalStatusType == 'question_halfHourFromQuestion_putup_clubbing' || internalStatusType == 'question_halfHourFromQuestion_putup_nameclubbing' 
									|| recommendationStatusType == 'question_halfHourFromQuestion_putup_clubbingPostAdmission' || recommendationStatusType == 'question_halfHourFromQuestion_putup_unclubbing' || recommendationStatusType == 'question_halfHourFromQuestion_putup_admitDueToReverseClubbing')
							&& (recommendationStatusType ne 'question_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_final_clubbingWithUnstarredFromPreviousSession'
									&& recommendationStatusType ne 'question_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_final_reject_clubbingWithUnstarredFromPreviousSession'
									&& recommendationStatusType ne 'question_unstarred_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_unstarred_final_clubbingWithUnstarredFromPreviousSession'
									&& recommendationStatusType ne 'question_unstarred_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_unstarred_final_reject_clubbingWithUnstarredFromPreviousSession'
									&& recommendationStatusType ne 'question_shortnotice_recommend_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_shortnotice_final_clubbingWithUnstarredFromPreviousSession'
									&& recommendationStatusType ne 'question_shortnotice_recommend_reject_clubbingWithUnstarredFromPreviousSession' && recommendationStatusType ne 'question_shortnotice_final_reject_clubbingWithUnstarredFromPreviousSession'))
						}">
						<security:authorize access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT')">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</security:authorize>
						<security:authorize access="hasAnyRole('QIS_ASSISTANT')">
							<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
						</security:authorize>					
					</c:if>
					<%--- Remove the Following if conditions after session... Hack given for the council branch  --%>
					
					<c:if test="${fn:contains(internalStatusType, 'question_final') || fn:contains(internalStatusType, 'question_unstarred_final') || fn:contains(internalStatusType, 'question_shortnotice_final') || fn:contains(internalStatusType, 'question_halfHourFromQuestion_final')}">
						<security:authorize access="hasAnyRole('QIS_CLERK','QIS_ASSISTANT')">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</security:authorize>
						<c:if test="${(internalStatusType=='question_final_admission' && recommendationStatusType=='question_final_admission' && parent ne '')}">
							<security:authorize access="hasAnyRole('QIS_ASSISTANT')">
								<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
							</security:authorize>
						</c:if>
						<c:if test="${(internalStatusType=='question_unstarred_final_admission' && recommendationStatusType=='question_unstarred_processed_answerReceived' && empty parent)}">
							<security:authorize access="hasAnyRole('QIS_CLERK')">
								<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
							</security:authorize>
						</c:if>					
					</c:if>
					
				</c:when>
				<c:otherwise>
					<c:if test="${bulkedit=='yes'}">
						<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
					</c:if>
				</c:otherwise>
			</c:choose>
		</p>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="locale"/>
		<form:hidden path="version"/>
		<form:hidden path="bulkSubmitted"/>
		<form:hidden path="workflowStarted"/>	
		<form:hidden path="endFlag"/>
		<form:hidden path="level"/>
		<form:hidden path="localizedActorName"/>
		<form:hidden path="workflowDetailsId"/>
		<form:hidden path="reasonForLateReply"/>
		<form:hidden path="questionsAskedInFactualPositionForMember"/>
		<form:hidden path="submittedInBatch1"/>
		<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
		<input type="hidden" name="status" id="status" value="${status }">
		<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
		<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
		<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
		<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
		<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="taskid" name="taskid" value="${taskid}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
		<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
		<input type="hidden" name="originalType" id="originalType" value="${originalType}">
		<input type="hidden" name="originalSubDepartment" id="originalSubDepartment" value="${originalSubDepartment}">
		<input type="hidden" name="originalAnsweringDate" id="originalAnsweringDate" value="${originalAnsweringDate}">
		<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
		<input id="questionType" name= "questionType" type="hidden" value="${questionType}" />
		
		<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
				<input type="hidden" name="halfHourDiscusionFromQuestionReferenceNumber" id="halfHourDiscusionFromQuestionReferenceNumber" value="${referredQuestionNumber}" />
				<input type="hidden" name="referenceDeviceType" id="referenceDeviceType" value="${domain.referenceDeviceType}"/>
				<input type="hidden" name="referenceDeviceMember" id="referenceDeviceMember" value="${domain.referenceDeviceMember}"/>
				<input type="hidden" name="referenceDeviceAnswerDate" id="referenceDeviceAnswerDate" value="${refDeviceAnswerDate}"/>
		</c:if>
		<c:if test="${domain.ballotStatus!=null}">
			<input type="hidden" name="ballotStatus" id="ballotStatusId" value="${domain.ballotStatus.id}"/>		
		</c:if>
		
		<input type="hidden" id="yaadiNumber" name="yaadiNumber" value="${domain.yaadiNumber}"/>
		<input type="hidden" id="yaadiLayingDate" name="yaadiLayingDate" value="${yaadiLayingDate}"/>
		<input type="hidden" id="answerReceivedMode" name="answerReceivedMode" value="${domain.answerReceivedMode}"/>
		<input id="ministrySelected" name="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" name="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="oldgroup" name="oldgroup" value="${group}" type="hidden">
		<input type="hidden" id="submissionPriority" name="submissionPriority" value="${domain.submissionPriority}"/>
		<input id="questionType" name= "questionType" type="hidden" value="${questionType}" />
	</form:form>
	
	<input id="formattedoldgroup" name="formattedoldgroup" value="${formattedGroup}" type="hidden">
	
	
	<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
	<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='question.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
	<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">
	<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
	<input id="originalLevel" value="${ domain.level}" type="hidden">
	<input id="oldRecommendationStatus" value="${ recommendationStatus}" type="hidden">
	<input id="questionTypeType" value="${selectedQuestionType}" type="hidden"/>
	<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
	<input type="hidden" id="departmentChangeRestricted" value="${departmentChangeRestricted}" />
	<input id="departmentChangeRestrictedMessage" value="<spring:message code='question.departmentChangeRestrictedMessage' text='Department change not allowed at the moment!'/>" type="hidden">

	
	<ul id="contextMenuItems" >
	<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
	<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
	</ul>
	</div>
</div>

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

<div id="referencingResultDiv" style="display:none;">
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="copyOfRejectionReason" name="copyOfRejectionReason"/>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>"/>
</body>
</html>