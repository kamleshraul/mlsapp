<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			console.log($("#selectedDepartment").val()+":"+$("#selectedSubDepartment").val());
		if(typeof $("#edit_selectedDepartment").val()=='undefined'||$("#edit_selectedDepartment").val()==null||$("#edit_selectedDepartment").val()==''){
		$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
		$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#edit_selectedSubDepartment").val()==null||$("#edit_subDepartment").val()==''){
			$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}		
		if($("#revisedSubject").val()!=''){
			$("#revisedSubjectDiv").show();
		}
		if($("#revisedQuestionText").val()!=''){
			$("#revisedQuestionTextDiv").show();
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
<div class="fields clearfix watermark">
<form:form action="question" method="PUT" modelAttribute="domain">

	<%@ include file="/common/info.jsp" %>
	<h2>${formattedQuestionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
		
	<p>
	<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
	<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
	<input id="number" name="number" value="${domain.number}" type="hidden">
	<form:errors path="number" cssClass="validationError"/>
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		
		<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
		<input class="sText" readonly="readonly" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
		<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		
	</c:if>
	
	<c:if test="${domain.type.type!='questions_halfhourdiscussion_from_question'}">
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
		<input name="priority" id="priority"  type="hidden" value="${priority }">	
		<form:errors path="priority" cssClass="validationError"/>
	</c:if>
	</p>
		
	<p>		
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	
	<c:if test="${domain.type.type!='questions_halfhourdiscussion_from_question'}">
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<input id="formattedAnsweringDate" name="formattedAnsweringDate" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
		<input id="answeringDate" name="answeringDate" type="hidden"  value="${answeringDate}">
	</c:if>
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
		<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" class="sText" readonly="readonly">
		<form:errors path="discussionDate" cssClass="validationError"/>
	</c:if>
	</p>
	
	<p>
	<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
	<select name="ministry" id="ministry" class="sSelect" disabled="disabled">
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
	
	<label class="small"><spring:message code="question.group" text="Group"/>*</label>
	<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
	<input type="hidden" id="group" name="group" value="${group }">
	<form:errors path="group" cssClass="validationError"/>		
	</p>	
	
	<p>
	<label class="small"><spring:message code="question.department" text="Department"/></label>
	<select name="department" id="department" class="sSelect" disabled="disabled">
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
	<form:errors path="department" cssClass="validationError"/>	
	
	<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
	<select name="subDepartment" id="subDepartment" class="sSelect" disabled="disabled">
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
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
	</p>			
	
	<p>
	<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
	<a href="#" id="p${parent}" ><c:out value="${formattedParentNumber}"></c:out></a>
	<input type="hidden" id="parent" name="parent" value="${parent}">
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
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	
	<c:if test="${domain.type.type=='questions_shortnotice' or domain.type.type=='questions_halfhourdiscussion_from_question'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
		<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question' or domain.type.type=='questions_halfhourdiscussion_standalone'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<p style="display:none;" class="revise" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise" id="revisedQuestionTextDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
	<select id="changeInternalStatus" disabled="disabled">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatuses}" var="i">
	<c:if test="${(i.type!='question_workflow_decisionstatus_discuss'&&i.type!='question_workflow_decisionstatus_sendback') }">
	<c:choose>
	<c:when test="${i.type=='question_workflow_decisionstatus_groupchanged' }">
	<option value="${i.id}" style="display: none;"><c:out value="${i.name}"></c:out></option>	
	</c:when>
	<c:otherwise>
	<c:choose>
	<c:when test="${i.id==internalStatusSelected }">
	<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
	</c:when>
	<c:otherwise>
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
	</c:otherwise>
	</c:choose>
	</c:otherwise>
	</c:choose>
	</c:if>
	</c:forEach>
	</select>	
	</p>
	<p id="clarificationDiv" style="display:none;">
	<label class="small"><spring:message code="question.clarificationneededfrom" text="Clarification Needed from"/></label>
	<select id="clarificationNeededFrom" name="clarificationNeededFrom" class="sSelect">	
	</select>
	</p>	
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>

<input id="edit_ministrySelected" value="${ministrySelected }" type="hidden">
<input id="edit_departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="edit_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="edit_answeringDateSelected" value="${ answeringDate}" type="hidden">
</form:form>
</div>
</body>
</html>