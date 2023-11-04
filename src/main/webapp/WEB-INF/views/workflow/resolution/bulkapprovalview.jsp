<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$(document).ready(function(){	
		/**** Check/Uncheck Submit All ****/		
		$("#chkall").change(function(){
			if($(this).is(":checked")){
				$(".action").attr("checked","checked");	
			}else{
				$(".action").removeAttr("checked");
			}
		});	
		var continueLoop=true;
		$(".action").each(function(){
			if(continueLoop){
				if(this.disabled){
					$("#chkall").attr("disabled","disabled");
					flag=true;
				}
			}
		});
		/**** Edit Resolutions ****/
		$(".edit").click(function(){
			editResolution($(this).attr("id"),"no");
		});
		/**** Resolutions Read Only View ****/
		$(".readonly").click(function(){
			editResolution($(this).attr("id"),"yes");
		});	
		/**** Details ****/
		$(".details").click(function(){
			var id=$(this).attr("id").split("device")[1];
			viewDetails(id);
		});		
	});	
	/**** Edit Resolutions ****/
	function editResolution(id,readonly){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var id=id.split("edit")[1]; 	
		var href='workflow/resolution';
		var params="role="+$("#role").val()+"&usergroup="+$("#usergroup").val()+"&usergroupType="+
					$("#usergroupType").val()+"&bulkedit=yes"+
					"&workflowdetails="+id;
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
	/**** View Details ****/
	function viewDetails(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get('resolution/'+id+'/details',function(data){
			 $.unblockUI();				
			 $.fancybox.open(data, {autoSize: true, width: 800, height:600});
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
		<c:when test="${!(empty bulkapprovals) }">			
			<c:if test="${! (empty recommendAdmission) }">
			<p style="color:green;margin-bottom: 15px;">${recommendAdmission} sent for admission.</p>
			</c:if>
			<c:if test="${! (empty recommendRejection) }">
			<p style="color:green;margin-bottom: 15px;">${recommendRejection} sent for rejection.</p>
			</c:if>
			<c:if test="${! (empty admitted) }">
			<p style="color:green;margin-bottom: 15px;">${admitted} are admitted.</p>
			</c:if>
			<c:if test="${! (empty rejected) }">
			<p style="color:green;margin-bottom: 15px;">${rejected} are rejected.</p>
			</c:if>
			<c:if test="${! (empty recommendClarificationFromDepartment) }">
			<p style="color:green;margin-bottom: 15px;">${recommendClarificationFromDepartment} sent for clarification needed from Department.</p>
			</c:if>
			<c:if test="${! (empty recommendClarificationFromMember) }">
			<p style="color:green;margin-bottom: 15px;">${recommendClarificationFromMember} sent for clarification needed from Member.</p>
			</c:if>
			<c:if test="${! (empty clarificationFromMember) }">
			<p style="color:green;margin-bottom: 15px;">${clarificationFromMember} are Clarification Needed From Member.</p>
			</c:if>
			<c:if test="${! (empty clarificationFromDepartment) }">
			<p style="color:green;margin-bottom: 15px;">${clarificationFromDepartment} are Clarification Needed From Department.</p>
			</c:if>
			<div style="overflow: scroll;">
			<table class="uiTable">
				<tr>					
					<th style="min-width:75px;text-align:center;"><spring:message code="resolution.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
					<th style="min-width:50px;"><spring:message code="resolution.number" text="Number"></spring:message></th>
					<th style="min-width:130px;text-align:center;"><spring:message code="resolution.member" text="Member"></spring:message></th>
					<th style="min-width:350px;text-align:center;"><spring:message code="resolution.subject" text="Subject"></spring:message></th>
					<th style="min-width:70px;text-align:center;"><spring:message code="resolution.lastdecision" text="Last Decision"></spring:message></th>
					<%-- <th style="min-width:120px;text-align:center;"><spring:message code="resolution.lastremark" text="Last Remark"></spring:message></th> --%>
				</tr>			
				<c:forEach items="${bulkapprovals}" var="i">
					<tr>
							<c:choose>
								<c:when test="${i.currentStatus=='PENDING'}">
								<td style="min-width:75px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
								<a href="#" class="edit" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
								</c:when>							
								<c:otherwise>
								<td style="min-width:75px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
								<a href="#" class="readonly" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td>
								</c:otherwise>
							</c:choose>
								<%-- <td style="min-width:50px;text-align:center;">${i.deviceNumber}</td> --%>
								<td>
									<span>
											${i.deviceNumber} 
											<c:if test="${i.sentForClarification=='YES'}">
												<img src="./resources/images/sent_for_clarification.png" style="display:inline-block;" title="Sent for Clarification" width="15px" height="15px">
											</c:if>
										</span>
									
										<br/> 
										${i.member}
										<br><br>
										<%-- <spring:message code="resolution.lastdecision" text="Last Decision"/> : ${i.lastDecision}
										<br>
										<spring:message code="resolution.lastremarkby" text="Last Remark By"/> ${i.lastRemarkBy} : ${i.lastRemark} --%>
										<c:forEach items="${i.revisions}" var="j">
											<c:if test="${not empty j[7]}">
												<b>${j[0]} : ${j[6]} </b> <spring:message code="resolution.remarks" text="Remark"/>: ${j[7]}
												<br>
											</c:if>
										</c:forEach>
								</td>
								<td style="min-width:130px;">${i.member}</td>
								<td style="text-align:justify;min-width:350px;">${i.subject}</td>
								<td style="min-width:70px;text-align:justify;">${i.lastDecision}</td>	
								<%-- <td style="text-align:justify;min-width:120px;">${i.lastRemarkBy} : ${i.lastRemark}</td> --%>
								
					</tr>
				</c:forEach>
			</table>
			</div>
		</c:when>
		<c:otherwise>
			<spring:message code="resolution.noresolutions" text="No Resolutions Found"></spring:message>				
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="resolutionId" value="${resolutionId}">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
