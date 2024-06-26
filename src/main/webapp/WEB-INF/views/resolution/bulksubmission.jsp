<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="resolution.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){
		$(".up").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];	
			if(index!=1){		
				var currChk=$("#row"+index+" .chk").html();	
				var currSub=$("#row"+index+" .subject").html();	
				var prevChk=$("#row"+(index-1)+" .chk").html();	
				var prevSub=$("#row"+(index-1)+" .subject").html();
				$("#row"+index+" .chk").html(prevChk);
				$("#row"+index+" .subject").html(prevSub);
				$("#row"+(index-1)+" .chk").html(currChk);
				$("#row"+(index-1)+" .subject").html(currSub);
			}
		});
		$(".down").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#row"+index+" .chk").html();
				var currSub=$("#row"+index+" .subject").html();
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#row"+nextIndex+" .chk").html();
				var nextSub=$("#row"+nextIndex+" .subject").html();
				$("#row"+index+" .chk").html(nextChk);
				$("#row"+index+" .subject").html(nextSub);
				$("#row"+nextIndex+" .chk").html(currChk);
				$("#row"+nextIndex+" .subject").html(currSub);
			}
		});
		$("#chkall").change(function(){
			console.log($(this).is(":checked"));			
			if($(this).is(":checked")){
				$(".action").attr("checked","checked");	
			}else{
				$(".action").removeAttr("checked");
			}
		});
		$("#bulksubmit").click(function(){
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var items=new Array();
					$(".action").each(function(){
						if($(this).is(":checked")){
						items.push($(this).attr("id").split("chk")[1]);
						}
					});
		        	$.post('resolution/bulksubmission?items='+items
		        			+"&usergroupType=" + $("#usergroupType").val()
		        			+"&houseType=" + $('#houseType').val()
		        			+"&deviceType=" + $('#deviceType').val()
		        			+"&locale=" + $('#locale').val(),
		    	            function(data){
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	
		    					$("#bulkSubmissionDiv").html(data);	
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
			}});	
		});
	});		
	</script>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div id="bulkSubmissionDiv">	
	<c:choose>
		<c:when test="${!(empty resolutions) }">
			<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
			<table class="uiTable" style="width: 100%">
				<tr>
					<th><spring:message code="resolution.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
					<th><spring:message code="resolution.subject" text="Subject"></spring:message></th>
					<th><spring:message code="resolution.up" text="Up"></spring:message></th>
					<th><spring:message code="resolution.down" text="Down"></spring:message></th>
				</tr>			
				<c:set var="index" value="1"></c:set>	
				<c:forEach items="${resolutions}" var="i">
					<tr id="row${index}">
						<td class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td>
						<c:choose>
							<c:when test="${i.subject==null || i.subject=='' }">
								<td class="subject" style="text-align: justify;">${i.noticeContent}</td>
							</c:when>
							<c:otherwise>
								<td class="subject" style="text-align: justify;">${i.subject}</td>
							</c:otherwise>
						</c:choose>
						
						<td><input type="button" value="&#x2191;" class="up" style="width: 40px;"/></td>
						<td><input type="button" value="&#x2193;" class="down" style="width: 40px;"/></td>						
					</tr>
				<c:set var="index" value="${index+1}"></c:set>					
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="resolutions.noresolutions" text="No Completed Resolutions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="size" value="${size }">	
</div>
	
	<input id="submissionMsg" value="<spring:message code='resolution.client.prompt.submit' text='Do you want to submit the resolutions'></spring:message>" type="hidden">
	<input type="hidden" id="usergroupType" value="${usergroupType}" name="usergroupType"/>
	<input type="hidden" name="houseType" id="houseType" value="${houseType}"/>
	<input type="hidden" name="deviceType" id="deviceType" value="${deviceType}"/>
	<input type="hidden" name="locale" id="locale" value="${locale}"/>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>