<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				$('#linkForReport').css('font-size','20px');
				
				$('#submitClubbedQuestions').click(function() {
					/* $.get('question/report/generateIntimationLetter?questionId='+$('#questionId').val()
							+'&clubbedQuestions='+$("#clubbedQuestions").val()).fail(function(){				
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}						
					}); */
					//$('#linkForReport').attr('href', 'question/report/generateIntimationLetter?questionId='+$('#questionId').val());
					$('#linkForReport').click;
				});
				
				$('#linkForReport').click(function() {
					if($('#clubbedQuestions').val()==undefined || $('#clubbedQuestions').val()=="") {
						alert("Please select atleast one clubbed question");
						return false;
					}
					$('#linkForReport').attr('href', 'question/report/generateClubbedIntimationLetter?questionId='+$('#questionId').val()
							+'&clubbedQuestions='+$('#clubbedQuestions').val()+'&outputFormat=WORD');
				});
			});
		</script>		 
	</head>	
	<body>		
		<p id="error_p" style="display: none;">&nbsp;</p>
		<h3><spring:message code='question.generateClubbedIntimationLetter.selectClubbedQuestions' text='Select Clubbed Questions'/>:</h3>
		<p style="margin-top: 20px;">
			<label style="position: relative;top: -95px;width: 150px;margin-right: 20px;"><spring:message code='question.generateClubbedIntimationLetter.clubbedQuestions' text='Clubbed Questions'/></label>
			<select id="clubbedQuestions" class="sSelectMultiple" name="clubbedQuestions" multiple="multiple">
				<c:forEach items="${nameClubbedQuestionVOs}" var="i">
					<option value="${i.id}">${i.number}</option>
				</c:forEach>						
			</select>
		</p>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport"><spring:message code='question.generateClubbedIntimationLetter.generateReport' text='Generate Report'/></a>
			</p>
		</div>		
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="questionId" name="questionId" value="${questionId}"/>				
	</body>
</html>