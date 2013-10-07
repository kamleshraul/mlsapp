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
					
					var reportURL = 'rotationorder/viewministrydepartmentreport?' + parameters_report;
					$("#ministryreport_pdf").attr('href', reportURL);
				});
				
				$("#ministryreport_word").click(function() {				
					var parameters_report = "houseType="+$("#selectedHouseType").val()
					 +"&sessionYear="+$("#selectedSessionYear").val()
					 +"&sessionType="+$("#selectedSessionType").val()
					 +"&questionType="+$("#selectedQuestionType").val()
					 +"&group="+$("#selectedGroup").val()
					 +"&status="+$("#selectedStatus").val()
					 +"&role="+$("#srole").val() 
					 + "&answeringDate=" + $("#selectedAnsweringDate").val()
					 + "&outputFormat=WORD";
					
					var reportURL = 'rotationorder/viewministrydepartmentreport?' + parameters_report;
					$("#ministryreport_word").attr('href', reportURL);
					/* window.open(reportURL, '_blank'); */
				});
			});
		</script>
		<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=2" />
		<!-- <style type="text/css" media="screen">
			#ministry_departments{
				display: none;
			}
		</style> -->
		
		<style type="text/css" media="print">
		
			/* body {counter-reset: header;}
			table{counter-reset: header;}
			thead:before{
				counter-increment: header;
				content: ".." counter(header) "..";
			}
 			*/
			@page {
			  size: 210mm 297mm;
			  margin: 5% 5% 5% 10%;
			}
			
			
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
				font-size: 18px; 
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
				padding-left: 100px;
				
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
			<a id="ministryreport_pdf" class="exportLink" href="#" style="text-decoration: none; margin-left: 40px;">
				<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32" title="<spring:message code='generic.ministryreport' text='Ministry Department Report' />">
			</a>
			&nbsp;&nbsp;
			<a id="ministryreport_word" class="exportLink" href="#" style="text-decoration: none;" target="_blank">
				<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32" title="<spring:message code='generic.ministryreport' text='Ministry Department Report' />">
			</a>
			&nbsp;&nbsp;
			<span><spring:message code='generic.ministryreport' text='Ministry Department Report' /></span>
		</div>
	</div>	
	<div id="reportDiv" style="overflow: hidden;" >
		<div id="ministryReport">
			<div id="ministry_departments" style="margin-top: 25px;">
				<table style="width: 750px;">
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
											<tr class="page-break">
												<td>
													<span class="ministryReportMinistryName">(${mi[0]})&nbsp;&nbsp;&nbsp;&nbsp;${mi[1]} --- ${mi[2]}</span><br />
													<span class="ministryReportDepartment">(${mi[4]})&nbsp;${mi[3]}</span><br />
										</c:when>
										<c:otherwise>
											<c:set var="count" value="2" />
											<span class="ministryReportDepartment">(${mi[4]})&nbsp;${mi[3]}</span><br />
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
	</div>
	</body>
</html>