<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="motion.citation"	text="Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	//first split specific for jwysiwyg plugin
	var existingCitations="";
	if($("#remarks").wysiwyg("getContent")!=undefined){
		 existingCitations=$("#remarks").wysiwyg("getContent").split("<p></p>")[1].split(",");

	}else{
		 existingCitations=$("#remarks").val().split(",");
	}
	$('#Citations').val(existingCitations);
	
	$('#citations option').each(function(){
		for(var i=0; i<existingCitations.length; i++) {
			if($(this).attr('value')==existingCitations[i]){
				$(this).attr('selected', 'selected');
			}
		}
	});		
	
	$("#addCitations").click(function(){		
		var currentCitations = $('#Citations').val().split(",");
		var updatedCitations = new Array();
		var j = 0;
		for(var i=0; i<currentCitations.length; i++) {
			if(currentCitations[i] != "") {
				updatedCitations[j] = currentCitations[i];
				j++;
			}
		}
		$('#Citations').val(updatedCitations);
		$("#remarks").wysiwyg("setContent","<p></p>" + $('#Citations').val());
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
				var currentCitations = $('#Citations').val();	
				if(currentCitations == "") {
					$('#Citations').val(currentCitations + element.attr('value'));	
				}
				else {
					$('#Citations').val(currentCitations + "," + element.attr('value'));		
				}
			} 
			else {				
				var currentCitations = $('#Citations').val().split(",");					
				for(var i=0; i<currentCitations.length; i++) {					
					if(currentCitations[i] == element.attr('value')) {
						currentCitations[i] = '';
					}
				}				
				$('#Citations').val(currentCitations);
			}			
		}
	});
});
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
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
		<spring:message code="motion.no citations" text="No citations found"></spring:message>
	</c:otherwise>
</c:choose>	
<input type="hidden" id="Citations">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>