<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="cutmotion.bulksubmissionassisatnt" text="Bulk Put Up" /></title>
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
					$("#actorDiv").hide();
					return false;
				}
		});
		/**** Page Load ****/
		viewContent();				
	});		
	/**** Display Motions File or Status Wise ****/
	function viewContent(){		
		 var resource='workflow/cutmotion/bulkapproval/view';
		 $.post(resource,{
			  houseType:$("#apprhouseType").val()
			 ,sessionYear:$("#apprsessionYear").val()
			 ,sessionType:$("#apprsessionType").val()
			 ,motionType:$("#apprmotionType").val()
			 ,status:$("#apprstatus").val()
			 ,role:$("#apprrole").val()
			 ,usergroup:$("#apprusergroup").val()
			 ,usergroupType:$("#apprusergroupType").val()
			 ,file:$("#apprfile").val()
			 ,itemscount:$("#appritemscount").val()
			 ,workflowSubType:$("#apprworkflowSubType").val()
			 ,subDepartment:$("#apprsubDepartment").val()
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
		var motion=$("#motionId").val();
		if(motion!=undefined&&motion!=''){
			var params="cutmotion="+motion+"&status=";
							//+value+"&usergroup="+$("#apprusergroup").val()+"&level=1";
			if(($("#currentusergroupType").val()=='assistant' 
				|| $("#currentusergroupType").val()=='section_officer') 
				&& ($("#apprworkflowSubType").val().indexOf("final")>-1)){
				
				params += $("#subWFMaster option[value='"+ $("#selectedSubWorkflow").val() +"']").text() + "&level=8";
				
			}else{
				params += value+"&level="+$("#motionLevel").val();
			}
			params +="&usergroup="+$("#apprusergroup").val() ;
			var resourceURL='ref/cutmotion/actors?'+params;				
			$.get(resourceURL,function(data){
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
					/* if(!$("#currentusergroupType").val()=='speaker'){
						$("#actorDiv").show();
					}else{
						$("#actorDiv").hide();
					} */
					$("#actorDiv").show();
					if($("#currentusergroupType").val()=='speaker'){
						$("#actorDiv").hide();
					}
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
		if($("#appractor").val()!=null&&$("#appractor").val()=='-'){
			$.prompt($("#selectActorMsg").val());
			return false;
		}else if($("#appractor").val()!=null&&$("#appractor").val()!="-"){
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
		var status="";
		if($("#apprworkflowSubType").val()=='request_to_supporting_member'){
		status=$("#apprInternalStatus").val();
		}else{
			status=$("#apprInternalStatusWf").val();			
		}
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('workflow/cutmotion/bulkapproval/update?actor='+next+"&level="+level,
			        	{items:items
			        	 ,status:status
			        	 ,houseType:$("#apprhouseType").val()
						 ,sessionYear:$("#apprsessionYear").val()
						 ,sessionType:$("#apprsessionType").val()
						 ,motionType:$("#apprmotionType").val()
						 ,role:$("#apprrole").val()
						 ,usergroup:$("#apprusergroup").val()
						 ,usergroupType:$("#apprusergroupType").val()
						 ,file:$("#apprfile").val()
						 ,itemscount:$("#appritemscount").val()
						 ,workflowSubType:$("#apprworkflowSubType").val()
					 	},
	    	            function(data){
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');	
	    					$.unblockUI();	
	    					$("#bulkResultDiv").empty();	
	    					$("#bulkResultDiv").html(data);	
	    	            }
	    	            ,'html').fail(function(){
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<c:choose>
	<c:when test="${workflowSubType=='request_to_supporting_member'}">
	<label class="small"><spring:message code="cutmotion.decisionstatus" text="Decision?"/></label>	
	<select id="apprInternalStatus" class="sSelect">
	<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
	</c:forEach>
	</select>	
	<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</c:when>
	<c:otherwise>
	<label class="small"><spring:message code="cutmotion.putupfor" text="Put up for"/></label>
	<select id="apprInternalStatusWf" class="sSelect">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
	</c:forEach>
	</select>
	<span id="actorDiv" style="margin: 10px;display: none;">
		<label class="small"><spring:message code="cutmotion.nextactor" text="Next Users"/></label>
		<select id="appractor" class="sSelect"></select>
	</span>	
	<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</c:otherwise>
	</c:choose>		
</p>
<div id="bulkResultDiv">	
</div>	
<input type="hidden" id="apprhouseType" value="${houseType }">
<input type="hidden" id="apprsessionType" value="${sessionType }">
<input type="hidden" id="apprsessionYear" value="${sessionYear }">
<input type="hidden" id="apprmotionType" value="${motionType }">
<input type="hidden" id="apprstatus" value="${status }">
<input type="hidden" id="apprrole" value="${role }">
<input type="hidden" id="apprusergroup" value="${usergroup }">
<input type="hidden" id="apprusergroupType" value="${usergroupType }">
<input type="hidden" id="appritemscount" value="${itemscount }">
<input type="hidden" id="apprfile" value="${file }">		
<input id="apprworkflowSubType" value="${workflowSubType }" type="hidden">
<input id="apprsubDepartment" value="${subDepartment }" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>		
</body>
</html>