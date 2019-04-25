<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="question.ordering_for_submission" text="Ordering for Submission" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{max-width:450px;min-height:30px;}
		th{max-width:450px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#submissionOrder').click(function() {
			//todo
		});
		$("#orderSubmission").live("click", function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var items=new Array();
			for(var i=1; i<=$('#questionsCount').val(); i++) {
				var questionId = $('#questionId'+i).val();
				var submissionOrder = $('#submissionOrder'+i).val();
				items.push(questionId+"_"+submissionOrder);
			}
        	$.post('question/determine_ordering_for_submission?items='+items
        			+"&houseType=" + $('#houseType').val()
        			+"&questionType=" + $('#deviceType').val()
        			+"&locale=" + $('#locale').val(), function(data){
    					$('html').animate({scrollTop:0}, 'slow');
    				 	$('body').animate({scrollTop:0}, 'slow');	
	 					$.unblockUI();	
	 					$("#orderSubmissionDiv").html(data);
	 	            },'html'
 	        ).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		});
	});		
	</script>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>	
	<div id="orderSubmissionDiv">	
		<c:choose>
			<c:when test="${!(empty questions) }">
				<input type="button" id="orderSubmission" value="<spring:message code='generic.submit' text='Order Submission'/>"  style="width: 100px;margin: 10px;"/>		
				<table class="uiTable" style="width: 850px;">
					<tr>
						<th style="min-width: 10px !important; max-width:25px !important;"><spring:message code="generic.serialNo" text="Serial Number"></spring:message>
						<th><spring:message code="question.subject" text="Subject"></spring:message></th>
						<th style="min-width: 100px !important; max-width:120px !important;"><spring:message code="question.ordering_for_submission" text="Submission Order"></spring:message></th>
					</tr>			
					<c:set var="index" value="1"></c:set>	
					<c:forEach items="${questions}" var="i" varStatus="loop">
						<input type="hidden" class="questionId" id="questionId${loop.count}" name="questionId${loop.count}" value="${i.id}">
						<tr id="row${loop.count}">
							<%-- <td class="chk"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"></td> --%>
							<td style="min-width: 10px !important; max-width:25px !important;vertical-align: top;">
								${formater.formatNumberNoGrouping(loop.count, locale)}
							</td>
							<td>${i.submissionPriority}. ${i.subject}</td>
							<td style="min-width: 100px !important; max-width:120px !important;">
								<select class="submissionOrder" id="submissionOrder${loop.count}" name="submissionOrder${loop.count}">
									<option value="${defaultSubmissionPriority}"><spring:message code="question.default_ordering_for_submission" text="Creation Order"/></option>	
									<c:forEach var="submissionOrder" begin="1" end="${fn:length(questions)}" step="1">
										<c:choose>
											<c:when test="${not empty i.submissionPriority and i.submissionPriority!=defaultSubmissionPriority and submissionOrder==loop.count}">
												<option value="${submissionOrder}" selected="selected">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
											</c:when>
											<c:otherwise>
												<option value="${submissionOrder}">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
											</c:otherwise>
										</c:choose>										
									</c:forEach>
								</select>
							</td>					
						</tr>					
						<%-- <c:set var="index" value="${index+1}"></c:set> --%>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<spring:message code="question.noquestions" text="No Completed Questions Found"></spring:message>
			</c:otherwise>
		</c:choose>
		<input type="hidden" id="questionsCount" value="${fn:length(questions)}"/>
	</div>
	<input id="submissionMsg" value="<spring:message code='question.ordering_for_submission.prompt' text='Do you want to update the submission order of questions?'></spring:message>" type="hidden">
	<input type="hidden" name="houseType" id="houseType" value="${houseType}"/>
	<input type="hidden" name="deviceType" id="deviceType" value="${deviceType}"/>
	<input type="hidden" name="questionType" id="questionType" value="${questionType}"/>
	<input type="hidden" name="locale" id="locale" value="${locale}"/>	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>