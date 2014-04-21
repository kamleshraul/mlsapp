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
<c:when test="${report == null}">
	<spring:message code="question.chart.notCreated" text="Chart is not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.chart.noEntries" text="There are no entries in the Chart"/>
</c:when>

<c:otherwise>
	<c:if test="${deviceType!='questions_halfhourdiscussion_standalone'}">
		<label class="small"><spring:message code="question.chart.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
	</c:if>
<table class="uiTable" border="1">
	<thead>
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
	</thead>
	<tbody>
		<c:set var="memberName" value=""></c:set>
		<c:set var="counter" value="1"></c:set>
		<c:set var="rejectedNotices" value="" />
		<c:set var="rejectedCount" value="" />
		<c:set var="extraCount" value="" />
		<c:forEach items="${report}" var="r">
			<c:choose>
				<c:when test="${memberName != r[1]}">
					<c:if test="${memberName!=''}">
						<c:if test="${counter <= maxQns}">
							<c:forEach begin="${counter}" end="${maxQns}" step="1">
								<td align="center">-</td>
							</c:forEach>
						</c:if>
						
						<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'questions_halfhourdiscussion_standalone'}">
							<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${rejectedNotices}" style="text-decoration: none;">${rejectedCount}</a></td>
							<td align="center">${extraCount}</td>
						</c:if>
						</tr>
						<c:set var="counter" value="1" />
					</c:if>
					
					<tr>
						<td>${r[1]}</td>
						<td align="center">
							<c:choose>		
								<c:when test="${r[3] == null or r[3]==0}">
									-
								</c:when>
								<c:otherwise>
									<c:choose>										
										<c:when test="${r[7] == 'n'}">												
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[10]}"><b>${r[4]}</b></a>
										</c:when>
										<c:otherwise>
											<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
												<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">${r[4]}</a>
											</c:if>
											<c:if test="${deviceType == 'questions_starred'}">
												<a href="#" class="deviceNumber" id="${r[3]}" title="${r[9]}">${r[4]}</a>
											</c:if>
										</c:otherwise>
									</c:choose>
									
									<c:choose>
										<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'question_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'question_system_clubbed'}">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:otherwise>
											<c:if test="${r[5]!='resolution_final_rejection' and r[5]!='0'}">
											<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
											</c:if>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</td>
						<c:set var="counter" value="${counter + 1}"/>
						<c:set var="rejectedNotices" value="${r[12]}" />
						<c:set var="rejectedCount" value="${r[13]}" />
						<c:set var="extraCount" value="${r[14]}" />
						<%-- <td>
							${counter}:${rejectedNotices}:${rejectedCount}:${extraCount}
						</td> --%>
				</c:when>
				<c:otherwise>
					<td align="center">
						<c:choose>
							<c:when test="${r[3] == null}" >
								-
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[7] == 'n'}">
										<a href="#" class="deviceNumber" id="${r[3]}" title="${r[10]}"><b>${r[4]}</b></a>
									</c:when>
									<c:otherwise>
										<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">${r[4]}</a>
										</c:if>
										<c:if test="${deviceType == 'questions_starred'}">
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[9]}">${r[4]}</a>
										</c:if>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'question_system_putup'}">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'resolution_system_putup' }">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'question_system_clubbed'}">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:otherwise>
										<c:if test="${r[5]!='resolution_final_rejection' and r[5]!='0'}">
										<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:if>
									</c:otherwise>
								</c:choose> 
							</c:otherwise>
						</c:choose>
					</td>
					<c:set var="counter" value="${counter + 1}"/>
					<c:set var="rejectedNotices" value="${r[12]}" />
					<c:set var="rejectedCount" value="${r[13]}" />
					<c:set var="extraCount" value="${r[14]}" />
					<%-- <td>
						${counter}:${rejectedNotices}:${rejectedCount}:${extraCount}
					</td> --%>
				</c:otherwise>
			</c:choose>
			<c:set var="memberName" value="${r[1]}" />
		</c:forEach>
				
		<c:if test="${counter < maxQns}">
			<c:forEach begin="${counter}" end="${maxQns}" step="1">
				<td align="center">-</td>
			</c:forEach>
		</c:if>
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'questions_halfhourdiscussion_standalone'}">
			<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${rejectedNotices}" style="text-decoration: none;">${rejectedCount}</a></td>
			<td align="center">${extraCount}</td>
		</c:if>
		</tr>
	</tbody>
</table>
</c:otherwise>
</c:choose>
</div>
<c:if test="${deviceType=='questions_starred'}">
	<div style="position: fixed; z-index: 999; background: scroll; right: 45px; bottom: 50px;">
		<div style="color: #FFF; border: 1px solid black; background: #F00; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;">${report[2][17]}</div>
		<div style="color: #000; border: 1px solid black; background: #0F0; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;">${report[2][15]}</div>
		<div style="color: #FFF; border: 1px solid black; background: #00F; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;;">${report[2][16]}</div>
	</div>
</c:if>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>