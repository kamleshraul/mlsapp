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
			
	
			 var revisedSubject = $(".revisedSubject_"+qsnId[i]).val() ;
			revisedSubject = cleanFormatting(revisedSubject);
			$(".revisedSubject_"+qsnId[i]).get(0).value = revisedSubject;
			//$(".revisedSubject_"+qsnId[i]).val() = revisedSubject;
			
			
		  var revisedDetails = $(".revisedDetails_"+qsnId[i]).val()  ;
		    revisedDetails = cleanFormatting(revisedDetails);
		    $(".revisedDetails_"+qsnId[i]).get(0).value = revisedDetails;
		   
			
		 	var subject = $(".subject_"+qsnId[i]).get(0).value;
		 	subject = cleanFormatting(subject);
		   $(".subject_"+qsnId[i]).get(0).value = subject;
		 
		      
			
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
	width:210px;
	height:400px;
}
</style>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<c:choose>
		<c:when test="${!(empty motions) }">
			<h3>${success} ${failure}</h3>
						
			
			<table class="uiTable">
					<tr>
						<th  style=" min-width:30px;"><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th style=" min-width:20px;"><spring:message code="" text="Number"></spring:message></th>
						<th style="min-width:90px;"><spring:message code="" text="Actor"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="" text="revisedSubject"></spring:message></th>
						<th style=" min-width:110px;" ><spring:message code="" text="revisedDetails"></spring:message></th>
						<th style=" min-width:100px;"><spring:message code="" text="reply"></spring:message></th>
						
					</tr>			
					
						<tr>
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${motions.id}" name="chk${motions.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							
							 <td style="min-width:20px;" id="${motions.id}">${motions.number}</td>
							
							<td  style=" min-width:90px;">
							<spring:message code="question.actor" text="Actor"></spring:message> :-
							<b>${motions.localizedActorName }</b>
							<br>
							:- ${motions.recommendationStatus.name }
							<br>
							:- ${motions.subDepartment.name }
							</td>
							
							<td  style=" min-width:170px;"><textarea class=" revisedSubject_${motions.id} txt" readonly>${motions.revisedSubject}	</textarea></td>
							
							<td style="min-width:110px;" ><textarea  class=" revisedDetails_${motions.id} txt" readonly>${motions.revisedDetails}</textarea></td>
							
							
							<td style="text-align:justify;min-width:100px;"><textarea class="subject_${motions.id} txt"  readonly>${motions.subject}</textarea></td> 
						</tr>
				
					
					<c:forEach items="${childMotions}" var="i" >
						<tr bgcolor="#D3D3D3">
							
							
							<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">						
							
							 <td style="min-width:30px;" id="${i.id}">${i.number}  </td>
							
							<td  style=" min-width:100px;">
							<spring:message code="question.actor" text="Actor"></spring:message> :-
							
							<br>
							<b>${i.recommendationStatus.name }</b>
							<br>
							<b>${i.subDepartment.name }</b>
							</td>
							
							<td  style=" min-width:170px;"><textarea class=" revisedSubject_${i.id} txt" readonly>${i.revisedSubject}</textarea></td>
							<td style="min-width:110px;" ><textarea  class=" revisedDetails_${i.id} txt" readonly>${i.revisedDetails}</textarea>
							</td>
							<td style="text-align:justify;min-width:100px;"><textarea class="reply_${i.id} txt" readonly >${i.reply}</textarea>
							</td> 
						</tr>
					</c:forEach> 
					
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="question.nomotions" text="No motions Found"></spring:message>
		</c:otherwise>
	</c:choose>
