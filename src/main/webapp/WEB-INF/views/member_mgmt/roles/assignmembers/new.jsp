<%@ include file="/common/taglibs.jsp" %>
<html>
<body>	
<form:form cssClass="wufoo" action="member_role/assignmembers" method="POST" 
	modelAttribute="memberRole">
	<div class="info">
			<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="Note: Fields marked * are mandatory"/></div>
	</div>
	<ul>
	<li class="section first">
			<c:if test="${isvalid eq false}">
				<p class="field_error"><spring:message code="generic.error.label"/></p>
			</c:if>
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
	<label class="desc"><spring:message code="mms.assignroles.assembly" text="Assembly"/>&nbsp;*</label>
		<div>
				<form:select path="assembly" items="${assemblies}" itemValue="id" itemLabel="assembly" id="assemblies" cssClass="field select medium">
	            </form:select>
	            <form:errors path="assembly" cssClass="field_error" />	
		</div>
	</li>
	<li>
	<label class="desc"><spring:message code="mms.assignroles.roles" text="Role"/>&nbsp;*</label>
		<div>
				<input id="role" name="role" cssClass="field text medium" value="${memberRole.role.name}" type="text" readonly="readonly">	           
	           	<input id="roleId" name="roleId" cssClass="field text medium" value="${memberRole.role.id}" type="hidden">	           
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
		<label class="desc"><spring:message code="mms.assignroles.members" text="Select Members"/></label>
			<div>
			<select multiple="multiple" id="members" name="members">
				<c:forEach items="${members}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>				
				</c:forEach>
	            </select>
     		</div>
	</li>
	<li>
		<label class="desc"><spring:message code="mms.assignroles.remarks" text="Remarks"/></label>
			<div>
				<form:textarea cssClass="field textarea small" path="remarks"/><form:errors path="remarks" cssClass="field_error" />	
			</div>
	</li>	
	<li class="buttons">
		<input id="saveForm" class="btTxt" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" />
	</li>
	<form:hidden path="id"/>		
	<form:hidden path="version"/>
	</ul>		
</form:form>
</body>
<head>
	<title><spring:message code="mms.assignmembers.new.title" text="Assign Members"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
</head>
</html>