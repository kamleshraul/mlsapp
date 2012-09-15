<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="Users with this Role" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">

	$(document).ready(function() {
		var recordId = ${domain.id};
		$('#key').val(recordId);			
	});//document .ready
</script>
</head>
<body>
	<div class="fields clearfix">
		<form:form action="role/user" method="PUT" id="form"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		${domain.id}
	</h2>
			<form:errors path="version" cssClass="validationError" />
					<p>
						<label class="small"><spring:message
								code="role.name" text="Role Name" /></label>
						${domain.name}									 
					</p>
					<p>
						<label class="small"><spring:message code="role.users" text="Users" /></label>
						<form:select cssClass="sexySelect" path="credentials" multiple="multiple" id="users">
							<c:forEach items="${users}" var="i">
								<c:set var="flag" value="false"/>
								<c:forEach items="${domain.credentials}" var="j">
										<c:if  test="${i.credential.id==j.id}">
											<c:set var="flag" value="true"/>
										</c:if>
								</c:forEach>
								<c:choose>									
									<c:when test="${flag==true}">
										<form:option value="${i.credential.id}" selected="selected">${i.firstName} ${i.middleName} ${i.lastName}</form:option>	
									</c:when>
									<c:otherwise>
										<form:option value="${i.credential.id}">${i.firstName} ${i.middleName} ${i.lastName}</form:option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
													
						</form:select>
						<form:errors path="credentials" cssClass="validationError" />
					</p>									
				
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<input type="hidden" id="key" name="key">
			<form:hidden path="version" />
			<form:hidden path="id" />
			<form:hidden path="locale" />
			<form:hidden path="name"/>
			<form:hidden path="type"/>													
		</form:form>
	</div>
</body>
</html>