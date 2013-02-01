<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">				
		$(document).ready(function() {		
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
		$("#to1").click(function(){
			var count=0;			
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());	
			$("#selectedItems option").each(function(){
				if($(this).attr("selected")!='selected'){
					$("#allItems option[value='"+$(this).val()+"']").remove();
				}else{
					$("#selectedItems option[value='"+$(this).val()+"']").remove();	
					count++;				
				}			
			});			
			$("#presenteeCount").text(parseInt($("#presenteeCount").text())-count);
			$("#availableCount").text(parseInt($("#availableCount").text())+count);
		});	
		$("#allTo2").click(function(){
			$("#selectedItems").empty();
			$("#selectedItems").html($("#allItems").html());
			$("#allItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);			
		});
		$("#allTo1").click(function(){
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());
			$("#selectedItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);
		});
		/**** for moving items up ****/
		$(".up").click(function(){
			console.log("hello");
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
		/**** if current list of members(present or absent) are locked then check locked ****/
		if($("#membersLocked").val()=='true'){
			var attendance=$("#selectedAttendance").val();
			if(attendance=='true'){
			$("#lock").attr("checked","checked");
			}else{
			$("#lockA").attr("checked","checked");
			}			
		}
		/**** submit function ****/
		$(".submit").click(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var items=new Array();
			$("#selectedItems option").each(function(){
				items.push($(this).val());
			});
			var attendance=$("#selectedAttendance").val();
			var locked=$("#locked").val();
			//var locked=false;
			//if(attendance=='true'){
				//locked=$("#lock").is(":checked");
			//}else{
				//locked=$("#lockA").is(":checked");
			//}
			//if(items.length!=0){
			$.ajax({
				url:'ballot/memberballot/attendance?items='+items
				+"&attendance="+$("#selectedAttendance").val()				
				+"&round="+$("#selectedRound").val()
				+'&session='+$("#session").val()
				+"&questionType="+$("#questionType").val()
				+"&locked="+locked,
				type:'PUT',
				success:function(data){
				console.log(data);
				 if(data=='success'){
					 $("#successDiv").show();
					 $("#lockedDiv").hide();					 
					 $("#failedDiv").hide();					 
				 }else if(data=='locked'){
					 $("#lockedDiv").show();
					 $("#successDiv").hide();					 
					 $("#failedDiv").hide();				 					 
				 }else{
					 $("#failedDiv").show();
					 $("#successDiv").hide();					 
					 $("#lockedDiv").hide();			 
				 }
				 $.unblockUI();					 
				}
			});
			//}else{
				//$.unblockUI();				
				//$.prompt($('#selectItemFirstMessage').val());				
			//}
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
	</style>
</head>

<body>

<div id="attendanceResultDiv">
<div class="toolTip tpGreen clearfix" id="successDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="update_success" text="Data saved successfully."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="lockedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="locked" text="Changes cannot be done after Member Ballot is created."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="failedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="failed" text="Changes cannot be updated.Please refresh and try again."/>
		</p>
		<p></p>
</div>



<table id="presentTable">
<tr>
<th>
<spring:message code="memberballot.eligiblesList" text="List of Available Members"></spring:message>
(<span id="availableCount">${allItemsCount}</span>)
</th>
<th>
</th>
<th>
<span id="presenteesList" >
<spring:message code="memberballot.presenteesList" text="List of Present Members"></spring:message>
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
<select id="selectedItems" multiple="multiple" style="height:300px;width:350px;">
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
<select id="selectedItems" multiple="multiple" style="height:300px;width:350px;">
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
<input id="membersLocked" name="membersLocked" value="${membersLocked }" type="hidden">
</div>

<input type="hidden" id="locked" name="locked" value="${locked}">
</body>
</html>