<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced motions ****/		
	function viewCutMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&cutMotionType="+$("#selectedCutMotionType").val()
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
		$.get('refentity/cutmotion/init?'+params,function(data){
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
		+"&cutMotionType="+$("#selectedCutMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='cutmotion/'+id+'/edit?'+parameters;
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
			var sendToSectionOfficer=$("#internalStatusMaster option[value='cutmotion_processed_sendToSectionOfficer']").text();
			var sendToDeskOfficer=$("#internalStatusMaster option[value='cutmotion_processed_sendToDeskOfficer']").text();
		    
		    //reset endflag value to continue by default..then in special cases to end workflow we will set it to end
		    $("#endFlag").val("continue");
		    
		    var valueToSend = "";
			if(value == sendToSectionOfficer ) {
				valueToSend = $("#internalStatus").val();
				if(($("#lastDateForReplyReceiving").val()!='') && (new Date()> new Date($("#lastDateForReplyReceiving").val()))){
					$("#lateReplyReasonDiv").css("display","block");
				}
			}else{
				valueToSend = value;
				$("#lateReplyReasonDiv").css("display","none");
			}
			
			/* hide submit for sending reply in case of late reply filling validation */
			if(value == sendToSectionOfficer
					&& $("#lateReplyFillingFlag").val()=="set"
					&& $("#internalStatusType").val()=="cutmotion_final_admission") {
				$('#submit').hide();
			}else{
				$('#submit').show();
			}
			
		    var params="cutmotion=" + $("#id").val() + "&status=" + valueToSend +
			"&usergroup=" + $("#usergroup").val() + "&level=" + $("#level").val();
			var resourceURL = 'ref/cutmotion/actors?' + params;
		    
			$.get(resourceURL,function(data){			
				if(data!=undefined && data!=null && data.length>0){
					var actor1="";
					var actCount = 1;
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
					if(value !=sendToSectionOfficer && value != sendToDeskOfficer){
						$("#internalStatus").val(value);
					} else {
						$("#internalStatus").val($("#oldInternalStatus").val());
					}
					if(value ==sendToDeskOfficer){
						$("#replyP").css("display","none");
						 $("#actorDiv").show();
					}else{
						$("#replyP").css("display","inline-block");
					}
					$("#recommendationStatus").val(value);
					/**** setting level,localizedActorName ****/
					 var temp = actor1.split("#");
					 $("#level").val(temp[2]);		    
					 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					if(value != sendToSectionOfficer && value != sendToDeskOfficer){
						$("#internalStatus").val(value);
					} else {
						$("#internalStatus").val($("#oldInternalStatus").val());
					}
					if(value ==sendToDeskOfficer){
						$("#replyP").css("display","none");
						 $("#actorDiv").show();
					}else{
						$("#replyP").css("display","inline-block");
					}
					$("#recommendationStatus").val(value);					
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
				$("#submit").attr("disabled","disabled");
				$("#actor").empty();
				$("#actorDiv").hide();
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
				scrollTop();
				$.unblockUI();
			});
		}else{			
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		    $.unblockUI();
		}
	}	
	/**** Load Ministries ****/
	function loadMinistries(){
		$.get('ref/session/'+$('#session').val()+'/ministries',	function(data){
			$("#ministry").empty();
			var ministryText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				ministryText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#ministry").html(ministryText);
			}else{
				$("#ministry").empty();
				var ministryText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#ministry").html(ministryText);	
			}
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
			$("#subDepartment").html(subDepartmentText);
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
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
	function resetMinistries() {
		$('#ministry').html($('#internalMinistry').html());
	}
	function resetSubDepartments() {
		$('#subDepartment').html($('#originalSubDepartment').html());
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
		//loadActors($("#changeInternalStatus").val());
		if($('#workflowstatus').val()=="PENDING") {
			$("#replyP").hide();			
		}
		var demandNumberWithoutSpace = $('#demandNumber').val().replace(/ /g,''); //added in order to remove spaces in between.. to be removed if populated through master entries
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace($('#specialDashCharacter').val(), "-");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace(",", "");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace("'", "");
		$('#demandNumber').val(demandNumberWithoutSpace);
		/**** Back To cutmotion ****/
		$("#backToMotion").click(function(){
			$("#clubbingResultDiv").hide();
			$("#referencingResultDiv").hide();
			//$("#backTomotionDiv").hide();
			$("#assistantDiv").show();
			/**** Hide update success/failure message on coming back to cutmotion ****/
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
			$.get('cutmotion/citations/'+$("#deviceType").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('cutmotion/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Contact Details ****/
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("#supportingMembersIds").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('cutmotion/members/contacts?members='+members,function(data){
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
			    $("#submit").attr("disabled","disabled");
			}		    
	    });
	    /*******Actor changes*************/
	    $("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
	  	//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		//$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide(); 
		
		$('#isTransferable').change(function() {
	        if ($(this).is(':checked')) {
	        	loadMinistries();
	        	//$("#ministry option[selected!='selected']").show();
	    		$("#subDepartment option[selected!='selected']").show(); 
	    		$("#transferP").css("display","inline-block");
	    		$("#submit").css("display","none");
	        }else{
	        	resetMinistries();
	        	resetSubDepartments();
	        	//$("#ministry option[selected!='selected']").hide();
	    		$("#subDepartment option[selected!='selected']").hide(); 
	    		$("#transferP").css("display","none");
	    		$("#submit").css("display","inline-block");
	    		$('#mlsBranchNotifiedOfTransfer').removeAttr('checked');
	    		$('#transferToDepartmentAccepted').removeAttr('checked');
	        }
	    });
		
		$('#mlsBranchNotifiedOfTransfer').change(function() {
	        if ($(this).is(':checked') && $("#isTransferable").is(':checked')) {
	        	$("#submit").css("display","inline-block");
	        } else if ($("#isTransferable").is(':checked')) {
	        	$("#submit").css("display","none");
	        }
	    });
		
		/********Submit Click*********/
		$('#submit').click(function(){					
			if($('#changeInternalStatus').val()=="-") {
				$.prompt("Please select the action");
				return false;
			}
			if($("#ministry").val()==''){
				$.prompt("Please select the ministry!");
				return false;
			}
			if($("#subDepartment").val()==''){
				$.prompt("Please select the department!");
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
			$("#ministry option[value='']").attr('selected', 'selected');
			//$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment option[value='']").attr('selected', 'selected');
			//$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}			    
		//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		//$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide(); 
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
	<form:form action="workflow/cutmotion" method="PUT" modelAttribute="domain">
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
			<label class="small"><spring:message code="cutmotion.houseType" text="House Type"/>*</label>
			<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
			<input id="houseType" name="houseType" value="${houseType}" type="hidden">
			<input id="houseTypeType" name="houseTypeType" value="${houseTypeType}" type="hidden">
			<form:errors path="houseType" cssClass="validationError"/>			
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.year" text="Year"/>*</label>
			<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
			<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.sessionType" text="Session Type"/>*</label>		
			<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
			<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
			<input type="hidden" id="session" name="session" value="${session}"/>
			<form:errors path="session" cssClass="validationError"/>	
		</p>
		
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.cutmotionType" text="Type"/>*</label>
			<input id="formattedCutMotionType" name="formattedCutMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
			<input id="deviceType" name="deviceType" value="${motionType}" type="hidden">		
			<form:errors path="deviceType" cssClass="validationError"/>		
		</p>
			
		<p>
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotion.number" text="Motion Nmber"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>		
			</p>
				
			<p style="display: inline;">		
				<label class="small"><spring:message code="cutmotion.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
			</p>
		</p>
		
		<p>
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotion.amountToBeDeducted" text="Deductible Amount"/>*</label>
				<input name="setAmountToBeDeducted" value="${formattedAmountToBeDeducted}" type="text" class="sText"/>
				<form:errors path="amountToBeDeducted" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotion.totalAmoutDemanded" text="Demanded Amount"/>*</label>
				<input id="setTotalAmoutDemanded" name="setTotalAmoutDemanded" type="text" class="sText" value="${formattedTotalAmoutDemanded}" readonly="readonly"/>
				<a href="#" id="reviseTotalAmoutDemanded" style="margin-left: 18px;position: relative;text-decoration: none;">
					<img id="reviseTotalAmoutDemanded_icon" src="./resources/images/Revise.jpg" title="<spring:message code='cutmotion.reviseTotalAmoutDemanded' text='Revise Total Amount Demanded'></spring:message>" class="imageLink" />
				</a>
				<input type="hidden" id="totalAmountDemanded" value="${domain.totalAmoutDemanded}">
				<form:errors path="totalAmoutDemanded" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</p>
		
		<p>
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotion.pageNumber" text="Page Number" /></label>		
				<input id="pageNumber" name="pageNumber" value="${domain.pageNumber}" type="text" class="sText integer">
				<form:errors path="pageNumber" cssClass="validationError"/>		
			</p>
				
			<p style="display: inline;">		
				<label class="small"><spring:message code="cutmotion.demandNumber" text="Demand Number"/></label>
				<input id="demandNumber" name="demandNumber" value="${formater.formatNumbersInGivenText(domain.demandNumber, domain.locale)}" type="text" class="sText">
				<form:errors path="demandNumber" cssClass="validationError"/>	
			</p>
		</p>
			
		<c:if test="${selectedMotionType=='motions_cutmotion_supplementary'}">
			<p>		
				<label class="small"><spring:message code="cutmotion.itemNumber" text="Item Number"/></label>
				<input id="itemNumber" name="itemNumber" value="${domain.itemNumber}" type="text" class="sText integer">
				<form:errors path="itemNumber" cssClass="validationError"/>	
			</p>
		</c:if>
		
		<p>	
			<label class="small"><spring:message code="cutmotion.task.creationtime" text="Task Created On"/></label>
			<input id="createdTime" name="createdTime" value="${taskCreationDate}" class="sText datetimemask" readonly="readonly">
			<label class="small"><spring:message code="cutmotion.lastDateFromDepartment" text="Last Date From Department"/></label>
			<input id="formattedLastReplyReceivingDate" name="formattedLastReplyReceivingDate" class="datemask sText" value="${formattedLastReplyReceivingDate}" readonly="readonly"/>
			<input type="hidden" id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
			<form:errors path="lastDateOfReplyReceiving" cssClass="validationError"/>
		</p>
		
		<p>
			<label class="small"><spring:message code="cutmotion.isTransferable" text="is cut cutmotion to be transfered?"/></label>
			<input type="checkbox" name="isTransferable" id="isTransferable" class="sCheck">
		</p>
		
		<p>
			<p style="display: inline;">
				<label class="small"><spring:message code="cutmotion.ministry" text="Ministry"/>*</label>
				<select name="ministry" id="ministry" class="sSelect">
					<option value=""><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${internalMinistries }" var="i">
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
				<select name="internalMinistry" id="internalMinistry" class="sSelect" hidden="true">
					<option value=""><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${internalMinistries }" var="i">
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
				<select name="originalSubDepartment" id="originalSubDepartment" class="sSelect" hidden="true">
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
		
		<p id="transferP" style="display:none;">
			<label class="small" id="subdepartmentValue"><spring:message code="cutmotion.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted?"/></label>
			<input type="checkbox" id="transferToDepartmentAccepted" name="transferToDepartmentAccepted" class="sCheck"/>
			
			<label class="small" style="margin-left: 175px;"><spring:message code="cutmotion.mlsBranchNotified" text="Is the Respective Cut Motion Branch Notified?"/></label>
			<input type="checkbox" id="mlsBranchNotifiedOfTransfer" name="mlsBranchNotifiedOfTransfer" class="sCheck"/>
		</p>
		
		<p>
			<label class="centerlabel"><spring:message code="generic.members" text="Members"/></label>
			<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50" style="width: 536px; height: 55px;">${memberNames}</textarea>
			<c:if test="${!(empty primaryMember)}">
				<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
			</c:if>
			<c:if test="${!(empty selectedSupportingMembersIds)}">
				<select name="supportingMembers" id="supportingMembers" multiple="multiple" style="display:none;">
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
			<label class="small"><spring:message code="cutmotion.primaryMemberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText">
			<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
		</p>			
		
		<p style="display:none;">
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="cutmotion.clubbing" text="Clubbing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="cutmotion.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="cutmotion.refresh" text="Refresh"></spring:message></a>	
		</p>	
			
		<c:if test="${!(empty parent)}">	
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.parentmotion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" onclick="viewmotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
			<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>
		</c:if>	
		<c:if test="${!(empty clubbedEntities) }">
		<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
			<c:choose>
			<c:when test="${!(empty clubbedEntities) }">
			<c:forEach items="${clubbedEntities }" var="i">
			<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewCutMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
			<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.referencedmotions" text="Referenced Motions"></spring:message></label>
			<c:choose>
			<c:when test="${!(empty referencedMotions) }">
			<c:forEach items="${referencedMotions }" var="i">
			<a href="#" id="rq${i.number}" class="clubbedRefMotions" onclick="viewCutMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
			</c:forEach>
			</c:when>
			<c:otherwise>
			<c:out value="-"></c:out>
			</c:otherwise>
			</c:choose>		
			</p>
		</c:if>
			
		<c:if test="${!(empty referencedQuestions) }">		
			<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.referencedquestions" text="Referenced Questions"></spring:message></label>
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
			<p style="display:none;">
			<label class="small"><spring:message code="cutmotion.referencedmotions" text="Referenced Resolutions"></spring:message></label>
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
		
		<p style="display:none;">
			<label class="centerlabel"><spring:message code="cutmotion.mainTitle" text="Main Title"/></label>
			<form:textarea path="mainTitle" readonly="true" rows="2" cols="50"></form:textarea>
			<form:errors path="mainTitle" cssClass="validationError"/>	
		</p>
		
		<c:if test="${selectedMotionType=='motions_cutmotion_budgetary'}">
			<p style="display: none;">	
				<label class="centerlabel"><spring:message code="cutmotion.secondaryTitle" text="Secondary Title"/></label>
				<form:textarea path="secondaryTitle" readonly="true" rows="2" cols="50"></form:textarea>
				<form:errors path="secondaryTitle" cssClass="validationError"/>	
			</p>	
		</c:if>
	
		<p style="display:none;">
			<label class="centerlabel"><spring:message code="cutmotion.subTitle" text="Sub Title"/></label>
			<form:textarea path="subTitle" readonly="true" rows="2" cols="50"></form:textarea>
			<form:errors path="subTitle" cssClass="validationError"/>	
		</p>	
		
		<p style="display:none;">
			<label class="wysiwyglabel"><spring:message code="cutmotion.noticeContent" text="Details"/></label>
			<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
			<form:errors path="noticeContent" cssClass="validationError"/>	
		</p>	
		
		<p style="display:none;">
			<a href="#" id="reviseMainTitle" style="margin-left: 162px;margin-right: 20px;"><spring:message code="cutmotion.reviseMainTitle" text="Revise Main Title"></spring:message></a>
			<c:if test="${selectedMotionType=='motions_cutmotion_budgetary'}">
				<a href="#" id=reviseSecondaryTitle style="margin-right: 20px;display: none;"><spring:message code="cutmotion.reviseSecondaryTitle" text="Revise Secondary Title"></spring:message></a>
			</c:if>
			<a href="#" id="reviseSubTitle" style="margin-right: 20px;"><spring:message code="cutmotion.reviseSubTitle" text="Revise Sub Title"></spring:message></a>
			<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="cutmotion.reviseNoticeContent" text="Revise Content"></spring:message></a>
			<a href="#" id="viewRevision"><spring:message code="cutmotion.viewrevisions" text="View Revisions"></spring:message></a>
		</p>	
		
		<p class="revise1" id="revisedMainTitleDiv">
			<label class="centerlabel"><spring:message code="cutmotion.mainTitle" text="Main Title"/></label>
			<form:textarea readonly="true" path="revisedMainTitle" rows="2" cols="50"></form:textarea>
			<form:errors path="revisedMainTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<p class="revise2" id="revisedSecondaryTitleDiv">
			<label class="centerlabel"><spring:message code="cutmotion.secondaryTitle" text="Secondary Title"/></label>
			<form:textarea readonly="true" path="revisedSecondaryTitle" rows="2" cols="50"></form:textarea>
			<form:errors path="revisedSecondaryTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<p class="revise3" id="revisedSubTitleDiv">
			<label class="centerlabel"><spring:message code="cutmotion.subTitle" text="Sub Title"/></label>
			<form:textarea readonly="true" path="revisedSubTitle" rows="2" cols="50"></form:textarea>
			<form:errors path="revisedSubTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<p class="revise4" id="revisedNoticeContentDiv">
			<label class="wysiwyglabel"><spring:message code="cutmotion.noticeContent" text="Notice Content"/></label>
			<form:textarea readonly="true" path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
			<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<p id="internalStatusDiv">
			<label class="small"><spring:message code="cutmotion.currentStatus" text="Current Status"/></label>
			<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
		</p>
		
		<table class="uiTable" style="margin-left: 165px;width: 900px;">
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
			</tbody>
		</table>			
		<c:if test="${workflowstatus != 'COMPLETED' and internalStatusType == 'cutmotion_final_admission'}">
			<!-- <p style="display:none;"> -->
			<p>
			<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
			<select id="changeInternalStatus" class="sSelect">
				<c:forEach items="${internalStatuses}" var="i">
					<c:choose>
						<c:when test="${i.type=='cutmotion_system_groupchanged' }">
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
			
			<p id="actorDiv" style="display: none;">
				<label class="small"><spring:message code="cutmotion.nextactor" text="Next Users"/></label>
				<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
			</p>
		</c:if>				
			
		<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
		<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
		
		<c:if test="${workflowstatus != 'COMPLETED'}">
			<p style="display:none;">
				<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="cutmotion.viewcitation" text="View Citations"></spring:message></a>	
			</p>
		</c:if>
		
		<c:choose>
		<c:when test="${workflowstatus=='COMPLETED'}">
			<p id="replyP">
				<label class="wysiwyglabel"><spring:message code="cutmotion.reply" text="Reply"/></label>
				<form:textarea path="reply" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="reply" cssClass="validationError"></form:errors>
			</p>
		</c:when>
		<c:otherwise>
			<p id="replyP">
				<label class="wysiwyglabel"><spring:message code="cutmotion.reply" text="Reply"/></label>
				<form:textarea path="reply" cssClass="wysiwyg"></form:textarea>
				<form:errors path="reply" cssClass="validationError"></form:errors>
			</p>
		</c:otherwise>
		</c:choose>
		
		<p id="lateReplyReasonDiv" style="display:none;">
			<label class="wysiwyglabel"><spring:message code="cutmotion.reasonForLateReply" text="Reason for Late Reply"/></label>
			<form:textarea path="reasonForLateReply" cssClass="wysiwyg"></form:textarea>
			<form:errors path="reasonForLateReply" cssClass="validationError"></form:errors>
		</p>
		
		<p>
		<label class="centerlabel"><spring:message code="cutmotion.remarks" text="Remarks"/></label>
		<%-- <form:textarea path="remarks" cssClass="wysiwyg" cssStyle=""></form:textarea> --%>
		<form:textarea path="remarks" rows="4" style="width: 250px;"></form:textarea>
		</p>	
		
		<c:if test="${workflowstatus!='COMPLETED' }">
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
		</c:if>
		
		<form:hidden path="id"/>
		<form:hidden path="locale"/>
		<form:hidden path="version"/>
		<form:hidden path="workflowStarted"/>	
		<form:hidden path="endFlag"/>
		<form:hidden path="level" value="${level}"/>
		<form:hidden path="localizedActorName"/>
		<form:hidden path="workflowDetailsId"/>	
		<form:hidden path="rejectionReason"/>
		<form:hidden path="internalNumber"/>
		<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
		<input type="hidden" name="status" id="status" value="${status }">
		<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
		<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${domain.dataEnteredBy}">
		<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
		<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
		<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="taskid" name="taskid" value="${taskid}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
		<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
		<input id="motionType" name= "motionType" type="hidden" value="${motionType}" />
		<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
		<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
		<input id="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
		<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
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
	
	<input id="ministrySelected" value="${ministrySelected }" type="hidden">
	<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
	<input id="originalLevel" value="${ domain.level}" type="hidden">		
	<input id="motionTypeType" value="${selectedMotionType}" type="hidden"/>
	<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
	<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input id="submissionMsg" value="<spring:message code='cutmotion.submitForReply' text='Do you want to submit for the reply of cutmotion?'></spring:message>" type="hidden">
	
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