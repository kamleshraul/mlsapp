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
		/**** Edit Motions ****/
		$(".edit").click(function(){
			editMotion($(this).attr("id"),"no");
		});
		/**** Motion's Read Only View ****/
		$(".readonly").click(function(){
			editMotion($(this).attr("id"),"yes");
		});	
		/**** Details ****/
		$(".details").click(function(){
			var id=$(this).attr("id").split("device")[1];
			viewDetails(id);
		});		
	});	
	/**** Edit Motions ****/
	function editMotion(id,readonly){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var id=id.split("edit")[1]; 			
		var href='workflow/cutmotion';
		var params="role="+$("#role").val()+"&usergroup="+$("#usergroup").val()+"&usergroupType="+
					$("#usergroupType").val()+"&bulkedit=yes"+
					"&workflowdetails="+id;
		$.get(href+"?"+params,function(data){
			$.unblockUI();	
		    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
	    },'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
		});
	    return false;
	}	
	/**** View Details ****/
	function viewDetails(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get('motion/'+id+'/details',function(data){
			 $.unblockUI();				
			 $.fancybox.open(data, {autoSize: true, width: 800, height:600});
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
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
			<div style="overflow: scroll;">
			<table class="uiTable">
				<tr>					
					<th><spring:message code="cutmotion.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
					<c:if test="${workflowSubType=='request_to_supporting_member'}">
					<th><spring:message code="cutmotion.type" text="Device"></spring:message></th>
					<th><spring:message code="cutmotion.member" text="Member"></spring:message></th>
					<th><spring:message code="cutmotion.mainTitle" text="Main Title"></spring:message></th>
					<th><spring:message code="cutmotion.lastdecision" text="Last Decision"></spring:message></th>
					<th></th>
					</c:if>
					<c:if test="${workflowSubType!='request_to_supporting_member'}">
					<th><spring:message code="cutmotion.type" text="Device"></spring:message></th>					
					<th><spring:message code="cutmotion.number" text="Number"></spring:message></th>
					<th><spring:message code="cutmotion.member" text="Member"></spring:message></th>
					<th><spring:message code="cutmotion.mainTitle" text="Main Title"></spring:message></th>
					<th><spring:message code="cutmotion.lastremark" text="Last Remark"></spring:message></th>
					<th><spring:message code="cutmotion.lastremarkby" text="Last Remark By"></spring:message></th>	
					<th><spring:message code="cutmotion.lastdecision" text="Last Decision"></spring:message></th>
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
									<td>${i.mainTitle}</td>
									<td>${i.lastDecision}</td>	
									<td>
									<a href="#" class="details" id="device${i.deviceId}"><spring:message code="cutmotion.Notice Content" text="Notice Content"></spring:message></a>
									<input type="hidden" id="sm${i.id}"  value="${i.supportingMemberId}"></td>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${i.currentStatus=='PENDING'}">
										<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
										<a href="#" class="edit" id="edit${i.id}"><spring:message code="cutmotion.edit" text="Edit"></spring:message></a></td>
									</c:when>							
									<c:otherwise>
										<td><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
										<a href="#" class="readonly" id="edit${i.id}"><spring:message code="cutmotion.edit" text="Edit"></spring:message></a></td>
									</c:otherwise>
								</c:choose>
								<td>${i.deviceType}</td>							
								<td>${i.deviceNumber}</td>
								<td>${i.member}</td>
								<td>${i.subject}</td>
								<td>${i.lastRemark}</td>
								<td>${i.lastRemarkBy}</td>
								<td>${i.lastDecision}</td>	
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
					<spring:message code="motion.nosupportingmembers" text="No Supporting Member Request Found"></spring:message>				
				</c:when>
				<c:otherwise>
					<spring:message code="motion.nomotions" text="No Motions Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="motionId" value="${motionId}">
	<input type="hidden" id="motionLevel" value="${apprLevel}" />
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
