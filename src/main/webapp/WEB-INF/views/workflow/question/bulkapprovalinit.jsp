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
		/**** Bulk Put Up ****/
		$("#bulksubmit").click(function(){
			bulkPutUpUpdate();			
		});	
		/**** Load Actors On Changing Status ****/
		$("#apprInternalStatusWf").change(function(){
				
				if($(this).val()!='-'){
					var value=parseInt($(this).val());
					loadActors(value);
				}else{
					if($("#actorDiv").css('visibility')=='visible'){
						$("#actorDiv").hide();
					}
				}
		});
		/**** Page Load ****/
		viewContent();				
	});		
	/**** Display Motions File or Status Wise ****/
	function viewContent(){		
		 var resource='workflow/question/bulkapproval/view';
		 $.post(resource,{
			  houseType:$("#apprhouseType").val()
			 ,sessionYear:$("#apprsessionYear").val()
			 ,sessionType:$("#apprsessionType").val()
			 ,deviceType:$("#apprquestionType").val()
			 ,status:$("#apprstatus").val()
			 ,role:$("#apprrole").val()
			 ,usergroup:$("#apprusergroup").val()
			 ,usergroupType:$("#apprusergroupType").val()
			  ,itemscount:$("#appritemscount").val()
			 ,workflowSubType:$("#apprworkflowSubType").val()
			 ,group:$('#apprGroup').val()
			 ,answeringDate:$('#apprAnsweringDate').val()
		 },function(data){
			 $("#bulkResultDiv").empty();
			 $("#bulkResultDiv").html(data);
		 },'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
	}	
	/**** load actors(Dynamically Change Actors-Actor Will be selected
	once and it will be set for all selected motions) ****/
	function loadActors(value){
		var question=$("#questionId").val();
		var deviceType = $("#apprquestionType").val();
		
		if(question!=undefined&&question!=''){
			var params="question="+question+"&status=";
			if(($("#currentusergroupType").val()=='assistant' 
					|| $("#currentusergroupType").val()=='section_officer') 
					&& ($("#apprworkflowSubType").val().indexOf("final")>-1)){
				if(deviceType=='questions_halfhourdiscussion_from_question'){
					params += $("#subWFMaster option[value='"+ $("#selectedSubWorkflow").val() +"']").text()
						+ "&level=" + $("#questionLevel").val();
				}else{
					params += $("#subWFMaster option[value='"+ $("#selectedSubWorkflow").val() +"']").text() + "&level=8";
				}
			}else{
				params += value+"&level="+$("#questionLevel").val();
			}
			params +="&usergroup="+$("#apprusergroup").val() ;
			var resourceURL='ref/question/actors?'+params;				
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					var length=data.length;
					$("#appractor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						if(i!=0){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}else{
							text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
						}
					}
					text+="<option value='-'>----"+$("#pleaseSelectMessage").val()+"----</option>";
					$("#appractor").html(text);
					$("#actorDiv").show();								
				}else{
					$("#appractor").empty();
					$("#actorDiv").hide();	
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
			$("#appractor").empty();
			$("#actorDiv").hide();			
		}	
	}
	function bulkPutUpUpdate(){
		var next="";
		var level="";
		/* if($("#appractor").val()!=null&&$("#appractor").val()=='-'){
			//$.prompt($("#selectActorMsg").val());
			//return false;
		}else */ if($("#appractor").val()!=null&&$("#appractor").val()!="-"){
			var temp=$("#appractor").val().split("#");
			next=temp[1];	
			level=temp[2];			
		}	
		var items=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
				if($("#apprworkflowSubType").val()=='request_to_supporting_member'){
					var wfId=$(this).attr("id").split("chk")[1];
					var smId=$("#sm"+wfId).val();
					items.push(wfId+"#"+smId);
				}else{
					items.push($(this).attr("id").split("chk")[1]);					
				}			
			}
		});		
		if(items.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}	
		var internalstatus="";
		if($("#apprworkflowSubType").val() == 'request_to_supporting_member'){
			internalstatus=$("#apprInternalStatus").val();
		}else{
			internalstatus=$("#apprInternalStatusWf").val();			
		}
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('workflow/question/bulkapproval/update?actor='+next+"&level="+level,
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
</head>
<body>	
<h4 id="error_p">&nbsp;</h4>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<c:choose>
	<c:when test="${workflowSubType=='request_to_supporting_member'}">
		<label class="small"><spring:message code="question.decisionstatus" text="Decision?"/></label>	
		<select id="apprInternalStatus" class="sSelect">
		<c:forEach items="${internalStatuses}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach>
		</select>	
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</c:when>
	<c:otherwise>
		<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
		<select id="apprInternalStatusWf" class="sSelect">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach>
		</select>
		<span id="actorDiv" style="margin: 10px;display: none;">
			<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
			<select id="appractor" class="sSelect"></select>
		</span>	
				
	</c:otherwise>
	</c:choose>		
</p>
<div id="bulkResultDiv">	
</div>
<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>	
<input type="hidden" id="apprhouseType" value="${houseType }">
<input type="hidden" id="apprsessionType" value="${sessionType }">
<input type="hidden" id="apprsessionYear" value="${sessionYear }">
<input type="hidden" id="apprquestionType" value="${deviceType }">
<input type="hidden" id="apprstatus" value="${status }">
<input type="hidden" id="apprrole" value="${role }">
<input type="hidden" id="apprusergroup" value="${usergroup }">
<input type="hidden" id="apprusergroupType" value="${usergroupType }">
<input type="hidden" id="appritemscount" value="${itemscount }">
<input type="hidden" id="apprGroup" value="${group}">
<input type="hidden" id="apprAnsweringDate" value="${answeringDate }">	
<input id="apprworkflowSubType" value="${workflowSubType }" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>		
</body>
</html>