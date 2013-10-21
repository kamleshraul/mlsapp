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
		$('.datemask').focus(function(){		
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});
		
		$(".up").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];	
			if(index!=1){		
				var currChk=$("#row"+index+" .chk").html();	
				var currTitle=$("#row"+index+" .title").html();	
				var currNum = $("#row"+index+" .nums").val();
				var currDate = $("#row"+index+" .dates").val();
				var currDateName = $("#row"+index+" .dates").attr('name');
				var prevChk=$("#row"+(index-1)+" .chk").html();	
				var prevTitle=$("#row"+(index-1)+" .title").html();
				var prevNum = $("#row"+(index-1)+" .nums").val();
				var prevDate = $("#row"+(index-1)+" .dates").val();
				var prevDateName = $("#row"+(index-1)+" .dates").attr('name');
				
				var prevDateNameNumber = prevDateName.split("ooo")[0];
				
				prevDateName = currDateName.split("ooo")[0]+"ooo"+prevDateName.split("ooo")[1]+"ooo"+prevDate;
				currDateName = prevDateNameNumber+"ooo"+currDateName.split("ooo")[1]+"ooo"+currDate;
				
				$("#row"+index+" .chk").html(prevChk);
				$("#row"+index+" .title").html(prevTitle);
				$("#row"+index+" .nums").val(currNum);
				$("#row"+index+" .dates").val(prevDate);
				$("#row"+index+" .dates").attr('name',prevDateName);
				$("#row"+(index-1)+" .chk").html(currChk);
				$("#row"+(index-1)+" .title").html(currTitle);
				$("#row"+(index-1)+" .nums").val(prevNum);
				$("#row"+(index-1)+" .dates").val(currDate);
				$("#row"+(index-1)+" .dates").attr('name',currDateName);
			}
		});
		$(".down").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#row"+index+" .chk").html();
				var currTitle=$("#row"+index+" .title").html();
				var currNum=$("#row"+index+" .nums").val();
				var currDate=$("#row"+index+" .dates").val();
				var currDateName = $("#row"+index+" .dates").attr('name');
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#row"+nextIndex+" .chk").html();
				var nextTitle=$("#row"+nextIndex+" .title").html();
				var nextNum = $("#row"+nextIndex+" .nums").val();
				var nextDate = $("#row"+nextIndex+" .dates").val();
				var nextDateName = $("#row"+nextIndex+" .dates").attr('name');
				
				var currDateNameNumber=currDateName.split("ooo")[0];
				
				currDateName = nextDateName.split("ooo")[0]+"ooo"+currDateName.split("ooo")[1]+"ooo"+currDate;
				nextDateName = currDateNameNumber+"ooo"+nextDateName.split("ooo")[1]+"ooo"+nextDate;
			
				$("#row"+index+" .chk").html(nextChk);
				$("#row"+index+" .title").html(nextTitle);
				$("#row"+index+" .nums").val(currNum);
				$("#row"+index+" .dates").val(nextDate);
				$("#row"+index+" .dates").attr('name',nextDateName);
				$("#row"+nextIndex+" .chk").html(currChk);
				$("#row"+nextIndex+" .title").html(currTitle);
				$("#row"+nextIndex+" .nums").val(nextNum);
				$("#row"+nextIndex+" .dates").val(currDate);
				$("#row"+nextIndex+" .dates").attr('name',currDateName);
			}
		});
		
		//============for discuss date================>
		$(".dup").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];	
			if(index!=1){		
				var currChk=$("#drow"+index+" .dchk").html();	
				var currTitle=$("#drow"+index+" .dtitle").html();	
				var currNum = $("#drow"+index+" .dnums").val();
				var currDate = $("#drow"+index+" .ddates").val();
				var currDateName = $("#drow"+index+" .ddates").attr('name');
				var prevChk=$("#drow"+(index-1)+" .dchk").html();	
				var prevTitle=$("#drow"+(index-1)+" .dtitle").html();
				var prevNum = $("#drow"+(index-1)+" .dnums").val();
				var prevDate = $("#drow"+(index-1)+" .ddates").val();
				var prevDateName = $("#drow"+(index-1)+" .ddates").attr('name');
				
				var prevDateNameNumber = prevDateName.split("ooo")[0];
				
				prevDateName = currDateName.split("ooo")[0]+"ooo"+prevDateName.split("ooo")[1]+"ooo"+prevDate;
				currDateName = prevDateNameNumber+"ooo"+currDateName.split("ooo")[1]+"ooo"+currDate;
				
				$("#drow"+index+" .dchk").html(prevChk);
				$("#drow"+index+" .dtitle").html(prevTitle);
				$("#drow"+index+" .dnums").val(currNum);
				$("#drow"+index+" .ddates").val(prevDate);
				$("#drow"+index+" .ddates").attr('name',prevDateName);
				$("#drow"+(index-1)+" .dchk").html(currChk);
				$("#drow"+(index-1)+" .dtitle").html(currTitle);
				$("#drow"+(index-1)+" .dnums").val(prevNum);
				$("#drow"+(index-1)+" .ddates").val(currDate);
				$("#drow"+(index-1)+" .ddates").attr('name',currDateName);
			}
		});
		$(".ddown").click(function(){
			var index=$(this).closest("tr").attr("id").split("row")[1];
			if(index!=parseInt($("#size").val())){		
				var currChk=$("#drow"+index+" .dchk").html();
				var currTitle=$("#drow"+index+" .dtitle").html();
				var currNum=$("#drow"+index+" .dnums").val();
				var currDate=$("#drow"+index+" .ddates").val();
				var currDateName = $("#drow"+index+" .ddates").attr('name');
				var nextIndex=parseInt(index)+1;
				var nextChk=$("#drow"+nextIndex+" .dchk").html();
				var nextTitle=$("#drow"+nextIndex+" .dtitle").html();
				var nextNum = $("#drow"+nextIndex+" .dnums").val();
				var nextDate = $("#drow"+nextIndex+" .ddates").val();
				var nextDateName = $("#drow"+nextIndex+" .ddates").attr('name');
				
				var currDateNameNumber=currDateName.split("ooo")[0];
				
				currDateName = nextDateName.split("ooo")[0]+"ooo"+currDateName.split("ooo")[1]+"ooo"+currDate;
				nextDateName = currDateNameNumber+"ooo"+nextDateName.split("ooo")[1]+"ooo"+nextDate;
			
				$("#drow"+index+" .dchk").html(nextChk);
				$("#drow"+index+" .dtitle").html(nextTitle);
				$("#drow"+index+" .dnums").val(currNum);
				$("#drow"+index+" .ddates").val(nextDate);
				$("#drow"+index+" .ddates").attr('name',nextDateName);
				$("#drow"+nextIndex+" .dchk").html(currChk);
				$("#drow"+nextIndex+" .dtitle").html(currTitle);
				$("#drow"+nextIndex+" .dnums").val(nextNum);
				$("#drow"+nextIndex+" .ddates").val(currDate);
				$("#drow"+nextIndex+" .ddates").attr('name',currDateName);
			}
		});
			
		
		$("#discussdatesubmit").click(function(){
			var checkedany = false;
			$('.daction').each(function(){
				if($(this).is(':checked') && $(this).attr('disabled')==undefined){
					checkedany = true;
				}
			});
						
			if(checkedany){
				$.prompt($('#dateSubmissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						var items = new Array();
						$(".ddates").each(function(){
							var chk = $("#dchk"+$(this).attr("name").split("ooo")[1]).is(':checked');
							
							if(chk){
								if($(this).val()!=''){
									items.push($(this).attr("name")+"ooo"+$(this).val());
								}
							}
						});
						
			        	$.post('bill/providedate/update?items='+items+ '&round='+ $('#discussdateround').val() +'&action=discussion'
			        			+'&usergroupType='+'&houseType='+ $("#selectedHouseType").val() +$("#usergroupType").val(),  		    	             
			    	            function(data){
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	
			    					$("#successDiscussionDate").html(data);	
			    					$(".daction").each(function(){
			    						if($(this).is(":checked")){
			    							$(this).attr('disabled','disabled');
			    						}
			    					});
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
			}else{
				$.prompt($('#dateSubmissionErrorMsg').val());
			}
		});
		
		//<============for discuss date================
		
		$("#chkall").change(function(){			
			if($(this).is(":checked")){
				$(".action").attr("checked","checked");	
			}else{
				$(".action").removeAttr("checked");
			}
		});
		
		$("#dchkall").change(function(){	
			if($(this).is(":checked")){
				$(".daction").attr("checked","checked");	
			}else{
				$(".daction").removeAttr("checked");
			}
		});
		$("#introdatesubmit").click(function(){
			var checkedany = false;
			$('.action').each(function(){
				if($(this).is(':checked') && $(this).attr('disabled')==undefined){
					checkedany = true;
				}
			});
						
			if(checkedany){
				$.prompt($('#dateSubmissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						var items = new Array();
						$(".dates").each(function(){
							var chk = $("#chk"+$(this).attr("name").split("ooo")[1]).is(':checked');
							
							if(chk){
								if($(this).val()!=''){
									items.push($(this).attr("name")+"ooo"+$(this).val());
								}
							}
						});
						
			        	$.post('bill/providedate/update?items='+items+'&round='+ $('#discussdateround').val() +'&action=introduction'
			        			+'&houseType='+$("#selectedHouseType").val()+"&usergroupType="+$("#usergroupType").val(),  		    	             
			    	            function(data){
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	
			    					$("#successIntroDate").html(data);
			    					
			    					$(".action").each(function(){
			    						if($(this).is(":checked")){
			    							$(this).attr('disabled','disabled');
			    						}
			    					});
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
			}else{
				$.prompt($('#dateSubmissionErrorMsg').val());
			}
		});
	});		
	</script>
</head>
<body>	
	<div>
		<h4 id="error_p">&nbsp;</h4>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
	</div>
	<div id="introDateSubmissionDiv">
		<h3 style="background: #B4CEE8; border: 2px solid #000000; width: 100%;"><spring:message code="bill.expectedintroductiondate" text="Expected Introduction Date" /></h3>
		<h4 id="successIntroDate" style="color: greeen;"></h4>
		<c:choose>
			<c:when test="${!(empty introBills) }">
				<c:choose>
					<c:when test="${usergroupType=='recommendationFromGovernor_department'}">
						<input type="button" id="introdatesubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
					</c:when>
				</c:choose>		
				<table class="uiTable">
					<tr>
						<th><spring:message code="bill.provideintrodate.priority" text="Priority" /></th>
						<th><spring:message code="bill.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th><spring:message code="bill.title" text="Title"></spring:message></th>
						<th><spring:message code="bill.up" text="Up"></spring:message></th>
						<th><spring:message code="bill.down" text="Down"></spring:message></th>
						<th><spring:message code="bill.provideintrodate" text="Date"></spring:message></th>
					</tr>			
					<c:set var="index" value="1"></c:set>	
					<c:forEach items="${introBills}" var="i">
						<tr id="row${index}">
							<td>${index}</td>
							<td class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true">&nbsp;&nbsp;${i.number}</td>
							<td class="title">
								${i.title}
								<input type="hidden" id="num${i.id}" name="num${i.id}" class="nums" value="${index}" />
							</td>
							<td><input type="button" value="&#x2191;" class="up" style="width: 40px;"/></td>
							<td><input type="button" value="&#x2193;" class="down" style="width: 40px;"/></td>
							<c:choose>
								<c:when test="${usergroupType=='recommendationFromGovernor_department'}">
									<td><input type="text" id="date${i.id}" name="${index}ooo${i.id}" value="${i.formattedExpectedIntroductionDate}" class="datemask sText dates" style="max-width: 65px;" /></td>
								</c:when>		
								<c:otherwise>
									<td><input type="text" id="date${i.id}" name="${index}ooo${i.id}" value="${i.formattedExpectedIntroductionDate}" class="datemask sText dates" style="max-width: 65px;" readonly="readonly" /></td>
								</c:otherwise>
							</c:choose>						
						</tr>
					<c:set var="index" value="${index+1}"></c:set>					
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<spring:message code="bill.nobills" text="No Completed Bills Found"></spring:message>
			</c:otherwise>
		</c:choose>
	</div>
	<div id="discussDateSubmissionDiv">
		<h3 style="background: #B4CEE8; border: 2px solid #000000; width: 100%;"><spring:message code="bill.expecteddiscussiondate" text="Expected Discussion Date" /></h3>
		<h5 id="successDiscussionDate" style="color: greeen;"></h5>
		<c:choose>
			<c:when test="${!(empty discussBills) }">
				<c:choose>
					<c:when test="${usergroupType=='recommendationFromGovernor_department'}">
						<input type="button" id="discussdatesubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
						|
						<label class="small" value="<spring:message code='bill.discussiondate.round' text='Round'/>" />
						<select id="discussdateround" style="width: 100px;margin: 10px;">
							<c:forEach items="${rounds}" var="r">
								<option value="${r.value}">${r.name}</option>
							</c:forEach>
						</select>
					</c:when>
				</c:choose>
				<table class="uiTable">
					<tr>
						<th><spring:message code="bill.providediscussdate.priority" text="Priority" /></th>
						<th><spring:message code="bill.submitall" text="Submit All"></spring:message>
						<input type="checkbox" id="dchkall" name="dchkall" class="sCheck" value="true"></th>
						<th><spring:message code="bill.title" text="Title"></spring:message></th>
						<th><spring:message code="bill.up" text="Up"></spring:message></th>
						<th><spring:message code="bill.down" text="Down"></spring:message></th>
						<th><spring:message code="bill.providediscussdate" text="Date"></spring:message></th>
					</tr>			
					
					<c:set var="dindex" value="1"></c:set>
					<c:forEach items="${discussBills}" var="i">
						<tr id="drow${dindex}">
							<td>${dindex}</td>
							<td class="dchk"><input type="checkbox" id="dchk${i.id}" name="dchk${i.id}" class="sCheck daction" value="true">&nbsp;&nbsp;${i.number}</td>
							<td class="dtitle">
								${i.title}
								<input type="hidden" id="dnum${i.id}" name="dnum${i.id}" class="dnums" value="${dindex}" />
							</td>
							<td><input type="button" value="&#x2191;" class="dup" style="width: 40px;"/></td>
							<td><input type="button" value="&#x2193;" class="ddown" style="width: 40px;"/></td>
							<c:choose>
								<c:when test="${usergroupType=='recommendationFromGovernor_department'}">
									<td><input type="text" id="ddate${i.id}" name="${dindex}ooo${i.id}" class="datemask sText ddates" style="max-width: 65px;" value="${i.formattedExpectedDiscussionDate}" /></td>
								</c:when>		
								<c:otherwise>
									<td><input type="text" id="ddate${i.id}" name="${dindex}ooo${i.id}" class="datemask sText ddates" style="max-width: 65px;" value="${i.formattedExpectedDiscussionDate}" readonly="readonly" /></td>
								</c:otherwise>
							</c:choose>				
						</tr>
					<c:set var="dindex" value="${dindex+1}"></c:set>					
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<spring:message code="bill.nobills" text="No Bills Found"></spring:message>
			</c:otherwise>
		</c:choose>	
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input id="dateSubmissionMsg" value="<spring:message code='bill.prompt.datesubmit' text='Do you want to submit the dates.'></spring:message>" type="hidden">
	<input id="dateSubmissionErrorMsg" value="<spring:message code='bill.prompt.datesubmiterror' text='No bill selected.'></spring:message>" type="hidden">
	<input type="hidden" id="usergroupType" value="${usergroupType}" name="usergroupType"/>
</body>
</html>