<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="motion.statusupdate" text="Mark as discussed" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){		
		/**** Load devices with input deviceNumbers ****/
		$("#loadDevices").click(function(){
			viewContent();
		});	
		/**** Load Actors On Changing Status ****/
		$("#updateStatus").change(function(){
			
		});
		/**** Bulk Put Up ****/
		$("#bulksubmit").click(function(){
			bulkUpdate();			
		});	
	});		
	/**** Display Motion File or Status Wise ****/
	function viewContent(){
		if($('#deviceNumbers').val()=="") {
			$.prompt("Please input atlease one device number!");
			$('#updateStatus').val("-");
			$('#updateStatusSpan').hide();
			$('#bulkResultDiv').empty();
			$('#remarks').val("");
			$('#remarksP').hide();
			$('#bulksubmit').hide();
			return false;
		} 
		else {
			$('#updateStatusSpan').show();
			$('#remarksP').show();
			$('#bulksubmit').show();
			
			var parameters = "houseType="+$("#houseTypeDU").val()
			 +"&session="+$("#sessionDU").val()
			 +"&deviceType="+$("#deviceTypeDU").val()
			 +"&deviceNumbers="+$("#deviceNumbers").val();
		
			var resource='poster_activities/update_decision/view_devices';
			var resourceURL=resource+"?"+parameters;
			$.get(resourceURL,function(data){
				$("#bulkResultDiv").empty();
				$("#bulkResultDiv").html(data);
			},'html')
			.fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});				 
		}		
	}	
	
	function bulkUpdate(){
		
		var items=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
			items.push($(this).attr("id").split("chk")[1]);
			}
		});		
		if(items.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}	
		var decisionStatus = $("#updateStatus").val();
		console.log("decisionStatus: "+decisionStatus);
		if(decisionStatus=="-") {
			$.prompt('Please select the final decision to be taken!');
			return false;
		}
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('poster_activities/update_decision/process',
			        	{items:items
			        	,decisionStatus:decisionStatus
					 	,houseType:$("#houseTypeDU").val()
					 	,session:$("#sessionDU").val()
					 	,deviceType:$("#deviceTypeDU").val()
			        	,deviceNumbers:$("#deviceNumbers").val()
			        	,remarks:$("#remarks").val()
					 	},
	    	            function(data){
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');	
	    					$.unblockUI();	
	    					$("#bulkResultDiv").empty();	
	    					$("#bulkResultDiv").html(data);	
	    	            }
	    	            ,'html').fail(function(){
	    					$.unblockUI();
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});
	        	}}});
	}		
	</script>
	<style type="text/css">	
	.true{
	}
	</style>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<%@ include file="/common/info.jsp" %>
	<h3><spring:message code="${device}.updateDecision" text='Update Decision for Devices'/></h3>
	<hr>
	
	<p>
		<label class="centerlabel"><spring:message code="${device}.numbers" text="Device Numbers"/></label>
		<textarea id="deviceNumbers" rows="2" cols="30" style="margin-left: 10px;"></textarea>
		
		<input type="button" id="loadDevices" class="centerlabel" value="<spring:message code='generic.updateDecision' text='Update Decision'/>"  style="width: 120px;margin: 10px;"/>
	
		<span id="updateStatusSpan" class="centerlabel" style="margin-left: 20px;display: none;">
			<label class="small"><spring:message code="${device}.putupfor" text="Put up for"/></label>	
			<select id="updateStatus" class="sSelect">
				<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
				<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
				</c:forEach>
			</select>
		</span>
	</p>
	
	<div id="bulkResultDiv">
	</div>	
	
	<p id="remarksP" style="display: none;">
		<label class="centerlabel"><spring:message code="question.remarks" text="Remarks"/></label>
		<textarea id="remarks" rows="2" cols="30"></textarea>
	</p>
	
	<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;display: none;"/>
	
	<input type="hidden" id="houseTypeDU" value="${houseType}">
	<input type="hidden" id="sessionDU" value="${session}">
	<input type="hidden" id="deviceTypeDU" value="${deviceType}">
	<%-- <input type="hidden" id="ydstatus" value="${status}">
	<input type="hidden" id="ydrole" value="${role}">
	<input type="hidden" id="ydusergroup" value="${usergroup}">
	<input type="hidden" id="ydusergroupType" value="${usergroupType}"> --%>
	<input id="submissionMsg" value="<spring:message code='client.prompt.updatedecision.submit' text='Are you sure to update the decisions?'></spring:message>" type="hidden">
	<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
	<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 question to continue..'></spring:message>" type="hidden">		
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>