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
		@media print{
			h3{
				font-size: 18px;
			}
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
	<spring:message code="motion.report.notCreated" text="Report not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="motion.report.empty" text="No entry found."/>
</c:when>

<c:otherwise>
<div id="reportDiv" >
	
	<c:set var="columns" value="0" />
	<c:set var="prevMinistry" value="0" />
	<table>
		<thead>
			<tr>
				<th colspan="2">
					<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px; text-decoration: underline; ">
						<h3 style="color: black; font-family: 'Times New Roman';">
							${report[0][0]}
						</h3>
						<span style="font-weight:bold; font-size: 14px;">
							${report[0][1]}
						</span><br />
						<span style="font-weight: bold; font-size: 14px;">
							${report[0][2]}
						</span><br />
					</div>
				</th>				
			</tr>
			
			<tr><th colspan="2">&nbsp;</th></tr>
			
			<tr><th colspan="2">&nbsp;</th></tr>
		</thead>
		<tbody>
			<c:forEach items="${report}" var="r" varStatus="counter">
				<c:if test="${r[4]!=prevMinistry}">
					<tr calss="page-break">
						<td colspan="2" style="text-align: left; font-weight: bold; font-size: 16px; text-decoration: underline;">${r[3]}</td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
				</c:if>
				<tr calss="page-break">
					<td style="font-weight: bold; font-size: 16px; width: 20px;">
						(${formater.formatNumberNoGrouping(counter.count, locale)})
					</td>
					<td style="font-weight: bold; text-align: left; padding: 5px; font-size: 16px;">
						${r[7]}, ${motion.findAllMemberNames(r[5],'firstnamelastname') }, ${r[8]}
					</td>
				</tr>
				<tr calss="page-break">
					<td style="width:20px;">&nbsp;</td>
					<td style="font-size: 16px; text-align: left; padding: 5px;">"${r[6]}"</td>	
				</tr>		
				<c:set var="prevMinistry" value="${r[4]}" />
			</c:forEach>
		</tbody>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>