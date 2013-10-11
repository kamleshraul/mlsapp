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
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committeetour" method="PUT" modelAttribute="domain">
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