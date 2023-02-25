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
	/**** Edit Questions ****/
	$(".edit").click(function(){
		editMotion($(this).attr("id"),"no");
	});
	/**** Question's Read Only View ****/
	$(".readonly").click(function(){
		editMotion($(this).attr("id"),"yes");
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
/**** Edit Motions ****/
function editMotion(id,readonly){
	var motionid=id.split("edit")[1];
	var href='motion/'+motionid+'/edit';
	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
	var params="role="+$("#assirole").val()+"&usergroup="+$("#assiusergroup").val()+"&usergroupType="+
				$("#assiusergroupType").val()+"&bulkedit=yes"
				+"&readonly="+readonly;
	$.get(href+"?"+params,function(data){
		$.unblockUI();	
	    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
    },'html').fail(function(){
		$.unblockUI();
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
		<c:when test="${!(empty motions) }">		
			<c:if test="${! (empty childDevices) }">
			<p style="color:red;margin-bottom: 15px;">${childDevices} cannot be processed. Please unclub or reverse club these devices and then process</p>
			</c:if>
			<c:if test="${! (empty finalAdmittedDevices) }">
			<p style="color:green;margin-bottom: 15px;">${finalAdmittedDevices} updated with final admission decision.</p>
			</c:if>
			<c:if test="${! (empty finalRejectedDevices) }">
			<p style="color:green;margin-bottom: 15px;">${finalRejectedDevices} updated with final rejection decision.</p>
			</c:if>
			<c:if test="${! (empty finalClarificationFromDeptDevices) }">
			<p style="color:green;margin-bottom: 15px;">${finalClarificationFromDeptDevices} sent for Clarification.</p>
			</c:if>
			<div style="max-width:900px; overflow: scroll;">
				<table class="uiTable">
						<tr>
							<th><spring:message code="motion.submitall" text="Submit All"></spring:message>
							<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
							<th style="max-width: 150px;"><spring:message code="motion.number" text="Number"></spring:message></th>
							<th><spring:message code="motion.member" text="Member"></spring:message></th>
							<th><spring:message code="motion.subject" text="Subject"></spring:message></th>
							<th><spring:message code="motion.remarks" text="Remarks"></spring:message></th>
							<th><spring:message code="motion.currentstatus" text="Current Status"></spring:message></th>
						</tr>			
						<c:forEach items="${motions}" var="i">
							<tr>
								<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
								<a href="#" class="edit" id="edit${i.id}"><spring:message code="motion.view" text="View"></spring:message></a></td>
								<td>${i.formatNumber()}</td>
								<td>${i.primaryMember.getFullname()}</td>
								<td>${i.subject}</td>
								<td>${i.remarks}</td>
								<td>${i.internalStatus.name}</td>
							</tr>
						</c:forEach>
				</table>
			</div>
		</c:when>
		<c:otherwise>
			<spring:message code="motion.nomotions" text="No Motions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="motionId" value="${motionId}">