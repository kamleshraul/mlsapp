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
	var href='standalonemotion/'+questionid+'/edit';
	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
	var params="role="+$("#ydrole").val()+"&usergroup="+$("#ydusergroup").val()+"&usergroupType="+
				$("#ydusergroupType").val()+"&bulkedit=yes"
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
			<h3>${success} ${failure}</h3>
			<table class="uiTable">
					<tr>
						<th><spring:message code="question.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th><spring:message code="question.number" text="Number"></spring:message></th>
						<th><spring:message code="question.member" text="Member"></spring:message></th>
						<th><spring:message code="question.subject" text="Subject"></spring:message></th>
						<th><spring:message code="question.currentstatus" text="To Be Put Up For?"></spring:message></th>
					</tr>			
					<c:forEach items="${questions}" var="i">
						<tr>
							
							<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" style="margin-right: 10px;">			
							<a href="#" class="readonly" id="edit${i.id}"><spring:message code="question.edit" text="Edit"></spring:message></a></td>
							<td>${i.formatNumber()}</td>
							<td>${i.primaryMember.getFullname()}</td>
							<td>${i.subject}</td>
							<td>${i.internalStatus.name}</td>
						</tr>
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="question.noquestions" text="No Questions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="questionId" value="${questionId}">