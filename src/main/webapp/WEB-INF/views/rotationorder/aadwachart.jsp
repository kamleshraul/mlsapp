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
				
				$("#aadwachart_pdf").click(function() {				
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 + "&outputFormat=PDF";
					
					var reportURL = 'rotationorder/viewaadwachartreport?' + parameters_report;
					$("#aadwachart_pdf").attr('href', reportURL);
				});
				
				$("#aadwachart_word").click(function() {				
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 + "&outputFormat=WORD";
					
					var reportURL = 'rotationorder/viewaadwachartreport?' + parameters_report;
					$("#aadwachart_word").attr('href', reportURL);
				});
			});		
		</script>
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/rotationOrderReport.css?v=47" />
	</head>

	<body>
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<br/>
		<a id="aadwachart_pdf" class="exportLink" href="#" style="text-decoration: none;" target="_blank">
			<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
		</a>
		&nbsp;&nbsp;
		<a id="aadwachart_word" class="exportLink" href="#" style="text-decoration: none;" target="_blank">
			<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
		</a>
		<div id="rotationOrderReport" style="overflow: scroll;" >
			<table class="strippedTable" border="1">
				<thead>
					<tr>
						<th><spring:message code='group.rotationorder.group' text='Group'></spring:message></th>
						<th><spring:message code='group.rotationorder.dayofweek' text='Day Of Week'></spring:message></th>
						<th><spring:message code='group.rotationorder.answeringDate' text='Answering Date'></spring:message></th>
						<th><spring:message code='group.rotationorder.submissionDate' text='Final Submission Date'></spring:message></th>
						<%-- <c:choose>
							<c:when test="${aadwaHouseType=='lowerhouse'}" >
								<th><spring:message code='group.rotationorder.speakerSendingDate' text='Speaker Sending Date'></spring:message></th>
							</c:when>
						</c:choose> --%>
						<th><spring:message code='group.rotationorder.lastSendingDateToDepartment' text='Last Sending Date To department'></spring:message></th>
						<th><spring:message code='group.rotationorder.lastReceivingDateFromDepartment' text='Last receiving date From Department'></spring:message></th>
						<th><spring:message code  ='group.rotationorder.yaadiPrintingDate' text='Yaadi printing Date'></spring:message></th>
						<th><spring:message code='group.rotationorder.yaadiReceivingDate' text='Yaadi Receiving Date'></spring:message></th>
						<th><spring:message code='group.rotationorder.suchhiPrintingDate' text='Suchhi Printing Date'></spring:message></th>
						<th><spring:message code='group.rotationorder.suchhiReceivingDate' text='Suchhi Receiving Date'></spring:message></th>
						<th><spring:message code='group.rotationorder.suchhiDistributionDate' text='Suchhi Distribution Date'></spring:message></th>
					</tr>
				</thead>
				<c:if test="${!(empty dates) }">
					<c:set var="count" value="1"></c:set>
					<c:forEach items="${dates}" var="i" varStatus="pageCount">						
						<c:if test="${count==i.rowId or count > i.rowId}">
							<c:choose>
								<c:when test="${aadwaHouseType=='lowerhouse'}">
									<!-- <tr><td colspan="12">&nbsp;</td></tr> -->
									<tr><td colspan="11">&nbsp;</td></tr>
								</c:when>
								<c:when test="${aadwaHouseType=='upperhouse'}">
									<tr><td colspan="11">&nbsp;</td></tr>
								</c:when>
							</c:choose>							
						</c:if>
						<c:choose>
							<c:when test="${pageCount.count mod 15 == 0 }">
								<tr class="page-break">
							</c:when>
							<c:otherwise>
								<tr>
							</c:otherwise>
						</c:choose>					
							<td>${i.group }</td>
							<td>${i.dayOfWeek }</td>
							<td>${i.answeringDate }</td>
							<td>${i.finalSubmissionDate }</td>
							<%-- <c:choose>
								<c:when test="${aadwaHouseType=='lowerhouse'}">
									<td>${i.speakerSendingDate }</td>
								</c:when>
							</c:choose> --%>
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