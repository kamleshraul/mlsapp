<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.statusreport" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
	</script>
	 <style type="text/css">
        @media all{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 750px;
	        	padding: 10px;
	        }	        
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="statusReportDiv">
		<%@ include file="/common/info.jsp" %>
				<c:choose>
					<c:when test="${report==null}">
						<spring:message code="question.statusreport" text="Status Report"/>
					</c:when>
					<c:when test="${empty report}">
						<spring:message code="question.statusreport" text="Status Report"/>
					</c:when>
					<c:otherwise>
						${report[0][0]}
					</c:otherwise>
				</c:choose>
	</div>
</div>
<input type="hidden" id="device" value="${device}" />
</body>
</html>