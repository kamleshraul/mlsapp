<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster" text="Roster"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css">
	.blue{
	color:blue;
	}
	.green{
	color:green;
	}
	.red{
	color:red;
	}
	</style>
	<script type="text/javascript">				
		$(document).ready(function() {		
			/**** Selected To 2 ****/
			$("#to2").click(function(){
				var count=0;			
				$("#allItems option:selected").each(function(){
					$("#selectedItems").append("<option value='"+$(this).val()+"' class='"+$(this).attr("class")+"'>"+$(this).text()+"</option>");
					$("#allItems option[value='"+$(this).val()+"']").remove();
					count++;
				});
				$("#selectedCount").text(parseInt($("#selectedCount").text())+count);
				$("#availableCount").text(parseInt($("#availableCount").text())-count);	
			});	
			/**** Selected To 1 ****/		
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
				$("#selectedCount").text(parseInt($("#selectedCount").text())-count);
				$("#availableCount").text(parseInt($("#availableCount").text())+count);
			});	
			/**** All From 1 To 2 ****/
			$("#allTo2").click(function(){
				$("#selectedItems").append($("#allItems").html());
				$("#allItems").empty();
				$("#selectedCount").text($("#selectedItems option").length);
				$("#availableCount").text($("#allItems option").length);			
			});
			/**** All From 2 To 1 ****/
			$("#allTo1").click(function(){
				var count=0;			
				$("#allItems").empty();
				$("#allItems").html($("#itemMaster").html());	
				$("#selectedItems").empty();
				$("#selectedCount").text($("#selectedItems option").length);
				$("#availableCount").text($("#allItems option").length);
			});	
			/**** Up ****/
			$(".up").click(function(){
				var stopSwapping="no";			
				$("#selectedItems option:selected").each(function(){
					if(stopSwapping=="no"){	
						var current=$(this);
						var index=parseInt(current.index());								
						if(index!=0){
							var prev=$("#selectedItems option:eq("+(index-1)+")");
							var prevVal=prev.val();
							var prevText=prev.text();
							prev.val(current.val());
							prev.text(current.text());
							current.val(prevVal);
							current.text(prevText);	
							prev.attr("selected","selected");
							current.removeAttr("selected");
						}else{
							stopSwapping="yes";
						}	
					}	
				});		
			});
			/**** Down ****/		
			$(".down").click(function(){
				var stopSwapping="no";			
				var length=$("#selectedItems option").length;
				var selectedItems=new Array();	
				$("#selectedItems option:selected").each(function(){
					selectedItems.push($(this));
				});
				for(var i=selectedItems.length-1;i>=0;i--){	
					if(stopSwapping=="no"){	
						var current=selectedItems[i];
						var index=parseInt(current.index());						
						if(index!=length-1){
							var next=$("#selectedItems option:eq("+(index+1)+")");
							var nextVal=next.val();
							var nextText=next.text();
							next.val(current.val());
							next.text(current.text());
							current.val(nextVal);
							current.text(nextText);	
							next.attr("selected","selected");
							current.removeAttr("selected");					
						}else{
							stopSwapping="yes";
						}	
					}
				}
			});			
			/**** Default Action ****/
			$("#action").val($("#defaultAction").val());
			if($("#selectedSlotDurationChangedFrom").val()==""&&$(".slotTimeChanged").html()==null){
				$("#slotDurationChangedFromDiv").hide();				
			}
			/**** Defining Actions To Be Shown ****/
			$("#selectedStartTime").change(function(){
				var action=$("#action").val();
				if(action!='save_without_creating_slots'&&action!='create_slots'){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
					var startTime=$(this).val();
					var endTime=$("#selectedEndTime").val();
					var slotDuration=parseInt($("#slotDuration").val());	
					var roster=$("#id").val();							
					if(startTime!=""&&endTime!=""&&slotDuration!=0&&roster!=""){
						$.post('ref/roster/actions',{'startTime':startTime,'endTime':endTime,'slotDuration':slotDuration,'roster':roster},function(data){
							$("#action").empty();
							var text="";
							for(var i=0;i<data.length;i++){
								text+="<option value='"+data[i].id+"'>"+$("#"+data[i].id).val()+"</option>";
							}
							$("#action").html(text);
							$.unblockUI();
						});
					}else{
						$.unblockUI();	 				
					}
				}
			});
			$("#selectedEndTime").change(function(){
				var action=$("#action").val();
				if(action!='save_without_creating_slots'&&action!='create_slots'){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var endTime=$(this).val();
					var startTime=$("#selectedStartTime").val();
					var slotDuration=parseInt($("#slotDuration").val());
					var roster=$("#id").val();								
					if(startTime!=""&&endTime!=""&&slotDuration!=0&&roster!=""){
						$.post('ref/roster/actions',{'startTime':startTime,'endTime':endTime,'slotDuration':slotDuration,'roster':roster},function(data){
							$("#action").empty();
							var text="";						
							for(var i=0;i<data.length;i++){
								text+="<option value='"+data[i].id+"'>"+$("#"+data[i].id).val()+"</option>";
							}
							$("#action").html(text);
							$.unblockUI();						
						});
					}else{
						$.unblockUI();						
					}
				}
			});	
			$("#slotDuration").change(function(){
				var action=$("#action").val();
				if(action!='save_without_creating_slots'&&action!='create_slots'){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var slotDuration=parseInt($("#slotDuration").val());	
					var startTime=$("#selectedStartTime").val();								
					var endTime=$("#selectedEndTime").val();
					var roster=$("#id").val();
					if(startTime!=""&&endTime!=""&&slotDuration!=0&&roster!=""){
						$.post('ref/roster/actions',{'startTime':startTime,'endTime':endTime,'slotDuration':slotDuration,'roster':roster},function(data){
							$("#action").empty();
							var text="";						
							for(var i=0;i<data.length;i++){
								text+="<option value='"+data[i].id+"'>"+$("#"+data[i].id).val()+"</option>";
							}
							$("#action").html(text);
							if(slotDuration!=parseInt($("#defaultSlotDuration").val())){
								var now = new Date();
								var date=AddZero(now.getDate());
								var month=AddZero(now.getMonth() + 1);
								var year=now.getFullYear();
								var hours=AddZero(now.getHours());
								var minutes=AddZero(now.getMinutes());
								var strDateTime =date+"/"+
												 month+"/"+
												 year+" "+
												 hours+":"+
												 minutes;
								$("#selectedSlotDurationChangedFrom").val(strDateTime);							
								$("#slotDurationChangedFromDiv").show();
							}else{
								$("#slotDurationChangedFromDiv").hide();
							}
							$.unblockUI();							
						});
					}else{
						$("#slotDurationChangedFromDiv").hide();
						$.unblockUI();		
					}
				}
			});					
	});
	function customLogicBeforeSubmission(){
		$("#selectedItems option").each(function(){
			$(this).attr("selected","selected");
		});		
	}
	function AddZero(num) {
	    return (num >= 0 && num < 10) ? "0" + num : num + "";
	}
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
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="roster" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.edit.heading" text="Roster Id:"/>${domain.id}		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<form:errors path="language" cssClass="validationError"/>	
	<form:errors path="session" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="roster.sessionType" text="Session Type"/>*</label>				
		<input type="hidden" id="session" name="session" value="${session}"/>
	</p>	
	
	<p>
		<label class="small"><spring:message code="roster.registryNo" text="Registry No."/>*</label>
		<form:input  cssClass="integer sText" path="registerNo" readonly="true"/>
		<form:errors path="registerNo" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.day" text="Day"/>*</label>
		<form:input  cssClass="integer sText" path="day" readonly="true"/>
		<form:errors path="day" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.starttime" text="Start Time"/>*</label>
		<input type="text" class="sText datetimenosecondmask" name="selectedStartTime" id="selectedStartTime" value="${startTime }">
		<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.endtime" text="End Time"/>*</label>
		<input type="text" class="sText datetimenosecondmask" name="selectedEndTime" id="selectedEndTime" value="${endTime }">
		<form:errors path="endTime" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="small"><spring:message code="roster.slotduration" text="Slot Duration"/>*</label>
		<select id="slotDuration" name="slotDuration" class="sSelect">
		<c:forEach var="i" begin="${start }" end="${end }" step="${step }">
		<c:choose>
		<c:when test="${i==slotDuration }">
		<option value="${i }" selected="selected">${i }</option>
		</c:when>
		<c:otherwise>
		<option value="${i }">${i }</option>		
		</c:otherwise>		
		</c:choose>
		</c:forEach>
		<c:choose>
		<c:when test="${empty slotDuration }">
		<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="0"><spring:message code="please.select" text="Please Select"></spring:message></option>
		</c:otherwise>
		</c:choose>		
		</select>
		<form:errors path="slotDuration" cssClass="validationError"/>	
	</p>
	
	<p id="slotDurationChangedFromDiv">
		<label class="small"><spring:message code="roster.slotdurationchangedfrom" text="Slot Duration Changed From"/>*</label>
		<input type="text" class="sText datetimenosecondmask" name=selectedSlotDurationChangedFrom id=selectedSlotDurationChangedFrom value="${slotDurationChangedFrom }">
		<form:errors path="slotDurationChangedFrom" cssClass="validationError slotTimeChanged"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.action" text="Action"/>*</label>
		<select id="action" name="action" class="sSelect">
		<c:choose>
		<c:when test="${slots_created=='yes' }">
		<option value="recreate_slots"><spring:message code="roster.recreateSlots" text="Re-Create Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="save_without_creating_slots"><spring:message code="roster.save_without_creating_slots" text="Save Roster Without Creating Slots"></spring:message></option>
		<option value="create_slots"><spring:message code="roster.createSlots" text="Create Slots"></spring:message></option>
		</c:otherwise>
		</c:choose>
		</select>				
		<form:errors path="action" cssClass="validationError"/>	
	</p>
	
	<table id="reporterTable">	
	<thead>
	<tr>
	<th>
	<spring:message code="ris.eligiblesList" text="List of Available Reporters"></spring:message>
	(<span id="availableCount">${allItemsCount}</span>)
	</th>
	<th>
	</th>
	<th>
	<span id="selectedList" >
	<spring:message code="ris.selectedList" text="List of Selected Reporters"></spring:message>
	(<span id="selectedCount">${selectedItemsCount}</span>)
	</span>
	</th>
	<th>
	</th>
	</tr>
	</thead>	
	<tbody>
	<tr>
	<td>
	<select id="allItems" multiple="multiple" style="height:300px;width:350px;">
	<c:forEach items="${allItems }" var="i">
	<c:if test="${!(empty i.houseType) }">
	<c:choose>
	<c:when test="${i.houseType.type=='lowerhouse'}">
	<option value="${i.id}" class="green"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:when test="${i.houseType.type=='upperhouse'}">
	<option value="${i.id}" class="red"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:otherwise>
	<option value="${i.id}" class="blue"><c:out value="${i.findFullName()}"></c:out></option>
	</c:otherwise>
	</c:choose>
	</c:if>	</c:forEach>
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
	<select id="selectedItems" name="selectedItems" multiple="multiple" style="height:300px;width:350px;">
	<c:forEach items="${selectedItems}" var="i">
	<c:if test="${!(empty i.houseType) }">
	<c:choose>
	<c:when test="${i.houseType.type=='lowerhouse'}">
	<option value="${i.id}" class="green"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:when test="${i.houseType.type=='upperhouse'}">
	<option value="${i.id}" class="red"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:otherwise>
	<option value="${i.id}" class="blue"><c:out value="${i.findFullName()}"></c:out></option>
	</c:otherwise>
	</c:choose>
	</c:if>	</c:forEach>
	</select>
	<br>
	</td>	
	<c:choose>
	<c:when test="${slots_created!='yes' }">
	<td>
	<input id="up" type="button" value="&#x2191;" class="up"/>
	<br>
	<input id="down" type="button" value="&#x2193;" class="down"/>
	</td>
	</c:when>
	</c:choose>	
	</tr>
	</tbody>	
	</table>	
	
	<select id="itemMaster" style="display:none;">
	<c:forEach items="${eligibles}" var="i">
	<c:if test="${!(empty i.houseType) }">
	<c:choose>
	<c:when test="${i.houseType.type=='lowerhouse'}">
	<option value="${i.id}" class="green"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:when test="${i.houseType.type=='upperhouse'}">
	<option value="${i.id}" class="red"><c:out value="${i.findFullName()}"></c:out></option>
	</c:when>
	<c:otherwise>
	<option value="${i.id}" class="blue"><c:out value="${i.findFullName()}"></c:out></option>
	</c:otherwise>
	</c:choose>
	</c:if>	</c:forEach>
	</select>
	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>		
	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<input type="hidden" name="language" id="language" value="${language }">
	<input type="hidden" name="slots_created" id="slots_created" value="${slots_created }">		
</form:form>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input id="recreate_slots" value="<spring:message code='ris.recreate_slots' text='Recreate Slots'/>" type="hidden">
<input id="turnoff_slots" value="<spring:message code='ris.turnoff_slots' text='Turn Off Slots'/>" type="hidden">
<input id="delete_slots" value="<spring:message code='ris.delete_slots' text='Delete Slots'/>" type="hidden">
<input id="create_new_slots" value="<spring:message code='ris.create_new_slots' text='Create New Slots'/>" type="hidden">
<input id="recreate_slots_from_slot_duration_changed_time" value="<spring:message code='ris.recreate_slots_from_slot_duration_changed_time' text='Recreate Slots From Slot Duration Changed Time'/>" type="hidden">

<input type="hidden" name="defaultAction" id="defaultAction" value="${domain.action }">
<input type="hidden" name="defaultStartTime" id="defaultStartTime" value="${defaultStartTime }">
<input type="hidden" name="defaultEndTime" id="defaultEndTime" value="${defaultEndTime }">
<input type="hidden" name="defaultSlotDuration" id="defaultSlotDuration" value="${slotDuration }">	
<input type="hidden" name="pleaseSelectMsg" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'></spring:message>">	
</div>
</body>
</html>