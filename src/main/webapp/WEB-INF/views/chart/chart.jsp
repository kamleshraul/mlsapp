<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			/**** Back to chart ****/
			$("#backToChart").click(function(){
				$("#chartResultDiv").show();
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				$("#backDiv").hide();		
			});		
			/**** On clicking a question on the chart ****/
			$(".questionNumber").click(function(){
				var parameters="houseType="+$("#selectedHouseType").val()
				+"&sessionYear="+$("#selectedSessionYear").val()
				+"&sessionType="+$("#selectedSessionType").val()
				+"&questionType="+$("#selectedQuestionType").val()
				+"&ugparam="+$("#ugparam").val()
				+"&status="+$("#selectedStatus").val()
				+"&role="+$("#srole").val()
				+"&edit=true";
				var resourceURL='question/'+$(this).attr("id")+'/edit?'+parameters;
				showTabByIdAndUrl('details_tab', resourceURL);
			});
			/**** On right clicking a question on the chart ****/
			$(".questionNumber").contextMenu({
		        menu: 'contextMenuItems'
		    },
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='view'){
					viewQuestionDetail(id);
				}else if(action=='clubbing'){
					clubbingInt(id);
				}else if(action=='referencing'){
					referencingInt(id);
				}
		    	});
			
		});	
		/**** Question Details ****/
		function viewQuestionDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#selectedQuestionType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			var resourceURL='question/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html');	
		}	
		/**** Clubbing ****/			
		function clubbingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			$.get('clubentity/init?id='+id,function(data){
				$.unblockUI();
				$("#clubbingResultDiv").empty();
				$("#clubbingResultDiv").html(data);
				$("#chartResultDiv").hide();
				$("#clubbingResultDiv").show();
				$("#referencingResultDiv").hide();
				$("#backDiv").show();					
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html');
		}
		/**** Referencing ****/
		function referencingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get('refentity/init?id='+id,function(data){
				$.unblockUI();			
				$("#referencingResultDiv").empty();
				$("#referencingResultDiv").html(data);
				$("#chartResultDiv").hide();
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").show();
				$("#backDiv").show();				
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html');
		}
	</script>
</head>

<body>
<div id="chartResultDiv">
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
					<a href="#" class="questionNumber" id="${questionVO.id}" ><b>${questionVO.number}</b></a>
				</c:when>
				<c:otherwise>
					<a href="#" class="questionNumber" id="${questionVO.id}" >${questionVO.number}</a>
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
<ul id="contextMenuItems" >
<li><a href="#clubbing" class="edit"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a></li>
<li><a href="#referencing" class="edit"><spring:message code="generic.referencing" text="Referencing"></spring:message></a></li>
<li><a href="#view" class="edit"><spring:message code="generic.view" text="View Details"></spring:message></a></li>

</ul>
</c:otherwise>
</c:choose>
</div>

<div id="backDiv">
<a href="#" id="backToChart"><spring:message code="chart.backtochart" text="Back To Chart"></spring:message></a>
</div>

<div id="clubbingResultDiv">
</div>

<div id="referencingresultDiv">
</div>

</body>
</html>