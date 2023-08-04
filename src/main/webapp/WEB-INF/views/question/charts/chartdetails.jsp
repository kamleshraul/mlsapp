<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
<script>
	$(document).ready(function(){
		
	});
	$("#chartChangeSubmit").click(function(){
		  if(!$.trim($("#chartRemark").val())){
			  alert($("#errorRemark").val());
		  }else{
			$.post("question/updatedChart/"+$("#changeChartAnsweringDate").val()+"/questionId/"+$("#qId").val(),{chartRemark : $("#chartRemark").val(),currentUserGroupType : $("#currentUserGroupType").val()},function(data){
				if(data == true){
				    setTimeout(5000,function(){alert("SUCCESS")}); 
				    $.fancybox.close();
				}
				else{
					setTimeout(5000,function(){alert("Failure: Please contact administrator")});
					$.fancybox.close();
				}
			});
		  }
		});
</script>
<style>
  table{
    justify-content:center;
  }
  #titleHeader{
   justify-content:center;
   text-align:center;
  }
  #submit{
  justify-content:center;
  }
  td{
    padding: 8px;
  }
</style>
</head>
<body>
<div id="titleHeader">
	<h1>${formattedDeviceType} : ${formattedQsNumber}</h1>
</div>
<br/>
<form>
<table>
   <tr>
     <td><spring:message code="generic.subject" text="Subject"/>:</td>
     <td>${subject}</td>
   </tr>
   <tr>
     <td><spring:message code="question.group" text="Group"/>:</td>
     <td>${groupNumber}</td>
   </tr>
	<tr>
     <td><spring:message code="question.chartDateTitle" text="Current Chart Answering Date"/>:</td>
     <td>${formattedChartAnsweringDate}</td>
   </tr>
   <tr>
     <td><spring:message code="question.changeChartAnsweringDate" text="Change Chart Answering Date"/>:</td>
     <td><select id="changeChartAnsweringDate" class="sSelect">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${questionDates}" var="i">
					 	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach>
		</select>
	</td>
   </tr>
   <tr>
     <td><spring:message code="question.remarks" text="Remarks"/>:</td>
     <td><textarea id="chartRemark" name="chartRemark" class="chartRemark" rows="4" style="width: 250px;"></textarea></td>
   </tr>
   <tr>
     <td colspan="2" style="text-align: center;"><input id="chartChangeSubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef"></td>
   </tr>
</table>
<input type="hidden" name="qId" id="qId" value="${questionId}"/>
<input type="hidden" name="currentUserGroupType" id="currentUserGroupType" value="${strusergroupType}"/>
<input type="hidden" name="errorRemark" id="errorRemark" value="<spring:message code="generic.chartRemarkEmpty" text="Please fill in the valid remark."/>">
</form>
</body>
</html>