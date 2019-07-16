<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$('.viewQuestion').click(function() {
		var qid = $(this).attr('id').split("_")[1];
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+$("#selectedQuestionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='question/'+qid+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	});
</script>
<c:choose>
	<c:when test="${empty memberwiseQuestions}">
		<h3><spring:message code="qis.memberwisequestions.noentriesfound" text="No Questions Submitted Yet."/></h3>
	</c:when>
	<c:otherwise>
		<div id="reportDiv" >
			<h2 style="margin-left: 25px;text-align: center;">${memberwiseQuestions[0][0]}</h2>
			<table class="strippedTable" border="1" style="margin-top: 20px; margin-left: 25px; font-size: 15px;">
				<thead>
					<tr>
						<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.session" text="Session"/></th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.number" text="Question Number"/></th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.questiontext" text="Question Text"/></th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.answer" text="Answer"/></th>
						<c:if test="${memberwiseQuestions[0][8]=='questions_starred' or memberwiseQuestions[0][8]=='questions_unstarred'}">
							<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.yaadiDetails" text="Yaadi Details"/></th>
						</c:if>
						<th style="text-align: center;font-size: 15px;vertical-align: top;"><spring:message code="qis.memberwisequestions.parentDetails" text="Parent Details"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${memberwiseQuestions}" var="question" varStatus="rowNumber">
						<tr>
							<td style="padding-left: 15px; vertical-align: top;">
								<%-- ${formatter.formatNumberNoGrouping(rowNumber.count, locale)}. --%>
								${question[1]}
							</td>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">								
								<a href="#" id="viewQuestion_${question[7]}" class="viewQuestion" style="">${question[2]}</a>
							</td>
							<td style="padding-left: 15px;vertical-align: top;text-align: justify;">
								${question[3]}
							</td>
							<td style="padding-left: 15px;vertical-align: top;text-align: justify;">
								${question[4]}
							</td>
							<%-- <c:if test="${question[12]=='upperhouse' and question[7]=='questions_halfhourdiscussion_from_question'}">
								<td style="padding-left: 15px;vertical-align: top;">
									${question[11]}
								</td>
							</c:if> --%>
							<%-- <td style="padding-left: 15px; font-weight: bold; text-align: center;vertical-align: top;">
								${question[15]} <br>
								<c:choose>
									<c:when test="${question[7]=='questions_starred' and question[8]=='question_final_admission' and (empty question[5])}">
										${question[3]}<br/><br/><spring:message code="generic.date" text="Date"/> ${question[4]}
									</c:when>
									<c:when test="${question[7]=='questions_starred' and question[8]=='question_final_admission' and (not empty question[5])}">
										${question[3]}<br/><br/><spring:message code="generic.date" text="Date"/> ${question[6]}
									</c:when>
									<c:otherwise>${question[3]}</c:otherwise>									
								</c:choose>							
							</td> --%>							
							<c:if test="${question[8]=='questions_starred' or question[8]=='questions_unstarred'}">
								<td style="padding-left: 15px; vertical-align: top;">
									${question[5]}
								</td>
							</c:if>
							<td style="padding-left: 15px; font-weight: bold;vertical-align: top;text-align: center;">
								<a href="#" id="viewQuestion_${question[9]}" class="viewQuestion" style="">${question[6]}</a>
							</td>		
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>