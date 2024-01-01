<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="standalonemotion" text="Standalone Information System"/>
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
		var resourceURL='standalonemotion/'+id+'/edit?'+parameters;
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
		var params="id="+id
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
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
		
		var resourceURL='standalonemotion/'+id+'/edit?'+parameters;
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
		var valueToSend="";
		if(value!='-'){		
		var type=$("#internalStatusMaster option[value='standalonemotion_processed_sendToDepartment']").text();
		var sendToMember=$("#internalStatusMaster option[value='standalonemotion_processed_sendToMember']").text();
		var answerReceived=$("#internalStatusMaster option[value='standalonemotion_processed_answerReceived']").text();
		var rejectedWithReason = $("#internalStatusMaster option[value='standalonemotion_processed_rejectionWithReason']").text();
		var clarificationReceived = $("#internalStatusMaster option[value='standalonemotion_processed_clarificationReceived']").text();
		var clarificationNotReceived = $("#internalStatusMaster option[value='standalonemotion_processed_clarificationNotReceived']").text();
		var clarificationFromMemberAndDepartment=$("#internalStatusMaster option[value='standalonemotion_recommend_clarificationNeededFromMemberAndDepartment']").text();		
		var departmentIntimated = $("#internalStatusMaster option[value='standalonemotion_processed_departmentIntimated']").text();
		var questionType = $("#selectedQuestionType").val();
		var recommendRepeatAdmission = $("#internalStatusMaster option[value='standalonemotion_recommend_repeatadmission']").text();
		var nameclubbing=$("#internalStatusMaster option[value='standalonemotion_final_nameclubbing']").text();
		var sendBack = $("#internalStatusMaster option[value='standalonemotion_recommend_sendback']").text();
		var discuss = $("#internalStatusMaster option[value='standalonemotion_recommend_discuss']").text();
		
		if((questionType == 'motions_standalonemotion_halfhourdiscussion') && (value == sendBack || value == discuss)){
			$("#endFlag").val("continue");
		}
		if(value==rejectedWithReason || value==departmentIntimated||value==clarificationReceived||value==clarificationNotReceived){
			$("#endFlag").val("end");
			$("#recommendationStatus").val(value);
			$("#actor").empty();
			$("#actorDiv").hide();
			
			if(value==departmentIntimated){
				$("#level").val($("#originalLevel").val());
			}
			return false;
		}else if(value==clarificationReceived || value==nameclubbing){
			$("#endFlag").val("end");
			$("#recommendationStatus").val(value);
			$("#actor").empty();
			$("#actorDiv").hide();
			return false;
		}else if(type==value || value==sendToMember){			
		    valueToSend=$("#internalStatus").val();
		    $("#endFlag").val("continue");
	    }else if(value==sendBack || value==discuss){			
		   // valueToSend=$("#internalStatus").val();
		     valueToSend=value;
		    $("#endFlag").val("continue");
	    } else {
		    valueToSend=value;
		    $("#internalStatus").val(value);
	    }	
		
		//$("#endflag").val("continue");
		if(value==recommendRepeatAdmission){
			$("#level").val($("#originalLevel").val());
		}
		var params="question="+$("#id").val()+"&status="+valueToSend+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/standalonemotion/actors?'+params;
		
		$.post(resourceURL,function(data){
			
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#actor").html(text);
				$("#actorDiv").show();
				/**** in case of sendback and discuss only recommendation status is changed ****/
				//if(value!=sendback&&value!=discuss){
				//$("#internalStatus").val(value);
				//}
				$("#recommendationStatus").val(value);	
				/**** setting level,localizedActorName ****/
				 var actor1=data[0].id;
				 var temp=actor1.split("#");
				 $("#level").val(temp[2]);
				 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
				 $("#actorName").val(temp[4]);
				 $("#actorName").css('display','inline');
			}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			/**** in case of sendback and discuss only recommendation status is changed ****/
			//if(value!=sendback&&value!=discuss){
			//$("#internalStatus").val(value);
			//}
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
			//$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	/**** group changed ****/
	function groupChanged(){
		var newgroup=$("#group").val();
		if(newgroup==''){
		    var groupChanged=$("#internalStatusMaster option[value='standalonemotion_system_groupchanged']").text();			
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
			    var groupChanged=$("#internalStatusMaster option[value='standalonemotion_system_groupchanged']").text();
			    $("#changeInternalStatus").val(newStatus);
			    $("#changeInternalStatus option").hide();			    
			    $("#changeInternalStatus option[value=']"+groupChanged+"'").show();
			    $("#internalStatus").val(groupChanged);
			    $("#recommendationStatus").val(groupChanged);
		    }else{
		    	var groupChanged=$("#internalStatusMaster option[value='standalonemotion_system_groupchanged']").text();
			    $("#changeInternalStatus").val("-");
			    $("#changeInternalStatus option").show();			    
			    $("#changeInternalStatus option[value=']"+groupChanged+"'").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());			    
		    }  
		    return false;  
	}
	/**** sub departments ****/
	function loadSubDepartments(ministry,department){
		/* $.get('ref/subdepartments/'+ministry+'/'+department,function(data){
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);
			}else{
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);
			}
			groupChanged();
		}); */
		
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
	/**** departments ****/
	function loadDepartments(ministry){
		$.get('ref/departments/'+ministry,function(data){
			$("#department").empty();
			var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#department").html(departmentText);
			loadSubDepartments(ministry,data[0].id);
			}else{
				$("#department").empty();
				var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#department").html(departmentText);				
				$("#subDepartment").empty();
				groupChanged();				
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
	function loadGroup(ministry){
		if(ministry!=''){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);
			loadDepartments(ministry);			
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
		$('#questionsAskedInThisFactualPosition').multiSelect();
		/*******Actor changes*************/
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
		/********Submit Click*********/
		$('#submit').click(function(){
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			
			if($("#questionsAskedInThisFactualPosition").val()!=undefined) {
				var questionsAskedInThisFactualPosition = $("#questionsAskedInThisFactualPosition").val();
				questionsAskedInThisFactualPosition = questionsAskedInThisFactualPosition.join("##");
				$('#questionsAskedInFactualPosition').val(questionsAskedInThisFactualPosition);	
			}				
			
			if($('#internalStatusType').val()=="standalonemotion_final_rejection"){
				if($('#rejectionReason').val()==""){
					$.prompt($('#noRejectionReasonProvidedMsg').val());
				}else{ 
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post($('form').attr('action')+'?operation=workflowsubmit',  
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
				return false;
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
			loadGroup($(this).val());
			}else{
				$("#formattedGroup").val("");
				$("#group").val("");				
				$("#department").empty();				
				$("#subDepartment").empty();		
				$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
				groupChanged();					
			}
		});
		/**** Department Changes ****/
		$("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		});
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('standalonemotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
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
			$(".revise2").toggle();		
			if($("#revisedQuestionTextDiv").css("display")=="none"){
				$("#revisedQuestionText").wysiwyg("setContent","");
			}else{
				$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val());				
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
		
		if($("#revisedSubject").val()!=''){
			$("#revisedSubjectDiv").show();
		}
		
		if($("#refText").val()!=''){
	    	$("#refTextDiv").show();    	
	    }
		
		/**** To show the revised fields ****/
		/* var houseType = $("#houseTypeMaster option[value='"+$("#selectedHouseType").val()+"']").text();
		if(houseType=='lowerhouse'){
			if($("#revisedQuestionText").val()!=''){
		    	$("#revisedQuestionTextDiv").show();
		    }
		}else if(houseType=='upperhouse'){ */		
			if($("#revisedReason").val()!=''){
			    $("#revisedReasonDiv").show();
		    }
			
		    if($("#revisedBriefExplanation").val()!=''){
		    	$("#revisedBriefExplanationDiv").show();
		    }			
		//}
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('standalonemotion/revisions/'+$("#id").val(),function(data){
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
		    $.get('standalonemotion/members/contacts?members='+members,function(data){
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
			    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();
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
				$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId,function(data){
					if(data=='SUCCESS'){
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
				
		
		//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		$("#department option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide();
		//**** Load Actors On Start Up ****/
		if($('#workflowstatus').val()!='COMPLETED' || ($("#workflowstatus").val()=='COMPLETED' 
				&& ($("#internalStatusType").val()=='standalonemotion_final_clarificationNeededFromDepartment' 
				|| $("#internalStatusType").val()=='standalonemotion_final_clarificationNeededFromMember'
				|| $("#internalStatusType").val()=='standalonemotion_final_clarificationNeededFromMemberAndDepartment'))){
			var statusType = $("#internalStatusType").val().split("_");
			var items = $("#internalStatusMaster option[value$='_"+statusType[statusType.length-1]+"']");
			var id= $(items[0]).text().trim();
			$("#changeInternalStatus").val(id);
			$("#changeInternalStatus").change();
			loadActors($("#changeInternalStatus").val());
		}
	});
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
<form:form action="workflow/standalonemotion" method="PUT" modelAttribute="domain">
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
		<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
		<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	
		<c:if test="${not (discussionDateSelected==null or (empty discussionDateSelected))}">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<input id="formattedDiscussionDate"value="${formattedDiscussionDateSelected }" class="sText" readonly="readonly">
			<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" type="hidden">
			<form:errors path="discussionDate" cssClass="validationError"/>
		</c:if>
	</p>
	
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect" >
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
		<c:if test="${houseTypeType=='upperhouse'}">
			<label class="small"><spring:message code="question.group" text="Group"/>*</label>
			<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
			<input type="hidden" id="group" name="group" value="${group}">
			<form:errors path="group" cssClass="validationError"/>
		</c:if>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<select name="subDepartment" id="subDepartment" class="sSelect" >
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
	
	<c:if test="${houseTypeType=='upperhouse'}">
		<p style="display:none;">
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
		<p>
			<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
			<c:choose>
			<c:when test="${!(empty referencedQuestions) }">
			<c:forEach items="${referencedQuestions }" var="i">
			<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
	</c:if>
	
	<c:if test="${houseTypeType=='lowerhouse'}">
		<%-- <p>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin-left: 162px;"><spring:message code="question.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="dereferencing" onclick="dereferencingInt(${domain.id});" style="margin: 20px;"><spring:message code="question.dereferencing" text="Dereferencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="question.refresh" text="Refresh"></spring:message></a>	
		</p> --%>
		
		<p>
			<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty referencedQuestions) }">
					<c:forEach items="${referencedQuestions }" var="i" varStatus="index">
						<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						&nbsp;(${referencedQuestionsSessionAndDevice[index.count-1]})	
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>
			<input type="hidden" name="referencedHDS" id="referencedHDS" value="${referencedHDS}" />
		</p>
	</c:if>
	
	<c:choose>
		<c:when test="${houseTypeType=='no1'}">
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
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
				<a href="#" id="reviseQuestionText" style="margin-right: 20px;"><spring:message code="question.reviseQuestionText" text="Revise Notice Content"></spring:message></a>				
			</p>
			
		</c:when>
		<c:when test="${houseTypeType=='upperhouse' or houseTypeType=='lowerhouse'}">
			<p>	
				<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
				<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
				<form:errors path="subject" cssClass="validationError"/>	
			</p>
			<p>
				<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
				<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
				<a href="#" id="reviseReason" style="margin-left: 20px;"><spring:message code="question.revise.halfhour.reason" text="Revise Reason"></spring:message></a>
				<a href="#" id="reviseBriefExplanation" style="margin: 0px 0px 10px 10px;"><spring:message code="question.reviseBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
			</p>
		</c:when>
	</c:choose>
		
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
	
	<p style="text-align: right; width: 720px;">
		<a href="#" id="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	
	<p>	
		<label class="wysiwyglabel"><spring:message code="standalone.refText" text="Referecnce Text"/></label>
		<form:textarea path="refText" cssClass="wysiwyg"></form:textarea>
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
			<c:set var="currUserFound" value="no" />
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
								${i[1]}<br>${i[0]}
							</td>
							<td>
								${i[3]}
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
			</c:forEach>
			<c:if test="${workflowstatus!='COMPLETED' or (workflowstatus=='COMPLETED' and (internalStatusType=='standalonemotion_final_clarificationNeededFromDepartment'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMember'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMemberAndDepartment'))}">
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
	<c:if test="${(internalStatusType == 'standalonemotion_final_clarificationNeededFromDepartment' || internalStatusType == 'standalonemotion_final_clarificationNeededFromMember')}">
		<p>
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
			<form:hidden path="questionsAskedInFactualPosition" id="questionsAskedInFactualPosition"/>
			<form:errors path="questionsAskedInFactualPosition" cssClass="validationError"/>	
		</p>		
		
		<c:if test="${!(empty domain.factualPosition)}">
			<p>
			<label class="wysiwyglabel"><spring:message code="hds.factualPosition" text="Factual Position"/></label>
			<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>
	</c:if>
	
	<c:if test="${!(empty domain.factualPosition)}">
		<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
		<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
		<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
		
	<c:if test="${workflowstatus!='COMPLETED' or (workflowstatus=='COMPLETED' and (internalStatusType=='standalonemotion_final_clarificationNeededFromDepartment'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMember'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMemberAndDepartment'))}">	
		<p>
			<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
			</select>	
			<form:errors path="internalStatus" cssClass="validationError"/>	
		</p>
		
		<p id="actorDiv" style="display:none;">
			<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
			<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
		</p>		
	</c:if>
		
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<input type="hidden" id="discussionStatus"  name="discussionStatus" value="${discussionStatus}">
		
	<c:if test="${internalStatusType == 'standalonemotion_recommend_rejection' or internalStatusType == 'standalonemotion_final_rejection'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
		<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
	</p>
	</c:if>	
		
	<c:if test="${!(empty domain.answer)}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
			<form:textarea path="answer" cssClass="wysiwyg" readonly="true"></form:textarea>
		</p>
	</c:if>
	
	<c:if test="${workflowstatus!='COMPLETED' }">
	<div class="fields">
		<h2></h2>
		<p class="tright">		
		<c:if test="${bulkedit!='yes'}">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
		<c:if test="${bulkedit=='yes'}">
			<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
		</c:if>
		</p>
	</div>
	</c:if>
	
	<c:if test="${workflowstatus!='COMPLETED' or (workflowstatus=='COMPLETED' and (internalStatusType=='standalonemotion_final_clarificationNeededFromDepartment'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMember'
					|| internalStatusType=='standalonemotion_final_clarificationNeededFromMemberAndDepartment'))}">
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
	
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<form:hidden path="file"/>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
	<input type="hidden" id="clarificationStatus" name="clarificationStatus" value="${clarificationStatus}"/>
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
	
	
	<!-- --------------------------PROCESS VARIABLES -------------------------------- -->
	
	
	<input id="mailflag" name="mailflag" value="${pv_mailflag}" type="hidden">
	<input id="timerflag" name="timerflag" value="${pv_timerflag}" type="hidden">
	<input id="reminderflag" name="reminderflag" value="${pv_reminderflag}" type="hidden">	
	
	<!-- mail related variables -->
	<input id="mailto" name="mailto" value="${pv_mailto}" type="hidden">
	<input id="mailfrom" name="mailfrom" value="${pv_mailfrom}" type="hidden">
	<input id="mailsubject" name="mailsubject" value="${pv_mailsubject}" type="hidden">
	<input id="mailcontent" name="mailcontent" value="${pv_mailcontent}" type="hidden">
	
	<!-- timer related variables -->
	<input id="timerduration" name="timerduration" value="${pv_timerduration}" type="hidden">
	<input id="lasttimerduration" name="lasttimerduration" value="${pv_lasttimerduration}" type="hidden">	
	
	<!-- reminder related variables -->
	<input id="reminderto" name="reminderto" value="${pv_reminderto}" type="hidden">
	<input id="reminderfrom" name="reminderfrom" value="${pv_reminderfrom}" type="hidden">
	<input id="remindersubject" name="remindersubject" value="${pv_remindersubject}" type="hidden">
	<input id="remindercontent" name="remindercontent" value="${pv_remindercontent}" type="hidden">
	<input id="ballotStatus" name="ballotStatus" value="${domain.ballotStatus.id}" type="hidden">
				
</form:form>
<input id="oldgroup" name="oldgroup" value="${group}" type="hidden">
<input id="formattedoldgroup" name="formattedoldgroup" value="${formattedGroup}" type="hidden">

<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='question.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
<input id="oldRecommendationStatus" value="${oldRecommendationStatus}" type="hidden">
<input id="selectedQuestionType" value="${selectedQuestionType}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="noRejectionReasonProvidedMsg" type="hidden" value='<spring:message code="client.error.noRejectionReason" text="Rejection Reason must be provided"></spring:message>'/>
<input id="internalStatusType" type="hidden" value="${internalStatusType}"/>
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input type="hidden" id="originalLevel" value="${level}" />
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
</body>
</html>