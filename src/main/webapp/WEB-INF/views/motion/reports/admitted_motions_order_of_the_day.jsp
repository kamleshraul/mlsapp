<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
  <style>
    #motionReportContainer {
      justify-content:center;
    }
 /*    #search {
	  background-color: #04AA6D;
	  border: none;
	  color: white;
	  cursor : pointer;
	  border-radius: 8px;
	  text-align: center;
	  text-decoration: none;
	  display: inline-block;
	  font-size: 13px;
	  margin: 4px 2px;
	  height: 20px;
	} */
  </style>
  <script type="text/javascript">
  
	//this is for autosuggest
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}
  
     $(document).ready(function(){
    	 
            var currentDate = new Date();
			currentDate.setHours(0,0,0,0);
			var maxChartAnsweringDate = new Date($("#discussionDate").val());
			if(currentDate > maxChartAnsweringDate){
			   $("#submitMotionOrder").hide();
			  /*  $("#searchBoxDiv").hide(); */
			}  
		
    		$("#chkall").change(function(){
    			if($(this).is(":checked")){
    				$(".action").attr("checked","checked");	
    			}else{
    				$(".action").removeAttr("checked");
    			}
    		});	
    		
       		
    		$( ".autosuggest").autocomplete({
    			minLength:3,			
    			source:'ref/orderoftheday/getmemberministers?session='+$("#sessionId").val(),
    			select:function(event,ui){
    			 var selectedValue = ui.item.id;
     			 var closestRow = $(this).closest('tr');
       			 var rowId = closestRow.attr('id');
    			 var stripRowId = rowId.split("row")[1]
    	     	 $("#primaryMinister"+stripRowId).val(ui.item.id);
    		}	
    		});	
    		
    		$("#submitMotionOrder").click(function(){
    		    submitMotionsOrder();   		
    		});
    		
    		$('#motion_order_of_the_day__word').click(function() {
				$(this).attr('href', '#');
				var parameters = "sessionId="+$("#sessionId").val()
				 +"&isRegularSitting="+$("#isRegularSitting").val()
				 +"&discussionDateId="+$("#discussionDateId").val()
				 +"&ugparam="+$("#ugparam").val();	
				var resourceURL = 'motion/report/orderoftheday/motion/fop?'+ parameters;
				$(this).attr('href', resourceURL);
			});
    		
    		$('#motion_detail_report').click(function() {
			     $(this).attr('href', '#');
				 var parameters = "sessionId="+$("#sessionId").val()
				 +"&isRegularSitting="+$("#isRegularSitting").val()
				 +"&discussionDateId="+$("#discussionDateId").val()
				 +"&ugparam="+$("#ugparam").val();	
				var resourceURL = 'motion/report/fop?'+ parameters;
				$(this).attr('href', resourceURL);  
			});
    		
    		$('#search').click(function(){
    			addMotions();
    		});
    		
	    	 
     });
     
     function appendDataToColumn(data){
    	 
    	for(var i=0; i<data.length; i++){
    		
    	 var rowCount = $("#myTable tbody tr").length;

    	 var newRow = $("<tr></tr>");
    	 
    	 newRow.append("<td style='min-width:100px;' align='center' class='chk'><input type='checkbox' id=chk"+data[i].number+" name=chk"+data[i].number+" class='sCheck action' value='true' checked  style='margin-right: 10px;'></td>");
    	 
    	 newRow.append("<td style='width: 50px;' align='center'><b>" + (rowCount + 1) + "</b></td>");

    	 newRow.append("<td style='width: 70px;' align='center'><b>" + data[i].formattedNumber + "</b></td>");
    	  
    	 newRow.append("<td style='width: 140px;' align='center'><b>" + data[i].displayName + "<br/>("+data[i].name+")</b></td>");
    	 
    	 newRow.append("<td style='width: 500px; padding: 15px;' align='justify'>" + data[i].type + "<br/><br/>"+data[i].value+"</td>");
    	  
    	  $("#myTable tbody").append(newRow);
    	} 
    	 
     }
     
     function addMotions(){
    	 var file=$("#selectedFileCount").val();
    	 $.prompt($('#addMotionsMsg').val(),{
	 			buttons: {Ok:true, Cancel:false}, callback: function(v){
	 	        if(v){
	 	        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
	 	        	$.ajax({url: 'motion/report/orderoftheday/getMotions', 
	 					data: {						
	 						 motionList:$("#searchvalue").val(),
	 						 sessionId: $("#sessionId").val(),
	 						 discussionDate: $("#discussionDateId").val(),
	 						 ugparam: $("#ugparam").val(),
	 						 report: "MOTION_ORDER_OF_THE_DAY",
	 						 locale: "mr_IN",
	 						 file:file
	 					},
	 					type: 'GET',
	 					async: false,
	 					success: function(data) {	
	 						/* $('html').animate({scrollTop:0}, 'slow');
	        				 	$('body').animate({scrollTop:0}, 'slow'); */
	        				 	
        				 	$("html, body").animate({ scrollTop: $(document).height()-$(window).height() });
	     					console.log(data);
 	     					$.unblockUI(); 	
	     					appendDataToColumn(data);
	 					},
	 					error: function(data) {
	 						$.unblockUI();
	 						if($("#ErrorMsg").val()!=''){
	 							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	 						}else{
	 							$("#error_p").html("Error occured contact for support.");
	 						}
	 					}
	 				});
	 				
	 	        }}}); 
    	 
     }
     
     function submitMotionsOrder(){
    	var items=new Array();
 		$(".action").each(function(){
 			if($(this).is(":checked")){
 					items.push($(this).attr("id").split("chk")[1]);					
 				}			
 		});
 		
 		var motions = new Array();
 		if(items.length === 0){
 			alert("You have not selected any motions...");
 		}
 		else {
 			
 			for(var i=0; i<items.length; i++){
	 	       	   motions.push({'id':items[i]});
 			}	   
 			   	   var file=$("#selectedFileCount").val();
	 	    	  $.prompt($('#submissionMsg').val(),{
	 	 			buttons: {Ok:true, Cancel:false}, callback: function(v){
	 	 	        if(v){
	 	 	        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
	 	 	        	$.ajax({url: 'motion/report/motionOrderOftheDay/update', 
	 	 					data: {						
	 	 						 items:motions,
	 	 						 itemLength: motions.length,
	 	 						 discussionDateId:$("#discussionDateId").val(),
	 	 						 sessionId: $("#sessionId").val(),
	 	 						 locale: "mr_IN",
	 	 						 isRegularSitting: $("#isRegularSitting").val(),
	 	 						 ugparam: $('#ugparam').val(),
	 	 						 sessionCreated: $('#isCreated').val(),
	 	 						 report:"MOTION_ORDER_OF_THE_DAY",
	 	 						 reportout: "admitted_motions_order_of_the_day",
	 	 						 file:file
	 	 					},
	 	 					type: 'POST',
	 	 					async: false,
	 	 					success: function(data) {	
	 	 						$('html').animate({scrollTop:0}, 'slow');
	 	        				 	$('body').animate({scrollTop:0}, 'slow');	
	 	     					$.unblockUI();	
	 	     					$("#motionReportContainer").empty();	
	 	     					$("#motionReportContainer").html(data);
	 	 					},
	 	 					error: function(data) {
	 	 						$.unblockUI();
	 	 						if($("#ErrorMsg").val()!=''){
	 	 							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	 	 						}else{
	 	 							$("#error_p").html("Error occured contact for support.");
	 	 						}
	 	 					}
	 	 				});
	 	 				
	 	 	        }}});  
	 	  
 		
 		}
 		
     }
          
  </script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	
	<br/><br/>
	<%@ include file="/common/info.jsp" %>
