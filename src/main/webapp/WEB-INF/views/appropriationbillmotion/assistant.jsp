<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced motions ****/		
	function viewApprorpiationBillMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&approrpiationBillMotionType="+$("#selectedApprorpiationBillMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='appropriationbillmotion/'+id+'/edit?'+parameters;
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
	/**** to view the referred resolution ****/
	function viewResolutionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&deviceType="+$("#deviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='resolution/'+id+'/edit?'+parameters;
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
		var params="approrpiationBillMotionId="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val();		
		$.get('clubentity/init?'+params,function(data){
			$.unblockUI();	
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#clubbingResultDiv").html(data);
			$("#clubbingResultDiv").show();
			$("#referencingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToMotionDiv").show();			
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
        +"&usergroupType="+$("#currentusergroupType").val()
        +"&deviceType="+$("#motionType").val();
		$.get('refentity/appropriationbillmotion/init?'+params,function(data){
			$.unblockUI();			
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#referencingResultDiv").html(data);
			$("#referencingResultDiv").show();
			$("#clubbingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToMotionDiv").show();			
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
		+"&approrpiationBillMotionType="+$("#selectedApprorpiationBillMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='appropriationbillmotion/'+id+'/edit?'+parameters;
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
		var params="appropriationbillmotion="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
		var resourceURL='ref/appropriationbillmotion/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='appropriationbillmotion_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='appropriationbillmotion_recommend_discuss']").text();		
		$.get(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					if(i!=0){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}else{
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
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
	/**** Load Sub Departments ****/
	function loadSubDepartments(ministry){
		$.get('ref/ministry/subdepartments?ministry=' + ministry + '&session='+$('#session').val(),
				function(data){
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
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
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
		
		/**** Back To motion ****/
		$("#backToMotion").click(function(){
			$("#clubbingResultDiv").hide();
			$("#referencingResultDiv").hide();
			//$("#backTomotionDiv").hide();
			$("#assistantDiv").show();
			/**** Hide update success/failure message on coming back to motion ****/
			$(".toolTip").hide();
		});
		/**** Ministry Changes ****/
		$("#ministry").change(function(){
			//console.log($("#subDepartment").val());
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			}
		});		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('appropriationbillmotion/citations/'+$("#deviceType").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});	
		var demandNumberWithoutSpace = $('#demandNumber').val().replace(/ /g,''); //added in order to remove spaces in between.. to be removed if populated through master entries
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace($('#specialDashCharacter').val(), "-");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace(",", "");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace("'", "");
		$('#demandNumber').val(demandNumberWithoutSpace);
		
		/**** Allow edit total amount demanded on click of revise link ****/
		$('#reviseAmountDemanded').click(function(){
			if($('#setAmountDemanded').is('[readonly]')) {
				$('#setAmountDemanded').removeAttr('readonly');
				$('#setAmountDemanded').val($('#totalAmountDemanded').val());
			} else {
				$('#setAmountDemanded').trigger('change');
			}			
		});
		$('#setAmountDemanded').change(function(){			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get('ref/appropriationbillmotion/updatedAmountDemanded?value='+$('#setAmountDemanded').val(), function(data) {
				if(data.length>0) {
					$('#totalAmountDemanded').val($('#setAmountDemanded').val());
					$('#setAmountDemanded').val(data);
					$('#setAmountDemanded').attr('readonly', 'readonly');
					$.unblockUI();
				} else {
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
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
		});
		/**** Revise mainTitle and text****/
		$("#reviseMainTitle").click(function(){
			$(".revise1").toggle();
			//console.log("revise1: " + $("#revisedMainTitleDiv").css("display") + ": "+$("#mainTitle").val());
			if($("#revisedMainTitleDiv").css("display")=="none"){
				$("#revisedMainTitle").val("");	
			}else{
				$("#revisedMainTitle").val($("#mainTitle").val());
			}						
			return false;			
		});
		
		$("#reviseSecondaryTitle").click(function(){
			$(".revise2").toggle();
			//console.log("revise2: " + $("#revisedSecondaryTitleDiv").css("display") + ": "+$("#secondaryTitle").val());
			if($("#revisedSecondaryTitleDiv").css("display")=="none"){
				$("#revisedSecondaryTitle").val("");	
			}else{
				$("#revisedSecondaryTitle").val($("#secondaryTitle").val());
			}						
			return false;			
		});	
		
		$("#reviseSubTitle").click(function(){
			$(".revise3").toggle();
			//console.log("revise3: " + $("#revisedSubTitleDiv").css("display")  + ": "+$("#subTitle").val());
			if($("#revisedSubTitleDiv").css("display")=="none"){
				$("#revisedSubTitle").val("");	
			}else{
				$("#revisedSubTitle").val($("#subTitle").val());
			}						
			return false;			
		});	
		
		$("#reviseNoticeContent").click(function(){
			$(".revise4").toggle();		
			//console.log("revise4: " + $("#revisedNoticeContentDiv").css("display") + ": "+$("#noticeContent").val());
			if($("#revisedNoticeContentDiv").css("display")=="none"){
				$("#revisedNoticeContent").wysiwyg("setContent","");
			}else{
				$("#revisedNoticeContent").wysiwyg("setContent",$("#noticeContent").val());				
			}				
			return false;			
		});			
		
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('appropriationbillmotion/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Contact Details ****/
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("#selectedSupportingMembers").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('appropriationbillmotion/members/contacts?members='+members,function(data){
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
	    $("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
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
			/**validate formatting in notice o */
			var startworkflowAllowed = true;
			if($("#revisedNoticeContent").val()!=null && $("#revisedNoticeContent").val()!="" && $("#revisedNoticeContent").val()!="<p></p>"){								
				if($('#revisedNoticeContent').val().toLowerCase().indexOf("mso") >= 0 || $('#revisedNoticeContent').val().toLowerCase().indexOf("w:") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;						
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("o:p") >= 0){
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;					
				} if($('#revisedNoticeContent').val().toLowerCase().indexOf("ol style=") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;				
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("&lt;ol&gt;&lt;/ol&gt;") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;				
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("<ol></ol>") >= 0){
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;			
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("br style=") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;				
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("-webkit-text-stroke-widt") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;					
				} else if($('#revisedNoticeContent').val().toLowerCase().indexOf("font-size: small") >= 0){	
					startworkflowAllowed = false;
					$.prompt($('#noInvalidFormattingInRevisedDeviceTextPrompt').val());
					return false;					
				}
			} else {
				if($("#noticeContent").val()!=null && $("#noticeContent").val()!=""){			
					if($('#noticeContent').val().toLowerCase().indexOf("mso") >= 0 || $('#noticeContent').val().toLowerCase().indexOf("w:") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;						
					} else if($('#noticeContent').val().toLowerCase().indexOf("o:p") >= 0){
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;					
					} if($('#noticeContent').val().toLowerCase().indexOf("ol style=") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;				
					} else if($('#noticeContent').val().toLowerCase().indexOf("&lt;ol&gt;&lt;/ol&gt;") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;				
					} else if($('#noticeContent').val().toLowerCase().indexOf("<ol></ol>") >= 0){
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false				
					} else if($('#noticeContent').val().toLowerCase().indexOf("br style=") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;				
					} else if($('#noticeContent').val().toLowerCase().indexOf("-webkit-text-stroke-widt") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;					
					} else if($('#noticeContent').val().toLowerCase().indexOf("font-size: small") >= 0){	
						startworkflowAllowed = false;
						$.prompt($('#noInvalidFormattingInDeviceTextPrompt').val());
						return false;					
					}
				}
			}
			if(startworkflowAllowed) {
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
			var reply = $('#reply').val();
			if(reply!=undefined && reply!='' && reply!='-' 
					&& reply!='<p>-</p>' && reply!='<p></p>' && reply!='<p></p>-' && reply!='-<p></p>'
					&& reply!='<br><p></p>' && reply!='<p><br></p>' && reply!='-<br><p></p>'
					&& reply!='<p>-<br></p>' && reply!='<p><br>-</p>') {
				if($('#replyRequestedDate').val()=='') {
					$.prompt("Please update reply requested date for the appropriationbillmotion!");
					return false;
				} else {
					if($('#replyReceivedDate').val()=='') {
						$.prompt("Please update reply received date for the appropriationbillmotion!");
						return false;
					}
				}
			}			
		});
	    /**** Right Click Menu ****/
		$(".clubbedRefMotions").contextMenu({
	        menu: 'contextMenuItems'
	    },
	        function(action, el, pos) {
			var id=$(el).attr("id");
			if(action=='unclubbing'){
				if(id.indexOf("cq")!=-1){
				var motionId=$("#id").val();
				var clubId=id.split("cq")[1];				
				$.post('clubentity/unclubbing?pId='+motionId+"&cId="+clubId+"&whichDevice=motions_appropriationbillmotion_",function(data){
					if(data=='UNCLUBBING_SUCCESS'){
						$.prompt("Unclubbing Successful");				
					}else{
						$.prompt("Unclubbing Failed");
					}		
					refreshEdit(motionId);
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
				var motionId=$("#id").val();
				var refId=id.split("rq")[1];				
				$.post('refentity/appropriationbillmotion/dereferencing?pId='+motionId+"&rId="+refId,function(data){
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
		//$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		//$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		if($("#revisedMainTitle").val()!=''){
		    $("#revisedMainTitleDiv").show();
	    }
		if($("#revisedSecondaryTitle").val()!=''){
		    $("#revisedSecondaryTitleDiv").show();
	    }
		if($("#revisedSubTitle").val()!=''){
		    $("#revisedSubTitleDiv").show();
	    }
	    if($("#revisedNoticeContent").val()!=''){
	    	$("#revisedNoticeContentDiv").show();
	    }  

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
	});
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        .imageLink{
			width: 18px;
			height: 18px;				
				/* box-shadow: 2px 2px 5px #000000;
				border-radius: 5px;
				padding: 2px;
				border: 1px solid #000000; */ 
				display: block;
			position: absolute;
			top: 50%;
			left: 50%;
			min-height: 100%;
			min-width: 100%;
			transform: translate(-50%, -50%);
		}
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
<form:form action="appropriationbillmotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
		${formattedMotionType}: ${formattedNumber}
		<c:choose>
			<c:when test="${not empty yaadiDetailsText}">
				&nbsp;&nbsp;(${yaadiDetailsText})
			</c:when>
			<c:otherwise></c:otherwise>
		</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="appropriationbillmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="appropriationbillmotion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="appropriationbillmotion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="appropriationbillmotion.appropriationbillmotionType" text="Type"/>*</label>
		<input id="formattedApprorpiationBillMotionType" name="formattedApprorpiationBillMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
		<input id="deviceType" name="deviceType" value="${motionType}" type="hidden">		
		<form:errors path="deviceType" cssClass="validationError"/>		
	</p>
		
	<p>
		<p style="display: inline;">
			<label class="small"><spring:message code="appropriationbillmotion.number" text="Motion Number"/>*</label>
			<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
			<input id="number" name="number" value="${domain.number}" type="hidden">
			<form:errors path="number" cssClass="validationError"/>		
		</p>
			
		<p style="display: inline;">		
			<label class="small"><spring:message code="appropriationbillmotion.submissionDate" text="Submitted On"/></label>
			<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		</p>
	</p>
	
	<p>
		<p style="display: inline;">
			<label class="small"><spring:message code="appropriationbillmotion.amountDemanded" text="Demanded Amount"/>*</label>
			<input id="setAmountDemanded" name="setAmountDemanded" type="text" class="sText" value="${formattedAmountDemanded}" readonly="readonly"/>
			<a href="#" id="reviseAmountDemanded" style="margin-left: 18px;position: relative;text-decoration: none;">
				<img id="reviseAmountDemanded_icon" src="./resources/images/Revise.jpg" title="<spring:message code='appropriationbillmotion.reviseAmountDemanded' text='Revise Total Amount Demanded'></spring:message>" class="imageLink" />
			</a>
			<input type="hidden" id="totalAmountDemanded" value="${domain.amountDemanded}">
			<form:errors path="amountDemanded" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</p>
	
	<p>
		<p style="display: inline;">
			<label class="small"><spring:message code="appropriationbillmotion.pageNumber" text="Page Number" /></label>		
			<input id="pageNumber" name="pageNumber" value="${domain.pageNumber}" type="text" class="sText integer">
			<form:errors path="pageNumber" cssClass="validationError"/>		
		</p>
			
		<p style="display: inline;">		
			<label class="small"><spring:message code="appropriationbillmotion.demandNumber" text="Demand Number"/></label>
			<input id="demandNumber" name="demandNumber" value="${formater.formatNumbersInGivenText(domain.demandNumber, domain.locale)}" type="text" class="sText">
			<form:errors path="demandNumber" cssClass="validationError"/>	
		</p>
	</p>
		
	<c:if test="${selectedMotionType=='motions_appropriationbillmotion_supplementary'}">
		<p>		
			<label class="small"><spring:message code="appropriationbillmotion.itemNumber" text="Item Number"/></label>
			<input id="itemNumber" name="itemNumber" value="${domain.itemNumber}" type="text" class="sText integer">
			<form:errors path="itemNumber" cssClass="validationError"/>	
		</p>
	</c:if>
	
	<p>
		<p style="display: inline;">
			<label class="small"><spring:message code="appropriationbillmotion.ministry" text="Ministry"/>*</label>
			<select name="ministry" id="ministry" class="sSelect">
				<option value=""><spring:message code='please.select' text='Please Select'/></option>
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
			<input type="hidden" name="department" value="${domain.department.id}"/>
		</p>	
		
		<p style="display: inline;">
			<label class="small"><spring:message code="generic.subdepartment" text="Department"/></label>
			<select name="subDepartment" id="subDepartment" class="sSelect">
				<option value=""><spring:message code='please.select' text='Please Select'/></option>
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
	</p>
	
	<p>
		<label class="centerlabel"><spring:message code="generic.members" text="Members"/></label>
		<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50" style="width: 536px; height: 55px;">${memberNames}</textarea>
		<c:if test="${!(empty primaryMember)}">
			<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
	    <select  name="selectedSupportingMembers" id="selectedSupportingMembers" multiple="multiple" style="display:none;">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" selected="selected"></option>
			</c:forEach>		
			</select>
		</c:if>	
	</p>
	
	<p>
		<label class="small"><spring:message code="generic.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<%-- <p>
		<c:if test="${bulkedit!='yes'}">	
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="generic.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="generic.refresh" text="Refresh"></spring:message></a>
		</c:if>	
	</p> --%>
		
	<%-- <c:if test="${!(empty parent)}">	
		<p>
			<label class="small"><spring:message code="appropriationbillmotion.parentmotion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" onclick="viewmotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
			<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>
	</c:if> --%>	
	
	<%-- <p>
		<label class="small"><spring:message code="generic.clubbed" text="Clubbed Motions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedEntities) }">
				<c:choose>
					<c:when test="${!(empty clubbedEntities) }">
						<c:forEach items="${clubbedEntities }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
				<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
					<c:forEach items="${clubbedEntities }" var="i">
						<option value="${i.id}" selected="selected"></option>
					</c:forEach>
				</select>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
	</p> --%>
		
	<%-- <p>
		<label class="small"><spring:message code="appropriationbillmotion.referencedmotions" text="Referenced Motions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedMotions) }">
				<c:choose>
					<c:when test="${!(empty referencedMotions) }">
						<c:forEach items="${referencedMotions }" var="i">
							<a href="#" id="rq${i.number}" class="clubbedRefMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>		
	</p> --%>
		
	<%-- <c:if test="${!(empty referencedQuestions) }">		
		<p>
			<label class="small"><spring:message code="motion.referencedquestions" text="Referenced Questions"></spring:message></label>
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
		</p>
	</c:if> --%>
		
	<%-- <c:if test="${!(empty referencedResolutions) }">
		<p>
			<label class="small"><spring:message code="motion.referencedmotions" text="Referenced Resolutions"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty referencedResolutions) }">
					<c:forEach items="${referencedResolutions }" var="i">
						<a href="#" id="rq${i.number}" class="clubbedRefMotions" onclick="viewResolutionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>		
		</p>
	</c:if> --%>
		
	<%-- <c:if test="${!(empty referencedEntities) }">
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedEntities }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</c:if> --%>
	
	<p>	
		<label class="centerlabel"><spring:message code="appropriationbillmotion.mainTitle" text="Main Title"/></label>
		<form:textarea path="mainTitle" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="mainTitle" cssClass="validationError"/>	
	</p>
	
	<c:if test="${selectedMotionType=='motions_appropriationbillmotion_budgetary'}">
		<p style="display: none;">	
			<label class="centerlabel"><spring:message code="appropriationbillmotion.secondaryTitle" text="Secondary Title"/></label>
			<form:textarea path="secondaryTitle" readonly="true" rows="2" cols="50"></form:textarea>
			<form:errors path="secondaryTitle" cssClass="validationError"/>	
		</p>	
	</c:if>

	<p>	
		<label class="centerlabel"><spring:message code="appropriationbillmotion.subTitle" text="Sub Title"/></label>
		<form:textarea path="subTitle" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="subTitle" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="appropriationbillmotion.noticeContent" text="Details"/></label>
		<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="noticeContent" cssClass="validationError"/>	
	</p>	
	
	<p>
		<a href="#" id="reviseMainTitle" style="margin-left: 162px;margin-right: 20px;"><spring:message code="appropriationbillmotion.reviseMainTitle" text="Revise Main Title"></spring:message></a>
		<a href="#" id="reviseSubTitle" style="margin-right: 20px;"><spring:message code="appropriationbillmotion.reviseSubTitle" text="Revise Sub Title"></spring:message></a>
		<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="appropriationbillmotion.reviseNoticeContent" text="Revise Content"></spring:message></a>
		<a href="#" id="viewRevision"><spring:message code="appropriationbillmotion.viewrevisions" text="View Revisions"></spring:message></a>
	</p>	
	
	<p style="display:none;" class="revise1" id="revisedMainTitleDiv">
		<label class="centerlabel"><spring:message code="appropriationbillmotion.revisedSubject" text="Revised Main Title"/></label>
		<form:textarea path="revisedMainTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedMainTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise3" id="revisedSubTitleDiv">
		<label class="centerlabel"><spring:message code="appropriationbillmotion.revisedSubTitle" text="Revised Sub Title"/></label>
		<form:textarea path="revisedSubTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedSubTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise4" id="revisedNoticeContentDiv">
		<label class="wysiwyglabel"><spring:message code="appropriationbillmotion.revisedContent" text="Revised Content"/></label>
		<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
		<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv" style="margin-top: 20px;">
		<label class="small"><spring:message code="generic.status" text="Current Status"/></label>
		<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:if test="${internalStatusType=='appropriationbillmotion_system_assistantprocessed'}">
		<p>	
			<label class="small"><spring:message code="generic.putupfor" text="Put up for"/></label>	
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
			<label class="small"><spring:message code="generic.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
		</p>
	</c:if>
	
	<c:if test="${fn:contains(internalStatusType, 'appropriationbillmotion_final')}">
		<form:hidden path="actor"/>
	</c:if>
	
	<c:choose>
		<c:when test="${internalStatusType eq 'appropriationbillmotion_final_admission'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="appropriationbillmotion.reply" text="Reply"/></label>
			<form:textarea path="reply" cssClass="wysiwyg"></form:textarea>
			<form:errors path="reply" cssClass="validationError"></form:errors>
		</p>
		</c:when>
		<c:otherwise>
			<form:hidden path="reply"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${not empty domain.rejectionReason}">
		<p>
			<label class="wysiwyglabel"><spring:message code="appropriationbillmotion.rejectionReason" text="Reply"/></label>
			<form:textarea path="rejectionReason" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="rejectionReason" cssClass="validationError"></form:errors>
		</p>
		</c:when>
		<c:otherwise>
			<form:hidden path="rejectionReason"/>
		</c:otherwise>
	</c:choose>		
	
	<c:choose>
		<c:when test="${internalStatusType eq 'appropriationbillmotion_final_admission'}">
			<p>
				<label class="small"><spring:message code="appropriationbillmotion.replyRequestedDate" text="Reply Requested Date"/></label>
				<input id="replyRequestedDate" name="setReplyRequestedDate" class="datetimemask sText" value="${formattedReplyRequestedDate}"/>
			</p>
			<p>
				<label class="small"><spring:message code="appropriationbillmotion.replyReceivedDate" text="Reply Received Date"/></label>
				<input id="replyReceivedDate" name="setReplyReceivedDate" class="datetimemask sText" value="${formattedReplyReceivedDate}"/>
			</p>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty formattedReplyRequestedDate}">
				<input type="hidden" id="replyRequestedDate" name="setReplyRequestedDate" class="datetimemask sText" value="${formattedReplyRequestedDate}"/>
			</c:if>
			<c:if test="${not empty formattedReplyReceivedDate}">
				<input type="hidden" id="replyReceivedDate" name="setReplyReceivedDate" class="datetimemask sText" value="${formattedReplyReceivedDate}"/>
			</c:if>
		</c:otherwise>
	</c:choose>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="generic.remarks" text="Remarks"/></label>
		<a href="#" id="viewCitation" style="display: inline; margin-left: 530px;"><spring:message code="appropriationbillmotion.viewcitation" text="View Citations"></spring:message></a>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">		
			<c:if test="${bulkedit!='yes'}">
				<%-- <c:if test="${internalStatusType=='appropriationbillmotion_submit' || internalStatusType=='appropriationbillmotion_system_assistantprocessed' || fn:contains(internalStatusType, 'appropriationbillmotion_final')}">
					<security:authorize access="hasAnyRole('ABMOIS_CLERK','ABMOIS_ASSISTANT')">
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					</security:authorize>
					
				</c:if> --%>
				<security:authorize access="hasAnyRole('ABMOIS_CLERK','ABMOIS_ASSISTANT')">
						<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					</security:authorize>
				<c:if test="${internalStatusType=='appropriationbillmotion_system_assistantprocessed' || fn:contains(internalStatusType, 'appropriationbillmotion_putup')}">
					<security:authorize access="hasAnyRole('ABMOIS_ASSISTANT')">
						<input id="startworkflow" type="button" value="<spring:message code='appropriationbillmotion.putupmotion' text='Put Up Motion'/>" class="butDef">
					</security:authorize>
				</c:if>
			</c:if>
			<c:if test="${bulkedit=='yes'}">
				<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
			</c:if>
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
	<form:hidden path="transferToDepartmentAccepted"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="reasonForLateReply"/>
	<c:if test="${not empty formattedLastReplyReceivingDate}">
		<input type="hidden" id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
	</c:if>
	<input type="hidden" id="replyReceivedMode" name="replyReceivedMode" value="${domain.replyReceivedMode}"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">		
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmMotionSubmission" value="<spring:message code='confirm.appropriationbillmotionsubmission.message' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='motion.startworkflowmessage' text='Do You Want To Put Up motion'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ recommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="motionType" type="hidden" value="${selectedMotionType}" />

<ul id="contextMenuItems" >
<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
</ul>
</div>

</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingResultDiv" style="display:none;">
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>