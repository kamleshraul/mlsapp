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
			console.log($("#allItems").html());			
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
<div class="fields clearfix watermark">
<form:form action="roster" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="roster.new.heading" text="Enter Roster Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<form:errors path="language" cssClass="validationError"/>	
	<form:errors path="session" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="roster.sessionType" text="Session Type"/>*</label>				
		<input type="hidden" id="session" name="session" value="${session}"/>
	</p>
	
	<c:if test="${committeeMeeting == null || committeeMeeting ==''}">	
	<p>
		<label class="small"><spring:message code="roster.registryNo" text="Registry No."/>*</label>
		<form:input  cssClass="integer sText" path="registerNo"/>
		<form:errors path="registerNo" cssClass="validationError"/>	
	</p>
	</c:if>
	
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
		<option value="0"><spring:message code="please.select" text="Please Select"></spring:message></option>
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
		</select>
		<form:errors path="slotDuration" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="roster.adjournment.action" text="Action"/>*</label>
		<select id="action" name="action" class="sSelect">
		<option value="save_without_creating_slots"><spring:message code="roster.save_without_creating_slots" text="Save Roster Without Creating Slots"></spring:message></option>
		<option value="create_slots"><spring:message code="roster.createSlots" text="Create Slots"></spring:message></option>
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
	</c:if>
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
	</c:if>
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
	</c:if>
	</c:forEach>
	</select>
		
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>		
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<c:if test="${committeeMeeting !=''}">
		<form:hidden path="committeeMeeting" value="${committeeMeeting}"/>
	</c:if>
	<form:hidden path="publish"/>
	<input type="hidden" name="language" id="language" value="${language }">
</form:form>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input type="hidden" name="defaultAction" id="defaultAction" value="${domain.action }">	
</div>
</body>
</html>