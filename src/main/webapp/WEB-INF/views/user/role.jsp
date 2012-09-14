<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="User Roles" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">

	$(document).ready(function() {
		var recordId = ${domain.id};
		$('#key').val(recordId);
			$('#submit').click(function() {		
			
			   	if($('#isEnabled').is(':checked'))
			   	{
					$('#isEnabled').val(true);		   	    
				}
				else
			   	{ 				
					$('#isEnabled').val(false);				
			   	};
					
		});
	});//document .ready
</script>
</head>
<body>
	<div class="fields clearfix">
		<form:form action="user/role" method="PUT" id="form"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.member" text="Member"></spring:message>:&nbsp;
		${domain.title} ${domain.firstName} ${domain.middleName } ${domain.lastName }]
	</h2>
			<form:errors path="version" cssClass="validationError" />
					<p>
						<label class="small"><spring:message
								code="user.user" text="User" /></label>
						${domain.firstName} ${domain.middleName} ${domain.lastName}	 
					</p>
					<p>
						<label class="small"><spring:message
								code="user.role" text="Roles" /></label>
						<select Class="sexySelect" name="roles" multiple="multiple" >
						<c:forEach items="${roles}" var="i">
							<c:set var="flag" value="false"/>
							<c:forEach items="${domain.credential.roles}" var="j">
								<c:if  test="${i.id==j.id}">
											<c:set var="flag" value="true"/>
								</c:if>
							</c:forEach>
							<c:choose>									
									<c:when test="${flag==true}">
										<option value="${i.type}" selected="selected"> ${i.name}</option>	
									</c:when>
									<c:otherwise>
										<option value="${i.type}"> ${i.name}</option>
									</c:otherwise>
								</c:choose>
						</c:forEach>
						</select>
						<form:errors path="credential.roles" cssClass="validationError" />
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
			<form:hidden path="firstName"/>
			<form:hidden path="middleName"/>
			<form:hidden path="lastName"/>
			<form:hidden path="title"/>
			<form:hidden path="birthDate"/>
			<form:hidden path="birthPlace"/>
			<form:hidden path="credential.id"/>
			<form:hidden path="credential.version"/>
			<form:hidden path="credential.locale"/>
			<form:hidden path="credential.username"/>
			<form:hidden path="credential.password"/>
			<form:hidden path="credential.email"/>
			<form:hidden path="credential.enabled"/>
			<form:hidden path="version" />
			<form:hidden path="id" />
			<form:hidden path="locale" />						
		</form:form>
	</div>
</body>
</html>