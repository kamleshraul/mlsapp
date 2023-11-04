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
        +"&deviceType="+$("#deviceType").val()
		+"&usergroupType="+$("#usergroupType").val()
		+"&houseType="+$("#houseTypeType").val();
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
	
	/*******Dereferencing resolution **********/
	function dereferencingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="pId="+id
		+"&rId="+$("#refResolutionEntity").val()
        +"&device="+$("#deviceType").val();
		$.post('refentity/dereferencing?'+params,function(data){
			if(data=='SUCCESS'){
				$.prompt("Dereferencing Successful");				
			}else{
				$.prompt("Dereferencing Failed");
			}
			$.unblockUI();
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
	/**** load actors ****/
	function loadActors(value){		
		if(value!='-' && value!=undefined){
			var params='';
			if($('#houseTypeType').val()=='lowerhouse'){
				 params="resolution="+$("#id").val()+"&status="+value+
				"&usergroup="+$("#usergroup").val()+"&level="+$("#levelLowerHouse").val()+"&workflowHouseType="+$("#workflowHouseType").val();
			}else if($('#houseTypeType').val()=='upperhouse'){
				 params="resolution="+$("#id").val()+"&status="+value+
				"&usergroup="+$("#usergroup").val()+"&level="+$("#levelUpperHouse").val()+"&workflowHouseType="+$("#workflowHouseType").val();
			}
			
			var resourceURL='ref/resolution/actors?'+params;
		    var sendback=$("#internalStatusMaster option[value='resolution_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='resolution_recommend_discuss']").text();		
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
					
					$("#actorDiv").show();				
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
					$("#internalStatus").val(value);
					}
					$("#recommendationStatus").val(value);	
					/**** setting level,localizedActorName ****/
					 //var actor1=data[0].id;
					 console.log(actor1);
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
		$("#ministry").change(function(){
			console.log($("#subDepartment").val());
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			}
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
			if($("#revisedNoticeContentDiv").css("display")=="none"){
				$("#revisedNoticeContent").wysiwyg("setContent","");
			}else{
				$("#revisedNoticeContent").wysiwyg("setContent",$("#noticeContent").val());				
			}				
			return false;			
		});	
		
			
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    var urlForRevisions;		    
	    	if($("#deviceType").val() == 'resolutions_government') {	    		
	    		urlForRevisions = 'resolution/revisions/'+$("#id").val() + '?workflowHouseType='+$("#workflowHouseType").val();
	    	} else {
	    		urlForRevisions = 'resolution/revisions/'+$("#id").val();
	    	}	    	
		    $.get(urlForRevisions,function(data){
			    $.fancybox.open(data,{autoSize: false, width: 700, height:700});
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
		    if(value!='-' || $("#isRepeatWorkFlow").val()=='yes' ){
			    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();			    
			    loadActors(value);	
			    //$("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");		    
		    }else{
		    	 if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").empty();
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#actorUpperHouse").empty();
				}
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#startworkflow").attr("disabled","disabled");
			    $("#submit").removeAttr("disabled");
			}		    
	    });
	    
	    /**** Recommendation Status Change ****/
	     $("#changeRecommendationStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
		    	var passed=$("#recommendationStatusMaster option[value='resolution_final_passed']").text();			
			    var negatived=$("#recommendationStatusMaster option[value='resolution_final_negatived']").text();
			    var withdrawn=$("#recommendationStatusMaster option[value='resolution_final_withdrawn']").text();
			    if(value==passed || value==negatived || value==withdrawn){
			    	$('#internalStatus').val(value);
			    	$('#status').val(value);
			    }
		    	$("#recommendationStatus").val(value);	    
		    }else{
		    	 if($('#houseTypeType').val()=='lowerhouse'){
					 $("#actorLowerHouse").empty();
				}else if($('#houseTypeType').val()=='upperhouse'){
					$("#actorUpperHouse").empty();
				}
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
		
		/**** if its repeat workflow remove disability ****/
		 if($('#isRepeatWorkFlow').val()!=''){
	    	if($('#isRepeatWorkFlow').val()=='yes'){
	    		$("#changeInternalStatus").val($("#recommendationStatus").val());
	    		loadActors($("#changeInternalStatus").val());
	    		//$("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");	
	    	}	    	
	    }
	    /**** Put Up ****/
		$("#startworkflow").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			if($('#votingDetail').val()=="null"){
				$('#votingDetail').val('');
			}
			if($('#ballotStatus').val()=="null"){
				$('#ballotStatus').val('');
			}
			if($('#discussionStatus').val()=="null"){
				$('#discussionStatus').val('');
			}
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
			if($('#votingDetail').val()=="null"){
				$('#votingDetail').val('');
			}
			if($('#ballotStatus').val()=="null"){
				$('#ballotStatus').val('');
			}
			if($('#discussionStatus').val()=="null"){
				$('#discussionStatus').val('');
			}
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
		
		$("#submit").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			
			if($('#votingDetail').val()=="null"){
				$('#votingDetail').val('');
			}
			if($('#ballotStatus').val()=="null"){
				$('#ballotStatus').val('');
			}
			if($('#discussionStatus').val()=="null"){
				$('#discussionStatus').val('');
			}
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
			$.post($('form').attr('action'), $("form").serialize(), function(data){
				$('.tabContent').html(data);
					$('html').animate({scrollTop:0}, 'slow');
					$('body').animate({scrollTop:0}, 'slow');	
				$.unblockUI();
			});
	         
	    });
			  
		$('#addVotingDetail').click(function(e) {
	    	$.get('votingdetail/new?deviceId='+$("#id").val()+ "&houseType="+ $("#selectedHouseType").val()
	    			+ "&deviceType="+ $("#selectedDeviceType").val()
	    			+ "&votingFor="+ $("#votingForPassingOfResolution").val()+ "&openThroughOverlay=yes", function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');		    	
		    return false;
	    });
	    $('#editVotingDetails').click(function(e) {
	    	$.get('votingdetail/editVotingDetailsForDevice?deviceId='+$("#id").val()+ "&houseType="+ $("#selectedHouseType").val()
	    			+ "&openThroughOverlay=yes"
	    			+ "&deviceType="+ $("#selectedDeviceType").val()
	    			+ "&votingFor="+ $("#votingForPassingOfResolution").val(), function(data){
			    $.fancybox.open(data, {autoSize: false, width: 1000, height:600});
		    },'html');		    	
		    return false;
	    });
	    /**** On Page Load ****/
	   // loadActors($("#changeInternalStatus").val());
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
	    if($("#revisedNoticeContent").val()!=''){
	    	$("#revisedNoticeContentDiv").show();
	    }
	   	  
	});
	
	/**** Clubbing ****/
	function clubbingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="resolutionId="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val()+'&useforfiling=yes';		
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
<form:form action="resolution" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedDeviceType}: ${formattedNumber}</h2>
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
	</p>
		
	<p>		
	<label class="small"><spring:message code="resolution.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	</p>
	<p>
	<label class="small"><spring:message code="resolution.ministry" text="Ministry"/>*</label>
	<select name="ministry" id="ministry" class="sSelect">
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
	<c:if test="${selectedDeviceType == 'resolutions_government'}">
		<label class="small"><spring:message code="resolution.discussionDate" text="Discussion Date"/></label>
		<form:input path="discussionDate" cssClass="datemask sText" readonly="${isDiscussionDateReadOnly}"/>
		<form:errors path="discussionDate" cssClass="validationError"/>
	</c:if>
	</p>	
	
	<p>
	<label class="small"><spring:message code="resolution.subdepartment" text="Sub Department"/></label>
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
	<label class="small"><spring:message code="resolution.member" text="Member"/></label>
	<input type="text" id="members" class="sTextarea" readonly="readonly" value="${formattedMember}">
	<c:if test="${!(empty member)}">
		<input id="member" name="member" value="${member}" type="hidden">
	</c:if>
	</p>
	
	<p>
		<label class="small"><spring:message code="resolution.memberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<c:if test="${selectedDeviceType=='resolutions_nonofficial'}" >
		<p>
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="resolution.addfile" text="Filing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});"><spring:message code="generic.referencing" text="Referencing"></spring:message></a>
			<a href="#dereferencing" onclick="dereferencingInt(${domain.id});" style="margin: 20px;"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="generic.refresh" text="Refresh"></spring:message></a>	
		</p>
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
	<a href="#" id="viewRevision"><spring:message code="resolution.viewrevisions" text="View Revisions"></spring:message></a>
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
	<c:if test="${recommendationStatusType=='resolution_processed_underconsideration' || 
				  recommendationStatusType=='resolution_processed_passed' ||
				  recommendationStatusType=='resolution_processed_negatived'||
				  recommendationStatusType=='resolution_processed_withdrawn'}">
		<p style="margin-left:165px">
			<a id="addVotingDetail" href="javascript:void(0);"><spring:message code="resolution.addVotingDetail" text="Add Voting Details"/></a>	
			<a id="editVotingDetails" href="javascript:void(0);" style="display: inline; margin-left: 20px;"><spring:message code="resolution.editVotingDetails" text="Edit Voting Details"/></a>
		</p>
	</c:if>
	<c:choose>
		<c:when test="${recommendationStatusType =='resolution_processed_underconsideration' }">
			
			<p id="recommendationStatusDiv">
				<label class="small"><spring:message code="resolution.currentStatus" text="Current Status"/></label>
				<input id="formattedRecommendationStatus" name="formattedRecommendationStatus" value="${formattedRecommendationStatus}" type="text" readonly="readonly">
			</p>
			<p>
				<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>
				<select id="changeRecommendationStatus" class="sSelect">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${recommendationStatuses}" var="i">
						<c:choose>
							<c:when test="${i.id==recommendationStatusSelected}">
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
				<select id="recommendationStatusMaster" style="display:none;">
				<c:forEach items="${recommendationStatuses}" var="i">
					<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
				</c:forEach>
				</select>
				<c:if test="${houseTypeForStatus=='lowerhouse'}">
					 <form:errors path="recommendationStatusLowerHouse" cssClass="validationError"/>	 
				</c:if>
				<c:if test="${houseTypeForStatus=='upperhouse'}">
				 	<form:errors path="recommendationStatusUpperHouse" cssClass="validationError"/>	 
				</c:if>
			</p>
		</c:when>
		<c:otherwise>
			<p id="internalStatusDiv">
				<label class="small"><spring:message code="resolution.currentStatus" text="Current Status"/></label>
				<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
				<c:if test="${domain.sentForClarification=='true'}">
					<img src="./resources/images/sent_for_clarification.png" style="display:inline-block;" title="Sent for Clarification" width="15px" height="15px">
				</c:if>
				</p>
				 
				 <c:if test="${((internalStatusType=='resolution_system_putup' or internalStatusType=='resolution_recommend_repeat') && selectedDeviceType=='resolutions_nonofficial') 
						|| (internalStatusType=='resolution_system_assistantprocessed' && selectedDeviceType=='resolutions_government')
						|| ((internalStatusType=='resolution_recommend_admission' or internalStatusType=='resolution_recommend_repeat' or internalStatusType=='resolution_recommend_rejection') && selectedDeviceType=='resolutions_nonofficial' && bulkedit=='yes')}">		
				<security:authorize access="hasAnyRole('ROIS_ASSISTANT')">
				<p>
				<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>
				<select id="changeInternalStatus" class="sSelect">
				<option value="-"><spring:message code='please.select' text='Please Select'/></option>
				<c:forEach items="${internalStatuses}" var="i">
					<c:if test="${(i.type!='resolution_recommend_sendback'&&i.type!='resolution_recommend_discuss') }">
							<c:choose>
								<c:when test="${i.id==internalStatusSelected}">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
								</c:when>
								<c:otherwise>
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
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
				<c:if test="${houseTypeForStatus=='lowerhouse'}">
				 	<form:errors path="internalStatusLowerHouse" cssClass="validationError"/>	 
				</c:if>
				<c:if test="${houseTypeForStatus=='upperhouse'}">
					 <form:errors path="internalStatusUpperHouse" cssClass="validationError"/>	 
				</c:if>
				</p>
				</security:authorize>
				<p id="actorDiv" style="display: none;">
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
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${selectedDeviceType== 'resolutions_nonofficial' and domain.discussionDate!=null}">
			<label class="small"><spring:message code="resolution.discussionDate" text="Discussion Date"/></label>
		<form:input path="discussionDate" cssClass="datemask sText"/>
		<form:errors path="discussionDate" cssClass="validationError"/>
		</c:when>
		<c:otherwise>
			<form:hidden path="discussionDate"/>
		</c:otherwise>
	</c:choose>
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
				<input type="hidden" id="internalStatus"  name="internalStatusLowerHouse" value="${internalStatus}">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatusLowerHouse" value="${recommendationStatus}">
				<input type="hidden" name="statusLowerHouse" id="status" value="${status}">
			</c:if>	
			<c:if test="${houseTypeForStatus=='upperhouse'}">
				<input type="hidden" id="internalStatus"  name="internalStatusUpperHouse" value="${internalStatus}">
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
	<p>
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="resolution.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="resolution.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">		
		<c:choose>
			<c:when test="${internalStatusType=='resolution_submit'}">	
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:when>
			<c:when test="${internalStatusType=='resolution_system_assistantprocessed' && selectedDeviceType=='resolutions_nonofficial'}">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:when>
			<c:when test="${(internalStatusType=='resolution_final_admission' || internalStatusType=='resolution_final_repeatadmission') && selectedDeviceType=='resolutions_nonofficial'}">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:when>
			<c:otherwise>		
					<c:if test="${bulkedit!='yes'}">
						<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						<c:choose>
							<c:when test="${internalStatusType=='resolution_system_putup' && selectedDeviceType=='resolutions_nonofficial' 
											|| internalStatusType=='resolution_system_assistantprocessed' && selectedDeviceType=='resolutions_government'}">
								<security:authorize access="hasAnyRole('ROIS_ASSISTANT')">
									<input id="startworkflow" type="button" value="<spring:message code='resolution.putupresolution' text='Put Up Resolution'/>" class="butDef">
								</security:authorize>
							</c:when>
						</c:choose>
					</c:if>
					<c:if test="${bulkedit=='yes'}">
						<input id="submitBulkEdit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
					</c:if>
			</c:otherwise>
		</c:choose>
		</p>
	</div>
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
	<form:hidden path="file"/>
	<form:hidden path="fileLowerHouse"/>
	<form:hidden path="fileUpperHouse"/>
	<form:hidden path="fileIndexLowerHouse"/>	
	<form:hidden path="fileIndexUpperHouse"/>	
	<form:hidden path="fileSentLowerHouse"/>
	<form:hidden path="fileSentUpperHouse"/>
	<form:hidden path="sentForClarification"/>
	<form:hidden path="ballotStatus" value="${ballotStatusId}"/>
	<form:hidden path="discussionStatus" value="${discussionStatusId}"/>
	<form:hidden path="karyavaliGenerationDate"/>
	<form:hidden path="karyavaliNumber"/>
	<form:hidden path="rejectionReason"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">	
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	
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
	
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
			
	<form:hidden path="questionsAskedInFactualPosition"/>
	<form:hidden path="votingDetail"/>		
	<c:if test="${selectedDeviceType == 'resolutions_government'}">
		<input type="hidden" name="ruleForDiscussionDate" value="${ruleForDiscussionDateSelected}"/>
	</c:if>	
</form:form>
<input id="votingDetailsId" type="hidden" name="votingDetailsId" value="${votingDetailId}"/>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmResolutionSubmission" value="<spring:message code='confirm.resolutionsubmission.message' text='Do you want to submit the resolution?'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='resolution.startworkflowmessage' text='Do You Want To Put Up Resolution?'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
<input id="deviceType" value="${domain.type.type}" type="hidden" />
<input id="isRepeatWorkFlow" type="hidden" value="${isRepeatWorkFlow}" />
<input id="resolutionType" type="hidden" value="${selectedDeviceType}" />
<input id="refResolutionEntity" type="hidden" value="${referencedEntityId}" />
<input id="houseTypeType" type="hidden" value="${houseTypeForStatus }"/>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="votingForPassingOfResolution" value="${votingFor}">
</div> 
</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingResultDiv" style="display:none;">
</div>
</body>
</html>