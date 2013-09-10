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
		<input type="text" id="committeeName" name="committeeName" value="${committeeName}" class="sText" readonly="readonly"/>
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
				<th><spring:message code="committee.member" text="Member"/></th>
				<th><spring:message code="committee.memberDesignation" text="Designation"/></th>
			</tr>
			<tr>
				<c:forEach items="${committeeMembers}" var="committeeMember">
					<td>${committeeMember.member.fullname}</td>
					<td>${committeeMember.designation.name}</td>
				</c:forEach>
			</tr>
		</table>
	</c:if>
	
	<!-- Table displaying invited members -->
	<c:if test="${not empty invitedMembers}">
		<label class="small"><spring:message code="committee.invitedMembers" text="Invited Members"/></label>
		<table class="uiTable" border="1">
			<tr>
				<th><spring:message code="committee.invitedMember" text="Invited Member"/></th>
			</tr>
			<tr>
				<c:forEach items="${invitedMembers}" var="invitedMember">
					<td>${invitedMember.member.fullname}</td>
				</c:forEach>
			</tr>
		</table>
	</c:if>

</form>
</div>
</body>
</html>