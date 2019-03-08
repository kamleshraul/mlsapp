
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<title><spring:message code="home.title" text="ELS - Home"/></title>
	<!-- BEGIN META -->
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="keywords" content="your,keywords">
	<meta name="description" content="Short explanation about this website">
	<!-- END META -->
	<!-- BEGIN STYLESHEETS -->
	<link href='http://fonts.googleapis.com/css?family=Roboto:300italic,400italic,300,400,500,700,900' rel='stylesheet' type='text/css' />
	<link type="text/css" rel="stylesheet" href="./resources/css/theme-default/bootstrap.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/theme-default/materialadmin.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/theme-default/font-awesome.min.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/theme-default/material-design-iconic-font.min.css" />
	<link type="text/css" rel="stylesheet" href="./resources/css/theme-default/libs/fullcalendar/fullcalendar.css">
	<link rel="stylesheet" href="./resources/css/template.css">
	<!-- <link rel="stylesheet" href="./resources/css/fastsearch.css"> -->
	
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<!-- <script src="./resources/js/libs/jquery/jquery-1.11.2.min.js?v=2"></script> -->
	<script src="./resources/js/libs/jquery/jquery-migrate-1.2.1.min.js?v=1"></script>
	<!-- <script src="./resources/js/libs/jquery/jquery-fastsearch.js?v=1"></script> -->
	<script src="./resources/js/libs/moment/moment.min.js"></script>
	<script type="text/javascript" src="./resources/js/jquery/blockUI.js?v=5"></script>	
	<script type="text/javascript" src="./resources/js/common.js?v=3051"></script>
	
	<style type="text/css">
		body{background: #ffffff;
		}
		
		.border{
			border: 1px solid #bdd2ff;
			background-color: #bdd2ff;
		}
		
		.display-hidden{	display:hidden;
		}
		
		.task{	padding: 15px;
				text-align:center;
				border-radius:25px;	}
				
		.headerblock{	padding: 10px 15px;	
						height: 90px;
						line-height: 1;
						font-weight: bold;
						text-align: center;
						font-size: 25px;
					    font-family:kokila;
    					border-radius: 10px 10px 0 0;  }
    					
    	.dashboard-background{	 background-color: #6E6E6E;
    							 border-color: #6E6E6E; 
    							 color: #fff;	}
    							 					
    	.total-task{	text-align: center;
    					font-weight: bold;
    					font-size: 20px;	}
    	
    	.panel-default{	border-radius:10px;
    					background-color:#dddddd;	} 
    	
    	.margin-top{ margin-top:10px; }
    	
    	.border-bottom{ border-bottom:1px solid black; }
    	
    	.pending-background{	background-color: #2E6E8C;
    							border-color: #2E6E8C;
    							color: #fff;	}
    	
    	.fs-20{	font-size:20px;	}
    	
		 .zoom {
		    transition: transform .2s; /* Animation */
		}
		
		.zoom:hover {
		    transform: scale(1.5);
		}
		 .sub-zoom {
		    transition: transform .2s; /* Animation */
		}
		
		.sub-zoom:hover {
		    transform: scale(1.2);
		}
		.sabha-background{
			background-color:#4CA636;	}
			
		.parishad-background{
			background-color:#CB1818;	}
			
		.text-color{
			color: #ffffff;	}
		
		.device-tasks{
				padding: 10px;
				font-size: 15px;
				text-align: center;
				font-family:kokila;
				font-size:20px;
		}
	
		.table-headers{
			background: #7266ba;;
			color: #ffffff;
			border:1px solid #000000;
			font-family:kokila;
			font-size:20px;
		}
		.devicetable{
		
			margin: 10px auto;
		}
	
		.devicetable tr:nth-child(odd){
			background:   #dedce8;
		}
		
		.devicetable tr:nth-child(even){
			background:   #bbb3e5;
		}
		
		.device-name{
			margin-left: 80px;
			color: #bd200d;
			font-family:kokila;
			font-size:30px;
		}
		
		.device-title{
			color: #667292;
		}
			
	</style>


<script>
$(document).ready(function(){
	$("#search").click(function(){
		$("#department-count-filtered-dashboard").empty();
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		url = "?house_type="+$("#selectedHouseType").val()
				+"&session_type="+$("#selectedSessionType").val()
				+"&session_year="+$("#selectedSessionYear").val()
				+"&subdepartment="+$("#selectedSubdepartment").val()
				+"&device_type="+$("#selectedDeviceType").val();
		resourceURL = 'departmentdashboard/getDepartmentDeviceCounts'+url;
		$.get(resourceURL,function(data){
			var text = '';
			for(var i=0;i<data.length;i++){	
				text = text + '<div class="col-md-3 col-sm-4 col-xs-12" >'
					+ '<div class="panel panel-default">'
					+ '<div class="headerblock dashboard-background">'+data[i].subdepartment+'</div>'
					+ '<div class="panel-body">'
			  		+		'<div class="total-task" title="Total Task/s" >'+data[i].totalCount+'</div>'
			    	+ 		'<div class="col-md-12 col-sm-12 col-xs-12">'
  			  		+ '<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom">' 
			  		+ '<div id="'+data[i].subdepartment+'#pending" class="btn-danger task glyphicon glyphicon-envelope workflow-task"></div>'
			   		+ '<div class="fs-20 text-center">'+data[i].pendingCount+'</div>'
			   		+ '<h5 text-center><spring:message code="mytask.pending" text="Pending"/></h5>'
			   		+ '</div>'
			   		+ '<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom">'
			  	 	+ '<div id="'+data[i].subdepartment+'#completed" class="btn-success task glyphicon glyphicon-ok workflow-task"></div>'
			  	 	+ '<div class="fs-20 text-center">'+data[i].completedCount+'</div>'
			  	 	+ '<h5 text-center><spring:message code="mytask.completed" text="Completed"/></h5>'
			   		+ '</div>'
			   		+ '<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom">'
			 	 			+ '<div class="btn-warning task glyphicon glyphicon-hourglass "></div>'
			 	 			+ '<div class="fs-20 text-center">'+data[i].timeoutCount+'</div>'
			 	  			+ '<h5 class="text-center"><spring:message code="department-dashboard.timeout" text="Timeout"/></h5>'
			   		+ '	</div>'
				    +'</div>'
				    +'</div>'
					+'</div>'
					+'</div>'
				 	+'<input id="houseType" name="houseType" value="${i.houseType}" type="hidden"/>'	
					+'<input id="sessionType" name="sessionType" value="${i.sessionType}" type="hidden"/>'
					+'<input id="sessionYear" name="sessionYear" value="${i.sessionYear}" type="hidden"/>'
					+'<input id="deviceType" name="deviceType" value="${i.deviceType}" type="hidden"/>';
			}
			$("#back").show();
			$("#dashboard").hide();
			$("#device-workflow-dashboard").hide();
			$("#backtohousewise").hide();
			$("#department-count-filtered-dashboard").html(text);
			$("#department-count-filtered-dashboard").show();
			$("#workflow-dashboard").hide();
			 loadWorkflowTaskEvents(); 
			$.unblockUI();
		});
	});
	
	 /* Back Buttons */
	 $("#back").click(function(){
        $("#dashboard").show();
        $("#department-count-filtered-dashboard").hide();
        $("#backtohousewise").hide();
        $("#workflow-dashboard").hide();
        $("#back").hide();
     });
	 
	 $("#backtohousewise").click(function(){
	        $("#workflow-dashboard").show();
	        $("#backtohousewise").hide();
	        $("#back").show();
	        $("#device-workflow-dashboard").hide();
	 });

});
	 
function loadWorkflowTaskEvents(){
	$(".workflow-task").click(function(){
		//$("#department-count-filtered-dashboard").empty();
    	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
    	var id = this.id;
    	var ids = [] ;
    	ids = id.split("#"); 
    	url = "	?house_type="+$("#selectedHouseType").val() 
				+"&session_type="+$("#selectedSessionType").val()
				+"&session_year="+$("#selectedSessionYear").val()
	 			+"&subdepartment="+ids[0]  
				+"&device_type="+$("#selectedDeviceType").val()
    			+"&status="+ids[1];
    	resourceURL = 'departmentdashboard/getDepartmentDeviceCountsByHouseType'+url;
    	$.get(resourceURL,function(data){
    		if(data == ""){
    			alert('<spring:message code="department.dashboard.nodata" text="No Tasks"/>');
    		}
    		var text = '';
			for(var i=0;i<data.length;i++){	
				text = text +	'<div class="col-md-3 col-sm-12 col-xs-12">'+
								'<div class="panel panel-default margin-top">'+
			  						'<div class="headerblock pending-background"><span class="subdepartment-name">'+data[i].subdepartment+'</span><br><br>'+data[i].sessionType+' '+data[i].sessionYear+'</div>'+
			  							'<div class="col-md-12 col-sm-6 col-xs-12 border">'+
				  							'<div class="panel-body">'+
				  								'<div class="col-md-6 col-sm-3 col-xs-6">'+
				  									/* This is assembly count */
				    								'<div id="'+data[i].subdepartment+"#"+data[i].sessionType+"#"+data[i].sessionYear+"#"+ids[1]+'#<spring:message code="generic.lowerhouse" text="vidhansabha"/>" class="task sabha-background sub-zoom assembly-workflow"><span class="text-color"><spring:message code="generic.lowerhouse" text="vidhansabha"/></span><br><span class="text-color">'+data[i].assemblyCount+'</span></div>'+
				    							'</div>'+
				    							'<div class="col-md-6 col-sm-3 col-xs-6">'+
				    								/* This is council count */
				    							 	'<div id="'+data[i].subdepartment+"#"+data[i].sessionType+"#"+data[i].sessionYear+"#"+ids[1]+'#<spring:message code="generic.upperhouse" text="vidhanparishad"/>" class="task parishad-background sub-zoom assembly-workflow"><span class="text-color"><spring:message code="generic.upperhouse" text="vidhanparishad"/></span><br><span class="text-color">'+data[i].councilCount+'</span></div>'+
				    							'</div>'+
				  							'</div>'+
			  							'</div>'+
									'</div>'+
								'</div>';
				}	
			
			$("#dashboard").hide();
			$("#device-workflow-dashboard").hide();
			$("#back").show();
			$("#backtohousewise").hide();
			$("#workflow-dashboard").html(text);
			$("#workflow-dashboard").show();
			$("#department-count-filtered-dashboard").hide();
			$.unblockUI();
			
			$(".assembly-workflow").click(function(){	
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var id = this.id;
				var ids = [] ;
		    	ids = id.split("#"); 
		    	url = "?subdepartment="+ids[0]+"&session_type="+ids[1]+"&session_year="+ids[2]+"&status="+ids[3]+"&house_type="+ids[4]+"&device_type="+$("#selectedDeviceType").val();
		    	resourceURL = 'departmentdashboard/getDepartmentDeviceCountsByDeviceType'+url;
		    	/* condition to display marathi status */
		    	if((ids[3])=="completed"){
					var status = $("#completedstatus").val();
				}
				else if((ids[3])=="pending"){
					var status = $("#pendingdstatus").val();
				}
		    	
		    	$.get(resourceURL,function(data){
		    		var text =	'<div class="total-task device-title">'+$("#selectedHouseType").val()+' '+ids[1]+' '+ids[2]+'</div>'+
		    					'<div class="total-task device-title">'+'<spring:message code="subdepartment" text="subdepartment"/>'+':'+' '+ids[0]+'</div>'+
		    					'<div class="total-task device-title">'+'<spring:message code="mytask.status" text="status"/>'+':'+' '+status+'</div></br>';

		    					for(var i=0;i<data.length;i++){
		    							text = text +'<div><table border="1" class="col-md-12 col-sm-12 col-xs-12 devicetable table">'+
		    							'<tr>'+
    								    '<th class="col-md-1 col-sm-1 col-xs-1 device-tasks table-headers">'+'<spring:message code="part.deviceNo" text="DeviceNumber"/>'+'</th>'+
    								    '<th class="col-md-3 col-sm-3 col-xs-2 device-tasks table-headers">'+'<spring:message code="mytask.assignee" text="Assignee"/>'+'</th>'+
    								    '<th class="col-md-3 col-sm-3 col-xs-2 device-tasks table-headers">'+'<spring:message code="mytask.assignmentTime" text="Assignment Time"/>'+'</th>'+
    								    '<th class="col-md-5 col-sm-5 col-xs-7 device-tasks table-headers">'+'<spring:message code="question.subject" text="subject"/>'+'</th>'+
    								    '</tr>'+'<span class="device-name">'+data[i].deviceType+'</span><br>';	

		    							for(var j=i;j<data.length;j++){
			    							if(data[i].type == data[j].type){
												text = text + '<tr>'+
													   '<td class="col-md-1 col-sm-1 col-xs-1 device-tasks">'+data[j].deviceNumber+'</td>'+
		    	    	    						   '<td class="col-md-3 col-sm-3 col-xs-2 device-tasks">'+data[j].assignee+'</td>'+
		    	    	    						   '<td class="col-md-3 col-sm-3 col-xs-2 device-tasks">'+data[j].assignmentTime+'</td>'+
		    	    	    						   '<td class="col-md-5 col-sm-5 col-xs-7 device-tasks">'+data[j].subject+'</td>'+
		    	    	    						   '</tr>'; 
		    	    	    							i=j;
		    	    	    				} 
			    							else{
					    						i = j;
					    						text = text+'</table></div><br><br>';
					    						break;
		    								}
		    							}
		    					} 
		    			$("#workflow-dashboard").hide();
		    			$("#back").hide();
		    			$("#backtohousewise").show();
		    			$("#device-workflow-dashboard").html(text);
		    			$("#device-workflow-dashboard").show();
		    			$.unblockUI();
		    		}); 
				});
		      });
			});
	}
</script>
</head>

<body>

 <!-- BEGIN HEADER-->
    <header id="header">
        <div class="headerbar">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="headerbar-left">
                <ul class="header-nav header-nav-options">
                    <li class="header-nav-brand">
                        <div class="brand-holder">
                            <a href="../../index.html">
                                <span class="text-l text-bold text-primary text-uppercase"><img rel="icon"  src="./resources/images/mlsicon.png" /><spring:message code="login.vidhanmandal" text="Maharashtra Vidhanbhavan"/></span>
                            </a>
                        </div>
                    </li>
                    <li>
                        <a class="btn btn-icon-toggle menubar-toggle" data-toggle="menubar" href="javascript:void(0);">
                            <i class="fa fa-bars"></i>
                        </a>
                    </li>
                </ul>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="headerbar-right">
                <ul class="header-nav header-nav-options">
                    <li>
                        <!-- Search form -->
                        <form class="navbar-search " role="search" style="display:none;">
                            <div class="form-group">
                                <input type="text" class="form-control" name="headerSearch" placeholder="Enter your keyword">
                            </div>
                            <button type="submit" class="btn btn-icon-toggle ink-reaction"><i class="fa fa-search"></i></button>
                        </form>
                    </li>
                    <li class="dropdown hidden-xs" style="display:none;">
                        <a href="javascript:void(0);" class="btn btn-icon-toggle btn-default" data-toggle="dropdown">
                            <i class="fa fa-bell"></i><sup class="badge style-danger">4</sup>
                        </a>
                        <ul class="dropdown-menu animation-expand">
                            <li class="dropdown-header">Today's messages</li>
                            <li>
                                <a class="alert alert-callout alert-warning" href="javascript:void(0);">
                                    <img class="pull-right img-circle dropdown-avatar" src="./resources/images/avatar2.jpg?1404026449" alt="" />
                                    <strong>Alex Anistor</strong>
                                    <br/>
                                    <small>Testing functionality...</small>
                                </a>
                            </li>
                            <li>
                                <a class="alert alert-callout alert-info" href="javascript:void(0);">
                                    <img class="pull-right img-circle dropdown-avatar" src="./resources/images/avatar3.jpg?1404026799" alt="" />
                                    <strong>Alicia Adell</strong>
                                    <br/>
                                    <small>Reviewing last changes...</small>
                                </a>
                            </li>
                            <li class="dropdown-header">Options</li>
                            <li><a href="../../html/pages/login.html">View all messages <span class="pull-right"><i class="fa fa-arrow-right"></i></span></a></li>
                            <li><a href="../../html/pages/login.html">Mark as read <span class="pull-right"><i class="fa fa-arrow-right"></i></span></a></li>
                        </ul>
                        <!--end .dropdown-menu -->
                    </li>
                    <li>
                        <a href="<c:url value='/j_spring_security_logout' />" class="btn btn-icon-toggle btn-default" >
                            <i class="fa fa-power-off"></i>
                        </a>
                    </li>
                    <!--end .dropdown -->
                </ul>
                <!--end .header-nav-options -->
                <!-- <ul class="header-nav header-nav-profile">
                    <li class="dropdown">
                        <a href="javascript:void(0);" class="dropdown-toggle ink-reaction" data-toggle="dropdown">
                            <img src="img/Chief_Minister_of_Maharashtra_Devendra_Fadnavis.jpg" alt="" />
                            <span class="profile-info">
                                    Admin
                                    <small>Administrator</small>
                                </span>
                        </a>
                        <ul class="dropdown-menu animation-dock">
                            <li class="dropdown-header">Config</li>
                            <li><a href="../../html/pages/profile.html">My profile</a></li>
                            <li><a href="../../html/pages/blog/post.html">My blog <span class="badge style-danger pull-right">16</span></a></li>
                            <li><a href="../../html/pages/calendar.html">My appointments</a></li>
                            <li class="divider"></li>
                            <li><a href="../../html/pages/locked.html"><i class="fa fa-fw fa-lock"></i> Lock</a></li>
                            <li><a href="index.html"><i class="fa fa-fw fa-power-off text-danger"></i> Logout</a></li>
                        </ul>
                    </li>
                </ul> -->
                <!--end .header-nav-profile -->
            </div>
            <!--end #header-navbar-collapse -->
        </div>
    </header>
    <!-- END HEADER-->
    



	
    <nav class="navbar navbar-default" <!-- style="background-color: #e2e2ff;" -->>
		<div class="navbar-header">
	        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
		       <span class="sr-only">Toggle navigation</span>
		       <span class="icon-bar"></span>
		       <span class="icon-bar"></span>
		       <span class="icon-bar"></span>
	      	</button>
	      	<a class="navbar-brand" href="#"><b>LOGO</b></a>
    	 </div>
    	 
		 <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      		<ul class="nav navbar-nav navbar-right">
			<a class="glyphicon glyphicon-off" href="#" aria-hidden="true" style="margin-top: 15px;margin-right: 15px;text-decoration:strong;"></a>
      		</ul>
   		</div><!-- /.navbar-collapse -->
	</nav>

	<div id="carouselExampleSlidesOnly" class="carousel slide" data-ride="carousel">
	  <div class="carousel-inner">
	    <div class="carousel-item active">
	      <img class="d-block w-100" src="./resources/images/vidhan-sabha-banner.jpg" alt="First slide" style="width: 100%;height: 240px;margin-bottom: 20px;">
	    </div>
	  </div>
	</div>
	
	<!-- filter code -->
	<div class="col-md-12 col-sm-12 card" style="margin-top: 10px;" id="selectionDiv1">
    
</select>
		<div class="row" style="padding:10px 0px 0px 0px">
			<div class="col-md-4 col-sm-4">	
				<div class="col-md-4 ">
					<a href="#" id="select_houseType" class="butSim">
						<spring:message code="mytask.housetype" text="House Type"/>
					</a>
				</div>
				<div class="col-md-8">
					<select name="selectedHouseType" id="selectedHouseType" style="width:150px;height: 70px;" multiple>	
						<option value=""><spring:message code="client.prompt.selectForDropdown" text="----PleaseSelect----"/></option>		
						<c:forEach items="${houseTypes}" var="i">
							<option value="${i.name}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>
					</select> |	
				</div>
			</div>
			<div class="col-md-4 col-sm-4">	
				<div class="col-md-4 ">
					<a href="#" id="select_session_year" class="butSim">
						<spring:message code="mytask.sessionYear" text="Year"/>
					</a>
				</div>
				<div class="col-md-8">
					<select name="selectedSessionYear" id="selectedSessionYear" style="width:150px;height: 70px;" multiple>	
						<option value=""><spring:message code="client.prompt.selectForDropdown" text="----PleaseSelect----"/></option>			
						<c:forEach var="i" items="${years}">
							<option value="${i}"><c:out value="${i}"></c:out></option>
						</c:forEach> 
					</select> |	
				</div>
			</div>
			<div class="col-md-4 col-sm-4">		
				<div class="col-md-4 ">						
					<a href="#" id="select_sessionType" class="butSim">
						<spring:message code="mytask.sessionType" text="Session Type"/>
					</a>
				</div>
				<div class="col-md-8">
					<select name="selectedSessionType" id="selectedSessionType" style="width:150px;height: 70px;" multiple>	
						<option value="Please select"><spring:message code="client.prompt.selectForDropdown" text="----PleaseSelect----"/></option>			
						<c:forEach items="${sessionTypes}" var="i">
							<option value="${i.sessionType}"><c:out value="${i.sessionType}"></c:out></option>			
						</c:forEach> 
					</select> |
				</div>		
			</div>
		</div>
		<div class="row" style="padding:10px 0px 10px 0px">
			<div class="col-md-4 col-sm-4">	
				<div class="col-md-4 ">
					<a href="#" id="select_deviceType" class="butSim">
						<spring:message code="mytask.deviceType" text="Device Type"/>
					</a>
				</div>
				<div class="col-md-8 ">
					<select name="selectedDeviceType" id="selectedDeviceType" style="width:150px;height: 70px;" multiple>
						<option value=""><spring:message code="client.prompt.selectForDropdown" text="----PleaseSelect----"/></option>				
						<c:forEach items="${deviceTypes}" var="i">
							<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
						</c:forEach> 
					</select> |
				</div>
			</div>
			<div class="col-md-4 col-sm-4">	
				<div class="col-md-4 ">
					<a href="#" id="select_subdepartment" class="butSim">
						<spring:message code="department.list" text="Subdepartment"/>
					</a>
				</div>
				<div class="col-md-8 ">
					<select name="selectedSubdepartment" id="selectedSubdepartment" style="width:150px;height: 70px;" multiple>		
						<option value=""><spring:message code="client.prompt.selectForDropdown" text="----PleaseSelect----"/></option>		
						<c:forEach items="${subDepartments}" var="i">
							<option value="${i.name}"><c:out value="${i.name}"></c:out></option>			
						</c:forEach> 
					</select> |
				</div>
			</div>
			<div class="col-md-4 col-sm-4">
				<button class="btn-primary" id="search"><spring:message code="generic.search" text="Search"/></button>	
			</div>
		</div>
	</div>
	<br><br>
	
	<div id="dashboard" >
		<c:forEach items="${result}" var="i" >
				<div class="col-md-3 col-sm-4 col-xs-12" >
					<div class="panel panel-default">
						<!-- Name Of Subdepartment -->
	  					<div class="headerblock dashboard-background">${i.subdepartment}</div>
	  					<div class="panel-body">
	  			  			<!-- Total Count -->
	  			    	 	<div class="total-task" title="Total Task/s" >${i.totalCount}</div>
	  			    	
	  			   			<div class="col-md-12 col-sm-12 col-xs-12">
		  			   			<!-- Pending Count -->
		  			  			<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom"> 
					  				<div id="${i.subdepartment}#pending" class="btn-danger task glyphicon glyphicon-envelope workflow-task"></div>
					   				
					   				<div class="fs-20 text-center">${i.pendingCount}</div>
					   
					   				<h5 text-center><spring:message code="mytask.pending" text="Pending"/></h5>
					   			</div>
					   
					   			<!-- Completed count -->
					   			<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom">
					  	 			<div id="${i.subdepartment}#completed" class="btn-success task glyphicon glyphicon-ok workflow-task"></div>
					  	 			
					  	 			<div class="fs-20 text-center">${i.completedCount}</div>
					  	 			
					   	 			<h5 text-center><spring:message code="mytask.completed" text="Completed"/></h5>
					   			</div>
					   
					  		    <!-- Timeout count -->
					   			<div class="col-md-4 col-sm-4 col-xs-4 text-center zoom">
					 	 			<div class="btn-warning task glyphicon glyphicon-hourglass "></div>
					 	 			 
					 	  			<div class="fs-20 text-center">${i.timeoutCount}</div>
					 	  			
					   	  			<h5 class="text-center"><spring:message code="department-dashboard.timeout" text="Timeout"/></h5>
					   			</div>
						    </div>
						</div>
					</div>
				</div>
				<input id="houseType" name="houseType" value="${i.houseType}" type="hidden"/>	
				<input id="sessionType" name="sessionType" value="${i.sessionType}" type="hidden"/>	
				<input id="sessionYear" name="sessionYear" value="${i.sessionYear}" type="hidden"/>	
				<input id="deviceType" name="deviceType" value="${i.deviceType}" type="hidden"/> 
		</c:forEach>
	</div>


<button class="btn btn-primary" id="back" style="display:none;">Go Back</button>

<button class="btn btn-primary" id="backtohousewise" style="display:none;">Go Back</button>

<div id="department-count-filtered-dashboard" class="display-hidden"></div>

<div id="workflow-dashboard" class="display-hidden"></div>

<div id="device-workflow-dashboard" class="display-hidden"></div>

<input type="hidden" id="completedstatus" name="completedstatus" value="<spring:message code="mytask.completed" text="Completed"/>">
<input type="hidden" id="pendingdstatus" name="pendingdstatus" value="<spring:message code="mytask.pending" text="pending"/>">
<input type="hidden" id="lowerhouse" name="lowerhouse" value="<spring:message code="generic.lowerhouse" text="assembly"/>">
<input type="hidden" id="upperhouse" name="upperhouse" value="<spring:message code="generic.upperhouse" text="council"/>">

</body>
</html>

