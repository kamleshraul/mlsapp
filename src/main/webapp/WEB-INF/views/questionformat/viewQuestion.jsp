<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	
	
	tinymce.remove();
	
	tinymce.init({
		  selector: 'div.editable',
		  
		  inline: true,
		  theme: 'inlite',
		  force_br_newlines : true,
    	  force_p_newlines : false,
    	  forced_root_block : "",
    	  //nonbreaking_force_tab: true,
    	  max_width: 250
    	 
		});
	
	
	$(".action").attr("checked","checked");	

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

function cleanFormattingAnswer(){
	
	
	$.prompt($('#answerFormatMsg').val(),{
		buttons: {Ok:true, Cancel:false}, callback: function(v){
        if(v){
        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
        	var qsnId=new Array();
        	$(".action").each(function(){
        		if($(this).is(":checked")){		
        		qsnId.push($(this).attr("id").split("chk")[1]);
        		}
        	});
        	
        	for(var i=0;i<qsnId.length;i++){
        		  if(tinyMCE.get("answer_"+qsnId[i]) != null){
        				var answer = tinyMCE.get("answer_"+qsnId[i]).getContent(); 
        				answer = cleanFormatting(answer);
        				$("#answer_"+qsnId[i]).html(answer) ;
        				
        		 	}
        	}
        	$.unblockUI();	
       	 }
       }
	});
	

}

function cleanFormattingData(){
	var qsnId=new Array();
	$(".action").each(function(){
		if($(this).is(":checked")){
		
		qsnId.push($(this).attr("id").split("chk")[1]);
		
		}
	});
	// console.log(qsnId)
	 for (var i=0; i<qsnId.length; i++) {
		    		    	
		  if(tinyMCE.get("questionText_"+qsnId[i]) != null){
				var questionText = tinyMCE.get("questionText_"+qsnId[i]).getContent(); 
				questionText = cleanFormatting(questionText);
				$("#questionText_"+qsnId[i]).html(questionText) ;
				
		 	}	
		  
		  if(tinyMCE.get("revisedQuestionText_"+qsnId[i]) != null){
				var revisedQuestionText = tinyMCE.get("revisedQuestionText_"+qsnId[i]).getContent(); 
				revisedQuestionText = cleanFormatting(revisedQuestionText);
				$("#revisedQuestionText_"+qsnId[i]).html(revisedQuestionText) ;
				
		 	}


		    			
		} 
}


function childRevision(id){
	
	 $.get('question/revisions/'+id,function(data){
	    $.fancybox.open(data,{autoSize: false, width: 800, height:700});		    
   }).fail(function(){
		if($("#ErrorMsg").val()!=''){
			$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		}else{
			$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		}
		
	});
	 
   return false; 
}

</script>

<style>

</style>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
		
	<c:choose>
		<c:when test="${!(empty questions) }">
			<h3>${success} ${failure}</h3>
						
			
			<table style="width:850px;" class="uiTable" >
					<tr>
						<th  style=" min-width:30px;"><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th style=" min-width:30px;"><spring:message code="question.number" text="Number"></spring:message></th>
						<th style="min-width:60px;"><spring:message code="question.actor" text="Actor"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="question.QuestionText" text="Question Text"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="yaadidetails.revisedQuestionText" text="revisedText"></spring:message></th>
						<th style=" min-width:100px;"><spring:message code="question.answer" text="SM"></spring:message>
						<a href='#' style='font-weight:bold; float:right;'   onclick='cleanFormattingAnswer()'> 
 									<img src='./resources/images/refresh.png' style='display:inline-block' title='Clean format Answer' width='15px' height='15px' align='justify'>
 						</a> 
						</th>
					</tr>			
					<c:forEach items="${questions}" var="i">
						<tr>
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">
								
							</td>					
							
							<td style="min-width:30px;" id="${i.id}">${i.formatNumber()}
							<div style="margin-top: 10px;">
								<a href='#' style='font-weight:bold'  onclick='childRevision(${i.id})'> 
 									<img src='./resources/images/referenced.png' style='display:inline-block' title='Referenced' width='15px' height='15px' align='justify'>
 								</a> 
							</div>
							</td>
							
							<td  style=" min-width:130px;">
							<div style="border-style: dotted;">
								Actor :-
								<c:choose>
									<c:when test="${!(empty actor) }">
										<p style="padding: 5px">
										  <b><u> ${actor} </u></b>
										</p>																			
									</c:when>
									<c:otherwise>
										null
									</c:otherwise>
								</c:choose>								
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p style="padding: 5px">
									status :-  <b> <u> ${i.recommendationStatus.name } </u></b>
								</p>								
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p style="padding: 5px">
									Department :-  <b> <u>${i.subDepartment.name }</u> </b>
								</p>	
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p>
									Answering Date :- <b> <u>${i.chartAnsweringDate.answeringDate }</u> </b>
								</p>
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p>
									Group Number :- <b> <u> ${i.group.number }</u> </b>
								</p>
							</div>

							</td>
							
							<%-- <td  style=" min-width:170px;"><textarea rows="7" cols="22">${i.subject}</textarea></td> --%>

							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="questionText_${i.id}">
									${i.questionText}
								</div>
							</td>
							
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="30">${i.revisedQuestionText}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="revisedQuestionText_${i.id}">
									${i.revisedQuestionText}
								</div>
							</td>
							
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="22">${i.remarks}</textarea></td> --%>
							
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="answer_${i.id}">
									${i.answer}
								</div>
							</td>
			
						</tr>
					</c:forEach>
					
					<c:forEach items="${childQuestions}" var="i" >
						<tr bgcolor="#D3D3D3">
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">	</td>					
							
							<td style="min-width:30px;" id="${i.id}">${i.formatNumber()} 
							<div style="margin-top: 10px;">
								<a href='#' style='font-weight:bold'   onclick='childRevision(${i.id})'> 
 									<img src='./resources/images/referenced.png' style='display:inline-block' title='Revision' width='15px' height='15px' align='justify'>
 								</a> 
							</div>
							 </td>
							
							<td  style=" min-width:60px;">
							<div style="border-style: dotted;">
								Actor :- 
								<c:choose>
									<c:when test="${!(empty actor) }">
										<b> <u> ${actor} </u> </b>
									</c:when>
									<c:otherwise>
										null
									</c:otherwise>
								</c:choose>								
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p style="padding: 5px">
									status :-  <b> <u> ${i.recommendationStatus.name } </u></b>
								</p>								
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p style="padding: 5px">
									Department :-  <b> <u>${i.subDepartment.name }</u> </b>
								</p>	
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p>
									Answering Date :- <b> <u>${i.chartAnsweringDate.answeringDate }</u> </b>
								</p>
							</div>
							
							<br>
							<div style="border-style: dotted;">
								<p>
									Group Number :- <b> <u> ${i.group.number }</u> </b>
								</p>
							</div>
							</td>
							
							<%-- <td  style=" min-width:170px;"><textarea rows="7" cols="22">${i.subject}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="questionText_${i.id}">
									${i.questionText}
								</div>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="30">${i.revisedQuestionText}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="revisedQuestionText_${i.id}">
									${i.revisedQuestionText}
								</div>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="22">${i.remarks}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="answer_${i.id}">
									${i.answer}
								</div>
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
		  text-align:center;
		  width: 50%;
		  border: 3px solid red;
		  padding-top: 15px;">
  			 Can't Find Number Please Check Once Again the details entered
		</div>
		
		
			
		</c:otherwise>
	</c:choose>
	<script>
	 setTimeout(cleanFormattingData, 2000);
	</script>