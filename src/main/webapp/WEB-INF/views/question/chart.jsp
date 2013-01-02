<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {			
			$(".questionNumber").click(function(){
				showTabByIdAndUrl('details_tab','question/'+$(this).attr("id")+'/edit');
			});
			
			$(".questionNumber").contextMenu({
		        menu: 'contextMenuItems'
		    },
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='view'){
					$.get('question/'+id+'/edit',function(data){
						$.fancybox.open(data,{autoSize:false,width:750,height:700});
					},'html');	
				}else if(action=='clubbing'){
					$.get('question/clubbing?id='+id,function(data){
						$.fancybox.open(data,{autoSize:false,width:750,height:700});
					},'html');
				}else if(action=='referencing'){
					$.get('question/referencing?id='+id,function(data){
						$.fancybox.open(data,{autoSize:false,width:750,height:700});
					},'html');
				}
		    	});
			
		});		
	</script>
</head>

<body>
<c:choose>
<c:when test="${chartVOs == null}">
	<spring:message code="question.chart.notCreated" text="Chart is not Created"/>
</c:when>

<c:when test="${empty chartVOs}">
	<spring:message code="question.chart.noEntries" text="There are no entries in the Chart"/>
</c:when>

<c:otherwise>
<label class="small"><spring:message code="question.chart.answeringDate" text="Answering Date"/>: ${answeringDate}</label>

<table border="1">
	<tr>
	<th><spring:message code="member.name" text="Member Name"/></th>
	<th><spring:message code="question1" text="Question 1"/></th>
	<th><spring:message code="question2" text="Question 2"/></th>
	<th><spring:message code="question3" text="Question 3"/></th>
	<th><spring:message code="question4" text="Question 4"/></th>
	<th><spring:message code="question5" text="Question 5"/></th>
	</tr>
	<c:forEach items="${chartVOs}" var="chartVO">
	<tr>
		<td>${chartVO.memberName}</td>
		<c:forEach items="${chartVO.questionVOs}" var="questionVO">
			<td align="center">
				<a href="#" class="questionNumber" id="${questionVO.id}" >${questionVO.number}</a>
				<c:choose>
				<c:when test="${questionVO.status == 'question_before_workflow_tobeputup'}">
					<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:when test="${questionVO.status == 'question_before_workflow_clubbed'}">
					<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:otherwise>
					<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:otherwise>
				</c:choose>
			</td>
		</c:forEach>
	</tr>
	</c:forEach>
</table>
<ul id="contextMenuItems" >
<li><a href="#clubbing" class="edit"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a></li>
<li><a href="#referencing" class="edit"><spring:message code="generic.referencing" text="Referencing"></spring:message></a></li>
</ul>
</c:otherwise>
</c:choose>
<div id="clubbingPage">
</div>
<div id="referencingPage">
</div>
</body>
</html>