<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.bulksubmission" text="Bulk Submissions" /></title>
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
				var currTitle=$("#row"+index+" .title").html();	
				var prevChk=$("#row"+(index-1)+" .chk").html();	
				var prevTitle=$("#row"+(index-1)+" .title").html();
				$("#row"+index+" .chk").html(prevChk);
				$("#row"+index+" .title").html(prevTitle);
				$("#row"+(index-1)+" .chk").html(currChk);
				$("#row"+(index-1)+" .title").html(currTitle);
			}
		});
		$(".down").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#row"+index+" .chk").html();
				var currTitle=$("#row"+index+" .title").html();
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#row"+nextIndex+" .chk").html();
				var nextTitle=$("#row"+nextIndex+" .title").html();
				$("#row"+index+" .chk").html(nextChk);
				$("#row"+index+" .title").html(nextTitle);
				$("#row"+nextIndex+" .chk").html(currChk);
				$("#row"+nextIndex+" .title").html(currTitle);
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
		        	$.post('bill/bulksubmission?items='+items
		        			+"&usergroupType="+$("#usergroupType").val(),  		    	             
		    	            function(data){
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	
		    					$("#bulkSubmissionDiv").html(data);	
		    	            },'html');
		        }
			}});	
		});
	});		
	</script>
</head>
<body>	
	<div id="bulkSubmissionDiv">	
	<c:choose>
		<c:when test="${!(empty bills) }">
			<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
			<table class="uiTable">
				<tr>
					<th><spring:message code="bill.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
					<th><spring:message code="bill.title" text="Title"></spring:message></th>
					<th><spring:message code="bill.up" text="Up"></spring:message></th>
					<th><spring:message code="bill.down" text="Down"></spring:message></th>
				</tr>			
				<c:set var="index" value="1"></c:set>	
				<c:forEach items="${bills}" var="i">
					<tr id="row${index}">
						<td class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td>
						<td class="title">${i.title}</td>
						<td><input type="button" value="&#x2191;" class="up" style="width: 40px;"/></td>
						<td><input type="button" value="&#x2193;" class="down" style="width: 40px;"/></td>						
					</tr>
				<c:set var="index" value="${index+1}"></c:set>					
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="bill.nobills" text="No Completed Bills Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="size" value="${size }">	
</div>
	
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the bills.'></spring:message>" type="hidden">
	<input type="hidden" id="usergroupType" value="${usergroupType}" name="usergroupType"/>
</body>
</html>