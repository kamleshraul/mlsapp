<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.schedule7OfConstitution" text="Schedule 7 Of Constitution" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
		$(document).ready(function() {
			var resourceURL = 'bill/viewSchedule7OfConstitution?sessionId='+$("#sessionId").val()
					+'&deviceTypeId='+$("#deviceTypeId").val()+'&language='+$("#language").val();
			$('#schedule7ofconstitution').attr("src",resourceURL);	
		});
	</script>
</head>
<body>
	<iframe id="schedule7ofconstitution" style="width: 800px !important; height: 600px !important;">
	</iframe>	
	<input type="hidden" id="language" value="${language}">
	<input type="hidden" id="sessionId" value="${sessionId}">
	<input type="hidden" id="deviceTypeId" value="${deviceTypeId}">
</body>
</html>