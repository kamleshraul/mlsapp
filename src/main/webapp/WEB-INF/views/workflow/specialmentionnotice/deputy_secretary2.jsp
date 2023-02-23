<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="adjournmentmotion_${houseTypeType}" text="Adjournment Motion"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/**** detail of clubbed and referenced motions ****/		
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
		var resourceURL='adjournmentmotion/'+id+'/edit?'+parameters;
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
		var params="motionId="+id
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
        +"&deviceType="+$("#motionType").val()
        +"&houseType="+$("#houseType").val();
		$.get('refentity/init?'+params,function(data){
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
		
		var resourceURL='adjournmentmotion/'+id+'/edit?'+parameters;
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
		var valueToSend = value;
		if(value!='-'){					
			var sendBack=$("#internalStatusMaster option[value='adjournmentmotion_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='adjournmentmotion_recommend_discuss']").text();	
		    var sendToSectionOfficer=$("#internalStatusMaster option[value='adjournmentmotion_processed_sendToSectionOfficer']").text();
		    var sendToDepartment=$("#internalStatusMaster option[value='adjournmentmotion_processed_sendToDepartment']").text();
		    var replyReceived = $("#internalStatusMaster option[value='adjournmentmotion_processed_replyReceived']").text();
		    var rejectedWithReason=$("#internalStatusMaster option[value='adjournmentmotion_processed_rejectionWithReason']").text();	  
		    var clubbingApproved = $("#internalStatusMaster option[value='adjournmentmotion_final_clubbing']").text();
		    var clubbingRejected = $("#internalStatusMaster option[value='adjournmentmotion_final_reject_clubbing']").text();	
		    var nameclubbingApproved = $("#internalStatusMaster option[value='adjournmentmotion_final_nameclubbing']").text();
		    var nameclubbingRejected = $("#internalStatusMaster option[value='adjournmentmotion_final_reject_nameclubbing']").text();	    
		    var clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='adjournmentmotion_recommend_clubbingPostAdmission']").text();
		    var clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='adjournmentmotion_recommend_reject_clubbingPostAdmission']").text();
		    var clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='adjournmentmotion_final_clubbingPostAdmission']").text();
		    var clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='adjournmentmotion_final_reject_clubbingPostAdmission']").text();
		    var unclubbingRecommendApprove = $("#internalStatusMaster option[value='adjournmentmotion_recommend_unclubbing']").text();
		    var unclubbingRecommendReject = $("#internalStatusMaster option[value='adjournmentmotion_recommend_reject_unclubbing']").text();
		    var unclubbingApproved = $("#internalStatusMaster option[value='adjournmentmotion_final_unclubbing']").text();
		    var unclubbingRejected = $("#internalStatusMaster option[value='adjournmentmotion_final_reject_unclubbing']").text();
		    var admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='adjournmentmotion_recommend_admitDueToReverseClubbing']").text();
		    var admitDueToReverseClubbing = $("#internalStatusMaster option[value='adjournmentmotion_final_admitDueToReverseClubbing']").text();
		    
		    //reset endflag value to continue by default..then in special cases to end workflow we will set it to end
		    $("#endFlag").val("continue");
		    
		    if(value==replyReceived || value==rejectedWithReason
		    		|| value==clubbingApproved || value==clubbingRejected					
					|| value==nameclubbingApproved || value == nameclubbingRejected
					|| value==clubbingPostAdmissionApproved || value==clubbingPostAdmissionRejected
					|| value==unclubbingApproved || value == unclubbingRejected
					|| value==admitDueToReverseClubbing){
				$("#endFlag").val("end");
				if(value!=replyReceived && value!=rejectedWithReason
						&&value!=clubbingPostAdmissionApproved && value!=clubbingPostAdmissionRejected
						&& value!=unclubbingApproved && value!=unclubbingRejected
						&& value!=admitDueToReverseClubbing) {
					$("#internalStatus").val(value);
				}				
				$("#recommendationStatus").val(value);
				$("#actor").empty();
				$("#actorDiv").hide();
				return false;
			}
		    
		    if(value==sendToSectionOfficer || value==sendToDepartment) {
		    	valueToSend = $("#internalStatus").val();	    	
		    }
		    
		    var params="motion=" + $("#id").val() + "&status=" + valueToSend +
			"&usergroup=" + $("#usergroup").val() + "&level=" + $("#originalLevel").val();
			var resourceURL = 'ref/adjournmentmotion/actors?' + params;
		    
			$.post(resourceURL,function(data){			
				if(data!=undefined||data!=null||data!=''){
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}				
					$("#actor").html(text);
					$("#actorDiv").show();			
					if(value!=sendBack && value!=discuss
							&& value != sendToSectionOfficer && value != sendToDepartment
							&& value != clubbingPostAdmissionRecommendApprove && value != clubbingPostAdmissionRecommendReject
							&& value != unclubbingRecommendApprove && value != unclubbingRecommendReject
							&& value != admitDueToReverseClubbingRecommendApprove){
						$("#internalStatus").val(value);
						$("#actorDiv").show();
					} else {
						$("#internalStatus").val($("#oldInternalStatus").val());
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
					$('#actorName').val("");
					$('#actorName').css('display','none');
					if(value!=sendBack && value!=discuss
							&& value != sendToSectionOfficer && value != sendToDepartment
							&& value != clubbingPostAdmissionRecommendApprove && value != clubbingPostAdmissionRecommendReject
							&& value != unclubbingRecommendApprove && value != unclubbingRecommendReject
							&& value != admitDueToReverseClubbingRecommendApprove){
						$("#internalStatus").val(value);
						$("#actorDiv").show();
					} else {
						$("#internalStatus").val($("#oldInternalStatus").val());
					}
					$("#recommendationStatus").val(value);
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				$("#submit").attr("disabled","disabled");
				$("#actor").empty();
				$("#actorDiv").hide();
				$('#actorName').val("");
				$('#actorName').css('display','none');
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
				scrollTop();
			});
		}else{			
			$("#actor").empty();
			$("#actorDiv").hide();
			$('#actorName').val("");
			$('#actorName').css('display','none');
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	function loadSubDepartments(ministry){
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data) {
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"
				+ $("#pleaseSelectMsg").val() + "----</option>";
			if(data.length>0) {
			for(var i=0 ;i<data.length; i++){
				subDepartmentText += "<option value='" + data[i].id + "'>" + data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);			
			}else{
				$("#subDepartment").empty();
				var subDepartmentText = 
					"<option value ='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>";				
				$("#subDepartment").html(subDepartmentText);				
			}
		}).fail(function(){
			if($("#ErrorMsg").val() != ''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	$(document).ready(function(){
		initControls();
		
		/**** Back To motion ****/
		$("#backToMotion").click(function(){
			$("#clubbingResultDiv").hide();
			$("#referencingResultDiv").hide();
			//$("#backTomotionDiv").hide();
			$("#assistantDiv").show();
			/**** Hide update success/failure message on coming back to motion ****/
			$(".toolTip").hide();
		});			
		/**** Adjourning Date ****/
		$('#changeAdjourningDate').click(function() {
			var yesLabel = $('#yesLabel').val();
			var noLabel = $('#noLabel').val();
			$.prompt('Do you really want to change the adjourning date?', {
				buttons: [
					{title: yesLabel, value: true},
					{title: noLabel, value: false}
				],
				callback: function(v) {
					if(v) {
						$('#adjourningDate').removeAttr('disabled');
						$('#changeAdjourningDate').hide();
					} else {
						return false;
					}
				}
			});			
		});
		/**** Ministry Autocomplete ****/
		$( "#formattedMinistry").autocomplete({
			minLength:3,			
			source:'ref/getministries?session=' + $('#session').val(),
			select:function(event,ui){		
				if(ui.item != undefined) {
					$("#ministry").val(ui.item.id);
				} else {
					$("#ministry").val('');
				}
			},
			change:function(event,ui){
				if(ui.item != undefined) {
					var ministryVal = ui.item.id;						
					if(ministryVal != ''){
						loadSubDepartments(ministryVal);
					}else{
						$("#subDepartment").empty();				
						$("#subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");				
					}
				} else {
					if($( "#formattedMinistry").val() == '') {
						$("#subDepartment").empty();				
						$("#subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");
					}					
				}			
			}
		});
		/**** Initialize SubDepartment to 'Please Select' if not already set by member ****/
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").
			prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}else{
			$("#subDepartment").
			prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}							
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('adjournmentmotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		$("#reviseNoticeContent").click(function(){
			$(".revise2").toggle();		
			if($("#revisedNoticeContentDiv").css("display")=="none"){
				$("#revisedNoticeContent").wysiwyg("setContent","");
			}else{
				$("#revisedNoticeContent").wysiwyg("setContent",$("#noticeContent").val());				
			}				
			return false;			
		});
		/**** Revisions ****/
	    $(".viewRevision").click(function(){
	    	$.get('adjournmentmotion/revisions/'+$("#id").val(), function(data){
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
		    $.get('adjournmentmotion/members/contacts?members='+members,function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
	    	$("#submit").removeAttr("disabled");	
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
	    /**** Actor Changed ****/
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");		
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
		});
		/**** Remarks ****/
		if($('#remarks').val()!=undefined
				&& $('#remarks').val()!="" 
				&& $('#remarks').val()!="<p></p>") {
			$('#remarks_div').show();
		} else {
			$('#remarks_div').hide();
		}
		/**** To show/hide viewClubbedAdjournmentMotionTextsDiv to view clubbed adjournment motion's text starts****/
		$("#clubbedAdjournmentMotionTextsDiv").hide();
		$("#hideClubMTDiv").hide();
		$("#viewClubbedAdjournmentMotionTextsDiv").click(function(){
			var parent = $("#key").val();
			if(parent==undefined || parent==''){
				parent = ($("#id").val()!=undefined && $("#id").val()!='')? $("#id").val():"";
			}
			if(parent!=undefined && parent!=''){			
				
				if($("#clubbedAdjournmentMotionTextsDiv").css('display')=='none'){
					$("#clubbedAdjournmentMotionTextsDiv").empty();
					$.get('ref/adjournmentmotion/'+parent+'/clubbedmotiontext',function(data){
						
						var text="";
						
						for(var i = 0; i < data.length; i++){
							text += "<p>"+data[i].name+" ("+data[i].displayName+")</p><p>"+data[i].value+"</p><hr />";
						}						
						$("#clubbedAdjournmentMotionTextsDiv").html(text);
						
					});	
					$("#hideClubMTDiv").show();
					$("#clubbedAdjournmentMotionTextsDiv").show();
				}else{
					$("#clubbedAdjournmentMotionTextsDiv").hide();
					$("#hideClubMTDiv").hide();
				}
			}
		});
		$("#hideClubMTDiv").click(function(){
			$(this).hide();
			$('#clubbedAdjournmentMotionTextsDiv').hide();
		});
		/**** To show/hide viewClubbedAdjournmentMotionTextsDiv to view clubbed adjournment motion's text end****/
		
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
				$.post('clubentity/unclubbing?pId='+motionId+"&cId="+clubId+"&whichDevice=motions_",function(data){
					$.prompt(data,{callback: function(v){ 
									refreshEdit(motionId);
									}
					});					
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
				$.post('refentity/adjournmentmotion/dereferencing?pId='+motionId+"&rId="+refId,function(data){
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
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }		
	    if($("#revisedNoticeContent").val()!=''
	    		&& $("#revisedNoticeContent").val()!='<p></p>'){
	    	$("#revisedNoticeContentDiv").show();
	    }
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
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.post($('form').attr('action')+'?operation=workflowsubmit',  
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
		//***** On Page Load Internal Status Actors Will be Loaded ****/
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
        
        #clubbedAdjournmentMotionTextsDiv{
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
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
	<div id="assistantDiv">
		<form:form action="workflow/adjournmentmotion" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2>
			<c:if test="${workflowstatus=='COMPLETED'}">
			<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>
			<br>
			</c:if>			
			${formattedMotionType}
			<c:choose>
				<c:when test="${not empty formattedAdjourningDate and not empty formattedNumber}">
					(${formattedAdjourningDate} - <spring:message code="generic.number" text="Number"/> ${formattedNumber})
				</c:when>
				<c:when test="${not empty formattedAdjourningDate and empty formattedNumber}">
					(${formattedAdjourningDate})
				</c:when>
				</c:choose>
			</h2>
			
			<form:errors path="version" cssClass="validationError"/>
			
			<p style="display:none;">
				<label class="small"><spring:message code="adjournmentmotion.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="adjournmentmotion.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="adjournmentmotion.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="adjournmentmotion.type" text="Type"/>*</label>
				<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
				<input id="type" name="type" value="${motionType}" type="hidden">
				<form:errors path="type" cssClass="validationError"/>		
			</p>
			
			<p>
				<label class="small"><spring:message code="adjournmentmotion.number" text="Motion Number"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
			    <input id="admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>
				
				<label class="small"><spring:message code="adjournmentmotion.selectadjourningdate" text="Adjourning Date"/></label>
				<%-- <select name="adjourningDate" id="adjourningDate" style="width:130px;height: 25px;" disabled="disabled">
				<c:forEach items="${sessionDates}" var="i">
					<option value="${i[0]}" ${i[0]==selectedAdjourningDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
				</c:forEach>
				</select>
				<a href="#" id="changeAdjourningDate" style="margin-left: 10px;"><spring:message code="adjournmentmotion.changeAdjourningDate" text="Change Adjourning Date"/></a> --%>
				<input id="formattedAdjourningDate" name="formattedAdjourningDate" value="${formattedAdjourningDate}" class="sText" readonly="readonly">
				<input id="adjourningDate" name="adjourningDate" type="hidden"  value="${selectedAdjourningDate}">
			</p>		
			
			<c:if test="${!(empty submissionDate)}">
			<p>
				<label class="small"><spring:message code="adjournmentmotion.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
			</p>
			</c:if>
				
			<p>
				<label class="small"><spring:message code="adjournmentmotion.ministry" text="Ministry"/></label>
				<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
				<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}" readonly="readonly">
				<%-- <form:select path="ministry" id="ministry" class="sSelect">
				<c:forEach items="${ministries}" var="i">
					<c:choose>
						<c:when test="${i.id==ministrySelected }">
							<option value="${i.id}" selected="selected">${i.dropdownDisplayName}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}" >${i.dropdownDisplayName}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</form:select> --%>
				<form:errors path="ministry" cssClass="validationError"/>			
				<label class="small"><spring:message code="adjournmentmotion.subdepartment" text="Sub Department"/></label>
				<select name="subDepartment" id="subDepartment" class="sSelect">
				<c:forEach items="${subDepartments}" var="i">
					<c:choose>
						<c:when test="${i.id==subDepartmentSelected}">
							<option value="${i.id}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.id}">${i.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
					<form:errors path="subDepartment" cssClass="validationError"/>							
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="adjournmentmotion.members" text="Members"/></label>
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
				<label class="small"><spring:message code="adjournmentmotion.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText">
				<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
			</p>			
			
			<p style="display:none;">
				<c:if test="${bulkedit!='yes' and domain.internalStatus.type!='adjournmentmotion_system_clubbed'}">
			
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="adjournmentmotion.clubbing" text="Clubbing"></spring:message></a>
				<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="adjournmentmotion.referencing" text="Referencing"></spring:message></a>
				<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="adjournmentmotion.refresh" text="Refresh"></spring:message></a>
				</c:if>	
			</p>
			
			
			<p>
				<label class="small"><spring:message code="adjournmentmotion.parentmotion" text="Clubbed To"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty parent)}">	
						<a href="#" id="p${parent}" onclick="viewMotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="parent" name="parent" value="${parent}">
			</p>
			
			<p>
				<label class="small"><spring:message code="adjournmentmotion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty clubbedMotions) }">
						<c:forEach items="${clubbedMotions }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
						<a href="javascript:void(0);" id="viewClubbedAdjournmentMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="adjournmentmotion.clubbed.texts" text="C"></spring:message></a>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
				<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
					<c:forEach items="${clubbedMotions}" var="i">
						<option value="${i.id}" selected="selected"></option>
					</c:forEach>
				</select>
			</p>
			
			<p>
				<label class="small"><spring:message code="adjournmentmotion.referencedmotion" text="Referenced Motion"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty referencedMotion) }">
						<a href="#" id="cq${referencedMotion.number}" class="referencedRefMotions" onclick="viewMotionDetail(${referencedMotion.number});" style="font-size: 18px;"><c:out value="${referencedMotion.name}"></c:out></a>
						<%-- <a href="javascript:void(0);" id="viewReferencedMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="adjournmentmotion.referenced.texts" text="R"></spring:message></a> --%>
						<input type="hidden" id="referencedAdjournmentMotion" name="parent" value="${referencedMotion.id}">
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>				
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="adjournmentmotion.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="adjournmentmotion.noticeContent" text="Notice Content"/>*</label>
				<form:textarea path="noticeContent" cssClass="wysiwyg"></form:textarea>
				<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>	
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="adjournmentmotion.reviseSubject" text="Revise Subject"></spring:message></a>
				<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="adjournmentmotion.reviseNoticeContent" text="Revise Notice Content"></spring:message></a>
				<a href="#" class="viewRevision"><spring:message code="adjournmentmotion.viewrevisions" text="View Revisions"></spring:message></a>
			</p>	
			
			<p style="display:none;" class="revise1" id="revisedSubjectDiv">
				<label class="centerlabel"><spring:message code="adjournmentmotion.revisedSubject" text="Revised Subject"/></label>
				<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
				<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p style="display:none;" class="revise2" id="revisedNoticeContentDiv">
				<label class="wysiwyglabel"><spring:message code="adjournmentmotion.revisedNoticeContent" text="Revised Notice Content"/></label>
				<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
				<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p id="internalStatusDiv">
				<label class="small"><spring:message code="adjournmentmotion.currentStatus" text="Current Status"/></label>
				<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
			</p>
			
			<p style="text-align: right; width: 720px;">
				<a href="#" class="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
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
					<c:if test="${workflowstatus != 'COMPLETED'}">
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
								<form:hidden path="remarksAboutDecision"/>
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
			
			<c:if test="${workflowstatus!='COMPLETED' }">	
			<p>
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
				
			<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
			<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					
			</div>
				
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
			<form:hidden path="level"/>
			<form:hidden path="localizedActorName"/>
			<form:hidden path="workflowDetailsId"/>
			<form:hidden path="reply"/>
			<form:hidden path="rejectionReason"/>
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
		</form:form>

		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">		
		<input id="originalLevel" value="${ domain.level}" type="hidden">		
		<input id="motionTypeType" value="${selectedMotionType}" type="hidden"/>
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="subDepartmentEmptyMsg" value='<spring:message code="client.error.subDepartmentEmptyMsg" text="SubDepartment can not be empty."></spring:message>' type="hidden">
		<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		
		<ul id="contextMenuItems" >
			<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
			<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
		</ul>
	</div>
	</div>
	<div id="clubbingResultDiv" style="display:none;"></div>
	<!--To show the motion texts of the clubbed motions -->
	<div id="clubbedAdjournmentMotionTextsDiv">
		<h1>		
			<spring:message code="adjournmentmotion.clubbedMotionTexts" text="Motion texts of clubbed motions:"></spring:message>
		</h1>
	</div>
	<div id="hideClubMTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
	
	<div id="referencingResultDiv" style="display:none;">
	</div>
</body>
</html>