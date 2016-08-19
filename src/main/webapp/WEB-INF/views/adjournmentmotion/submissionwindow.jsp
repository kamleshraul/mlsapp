<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="adjournmentmotion.citation"	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	$("#updateSubmissionWindow").click(function(){
		$('#errorDiv').hide();
		$('#successDiv').hide();
		if($('#startTimeHour').val()==undefined || $('#startTimeHour').val()=="") {
			alert("Please enter submission start time hour!");
			return false;
		} else if($('#startTimeMinute').val()==undefined || $('#startTimeMinute').val()=="") {
			alert("Please enter submission start time minute!");
			return false;
		} else if($('#endTimeHour').val()==undefined || $('#endTimeHour').val()=="") {
			alert("Please enter submission end time hour!");
			return false;
		} else if($('#endTimeMinute').val()==undefined || $('#endTimeMinute').val()=="") {
			alert("Please enter submission end time minute!");
			return false;
		} else {			
			var params = $('#submissionWindowForm').serialize();
			$.post('adjournmentmotion/submissionwindow?'+params, function(data) {
				if(data!=undefined && data=='success') {
					$('#successDiv').show();					
				} else {
					$('#errorDiv').show();
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				$('#errorDiv').show();
			});
		}
				
	    //$.fancybox.close();	    	
	});	
});
</script>
</head>
<body>	
	<h3>
		<label>
			<spring:message code="adjournmentmotion.submissionTimeWindow" text="Submission Time Window"/>
		</label>
	</h3>
	<h4>	
		<label style="margin-top: 20px;">
			(<spring:message code="adjournmentmotion.adjourningDate" text="Adjourning Date"/>: ${formattedAdjourningDate})
		</label>
	</h4>	
	<div style="margin-top: 30px;">
		<form id="submissionWindowForm">
		<div id="errorDiv" class="toolTip tpRed clearfix" style="display: none; max-width: 355px !important; max-height: 30px !important;">
			<p style="font-size: 14px;">
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="update_failed_short_message" text="Please correct following errors."/>
			</p>
			<p></p>
		</div>
		<div id="successDiv" class="toolTip tpGreen clearfix" style="display: none; max-width: 355px !important; max-height: 30px !important;">
			<p style="font-size: 14px;">
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="update_success" text="Data saved successfully."/>
			</p>
			<p></p>
		</div>
		<p> 
			<table border="0">
				<tr>
				    <td width="48%"
				    	<label class="small"><spring:message code="adjournmentmotion.submissionwindow.startTime" text="Submission Start Time"></spring:message></label>
				    </td>
				    <td width="6%">
				    	<b>:</b>
				    </td>
				    <td width="46%">
				    	<input type="text" style="width: 20px;text-align: right;padding-right: 5px;" id="startTimeHour" name="startTimeHour" value="${startTimeHour}" ${(isAdjourningDateInPast=='yes' || usergroupTypeForSubmissionWindow=='member')?'readonly="readonly"':''} maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.hour" text="HH"></spring:message></label>
						<input type="text" style="margin-left: 15px;width: 20px;text-align: right;padding-right: 5px;" id="startTimeMinute" name="startTimeMinute" value="${startTimeMinute}" ${(isAdjourningDateInPast=='yes' || usergroupTypeForSubmissionWindow=='member')?'readonly="readonly"':''} maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.minute" text="MM"></spring:message></label>
				    </td>
				</tr>
			</table>			
		</p>	
		<p> 
			<table border="0">
				<tr>
				    <td width="48%"
				    	<label class="small"><spring:message code="adjournmentmotion.submissionwindow.endTime" text="Submission End Time"></spring:message></label>
				    </td>
				    <td width="6%">
				    	<b>:</b>
				    </td>
				    <td width="46%">
				    	<input type="text" style="width: 20px;text-align: right;padding-right: 5px;" id="endTimeHour" name="endTimeHour" value="${endTimeHour}" ${(isAdjourningDateInPast=='yes' || usergroupTypeForSubmissionWindow=='member')?'readonly="readonly"':''} maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.hour" text="HH"></spring:message></label>
						<input type="text" style="margin-left: 15px;width: 20px;text-align: right;padding-right: 5px;" id="endTimeMinute" name="endTimeMinute" value="${endTimeMinute}" ${(isAdjourningDateInPast=='yes' || usergroupTypeForSubmissionWindow=='member')?'readonly="readonly"':''} maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.minute" text="MM"></spring:message></label>
				    </td>
				</tr>
			</table>
		</p>
		<c:if test="${isAdjourningDateInPast!='yes' and usergroupTypeForSubmissionWindow!='member'}">
			<p class="tright" style="margin-top: 30px !important;">
				<input id="updateSubmissionWindow" type="button" value="<spring:message code='generic.submit' text='Update'/>" class="butDef">
			</p>
		</c:if>	
		<input type="hidden" id="sessionForSubmissionWindow" name="session" value="${sessionForSubmissionWindow}">
		<input type="hidden" id="motionTypeForSubmissionWindow" name="motionType" value="${motionTypeForSubmissionWindow}">
		<input type="hidden" id="adjourningDateForSubmissionWindow" name="adjourningDate" value="${adjourningDateForSubmissionWindow}">
		<input type="hidden" id="isAdjourningDateInPast" name="isAdjourningDateInPast" value="${isAdjourningDateInPast}">
		<input type="hidden" id="usergroupTypeForSubmissionWindow" name="usergroupTypeForSubmissionWindow" value="${usergroupTypeForSubmissionWindow}">	
		</form>
	</div>
</body>
</html>