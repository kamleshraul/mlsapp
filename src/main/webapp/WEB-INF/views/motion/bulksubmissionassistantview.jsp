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
	/**** Edit Motions ****/
	$(".edit").click(function(){
		editMotion($(this).attr("id"),"no");
	});
	/**** Motion's Read Only View ****/
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
			<c:if test="${! (empty assistantProcessed) }">
			<p style="color:red;margin-bottom: 15px;">${assistantProcessed} cannot be put up.Please change put up options to admission,rejection etc.</p>
			</c:if>
			<c:if test="${! (empty recommendAdmission) }">
			<p style="color:green;margin-bottom: 15px;">${recommendAdmission} sent for admission.</p>
			</c:if>
			<c:if test="${! (empty recommendRejection) }">
			<p style="color:green;margin-bottom: 15px;">${recommendRejection} sent for rejection.</p>
			</c:if>
			<table class="uiTable">
					<tr>
						<th><spring:message code="motion.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th><spring:message code="motion.number" text="Number"></spring:message></th>
						<th><spring:message code="motion.member" text="Member"></spring:message></th>
						<th><spring:message code="motion.subject" text="Subject"></spring:message></th>
						<th><spring:message code="motion.remarks" text="Remarks"></spring:message></th>
						<th><spring:message code="motion.currentstatus" text="To Be Put Up For?"></spring:message></th>
					</tr>			
					<c:forEach items="${motions}" var="i">
						<tr class="${i.fileSent}">
							<c:choose>
							<c:when test="${!i.fileSent}">
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							<a href="#" class="edit" id="edit${i.id}"><spring:message code="motion.edit" text="Edit"></spring:message></a></td>
							</c:when>
							<c:otherwise>
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
							<a href="#" class="readonly" id="edit${i.id}"><spring:message code="motion.edit" text="Edit"></spring:message></a></td>
							</c:otherwise>
							</c:choose>
							<td>${i.formatNumber()}</td>
							<td>${i.primaryMember.getFullname()}</td>
							<td>${i.subject}</td>
							<td>${i.remarks}</td>
							<td>${i.internalStatus.name}</td>
						</tr>
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="motion.nomotions" text="No Motions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="motionId" value="${motionId}">