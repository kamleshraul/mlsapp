<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="Users" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);		
		
		var enabled = ${domain.credential.enabled};
		if(enabled == true){
			$("#isEnabled").attr("checked","checked");
		}
		
		$('#submit').click(function() {
			if ($('#isEnabled').is(':checked')) {
				$('#isEnabled').val(true);
			} else {
				$('#isEnabled').val(false);
			};
		});

		var roles=$("#selectedRoles").val().split(",");
		for(var i=0;i<roles.length;i++){
			$("#roles option[value='"+roles[i]+"']").attr("selected","selected");
		}
	});		
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
		<form:form action="user" method="PUT"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2><spring:message code="generic.edit.heading" text="Details"/>
				[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
			</h2>
			<form:errors path="version" cssClass="validationError" />
			<p>
				<label class="small"><spring:message
						code="user.houseType" text="House Type" />&nbsp;*</label>
				<form:select cssClass="sSelect " path="houseType" items="${houseTypes}" itemLabel="name" itemValue="id" />
				<form:errors path="houseType" cssClass="validationError" />
			</p>
			
			<p>
			<label class="small"><spring:message code="user.title" text="Title"/></label>
			<form:select cssClass="sSelect " path="title" items="${titles}" itemLabel="name" itemValue="name" />
			<form:errors path="title" cssClass="validationError"/>
			</p>
			
			<p>
				<label class="small"><spring:message
						code="user.firstName" text="First Name" />&nbsp;*</label>
				<form:input cssClass="sText " path="firstName" />
				<form:errors path="firstName" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="user.middleName" text="Middle Name" />&nbsp;*</label>
				<form:input cssClass="sText " path="middleName" />
				<form:errors path="middleName" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="user.lastName" text="Last Name" />&nbsp;*</label>
				<form:input cssClass="sText " path="lastName" />
				<form:errors path="lastName" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="user.birthDate" text="Birth Date" />&nbsp;*</label>
				<form:input cssClass="datemask sText" path="birthDate" />
				<form:errors path="birthDate" cssClass="validationError" />
			</p>
			
			<p>
				<label class="small"><spring:message
						code="user.birthPlace" text="Birth Place" />&nbsp;*</label>
				<form:input cssClass="sText" path="birthPlace" />
				<form:errors path="birthPlace" cssClass="validationError" />
			</p>
			
			<p>
				<label class="small"><spring:message
						code="user.email" text="Email ID" />&nbsp;*</label>
				<input type="text" class="sText" name="email" value="${domain.credential.email}" />
				<form:errors path="credential.email" cssClass="validationError" />
			</p>
			
			<p>
				<label class="small"><spring:message code="user.enabled" text="Enabled?" /></label>
				<input type="checkbox" name="isEnabled" id="isEnabled" class="sCheck">
			</p>
			
			<p>
			<label class="small"><spring:message
								code="user.role" text="Roles" /></label>
								<select id="roles" name="roles" multiple="multiple" size="5" style="width:188px;">
								<c:forEach items="${roles}" var="i">
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
								</c:forEach>
								</select>
								
			</p>	
			
			<div class="fields expand">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
						
				</p>
			</div>
			<input type="hidden" name="userId" value="${domain.id}"/>
			<input type="hidden" id="key" name="key">
			<input type="hidden" id="selectedRoles" name="selectedRoles" value="${selectedRoles}">	
			<input type="hidden" id="credential" name="credential" value="${credential}">					
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>