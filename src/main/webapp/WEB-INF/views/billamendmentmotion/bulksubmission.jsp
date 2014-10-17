<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="billamendmentmotion.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){
		$('.amendedBill').each(function() {
			var amendedBill_TdId = this.id;			
			var amendedBillInfo = $('#'+amendedBill_TdId).html().replace(/\#/g,"~");
			$.get('ref/billamendmentmotion/amendedBillInfo?amendedBillInfo='+amendedBillInfo, function(data) {
				$('#'+amendedBill_TdId).empty();
				$('#'+amendedBill_TdId).html(data);			
			}).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		});
		
		$('.amendedSection').each(function() {			
			var amendedSection_TdId = this.id;			
			var amendedSectionInfo = $('#sectionNumberLabel').val() + " " + $('#'+amendedSection_TdId).html().split("#")[1];
			$('#'+amendedSection_TdId).html(amendedSectionInfo);
		});
		
		$(".up").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];	
			if(index!=1){		
				var currChk=$("#row"+index+" .chk").html();	
				var currAmendedBill=$("#row"+index+" .amendedBill").html();	
				var currAmendedSection=$("#row"+index+" .amendedSection").html();	
				var prevChk=$("#row"+(index-1)+" .chk").html();	
				var prevAmendedBill=$("#row"+(index-1)+" .amendedBill").html();
				var prevAmendedSection=$("#row"+(index-1)+" .amendedSection").html();
				$("#row"+index+" .chk").html(prevChk);
				$("#row"+index+" .amendedBill").html(prevAmendedBill);
				$("#row"+index+" .amendedSection").html(prevAmendedSection);
				$("#row"+(index-1)+" .chk").html(currChk);
				$("#row"+(index-1)+" .amendedBill").html(currAmendedBill);
				$("#row"+(index-1)+" .amendedSection").html(currAmendedSection);
			}
		});
		$(".down").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#row"+index+" .chk").html();
				var currAmendedBill=$("#row"+index+" .amendedBill").html();	
				var currAmendedSection=$("#row"+index+" .amendedSection").html();
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#row"+nextIndex+" .chk").html();
				var nextSub=$("#row"+nextIndex+" .subject").html();
				var nextAmendedBill=$("#row"+nextIndex+" .amendedBill").html();
				var nextAmendedSection=$("#row"+nextIndex+" .amendedSection").html();
				$("#row"+index+" .chk").html(nextChk);
				$("#row"+index+" .amendedBill").html(nextAmendedBill);
				$("#row"+index+" .amendedSection").html(nextAmendedSection);
				$("#row"+nextIndex+" .chk").html(currChk);
				$("#row"+nextIndex+" .amendedBill").html(currAmendedBill);
				$("#row"+nextIndex+" .amendedSection").html(currAmendedSection);
			}
		});
		$("#chkall").change(function(){
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
		        	$.post('billamendmentmotion/bulksubmission?items='+items
		        			+"&usergroupType="+$("#usergroupType").val(),  		    	             
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
		<c:when test="${!(empty billAmendmentMotions) }">
			<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>		
			<table class="uiTable">
				<tr>
					<th style="text-align:center"><spring:message code="billamendmentmotion.submitall" text="Submit All"></spring:message>
					<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
					<th><spring:message code="billamendmentmotion.amendedBill" text="Amended Bill"></spring:message></th>
					<th><spring:message code="billamendmentmotion.amendedSection" text="Amended Section"></spring:message></th>
					<th><spring:message code="billamendmentmotion.up" text="Up"></spring:message></th>
					<th><spring:message code="billamendmentmotion.down" text="Down"></spring:message></th>
				</tr>			
				<c:set var="index" value="1"></c:set>	
				<c:forEach items="${billAmendmentMotions}" var="i">
					<tr id="row${index}">
						<td class="chk" style="text-align:center;max-width:50px !important;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td>
						<td class="amendedBill" id="amendedBill${index}" style="min-width:240px !important;">${i.amendedBillInfo}</td>
						<td class="amendedSection" id="amendedSection${index}">${i.defaultAmendedSectionNumberInfo}</td>
						<td><input type="button" value="&#x2191;" class="up"  style="width: 40px;"/></td>
						<td><input type="button" value="&#x2193;" class="down"  style="width: 40px;"/></td>						
					</tr>
				<c:set var="index" value="${index+1}"></c:set>					
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="billamendmentmotion.nomotions" text="No Completed Bill Amendment Motions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="size" value="${size }">	
</div>	
	<input id="sectionNumberLabel" value="<spring:message code='billamendmentmotion.sectionAmendment.sectionNumber' text='Section Number'></spring:message>" type="hidden">
	<input id="submissionMsg" value="<spring:message code='billamendmentmotion.client.prompt.bulksubmit' text='Do you want to submit the selected bill amendment motions?'></spring:message>" type="hidden">
	<input type="hidden" id="usergroupType" value="${usergroupType}" name="usergroupType"/>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>