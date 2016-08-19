<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.revisions" text="Revisions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
		$('#linkForReport').click(function() {
			if($('#karyavaliNumber').val()=="") {
				$.prompt("Please Enter Karyavali number");
				return false;
			} else if($('#karyavaliDate').val()=="") {
				$.prompt("Please Enter Karyavali Date");
				return false;
			}
			$('#linkForReport').attr('href', 'resolution/report/generateTemporarykaryavalireport?sessionId='+$('#sessionId').val()
					+'&karyavaliNumber='+$('#karyavaliNumber').val()
					+'&karyavaliDate='+$('#karyavaliDate').val()
					+'&outputFormat=WORD');
		});
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
		<div class="fields">
			<h2></h2>
			<p>
				<label class="small"><spring:message code='resolution.temporaryKaryavali.karyavaliNumber' text='Karyavali Number'/></label>
				<input type="text" name="karyavaliNumber" id="karyavaliNumber" class="sText" value="${karyavaliNumber}">
			</p>
			<br>
			<p>
				<label class="small"><spring:message code='resolution.temporaryKaryavali.newDate' text='New Karyavali Date'/></label>
				<input type="text" name="karyavaliDate" id="karyavaliDate" class="sText datemask" value="${karyavaliDate}">
			</p>
			<p class="tright">
				<a href="javascript(0);" id="linkForReport"><spring:message code='resolution.karyavali.generateReport' text='Generate Report'/></a>
			</p>
		</div>
	</div>
	<input type="hidden" name="sessionId" id="sessionId" value="${sessionId}"/>
</body>
</html>