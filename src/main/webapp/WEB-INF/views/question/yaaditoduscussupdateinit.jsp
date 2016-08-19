<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.yaadtodiscussupdate" text="Mark as discussed" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){		
		/**** Bulk Put Up ****/
		$("#bulksubmit").click(function(){
			bulkUpdate();			
		});		
		/**** Load Actors On Changing Status ****/
		$("#updateStatus").change(function(){
			if($(this).val()!='-'){
				$.get('ref/selectedStatusType?statusId='+ $(this).val(),function(data){
					var statusType = data;
					if(statusType != '' && statusType=='question_processed_discussed'){
						$.get('question/yaadidiscussiondate',function(data){
						    $.fancybox.open(data, {autoSize: false, width: 500, height:200});
					    },'html').fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
					}
					 return false;
				});
			}
		});
		/**** Page Load ****/
		viewContent();
	});		
	/**** Display Questions File or Status Wise ****/
	function viewContent(){

		var parameters = "houseType="+$("#ydhouseType").val()
		 +"&sessionYear="+$("#ydsessionYear").val()
		 +"&sessionType="+$("#ydsessionType").val()
		 +"&questionType="+$("#ydquestionType").val()
		 +"&status="+$("#ydstatus").val()
		 +"&role="+$("#ydrole").val()
		 +"&usergroup="+$("#ydusergroup").val()
		 +"&usergroupType="+$("#ydusergroupType").val()
		 +"&group="+$("#ydgroup").val();
	
		var resource='question/yaaditodiscussupdate/assistant/view';
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
	
	function bulkUpdate(){
		
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
		var decisionStatus = $("#updateStatus").val();
		var status = $("#ydstatus").val();
		$.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('question/yaaditodiscussupdate/assistant/update?loadIt=yes',
			        	{items:items
			        	,decisionStatus:decisionStatus
			        	,status:status
			        	,houseType:$("#ydhouseType").val()
			   		 	,sessionYear:$("#ydsessionYear").val()
					 	,sessionType:$("#ydsessionType").val()
					 	,questionType:$("#ydquestionType").val()
					 	,role:$("#ydrole").val()
					 	,usergroup:$("#ydusergroup").val()
					 	,usergroupType:$("#ydusergroupType").val()
					 	,group:$("#ydgroup").val()
					 	,discussionDate:$("#ydDiscussionDate").val()
					 	,remark:$("#ydRemark").val()					 	
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
		<select id="updateStatus" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach>
		</select>
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
	</p>
	<div id="bulkResultDiv">	
	</div>	
	<input type="hidden" id="ydhouseType" value="${houseType}">
	<input type="hidden" id="ydsessionType" value="${sessionType}">
	<input type="hidden" id="ydsessionYear" value="${sessionYear}">
	<input type="hidden" id="ydquestionType" value="${questionType}">
	<input type="hidden" id="ydstatus" value="${status}">
	<input type="hidden" id="ydrole" value="${role}">
	<input type="hidden" id="ydusergroup" value="${usergroup}">
	<input type="hidden" id="ydusergroupType" value="${usergroupType}">
	<input type="hidden" id="ydgroup" value="${group}">
	<input type="hidden" id="ydDiscussionDate">
	<input type="hidden" id="ydRemark">
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
	<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 question to continue..'></spring:message>" type="hidden">		
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>