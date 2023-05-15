<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.report.party" text="Party Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
		
			
			
			loadPartyReport()
			
			  $("#filter").change(function(){
			    	    
			    	    if($("#filter").val() == "1")
			    	    	{
			    	    	var params = "sessionId="+$("#sessionId").val()
			    			+"&deviceTypeId="+$("#deviceTypeId").val()
			    			+"&partyId="+$("#partyId").val()
			    			+"&locale="+$("#locale").val()
			    			+"&statusId="+$("#statusId").val()
			    			+"&report=MOIS_PARTY_WISE_SEND_TO_DEPT_REPORT&reportout=motionPartyReport"
			    			
			    			$.get('motion/report/motion/genreport?'+ params,function(data1){			    					
			    					$('#reportWindow1').empty();
			    					$('#reportWindow1').html(data1);
			    		 	}).fail(function()
			    		 		{				 
			    		 		});
			    	    	}
			    	    else{
			    	    	loadPartyReport()
			    	    	}
			    });		
			
			
			
			
		});
		
		
		function loadPartyReport(){
			var params = "sessionId="+$("#sessionId").val()
			+"&deviceTypeId="+$("#deviceTypeId").val()
			+"&partyId="+$("#partyId").val()
			+"&locale="+$("#locale").val()
			+"&statusId="+$("#statusId").val()
			+"&report=MOIS_PARTY_WISE_REPORT&reportout=motionPartyReport"
			
			 $.get('motion/report/motion/genreport?'+ params,function(data1){
					
					$('#reportWindow1').empty();
					$('#reportWindow1').html(data1);
		 }).fail(function(){				 
		 });
		}
		
		
	</script>
	 <style type="text/css">
      
    </style>
</head> 

<body>

<select id="filter">
    <option value="0">All</option>
    <option value="1">Sent to Dept</option>
</select>

 <hr>
 
  <div id="reportWindow1">
  </div>




	
	<input type="hidden" name="sessionId" id="sessionId" value="${sessionId }">
	<input type="hidden" name="deviceTypeId" id="deviceTypeId" value="${deviceTypeId }">
	<input type="hidden" name="partyId" id="partyId" value="${partyId }">
	<input type="hidden" name="locale" id="locale" value="${locale }">
	<input type="hidden" name="statusId" id="statusId" value="${statusId}">
	
</body>
</html>