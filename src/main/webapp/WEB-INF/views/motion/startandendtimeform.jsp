<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="adjournmentmotion.citation"	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){
	
	
	$("#updateSubmissionWindow").click(function(){
		console.log("inside")
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
			var params = "session=" + $("#sessionForSubmissionWindow").val()
			+ "&motionType=" + $("#motionTypeForSubmissionWindow").val()
			+ "&startTimeHour=" + $("#startTimeHour").val()
			+ "&startTimeMinute=" + $("#startTimeMinute").val()
			+ "&endTimeHour=" + $("#endTimeHour").val()
			+ "&endTimeMinute=" + $("#endTimeMinute").val()
			+ "&sessionDate=" + $( "#selectedSessionDate option:selected" ).val()
			+ "&usergroupTypeForSubmissionWindow=" + $( "#usergroupTypeForSubmissionWindow" ).val()
			+ "&endTimeMin=" + $("#endTimeMinute").val()
			+"&locale="+$("#moduleLocale").val();
			
		
			
			  $.post('motion/submissionwindow?'+params, function(data) {
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
		
				
	    //$.fancybox.close();	  
		}
	});
	
	 var s_Date = $( "#selectedSessionDate option:selected" ).val().split("/");

     var selDate = new Date(s_Date[2], s_Date[1] - 1, s_Date[0]).setHours(0, 0, 0, 0);
     
     var c_Date = $( "#CurrDatetest" ).val().split("/");
     
     var currDate = new Date(c_Date[2], c_Date[1] - 1, c_Date[0]).setHours(0, 0, 0, 0);
     
     if (selDate - currDate < 0) {
         console.log("inisde")
    	 $("#updateSubmissionWindow").hide();
    	 $('#startTimeHour').attr('readonly', true);
    	 $('#startTimeMinute').attr('readonly', true);
    	 $('#endTimeHour').attr('readonly', true);
    	 $('#endTimeMinute').attr('readonly', true);

     }
     else{
    	 $("#updateSubmissionWindow").show();
    	 $('#startTimeHour').attr('readonly', false);
    	 $('#startTimeMinute').attr('readonly', false);
    	 $('#endTimeHour').attr('readonly', false);
    	 $('#endTimeMinute').attr('readonly', false);
     }
	
});	
	

</script>
</head>
<body>	

		<form id="submissionWindowForm">
		
		<p> 
			<table border="0">
				<tr>
				    <td width="48%"
				    	<label class="small"><spring:message code="House.startTime" text=" Start Time"></spring:message></label>
				    </td>
				    <td width="6%">
				    	<b>:</b>
				    </td>
				    <td width="46%">
				    	<input type="text" style="width: 20px;text-align: right;padding-right: 5px;" id="startTimeHour" name="startTimeHour" value="${startTimeHour}"  maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.hour" text="HH"></spring:message></label>
						<input type="text" style="margin-left: 15px;width: 20px;text-align: right;padding-right: 5px;" id="startTimeMinute" name="startTimeMinute" value="${startTimeMinute}"  maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.minute" text="MM"></spring:message></label>
				    </td>
				</tr>
			</table>			
		</p>	
		<p> 
			<table border="0">
				<tr>
				    <td width="48%"
				    	<label class="small"><spring:message code="House.endTime" text="End Time"></spring:message></label>
				    </td>
				    <td width="6%">
				    	<b>:</b>
				    </td>
				    <td width="46%">
				    	<input type="text" style="width: 20px;text-align: right;padding-right: 5px;" id="endTimeHour" name="endTimeHour" value="${endTimeHour}"  maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.hour" text="HH"></spring:message></label>
						<input type="text" style="margin-left: 15px;width: 20px;text-align: right;padding-right: 5px;" id="endTimeMinute" name="endTimeMinute" value="${endTimeMinute}"  maxlength="2"/>
						<label style="margin-left: 2px;"><spring:message code="generic.minute" text="MM"></spring:message></label>
				    </td>
				</tr>
			</table>
		</p>
		
			<p class="tright" style="margin-top: 30px !important;">
				<input id="updateSubmissionWindow" type="button" value="<spring:message code='generic.submit' text='Update'/>" class="butDef">
			
			</p>
		

		
		</form>
	
</body>
</html>