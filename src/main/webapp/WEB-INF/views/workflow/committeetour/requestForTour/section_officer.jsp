<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour" text="Committee Tour"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	function onPageLoad() {
		var isRenderAsReadOnly = $('#renderAsReadOnly').val();
		if(isRenderAsReadOnly == 'true') {
			// Render all the visible attributes on the page as readOnly
			$('#remarks').attr('readOnly', true);
		}
	}

	function onStatusChange(statusId) {
		if(statusId != 0) {
			var status = "status=" + statusId;
			var houseType = "houseType=" + getHouseTypeId();
			var userGroup = "userGroup=" + getUserGroupId();
			var level = "assigneeLevel=" + $('#assigneeLevel').val();
			var parameters = status + "&" + houseType + "&" + userGroup + "&" + level;
			var resourceURL = "ref/committeetour/actors/workflow/" + getWorkflowName() + "?" + parameters;
			$.get(resourceURL, function(data){
				var dataLength = data.length;
				if(dataLength > 0) {
					var text = "";
					for(var i = 0; i < dataLength; i++) {
						text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$('#actor').empty();
					$('#actor').html(text);
				}
				else {
					$('#actor').empty();
				}
			});
		}
		else {
			$('#actor').empty();
		}
	}

	function getHouseTypeId() {
		var id = $('#houseTypeId').val();
		return id;
	}

	function getUserGroupId() {
		var id = $('#userGroupId').val();
		return id;
	}

	function getWorkflowName() {
		return $('#workflowName').val();
	}
		
	$('document').ready(function(){	
		onPageLoad();

		$('#status').change(function(){
			var statusId = $('#status').val();
			onStatusChange(statusId);
		});
	});		
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error != '') && (error != null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix">
<form:form action="committeetour" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2>
		<spring:message code="committeetour.requestForTour" text="Request for Tour"/>
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>
	<select class="sSelect" id="committeeName" name="committeeName">
		<option value="${committeeName.id}" selected="selected"><c:out value="${committeeName.displayName}"></c:out></option>
	</select>
	<form:errors path="committee" cssClass="validationError"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.state" text="State" />*</label>
	<input type="text" id="state" name="state" value="${state.name}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.district" text="District" />*</label>
	<input type="text" id="district" name="district" value="${district.name}" class="sText" readonly="readonly"/>
	</p>
	
	<input type="hidden" id="selectedTowns" value="${selectedTowns}" />
	<p>
	<label class="small"><spring:message code="committeetour.town" text="Town" />*</label>
	<form:select path="towns" items="${towns}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>										
	<form:errors path="towns" cssClass="validationError"/>
	</p>
	
	<input type="hidden" id="selectedZillaparishads" value="${selectedZillaparishads}" />
	<p>
		<label class="small"><spring:message code="committeetour.zillaparishads" text="Zillaparishad"/></label>
		<form:select path="zillaparishads" items="${zillaparishads}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="zillaparishads" cssClass="validationError"/>
	</p>
	
	<p> 
	<label class="small"><spring:message code="committeetour.venueName" text="Venue Name"/>*</label>
	<form:input path="venueName" cssClass="sText" readonly="true"/>
	<form:errors path="venueName" cssClass="validationError"/>	
	</p>
	
	<p> 
	<label class="small"><spring:message code="committeetour.subject" text="Subject"/>*</label>
	<form:input path="subject" cssClass="sText" readonly="true"/>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.fromDate" text="From Date"/>*</label>
	<form:input path="fromDate" cssClass="datetimemask sText" readonly="true"/>
	<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.toDate" text="To Date"/>*</label>
	<form:input path="toDate" cssClass="datetimemask sText" readonly="true"/>
	<form:errors path="toDate" cssClass="validationError"/>	
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
						<textarea id="tourItineraryDetails${itineraryCount}" name="tourItineraryDetails${itineraryCount}"  readonly="readonly" rows="2" cols="50">${itinerary.details}</textarea>
					</td>
					<td>
						<textarea id="tourItineraryStayover${itineraryCount}" name="tourItineraryStayover${itineraryCount}"  readonly="readonly" rows="2" cols="50">${itinerary.stayOver}</textarea>
					</td>
				</tr>
				<input type="hidden" id="tourItineraryId${itineraryCount}" name="tourItineraryId${itineraryCount}" value="${itinerary.id}"/>
				<input type="hidden" id="tourItineraryLocale${itineraryCount}" name="tourItineraryLocale${itineraryCount}" value="${itinerary.locale}"/>
				<input type="hidden" id="tourItineraryVersion${itineraryCount}" name="tourItineraryVersion${itineraryCount}" value="${itinerary.version}"/>
				<input type="hidden" id="tourItineraryDate${itineraryCount}" name="tourItineraryDate${itineraryCount}" value="${itinerary.formatDate()}"/>
				<input type="hidden" id="tourItineraryFromTime${itineraryCount}" name="tourItineraryFromTime${itineraryCount}" value="${itinerary.fromTime}"/>
				<input type="hidden" id="tourItineraryToTime${itineraryCount}" name="tourItineraryToTime${itineraryCount}" value="${itinerary.toTime}"/>
				<c:set var="itineraryCount" value="${itineraryCount + 1}"></c:set>
			</c:forEach>
		</table>
		<input type="hidden" id="tourItineraryCount" name="tourItineraryCount" value="${fn:length(itineraries)}"/>
	</c:if>
	
	<!-- Table displaying Reporters -->
	<c:if test="${not empty reporters}">
		<label class="small"><spring:message code="committeetour.committeereporter" text="Reporters"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="committeetour.committeereporter.language" text="Language"/></th>
				<th><spring:message code="committeetour.committeereporter.noOfReporters" text="No. Of Reporters"/></th>
			</tr>
			<c:set var="reporterCount" value="1"></c:set>
			<c:forEach items="${reporters}" var="reporter">
				<tr>		
					<td>${reporter.language.name}</td>
					<td>${reporter.noOfReporters}</td>
				</tr>
				<input type="hidden" id="committeeReporterId${reporterCount}" name="committeeReporterId${reporterCount}" value="${reporter.id}"/>
				<input type="hidden" id="committeeReporterLocale${reporterCount}" name="committeeReporterLocale${reporterCount}" value="${reporter.locale}"/>
				<input type="hidden" id="committeeReporterVersion${reporterCount}" name="committeeReporterVersion${reporterCount}" value="${reporter.version}"/>
				<input type="hidden" id="committeeReporterLanguage${reporterCount}" name="committeeReporterLanguage${reporterCount}" value="${reporter.language.id}"/>
				<input type="hidden" id="committeeReporterNoOfReporters${reporterCount}" name="committeeReporterNoOfReporters${reporterCount}" value="${reporter.noOfReporters}"/>
				<c:set var="reporterCount" value="${reporterCount + 1}"></c:set>
			</c:forEach>
		</table>
		<input type="hidden" id="committeeReporterCount" name="committeeReporterCount" value="${fn:length(reporters)}"/>
	</c:if>	
	
	<p>
	<label class="small"><spring:message code="committeetour.putUpFor" text="Put Up For" /></label>
	<select id="status" name="status" class="sSelect">
	<c:choose>
		<c:when test="${not empty statuses}">
			<c:forEach items="${statuses}" var="i">
				<c:choose>
					<c:when test="${status.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<option value="${status.id}" selected="selected"><c:out value="${status.name}"></c:out></option>
		</c:otherwise>
	</c:choose>		
	</select>
	</p>
		
	<c:if test="${hideNextActors ne true}">
		<c:if test="${not empty actor}">
			<p>
			<label class="small"><spring:message code="committeetour.nextactor" text="Next Actor"/></label>
			<select id="actor" name="actor" class="sSelect">
			<c:choose>
				<c:when test="${not empty actors}">
					<c:forEach items="${actors}" var="i">
						<c:choose>
							<c:when test="${actor.id == i.id}">
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<option value="${actor.id}" selected="selected"><c:out value="${actor.name}"></c:out></option>
				</c:otherwise>
			</c:choose>
			</select>
			</p>
		</c:if>
	</c:if>
		
	<p>
		<label class="wysiwyglabel"><spring:message code="committee.remarks" text="Remarks"/></label>
		<textarea id="remarks" name="remarks"  class="wysiwyg" rows="2" cols="50">${remarks}</textarea>	
	</p>	
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
		<c:if test="${renderAsReadOnly ne true}">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	
	<input type="hidden" id="workflowInit" name="workflowInit" value="${workflowInit}"/>
	<input type="hidden" id="workflowName" name="workflowName" value="${workflowName}"/>
	<input type="hidden" id="assigneeLevel" name="assigneeLevel" value="${assigneeLevel}"/>
 	<input type="hidden" id="houseTypeId" name="houseTypeId" value="${houseType.id}"/>
	<input type="hidden" id="userGroupId" name="userGroupId" value="${userGroup.id}"/>
	<input id="requestForTourMsg" value="<spring:message code='committeetour.requestForTourMsg' text='Do you want to put up request for tour?'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="renderAsReadOnly" name="renderAsReadOnly" value="${renderAsReadOnly}"/>
</form:form>
</div>
</body>
</html>