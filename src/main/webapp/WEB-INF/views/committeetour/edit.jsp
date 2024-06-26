<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour" text="Committee Tour"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function onPageLoad() {
		prependOptionToStatus();
		$('#actor').empty();
		prependOptionToActor();
	}

	function prependOptionToStatus() {
		var optionValue = $('#pleaseSelect').val();
		var option = "<option value='0' selected>" + optionValue + "</option>";
		$('#status').prepend(option);
	}

	function prependOptionToActor() {
		var optionValue = $('#pleaseSelect').val();
		var option = "<option value='0' selected>" + optionValue + "</option>";
		$('#actor').prepend(option);
	}
	
	function onStateChange(stateId) {
		var resourceURL = "ref/state/" + stateId + "/districts";
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#districts').empty();
				$("#towns").empty();
				$("#zillaparishads").empty();
				$('#districts').html(text);

				// Trigger District change, so that towns corresponding to the district will be set
// 				var districtId = data[0].id;
// 				onDistrictChange(districtId);
			}
			else {
				$('#districts').empty();
			}
		});
	}

	function loadTowns() {
		
		var locale=$("#locale").val();		
	
		var districts=$("#districts").val();
		if(districts!=''){
			$.post('ref/towns/bydistricts',{'districts':districts},function(data){
				$("#towns").empty();
				var text="";
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
					$("#towns").html(text);
					$("#towns").multiSelect();
					$.unblockUI();				
				}else{
					$("#towns").empty();
					$.unblockUI();				
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}else{
			$("#towns").empty();
			
			$.unblockUI();			
		}
	
	}
	
function loadZillaparishads() {
		
		var locale=$("#locale").val();		
		
		var districts=$("#districts").val();
		if(districts!=''){
			$.post('ref/zillaparishads/bydistricts',{'districts':districts},function(data){
				$("#zillaparishads").empty();
				var text="";
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
					$("#zillaparishads").html(text);
					$("#zillaparishads").multiSelect();
					$.unblockUI();				
				}else{
					$("#zillaparishads").empty();
					$.unblockUI();				
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}else{
			$("#zillaparishads").empty();
			
			$.unblockUI();			
		}
	
	}
	
$('#districts').change(function(){
	
//		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
	$("#towns").empty();
	loadTowns();
//		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
	$("#zillaparishads").empty();
	loadZillaparishads();
});

	function onDistrictChange(districtId) {
		var resourceURL = "ref/district/" + districtId + "/towns";
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#towns').empty();
				$('#towns').html(text);
			}
			else {
				$('#towns').empty();
			}
		});
	}

	var tourItineraryCount = parseInt($('#tourItineraryCount').val());
	var totalTourItineraryCount = 0;
	totalTourItineraryCount = totalTourItineraryCount + tourItineraryCount;
	function addItinerary() {
		tourItineraryCount = tourItineraryCount + 1;
		totalTourItineraryCount = totalTourItineraryCount + 1;

		var text = "<div id='itinerary" + tourItineraryCount + "'>" +
		    "<p>" +
  		  	"<label class='small'>" + $('#tourItineraryDateMessage').val() + "*</label>" +
  		  	"<input name='tourItineraryDate" + tourItineraryCount + "' id='tourItineraryDate" + tourItineraryCount + "' class='datemask sText'>" +
  		    "</p>" +
  		 	"<p>" +
		  	"<label class='small'>" + $('#tourItineraryFromTimeMessage').val() + "*</label>" +
		  	"<input name='tourItineraryFromTime" + tourItineraryCount + "' id='tourItineraryFromTime" + tourItineraryCount + "' class='timemask sText'>" +
		  	"</p>" +
		  	"<p>" +
  		  	"<label class='small'>" + $('#tourItineraryToTimeMessage').val() + "*</label>" +
  		    "<input name='tourItineraryToTime" + tourItineraryCount + "' id='tourItineraryToTime" + tourItineraryCount + "' class='timemask sText'>" +
  		  	"</p>" +
  		 	"<p>" +
		  	"<label class='small'>" + $('#tourItineraryDetailsMessage').val() + "</label>" +
		  	"<textarea name='tourItineraryDetails" + tourItineraryCount + "' id='tourItineraryDetails" + tourItineraryCount + "' rows='2' cols='50'></textarea>" +
		    "</p>" +
		 	"<p>" +
		  	"<label class='small'>" + $('#tourItineraryStayoverMessage').val() + "</label>" +
		  	"<textarea name='tourItineraryStayover" + tourItineraryCount + "' id='tourItineraryStayover" + tourItineraryCount + "' rows='2' cols='50'></textarea>" +
		  	"</p>" +
  		  	"<input type='button' class='button' id='" + tourItineraryCount + "' value='" + $('#deleteItineraryMessage').val() + "' onclick='deleteItinerary(" + tourItineraryCount + ");'>" +
		  	"<input type='hidden' id='tourItineraryId" + tourItineraryCount + "' name='tourItineraryId" + tourItineraryCount +"'>" +
		  	"<input type='hidden' id='tourItineraryLocale" + tourItineraryCount + "' name='tourItineraryLocale" + tourItineraryCount + "' value='" + $('#locale').val() +"'>" +
		  	"<input type='hidden' id='tourItineraryVersion" + tourItineraryCount + "' name='tourItineraryVersion" + tourItineraryCount + "'>" +
		  	"</div>"; 

		var prevCount = tourItineraryCount - 1;
		if(totalTourItineraryCount == 1){
			$('#addItinerary').after(text);
		} else{
			$('#itinerary'+ prevCount).after(text);
		}
		$('#tourItineraryCount').val(tourItineraryCount); 

		// To apply datemask to the date fields
		$('.datemask').focus(function(){
			if($(this).val() == ""){
				$(".datemask").mask("99/99/9999");
			}
		});

		// To apply timemask to the time fields
		$('.timemask').focus(function(){
			if($(this).val() == ""){
				$(".timemask").mask("99:99:99");
			}
		});
	}

	function deleteItinerary(id) {
		var tourItineraryId = $('#tourItineraryId' + id).val();
		if(tourItineraryId != ''){			
	    	$.delete_('committeetour/touritinerary/' + tourItineraryId + '/delete', 
	    	    null, 
	    	    function(data, textStatus, XMLHttpRequest) {
	    			$('#itinerary' + id).remove();
	    			tourItineraryCount = tourItineraryCount - 1;
					if(id == tourItineraryCount) {
						tourItineraryCount = tourItineraryCount - 1;
					}
	    		}
    		).fail(function() {
				if($("#ErrorMsg").val() != '') {
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}
				else {
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		} 
		else {
			$('#itinerary'+id).remove();
			totalTourItineraryCount = totalTourItineraryCount - 1;
			if(id == tourItineraryCount) {
				tourItineraryCount = tourItineraryCount - 1;
			}
		}	
	}

	var committeeReporterCount = parseInt($('#committeeReporterCount').val());
	var totalCommitteeReporterCount = 0;
	totalCommitteeReporterCount = totalCommitteeReporterCount + committeeReporterCount;

	function addReporter() {
		committeeReporterCount = committeeReporterCount + 1;
		totalCommitteeReporterCount = totalCommitteeReporterCount + 1;

		var text = "<div id='reporter" + committeeReporterCount + "'>" +
			"<p>" +
			"<label class='small'>" + $('#committeeReporterLanguageMessage').val() + "*</label>" +
			"<select name='committeeReporterLanguage" + committeeReporterCount + "' id='committeeReporterLanguage" + committeeReporterCount + "' class='sSelect'>" +
			$('#languageMaster').html() +
			"</select>" +
		    "</p>" +
			"<p>" +
			"<label class='small'>" + $('#committeeReporterNoOfReportersMessage').val() + "*</label>" +
			"<input name='committeeReporterNoOfReporters" + committeeReporterCount + "' id='committeeReporterNoOfReporters" + committeeReporterCount + "' class='sText Integer'>" +
			"</p>" +
			"<input type='button' class='button' id='" + committeeReporterCount + "' value='" + $('#deleteCommitteeReporterMessage').val() + "' onclick='deleteReporter(" + committeeReporterCount + ");'>" +
			"<input type='hidden' id='committeeReporterId" + committeeReporterCount + "' name='committeeReporterId" + committeeReporterCount +"'>" +
			"<input type='hidden' id='committeeReporterLocale" + committeeReporterCount + "' name='committeeReporterLocale" + committeeReporterCount + "' value='" + $('#locale').val() +"'>" +
			"<input type='hidden' id='committeeReporterVersion" + committeeReporterCount + "' name='committeeReporterVersion" + committeeReporterCount + "'>" +
			"</div>"; 

		var prevCount = committeeReporterCount - 1;
		if(totalCommitteeReporterCount == 1){
			$('#addReporter').after(text);
		} else{
			$('#reporter'+ prevCount).after(text);
		}
		$('#committeeReporterCount').val(committeeReporterCount);
	}

	function deleteReporter(id) {
		var committeeReporterId = $('#committeeReporterId' + id).val();
		if(committeeReporterId != ''){			
			$.delete_('committeetour/committeereporter/' + committeeReporterId + '/delete', 
				null, 
				function(data, textStatus, XMLHttpRequest) {
					$('#reporter' + id).remove();
					committeeReporterCount = committeeReporterCount - 1;
					if(id == committeeReporterCount) {
						committeeReporterCount = committeeReporterCount - 1;
					}
				}
			).fail(function() {
				if($("#ErrorMsg").val() != '') {
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}
				else {
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		} 
		else {
			$('#reporter' + id).remove();
			totalCommitteeReporterCount = totalCommitteeReporterCount - 1;
			if(id == committeeReporterCount) {
				committeeReporterCount = committeeReporterCount - 1;
			}
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
					prependOptionToActor();
				}
			});
		}
		else {
			$('#actor').empty();
			prependOptionToActor();
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
		initControls();
		$('#key').val('');

		onPageLoad();

		$('#state').change(function(){
			var stateId = $('#state').val();
			onStateChange(stateId);
		});

		$('#district').change(function(){
			var districtId = $('#district').val();
			onDistrictChange(districtId);
		});

		$('#addItinerary').click(function(){
			addItinerary();
		});

		$('#addReporter').click(function(){
			addReporter();
		});

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
	
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- committeename is a simple input field and not a form input field because
		 it is not an attribute of the CommitteeTour instance. -->
	<p>
	
	<label class="small"><spring:message code="committeetour.committeename" text="Committee Name" />*</label>
	<select id="committeeName" name="committeeName" class="sSelect">
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
	<form:errors path="committee" cssClass="validationError"/>
	</p>

<input type="hidden" id="selectedStates" value="${selectedStates}" />

		<p>
		<label class="small"><spring:message code="committeetour.state" text="State"/>*</label>
		<form:select path="state" items="${states}" itemValue="id" itemLabel="name"  class="sSelect" id="state" name="state" />
		<form:errors path="state" cssClass="validationError"/>
	</p>

	
	<!-- district is a simple input field and not a form input field because
		 it is not an attribute of the CommitteeTour instance. -->
<input type="hidden" id="selectedDistricts" value="${selectedDistricts}" />

		<p>
		<label class="small"><spring:message code="committeetour.district" text="District"/>*</label>
		<form:select path="districts" items="${districts}" itemValue="id" itemLabel="name"  multiple="true" size="5" cssClass="sSelect" cssStyle="height:100px;margin-top:5px;"/>
		<form:errors path="districts" cssClass="validationError"/>
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
	<form:input path="venueName" cssClass="sText"/>
	<form:errors path="venueName" cssClass="validationError"/>	
	</p>
	
	<p> 
	<label class="small"><spring:message code="committeetour.subject" text="Subject"/>*</label>
	<form:input path="subject" cssClass="sText"/>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.fromDate" text="From Date"/>*</label>
	<form:input path="fromDate" cssClass="datetimemask sText" />
	<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="small"><spring:message code="committeetour.toDate" text="To Date"/>*</label>
	<form:input path="toDate" cssClass="datetimemask sText" />
	<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	
	<!-- Dynamic Addition of Itinerary -->
	<div>
		<input type="button" id="addItinerary" class="button" value="<spring:message code='committeetour.addItinerary' text='Add Itinerary'></spring:message>">
		<form:errors path="itineraries" cssClass="validationError"></form:errors>
		<c:if test="${not empty itineraries}">
			<c:set var="itineraryCount" value="1"></c:set>
			<c:forEach items="${itineraries}" var="outer">
				<div id="itinerary${itineraryCount}">
					<p>
					<label class="small"><spring:message code="committeetour.touritinerary.date" text="Date"/>*</label>
					<input id="tourItineraryDate${itineraryCount}" name="tourItineraryDate${itineraryCount}" class="datemask sText" value="${outer.formatDate()}">
					</p>
					
					<p>
					<label class="small"><spring:message code="committeetour.touritinerary.fromTime" text="From time"/>*</label>
					<input id="tourItineraryFromTime${itineraryCount}" name="tourItineraryFromTime${itineraryCount}" class="timemask sText" value="${outer.getFromTime()}">
					</p>
					
					<p>
					<label class="small"><spring:message code="committeetour.touritinerary.toTime" text="To time"/>*</label>
					<input id="tourItineraryToTime${itineraryCount}" name="tourItineraryToTime${itineraryCount}" class="timemask sText" value="${outer.getToTime()}">
					</p>
					
					<p>
					<label class="small"><spring:message code="committeetour.touritinerary.details" text="Details"/></label>
					<textarea id="tourItineraryDetails${itineraryCount}" name="tourItineraryDetails${itineraryCount}" rows="2" cols="50">${outer.getDetails()}</textarea>
					</p>
					
					<p>
					<label class="small"><spring:message code="committeetour.touritinerary.stayover" text="Stayover"/></label>
					<textarea id="tourItineraryStayover${itineraryCount}" name="tourItineraryStayover${itineraryCount}" rows="2" cols="50">${outer.getStayOver()}</textarea>
					</p>
					
					<input type='button' id='${itineraryCount}' class='button' value='<spring:message code="committeetour.touritinerary.deleteItinerary" text="Delete Itinerary"></spring:message>' onclick='deleteItinerary(${itineraryCount});'/>
					
					<!-- Hidden variables required for each instance of TourItinerary -->
					<input type='hidden' id='tourItineraryId${itineraryCount}' name='tourItineraryId${itineraryCount}' value="${outer.id}">
					<input type='hidden' id='tourItineraryVersion${itineraryCount}' name='tourItineraryVersion${itineraryCount}' value="${outer.version}">
					<input type='hidden' id='tourItineraryLocale${itineraryCount}' name='tourItineraryLocale${itineraryCount}' value="${domain.locale}">
				</div>
				<c:set var="itineraryCount" value="${itineraryCount + 1}"></c:set>
			</c:forEach>		
		</c:if>
		
		<!-- Hidden Messages to preserve the localization of the field names -->
		<input type="hidden" id="tourItineraryCount" name="tourItineraryCount" value="${tourItineraryCount}"/>
		
		<input type="hidden" id="deleteItineraryMessage" name="deleteItineraryMessage" value="<spring:message code='committeetour.touritinerary.deleteItinerary' text='Delete Itinerary'></spring:message>" disabled="disabled"/>
		
		<input type="hidden" id="tourItineraryDateMessage" name="tourItineraryDateMessage" value="<spring:message code='committeetour.touritinerary.date' text='Date'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="tourItineraryFromTimeMessage" name="tourItineraryFromTimeMessage" value="<spring:message code='committeetour.touritinerary.fromTime' text='From time'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="tourItineraryToTimeMessage" name="tourItineraryToTimeMessage" value="<spring:message code='committeetour.touritinerary.toTime' text='To time'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="tourItineraryDetailsMessage" name="tourItineraryDetailsMessage" value="<spring:message code='committeetour.touritinerary.details' text='Details'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="tourItineraryStayoverMessage" name="tourItineraryStayoverMessage" value="<spring:message code='committeetour.touritinerary.stayover' text='Stayover'></spring:message>" disabled="disabled"/>
	</div>
	
	<!-- Dynamic Addition of number of Reporters as per Language -->
	<div>
		<input type="button" id="addReporter" class="button" value="<spring:message code='committeetour.addReporter' text='Add Reporter'></spring:message>">
		<form:errors path="reporters" cssClass="validationError"></form:errors>
		<c:if test="${not empty reporters}">
			<c:set var="reportersCount" value="1"></c:set>
			<c:forEach items="${reporters}" var="outer">
				<div id="reporter${reportersCount}">
					<p>
					<label class="small"><spring:message code="committeetour.committeereporter.language" text="Language"/>*</label>
					<select name="committeeReporterLanguage${reportersCount}" id="committeeReporterLanguage${reportersCount}" class="sSelect">
						<c:forEach items="${languages}" var="i">
							<c:choose>
								<c:when test="${outer.language.id == i.id}">
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
					<label class="small"><spring:message code="committeetour.committeereporter.noOfReporters" text="No. of Reporters"/>*</label>
					<input id="committeeReporterNoOfReporters${reportersCount}" name="committeeReporterNoOfReporters${reportersCount}" class="sText Integer" value="${outer.noOfReporters}">
					</p>
					
					<input type='button' id='${reportersCount}' class='button' value='<spring:message code="committeetour.committeereporter.deleteReporter" text="Delete Reporter"></spring:message>' onclick='deleteReporter(${reportersCount});'/>
					
					<!-- Hidden variables required for each instance of CommitteeReporter -->
					<input type='hidden' id='committeeReporterId${reportersCount}' name='committeeReporterId${reportersCount}' value="${outer.id}">
					<input type='hidden' id='committeeReporterVersion${reportersCount}' name='committeeReporterVersion${reportersCount}' value="${outer.version}">
					<input type='hidden' id='committeeReporterLocale${reportersCount}' name='committeeReporterLocale${reportersCount}' value="${domain.locale}">
				</div>
				<c:set var="reportersCount" value="${reportersCount + 1}"></c:set>
			</c:forEach>		
		</c:if>
		
		<!-- To be used from Javascript functions when a Reporter is to be
			 added dynamically  -->
		<p style="display:none;">
		<select name="languageMaster" id="languageMaster" class="sSelect" disabled="disabled">
			<c:forEach items="${languages}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
		</select>
		</p>
		
		<!-- Hidden Messages to preserve the localization of the field names -->
		<input type="hidden" id="committeeReporterCount" name="committeeReporterCount" value="${committeeReporterCount}"/>
		
		<input type="hidden" id="deleteCommitteeReporterMessage" name="deleteCommitteeReporterMessage" value="<spring:message code='committeetour.committeereporter.deleteReporter' text='Delete Reporter'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="committeeReporterLanguageMessage" name="committeeReporterLanguageMessage" value="<spring:message code='committeetour.committeereporter.language' text='Language'></spring:message>" disabled="disabled"/>
		<input type="hidden" id="committeeReporterNoOfReportersMessage" name="committeeReporterNoOfReportersMessage" value="<spring:message code='committeetour.committeereporter.noOfReporters' text='No. of Reporters'></spring:message>" disabled="disabled"/>
	</div>
	
	<c:if test="${internalStatus.type eq 'committeetour_created'}">
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
		
		<p>
		<label class="wysiwyglabel"><spring:message code="committee.remarks" text="Remarks"/></label>
		<textarea id="remarks" name="remarks"  class="wysiwyg" rows="2" cols="50">${remarks}</textarea>	
		</p>	
	</c:if>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
		<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		<c:if test="${internalStatus.type eq 'committeetour_incomplete'}">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</c:if>
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<input type="hidden" id="committeeName" name="committeeName" value="${committeeName}"/>
	<input type="hidden" id="workflowInit" name="workflowInit" value="${workflowInit}"/>
	<input type="hidden" id="workflowName" name="workflowName" value="${workflowName}"/>
	<input type="hidden" id="assigneeLevel" name="assigneeLevel" value="${assigneeLevel}"/>
 	<input type="hidden" id="houseTypeId" name="houseTypeId" value="${houseType.id}"/>
	<input type="hidden" id="userGroupId" name="userGroupId" value="${userGroup.id}"/>
	<input id="requestForTourMsg" value="<spring:message code='committeetour.requestForTourMsg' text='Do you want to put up request for tour?'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="pleaseSelect" name="pleaseSelect" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
</form:form>
</div>
</body>
</html>