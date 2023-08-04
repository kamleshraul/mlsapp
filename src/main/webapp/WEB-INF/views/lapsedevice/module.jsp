<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="proceedingautofill.list" text="List of Proceeding Autofill"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* =============== ACTIONS =============== */
	
	
	/* =============== DOCUMENT READY =============== */
	$('document').ready(function(){
		
		
		
		$("#submit").click(function(){
			
			  $.prompt($('#toBeLapsedMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	
			        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						
						var parameters = "houseType="+$("#selectedHouseType").val()
						 +"&deviceType="+$("#selectedDeviceType").val()
						 +"&memberIds="+$("#memberIds").val()
						 +"&latestAssemblyHouseFormationDate="+$("#latestAssemblyHouseFormationDate").val();
						 
						 var resource = ''
						 if( $("#selectedDeviceType").val() == 4 ||  $("#selectedDeviceType").val() == 5){
								resource='question/getNumberToLapse';
						 } 
						 
						  $.get(resource+"?"+parameters,function(data){
							
							 $("#ResultDiv").empty();
							 $("#ResultDiv").html("<p  style = ' width: 850px; word-wrap: break-word;'>"+data+"</p>");				 
						 
							 $.unblockUI();
						 },'html').fail(function(){
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
</head>
<body>
<div class="clearfix tabbar">
	<ul class="tabs">
		<li>
			<a href="#" id="list_tab" class="selected tab">
				<spring:message code="generic.list" text="List"></spring:message>
			</a>
		</li>
	</ul>
		
	
	<div class="tabContent">
	
	
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="adjournmentmotion.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:99px;height: 25px;">			
				<c:forEach items="${allhousetype}" var="i">
					<c:choose>
						<c:when test="${houseType==i.type}">
							<option value="${i.name}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> |		
			
			<a href="#" id="select_deviceType" class="butSim">
				<spring:message code="mytask.deviceType" text="Session Type"/>
			</a>
			<select name="selectedDeviceType" id="selectedDeviceType" style="width:99px;height: 25px;">				
				<c:forEach items="${alldevices}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
				</c:forEach> 
			</select> |		
			
			<br>
			<br>
			<label style="width:150px;height: 25px;"  class="small"><spring:message code="question.number" text="Name" />  </label>			
			<label style="width:150px;height: 25px;"  class="small">:   </label>			
			<textarea id="memberIds" ></textarea> 
			 <input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						
			<div id="ResultDiv" class="fields clearfix" >
				
				<c:if test="${(error!='') && (error!=null)}">
					<h4 style="color: #FF0000;">${error}</h4>
				</c:if>
				
			</div>
	</div>
	
	<input type="hidden" id="key" name="key">
	<input type="hidden" id="toBeLapsedMessage"  value="Do You want lapse devices of following Members ?">
	
</div>
</body>
</html>