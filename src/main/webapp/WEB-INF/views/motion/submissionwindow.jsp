<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="adjournmentmotion.citation"	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	
	
	var timeparam = "houseType="+$("#selectedHouseType").val()
	            +"&selectedDate="+$( "#selectedSessionDate option:selected" ).val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&motionType="+$("#selectedMotionType").val()
				+"&locale="+$("#moduleLocale").val();
	
	

	$.get('motion/timeslotwindow?'+timeparam, function(data) {
		if(data) {
			
			$('#ResultDiv').html(data);					
		} else {
			$('#errorDiv').show();
		}
	},'html')
	
	$("#selectedSessionDate").change(function(){
		
		
		var timeparam = "houseType="+$("#selectedHouseType").val()
        +"&selectedDate="+$( "#selectedSessionDate option:selected" ).val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&motionType="+$("#selectedMotionType").val()
		+"&locale="+$("#moduleLocale").val();
		
		$.get('motion/timeslotwindow?'+timeparam, function(data) {
			if(data) {
				$('#errorDiv').hide();
				$('#successDiv').hide();
				$('#ResultDiv').empty();
				$('#ResultDiv').html(data);					
			} else {
				$('#errorDiv').show();
			}
		},'html')

		
		
		}); 
	
	
	
});


function process(date){
	
	   var parts = date.split("/");
	   return new Date(parts[2], parts[1] - 1, parts[0]);
	}
</script>
</head>
<body>	
	<h3>
		<label>
			<spring:message code="session.session_time_window" text="House Start And End  Time Window"/>
		</label>
	</h3>
	<h4>	
		<label style="margin-top: 20px;">
			<spring:message code="House.dates" text="Session Date"/>:
		</label>
		<select name="selectedSessionDate" id="selectedSessionDate" style="width:130px;height: 25px;">	
			<c:forEach items="${sessionDates}" var="i">
				<option value="${i[0]}" ${i[0] == CurrDate?'selected=selected':''}><c:out value="${i[1]}"></c:out></option>		
			</c:forEach>
			</select>
	</h4>
	<p>
		
	</p>
		
	<div style="margin-top: 30px;" id="ResultDiv">
		
	</div>
	
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
	<input type="hidden" id="CurrDatetest" name="CurrDate" value="${CurrDate}">
	<input type="hidden" id="usergroupTypeForSubmissionWindow" name="usergroupTypeForSubmissionWindow" value="${usergroupTypeForSubmissionWindow}">	
	<input type="hidden" id="sessionForSubmissionWindow" name="session" value="${sessionForSubmissionWindow}">
	<input type="hidden" id="motionTypeForSubmissionWindow" name="motionType" value="${motionTypeForSubmissionWindow}">

</body>
</html>