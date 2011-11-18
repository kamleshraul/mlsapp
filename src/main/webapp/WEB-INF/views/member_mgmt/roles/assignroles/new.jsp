<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="mms.assignroles.new.title" text="Assign New Roles"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>
<form:form cssClass="wufoo" action="member_role/assignroles/createMemberRoles" method="POST" 
	modelAttribute="memberRole">
	<div class="info">
			<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="Note: Fields marked * are mandatory"/></div>
	</div>
	<ul>
	<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error"><spring:message code="generic.error.label"/></p>
			</c:if>
			 <form:errors path="assembly" cssClass="field_error" />	
	</li>			
		<li>
		<label class="desc"><spring:message code="generic.locale" text="Select language"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select medium" path="locale"> 
				<form:option value="en"><spring:message code="generic.lang.english" text="English"/></form:option>
					<form:option value="hi_IN"><spring:message code="generic.lang.hindi" text="Hindi"/></form:option>
					<form:option value="mr_IN"><spring:message code="generic.lang.marathi" text="Marathi"/></form:option>
				</form:select>
			</div>
		</li>
	<li>
		<label class="desc"><spring:message code="mms.assignroles.memberid" text="Member Id"/>&nbsp;*</label>
			<div>
				<input type="text" value="${memberRole.member.id}" readonly="readonly" class="field text medium" name="memberId" id="memberId">
			</div>
	</li>		
	<li>
		<label class="desc"><spring:message code="mms.assignroles.member" text="Member Name"/>&nbsp;*</label>
			<div>
				<input type="text" value="${memberRole.member.firstName} ${memberRole.member.middleName} ${memberRole.member.lastName}" readonly="readonly" class="field text medium" >
			</div>
	</li>	
	<li>
	<label class="desc"><spring:message code="mms.assignroles.assembly" text="Assembly"/>&nbsp;*</label>
		<div>
				<form:select path="assembly" items="${assemblies}" itemValue="id" itemLabel="assembly" id="assemblies" cssClass="field select medium">
	            </form:select>
	           
		</div>
	</li>
	<li>
	<label class="desc"><spring:message code="mms.assignroles.roles" text="Role"/>&nbsp;*</label>
		<div>
				<select multiple="multiple" id="roles" name="roles">
				<c:forEach items="${roles}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>				
				</c:forEach>
	            </select>
	            <form:errors path="role" cssClass="field_error" />	
		</div>
	</li>
	<li>
		<label class="desc"><spring:message code="mms.assignroles.fromdate" text="From"/>&nbsp;*</label>
			<div>
				<form:input cssClass="date field text medium" path="fromDate"/><form:errors path="fromDate" cssClass="field_error" />	
			</div>
		</li>	
		
	<li>
		<label class="desc"><spring:message code="mms.assignroles.todate" text="To"/></label>
			<div>
				<form:input cssClass="date field text medium" path="toDate"/><form:errors path="toDate" cssClass="field_error" />	
			</div>
	</li>	
	<li>
		<label class="desc"><spring:message code="mms.assignroles.remarks" text="Remarks"/></label>
			<div>
				<form:textarea cssClass="field textarea small" path="remarks"/><form:errors path="remarks" cssClass="field_error" />	
			</div>
		</li>
	<li class="buttons">
		<input type="hidden" name="assignmentDate" value="${assignmentDate}" id="assignmentDate">
		<input id="saveForm" class="btTxt" type="submit" 
			value="<spring:message code='generic.submit' text='Submit'/>" />
	</li>
	<form:hidden path="id"/>		
	<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
</html>