<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="motion.bulksubmissionassistant" text="Bulk Put Up" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			//As tinymce once registered doesnot get reinitialize when the same page is loaded, hence removing the previous tinymce instance
			tinymce.remove();
			
			$('.viewMotionDetails').click(function() {
				var controlId = this.id;
			 	var parent = controlId.split("qid")[1];	 	
				if(parent!=undefined && parent!=''){			
					var motionId = $("#motionId"+parent).val();
					viewMotionDetail(motionId);
				}
			});
			
			/**** Check/Uncheck Submit All ****/		
			$("#chkall").change(function(){
				if($(this).is(":checked")){
					$(".action").attr("checked","checked");	
				}else{
					$(".action").removeAttr("checked");
				}
			});	
			
			/**** Preserve Decisions of Previous Actor ****/		
			$("#preserveDecisions").change(function(){
				if($(this).is(":checked")){
					$(this).attr("value", true);
				}else{
					$(this).attr("value", false);
				}
			});
			
			$("#actorDiv").hide();
			/**** Bulk Put Up ****/
			$("#bulksubmit").click(function(){
				bulkPutUpUpdate();			
			});	
			
			$(".internalStatus").change(function(){
				var controlName = $(this).attr("name").split("internalStatus")[1];
				loadActors($(this).val(), controlName);
			});
			
			 tinymce.init({
				  selector: 'div.editable',
				  inline: true,
				  theme: 'inlite',
				  force_br_newlines : true,
		    	  force_p_newlines : false,
		    	  forced_root_block : "",
		    	  nonbreaking_force_tab: true,
				  plugins: [
				    'searchreplace fullscreen',
				     'table paste'
				  ],
				  toolbar: 'bold italic | alignleft aligncenter alignright alignjustify'
				});
			 
			 	/**** To show/hide viewNoticeContentTextDiv to view notice content text starts****/
				$("#noticeContentTextDiv").hide();
				$("#hideClubQTDiv").hide();
				$(".viewNoticeContentTextDiv").click(function(){
				 	var controlId = this.id;				 	
				 	var motionIndex = controlId.split("viewNoticeContentTextDiv")[1];
				 	if(motionIndex!=undefined && motionIndex!=''){			
						var motionId = $("#motionId"+motionIndex).val();
						console.log("motionId: " + motionId);
						if($("#noticeContentTextDiv").css('display')=='none'){
							$("#noticeContentTextDiv").empty();
							$.get('ref/adjournmentmotion/'+motionId+'/notice_content_text',function(data){
								
								var text="<p style='margin-top: 10px;'>"+data+"</p>";
								
								$("#noticeContentTextDiv").html(text);
								
							});	
							$("#hideNoticeContentDiv").show();
							$("#noticeContentTextDiv").show();
						}else{
							$("#noticeContentTextDiv").hide();
							$("#hideNoticeContentDiv").hide();
						}
					}
				});
				
				/**** To show/hide viewClubbedMotionTextsDiv to view clubbed motions text starts****/
				$("#clubbedMotionTextsDiv").hide();
				$("#hideClubQTDiv").hide();
				$(".viewClubbedMotionTextsDiv").click(function(){
				 	var controlId = this.id;
				 	var parent = controlId.split("viewClubbedMotionTextsDiv")[1];
				 	if(parent!=undefined && parent!=''){			
						var motionId = $("#motionId"+parent).val();
						if($("#clubbedMotionTextsDiv").css('display')=='none'){
							$("#clubbedMotionTextsDiv").empty();
							$.get('ref/adjournmentmotion/'+motionId+'/clubbedmotiontext',function(data){
								
								var text="";
								
								for(var i = 0; i < data.length; i++){
									text += "<p>"+data[i].name+"</p><p>"+data[i].value+"</p><hr />";
								}						
								$("#clubbedMotionTextsDiv").html(text);
								
							});	
							$("#hideClubQTDiv").show();
							$("#clubbedMotionTextsDiv").show();
						}else{
							$("#clubbedMotionTextsDiv").hide();
							$("#hideClubQTDiv").hide();
						}
					}
				});
				
				/**** To show/hide viewReferencedMotionTextsDiv to view referenced motions text starts****/
				$(".viewReferencedMotionTextsDiv").click(function(){
				 	var controlId = this.id;
				 	var parent = controlId.split("viewReferencedMotionTextsDiv")[1];				 	
					if(parent!=undefined && parent!=''){			
						var motionId = $("#motionId"+parent).val();
						if($("#clubbedMotionTextsDiv").css('display')=='none'){
							$("#clubbedMotionTextsDiv").empty();
							$.get('ref/adjournmentmotion/'+motionId+'/referencedmotiontext',function(data){
								
								var text="";
								
								for(var i = 0; i < data.length; i++){
									text += "<p>"+data[i].name+"</p><p>"+data[i].value+"</p><hr />";
								}						
								$("#clubbedMotionTextsDiv").html(text);
								
							});	
							$("#hideClubQTDiv").show();
							$("#clubbedMotionTextsDiv").show();
						}else{
							$("#clubbedMotionTextsDiv").hide();
							$("#hideClubQTDiv").hide();
						}
					}
				});
				
				$("#hideNoticeContentDiv").click(function(){
					$(this).hide();
					$('#noticeContentTextDiv').hide();
				});
				/**** To show/hide viewNoticeContentTextDiv to view notice content text end****/
				
				$("#hideClubQTDiv").click(function(){
					$(this).hide();
					$('#clubbedMotionTextsDiv').hide();
				});
				/**** To show/hide viewClubbedMotionTextsDiv to view clubbed motions text end****/
						
		});		
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected motions) ****/
		function loadActors(value, controlName){
			var motion = $("#motionId"+controlName).val();
			var deviceType = $("#deviceType").val();
			
			if(motion!=undefined&&motion!=''){
				var params="motion="+motion+"&status=";
				if(($("#currentusergroupType").val()=='assistant' 
						|| $("#currentusergroupType").val()=='section_officer') 
						&& (value.indexOf("final")>-1)){
					params += value + "&level=8";
				}else{
					params += value+"&level="+$("#motionLevel").val();
				}
				params +="&usergroup="+$("#usergroup").val() ;
				var resourceURL='ref/adjournmentmotion/actors?'+params;				
				$.post(resourceURL,function(data){
					if(data!=undefined||data!=null||data!=''){
						var length=data.length;
						$("#actor"+controlName).empty();
						var text="";
						for(var i=0;i<data.length;i++){
							if(i!=0){
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
							}else{
								text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
							}
						}
						text+="<option value='-'>----"+$("#pleaseSelectMessage").val()+"----</option>";
						$("#actor"+controlName).html(text);
						$("#actorDiv"+controlName).show();								
					}else{
						$("#actor"+controlName).empty();
						$("#actorDiv"+controlName).hide();	
					}		
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else{
				$("#actor"+controlName).empty();
				$("#actorDiv"+controlName).hide();			
			}	
		}
		
		function viewMotionDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&motionType="+$("#deviceType").val()
			//+"&ugparam="+$("#ugparam").val() //commented as no need to send group from here.. it will be taken from motion itself
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='adjournmentmotion/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}		
	</script>
	<style type="text/css">
		  #noticeContentTextDiv, #clubbedMotionTextsDiv {
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
		<div id="error_p">&nbsp;</div>
<%-- 		<c:if test="${(error!='') && (error!=null)}">
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
		</c:if> --%>
		<%@ include file="/common/info.jsp"%>
		<div id="bulkResultDiv">	
				<c:choose>
				<c:when test="${!(empty bulkapprovals) }">			
					<div style="font-size: 16px; font-weight: bold; margin-bottom: 20px;">
					    <spring:message code="generic.date" text="Date"></spring:message>
						${bulkapprovals[0].formattedAdjourningDate} <spring:message code="adjournmentmotion.advancebulkapproval.header" text="Adjournment Notices"></spring:message> ${selectedWorkflowStatus})
					</div>
					<div style="overflow: scroll;">
					<form action="workflow/adjournmentmotion/advancedbulkapproval" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:40px;text-align:center;vertical-align: top;">
									<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true">
									<spring:message code="generic.submit.all" text="Submit All"></spring:message>								
								</th>					
								<th style="min-width:175px;text-align:justify;vertical-align: top;"><spring:message code="adjournmentmotion.number" text="Number"></spring:message></th>
								<%-- <th style="text-align:justify;min-width:150px;vertical-align: top;"><spring:message code="motion.member" text="Member"></spring:message></th> --%>
								<th style="text-align:justify;min-width:350px;vertical-align: top;"><spring:message code="generic.subject" text="Subject"></spring:message></th>
								<%-- <th style="text-align:justify;min-width:200px;vertical-align: top;"><spring:message code="motion.motiontext" text="Motion Text"></spring:message></th> --%>
								<th style="min-width:200px;text-align:justify;vertical-align: top;">
									<spring:message code="generic.decision" text="Decision"></spring:message>
									<input type="checkbox" id="preserveDecisions" name="preserveDecisions" class="sCheck">
								</th>
								<%-- <th style="min-width:70px;text-align:justify;">
									<spring:message code="motion.lastdecision" text="Last Decision"/>
									<spring:message code="motion.lastremarkby" text="Last Remark By"/>
								</th> --%>
							</tr>			
							<c:forEach items="${bulkapprovals}" var="i" varStatus="j">
								
								<tr>
									<c:choose>
										<c:when test="${i.currentStatus=='PENDING'}">
											<td style="min-width:40px;text-align:center;vertical-align: top;"> <input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
										</c:when>							
										<c:otherwise>
											<td style="min-width:40px;text-align:center;vertical-align: top;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
										</c:otherwise>
									</c:choose>
											<%-- <td>${i.deviceType}</td> --%>							
									<td style="min-width:175px;text-align:justify;vertical-align: top;">
										<span style="font-size: 14px;font-weight: bold;">
											${i.deviceNumber}
										</span>
										<span style="margin-left: 5px;">
											<a href="#" id="qid${j.index}" class="viewMotionDetails"><spring:message code="advancedbulk.motion.details" text="Details"/></a>
										</span>
										<br/> 
										${i.member}
										<br><br>
										<spring:message code="motion.lastdecision" text="Last Decision"/> : ${i.lastDecision}
										<br>
										<spring:message code="motion.lastremarkby" text="Last Remark By"/> ${i.lastRemarkBy} : ${i.lastRemark}
										
									</td>
									<%-- <td style="text-align:justify;min-width:150px;">${i.member}</td> --%>
									<td style="text-align:justify;min-width:350px;vertical-align: top;">
										<div class="editable" id="subject${j.index}">
											${i.subject}
										</div>
										<br>
										<b><spring:message code="adjournmentmotion.noticeContent" text="Notice Content" /> </b> : 
										<a href="javascript:void(0);" id="viewNoticeContentTextDiv${j.index}" class="viewNoticeContentTextDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="motion.noticecontent.text" text="N"></spring:message></a>
										<br/><br/>
										<b><spring:message code="adjournmentmotion.clubbingTitle" /> </b> : ${i.formattedClubbedNumbers}
										<a href="javascript:void(0);" id="viewClubbedMotionTextsDiv${j.index}" class="viewClubbedMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="motion.clubbed.texts" text="C"></spring:message></a>
										<br/><br/>
										<%-- <b><spring:message code="motion.referencingTitle" text="Referenced Motion" /> </b> : ${i.formattedReferencedNumbers}
										<a href="javascript:void(0);" id="viewReferencedMotionTextsDiv${j.index}" class="viewReferencedMotionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="motion.referenced.texts" text="R"></spring:message></a>
										 --%><input type="hidden" id="motionId${j.index}" name="motionId${j.index}" value="${i.deviceId}"/>
										<input type="hidden" id="workflowDetailsId${j.index}" name="workflowDetailsId${j.index}" value="${i.id}"/>
									</td>
									<%-- <td style="text-align:justify;min-width:200px;vertical-align: top;">
										<div class="editable" id="motionText${j.index}">
											${i.briefExpanation}
										</div>
									</td> --%>
									<td style="text-align:justify;min-width:200px;vertical-align: top;">
										<c:forEach items="${internalStatuses}" var="i">
											<input type="radio" name="internalStatus${j.index}" value="${i.id}" class="sCheck internalStatus"> ${i.name}
											<br>
										</c:forEach>
										<br>
										<div id="actorDiv${j.index}">
											<select id="actor${j.index}" name="actor${j.index}">
												<option value='-'><spring:message code='client.prompt.pleaseselect' text='Please Select.'/></option>
											</select>
										</div>
										<br>
										<textarea class="sTextarea" name= "remark${j.index}" id="remark${j.index}" rows="5" cols="45"><c:out value="${bulkapprovals[j.index].lastRemark}"/></textarea>
									</td>
									<!-- 
									<td style="min-width:70px;text-align:justify;vertical-align: top;">
										
									</td> -->
									
								</tr>
								
							</c:forEach>
						</table>
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
							<input type="hidden" name="motionlistSize" id="motionlistSize" value="${bulkapprovals.size()}"/>
							<input type="hidden" id="motionLevel" name ="motionLevel" value="${level}" />
							<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}"/>
							<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}"/>	
							<input type="hidden" id="status" name="status" value="${status}"/>
							<input type="hidden" id="srole" value="${role}" />			
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="adjournmentmotion.nomotions" text="No Adjournment Motion Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<div id="noticeContentTextDiv">
			<h1>Notice Content of the Motion</h1>
		</div>
		<div id="clubbedMotionTextsDiv">
			<h1>Assistant Notice Contents of clubbed motions</h1>
		</div>
		<div id="hideNoticeContentDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
		<div id="hideClubQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>		
</body>
</html>