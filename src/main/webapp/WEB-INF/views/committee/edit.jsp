<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	


	
	function listbox_move(listID, direction) {

		var listbox = document.getElementById(listID);
		var selIndex = listbox.selectedIndex;

		if(-1 == selIndex) {
			alert("Please select an option to move.");
			return;
		}

		var increment = -1;
		if(direction == 'up')
			increment = -1;
		else
			increment = 1;

		if((selIndex + increment) < 0 ||
			(selIndex + increment) > (listbox.options.length-1)) {
			return;
		}

		var selValue = listbox.options[selIndex].value;
		var selText = listbox.options[selIndex].text;
		listbox.options[selIndex].value = listbox.options[selIndex + increment].value
		listbox.options[selIndex].text = listbox.options[selIndex + increment].text

		listbox.options[selIndex + increment].value = selValue;
		listbox.options[selIndex + increment].text = selText;

		listbox.selectedIndex = selIndex + increment;
	}

	/* WHEN committeeType CHANGES, SHOW committeeNames SPECIFIC TO THE committeeType */
	function onCommitteeTypeChange(committeeTypeId) {
		// Make an ajax call to fetch committeeNames corresponding to committeeType
		// Set the committeeNames as this newly fetched committeeNames
		var resourceURL = "ref/committeeNames/committeeType/" + committeeTypeId;
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#committeeName').empty();
				$('#committeeName').html(text);

				// Trigger Committee Name change, so that appropriate foundationDate will be set
				var committeeNameId = data[0].id;
				onCommitteeNameChange(committeeNameId);
			}
			else {
				$('#committeeName').empty();
			}
		});
	}
	
	/* WHEN committeeName IS SELECTED, IT'S CORRESPONDING foundationDate SHOULD BE SET */
	function onCommitteeNameChange(committeeNameId) {
		// Make an ajax call to fetch foundationDate corresponding to the selected
		// committeeName
		var resourceURL = "ref/committeeName/" + committeeNameId + "/foundationDate";
		$.get(resourceURL, function(data){
			$('#foundationDate').val(data.name);
		});
	}

	function onFormationDateChange() {
		var varFormationDate = $('#formationDate').val();
		if(varFormationDate != null || varFormationDate != '') {
			// Make an ajax call to get the dissolutionDate based on committeeName
			// and formationDate
			var committeeNameParam = "committeeName=" + $('#committeeName').val();
			var formationDateParam = "formationDate=" + varFormationDate;
			var parameters = committeeNameParam + "&" + formationDateParam;
			var resourceURL = "ref/committee/dissolutionDate" + "?" + parameters;

			// TO DO Instead of programming for success attribute, program for complete attribute.
			$.ajax({
				url: resourceURL,
				success: function(data){
						$('#dissolutionDate').val(data.name);
					},
				async: false
			});
		}
	}

	/**** submit function ****/
	$("#submit").click(function(){
		
		var items=new Array();
		$("#committeeMembers option").each(function(){
			items.push($(this).val());
		});
		
		$('#allItems').val(items);
		
	
});
	
	$('document').ready(function(){	
		$('#committeeType').change(function(){
			var committeeTypeId = $('#committeeType').val();
			onCommitteeTypeChange(committeeTypeId);
		});

		$('#committeeName').change(function(){
			var committeeNameId = $('#committeeName').val();
			onCommitteeNameChange(committeeNameId);
		});

		$('#formationDate').change(function(){
			onFormationDateChange();
		});	

		$("form").submit(function(event){
			onFormationDateChange();
		}); 
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committee" method="put" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- committeeType is a simple input field and not a form input field because
		 it is not an attribute of the committee instance. -->
	<p>
		<label class="small"><spring:message code="committee.committeeType" text="Committee Type" /></label>
		<select id="committeeType" name="committeeType" class="sSelect">
			<c:forEach items="${committeeTypes}" var="i">
				<c:choose>
					<c:when test="${committeeType.id == i.id}">
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
		<label class="small"><spring:message code="committee.committeeName" text="Committee Name" />*</label>
		<form:select path="committeeName" items="${committeeNames}" itemLabel="displayName" itemValue="id" cssClass="sSelect"></form:select>
		<form:errors path="committeeName" cssClass="validationError"/>										
	</p>
	
	<!-- foundationDate is a simple input field and not a form input field because
		 it is not an attribute of the committee instance. -->
	<p>
		<label class="small"><spring:message code="committee.foundationDate" text="Foundation Date"/></label>
		<input type="text" id="foundationDate" name="foundationDate" value="${foundationDate}" class="datemask sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.formationDate" text="Formation Date"/>*</label>
		<form:input path="formationDate" cssClass="datemask sText" />
		<form:errors path="formationDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.dissolutionDate" text="Dissolution Date"/>*</label>
		<form:input path="dissolutionDate" cssClass="datemask sText" />
		<form:errors path="dissolutionDate" cssClass="validationError"/>	
	</p>
	

	

	
			<table class="uiTable" border="1">
			<tbody>
				<tr>
					<label class="small"><spring:message code="committee.members" text="Committee Members"/></label>
				</tr>
				<tr>
				<td>
						<!-- List displaying members -->
	<c:if test="${not empty committeeMembers}">
			
		<select id="committeeMembers" multiple="multiple" style="height:500px;max-width:275px;min-width:275px;font-size: 16px;">
			
			<c:forEach var="i" begin="1" end="${committeeName.noOfLowerHouseMembers+committeeName.noOfUpperHouseMembers}">
			 <c:set var="counter" value="0" />   
			<c:forEach items="${committeeMembers}" var="committeeMember" varStatus="loopCounter">
			 <c:choose>
			
			 <c:when test="${i eq committeeMember.position}">
			  <c:set var="counter" value="1" /> 
				
						<c:choose>
							<c:when test="${committeeMember.member.getAliasEnabled()==true && committeeMember.member.getAlias()!=''}">
							
							 	<c:choose>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.getAlias()} ${committeeMember.designation.name}"></c:out></option>
								</c:when>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.getAlias()} ${committeeMember.designation.name}"></c:out></option>
								</c:when>
								<c:otherwise>
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.getAlias()} ${committeeMember.designation.name}"></c:out></option>
								</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
							
							 	<c:choose>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.firstName} ${committeeMember.member.lastName},${committeeMember.designation.name}"></c:out></option>
								</c:when>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.firstName} ${committeeMember.member.lastName},${committeeMember.designation.name}"></c:out></option>
								</c:when>
								<c:otherwise>
								<option value="${committeeMember.id}" ><c:out value=" ${committeeMember.member.title.name} ${committeeMember.member.firstName} ${committeeMember.member.lastName},${committeeMember.designation.name}"></c:out></option>
								</c:otherwise>
								</c:choose>
									
							</c:otherwise>
						</c:choose>
							
					
				
					
				 
			
			 </c:when>
 
      </c:choose>
			</c:forEach>
			<c:if test="${counter eq 0}">
				<option value="0" ><c:out value="${committeeMember.member.title.name}"><spring:message code="committeemember.vacant" text="Vacant"/></c:out></option>
			</c:if>
			</c:forEach>
			</select>
		
	</c:if>
				</td>
				<td>
				<input type="button" id="up" value="UP" onclick="listbox_move('committeeMembers', 'up')" style="height:50px;max-width:50px;min-width:50px;font-size: 16px;"/>
