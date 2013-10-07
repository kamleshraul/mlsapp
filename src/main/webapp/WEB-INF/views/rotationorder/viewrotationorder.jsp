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
				
				$("#ministryreport_pdf").click(function() {				
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 + "&outputFormat=PDF";
					
					var reportURL = 'rotationorder/viewrotationorderreport?' + parameters_report;
					$("#rotationorder_pdf").attr('href', reportURL);
				});
				
				$("#rotationorder_word").click(function() {				
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 + "&outputFormat=WORD";
					
					var reportURL = 'rotationorder/viewrotationorderreport?' + parameters_report;
					$("#rotationorder_word").attr('href', reportURL);
					/* window.open(reportURL, '_blank'); */
				});
			});
		</script>
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/rotationOrderReport.css?v=2" />
		<style type="text/css" media="screen">
			#ministry_departments{
				display: none;
			}
		</style>
		
		<style type="text/css" media="print">
			#ministry_departments{
				display: block;
			}
			.do-break{
				page-break-after: always;
			}
		</style>
		
		<style type="text/css" media="all">
			.ministryReportHead{
				font-weight: bold; 
				font-size: 20px; 
				text-decoration: underline; 
				text-align: center; 
				padding: 10px;
			}
			
			.ministryReportMinistryName{
				font-weight: bolder; 
				font-size: 16px; 
				padding: 5px;					
			}
			
			.ministryReportDepartment{
				font-size: 14px; 
				padding-left: 40px;
			}
		</style>
	</head>

	<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<br/>
	<div>
		<div align="left" style="width: 100%; border: 2px solid black;">
			<a id="rotationorder_pdf" class="exportLink" href="#" style="text-decoration: none; margin-left: 40px;">
				<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32" title="<spring:message code='generic.rotationorder' text='Rotation Order' />">
			</a>
			&nbsp;&nbsp;
			<a id="rotationorder_word" class="exportLink" href="#" style="text-decoration: none;" target="_blank">
				<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32" title="<spring:message code='generic.rotationorder' text='Rotation Order' />">
			</a>
			&nbsp;&nbsp;
			<span><spring:message code='generic.rotationorder' text='Rotation Order' /></span>
		</div>
	</div>	
	<div id="rotationOrderReport" style="overflow: scroll;" >
		<%-- <div id="rotationOrderHeader" style="width: 100%">
			${rotationOrderHeader }
			<br>
			${rotationOrderCover }
			<br>
			<br>
		</div> --%>
		<div id="rotationOrder">
				<table class="strippedTable" style="width: 100%" border="1">
					<thead>
						<tr>
							<th style="width: 50px; text-align: center;"><spring:message code='group.rotationorder.group' text='Group'></spring:message></th>
							<th style="width: 250px; text-align: center;"><spring:message code='group.rotationorder.ministries' text='Ministries'></spring:message></th>
							<th style="width: 250px; text-align: center;"><spring:message code='group.rotationorder.answeringDate' text='Answering Date'></spring:message></th>
							<th style="width: 250px; text-align: center;"><spring:message code='group.rotationorder.submissionDate' text='Final Submission Date'></spring:message></th>
						</tr>
					</thead>
					
					<c:if test="${!(empty dates) }">
						<c:set var="count" value="1"></c:set>
						<c:forEach items="${dates}" var="i" varStatus="pageCount">
							<c:if test="${count==i.rowId or count > i.rowId}">
								<tr><td colspan="12">&nbsp;</td></tr>
							</c:if>
							<c:choose>							
								<c:when test="${pageCount.count mod 3 == 0 }">
									<tr class="page-break">
								</c:when>
								<c:otherwise>
									<tr>
								</c:otherwise>
							</c:choose>
							
								<td style="width: 40px; text-align: center;">${i.group}</td>
								<td style="width: 430px;">
									<c:forEach items= "${i.ministries}" var="j" varStatus="cnt">
									(${i.numberOfMinisteries[cnt.count-1]}) ${j}
										<br>
									</c:forEach> 
								</td>
								<td style="width: 150px; text-align: justify;">
								<c:forEach items= "${i.answeringDates}" var="k">
									${k}
									<br>
								</c:forEach>
								</td>
								<td style="width: 180px; text-align: justify;">
								<c:forEach items= "${i.finalSubmissionDates}" var="l">
									${l}
									<br>
								</c:forEach> </td>
							</tr>						
							<c:set var="count" value="${i.rowId}"></c:set>
						</c:forEach>
					</c:if>
				</table>
			</div>
			<%-- <div id="rotationOrderFooter" style="width: 100%">
				${rotationOrderFooter}
			</div>
			<div style="width: 120px; height: 20px; margin-left: 400px; margin-top: 10px;padding: 5px">
				<a id="header" href="#rotationOrderHeader" style="width: 40px; display: inline; float: left;"> 1</a> 
				<a id="rotationOrderLink" href="#rotationOrder" style="width: 40px; display: inline; float: left;"> 2</a> 
				<a id="footer" href="#rotationOrderFooter" style="width: 40px; display: inline; float: left;"> 3</a>
				<p>&nbsp;</p>
			</div> --%>
			<span class="do-break" >&nbsp;</span>
			<div id="ministry_departments" style="margin-top: 25px;">
				<table style="width: 100%">
					<thead>
						<tr>
							<td colspan="2" class="ministryReportHead">
								<spring:message code="ministry.report.head" text="Departments under ministries" />
							</td>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not (empty ministryreport or ministryreport==null)}">
								<c:set var="ministry" value="" />
								<c:set var="count" value="1" />
								<c:forEach items="${ministryreport}" var="mi">
									<c:choose>
										<c:when test="${ministry != mi[1]}">
											<c:if test="${count > 1}">
													</td>
												</tr>
												<c:set var="count" value="1" />
											</c:if>
											<c:set var="count" value="2" />
											<tr>
												<td colspan="2" class="ministryReportMinistryName">
													(${mi[0]})&nbsp;&nbsp;&nbsp;&nbsp;${mi[1]} --- ${mi[2]}
												</td>
											</tr>
											<tr class="page-break">
												<td class="ministryReportDepartment">
													(${mi[4]})&nbsp;${mi[3]} <br />
										</c:when>
										<c:otherwise>
											<c:set var="count" value="2" />
											(${mi[4]})&nbsp;${mi[3]} <br />
										</c:otherwise>
									</c:choose>
									<c:set var="ministry" value="${mi[1]}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan="2">
										<spring:message code="generic.nodatafound" text="Content not available." />
									</td>
								</tr>
							</c:otherwise>						
						</c:choose>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>