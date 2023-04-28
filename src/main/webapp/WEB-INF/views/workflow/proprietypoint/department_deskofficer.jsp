<%@ include file="/common/taglibs.jsp" %>
<%-- <%@ page import="java.util.Date;" %> --%>
<html>
<head>
	<title>
	<spring:message code="proprietypoint_${houseTypeType}" text="Propriety Point"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/**** detail of clubbed and referenced motions ****/		
	function viewMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&deviceType="+$("#selectedDeviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='proprietypoint/'+id+'/edit?'+parameters;
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
       +"&deviceType="+$("#selectedDeviceType").val()
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
		+"&deviceType="+$("#selectedDeviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='proprietypoint/'+id+'/edit?'+parameters;
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
			var sendToSectionOfficer=$("#internalStatusMaster option[value='proprietypoint_processed_sendToSectionOfficer']").text();
			var sendToDeskOfficer=$("#internalStatusMaster option[value='proprietypoint_processed_sendToDeskOfficer']").text();
		    
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
					&& $("#internalStatusType").val()=="proprietypoint_final_admission") {
				$('#submit').hide();
			}else{
				$('#submit').show();
			}
			
		    var params="proprietypoint=" + $("#id").val() + "&status=" + valueToSend +
			"&usergroup=" + $("#usergroup").val() + "&level=" + $("#originalLevel").val();
			var resourceURL = 'ref/proprietypoint/actors?' + params;
		    
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
		loadActors($("#changeInternalStatus").val());
		if($('#workflowstatus').val()=="PENDING") {
			$("#replyP").hide();			
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
		   console.log("Hello World");
			$.get("proprietypoint/citations/"+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		}); 
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
	    	$.get('proprietypoint/revisions/'+$("#id").val(), function(data){
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
		    $.get('proprietypoint/members/contacts?members='+members,function(data){
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
		
		//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide(); 
		$('#changeInternalStatus').attr('disabled', false);
		$('#actor').attr('disabled', false);
		$('#isTransferable').change(function() {
	        if ($(this).is(':checked')) {
	        	$("#ministry option[selected!='selected']").show();
	    		$("#subDepartment option[selected!='selected']").show(); 
	    		$("#transferP").css("display","inline-block");
	    		$("#submit").css("display","none");
	    		$('#changeInternalStatus').attr('disabled', true);
	    		$('#actor').attr('disabled', true);
	        }else{
	        	$("#ministry option[selected!='selected']").hide();
	    		$("#subDepartment option[selected!='selected']").hide(); 
	    		$("#transferP").css("display","none");
	    		$("#submit").css("display","inline-block");
	    		$('#changeInternalStatus').attr('disabled', false);
	    		$('#actor').attr('disabled', false);
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
			var sendToSectionOfficer=$("#internalStatusMaster option[value='proprietypoint_processed_sendToSectionOfficer']").text();
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
    </style>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
	<div id="assistantDiv">
		<form:form action="workflow/proprietypoint" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2>
			<c:if test="${workflowstatus=='COMPLETED'}">
			<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>
			<br>
			</c:if>			
			${formattedDeviceType}
			<c:choose>
				<c:when test="${not empty formattedProprietyPointDate and not empty formattedNumber}">
					(${formattedProprietyPointDate} - <spring:message code="generic.number" text="Number"/> ${formattedNumber})
				</c:when>
				<c:when test="${not empty formattedProprietyPointDate and empty formattedNumber}">
					(${formattedProprietyPointDate})
				</c:when>
				</c:choose>
			</h2>
			
			<form:errors path="version" cssClass="validationError"/>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<input id="houseTypeType" name="houseTypeType" value="${houseTypeType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.type" text="Type"/>*</label>
				<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
				<input id="type" name="type" value="${deviceType}" type="hidden">
				<%-- <form:errors path="type" cssClass="validationError"/>	 --%>	
			</p> 
			
			<p>
				<label class="small"><spring:message code="proprietypoint.number" text="Motion Number"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
				<input id="admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>
				
				<label class="small"><spring:message code="proprietypoint.selectproprietypointdate" text="Propriety Point Date"/></label>
				<%-- <select name="specialMentionNoticeDate" id="specialMentionNoticeDate" style="width:130px;height: 25px;" disabled="disabled">
				<c:forEach items="${sessionDates}" var="i">
					<option value="${i[0]}" ${i[0]==selectedAdjourningDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
				</c:forEach>
				</select>
				<a href="#" id="changeSpecialMentionNoticeDate" style="margin-left: 10px;"><spring:message code="specialmentionnotice.changeSpecialMentionNoticeDate" text="Change Special Mention Notice Date"/></a> --%>
				<input id="formattedProprietyPointDate" name="formattedProprietyPointDate" value="${formattedProprietyPointDate}" class="sText" readonly="readonly">
				<input id="proprietyPointDate" name="proprietyPointDate" type="hidden"  value="${selectedProprietyPointDate}">
			</p>		
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
			</p>
			
			<p>	
				<label class="small"><spring:message code="proprietypoint.task.creationtime" text="Task Created On"/></label>
				<input id="createdTime" name="createdTime" value="${taskCreationDate}" class="sText datetimemask" readonly="readonly">
				<label class="small"><spring:message code="proprietypoint.lastDateFromDepartment" text="Last Date From Department"/></label>
				<input id="formattedLastReplyReceivingDate" name="formattedLastReplyReceivingDate" class="datemask sText" value="${formattedLastReplyReceivingDate}" readonly="readonly"/>
				<input type="hidden" id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
				<form:errors path="lastDateOfReplyReceiving" cssClass="validationError"/>
			</p>
			
			<p>
				<label class="small"><spring:message code="proprietypoint.isTransferable" text="is propreity point to be transfered?"/></label>
				<input type="checkbox" name="isTransferable" id="isTransferable" class="sCheck">
			</p>
				
			<p>
				<label class="small"><spring:message code="proprietypoint.ministry" text="Ministry"/></label>
				<%-- <input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
				<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}"> --%>
				<form:select path="ministry" id="ministry" class="sSelect">
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
				</form:select>
				<form:errors path="ministry" cssClass="validationError"/>			
				<label class="small"><spring:message code="proprietypoint.subdepartment" text="Sub Department"/></label>
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
			</p>
			
			<p id="transferP" style="display:none;">
				<label class="small" id="subdepartmentValue"><spring:message code="proprietypoint.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted?"/></label>
				<input type="checkbox" id="transferToDepartmentAccepted" name="transferToDepartmentAccepted" class="sCheck"/>
				
				<label class="small" style="margin-left: 175px;"><spring:message code="proprietypoint.mlsBranchNotified" text="Is the Respective Propriety Point Branch Notified?"/></label>
				<input type="checkbox" id="mlsBranchNotifiedOfTransfer" name="mlsBranchNotifiedOfTransfer" class="sCheck"/>
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="proprietypoint.members" text="Members"/></label>
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
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText">
				<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
			</p>			
			
			<p style="display:none;">
				<c:if test="${bulkedit!='yes' and domain.internalStatus.type!='proprietypoint_system_clubbed'}">
			
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="proprietypoint.clubbing" text="Clubbing"></spring:message></a>
				<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="proprietypoint.referencing" text="Referencing"></spring:message></a>
				<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="proprietypoint.refresh" text="Refresh"></spring:message></a>
				</c:if>	
			</p>
			
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.parentmotion" text="Clubbed To"></spring:message></label>
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
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.clubbedmotions" text="Clubbed Motions"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty clubbedMotions) }">
						<c:forEach items="${clubbedMotions }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
						<a href="javascript:void(0);" id="viewClubbedProprietyPointTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="proprietypoint.clubbed.texts" text="C"></spring:message></a>
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
			
			<p style="display:none;">
				<label class="centerlabel"><spring:message code="proprietypoint.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50"></form:textarea>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="proprietypoint.pointsOfPropriety" text="Notice Content"/>*</label>
				<form:textarea path="pointsOfPropriety" cssClass="wysiwyg"></form:textarea>
				<form:errors path="pointsOfPropriety" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>	
			
			<p>
				<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="proprietypoint.reviseSubject" text="Revise Subject"></spring:message></a>
				<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="proprietypoint.reviseNoticeContent" text="Revise Notice Content"></spring:message></a>
				<a href="#" id="viewRevision"><spring:message code="proprietypoint.viewrevisions" text="View Revisions"></spring:message></a>
			</p>	
			
			<p class="revise1" id="revisedSubjectDiv">
				<label class="centerlabel"><spring:message code="proprietypoint.revisedSubject" text="Revised Subject"/></label>
				<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
				<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p class="revise2" id="revisedPointsOfProprietyDiv">
				<label class="wysiwyglabel"><spring:message code="proprietypoint.revisedPointsOfPropriety" text="Revised Notice Content"/></label>
				<form:textarea path="revisedPointsOfPropriety" cssClass="wysiwyg"></form:textarea>
				<form:errors path="revisedPointsOfPropriety" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p id="internalStatusDiv">
				<label class="small"><spring:message code="proprietypoint.currentStatus" text="Current Status"/></label>
				<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
			</p>
			
			<table class="uiTable" style="margin-left: 165px;width: 600px;">
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
			<c:if test="${workflowstatus != 'COMPLETED' and internalStatusType == 'proprietypoint_final_admission'}">
				<!-- <p style="display:none;"> -->
				<p>
				<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
				<select id="changeInternalStatus" class="sSelect">
					<c:forEach items="${internalStatuses}" var="i">
						<c:choose>
							<c:when test="${i.type=='proprietypoint_system_groupchanged' }">
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
					<label class="small"><spring:message code="proprietypoint.nextactor" text="Next Users"/></label>
					<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
				</p>
			</c:if>				
				
			<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
			<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
			
			<c:if test="${workflowstatus != 'COMPLETED'}">
				<p>
					<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="proprietypoint.viewcitation" text="View Citations"></spring:message></a>	
				</p>
			</c:if>
			
			<c:choose>
			<c:when test="${workflowstatus=='COMPLETED'}">
				<p id="replyP">
					<label class="wysiwyglabel"><spring:message code="proprietypoint.reply" text="Reply"/></label>
					<form:textarea path="reply" cssClass="wysiwyg" readonly="true"></form:textarea>
					<form:errors path="reply" cssClass="validationError"></form:errors>
				</p>
			</c:when>
			<c:otherwise>
				<p id="replyP">
					<label class="wysiwyglabel"><spring:message code="proprietypoint.reply" text="Reply"/></label>
					<form:textarea path="reply" cssClass="wysiwyg"></form:textarea>
					<form:errors path="reply" cssClass="validationError"></form:errors>
				</p>
			</c:otherwise>
			</c:choose>
			
			<p id="lateReplyReasonDiv" style="display:none;">
				<label class="wysiwyglabel"><spring:message code="proprietypoint.reasonForLateReply" text="Reason for Late Reply"/></label>
				<form:textarea path="reasonForLateReply" cssClass="wysiwyg"></form:textarea>
				<form:errors path="reasonForLateReply" cssClass="validationError"></form:errors>
			</p>
			
			<c:if test="${workflowstatus!='COMPLETED'}">
				<p>
					<label class="centerlabel"><spring:message code="proprietypoint.remarks" text="Remarks"/></label>
					<form:textarea path="remarks" rows="4" style="width: 250px;"></form:textarea>
					<form:hidden path="remarksAboutDecision"/>
				</p>	
			</c:if>
			
			<c:if test="${workflowstatus!='COMPLETED' }">
			<div class="fields">
				<h2></h2>				
				<p class="tright">
					<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
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
			<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
			<input type="hidden" name="status" id="status" value="${status }">
			<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
			<%-- <input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${domain.dataEnteredBy}"> --%>
			<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
			<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
			<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
			<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="taskid" name="taskid" value="${taskid}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
			<input type="hidden" name="houseTypeType" id="houseTypeType" value="${houseTypeType}">
			<%-- <input id="motionType" name= "motionType" type="hidden" value="${motionType}" /> --%>
			<input id="deviceType" name= "deviceType" type="hidden" value="${deviceType}" />
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
		<%-- <input id="motionTypeType" value="${selectedMotionType}" type="hidden"/> --%>
		<input id="deviceTypeType" value="${selectedDeviceType}" type="hidden"/>
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input id="submissionMsg" value="<spring:message code='proprietypoint.submitForReply' text='Do you want to submit for the reply of special mention notice?'></spring:message>" type="hidden">
		
		<ul id="contextMenuItems" >
			<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
			<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
		</ul>
	</div>
	</div>
	<div id="clubbingResultDiv" style="display:none;"></div>
	<!--To show the motion texts of the clubbed motions -->
	<div id="clubbedProprietyPointTextsDiv" style="display: none;">
		<h1>		
			<spring:message code="proprietypoint.clubbedMotionTexts" text="Motion texts of clubbed motions:"></spring:message>
		</h1>
	</div>
	<div id="hideClubMTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
	
	<div id="referencingResultDiv" style="display:none;">
	</div>
</body>
</html>