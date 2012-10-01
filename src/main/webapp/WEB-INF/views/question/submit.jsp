<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	 <!-- <link rel="stylesheet" type="text/css" media="screen" href="./resources/css/common.css?v=1" /> -->
	<script type="text/javascript">
	$(document).ready(function(){
		$('.multipleSelect').multiselect({
			dividerLocation : 0.5
		});
		
		$('#submit').click(function(){						
			$.ajax({
				type		: "POST",
				cache	: false,				
				url		: "question/"+$('#questions').val()+"/submit",				
				success: function(data) {					
					parent.$.fancybox.close();
					showTabByIdAndUrl('list_tab', data);
				}
			});
		});
		$('#cancel').click(function(){
			parent.$.fancybox.close();
			showTabByIdAndUrl('list_tab', data);
		});
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="question/submit" method="POST">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="question.submit.heading" text="Submit Questions"/></h2>		
	
	<select id="questions" name="questions" class="multipleSelect" multiple="multiple">
	<c:forEach items="${questions}" var="i" >
		<option value="${i.id}"><c:out value="${i.subject}"></c:out></option>
	</c:forEach>
	</select>	
		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	
		
</form:form>
</div>
</body>
</html>