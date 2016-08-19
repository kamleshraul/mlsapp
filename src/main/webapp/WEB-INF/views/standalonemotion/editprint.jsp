<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css">
		.leftCol{
			width: 250px;
			font-weight: bold;
		}		
	</style>
	
	<script type="text/javascript">
	
		$(document).ready(function(){	
				
		});
	
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="standalonemotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
		<table>
			<tr>
				<td colspan="2">
					<h2>${formattedQuestionType} ${formattedNumber}</h2>
				</td>
			</tr>	
			<tr>
				<td colspan="2"><hr></td>
			</tr>
			<c:if test="${!(empty submissionDate)}">
				<tr>
					<td class="leftCol">
						<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>	
					</td>
					<td>
						${formattedSubmissionDate }
					</td>
				</tr>			
			</c:if>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<tr>
					<td class="leftCol">
						<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
					</td>
					<td>
						${formattedPrimaryMember}
					</td>							
				</tr>
				
				<tr>
					<td class="leftCol">
						<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
					</td>
					<td>
						${constituency}
					</td>
				</tr>
			</security:authorize>
			<security:authorize access="not hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
				<tr>
					<td class="leftCol">
						<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
					</td>
					<td>
						${formattedPrimaryMember}
					</td>
				</tr>
			</security:authorize>
			<tr>
				<td class="leftCol">
					<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
				</td>
				<td>
					<c:choose>
						<c:when test="${not (empty supportingMembersName)}" > ${supportingMembersName} </c:when>
						<c:otherwise>-</c:otherwise>
					</c:choose>
				</td>									
			</tr>
			
			<c:choose>
				<c:when test="${houseTypeType=='lowerhouse'}">
					<tr>
						<td class="leftCol">
							<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
						</td>
						<td>
							${domain.subject}
						</td>						
					</tr>
						
					<tr>
						<td class="leftCol">
							<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
						</td>
						<td>
							${domain.questionText}
						</td>	
					</tr>
				</c:when>
				<c:when test="${houseTypeType=='upperhouse'}">
					<tr>
						<td class="leftCol">
							<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
						</td>
						<td>
							${domain.subject}
						</td>						
					</tr>
					
					<tr>
						<td class="leftCol">
							<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
						</td>
						<td>
							${domain.reason}
						</td>
					</tr>	
					
					<tr>
						<td class="leftCol">
							<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
						</td>
						<td>
							${domain.briefExplanation}
						</td>	
					</tr>
				</c:when>				
			</c:choose>
			
			<c:if test="${internalStatusType != null }">
				<c:if test="${!empty internalStatusType}">
					<c:if test="${sectionofficer_remark != null}">
						<c:if test="${! empty sectionofficer_remark}">
							<c:if test="${internalStatusType=='standalonemotion_final_rejection'}">
								<tr>
									<td class="leftCol">
										<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
									</td>
									<td>
										${domain.remarks}
									</td>
								</tr>
							</c:if>
						</c:if>
					</c:if>
				</c:if>
			</c:if>
			
			<tr>
				<td class="leftCol">
					<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
				</td>
				<td>
					${formattedInternalStatus }
				</td>
			</tr>
				
			<c:choose>
				<c:when test="${!empty ministries}">
					<tr>
						<td class="leftCol">
							<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
						</td>
						<td>
							${formattedMinistry}
						</td>
					</tr>
					
					<tr>
						<td class="leftCol">
							<label class="small"><spring:message code="question.department" text="Department"/></label>
						</td>
						<td>
							<c:set var="dept" value="-" />
							<c:forEach items="${subDepartments }" var="i">
								<c:choose>
									<c:when test="${i.id==subDepartmentSelected }">
										<c:set var="dept" value="${i.name}" />
									</c:when>
								</c:choose>
							</c:forEach>
							<c:out value="${dept}"></c:out>
						</td>
					</tr>
																
					
					
					<c:if test="${selectedQuestionType=='motions_standalonemotion_halfhourdiscussion'}">
						<tr>
							<td class="leftCol">
								<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
							</td>		
							<td>
								<c:set var="discDate" value="-"	/>				
								<c:forEach items="${discussionDates}" var="i">
									<c:choose>
										<c:when  test="${i==discussionDateSelected}">
											<c:set var="discDate" value="${i}" />
										</c:when>						
									</c:choose>
								</c:forEach>
								<c:out value="${discDate}"></c:out>
							</td>
						</tr>
					</c:if>	
					
					<c:choose>
						<c:when test="${houseTypeType=='upperhouse'}">
							<tr>
								<td class="leftCol">
									<label class="small"><spring:message code="question.group" text="Group"/>*</label>
								</td>
								<td>
									${formattedGroup}
								</td>		
							</tr>	
						</c:when>							
					</c:choose>
					
				</c:when>
			</c:choose>
				
			<c:if test="${recommendationStatusType == 'standalonemotion_processed_rejectionWithReason'}">
				<tr>
					<td class="leftCol">
						<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
					</td>
					<td>
						${domain.rejectionReason}
					</td>
				</tr>
			</c:if>
		</table>
	
	
	</div>	 
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
	<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<input type="hidden" name="department" id="department" value="${departmentSelected }">
	
</form:form>
<input id="currentStatus" value="${internalStatusType }" type="hidden">

<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">

<input id="noQuestionMsg" value='<spring:message code="question.notfound" text="Question does not exist."></spring:message>' type="hidden" />
<input id="supportingMembersCountErrorMsg" value='<spring:message code="client.error.question.limit.supportingmemebers" text="Please provide proper number of supporting members."></spring:message>' type="hidden">
<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.question.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
<input id="subjectEmptyMsg" value='<spring:message code="client.error.question.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
<input id="questionEmptyMsg" value='<spring:message code="client.error.question.questionEmpty" text="Question Details can not be empty."></spring:message>' type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
<input id="referenceQuestionIncorrectMsg" value="<spring:message code='client.error.referencequestionincorrect' text='Please Provide Correct Question Number'/>" type="hidden">
<input id="questionNumberIncorrectMsg" value="<spring:message code='client.error.referencequestionincorrect' text='Please Provide Proper Question Number'/>" type="hidden">
<input id="questionReferenceEmptyMsg" value="<spring:message code='client.error.questionreferenceempty' text='Please Provide Proper Refernce Number'/>" type="hidden">
<input id="lateSubmissionMsg" value="<spring:message code='client.error.latesubmission' text='Too late to submit.'/>" type="hidden">
<input id="earlySubmissionMsg" value="<spring:message code='client.error.earlysubmission' text='Too early to submit.'/>" type="hidden">

<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>
</body>
</html>