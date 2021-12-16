<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster" text="Roster"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css" media="all">
		.reporterName{
			min-width: 170px;
			max-width: 250px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.slotName{
			width: 50px;
			max-width: 105px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.slotTime{
			width: 100px;
			max-width: 110px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}	
		.td{
			text-align: center;
			padding: 5px;
		}	
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=47" media="print"/>
</head>
<body>
<div id="reportDiv">
	<div style="text-align: center; width: 100%;">
		<span style="font-size: 20px; color: black; font-weight: bold; margin-bottom: 10px;"><spring:message code="roster.header" text="Roster" /></span>
		<br /><br />
		<span style="font-size: 14px; color: black; font-weight: bold;"><spring:message code="generic.date" text="Date" />: ${rosterDate}</span>
		<br /><br />
		<span style="font-size: 14px; color: black; font-weight: bold;"><spring:message code="committeeName" text="Committee Name" />: ${committeName}</span>
	</div>
	<div style="min-width: 800px; overflow: scroll; margin: 10px 0px 0px 10px;" id="repDiv">
		<table border="1" style="width: 100%;">
			<thead>
				<c:set var="repHeadName" value="" />
				<c:set var="count" value="1" />
				<c:forEach items="${report}" var="headSlot" varStatus="counter">
					<c:if test="${count==1}">
						<c:choose>
							<c:when test="${repHeadName != headSlot[0]}">
								<c:if test="${repHeadName!=''}">
									</tr>
									<c:set var="count" value="2" />
								</c:if>
								<c:if test="${count==1}">
								<tr>
									<th class="reporterName"><spring:message code="roster.reportername" text="Reporter Name" /></th>
									<th class="slotName"><spring:message code="roster.slotname" text="Slot" /></th>
									<th class="slotTime"><spring:message code="roster.slottime" text="Slot Timing" /></th>
								</c:if>							
							</c:when>
							<c:otherwise>
								<c:if test="${count==1}">
									<th class="slotName"><spring:message code="roster.slotname" text="Slot" /></th>
									<th class="slotTime"><spring:message code="roster.slottime" text="Slot Timing" /></th>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:set var="repHeadName" value="${headSlot[0]}" />
					</c:if>
				</c:forEach>
			</thead>
			<tbody>
				<c:set var="repName" value="" />
				<c:set var="mCounter" value="0" />	
				<c:set var="maxCount" value="0" />			
				<c:forEach items="${report}" var="slot">
					<c:choose>
						<c:when test="${repName != slot[0]}">
							<c:if test="${repName!=''}">
								<c:if test="${mCounter < maxCount}">
									<c:forEach begin="${mCounter}" end="${maxCount}" step="1">
										<td class="td">-</td>						
									</c:forEach>
								</c:if>
								</tr>
								<c:set var="mCounter" value="0" />
							</c:if>
							
							<tr>
								<td class="td">${slot[0]}</td>
								<td class="td">${slot[1]}</td>
								<td class="td">${slot[2]}</td>
								<c:set var="mCounter" value="${mCounter + 1}" />
						</c:when>
						<c:otherwise>
							<td class="td">${slot[1]}</td>
							<td class="td">${slot[2]}</td>
							<c:set var="mCounter" value="${mCounter + 1}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${maxCount < mCounter}">
						<c:set var="maxCount" value="${mCounter}" />
					</c:if>	
					<c:set var="repName" value="${slot[0]}" />
				</c:forEach>
				<c:if test="${mCounter < maxCount}">
					<c:forEach begin="${mCounter}" end="${maxCount}" step="1">
						<td class="td">-</td>						
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>
</div>
<input type="hidden" id="rosterId" value="${rosterId}" />
</body>
</html>