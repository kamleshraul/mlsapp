<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="houseparty" text="House Party"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function getHouseTypeType() {
		// Read the id from houseType.  
		var houseTypeId = $('#houseType').val();

		// Find the type corresponding to the houseTypeId from houseTypeTypes.
		$('#houseTypeTypes').val(houseTypeId);
		var type = $('#houseTypeTypes option:selected').text().trim();
		
		return type;
	}
	
	function onHouseTypeChange(houseTypeType) {
		var resourceURL = "ref/houses/" + houseTypeType;
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#house').empty();
				$('#house').html(text);
			}
			else {
				$('#house').empty();
			}
		});
	}
	
	function onToRightClick() {
		$('#allParties option:selected').each(function(){
			var opt = "<option value='" + $(this).val() + "'>" + $(this).text() + "</option>";
			$('#parties').append(opt);
			$("#allParties option[value='"+ $(this).val() + "']").remove();
		});
	}

	function onToLeftClick() {
		var nonSelectedParties = "";

		// Remove the selected options from parties
		$('#parties option').each(function(){
			if($(this).attr('selected') == "selected") {
				$("#parties option[value='" + $(this).val() + "']").remove();
			}
			else {
				nonSelectedParties = nonSelectedParties + $(this).val() + ",";
			}
		});
		
		// Set allParties from partiesMaster
		$('#allParties').empty();
		$('#allParties').html($('#partiesMaster').html());

		// Remove nonSelectedParties from allParties
		var arrNonSelectedParties = nonSelectedParties.split(",");
		var length = arrNonSelectedParties.length - 1;
		for(var i = 0; i < length; i++) {
			$("#allParties option[value='" + arrNonSelectedParties[i] + "']").remove();
		}
	}

	function onAllToRightClick() {
		$('#parties').append($('#allParties').html());
		$('#allParties').empty();
	}

	function onAllToLeftClick() {
		$('#allParties').empty();
		$('#allParties').html($('#partiesMaster').html());	
		$('#parties').empty();
	}

	function onUpClick() {
		$('#parties option:selected').each(function(){
			var current = $(this);
			var currentIndex = parseInt(current.index());
			if(currentIndex != 0) {
				var previous = current.prev();
				if(previous.attr('selected') != "selected") {
					// Swap current with previous
					var currentValue = current.val();
					var currentText = current.text();
					var previousValue = previous.val();
					var previousText = previous.text();

					current.val(previousValue);
					current.text(previousText);
					previous.val(currentValue);
					previous.text(currentText);

					// Set & unset the 'selected' attribute
					current.removeAttr("selected");
					previous.attr("selected", "selected");
				}
			}
		});
	}
 
	function onDownClick() {
		var last = $('#parties option').last();
		var lastIndex = parseInt(last.index());
		
		var arrParties = new Array();	
		$("#parties option:selected").each(function(){
			arrParties.push($(this));
		});
		var length = arrParties.length - 1;
		
		for(var i = length; i >= 0; i--) {
			var current = arrParties[i];
			var currentIndex = parseInt(current.index());

			if(currentIndex < lastIndex) {
				var next = current.next();
				if(next.attr('selected') != "selected") {
					// Swap current with previous
					var currentValue = current.val();
					var currentText = current.text();
					var nextValue = next.val();
					var nextText = next.text();

					current.val(nextValue);
					current.text(nextText);
					next.val(currentValue);
					next.text(currentText);

					// Set & unset the 'selected' attribute
					current.removeAttr("selected");
					next.attr("selected", "selected");
				}
			}
		}
	}

	function beforeSubmission() {
		$('#parties option').each(function(){
			var current = $(this);
			current.removeAttr("selected");
			current.attr("selected", "selected");
		});
	}
	
	$(document).ready(function(){
		$('#houseType').change(function(){
			var houseTypeType = getHouseTypeType();
			onHouseTypeChange(houseTypeType);
		});
		
		$('#toRight').click(function(){
			onToRightClick();
		});

		$('#toLeft').click(function(){
			onToLeftClick();
		});

		$('#allToRight').click(function(){
			onAllToRightClick();
		});

		$('#allToLeft').click(function(){
			onAllToLeftClick();
		});	

		$('#up').click(function(){
			onUpClick();
		});

		$('#down').click(function(){
			onDownClick();
		});

		$('#housePartyForm').submit(function(){
			beforeSubmission();
		});	
	});
	</script>
	
	<style type="text/css">
		input[type=button]{
			width:30px;
			margin: 5px;
			font-size: 13px; 
			padding: 5px; 		
		}	
	</style>
</head>
<body>
<div class="fields clearfix">
<form:form id="housePartyForm" action="houseparty" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	<form:errors path="parties" cssClass="validationError"/>
	
	<!-- houseType is a simple input field and not a form input field because
		 it is not an attribute of the committee instance. -->
	<p>
		<label class="small"><spring:message code="houseparty.houseType" text="House Type" /></label>
		<select class="sSelect" id="houseType">
			<c:forEach items="${houseTypes}" var="i">
				<c:choose>
					<c:when test="${houseType.id == i.id}">
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
		<label class="small"><spring:message code="houseparty.house" text="House" />*</label>
		<form:select path="house" items="${houses}" itemLabel="displayName" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="house" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="houseparty.partytype" text="Party Type" />*</label>
		<form:select path="partyType" items="${partyTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="partyType" cssClass="validationError"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="houseparty.fromDate" text="From Date"/>*</label>
		<form:input path="fromDate" cssClass="datemask sText" />
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="houseparty.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="datemask sText" />
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>
	
	<label class="small"><spring:message code="houseparty.parties" text="Parties"/>*</label>
	<p></p>
	<table>
		<tbody>
			<tr>
				<th><spring:message code="houseparty.listOfParties" text="List of Parties"/></th>
				<th></th>
				<th><spring:message code="houseparty.selectedParties" text="Selected Parties"/></th>
				<th></th>
			</tr>
			
			<tr>
				<td>
					<select id="allParties" name="allParties" multiple="multiple" style="height:300px; width:350px;">
						<c:forEach items="${allParties}" var="i">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>
					</select>
				</td>
				<td>
					<input id="toRight" value="&gt;" type="button">
					<input id="allToRight" value="&gt;&gt;" type="button">
					<br>
					<input id="allToLeft"  value="&lt;&lt;" type="button">
					<input id="toLeft"  value="&lt;" type="button">
				</td>
				<td>
					<select id="parties" name="parties" multiple="multiple" style="height:300px; width:350px;">
						<c:forEach items="${parties}" var="i">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>
					</select>
				</td>
				<td>
					<input id="up"  value="&#x2191;" type="button">
					<br>
					<input id="down" value="&#x2193;" type="button">
				</td>
			</tr>
		</tbody>
	</table>
	
	<p> 
		<label class="small"><spring:message code="houseparty.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" rows="2" cols="50"></form:textarea>
		<form:errors path="remarks" cssClass="validationError"/>	
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
	
	<!-- Hidden fields that aid in Client side actions performed in Javascript -->
	<p style="display:none;">
		<select id="houseTypeTypes" class="sSelect">
			<c:forEach items="${houseTypes}" var="i">
				<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
			</c:forEach>
		</select>
	</p>
	
	<p style="display:none;">
		<select id="partiesMaster" class="sSelect">
			<c:forEach items="${partiesMaster}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
		</select>
	</p>
	
</form:form>
</div>
</body>
</html>