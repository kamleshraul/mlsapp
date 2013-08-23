<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmissionassisatnt" text="Bulk Put Up" /></title>
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
		$("#assiInternalStatus").change(function(){
				if($(this).val()!='-' && $(this).val()!=''){
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
	/**** Display Resolution File or Status Wise ****/
	function viewContent(){
		var parameters = "houseType="+$("#assihouseType").val()
		 +"&sessionYear="+$("#assisessionYear").val()
		 +"&sessionType="+$("#assisessionType").val()
		 +"&deviceType="+$("#assideviceType").val()
		 +"&status="+$("#assistatus").val()
		 +"&role="+$("#assirole").val()
		 +"&usergroup="+$("#assiusergroup").val()
		 +"&usergroupType="+$("#assiusergroupType").val()
		 +"&file="+$("#assifile").val()
		 +"&itemscount="+$("#assiitemscount").val();	
		 var resource='resolution/bulksubmission/assistant/view';
		 var resourceURL=resource+"?"+parameters;
		 $.get(resourceURL,function(data){
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
		var resolution=$("#resolutionId").val();
		if(resolution!=undefined&&resolution!=''){
			var params="resolution="+resolution+"&status="+value+
			"&usergroup="+$("#assiusergroup").val()+"&level=1"+"&workflowHouseType="+$("#assihouseType").val();
			var resourceURL='ref/resolution/actors?'+params;				
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					var length=data.length;
					$("#assiactor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
						if(i!=0){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}else{
							text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
						}
					}
					text+="<option value='-'>----"+$("#pleaseSelectMessage").val()+"----</option>";
					$("#assiactor").html(text);
					$("#actorDiv").show();								
				}else{
					$("#assiactor").empty();
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
			$("#assiactor").empty();
			$("#actorDiv").hide();			
		}	
	}
	function bulkPutUpUpdate(){
		var next="";
		var level="";
		if($("#assiactor").val()!=null&&$("#assiactor").val()=='-'){
			$.prompt($("#selectActorMsg").val());
			return false;
		}else if($("#assiactor").val()!=null&&$("#assiactor").val()!="-"){
			var temp=$("#assiactor").val().split("#");
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
		var status=$("#assiInternalStatus").val();
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('resolution/bulksubmission/assistant/update?actor='+next+"&level="+level,
			        	{items:items
			        	,status:status
			        	,houseType:$("#assihouseType").val()
			   		 	,sessionYear:$("#assisessionYear").val()
					 	,sessionType:$("#assisessionType").val()
					 	,deviceType:$("#assideviceType").val()
					 	,role:$("#assirole").val()
					 	,usergroup:$("#assiusergroup").val()
					 	,usergroupType:$("#assiusergroupType").val()
					 	,file:$("#assifile").val()
					 	,itemscount:+$("#assiitemscount").val()
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
	.true{
	}
	</style>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<p>
		<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>	
		<select id="assiInternalStatus" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach>
		</select>
		<span id="actorDiv" style="margin: 10px;display: none;">
		<label class="small"><spring:message code="resolution.nextactor" text="Next Users"/></label>
		<select id="assiactor" class="sSelect"></select>
		</span>	
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</p>
	<div id="bulkResultDiv">	
	</div>	
	<input type="hidden" id="assihouseType" value="${houseType}">
	<input type="hidden" id="assisessionType" value="${sessionType }">
	<input type="hidden" id="assisessionYear" value="${sessionYear }">
	<input type="hidden" id="assideviceType" value="${deviceType }">
	<input type="hidden" id="assistatus" value="${status }">
	<input type="hidden" id="assirole" value="${role }">
	<input type="hidden" id="assiusergroup" value="${usergroup }">
	<input type="hidden" id="assiusergroupType" value="${usergroupType }">
	<input type="hidden" id="assiitemscount" value="${itemscount }">
	<input type="hidden" id="assifile" value="${file }">
	<input type="hidden" id="assiworkflowHouseType" value="${workflowHouseType}">	
	<input id="submissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='Do you want to submit the resolution'></spring:message>" type="hidden">
	<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
	<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 resolution to continue..'></spring:message>" type="hidden">		
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
	<input id="submissionMsg" value="<spring:message code='resolutions.client.prompt.submit' text='Do you want to submit the resolutions'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>		
</body>
</html>