<div id="motionReportContainer">
   <c:if test="${isCreated == 'true'}">
	    <div style="float:right;">
	    <a id="motion_detail_report" class="exportLink" href="#" style="text-decoration: none;">
	       <!-- <img src="./resources/images/word_new.png" alt="Export to WORD" width="32" height="32"> -->
	       Motion Detail Report
	    </a> &nbsp;&nbsp;&nbsp;
	   <a id="motion_order_of_the_day__word" class="exportLink" href="#" style="text-decoration: none;">
			<img src="./resources/images/word_new.png" alt="Export to WORD" width="32" height="32">
		</a>
	  </div>
  </c:if>
  <c:if test="${!(empty report)}">
  <center>	
	<h3><b>${topHeader[0]}</b></h3>
	<h4><b>${topHeader[1]}</b></h4>
	<h4><b>${topHeader[2]}</b></h4>
	<h4>
	    <c:choose>
	       <c:when test="${isRegularSitting == 'true'}">
               <spring:message code='orderoftheday.regularsitting' text='Regular Sitting'/>  	    	
	       </c:when>
	       <c:otherwise>
	           <spring:message code='orderoftheday.specialsitting' text='Special Sitting'/>
	       </c:otherwise>
	    </c:choose>
	</h4>
	<h4><b>${topHeader[3]}</b></h4>
  </center>
  <br/>
    <div id="searchBoxDiv">
     <center>
    	<table style="padding: 0px; margin: 0px;"> 
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:2px;">
				<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="search" value="Add Motion" style="border-style: none; width: 100px; height: 20px;">
			</td>
		 </tr>
		</table>
	  </center>
     </div> 
  <br/>
  <center>
   <table id="myTable" border="1">
     	<thead>
		   <tr>
		    <th style="min-width:100px;text-align:center;"><spring:message code="resolution.submitall" text="Submit All"></spring:message>
		      <input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true"></th>	
		      <th style="width: 50px;"><center><b>${colHeader[0]}</b></center></th>
		      <th style="width: 70px;"><center><b>${colHeader[1]}</b></center></th>
		      <th style="width: 100px;"><center><b>${colHeader[2]}</b></center></th>
		      <th style="width: 500px;"><center><b>${colHeader[3]}</b></center></th>
		      <%-- <th style="width: 140px;"><center><b>${colHeader[4]}</b></center></th> --%>
		   </tr>
		</thead>    
		<tbody>
		<c:set var="index" value="1"></c:set>
        <c:forEach items="${report}" var="r" varStatus="counter">
		   <tr id="row${r[16]}">
		     <td style="min-width:100px;" align="center" class="chk"><input type="checkbox" id="chk${r[16]}" name="chk${r[16]}" class="sCheck action" value="true" checked  style="margin-right: 10px;"></td>
			 <td style="width: 50px;" align="center">${counter.count}</td>
			 <td style="width: 70px;" align="center"><b>${r[7]}</td>
			 <td style="width: 140px;" align="center"><b>${r[2]}<br/>(${r[4]})</b></td>
			 <td style="width: 500px; padding: 15px;" align="justify"><b>${r[8]}</b><br/><br/>${r[9]}</td>
		    <%--  <td style="width: 140px; padding:30px;" align="center">
		         <input id="formattedMinister${r[16]}" name="formattedMinister${r[16]}" type="text" class="sText autosuggest"/>
		         <input name="primaryMinister${r[16]}" id="primaryMinister${r[16]}" type="hidden"/>
		     </td> --%>
		  </tr>    
		 <c:set var="index" value="${index+1}"></c:set> 
      </c:forEach>
       </tbody> 
      </table>
      </center>
      <br/>
      <center>
     	 <input id="submitMotionOrder" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
     </center>
    </c:if>
    <c:if test="${empty report}">
        <center>
           <h3><b><spring:message code='orderoftheday.nolist' text='No Notices Found'/></b></h3>
        </center>
    </c:if> 
</div>
    <input id="submissionMsg" value="<spring:message code='orderoftheday.client.prompt.submit' text='Do you want to create order of the day..?'></spring:message>" type="hidden">
    <input id="addMotionsMsg" value="<spring:message code='orderoftheday.client.prompt.submit' text='Do you want to add motions?'></spring:message>" type="hidden">
    <input id="sessionId" name="sessionId" value="${sessionId}" type="hidden">
	<input id="isRegularSitting" name="isRegularSitting" value="${isRegularSitting}" type="hidden"/>
	<input id="discussionDateId" name="discussionDateId" value="${discussionDateId}" type="hidden"/>
	<input id="discussionDate" name="discussionDate" value="${discussionDate}" type="hidden"/>
	<input id="formattedDiscussionDate" name="formattedDiscussionDate" value="${formattedDiscussionDate}" type="hidden"/>
	<input id="ugparam" name="ugparam" value="${ugparam}" type="hidden"/>
	<input id="isCreated" name="isCreated" value="${isCreated}" type="hidden"/>
</body>
</html>