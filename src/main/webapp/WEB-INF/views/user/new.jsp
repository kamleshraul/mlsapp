<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="user" text="Users" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function(){	
		initControls();
		$('#key').val('');	
		
		$('#submit').click(function() {
			if ($('#isEnabled').is(':checked')) {
				$('#isEnabled').val(true);
			} else {
				$('#isEnabled').val(false);
			};
		});

		
		$("#username").change(function(){
			var reportURL = "question/report/generalreport?username="+$("#username").val()+"&report=USER_AVAILABLE&reportout=useravailable&locale=mr_IN";
			$.get(reportURL,function(data){
				$("#userAvailable").empty();
				$("#userAvailable").html(data);
				/* if($('#email').val()==undefined || $('#email').val()=="") {
					$('#email').val($("#username").val()+"@"+$("#default_email_hostname").val());
				} */
				$('#email').val($("#username").val()+"@"+$("#default_email_hostname").val());
			},'html');
			
		});
		
	});
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>

<div class="fields clearfix">
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
						code="user.username" text="Username" />&nbsp;*</label>
				<input  type="text" class="sText" name="username" value="${username}" id="username"  />
				<div style="top: 522px; width: 200px; display: inline; height: 20px; float: right; position:absolute; left: 450px;" id="userAvailable" ></div>
			</p>
			
			<p>
				<label class="small"><spring:message
						code="user.email" text="Email ID" />&nbsp;*</label>
				<input  type="text" class="sText" name="email" id="email" />
				<input type="hidden" id="default_email_hostname" value="${default_email_hostname}">
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
			
			<p>
				<label class="small"><spring:message code="user.starturl" text="Start URL" /></label>
				<form:select path="startURL" cssClass="sSelect" items="${menus }" itemLabel="text" itemValue="url"></form:select>
			</p>	
			
			<p>
				<label class="small"><spring:message code="user.groupsallowed" text="Groups Allowed(In case of Questions)" /></label>
				<form:input path="groupsAllowed" cssClass="sText"></form:input>
			</p>
			
			<p>
				<label class="small"><spring:message code="user.language" text="Language(In case of Reporters)" /></label>
				<select id="languages" name="languages" multiple="multiple" size="5" style="width:188px;">
				<c:forEach items="${languages}" var="i">
				<c:choose>
				<c:when test="${fn:contains(selectedLanguage,i.name)}">
				<option value="${i.name}" selected="selected"><c:out value="${i.name}"></c:out></option>
				</c:when>
				<c:otherwise>
				<option value="${i.name}"><c:out value="${i.name}"></c:out></option>
				</c:otherwise>
				</c:choose>						
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
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>