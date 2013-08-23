<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	var continueLoop=true;
	$(".action").each(function(){
		if(continueLoop){
			if(this.disabled){
				$("#chkall").attr("disabled","disabled");
				flag=true;
			}
		}
	});
	/**** Edit Resolution ****/
	$(".edit").click(function(){
		editResolution($(this).attr("id"),"no");
	});
	/**** Resolutions Read Only View ****/
	$(".readonly").click(function(){
		editResolution($(this).attr("id"),"yes");
	});
	/**** Check/Uncheck Submit All ****/		
	$("#chkall").change(function(){
		if($(this).is(":checked")){
			$(".action").attr("checked","checked");	
		}else{
			$(".action").removeAttr("checked");
		}
	});
});
/**** Edit Resolution ****/
function editResolution(id,readonly){
	var resolutionid=id.split("edit")[1];
	var href='resolution/'+resolutionid+'/edit';
	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
	var params="role="+$("#assirole").val()+"&usergroup="+$("#assiusergroup").val()+"&usergroupType="+
				$("#assiusergroupType").val()+"&bulkedit=yes"
				+"&readonly="+readonly;
	$.get(href+"?"+params,function(data){
		$.unblockUI();	
	    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
    },'html').fail(function(){
		if($("#ErrorMsg").val()!=''){
			$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		}else{
			$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		}
		scrollTop();
	});
    return false;
}
</script>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty resolutions) }">		
			<c:if test="${! (empty assistantProcessed) }">
			<p style="color:red;margin-bottom: 15px;">${assistantProcessed} cannot be put up.Please change put up options to admission,rejection etc.</p>
			</c:if>
			<c:if test="${! (empty recommendAdmission) }">
			<p style="color:green;margin-bottom: 15px;">${recommendAdmission} sent for admission.</p>
			</c:if>
			<c:if test="${! (empty recommendRejection) }">
			<p style="color:green;margin-bottom: 15px;">${recommendRejection} sent for rejection.</p>
			</c:if>
			<c:if test="${! (empty recommendClarificationFromDepartment) }">
			<p style="color:green;margin-bottom: 15px;">${recommendAdmission} sent for admission.</p>
			</c:if>
			<c:if test="${! (empty recommendClarificationFromMember) }">
			<p style="color:green;margin-bottom: 15px;">${recommendRejection} sent for rejection.</p>
			</c:if>
			<table class="uiTable">
					<tr>
						<th><spring:message code="resolution.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th><spring:message code="resolution.number" text="Number"></spring:message></th>
						<th><spring:message code="resolution.member" text="Member"></spring:message></th>
						<th><spring:message code="resolution.subject" text="Subject"></spring:message></th>
						<th><spring:message code="resolution.currentstatus" text="To Be Put Up For?"></spring:message></th>
					</tr>			
					<c:forEach items="${resolutions}" var="i">
					<c:if test="${hType=='lowerhouse'}">
						<tr class="${i.fileSentLowerHouse}">
							<c:choose>
							<c:when test="${i.internalStatusLowerHouse.type=='resolution_system_putup'||!i.fileSentLowerHouse}">
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							<a href="#" class="edit" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
							</c:when>
							<c:otherwise>
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
							<a href="#" class="readonly" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
							</c:otherwise>
							</c:choose>
							<td>${i.formatNumber()}</td>
							<td>${i.member.getFullname()}</td>
							<td>${i.subject}</td>
							<td>${i.internalStatusLowerHouse.name}</td>
						</tr>
					</c:if>
					<c:if test="${hType=='upperhouse'}">
						<tr class="${i.fileSentUpperHouse}">
							<c:choose>
							<c:when test="${i.internalStatusUpperHouse.name=='resolution_system_putup'||!i.fileSentUpperHouse}">
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							<a href="#" class="edit" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
							</c:when>
							<c:otherwise>
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
							<a href="#" class="readonly" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
							</c:otherwise>
							</c:choose>
							<td>${i.formatNumber()}</td>
							<td>${i.member.getFullname()}</td>
							<td>${i.subject}</td>
							<td>${i.internalStatusUpperHouse.name}</td>
						</tr>
					</c:if>
					</c:forEach>
			</table>
			<c:out value="Total Count = ${resolutions.size() }"></c:out>
		</c:when>
		<c:otherwise>
			<spring:message code="resolution.noresolutions" text="No Resolutions Found"></spring:message>
		</c:otherwise>
	</c:choose>
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
	<input type="hidden" id="resolutionId" value="${resolutionId}">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>