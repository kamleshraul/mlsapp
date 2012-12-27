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
<c:choose>
<c:when test="${!(empty attendance) }">
<table>
<tr>
<td>
<select id="allItems" multiple="multiple" style="height:300px;width:300px;">
<c:forEach items="${attendance }" var="i">
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
<c:forEach items="${attendance }" var="i">
<c:if test="${i.attendance==true }">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:if>
</c:forEach>
</select>
</td>
<td>
<input id="up" type="button" value="&#x2191;" />
<br>
<input id="down" type="button" value="&#x2193;" />
</td>
</tr>
</table>
</c:when>
<c:otherwise>
<spring:message code="attendance.noeligible" text="No Eligible Members Found"></spring:message>
</c:otherwise>
</c:choose>
<select id="itemMaster" style="display:none;">
<c:forEach items="${attendance }" var="i">
<option value="${i.id}"><c:out value="${i.member.getFullname()}"></c:out></option>
</c:forEach>
</select>
</body>
</html>