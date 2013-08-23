<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			/**** On clicking a question on the chart ****/
			$(".deviceNumber").click(function(){
				if($('#currentDeviceType').val()=='questions_starred'){
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
				}else if($('#currentDeviceType').val()=='resolutions_nonofficial'){
					var parameters="houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&deviceType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()	
					+"&edit=true";
					var resourceURL='resolution/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
				}else if($('#currentDeviceType').val()=='questions_halfhourdiscussion_standalone'){
					var parameters="houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&deviceType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()	
					+"&edit=true";
					var resourceURL='question/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
				}
				
			});
			
			 /**** Right Click Menu ****/
			$(".deviceNumber").contextMenu({
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

			$(".deviceNumber a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});

			$(".rejectedCount a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});
			
	    	$(".scrollable").scrollLeft();			
		});			
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="scrollable">
<c:choose>
<c:when test="${chartVOs == null}">
	<spring:message code="question.chart.notCreated" text="Chart is not Created"/>
</c:when>

<c:when test="${empty chartVOs}">
	<spring:message code="question.chart.noEntries" text="There are no entries in the Chart"/>
</c:when>

<c:otherwise>
	<c:if test="${deviceType!='questions_halfhourdiscussion_standalone'}">
		<label class="small"><spring:message code="question.chart.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
	</c:if>

<table class="uiTable" border="1">
	<tr>
	<th><spring:message code="member.name" text="Member Name"/></th>
	<c:forEach begin="1" end="${maxQns}" var="i">
			<c:if test="${deviceType == 'questions_starred'}">
			<th><spring:message code="chart.question" text="Question ${i}"/></th>
			</c:if>
			<c:if test="${deviceType == 'resolutions_nonofficial' }">
				<th style="min-width: 100px;"><spring:message code="chart.resolution" text="Resolution ${i}"/></th>
			</c:if>
			<c:if test="${deviceType == 'questions_halfhourdiscussion_standalone' }">
				<th><spring:message code="chart.question.HDS" text="HDS ${i}"/></th>
			</c:if>
		</c:forEach>
	<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
	<th><spring:message code="chart.rejectedCount" text="Reject Count"/></th>
	<th><spring:message code="chart.extraCount" text="Extra Count"/></th>
	</c:if>
	</tr>
	<c:forEach items="${chartVOs}" var="chartVO">
	<tr>
		<td>${chartVO.memberName}</td>
		<c:set var="count" value="1"></c:set>		
		<c:forEach items="${chartVO.deviceVOs}" var="deviceVO">
			<td align="center">
				<c:choose>
				<c:when test="${ empty deviceVO}">
				-				
				</c:when>
				<c:when test="${deviceVO.hasParent == false}">
					<a href="#" class="deviceNumber" id="${deviceVO.id}" title="${deviceVO.kids}"><b>${deviceVO.number}</b></a>
				</c:when>
				<c:otherwise>
					<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
						<a href="#" class="deviceNumber" id="${deviceVO.id}" title="${deviceVO.localisedStatus}">${deviceVO.number}</a>
					</c:if>
					<c:if test="${deviceType == 'questions_starred'}">
						<a href="#" class="deviceNumber" id="${deviceVO.id}" title="${deviceVO.parent}">${deviceVO.number}</a>
					</c:if>
				</c:otherwise>
				</c:choose>
				
				<c:choose>
				<c:when test="${deviceVO.isFactualRecieved == true && deviceVO.status == 'resolution_system_putup'}">
					<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:when test="${deviceVO.status == 'question_system_putup'}">
					<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:when test="${deviceVO.status == 'resolution_system_putup'}">
					<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:when test="${deviceVO.status == 'question_system_clubbed'}">
					<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
				</c:when>
				<c:otherwise>
					<c:if test="${deviceVO.status!='resolution_final_rejection' }">
					<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
					</c:if>
				</c:otherwise>
				</c:choose>
			</td>
		<c:set var="count" value="${count+1 }"></c:set>
		</c:forEach>
		<c:if test="${count<=maxQns}">
			<c:forEach begin="${count }" var="i" end="${maxQns}">
				<td align="center">
				-
				</td>
			</c:forEach>
		</c:if>
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'questions_halfhourdiscussion_standalone'}">
			<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${chartVO.rejectedNotices}" style="text-decoration: none;">${chartVO.rejectedCount}</a></td>
			<td align="center">${chartVO.extraCount}</td>
		</c:if>
	</tr>
	</c:forEach>
</table>
</c:otherwise>
</c:choose>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>