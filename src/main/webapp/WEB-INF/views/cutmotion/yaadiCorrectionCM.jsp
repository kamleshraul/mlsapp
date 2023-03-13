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
	
	
	tinymce.init({
		  selector: 'div.editable',
		  inline: true,
		  theme: 'inlite',
		 
		  force_br_newlines : false,
	  force_p_newlines : false,
	  forced_root_block : "",
	  nonbreaking_force_tab: true,
		  plugins: [
		    'searchreplace fullscreen',
		     ''
		  ],
		  toolbar: 'bold italic | alignleft aligncenter alignright alignjustify'
		});
	
	$("#qsnSubmit").click(function(){
	
		var cutmotionId=new Array();
		$(".action").each(function(){
			if($(this).is(":checked")){
				cutmotionId.push($(this).attr("id").split("chk")[1]);
			
			}
		});
		
		if(cutmotionId.length<=0){
			$.prompt($("#selectItemsMsg").val());
			return false;	
		}
		
		
		 for (var i=0; i<cutmotionId.length; i++) {
			 var noticeContent = $(".noticeContent_"+cutmotionId[i]).get(0).value 
				noticeContent = cleanFormatting(noticeContent)
				$(".noticeContent_"+cutmotionId[i]).get(0).value = noticeContent;
		 }
		
		
		
		
		var items =new Array();
		for (var i=0; i<cutmotionId.length; i++) {
			console.log(i+"here")
		    items.push({
		    	'cutmotionId':cutmotionId[i],
		    	'amount_tab_deducted':$(".amount_tab_deducted_"+cutmotionId[i]).get(0).innerText ,
		    	'demand_number':$(".demand_number_"+cutmotionId[i]).get(0).innerText ,
		    	'main_title':$(".main_title_"+cutmotionId[i]).get(0).innerText,
		    	'revised_sub_title':$(".revised_sub_title_"+cutmotionId[i]).get(0).innerText,
		    	'item_number':$(".item_number_"+cutmotionId[i]).get(0).innerText,
		    	'total_amount_demanded':$(".total_amount_demanded_"+cutmotionId[i]).get(0).innerText,
		    	'noticeContent':$(".noticeContent_"+cutmotionId[i]).get(0).value
		    	
		});
		}
		console.log(items); 
		
		 $.prompt($('#submissionMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post('cutmotion/cutmotionFormatYaadi?loadIt=yes',
			        	{items:items,
					    itemsLength:items.length,
					    cutmotionId:cutmotionId,
					    houseType:$("#selectedHouseType").val()
						 ,deviceType:$("#selectedDeviceType").val()
						 		 	
					 	},
	    	            function(data){
	       					console.log(data)
	    					$.unblockUI();	
	    					$("#bulkResultDiv").empty();	
	    					$("#bulkResultDiv").html(data);
	    					$("#qsnUpdateButton").hide();
	    	            }
	    	            ).fail(function(){
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



</script>
<style>
.txt{
	width:340px;
	height:300px;
}
</style>

	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div id="bulkResultDiv">
	<h3>${success} ${failure}</h3>
	</div>
	
	<c:choose>
		<c:when test="${!(empty cutmotions) }">
			<h3>${success} ${failure}</h3>
						
				<div id="qsnUpdateButton">
						<p >
							<input id="qsnSubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
				</div>
			<table class="uiTable">
					<tr>
						<th  style=" min-width:30px;"><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>
						<th style=" min-width:30px;"><spring:message code="cutmotion.number" text="Number"></spring:message></th>
						<th style=" min-width:30px;"><spring:message code="dashboard.membername" text=""></spring:message></th>
						<th style="min-width:90px;"><spring:message code="yaadiDetails.amountToBeDeducted" text=""></spring:message></th>
						<th style=" min-width:200px;" ><spring:message code="yaadiDetails.details" text=""></spring:message></th>
						<%-- <th style=" min-width:100px;" ><spring:message code="yaadiDetails.totalAmountDemanded" text=""></spring:message></th> --%>
						<th style=" min-width:200px;" ><spring:message code="cutmotion.noticeContent" text=""></spring:message></th>
						
					</tr>			
					<c:forEach items="${cutmotions}" var="i">
						<tr>
							<td style="min-width:30px;"><input type="checkbox" id="chk${i[0]}" name="chk${i[0]}" class="sCheck action" value="true"  style="margin-right: 10px;">	</td>						
						
						<td style="min-width:30px;" id="${i[0]}">${i[24]}</td>
						<td style="min-width:90px;" >${i[64] }</td>
						
						<td style="min-width:90px;" >
						<div class="editable amount_tab_deducted_${i[0]}">
						<fmt:formatNumber type="number" groupingUsed="false"  maxIntegerDigits="12" value="${i[4]}"  />
						</div>
						</td>
						
						<td style="min-width:200px;" >
						
						<label class="small"><b><spring:message code="yaadiDetails.demandnumber" text="प"/> </b></label> 
						
						<div class="editable demand_number_${i[0]} " >
						${i[10]}
						</div><br>
						
						<label class="small"><b><spring:message code="yaadiDetails.maintitle" text="प"/> </b></label>
						
						<div class="editable  main_title_${i[0]}" >
						${i[29] }
						</div>
						<br>
						
						<label class="small"><b><spring:message code="yaadiDetails.revisedSubTitle" text="प"/> </b></label>
						
						<div class="editable  revised_sub_title_${i[0]}"  >
						${i[32] }
						</div>
						<br>
						
						<label class="small"><b><spring:message code="yaadiDetails.itemNumber" text="प"/> </b></label>
						
						<div class="editable  item_number_${i[0]}" >
						${i[19] }
						</div><br>
						
						<label class="small"><b><spring:message code="yaadiDetails.totalAmountDemanded" text="प"/> </b></label>
						
						<div class="editable total_amount_demanded_${i[0]}">
						<fmt:formatNumber type="number" groupingUsed="false" maxIntegerDigits="12" value="${i[37]}"  />
						</div>
						</td>
						
					<%-- 	<td style="min-width:100px;" id="${i[0]}">
						
						<div class="editable">
						<fmt:formatNumber type="number" maxIntegerDigits="12" value="${i[37]}"  />
						</div></td> --%>
						
						<td style="min-width:200px;" id="${i[0]}"><textarea class="noticeContent_${i[0]}  txt" >${i[30]}</textarea></td>
						</tr>
						
						
					</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="" text=""></spring:message>
		</c:otherwise>
	</c:choose>
	
			<input id="selectItemsMsg" value="<spring:message code='' text='Please select a cutmotion number'></spring:message>" type="hidden">
			<input id="submissionMsg" value="<spring:message code='' text='Do you want to update selected motions'></spring:message>" type="hidden">