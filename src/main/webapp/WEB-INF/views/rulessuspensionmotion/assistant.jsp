<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="rulessuspensionmotion" text="Rules Suspension Motion"/>
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
		var resourceURL='rulessuspensionmotion/'+id+'/edit?'+parameters;
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
		var params="rulesSuspensionMotionId="+id
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
		
		var resourceURL='rulessuspensionmotion/'+id+'/edit?'+parameters;
		$('a').removeClass('selected');
		//id refers to the tab name and it is used just to highlight the selected tab
		$('#'+ id).addClass('selected');
		//tabcontent is the content area where result of the url load will be displayed
		$('.tabContent').load(resourceURL,function(data){
			scrollTop();
			$.unblockUI();
		});
		$("#referencingResultDiv").hide();
		$("#clubbingResultDiv").hide();
		$("#assistantDiv").show();					
	}
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
		var params="motion="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/rulessuspensionmotion/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='rulessuspensionmotion_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='rulessuspensionmotion_recommend_discuss']").text();		
	    var clubbingPostAdmission = $("#internalStatusMaster option[value='rulessuspensionmotion_recommend_clubbingPostAdmission']").text();
		var unclubbing = $("#internalStatusMaster option[value='rulessuspensionmotion_recommend_unclubbing']").text();
		var admitDueToReverseClubbing = $("#internalStatusMaster option[value='rulessuspensionmotion_recommend_admitDueToReverseClubbing']").text();
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}				
				$("#actor").html(text);
				$("#actorDiv").show();				
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value!=sendback && value!=discuss
							&& value!=clubbingPostAdmission 
							&& value!=unclubbing
							&& value!=admitDueToReverseClubbing){
					$("#internalStatus").val(value);
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
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value!=sendback && value!=discuss
							&& value!=clubbingPostAdmission 
							&& value!=unclubbing
							&& value!=admitDueToReverseClubbing){
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
			$('#actorName').val("");
			$('#actorName').css('display','none');
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}

	function loadSubDepartment(ministry){
		$.get('ref/getSubDeparmentsByMinistries?ministries='+ ministry +'&session='+$('#session').val(),
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
		/**** Rules Suspension Date ****/
		$('#changeRuleSuspensionDate').click(function() {
			var yesLabel = $('#yesLabel').val();
			var noLabel = $('#noLabel').val();
			$.prompt('Do you really want to change the rule suspension date?', {
				buttons: [
					{title: yesLabel, value: true},
					{title: noLabel, value: false}
				],
				callback: function(v) {
					if(v) {
						$('#ruleSuspensionDate').removeAttr('disabled');
						$('#changeRuleSuspensionDate').hide();
					} else {
						return false;
					}
				}
			});			
		});

		/**** Ministry Changes ****/
		$("#ministries").change(function(){
			if($(this).val()!='' && $(this).val()!=null){
				loadSubDepartment($(this).val());
			}
		});						
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('rulessuspensionmotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});
		
		/**** Citations ****/
		$("#viewUserCitation").click(function(){
			$.get('rulessuspensionmotion/usercitations/'+$("#type").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:800});
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
		
		/**** Add subject and text****/
		$("#addSubject").click(function(){
			$("#authorityDraft").wysiwyg("setContent",$("#authorityDraft").wysiwyg("getContent")+ " "+ $("#subject").val());							
			return false;			
		});	
		$("#addNoticeContent").click(function(){
			$("#authorityDraft").wysiwyg("setContent",$("#authorityDraft").wysiwyg("getContent") +"<br>" + $("#noticeContent").val());			
			return false;			
		});
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
	    	$.get('rulessuspensionmotion/revisions/'+$("#id").val(), function(data){
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
		    $.get('rulessuspensionmotion/members/contacts?members='+members,function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
	    	var value=$(this).val();
		    if(value!='-'){
		    	$('#remarks_div').show();
			    loadActors(value);		
			    $("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $('#remarks_div').hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());	
			    $("#startworkflow").attr("disabled","disabled");
			    $("#submit").removeAttr("disabled");
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
			if($('#remarks').val()==undefined || $('#remarks').val()=="") {
				$.prompt("Please enter the remarks about the decision to be putup");
				return false;
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
		/**** To show/hide viewClubbedRules SuspensionMotionTextsDiv to view clubbed adjournment motion's text starts****/
		$("#clubbedRulesSuspensionMotionTextsDiv").hide();
		$("#hideClubMTDiv").hide();
		$("#viewClubbedRulesSuspensionMotionTextsDiv").click(function(){
			var parent = $("#key").val();
			if(parent==undefined || parent==''){
				parent = ($("#id").val()!=undefined && $("#id").val()!='')? $("#id").val():"";
			}
			if(parent!=undefined && parent!=''){			
				
				if($("#clubbedRulesSuspensionMotionTextsDiv").css('display')=='none'){
					$("#clubbedRulesSuspensionMotionTextsDiv").empty();
					$.get('ref/rulessuspensionmotion/'+parent+'/clubbedmotiontext',function(data){
						
						var text="";
						
						for(var i = 0; i < data.length; i++){
							text += "<p>"+data[i].name+"</p><p>"+data[i].value+"</p><hr />";
						}						
						$("#clubbedRulesSuspensionMotionTextsDiv").html(text);
						
					});	
					$("#hideClubMTDiv").show();
					$("#clubbedRulesSuspensionMotionTextsDiv").show();
				}else{
					$("#clubbedRulesSuspensionMotionTextsDiv").hide();
					$("#hideClubMTDiv").hide();
				}
			}
		});
		$("#hideClubMTDiv").click(function(){
			$(this).hide();
			$('#clubbedRulesSuspensionMotionTextsDiv').hide();
		});
		/**** To show/hide viewClubbedRules SuspensionMotionTextsDiv to view clubbed adjournment motion's text end****/
		
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
				$.post('clubentity/unclubbing?pId='+motionId+"&cId="+clubId+"&whichDevice=motions_rules_suspension_"+"&usergroupType="+$("#currentusergroupType").val(),function(data){
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
			}
	    });
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }		
	    if($("#revisedNoticeContent").val()!=''
	    		&& $("#revisedNoticeContent").val()!='<p></p>'){
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
        
        #clubbedRulesSuspensionMotionTextsDiv{
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
		<form:form action="rulessuspensionmotion" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2>
				${formattedMotionType}
				<c:choose>
				<c:when test="${not empty formattedRuleSuspensionDate and not empty formattedNumber}">
					(${formattedRuleSuspensionDate} - <spring:message code="generic.number" text="Number"/> ${formattedNumber})
				</c:when>
				<c:when test="${not empty formattedRuleSuspensionDate and empty formattedNumber}">
					(${formattedRuleSuspensionDate})
				</c:when>
				</c:choose>
			</h2>
			<form:errors path="version" cssClass="validationError"/>
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
				<form:errors path="houseType" cssClass="validationError"/>			
			</p>	
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.year" text="Year"/>*</label>
				<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
				<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.sessionType" text="Session Type"/>*</label>		
				<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
				<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
				<input type="hidden" id="session" name="session" value="${session}"/>
				<form:errors path="session" cssClass="validationError"/>	
			</p>
			
			<p style="display:none;">
				<label class="small"><spring:message code="rulessuspensionmotion.type" text="Type"/>*</label>
				<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
				<input id="type" name="type" value="${motionType}" type="hidden">
				<form:errors path="type" cssClass="validationError"/>		
			</p>
			
			<p>
				<label class="small"><spring:message code="rulessuspensionmotion.number" text="Motion Number"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>
				
				<label class="small"><spring:message code="rulessuspensionmotion.selectrulessuspensiondate" text="Rules Suspension Date"/></label>
				<input id="formattedRuleSuspensionDate" name="formattedRuleSuspensionDate" value="${formattedRuleSuspensionDate}" class="sText" readonly="readonly">
				<input id="ruleSuspensionDate" name="ruleSuspensionDate" type="hidden"  value="${selectedRuleSuspensionDate}">
			</p>		
			
			<c:if test="${!(empty submissionDate)}">
			<p>
				<label class="small"><spring:message code="rulessuspensionmotion.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate}" class="sText" readonly="readonly">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
			</p>
			</c:if>
				
			<p>
				<label class="small"><spring:message code="rulesuspensionmotion.ministry" text="Ministry"/></label>
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
								<option selected="selected" value="${i.id}">${i.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${i.id}">${i.name}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select> 	
				<form:errors path="ministries" cssClass="validationError"/>		
				<label class="small"><spring:message code="rulesuspensionmotion.subdepartment" text="Sub Department"/></label>
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
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.members" text="Members"/></label>
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
				<label class="small"><spring:message code="rulessuspensionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText">
				<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
			</p>			
			
			<p>
				<c:if test="${bulkedit!='yes' and domain.internalStatus.type!='rulessuspensionmotion_system_clubbed'}">
			
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="rulessuspensionmotion.clubbing" text="Clubbing"></spring:message></a>
				<%-- <a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="rulessuspensionmotion.referencing" text="Referencing"></spring:message></a> --%>
				<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="rulessuspensionmotion.refresh" text="Refresh"></spring:message></a>
				</c:if>	
			</p>
			
			
			<p>
				<label class="small"><spring:message code="rulessuspensionmotion.parentmotion" text="Clubbed To"></spring:message></label>
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
				<label class="small"><spring:message code="rulessuspensionmotion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty clubbedMotions) }">
						<c:forEach items="${clubbedMotions }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
						<a href="javascript:void(0);" id="viewClubbedRulesSuspensionMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="rulessuspensionmotion.clubbed.texts" text="C"></spring:message></a>
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
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.subject" text="Subject"/>*</label>
				<form:textarea path="subject" rows="2" cols="50" readonly="true"></form:textarea>
				<form:errors path="subject" cssClass="validationError" />	
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.noticeContent" text="Notice Content"/>*</label>
				<form:textarea path="noticeContent" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="noticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>	
			
			<p>
				<c:choose>
					<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<a href="#" id="viewRevision" style="margin-left: 162px;margin-right: 20px;"><spring:message code="rulessuspensionmotion.viewrevisions" text="View Revisions"></spring:message></a>
					</c:when>
					<c:otherwise>
						<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="rulessuspensionmotion.reviseSubject" text="Revise Subject"></spring:message></a>
						<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="rulessuspensionmotion.reviseNoticeContent" text="Revise Notice Content"></spring:message></a>
						<a href="#" id="viewRevision"><spring:message code="rulessuspensionmotion.viewrevisions" text="View Revisions"></spring:message></a>
					</c:otherwise>
				</c:choose>
				
				
			</p>	
			
			<p style="display:none;" class="revise1" id="revisedSubjectDiv">
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.revisedSubject" text="Revised Subject"/></label>
				<c:choose>
					<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<form:textarea path="revisedSubject" rows="4" cols="70" readonly="true"></form:textarea>
					</c:when>
					<c:otherwise>
						<form:textarea path="revisedSubject" rows="4" cols="70"></form:textarea>
					</c:otherwise>
				</c:choose>
				<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p style="display:none;" class="revise2" id="revisedNoticeContentDiv">
				<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.revisedNoticeContent" text="Revised Notice Content"/></label>
				<c:choose>
					<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<form:textarea path="revisedNoticeContent" cssClass="wysiwyg" readonly="true"></form:textarea>
					</c:when>
					<c:otherwise>
						<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
					</c:otherwise>
				</c:choose>
				<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p>
				<c:choose>
					<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<a href="#" id="addSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="rulessuspensionmotion.addSubject" text="Add Subject"></spring:message></a>
						<a href="#" id="addNoticeContent" style="margin-right: 20px;"><spring:message code="rulessuspensionmotion.addNoticeContent" text="Add Notice Content"></spring:message></a>
					</c:when>
					<c:otherwise>
						
					</c:otherwise>
				</c:choose>
			</p>
			
			<c:choose>
				<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<p>
							<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.authorityDraft" text="Authority Draft"/></label>
							<form:textarea path="authorityDraft" cssClass="wysiwyg"></form:textarea>
							<form:errors path="authorityDraft" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
						</p>
				</c:when>
				<c:otherwise>
					<form:hidden path="authorityDraft"/>
				</c:otherwise>
			</c:choose>

			
			<p id="internalStatusDiv">
				<label class="small"><spring:message code="rulessuspensionmotion.currentStatus" text="Current Status"/></label>
				<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
			</p>
			
			<c:if test="${internalStatusType == 'rulessuspensionmotion_system_assistantprocessed' || internalStatusType == 'rulessuspensionmotion_putup_rejection'
				|| internalStatusType == 'rulessuspensionmotion_putup_clubbing' || internalStatusType == 'rulessuspensionmotion_putup_nameclubbing' 
				|| recommendationStatusType == 'rulessuspensionmotion_putup_clubbingPostAdmission' || recommendationStatusType == 'rulessuspensionmotion_putup_clubbingWithUnstarredFromPreviousSession'
				|| recommendationStatusType == 'rulessuspensionmotion_putup_unclubbing' || recommendationStatusType == 'rulessuspensionmotion_putup_admitDueToReverseClubbing'}">
				<security:authorize access="hasAnyRole('RSMOIS_ASSISTANT','RSMOIS_SECTION_OFFICER')">
				<p>	
					<label class="small"><spring:message code="rulessuspensionmotion.putupfor" text="Put up for"/></label>	
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
				</security:authorize>
				<p id="actorDiv" style="display: none;">
					<label class="small"><spring:message code="rulessuspensionmotion.nextactor" text="Next Users"/></label>
					<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
					<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
				</p>
			</c:if>		
			<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
			<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">			
			<c:if test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
				<form:hidden path="actor"/>
			</c:if>
			<c:if test="${fn:contains(internalStatusType, 'rulessuspensionmotion_final')}">
				<form:hidden path="actor"/>
			</c:if>
			
			<c:choose>
			<c:when test="${not empty domain.reply}">
			<p>
				<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.reply" text="Reply"/></label>
				<form:textarea path="reply" cssClass="wysiwyg" readonly="true"></form:textarea>
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
				<label class="wysiwyglabel"><spring:message code="rulessuspensionmotion.rejectionReason" text="Reply"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="rejectionReason" cssClass="validationError"></form:errors>
			</p>
			</c:when>
			<c:otherwise>
				<form:hidden path="rejectionReason"/>
			</c:otherwise>
			</c:choose>			
			
			<p>
				<c:choose>
					<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
						<a href="#" id="viewUserCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="rulessuspensionmotion.viewcitation" text="View Citations"></spring:message></a>
					</c:when>
					<c:otherwise>
						<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="rulessuspensionmotion.viewcitation" text="View Citations"></spring:message></a>
					</c:otherwise>
				</c:choose>
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="rulessuspensionmotion.remarks" text="Remarks"/></label>
				<form:textarea path="remarks" rows="4" cols="70"></form:textarea>
				<form:hidden path="remarksAboutDecision"/>
			</p>	
			</div>
				
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<c:choose>
						<c:when test="${usergroupType == 'section_officer' || usergroupType == 'principal_secretary'}">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${(internalStatusType eq'rulessuspensionmotion_final_admission' || internalStatusType eq 'rulessuspensionmotion_final_rejection') && empty parent}">
									<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
								</c:when>
								<c:when test="${bulkedit!='yes'}">
									<c:if test="${internalStatusType=='rulessuspensionmotion_submit' || internalStatusType=='rulessuspensionmotion_system_assistantprocessed' || internalStatusType=='rulessuspensionmotion_system_putup'
										|| internalStatusType=='rulessuspensionmotion_putup_rejection' || internalStatusType == 'rulessuspensionmotion_putup_clubbing' || internalStatusType == 'rulessuspensionmotion_putup_nameclubbing' 
										|| recommendationStatusType == 'rulessuspensionmotion_putup_clubbingPostAdmission' || recommendationStatusType == 'rulessuspensionmotion_putup_unclubbing' 
										|| recommendationStatusType == 'rulessuspensionmotion_putup_admitDueToReverseClubbing'}">
										<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
										<security:authorize access="hasAnyRole('RSMOIS_ASSISTANT','RSMOIS_SECTION_OFFICER')">
											<input id="startworkflow" type="button" value="<spring:message code='rulessuspensionmotion.putuprulessuspensionmotion' text='Put Up Motion'/>" class="butDef">
										</security:authorize>					
									</c:if>							
								</c:when>						
								<c:otherwise>
									<c:if test="${bulkedit=='yes'}">
										<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>

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
			<input id="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
		</form:form>

		<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='rulessuspensionmotion.startworkflowmessage' text='Do You Want To Put Up Rules Suspension Motion?'></spring:message>" type="hidden">
		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">		
		<input id="originalLevel" value="${ domain.level}" type="hidden">		
		<input id="motionTypeType" value="${selectedMotionType}" type="hidden"/>
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
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
	<div id="clubbedRulesSuspensionMotionTextsDiv">
		<h1>		
			<spring:message code="rulessuspensionmotion.clubbedMotionTexts" text="Motion texts of clubbed motions:"></spring:message>
		</h1>
	</div>
	<div id="hideClubMTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
	
	<div id="referencingResultDiv" style="display:none;">
	</div>
</body>
</html>