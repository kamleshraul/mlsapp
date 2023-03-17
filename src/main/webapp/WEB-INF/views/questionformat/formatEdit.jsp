
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/info.jsp" %>

<html>
<head>
	<title>
	<spring:message code="QuestionEdit" text="QuestionEdit"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$("#qsnUpdateButton").hide();
		
		
		$("#submit").click(function(){

			
			var parameters = "houseType="+$("#selectedHouseType").val()
			 +"&deviceType="+$("#selectedDeviceType").val()
			 +"&qsnId="+$("#questionNumberField").val();
			 
			var resource = ''
			if( $("#selectedDeviceType").val() == 4 ||  $("#selectedDeviceType").val() == 5){
			resource='question/questionFormatView';
			}
			else if($("#selectedDeviceType").val() == 101 )
			{
				resource='motion/motionFormatView';
				$("#qsnUpdateButton").show();
			}
			
			
			
			 var resourceURL=resource+"?"+parameters;
			
			 $.get(resourceURL,function(data){
				
				 $("#bulkResultDiv").empty();
				
				 $("#bulkResultDiv").html(data);
				
				 $("#qsnUpdateButton").show();
			 },'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			
		
			});	
		
		
		$("#qsnSubmit").click(function(){
			var qsnId=new Array();
			$(".action").each(function(){
				if($(this).is(":checked")){
				qsnId.push($(this).attr("id").split("chk")[1]);
				
				}
			});
			
			
			if(qsnId.length<=0){
				$.prompt($("#selectItemsMsg").val());
				return false;	
			}
			
			 
			var items =new Array();
			var formaturl = '';
			if( $("#selectedDeviceType").val() == 4  ){
				for (var i=0; i<qsnId.length; i++) {
					console.log(i+"here")
				    items.push({'questionId':qsnId[i],'questionText':$(".questionText_"+qsnId[i]).get(0).value ,'revisedQuestionText':$(".revisedQuestionText_"+qsnId[i]).get(0).value ,
				    	'answer':$(".answer_"+qsnId[i]).get(0).value
				});
				}
				formaturl ='question/questionFormatUpdate';
				}
			
			else if( $("#selectedDeviceType").val() == 5  ){
				for (var i=0; i<qsnId.length; i++) {
					
				    items.push({'questionId':qsnId[i],'questionText':$(".questionText_"+qsnId[i]).get(0).value ,'revisedQuestionText':$(".revisedQuestionText_"+qsnId[i]).get(0).value ,
				    	'answer':$(".answer_"+qsnId[i]).get(0).value
				});
				}
				formaturl ='question/questionFormatUpdate';
				}

				else if($("#selectedDeviceType").val() == 101 )
				{
					for (var i=0; i<qsnId.length; i++) {
						console.log(i+"here")
					    items.push({'motionId':qsnId[i],'revisedSubject':$(".revisedSubject_"+qsnId[i]).val() ,'revisedDetails':$(".revisedDetails_"+qsnId[i]).val() ,
					    	'subject':$(".subject_"+qsnId[i]).val()
					});
					} 
					formaturl ='motion/motionFormatUpdate';
				} 
		

			  $.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post(''+formaturl+'?loadIt=yes',
				        	{items:items,
						    itemsLength:items.length,
						    houseType:$("#selectedHouseType").val()
							 ,deviceType:$("#selectedDeviceType").val()
							 ,qsnId: $("#questionNumberField").val()			 	
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
	
	function submit() {
		alert("working");
	}
</script>
<script type="text/javascript" src="./resources/js/common.js?v=3050"></script>
<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
<script type="text/javascript">
	     
	</script>
</head>
<body>
<div class="clearfix tabbar">
<ul class="tabs">
				<li>
					
				</li>			
			</ul>
<div class="tabContent clearfix">
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>



<label style="width:150px;height: 25px;"  class="small"><spring:message
						code="question.houseType" text="Name" /> : </label>
<%-- <select name="selectedHouseType" id="selectedHouseType" style="width:150px;height: 25px;">			
				<c:forEach items="${housetypes}" var="i">
					<c:choose>
						<c:when test="${houseType==i.type}">
							<option value="${i.name}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> --%>
			
<select id="selectedHouseType">
  <c:forEach items="${housetypes}" var="i" varStatus="loop">
    <option value="${i.name}">
        ${i.name}
    </option>
  </c:forEach>
</select>
<label style="width:150px;height: 25px;"  class="small"> | </label>
<%-- <label style="width:150px;height: 25px;"  class="small"><spring:message
						code="question.sessionType" text="Name" /> : </label>
			<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">				
							<c:forEach items="${sessionTypes}" var="i">
								<c:choose>
									<c:when test="${sessionType==i.id}">
										<option value="${i.sessionType}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
									</c:when>
									<c:otherwise>
										<option value="${i.sessionType}"><c:out value="${i.sessionType}"></c:out></option>			
									</c:otherwise>
								</c:choose>			
							</c:forEach> 
						</select>
<label style="width:150px;height: 25px;"  class="small"> | </label> --%>
<label style="width:150px;height: 25px;"  class="small"><spring:message
						code="device.deviceType" text="Name" /> : </label>						
						
						<select name="selectedDeviceType" id="selectedDeviceType" style="width:150px;height: 25px;">				
							<c:forEach items="${devices}" var="i">
								<c:choose>
									<c:when test="${device==i.id}">
										<option value="${i.name}" selected="selected"><c:out value="${i.name}"></c:out></option>				
									</c:when>
									<c:otherwise>
										<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
									</c:otherwise>
								</c:choose>			
							</c:forEach> 
						</select>

<br>
<br>
<label style="width:150px;height: 25px;"  class="small"><spring:message
						code="question.number" text="Name" />  </label>
						<label style="width:150px;height: 25px;"  class="small">:   </label>
						
<textarea id="questionNumberField" >
</textarea> 
 


<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">

		
							
						
					
<div id="bulkResultDiv">
</div>
	<div id="qsnUpdateButton">
						<h2></h2>
						<p class="tright">
							<input id="qsnSubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
</div>					
</div>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input id="submissionMsg" value="<spring:message code='client.prompt.submitEn' text='Do you want to submit the changes'></spring:message>" type="hidden">
</body>
</html>