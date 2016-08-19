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
	<div id="reportDiv" style="width: 750px !important;">
		<div style="width: 300px; min-width: 300px; display: inline-block;">
			&nbsp;
		</div>
		<div style="width: 450px !important; display: inline; float: right; word-wrap:break-word; font-size: 17px;">
			${report[0][1]}
		</div>		
	</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>