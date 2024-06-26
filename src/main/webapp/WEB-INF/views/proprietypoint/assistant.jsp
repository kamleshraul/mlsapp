<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proprietypoint" text="Propriety Point"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed propriety points ****/
	function viewProprietyPointDetail(id){
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
		var params="proprietyPointId="+id
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
		var params="proprietypoint="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#originalLevel").val();
		var resourceURL='ref/proprietypoint/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='proprietypoint_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='proprietypoint_recommend_discuss']").text();		
	   $.get(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}				
				$("#actor").html(text);
				$("#actorDiv").show();				
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value!=sendback && value!=discuss){
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
				if(value!=sendback && value!=discuss){
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
			$.get('proprietypoint/citations/'+$("#deviceType").val()+ "?status=" + $("#internalStatus").val(),function(data){
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
		$("#revisePointsOfPropriety").click(function(){
			$(".revise2").toggle();		
			if($("#revisedPointsOfProprietyDiv").css("display")=="none"){
				$("#revisedPointsOfPropriety").wysiwyg("setContent","");
			}else{
				$("#revisedPointsOfPropriety").wysiwyg("setContent",$("#pointsOfPropriety").val());				
			}				
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
		
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }		
	    if($("#revisedPointsOfPropriety").val()!=''
	    		&& $("#revisedPointsOfPropriety").val()!='<p></p>'){
	    	$("#revisedPointsOfProprietyDiv").show();
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
    </style>
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
	<div id="assistantDiv">
		<form:form action="proprietypoint" method="PUT" modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
			<div id="reportDiv">
			<h2>
				${formattedDeviceType}
				<c:choose>
					<c:when test="${not empty formattedProprietyPointDate and not empty formattedNumber}">
						(${formattedProprietyPointDate} - <spring:message code="generic.number" text="Number"/> ${formattedNumber})
					</c:when>
					<c:when test="${not empty formattedProprietyPointDate and empty formattedNumber}">
						(${formattedProprietyPointDate})
					</c:when>
					<c:when test="${not empty formattedNumber}">
						(<spring:message code="generic.number" text="Number"/> ${formattedNumber})
					</c:when>
				</c:choose>
			</h2>
			<form:errors path="version" cssClass="validationError"/>
			
			<p style="display:none;">
				<label class="small"><spring:message code="proprietypoint.houseType" text="House Type"/>*</label>
				<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
				<input id="houseType" name="houseType" value="${houseType}" type="hidden">
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
				<label class="small"><spring:message code="proprietypoint.deviceType" text="Device Type"/>*</label>
				<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
				<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">
				<form:errors path="deviceType" cssClass="validationError"/>		
			</p>
			
			<p>
				<label class="small"><spring:message code="proprietypoint.number" text="Device Number"/>*</label>
				<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
				<input id="number" name="number" value="${domain.number}" type="hidden">
				<form:errors path="number" cssClass="validationError"/>
				
				<%-- <c:if test="${houseTypeType=='lowerhouse' and !(empty submissionDate)}">
					<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
					<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
					<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
				</c:if> --%>
				
				<%-- <c:if test="${houseTypeType=='upperhouse'}"> --%>
					<label class="small"><spring:message code="proprietypoint.selectproprietypointdate" text="Propriety Point Date"/></label>
					<input id="formattedProprietyPointDate" name="formattedProprietyPointDate" value="${formattedProprietyPointDate}" class="sText" readonly="readonly">
					<input id="proprietyPointDate" name="proprietyPointDate" type="hidden"  value="${selectedProprietyPointDate}">
				<%-- </c:if> --%>
			</p>
			
			<c:if test="${!(empty submissionDate)}">
			<p>
				<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
				<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
				
				<c:choose>
				<c:when test="${internalStatusType=='proprietypoint_final_admission'}">
					<label class="small"><spring:message code="proprietypoint.admissionNumber" text="Admission Number"/></label>
					<input id="formattedAdmissionNumber" name="formattedAdmissionNumber" value="${formattedAdmissionNumber}" class="sText" readonly="readonly">		
					<input id="admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
					<form:errors path="admissionNumber" cssClass="validationError"/>	
				</c:when>
				<c:otherwise>
					<input id="admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
				</c:otherwise>
				</c:choose>
			</p>
			</c:if>
				
			<p>
				<label class="small"><spring:message code="proprietypoint.ministry" text="Ministry"/></label>
				<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
				<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}">
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
			
			<p>
				<label class="small"><spring:message code="proprietypoint.primaryMemberConstituency" text="Constituency"/>*</label>
				<input type="text" readonly="readonly" value="${constituency}" class="sText">
				<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
			</p>	
			
			<p>
				<c:if test="${bulkedit!='yes' and domain.internalStatus.type!='proprietypoint_system_clubbed'}">
			
				<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="proprietypoint.clubbing" text="Clubbing"></spring:message></a>
				<%-- <a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="proprietypoint.referencing" text="Referencing"></spring:message></a> --%>
				<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="proprietypoint.refresh" text="Refresh"></spring:message></a>
				</c:if>	
			</p>			
			
			<p>
				<label class="small"><spring:message code="proprietypoint.parent" text="Clubbed To"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty parent)}">	
						<a href="#" id="p${parent}" onclick="viewProprietyPointDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="parent" name="parent" value="${parent}">
			</p>
			
			<p>
				<label class="small"><spring:message code="proprietypoint.clubbedmotions" text="Clubbed Motions"></spring:message></label>
				<c:choose>
					<c:when test="${!(empty clubbedProprietyPoints) }">
						<c:forEach items="${clubbedProprietyPoints }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefProprietyPoints" onclick="viewProprietyPointDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
						</c:forEach>
						<a href="javascript:void(0);" id="viewClubbedProprietyPointTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="proprietypoint.clubbed.texts" text="C"></spring:message></a>
					</c:when>
					<c:otherwise>
						<c:out value="-"></c:out>
					</c:otherwise>
				</c:choose>
				<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
					<c:forEach items="${clubbedProprietyPoints}" var="i">
						<option value="${i.id}" selected="selected"></option>
					</c:forEach>
				</select>
			</p>		
			
			<p>
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
				<a href="#" id="revisePointsOfPropriety" style="margin-right: 20px;"><spring:message code="proprietypoint.revisePointsOfPropriety" text="Revise Notice Content"></spring:message></a>
				<a href="#" id="viewRevision"><spring:message code="proprietypoint.viewrevisions" text="View Revisions"></spring:message></a>
			</p>	
			
			<p style="display:none;" class="revise1" id="revisedSubjectDiv">
				<label class="centerlabel"><spring:message code="proprietypoint.revisedSubject" text="Revised Subject"/></label>
				<form:textarea path="revisedSubject" rows="4" cols="70"></form:textarea>
				<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p style="display:none;" class="revise2" id="revisedPointsOfProprietyDiv">
				<label class="wysiwyglabel"><spring:message code="proprietypoint.revisedPointsOfPropriety" text="Revised Notice Content"/></label>
				<form:textarea path="revisedPointsOfPropriety" cssClass="wysiwyg"></form:textarea>
				<form:errors path="revisedPointsOfPropriety" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
			
			<p id="internalStatusDiv">
				<label class="small"><spring:message code="proprietypoint.currentStatus" text="Current Status"/></label>
				<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
			</p>
			
			<c:if test="${internalStatusType == 'proprietypoint_system_assistantprocessed' || internalStatusType == 'proprietypoint_putup_rejection'}">
				<security:authorize access="hasAnyRole('PROIS_ASSISTANT')">
				<p>	
					<label class="small"><spring:message code="proprietypoint.putupfor" text="Put up for"/></label>	
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
					<label class="small"><spring:message code="proprietypoint.nextactor" text="Next Users"/></label>
					<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
					<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
				</p>
			</c:if>		
			<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
			<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
			<c:if test="${fn:contains(internalStatusType, 'proprietypoint_final')}">
				<form:hidden path="actor"/>
			</c:if>
			
			<c:choose>
			<c:when test="${not empty domain.reply}">
			<p>
				<label class="wysiwyglabel"><spring:message code="proprietypoint.reply" text="Reply"/></label>
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
				<label class="wysiwyglabel"><spring:message code="proprietypoint.rejectionReason" text="Reply"/></label>
				<form:textarea path="rejectionReason" cssClass="wysiwyg" readonly="true"></form:textarea>
				<form:errors path="rejectionReason" cssClass="validationError"></form:errors>
			</p>
			</c:when>
			<c:otherwise>
				<form:hidden path="rejectionReason"/>
			</c:otherwise>
			</c:choose>			
			
			<p>
				<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="proprietypoint.viewcitation" text="View Citations"></spring:message></a>	
			</p>
			
			<p>
				<label class="centerlabel"><spring:message code="proprietypoint.remarks" text="Remarks"/></label>
				<form:textarea path="remarks" rows="4" cols="70"></form:textarea>
				<%-- <form:hidden path="remarksAboutDecision"/> --%>
			</p>	
			</div>
				
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<c:choose>
						<c:when test="${internalStatusType eq'proprietypoint_final_admission' || internalStatusType eq 'proprietypoint_final_rejection'}">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</c:when>
						<c:when test="${bulkedit!='yes'}">
							<c:if test="${internalStatusType=='proprietypoint_submit' || internalStatusType=='proprietypoint_system_assistantprocessed' 
								|| internalStatusType=='proprietypoint_system_putup' || internalStatusType=='proprietypoint_putup_rejection'}">
								<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
								<security:authorize access="hasAnyRole('PROIS_ASSISTANT')">
									<input id="startworkflow" type="button" value="<spring:message code='proprietypoint.putupproprietypoint' text='Put Up Propriety Point'/>" class="butDef">
								</security:authorize>					
							</c:if>							
						</c:when>						
						<c:otherwise>
							<c:if test="${bulkedit=='yes'}">
								<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
							</c:if>
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
			<%-- <input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${domain.dataEnteredBy}"> --%>
			<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
			<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
			<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
			<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
			<input id="role" name="role" value="${role}" type="hidden">
			<input id="taskid" name="taskid" value="${taskid}" type="hidden">
			<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
			<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
			<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
			<%-- <input id="deviceType" name= "deviceType" type="hidden" value="${deviceType}" /> --%>
			<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
			<input id="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
		</form:form>

		<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='proprietypoint.startworkflowmessage' text='Do You Want To Put Up Propriety Point?'></spring:message>" type="hidden">
		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">		
		<input id="originalLevel" value="${ domain.level}" type="hidden">		
		<input id="deviceTypeType" value="${selectedDeviceType}" type="hidden"/>
		<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>		
	</div>
	</div>	
</body>
</html>