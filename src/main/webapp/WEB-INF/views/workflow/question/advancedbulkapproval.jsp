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
			 
			 /**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text starts****/
				$("#clubbedQuestionTextsDiv").hide();
				$("#hideClubQTDiv").hide();
				$(".viewClubbedQuestionTextsDiv").click(function(){
				 	var controlId = this.id;
				 	var parent = controlId.split("viewClubbedQuestionTextsDiv")[1];
					if(parent!=undefined && parent!=''){			
						var questionId = $("#questionId"+parent).val();
						if($("#clubbedQuestionTextsDiv").css('display')=='none'){
							$("#clubbedQuestionTextsDiv").empty();
							$.get('ref/'+questionId+'/clubbedquestiontext',function(data){
								
								var text="";
								
								for(var i = 0; i < data.length; i++){
									text += "<p>"+data[i].name+"</p><p>"+data[i].value+"</p><hr />";
								}						
								$("#clubbedQuestionTextsDiv").html(text);
								
							});	
							$("#hideClubQTDiv").show();
							$("#clubbedQuestionTextsDiv").show();
						}else{
							$("#clubbedQuestionTextsDiv").hide();
							$("#hideClubQTDiv").hide();
						}
					}
				});
				
				$("#hideClubQTDiv").click(function(){
					$(this).hide();
					$('#clubbedQuestionTextsDiv').hide();
				});
				/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text end****/
						
		});		
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected questions) ****/
		function loadActors(value, controlName){
			var question = $("#questionId"+controlName).val();
			var deviceType = $("#deviceType").val();
			
			if(question!=undefined&&question!=''){
				var params="question="+question+"&status=";
				if(($("#currentusergroupType").val()=='assistant' 
						|| $("#currentusergroupType").val()=='section_officer') 
						&& (value.indexOf("final")>-1)){
					if(deviceType=='questions_halfhourdiscussion_from_question'){
						params += value
							+ "&level=" + $("#questionLevel").val();
					}else{
						params += value + "&level=8";
					}
				}else{
					params += value+"&level="+$("#questionLevel").val();
				}
				params +="&usergroup="+$("#usergroup").val() ;
				var resourceURL='ref/question/actors?'+params;				
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
					<form action="workflow/question/advancedbulkapproval" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:75px;text-align:center;"><spring:message code="question.submitall" text="Submit All"></spring:message>
								<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
								<th style="min-width:140px;text-align:justify;"><spring:message code="question.number" text="Number"></spring:message></th>
								<%-- <th style="text-align:justify;min-width:150px;"><spring:message code="question.member" text="Member"></spring:message></th> --%>
								<th style="text-align:justify;min-width:200px;"><spring:message code="question.subject" text="Subject"></spring:message></th>
								<th style="text-align:justify;min-width:200px;"><spring:message code="question.questiontext" text="Question Text"></spring:message></th>
								<th style="min-width:70px;text-align:justify;"><spring:message code="question.decision" text="Decision"></spring:message></th>
								<%-- <th style="min-width:70px;text-align:justify;">
									<spring:message code="question.lastdecision" text="Last Decision"/>
									<spring:message code="question.lastremarkby" text="Last Remark By"/>
								</th> --%>
							</tr>			
							<c:forEach items="${bulkapprovals}" var="i" varStatus="j">
								
								<tr>
									<c:choose>
										<c:when test="${i.currentStatus=='PENDING'}">
											<td style="min-width:75px;text-align:center;"> <input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
										</c:when>							
										<c:otherwise>
											<td style="min-width:75px;text-align:center;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true" disabled="disabled" style="margin-right: 10px;">			
										</c:otherwise>
									</c:choose>
											<%-- <td>${i.deviceType}</td> --%>							
									<td style="min-width:140px;text-align:justify;">
										${i.deviceNumber} <br> 
										${i.member}
										<br><br>
										<spring:message code="question.lastdecision" text="Last Decision"/> : ${i.lastDecision}
										<br>
										<spring:message code="question.lastremarkby" text="Last Remark By"/> ${i.lastRemarkBy} : ${i.lastRemark}
										
									</td>
									<%-- <td style="text-align:justify;min-width:150px;">${i.member}</td> --%>
									<td style="text-align:justify;min-width:200px;">
										<div class="editable" id="subject${j.index}">
											${i.subject}
										</div>
										<br>
										<b><spring:message code="question.clubbingTitle" /> </b> : ${i.formattedClubbedNumbers}
										<a href="javascript:void(0);" id="viewClubbedQuestionTextsDiv${j.index}" class="viewClubbedQuestionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
										<b><spring:message code="question.referencingTitle" text="Referenced Question" /> </b> : ${i.formattedReferencedNumbers}
										<input type="hidden" id="questionId${j.index}" name="questionId${j.index}" value="${i.deviceId}"/>
										<input type="hidden" id="workflowDetailsId${j.index}" name="workflowDetailsId${j.index}" value="${i.id}"/>
									</td>
									<td style="text-align:justify;min-width:200px;">
										<div class="editable" id="questionText${j.index}">
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
							<input type="hidden" name="questionlistSize" id="questionlistSize" value="${bulkapprovals.size()}"/>
							<input type="hidden" id="questionLevel" name ="questionLevel" value="${level}" />
							<input type="hidden" id="usergroup" name="usergroup" value="${usergroup}"/>
							<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}"/>	
							<input type="hidden" id="group" name="group" value="${group}"/>	
							<input type="hidden" id="status" name="status" value="${status}"/>
							<input type="hidden" id="answeringDate" name="answeringDate" value="${answeringDate}"/>					
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="question.noquestions" text="No Questiosn Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<div id="clubbedQuestionTextsDiv">
			<h1>Assistant Questio texts of clubbed questions</h1>
		</div>
		<div id="hideClubQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
</body>
</html>