<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$(document).ready(function(){	
	   
		$("#bulkSubmitAdmissionNumberChange").click(function(){
			bulkUpdateAdmissionNumberChange();			
		});
		
		$("#chkall").change(function(){
			if($(this).is(":checked")){
				$(".action").attr("checked","checked");	
			}else{
				$(".action").removeAttr("checked");
			}
		});	
		
		/**** Register Report Generation ****/
		$("#smis_register_admission_number_report").click(function() {
			$(this).attr('href','#');
			console.log("Register Report");
			generateRegisterReport();
		});
		
	});
	
	function generateRegisterReport() {
		var selectedSpecialMentionNoticeDate = $('#selectedSpecialMentionNoticeDate').val();
		$("#smis_register_admission_number_report").attr('href',
				'specialmentionnotice/report/register?'
				+'specialMentionNoticeDate=' + selectedSpecialMentionNoticeDate
				+'&sessionId=' + $("#loadedSession").val()
				+'&reportQueryName=SMIS_REGISTER_REPORT');
	}
	
	function bulkUpdateAdmissionNumberChange(){
		var items=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
					items.push($(this).attr("id").split("chk")[1]);					
				}			
		});
		
		var admission=new Array();
		for (var i=0; i<items.length; i++) {		
			admission.push({'id':items[i] , 'admissionNumber':$("#admissionNumbers_"+items[i]).val()});
		}
		
		var file=$("#selectedFileCount").val();	
	    $.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.ajax({url: 'specialmentionnotice/bulkadmissionnoticenumberapproval/update', 
					data: {						
						 items:admission,
						 itemLength: admission.length,
						 houseType:$("#selectedHouseType").val(),
							sessionYear:$("#selectedSessionYear").val(),
							sessionType:$("#selectedSessionType").val(),
							deviceType:$("#selectedDeviceType").val(),
							usergroup:$("#currentusergroup").val(),
							usergroupType:$("#currentusergroupType").val(),
							itemsCount:$("#selectedItemsCount").val(),
							file:file
					},
					type: 'POST',
					async: false,
					success: function(data) {	
						$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');	
    					$.unblockUI();	
    					$("#bulkResultDiv").empty();	
    					$("#bulkResultDiv").html(data);
					},
					error: function(data) {
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.");
						}
					}
				});
	        	}}}); 
	}
</script>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	
	<br/><br/>
	
<div id="bulkResultDiv">
<a href="#" id="smis_register_admission_number_report" class="butSim">
	<spring:message code="smis.rejected_report" text="Register Report"/>
</a>	
<br/><br/>
<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<%@ include file="/common/info.jsp" %>
	<c:choose>
		<c:when test="${!(empty bulkapprovals) }">			
		  <div style="overflow: scroll;">
			<table class="uiTable">
				<tr>					
					<th style="min-width:75px;text-align:center;"><spring:message code="resolution.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>			
					<th style="min-width:50px;"><spring:message code="resolution.Date" text="Date"></spring:message></th>		
					<th style="min-width:50px;"><spring:message code="specialmentionnotice.number" text="Number"></spring:message></th>
					<th style="min-width:130px;text-align:center;"><spring:message code="resolution.member" text="Member"></spring:message></th>
					<th style="min-width:350px;text-align:center;"><spring:message code="resolution.subject" text="Subject"></spring:message></th>
					<th style="min-width:50px;text-align:center;"><spring:message code="resolution.admissionNumberChange" text="Admission Number Change"></spring:message></th>
				</tr>			
				<c:set var="index" value="1"></c:set>
				<c:forEach items="${bulkapprovals}" var="i"  varStatus="status">
					<tr id="row${index}">
						     	<td style="min-width:75px;" class="chk"><input type="checkbox" id="chk${i.deviceId}" name="chk${i.deviceId}" class="sCheck action" value="true"  style="margin-right: 10px;"></td>
								<td style="min-width:50px;">${i.deviceDate}</td>
								<td style="min-width:50px;">${i.deviceNumber}</td>
								<td style="min-width:130px;">${i.member}</td>
								<td style="text-align:justify;min-width:350px;">${i.subject}</td>
															<td style="min-width:50px;">
								 <select name="admissionNumbers" id="admissionNumbers_${i.deviceId}" class="sSelect" style="width: 270px;">
								    <option value="${defaultSubmissionPriority}"><spring:message code="please.select" text="Please select the option"/></option>
									<c:forEach items="${bulkapprovals }" var="j">
									     <c:choose>
									        <c:when test="${not empty i.supportingMemberId and i.supportingMemberId == j.supportingMemberId}">
									             <option value="${j.supportingMemberId}" selected="selected">${j.deviceAdmissionNumber}</option>
									        </c:when>
									        <c:otherwise>
									            <option value="${j.supportingMemberId}">${j.deviceAdmissionNumber}</option>
									        </c:otherwise>
									     </c:choose>							 			 
									</c:forEach>
								</select>
						</td>
					</tr>
					<c:set var="index" value="${index+1}"></c:set>	
				</c:forEach>
			</table>
			<br/>
			 <p>
				<input type="button" id="bulkSubmitAdmissionNumberChange" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
			</p>
			</div>
		</c:when>
		<c:otherwise>
			<spring:message code="resolution.noresolutions" text="No Special Mention Notices Found"></spring:message>				
		</c:otherwise>
	</c:choose>
<%-- 	<input type="hidden" id="resolutionId" value="${resolutionId}"> --%>
	<input id="submissionMsg" value="<spring:message code='specialmentionnotice.client.prompt.submit' text='Do you want change the admission Number of Special Mention Notice ?'></spring:message>" type="hidden">
<%-- 	<input id="lapseMsg" value="<spring:message code='specialmentionnotice.client.prompt.lapse' text='Do you want to lapse the Special Mention Notice?'></spring:message>" type="hidden"> --%>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>	
