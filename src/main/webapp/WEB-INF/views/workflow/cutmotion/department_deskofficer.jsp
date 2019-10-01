<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced motions ****/		
	function viewMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&motionType="+$("#selectedMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='cutmotion/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html').fail(function(){
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
		$.get('refentity/motion/init?'+params,function(data){
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
		+"&motionType="+$("#selectedMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='motion/'+id+'/edit?'+parameters;
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
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(value!='-'){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			
			var sendback=$("#internalStatusMaster option[value='adjournmentmotion_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='adjournmentmotion_recommend_discuss']").text();
		    var sendToSectionOfficer=$("#internalStatusMaster option[value='adjournmentmotion_processed_sendToSectionOfficer']").text();
			var sendToDeskOfficer=$("#internalStatusMaster option[value='adjournmentmotion_processed_sendToDeskOfficer']").text();
		    
		    //reset endflag value to continue by default..then in special cases to end workflow we will set it to end
		    $("#endFlag").val("continue");
		    
		    var valueToSend = "";
			if(value == sendToSectionOfficer) {
				valueToSend = $("#internalStatus").val();
				if(($("#lastDateForReplyReceiving").val()!='') && (new Date()> new Date($("#lastDateForReplyReceiving").val()))){
					$("#lateReplyReasonDiv").css("display","block");
				}
			}else if(value == sendToDeskOfficer) {
				valueToSend = $("#internalStatus").val();
				$("#lateReplyReasonDiv").css("display","none");
			}else{
				valueToSend = value;
				$("#lateReplyReasonDiv").css("display","none");
			}
			
			/* hide submit for sending reply in case of late reply filling validation */
			if(value == sendToSectionOfficer
					&& $("#lateReplyFillingFlag").val()=="set"
					&& $("#internalStatusType").val()=="adjournmentmotion_final_admission") {
				$('#submit').hide();
			}else{
				$('#submit').show();
			}
			
			var params="motion="+$("#id").val()+"&status="+valueToSend+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
			var resourceURL='ref/cutmotion/actors?'+params;
		    		
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					var length=data.length;
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						var ugtActor = data[i].id.split("#")
						var ugt = ugtActor[1];
						if(ugt!='member' && data[i].state!='active'){
							text += "<option value='" + data[i].id + "' disabled='disabled'>" + data[i].name  +"("+ugtActor[4]+")"+ "</option>";
						}else if(ugt == 'section_officer'){
							text += "<option value='" + data[i].id +"'>" + data[i].name  + " ( "+$("#formattedHouseType").val() + " )" + "</option>";
							if(actCount == 1){
								actor1=data[i].id;
								console.log(actor1);
								actCount++;
							}
						}else{
							text += "<option value='" + data[i].id + "'>" + data[i].name  +"("+ugtActor[4]+")"+ "</option>";	
							if(actCount == 1){
								actor1=data[i].id;
								console.log(actor1);
								actCount++;
							}
						}
					}				
					$("#actor").html(text);
					$("#actorDiv").show();								
					if(value ==sendToDeskOfficer){
						$("#replyP").css("display","none");
						 $("#actorDiv").show();
					}else{
						$("#replyP").css("display","inline-block");
					}
					/**** in case of department user, only recommendation status is changed ****/
					$("#internalStatus").val($("#oldInternalStatus").val());
					$("#recommendationStatus").val(value);	
					/**** setting level,localizedActorName ****/
					 var actor1=data[0].id;
					 var temp=actor1.split("#");
					 $("#level").val(temp[2]);		    
					 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");					
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					/**** in case of department user, only recommendation status is changed ****/
					$("#internalStatus").val($("#oldInternalStatus").val());
					$("#recommendationStatus").val(value);
					if(value ==sendToDeskOfficer){
						$("#replyP").css("display","none");
						 $("#actorDiv").show();
					}else{
						$("#replyP").css("display","inline-block");
					}										
					if(value == sendToSectionOfficer){
						alert("No Section Officer Found!");
						$('#submit').attr('disabled', 'disabled');
					} else if(value == sendToDeskOfficer){
						alert("No Desk Officer Found!");
						$('#submit').attr('disabled', 'disabled');
					}
				}
				$.unblockUI();
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
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
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
		initControls();
		loadActors($("#changeInternalStatus").val());
		if($('#workflowstatus').val()=="PENDING") {
			$("#replyP").hide();			
		}
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
			console.log($("#subDepartment").val());
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			}
		});	
		$('#subDepartment').change(function(){
			$("#subdepartmentValue").text($("#subDepartment option:selected").text() +"  "+ $("#subdepartmentValue").text());
 		});		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('motion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		$("#reviseDetails").click(function(){
			$(".revise2").toggle();		
			if($("#revisedDetailsDiv").css("display")=="none"){
				$("#revisedDetails").wysiwyg("setContent","");
			}else{
				$("#revisedDetails").wysiwyg("setContent",$("#details").val());				
			}				
			return false;			
		});			
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('motion/revisions/'+$("#id").val(),function(data){
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
		    $.get('motion/members/contacts?members='+members,function(data){
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
			  //  $("#submit").attr("disabled","disabled");
			    //$("#startworkflow").removeAttr("disabled");		    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    //$("#startworkflow").attr("disabled","disabled");
			   // $("#submit").removeAttr("disabled");
			}		    
	    });
	    $("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
	    });
	  	//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide(); 
		
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
		
		/********Submit Click*********/
		$('#submit').click(function(){					
			if($('#changeInternalStatus').val()=="-") {
				$.prompt("Please select the action");
				return false;
			}			
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			if($('#isTransferable').is(':checked')) {
				$('#isTransferable').val(true);		   	    
			} else { 				
				$('#isTransferable').val(false);				
		   	};
			if($('#transferToDepartmentAccepted').is(':checked')) {
				$('#transferToDepartmentAccepted').val(true);		   	    
			} else { 				
				$('#transferToDepartmentAccepted').val(false);				
		   	};
		   	if($('#mlsBranchNotifiedOfTransfer').is(':checked')) {
				$('#mlsBranchNotifiedOfTransfer').val(true);		   	    
			} else { 				
				$('#mlsBranchNotifiedOfTransfer').val(false);				
		   	};
		   	var sendToSectionOfficer=$("#internalStatusMaster option[value='adjournmentmotion_processed_sendToSectionOfficer']").text();
			var changedInternalStatus = $("#changeInternalStatus").val();
			if(changedInternalStatus == sendToSectionOfficer) {
				if(($('#reply').val()=="" || ($("#workflowstatus").val()=='COMPLETED' && $('#rereply').val()==""))){
					$.prompt($('#noReplyProvidedMsg').val());
					return false;
				}
				if(/*$('#houseTypeType').val()=='upperhouse' &&*/ $("#workflowstatus").val()=='PENDING'
						&& $("#lastDateForReplyReceiving").val()!=''
						&& new Date()> new Date($("#lastDateForReplyReceiving").val())  && $('#reasonForLateReply').val()==""){
					$.prompt($('#noLateReplyReasonProvidedMsg').val());
					return false;
				}
			}
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
			return false;			
		});
		
	    /**** On page Load ****/
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

		if($('#workflowstatus').val()!='COMPLETED'){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value$='"+statusType[statusType.length-1]+"']").text();
			$("#changeInternalStatus").val(id);
			$("#changeInternalStatus").change();
			//loadActors($("#changeInternalStatus").val());
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
<form:form action="workflow/motion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedMotionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<input id="houseTypeType" name="houseTypeType" value="${houseTypeType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.type" text="Type"/>*</label>
		<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${motionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
	<label class="small"><spring:message code="motion.number" text="Motion Nmber"/>*</label>
	<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
	<input id="number" name="number" value="${domain.number}" type="hidden">
	<form:errors path="number" cssClass="validationError"/>		
	</p>
		
	<p style="display:none;">
		<label class="small"><spring:message code="cutmotion.submissionDate" text="Submitted On"/></label>
		<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
	</p>
	
	<p>	
		<label class="small"><spring:message code="cutmotion.task.creationtime" text="Task Created On"/></label>
		<input id="createdTime" name="createdTime" value="${taskCreationDate}" class="sText datetimemask" readonly="readonly">
		<label class="small"><spring:message code="cutmotion.lastDateFromDepartment" text="Last Date From Department"/></label>
		<input id="formattedLastReplyReceivingDate" name="formattedLastReplyReceivingDate" class="datemask sText" value="${formattedLastReplyReceivingDate}" readonly="readonly"/>
		<input type="hidden" id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
		<form:errors path="lastDateOfReplyReceiving" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="cutmotion.isTransferable" text="is adjournment motion to be transfered?"/></label>
		<input type="checkbox" name="isTransferable" id="isTransferable" class="sCheck">
	</p>
	
	<p>
		<label class="small"><spring:message code="motion.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect" style="width: 270px;">
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
	</p>	
	
	<p>
	<label class="small"><spring:message code="motion.subdepartment" text="Sub Department"/></label>
	<select name="subDepartment" id="subDepartment" class="sSelect" style="width: 270px;">
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
	
	<p id="transferP" style="display:none;">
		<label class="small" id="subdepartmentValue"><spring:message code="adjournmentmotion.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted?"/></label>
		<input type="checkbox" id="transferToDepartmentAccepted" name="transferToDepartmentAccepted" class="sCheck"/>
		
		<label class="small" style="margin-left: 175px;"><spring:message code="adjournmentmotion.mlsBranchNotified" text="Is the Respective Adjournment Motion Branch Notified?"/></label>
		<input type="checkbox" id="mlsBranchNotifiedOfTransfer" name="mlsBranchNotifiedOfTransfer" class="sCheck"/>
	</p>
	
	<p>
	<label class="centerlabel"><spring:message code="motion.members" text="Members"/></label>
	<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
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
		<label class="small"><spring:message code="motion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<p>
		<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="motion.clubbing" text="Clubbing"></spring:message></a>
		<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="motion.referencing" text="Referencing"></spring:message></a>
		<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="motion.refresh" text="Refresh"></spring:message></a>	
	</p>	
		
	<c:if test="${!(empty parent)}">	
	<p>
		<label class="small"><spring:message code="motion.parentmotion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewmotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>
	</c:if>	
	<c:if test="${!(empty clubbedEntities) }">
	<p>
		<label class="small"><spring:message code="motion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
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
	</p>
	</c:if>
		
	<c:if test="${!(empty referencedMotions) }">		
		<p>
		<label class="small"><spring:message code="motion.referencedmotions" text="Referenced Motions"></spring:message></label>
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
		</p>
	</c:if>
		
	<c:if test="${!(empty referencedQuestions) }">		
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
	</c:if>
		
	<c:if test="${!(empty referencedResolutions) }">
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
	</c:if>
		
	<c:if test="${!(empty referencedEntities) }">
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
		<c:forEach items="${referencedEntities }" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>
		</select>
	</c:if>
	
	<p>	
	<label class="centerlabel"><spring:message code="motion.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>	
	
	<p>
	<label class="wysiwyglabel"><spring:message code="motion.details" text="Details"/></label>
	<form:textarea path="details" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="details" cssClass="validationError"/>	
	</p>	
	
	<p>
		<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="motion.reviseSubject" text="Revise Subject"></spring:message></a>
		<a href="#" id="reviseDetails" style="margin-right: 20px;"><spring:message code="motion.reviseDetails" text="Revise Details"></spring:message></a>
		<a href="#" id="viewRevision"><spring:message code="motion.viewrevisions" text="View Revisions"></spring:message></a>
	</p>	
	
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="motion.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedDetailsDiv">
	<label class="wysiwyglabel"><spring:message code="motion.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedDetails" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedDetails" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="motion.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<p style="display:none;">
	<label class="small"><spring:message code="motion.putupfor" text="Put up for"/></label>	
	<select id="changeInternalStatus" class="sSelect">
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
	
	<p id="actorDiv" style="display:none;">
	<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
	<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
	</p>	
		
	<p>
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="motion.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<c:choose>
	<c:when test="${workflowstatus=='COMPLETED'}">
		<p id="replyP">
			<label class="wysiwyglabel"><spring:message code="adjournmentmotion.reply" text="Reply"/></label>
			<form:textarea path="reply" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="reply" cssClass="validationError"></form:errors>
		</p>
	</c:when>
	<c:otherwise>
		<p id="replyP">
			<label class="wysiwyglabel"><spring:message code="adjournmentmotion.reply" text="Reply"/></label>
			<form:textarea path="reply" cssClass="wysiwyg"></form:textarea>
			<form:errors path="reply" cssClass="validationError"></form:errors>
		</p>
	</c:otherwise>
	</c:choose>
	
	<p id="lateReplyReasonDiv" style="display:none;">
		<label class="wysiwyglabel"><spring:message code="adjournmentmotion.reasonForLateReply" text="Reason for Late Reply"/></label>
		<form:textarea path="reasonForLateReply" cssClass="wysiwyg"></form:textarea>
		<form:errors path="reasonForLateReply" cssClass="validationError"></form:errors>
	</p>	
	
	<p>
	<label class="wysiwyglabel"><spring:message code="motion.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
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
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level" value="${level}"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>	
	<form:hidden path="rejectionReason"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
	<c:if test="${not empty formattedReplyRequestedDate}">
		<input type="hidden" id="replyRequestedDate" name="setReplyRequestedDate" class="datetimemask sText" value="${formattedReplyRequestedDate}"/>
	</c:if>
	<c:if test="${not empty formattedReplyReceivedDate}">
		<input type="hidden" id="replyReceivedDate" name="setReplyReceivedDate" class="datetimemask sText" value="${formattedReplyReceivedDate}"/>
	</c:if>
	<input type="hidden" id="lateReplyFillingFlag" name="lateReplyFillingFlag" value="${lateReplyFillingFlag}"/>
	<input type="hidden" id="lastDateForReplyReceiving" value="${lastDateOfReplyReceiving}"/>
	<input id="noLateReplyReasonProvidedMsg" value='<spring:message code="client.error.nolatereplyreason" text="Please provide reason for Late Reply"></spring:message>' type="hidden" />
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmMotionSubmission" value="<spring:message code='confirm.motionsubmission.message' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='motion.startworkflowmessage' text='Do You Want To Put Up motion'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="motionType" type="hidden" value="${selectedMotionType}" />
<input id="internalStatusType" type="hidden" value="${internalStatusType}"/>
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input type="hidden" id="originalLevel" value="${level}" />
<input id="submissionMsg" value="<spring:message code='adjournmentmotion.submitForReply' text='Do you want to submit for the reply of adjournment motion?'></spring:message>" type="hidden">

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