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
		<h2 style="color: black; text-decoration: underline; font-family: 'Times New Roman';">
			${report[0][6]}
		</h2>
	</div>
	<br />
	<c:set var="columns" value="0" />
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr>
				<c:forEach items="${topHeader}" var="h" varStatus="headCols">
					<th>${h}</th>
					<c:set var="columns" value="${headCols.count}" />
				</c:forEach>
			</tr>
			<tr>
				<th style="font-size: 8px;" colspan="${columns}">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="discDate" value="-"></c:set>
			<c:set var="labelCounter" value="0" />
			<c:set var="lastNodeReceiveDate" value="-" />
			<c:forEach items="${report}" var="r" varStatus="counter">
				<c:choose>
					<c:when test="${discDate!=r[2]}">
							<c:set var="labelCounter" value="${labelCounter + 1 }" />
							<c:if test="${discDate!='-'}">
									</td>
									<td>${lastNodeReceiveDate}</td>
								</tr>
							</c:if>
						<tr>
							<td>${formater.formatNumberNoGrouping(labelCounter, locale)}</td>
							<td>${r[3]}</td>
							<td>${r[4]}<br />
					</c:when>
					<c:otherwise>
						${r[4]}<br/>
					</c:otherwise>
				</c:choose>
				<c:set var="discDate" value="${r[2]}" />	
				<c:set var="lastNodeReceiveDate" value="${r[6]}" />			
			</c:forEach>
			</td>
				<td>${lastNodeReceiveDate}</td>
			</tr>
		</tbody>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>