<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="Users" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
		
		$('#submit').click(function() {
			if ($('#isEnabled').is(':checked')) {
				$('#isEnabled').val(true);
			} else {
				$('#isEnabled').val(false);
			};
		});
	});
</script>
</head>
<body>

<div class="fields clearfix vidhanmandalImg">
		<form:form action="user" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<form:errors path="version" cssClass="validationError" />
		<p>
			<label class="small"><spring:message code="user.title" text="Title"/></label>
			<form:input cssClass="sText " path="title" />
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
				<input  type="text" class="sText" name="email"  />
				<form:errors path="credential.email" cssClass="validationError" />
			</p>
			
			<p>
				<label class="small"><spring:message code="user.enabled" text="Enabled?" /></label>
				<input type="checkbox" name="isEnabled" id="isEnabled" class="sCheck">
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
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>