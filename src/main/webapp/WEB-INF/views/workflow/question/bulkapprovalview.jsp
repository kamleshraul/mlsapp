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
		/**** Edit Question ****/
		$(".edit").click(function(){
			editQuestion($(this).attr("id"),"no");
		});
		/**** Question's Read Only View ****/
		$(".readonly").click(function(){
			editQuestion($(this).attr("id"),"yes");
		});	
		/**** Details ****/
		$(".details").click(function(){
			var id=$(this).attr("id").split("device")[1];
			viewDetails(id);
		});		
	});	
	/**** Edit Questions ****/
	function editQuestion(id,readonly){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var id=id.split("edit")[1]; 			
		var href='workflow/question';
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
		$.get('question/'+id+'/details',function(data){
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
	<h4 id="error_p">&nbsp;</h4>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
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
	<c:choose>
		<c:when test="${!(empty bulkapprovals) }">			
			
			<div style="overflow: scroll;">
			<table class="uiTable">
				<tr>					
					<th style="min-width:75px;text-align:center;"><spring:message code="question.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
					<c:if test="${workflowSubType=='request_to_supporting_member'}">
					<th><spring:message code="question.type" text="Device"></spring:message></th>
					<th><spring:message code="question.member" text="Member"></spring:message></th>
					<th><spring:message code="question.subject" text="Subject"></spring:message></th>
					<th><spring:message code="question.lastdecision" text="Last Decision"></spring:message></th>
					<th></th>
					</c:if>
					<c:if test="${workflowSubType!='request_to_supporting_member'}">
					<th style="min-width:140px;text-align:justify;"><spring:message code="question.number" text="Number"></spring:message></th>
					<%-- <th style="text-align:justify;min-width:150px;"><spring:message code="question.member" text="Member"></spring:message></th> --%>
					<th style="text-align:justify;min-width:200px;"><spring:message code="question.subject" text="Subject"></spring:message></th>
					<th style="text-align:justify;min-width:200px;"><spring:message code="question.questiontext" text="Question Text"></spring:message></th>
					<th style="min-width:70px;text-align:justify;"><spring:message code="question.lastdecision" text="Last Decision"></spring:message></th>
					<th style="text-align:justify;min-width:120px;"><spring:message code="question.lastremarkby" text="Last Remark By"></spring:message></th>	
					
					</c:if>									
				</tr>			
				<c:forEach items="${bulkapprovals}" var="i">
					<tr>
						<c:choose>
						<c:when test="${workflowSubType=='request_to_supporting_member'}">
							<c:choose>
								<c:when test="${i.currentStatus=='PENDING'}">
								<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
								</c:when>							
								<c:otherwise>
								<td style="text-align: center;"><span style="font-weight: bolder;color: green;font-size: 18px;">&#x2713;</span></td>
								</c:otherwise>
							</c:choose>
								<td>${i.deviceType}</td>
								<td>${i.member}</td>
								<td>${i.subject}</td>
								<td>${i.lastDecision}</td>	
								<td>
								<a href="#" class="details" id="device${i.deviceId}"><spring:message code="motion.details" text="Details"></spring:message></a>
								<input type="hidden" id="sm${i.id}"  value="${i.supportingMemberId}"></td>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${i.currentStatus=='PENDING'}">
								<td style="min-width:75px;text-align:center;"> <input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
								<a href="#" class="edit" id="edit${i.id}"><spring:message code="motion.edit" text="Edit"></spring:message></a></td>
								</c:when>							
								<c:otherwise>
								<td style="min-width:75px;text-align:center;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
								<a href="#" class="readonly" id="edit${i.id}"><spring:message code="motion.edit" text="Edit"></spring:message></a></td>
								</c:otherwise>
							</c:choose>
								<%-- <td>${i.deviceType}</td> --%>							
								<td style="min-width:140px;text-align:justify;">${i.deviceNumber} <br> ${i.member}</td>
								<%-- <td style="text-align:justify;min-width:150px;">${i.member}</td> --%>
								<td style="text-align:justify;min-width:200px;">${i.subject}</td>
								<td style="text-align:justify;min-width:200px;">${i.briefExpanation}</td>
								<td style="min-width:70px;text-align:justify;">${i.lastDecision}</td>
								<td style="text-align:justify;min-width:120px;">${i.lastRemarkBy} : ${i.lastRemark}</td>
									
						</c:otherwise>
						</c:choose>									
					</tr>
				</c:forEach>
			</table>
			</div>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${workflowSubType=='request_to_supporting_member'}">
					<spring:message code="question.nosupportingmembers" text="No Supporting Member Request Found"></spring:message>				
				</c:when>
				<c:otherwise>
					<spring:message code="question.noquestions" text="No Questiosn Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="questionId" value="${questionId}">
	<input type="hidden" id="questionLevel" value="${apprLevel}" />
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
