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
				var supplementaryClubbingReceived = $("#internalStatusMaster option[value='question_processed_supplementaryClubbingReceived']").text();
				if( $(this).val() != supplementaryClubbingReceived){
					loadActors($(this).val(), controlName);
					$("actorDiv"+controlName).css("display","block");
				}
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
				
				/**** To show/hide viewReferencedQuestionTextsDiv to view referenced questions text starts****/
				$(".viewReferencedQuestionTextsDiv").click(function(){
				 	var controlId = this.id;
				 	var parent = controlId.split("viewReferencedQuestionTextsDiv")[1];				 	
					if(parent!=undefined && parent!=''){			
						var questionId = $("#questionId"+parent).val();
						if($("#clubbedQuestionTextsDiv").css('display')=='none'){
							$("#clubbedQuestionTextsDiv").empty();
							$.get('ref/'+questionId+'/referencedquestiontext',function(data){
								
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
				
				$(".parent").click(function(){
					var rowIndex = $(this).attr("id").split("parentQuestion")[1];
					var parentId = $(this).attr("title");
					console.log(parentId);
					var clubbedQuestionIds = $("#questionId"+rowIndex).val();
					console.log(clubbedQuestionIds);
					$(this).attr('href', 'question/report/generateClubbedIntimationLetter?questionId='+parentId
							+'&clubbedQuestions='+clubbedQuestionIds+'&outputFormat=PDF');
				});
						
		});		
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected questions) ****/
		function loadActors(value, controlName){
			var question = $("#questionId"+controlName).val().split(",");
			var deviceType = $("#deviceType").val();
			
			if(question!=undefined&&question!=''){
				var params="question="+question[0]+"&status=";
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
							var ugtActor = data[i].id.split("#")
							var ugt = ugtActor[1];
							if(i!=0){
							text+="<option value='"+data[i].id+"'>" + data[i].name  +"("+ugtActor[4]+")"+"</option>";
							}else{
								text+="<option value='"+data[i].id+"' selected='selected'>" + data[i].name  +"("+ugtActor[4]+")"+"</option>";
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
		
		function viewQuestionDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#deviceType").val()
			//+"&ugparam="+$("#ugparam").val() //commented as no need to send group from here.. it will be taken from question itself
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='question/'+id+'/edit?'+parameters;
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
		
		function bulkPutUpUpdate(){
			var next="";
			var level="";
			if($("#appractor").val()!=null && $("#appractor").val()!="-"){
				var temp=$("#appractor").val().split("#");
				next=temp[1];	
				level=temp[2];			
			}	
			var items=new Array();
			$(".action").each(function(){
				if($(this).is(":checked")){
					var wfIds = $(this).attr("id").split("chk")[1];
					items.push(wfIds);					
				}
			});		
			if(items.length<=0){
				$.prompt($("#selectItemsMsg").val());
				return false;	
			}	
			var internalstatus = $("#apprInternalStatusWf").val();			
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post('workflow/question/supplementquestionworkflow?actor='+next+"&level="+level,
				        	{items:items
				        	 ,internalstatus:internalstatus
				        	 ,houseType:$("#apprhouseType").val()
							 ,sessionYear:$("#apprsessionYear").val()
							 ,sessionType:$("#apprsessionType").val()
							 ,deviceType:$("#apprquestionType").val()
							 ,role:$("#apprrole").val()
							 ,usergroup:$("#apprusergroup").val()
							 ,usergroupType:$("#apprusergroupType").val()
							 ,itemscount:$("#appritemscount").val()
							 ,workflowSubType:$("#apprworkflowSubType").val()
							 ,status:$("#apprstatus").val()
						 	},
		    	            function(data){
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	
		    					$("#bulkResultDiv").empty();	
		    					$("#bulkResultDiv").html(data);	
		    	            }
		    	            ,'html').fail(function(){
		    	    			$.unblockUI();
		    	    			if($("#ErrorMsg").val()!=''){
		    	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    	    			}else{
		    	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    	    			}
		    	    			scrollTop();
		    	    		});
		        	}}});
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
					<form action="workflow/question/supplementquestionworkflow" method="POST">
						<table class="uiTable">
							<tr>					
								<th style="min-width:75px;text-align:center;"><spring:message code="question.submitall" text="Submit All"></spring:message>
								<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>					
								<th style="min-width:200px;text-align:justify;"><spring:message code="question.number" text="Question Number"></spring:message></th>
								<th style="text-align:justify;min-width:350px;"><spring:message code="question.questiontext" text="Question Text"></spring:message></th>
								<th style="min-width:70px;text-align:justify;"><spring:message code="question.decision" text="Decision"></spring:message></th>
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
									 <td style="min-width:200px;text-align:justify;">
										<span>
											<a href="javascript.void(0);" id="parentQuestion${j.index}" class="parent" title="${i.parentId}">${i.formattedParentNumber}</a>
										</span>
										<br/>
										<spring:message code="question.supplementaryNumbers" text="Supplementary Question Number"/> :${i.deviceNumber} 
										<br/> 
										<spring:message code="question.lastdecision" text="Last Decision"/> : ${i.lastDecision}
										<br>
										<spring:message code="question.lastremarkby" text="Last Remark By"/> ${i.lastRemarkBy} : ${i.lastRemark}
										
									</td>
									<td style="text-align:justify;min-width:350px;">
										<div class="editable" id="subject${j.index}">
											${i.subject}
										</div>
										<br>
										<%-- <b><spring:message code="question.clubbingTitle" /> </b> : ${i.formattedClubbedNumbers}
										<a href="javascript:void(0);" id="viewClubbedQuestionTextsDiv${j.index}" class="viewClubbedQuestionTextsDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
										<br/><br/> --%>
										<input type="hidden" id="questionId${j.index}" name="questionId${j.index}" value="${i.deviceId}"/>
										<input type="hidden" id="workflowDetailsId${j.index}" name="workflowDetailsId${j.index}" value="${i.id}"/>
									</td>
									<td style="text-align:justify;min-width:200px;">
										<c:forEach items="${internalStatuses}" var="i">
											<input type="radio" name="internalStatus${j.index}" value="${i.id}" class="sCheck internalStatus"> ${i.name}
											<br>
										</c:forEach>
										<br>
										<div id="actorDiv${j.index}" style="display:none;">
											<select id="actor${j.index}" name="actor${j.index}" class="sSelect">
												<option value=''><spring:message code='client.prompt.pleaseselect' text='Please Select.'/></option>
											</select>
										</div>
										<br>
										<textarea class="sTextarea" name= "remark${j.index}" id="remark${j.index}"/>
									</td>
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
							<input type="hidden" id="srole" value="${role}" />			
						</form>
					</div>
					
				</c:when>
				<c:otherwise>
					<spring:message code="question.noquestions" text="No Questiosn Found"></spring:message>				
				</c:otherwise>
			</c:choose>
		</div>	
		<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
		</select>
		<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
		<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
		<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
		<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>	
		<div id="clubbedQuestionTextsDiv">
			<h1>Assistant Question texts of clubbed questions</h1>
		</div>
		<div id="hideClubQTDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
</body>
</html>