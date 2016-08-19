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
			vertical-align: top;
		}
		
		td{
			margin-left: 5px;
			margin-right: 5px;
			vertical-align: top;
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
		<h2 style="color: black; text-decoration: underline; font-family: 'Times New Roman';">${topHeader[0]}</h2>
	</div>
	<br />
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr style="margin-top: 5px;">
				<th style="max-width: 20px;">${topHeader[1]}</th>
				<th style="max-width: 10px;">${topHeader[2]}</th>
				<th style="max-width: 100px;">${topHeader[3]}</th>				
				<th style="max-width: 150px;">${topHeader[4]}</th>
				<th style="max-width: 70px;">${topHeader[5]}</th>
				<th style="max-width: 70px;">${topHeader[6]}</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="font-size: 8px;" colspan="6">&nbsp;</td>
			</tr>
			<c:forEach items="${report}" var="r" varStatus="ctr">
				<tr class="page-break">
					<td style="max-width: 20px;">${r[1]}</td>
					<td style="max-width: 10px;text-align: center;">${r[2]}</td>
					<td style="max-width: 100px;">${r[3]}</td>
					<td style="max-width: 150px;">${r[4]}</td>
					<td style="max-width: 70px;">${r[5]}</td>
					<td style="max-width: 70px;">${r[6]}</td>
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