<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced motions ****/		
	function viewMotionDetail(id, targetD){
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
		var resourceURL='motion/'+id+'/edit?'+parameters;
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
	function viewQuestionDetail(id, targetD){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+targetD
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
	function viewResolutionDetail(id, targetD){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&deviceType="+targetD
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
		if(1==2){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val()
	        +"&deviceType="+$("#motionType").val();
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
		if(value!='-'){
		var params="motion="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/motion/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='motion_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='motion_recommend_discuss']").text();		
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
					if(i!=0){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}else{
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
						$("#actorName").val(data[i].id.split("#")[4]);
					}
				}
				text+="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
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
				 $("#actorName").val(temp[3]+"("+temp[4]+")");					
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
			$("#actorName").val(temp[3]+"("+temp[4]+")");
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
				$.post('clubentity/unclubbing?whichDevice=motions_&pId='+motionId+"&cId="+clubId,function(data){
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
				$.post('refentity/motion/dereferencing?pId='+motionId+"&rId="+refId,function(data){
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
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
	    if($("#revisedDetails").val()!=''){
	    	$("#revisedDetailsDiv").show();
	    }  	  
	    
	    /**** Load Actors On Start Up ****/
		if($('#workflowstatus').val()!='COMPLETED'){
			var statusType = $("#internalStatusType").val().split("_");
			var id = $("#internalStatusMaster option[value$='"+statusType[statusType.length-1]+"']").text();
			//alert($('#workflowstatus').val()+":"+statusType+":"+id);
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
	<c:choose>
		<c:when test="${domain.postBallotNumber != null}">
			<h2>${formattedMotionType}: ${formattedPostBallotNumber}</h2>
		</c:when>
		<c:otherwise>
			<h2>${formattedMotionType}: ${formattedNumber}</h2>
		</c:otherwise>
	</c:choose>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
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
	
	<div style="display: inline-block;">
		<p>
			<label class="small"><spring:message code="motion.number" text="Motion Nmber"/>*</label>
			<c:choose>
				<c:when test="${domain.postBallotNumber != null}">
					<input id="formattedNumber" name="formattedNumber" value="${formattedPostBallotNumber}" class="sText" readonly="readonly">
					<input id="number" name="number" value="${domain.number}" type="hidden">
				</c:when>
				<c:otherwise>
					<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">
					<input id="number" name="number" value="${domain.number}" type="hidden">
				</c:otherwise>
			</c:choose>
			<form:errors path="number" cssClass="validationError"/>
		</p>
		
		<p>		
			<label class="small"><spring:message code="motion.submissionDate" text="Submitted On"/></label>
			<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		</p>
			
	</div>
	
	<div style="display: inline-block;">
		<p>
			<label class="small"><spring:message code="motion.ministry" text="Ministry"/>*</label>
			<select name="ministry" id="ministry" class="sSelect">
				<option value=""><spring:message code='please.select' text='Please Select'/></option>
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
		</p>	
		
		<p>
			<label class="small"><spring:message code="motion.subdepartment" text="Sub Department"/></label>
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
	</div>	
	
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
	
	<security:authorize access="hasAnyRole('MOIS_ASSISTANT')">
		<p>
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="motion.clubbing" text="Clubbing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="motion.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="motion.refresh" text="Refresh"></spring:message></a>	
		</p>	
	</security:authorize>
		
	<c:if test="${!(empty parent)}">	
	<p>
		<label class="small"><spring:message code="motion.parentmotion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewMotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>
	</c:if>	
	
	<c:if test="${!(empty clubbedEntities) }">
	<p>
		<label class="small"><spring:message code="motion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
		<c:choose>
		<c:when test="${!(empty clubbedEntities) }">
		<c:forEach items="${clubbedEntities }" var="i">
		<a href="#" id="cq${i.number}" class="cb" onclick="viewMotionDetail(${i.number},101);" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
		
	<p>
		<label class="small"><spring:message code="motion.referencedmotions" text="Referenced Motions"></spring:message></label>
		<c:if test="${!(empty referencedMotions) }">
			<c:choose>
				<c:when test="${!(empty referencedMotions) }">
					<c:forEach items="${referencedMotions }" var="i">
						<a href="#" id="rq${i.device}" class="cbdevice${i.deviceTypeId}" onclick="viewMotionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>	
		</c:if>	
	</p>
					
	<p>
		<label class="small"><spring:message code="motion.referencedquestions" text="Referenced Questions"></spring:message></label>
		<c:if test="${!(empty referencedQuestions) }">
			<c:choose>
				<c:when test="${!(empty referencedQuestions) }">
					<c:forEach items="${referencedQuestions }" var="i">
						<a href="#" id="rq${i.device}" class="cbdevice${i.deviceTypeId}" onclick="viewQuestionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>		
		</c:if>	
	</p>
		
	
	<p>
		<label class="small"><spring:message code="motion.referencedmotions" text="Referenced Resolutions"></spring:message></label>
		<c:if test="${!(empty referencedResolutions) }">
			<c:choose>
				<c:when test="${!(empty referencedResolutions) }">
					<c:forEach items="${referencedResolutions }" var="i">
						<a href="#" id="rq${i.device}" class="cbdevice${i.deviceTypeId}" onclick="viewResolutionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>
		</c:if>		
	</p>
		
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
	
	<c:if test="${workflowstatus=='PENDING'}">
	<p>
	<label class="small"><spring:message code="motion.putupfor" text="Put up for"/></label>	
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
	
	<p id="actorDiv"style="display: none;">
	<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
	<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
	<input type="text" class="sText" readonly="readonly" id="actorName" value=""/>
	</p>	
	</c:if>
		
	<p>
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="motion.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="motion.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<c:if test="${workflowstatus=='PENDING'}">
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
	<input type="hidden" name="workflowDetailsId" id="workflowDetailsId" value="${workflowdetails}" />
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="replyReceivedDate"/>
	<form:hidden path="advanceCopySent"/>
	<form:hidden path="advanceCopyPrinted"/>
	<form:hidden path="advanceCopyActor"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<input type="hidden" id="discussionStatus"  name="discussionStatus" value="${discussionStatus}">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
	<input type="hidden" id="answeringDate" name="setAnsweringDate" class="datemask sText" value="${formattedAnsweringDate}"/>
	<input type="hidden" id="discussionDate" name="setDiscussionDate" class="datemask sText" value="${formattedDiscussionDate}"/>
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
<input type="hidden" id="originalLevel" value="${domain.level}" />
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">

 <select id="allDevices" style="display: none;">
	<c:forEach items="${allDevices}" var="i">
		<option value="${i.id}">${i.type}</option>
	</c:forEach>
</select>

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