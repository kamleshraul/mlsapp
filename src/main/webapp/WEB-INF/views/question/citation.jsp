<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.citation"
	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){		
	var orderedContent = $("#remarks").wysiwyg("getContent");
	var content = orderedContent.split(",");
	$('#citations option').each(function(){
		for(var i=0; i<content.length; i++) {
			if($(this).attr('value')==content[i]){
				$(this).attr('selected', 'selected');
			}
		}
	});		
	
	$("#addCitations").click(function(){
		//alert("added " + $("#citations").val());	
		
		$("#remarks").wysiwyg("setContent",orderedContent);
	    $.fancybox.close();	    	
	});
	
	$('#citations').sexyselect({	
		width: 'auto',
		showTitle: false,			
		allowFilter: true,			
		allowDelete: false,
		selectionMode: 'multiple',	
		defaultCheckAllText: $('#defaultCheckAllText').val(),
		defaultUnCheckAllText: $('#defaultUnCheckAllText').val(),
		onItemSelected:function(element,options){
			if(element.is(':checked')){				
				if(orderedContent.contains(element.attr('value'))) {
					orderedContent.append(","+element.attr('value'));
				}				
			} 
			else {
				orderedContent.replace(','+element.attr('value'),'');
			}
			alert(orderedContent);
		}
	});
});
</script>
</head>
<body>
<c:choose>
	<c:when test="${!(empty citations) }">
		<p> 
			<label class="small"><spring:message code="citation.text" text="Citation"></spring:message></label>
			<select id="citations" name="citations" multiple="multiple">
				<c:forEach items="${citations}" var="i" varStatus="cnt">
					<option id="citation${cnt}" value="${i.text}">${i.text}</option>
				</c:forEach>
			</select>
		</p>	
		<p class="tright">
			<input id="addCitations" type="button" value="<spring:message code='citation.addCitations' text='Add Citations'/>" class="butDef">
		</p>	
	</c:when>
	<c:otherwise>
		<spring:message code="question.no citations" text="No citations found"></spring:message>
	</c:otherwise>
</c:choose>	

