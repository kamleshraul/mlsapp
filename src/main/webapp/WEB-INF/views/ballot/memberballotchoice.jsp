<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#member").change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			var value=$(this).val();
			if(value!='-'){
			var parameters="member="+$(this).val()+"&session="
			+$("#session").val()+"&questionType="+$("#questionType").val();
			var resource='ballot/memberballot/listchoices';
			$.get(resource+'?'+parameters,function(data){
				$("#listchoices").empty();	
				$("#listchoices").html(data);
				$.unblockUI();		
			},'html');
			}else{
				$("#listchoices").empty();
				$.unblockUI();			
			}
			$("#errorDiv").hide();
			$("#successDiv").hide();
		});		
		$("form").submit(function(e){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
			$.post($('form').attr('action'),  
	            $("form").serialize(),  
	            function(data){
					$("#successDiv").show();
  					$("#errorDiv").hide();  					
   					$("#listchoices").empty();	
   					$("#listchoices").html(data);   	   					
   					$('html').animate({scrollTop:0}, 'slow');
   				 	$('body').animate({scrollTop:0}, 'slow');	
					$.unblockUI();	   				 	   				
	            },'html');
	        return false;  
	    }); 	   
	});
</script>
</head>
<body>
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

<form action="ballot/memberballot/choices" method="post">
<p><label style="margin: 10px;"><spring:message
	code="memberballotchoice.member" text="Member" /></label>
	 <select id="member" name="member">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
	<c:forEach items="${eligibleMembers}" var="i">
		<option value="${i.id }"><c:out value="${i.getFullname()}"></c:out></option>
	</c:forEach>
</select>
</p>
<div id="listchoices">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
</form>

<input type="hidden" name="pleaseSelect" id="pleaseSelect"
	value="<spring:message code='please.select' text='Please Select'/>">
</body>
</html>