<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="discussionmotion.bulksubmissionassisatnt" text="Bulk Put Up" /></title>
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
		        	$.post('discussionmotion/bulkapproval?items='+items,"&status="+status,		        			 		    	             
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
	<label class="small"><spring:message code="di.putupfor" text="Put up for"/></label>	
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
					<th><spring:message code="discussionmotion.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
					<th><spring:message code="discussionmotion.number" text="Number"></spring:message></th>
					<th><spring:message code="discussionmotion.member" text="Member"></spring:message></th>
					<th><spring:message code="discussionmotion.subject" text="Main Title"></spring:message></th>
					<th><spring:message code="discussionmotion.lastremark" text="Last Remark"></spring:message></th>
					<th><spring:message code="discussionmotion.lastremarkby" text="Last Remark By"></spring:message></th>	
					<th><spring:message code="discussionmotion.lastdecision" text="Last Decision"></spring:message></th>									
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
			<spring:message code="discussionmotion.nomotions" text="No Completed Motions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="size" value="${size }">	
</div>
	
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>