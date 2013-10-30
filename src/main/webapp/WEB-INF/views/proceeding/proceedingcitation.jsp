<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="part.citation"	text=" Proceeding Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	//first split specific for jwysiwyg plugin
	var counter=$('#counter').val();
	
	$("#addCitations").click(function(){		
		var currentCitations = $('#citations').val();
		$('#mergedCitations').val(currentCitations);
		var content="";
		if(counter!=null && counter!=""){
			content=$('#content'+counter).wysiwyg("getContent");
			$('#content'+counter).wysiwyg("setContent",content+"<p></p>" + $('#mergedCitations').val());
		}else{
			content=$('#proceedingContent').wysiwyg("getContent");
			$("#proceedingContent").wysiwyg("setContent",content+"<p></p>" + $('#mergedCitations').val());
		}
		   $.fancybox.close();	    	
	});
	
	
	});
</script>
</head>
<body>
<c:choose>
	<c:when test="${!(empty citations) }">
		<p> 
			<label class="labelcentered"><spring:message code="proceedingcitation.text" text="Proceeding Citation"></spring:message></label>
			<select id="citations" name="citations" multiple="multiple" class="sSelectMultiple">
				<c:forEach items="${citations}" var="i" varStatus="cnt">
					<option id="citation" value="${i.text}">${i.text}</option>
				</c:forEach>
			</select>
		</p>	
		<p class="tright">
			<input id="addCitations" type="button" value="<spring:message code='citation.addCitations' text='Add 

Citations'/>" class="butDef">
		</p>	
	</c:when>
	<c:otherwise>
		<spring:message code="proceeding.no citations" text="No citations found"></spring:message>
	</c:otherwise>
</c:choose>	
<input type="hidden" id="mergedCitations" name="mergedCitations"/>
<input type="hidden" id="counter" name="counter" value="${counter}"/>
</body> 
</html>