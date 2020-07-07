<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="adjournmentmotion" text="Adjournment Motion"/>
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
		$(document).ready(function(){
			if($("#subDepartmentSelected").val()==''){
				$("#readonly_subDepartment").prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");			
			}else{
				$("#readonly_subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");			
			}
			/**** Revisions ****/
		    $("#readonly_viewRevision").click(function(){
		    	$.get('adjournmentmotion/revisions/'+$("#readonly_id").val(), function(data){
		    		$.fancybox.open(data);			    	
			    });
			    return false;
		    });
		    /**** Contact Details ****/
		    $("#readonly_viewContacts").click(function(){
			    var primaryMember=$("#readonly_primaryMember").val();
			    var supportingMembers=$("#readonly_selectedSupportingMembers").val();
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
		    /**** Show Revised Subject & Notice Content if mentioned ****/
			if($("#readonly_revisedSubject").val()!=''){
			    $("#readonly_revisedSubjectDiv").show();
		    }		
		    if($("#readonly_revisedNoticeContent").val()!=''
		    		&& $("#readonly_revisedNoticeContent").val()!='<p></p>'){
		    	$("#readonly_revisedNoticeContentDiv").show();
		    }
		    /**** Remarks ****/
			if($('#readonly_remarks').val()!=undefined
					&& $('#readonly_remarks').val()!="" 
					&& $('#readonly_remarks').val()!="<p></p>") {
				$('#readonly_remarks_div').show();
			} else {
				$('#readonly_remarks_div').hide();
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
		<p id="readonly_error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields clearfix watermark">
			<div id="readonly_assistantDiv">
				<form:form id="readonly_currentForm" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>
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
					
					<p style="display:none;">
						<label class="small"><spring:message code="adjournmentmotion.houseType" text="House Type"/>*</label>
						<input id="readonly_formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="readonly_houseType" value="${houseType}" type="hidden">							
					</p>	
					
					<p style="display:none;">
						<label class="small"><spring:message code="adjournmentmotion.year" text="Year"/>*</label>
						<input id="readonly_formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="readonly_sessionYear" value="${sessionYear}" type="hidden">
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="adjournmentmotion.sessionType" text="Session Type"/>*</label>		
						<input id="readonly_formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="readonly_sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="readonly_session" name="session" value="${session}"/>							
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="adjournmentmotion.type" text="Type"/>*</label>
						<input id="readonly_formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
						<input id="readonly_type" value="${motionType}" type="hidden">								
					</p>
					
					<p>
						<label class="small"><spring:message code="adjournmentmotion.number" text="Motion Number"/>*</label>
						<input id="readonly_formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="readonly_number" value="${domain.number}" type="hidden">						
						
						<label class="small"><spring:message code="adjournmentmotion.selectadjourningdate" text="Adjourning Date"/></label>
						<input id="readonly_formattedAdjourningDate" value="${formattedAdjourningDate}" class="sText" readonly="readonly">
						<input id="readonly_adjourningDate" type="hidden"  value="${selectedAdjourningDate}">
					</p>		
					
					<c:if test="${!(empty submissionDate)}">
					<p>
						<label class="small"><spring:message code="adjournmentmotion.submissionDate" text="Submitted On"/></label>
						<input id="readonly_formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
						<input id="readonly_setSubmissionDate" type="hidden"  value="${submissionDate}">	
						
						<c:choose>
						<c:when test="${internalStatusType=='adjournmentmotion_final_admission'}">
							<label class="small"><spring:message code="adjournmentmotion.admissionNumber" text="Admission Number"/></label>
							<input id="readonly_formattedAdmissionNumber" name="formattedAdmissionNumber" value="${formattedAdmissionNumber}" class="sText" readonly="readonly">		
							<input id="readonly_admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
							<form:errors path="admissionNumber" cssClass="validationError"/>	
						</c:when>
						<c:otherwise>
							<input id="admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
						</c:otherwise>
						</c:choose>
					</p>
					</c:if>
						
					<p>
						<label class="small"><spring:message code="adjournmentmotion.ministry" text="Ministry"/></label>
						<input id="readonly_formattedMinistry" type="text" class="sText" value="${formattedMinistry}" readonly="readonly">
						<input id="readonly_ministry" type="hidden" value="${ministrySelected}">
						<label class="small"><spring:message code="adjournmentmotion.subdepartment" text="Sub Department"/></label>
						<select id="readonly_subDepartment" class="sSelect" disabled="disabled" style="font-weight: bold;">
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
						<label class="centerlabel"><spring:message code="adjournmentmotion.members" text="Members"/></label>
						<textarea id="readonly_members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
						<c:if test="${!(empty primaryMember)}">
							<input id="readonly_primaryMember" value="${primaryMember}" type="hidden">
						</c:if>
						<c:if test="${!(empty supportingMembers)}">
						    <select id="readonly_selectedSupportingMembers" multiple="multiple" style="display:none;">
								<c:forEach items="${supportingMembers}" var="i">
								<option value="${i.id}" selected="selected"></option>
								</c:forEach>		
							</select>
						</c:if>
					</p>
					
					<p>
						<label class="small"><spring:message code="adjournmentmotion.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText">
						<a href="#" id="readonly_viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
					</p>			
					
					<p>
						<label class="small"><spring:message code="adjournmentmotion.parentmotion" text="Clubbed To"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty parent)}">	
								<a href="#" id="readonly_p${parent}" onclick="viewMotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="readonly_parent" value="${parent}">
					</p>
					
					<p>
						<label class="small"><spring:message code="adjournmentmotion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty clubbedMotions) }">
								<c:forEach items="${clubbedMotions }" var="i">
									<a href="#" id="readonly_cq${i.number}" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
								</c:forEach>
								<a href="javascript:void(0);" id="readonly_viewClubbedAdjournmentMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="adjournmentmotion.clubbed.texts" text="C"></spring:message></a>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<select id="readonly_clubbedEntities" multiple="multiple" style="display:none;">
							<c:forEach items="${clubbedMotions}" var="i">
								<option value="${i.id}" selected="selected"></option>
							</c:forEach>
						</select>
					</p>
					
					<p>
						<label class="small"><spring:message code="adjournmentmotion.referencedmotion" text="Referenced Motion"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty referencedMotion) }">
								<a href="#" id="readonly_cq${referencedMotion.number}" class="referencedRefMotions" onclick="viewMotionDetail(${referencedMotion.number});" style="font-size: 18px;"><c:out value="${referencedMotion.name}"></c:out></a>
								<%-- <a href="javascript:void(0);" id="readonly_viewReferencedMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="adjournmentmotion.referenced.texts" text="R"></spring:message></a> --%>
								<input type="hidden" id="readonly_referencedAdjournmentMotion" value="${referencedMotion.id}">
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>				
					</p>
					
					<p>
						<label class="centerlabel"><spring:message code="adjournmentmotion.subject" text="Subject"/>*</label>
						<form:textarea id="readonly_subject" path="subject" rows="2" cols="50" readonly="true"></form:textarea>							
					</p>
						
					<p>
						<label class="wysiwyglabel"><spring:message code="adjournmentmotion.noticeContent" text="Notice Content"/>*</label>
						<form:textarea id="readonly_noticeContent" path="noticeContent" cssClass="wysiwyg" readonly="true"></form:textarea>							
					</p>	
					
					<p>
						<a href="#" id="readonly_viewRevision" style="margin-left: 162px;margin-right: 20px;"><spring:message code="adjournmentmotion.viewrevisions" text="View Revisions"></spring:message></a>
					</p>	
					
					<p style="display:none;" id="readonly_revisedSubjectDiv">
						<label class="centerlabel"><spring:message code="adjournmentmotion.revisedSubject" text="Revised Subject"/></label>
						<form:textarea id="readonly_revisedSubject" path="revisedSubject" rows="2" cols="50" readonly="true"></form:textarea>						
					</p>
					
					<p style="display:none;" id="readonly_revisedNoticeContentDiv">
						<label class="wysiwyglabel"><spring:message code="adjournmentmotion.revisedNoticeContent" text="Revised Notice Content"/></label>
						<form:textarea id="readonly_revisedNoticeContent" path="revisedNoticeContent" cssClass="wysiwyg" readonly="true"></form:textarea>						
					</p>
					
					<p id="readonly_internalStatusDiv">
						<label class="small"><spring:message code="adjournmentmotion.currentStatus" text="Current Status"/></label>
						<input id="readonly_formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>
											
					<input type="hidden" id="readonly_internalStatus"  name="internalStatus" value="${internalStatus}">
					<input type="hidden" id="readonly_recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					
					<c:if test="${fn:contains(internalStatusType, 'adjournmentmotion_final')}">
						<form:hidden path="actor"/>
					</c:if>					
					
					<p>
					<label class="wysiwyglabel"><spring:message code="adjournmentmotion.remarks" text="Remarks"/></label>
					<form:textarea id="readonly_remarks" path="remarks" cssClass="wysiwyg" readonly="true"></form:textarea>
					</p>
					
					<form:hidden id="readonly_id" path="id"/>
					<form:hidden id="readonly_locale" path="locale"/>
					<form:hidden id="readonly_version" path="version"/>
					<form:hidden id="readonly_workflowStarted" path="workflowStarted"/>	
					<form:hidden id="readonly_endFlag" path="endFlag"/>
					<form:hidden id="readonly_level" path="level"/>
					<form:hidden id="readonly_localizedActorName" path="localizedActorName"/>
					<form:hidden id="readonly_workflowDetailsId" path="workflowDetailsId"/>
					<input id="readonly_bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" id="readonly_status" value="${status }">
					<input type="hidden" id="readonly_createdBy" value="${createdBy }">
					<input type="hidden" id="readonly_dataEnteredBy" value="${domain.dataEnteredBy}">
					<input type="hidden" id="readonly_setCreationDate" value="${creationDate }">
					<input id="readonly_setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input type="hidden" id="readonly_workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" id="readonly_taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input id="readonly_role" value="${role}" type="hidden">
					<input id="readonly_taskid" value="${taskid}" type="hidden">
					<input id="readonly_usergroup" value="${usergroup}" type="hidden">
					<input id="readonly_usergroupType" value="${usergroupType}" type="hidden">	
					<input type="hidden" id="readonly_houseTypeType" value="${houseTypeType}" />
					<input id="readonly_motionType" type="hidden" value="${motionType}" />
					<input id="readonly_oldInternalStatus" value="${internalStatus}" type="hidden">
					<input id="readonly_oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
				</form:form>
				
				<input id="readonly_startWorkflowMessage" value="<spring:message code='adjournmentmotion.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
				<input id="readonly_ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="readonly_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
				<input id="readonly_answeringDateSelected" value="${ answeringDateSelected}" type="hidden">		
				<input id="readonly_originalLevel" value="${ domain.level}" type="hidden">		
				<input id="readonly_motionTypeType" value="${selectedMotionType}" type="hidden"/>
				<input id="readonly_ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
				<input id="readonly_pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
				<input type="hidden" id="readonly_ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
			</div>		
		</div>
	</body>
</html>