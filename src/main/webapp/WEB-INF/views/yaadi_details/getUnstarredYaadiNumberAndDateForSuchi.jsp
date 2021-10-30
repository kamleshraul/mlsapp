<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {
				$('#yaadiNumber').change(function() {
					$.get('ref/findYaadiLayingDateForYaadi?sessionId='+$('#sessionId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val(), function(data) {
						
						var yaadiLayingDateForYaadiNumber = data.name.toString();						
						if(yaadiLayingDateForYaadiNumber!=undefined && yaadiLayingDateForYaadiNumber!='-'
								&& yaadiLayingDateForYaadiNumber!='') {
							$('#yaadiLayingDate').val(yaadiLayingDateForYaadiNumber);
						} else {	
							alert("yaadi with number " + $('#yaadiNumber').val() + "is not laid yet in this session");
							$('#yaadiNumber').val("");
							$('#yaadiLayingDate').val("");
						}				
					}).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#overlay_error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							$('#yaadiNumber').val("");
							$('#yaadiLayingDate').val("");
						}else{
							$("#overlay_error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							$('#yaadiNumber').val("");
							$('#yaadiLayingDate').val("");
						}						
					});
				});
				
				$('#linkForReport').click(function() {
					if($('#yaadiNumber').val()=="") {
						$.prompt("Please select yaadi number");
						return false;
					} else if($('#yaadiLayingDate').val()=="") {
						$.prompt("Please select yaadi laying date");
						return false;
					}
					$('#linkForReport').attr('href', 'yaadi_details/generateUnstarredSuchiReport?sessionId='+$('#sessionId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val()
							+'&yaadiLayingDate='+$('#yaadiLayingDate').val()
							+'&suchiParameter='+$('#suchiParameter').val()
							+'&outputFormat=WORD');
				});
			});
		</script>		 
	</head>	
	<body>		
		<p id="overlay_error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<p style="color: #FF0000;"><spring:message code="${error}" text="Error Occured Contact For Support."/></p>
		</c:if>
		<div class="fields clearfix watermark">
		<h3><spring:message code='question.generateUnstarredYaadiReport.getUnstarredYaadiNumberAndDate' text='Select Yaadi Number & Laying Date'/>:</h3>
		<p style="margin-top: 20px;">
			<label class="small"><spring:message code='question.unstarred_suchi_report.yaadiNumber' text='Yaadi Number'/></label>
			<input id="yaadiNumber" class="sInteger" name="yaadiNumber" value="${yaadiNumber}"/>
		</p>
		<p style="margin-top: 10px;">
			<label class="small"><spring:message code='question.unstarred_suchi_report.yaadiLayingDate' text='Yaadi Laying Date'/></label>
			<input id="yaadiLayingDate" class="sText datemask" value="${yaadiLayingDate}" readonly="readonly"/>
		</p>	
		<p style="margin-top: 10px;">
			<label class="small"><spring:message code="question.unstarred_suchi_report.suchiParameter" text="Suchi Parameter"/></label>
			<select name="suchiParameter" id="suchiParameter" class="sSelect">
				<c:choose>
				<c:when test="${houseTypeType=='upperhouse'}">
					<option value="session" selected="selected"><spring:message code='question.unstarred_suchi_report.suchiParameter.session' text='Session'/></option>
					<option value="subject"><spring:message code='question.unstarred_suchi_report.suchiParameter.subject' text='Subject'/></option>
				</c:when>
				<c:otherwise>
					<option value="subject" selected="selected"><spring:message code='question.unstarred_suchi_report.suchiParameter.subject' text='Subject'/></option>
					<option value="session"><spring:message code='question.unstarred_suchi_report.suchiParameter.session' text='Session'/></option>
				</c:otherwise>
				</c:choose>
			</select>
		</p>	
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport" style="font-size: 20px"><spring:message code='question.unstarred_suchi_report.generateReport' text='Generate Suchi Report'/></a>
			</p>
		</div>		
		</div>
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="sessionId" name="sessionId" value="${sessionId}"/>			
	</body>
</html>