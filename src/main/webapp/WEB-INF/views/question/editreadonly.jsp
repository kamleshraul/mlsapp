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
			} else{
				$("#departmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}
			
			if($("#edit_subDepartmentSelected").val()==null||$("#edit_subDepartmentSelected").val()==''){
				$("#subDepartmentEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			} else{
				$("#subDepartmentEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}
			
			if($("#edit_answeringDateSelected").val()==null||$("#edit_answeringDateSelected").val()==''){
				$("#answeringDateEdit").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			} else{
				$("#answeringDateEdit").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}
			
			if($("#revisedSubjectEdit").val()!=''){
				$("#revisedSubjectEditDiv").show();
			}
			
			if($("#revisedQuestionTextEdit").val()!=''){
				$("#revisedQuestionTextEditDiv").show();
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
<%-- <form:form action="question" method="PUT" modelAttribute="domain"> --%>

	<%-- <%@ include file="/common/info.jsp" %> --%>
	<h2>
		${formattedQuestionType}: ${formattedNumber}
		<c:if test="${not empty yaadiDetailsText}">
			&nbsp;&nbsp;(${yaadiDetailsText})
		</c:if>
	</h2>
		
	<p>
		<c:choose>
			<c:when test="${fn:contains(domain.type.type,'questions_halfhourdiscussion')}">
				<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
			</c:when>
			<c:otherwise>
				<label class="small"><spring:message code="question.number" text="Motion Number"/>*</label>
			</c:otherwise>
		</c:choose>
	<input id="formattedNumberEdit" name="formattedNumberEdit" value="${formattedNumber}" class="sText" readonly="readonly">		
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		
		<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
		<input class="sText" readonly="readonly" type="text" name="halfHourDiscussionReference_questionNumberEdit" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumberEdit" />
	</c:if>
	
	<c:if test="${domain.type.type!='questions_halfhourdiscussion_from_question' and domain.type.type!='questions_shortnotice' and domain.type.type!='questions_halfhourdiscussion_standalone'}">
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<input name="formattedPriorityEdit" id="formattedPriorityEdit" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
	</c:if>
	</p>
		
	<p>		
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDateEdit" name="formattedSubmissionDateEdit" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	
	<c:if test="${domain.type.type!='questions_halfhourdiscussion_from_question' and domain.type.type!='questions_halfhourdiscussion_standalone'}">
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<input id="formattedAnsweringDateEdit" name="formattedAnsweringDateEdit" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
	</c:if>
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question' or domain.type.type=='questions_halfhourdiscussion_standalone'}">
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
	<c:if test="${domain.type.type!='questions_halfhourdiscussion_standalone'}">
	<label class="small"><spring:message code="question.group" text="Group"/>*</label>
	<input type="text" class="sText" id="formattedGroupEdit" name="formattedGroupEdit"  readonly="readonly" value="${formattedGroup}">
	</c:if>
	</p>	
	
	<p>
	<label class="small"><spring:message code="question.department" text="Department"/></label>
	<c:if test="${domain.type.type=='xyz'}">
	<select name="departmentEdit" id="departmentEdit" class="sSelect" disabled="disabled">
	<option value=""><spring:message code='please.select' text='Please Select'/></option>
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
	
	<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
	</c:if>
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
	
	<p>	
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<textarea id="subjectEdit" readonly="readonly" rows="2" cols="50">${domain.subject}</textarea>
	</p>
	
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
		<textarea id="questionTextEdit" class="wysiwyg" readonly="readonly">${domain.questionText}</textarea>
	</p>
	
	<c:if test="${not empty domain.answer}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
			<textarea id="answerEdit" class="wysiwyg" readonly="readonly">${domain.answer}</textarea>
		</p>
	</c:if>
	
	<c:if test="${domain.type.type=='questions_starred' or domain.type.type=='questions_unstarred'}">
		<c:if test="${domain.questionreferenceText!=null}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.questionreferenceText" text="Reference Text"/>*</label>
				<textarea id="questionreferenceTextEdit" class="wysiwyg" readonly="readonly">${domain.questionreferenceText}</textarea>
			</p>
		</c:if>
	</c:if>
	
	<c:if test="${domain.type.type=='questions_shortnotice' or domain.type.type=='questions_halfhourdiscussion_from_question'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
		<textarea id="reasonEdit" class="wysiwyg" readonly="readonly">${domain.reason}</textarea>
	</p>
	</c:if>
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<textarea id="briefExplanationEdit" class="wysiwyg" readonly="readonly">${domain.briefExplanation}</textarea>
		</p>
	</c:if>
	
	<p style="display:none;" class="revise" id="revisedSubjectEditDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<textarea id="revisedSubjectEdit" rows="2" cols="50" class="sTextarea" readonly="readonly">${domain.revisedSubject}</textarea>
	</p>
	
	<p style="display:none;" class="revise" id="revisedQuestionTextEditDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<textarea id="revisedQuestionTextEdit" class="wysiwyg" readonly="readonly">${domain.revisedQuestionText}</textarea>
	</p>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:if test="${not empty domain.rejectionReason}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
		<textarea id="rejectionReasonEdit" class="wysiwyg" readonly="readonly">${domain.rejectionReason}</textarea>
	</p>
	</c:if>
	
	<c:if test="${not empty domain.factualPosition}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPosition" text="Factual Postion"/></label>
		<textarea id="factualPositionEdit" class="wysiwyg" readonly="readonly">${domain.factualPosition}</textarea>
	</p>
	</c:if>
	
	<c:if test="${not empty domain.factualPositionFromMember}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.factualPositionFromMember" text="Factual Postion From Member"/></label>
		<textarea id="factualPositionFromMemberEdit" class="wysiwyg" readonly="readonly">${domain.factualPositionFromMember}</textarea>
	</p>
	</c:if>
		
	<p>
	<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
	<textarea id="remarksEdit" class="wysiwyg" readonly="readonly">${domain.remarks}</textarea>
	</p>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="edit_ministrySelected" value="${ministrySelected }" type="hidden">
<input id="edit_departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="edit_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="edit_answeringDateSelected" value="${ answeringDate}" type="hidden">
<%-- </form:form> --%>
</div>
</body>
</html>