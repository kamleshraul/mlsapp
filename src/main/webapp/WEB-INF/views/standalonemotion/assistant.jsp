<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="standalonemotion" text="StandaloneMotion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced questions ****/		
	function viewStandaloneMotionDetail(id){
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
		var params="standaloneMotionId="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val()+(($("#htType").val()=='lowerhouse')? '&useforfiling=yes':'');		
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
        +"&deviceType="+$("#questionType").val();
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
			"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
			var resourceURL='ref/standalonemotion/actors?'+params;
		    var sendback=$("#internalStatusMaster option[value='standalonemotion_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='standalonemotion_recommend_discuss']").text();		
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#actor").html(text);
					$("#actorDiv").show();				
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
					$("#internalStatus").val(value);
					}
					$("#recommendationStatus").val(value);			
					/**** setting level,localizedActorName ****/
					 var actor1=data[0].id;
					 var temp=actor1.split("#");
					 $("#level").val(temp[2]);		    
					 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
					 $('#actorName').val(temp[4]);
					 $('#actorName').css('display','inline');
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
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
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	/**** departments ****/
//	function loadDepartments(ministry){
//		$.get('ref/departments/'+ministry,function(data){
//			$("#department").empty();
//			var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
//			if(data.length>0){
//			for(var i=0;i<data.length;i++){
//				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
//			}
//			$("#department").html(departmentText);
//			loadSubDepartments(ministry,data[0].id);
//			}else{
//				$("#department").empty();
//				var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
//				$("#department").html(departmentText);				
//				$("#subDepartment").empty();
//				groupChanged();				
//			}
//		}).fail(function(){
//			if($("#ErrorMsg").val()!=''){
//				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
//			}else{
//				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
//			}
//			scrollTop();
//		});
//	}
    /**** groups ****/
	function loadGroup(ministry){
		if(ministry!=''){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);
			//loadDepartments(ministry);
			loadSubDepartments(ministry);
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
			if($(this).val()!=''){
				loadGroup($(this).val());
				//loadSubDepartments($(this).val());
			}else{
				$("#formattedGroup").val("");
				$("#group").val("");				
				$("#department").empty();				
				$("#subDepartment").empty();				
				$("#answeringDate").empty();		
				$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
				groupChanged();					
			}
		});
		/**** Department Changes ****/
		/* $("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		}); */
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
		    var supportingMembers=$("select[name='selectedSupportingMembers']").val();
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
					$("#clubbedQuestionTextsDiv").empty();
					$.get('ref/'+parent+'/clubbedquestiontext',function(data){
						
						var text="";
						
						for(var i = 0; i < data.length; i++){
							text += "<p>"+data[i].name+" ("+data[i].displayName+")</p><p>"+data[i].value+"</p><hr />";
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
				$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId+"&whichDevice=questions_",function(data){
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
				$.post('refentity/dereferencing?pId='+questionId+"&rId="+refId+"&device="+$("#questionType").val(),function(data){
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
		if(1==2){
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
	    
	    if($("#refText").val()!=''){
	    	$("#refTextDiv").show();    	
	    }
	  
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
				$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()+'&primaryMemberId='+$('#primaryMember').val(), {
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
			/* $.get('question/'+$("#id").val()+'/edit?editPrint=true&usergroup=' + $("#currentusergroup").val() + '&usergroupType=' + $("#currentusergroupType").val(),function(data){
				if(data){
					var divD = $("<div></div>").html(data);
					$(divD).print();
				}
			},'html'); */
			//var text = "<div><p>"+$("#selectedSupportingMembers").val()+"</p><p>"+$("#subject").val()+"</p><p>"+$("#questionText").val()+"</p><spring:message code='generic.new' text='new' /></div>";
			var myWindow = window.open('standalonemotion/'+$("#id").val()+'/edit?editPrint=true&usergroup=' + 
						$("#currentusergroup").val() + '&usergroupType=' + $("#currentusergroupType").val(),'_blank','width=700,height=768,scrollbars=1,menubar=yes');
			myWindow.print();
			/* var doc = myWindow.document;
			doc.open();
			doc.write(text);
			doc.close(); */
			/*
			channelmode=yes|no|1|0
			directories=yes|no|1|0
			fullscreen=yes|no|1|0
			height=pixels
			left=pixels
			location=yes|no|1|0
			menubar=yes|no|1|0
			resizable=yes|no|1|0
			scrollbars=yes|no|1|0
			status=yes|no|1|0
			titlebar=yes|no|1|0
			toolbar=yes|no|1|0
			top=pixels
			width=pixels

			*/
			/* var form = $("form").clone();
			var formHeader = $(form).children().filter("h2")[0];
			var formData = $(form).children().filter("p");
			var text = $("<div></div>").append($(formHeader)).append("<hr>");
			for(var i = 0; i < formData.length; i++){
				if($(formData[i]).css('display')!='none'){
					var putEle = $(formData[i]);
					$(putEle).children().filter("select").each(function(){
						$(this).css({'max-width':'100px !important','width':'100px !important'});
					});
					
					$(text).append($(putEle));
				}
			}
			var wind = window.open('','_blank','width=700,height=768,scrollbars=1,menubar=yes');
			var doc = wind.document;
			doc.open();
			doc.write($(text).html());
			doc.close(); */
			
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
        
        #clubbedQuestionTextsDiv{
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
	<form:form action="standalonemotion" method="PUT" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<h2>${formattedQuestionType}: ${formattedNumber}</h2>
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
			<c:choose>
				<c:when test="${formattedNumber != null}">
					<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
					<input id="number" name="number" value="${domain.number}" type="hidden">
				</c:when>
				<c:otherwise>		
					<input id="number" name="number" class="sText integer" value="" type="text">
				</c:otherwise>
			</c:choose>
			<form:errors path="number" cssClass="validationError"/>
		</p>
			
		<p>		
			<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
			<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
			<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">				
			
			<c:choose>
				<c:when test="${discussionDateSelected != null}">
					<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
					<input id="formattedDiscussionDate" name="formattedDiscussionDate" value="${formattedDiscussionDateSelected}" class="sText" readonly="readonly">
					<input id="discussionDate" name="discussionDate" type="hidden"  value="${discussionDateSelected}">
				</c:when>
				<c:otherwise>
					<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
					<select name="discussionDate" id="discussionDate" class="sSelect">
						<option value="">--<spring:message code="please.select" text="Select"/>--</option>
						<c:forEach items="${discussionDates}" var="i">
							<c:choose>
								<c:when test="${i.id==discussionDateSelected }">
									<option value="${i.id}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}" >${i.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</c:otherwise>
			</c:choose>			
			<form:errors path="discussionDate" cssClass="validationError"/>
		</p>
	
		<p>
			<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
			<select name="ministry" id="ministry" class="sSelect">
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
			<c:choose>	
				<c:when test="${not (selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' and houseTypeType=='lowerhouse')}">
					<label class="small"><spring:message code="question.group" text="Group"/>*</label>
					<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
					<input type="hidden" id="group" name="group" value="${group }">
					<form:errors path="group" cssClass="validationError"/>
				</c:when>	
			</c:choose>
		</p>	
		
		<p>
			<c:if test="${selectedQuestionType=='xyz'}" >
				<%--Scrapped code keeping just for future use if ever arise --%>
				<label class="small"><spring:message code="question.department" text="Department"/></label>
				<select name="department" id="department" class="sSelect">
					<c:forEach items="${departments }" var="i">
						<c:choose>
							<c:when test="${i.id==departmentSelected }">
								<option value="${i.id }" selected="selected">${i.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${i.id }" >${i.name}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
				<form:errors path="department" cssClass="validationError"/>
			</c:if>
			<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
			<select name="subDepartment" id="subDepartment" class="sSelect">
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
		
		<c:choose>
			<c:when test="${selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' and houseTypeType=='lowerhouse'}">
				<p>
						<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="motion.addfile" text="Filing"></spring:message></a>
						<a href="#" id="referencing" onclick="referencingInt(${domain.id});"><spring:message code="question.referencing" text="Referencing"></spring:message></a>
						<a href="#" id="dereferencing" onclick="dereferencingInt(${referencedHDS});" style="margin: 20px;"><spring:message code="question.dereferencing" text="Dereferencing"></spring:message></a>
						<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="question.refresh" text="Refresh"></spring:message></a>	
				</p>
				
				<p>
						<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
						<a href="#" id="p${parent}" onclick="viewQuestionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
						<input type="hidden" id="parent" name="parent" value="${parent}">
				</p>	
			</c:when>
			<c:otherwise>
				<p>
						<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="question.clubbing" text="Clubbing"></spring:message></a>
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
									<a href="#" id="cq${i.number}" class="clubbedRefQuestions" onclick="viewStandaloneMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
			</c:otherwise>		
		</c:choose>			
		
		<p>
			<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty referencedQuestions) }">
					<c:forEach items="${referencedQuestions }" var="i" varStatus="index">
						<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewStandaloneMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						<%--&nbsp;(${referencedQuestionsSessionAndDevice[index.count-1]}) --%>	
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>
			<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='lowerhouse'}">	
				<input type="hidden" id="referencedHDS" name="referencedHDS" value="${referencedHDS}" />
			</c:if>
			<c:if test="${not (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='lowerhouse')}">	
				<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
					<c:forEach items="${referencedQuestions }" var="i">
						<option value="${i.id}" selected="selected"></option>
					</c:forEach>
				</select>
			</c:if>
		</p>
		
		<p>	
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
			<form:textarea path="subject" rows="2" cols="50"></form:textarea>
			<form:errors path="subject" cssClass="validationError"/>	
		</p>
	
		<%-- <c:if test="${houseTypeType=='lowerhouse'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
				<form:textarea path="questionText" cssClass="wysiwyg"></form:textarea>
				<form:errors path="questionText" cssClass="validationError"/>	
			</p>
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px; margin-right: 10px;"><spring:message code="question.reviseSubject" text="Revise Sbject"></spring:message></a>
				<a href="#" id="reviseQuestionText" style="margin-left: 10px; "><spring:message code="question.reviseHDSQuestionText" text="Revise Notice Content"></spring:message></a>				
				<a href="#" id="viewRevision" style="margin-left: 20px;"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
			</p>
		</c:if> --%>
		
		
		<%-- <c:if test="${houseTypeType=='upperhouse'}"> --%>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
				<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
				<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
				<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 10px;"><spring:message code="question.reviseSubject" text="Revise Sbject"></spring:message></a>
				<a href="#" id="reviseReason" style="margin-left: 10px;"><spring:message code="question.reviseReason" text="Revise Reason"></spring:message></a>
				<a href="#" id="reviseBriefExplanation" style="margin-left: 20px;"><spring:message code="question.reviseBriefExplanation" text="Revise Brief Explanation"></spring:message></a>				
				<a href="#" id="viewRevision" style="margin-left: 20px;"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
				<br />
			</p>
		<%-- </c:if> --%>
		
		<p style="display:none;" class="revise1" id="revisedSubjectDiv">
			<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
			<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
			<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<%-- <p style="display:none;" class="revise2" id="revisedQuestionTextDiv">
			<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
			<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
			<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p> --%>
		
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
		
		<p id="internalStatusDiv">
			<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
			<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
		</p>
		
		<c:if test="${(internalStatusType=='standalonemotion_system_putup' && selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='lowerhouse')
		||((internalStatusType=='standalonemotion_system_putup' || internalStatusType=='standalonemotion_system_assistantprocessed') && selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='upperhouse')}">
			<security:authorize access="hasAnyRole('SMOIS_ASSISTANT')">		
				<p>
					<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
					<select id="changeInternalStatus" class="sSelect">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${internalStatuses}" var="i">
						<c:if test="${(i.type!='standalonemotion_recommend_sendback'&&i.type!='standalonemotion_recommend_discuss') }">
							<c:choose>
								<c:when test="${i.type=='standalonemotion_system_groupchanged' }">
									<option value="${i.id}" style="display: none;"><c:out value="${i.name}"></c:out></option>	
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${i.id==internalStatusSelected }">
											<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
										</c:when>
										<c:otherwise>
											<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
					</select>
					
					<select id="internalStatusMaster" style="display:none;">
					<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
					</c:forEach>
					</select>	
					<form:errors path="internalStatus" cssClass="validationError"/>	
				</p>
			</security:authorize>
	
			<p id="actorDiv" style="display: none;">
				<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
				<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }" />
				<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
			</p>		
	
		</c:if>		
			
		<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
		<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
		<c:if test="${fn:contains(internalStatusType, 'standalonemotion_final')}">
			<form:hidden path="actor"/>
		</c:if>
		<c:if test="${!(empty domain.factualPosition) || 
				(internalStatusType=='standalonemotion_final_clarificationNeededFromDepartment'&& houseTypeType=='upperhouse')
				 }">
			<p>
			<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Position"/></label>
			<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
			<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>	
		
		<c:if test="${houseTypeType=='upperhouse' && internalStatusType=='standalonemotion_final_rejection'}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:if>
		
		<p id="refTextDiv">	
			<label class="wysiwyglabel"><spring:message code="standalone.refText" text="Referecnce Text"/></label>
			<form:textarea path="refText" cssClass="wysiwyg"></form:textarea>
		</p>	
		
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
				<c:if test="${internalStatusType=='standalonemotion_submit'
									||internalStatusType=='standalonemotion_system_assistantprocessed'
									||((internalStatusType=='standalonemotion_system_putup' || internalStatusType=='standalonemotion_system_groupchanged') && (selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='lowerhouse'))
									||((internalStatusType=='standalonemotion_system_putup' || internalStatusType=='standalonemotion_system_assistantprocessed' || internalStatusType=='standalonemotion_system_groupchanged') && (selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='upperhouse'))
									}">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<security:authorize access="hasAnyRole('SMOIS_ASSISTANT')">
						<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
					</security:authorize>					
				</c:if>
				
				<c:if test="${fn:contains(internalStatusType, 'standalonemotion_final')}">
					<security:authorize access="hasAnyRole('SMOIS_CLERK','SMOIS_ASSISTANT')">
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					</security:authorize>					
				</c:if>
			<%-- <c:choose>
				<c:when test="${bulkedit!='yes'}">
					<c:if test="${internalStatusType=='standalonemotion_submit'
								||internalStatusType=='standalonemotion_system_assistantprocessed'
								||((internalStatusType=='standalonemotion_system_putup' || internalStatusType=='standalonemotion_system_groupchanged') && (selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='lowerhouse'))
								||((internalStatusType=='standalonemotion_system_assistantprocessed' || internalStatusType=='standalonemotion_system_groupchanged') && (selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='upperhouse'))
								}">
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						<security:authorize access="hasAnyRole('SMOIS_ASSISTANT')">
							<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
						</security:authorize>					
					</c:if>
					- Remove the Following if conditions after session... Hack given for the council branch 
					
					<c:if test="${fn:contains(internalStatusType, 'question_final')}">
						<security:authorize access="hasAnyRole('SMOIS_CLERK','SMOIS_ASSISTANT')">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</security:authorize>					
					</c:if>
					<c:if test="${(internalStatusType=='question_system_putup'||internalStatusType=='question_putup_nameclubbing'&& selectedQuestionType=='questions_starred')
								||(internalStatusType=='question_system_assistantprocessed'&&selectedQuestionType=='questions_shortnotice')
								||(internalStatusType=='question_system_assistantprocessed'&&selectedQuestionType=='questions_halfhourdiscussion_from_question')
								||(internalStatusType=='question_system_assistantprocessed'&&selectedQuestionType=='questions_unstarred')
								||(internalStatusType=='question_system_putup'&&selectedQuestionType=='questions_halfhourdiscussion_standalone')}">		
						<c:if test="${bulkedit!='yes'}">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
							<input id="startworkflow" type="button" value="<spring:message code='question.putupquestion' text='Put Up Question'/>" class="butDef">
						</c:if>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${bulkedit=='yes'}">
						<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
					</c:if>
				</c:otherwise>
			</c:choose> --%>
		</p>
		</div>
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
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="taskid" name="taskid" value="${taskid}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
		<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
		<input type="hidden" name="originalType" id="originalType" value="${originalType}">
		<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
		
		<c:if test="${selectedQuestionType=='motions_standalonemotion_halfhourdiscussion' && houseTypeType=='lowerhouse'}">
			<!-- --------------------------PROCESS VARIABLES -------------------------------- -->
		
			
			<input id="mailflag" name="mailflag" value="${pv_mailflag}" type="hidden">
			<input id="timerflag" name="timerflag" value="${pv_timerflag}" type="hidden">
			<input id="reminderflag" name="reminderflag" value="${pv_reminderflag}" type="hidden">	
			
			<!-- mail related variables -->
			<input id="mailto" name="mailto" value="${pv_mailto}" type="hidden" />
			<input id="mailfrom" name="mailfrom" value="${pv_mailfrom}" type="hidden" />
			<input id="mailsubject" name="mailsubject" value="${pv_mailsubject}" type="hidden" />
			<input id="mailcontent" name="mailcontent" value="${pv_mailcontent}" type="hidden" />
			
			<!-- timer related variables -->
			<input id="timerduration" name="timerduration" value="${pv_timerduration}" type="hidden">
			<input id="lasttimerduration" name="lasttimerduration" value="${pv_lasttimerduration}" type="hidden">	
			
			<!-- reminder related variables -->
			<input id="reminderto" name="reminderto" value="${pv_reminderto}" type="hidden">
			<input id="reminderfrom" name="reminderfrom" value="${pv_reminderfrom}" type="hidden">
			<input id="remindersubject" name="remindersubject" value="${pv_remindersubject}" type="hidden">
			<input id="remindercontent" name="remindercontent" value="${pv_remindercontent}" type="hidden">	
		</c:if>		
		
		<c:if test="${domain.ballotStatus!=null}">
			<input type="hidden" name="ballotStatus" id="ballotStatusId" value="${domain.ballotStatus.id}"/>		
		</c:if>
		
		<%-- <h1>
		<c:forEach items="${enx}" var="i">
			${i}<br>
		</c:forEach>
	</h1> --%>
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
	<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
	<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
	<input id="questionType" type="hidden" value="${selectedQuestionType}" />
	<input type="hidden" id="hdsRefEntity" value="${hdsRefEntity}" />
	<input id="htType" type="hidden" value="${houseTypeType}" />
	
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

<div id="referencingResultDiv" style="display:none;">
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>