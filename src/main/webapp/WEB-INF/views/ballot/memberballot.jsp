<%@ include file="/common/taglibs.jsp" %>
<c:choose>
<c:when test="${!(empty memberBallots)}">
<table class="uiTable">
	<tr>
	<th><spring:message code="memberballot.position" text="S.no"/></th>
	<th>	
	<spring:message code="memberballot.member" text="Members"/>
	</th>
	<th>	
	<spring:message code="memberballot.question" text="Question No."/>
	</th>		
	</tr>
	<c:forEach items="${memberBallots}" var="i">
	<tr>
		<td>${i.position}</td>
		<td>${i.member}</td>	
		<td>
		<c:choose>
		<c:when test="${!(empty i.questions)}">
		<c:forEach items="${i.questions}" var="j">
		<span style="margin-right:5px;">${j.number}&nbsp;&nbsp;(${j.answeringDate})</span>
		</c:forEach>
		</c:when>
		<c:otherwise>
		-
		</c:otherwise>
		</c:choose>
		</td>		
	</tr>
	</c:forEach>
</table>
</c:when>
<c:otherwise>
<spring:message code="memberballot.noballot" text="No Member Ballot Found"/>
</c:otherwise>
</c:choose>
