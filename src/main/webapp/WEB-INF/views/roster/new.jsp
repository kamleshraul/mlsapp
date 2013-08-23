<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster" text="Roster"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">				
		$(document).ready(function() {		
		/**** Selected To 2 ****/
		$("#to2").click(function(){
			var count=0;			
			$("#allItems option:selected").each(function(){
				$("#selectedItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
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
		/**** Down ****/		
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
		$("#generateslot").click(function(){
			$("#operation").val("GENERATE_SLOT");
			submitForm();
			return false;
		});

			
	});
	function customLogicBeforeSubmission(){
		$("#selectedItems option").each(function(){
			$(this).attr("selected","selected");
		});		
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">
<form:form action="roster" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.new.heading" text="Enter Roster Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="roster.sessionType" text="Session Type"/>*</label>				
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="small"><spring:message code="roster.registryNo" text="Registry No."/>*</label>
		<form:input  cssClass="integer sText" path="registerNo"/>
		<form:errors path="registerNo" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.starttime" text="Start Time"/>*</label>
		<input type="text" class="sText datetimemask" name="selectedStartTime" id="selectedStartTime" value="${startTime }">
		<form:errors path="startTime" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.endtime" text="End Time"/>*</label>
		<input type="text" class="sText datetimemask" name="selectedEndTime" id="selectedEndTime" value="${endTime }">
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
		<option value="${i }" >${i }</option>		
		</c:otherwise>		
		</c:choose>
		</c:forEach>		
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
		</select>
		<form:errors path="slotDuration" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.action" text="Action"/>*</label>
		<select id="action" name="action" class="sSelect">
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
		<c:choose>
		<c:when test="${domain.action=='turnoff'}">
		<option value="turnoff" selected="selected"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="turnoff"><spring:message code="roster.adjournment.turnofflots" text="Turn Off Slots"></spring:message></option>
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${domain.action=='shift'}">
		<option value="shift" selected="selected"><spring:message code="roster.adjournment.shiftslots" text="Shift Slots"></spring:message></option>
		</c:when>
		<c:otherwise>
		<option value="shift"><spring:message code="roster.adjournment.shiftslots" text="Shift Slots"></spring:message></option>
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
	<option value="${i.id}"><c:out value="${i.findFullName()}"></c:out></option>
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
	<select id="selectedItems" name="selectedItems" multiple="multiple" style="height:300px;width:350px;">
	<c:forEach items="${selectedItems}" var="i">
	<option value="${i.id}"><c:out value="${i.findFullName()}"></c:out></option>
	</c:forEach>
	</select>
	<br>
	</td>
	<td>
	<input id="up" type="button" value="&#x2191;" class="up"/>
	<br>
	<input id="down" type="button" value="&#x2193;" class="down"/>
	</td>
	</tr>
	</tbody>	
	</table>	
	
	<select id="itemMaster" style="display:none;">
	<c:forEach items="${eligibles}" var="i">
	<option value="${i.id}"><c:out value="${i.findFullName()}"></c:out></option>
	</c:forEach>
	</select>
		
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
			<input id="generateslot" type="button" value="<spring:message code='roster.generateslot' text='Generate Slot'/>" class="butDef" style="width: auto;">
		</p>
	</div>		
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<input type="hidden" name="language" id="language" value="${language }">
	<input type="hidden" name="operation" id="operation">	
</form:form>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
</div>
</body>
</html>