<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeemeeting" text="Committee Meeting"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	
	// To apply timemask to the time fields
	$('.timemask').focus(function(){
		if($(this).val() == ""){
			$(".timemask").mask("99:99:99");
		}
	});
	
	function onCommitteeNameChange(committeeNameId) {
		// Make an ajax call to fetch CommitteType corresponding to the selected
		// committeeName
		var resourceURL = "ref/committeeName/" + committeeNameId + "/committeeType";
		$.get(resourceURL, function(data){
			$('#committeeType').val(data.name);
		});
		
		
	}
	function hideUnhideCommitteeTourField() {
		var committeeMeetingType = $('#committeemeetingtype').val();
		if(committeeMeetingType == "2") {
		
			$('.committeetourField').show();
			var committeeNameId = $('#committeeName').val();
			var resourceURL = "ref/committeeName/" + committeeNameId + "/committeeTours";
			$.get(resourceURL, function(data){
				var dataLength = data.length;
				if(dataLength > 0) {
					var text = "";
					for(var i = 0; i < dataLength; i++) {
						text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$('#committeetour').empty();
					$('#committeetour').html(text);
				}
				else {
					$('#committeetour').empty();
				}
			});
		}
		else {
			$('.committeetourField').hide();
			$('#committeetour').empty();
		}
	}
	
	function hideUnhideSpeechField() {
		var returnvalue = jQuery("#isSpeech").prop("checked");
		  if (returnvalue) {
			  $('.speechField').show();
          }
          else {
        	  $('.speechField').hide();
          }
		
		
			
	}
	
	$('document').ready(function(){	
	
	/* 	$('#committeeName').change(function(){
			var committeeNameId = $('#committeeName').val();
			 $('#committeemeetingtype').val("-");
			onCommitteeNameChange(committeeNameId);
			hideUnhideCommitteeTourField();
		}); */
		
		hideUnhideCommitteeTourField();
		hideUnhideSpeechField();
		$('#committeemeetingtype').change(function(){
		
			hideUnhideCommitteeTourField();
		});
		$('#isSpeech').click(function(){
			
			hideUnhideSpeechField();
		});
	
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committeemeeting" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<c:if test="${committeeDisolved eq true}">
		<div style="background-color:red;color:white;padding:10px;font-size:15px;">
			<c:if test="${committeeDisolutionDateFormatted eq ''}">
				<spring:message code="committee.meeting.disabled" 
					text="Current Committee Is Disolved, Currently Cannot Create Meeting"/>
			</c:if>
			<c:if test="${!(committeeDisolutionDateFormatted eq '')}">
				<spring:message code="committee.meeting.disolved" 
					text="Current Committee Is Disolved, Currently Cannot Create Meeting"
					arguments="${committeeDisolutionDateFormatted}"
					htmlEscape="false"
					argumentSeparator=";"></spring:message>
			</c:if>
			<%-- <c:url value="${committeeDisolutionDateFormatted}"/> --%>
		</div>
		<br/><br/>
	</c:if>
	

	<p>
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>
	<%-- <select class="sSelect" id="committeeName" name="committeeName">
		<c:forEach items="${committeeNames}" var="i">
			<c:choose>
				<c:when test="${committeeName.id == i.id}">
					<option value="${i.id}" selected="selected"><c:out value="${i.displayName}"></c:out></option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
	<form:errors path="committee" cssClass="validationError"/> --%>
	<input type="text" id="committeeDisplayName" name="committeeDisplayName" value="${committeeDisplayName}" readonly="readonly"/>
	<input type="hidden" id="committeeName" name="committeeName" value="${committeName}"/>
	</p>

	
<p>
		<label class="small"><spring:message code="committee.committeeType" text="Committee Type"/></label>
		<input type="text" id="committeeType" name="committeeType" value="${committeeType}" readonly="readonly"/>
	</p>
			<p>
	<label class="small"><spring:message code="committeetour.subject" text="Committee Subjects" />*</label>
	<select class="sSelect" id="committeeSubject" name="committeeSubject">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
	
		<c:forEach items="${committeeSubjects}" var="i">
			<c:choose>
				<c:when test="${committeeSubject.id == i.id}">
					<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
	<form:errors path="committee" cssClass="validationError"/>
	</p>
	
		
	
	<p>
		<label class="small"><spring:message code="generic.date" text="Date"/>*</label>
		<form:input path="meetingDate" cssClass="datemask sText" />
		<form:errors path="meetingDate" cssClass="validationError"/>	
	</p>
		

	
	<p>
	<label class="small"><spring:message code="committeetour.touritinerary.fromTime" text="From Date"/>*</label>
	<form:input path="startTime" cssClass="timemask sText" />
	<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.touritinerary.toTime" text="To Date"/>*</label>
	<form:input path="endTime" cssClass="timemask sText" />
	<form:errors path="endTime" cssClass="validationError"/>	
	</p>

			<p>
		<label class="small"><spring:message code="prashnavali" text="Prashnavali" /></label>
		<select id="prashnavali" name="prashnavali" class="sSelect">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${prashnavalis}" var="i">
				<c:choose>
					<c:when test="${prashnavali.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.prashnavaliName}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.prashnavaliName}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
			<p>
		<label class="small"><spring:message code="committeemeetingtype" text="Committee meeting Type" /></label>
		<select id="committeemeetingtype" name="committeemeetingtype" class="sSelect">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${committeemeetingtypes}" var="i">
				<c:choose>
					<c:when test="${committeemeetingtype.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>

			<p> 
	<label class="small"><spring:message code="committeetour.venueName" text="Venue Name"/>*</label>
	<form:input path="meetingLocation" cssClass="sText"/>
	<form:errors path="meetingLocation" cssClass="validationError"/>	
	</p>
	<p class="committeetourField">
		<label class="small"><spring:message code="committeetour" text="Committee Tour" /></label>
		<select id="committeetour" name="committeetour" class="sSelect">
			<c:forEach items="${committeetours}" var="i">
				<c:choose>
					<c:when test="${committeetour.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.subject}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.subject}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p> 
	<label class="wysiwyglabel"><spring:message code="committee.conciseMinutes" text="Concise Minutes"/>*</label>
	<form:textarea path="conciseMinutes" cssClass="wysiwyg"/>
	<form:errors path="conciseMinutes" cssClass="validationError"/>	
	</p>
	<p> 
	<label class="small"><spring:message code="committee.chairmanSpeech" text="Comittee Chairman Speech"/></label>
	<input type="checkbox" id="isSpeech" name="isSpeech"> 
	</p>	<br></br>	
	<p class="speechField"> 
	<label class="small"><spring:message code="committee.Speech" text="Comittee Speech"/></label>
	<form:textarea path="speech" cssClass="wysiwyg"/>
	<form:errors path="speech" cssClass="validationError"/>	
	</p>
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<c:if test="${committeeDisolved != '' && !committeeDisolved eq true}">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:if>
			<c:if test="${committeeDisolved == '' || committeeDisolved eq false }">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:if>
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>