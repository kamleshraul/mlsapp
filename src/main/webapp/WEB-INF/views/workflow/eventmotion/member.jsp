<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced motions ****/		
	function viewEventMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&eventMotionType="+$("#selectedEventMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='eventmotion/'+id+'/edit?'+parameters;
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
		$.get('refentity/eventmotion/init?'+params,function(data){
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
		+"&cutMotionType="+$("#selectedEventMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='eventmotion/'+id+'/edit?'+parameters;
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
		var params="eventmotion="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/eventmotion/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='eventmotion_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='eventmotion_recommend_discuss']").text();		
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
			$.get('eventmotion/citations/'+$("#deviceType").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});	
		
		/**** Revise mainTitle and text****/
		$("#reviseEventTitle").click(function(){
			$(".revise1").toggle();
			//console.log("revise1: " + $("#revisedMainTitleDiv").css("display") + ": "+$("#mainTitle").val());
			if($("#revisedEventTitleDiv").css("display")=="none"){
				$("#revisedEventTitle").val("");	
			}else{
				$("#revisedEventTitle").val($("#eventTitle").val());
			}						
			return false;			
		});
						
		$("#reviseDescription").click(function(){
			$(".revise4").toggle();		
			//console.log("revise4: " + $("#revisedNoticeContentDiv").css("display") + ": "+$("#noticeContent").val());
			if($("#revisedDescriptionDiv").css("display")=="none"){
				$("#revisedDescription").wysiwyg("setContent","");
			}else{
				$("#revisedDescription").wysiwyg("setContent",$("#description").val());				
			}				
			return false;			
		});			
		
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('eventmotion/revisions/'+$("#id").val(),function(data){
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
		    $.get('eventmotion/members/contacts?members='+members,function(data){
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
	    /**** On page Load ****/
	    //$("#startworkflow").attr("disabled","disabled");
		//$("#submit").removeAttr("disabled");
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
		    	            });
    	            }
			}});			
	        return false;  
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
				$.post('clubentity/eventmotion/unclubbing?pId='+motionId+"&cId="+clubId,function(data){
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
				var motionId=$("#id").val();
				var refId=id.split("rq")[1];				
				$.post('refentity/eventmotion/dereferencing?pId='+motionId+"&rId="+refId,function(data){
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
		if($("#revisedEventTitle").val()!=''){
		    $("#revisedEventTitleDiv").show();
	    }
		
	    if($("#revisedDescription").val()!=''){
	    	$("#revisedDescriptionDiv").show();
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
        
        div.wysiwyg{
			left: 165px;
			width: 543px !important;
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
<form:form action="eventmotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedMotionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="eventmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="eventmotion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="eventmotion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="eventmotion.eventmotionType" text="Type"/>*</label>
		<input id="formattedEventMotionType" name="formattedEventMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
		<input id="deviceType" name="deviceType" value="${motionType}" type="hidden">		
		<form:errors path="deviceType" cssClass="validationError"/>		
	</p>

	<p>
		<p style="display: inline;">
			<label class="small"><spring:message code="eventmotion.number" text="Motion Number"/>*</label>
			<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">
			<input id="number" name="number" value="${domain.number}" type="hidden">
			<form:errors path="number" cssClass="validationError"/>		
		</p>
			
		<p style="display: inline;">		
			<label class="small"><spring:message code="eventmotion.submissionDate" text="Submitted On"/></label>
			<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		</p>
	</p>
	
	<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
		<div>
			<p style="display: inline-block;">
				<label class="small"><spring:message code="eventmotion.designationOfPerson" text="Designation"/>*</label>
				<input name="designationOfPerson" id="designationOfPerson" type="text" value="${domain.designationOfPerson}" class="sText">		
				<form:errors path="designationOfPerson" cssClass="validationError"/>
			</p>
			
			<p style="display: inline-block;">
				<label class="small"><spring:message code="eventmotion.constituencyOfPerson" text="Contituency"/>*</label>
				<input name="constituencyOfPerson" id="constituencyOfPerson" type="text" value="${domain.constituencyOfPerson}" class="sText">		
				<form:errors path="constituencyOfPerson" cssClass="validationError"/>		
			</p>
		</div>
	</c:if>
	
	<div>
		<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
			<p style="display: inline-block;">
				<label class="small"><spring:message code="eventmotion.tenureOfPerson" text="Tenure"/>*</label>
				<input name="tenureOfPerson" id="tenureOfPerson" type="text" value="${domain.tenureOfPerson}" class="sText">		
				<form:errors path="tenureOfPerson" cssClass="validationError"/>
			</p>
		</c:if>
		
		<p style="display: inline-block;">
			<label class="small"><spring:message code="eventmotion.eventDate" text="Event Date"/>*</label>
			<input name="formattedEventDate" id="formattedEventDate" type="text" value="${formattedEventDate}" class="sText datemask">
			<input name="eventDate" id="eventDate" type="hidden" value="${eventDate}">		
			<form:errors path="eventDate" cssClass="validationError"/>
		</p>
		
		<p style="display: inline-block;">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<select name="discussionDate" id="discussionDate" class="sSelect">
				<option value="">--<spring:message code="please.select" text="Select"/>--</option>
				<c:forEach items="${discussionDates}" var="i">
					<c:choose>
						<c:when test="${i.value==discussionDateSelected }">
							<option value="${i.value}" selected="selected">${i.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${i.value}" >${i.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<form:errors path="discussionDate" cssClass="validationError"/>
		</p>
	</div>
		
	<c:choose>
		<c:when test="${selectedMotionType=='motions_eventmotion_condolence'}">
			<c:if test="${!(empty primaryMember)}">
				<div>
					<p style="display: inline-block;">
						<label class="centerlabel"><spring:message code="generic.members" text="Members"/></label>
						<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50" style="width: 536px; height: 55px;">${memberNames}</textarea>
						<input id="primaryMember" name="member" value="${primaryMember}" type="hidden">
					</p>								
					
					<p style="display: inline-block;">
						<label class="small"><spring:message code="generic.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText">
						<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
					</p>
				</div>
			</c:if>
		</c:when>
		<c:when test="${selectedMotionType=='motions_eventmotion_congratulatory'}">
			<p>
				<label class="centerlabel"><spring:message code="generic.members" text="Members"/></label>
				<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50" style="width: 536px; height: 55px;">${memberNames}</textarea>
				<c:if test="${!(empty primaryMember)}">
					<input id="primaryMember" name="member" value="${primaryMember}" type="hidden">
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
			
		</c:when>
	</c:choose>			
	
	<c:if test="${selectedMotionType=='motions_eventmotion_congratulatory'}">
		<p>
			<c:if test="${bulkedit!='yes'}">	
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a>
				<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="generic.referencing" text="Referencing"></spring:message></a>
				<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="generic.refresh" text="Refresh"></spring:message></a>
			</c:if>	
		</p>	
	</c:if>
		
	<c:if test="${!(empty parent)}">	
		<p>
			<label class="small"><spring:message code="cutmotion.parentmotion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" onclick="viewmotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
			<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>
	</c:if>	
	
	<c:if test="${!(empty clubbedEntities) }">
		<p>
			<label class="small"><spring:message code="generic.clubbed" text="Clubbed Motions"></spring:message></label>
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
			<label class="small"><spring:message code="cutmotion.referencedmotions" text="Referenced Motions"></spring:message></label>
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
		<label class="centerlabel"><spring:message code="eventmotion.eventTitle" text="Event Title"/></label>
		<form:textarea path="eventTitle" readonly="true" rows="2" cols="72"></form:textarea>
		<form:errors path="eventTitle" cssClass="validationError"/>	
	</p>
		
	<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
		<p>
			<label class="centerlabel"><spring:message code="eventmotion.eventReason" text="Event Reason"/>*</label>
			<form:textarea path="eventReason" rows="2" cols="72" readonly="true"></form:textarea>
			<form:errors path="eventReason" cssClass="validationError" />
		</p>	
	</c:if>
	
	<p>
		<label class="wysiwyglabel"><spring:message code="eventmotion.description" text="Description"/></label>
		<form:textarea path="description" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="description" cssClass="validationError"/>	
	</p>
	
	<div>	
		<p style="display: inline-block;">
			<label class="small"><spring:message code="eventmotion.isHouseAdjourned" text="House Adjourned?"/>*</label>
			<c:choose>
				<c:when test="${domain.isHouseAdjourned}">
					<form:checkbox path="isHouseAdjourned" ></form:checkbox>
				</c:when>
				<c:otherwise>
					<form:checkbox path="isHouseAdjourned"></form:checkbox>
				</c:otherwise>
			</c:choose>
			<form:errors path="isHouseAdjourned" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<c:if test="${selectedMotionType=='motions_eventmotion_condolence'}">
			<p style="display: inline-block;">
				<label class="small"><spring:message code="eventmotion.collectorReport" text="Collector Report"/></label>
				<span id="image_gallery" style="display: inline;margin: 0px;padding: 0px;">
					<img alt="" src="" id="image_photo" width="70" height="70">
				</span>
				<c:choose>		
				<c:when test="${empty domain.collectorReport}">
				<jsp:include page="/common/file_upload.jsp">
					<jsp:param name="fileid" value="collectorReport" />
				</jsp:include>
				</c:when>
				<c:otherwise>		
				<jsp:include page="/common/file_download.jsp">
					<jsp:param name="fileid" value="collectorReport" />
					<jsp:param name="filetag" value="${domain.collectorReport}" />
				</jsp:include>
				</c:otherwise>
				</c:choose>		
				<form:errors path="collectorReport" cssClass="validationError" />
			</p>			
		</c:if>	
	</div>	
		
	<div>
		<p id="internalStatusDiv" style="margin-top: 20px; display: inline-block;">
			<label class="small"><spring:message code="generic.status" text="Current Status"/></label>
			<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly" width="20">
		</p>
		
		<p style="display: inline-block;">
			<a href="#" id="reviseEventTitle" style="margin-left: 50px;margin-right: 20px;"><spring:message code="eventmotion.reviseEventTitle" text="Revise Event Title"></spring:message></a>
			<a href="#" id="reviseDescription" style="margin-right: 20px;"><spring:message code="eventmotion.reviseDescription" text="Revise Description"></spring:message></a>
			<a href="#" id="viewRevision" style="margin-right: 20px;"><spring:message code="eventmotion.viewrevision" text="Revisions"></spring:message></a>
		</p>
	</div>
	
	<p style="display:none;" class="revise1" id="revisedEventTitleDiv">
		<label class="centerlabel"><spring:message code="eventmotion.revisedEventTitle" text="Revised Event Title"/></label>
		<form:textarea path="revisedEventTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedEventTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
		
	<p style="display:none;" class="revise4" id="revisedDescriptionDiv">
		<label class="wysiwyglabel"><spring:message code="eventmotion.revisedDescription" text="Revised Description"/></label>
		<form:textarea path="revisedDescription" cssClass="wysiwyg"></form:textarea>
		<form:errors path="revisedDescription" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>	
	<c:if test="${workflowstatus=='PENDING'}">
	<div>		
		<p style="display: inline-block;">	
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
		
		<p id="actorDiv" style="display: inline-block;">
			<label class="small"><spring:message code="generic.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
		</p>
	</div>
	</c:if>
	
	<p>
		<label class="wysiwyglabel"><spring:message code="generic.remarks" text="Remarks"/></label>
		<a href="#" id="viewCitation" style="display: inline; margin-left: 550px;"><spring:message code="cutmotion.viewcitation" text="View Citations"></spring:message></a>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<c:if test="${workflowstatus=='PENDING'}">
	<div class="fields">
		<h2></h2>
		<p class="tright">		
			<c:if test="${bulkedit!='yes'}">
				<c:if test="${internalStatusType=='eventmotion_submit'}">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<input id="startworkflow" type="button" value="<spring:message code='eventmotion.putupmotion' text='Put Up Motion'/>" class="butDef">
				</c:if>
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
<input id="confirmMotionSubmission" value="<spring:message code='confirm.cutmotionsubmission.message' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='motion.startworkflowmessage' text='Do You Want To Put Up motion'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="motionType" type="hidden" value="${selectedMotionType}" />
<input id="originalLevel" type="hidden" value="${level}" />

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