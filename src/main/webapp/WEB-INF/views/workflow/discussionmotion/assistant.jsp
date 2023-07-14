<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="./resources/css/printerfriendly.css?v=5" media="print" />
	<script type="text/javascript">
	function viewDiscussionMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&discussionMotionType="+$("#selectedDiscussionMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='discussionmotion/'+id+'/edit?'+parameters;
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
	
	

	/**** load actors ****/
	function loadActors(value){
		
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(value!='-'){
		var sendToSectionOfficer = $("#internalStatusMaster option[value='discussionmotion_processed_sendToSectionOfficer']").text();
		var valueToSend = "";
		var changedInternalStatus = $("#changeInternalStatus").val();
		if(changedInternalStatus == sendToSectionOfficer) {
			valueToSend = $("#internalStatus").val();
		}else{
			valueToSend = value;
		}
		var level=$('#originalLevel').val();
		var params="discussionmotion="+$("#id").val()+"&status="+valueToSend+
		"&usergroup="+$("#usergroup").val()+"&level="+level;
		var resourceURL='ref/discussionmotion/actors?'+params;
		$.get(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#actor").html(text);
				$("#actorDiv").hide();
			
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != sendToSectionOfficer){
					$("#internalStatus").val(value);
				}
				$("#recommendationStatus").val($("#changeInternalStatus").val());
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
			if(value != sendToSectionOfficer){
				$("#internalStatus").val(value);
			}
		    $("#recommendationStatus").val($("#changeInternalStatus").val());
			}
			$.unblockUI();	
		}).fail(function(){
			$.unblockUI();	
			if($("#actorDiv").css("display")=='block'){
				$("#actorDiv").hide();
			}
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html("No such flow defined.").css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
		}else{
			$.unblockUI();	
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	
	$(document).ready(function(){
		/*******Actor changes*************/
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('discussionmotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		
		
		
		$("#reviseBriefExplanation").click(function(){
			
			$(".revise3").toggle();		
			if($("#revisedBriefExplanationDiv").css("display")=="none"){
				$("#revisedBriefExplanation").wysiwyg("setContent","");
			}else{
				$("#revisedBriefExplanation").wysiwyg("setContent",$("#briefExplanation").val());				
			}				
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
	    $("#viewRevision").click(function(){
		    $.get('discussionmotion/revisions/'+$("#id").val(),function(data){
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
		    $.get('discussionmotion/members/contacts?members='+members,function(data){
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
			
			$('#bulkedit').val('yes');
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
		
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
		if($("#revisedNoticeContent").val()!=''){
		    	$("#revisedNoticeContentDiv").show();
		 }
	});
	
	function loadSubDepartment(ministry){
		$.get('ref/getSubDeparmentsByMinistries?ministries='+ministry+'&session='+$('#session').val(),
				function(data){
			if(data.length>0){
				var selectedSubDepartments = $('#subDepartments').val();
				var subDepartmentText='';
				for(var i=0;i<data.length;i++){
					var flag=false;
					if(selectedSubDepartments!=null && selectedSubDepartments!=''){
						for(var j=0;j<selectedSubDepartments.length;j++){
							if(selectedSubDepartments[j]==data[i].id){
								flag=true;
								break;
							}
						}
					}
					if(flag){
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}else{
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
				}
				$('#subDepartments').html(subDepartmentText);
			}
		});
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
<p id="error_p">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<p style="color: #FF0000;">${error}</p>
</c:if>
<div class="fields clearfix watermark">
<div id="reportDiv">
<div id="assistantDiv">
<form:form action="workflow/discussionmotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${workflowstatus=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>
	<br>
	${formattedDiscussionMotionType}: ${formattedNumber}		
	</c:when>
	<c:otherwise>
	${formattedDiscussionMotionType}: ${formattedNumber}		
	</c:otherwise>
	</c:choose>
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.type" text="Type"/>*</label>
		<input id="formattedDiscussionMotionType" name="formattedDiscussionMotionType" value="${formattedDiscussionMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${discussionMotionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="discussionmotion.number" text="Motion Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
	
		<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.priority" text="Priority"/>*</label>
		<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
		<input name="priority" id="priority"  type="hidden" value="${priority }">	
		<form:errors path="priority" cssClass="validationError"/>
		</p>
	
	</p>
	
	<p>		
	<label class="small"><spring:message code="discussionmotion.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
		
	
<c:if test="${(internalStatusType=='discussionmotion_final_admission')}">
		<label class="small"><spring:message code="discussionmotion.discussionDate" text="Discussion Date"/></label>
		
		<input id="formattedDiscussionDate" name="formattedDiscussionDate" value="${formattedDiscussionDate}" class="datemask sText" />
		<input id="setDiscussionDate" name="discussionDate" value="${discussionDate}" class="sText" type="hidden" />
	
	</c:if>
	</p>
	
		
	<p>
		<label class="centerlabel"><spring:message code="discussionmotion.members" text="Members"/></label>
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
		<label class="small"><spring:message code="discussionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>

	<c:if test="${!(empty parent)}">	
		<p>
			<label class="small"><spring:message code="motion.parentmotion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" onclick="viewDiscussionMotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
			<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>
	</c:if>	
	<!-- 
	<p>
		<label class="small"><spring:message code="generic.clubbed" text="Clubbed Motions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedEntities) }">
				<c:choose>
					<c:when test="${!(empty clubbedEntities) }">
						<c:forEach items="${clubbedEntities }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewDiscussionMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
	</p>
	
	<c:if test="${!(empty referencedEntities) }">
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedEntities }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</c:if>
	 -->
	<p>
	<label class="labeltop"><spring:message code="discussionmotion.ministries" text="Ministries"/>*</label>
	<select name="ministries" id="ministries" multiple="multiple" size="5" style="width:200px;">
		<c:forEach items="${ministries}" var="i">
			<c:set var="selectedMinistry" value="no"></c:set>
			<c:forEach items="${selectedministries}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedMinistry" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedMinistry=='yes'}">
					<option selected="selected" value="${i.id}">${i.dropdownDisplayName}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.dropdownDisplayName}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select> 	
	<form:errors path="ministries" cssClass="validationError"/>
	
	<label class="labeltop"><spring:message code="discussionmotion.subdepartments" text="Sub Departments"/></label>
	<select name="subDepartments" id="subDepartments" multiple="multiple" size="5">
		<c:forEach items="${subDepartments}" var="i">
			<c:set var="selectedSubDepartment" value="no"></c:set>
			<c:forEach items="${selectedSubDepartments}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedSubDepartment" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedSubDepartment=='yes'}">
					<option selected="selected" value="${i.id}">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>			
	<form:errors path="subDepartments" cssClass="validationError"/>	
	</p>		
	
	<p>	
	<label class="centerlabel"><spring:message code="discussionmotion.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>

	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.details" text="Details"/></label>
		<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="noticeContent" cssClass="validationError"/>	
	</p>
	
	<c:if test="${selectedDiscussionMotionType =='motions_discussionmotion_shortduration'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.briefExplanation" text="Brief Explanation"/></label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError"/>	
	</p>
	</c:if>

	<p>
		
		<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="discussionmotion.reviseSubject" text="Revise Subject"></spring:message></a>
		<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="discussionmotion.reviseNoticeContent" text="Revise Notice Content"></spring:message></a>
		<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
		<a href="#" id="reviseBriefExplanation" style="margin-right: 20px;"><spring:message code="discussionmotion.revisedBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
		</c:if>
		
		<a href="#" id="viewRevision"><spring:message code="device.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	
		
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedNoticeContentDiv">
	<label class="wysiwyglabel"><spring:message code="discussionmotion.revisedNoticeContent" text="Revised Notice Content"/></label>
	<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
	<p  class="revise3" id="revisedBriefExplanationDiv">
	<label class="wysiwyglabel"><spring:message code="discussionmotion.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
	<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="discussionmotion.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
		
	<p>
	<label class="small"><spring:message code="discussionmotion.putupfor" text="Put up for"/></label>	
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
	
	<p id="actorDiv" >
		<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
		<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
		<input type="text" id="actorName" class="sText" readonly="readonly" value="-" style="display: none;" />
	</p>	
		
	<p>
		<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="motion.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
		
	<c:if test="${internalStatusType == 'question_recommend_rejection' or internalStatusType == 'question_final_rejection'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.rejectionReason" text="Rejection reason"/></label>
		<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
	</p>
	</c:if>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>

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

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/>
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
			
	</form:form>

	<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="confirmMotionSubmission" value="<spring:message code='confirm.motionsubmission.message' text='Do you want to submit the Motion'></spring:message>" type="hidden">
	<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='discussionmotion.startworkflowmessage' text='Do You Want To Put Up Motion?'></spring:message>" type="hidden">
	<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
	<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
	<input id="oldRecommendationStatus" value="${oldRecommendationStatus}" type="hidden">
	<input id="selectedDiscussionMotionType" value="${selectedDiscussionMotionType}" type="hidden">
	<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
	<input id="noRejectionReasonProvidedMsg" type="hidden" value='<spring:message code="client.error.noRejectionReason" text="Rejection Reason must be provided"></spring:message>'/>

	<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
	<input type="hidden" id="originalLevel" value="${level}" />
	</div>

</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>