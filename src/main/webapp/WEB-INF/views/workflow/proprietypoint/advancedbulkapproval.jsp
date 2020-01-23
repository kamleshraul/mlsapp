<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="proprietypoint.bulksubmissionassistant" text="Bulk Put Up" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			//As tinymce once registered doesnot get reinitialize when the same page is loaded, hence removing the previous tinymce instance
			tinymce.remove();
			
			$('.viewProprietyPointDetails').click(function() {
				var controlId = this.id;
			 	var deviceId = controlId.split("qid")[1];	 	
				if(deviceId!=undefined && deviceId!=''){			
					var proprietyPointId = $("#proprietyPointId"+deviceId).val();
					viewProprietyPointDetail(proprietyPointId);
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
			 
			 	/**** To show/hide viewPointsOfProprietyTextDiv to view points of propriety text starts****/
				$("#pointsOfProprietyTextDiv").hide();
				$(".viewPointsOfProprietyTextDiv").click(function(){
				 	var controlId = this.id;				 	
				 	var deviceIndex = controlId.split("viewPointsOfProprietyTextDiv")[1];
				 	if(deviceIndex!=undefined && deviceIndex!=''){			
						var proprietyPointId = $("#proprietyPointId"+deviceIndex).val();
						console.log("proprietyPointId: " + proprietyPointId);
						if($("#pointsOfProprietyTextDiv").css('display')=='none'){
							$("#pointsOfProprietyTextDiv").empty();
							$.get('ref/proprietypoint/'+proprietyPointId+'/points_of_propriety_text',function(data){
								
								var text="<p style='margin-top: 10px;'>"+data+"</p>";
								
								$("#pointsOfProprietyTextDiv").html(text);
								
							});	
							$("#hidePointsOfProprietyDiv").show();
							$("#pointsOfProprietyTextDiv").show();
						}else{
							$("#pointsOfProprietyTextDiv").hide();
							$("#hidePointsOfProprietyDiv").hide();
						}
					}
				});			 
				$("#hidePointsOfProprietyDiv").click(function(){
					$(this).hide();
					$('#pointsOfProprietyTextDiv').hide();
				});
				/**** To show/hide viewPointsOfProprietyTextDiv to view points of propriety text end****/
		});		
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected proprietypoints) ****/
		function loadActors(value, controlName){
			var proprietyPoint = $("#proprietyPointId"+controlName).val();
			var deviceType = $("#deviceType").val();
			
			if(proprietyPoint!=undefined && proprietyPoint!=''){
				var params="proprietypoint="+proprietyPoint+"&status=";
				if(($("#currentusergroupType").val()=='assistant' 
						|| $("#currentusergroupType").val()=='section_officer') 
						&& (value.indexOf("final")>-1)){
					params += value + "&level=8";
				}else{
					params += value+"&level="+$("#proprietyPointLevel").val();
				}
				params +="&usergroup="+$("#usergroup").val() ;
				var resourceURL='ref/proprietypoint/actors?'+params;				
				$.get(resourceURL,function(data){
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
		
		function viewDeviceDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#deviceType").val()
			//+"&ugparam="+$("#ugparam").val() //commented as no need to send group from here.. it will be taken from propriety point itself
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='proprietypoint/'+id+'/edit?'+parameters;
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
		  #pointsOfProprietyTextDiv {
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
					<div style="overflow: scroll;">
					<form action="workflow/proprietypoint/advancedbulkapproval" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:40px;text-align:center;vertical-align: top;">
									<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true">
									<spring:message code="generic.submit.all" text="Submit All"></spring:message>								
								</th>					
								<th style="min-width:175px;text-align:justify;vertical-align: top;"><spring:message code="proprietypoint.number" text="Number"></spring:message></th>
								<%-- <th style="text-align:justify;min-width:150px;vertical-align: top;"><spring:message code="proprietypoint.member" text="Member"></spring:message></th> --%>
								<th style="text-align:justify;min-width:350px;vertical-align: top;"><spring:message code="generic.subject" text="Subject"></spring:message></th>
								<%-- <th style="text-align:justify;min-width:200px;vertical-align: top;"><spring:message code="proprietypoint.proprietypointtext" text="Device Text"></spring:message></th> --%>
								<th style="min-width:200px;text-align:justify;vertical-align: top;">
									<spring:message code="generic.decision" text="Decision"></spring:message>
									<input type="checkbox" id="preserveDecisions" name="preserveDecisions" class="sCheck">
								</th>
								<%-- <th style="min-width:70px;text-align:justify;">
									<spring:message code="proprietypoint.lastdecision" text="Last Decision"/>
									<spring:message code="proprietypoint.lastremarkby" text="Last Remark By"/>
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
											<a href="#" id="qid${j.index}" class="viewProprietyPointDetails"><spring:message code="advancedbulk.proprietypoint.details" text="Details"/></a>
										</span>
										<br/> 
										${i.member}
										<br><br>
										<spring:message code="proprietypoint.lastdecision" text="Last Decision"/> : ${i.lastDecision}
										<br>
										<spring:message code="proprietypoint.lastremarkby" text="Last Remark By"/> ${i.lastRemarkBy} : ${i.lastRemark}
										
									</td>
									<%-- <td style="text-align:justify;min-width:150px;">${i.member}</td> --%>
									<td style="text-align:justify;min-width:350px;vertical-align: top;">
										<div class="editable" id="subject${j.index}">
											${i.subject}
										</div>
										<br>
										<b><spring:message code="proprietypoint.pointsOfPropriety" text="Points of Propriety" /> </b> : 
										<a href="javascript:void(0);" id="viewPointsOfProprietyTextDiv${j.index}" class="viewPointsOfProprietyTextDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="proprietypoint.noticecontent.text" text="N"></spring:message></a>
										<br/><br/>
										<input type="hidden" id="proprietyPointId${j.index}" name="proprietyPointId${j.index}" value="${i.deviceId}"/>
										<input type="hidden" id="workflowDetailsId${j.index}" name="workflowDetailsId${j.index}" value="${i.id}"/>
									</td>
									<%-- <td style="text-align:justify;min-width:200px;vertical-align: top;">
										<div class="editable" id="proprietypointText${j.index}">
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
							<input type="hidden" name="proprietyPointlistSize" id="proprietyPointlistSize" value="${bulkapprovals.size()}"/>
							<input type="hidden" id="proprietyPointLevel" name ="proprietyPointLevel" value="${level}" />
							<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}"/>
							<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}"/>	
							<input type="hidden" id="status" name="status" value="${status}"/>
							<input type="hidden" id="srole" value="${role}" />			
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="proprietypoint.noproprietypoints" text="No Propriety Point Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the propriety points?'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<div id="pointsOfProprietyTextDiv">
			<h1>Points of Propriety Text</h1>
		</div>
		<div id="hidePointsOfProprietyDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
</body>
</html>