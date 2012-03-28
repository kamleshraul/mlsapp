<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="${urlPattern}.list"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
	});
</script>
</head>
<body>
	<div class="commandbar">
	</div>
	<div class="fields clearfix">
		<form:form action="${urlPattern}" method="POST"
			modelAttribute="domain">
			<form:input path="house" type="hidden" name="houseId" value="${houseId}" id="houseId"/>
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
						code="${urlPattern}.number" text="Session Number" />&nbsp;*</label>
				<form:input path="number" cssClass="integer sText"></form:input>
			<p>
				<label class="small"><spring:message
						code="${urlPattern}.startDate" text="Start Date" />&nbsp;*</label>
				<form:input cssClass="datemask sText" path="startDate" />
				<form:errors path="startDate" cssClass="validationError" />

			</p>

			<p>
				<label class="small"><spring:message code="${urlPattern}.endDate"
						text="End Date" /></label>
				<form:input cssClass="datemask sText" path="endDate" />
				<form:errors path="endDate" cssClass="validationError" />

			</p>
			<p>
				<label class="small"><spring:message code="${urlPattern}.type" text="Session Type" />&nbsp;*</label>
				<form:select cssClass="sSelect" path="type"
					items="${sessionType}" itemValue="id" itemLabel="sessionType">
				</form:select>

			</p>
			<p>
				<label class="small"><spring:message code="${urlPattern}.place" text="Session Place" />&nbsp;*</label>
				<form:select cssClass="sSelect" path="place"
					items="${place}" itemValue="id" itemLabel="place">
				</form:select>

			</p>
			<p>
		<label class="small"><spring:message code="${urlPattern}.year" text="Session Year"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="year"/>
				<form:errors path="year" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="${urlPattern}.durationInDays" text="Session Duration In days"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInDays"/>
				<form:errors path="durationInDays" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="${urlPattern}.durationInHrs" text="Session Duration In Hours"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInHrs"/>
				<form:errors path="durationInHrs" cssClass="validationError" />	
			
		</p>
		<p>
		<label class="small"><spring:message code="${urlPattern}.durationInMins" text="Session Duration In Mins"/>&nbsp;*</label>
				<form:input cssClass="integer sText" path="durationInMins"/>
				<form:errors path="durationInMins" cssClass="validationError" />	
			
		</p>
		<p>
				<label class="small"><spring:message code="${urlPattern}.remarks"
						text="Remarks" /></label>
				<form:textarea cssClass="sTextarea" path="remarks" />
				<form:errors path="remarks" cssClass="validationError" />

			</p>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef">
				</p>
			</div>
			<form:hidden path="id" />
			<form:hidden path="version" />
			<form:hidden path="locale" />
			
		</form:form>
	</div>
</body>
</html>