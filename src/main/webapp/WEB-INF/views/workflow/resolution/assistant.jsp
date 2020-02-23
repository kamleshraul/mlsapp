<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="resolution" text="Resolution Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** Referencing ****/
	function referencingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="id="+id
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val()
        +"&deviceType="+$("#deviceType").val();
		+"&usergroupType="+$("#usergroupType").val();
		alert(params);
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
	/**** refresh referencing ****/
	function refreshEdit(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&device="+$("#deviceType").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='resolution/'+id+'/edit?'+parameters;
		$('a').removeClass('selected');
		//id refers to the tab name and it is used just to highlight the selected tab
		$('#'+ id).addClass('selected');
		//tabcontent is the content area where result of the url load will be displayed
		$('.tabContent').load(resourceURL);
		$("#referencingResultDiv").hide();
		$("#assistantDiv").show();
		scrollTop();
		$.unblockUI();			
	}
	
	/**** to view the referred resolution ****/
	function viewResolutionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&deviceType="+$("#selectedDeviceType").val()
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
	/**** load actors ****/
	function loadActors(value){
		console.log(value);
		if(value!='-'){
		var sendback=$("#internalStatusMaster option[value='resolution_recommend_sendback']").text();			
		var discuss=$("#internalStatusMaster option[value='resolution_recommend_discuss']").text();
		var sendToSectionOfficer = $("#internalStatusMaster option[value='resolution_processed_sendToSectionOfficer']").text();
		var currentRecommendationStatus=$("#recommendationStatus").val();
		var deviceType = $("#selectedDeviceType").val();
		var valueToSend = "";
		var changedInternalStatus = $("#changeInternalStatus").val();
		if(changedInternalStatus == sendToSectionOfficer) {
			valueToSend = $("#internalStatus").val();
		}
		else {
			valueToSend = value;
		}
		var params='';
		if($('#houseTypeType').val()=='lowerhouse'){
			 params="resolution="+$("#id").val()+"&status="+valueToSend+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#levelLowerHouse").val()+"&workflowHouseType="+$("#workflowHouseType").val();
		}else if($('#houseTypeType').val()=='upperhouse'){
			 params="resolution="+$("#id").val()+"&status="+valueToSend+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#levelUpperHouse").val()+"&workflowHouseType="+$("#workflowHouseType").val();
		}
		var resourceURL='ref/resolution/actors?'+params;
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").empty();
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#actorUpperHouse").empty();
				}
				var actor1="";
				var actCount = 1;
				var text="";
				for(var i=0;i<length;i++){		
					var ugt = data[i].id.split("#")[1];
					if(ugt!='member' && data[i].state!='active'){
						text += "<option value='" + data[i].id + "' disabled='disabled'>" + data[i].name + "</option>";
					} else {
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						if(actCount == 1){
							actor1=data[i].id;
							actCount++;
						}
					}
				}
				if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").html(text);
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#actorUpperHouse").html(text);
				}
				if(currentRecommendationStatus == sendback || currentRecommendationStatus == discuss){
					$("#actorDiv").show();	
				}else{
					$("#actorDiv").hide();
				}
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value != sendback && value != discuss && value != sendToSectionOfficer){
					$("#internalStatus").val(value);
				}
				$("#recommendationStatus").val($("#changeInternalStatus").val());
				if($("#oldRecommendationStatus").val()== sendback || $("#oldRecommendationStatus").val()==discuss){
					$("#actorDiv").show();
				}
				/**** setting level,localizedActorName ****/
				//console.log(data[0].id);
				 //var actor1=data[0].id;
				 var temp=actor1.split("#");
				 if($('#houseTypeType').val()=='lowerhouse'){
					 $("#levelLowerHouse").val(temp[2]);		    
					 $("#localizedActorNameLowerHouse").val(temp[3]+"("+temp[4]+")");
				}else if($('#houseTypeType').val()=='upperhouse'){
					 $("#levelUpperHouse").val(temp[2]);		    
					 $("#localizedActorNameUpperHouse").val(temp[3]+"("+temp[4]+")");
				}	
			}else{
				 if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").empty();
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#actorUpperHouse").empty();
				}
			$("#actorDiv").hide();
			/**** in case of sendback and discuss only recommendation status is changed ****/
			if(value != sendback && value != discuss && value != sendToSectionOfficer){
			$("#internalStatus").val(value);
			}
		    $("#recommendationStatus").val($("#changeInternalStatus").val());
			}
			if($("#oldRecommendationStatus").val()== sendback || $("#oldRecommendationStatus").val()==discuss){
				$("#actorDiv").show();
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
			 if($('#houseTypeType').val()=='lowerhouse'){
				 $("#actorLowerHouse").empty();
			}else if($('#houseTypeType').val()=='upperhouse'){
				$("#actorUpperHouse").empty();
			}
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	
	/**** sub departments ****/
	/* function loadSubDepartments(ministry,department){
		$.get('ref/subdepartments/'+ministry+'/'+department,function(data){
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
		});
	} */
	/**** departments ****/
	/* function loadDepartments(ministry){
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
			}
		});
	} */
	
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
    	
	$(document).ready(function(){
		 $("#actorLowerHouse").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    console.log(temp);
			    $("#levelLowerHouse").val(temp[2]);		    
			    $("#localizedActorNameLowerHouse").val(temp[3]+"("+temp[4]+")");
		    });
		 
		 $("#actorUpperHouse").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    console.log(temp);
			    $("#levelUpperHouse").val(temp[2]);		    
			    $("#localizedActorNameUpperHouse").val(temp[3]+"("+temp[4]+")");
		    });
		/**** Ministry Changes ****/
		/* $("#ministry").change(function(){
			if($(this).val()!=''){
			loadDepartments($(this).val());
			}else{
				$("#department").empty();				
				$("#subDepartment").empty();				
				$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			}
		}); */
		
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
		/**** Department Changes ****/
		$("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		});
		/**** Citations ****/
		 $("#viewCitation").click(function(){
			$.get('resolution/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		$("#reviseNoticeContent").click(function(){
			$(".revise2").toggle();		
			if($("#revisedNoticeContent").css("display")=="none"){
				$("#revisedNoticeContent").wysiwyg("setContent","");
			}else{
				$("#revisedNoticeContent").wysiwyg("setContent",$("#noticeContent").val());				
			}				
			return false;			
		});	
		/**** Revisions ****/
	     $("#viewRevision").click(function(){
		    var urlForRevisions;			    
	    	if($("#typeOfSelectedDeviceType").val() == 'resolutions_government') {	    		
	    		urlForRevisions = 'resolution/revisions/'+$("#id").val() + '?workflowHouseType='+$("#workflowHouseType").val();
	    	} else {
	    		urlForRevisions = 'resolution/revisions/'+$("#id").val();
	    	}
	    	$.get(urlForRevisions,function(data){
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
		    var member=$("#member").val();
		    $.get('resolution/members/contacts?member='+member,function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });	    
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
			   loadActors(value);			    
		    }else{
		    	 if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").empty();
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#houseTypeType").empty();
				}
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
		
		if($("#revisedSubject").val()!=''){
			    $("#revisedSubjectDiv").show();
		 }
		if($("#revisedNoticeContent").val()!=''){
		    	$("#revisedNoticeContentDiv").show();
		 }	 
			 
		//***** On Page Load Internal Status Actors Will be Loaded ****/
		if($('#bulkedit').val()!='yes'&& $('#workflowstatus').val()!='COMPLETED'){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value$='"+statusType[statusType.length-1]+"']").text();
			$("#changeInternalStatus").val(id);
			//$("#changeInternalStatus").change();
			/* loadActors($("#changeInternalStatus").val()); */
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
<form:form action="workflow/resolution" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${workflowstatus=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>
	<br>
	${formattedDeviceType}: ${formattedNumber}		
	</c:when>
	<c:otherwise>
	${formattedDeviceType}: ${formattedNumber}		
	</c:otherwise>
	</c:choose>
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="resolution.type" text="Type"/>*</label>
		<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${deviceType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="resolution.number" text="Resolution Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>
		<label class="small"  style="margin-left:30px;"><spring:message code="resolution.submissionDate" text="Submitted On"/></label>
		<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	</p>
	
	<p>
	<label class="small"><spring:message code="resolution.members" text="Members"/></label>
	<input id="members" class="sTextarea" readonly="readonly" value="${formattedMember}">
	<c:if test="${!(empty member)}">
		<input id="member" name="member" value="${member}" type="hidden">
	</c:if>
	<label class="small"  style="margin-left:30px;"><spring:message code="resolution.memberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>
	</p>

	<p>
		<label class="small"><spring:message code="resolution.ministry" text="Ministry"/>*</label>
			<select name="ministry" id="ministry" class="sSelect" >
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
		<label class="small" style="margin-left:30px;"><spring:message code="resolution.subdepartment" text="Sub Department"/></label>
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
	<%-- <label class="small"><spring:message code="resolution.department" text="Department"/></label>
	<select name="department" id="department" class="sSelect" >
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
	<form:errors path="department" cssClass="validationError"/>	 --%>
	<c:if test="${selectedDeviceType == 'resolutions_government'}">
		<label class="small"><spring:message code="resolution.discussionDate" text="Discussion Date"/></label>
		<form:input path="discussionDate" cssClass="datemask sText" readonly="${isDiscussionDateReadOnly}"/>
		<form:errors path="discussionDate" cssClass="validationError"/>
	</c:if>
	
	</p>			
	
	<%-- <p>
		<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="resolution.clubbing" text="Clubbing"></spring:message></a>
		<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="resolution.referencing" text="Referencing"></spring:message></a>
		<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="resolution.refresh" text="Refresh"></spring:message></a>	
	</p> --%>	
	<c:if test="${selectedDeviceType=='resolution_nonofficial'}" >
	<p>
		<label class="small"><spring:message code="resolution.referredresolution" text="Referred Resolution"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedResolutions) }">
				<c:forEach items="${referencedResolutions }" var="i" varStatus="index">
					<a href="#" id="rr${i.number}" class="referencedResolution" onclick="viewResolutionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
					&nbsp;(${referencedResolutionsSessionAndDevice[index.count-1]})	
					<input id="refResolution" name="refResolution" type="hidden" value="${i.number}" />
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
		<select id="referredEntities" name="referredEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedResolutions }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</p>
	</c:if>
	<p>	
	<label class="centerlabel"><spring:message code="resolution.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="resolution.noticeContent" text="Notice Content"/></label>
	<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="noticeContent" cssClass="validationError"/>	
	</p>
	
	<p>
	<c:if test="${selectedDeviceType == 'resolutions_nonofficial'}">
	<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="resolution.reviseSubject" text="Revise Subject"></spring:message></a>
	<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="resolution.reviseNoticeContent" text="Revise Notice Content"></spring:message></a>
	
	</c:if>
	<c:if test="${selectedDeviceType == 'resolutions_government'}">
	<a href="#" id="viewRevision" style="margin-left: 162px;margin-right: 20px;"><spring:message code="resolution.viewrevisions" text="View Revisions"></spring:message></a>
	</c:if>
	</p>
	
	<c:if test="${selectedDeviceType == 'resolutions_nonofficial'}">
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="resolution.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedNoticeContentDiv">
	<label class="wysiwyglabel"><spring:message code="resolution.revisedNoticeContent" text="Revised Notice Content"/></label>
	<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>
	
	<c:if test="${selectedDeviceType == 'resolutions_nonofficial'}">
		<p>
		<label class="small"><spring:message code="resolution.referencedResolutionText" text="Referenced Resolution Text"/></label>
		<form:textarea path="referencedResolutionText" cssClass="sTextarea" cols="50"></form:textarea>
		<form:errors path="referencedResolutionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
	
	<c:if test="${!(empty domain.factualPosition) && (selectedDeviceType == 'resolutions_nonofficial')}">
		<p>
		<label class="wysiwyglabel"><spring:message code="resolution.factualPosition" text="Factual Position"/></label>
		<form:textarea path="factualPosition" cssClass="wysiwyg"></form:textarea>
		<form:errors path="factualPosition" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="resolution.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	<p>
		<a href="#" id="viewRevision" style="margin-left:650px;"><spring:message code="resolution.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	<table class="uiTable" style="margin-left:165px;width:600px;">
		<thead>
		<tr>
		<th style="text-align: center">
		<spring:message code="rois.latestrevisions.user" text="Usergroup"></spring:message>
		</th>
		<th style="text-align: center">
		<spring:message code="rois.latestrevisions.decision" text="Decision"></spring:message>
		</th>
		<th style="text-align: center">
		<spring:message code="rois.latestrevisions.remarks" text="Remarks"></spring:message>
		</th>
		</tr>
		</thead>
		<tbody>	
			<c:forEach items="${latestRevisions}" var="i">
				<tr>
					<td style="text-align: left">
					${i[0]}<br>(${i[2]})
					</td>
					<td style="text-align: center">
					${i[6]}
					</td>
					<td style="text-align: center">
					${i[7]}
					</td>
				</tr>
			</c:forEach>	
			<c:if test="${workflowstatus != 'COMPLETED'}">
				<tr>
					<td style="text-align: left">
						${userName}<br>
						(${userGroupName})
					</td>
					<td style="text-align: center">
						<select id="changeInternalStatus" class="sSelect">
							<c:forEach items="${internalStatuses}" var="i">
									<c:choose>
										<c:when test="${i.id==internalStatus }">
											<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
										</c:when>
										<c:otherwise>
											<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
										</c:otherwise>
									</c:choose>
							</c:forEach>
						</select>
						<c:if test="${houseTypeForStatus=='lowerhouse'}">
						 <form:errors path="internalStatusLowerHouse" cssClass="validationError"/>	 
						</c:if>
						<c:if test="${houseTypeForStatus=='upperhouse'}">
						 <form:errors path="internalStatusUpperHouse" cssClass="validationError"/>	 
						</c:if>
					</td>
					<td style="text-align: center">
						<a href="#" id="viewCitation" style="margin-left: 210px;margin-top: 30px;"><spring:message code="resolution.viewcitation" text="View Citations"></spring:message></a>
						<form:textarea path="remarks" rows="4" style="width: 250px;"></form:textarea>
					</td>
				</tr>
			</c:if>	
		</tbody>
	</table>
	
	<c:if test="${workflowstatus!='COMPLETED' }">	
		<select id="internalStatusMaster" style="display:none;">
		<c:forEach items="${internalStatuses}" var="i">
		<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
		</c:forEach>
		</select>	
	
		<p id="actorDiv" style="display:none;">
		<label class="small"><spring:message code="resolution.nextactor" text="Next Users"/></label>
		<c:if test="${houseTypeForStatus=='lowerhouse'}">
			<form:select path="actorLowerHouse" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
			<form:hidden path="actorUpperHouse"/>
		</c:if>	
		<c:if test="${houseTypeForStatus=='upperhouse'}">
			<form:select path="actorUpperHouse" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>
			<form:hidden path="actorLowerHouse"/>				
		</c:if>
		</p>		
	</c:if>
		
	<c:choose>
		<c:when test="${selectedDeviceType == 'resolutions_government'}">
			<c:if test="${houseTypeForStatus=='lowerhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusLowerHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusLowerHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusLowerHouse" id="status" value="${status}">
				
				<input type="hidden" name="internalStatusUpperHouse" value="${internalStatusUpperHouse }">
				<input type="hidden" name="recommendationStatusUpperHouse" value="${recommendationStatusUpperHouse}">
				<input type="hidden" name="statusUpperHouse" value="${statusUpperHouse}">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusUpperHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusUpperHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusUpperHouse" id="status" value="${status}">
				
				<input type="hidden" name="internalStatusLowerHouse" value="${internalStatusLowerHouse }">
				<input type="hidden" name="recommendationStatusLowerHouse" value="${recommendationStatusLowerHouse}">
				<input type="hidden" name="statusLowerHouse" value="${statusLowerHouse}">
			</c:if>
			<input type="hidden" id="workflowHouseType" name="workflowHouseType" value="${workflowHouseType}">	
		</c:when>
		<c:otherwise>
			<c:if test="${houseTypeForStatus=='lowerhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusLowerHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusLowerHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusLowerHouse" id="status" value="${status}">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusUpperHouse" value="${internalStatus }">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusUpperHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusUpperHouse" id="status" value="${status}">
			</c:if>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${selectedDeviceType == 'resolutions_government'}">
			<c:if test="${houseTypeForStatus=='lowerhouse'}">
				<input type="hidden" name="workflowStartedOnDateLowerHouse" id="workflowStartedOnDateLowerHouse" value="${workflowStartedOnDateLowerHouse }">
				<input type="hidden" name="taskReceivedOnDateLowerHouse" id="taskReceivedOnDateLowerHouse" value="${taskReceivedOnDateLowerHouse }">
				
				<input type="hidden" name="workflowStartedOnDateUpperHouse" id="workflowStartedOnDateUpperHouse" value="${workflowStartedOnDateUpperHouse }">
			<input type="hidden" name="taskReceivedOnDateUpperHouse" id="taskReceivedOnDateUpperHouse" value="${taskReceivedOnDateUpperHouse }">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" name="workflowStartedOnDateUpperHouse" id="workflowStartedOnDateUpperHouse" value="${workflowStartedOnDate }">
				<input type="hidden" name="taskReceivedOnDateUpperHouse" id="taskReceivedOnDateUpperHouse" value="${taskReceivedOnDateUpperHouse }">
				
				<input type="hidden" name="workflowStartedOnDateLowerHouse" id="workflowStartedOnDateLowerHouse" value="${workflowStartedOnDate }">
				<input type="hidden" name="taskReceivedOnDateLowerHouse" id="taskReceivedOnDateLowerHouse" value="${taskReceivedOnDateLowerHouse }">				
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${houseTypeForStatus=='lowerhouse'}">
				<input type="hidden" name="workflowStartedOnDateLowerHouse" id="workflowStartedOnDateLowerHouse" value="${workflowStartedOnDateLowerHouse }">
				<input type="hidden" name="taskReceivedOnDateLowerHouse" id="taskReceivedOnDateLowerHouse" value="${taskReceivedOnDateLowerHouse }">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" name="workflowStartedOnDateUpperHouse" id="workflowStartedOnDateUpperHouse" value="${workflowStartedOnDateUpperHouse }">
				<input type="hidden" name="taskReceivedOnDateUpperHouse" id="taskReceivedOnDateUpperHouse" value="${taskReceivedOnDateUpperHouse }">
			</c:if>
		</c:otherwise>
	</c:choose>	

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
	<form:hidden path="file"/>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStartedLowerHouse"/>	
	<form:hidden path="workflowStartedUpperHouse"/>	
	<form:hidden path="endFlagLowerHouse"/>
	<form:hidden path="endFlagUpperHouse"/>
	<form:hidden path="levelLowerHouse"/>
	<form:hidden path="levelUpperHouse"/>
	<form:hidden path="localizedActorNameLowerHouse"/>
	<form:hidden path="localizedActorNameUpperHouse"/>
	<form:hidden path="workflowDetailsIdLowerHouse"/>
	<form:hidden path="workflowDetailsIdUpperHouse"/>
	<form:hidden path="fileLowerHouse"/>
	<form:hidden path="fileUpperHouse"/>
	<form:hidden path="fileIndexLowerHouse"/>	
	<form:hidden path="fileIndexUpperHouse"/>	
	<form:hidden path="fileSentLowerHouse"/>
	<form:hidden path="fileSentUpperHouse"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
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
	
	<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">	
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">			
	
	<form:hidden path="questionsAskedInFactualPosition"/>
	<c:if test="${selectedDeviceType == 'resolutions_government'}">
		<form:hidden path="ruleForDiscussionDate" value="${ruleForDiscussionDateSelected}"/>
	</c:if>
	<input id="ministrySelected" value="${ministrySelected }" type="hidden">
	<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
	<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
</form:form>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmResolutionSubmission" value="<spring:message code='confirm.resolutionsubmission.message' text='Do you want to submit the resolution.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='resolution.startworkflowmessage' text='Do You Want To Put Up Resolution?'></spring:message>" type="hidden">

<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ oldRecommendationStatus}" type="hidden">
<input id="selectedDeviceType" value="${selectedDeviceType}" type="hidden">
<input id="typeOfSelectedDeviceType" value="${selectedDeviceType}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input id="isRepeatWorkFlow" type="hidden" value="${isRepeatWorkFlow}" />
<input id="houseTypeType" type="hidden" value="${houseTypeForStatus}"/>
<input id="internalStatusType" type="hidden" value="${internalStatusType}"/>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>
</div>
</body>
</html>