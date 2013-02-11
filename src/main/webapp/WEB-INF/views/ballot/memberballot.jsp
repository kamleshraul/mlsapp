<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	$("#with").click(function(){
		$(".question").show();
	});
	$("#without").click(function(){
		$(".question").hide();
	});
	$(".question").hide();
});
</script>
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
	color: #F26522;
	}
	.withQuestion,.withoutQuestion{
	font-size: 14px;
	font-weight: bolder;
	margin-right: 20px;
	cursor: pointer;
	}
	.withQuestion:HOVER{
		color:blue;
		font-size: 16px;
	}
	.withoutQuestion:HOVER{
		color:blue;
		font-size: 16px;
	}
	</style>
<c:choose>
<c:when test="${!(empty memberBallots)}">
<h4><span class="withQuestion"><a id="with">+</a></span><span class="withoutQuestion"><a id="without">-</a></span></h4>
<table class="uiTable">
	<tr>
	<th><spring:message code="memberballot.position" text="S.no"/></th>
	<th>	
	<spring:message code="memberballot.member" text="Members"/>
	</th>
	<th class="question">	
	<spring:message code="memberballot.question" text="Question No."/>
	</th>		
	</tr>
	<c:forEach items="${memberBallots}" var="i">	
	<tr>
		<td class="round${i.round }">${i.position}</td>
		<td class="round${i.round }">${i.member}</td>	
		<td class="question">
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

