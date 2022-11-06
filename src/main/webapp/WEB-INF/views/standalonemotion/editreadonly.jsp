<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
		if($("#edit_departmentSelected").val()==null||$("#edit_departmentSelected").val()==''){
			$("#departmentEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#departmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#edit_subDepartmentSelected").val()==null||$("#edit_subDepartmentSelected").val()==''){
			$("#subDepartmentEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#subDepartmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}		
		if($("#edit_answeringDateSelected").val()==null||$("#edit_answeringDateSelected").val()==''){
			$("#answeringDateEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
			$("#answeringDateEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#houseT").val()=='lowerhouse'){
			if($("#revisedSubject").val()!=''){
				$("#revisedSujectDiv").show();
			}
			if($("#revisedQuestionText").val()!=''){
				$("#reviseQuestionTextDiv").show();
			}
		}else if($("#houseT").val()=='upperhouse'){
			if($("#revisedSubject").val()!=''){
				$("#revisedSujectDiv").show();
			}
			if($("#revisedReason").val()!=''){
				$("#reviseReasonDiv").show();
			}
			if($("#revisedBriefExplanationa").val()!=''){
				$("#reviseBriefExplanationDiv").show();
			}
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
<form:form action="question" method="PUT" modelAttribute="domain">

	<%@ include file="/common/info.jsp" %>
	<h2>${formattedQuestionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
		
	<p>
		<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
		<input id="formattedNumberEdit" name="formattedNumberEdit" value="${formattedNumber}" class="sText" readonly="readonly">
	</p>
		
	<p>		
		<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
		<input id="formattedSubmissionDateEdit" name="formattedSubmissionDateEdit" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
		
		<c:if test="${discussionDateSelected != null and not(empty discussionDateSelected)}">
			<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
			<input id="discussionDateEdit" name="discussionDateEdit" value="${discussionDateSelected }" class="sText" readonly="readonly">
		</c:if>		
	</p>
	
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministryEdit" id="ministryEdit" class="sSelect" disabled="disabled">
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
		<c:if test="${not(domain.type.type=='motions_standalonemotion_halfhourdiscussion' and houseTypeType=='upperhouse') }">
			<label class="small"><spring:message code="question.group" text="Group"/>*</label>
			<input type="text" class="sText" id="formattedGroupEdit" name="formattedGroupEdit"  readonly="readonly" value="${formattedGroup}">
		</c:if>
	</p>	

	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<select name="subDepartmentEdit" id="subDepartmentEdit" class="sSelect" disabled="disabled">
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
		
	
	<p>
		<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
		<textarea id="membersEdit" class="sTextareaEdit" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
		<c:if test="${!(empty primaryMember)}">
			<input id="primaryMemberEdit" name="primaryMemberEdit" value="${primaryMember}" type="hidden">
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
			<select  name="selectedSupportingMembersEdit" id="selectedSupportingMembersEdit" multiple="multiple" style="display:none;">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" selected="selected"></option>
			</c:forEach>		
			</select>
		</c:if>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
	</p>			
	
	<c:if test="${domain.type.type=='motions_standalonemotion_halfhourdiscussion' and houseTypeType=='upperhouse'}">
		<p>
			<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" ><c:out value="${formattedParentNumber}"></c:out></a>
		</p>	
	
		<p>
			<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty clubbedQuestions) }">
					<c:forEach items="${clubbedQuestions }" var="i">
						<a href="#" id="cq${i.id}" class="clubbedQuestions" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>	
		</p>
	</c:if>
	
	<p>
		<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty referencedQuestions) }">
				<c:forEach items="${referencedQuestions }" var="i">
					<a href="#" id="rq${i.number}" class="referencedQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>	
	</p>
	
	<c:choose>
		<c:when test="${houseTypeType=='lowerhouse'}">
			<p>	
				<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
				<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
				<form:textarea path="briefExplanation" readonly="true" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:when>
		<c:when test="${houseTypeType=='upperhouse'}">		
			<p>	
				<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
				<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
			</p>
				
			<p>
				<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
				<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
			</p>
			
			<p>
				<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
				<form:textarea path="briefExplanation" readonly="true" cssClass="wysiwyg"></form:textarea>
			</p>
		</c:when>
	</c:choose>
	
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
		<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
		<form:textarea path="revisedSubject" rows="2" cols="50" readonly="true"></form:textarea>
		<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedQuestionTextDiv">
		<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
		<form:textarea path="revisedQuestionText" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise3" id="revisedReasonDiv">
		<label class="wysiwyglabel"><spring:message code="question.revisedReason" text="Revised Reason"/></label>
		<form:textarea path="revisedReason" rows="2" cols="50" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="revisedReason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise4" id="revisedBriefExplanationDiv">
		<label class="wysiwyglabel"><spring:message code="question.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
		<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv">
		<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
		<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:if test="${domain.houseType.type=='upperhouse'}">
		<c:if test="${not empty domain.rejectionReason}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
				<textarea id="rejectionReason" class="wysiwyg" readonly="readonly">${domain.rejectionReason}</textarea>
			</p>
		</c:if>
	</c:if>
		
	<p>
		<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
		<textarea id="remarksEdit" class="wysiwyg">${domain.remarks}</textarea>
	</p>
<input type="hidden" id="houseT" value="${houseTypeType}" />
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="edit_ministrySelected" value="${ministrySelected }" type="hidden">
<input id="edit_departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="edit_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="edit_answeringDateSelected" value="${ answeringDate}" type="hidden">
</form:form>
</div>
</body>
</html>