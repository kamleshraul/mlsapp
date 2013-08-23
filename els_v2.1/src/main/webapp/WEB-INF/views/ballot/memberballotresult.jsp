<%@ include file="/common/taglibs.jsp" %>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
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
		<td>${i.member.getFullname()}</td>	
		<td>
		<c:choose>
		<c:when test="${!(empty i.questionChoices) }">
		<c:forEach items="${i.questionChoices}" var="j">
		<span style="margin-right:5px;">${j.question.findFormattedNumber()}&nbsp;&nbsp;(${j.newAnsweringDate.findFormattedAnsweringDate()})</span>
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
