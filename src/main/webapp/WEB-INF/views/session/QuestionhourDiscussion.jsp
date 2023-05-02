<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(document).ready(function(){

	$("#chkall").change(function(){
		if($(this).is(":checked")){
			$(".action").attr("checked","checked");	
		}else{
			$(".action").removeAttr("checked");
		}
	});
	
	
	
	
	var length = $(".boolCheck").length;
		for(var i=0;i<length;i++)
		{
			var id = $(".boolCheck").get(i).id;
			
			 if ($(".boolCheck").get(i).checked) {
				 $('#Yes_'+id).show();
				 $('#No_'+id).hide();
		        } else {
		        	$('#Yes_'+id).hide();
					 $('#No_'+id).show();
		        }
		}
	
	
	
	
	$('input[type="checkbox"]').click(function(){
		var id = $(this).attr('id');
		 if ($("#"+id).get(0).checked) {
			 $('#Yes_'+id).show();
			 $('#No_'+id).hide();
	        } else {
	        	$('#Yes_'+id).hide();
				 $('#No_'+id).show();
	        }
		});
	
	
	$("#questionHourDiscussionSubmit").click(function(){
		var DateId=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
				DateId.push($(this).attr("id").split("chk")[1]);
			
			}
		});
		
		
		if(DateId.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}
		
		
		
		 
		
		//length = $(".boolCheck").length;
		var myArray = new Array();
			 for (var i=0; i<DateId.length; i++) {			
				myArray.push({
					"Date":$("#Date_"+DateId[i]).get(0).innerText,
					"Status":$("#status_"+DateId[i]).get(0).checked
					})					 
				}

		   $.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('session/updateQuestionHourDiscussed?loadIt=yes',
			        	{items:myArray,
					    itemsLength:DateId.length,
					    sessionId:$("#SessionId").val()			 	
					 	},
	    	            function(data){
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');	
	    					$.unblockUI();	
	    					$("#bulkResultDiv").empty();	
	    					$("#bulkResultDiv").html(data);	
	    	            }
	    	            ,'html').fail(function(){
	    					$.unblockUI();
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});
	        	}}});  
	});
	
	
	
});


</script>
<style>
.txt{
	width:340px;
	height:300px;
}

input[type=number]
{
-moz-appearance:textfield;
}
</style>
<div id="bulkResultDiv">
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	
	<%-- <h3>${success} ${failure}</h3> --%>
	<c:choose>
		<c:when test="${!(empty SessionDates) }">
			<h3>${success} ${failure}</h3>
						
				<div id="qsnUpdateButton">
						<p >
							<input id="questionHourDiscussionSubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
				</div>
				
			<table class="uiTable">
					<tr>
						<th  style=" min-width:30px;"><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th style=" min-width:30px;"><spring:message code="generic.Date" text="Date"></spring:message></th>
						<th style=" min-width:10px;"><spring:message code="" text="Incuded Or Not"></spring:message></th>
					</tr>			
					<c:forEach items="${SessionDates}" var="i">
						<tr>
						
						<td style="min-width:30px;"><input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck action" value="true"  style="margin-right: 10px;">	</td>						
						<td style="min-width:30px;" id="Date_${i.id}">${i.sessionDate}</td>
						<td style="min-width:10px;" id="${i.id}"> 
						<input type="checkbox" class="boolCheck" name="status_${i.id}"   id="status_${i.id}" ${i.isQuestionHourIncluded==true?'checked':''}>
     					<span style="display:none" id="Yes_status_${i.id}"> Yes</span>
     					<span style="display:none" id="No_status_${i.id}"> No</span>
     					</td>
						
						
						</tr>
						
						
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="" text=""></spring:message>
		</c:otherwise>
	</c:choose>
	</div>

<input type="hidden" id="SessionId" name="SessionId" value="${SessionId}">
<input id="selectItemsMsg" value="<spring:message code='' text='Please select a Date'></spring:message>" type="hidden">
<input id="submissionMsg" value="<spring:message code='' text='Do you want to update selected Date'></spring:message>" type="hidden">