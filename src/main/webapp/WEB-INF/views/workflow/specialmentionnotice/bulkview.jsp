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
		
		$(".up").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];	
			if(index!=1){		
				var currChk=$("#row"+index+" .chk").html();	
				var currSub=$("#row"+index+" .admissionNumber").html();	
				var prevChk=$("#row"+(index-1)+" .chk").html();	
				var prevSub=$("#row"+(index-1)+" .admissionNumber").html();
				$("#row"+index+" .chk").html(prevChk);
				$("#row"+index+" .admissionNumber").html(prevSub);
				$("#row"+(index-1)+" .chk").html(currChk);
				$("#row"+(index-1)+" .admissionNumber").html(currSub);
			}
		});
		
		$(".down").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#row"+index+" .chk").html();
				var currSub=$("#row"+index+" .admissionNumber").html();
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#row"+nextIndex+" .chk").html();
				var nextSub=$("#row"+nextIndex+" .admissionNumber").html();
				$("#row"+index+" .chk").html(nextChk);
				$("#row"+index+" .admissionNumber").html(nextSub);
				$("#row"+nextIndex+" .chk").html(currChk);
				$("#row"+nextIndex+" .admissionNumber").html(currSub);
			}
		});
		
		$("#bulksubmit").click(function(){
			/* if($("#selectedAction").val() == "admissionNumberChange"){
				bulkUpdate();
			}else if($("#selectedAction").val() == "toLapse"){ */
				bulkLapse();	
		/* 	} */
			/* else{
				alert("Some Error occured. Please contact Administrator");
			} */
		});		
	});	
	/**** Edit Resolutions ****/
	function editResolution(id,readonly){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var id=id.split("edit")[1]; 	
		var href='workflow/specialmentionnotice';
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
		$.get('specialmentionnotice/'+id+'/details',function(data){
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
	
	function bulkUpdate(){
		var items=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
					items.push($(this).attr("id").split("chk")[1]);					
				}			
		});
		
		var admission=new Array();
		for (var i=0; i<items.length; i++) {		
			admission.push({'id':items[i] , 'admissionNumber':$(".am_"+items[i]).get(0).innerText});
		}
		
		var file=$("#selectedFileCount").val();	
	    $.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.ajax({url: 'workflow/specialmentionnotice/bulkadmissionapproval/update', 
					data: {						
						 items:admission,
						 itemLength: items.length,
						 houseType:$("#selectedHouseType").val(),
							sessionYear:$("#selectedSessionYear").val(),
							sessionType:$("#selectedSessionType").val(),
							deviceType:$("#selectedDeviceType").val(),
							status:$("#selectedStatus").val(),
							workflowSubType:$("#selectedSubWorkflow").val(),//departmentwise bulk for cutmotion and other department based devices
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
	
	function bulkLapse(){
		var items=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
					items.push({'id':$(this).attr("id").split("chk")[1]});					
				}			
		});
		
		var file=$("#selectedFileCount").val();	
	    $.prompt($('#lapseMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.ajax({url: 'workflow/specialmentionnotice/bulklapse/update', 
					data: {						
						 items:items,
						 itemLength: items.length,
						 houseType:$("#selectedHouseType").val(),
							sessionYear:$("#selectedSessionYear").val(),
							sessionType:$("#selectedSessionType").val(),
							deviceType:$("#selectedDeviceType").val(),
							status:$("#selectedStatus").val(),
							workflowSubType:$("#selectedSubWorkflow").val(),//departmentwise bulk for cutmotion and other department based devices
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
<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<%@ include file="/common/info.jsp" %>
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
					<th style="min-width:50px;"><spring:message code="resolution.Date" text="Date"></spring:message></th>		
					<th style="min-width:50px;"><spring:message code="resolution.number" text="Number"></spring:message></th>
					<th style="min-width:130px;text-align:center;"><spring:message code="resolution.member" text="Member"></spring:message></th>
					<th style="min-width:350px;text-align:center;"><spring:message code="resolution.subject" text="Subject"></spring:message></th>
					<%-- <th style="min-width:70px;text-align:center;"><spring:message code="resolution.lastdecision" text="Last Decision"></spring:message></th> --%>
					<th style="min-width:50px;"><spring:message code="resolution.admissionNumber" text="Admission Number"></spring:message></th>
				<%-- 	<th style="min-width:50px;text-align:center;"><spring:message code="resolution.up" text="Up"></spring:message></th>
					<th style="min-width:50px;text-align:center;"><spring:message code="resolution.down" text="Down"></spring:message></th> --%>
					<%-- <th style="min-width:120px;text-align:center;"><spring:message code="resolution.lastremark" text="Last Remark"></spring:message></th> --%>
				</tr>			
				<c:set var="index" value="1"></c:set>
				<c:forEach items="${bulkapprovals}" var="i"  varStatus="status">
					<tr id="row${index}">
							 <c:choose>
								<c:when test="${i.currentStatus=='PENDING'}">
								<td style="min-width:75px;" class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
								<%-- <a href="#" class="edit" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td> --%>
								</c:when>							
								<c:otherwise>
								<td style="min-width:75px;" class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" style="margin-right: 10px;">			
								<%-- <a href="#" class="readonly" id="edit${i.id}"><spring:message code="resolution.edit" text="Edit"></spring:message></a></td> --%>
								</c:otherwise>
							</c:choose> 
								<%-- <td style="min-width:50px;text-align:center;">${i.deviceNumber}</td> --%>
								<td style="min-width:50px;">${i.deviceDate}</td>
								<td style="min-width:50px;">${i.deviceNumber}</td>
								<td style="min-width:130px;">${i.member}</td>
								<td style="text-align:justify;min-width:350px;">${i.subject}</td>
						<%-- 		<td style="min-width:70px;text-align:justify;">${i.lastDecision}</td> --%>	
								<td style="min-width:50px;" class="admissionNumber am_${i.id}">${i.deviceAdmissionNumber}</td>
								<%-- <c:if test="${status.count != 1}">
								<td style="min-width:50px;"><input type="button" value="&#x2191;" class="up" style="width: 40px;"/></td>
								</c:if>
								<c:if test="${fn:length(bulkapprovals) != status.count}">
								<td style="min-width:50px;"><input type="button" value="&#x2193;" class="down" style="width: 40px;"/></td>
								</c:if> --%>
								<%-- <td style="text-align:justify;min-width:120px;">${i.lastRemarkBy} : ${i.lastRemark}</td> --%>
								
					</tr>
					<c:set var="index" value="${index+1}"></c:set>	
				</c:forEach>
			</table>
			<br/>
			 <p>
			    <%-- <select name="selectedAction" id="selectedAction" style="width:100px;height: 25px;">			
						<option value="admissionNumberChange"><spring:message code='mytask.admissionNumber' text='Admission Number Change'/></option>
						<option value="toLapse"><spring:message code='mytask.lapsed' text='To Lapse'/></option>			
				</select> --%> 
				<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
			</p>
			</div>
		</c:when>
		<c:otherwise>
			<spring:message code="resolution.noresolutions" text="No Special Mention Notices Found"></spring:message>				
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="resolutionId" value="${resolutionId}">
	<input id="submissionMsg" value="<spring:message code='specialmentionnotice.client.prompt.submit' text='Do you want change the admission Number of Special Mention Notice ?'></spring:message>" type="hidden">
	<input id="lapseMsg" value="<spring:message code='specialmentionnotice.client.prompt.lapse' text='Do you want to lapse the Special Mention Notice?'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</div>	