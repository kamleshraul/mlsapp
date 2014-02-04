<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.instructionalOrder" text="Instructional Order" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
		$(document).ready(function() {
			var resourceURL = 'bill/viewInstructionalOrder?sessionId='+$("#sessionId").val()
					+'&deviceTypeId='+$("#deviceTypeId").val();
			$('#instructionalOrder').attr("src",resourceURL);	
		});
	</script>
</head>
<body>
	<iframe id="instructionalOrder" style="width: 800px !important; height: 600px !important;">
	</iframe>	
	<input type="hidden" id="sessionId" value="${sessionId}">
	<input type="hidden" id="deviceTypeId" value="${deviceTypeId}">
</body>
</html>