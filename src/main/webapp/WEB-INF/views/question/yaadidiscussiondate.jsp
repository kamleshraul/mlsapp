<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="question.yaadidiscussionDate"	text="Yaadi Discussion Date" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function(){	
		$('.datemask').focus(function(){		
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});
		
		$("#addDate").click(function(){
			$("#ydDiscussionDate").val($('#discussionDate').val());
			$("#ydRemark").val($('#remark').val());
		});
	});
</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark" >
	<p> 
		<label class="small"><spring:message code="yaadiDiscussionDate.discussionDate" text="Discussion Date"></spring:message></label>
		<input type="text" id="discussionDate" name="discussionDate" class="datemask sText"/>
 	</p>
 	<br>
 	<p> 
		<label class="labelcentered"><spring:message code="yaadiDiscussionDate.remark" text="Remark"></spring:message></label>
		<textarea rows="3" cols="30" id="remark" name="remark"></textarea>
 	</p>	
	<p class="tright">
		<input id="addDate" type="button" value="<spring:message code='yaadiDiscussionDate.addDate' text='Add Date'/>" class="butDef">
	</p>
	</div>
</body>
</html>