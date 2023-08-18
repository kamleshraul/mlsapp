<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><spring:message code="guesthouse.guesthousebooking"
		text="Guest House Booking" /></title>
<!-- BEGIN META -->

<!-- END META -->
<!-- BEGIN STYLESHEETS -->

<link type="text/css" rel="stylesheet"
	href="./resources/css/theme-default/libs/fullcalendar/fullcalendar.css">
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
		   		scrollTop();
		   		$('#update_success_div').hide();
				$("#successDiv").hide();	
				$("#failedDiv").hide();
				

				loadBookingDetails();
		   		

		   		/**** Calendar Initialization ****/
		   		$('#calendar').fullCalendar({
		   			height: 500,
		   			locale: 'mr',
		   			header :{
		   			    left:'',
		   			    center: 'title',
		   			    right: 'today prev,next',
		   			
		   			    
		   			},
		   			
		   			eventMouseover: function(calEvent, jsEvent) { var tooltip = '<div class="tooltipevent" style="width:330px;height:100px;background:#aed0ea;position:absolute;z-index:10001;"> ' + calEvent.description + '</div>'; var $tool = $(tooltip).appendTo('body');
		   			$(this).mouseover(function(e) {
		   			    $(this).css('z-index', 10000);
		   			            $tool.fadeIn('500');
		   			            $tool.fadeTo('10', 1.9);
		   			}).mousemove(function(e) {
		   			    $tool.css('top', e.pageY + 10);
		   			    $tool.css('left', e.pageX + 20);
		   			});
		   			},
		   			eventMouseout: function(calEvent, jsEvent) {
		   			$(this).css('z-index', 8);
		   			$('.tooltipevent').remove();
		   			},
		   	
		   		    eventClick: function(event) {
		   		        if (event.title) {
		   		        alert(event.title);                 
		   		        }
		   		     
		   		    },
		   			
		   		 dayClick: function(date, jsEvent, view) { 
		   		

		   		

		   		    // change the day's background color just for fun
		   		    //$(this).css('background-color', 'blue'); 
		         }
		   		
		   		});

		   	});

		   	function loadBookingDetails(){
		   			$.get("admin/getEventsForCalendar", function(data){
		   			
		   			
		   			/*----------------*/
		   				  if(data != null && data.length>0){
		   					$('#calendar').fullCalendar( 'removeEvents');
		   					//console.log(data)
		   					//$('#calendar').fullCalendar('gotoDate', data[0].formattedNumber);
		   					//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		   					for(var i=0;i<data.length;i++){
		   						
		   							var newEvent = new Object();
		   	   						newEvent.id = data[i].id;
		   	   						var deviceTypeAndKey = data[i].paramterKey.split(",")
		   	   						newEvent.title = deviceTypeAndKey[0]+' || '+deviceTypeAndKey[1] +'';		   	   					
		   	   						var dateAndTime = data[i].parameterValue.split(",");
		   	   						

			   	   					if (dateAndTime.length > 1) {
			   	   					  var startDateAndTime = dateAndTime[0];
			   	   					  var endDateAndTime = dateAndTime[1];
	
			   	   					  var startDateAndTimeSplitted = startDateAndTime.split(" ");
			   	   					  if (startDateAndTimeSplitted[0].includes("/")) {
			   	   					    //console.log("/ Yes");
			   	   					    var formatDate = startDateAndTimeSplitted[0].split("/")
			   	   					    newEvent.description = 'config:- ' +deviceTypeAndKey[1] + '<br> Start Date :-  '+ startDateAndTime +  '<br> End Date :-' + endDateAndTime;
			   	   						newEvent.start = formatDate[2]+'-'+formatDate[1]+'-'+formatDate[0]+'T'+startDateAndTimeSplitted[1];
			   	   						newEvent.end = formatDate[2]+'-'+formatDate[1]+'-'+formatDate[0]+'T23:59:00';
	
			   	   					  } else {
			   	   					    //console.log(" - Yes");
			   	   					    newEvent.description = 'config:- ' +deviceTypeAndKey[1] + '<br> Start Date :-  '+startDateAndTime+'<br> End Date :-'+ endDateAndTime;
			   	   						newEvent.start = startDateAndTimeSplitted[0]+'T'+startDateAndTimeSplitted[1];
			   	   						newEvent.end = startDateAndTimeSplitted[0]+'T23:59:00';
			   	   					  }
			   	   					} else {
			   	   					  var startDateAndTime = dateAndTime[0];
			   	   					  var startDateAndTimeSplitted = startDateAndTime.split(" ");
			   	   					  if (startDateAndTimeSplitted[0].includes("/")) {
			   	   					    //console.log("/ Yes");
			   	   					    var formatDate = startDateAndTimeSplitted[0].split("/")
			   	   					    newEvent.description = 'config:- ' +deviceTypeAndKey[1] + '<br> Start Date :-  '+ startDateAndTime ;
			   	   						newEvent.start = formatDate[2]+'-'+formatDate[1]+'-'+formatDate[0]+'T'+startDateAndTime[1];
			   	   						newEvent.end = formatDate[2]+'-'+formatDate[1]+'-'+formatDate[0]+'T23:59:00';
	
			   	   					  } else {
			   	   					    //console.log(" - Yes");
			   	   					    newEvent.description = 'config:- ' +deviceTypeAndKey[1] + '<br> Start Date :-  '+startDateAndTime;
			   	   						newEvent.start = startDateAndTimeSplitted[0]+'T'+startDateAndTimeSplitted[1];
			   	   						newEvent.end = startDateAndTimeSplitted[0]+'T23:59:00';
			   	   					  }
			   	   					  
			   	   					}

		   	   						
		   	   			   			if(data[i].type == 'lowerhouse'){
		   	   			   				newEvent.color = '#09C116'
		   	   			   			}
		   	   			   			else{
		   	   			   				newEvent.color = '#EA2F06'
		   	   			   			}
		   	   			   			
		   	   			   			newEvent.allDay = false;
		   	   			   			displayEventTime: false;
		   	   			   	 		//isUserCreated: true;
		   	   			   					   	   			   
		   	   			   			$('#calendar').fullCalendar( 'renderEvent', newEvent, true);
		   	   			   
		   	   						//console.log(newEvent)		   						
		   					}
		   					
		   				}else{
		   				
		   					//Remove events with ids of non usercreated events
		   					$('#calendar').fullCalendar( 'removeEvents');
		   					return false;
		   				}  
		   			/*----------------*/
		   				//$.unblockUI();	
		   			});
		   	}
	    </script>
