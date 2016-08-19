<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
	<style type="text/css" media="print">
		th{			
			font-size: 15px !important;
			vertical-align: top;
		}		
		#topHeader{
			font-size: 20px !important;
			font-weight: bold;
		}
	</style>
	<style type="text/css">
		th{
			border-left: 0px;border-right: 0px;
		}
		td{			
			vertical-align: top;
			border: 0px;
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
		<div style="color: green;"><spring:message code="user.report.notCreated" text="Username available."/></div>
	</c:when>
	
	<c:when test="${empty report}">
		<div style="color: green;"><spring:message code="user.report.notCreated" text="Username available."/></div>
	</c:when>
	
	<c:otherwise>
	
	<div id="reportDiv" style="color: red;">
		<spring:message code="user.report.created" text="Username not available."/>
	</div>
	</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="locale" value="${locale}"/>
</body>

</html>