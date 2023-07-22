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
			console.log("inside ")
			bulkUpdate();		
			
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
		 +"&group="+$("#ydgroup").val()
		 +"&answeringDate="+$("#ydansweringDate").val();
	
		var resource='question/yaaditoupdateContent/assistant/view';
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
	
		var qsnId=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
			
			qsnId.push($(this).attr("id").split("chk")[1]);
			
			}
		});	
	
		if(qsnId.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}
		
		 
		var items =new Array();
		for (var i=0; i<qsnId.length; i++) {
			console.log(i+"here")
		    items.push({'questionId':qsnId[i],'subject':document.querySelector('.subject_'+qsnId[i]).innerText,'revisedQuestionText':document.querySelector('.revisedQuestionText_'+qsnId[i]).innerText,
		    	'answer':document.querySelector('.answer_'+qsnId[i]).innerText
		})
		         
			;
		}
		console.log(items)
		 $.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('question/yaaditoUpdateQsnContent/assistant/update?loadIt=yes',
			        	{items:items,
					    itemsLength:items.length
			        	,houseType:$("#ydhouseType").val()
			   		 	,sessionYear:$("#ydsessionYear").val()
					 	,sessionType:$("#ydsessionType").val()
					 	,questionType:$("#ydquestionType").val()
					 	,role:$("#ydrole").val()
					 	,usergroup:$("#ydusergroup").val()
					 	,usergroupType:$("#ydusergroupType").val()
					 	,group:$("#ydgroup").val()
					 	,answeringDate:$("#ydansweringDate").val()
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
	<input type="hidden" id="ydansweringDate" value="${answeringDate}">
	<input type="hidden" id="ydDiscussionDate">
	<input type="hidden" id="ydRemark">
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="selectActorMsg" value="<spring:message code='client.prompt.selectactor' text='Please select the actor.'></spring:message>" type="hidden">
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.pleaseselect' text='Please Select.'></spring:message>" type="hidden">		
	<input id="selectItemsMsg" value="<spring:message code='client.prompt.selectitems' text='Please select atleast 1 question to continue..'></spring:message>" type="hidden">		
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>