<%@ include file="/common/taglibs.jsp" %>
<%@ page import="java.util.Date;" %>
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
		var deviceId = $("#deviceTypeMasterIds option[value='"+$("#selectedDeviceType").val()+"']").text();
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+deviceId
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
		var params = "id=" + id
					+ "&usergroup=" + $("#currentusergroup").val()
			        + "&usergroupType=" + $("#currentusergroupType").val();		
		$.get('clubentity/init?' + params,function(data){
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
		var params = "id=" + id
		+ "&usergroup=" + $("#currentusergroup").val()
        + "&usergroupType=" + $("#currentusergroupType").val();
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
		//var valueToSend="";
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(value!='-'){	
			//console.log(value);
			var deviceTypeType = $('#selectedQuestionType').val();
			var sendback = '';
			var discuss = '';
			var sendToDeskOfficer = '';
			if(deviceTypeType == 'questions_starred') {
				sendback = 
					$("#internalStatusMaster option[value='question_recommend_sendback']").text();			
			    discuss = 
			    	$("#internalStatusMaster option[value='question_recommend_discuss']").text();	
			    sendToDeskOfficer = 
			    	$("#internalStatusMaster option[value='question_processed_sendToDeskOfficer']").text();
			} else if(deviceTypeType == 'questions_unstarred') {
				sendback = 
					$("#internalStatusMaster option[value='question_unstarred_recommend_sendback']").text();	
				discuss = 
			    	$("#internalStatusMaster option[value='question_unstarred_recommend_discuss']").text();
				sendToDeskOfficer = 
			    	$("#internalStatusMaster option[value='question_unstarred_processed_sendToDeskOfficer']").text();
			} else if(deviceTypeType == 'questions_shortnotice') {
				sendback = 
					$("#internalStatusMaster option[value='question_shortnotice_recommend_sendback']").text();			
			    discuss = 
			    	$("#internalStatusMaster option[value='question_shortnotice_recommend_discuss']").text();
			    sendToDeskOfficer = 
			    	$("#internalStatusMaster option[value='question_shortnotice_processed_sendToDeskOfficer']").text();
			} else if(deviceTypeType == 'questions_halfhourdiscussion_from_question') {
				sendback = 
					$("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_sendback']").text();			
			    discuss = 
			    	$("#internalStatusMaster option[value='question_halfHourFromQuestion_recommend_discuss']").text();
			    sendToDeskOfficer = 
			    	$("#internalStatusMaster option[value='question_halfHourFromQuestion_processed_sendToDeskOfficer']").text();
			}
			var valueToSend = "";
			var changedInternalStatus = $("#changeInternalStatus").val();
			if(changedInternalStatus == sendToDeskOfficer ) {
				valueToSend = $("#internalStatus").val();
			}else{
				valueToSend = value;
			} 
			var params="question=" + $("#id").val()
			+ "&status=" + valueToSend 
			+ "&usergroup=" + $("#usergroup").val()
			+ "&level=" + $("#originalLevel").val();
			var resourceURL='ref/question/actors?'+params;
			
			$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				
				 var actor1="";
				 var actCount = 1;
				 
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
					var act = data[i].id;
					if(value != sendToDeskOfficer){
						var ugtActor = data[i].id.split("#")
						var ugt = ugtActor[1];
						if(ugt!='member' && data[i].state!='active'){
							text += "<option value='" + data[i].id + "' disabled='disabled'>" + data[i].name +"("+ugtActor[4]+")"+ "</option>";
						}else{
							text += "<option value='" + data[i].id + "'>" + ugtActor[4]+ "</option>";	
							if(actCount == 1){
								actor1=data[i].id;
								actCount++;
							}
						}
					}else{
						if(act.indexOf("section_officer") < 0){
							var ugtActor = data[i].id.split("#")
							var ugt = ugtActor[1];
							if(ugt!='member' && data[i].state!='active'){
								text += "<option value='" + data[i].id + "' disabled='disabled'>" + data[i].name +"("+ugtActor[4]+")"+ "</option>";
							}else{
								text += "<option value='" + data[i].id + "'>" + ugtActor[4]+ "</option>";	
								if(actCount == 1){
									actor1=data[i].id;
									actCount++;
								}
							}
						}
					}					
				}
				$("#actor").html(text);
				//$("#actorDiv").hide();				
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != sendback && value != discuss && value != sendToDeskOfficer){
					$("#internalStatus").val(value);
				}
				$("#recommendationStatus").val(value);	
				/**** setting level,localizedActorName ****/
				 //var actor1 = data[0].id;
				 var temp = actor1.split("#");
				 $("#level").val(temp[2]);		    
				 $("#localizedActorName").val(temp[3] + "(" + temp[4] + ")");
				
			}else{
				$("#actor").empty();
				$("#actorDiv").hide();
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != sendback && value != discuss && value != sendToDeskOfficer){
					$("#internalStatus").val(value);
				}
			    $("#recommendationStatus").val(value);
			   
				}
			 $.unblockUI();
			
			});
			
		}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			//$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		    $.unblockUI();
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
	}		
    
	/**** Load Clarifications ****/
	function loadClarifications(){
		$.get('ref/clarifications',function(data){
			if(data.length > 0){
				var text = "";
				for( var i=0 ; i<data.length ; i++){
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
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
		/** Disable edit of revised question text of parent while its child pending for clubbing approval has some modifications in the same **/
		var isPendingClubbedQuestionSearched = false;
	    var isPendingClubbedQuestionFound = false;
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
			    	if(isPendingClubbedQuestionFound) {
			    		if($('#'+idval).val()!=revisedQuestionTextOriginal) {
		    				$('#'+idval+'-wysiwyg-iframe').contents().find('html').html(revisedQuestionTextOriginal);
		    			}
			    		$.prompt("Questions " + clubbedQuestionNumbers + " Pending in Clubbing Approval Flows");
			    		return false;
			    	}
			    	if($('#clubbedEntities option').length>0 && !isPendingClubbedQuestionSearched) {
			    		$.get('ref/question/'+$('#id').val()+'/is_clubbedquestion_pendingwith_updatedquestiontext', function(data) {
				    		if(data!=undefined && data.length>0) {
				    			clubbedQuestionNumbers = data;
				    			if($('#'+idval).val()!=revisedQuestionTextOriginal) {
				    				$('#'+idval+'-wysiwyg-iframe').contents().find('html').html(revisedQuestionTextOriginal);
				    			}
				    			isPendingClubbedQuestionFound = true;
				    			$.prompt("Questions " + clubbedQuestionNumbers + " Pending in Clubbing Flows");
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
		
		//loadActors($("#changeInternalStatus").val());
		$('#questionreferenceText-wysiwyg-iframe').css('max-height','50px');
		$('#remarks-wysiwyg-iframe').css('max-height','50px');

		$('#mlsBranchNotifiedOfTransfer').val(null);
		$('#transferToDepartmentAccepted').val(null);
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
			if($(this).val()!=''){
				$("#formattedGroup").val("");
				$("#group").val("");
				loadSubDepartments($(this).val());
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
		
		$('#subDepartment').change(function(){
			loadGroup($(this).val());
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
				
				
				var url = 'ref/questionid?strQuestionNumber=' + questionNumber 
						+ '&strSessionId=' + sessionId 
						+ '&deviceTypeId='+deviceTypeTemp
						+ '&locale=' + locale 
						+ '&view=view';
				
				//alert(url);
				
				$.get(url, function(data) {
					if(data.id==0){
						$.prompt('No question found.');
					}else if(data.id==-1){
						$.prompt('Please provide valid question number.');
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid='+data.id,function(data){
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
		//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide(); 
		//**** Load Actors On page Load ****/
		if($('#workflowstatus').val()!='COMPLETED'){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value$='"+statusType[statusType.length-1]+"']").text();
			$("#changeInternalStatus").val(id);
			$("#changeInternalStatus").change();
			//loadActors($("#changeInternalStatus").val());
			
		}
		
		
		$('#sendBack').click(function(){			
			var currTimeMillis = (new Date()).getTime();
			var goAhead = 'false';
			
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			goAhead='true';
			/* if((currTimeMillis <=  parseInt($("#sendBackTimeLimit").val())) && ($("#remarks").val()!='')){
				goAhead='true';
			} */
			if((goAhead=='true') || (goAhead=='false' && ($('#answer').val().replace(/<[^>]+>/g,"")!=''))){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post($('form').attr('action'),  
	    	            $("form").serialize(),
	    	            function(data){
	       					$('.tabContent').html(data);
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');
	       				 	$.unblockUI();	
	    	            }
				).fail(function(){
	    			$.unblockUI();
	    			if($("#ErrorMsg").val()!=''){
	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    			}else{
	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    			}
	    			scrollTop();
	    		});
			}else{
				$.prompt("Please provide remarks.");
			}
			
		});
		
		$('#submit').click(function(){
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"||wysiwygVal==$('#defaultAnswerMessage').val()){
					$(this).val("");
				}
			});
			var deviceTypeType = $('#selectedQuestionType').val();
			if($('#isTransferable')!=null && $('#isTransferable').is(':checked') 
					&& (deviceTypeType == 'questions_starred' || deviceTypeType == 'questions_unstarred')){
				var subdepartmentIdVal=0;
				var ministryIdVal=0;
				if($('#subDepartment')!==null && $('#subDepartment')!==undefined && $('#subDepartment').length>0)
					subdepartmentIdVal=$('#subDepartment').get(0)
				
				if($('#ministry')!==null && $('#ministry')!==undefined && $('#ministry').length>0)
					ministryIdVal=$('#ministry').get(0)
					
				if(subdepartmentIdVal.value >0 && ministryIdVal.value >0){}
				else {
					$.prompt($('#departmentTransferRestrictionMsg').val());
					return false;
				}
			}
						
			
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){				        	
							$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
							var url = $('form').attr('action')+'?operation=workflowsubmit';
							if($("#workflowstatus").val()=='COMPLETED'){
								url += '&reanswerstatus=reanswer';
							}
							$.post(url,  
				    	            $("form").serialize(),
				    	            function(data){
				       					$('.tabContent').html(data);
				       					$('html').animate({scrollTop:0}, 'slow');
				       				 	$('body').animate({scrollTop:0}, 'slow');	
				       				 	$.unblockUI();	
				    	            }
							).fail(function(){
		    	    			$.unblockUI();
		    	    			if($("#ErrorMsg").val()!=''){
		    	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    	    			}else{
		    	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    	    			}
		    	    			scrollTop();
		    	    		});
				        }
					}
				});
			
			return false;		
		});
		
		/**** To make the next task available ****/
		$("#reanswer_workflow").click(function() {
			resendAnswer();
		});
		
		$('#isTransferable').change(function() {
			var changeStatusDrp=$('#changeInternalStatus')!=null && $('#changeInternalStatus').length>0? $('#changeInternalStatus')[0]:{};
			var changeActorDrp=$('select#actor')!=null && $('select#actor').length>0 ?$('select#actor')[0]:{};
	        if ($(this).is(':checked')) {
	        	changeStatusDrp.disabled=true;
	        	changeActorDrp.disabled=true;
	        	$.ajax({
					method: "GET",
					url: "ref/isDepartmentChangeRestricted",
					data: { deviceId: $('#id').val(), usergroupType: $("#currentusergroupType").val() },
					async: false
				}).done(function( isDepartmentChangeRestricted ) {
					if(isDepartmentChangeRestricted!=undefined && isDepartmentChangeRestricted=="YES") {
				    	$.prompt($('#departmentChangeRestrictedMessage').val());
		        		$("#ministry option[selected!='selected']").hide();
			    		$("#subDepartment option[selected!='selected']").hide(); 
			    		$("#transferP").css("display","none");		    		
			    		$('#isTransferable').removeAttr('checked');
			    		return false;
				    } else { 
				    	if($("#houseTypeType").val()=='lowerhouse' &&
				    			$('#selectedQuestionType').val()=='questions_starred'
				    			&& $("#internalStatusType").val() == 'question_final_admission'){
				        	var currentDate = new Date();
				        	currentDate.setHours(0,0,0,0);
				        	var lastDepartmentChangeDate = new Date($("#lastDateForDepartmentChange").val());
				        	if(currentDate <= lastDepartmentChangeDate){
				        		$("#ministry option[selected!='selected']").show();
					    		$("#subDepartment option[selected!='selected']").show(); 
					    		$("#transferP").css("display","inline-block");
					    		$("#submit").css("display","none");
				        	}else{
				        		$.prompt($("#lateDepartmentChangeMessage").val());
				        		$("#submit").css("display","none");
				        	}
			        	}else{
			        		$("#ministry option[selected!='selected']").show();
				    		$("#subDepartment option[selected!='selected']").show(); 
				    		$("#transferP").css("display","inline-block");
				    		$("#submit").css("display","none");
			        	}
				    }
				});
	        }else{
	        	changeStatusDrp.disabled=false;
	        	changeActorDrp.disabled=false;
	        	$("#ministry option[selected!='selected']").hide();
	    		$("#subDepartment option[selected!='selected']").hide(); 
	    		$("#transferP").css("display","none");
	    		$("#submit").css("display","inline-block");
	        }
	    });
		
		$('#mlsBranchNotifiedOfTransfer').change(function() {
	        if ($(this).is(':checked') && $("#isTransferable").is(':checked')) {
	        	$("#submit").css("display","inline-block");
	        }else{
	        	$("#submit").css("display","none");
	        }
	    });
		
/* 		$('#answer').wysiwyg({
			initialContent:$('#defaultAnswerMessage').val(),
			events : {
				focus:function(e){
					if($('#workflowstatus').val()!='COMPLETED'){
						$('#answer').wysiwyg('setContent','');
					}
				},
				blur:function(e){
					if($('#answer').wysiwyg('getContent')==""){
						$('#answer').wysiwyg('setContent',$('#defaultAnswerMessage').val());
					}
				}
			}
		}); */
		
		
	});
	
	function resendAnswer(){

		var url = 'workflow/question?workflowdetails='+ $("#workflowdetails").val()+ '&reanswerstatus=reanswer';
		showTabByIdAndUrl('details_tab', url);
	}
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            	display:none;
            }
        }
    </style>
</head> 

<body>
<h4 id="error_p">&nbsp;</h4>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
<c:set var="currTimeMillis" value="<%=(new Date()).getTime()%>" />
<form:form action="workflow/question" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<%-- <c:if test="${(answeringAttempts < maxAnsweringAttempts) and (workflowstatus=='COMPLETED')}">
		<a href="#" id="reanswer_workflow" class="butSim">	
			<spring:message code="generic.reactivate_task" text="Re-Send Answer"/>
		</a> | 
	</c:if> --%>
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
		<input class="sText" readonly="readonly" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
		<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>	
		
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_starred'}">
		<div id="priorityDiv" style="display:none;"> 
			<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
			<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
			<input name="priority" id="priority"  type="hidden" value="${priority }">	
			<form:errors path="priority" cssClass="validationError"/>
		</div>
	</c:if>
	</p>
	
	<p>	
		<label class="small"><spring:message code="question.task.creationtime" text="Task Created On"/></label>
		<input id="createdTime" name="createdTime" value="${taskCreationDate}" class="sText datetimemask" readonly="readonly">
		<c:if test="${selectedQuestionType == 'questions_starred' || 
					selectedQuestionType == 'questions_unstarred' ||
					selectedQuestionType == 'questions_shortnotice'}">
			<label class="small"><spring:message code="question.lastDateFromDepartment" text="Last Date From Department"/></label>
			<input id="formattedLastAnswerReceivingDate" name="formattedLastAnswerReceivingDate" class="datemask sText" value="${formattedLastAnswerReceivingDate}" readonly="readonly"/>
			<input type="hidden" id="lastDateOfAnswerReceiving" name="setLastDateOfAnswerReceiving" class="datemask sText" value="${formattedLastAnswerReceivingDate}"/>
			<form:errors path="lastDateOfAnswerReceiving" cssClass="validationError"/>
		</c:if>
	</p>
	
	<c:if test="${selectedQuestionType == 'questions_starred'}">
	<p>
		<label class="small"><spring:message code="question.lastDateForChangingDepartment" text="Last Date For Changing Department"/></label>
		<input id="lastDateForChangingDepartment" class="datemask sText" value="${formattedLastDateForChangingDepartment}" readonly="readonly"/>
		<input type="hidden" id="lastDateForDepartmentChange" name="lastDateForDepartmentChange" value="${lastDateForChangingDepartment}"/>
	</p>
	</c:if>
	
	<p>
	<div id="submissionDateDiv" style="display:none;">		
		<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
		<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	</div>
	<c:if test="${selectedQuestionType=='questions_starred'}">
		<c:if test="${not empty formattedAnsweringDate}">
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<input id="formattedAnsweringDate" name="formattedAnsweringDate" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
		<input id="answeringDate" name="answeringDate" type="hidden"  value="${answeringDate}">
		</c:if>
		<c:if test="${not empty formattedChartAnsweringDate}">
		<input id="chartAnsweringDate" name="chartAnsweringDate" type="hidden"  value="${chartAnsweringDate}">
		</c:if>
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_unstarred'}">
		<c:if test="${not empty formattedAnsweringDate}">
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<input id="formattedAnsweringDate" name="formattedAnsweringDate" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
		<input id="answeringDate" name="answeringDate" type="hidden"  value="${answeringDate}">
		</c:if>
		<c:if test="${not empty formattedChartAnsweringDate}">
		<input id="chartAnsweringDate" name="chartAnsweringDate" type="hidden"  value="${chartAnsweringDate}">
		</c:if>
	</c:if>
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<c:if test="${not empty discussionDateSelected}">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<input id="formattedDiscussionDate"value="${formattedDiscussionDateSelected }" class="sText" readonly="readonly">
			<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" type="hidden">
			<form:errors path="discussionDate" cssClass="validationError"/>
		</c:if>
	</c:if>
	</p>
	<p>
		<label class="small"><spring:message code="question.isTransferable" text="is question to be transfered?"/></label>
		<input type="checkbox" name="isTransferable" id="isTransferable" class="sCheck">
	</p>
	<p>
	<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
	<select name="ministry" id="ministry" class="sSelect" style="width: 270px;">
		<c:forEach items="${ministries }" var="i">
			<c:choose>
				<c:when test="${i.id==ministrySelected }">
					<option value="${i.id }" selected="selected">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id }" >${i.name}</option>
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
	</p>	
	
	<p id="transferP" style="display:none;">
		<label class="small" id="subdepartmentValue"><spring:message code="question.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted"/></label>
		<input type="checkbox" id="transferToDepartmentAccepted" name="transferToDepartmentAccepted" class="sCheck"/>
		
		<label class="small" style="margin-left: 175px;"><spring:message code="question.mlsBranchNotified" text="Is the Respective Question Branch Notified"/></label>
		<input type="checkbox" id="mlsBranchNotifiedOfTransfer" name="mlsBranchNotifiedOfTransfer" class="sCheck"/>
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
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;display:none;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	
	<p style="display:none;">
		<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="question.clubbing" text="Clubbing"></spring:message></a>
		<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="question.referencing" text="Referencing"></spring:message></a>
		<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="question.refresh" text="Refresh"></spring:message></a>	
	</p>	
		
	<p style="display:none;">
		<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewQuestionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>	
	<p style="display:none;">
		<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedQuestions) }">
				<c:forEach items="${clubbedQuestions }" var="i">
					<a href="#" id="cq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
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
	<p style="display:none;">
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
	
	<p style="display:none;">
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	
	<p style="display:none;">
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
		<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="questionText" cssClass="validationError"/>
	</p>
	
	<c:if test="${selectedQuestionType=='questions_starred' or selectedQuestionType=='questions_unstarred'}">
		<c:if test="${questionreferenceText != null and questionreferenceText !='' }">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.reference" text="Reference Text"/>*</label>
				<form:textarea path="questionreferenceText" cssClass="wysiwyg"></form:textarea>
				<form:errors path="questionreferenceText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>
	</c:if>
	
	
	<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question' }">
	<p style="display:none;">
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
		<p style="display:none;">
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>

	<p>
		<c:if test="${selectedQuestionType != 'questions_halfhourdiscussion_from_question'}">
			<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;display:none;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
		</c:if>
	
		<c:if test="${selectedQuestionType == 'questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			
			<c:choose>
				<c:when test="${selectedQuestionType=='questions_shortnotice'}">
					<a href="#" id="reviseReason" style="margin-left: 20px;display:none;"><spring:message code="question.revise.shortnotice.reason" text="Revise Reason"></spring:message></a>
				</c:when>
				<c:otherwise>
					<a href="#" id="reviseReason" style="margin-left: 162px;display:none;"><spring:message code="question.revise.halfhour.reason" text="Revise Reason"></spring:message></a>
				</c:otherwise>
			</c:choose>					
			<c:if test="${selectedQuestionType!='questions_shortnotice'}">	
				<a href="#" id="reviseBriefExplanation" style="margin: 0px 20px 10px 10px;display:none;"><spring:message code="question.reviseBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
			</c:if>
		</c:if>
		<a href="#" id="viewRevision" style="display:none;"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	
	<p class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50" readonly="true"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p class="revise2" id="revisedQuestionTextDiv">
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg" readonly="true"></form:textarea>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<p style="display:none;">
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<p style="display:none;" class="revise3" id="revisedReasonDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedReason" text="Revised Reason"/></label>
	<form:textarea path="revisedReason" rows="2" cols="50" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedReason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise4" id="revisedBriefExplanationDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
	<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv" >
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	<p>
	<table class="uiTable" style="width:900px;">
		<thead>
			<tr>
			<th>
			<spring:message code="qis.latestrevisions.user" text="Usergroup"></spring:message>
			</th>
			<th>
			<spring:message code="qis.latestrevisions.decision" text="Decision"></spring:message>
			</th>
			<th>
			<spring:message code="qis.latestrevisions.revisedQuestionText" text="Revised Question Text"></spring:message>
			</th>
			<th>
			<spring:message code="qis.latestrevisions.remarks" text="Remarks"></spring:message>
			</th>
			</tr>
		</thead>
		<tbody>	
			<c:set var="startingActor" value="${startingActor}"></c:set>
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
			<c:set var="revisedQuestionTextRevision" value=" "/>
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
							<td style="text-align: justify;">
								<c:if test="${i[7]!= revisedQuestionTextRevision}">
									${i[7]}
								</c:if>
							</td>
							<td>
							${i[4]}
							</td>
						</tr>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="count" value="${count+1 }"></c:set>
					</c:otherwise>
				</c:choose>
				<c:set var="revisedQuestionTextRevision" value="${i[7]}"/>
			</c:forEach>
		</tbody>
	</table>
	</p>
	<%-- <c:if test="${workflowstatus!='COMPLETED' or ((answeringAttempts <= maxAnsweringAttempts) and workflowstatus=='COMPLETED')}"> --%>
	<c:if test="${workflowstatus!='COMPLETED' and fn:contains(internalStatusType, 'final')}">	
		<!-- <p style="display:none;"> -->
		<p>
		<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
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
		
		<select id="internalStatusMaster" style="display:none;">
		<c:forEach items="${internalStatuses}" var="i">
		<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
		</c:forEach>
		</select>	
		<form:errors path="internalStatus" cssClass="validationError"/>	
		</p>
		
		<p id="actorDiv">
		<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
		<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
		</p>	
	</c:if>
		
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
		
	<p style="display:none;">
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
	</p>	
	
	<c:if test="${selectedQuestionType == 'questions_shortnotice'}">
		<p>
		<label class="small"><spring:message code="question.dateOfAnsweringByMinister" text="Answering Date"/></label>
		<form:input path="dateOfAnsweringByMinister" cssClass="datemask sText"/>
		<form:errors path="dateOfAnsweringByMinister" cssClass="validationError"/>
		</p>
	</c:if>
	
	<c:choose>
		<c:when test="${workflowstatus=='COMPLETED' and 
		(internalStatusType != 'question_final_clarificationNeededFromDepartment' and
		 internalStatusType != 'question_unstarred_final_clarificationNeededFromDepartment' and
		 internalStatusType != 'question_shortnotice_final_clarificationNeededFromDepartment' and
		 internalStatusType != 'question_halfHourFromQuestion_final_clarificationNeededFromDepartment')}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
				<form:textarea path="answer" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="answer" cssClass="validationError"></form:errors>
			</p>
		</c:when>
		<c:otherwise>
			<c:if test="${internalStatusType != 'question_final_clarificationNeededFromDepartment' and
						 internalStatusType != 'question_unstarred_final_clarificationNeededFromDepartment' and
						 internalStatusType != 'question_shortnotice_final_clarificationNeededFromDepartment' and
						 internalStatusType != 'question_halfHourFromQuestion_final_clarificationNeededFromDepartment'}">
				<p style="display:none;">
					<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
					<form:textarea path="answer" cssClass="wysiwyg"></form:textarea>
					<form:errors path="answer" cssClass="validationError"></form:errors>
				</p>
			</c:if>
		</c:otherwise>
	</c:choose>
	
	<%-- <c:if test="${workflowstatus=='COMPLETED'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.reanswer" text="Re-Answer"/></label>
			<textarea id="reanswer" name="reanswer" class="wysiwyg">${reanswerText}</textarea>
		</p>
	</c:if> --%>
	<c:choose>
		<c:when test="${not empty domain.factualPosition}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.questionsAskedInFactualPosition" text="Questions Asked In Factual Position"/></label>
				<textarea class="wysiwyg" rows="5" cols="50" readonly="readonly">${formattedQuestionsAskedInFactualPosition}</textarea>
				<form:hidden path="questionsAskedInFactualPosition"/>
				<form:hidden path="questionsAskedInFactualPositionForMember"/>
			</p>
			<p>
			<label class="small"><spring:message code="question.lastDateOfFactualPositionReceiving" text="Last date of receiving Factual Position"/></label>
			<form:input path="lastDateOfFactualPositionReceiving" cssClass="datemask sText" readonly="true"/>
			<form:errors path="lastDateOfFactualPositionReceiving" cssClass="validationError"/>
			</p>
			<p>
			<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
			<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:when>
		<c:otherwise>
			<c:if test="${fn:contains(internalStatusType, 'final_clarificationNeededFromDepartment')
						|| fn:contains(internalStatusType, 'final_clarificationNeededFromMemberAndDepartment')}">
				<p>
					<label class="wysiwyglabel"><spring:message code="question.questionsAskedInFactualPosition" text="Questions Asked In Factual Position"/></label>
					<textarea class="wysiwyg" rows="5" cols="50" readonly="readonly">${formattedQuestionsAskedInFactualPosition}</textarea>
					<form:hidden path="questionsAskedInFactualPosition"/>
					<form:hidden path="questionsAskedInFactualPositionForMember"/>
				</p>
				<p>
				<label class="small"><spring:message code="question.lastDateOfFactualPositionReceiving" text="Last date of receiving Factual Position"/></label>
				<form:input path="lastDateOfFactualPositionReceiving" cssClass="datemask sText" readonly="true"/>
				<form:errors path="lastDateOfFactualPositionReceiving" cssClass="validationError"/>
				</p>
				<p>
				<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
				<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
				<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
				</p>
			</c:if>
		</c:otherwise>
		
	</c:choose>

	
	<c:if test="${not empty domain.reasonForLateReply}">
		<p>
		<label class="wysiwyglabel"><spring:message code="question.reasonForLateReply" text="Reason for Late Reply"/></label>
		<form:textarea path="reasonForLateReply" cssClass="wysiwyg"></form:textarea>
		<form:errors path="reasonForLateReply" cssClass="validationError"></form:errors>
		</p>
	</c:if>
	
	<%-- <c:if test="${currTimeMillis <= sendbacktimelimit and workflowstatus!='COMPLETED'}"> --%>
	<c:if test="${workflowstatus!='COMPLETED'}">
		<p>
		<label class="centerlabel"><spring:message code="question.remarks" text="Remarks"/></label>
		<%-- <form:textarea path="remarks" cssClass="wysiwyg" cssStyle=""></form:textarea> --%>
		<form:textarea path="remarks" rows="4" style="width: 250px;"></form:textarea>
		</p>
	</c:if>
	
	<c:if test="${workflowstatus!='COMPLETED' and fn:contains(internalStatusType, 'final')}">
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<%-- <c:if test="${currTimeMillis <= sendbacktimelimit}"> --%>
				<input id="sendBack" type="button" value="<spring:message code='question.sendback' text='Send Back'/>" class="butDef" style="display:none;">
			<%-- </c:if> --%>
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">			
		</p>
	</div>
	</c:if>
