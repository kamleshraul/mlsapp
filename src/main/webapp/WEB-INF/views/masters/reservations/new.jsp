<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="${urlPattern}" text="Reservation type" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>

	<div class="fields clearfix">
		<form:form action="${urlPattern}" method="POST"
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
						code="${urlPattern}.reservationType" text="Reservation type" />&nbsp;*</label>
				<form:input cssClass="sSelect " path="reservationType" />
				<form:errors path="reservationType" cssClass="validationError" />
			</p>
			
			<p>
				<label class="small"><spring:message
						code="${urlPattern}.shortName" text="Short Name" />&nbsp;*</label>
				<form:input cssClass="sSelect " path="shortName" />
				<form:errors path="shortName" cssClass="validationError" />
			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef"> 
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
		</form:form>
	</div>
</body>
</html>