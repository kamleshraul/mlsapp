<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour" text="Committee Tour"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function onStateChange(stateId) {
		var resourceURL = "ref/state/" + stateId + "/districts";
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#district').empty();
				$('#district').html(text);

				// Trigger District change, so that towns corresponding to the district will be set
				var districtId = data[0].id;
				onDistrictChange(districtId);
			}
			else {
				$('#district').empty();
			}
		});
	}

	function onDistrictChange(districtId) {
		var resourceURL = "ref/district/" + districtId + "/towns";
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#town').empty();
				$('#town').html(text);
			}
			else {
				$('#town').empty();
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
  		  	"<label class='small'>" + $('#tourItineraryDateMessage').val() + "</label>" +
  		  	"<input name='tourItineraryDate" + tourItineraryCount + "' id='tourItineraryDate" + tourItineraryCount + "' class='datemask sText'>" +
  		    "</p>" +
  		 	"<p>" +
		  	"<label class='small'>" + $('#tourItineraryFromTimeMessage').val() + "</label>" +
		  	"<input name='tourItineraryFromTime" + tourItineraryCount + "' id='tourItineraryFromTime" + tourItineraryCount + "' class='sText'>" +
		  	"</p>" +
		  	"<p>" +
  		  	"<label class='small'>" + $('#tourItineraryToTimeMessage').val() + "</label>" +
  		    "<input name='tourItineraryToTime" + tourItineraryCount + "' id='tourItineraryToTime" + tourItineraryCount + "' class='sText'>" +
  		  	"</p>" +
  		 	"<p>" +
		  	"<label class='wysiwyglabel'>" + $('#tourItineraryDetailsMessage').val() + "</label>" +
		  	"<input name='tourItineraryDetails" + tourItineraryCount + "' id='tourItineraryDetails" + tourItineraryCount + "' class='wysiwyg'>" +
		    "</p>" +
		 	"<p>" +
		  	"<label class='wysiwyglabel'>" + $('#tourItineraryStayoverMessage').val() + "</label>" +
		  	"<input name='tourItineraryStayover" + tourItineraryCount + "' id='tourItineraryStayover" + tourItineraryCount + "' class='wysiwyg'>" +
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
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});
	}

	function deleteItinerary(id) {
		var tourItineraryId = $('#tourItineraryId' + id).val();
		if(tourItineraryId != ''){			
	    	$.delete_('member/minister/department/'+memberDepartmentId+'/delete', 
	    	    null, 
	    	    function(data, textStatus, XMLHttpRequest) {
	    			$('#memberDepartment'+id).remove();
	    			totalMemberDepartmentCount=totalMemberDepartmentCount-1;
					if(id==memberDepartmentCount){
						memberDepartmentCount=memberDepartmentCount-1;
					}
	    		}
    		).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		} else {
			$('#memberDepartment'+id).remove();
			totalMemberDepartmentCount = totalMemberDepartmentCount - 1;
			if(id == memberDepartmentCount){
				memberDepartmentCount = memberDepartmentCount - 1;
			}
		}	
	}
	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');

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
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committeetour" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- state is a simple input field and not a form input field because
		 it is not an attribute of the CommitteeTour instance. -->
	<p>
		<label class="small"><spring:message code="committeetour.state" text="State" />*</label>
		<select class="sSelect" id="state">
			<c:forEach items="${states}" var="i">
				<c:choose>
					<c:when test="${state.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	
	<!-- district is a simple input field and not a form input field because
		 it is not an attribute of the CommitteeTour instance. -->
	<p>
		<label class="small"><spring:message code="committeetour.district" text="District" />*</label>
		<select class="sSelect" id="district">
			<c:forEach items="${districts}" var="i">
				<c:choose>
					<c:when test="${district.id == i.id}">
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
		<label class="small"><spring:message code="committeetour.town" text="Town" />*</label>
		<form:select path="town" items="${towns}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="town" cssClass="validationError"/>
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeetour.venueName" text="Venue Name"/>*</label>
		<form:input path="venueName" cssClass="sText"/>
		<form:errors path="venueName" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committeetour.fromDate" text="From Date"/>*</label>
		<form:input path="fromDate" cssClass="datemask sText" />
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committeetour.toDate" text="To Date"/>*</label>
		<form:input path="toDate" cssClass="datemask sText" />
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	
	<!-- Dynamic Addition of Itinerary -->
	<div>
		<input type="button" id="addItinerary" class="button" value="<spring:message code='committeetour.addItinerary' text='Add Itinerary'></spring:message>">
		<form:errors path="itineraries" cssClass="validationError"></form:errors>
		<c:if test="${not empty itineraries}">
			<c:set var="count" value="1"></c:set>
			<c:forEach items="${itineraries}" var="outer">
				<div id="itinerary${count}">
					<p>
						<label class="small"><spring:message code="committeetour.touritinerary.date" text="Date"/>*</label>
						<input id="tourItineraryDate${count}" name="tourItineraryDate${count}" class="datemask sText" value="${outer.formatDate()}">
					</p>
					
					<p>
						<label class="small"><spring:message code="committeetour.touritinerary.fromTime" text="From time"/>*</label>
						<input id="tourItineraryFromTime${count}" name="tourItineraryFromTime${count}" class="sText" value="${outer.getFromTime()}">
					</p>
					
					<p>
						<label class="small"><spring:message code="committeetour.touritinerary.toTime" text="To time"/>*</label>
						<input id="tourItineraryToTime${count}" name="tourItineraryToTime${count}" class="sText" value="${outer.getToTime()}">
					</p>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="committeetour.touritinerary.details" text="Details"/></label>
						<textarea id="tourItineraryDetails${count}" name="tourItineraryDetails${count}"  class="wysiwyg" rows="2" cols="50">${outer.getDetails()}</textarea>
					</p>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="committeetour.touritinerary.stayover" text="Stayover"/></label>
						<textarea id="tourItineraryStayover${count}" name="tourItineraryStayover${count}"  class="wysiwyg" rows="2" cols="50">${outer.getStayOver()}</textarea>
					</p>
					
					<input type='button' id='${count}' class='button' value='<spring:message code="committeetour.touritinerary.deleteItinerary" text="Delete Itinerary"></spring:message>' onclick='deleteItinerary(${count});'/>
					
					<!-- Hidden variables required for each instance of TourItinerary -->
					<input type='hidden' id='tourItineraryId${count}' name='tourItineraryId${count}' value="${outer.id}">
					<input type='hidden' id='tourItineraryVersion${count}' name='tourItineraryVersion${count}' value="${outer.version}">
					<input type='hidden' id='tourItineraryLocale${count}' name='tourItineraryLocale${count}' value="${domain.locale}">
				</div>
				<c:set var="count" value="${count + 1}"></c:set>
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
	
	<p>
		<label class="wysiwyglabel"><spring:message code="committeetour.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
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