<%-- 	<c:if test="${workflowstatus=='COMPLETED'}">
	<div class="fields">
		<h2></h2>
		<p class="tright">		
			<input id="sendBack" type="button" value="<spring:message code='generic.sendback' text='Send Back'/>" class="butDef"> 
			<c:if test="${answeringAttempts < maxAnsweringAttempts}">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:if>
		</p>
	</div>
	</c:if> --%>
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<input type="hidden" name="originalSubDepartment" id="originalSubDepartment" value="${originalSubDepartment}">
	<input type="hidden" name="originalAnsweringDate" id="originalAnsweringDate" value="${originalAnsweringDate}">
	<form:hidden path="answeringAttemptsByDepartment" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="bulkSubmitted"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="factualPositionFromMember"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="processed"/>
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
	<c:if test="${not empty formattedAnswerRequestedDate}">
		<input type="hidden" id="answerRequestedDate" name="setAnswerRequestedDate" class="datetimemask sText" value="${formattedAnswerRequestedDate}"/>
	</c:if>
	<c:if test="${not empty formattedAnswerReceivedDate}">
		<input type="hidden" id="answerReceivedDate" name="setAnswerReceivedDate" class="datetimemask sText" value="${formattedAnswerReceivedDate}"/>
	</c:if>
	
	<input type="hidden" name="answeringAttempts" value="${answeringAttempts}" />
	<input type="hidden" id="reanswerstatus" name="reanswerstatus" value="${reanswerstatus}" />
	<input type="hidden" id="workflowstatus" value="${workflowstatus}" />
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
			<input type="hidden" name="halfHourDiscusionFromQuestionReferenceNumber" id="halfHourDiscusionFromQuestionReferenceNumber" value="${referredQuestionNumber}" />
			<input type="hidden" name="referenceDeviceType" id="referenceDeviceType" value="${domain.referenceDeviceType}"/>
			<input type="hidden" name="referenceDeviceMember" id="referenceDeviceMember" value="${domain.referenceDeviceMember}"/>
			<input type="hidden" name="referenceDeviceAnswerDate" id="referenceDeviceAnswerDate" value="${refDeviceAnswerDate}"/>
	</c:if>
	
	<input type="hidden" id="yaadiNumber" name="yaadiNumber" value="${domain.yaadiNumber}"/>
	<input type="hidden" id="yaadiLayingDate" name="yaadiLayingDate" value="${yaadiLayingDate}"/>
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
<input id="oldRecommendationStatus" value="${oldRecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="noAnswerProvidedMsg" value='<spring:message code="client.error.noanswer" text="Please provide answer."></spring:message>' type="hidden" />
<input id="submissionMsg" value="<spring:message code='generic.submitquestion' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input type="hidden" id="selectedQuestionType" value="${selectedQuestionType}" />
<input type="hidden" id="srole" value="${role}" />
<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
<input type="hidden" id="departmentChangeRestricted" value="${departmentChangeRestricted}" />
<input id="departmentChangeRestrictedMessage" value="<spring:message code='question.departmentChangeRestrictedMessage' text='Department change not allowed at the moment!'/>" type="hidden">
<input id="departmentTransferRestrictionMsg" value="<spring:message code='question.departmentChangeSelectionRequired' text='For department change select ministry and subdepartment'/>" type="hidden"/>
<ul id="contextMenuItems" >
<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
</ul>
</div>

</div>

<div id="backToQuestionDiv" style="display:none;">
<a href="#" id="backToQuestion"><spring:message code="question.backtoquestion" text="Back To Question"></spring:message></a>
</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingresultDiv" style="display:none;">
</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="maxReansweringAttempts" value="${maxAnsweringAttempts}" />
<input type="hidden" id="sendBackTimeLimit" value="${sendbacktimelimit}" />
<c:choose>
	<c:when test="${workflowstatus=='COMPLETED'}">
		<input type="hidden" id="defaultAnswerMessage" value=""/>
	</c:when>
	<c:otherwise>
		<input type="hidden" id="defaultAnswerMessage" value="<spring:message code='question.defaultAnswer' text='Please Enter your Answer here.'/>"/>
	</c:otherwise>
</c:choose>
 <input type="hidden" id="lateDepartmentChangeMessage" value="<spring:message code='question.lateDepartmentChangeMessage' text='You cannot transfer the question as ballot date is been closed, kindly contact with Question branch.'/>"/>
</body>
</html>