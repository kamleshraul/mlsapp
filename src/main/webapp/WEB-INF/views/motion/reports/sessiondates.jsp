<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="motion.list" text="List Of Motions"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
 <script type="text/javascript">
 $(document).ready(function(){  
	 
         $('#motionList').hide();	 
		
		 $('#selectMotionDate').click(function(){
		   fetchMotions();		
		   $.fancybox.close();
		});	
		 
		 
			 $('#selectSessionDate').change(function(){ 
				 if($('#selectOrderType').val() != '-' && $('#selectSessionDate').val() != '-'){
     				 $('#sessionDateError').empty();
				 }
				 if($('#selectOrderType').val() != null && $('#selectOrderType').val() != '-'){
				 	 
				 $('#loading').html('<img src="./resources/images/waitAnimated.gif" style=" margin-left:10px; width:58px;height:58px;">'); 
					$.ajax({url: 'ref/orderoftheday/getSessionCreation', 
						data: {						
							discusssionDateId:$("#selectSessionDate").val(),
							 isRegularSitting:$('#selectOrderType').val()
						},
						type: 'GET',
						async: false,
						success: function(data) {
							$('#sessionCreated').val(data);
							/* $('html').animate({scrollTop:0}, 'slow');
		  				 	$('body').animate({scrollTop:0}, 'slow');	 */
		  				 	$('#loading').empty();
							if(data === false && ($('#selectOrderType').val() != '-' || $('#selectSessionDate').val() != '-')){
							   $('#motionList').show();					
							} else {
								$('#motionList').hide();	
							}					
						},
						error: function(data) {
							$('#loading').empty();
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.");
							}
						}
					});		
				 }
				 else {
					 $('#orderTypeError').html("<h3 style='color:red;'><spring:message code='please.select' text='Please Select Order Type'/></h3>"); 
				 }
			 });		 
		
		 
		 
			 $('#selectOrderType').change(function(){
				 if($('#selectOrderType').val() != '-' && $('#selectSessionDate').val() != '-'){					 
				   $('#orderTypeError').empty();
				 }
				 if($('#selectSessionDate').val() != null && $('#selectSessionDate').val() != '-'){	
					 
					 $('#loading').html('<img src="./resources/images/waitAnimated.gif" style=" margin-left:10px; width:58px;height:58px;">'); 
						$.ajax({url: 'ref/orderoftheday/getSessionCreation', 
							data: {						
								discusssionDateId:$("#selectSessionDate").val(),
								 isRegularSitting:$('#selectOrderType').val()
							},
							type: 'GET',
							async: false,
							success: function(data) {
								$('#sessionCreated').val(data);
								/* $('html').animate({scrollTop:0}, 'slow');
			  				 	$('body').animate({scrollTop:0}, 'slow');	 */
			  				 	$('#loading').empty();
								if(data === false && ($('#selectOrderType').val() != '-' || $('#selectSessionDate').val() != '-')){
								   $('#motionList').show();					
								} else {
									$('#motionList').hide();	
								}					
							},
							error: function(data) {
								$('#loading').empty();
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.");
								}
							}
						});
					 
				 
				 } else {
					 $('#sessionDateError').html("<h3 style='color:red;'><spring:message code='please.select' text='Please Select Session Date'/></h3>"); 
				 }
				 
			 });
			
		 		
	});
	
	function fetchMotions(){
		
		var params = "sessionId="+$('#sessionId').val()+
					 "&locale="+$('#moduleLocale').val()+
					 "&discussionDate="+$('#selectSessionDate').val()+
					 "&ugparam="+$('#ugparam').val()+
					 "&motionList="+$('#motionno').val()+
					 "&sessionCreated="+$('#sessionCreated').val()+
					 "&report=MOTION_ORDER_OF_THE_DAY&reportout=admitted_motions_order_of_the_day"+
					 "&isRegularSitting="+$('#selectOrderType').val();		
		showTabByIdAndUrl("details_tab","motion/report/sessiondates/orderoftheday/motions?"+params);
	}
 </script>
</head>
<body>
 <form:form>
   <p>
	   <spring:message code='motion.orderoftheday.selectDate' text='Please Select Date'/> :
	   <select id="selectSessionDate" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>
		  <c:forEach items="${sessionDates}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		  </c:forEach>
		</select>
   </p>
   <div id="sessionDateError"></div>
   <p>
      <spring:message code='motion.orderoftheday.ordertype' text='Please Select Order Type'/> :
	   <select id="selectOrderType" class="sSelect">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<option value="true"><spring:message code='orderoftheday.regularsitting' text='Regular Sitting'/></option>    
			<option value="false"><spring:message code='orderoftheday.specialsitting' text='Special Sitting'/></option>
	   </select>
   </p>
    <div id="orderTypeError"></div>
   <div id="loading">
				
   </div>
   <p id="motionList">
   	  <spring:message code='motion.orderoftheday.motionno' text='Please Mention Motion No:'/> :
      <textarea name="motionno" id="motionno" class="sTextarea"></textarea>
   </p>
 <center><input id="selectMotionDate" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef"> </center>
<input id="sessionId" name="sessionId" value="${sessionId}" type="hidden"/>
<input id="sessionCreated" name="sessionCreated" type="hidden"/>
<input id="ugparam" name="ugparam" value="${ugparam}" type="hidden"/>
</form:form>  
</body>
</html>