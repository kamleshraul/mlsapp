<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.bulksubmissionassistant" text="Bulk Put Up" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
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
			$(".ministry option[selected!='selected']").hide();
			$(".subdepartment option[selected!='selected']").hide(); 
			$("#actorDiv").hide();
			/**** Bulk Put Up ****/
			$("#bulksubmit").click(function(){
				bulkPutUpUpdate();			
			});	
			
			$(".internalStatus").change(function(){
				var id = $(this).attr("name").split("internalStatus")[1];
				var advanceCopyReceived = $("#internalStatusMaster option[value='motion_system_advanceCopyReceived']").text();
				if($(this).val() != advanceCopyReceived){
					loadActors(id, $("#motionId"+id).val());
				}else{
					$("#actor"+id).empty();
					$("#actorDiv"+id).hide();
				}
			}); 
			
			$('.transferable').change(function() {
				var id = $(this).attr("id").split("isTransferable")[1];
		        if ($(this).is(':checked')) {
		        	$("#ministry"+id +" option[selected!='selected']").show();
		    		$("#subdepartment"+id +" option[selected!='selected']").show(); 
		    		$(".transferP"+id).css("display","inline-block");
		    		$("#submit").css("display","none");
		    		$("#decisionDiv").css("display","none");
		    		$(".internalStatus").attr("checked",false);
		    		$("#actor"+id).empty();
		        }else{
		        	$("#ministry"+id +" option[selected!='selected']").hide();
		        	$("#subdepartment"+id +" option[selected!='selected']").hide(); 
		    		$(".transferP"+id).css("display","none");
		    		$("#submit").css("display","inline-block");
		    		$("#decisionDiv").css("display","inline-block");
		    		$(".internalStatus").attr("checked",false);
		    		$("#actor"+id).empty();
		        }
		    });
			
			$('.mlsBranchNotified').change(function() {
				var id = $(this).attr("id").split("mlsBranchNotifiedOfTransfer")[1];
		        if ($(this).is(':checked') && $("#isTransferable"+id).is(':checked')) {
		        	$("#submit").css("display","inline-block");
		        }else{
		        	$("#submit").css("display","none");
		        }
		    });
			
			
			/**** Ministry Changes ****/
			$(".ministry").change(function(){
				var id = $(this).attr("id").split("ministry")[1];
				if($(this).val()!=''){
					loadSubDepartments($(this).val(), id);
				}else{
					$("#subdepartment"+id).prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				}
			});
			
			$(".deviceNumber").click(function(){
				var id = $(this).attr("id").split("deviceNumber")[1];
				$(this).attr('href', 'motion/report/commonadmissionreport?motionId='+$("#motionId"+id).val()
						+'&outputFormat=PDF&copyType=advanceCopy&isAdvanceCopy=yes');
			});
			
		});		
		
		//Load Subdepartments
		function loadSubDepartments(ministry, controlName){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data){
				$("#subdepartment"+controlName).empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				
				$("#subdepartment"+controlName).html(subDepartmentText);			
				}else{
					$("#subdepartment"+controlName).empty();
					var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
					$("#subdepartment"+controlName).html(subDepartmentText);				
				}
				$.unblockUI();
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected questions) ****/
		function loadActors(id, value){
			if(value != undefined && value!=''){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var params="motion="+value +"&status="+$("#admissionStatus").val()+"&usergroup="+$("#usergroup").val()+"&level="+$("#motionLevel").val();
				var resourceURL='ref/motion/actors?'+params;				
				$.post(resourceURL,function(data){
					if(data!=undefined||data!=null||data!=''){
						var length=data.length;
						$("#actor"+id).empty();
						var text="";
						for(var i=0;i<data.length;i++){
							var ugtActor = data[i].id.split("#")
							text+="<option value='"+ugtActor[0]+"'>"+ "("+ugtActor[4]+")" + "</option>";
						}
						text+="<option value='-'>----"+$("#pleaseSelectMessage").val()+"----</option>";
						$("#actor"+id).html(text);
						$("#actorDiv"+id).show();								
					}else{
						$("#actor"+id).empty();
						$("#actorDiv"+id).hide();	
					}	
					$.unblockUI();
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else{
				$("#actor"+id).empty();
				$("#actorDiv"+id).hide();	
				$.unblockUI();
			}	
		}
		

		
	</script>
	<style type="text/css">
		  #clubbedQuestionTextsDiv {
			background: none repeat-x scroll 0 0 #FFF;
			box-shadow: 0 2px 5px #888888;
			max-height: 260px;
			right: 0;
			position: fixed;
			top: 10px;
			width: 300px;
			z-index: 10000;
			overflow: auto;
			border-radius: 10px;
		}
	</style>
</head>
	<body>	
		<h4 id="error_p">&nbsp;</h4>
		<%@ include file="/common/info.jsp"%>
		<div id="bulkResultDiv">	
				<c:choose>
				<c:when test="${!(empty bulkapprovals) }">			
					
					<div style="overflow: scroll;">
					<form action="motion/advancecopy" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:75px;text-align:center;"><spring:message code="motion.submitall" text="Submit All"></spring:message>
								<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
								<th style="min-width:200px;text-align:justify;"><spring:message code="motion.number" text="Question Number"></spring:message></th>
								<th style="text-align:justify;min-width:350px;"><spring:message code="motion.details" text="Motion"></spring:message></th>
								<th style="min-width:70px;text-align:justify;"><spring:message code="motion.decision" text="Decision"></spring:message></th>
							</tr>			
							<c:forEach items="${bulkapprovals}" var="i" varStatus="j">
								<tr>
									<c:choose>
										<c:when test="${not empty i.advanceCopyActor and i.advanceCopyActor==true}">
											<td style="min-width:75px;text-align:center;"> <input type="checkbox" id="chk${j.index}" name="chk${j.index}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">						
										</c:when>							
										<c:otherwise>
											<td style="min-width:75px;text-align:center;"><input type="checkbox" id="chk${j.index}" name="chk${j.index}" class="sCheck action" value="true"  style="margin-right: 10px;">			
										</c:otherwise>
									</c:choose>
									 <td style="min-width:200px;text-align:justify;">
										<a href="javascript:void(0)" id="deviceNumber${j.index}" class="deviceNumber">${i.deviceNumber}</a> 
										<input type="hidden" name="motionId${j.index}" id="motionId${j.index}" value="${i.deviceId}"/>
										<br>
										<p>
											<label class="small"><spring:message code="motion.isTransferable" text="is Motion to be transfered?"/></label>
											<input type="checkbox" name="isTransferable${j.index}" id="isTransferable${j.index}" class="sCheck transferable">
										</p>
										
										<p>
										<label class="small"><spring:message code='motion.ministry' text='Ministry'/></label>
										<select id="ministry${j.index}" name="ministry${j.index}" class="sSelect ministry">
											<c:forEach items="${ministries}" var="k">
												<c:choose>
													<c:when test="${k.id==i.ministryId }">
														<option value="${k.id }" selected="selected">${k.dropdownDisplayName}</option>
													</c:when>
													<c:otherwise>
														<option value="${k.id }" >${k.dropdownDisplayName}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</select>
									   </p>
									   <p>
										<label class="small"><spring:message code='motion.subdepartment' text='Subdepartment'/></label>
										<select id="subdepartment${j.index}" name="subdepartment${j.index}" class="sSelect subdepartment">
											<c:forEach items="${subdepartments}" var="k">
												<c:choose>
													<c:when test="${k.id==i.subdepartmentId }">
														<option value="${k.id }" selected="selected">${k.name}</option>
													</c:when>
													<c:otherwise>
														<option value="${k.id }" >${k.name}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</select>
									   </p>
									   	<p class="transferP${j.index}" style="display:none;">
											<label class="small" id="subdepartmentValue"><spring:message code="motion.transferToDepartmentAccepted" text="Is the Transfer to Department Accepted"/></label>
											<input type="checkbox" id="transferToDepartmentAccepted${j.index}" name="transferToDepartmentAccepted${j.index}" class="sCheck"/>
										</p>
										<p class="transferP${j.index}" style="display:none;">	
											<label class="small"><spring:message code="motion.mlsBranchNotified" text="Is the Respective Motion Branch Notified"/></label>
											<input type="checkbox" id="mlsBranchNotifiedOfTransfer${j.index}" name="mlsBranchNotifiedOfTransfer${j.index}" class="sCheck mlsBranchNotified"/>
										</p>
									</td>
									<td style="text-align:justify;min-width:350px;">
										<div class="editable" id="subject${j.index}">
											${i.subject}
										</div>
										<br>
									</td>
									<td style="text-align:justify;min-width:200px;">
										<div id="decisionDiv">
											<c:forEach items="${internalStatuses}" var="i">
												<input type="radio" name="internalStatus${j.index}" value="${i.id}" class="sCheck internalStatus"> ${i.name}
												<br>
											</c:forEach>
											<br>
											<div id="actorDiv${j.index}">
												<select id="actor${j.index}" name="actor${j.index}" class="sSelect">
													<option value=''><spring:message code='client.prompt.pleaseselect' text='Please Select.'/></option>
												</select>
											</div>
											<br>
										</div>
										<textarea class="sTextarea" name= "remark${j.index}" id="remark${j.index}"/>
									</td>
								</tr>
								
							</c:forEach>
						</table>
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
							<input type="hidden" name="motionlistSize" id="motionlistSize" value="${bulkapprovals.size()}"/>
							<input type="hidden" id="motionLevel" name ="motionLevel" value="${level}" />
							<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}"/>
							<input type="hidden" id="admissionStatus" name="admissionStatus" value="${admissionStatus}"/>
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="motion.nomotions" text="No Motion Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
		</select>
		<input type="hidden" name="session" id="session" value="${session}"/>
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
</body>
</html>