<input type="button" id="down" value="DOWN" onclick="listbox_move('committeeMembers', 'down')"  style="height:50px;max-width:60px;min-width:50px;font-size: 16px;"/>
				</td>
				</tr>
				</tbody>
				</table>


	<!-- Table displaying invited members -->
	<c:if test="${not empty invitedMembers}">
		<label class="small"><spring:message code="committee.invitedMembers" text="Invited Members"/></label>
		<table class="uiTable" border="1">
			<tbody>
				<tr>
					<th><spring:message code="committee.invitedMember" text="Invited Member"/></th>
					
				</tr>
				<c:forEach items="${invitedMembers}" var="invitedMember">
					<tr>
						<td>
						<c:choose>
							<c:when test="${invitedMember.member.getAlias() != ''}">
							 ${invitedMember.member.title.name} ${invitedMember.member.getAlias()},
							 <c:choose>
								<c:when test="${invitedMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<spring:message code="committeemember.type.lowerhouse" text="Member LowerHouse"/>
								</c:when>
								<c:when test="${invitedMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<spring:message code="committeemember.type.upperhouse" text="Member UpperHouse"/>
								</c:when>
									<c:otherwise>
								<spring:message code="committeemember.type.bothhouse" text="Member BothHouse"/>
								</c:otherwise>
								</c:choose> 
							</c:when>
							<c:otherwise>
							${invitedMember.member.title.name} ${invitedMember.member.firstName} ${invitedMember.member.lastName},
							 <c:choose>
								<c:when test="${invitedMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<spring:message code="committeemember.type.lowerhouse" text="Member LowerHouse"/>
								</c:when>
								<c:when test="${invitedMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<spring:message code="committeemember.type.upperhouse" text="Member UpperHouse"/>
								</c:when>
									<c:otherwise>
								<spring:message code="committeemember.type.bothhouse" text="Member BothHouse"/>
								</c:otherwise>
								</c:choose> 
							</c:otherwise>
						</c:choose>
						
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
		<form:select style="display:none;" path="members"  items="${committeeMembers}" itemValue="id" itemLabel="member.firstName"/>
		<form:select style="display:none;" path="invitedMembers"  items="${invitedMembers}" itemValue="id" itemLabel="member.firstName"/>
			<input type="hidden" name="status" id="status" value="${domain.status.id}">
			<input type="hidden" name="allItems" id="allItems" value="">
			
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