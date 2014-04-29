<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css" media="screen">
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
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=4" media="print" />
	</head>
	<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<br><br>
<div id="reportDiv">
<table class="uiTable">
	<thead>		
		<tr>
			<th>
			<spring:message code="memberballotstatus.present" text="Present Members"/>
			</th>
		</tr>
		<tr>
			<th><spring:message code="memberballotstatus.round" text="Round"/>
			</th>			
			<th>	
			<spring:message code="memberballotstatus.memberballot" text="Member Ballot"/>
			</th>		
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${presentBallot}" var="i">	
			<tr>
				<td class="round${i.number }">${i.id}</td>
				<td class="round${i.number}">${i.name}</td>					
			</tr>
		</c:forEach>
	</tbody>	
</table>
<br><br>
<table class="uiTable">
	<thead>		
		<tr>
			<th>
			<spring:message code="memberballotstatus.absent" text="Absent Members"/>
			</th>
		</tr>
		<tr>
			<th><spring:message code="memberballotstatus.round" text="Round"/>
			</th>			
			<th>	
			<spring:message code="memberballotstatus.memberballot" text="Member Ballot"/>
			</th>		
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${absentBallot}" var="i">	
			<tr>
				<td class="round${i.number }">${i.id}</td>
				<td class="round${i.number }">${i.name}</td>					
			</tr>
		</c:forEach>
	</tbody>	
</table>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
