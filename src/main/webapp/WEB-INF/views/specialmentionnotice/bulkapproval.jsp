<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmission" text="Bulk Put Up" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){		
		$("#chkall").change(function(){
			if($(this).is(":checked")){
				$(".action").attr("checked","checked");	
			}else{
				$(".action").removeAttr("checked");
			}
		});
		$("#bulksubmit").click(function(){
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var items=new Array();
					$(".action").each(function(){
						if($(this).is(":checked")){
						items.push($(this).attr("id").split("chk")[1]);
						}
					});
					var status=$("#selectedInternalStatus").val();
					if(status!='-'){
		        	$.post('workflow/specialmentionnotice/bulkapproval?items='+items,"&status="+status,		        			 		    	             
		    	            function(data){
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	
		    					$("#bulkSubmissionDiv").html(data);	
		    	            },'html').fail(function(){
		    	    			$.unblockUI();
		    	    			if($("#ErrorMsg").val()!=''){
		    	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    	    			}else{
		    	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    	    			}
		    	    			scrollTop();
		    	    		});
					}
		        }
			}});	
		});
	});		
	</script>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div id="bulkSubmissionDiv" style="overflow: scroll;">	
	<c:choose>
		<c:when test="${!(empty bulkapprovals) }">
	<p>
	<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>	
	<select id="selectedInternalStatus" class="sSelect">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
	</c:forEach>
	</select>
	<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</p>
			<table class="uiTable">
				<tr>
					<th><spring:message code="resolution.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
					<th><spring:message code="resolution.number" text="Number"></spring:message></th>
					<th><spring:message code="resolution.member" text="Member"></spring:message></th>
					<th><spring:message code="resolution.subject" text="Subject"></spring:message></th>
					<th><spring:message code="resolution.lastremark" text="Last Remark"></spring:message></th>
					<th><spring:message code="resolution.lastremarkby" text="Last Remark By"></spring:message></th>	
					<th><spring:message code="resolution.lastdecision" text="Last Decision"></spring:message></th>									
				</tr>			
				<c:forEach items="${bulkapprovals}" var="i">
					<tr>
						<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td>
						<td><a id="dv${i.deviceId}" class="device">${i.deviceNumber}</a></td>
						<td>${i.member}</td>
						<td>${i.subject}</td>
						<td>${i.lastRemark}</td>
						<td>${i.lastRemarkBy}</td>
						<td>${i.lastDecision}</td>						
					</tr>
				</c:forEach>
			</table>
		
		</c:when>
		<c:otherwise>
			<spring:message code="specialmentionnotice.nospecialmentionnotices" text="No Completed Special Mention Notices Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="size" value="${size }">	
</div>
		<!-- --------------------------PROCESS VARIABLES -------------------------------- -->
	
	<input id="mailflag" name="mailflag" value="${pv_mailflag}" type="hidden">
	<input id="timerflag" name="timerflag" value="${pv_timerflag}" type="hidden">
	<input id="reminderflag" name="reminderflag" value="${pv_reminderflag}" type="hidden">	
	
	<!-- mail related variables -->
	<input id="mailto" name="mailto" value="${pv_mailto}" type="hidden">
	<input id="mailfrom" name="mailfrom" value="${pv_mailfrom}" type="hidden">
	<input id="mailsubject" name="mailsubject" value="${pv_mailsubject}" type="hidden">
	<input id="mailcontent" name="mailcontent" value="${pv_mailcontent}" type="hidden">
	
	<!-- timer related variables -->
	<input id="timerduration" name="timerduration" value="${pv_timerduration}" type="hidden">
	<input id="lasttimerduration" name="lasttimerduration" value="${pv_lasttimerduration}" type="hidden">	
	
	<!-- reminder related variables -->
	<input id="reminderto" name="reminderto" value="${pv_reminderto}" type="hidden">
	<input id="reminderfrom" name="reminderfrom" value="${pv_reminderfrom}" type="hidden">
	<input id="remindersubject" name="remindersubject" value="${pv_remindersubject}" type="hidden">
	<input id="remindercontent" name="remindercontent" value="${pv_remindercontent}" type="hidden">
	<input id="submissionMsg" value="<spring:message code='resolutions.client.prompt.submit' text='Do you want to submit the resolutions'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>