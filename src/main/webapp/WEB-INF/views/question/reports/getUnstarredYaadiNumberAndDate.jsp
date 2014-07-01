<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				$('.datemask').focus(function(){		
					$(".datemask").mask("99/99/9999");
				});				
				$('#linkForReport').css('font-size','20px');
				
				$('#linkForReport').click(function() {
					if($('#yaadiNumber').val()=="") {
						alert("Please select yaadi number");
						return false;
					} else if($('#yaadiLayingDate').val()=="") {
						alert("Please select yaadi laying date");
						return false;
					}
					$('#linkForReport').attr('href', 'question/report/generateUnstarredYaadiReport?sessionId='+$('#sessionId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val()
							+'&yaadiLayingDate='+$('#yaadiLayingDate').val()
							+'&outputFormat=WORD');
				});
			});
		</script>		 
	</head>	
	<body>		
		<p id="error_p" style="display: none;">&nbsp;</p>
		<h3><spring:message code='question.generateUnstarredYaadiReport.getUnstarredYaadiNumberAndDate' text='Select Yaadi Number & Laying Date'/>:</h3>
		<p style="margin-top: 20px;">
			<label style="width: 150px;margin-right: 42px;"><spring:message code='question.unstarred_yaadi_report.yaadiNumber' text='Yaadi Number'/></label>
			<input id="yaadiNumber" class="sInteger" name="yaadiNumber" value="${yaadiNumber}"/>
		</p>
		<p style="margin-top: 20px;">
			<label style="width: 150px;margin-right: 20px;"><spring:message code='question.unstarred_yaadi_report.yaadiLayingDate' text='Yaadi Laying Date'/></label>
			<input id="yaadiLayingDate" class="datemask sText" name="yaadiLayingDate" value="${yaadiLayingDate}"/>
		</p>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport"><spring:message code='question.unstarred_yaadi_report.generateReport' text='Generate Report'/></a>
			</p>
		</div>		
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="sessionId" name="sessionId" value="${sessionId}"/>				
	</body>
</html>