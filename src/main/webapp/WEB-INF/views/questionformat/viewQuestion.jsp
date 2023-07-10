<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	
	$(".action").attr("checked","checked");
	
	
	var qsnId=new Array();
	$(".action").each(function(){
		if($(this).is(":checked")){
		
		qsnId.push($(this).attr("id").split("chk")[1]);
		
		}
	});

		   for (var i=0; i<qsnId.length; i++) {

			 var questionText = $(".questionText_"+qsnId[i]).get(0).value ;
			questionText = cleanFormatting(questionText);
			$(".questionText_"+qsnId[i]).get(0).value = questionText;
			
			
		   var revisedQuestionText = $(".revisedQuestionText_"+qsnId[i]).get(0).value  ;
			revisedQuestionText = cleanFormatting(revisedQuestionText);
			$(".revisedQuestionText_"+qsnId[i]).get(0).value  = revisedQuestionText;
			
			
		   var 	answer = $(".answer_"+qsnId[i]).get(0).value;
		   answer = cleanFormatting(answer);
		   $(".answer_"+qsnId[i]).get(0).value= answer;
		    
			
		} 

	/* var continueLoop=true;
	$(".action").each(function(){
		if(continueLoop){
			if(this.disabled){
				$("#chkall").attr("disabled","disabled");
				flag=true;
			}
		}
	}); */

	/**** Check/Uncheck Submit All ****/		
	$("#chkall").change(function(){
		if($(this).is(":checked")){
			$(".action").attr("checked","checked");	
		}else{
			$(".action").removeAttr("checked");
		}
	});
	
	
});

</script>

<style>
.txt{
	width:160px;
	height:400px;
}
</style>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty questions) }">
			<h3>${success} ${failure}</h3>
						
			
			<table class="uiTable">
					<tr>
						<th  style=" min-width:30px;"><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th style=" min-width:30px;"><spring:message code="question.number" text="Number"></spring:message></th>
						<th style="min-width:60px;"><spring:message code="question.actor" text="Actor"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="question.QuestionText" text="Question Text"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="yaadidetails.revisedQuestionText" text="revisedText"></spring:message></th>
						<th style=" min-width:100px;"><spring:message code="question.answer" text="SM"></spring:message></th>
					</tr>			
					<c:forEach items="${questions}" var="i">
						<tr>
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							
							<td style="min-width:30px;" id="${i.id}">${i.formatNumber()}  </td>
							
							<td  style=" min-width:100px;">
							<spring:message code="question.actor" text="Actor"></spring:message> :-
							${actor}
							<br>
							<b>${i.recommendationStatus.name }</b>
							<br>
							<b>${i.subDepartment.name }</b>
							</td>
							
							<%-- <td  style=" min-width:170px;"><textarea rows="7" cols="22">${i.subject}</textarea></td> --%>
							<td style="min-width:110px;" >
							
								 <textarea  class=" questionText_${i.id} txt">${i.questionText}</textarea>
							 
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="30">${i.revisedQuestionText}</textarea></td> --%>
							<td style="text-align:justify;min-width:110px;" >
									
								<textarea  class=" revisedQuestionText_${i.id} txt">${i.revisedQuestionText}</textarea>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="22">${i.remarks}</textarea></td> --%>
							<td style="text-align:justify;min-width:100px;">
										<textarea class="answer_${i.id} txt" >${i.answer}</textarea>
							</td>
						</tr>
					</c:forEach>
					
					<c:forEach items="${childQuestions}" var="i" >
						<tr bgcolor="#D3D3D3">
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							
							<td style="min-width:30px;" id="${i.id}">${i.formatNumber()}  </td>
							
							<td  style=" min-width:60px;">
							${i.actor } 
							<br>
							<b>${i.recommendationStatus.name }</b>
							<br>
							<b>${i.subDepartment.name }</b>
							</td>
							
							<%-- <td  style=" min-width:170px;"><textarea rows="7" cols="22">${i.subject}</textarea></td> --%>
							<td style="text-align:justify;min-width:110px;" >
										<textarea class=" questionText_${i.id} txt">${i.questionText}</textarea>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="30">${i.revisedQuestionText}</textarea></td> --%>
							<td style="text-align:justify;min-width:110px;" >
										<textarea  class="revisedQuestionText_${i.id} txt">${i.revisedQuestionText}</textarea>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="22">${i.remarks}</textarea></td> --%>
							<td style="text-align:justify;min-width:100px;" >
										<textarea class="answer_${i.id} txt" >${i.answer}</textarea>
							</td>
						</tr>
					</c:forEach>
					
			</table>
		</c:when>
		<c:otherwise>
		<div>
		</div>
		<br>
		<div style="margin: auto;
  width: 50%;
  border: 3px solid red;
  padding-top: 15px;">
  Can't Find Number Please Check Once Again the details entered
		</div>
			
		</c:otherwise>
	</c:choose>
