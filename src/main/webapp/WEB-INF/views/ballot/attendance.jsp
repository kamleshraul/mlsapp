<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">				
		$(document).ready(function() {		
		$("#to2").click(function(){
			$("#allItems option:selected").each(function(){
				$("#selectedItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
				$("#allItems option[value='"+$(this).val()+"']").remove();
			});			
		});	
		$("#to1").click(function(){
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());	
			$("#selectedItems option").each(function(){
				if($(this).attr("selected")!='selected'){
					$("#allItems option[value='"+$(this).val()+"']").remove();
				}else{
					$("#selectedItems option[value='"+$(this).val()+"']").remove();					
				}			
			});			
		});	
		$("#allTo2").click(function(){
			$("#selectedItems").empty();
			$("#selectedItems").html($("#allItems").html());
			$("#allItems").empty();
		});
		$("#allTo1").click(function(){
			$("#allItems").empty();
			$("#allItems").html($("#itemMaster").html());
			$("#selectedItems").empty();
		});
		$("#up").click(function(){
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
			if(index!=0){
				var prev=$("#selectedItems option:eq("+(index-1)+")");
				var prevVal=prev.val();
				var prevText=prev.text();
				prev.val(current.val());
				prev.text(current.text());
				current.val(prevVal);
				current.text(prevText);				
			}
		});
		$("#down").click(function(){
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
			var length=$("#selectedItems option").length;
			if(index!=length-1){
				var next=$("#selectedItems option:eq("+(index+1)+")");
				var nextVal=next.val();
				var nextText=next.text();
				next.val(current.val());
				next.text(current.text());
				current.val(nextVal);
				current.text(nextText);				
			}
		});
		$("#submit").click(function(){
			var items=new Array();
			$("#selectedItems option").each(function(){
				items.push($(this).val());
			});
			if(items.length!=0){
			$.ajax({
				url:'question/attendance?items='+items+'&attendance='+$("#attendance").val()+'&session='+$("#session").val()+"&questionType="+$("#questionType").val(),
				type:'PUT',
				success:function(data){
				 if(data='success'){
					 $("#successDiv").show();
				 }
				}
			});
			}else{
				$.prompt($('#selectItemFirstMessage').val());				
			}
		});
		$("#presentees").click(function(){			
			markAtt("presentees");			
		});
		$("#absentees").click(function(){			
			markAtt("absentees");			
		});
		$("#presenteesList").show();
		$("#absenteesList").hide();
		});
		function markAtt(operation){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = $("#gridURLParams").val();
			if(parameters==undefined){
				parameters="houseType="+$("#houseType").val()+"&sessionYear="+$("#sessionYear").val()+"&sessionType="+$("#sessionType").val()+"&questionType="+$("#questionType").val()+"&operation="+operation;
			}
			var resourceURL = 'question/attendance?' + parameters;
			$.get(resourceURL,function(data){			
			$('.tabContent').html(data);
			if(operation=='presentees'){
			$("#attendance").val("true");
			$("#presenteesList").show();
			$("#absenteesList").hide();
			}else{
				$("#attendance").val("false");	
				$("#presenteesList").hide();
				$("#absenteesList").show();	
			}
			$.unblockUI();				
			},'html');			
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
	</style>
</head>

<body>
<div class="commandbarContent" style="margin: 20px;" id="selectionDiv3">		
			<a href="#" id="presentees" class="butSim">
				<spring:message code="question.presentees" text="Presentees"/>
			</a> |	
			<a href="#" id="absentees" class="butSim">
				<spring:message code="question.absentees" text="Absentees"/>
			</a> 			
</div>
<div class="toolTip tpGreen clearfix" id="successDiv" style="display:none;height:30px;">
		<p style="font-size: 12px;">
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="update_success" text="Data saved successfully."/>
		</p>
		<p></p>
</div>
<table>
<tr>
<th>
<spring:message code="attendance.eligiblesList" text="List of Members"></spring:message>
</th>
<th>
</th>
<th>
<span id="presenteesList" style="display:none;">
<spring:message code="attendance.presenteesList" text="List of Present Members"></spring:message>
</span>
<span id="absenteesList" style="display:none;">
<spring:message code="attendance.absenteesList" text="List of Absent Members"></spring:message>
</span>
</th>
</tr>
<tr>
<td>
<select id="allItems" multiple="multiple" style="height:300px;width:300px;margin-top:-50px;">
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
<select id="selectedItems" multiple="multiple" style="height:300px;width:300px;">
<c:forEach items="${selectedItems}" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
<br>
<input type="button" value="<spring:message code='generic.submit' text='Submit'/>" id="submit" style="width:100px;height:40px;margin: 5px;margin-left:100px; ">
</td>
<td>
<input id="up" type="button" value="&#x2191;" />
<br>
<input id="down" type="button" value="&#x2193;" />
</td>
</tr>
</table>
<select id="itemMaster" style="display:none;">
<c:forEach items="${eligibles }" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
<input id="selectItemFirstMessage" value="<spring:message code='attendance.selectitem' text='Select an item first'/>" type="hidden">
<input id="session" value="${session}" type="hidden">
<input id="questionType" value="${questionType}" type="hidden">
<input id="sessionType" value="${sessionType}" type="hidden">
<input id="houseType" value="${houseType}" type="hidden">
<input id="sessionYear" value="${sessionYear}" type="hidden">
<input id="attendance" value="true" type="hidden">
</body>
</html>