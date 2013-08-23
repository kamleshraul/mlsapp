<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">				
		$(document).ready(function() {		
		/**** Move Selected Items To Select Box ****/
		$("#to2").click(function(){
			var count=0;			
			$("#allItems option:selected").each(function(){
				$("#selectedItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
				$("#allItems option[value='"+$(this).val()+"']").remove();
				count++;
			});
			$("#presenteeCount").text(parseInt($("#presenteeCount").text())+count);
			$("#availableCount").text(parseInt($("#availableCount").text())-count);	
		});	
		/**** Move Selected Items To All Items Box ****/		
		$("#to1").click(function(){
			var count=0;		
			var itemsToHide="";	
			$("#selectedItems option").each(function(){
					if($(this).attr("selected")=='selected'){
						$("#selectedItems option[value='"+$(this).val()+"']").remove();	
						count++;
					}else{
						itemsToHide=itemsToHide+$(this).val()+",";	
					}					
			});	
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());	
			var itemsToRemoveFromBox1=itemsToHide.split(",");
			for(var i=0;i<itemsToRemoveFromBox1.length;i++){
				$("#allItems option[value='"+itemsToRemoveFromBox1[i]+"']").remove();					
			}		
			$("#presenteeCount").text(parseInt($("#presenteeCount").text())-count);
			$("#availableCount").text(parseInt($("#availableCount").text())+count);
		});	
		/***** Move All Items To Selected Box ****/
		$("#allTo2").click(function(){
			$("#selectedItems").append($("#allItems").html());
			$("#allItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);			
		});
		/**** Move All Selected Items To All Item Box ****/
		$("#allTo1").click(function(){
			var count=0;			
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());	
			$("#selectedItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);
			$("#selectedItems").removeClass("round"+$("#round").val());				
		});
		/**** for moving items up ****/
		$(".up").click(function(){
			//get the currently slected item and its index
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
				//if index is not 0 then proceed
				if(index!=0){
					//swap current with previous
					var prev=$("#selectedItems option:eq("+(index-1)+")");
					var prevVal=prev.val();
					var prevText=prev.text();
					prev.val(current.val());
					prev.text(current.text());
					current.val(prevVal);
					current.text(prevText);	
					//set previous as selected and remove selection from current
					prev.attr("selected","selected");
					current.removeAttr("selected");								
				}
			
		});
		/**** for moving items down ****/		
		$(".down").click(function(){
			//get the currently slected item and its index			
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
			var length=$("#selectedItems option").length;
			//if end of items is not reached then proceed
				if(index!=length-1){
					//swap current with next				
					var next=$("#selectedItems option:eq("+(index+1)+")");
					var nextVal=next.val();
					var nextText=next.text();
					next.val(current.val());
					next.text(current.text());
					current.val(nextVal);
					current.text(nextText);	
					//set next as selected and remove selection from current
					next.attr("selected","selected");
					current.removeAttr("selected");					
				}			
		});		
		/**** submit function ****/
		$(".submit").click(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var items=new Array();
			$("#selectedItems option").each(function(){
				items.push($(this).val());
			});
			var attendance=$("#selectedAttendance").val();
			$.ajax({
				url:'ballot/attendance?items='+items
				+"&attendance="+$("#selectedAttendance").val()				
				+'&session='+$("#session").val()
				+"&deviceType="+$("#deviceType").val(),
				type:'PUT',
				success:function(data){
				 if(data=='success'){
					 $("#successDiv").show();
					 $("#failedDiv").hide();	
					 $("#noMemberDiv").hide();					 				 
				 }else if(data=='nomembers'){
					 $("#noMemberDiv").show();
					 $("#successDiv").hide();					 
					 $("#failedDiv").hide();				 					 
				 }else{
					 $("#failedDiv").show();
					 $("#successDiv").hide();					 
					 $("#noMemberDiv").hide();					 			 
				 }
				 $.unblockUI();					 
				}
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
	});
		</script>
	
	<style type="text/css">
		input[type=button]{
			width:30px;
			margin: 5px;
			font-size: 13px; 
			padding: 5px;
			/*display: inline;*/ 		
		}	
	.highlight{
	font-weight: bold;
	}
	</style>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=3" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="reportDiv">
<div id="attendanceResultDiv">
<div class="toolTip tpGreen clearfix" id="successDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="attendance.update_success" text="Attendance saved successfully."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="failedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="attendance.failed" text="Attendance update failed.Please refresh and try again."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="noMemberDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="attendance.nomember" text="There are no members to create attendance."/>
		</p>
		<p></p>
</div>



<table id="presentTable">
<tr>
<th>
<spring:message code="attendance.eligiblesList" text="List of Available Members"></spring:message>
(<span id="availableCount">${allItemsCount}</span>)
</th>
<th>
</th>
<th>
<span id="presenteesList" >
<spring:message code="attendance.presenteesList" text="List of Present Members"></spring:message>
(<span id="presenteeCount">${selectedItemsCount}</span>)
</span>
</th>
<th>
</th>
</tr>
<tr>
<td>
<select id="allItems" multiple="multiple" style="height:300px;width:350px;margin-top:-50px;">
<c:forEach items="${allItems }" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
</td>
<td>
<input type="button" id="to2" value="&gt;" />
<input type="button" id="allTo2" value="&gt;&gt;" />
<br>
<input type="button" id="allTo1" value="&lt;&lt;" />
<input type="button" id="to1" value="&lt;"  />
</td>
<td>
<select id="selectedItems" multiple="multiple" style="height:300px;width:350px;" class="round${round }">
<c:forEach items="${selectedItems}" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
<br>
<input type="button" value="<spring:message code='generic.submit' text='Submit'/>" id="submit" style="width:100px;height:40px;margin: 5px;margin-left:100px; " class="submit">
</td>
<td>
<input id="up" type="button" value="&#x2191;" class="up"/>
<br>
<input id="down" type="button" value="&#x2193;" class="down"/>
</td>
</tr>
</table>

<table id="absentTable">
<tr>
<th>
<span id="absenteeList" >
<spring:message code="memberballot.absenteeList" text="List of Absent Members"></spring:message>
(<span id="absenteeCount">${selectedItemsCount}</span>)
</span>
</th>
<th>
</th>
</tr>
<tr>
<td>
<select id="selectedItems" multiple="multiple" style="height:300px;width:350px;" class="round${round }">
<c:forEach items="${selectedItems}" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
<br>
<input type="button" value="<spring:message code='generic.submit' text='SubmitA'/>" id="submit" style="width:100px;height:40px;margin: 5px;margin-left:100px; " class="submit">
</td>
<td>
<input id="upA" type="button" value="&#x2191;" class="up"/>
<br>
<input id="downA" type="button" value="&#x2193;" class="down"/>
</td>
</tr>
</table>


<select id="itemMaster" style="display:none;">
<c:forEach items="${eligibles }" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
<input id="selectItemFirstMessage" value="<spring:message code='attendance.selectitem' text='Select an item first'/>" type="hidden">
</div>
</div>
<input type="hidden" id="currentattendance" name="currentattendance" value="${attendance}">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>