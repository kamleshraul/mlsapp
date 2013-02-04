<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {					    
			/**** On clicking a question on the chart ****/
			$(".questionNumber").click(function(){
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&questionType="+$("#selectedQuestionType").val()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&usergroup="+$("#currentusergroup").val()
				+"&usergroupType="+$("#currentusergroupType").val()	
				+"&edit=true";
				var resourceURL='question/'+$(this).attr("id")+'/edit?'+parameters;
				showTabByIdAndUrl('details_tab', resourceURL);
			});	
			 /**** Right Click Menu ****/
			$(".questionNumber").contextMenu({
		        menu: 'contextMenuItems'
		    },
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='clubbing'){
					clubbingInt(id);		
				}else if(action=='referencing'){
					referencingInt(id);
				}
		    });	

			$(".questionNumber a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});			
		});			
	</script>
</head>

<body>
<div>
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
				<c:choose>
				<c:when test="${questionVO.hasParent == false}">
					<a href="#" class="questionNumber" id="${questionVO.id}" title="${questionVO.kids}"><b>${questionVO.number}</b></a>
				</c:when>
				<c:otherwise>
					<a href="#" class="questionNumber" id="${questionVO.id}" title="${questionVO.parent}">${questionVO.number}</a>
				</c:otherwise>
				</c:choose>
				
				<c:choose>
				<c:when test="${questionVO.status == 'question_system_putup'}">
					<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:when test="${questionVO.status == 'question_system_clubbed'}">
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
</c:otherwise>
</c:choose>
</div>
</body>
</html>