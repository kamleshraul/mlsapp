<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="specialmentionnotice.bulksubmissionassisatnt" text="Bulk Put Up" /></title>
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
				var value=parseInt($(this).val());
				if(value!='-'){
					loadActors(value);
				}
		});
		/**** Page Load ****/
		viewContent();				
	});		
	/**** Display Resolution File or Status Wise ****/
	function viewContent(){		
		 var resource='workflow/specialmentionnotice/bulkapproval/view';
		 $.post(resource,{
			  houseType:$("#apprhouseType").val()
			 ,sessionYear:$("#apprsessionYear").val()
			 ,sessionType:$("#apprsessionType").val()
			 ,deviceType:$("#apprdeviceType").val()
			 ,status:$("#apprstatus").val()
			 ,role:$("#apprrole").val()
			 ,usergroup:$("#apprusergroup").val()
			 ,usergroupType:$("#apprusergroupType").val()
			 ,itemscount:$("#appritemscount").val()
			 ,workflowSubType:$("#apprworkflowSubType").val()
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
	once and it will be set for all selected resolutions) ****/
	function loadActors(value){
		var resolution=$("#resolutionId").val();
		if(resolution!=undefined&&resolution!=''){
			var params="motion="+resolution+"&status="+value+
			"&usergroup="+$("#apprusergroup").val()+"&level=1"+"&workflowHouseType="+$("#apprhouseType").val();
		
			var resourceURL='ref/specialmentionnotice/actors?'+params;				
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
					/* $("#refTextDiv").show();
					$("#remarkDiv").show(); */
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
					items.push($(this).attr("id").split("chk")[1]);					
				}			
		});
				
		if(items.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}	
		var status=$("#apprInternalStatusWf").val();
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('workflow/specialmentionnotice/bulkapproval/update?actor='+next+"&level="+level,
			        	{items:items
			        	 ,status:status
			        	 ,houseType:$("#apprhouseType").val()
						 ,sessionYear:$("#apprsessionYear").val()
						 ,sessionType:$("#apprsessionType").val()
						 ,deviceType:$("#apprdeviceType").val()
						 ,role:$("#apprrole").val()
						 ,usergroup:$("#apprusergroup").val()
						 ,usergroupType:$("#apprusergroupType").val()
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>
	<select id="apprInternalStatusWf" class="sSelect">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
	</c:forEach>
	</select>
	<span id="actorDiv" style="margin: 10px;display: none;">
		<label class="small"><spring:message code="resolution.nextactor" text="Next Users"/></label>
		<select id="appractor" class="sSelect"></select>
	</span>	
	<p>
		<label class="small"><spring:message code="question.remarks" text="Remarks"/></label>
		<textarea name="remarks" id="remarks" class="sTextArea" ></textarea>
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
	</p>	
			
			
		
</p>
<div id="bulkResultDiv">	
</div>	
<input type="hidden" id="apprhouseType" value="${houseType }">
<input type="hidden" id="apprsessionType" value="${sessionType }">
<input type="hidden" id="apprsessionYear" value="${sessionYear }">
<input type="hidden" id="apprdeviceType" value="${deviceType }">
<input type="hidden" id="apprstatus" value="${status }">
<input type="hidden" id="apprrole" value="${role }">
<input type="hidden" id="apprusergroup" value="${usergroup}">
<input type="hidden" id="apprusergroupType" value="${usergroupType}">
<input type="hidden" id="appritemscount" value="${itemscount }">		
<input id="apprworkflowSubType" value="${workflowSubType }" type="hidden">

	<!-- --------------------------PROCESS VARIABLES -------------------------------- -->
	
	<input id="mailflag" name="mailflag" value="${pv_mailflag}" type="hidden">
	<input id="timerflag" name="timerflag" value="${pv_timerflag}" type="hidden">
	<input id="reminderflag" name="reminderflag" value="${pv_reminderflag}" type="hidden">	
	
	<!-- mail related variables -->
	<input id="mailto" name="mailto" value="${pv_mailto}" type="hidden">
	<input id="mailfrom" name="mailfrom" value="${pv_mailfrom}" type="hidden">
	<input id="mailsubject" name="mailsubject" value="${pv_mailsubject}" type="hidden">
	<input id="mailcontent" name="mailcontent" value="${pv_mailcontent}" type="hidden">
	
	<!-- timer related variables -->
	<input id="timerduration" name="timerduration" value="${pv_timerduration}" type="hidden">
	<input id="lasttimerduration" name="lasttimerduration" value="${pv_lasttimerduration}" type="hidden">	
	
	<!-- reminder related variables -->
	<input id="reminderto" name="reminderto" value="${pv_reminderto}" type="hidden">
	<input id="reminderfrom" name="reminderfrom" value="${pv_reminderfrom}" type="hidden">
	<input id="remindersubject" name="remindersubject" value="${pv_remindersubject}" type="hidden">
	<input id="remindercontent" name="remindercontent" value="${pv_remindercontent}" type="hidden">
	
<input id="submissionMsg" value="<spring:message code='specialmentionnotice.client.prompt.submit' text='Do you want to submit the special Mention Notice'></spring:message>" type="hidden">
<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 item to continue..'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>		
</body>
</html>