<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="proprietypoint" text="Propriety Point"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
		$(document).ready(function(){
			if($("#subDepartmentSelected").val()==''){
				$("#readonly_subDepartment").prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");			
			}else{
				$("#readonly_subDepartment").prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");			
			}
			/**** Revisions ****/
		    $("#readonly_viewRevision").click(function(){
		    	$.get('proprietypoint/revisions/'+$("#readonly_id").val(), function(data){
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
			    $.get('proprietypoint/members/contacts?members='+members,function(data){
				    $.fancybox.open(data);
			    });
			    return false;
		    });
		    /**** Show Revised Subject & Notice Content if mentioned ****/
			if($("#readonly_revisedSubject").val()!=''){
			    $("#readonly_revisedSubjectDiv").show();
		    }		
		    if($("#readonly_revisedPointsOfPropriety").val()!=''
		    		&& $("#readonly_revisedPointsOfPropriety").val()!='<p></p>'){
		    	$("#readonly_revisedPointsOfProprietyDiv").show();
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
						${formattedDeviceType}
						<c:if test="${not empty formattedNumber}">
							(<spring:message code="generic.number" text="Number"/> ${formattedNumber})
						</c:if>
					</h2>					
					
					<p style="display:none;">
						<label class="small"><spring:message code="proprietypoint.houseType" text="House Type"/>*</label>
						<input id="readonly_formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="readonly_houseType" value="${houseType}" type="hidden">							
					</p>	
					
					<p style="display:none;">
						<label class="small"><spring:message code="proprietypoint.year" text="Year"/>*</label>
						<input id="readonly_formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="readonly_sessionYear" value="${sessionYear}" type="hidden">
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="proprietypoint.sessionType" text="Session Type"/>*</label>		
						<input id="readonly_formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="readonly_sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="readonly_session" name="session" value="${session}"/>							
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="proprietypoint.deviceType" text="Device Type"/>*</label>
						<input id="readonly_formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
						<input id="readonly_type" value="${deviceType}" type="hidden">								
					</p>
					
					<p>
						<label class="small"><spring:message code="proprietypoint.number" text="Device Number"/>*</label>
						<input id="readonly_formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="readonly_number" name="number" value="${domain.number}" type="hidden">
						<form:errors path="number" cssClass="validationError"/>
						
						<c:if test="${houseTypeType=='lowerhouse' and !(empty submissionDate)}">
							<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
							<input id="readonly_formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
							<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
						</c:if>
						
						<%-- <c:if test="${houseTypeType=='upperhouse'}"> --%>
							<label class="small"><spring:message code="proprietypoint.selectproprietypointdate" text="Propriety Point Date"/></label>
							<input id="readonly_formattedProprietyPointDate" name="formattedProprietyPointDate" value="${formattedProprietyPointDate}" class="sText" readonly="readonly">
							<input id="readonly_proprietyPointDate" name="proprietyPointDate" type="hidden"  value="${selectedProprietyPointDate}">
						<%-- </c:if> --%>
					</p>
					
					<c:if test="${houseTypeType=='upperhouse' and !(empty submissionDate)}">
					<p>
						<label class="small"><spring:message code="proprietypoint.submissionDate" text="Submitted On"/></label>
						<input id="readonly_formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
						<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
						
						<c:choose>
						<c:when test="${internalStatusType=='proprietypoint_final_admission'}">
							<label class="small"><spring:message code="proprietypoint.admissionNumber" text="Admission Number"/></label>
							<input id="readonly_formattedAdmissionNumber" name="formattedAdmissionNumber" value="${formattedAdmissionNumber}" class="sText" readonly="readonly">		
							<input id="readonly_admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
							<form:errors path="admissionNumber" cssClass="validationError"/>	
						</c:when>
						<c:otherwise>
							<input id="readonly_admissionNumber" name="admissionNumber" value="${domain.admissionNumber}" type="hidden">
						</c:otherwise>
						</c:choose>
					</p>
					</c:if>	
					
					<p>
						<label class="small"><spring:message code="proprietypoint.ministry" text="Ministry"/></label>
						<input id="readonly_formattedMinistry" type="text" class="sText" value="${formattedMinistry}" readonly="readonly">
						<input id="readonly_ministry" type="hidden" value="${ministrySelected}">
						<label class="small"><spring:message code="proprietypoint.subdepartment" text="Sub Department"/></label>
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
						<label class="centerlabel"><spring:message code="proprietypoint.members" text="Members"/></label>
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
					
					<h2></h2>
						
					<c:if test="${houseTypeType=='upperhouse'}">
					<p>
						<label class="small"><spring:message code="proprietypoint.selectproprietypointdate" text="Propriety Point Date"/></label>
						<input id="readonly_formattedProprietyPointDate" value="${formattedProprietyPointDate}" class="sText" readonly="readonly">
						<input id="readonly_proprietyPointDate" type="hidden" value="${selectedProprietyPointDate}">
					</p>
					</c:if>			
					
					<p>
						<label class="centerlabel"><spring:message code="proprietypoint.subject" text="Subject"/>*</label>
						<form:textarea id="readonly_subject" path="subject" rows="2" cols="50" readonly="true"></form:textarea>							
					</p>
						
					<p>
						<label class="wysiwyglabel"><spring:message code="proprietypoint.pointsOfPropriety" text="Points of Propriety"/>*</label>
						<form:textarea id="readonly_pointsOfPropriety" path="pointsOfPropriety" cssClass="wysiwyg" readonly="true"></form:textarea>							
					</p>	
					
					<p>
						<a href="#" id="readonly_viewRevision" style="margin-left: 162px;margin-right: 20px;"><spring:message code="proprietypoint.viewrevisions" text="View Revisions"></spring:message></a>
					</p>	
					
					<p style="display:none;" id="readonly_revisedSubjectDiv">
						<label class="centerlabel"><spring:message code="proprietypoint.revisedSubject" text="Revised Subject"/></label>
						<form:textarea id="readonly_revisedSubject" path="revisedSubject" rows="2" cols="50" readonly="true"></form:textarea>						
					</p>
					
					<p style="display:none;" id="readonly_revisedPointsOfProprietyDiv">
						<label class="wysiwyglabel"><spring:message code="proprietypoint.revisedPointsOfPropriety" text="Revised Notice Content"/></label>
						<form:textarea id="readonly_revisedPointsOfPropriety" path="revisedPointsOfPropriety" cssClass="wysiwyg" readonly="true"></form:textarea>						
					</p>
					
					<p id="readonly_internalStatusDiv">
						<label class="small"><spring:message code="proprietypoint.currentStatus" text="Current Status"/></label>
						<input id="readonly_formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>
											
					<input type="hidden" id="readonly_internalStatus"  name="internalStatus" value="${internalStatus}">
					<input type="hidden" id="readonly_recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					
					<c:if test="${fn:contains(internalStatusType, 'proprietypoint_final')}">
						<form:hidden path="actor"/>
					</c:if>					
					
					<p>
					<label class="wysiwyglabel"><spring:message code="proprietypoint.remarks" text="Remarks"/></label>
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
					<%-- <input type="hidden" id="readonly_dataEnteredBy" value="${domain.dataEnteredBy}"> --%>
					<input type="hidden" id="readonly_setCreationDate" value="${creationDate }">
					<input id="readonly_setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input type="hidden" id="readonly_workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" id="readonly_taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input id="readonly_role" value="${role}" type="hidden">
					<input id="readonly_taskid" value="${taskid}" type="hidden">
					<input id="readonly_usergroup" value="${usergroup}" type="hidden">
					<input id="readonly_usergroupType" value="${usergroupType}" type="hidden">	
					<input type="hidden" id="readonly_houseTypeType" value="${houseTypeType}" />
					<input id="readonly_deviceType" type="hidden" value="${deviceType}" />
					<input id="readonly_oldInternalStatus" value="${internalStatus}" type="hidden">
					<input id="readonly_oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
				</form:form>
				
				<input id="readonly_startWorkflowMessage" value="<spring:message code='proprietypoint.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
				<input id="readonly_ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="readonly_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
				<input id="readonly_answeringDateSelected" value="${ answeringDateSelected}" type="hidden">		
				<input id="readonly_originalLevel" value="${ domain.level}" type="hidden">		
				<input id="readonly_deviceTypeType" value="${selectedDeviceType}" type="hidden"/>
				<input id="readonly_ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
				<input id="readonly_pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
				<input type="hidden" id="readonly_ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
			</div>		
		</div>
	</body>
</html>