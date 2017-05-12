<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour" text="Committee Tour"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	</script>
</head>
<body>
<div class="fields clearfix">
<form>
	<h2><spring:message code="generic.view.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${id}]
	</h2>
	
	<p>
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" /></label>
	<input type="text" id="committeeName" name="committeeName" value="${committeeName}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.state" text="State" /></label>
	<input type="text" id="state" name="state" value="${state}" class="sText" readonly="readonly"/>
	</p>

	<p> 
	<label class="small"><spring:message code="committeetour.venueName" text="Venue Name"/></label>
	<input type="text" id="venueName" name="venueName" value="${venueName}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.fromDate" text="From Date"/></label>
	<input type="text" id="fromDate" name="fromDate" value="${fromDate}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.toDate" text="To Date"/></label>
	<input type="text" id="toDate" name="toDate" value="${toDate}" class="sText" readonly="readonly"/>
	</p>
	
	<!-- Table displaying Itinerary -->
	<c:if test="${not empty itineraries}">
		<label class="small"><spring:message code="committeetour.itinerary" text="Itinerary"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="committeetour.touritinerary.date" text="Date"/></th>
				<th><spring:message code="committeetour.touritinerary.fromTime" text="From Time"/></th>
				<th><spring:message code="committeetour.touritinerary.toTime" text="To Time"/></th>
				<th><spring:message code="committeetour.touritinerary.details" text="Details"/></th>
				<th><spring:message code="committeetour.touritinerary.stayover" text="Stayover"/></th>
			</tr>
			<c:set var="itineraryCount" value="1"></c:set>
			<c:forEach items="${itineraries}" var="itinerary">
				<tr>
					<td>${itinerary.formatDate()}</td>
					<td>${itinerary.fromTime}</td>
					<td>${itinerary.toTime}</td>
					<td>
						<textarea id="tourItineraryDetails${itineraryCount}" name="tourItineraryDetails${itineraryCount}"  class="wysiwyg" readonly="readonly" rows="2" cols="50">${itinerary.details}</textarea>
					</td>
					<td>
						<textarea id="tourItineraryStayover${itineraryCount}" name="tourItineraryStayover${itineraryCount}"  class="wysiwyg" readonly="readonly" rows="2" cols="50">${itinerary.stayOver}</textarea>
					</td>
				</tr>
				<c:set var="itineraryCount" value="${itineraryCount + 1}"></c:set>
			</c:forEach>
		</table>
	</c:if>
	
	<!-- Table displaying Reporters -->
	<c:if test="${not empty reporters}">
		<label class="small"><spring:message code="committeetour.committeereporter" text="Reporters"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="committeetour.committeereporter.language" text="Language"/></th>
				<th><spring:message code="committeetour.committeereporter.noOfReporters" text="No. Of Reporters"/></th>
			</tr>
			<c:forEach items="${reporters}" var="reporter">
				<tr>
					<td>${reporter.language.name}</td>
					<td>${reporter.noOfReporters}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	
</form>
</div>
</body>
</html>