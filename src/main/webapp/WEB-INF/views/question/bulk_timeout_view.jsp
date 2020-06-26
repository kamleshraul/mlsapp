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
		editQuestion($(this).attr("id"),"no");
	});
	/**** Question's Read Only View ****/
	$(".readonly").click(function(){
		editQuestion($(this).attr("id"),"yes");
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
/**** Edit Questions ****/
function editQuestion(id,readonly){
	var questionid=id.split("edit")[1];
	var href='question/'+questionid+'/edit';
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
		<c:when test="${!(empty questions) }">		
			<c:if test="${! (empty assistantProcessed) }">
			<p style="color:red;margin-bottom: 15px;">${assistantProcessed} cannot be put up.Please change put up options to admission,rejection etc.</p>
			</c:if>
			<c:if test="${! (empty clerkProcessed) }">
			<p style="color:green;margin-bottom: 15px;">Clerk has Processed the following questions  :- ${clerkProcessed}.</p>
			</c:if>
			<c:if test="${! (empty clarificationFromDepartment) }">
			<p style="color:green;margin-bottom: 15px;">${clarificationFromDepartment} ready to be putup on timeout successfully.</p>
			</c:if>
			<c:if test="${! (empty clarificationFromMember) }">
			<p style="color:green;margin-bottom: 15px;">${clarificationFromMember} ready to be putup on timeout successfully.</p>
			</c:if>
			<c:if test="${! (empty clarificationFromMemberAndDepartment) }">
			<p style="color:green;margin-bottom: 15px;">${clarificationFromMemberAndDepartment} ready to be putup on timeout successfully.</p>
			</c:if>
			<table class="uiTable" style="min-width:900px;">
					<tr>
						<th style="width:20px;"><spring:message code="question.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th><spring:message code="question.detail" text="Details"/></th>
						<th><spring:message code="question.subject" text="Subject"></spring:message></th>
						<th><spring:message code="question.questionText" text="Question text"></spring:message></th>
						<%-- <th><spring:message code="question.currentstatus" text="To Be Put Up For?"></spring:message></th> --%>
					</tr>			
					<c:forEach items="${questions}" var="i">
						<tr>
							<td style="width:20px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							<a href="#" class="edit" id="edit${i.id}"><spring:message code="question.edit" text="Edit"></spring:message></a></td>
							<td>
								<b><spring:message code="question.number" text="Number"/> </b>: ${i.formatNumber()} <br>
								<b><spring:message code="question.primaryMember" text="Member"/> </b> : ${i.primaryMember.getFullname()} <br>
								<b><spring:message code="question.department" text="Department"/> </b> : ${i.subDepartment.name}</td>
							<td>${i.subject}</td>
							<td>${i.questionText}</td>
							<%-- <td>${i.internalStatus.name}</td> --%>
						</tr>
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="question.noquestions" text="No Questions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="questionId" value="${questionId}">