<style type="text/css">

.flex-container {
  display: flex;
  flex-wrap: nowrap;
  background-color: #66ccff;
}

.flex-container > div {
  background-color: #529900;
  width: 100px;
  margin: 10px;
  text-align: center;
  line-height: 50px;
  font-size: 20px;
}

#ip1 {
	border-radius: 18px;
	background: #529900;
	padding: 20px;
	width: 15px;
	height: 15px;
	display: inline-block;
}

#ip2 {
	border-radius: 18px;
	background: #666;
	padding: 20px;
	width: 15px;
	height: 15px;
	display: inline-block;
}

.card-body .alert-callout {
	min-height: 105px !important;
}

.fc-event {
	
}
</style>
</head>
<body>
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li><a id="details_tab" href="#" class="tab selected"> <spring:message
						code="generic.details" text="Details"></spring:message>
			</a></li>
		</ul>
		<div class="tabContent clearfix">
			<p id="error_p" style="display: none;">&nbsp;</p>
			<c:if test="${(error!='') && (error!=null)}">
				<h4 style="color: #FF0000;">${error}</h4>
			</c:if>
	
		<div class="toolTip tpGreen clearfix" id="successDiv" style="display:none;height:30px;">
				<p style="font-size: 12px;">
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="guesthouse.update_success" text="Data saved successfully."/>
				</p>
				<p></p>
		</div>
		<div class="toolTip tpRed clearfix" id="failedDiv" style="display:none;height:30px;">
				<p style="font-size: 12px;">
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="guesthouse.failed" text="Data Save Failed."/>
				</p>
				<p></p>
		</div>
		
			<div class="fields">
				<%@ include file="/common/info.jsp"%>
				
				<div class="card calender">
						<div class="card-body no-padding">
							<div id="calendar"></div>
						</div>
					</div>
			</div>

			
		</div>
</body>
</html>