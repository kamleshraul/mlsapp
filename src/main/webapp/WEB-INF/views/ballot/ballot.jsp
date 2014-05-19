<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$('.questionNumber').click(function() {
				var id = $(this).attr("id");
				editQuestion(id);
			});
		});
	</script>
	<style type="text/css">
		.memberName{
			min-width: 170px;
			max-width: 250px;
			text-align: left;
			padding: 5px;
			font-weight: bold;
		}
		.questionNumber{
			width: 50px;
			max-width: 105px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}	
		.round{
			width: 50px;
			max-width: 105px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.td{
			text-align: center;
			padding: 5px;
		}	
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${ballotVOs == null}">
	<spring:message code="question.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty ballotVOs}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
	<h2 style="text-decoration: underline; font-family: 'Times New Roman';"><spring:message code="generic.ballot.list" text="BALLOT LIST" /></h2>
</div>
<table style="margin: 0px 0px 10px 25px; width: 800px; max-width: 800px; font-size: 15px;">
	<tr>
		<td colspan="2" style="text-align: right;">
			<span><spring:message code="generic.date" text="Answering Date"/>&nbsp;&nbsp;&nbsp;<span style="text-decoration: underline; font-weight: bold;"> ${answeringDate}</span></span>
		</td>
		<td colspan="5" style="text-align: center; font-family: 'Arial'">
			<spring:message code="question.ballot.starred.lowerhouse.headercontent" text="Ballot Of the Day"></spring:message>
		</td>
	</tr>
</table>
<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
	<tr style="margin-top: 5px;">
		<%-- <th><spring:message code="general.srnumber" text="Serial Number"/></th> --%>
		<th><spring:message code="question.ballot.starred.lowerhouse.member.name" text="Member Name"/></th>
		<th><spring:message code="question.ballot.starred.lowerhouse.round1" text="Round 1"/></th>
		<th><spring:message code="question.ballot.starred.lowerhouse.ballotnumber" text="Ballot Number"/></th>		
		<th><spring:message code="question.ballot.starred.lowerhouse.round2" text="Round 2"/></th>
		<th><spring:message code="question.ballot.starred.lowerhouse.ballotnumber" text="Ballot Number"/></th>
		<th><spring:message code="question.ballot.starred.lowerhouse.round3" text="Round 3"/></th>
		<th><spring:message code="question.ballot.starred.lowerhouse.ballotnumber" text="Ballot Number"/></th>
	</tr>
	<tr>
		<td style="font-size: 8px;" colspan="7">&nbsp;</td>
	</tr>
	<c:set var="counter" value="1" />
	<c:set var="memberName" value="" />
	<c:set var="mCounter" value="1" />
	<c:set var="maxCount" value="3" />
	<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="ctr">
		<c:choose>
			<c:when test="${memberName != ballotVO[0]}">
				<c:if test="${memberName!=''}">
					<c:if test="${mCounter <= maxCount}">
						<c:forEach begin="${mCounter}" end="${maxCount}" step="1" varStatus="ctr">
							<td class="td">-</td>
							<td class="td">-</td>						
						</c:forEach>
					</c:if>
					
					</tr>
					<c:set var="mCounter" value="1" />
				</c:if>
				
				<tr>
					<%-- <td class="td">${counter}</td> --%>
					<td class="memberName">${ballotVO[0]}</td>
					<c:if test="${not empty ballotVO[2] and ballotVO[2]!=''}">
					<td class="td">${ballotVO[1]}</td>
					<td class="td">${ballotVO[2]}</td>
					<c:set var="counter" value="${counter + 1}" />
					<c:set var="mCounter" value="${mCounter + 1}" />
					</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty ballotVO[2] and ballotVO[2]!=''}">
				<td class="td">${ballotVO[1]}</td>
				<td class="td">${ballotVO[2]}</td>
				<c:set var="mCounter" value="${mCounter + 1}" />
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:set var="memberName" value="${ballotVO[0]}" />   
	</c:forEach>
	<c:if test="${mCounter <= maxCount}">
		<c:forEach begin="${mCounter}" end="${maxCount}" step="1">
			<td class="td">-</td>
			<td class="td">-</td>						
		</c:forEach>
	</c:if>
</table>
<table style="max-width: 800px; width: 800px; margin-left: 25px; margin-top: 20px;">
	<tr>
		<td colspan="7">
			<p style="font-size: 15px;">
				<span style="font-weight: bold;">
					<spring:message code="generic.total" text="Total" />:&nbsp;&nbsp;&nbsp;&nbsp;${totalMembers}
				</span><br /><br />
				<spring:message code="generic.date" text="Answering Date"/> ${answeringDate}<spring:message code="question.ballot.starred.lowerhouse.footer1" text="Footer"/>
				${formattedCurrentDate}<spring:message code="question.ballot.starred.lowerhouse.footer2" text="Footer"/>
			</p>
		</td>		
	</tr>
</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>