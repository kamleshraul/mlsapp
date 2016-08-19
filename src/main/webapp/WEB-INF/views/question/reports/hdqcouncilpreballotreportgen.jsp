<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			if($("#houseType").val()=='upperhouse'){
				if($("#preballot").val()=='yes'){
					if($("#deviceType").val()=='questions_halfhourdiscussion_from_question'){
						$.get('ballot/ballotfooter?report=HDQ_COUNCIL_PREBALLOT_FOOTER&session='+$("#session").val()+'&device='+$("#device").val()+'&answerDate='+$("#answeringDate").val(), function(data){
							if(data){
								var text = data[0][1]+ " " + data[0][2] + data[0][3]+"<br><br><br>"+data[0][4]+"<br><br><br>"+data[0][5];
								
								$("#balFoot").empty();
								$("#balFoot").html(text);
							}							
						});
					}else if($("#deviceType").val()=='motions_standalonemotion_halfhourdiscussion'){
						$.get('ballot/ballotfooter?report=HDS_COUNCIL_PREBALLOT_FOOTER&session='+$("#session").val()+'&device='+$("#device").val()+'&answerDate='+$("#answeringDate").val(), function(data){
							if(data){
								var text = data[0][1]+ " " + data[0][2] + data[0][3]+"<br><br><br>"+data[0][4]+"<br><br><br>"+data[0][5];
								
								$("#balFoot").empty();
								$("#balFoot").html(text);
							}
						});
					}
				}
			}
		});
	</script>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="question.ballot.notCreated" text="Ballot is not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.ballot.noEntries" text="There are no entries in the Ballot"/>
</c:when>

<c:otherwise>
<div id="reportDiv" style="width: 750px;">
	<div style="width: 100%; font-weight: bold; text-align: center; font-size: 22px; ">
		<c:choose>
			<c:when test="${report[0][6]=='lowerhouse'}">
				<spring:message code="generic.maharashtra.lowerhouse" text="Maharashtra Assembly"/>
			</c:when>
			<c:when test="${report[0][6]=='upperhouse'}">
				<spring:message code="generic.maharashtra.upperhouse" text="Maharashtra Council"/>
			</c:when>
		</c:choose>
	</div>
	<div style="width: 100%; font-weight: bold; text-align: center; ">
		${report[0][12]}
	</div>
	<br>
	<div style="width: 100%;font-size: 16px; font-weight: bold; text-align: center; width:">
		<spring:message code="question.ballot.discussionDate" text="Discussion Date"/> : ${report[0][13]}
	</div>
	<table class="strippedTable" border="1">
	<thead>
		<tr>
			<th style="width: 50px; text-align: center;">${topHeader[0]}</th>
			<th style="width: 180px; text-align: center;">${topHeader[1]}</th>
			<th style="width: 50px; text-align: center;">${topHeader[2]}</th>
			<th style="width: 50px; text-align: center;">${topHeader[3]}</th>
			<th style="width: 200px; text-align: center;">${topHeader[4]}</th>
			<th style="width: 220px; text-align: center;">${topHeader[5]}</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${report}" var="b" varStatus="counter">
		<tr>
			<td style="width: 50px; text-align: center;">${formater.formatNumberNoGrouping(counter.count, locale)}</td>
			<td style="width: 180px; text-align: center;">${b[2]}</td>
			<td style="width: 50px; text-align: center;">${b[1]}</td>
			<td style="width: 50px; text-align: center;">${b[4]}</td>
			<td style="width: 200px; text-align: center;">${b[5]}</td>
			<td style="width: 220px; text-align: center;">${b[3]}</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<br><br>
	<div style="width: 100%; font-size: 16px;" id="balFoot">
					-
	</div>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="houseType" value="${report[0][6]}" />
<input type="hidden" id="deviceType" value="${report[0][8]}" />
<input type="hidden" id="preballot" value="${report[0][10]}" />
<input type="hidden" id="session" value="${report[0][7]}" />
<input type="hidden" id="device" value="${report[0][9]}" />
<input type="hidden" id="answeringDate" value="${report[0][11]}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>