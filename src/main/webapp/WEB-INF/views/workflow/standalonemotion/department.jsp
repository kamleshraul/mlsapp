<%@ include file="/common/taglibs.jsp" %>
<%@ page import="java.util.Date;" %>
<html>
<head>
	<title>
	<spring:message code="standalonemotion" text="Standalonemotion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced standalonemotions ****/		
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
		//var valueToSend="";
		if(value!='-'){	
			var sendback=$("#internalStatusMaster option[value='standalonemotion_recommend_sendback']").text();
			var discuss=$("#internalStatusMaster option[value='standalonemotion_recommend_discuss']").text();
			var sendToDeskOfficer=$("#internalStatusMaster option[value='standalonemotion_processed_sendToDeskOfficer']").text();
			if(value == sendToDeskOfficer){
			    valueToSend = $("#internalStatus").val();
		    }else{
			    valueToSend = value;
		    }			
		var params="question="+$("#id").val()+"&status="+valueToSend+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/standalonemotion/actors?'+params;
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="";
				 var actor1="";
				 var actCount = 1;
				for(var i=0;i<length;i++){
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
				$("#actorDiv").show();				
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value!=sendback && value!=discuss && value!=sendToDeskOfficer){
					$("#internalStatus").val(value);
				}
				$("#recommendationStatus").val(value);	
				/**** setting level,localizedActorName ****/
				 var actor1=data[0].id;
				 var temp=actor1.split("#");
				 $("#level").val(temp[2]);		    
				 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
			}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			/**** in case of sendback and discuss only recommendation status is changed ****/
			if(value!=sendback && value!=discuss && value!=sendToDeskOfficer){
				$("#internalStatus").val(value);
			}
		    $("#recommendationStatus").val(value);
			}
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
			$.unblockUI();
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
		
		$('#mlsBranchNotifiedOfTransfer').val(null);
		$('#transferToDepartmentAccepted').val(null);
				
		/*******Actor changes*************/
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
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
				loadSubDepartments($(this).val());
			}else{
				$("#formattedGroup").val("");
				$("#group").val("");				
				//$("#department").empty();				
				$("#subDepartment").empty();				
				$("#answeringDate").empty();		
				//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
				//groupChanged();					
			}
		});
		/**** Department Changes ****/
		/* $("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		}); */
		
		$('#subDepartment').change(function(){
			if($("#houseTypeType").val()=='upperhouse'){
				loadGroup($(this).val());
			}
		});
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('standalonemotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
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

		//if($('#selectedQuestionType').val()=='motions_standalonemotion_halfhourdiscussion' && $("#selectedHouseType").val()=='upperhouse'){
		
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
				
		
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
		
		if($("#refText").val()!=''){
	    	$("#refTextDiv").show();    	
	    }
		
		/* if($('#selectedQuestionType').val()=='motions_standalonemotion_halfhourdiscussion' && $("#selectedHouseType").val()=='lowerhouse'){
		    if($("#revisedQuestionText").val()!=''){
		    	$("#revisedQuestionTextDiv").show();
		    }
		}  */
		
		//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		//$("#department option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide();
		//**** Load Actors On page Load ****/
		if($('#workflowstatus').val()!='COMPLETED'){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value='"+statusType[statusType.length-1]+"']").text();
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
						
			if(($('#answer').val()=="" && $('#factualPosition').val()=="" || ($("#workflowstatus").val()=='COMPLETED' && $('#reanswer').val()=="")) && $('#selectedQuestionType').val()!='motions_standalonemotion_halfhourdiscussion'){
				$.prompt($('#noAnswerProvidedMsg').val());
			}else{
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){				        	
							$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
							var url = $('form').attr('action')+'?operation=workflowsubmit';
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
			}
			return false;		
		});
		
		$('#answer').wysiwyg({
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
		});
		
		$('#isTransferable').change(function() {
	        if ($(this).is(':checked')) {
	        	$("#ministry option[selected!='selected']").show();
	    		$("#subDepartment option[selected!='selected']").show(); 
	    		$("#transferP").css("display","inline-block");
	    		$("#submit").css("display","none");
	        }else{
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
<c:set var="currTimeMillis" value="<%=(new Date()).getTime()%>" />
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
		
		<c:if test="${not (discussionDateSelected==null and (empty discussionDateSelected))}">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<input id="formattedDiscussionDate"value="${formattedDiscussionDateSelected }" class="sText" readonly="readonly">
			<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" type="hidden">
			<form:errors path="discussionDate" cssClass="validationError"/>
		</c:if>
	</p>
	<p>
		<label class="small"><spring:message code="standalonemotion.isTransferable" text="is resolution to be transfered?"/></label>
		<input type="checkbox" name="isTransferable" id="isTransferable" class="sCheck">
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
		<c:if test="${(selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' and houseTypeType=='upperhouse')}">
		<label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
		<input type="hidden" id="group" name="group" value="${group }">
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
		
	<p id="transferP" style="display:none;">
		<label class="small" id="subdepartmentValue"><spring:message code="standalonemotion.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted"/></label>
		<input type="checkbox" id="transferToDepartmentAccepted" name="transferToDepartmentAccepted" class="sCheck"/>
		
		<label class="small" style="margin-left: 175px;"><spring:message code="standalonemotion.mlsBranchNotified" text="Is the Respective Standalone Motion Branch Notified"/></label>
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
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;display:none;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<c:if test="${houseTypeType=='upperhouse'}">
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
		
		<p style="display:none;">
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

	<p style="display:none;"> 
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
		<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
		<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;">
		<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	
	<p style="display:none;">
		<a href="#" id="reviseSubject" style="margin-left: 162px; margin-right: 10px;"><spring:message code="question.reviseSubject" text="Revise Subject"></spring:message></a>
		<a href="#" id="reviseReason" style="margin-left: 10px; margin-right: 10px;"><spring:message code="question.reviseReason" text="Revise Reason"></spring:message></a>
		<a href="#" id="reviseBriefExplanation" style="margin-left: 10px; margin-right: 10px;"><spring:message code="question.reviseReason" text="Revise Brief Explanation"></spring:message></a>
		<a href="#" id="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>

	
	<p  class="revise1" id="revisedSubjectDiv">
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
		<form:textarea path="revisedSubject" rows="2" cols="50" readonly="true"></form:textarea>
		<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedQuestionTextDiv">
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
		<form:textarea path="revisedQuestionText" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p class="revise3" id="revisedReasonDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedReason" text="Revised Reason"/></label>
	<form:textarea path="revisedReason" rows="2" cols="50" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedReason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p  class="revise4" id="revisedBriefExplanationDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
	<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
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
			<c:forEach items="${latestRevisions}" var="i">
				<c:choose>
					<c:when test="${count>= startingActorCount}">
						<tr>
							<td>
								${i[0]}<br>${i[1]}
							</td>
							<td>
								${i[2]}
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
		</tbody>
	</table>
	<c:if test="${workflowstatus!='COMPLETED'}">	
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
	<input type="hidden" id="discussionStatus"  name="discussionStatus" value="${discussionStatus}">
		
	<p style="display:none;">
		<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
	</p>	
		
	<c:if test="${internalStatusType == 'standalonemotion_final_clarificationNeededFromDepartment' or internalStatusType == 'standalonemotion_final_clarificationNeededFromMemberAndDepartment'}">
		<p>
			<label class="small"><spring:message code="question.questionsAskedInFactualPosition" text="Questions Asked In Factual Position"/></label>
			<textarea class="wysiwyg" rows="5" cols="50" readonly="readonly">${formattedQuestionsAskedInFactualPosition}</textarea>
			<form:hidden path="questionsAskedInFactualPosition"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="question.lastDateOfFactualPositionReceiving" text="Last date of receiving Factual Position"/></label>
			<form:input path="lastDateOfFactualPositionReceiving" cssClass="datemask sText" readonly="true"/>
			<form:errors path="lastDateOfFactualPositionReceiving" cssClass="validationError"/>
		</p>
	</c:if>
	<c:if test="${not empty domain.factualPosition}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
			<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
	
	<c:if test="${houseTypeType=='upperhouse'}">
		<c:if test="${internalStatusType == 'standalonemotion_recommend_rejection' or internalStatusType == 'standalonemotion_final_rejection'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg" readonly="true"></form:textarea>
			</p>
		</c:if>
	</c:if>
	
	<p style="display:none;">	
		<label class="wysiwyglabel"><spring:message code="standalone.refText" text="Referecnce Text"/></label>
		<form:textarea path="refText" cssClass="wysiwyg"></form:textarea>
	</p>
	
	<%-- <c:if test="${currTimeMillis <= sendbacktimelimit and workflowstatus!='COMPLETED'}"> --%>
	<c:if test="${workflowstatus!='COMPLETED'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
			<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		</p>
	</c:if>
	
	<c:if test="${workflowstatus!='COMPLETED' }">
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
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
	<input type="hidden" name="answeringAttempts" value="${answeringAttempts}" />
	<input type="hidden" id="reanswerstatus" name="reanswerstatus" value="${reanswerstatus}" />
	<input type="hidden" id="workflowstatus" value="${workflowstatus}" />
	<input id="ballotStatus" name="ballotStatus" value="${domain.ballotStatus.id}" type="hidden">
	<input id="ministrySelected" name="ministrySelected" value="${ministrySelected }" type="hidden">
	<input id="subDepartmentSelected" name="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
</form:form>
<input id="oldgroup" name="oldgroup" value="${group}" type="hidden">
<input id="formattedoldgroup" name="formattedoldgroup" value="${formattedGroup}" type="hidden">

<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='question.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">

<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
<input id="oldRecommendationStatus" value="${oldRecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="noAnswerProvidedMsg" value='<spring:message code="client.error.noanswer" text="Please provide answer."></spring:message>' type="hidden" />
<input id="submissionMsg" value="<spring:message code='generic.submitquestion' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input id="originalLevel" name="originalLevel" value="${domain.level}" type="hidden">
<input type="hidden" id="selectedQuestionType" value="${selectedQuestionType}" />
<input type="hidden" id="houseTypeType" value="${houseTypeType}"/>
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
 
</body>
</html>