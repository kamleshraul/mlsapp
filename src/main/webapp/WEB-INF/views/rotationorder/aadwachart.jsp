<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="group.title" text="Groups"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
		<script type="text/javascript">
			$('document').ready(function(){	
				initControls();
				$('#key').val('');		
			});		
		</script>
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/rotationOrderReport.css?v=46" />
	</head>

	<body>
		<div id="rotationOrderReport" style="overflow: scroll;" >
			<table class="uiTable">
				<tr>
				<th><spring:message code='group.rotationorder.group' text='Group'></spring:message></th>
				<th><spring:message code='group.rotationorder.dayofweek' text='Day Of Week'></spring:message></th>
				<th><spring:message code='group.rotationorder.answeringDate' text='Answering Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.submissionDate' text='Final Submission Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.speakerSendingDate' text='Speaker Sending Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.lastSendingDateToDepartment' text='Last Sending Date To department'></spring:message></th>
				<th><spring:message code='group.rotationorder.lastReceivingDateFromDepartment' text='Last receiving date From Department'></spring:message></th>
				<th><spring:message code  ='group.rotationorder.yaadiPrintingDate' text='Yaadi printing Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.yaadiReceivingDate' text='Yaadi Receiving Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.suchhiPrintingDate' text='Suchhi Printing Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.suchhiReceivingDate' text='Suchhi Receiving Date'></spring:message></th>
				<th><spring:message code='group.rotationorder.suchhiDistributionDate' text='Suchhi Distribution Date'></spring:message></th>
				</tr>
				
				<c:if test="${!(empty dates) }">
					<c:set var="count" value="1"></c:set>
					<c:forEach items="${dates}" var="i">
						<c:if test="${count==i.rowId or count > i.rowId}">
							<tr><td colspan="12">&nbsp;</td></tr>
						</c:if>
						<tr>
							<td>${i.group }</td>
							<td>${i.dayOfWeek }</td>
							<td>${i.answeringDate }</td>
							<td>${i.finalSubmissionDate }</td>
							<td>${i.speakerSendingDate }</td>
							<td>${i.lastSendingDateToDepartment }</td>
							<td>${i.lastReceivingDateFromDepartment }</td>
							<td>${i.yaadiPrintingDate }</td>
							<td>${i.yaadiReceivingDate }</td>
							<td>${i.suchhiPrintingDate}</td>
							<td>${i.suchhiReceivingDate }</td>
							<td>${i.suchhiDistributionDate }</td>
						</tr>						
						<c:set var="count" value="${i.rowId}"></c:set>
					</c:forEach>
				</c:if>
			</table>
		</div>
	</body>
</html>