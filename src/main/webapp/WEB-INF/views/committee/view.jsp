<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
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
		<label class="small"><spring:message code="committee.committeeType" text="Committee Type" /></label>
		<input type="text" id="committeeType" name="committeeType" value="${committeeType}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.committeeName" text="Committee Name" /></label>
		<input type="text" id="committeeName" name="committeeName" value="${committeeName.name}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.foundationDate" text="Foundation Date"/></label>
		<input type="text" id="foundationDate" name="foundationDate" value="${foundationDate}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.formationDate" text="Formation Date"/></label>
		<input type="text" id="formationDate" name="formationDate" value="${formationDate}" class="sText" readonly="readonly"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.dissolutionDate" text="Dissolution Date"/></label>
		<input type="text" id="dissolutionDate" name="dissolutionDate" value="${dissolutionDate}" class="sText" readonly="readonly"/>
			
	</p>
	
	<!-- Table displaying members -->
	<c:if test="${not empty committeeMembers}">
		<label class="small"><spring:message code="committee.members" text="Committee Members"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="general.srnumber" text="Sr.No."/></th>
				<th><spring:message code="committee.member" text="Member"/></th>
				<th><spring:message code="committee.memberDesignation" text="Designation"/></th>
			
			</tr>
		
			<c:forEach var="i" begin="1" end="${committeeName.noOfLowerHouseMembers+committeeName.noOfUpperHouseMembers}">
			 <c:set var="counter" value="0" />   
			<c:forEach items="${committeeMembers}" var="committeeMember" varStatus="loopCounter">
			 <c:choose>
			
			 <c:when test="${i eq committeeMember.position}">
			  <c:set var="counter" value="1" /> 
				<tr>
					<td>${i}</td>	
					<td>
						<c:choose>
							<c:when test="${committeeMember.member.getAlias() != ''}">
							 ${committeeMember.member.title.name} ${committeeMember.member.getAlias()},
							 	<c:choose>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<spring:message code="committeemember.type.lowerhouse" text="Member LowerHouse"/>
								</c:when>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<spring:message code="committeemember.type.upperhouse" text="Member UpperHouse"/>
								</c:when>
									<c:otherwise>
								<spring:message code="committeemember.type.bothhouse" text="Member BothHouse"/>
								</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
							${committeeMember.member.title.name} ${committeeMember.member.firstName} ${committeeMember.member.lastName},
								<c:choose>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[lowerhouse]'}">
								<spring:message code="committeemember.type.lowerhouse" text="Member LowerHouse"/>
								</c:when>
								<c:when test="${committeeMember.member.findCurrentHouseType() == '[upperhouse]'}">
								<spring:message code="committeemember.type.upperhouse" text="Member UpperHouse"/>
								</c:when>
									<c:otherwise>
								<spring:message code="committeemember.type.bothhouse" text="Member BothHouse"/>
								</c:otherwise>
								</c:choose>
									
							</c:otherwise>
						</c:choose>
							
					
					</td>
					<td>${committeeMember.designation.name}</td>
				 
				</tr>
			 </c:when>
 
      </c:choose>
			</c:forEach>
			<c:if test="${counter eq 0}">
			<tr>
					<td>${i}</td>	
					<td><spring:message code="committeemember.vacant" text="Vacant"/></td>
					<td><spring:message code="committeemember.vacant" text="Vacant"/></td>
				 
				</tr>
			 </c:if>
			</c:forEach>
		</table>
	</c:if>
	
	<!-- Table displaying invited members -->
	<c:if test="${not empty invitedMembers}">
		<label class="small"><spring:message code="committee.invitedMembers" text="Invited Members"/></label>
		<table class="uiTable" border="1">
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
		</table>
	</c:if>

</form>
</div>
</body>
</html>