<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="guesthouse" text="Guest House" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
	});
</script>
</head>
<body>

	<div class="fields clearfix vidhanmandalImg">
		<form:form action="guesthouse" method="POST" modelAttribute="domain">
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
						code="guesthouse.location" text="Location" />&nbsp;*</label>
				<form:input cssClass="sText " path="location" />
				<form:errors path="location" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="guesthouse.numberOfRooms" text="Number Of Rooms" />&nbsp;*</label>
				<form:input cssClass="sText " path="numberOfRooms" />
				<form:errors path="numberOfRooms" cssClass="validationError" />
			</p>

			<p>
				<label class="small"><spring:message
						code="guesthouse.fromDate" text="From Date" />*</label>
				<form:input path="fromDate" cssClass="datemask sText" />
				<form:errors path="fromDate" cssClass="validationError" />
			</p>

			<p>
				<label class="small"><spring:message
						code="guesthouse.toDate" text="To Date" />*</label>
				<form:input path="toDate" cssClass="datemask sText" />
				<form:errors path="toDate" cssClass="validationError" />
			</p>

			<div class="fields expand">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef"> <input id="cancel" type="button"
						value="<spring:message code='generic.cancel' text='Cancel'/>"
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