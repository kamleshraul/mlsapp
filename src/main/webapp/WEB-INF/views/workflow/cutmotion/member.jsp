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
		initControls();
		$('#remarks-wysiwyg-iframe').css('max-height','50px');
	    var demandNumberWithoutSpace = $('#demandNumber').val().replace(/ /g,''); //added in order to remove spaces in between.. to be removed if populated through master entries
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace($('#specialDashCharacter').val(), "-");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace(",", "");
		demandNumberWithoutSpace = demandNumberWithoutSpace.replace("'", "");
		$('#demandNumber').val(demandNumberWithoutSpace);
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
	  	//************Hiding Unselected Options In Ministry,Department,SubDepartment ***************//
		$("#ministry option[selected!='selected']").hide();
		$("#subDepartment option[selected!='selected']").hide();
		
		/********Submit Click*********/
		$('#submit').click(function(){					
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
		
	    /**** On page Load ****/
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
	<h2>${formattedMotionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="cutmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
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
		<p style="display: inline;">
			<label class="small"><spring:message code="cutmotion.ministry" text="Ministry"/>*</label>
			<select name="ministry" id="ministry" class="sSelect">
				<option value=""><spring:message code='please.select' text='Please Select'/></option>
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
			<form:errors path="subDepartment" cssClass="validationError"/>	
		</p>	
	</p>
	
	<p>
		<label class="centerlabel"><spring:message code="generic.members" text="Members"/></label>
		<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50" style="width: 536px; height: 55px;">${memberNames}</textarea>
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
		<label class="small"><spring:message code="generic.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<p style="display: none;">
		<c:if test="${bulkedit!='yes'}">	
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="generic.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="generic.refresh" text="Refresh"></spring:message></a>
		</c:if>	
	</p>
		
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

	<p>	
		<label class="centerlabel"><spring:message code="cutmotion.subTitle" text="Sub Title"/></label>
		<form:textarea path="subTitle" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="subTitle" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="cutmotion.noticeContent" text="Details"/></label>
		<form:textarea path="noticeContent" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="noticeContent" cssClass="validationError"/>	
	</p>	
	
	<p style="display: none;">
		<a href="#" id="reviseMainTitle" style="margin-left: 162px;margin-right: 20px;"><spring:message code="cutmotion.reviseMainTitle" text="Revise Main Title"></spring:message></a>
		<c:if test="${selectedMotionType=='motions_cutmotion_budgetary'}">
			<a href="#" id=reviseSecondaryTitle style="margin-right: 20px;display: none;"><spring:message code="cutmotion.reviseSecondaryTitle" text="Revise Secondary Title"></spring:message></a>
		</c:if>
		<a href="#" id="reviseSubTitle" style="margin-right: 20px;"><spring:message code="cutmotion.reviseSubTitle" text="Revise Sub Title"></spring:message></a>
		<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="cutmotion.reviseNoticeContent" text="Revise Content"></spring:message></a>
		<a href="#" id="viewRevision"><spring:message code="cutmotion.viewrevisions" text="View Revisions"></spring:message></a>
	</p>	
	
	<p style="display:none;" class="revise1" id="revisedMainTitleDiv">
		<label class="centerlabel"><spring:message code="cutmotion.revisedSubject" text="Revised Main Title"/></label>
		<form:textarea path="revisedMainTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedMainTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedSecondaryTitleDiv">
		<label class="centerlabel"><spring:message code="cutmotion.revisedSecondaryTitle" text="Revised Secondary Title"/></label>
		<form:textarea path="revisedSecondaryTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedSecondaryTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise3" id="revisedSubTitleDiv">
		<label class="centerlabel"><spring:message code="cutmotion.revisedSubTitle" text="Revised Sub Title"/></label>
		<form:textarea path="revisedSubTitle" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedSubTitle" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise4" id="revisedNoticeContentDiv">
		<label class="wysiwyglabel"><spring:message code="cutmotion.revisedContent" text="Revised Content"/></label>
		<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
		<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv" style="margin-top: 20px;">
		<label class="small"><spring:message code="generic.status" text="Current Status"/></label>
		<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:choose>
		<c:when test="${internalStatusType == 'cutmotion_final_admission'}">
			<p style="display: none;">
			<label class="small"><spring:message code="cutmotion.lastDateOfReplyReceiving" text="Last date of receiving reply"/></label>
			<%-- <form:input path="lastDateOfReplyReceiving" cssClass="datemask sText" value='${formattedLastReplyReceivingDate}'/> --%>
			<input id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
			<form:errors path="lastDateOfReplyReceiving" cssClass="validationError"/>
			</p>
		</c:when>
		<c:otherwise>
			<input type="hidden" id="lastDateOfReplyReceiving" name="setLastDateOfReplyReceiving" class="datemask sText" value="${formattedLastReplyReceivingDate}"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${internalStatusType == 'cutmotion_final_admission'}">
			<p style="display: none;">
				<label class="small"><spring:message code="cutmotion.replyRequestedDate" text="Reply Requested Date"/></label>
				<input id="replyRequestedDate" name="setReplyRequestedDate" class="datetimemask sText" value="${formattedReplyRequestedDate}"/>
			</p>
			<c:if test="${not empty formattedReplyReceivedDate}">
			<p style="display: none;">
				<label class="small"><spring:message code="cutmotion.replyReceivedDate" text="Reply Received Date"/></label>
				<input id="replyReceivedDate" name="setReplyReceivedDate" class="datetimemask sText" value="${formattedReplyReceivedDate}" readonly="readonly"/>
			</p>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty formattedReplyRequestedDate}">
				<input type="hidden" id="replyRequestedDate" name="setReplyRequestedDate" class="datetimemask sText" value="${formattedReplyRequestedDate}"/>
			</c:if>
			<c:if test="${not empty formattedReplyReceivedDate}">
				<input type="hidden" id="replyReceivedDate" name="setReplyReceivedDate" class="datetimemask sText" value="${formattedReplyReceivedDate}"/>
			</c:if>
		</c:otherwise>
	</c:choose>
	
	<table class="uiTable" style="margin-left:165px;width:600px;display: none;">
		<thead>
		<tr>
		<th style="text-align: center">
		<spring:message code="cmois.latestrevisions.user" text="Usergroup"></spring:message>
		</th>
		<th style="text-align: center">
		<spring:message code="cmois.latestrevisions.decision" text="Decision"></spring:message>
		</th>
		<th style="text-align: center">
		<spring:message code="cmois.latestrevisions.remarks" text="Remarks"></spring:message>
		</th>
		</tr>
		</thead>
		<tbody>	
			<c:forEach items="${latestRevisions}" var="i">
				<tr>
					<td style="text-align: left">
					${i[1]}<br>(${i[7]})
					</td>
					<td style="text-align: center">
					${i[3]}
					</td>
					<td style="text-align: center">
					${i[6]}
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
										<c:when test="${i.id==internalStatusSelected }">
											<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
										</c:when>
										<c:otherwise>
											<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
										</c:otherwise>
									</c:choose>
							</c:forEach>
						</select>
						<form:errors path="internalStatus" cssClass="validationError"/>
					</td>
					<td>
						<a href="#" id="viewCitation" style="margin-left: 210px;margin-top: 30px;"><spring:message code="cutmotion.viewcitation" text="View Citations"></spring:message></a>
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
			<label class="small"><spring:message code="cutmotion.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors}"/>	
		</p>		
	</c:if>	
	
	<c:choose>
		<c:when test="${internalStatusType == 'cutmotion_final_rejection'}">
		<p style="display: none;">
			<label class="wysiwyglabel"><spring:message code="cutmotion.rejectionReason" text="Rejection reason"/></label>
			<form:textarea path="rejectionReason" cssClass="wysiwyg" readonly="${workflowstatus=='COMPLETED'}"></form:textarea>
			<form:errors path="rejectionReason" cssClass="validationError"></form:errors>
		</p>
		</c:when>
		<c:otherwise>
			<form:hidden path="rejectionReason"/>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${!(empty domain.reply)}">
	<p>
		<label class="wysiwyglabel"><spring:message code="cutmotion.reply" text="Reply"/></label>
		<form:textarea path="reply" cssClass="wysiwyg" readonly="true"></form:textarea>
	</p>
	</c:if>
	
	<c:if test="${not empty domain.reasonForLateReply}">
	<p>
		<label class="wysiwyglabel"><spring:message code="cutmotion.reasonForLateReply" text="Reason for Late Reply"/></label>
		<form:textarea path="reasonForLateReply" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="reasonForLateReply" cssClass="validationError"></form:errors>
	</p>
	</c:if>
	
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
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="transferToDepartmentAccepted"/>
	<form:hidden path="mlsBranchNotifiedOfTransfer"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/>
	<form:hidden path="internalNumber"/>
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
	<input id="workflowdetails" type="hidden" name="workflowdetails" value="${workflowdetails}"/>
</form:form>
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmMotionSubmission" value="<spring:message code='confirm.cutmotionsubmission.message' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='motion.startworkflowmessage' text='Do You Want To Put Up motion'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="internalStatusType" name="internalStatusType" type="hidden" value="${internalStatusType}">
<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="motionType" type="hidden" value="${selectedMotionType}" />
<input id="workflowstatus" type="hidden" value="${workflowstatus}"/>
<input type="hidden" id="originalLevel" value="${level}" />

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