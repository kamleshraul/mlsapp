<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster" text="Roster"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css" media="all">
		.Date{
			min-width: 170px;
			max-width: 250px;
			text-align: center;
			padding: 5px;
			font-weight: bold;
		}
		.Time{
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
		<span style="font-size: 20px; color: black; font-weight: bold; margin-bottom: 10px;"><spring:message code="roster.totalworkreport" text="Total Work Report" /></span>
		<br />
	</div>
	

	<div style="min-width: 800px; overflow: scroll; margin: 50px 0px 0px 10px;" id="repDiv">
		<table border="1" style="width: 100%;">
			<thead>
			
								<tr>
									<th class="Date"><spring:message code="roster.date" text="Date" /></th>
									<th class="Time"><spring:message code="roster.startTime" text="Start Time" /></th>
									<th class="Time"><spring:message code="roster.endTime" text="End Time" /></th>
									<th class="Time"><spring:message code="roster.totalTime" text="Total Work" /></th>
									<th class="Time"><spring:message code="roster.wastedTime" text="Total wasted" /></th>
									<th class="Time"><spring:message code="roster.actualTime" text="Actual Work" /></th>
								<tr>					
						
				
			</thead>
			<tbody>
				
				<c:forEach items="${report}" var="slot">
					
							
							<tr>
								<td class="td">${slot[0]}</td>
								<td class="td">${slot[1]}</td>
								<td class="td">${slot[2]}</td>
								<td class="td">${slot[3]}</td>
								<td class="td">${slot[4]}</td>
								<td class="td">${slot[5]}</td>
							
							</tr>
				</c:forEach>
			</tbody>
					<c:forEach items="${report1}" var="slot">
					
							
							<tr>
								<td class="td">${slot[6]}</td>
								<td class="td">${slot[6]}</td>
								<td class="Time">${slot[2]}</td>
								<td class="td">${slot[3]}</td>
								<td class="td">${slot[4]}</td>
								<td class="td">${slot[5]}</td>
						   </tr>
							<tr>
								<td class="td">${slot[6]}</td>
								<td class="td">${slot[6]}</td>
								<td colspan="3" class="Time">${slot[0]}</td>
								<td class="Time">${slot[1]}</td>
							
							
							</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<input type="hidden" id="sessionId" value="${sessionId}" />

</body>
</html>