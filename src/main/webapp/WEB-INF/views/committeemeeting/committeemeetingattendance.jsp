<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function setSelectedCommitteeType() {
		var touritineraryId = $('#tourdateFilter').val();
		$('#selectedtourdate').val(touritineraryId);
	}
	function onTourdateFilterChange() {
		 $("#failedDiv").hide();
		 $("#successDiv").hide();					 
		 $("#lockedDiv").hide();
		 $("#noMemberDiv").hide();	
		 $("#ballotnotcreatedDiv").hide();		
		var touritineraryId = $('#tourdateFilter').val();
		
		var resourceURL = "ref/CommitteeMemberAttendance/" + touritineraryId;
	
			$.get(resourceURL, function(data){
				
				var dataLength = data.length;
				
				if(dataLength > 0) {
					var text1 = "";
					var text2 ="";
					var text1Count=0;
					var text2Count=0;
					for(var i = 0; i < dataLength; i++) {
						
						if(data[i].isSelected)
						{
						text2 += "<option value='" + data[i].id + "' class='highlight'>" + data[i].name + "</option>";
						text2Count+=1;
						}
						else
							{
						text1 += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
						text1Count+=1;
							}
					}
					
					$('#allItems').empty();
					$('#selectedItems').empty();
					
					$('#allItems').html(text1);
					$('#selectedItems').html(text2);
					$("#presenteeCount").text(text2Count);
					$("#availableCount").text(text1Count);	
				}
				else {
					$('#allItems').empty();
					$('#selectedItems').empty();
				}
		
			});
		
	}
		/**** Move Selected Items To Select Box ****/
		$("#to2").click(function(){
			var count=0;			
			$("#allItems option:selected").each(function(){
				$("#selectedItems").append("<option value='"+$(this).val()+"' class='highlight'>"+$(this).text()+"</option>");
				$("#allItems option[value='"+$(this).val()+"']").remove();
				count++;
			});
			$("#presenteeCount").text(parseInt($("#presenteeCount").text())+count);
			$("#availableCount").text(parseInt($("#availableCount").text())-count);	
			$("#selectedItems").addClass("round"+$("#round").val());
		});	
			/***** Move All Items To Selected Box ****/
		$("#allTo2").click(function(){
			var itemsfromprevious=$("#itemsFrompreviousRounds").val();			
			$("#selectedItems").append($("#allItems").html());
			$("#allItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);
			$("#selectedItems option").each(function(){
				if(itemsfromprevious.indexOf($(this).text())==-1){
					$(this).addClass("highlight");				
				}	
			});		
			$("#selectedItems").addClass("round"+$("#round").val());					
		});
		/**** Move Selected Items To All Items Box ****/		
		$("#to1").click(function(){
			var count=0;			
			$("#selectedItems option:selected").each(function(){
				$("#allItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
				$("#selectedItems option[value='"+$(this).val()+"']").remove();
				count++;
			});
			$("#presenteeCount").text(parseInt($("#presenteeCount").text())-count);
			$("#availableCount").text(parseInt($("#availableCount").text())+count);	
			$("#selectedItems").removeClass("round"+$("#round").val());
		
		});	
		/**** Move All Selected Items To All Item Box ****/
		$("#allTo1").click(function(){

			var itemsfromprevious=$("#itemsFrompreviousRounds").val();			
			$("#allItems").append($("#selectedItems").html());
			$("#selectedItems").empty();
			$("#presenteeCount").text($("#selectedItems option").length);
			$("#availableCount").text($("#allItems option").length);
			$("#allItems option").each(function(){
				if(itemsfromprevious.indexOf($(this).text())==-1){
					$(this).removeClass("highlight");			
				}	
			});		
			$("#allItems").removeClass("round"+$("#round").val());
		});
		
		/**** submit function ****/
		$(".submit").click(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var items=new Array();
			$("#selectedItems option").each(function(){
				items.push($(this).val());
			});
			var absentMembers=new Array();
			$("#allItems option").each(function(){
				absentMembers.push($(this).val());
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
				url:'committeemeeting/attendance?items='+items
				+"&absentMembers="+absentMembers
				+"&attendance="+$("#selectedAttendance").val()				
				+"&round="+$("#selectedRound").val()
				+"&totalrounds="+$("#noOfRounds").val()
				+'&session='+$("#session").val()
				+"&questionType="+$("#questionType").val()
				+"&locked="+locked,
				type:'PUT',
				success:function(data){
				 if(data=='success'){
					 $("#successDiv").show();
					 $("#lockedDiv").hide();					 
					 $("#failedDiv").hide();	
					 $("#noMemberDiv").hide();		
					 $("#ballotnotcreatedDiv").hide();					 			 				 
				 }else if(data=='locked'){
					 $("#lockedDiv").show();
					 $("#successDiv").hide();					 
					 $("#failedDiv").hide();
					 $("#noMemberDiv").hide();	 	
					 $("#ballotnotcreatedDiv").hide();						 			 					 
				 }else if(data=='nomembers'){
					 $("#noMemberDiv").show();
					 $("#lockedDiv").hide();
					 $("#successDiv").hide();					 
					 $("#failedDiv").hide();
					 $("#ballotnotcreatedDiv").hide();						 				 					 
				 }else if(data=='ballotnotcreated'){
					 $("#noMemberDiv").hide();
					 $("#lockedDiv").hide();
					 $("#successDiv").hide();					 
					 $("#failedDiv").hide();
					 $("#ballotnotcreatedDiv").show();	 				 					 
				 }else{
					 $("#failedDiv").show();
					 $("#successDiv").hide();					 
					 $("#lockedDiv").hide();
					 $("#noMemberDiv").hide();	
					 $("#ballotnotcreatedDiv").hide();				 			 
				 }
				 $.unblockUI();					 
				}
			}).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
		
	});
		
	/* =============== DOCUMENT READY =============== */
	$('document').ready(function(){
			$('#tourdateFilter').change(function(){
			onTourdateFilterChange();
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
	.round1{
	color:green ;
	}
	.round2{
	color:blue ;
	}
	.round3{
	color: red;
	}
	.round4{
	color: black;
	}
	.round5{
	color: #F26522;
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
			<spring:message code="memberballotattendance.update_success" text="Data saved successfully."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="lockedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="memberballotattendance.locked" text="Changes cannot be done after Member Ballot is created."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="failedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="memberballotattendance.failed" text="Changes cannot be updated.Please refresh and try again."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="noMemberDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="memberballotattendance.nomember" text="There are no members to create member ballot."/>
		</p>
		<p></p>
</div>
<div class="toolTip tpRed clearfix" id="ballotnotcreatedDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="memberballotattendance.previousmemberballotnotcreated" text="Member Ballot of previous round not created"/>
		</p>
		<p></p>
</div>
<c:if test="${tourdates!=null}">
		<div id="selectiontourdate" class="commandbarContent" style="margin-top: 10px;">
		<a href="#" id="select_tourdate" class="butSim">
			<spring:message code="generic.date" text="Tour Date"/>
		</a>
		<select id="tourdateFilter" name="tourdateFilter" class="sSelect">
		<c:forEach items="${tourdates}" var="i">
			<c:choose>
				<c:when test="${selectedtourdate == i.id}">
					<option value="${i.id}" selected="selected"><c:out value="${i.date}"></c:out></option>			
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.date}"></c:out></option>			
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</select> |
		</div>
</c:if>
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
<select id="allItems" multiple="multiple" style="height:500px;max-width:275px;min-width:275px;margin-top:-50px;font-size: 16px;">
<c:forEach items="${allItems }" var="i">
<option value="${i.id}" ><c:out value="${i.committeeMember.member.findFirstLastName()}"></c:out></option>
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
<select id="selectedItems" multiple="multiple" style="height:500px;max-width:275px;min-width:275px;font-size: 16px;" class="round${round }">
<c:forEach items="${selectedItems}" var="i">
<option value="${i.id}" style="font-size: 16px;"><c:out value="${i.committeeMember.member.findFirstLastName()}"></c:out></option>
</c:forEach>
</select>
<br>
<input type="button" value="<spring:message code='generic.submit' text='Submit'/>" id="submit" style="width:100px;height:40px;margin: 5px;margin-left:100px; " class="submit">
</td>

</tr>
</table>

<table id="absentTable" style="display:none;">
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
<select id="selectedItems" multiple="multiple" style="height:500px;max-width:275px;min-width:275px;font-size: 16px;" class="round${round }">
<c:forEach items="${selectedItems}" var="i">
<option value="${i.id}" class="highlight" style="font-size: 16px;"><c:out value="${i.committeeMember.member.findFirstLastName()}"></c:out></option>
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
<option value="${i.id}"><c:out value="${i.committeeMember.member.findFirstLastName()}"></c:out></option>
</c:forEach>
</select>
<input id="selectItemFirstMessage" value="<spring:message code='attendance.selectitem' text='Select an item first'/>" type="hidden">
<input id="membersLocked" name="membersLocked" value="${membersLocked }" type="hidden">
</div>
</div>
<input type="hidden" id="locked" name="locked" value="${locked}">
<input type="hidden" id="round" name="round" value="${round}">
<input type="hidden" id="reorderingPattern" name="reorderingPattern" value="${reorderingPattern}">
<input type="hidden" id="currentattendance" name="currentattendance" value="${attendance}">
<input type="hidden" id="itemsFrompreviousRounds" name="itemsFrompreviousRounds" value="${oldMembers }">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>