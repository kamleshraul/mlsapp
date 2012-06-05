<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="User Roles" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">

	/* function populateUsers(memberType) {
		$.get('ref/memberType/' + memberType + '/users', function(data) {
			$('#users option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].firstName+" "+ data[i].middleName+" "+data[i].lastName
						+ "</option>";
			}
			$('#users').html(options);
			});
	} */
	
	$(document).ready(function() {
		/* if ($('#memberTypes').val() != undefined) {
			$('#memberTypes').change(function() {
				populateUsers($('#memberTypes').val());
			}); 
		}  */
		
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
		<form:form action="user" method="PUT" id="form"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.member" text="Member"></spring:message>:&nbsp;
		${domain.title} ${domain.firstName} ${domain.middleName } ${domain.lastName }]
	</h2>
			<form:errors path="version" cssClass="validationError" />
							
					<p>
						<label class="small"><spring:message
								code="user.memberType" text="Member Type" /></label> 
						${memberType}		
					</p>
					<p>
						<label class="small"><spring:message
								code="user.user" text="User" /></label>
						${domain.firstName} ${domain.middleName} ${domain.lastName}	 
					</p>
					<p>
						<label class="small"><spring:message
								code="user.role" text="Roles" /></label>
						<form:select cssClass="sSelectMultiple" path="credential.roles" items="${roles}" itemValue="id"
							itemLabel="name" multiple="multiple" id="roles"></form:select>
						<form:errors path="credential.roles" cssClass="validationError" />
					</p>
					<p>
						<label class="small"><spring:message
								code="user.enabled" text="Enabled?" /></label>
						<form:checkbox cssClass="sCheck" path="credential.enabled" id="isEnabled"/>
						<form:errors path="credential.enabled" cssClass="validationError" />
					</p>					
				
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<form:hidden path="credential.id"/>
			<form:hidden path="credential.version"/>
			<form:hidden path="credential.username"/>
			<form:hidden path="credential.password"/>
			<form:hidden path="credential.email"/>
			<form:hidden path="credential.lastLoginTime"/>
			<form:hidden path="version" />
			<form:hidden path="id" />
			<form:hidden path="locale" />						
		</form:form>
	</div>
</body>
</html>