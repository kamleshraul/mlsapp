<%@ include file="/common/taglibs.jsp" %>
	<style type="text/css">
	.true{
	font-size: 14px;
	}
	.false{
	font-size: 14px;
	}
	.round1{
	color:green ;
	}
	.round2{
	color:blue ;
	}
	.round3{
	color: red;
	}
	.round4{
	color: black;
	}
	.round5{
	color: lime;
	}
	</style>
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
		<c:choose>
		<c:when test="${!(empty j.parentNumber) }">
		<div style="margin-right:5px;" class="${i.attendance} round${i.round}">${j.number}&nbsp;&nbsp;(${j.answeringDate})(<spring:message code="memberballot.clubbedto" text="Clubbed To"></spring:message>-${j.parentNumber})</div>
		</c:when>
		<c:otherwise>
		<div style="margin-right:5px;" class="${i.attendance} round${i.round}">${j.number}&nbsp;&nbsp;(${j.answeringDate})</div>
		</c:otherwise>
		</c:choose>
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

