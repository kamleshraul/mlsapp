<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeeName" text="Committee Name"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* IF 'durationInYears' IS SET, THEN MAKE 'durationInMonths' and 'durationInDays' 
	 * 	AS EMPTY & UNEDITABLE.
	 * IF 'durationInMonths' IS SET, THEN MAKE 'durationInYears' and 'durationInDays' 
	 * 	AS EMPTY & UNEDITABLE. 
	 * IF 'durationInDays' IS SET, THEN MAKE 'durationInMonths' and 'durationInYears' 
	 * 	AS EMPTY & UNEDITABLE. 
	 * RESET THE UNEDITABLE MODE BACK TO INPUT MODE WHEN OTHER CONTROL IS SET TO EMPTY.
	 */
	function onDurationInYearsChange() {
		if($('#durationInYears').val().trim() == '') {
			$('#durationInMonths').attr('readOnly', false);
			$('#durationInDays').attr('readOnly', false);
		}
		else {
			$('#durationInDays').val('');
			$('#durationInDays').attr('readOnly', true);
			$('#durationInMonths').val('');
			$('#durationInMonths').attr('readOnly', true);
		}
	}

	function onDurationInMonthsChange() {
		if($('#durationInMonths').val().trim() == '') {
			$('#durationInYears').attr('readOnly', false);
			$('#durationInDays').attr('readOnly', false);
		}
		else {
			$('#durationInYears').val('');
			$('#durationInYears').attr('readOnly', true);
			$('#durationInDays').val('');
			$('#durationInDays').attr('readOnly', true);
		}
	}
	
	function onDurationInDaysChange() {
		if($('#durationInDays').val().trim() == '') {
			$('#durationInMonths').attr('readOnly', false);
			$('#durationInYears').attr('readOnly', false);
		}
		else {
			$('#durationInMonths').val('');
			$('#durationInMonths').attr('readOnly', true);
			$('#durationInYears').val('');
			$('#durationInYears').attr('readOnly', true);
		}
	}
	
	function onHouseTypeChange() {
		setCommitteeTypes();
		hideUnhideHouseSpecificFields();
	}

	/* WHEN houseType CHANGES, SHOW committeeTypes SPECIFIC TO THE houseType */
	function setCommitteeTypes() {
		// Get the currently selected houseType
		var houseTypeId = $('#houseType').val();

		// Make an ajax call to fetch committeeTypes corresponding to houseType
		// Set the committeeTypes attribute as this newly fetched committeeTypes
		var parameters = "houseType=" + houseTypeId;
		var resourceURL = "ref/committeeTypes/houseType/" + houseTypeId;
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#committeeType').empty();
				$('#committeeType').html(text);
			}
			else {
				$('#committeeType').empty();
			}
		});
	}
	
	/* IF houseType == lowerhouse, HIDE THE FIELD 'noOfUpperHouseMembers'
 	 * IF houseType == upperhouse, HIDE THE FIELD 'noOfLowerHouseMembers'
 	 * IF houseType == bothhouse, SHOW THE FIELDS 'noOfLowerHouseMembers'
 	 * 	AND 'noOfUpperHouseMembers'
 	 */
	function hideUnhideHouseSpecificFields() {
		var houseTypeType = getHouseTypeType();
		if(houseTypeType == "lowerhouse") {
			$('#noOfUpperHouseMembers').val('');
			$('.upperHouseFields').hide();
			$('.lowerHouseFields').show();
		}
		else if(houseTypeType == "upperhouse") {
			$('#noOfLowerHouseMembers').val('');
			$('.lowerHouseFields').hide();
			$('.upperHouseFields').show();
		}
		else if(houseTypeType == "bothhouse") {
			$('.lowerHouseFields').show();
			$('.upperHouseFields').show();
		}
	}
	
	function getHouseTypeType() {
		// Read the id from houseType.  
		var houseTypeId = $('#houseType').val();

		// Find the type corresponding to the houseTypeId from houseTypeTypes.
		$('#houseTypeTypes').val(houseTypeId);
		var type = $('#houseTypeTypes option:selected').text().trim();
		
		return type;
	}

	$('document').ready(function(){	
		initControls();
		$('#key').val('');

		onDurationInYearsChange();
		$('#durationInYears').change(function(){
			onDurationInYearsChange();
		});

		onDurationInMonthsChange();
		$('#durationInMonths').change(function(){
			onDurationInMonthsChange();
		});
		
		onDurationInDaysChange();					
		$('#durationInDays').change(function(){
			onDurationInDaysChange();
		}); 

		hideUnhideHouseSpecificFields();
		$('#houseType').change(function(){
			onHouseTypeChange();
		});		 
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committeename" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- houseType is a simple input field and not a form input field because
		 it is not an attribute of the committeename instance. -->
	<p>
		<label class="small"><spring:message code="committeename.houseType" text="House Type" /></label>
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
		<label class="small"><spring:message code="committeename.committeeType" text="Committee Type" />*</label>
		<form:select path="committeeType" items="${committeeTypes}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="committeeType" cssClass="validationError"/>
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.name" text="Name"/>*</label>
		<form:input path="name" cssClass="sText"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.displayName" text="Display Name"/>*</label>
		<form:input path="displayName" cssClass="sText"/>
		<form:errors path="displayName" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committeename.foundationDate" text="Foundation Date"/>*</label>
		<form:input path="foundationDate" cssClass="datemask sText" />
		<form:errors path="foundationDate" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.durationInYears" text="Duration in Years"/></label>
		<form:input path="durationInYears" cssClass="sText integer"/>
		<form:errors path="durationInYears" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.durationInMonths" text="Duration in Months"/></label>
		<form:input path="durationInMonths" cssClass="sText integer"/>
		<form:errors path="durationInMonths" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.durationInDays" text="Duration in Days"/></label>
		<form:input path="durationInDays" cssClass="sText integer"/>
		<form:errors path="durationInDays" cssClass="validationError"/>	
	</p>
	
	<p class="lowerHouseFields"> 
		<label class="small"><spring:message code="committeename.noOfLowerHouseMembers" text="No. of Assembly Members"/></label>
		<form:input path="noOfLowerHouseMembers" cssClass="sText integer"/>
		<form:errors path="noOfLowerHouseMembers" cssClass="validationError"/>	
	</p>
	
	<p class="upperHouseFields"> 
		<label class="small"><spring:message code="committeename.noOfUpperHouseMembers" text="No. of Council Members"/></label>
		<form:input path="noOfUpperHouseMembers" cssClass="sText integer"/>
		<form:errors path="noOfUpperHouseMembers" cssClass="validationError"/>	
	</p>
	
	<p> 
		<label class="small"><spring:message code="committeename.rule" text="Rule"/></label>
		<form:textarea path="rule" rows="2" cols="50"></form:textarea>
		<form:errors path="rule" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committeename.isExpired" text="Is Expired?"/></label>
		<form:checkbox path="isExpired" id="isExpired" cssClass="sCheck"/>
		<form:errors path="isExpired" cssClass="validationError"/>	
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
	
</form:form>
</div>
</body>
</html>