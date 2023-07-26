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
		
	/* var continueLoop=true;
	$(".action").each(function(){
		if(continueLoop){
			if(this.disabled){
				$("#chkall").attr("disabled","disabled");
				flag=true;
			}
		}
	}); */
	/**** Edit Questions ****/
	$(".edit").click(function(){
		editQuestion($(this).attr("id"),"no");
	});
	/**** Question's Read Only View ****/
	$(".readonly").click(function(){
		editQuestion($(this).attr("id"),"yes");
	});
	/**** Check/Uncheck Submit All ****/		
	$("#chkall").change(function(){
		if($(this).is(":checked")){
			$(".action").attr("checked","checked");	
		}else{
			$(".action").removeAttr("checked");
		}
	});
	
	  $(".ViewSm").click(function(event){
		  	var QsnhiddenId = $(event.target).siblings();
		  	var id ="";
		  	if (QsnhiddenId != null && QsnhiddenId.length > 0)
		  		{
		  		
		  		id = QsnhiddenId[0].value;
		  	}
		  	console.log(QsnhiddenId );
		  	
		    $.get('question/status/'+id,function(data){
			    $.fancybox.open(data,{autoSize: false, width: 500, height:400});
		    }).fail(function(){
  			if($("#ErrorMsg").val()!=''){
  				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
  			}else{
  				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
  			}
  			scrollTop();
  		});
		    return false;
	    });
	 
});

function cleanFormattingData(){

	
	var myarray = $("#questionIds").val().split(",")
	myarray.pop();
		
	
  	for (var i=0; i<myarray.length; i++) {
				 			
 			if(tinyMCE.get("subject_"+myarray[i]) != null){
			 var subject = tinyMCE.get("subject_"+myarray[i]).getContent(); 
			 subject = cleanFormatting(subject);
			 $("#subject_"+myarray[i]).html(subject) ;
 			} 
			
 			if(tinyMCE.get("revisedQuestionText_"+myarray[i]) != null){
			 var content = tinyMCE.get("revisedQuestionText_"+myarray[i]).getContent(); 
			 content = cleanFormatting(content);
			$("#revisedQuestionText_"+myarray[i]).html(content) ;
 			}								
		} 
}

</script>
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
						<th style="min-width:100px;"><spring:message code="question.members" text="Member"></spring:message></th>
						<th style=" min-width:150px;" ><spring:message code="question.subject" text="Subject"></spring:message></th>
						<th style=" min-width:150px;" ><spring:message code="question.QuestionText" text="revisedText"></spring:message></th>
						<th style=" min-width:30px;"><spring:message code="question.answer" text="SM"></spring:message></th>
					</tr>			
					<c:forEach items="${questions}" var="i">
						<tr>
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							
							<td style="min-width:30px;" id="${i.id}">${i.formatNumber()}</td>
							
							<td  style=" min-width:80px;">${i.primaryMember.getFullname()}
							<div>                              
							 <a href="#" class="ViewSm"><spring:message code="yaadi.ViewSM" text="ViewSM"></spring:message></a>
							 <input type="hidden" class="Question_id" value="${i.id}">
							 </div>  
							</td>
							
							<%-- <td  style=" min-width:170px;"><textarea rows="7" cols="22">${i.subject}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="subject_${i.id}">
									${i.subject}
								</div>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="30">${i.revisedQuestionText}</textarea></td> --%>
							<td style="text-align:justify;min-width:200px;" >
								<div  class="editable" width="250px"  id="revisedQuestionText_${i.id}">
									${i.revisedQuestionText}
								</div>
							</td>
							<%-- <td style=" min-width:170px;" ><textarea rows="7" cols="22">${i.remarks}</textarea></td> --%>
							<td style="text-align:justify;min-width:260px;" >
								<div  class="editable" width="250px"  id="answer_${i.id}">
									${i.answer}
								</div>								
							</td>
						</tr>
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="question.noquestions" text="No Questions Found"></spring:message>
		</c:otherwise>
	</c:choose>
	<input type="hidden" id="questionIds" value="${questionIds}">