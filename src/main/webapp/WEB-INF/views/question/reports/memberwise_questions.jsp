<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {		
		var myArray = [];
		
		$('#member option').each(function(){
			myArray.push( this.text );				
		});
		
		$( ".autosuggest").autocomplete({
						
				source: myArray,
				select:function(event,ui){		
					$('#member option').each(function(){
						if($(this).text()==ui.item.value) {
							$("#memberId").val(this.value);
						}
					});					
					console.log($("#memberId").val());
				}	
		});
		
		$("#member").change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			var value=$(this).val();
			if(value!='-'){
			var parameters="member="+$(this).val()+"&session="
			+$("#session").val()+"&questionType="+$("#questionType").val();
			var resource='question/report/memberwisequestions/questions';
			$.get(resource+'?'+parameters,function(data){
				$("#listQuestionsDiv").empty();	
				$("#listQuestionsDiv").html(data);
				$.unblockUI();		
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
			}else{
				$("#listQuestionsDiv").empty();
				$.unblockUI();			
			}
			$("#errorDiv").hide();
			$("#successDiv").hide();
		});		
	});
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="toolTip tpRed clearfix" id="errorDiv" style="display: none;">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_failed" text="Please correct following errors." /></p>
<p></p>
</div>

<div class="toolTip tpGreen clearfix" id="successDiv" style="display: none;">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_success" text="Data saved successfully." /></p>
<p></p>
</div>

<p><label style="margin: 10px;"><spring:message
	code="memberwisereport.member" text="Member" /></label>
	<select id="member" name="member">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
	<c:forEach items="${eligibleMembers}" var="i">
		<option value="${i.id }"><c:out value="${i.name}"></c:out></option>
	</c:forEach>
</select>
<%-- <a id="cumulativeMemberQuestionsReport" href="#" style="margin-left: 20px;">
	<spring:message code="memberwisereport.cumulativeMemberQuestionsReport" text="Cumulative Member Questions Report"/>
</a> --%>
<div id="memberText">
	<input type="text" class="autosuggest sText" id="memberOption" style="width: 100px;" />
	<a href="#" id="createMemberwiseReport" style="text-decoration: none;"><span id="goBtn"><spring:message code="part.memberwiseReport" text="Go" ></spring:message></span></a>
</div>
</p>
<div id="listQuestionsDiv">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
<input type="hidden" id="group" name="group" value="${group}">
<input type="hidden" id="answeringDate" name="answeringDate" value="${answeringDate}">
<input type="hidden" id="memberId" name="memberId">	

<input type="hidden" name="pleaseSelect" id="pleaseSelect"
	value="<spring:message code='please.select' text='Please Select'/>">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>