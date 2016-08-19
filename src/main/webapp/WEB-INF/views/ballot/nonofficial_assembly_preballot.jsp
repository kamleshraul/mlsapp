<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	</script>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=3" media="print" />
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div id="reportDiv">
		<div style="text-align:center; font-weight: bold;font-size: 18px;" >
			<spring:message code="resolution.preballot" text="Preballot"/>
			<br>
			<spring:message code="resolution.nonofficial" text="Non Official Resolution"/>
		</div>
		<c:choose>
			<c:when test="${ballotVOs == null}">
				<spring:message code="resolution.ballot.notCreated" text="Ballot is not Created"/>
			</c:when>
			
			<c:when test="${empty ballotVOs}">
				<spring:message code="resolution.ballot.noEntries" text="There are no entries in the Ballot"/>
			</c:when>
			
			<c:otherwise>
				<label class="small"><spring:message code="resolution.ballot.discussionDate" text="Discussion Date"/>: ${answeringDate}</label>
				
				<table class="strippedTable" border="1">
					<tr>
						<th style="text-align:center;"><spring:message code="general.srnumber" text="Serial Number"/></th>
						<th style="text-align:center;"><spring:message code="member.name" text="Member Name"/></th>
					</tr>
					<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="counter">
					<tr>
						<td style="text-align:center;">${counter.count}</td>
						<td>${ballotVO.memberName}</td>
					</tr>
					</c:forEach>
				</table>
				<div style="margin-left: 550px;font-weight: bold;margin-top: 20px;">
					<spring:message code="resolution.totalCount" text="Total Count"/> :  ${ballotVOs.size()}
				</div>
			</c:otherwise>
		</c:choose>
		
		
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>