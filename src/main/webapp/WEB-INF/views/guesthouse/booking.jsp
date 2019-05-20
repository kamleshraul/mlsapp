<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title><spring:message code="guesthouse.guesthousebooking" text="Guest House Booking"/></title>
	    <!-- BEGIN META -->

	    <!-- END META -->
	    <!-- BEGIN STYLESHEETS -->

	    <link type="text/css" rel="stylesheet" href="./resources/css/theme-default/libs/fullcalendar/fullcalendar.css">
	    <link rel="stylesheet" href="./resources/css/template.css">
	    <link rel="icon" type="image/png" href="./resources/images/mlsicon.png" />
	    <script src="./resources/js/libs/jquery/jquery-1.11.2.min.js?v=2"></script>
	    <script src="./resources/js/libs/jquery/jquery-migrate-1.2.1.min.js?v=1"></script>
	    <script src="./resources/js/libs/moment/moment.min.js"></script>
	    <script src="./resources/js/libs/fullcalendar/fullcalendar.js"></script>
	     <script src="./resources/js/libs/fullcalendar/lang/mr.js"></script>
	    <!-- <script src="./resources/js/core/demo/DemoCalendar.js?v=3"></script> -->

	        <script src="./resources/js/libs/daterangepicker/daterangepicker.min.js"></script>
		<link rel="stylesheet" href="./resources/css/daterangepicker.css" />
		
		
		
	      <script type="text/javascript">
	      
	
	      
		   	$(document).ready(function() {
		   		
		   		$('#update_success_div').hide();		
		   		
		   		function convertDate(d){
		   		 var parts = d.split(" ");
		   		 var months = {Jan: "01",Feb: "02",Mar: "03",Apr: "04",May: "05",Jun: "06",Jul: "07",Aug: "08",Sep: "09",Oct: "10",Nov: "11",Dec: "12"};
		   		 return parts[2]+"/"+months[parts[1]]+"/"+parts[3];
		   		}
		   		
		   	   	$(function() {
				   	  $('input[name="daterange"]').daterangepicker({
				   	    opens: 'left',
				   	 minDate: $('#mindate').val(), 
				   	maxDate: $('#maxdate').val(),
				   	startDate:$('#mindate').val(),
				   	endDate: $('#mindate').val()
				   	
				   	  }, function(start, end, label) {

			
				   		days = (end- start) / (1000 * 60 * 60 * 24);
				       if(Math.round(days)>3){	
				    	
				    	 
				        	   alert("Not Allowed to book more than 3 days");
				        	   document.getElementById("submit").setAttribute("type", "hidden"); 
				    		   } else
				    	   {
				    			   document.getElementById("submit").setAttribute("type", "submit"); 
					   		$('#fromDate').val(convertDate(start.toLocaleString('en-US')));
					   		$('#toDate').val(convertDate(end.toLocaleString('en-US')));
				    	   }
				   	   
				   	  });
				   	});
				   	
		   	 document.getElementById("daterange").setAttribute("value", "hidden"); 
	   		
		   		/**** Calendar Initialization ****/
		   		$('#calendar').fullCalendar({
		   			height: 500,
		   			locale: 'mr',
		   			header :{
		   			    left:'',
		   			    center: 'title',
		   			    right: 'today prev,next',
		   			
		   			    
		   			},
		   		    eventClick: function(event) {
		   		        if (event.url) {
		   		            window.open(event.url);
		   		            return false;
		   		        }
		   		    }
		   		
		   		});
		   		
 		
		   		
		   		$("#guesthouse").change(function(){
		   	
		   			getAvailableRooms();
		   			loadBookingDetails();
		   		});
		   		
		   		$("#daterange").change(function(){
				 	getAvailableRooms();
		   			loadBookingDetails();
		   			
		   		});
		   		
   		
		   		
		   		if($("#currentHouseType").val()=='lowerhouse'){
		   			$(".deviceCount").css("color","#406b03");
		   			//$(".sessionBar").css("background-color","#406b03");
		   		}else if($("#currentHouseType").val()=='upperhouse'){
		   			$(".deviceCount").css("color","#ac031f");
		   			//$(".sessionBar").css("background-color","#ac031f");
		   		} 
		   	});
		   	
		
		
			function getAvailableRooms() {
			
				if($("#fromDate").val()!=''){
			
				$.get('guesthouse/getAvailableRooms?' +
						'guesthouse=' + $("#guesthouse").val() + 
						'&fromDate='+ $("#fromDate").val() + 
						'&toDate='+$("#toDate").val(),function(data){
	   				if(data != null && data.length>0){
					var dataLength = data.length;
					
						var text = "";
						for(var i = 0; i < dataLength; i++) {
							text += "<option value='" + data[i].number + "'>" + data[i].name + "</option>";
						}
						
						$('#guesthouserooms').empty();
						$('#guesthouserooms').html(text);
					}
					else {
						$('#guesthouserooms').empty();
					}
				});
				}
			}
			
		   	function loadBookingDetails(){
		   			$.get("guesthouse/bookingDetails?guesthouse="+$("#guesthouse").val(), function(data){
	   				if(data != null && data.length>0){
	   					$('#calendar').fullCalendar( 'removeEvents');
	   					$('#calendar').fullCalendar('gotoDate', data[0].formattedNumber);
	   					for(var i=0;i<data.length;i++){
	   						var evento = $("#calendar").fullCalendar('clientEvents', data[i].id);
	   						if(evento == null || evento == ''){
	   							var newEvent = new Object();
	   	   						newEvent.id = data[i].id;
	   	   						newEvent.title = data[i].name + ' Room Number:' + data[i].number;
	   	   			   			newEvent.start = data[i].formattedNumber;
	   	 
	   	   			   			newEvent.end = data[i].formattedOrder+'T23:59:00';
	   	   			   			newEvent.allDay = false;
	   	   			   	displayEventTime: false;
	   	   			   	 		isUserCreated: true;
	   	   			   			
	   	   			   	$('#mindate').val(data[i].type)
	   	   			   	$('#maxdate').val(data[i].displayName)
	   	   			   			/* if(data[i].type == 'Order of the Day'){
	   	   			   				if($("#houseType").val()=='lowerhouse'){
	   	   			   					newEvent.backgroundColor = "#FFFFCC";
	   	   			   				}else{
	   	   			   					newEvent.backgroundColor = "#830303";
	   	   			   				}
	   	   			   				newEvent.textColor = "#DAF7A6";
	   	   			   			}else if(data[i].type == 'Suchi'){
			   	   			   		if($("#houseType").val()=='lowerhouse'){
			  			   				newEvent.backgroundColor = "#FFFFCC";
			  			   			 }else{
			  			   				newEvent.backgroundColor = "#a70202";
			  			   			 }
	   	   			   				
	   	   			   				newEvent.textColor = "#DAF7A6";
	   	   			   			}else if(data[i].type == 'Yaadi'){
			   	   			   		if($("#houseType").val()=='lowerhouse'){
			  			   				newEvent.backgroundColor = "#FFFFCC";
			  			   			 }else{
			  			   				newEvent.backgroundColor = "#f80606";
			  			   			 }
	   	   			   				
	   	   			   				newEvent.textColor = "#DAF7A6";
	   	   			   			}else{
			   	   			   		if($("#houseType").val()=='lowerhouse'){
			  			   				newEvent.backgroundColor = "#FFFFCC";
			  			   			 }else{
			  			   				newEvent.backgroundColor = " #00FF7F";
			  			   			 }
	   	   			   				newEvent.textColor = "#DAF7A6";
	   	   			   			}   */
	   	   			   			$('#calendar').fullCalendar( 'renderEvent', newEvent, true);
	   			   			
	   						}
	   					}
	   					
	   					if($("#houseType").val()=='lowerhouse'){
	  			   			$(".fc-event").css("color","#406b03");
	  			   			$(".fc-event").css("border-left","5px solid #406b03");
	  			   			$(".fc-event").css("background-color","white");
	   					}else{
	   						$(".fc-event").css("color","#ac031f");
	  			   			$(".fc-event").css("border-left","5px solid #ac031f");
	  			   			$(".fc-event").css("background-color","white");
	   					}
	   				}else{
	   				

	   					//Remove events with ids of non usercreated events
	   					$('#calendar').fullCalendar( 'removeEvents');
	   					return false;
	   				}
	   			});
		   	}
	    </script>
	    <style type="text/css">
	    
	    	.card-body .alert-callout{
	    		min-height:105px !important;
	    	}
	    	
	    	.fc-event{
	    		font-weight:bold;
	    		border-left-color: green ;
	    		background-color: white;
	    	}
	    </style>
	</head>
	<body>		
			<div class="clearfix tabbar">
			<ul class="tabs">
				<li>
					<a id="details_tab" href="#" class="tab selected">
					   <spring:message code="generic.details" text="Details"></spring:message>
					</a>
				</li>			
			</ul>
			<div class="tabContent clearfix">
				<p id="error_p" style="display: none;">&nbsp;</p>
				<c:if test="${(error!='') && (error!=null)}">
					<h4 style="color: #FF0000;">${error}</h4>
				</c:if>
				<div class="fields">
				<form action="guesthouse/booking" method="POST">
					<%@ include file="/common/info.jsp" %>
					
			
							<p>
					<label class="small"><spring:message code="guesthouse.selectdates" text="Select Dates"/>*:</label>
				<input type="text"  id="daterange" name="daterange" value="" />	
				</p>
				

				
						<p>
					<label class="small"><spring:message code="guesthouse.guesthouselocation" text="GuestHouse"/>*</label>
					<select name="guesthouse" id="guesthouse" class="sSelect">
						<c:forEach items="${guesthouses}" var="i">
							
									<option value="${i.id}" >${i.location}</option>
								
						</c:forEach>
					</select>
					</p>
			
				<p>
						<label class="small"><spring:message code="guesthouse.roomnumber" text="Room Number"/>*:</label>
	<select name="guesthouserooms" id="guesthouserooms" class="sSelect">
						<c:forEach items="${availablerooms}" var="i">
							
									<option value="${i.number}" >${i.name}</option>
								
						</c:forEach>
					</select>
					</p>
					
					<div class="fields">
		<h2></h2>	
		<p class="tleft">
			<input id="submit" name="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">

		</p>
	</div>	
				 <div class="card calender">
                                <div class="card-body no-padding">
                                    <div id="calendar"></div>
                                </div>
          		</div>
                         
				
					<input type="hidden" id="mindate" name="mindate" value="${mindate}">
					<input type="hidden" id="maxdate" name="maxdate" value="${maxdate}">
					<input type="hidden" id="username" name="username" value="${username}">		
					<input type="hidden" id="member" name="member" value="${member}">		
					<input type="hidden" id="fromDate" name="fromDate" value="${fromDate}">	
					<input type="hidden" id="toDate" name="toDate" value="${toDate}">		
					
				</form>	
				</div>
		
			<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">			
		</div>		
	</body>
</html>