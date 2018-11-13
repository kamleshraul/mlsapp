<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmissionassistant" text="Bulk Put Up" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			//As tinymce once registered doesnot get reinitialize when the same page is loaded, hence removing the previous tinymce instance
			tinymce.remove();
			
			$('.viewResolutionDetails').click(function() {
				var controlId = this.id;
			 	var parent = controlId.split("rid")[1];	 	
				if(parent!=undefined && parent!=''){			
					var resolutionId = $("#resolutionId"+parent).val();
					viewResolutionDetail(resolutionId);
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
			 
			 /**** To show/hide viewClubbedResolutionTextsDiv to view clubbed resolutions text starts****/
				$("#clubbedResolutionTextsDiv").hide();
				$("#hideClubQTDiv").hide();
				
				/**** To show/hide viewReferencedResolutionTextsDiv to view referenced resolutions text starts****/
				$(".viewReferencedResolutionTextsDiv").click(function(){
				 	var controlId = this.id;
				 	var parent = controlId.split("viewReferencedResolutionTextsDiv")[1];				 	
					if(parent!=undefined && parent!=''){			
						var resolutionId = $("#resolutionId"+parent).val();
						if($("#clubbedResolutionTextsDiv").css('display')=='none'){
							$("#clubbedResolutionTextsDiv").empty();
							$.get('ref/'+resolutionId+'/referencedresolutiontext',function(data){
								var	text = "<p>"+data.name+"</p><p>"+data.value+"</p><hr />";
								$("#clubbedResolutionTextsDiv").html(text);
							});	
							$("#hideClubQTDiv").show();
							$("#clubbedResolutionTextsDiv").show();
						}else{
							$("#clubbedResolutionTextsDiv").hide();
							$("#hideClubQTDiv").hide();
						}
					}
				});
				
				$("#hideClubQTDiv").click(function(){
					$(this).hide();
					$('#clubbedResolutionTextsDiv').hide();
				});
				/**** To show/hide viewClubbedResolutionTextsDiv to view clubbed resolutions text end****/
						
		});		
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected resolutions) ****/
		function loadActors(value, controlName){
			var resolution = $("#resolutionId"+controlName).val();
			var deviceType = $("#deviceType").val();
			
			if(resolution!=undefined && resolution!=''){
				var params="resolution="+resolution+"&status=";
				params += value+"&level="+$("#resolutionLevel").val();
				params +="&usergroup="+$("#usergroup").val()+"&workflowHouseType="+$("#housetype").val() ;
				var resourceURL='ref/resolution/actors?'+params;				
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
		
		function viewResolutionDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#deviceType").val()
			//+"&ugparam="+$("#ugparam").val() //commented as no need to send group from here.. it will be taken from resolution itself
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='resolution/'+id+'/edit?'+parameters;
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
		  #clubbedResolutionTextsDiv {
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
					<form action="workflow/resolution/advancedbulkapproval" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:60px;text-align:center;"><spring:message code="resolution.submitall" text="Submit All"></spring:message>
								<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
								<th style="min-width:155px;text-align:justify;"><spring:message code="resolution.number" text="Number"></spring:message></th>
								<th style="text-align:justify;min-width:400px;"><spring:message code="resolution.noticeContent" text="Notice Content"></spring:message></th>
								<th style="min-width:70px;text-align:justify;"><spring:message code="resolution.decision" text="Decision"></spring:message></th>
							</tr>			
							<c:forEach items="${bulkapprovals}" var="i" varStatus="j">
								
								<tr>
									<c:choose>
										<c:when test="${i.currentStatus=='PENDING'}">
											<td style="min-width:60px;text-align:center;"> <input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
										</c:when>							
										<c:otherwise>
											<td style="min-width:60px;text-align:center;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
										</c:otherwise>
									</c:choose>
											<%-- <td>${i.deviceType}</td> --%>							
									<td style="min-width:155px;text-align:justify;">
										<span>
											${i.deviceNumber} 
										</span>
										<span style="margin-left: 5px;">
											<a href="#" id="rid${j.index}" class="viewResolutionDetails"><spring:message code="advancedbulk.resolution.details" text="Details"/></a>
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
									<td style="text-align:justify;min-width:300px;">
										<div class="editable" id="noticeContent${j.index}">
											${i.subject}
										</div>
										<br>
										<b><spring:message code="resolution.referencingTitle" text="Referenced Resolution" /> </b> : ${i.formattedReferencedNumbers}
										<a href="javascript:void(0);" id="viewReferencedResolutionTextsDiv${j.index}" class="viewReferencedResolutionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="resolution.referenced.texts" text="R"></spring:message></a>
										<input type="hidden" id="resolutionId${j.index}" name="resolutionId${j.index}" value="${i.deviceId}"/>
										<input type="hidden" id="workflowDetailsId${j.index}" name="workflowDetailsId${j.index}" value="${i.id}"/>
										<div class="editable" id="referenceText${j.index}">
											${i.briefExpanation}
										</div>
									</td>
									<td style="text-align:justify;min-width:200px;">
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
										<textarea class="sTextarea" name= "remark${j.index}" id="remark${j.index}"/>
									</td>
									<!-- 
									<td style="min-width:70px;text-align:justify;">
										
									</td> -->
									
								</tr>
								
							</c:forEach>
						</table>
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
							<input type="hidden" name="resolutionlistSize" id="resolutionlistSize" value="${bulkapprovals.size()}"/>
							<input type="hidden" id="resolutionLevel" name ="resolutionLevel" value="1" />
							<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}"/>
							<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}"/>	
							<input type="hidden" id="group" name="group" value="${group}"/>	
							<input type="hidden" id="status" name="status" value="${status}"/>
							<input type="hidden" id="srole" value="${role}" />	
							<input type="hidden" id="housetype" value="${houseType}"/>		
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="resolution.noresolutions" text="No Resolution Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<div id="clubbedResolutionTextsDiv">
			<h1>Assistant Resolution texts of clubbed Resolution</h1>
		</div>
		<div id="hideClubQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
</body>
</html>