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
	/**** Display Questions File or Status Wise ****/
	function viewContent(){

		var parameters = "houseType="+$("#assihouseType").val()
		 +"&sessionYear="+$("#assisessionYear").val()
		 +"&sessionType="+$("#assisessionType").val()
		 +"&questionType="+$("#assiquestionType").val()
		 +"&status="+$("#assistatus").val()
		 +"&role="+$("#assirole").val()
		 +"&usergroup="+$("#assiusergroup").val()
		 +"&usergroupType="+$("#assiusergroupType").val()
		 +"&itemscount="+$("#assiitemscount").val()
		 +"&group="+$("#assigroup").val()
		 +"&department="+$("#assidepartment").val();
	
		var resource='question/bulktimeout/view';
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
	once and it will be set for all selected questions) ****/
	function loadActors(value){
		var question=$("#questionId").val();
		if(question!=undefined&&question!=''){
			var params="question="+question+"&status="+value+
			"&usergroup="+$("#assiusergroup").val()+"&level=1";
			var resourceURL='ref/question/actors?'+params;				
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
		/* var next="";
		var level="";
		if($("#assiactor").val()!=null&&$("#assiactor").val()=='-'){
			$.prompt($("#selectActorMsg").val());
			return false;
		}else if($("#assiactor").val()!=null&&$("#assiactor").val()!="-"){
			var temp=$("#assiactor").val().split("#");
			next=temp[1];	
			level=temp[2];			
		} */	
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
				$.post('question/bulktimeout/update',
			        	{items:items
			        	,currentStatus:status
			        	,houseType:$("#assihouseType").val()
			   		 	,sessionYear:$("#assisessionYear").val()
					 	,sessionType:$("#assisessionType").val()
					 	,questionType:$("#assiquestionType").val()
					 	,role:$("#assirole").val()
					 	,usergroup:$("#assiusergroup").val()
					 	,usergroupType:$("#assiusergroupType").val()
					 	,itemscount:$("#assiitemscount").val()
					 	,group:$("#assigroup").val()
					 	,status:$("#assistatus").val()
					 	,department:$("#assdepartment").val()
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
		<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>	
		<select id="assiInternalStatus" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach>
		</select>
		<span id="actorDiv" style="margin: 10px;display: none;">
		<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
		<select id="assiactor" class="sSelect"></select>
		</span>	
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</p>
	<div id="bulkResultDiv">	
	</div>	
	<input type="hidden" id="assihouseType" value="${houseType }">
	<input type="hidden" id="assisessionType" value="${sessionType }">
	<input type="hidden" id="assisessionYear" value="${sessionYear }">
	<input type="hidden" id="assiquestionType" value="${questionType }">
	<input type="hidden" id="assistatus" value="${status }">
	<input type="hidden" id="assirole" value="${role }">
	<input type="hidden" id="assiusergroup" value="${usergroup }">
	<input type="hidden" id="assiusergroupType" value="${usergroupType }">
	<input type="hidden" id="assiitemscount" value="${itemscount }">
	<input type="hidden" id="assigroup" value="${group}">
	<input type="hidden" id="assidepartment" value="${department}">
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
	<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 motion to continue..'></spring:message>" type="hidden">		
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>