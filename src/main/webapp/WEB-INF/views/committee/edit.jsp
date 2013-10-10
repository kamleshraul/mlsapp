<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
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
			$.get(resourceURL, function(data){
				$('#dissolutionDate').val(data.name);
			}); 
		}
	}
	
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
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committee" method="PUT" modelAttribute="domain">
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

	<!-- Table displaying members -->
	<c:if test="${status.type eq 'committee_membersAdded' and not empty committeeMembers}">
		<label class="small"><spring:message code="committee.members" text="Committee Members"/></label>
		<table class="uiTable" border="1">
			<tbody>
				<tr>
					<th><spring:message code="committee.member" text="Member"/></th>
					<th><spring:message code="committee.memberDesignation" text="Designation"/></th>
				</tr>
				<c:forEach items="${committeeMembers}" var="committeeMember">
					<tr>
					
						<td>${committeeMember.member.fullname}</td>
						<td>${committeeMember.designation.name}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	
	<!-- Table displaying invited members -->
	<c:if test="${status.type eq 'committee_invitedMembersAdded' and not empty invitedMembers}">
		<label class="small"><spring:message code="committee.invitedMembers" text="Invited Members"/></label>
		<table class="uiTable" border="1">
			<tbody>
				<tr>
					<th><spring:message code="committee.invitedMember" text="Invited Member"/></th>
				</tr>
				<c:forEach items="${invitedMembers}" var="invitedMember">
					<tr>
						<td>${invitedMember.member.fullname}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	
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