<%@ include file="/common/taglibs.jsp" %>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${!(empty ballots)}">
<table class="uiTable">
	<tr>
	<th><spring:message code="ballot.position" text="S.no"/></th>
	<th>	
	<spring:message code="ballot.member" text="Members"/>
	</th>		
	</tr>
	<c:forEach items="${ballots}" var="i">	
	<tr>
		<td>${i.id}</td>
		<td>${i.name}</td>			
	</tr>
	</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="ballot.noballot" text="No Ballot Found"/>
</c:otherwise>
</c:choose>

