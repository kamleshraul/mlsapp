<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			
		});
	</script>
	<style type="text/css">
		th{
			text-align: center;
		}
		
		td{
			text-align: center;
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
<c:when test="${report == null}">
	<spring:message code="question.report.notCreated" text="Report not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.report.empty" text="No entry found."/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h2 style="color: black; text-decoration: underline; font-family: 'Times New Roman';"><spring:message code="question.report.admission" text="ADMISSION LIST" /></h2>
	</div>
	<br />
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr>
				<th colspan="6">
					<h3 style="color: black;">
						<spring:message code="question.report.groupnumber" text="Group"/> : ${report[0][2]}
						<c:if test="${showSubDepartment!='0'}"> 
							<br />
							<spring:message code="question.report.department" text="Department" />: ${report[0][8]}
						</c:if>
					</h3>
				</th>
			</tr>
			<tr style="margin-top: 5px;">
				<th><spring:message code="question.report.srnumber" text="Serial Number"/></th>
				<th><spring:message code="question.report.member.name" text="Member Name"/></th>
				<th><spring:message code="question.report.question_number" text="Number"/></th>
				<th><spring:message code="question.report.subject" text="Subject"/></th>		
				<th><spring:message code="question.report.answeringdate" text="Answering Date"/></th>
				<th><spring:message code="question.report.status" text="Status"/></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="font-size: 8px;" colspan="6">&nbsp;</td>
			</tr>
			<c:forEach items="${report}" var="r" varStatus="ctr">
				<tr class="page-break">
					<td>${formater.formatNumberNoGrouping(ctr.count, locale)}</td>
					<td>${r[3]}</td>
					<td>${r[4]}</td>
					<td>${formater.formatNumbersInGivenText(r[5], locale)}</td>
					<td>${r[6]}</td>
					<td>${r[7]}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>