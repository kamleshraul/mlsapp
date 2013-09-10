<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function onPageLoad() {
		conditionalReadOnlyRendering();
	}

	function conditionalReadOnlyRendering() {
		var isRenderAsReadOnly = $('#renderAsReadOnly').val();
		if(isRenderAsReadOnly == true) {
			// Render all the visible attributes on the page as readOnly
			$('#remarks').attr('readOnly', true);
		}
	}

	$('document').ready(function(){	
		onPageLoad();
	});
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="workflow/committee/memberAddition" method="PUT" modelAttribute="committeeCompositeVO">
	<%@ include file="/common/info.jsp" %>
	
	<h2>
		<c:choose>
			<c:when test="${workflowSubType eq 'PARLIAMENTARY_MINISTER'}">
				<spring:message code="committee.requestToParliamentaryMinister" text="Request to Parliamentary Minister"/>
			</c:when>
			<c:when test="${workflowSubType eq 'LEADER_OF_OPPOSITION'}">
				<spring:message code="committee.requestToLeaderOfOpposition" text="Request to Leader of Opposition"/>
			</c:when>
		</c:choose>
	</h2>
	
	<c:set var="noOfRulingParties" value="${fn:length(committeeCompositeVO.rulingParties)}"></c:set>
	<c:set var="noOfOppositionParties" value="${fn:length(committeeCompositeVO.oppositionParties)}"></c:set>
	
	<table class="uiTable" border="1">
		<tr>
			<th rowspan="2"><spring:message code="committee.committees" text="Committees"/></th>
			<th rowspan="2"><spring:message code="committee.maximumMembers" text="Maximum Members"/></th>
			<!-- 1 is added to incorporate a "Total" column -->
			<th colspan="${noOfRulingParties + 1}"><spring:message code="committee.rulingParty" text="Ruling Party"/></th>
			<!-- 1 is added to incorporate a "Total" column -->
			<th colspan="${noOfOppositionParties + 1}"><spring:message code="committee.oppositionParty" text="Opposition Party"/></th>
		</tr>
		
		<tr>
			<c:forEach items="${committeeCompositeVO.rulingParties}" var="i">
				<th>${i.name}</th>
			</c:forEach>
			<th><spring:message code="generic.total" text="Total"/></th>
			
			<c:forEach items="${committeeCompositeVO.oppositionParties}" var="i">
				<th>${i.name}</th>
			</c:forEach>
			<th><spring:message code="generic.total" text="Total"/></th>
		</tr>
		
		<c:set var="committeeCounter" value="1"></c:set>
		<c:forEach items="${committeeCompositeVO.committeeVOs}" var="committeeVO">
			<tr>
				<td>${committeeVO.committeeDisplayName}</td>
				
				<td>${committeeVO.maxCommitteeMembers}</td>
				
				<c:set var="rulingPartyCounter" value="0"></c:set>
				<c:forEach items="${committeeVO.rulingParties}" var="rulingPartyVO">
					<td>${rulingPartyVO.noOfMembers}</td>
					<c:set var="rulingPartyCounter" value="${rulingPartyCounter + 1}"></c:set>
				</c:forEach>
				<c:if test="${rulingPartyCounter < noOfRulingParties}">
					<c:forEach var="i" begin="${rulingPartyCounter + 1}" end="${noOfRulingParties}">
						<td>-</td>
					</c:forEach>
				</c:if>
				
				<td>${committeeVO.rulingPartyCommitteeMembersCount}</td>
				
				<c:set var="oppositionPartyCounter" value="0"></c:set>
				<c:forEach items="${committeeVO.oppositionParties}" var="oppositionPartyVO">
					<td>${oppositionPartyVO.noOfMembers}</td>
					<c:set var="oppositionPartyCounter" value="${oppositionPartyCounter + 1}"></c:set>
				</c:forEach>
				<c:if test="${oppositionPartyCounter < noOfOppositionParties}">
					<c:forEach var="i" begin="${oppositionPartyCounter + 1}" end="${noOfOppositionParties}">
						<td>-</td>
					</c:forEach>
				</c:if>
				
				<td>${committeeVO.oppositionPartyCommitteeMembersCount}</td>
			</tr>
			
			<!-- Hidden input field that stores the id of the committee. This will be used in the
				 Controller to determine the committees being processed -->
			<input type="hidden" id="committeeId${committeeCounter}" name="committeeId${committeeCounter}" value="${committeeVO.committeeId}"/>
			<c:set var="committeeCounter" value="${committeeCounter + 1}"></c:set>
		</c:forEach>
	</table>
	
	<p></p>
	
	<p>
		<label class="small"><spring:message code="committee.putUpFor" text="Put Up For" /></label>
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
		<label class="small"><spring:message code="committee.nextactor" text="Next Actor"/></label>
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
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<c:if test="${renderAsReadOnly ne true}">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</c:if>
		</p>
	</div>
	
	<!-- Hidden fields  -->
	<input type="hidden" id="workflowInit" name="workflowInit" value="${workflowInit}"/>
	<input type="hidden" id="workflowName" name="workflowName" value="${workflowName}"/>
	<input type="hidden" id="renderAsReadOnly" name="renderAsReadOnly" value="${renderAsReadOnly}"/>
</form:form>
</div>
</body>
</html>