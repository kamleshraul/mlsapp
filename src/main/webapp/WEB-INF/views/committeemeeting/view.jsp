<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeemeeting" text="Committee Meeting"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function() {

		initControls();
	});
	</script>
</head>
<body>
<div class="fields clearfix">
<form>
	<h2><spring:message code="generic.view.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${id}]
	</h2>
	
	
	
	<p>
		<label class="small"><spring:message code="committee.committeeName" text="Committee Name" /></label>
		<input type="text" id="committeeName" name="committeeName" value="${committeeName}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.committeeType" text="Committee Type" /></label>
		<input type="text" id="committeeType" name="committeeType" value="${committeeType}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committeetour.subject" text="Subject"/></label>
		<input type="text" id="committeeSubject" name="committeeSubject" value="${committeeSubject}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="generic.date" text="Date"/></label>
		<input type="text" id="meetingDate" name="meetingDate" value="${meetingDate}" class="sText" readonly="readonly"/>	
	</p>
	
		<p>
	<label class="small"><spring:message code="committeetour.touritinerary.fromTime" text="From Date"/>*</label>
		<input type="text" id="fromDate" name="fromDate" value="${fromDate}" class="sText" readonly="readonly"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.touritinerary.toTime" text="To Date"/>*</label>
			<input type="text" id="toDate" name="toDate" value="${toDate}" class="sText" readonly="readonly"/>	
	</p>
	<p>
	<label class="small"><spring:message code="prashnavali" text="Prashnavali" /></label>
			<input type="text" id="prashnavali" name="prashnavali" value="${prashnavali}" class="sText" readonly="readonly"/>
	</p>
	<p>
	<label class="small"><spring:message code="committeemeetingtype" text="Committee meeting Type" /></label>
			<input type="text" id="committeemeetingtype" name="committeemeetingtype" value="${committeemeetingtype}" class="sText" readonly="readonly"/>
	</p>
	<p> 
	<label class="small"><spring:message code="committeetour.venueName" text="Venue Name"/>*</label>
		<input type="text" id="venueName" name="venueName" value="${venueName}" class="sText" readonly="readonly"/>	
	</p>

	<c:if test="${not empty committeetour}">
	<p> 
	<label class="small"><spring:message code="committeetour" text="Committee Tour"/>*</label>
		<input type="text" id="committeetour" name="committeetour" value="${committeetour}" class="sText" readonly="readonly"/>	
	</p>
	</c:if>
<p> 
	<label class="wysiwyglabel"><spring:message code="committee.conciseMinutes" text="Concise Minutes"/>*</label>
	<textarea name="conciseMinutes" id="conciseMinutes" class="wysiwyg">${conciseMinutes}</textarea>
	<form:errors path="conciseMinutes" cssClass="validationError"/>	
	</p>
	<c:if test="${not empty speech}">
<p> 
	<label class="wysiwyglabel"><spring:message code="committee.chairmanSpeech" text="Comittee Chairman Speech"/></label>
	<textarea name="speech" id="speech" class="wysiwyg">${speech}</textarea>
	<form:errors path="speech" cssClass="validationError"/>	
	</p>
	</c:if>

</form>
</div>
</body>
</html>