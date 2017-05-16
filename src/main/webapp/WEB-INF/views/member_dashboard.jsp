<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <title><spring:message code="login.vidhanmandal" text="Maharashtra Vidhanbhavan"/></title>
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
    <link rel="icon" type="image/png" href="./resources/images/mlsicon.png" />
    <script src="./resources/js/libs/jquery/jquery-1.11.2.min.js?v=2"></script>
    <script src="./resources/js/libs/jquery/jquery-migrate-1.2.1.min.js?v=1"></script>
    <script src="./resources/js/libs/moment/moment.min.js"></script>
    <script src="./resources/js/libs/fullcalendar/fullcalendar.js"></script>
    <!-- <script src="./resources/js/core/demo/DemoCalendar.js?v=3"></script> -->
     <script type="text/javascript" src="./resources/js/common.js?v=3051"></script>
     
     <script type="text/javascript">
	   	$(document).ready(function() {
	   		
	   		/**** Load Internal Application for Device Submission ****/
	   		$('#loadELSApplication').click(function() {		
	   			loadELSApplication();
	   		});
	   		
	   		/**** Calendar Initialization ****/
	   		$('#calendar').fullCalendar({
	   			height: 500,
	   		    eventClick: function(event) {
	   		        if (event.url) {
	   		            window.open(event.url);
	   		            return false;
	   		        }
	   		    }
	   		});
	   		
	   		/*** Loading calendar event for session if exists ***/
	   		loadSessionEvents();
	   		
	   		/***** Session Type Change *****/	   		
	   		
	   		$("#sessionType").change(function(){
	   			loadSessionEvents();
	   		});
	   		
	   		
	   		if($("#currentHouseType").val()=='lowerhouse'){
	   			$(".deviceCount").css("color","#406b03");
	   			$(".sessionBar").css("background-color","#406b03");
	   		}else if($("#currentHouseType").val()=='upperhouse'){
	   			$(".deviceCount").css("color","#ac031f");
	   			$(".sessionBar").css("background-color","#ac031f");
	   		} 
	   	});
	   	
	   	function loadELSApplication() {
	   		var parameters = {
	      			redirectedToHomePage	: 	'yes'
	      		}
	   		form_submit_with_target('home.htm', parameters, 'POST', '_blank');
	   	}
	   	
	   	function loadSessionEvents(){
	   			$.get("ref/documentlinks?sessiontype="+$("#sessionType").val()
   					+ "&sessionyear="+$("#sessionYear").val()
   					+ "&housetype="+$("#houseType").val(), function(data){
   				if(data != null && data.length>0){
   					$('#calendar').fullCalendar('gotoDate', data[0].sessionDate);
   					for(var i=0;i<data.length;i++){
   						var evento = $("#calendar").fullCalendar('clientEvents', data[i].id);
   						if(evento == null || evento == ''){
   							var newEvent = new Object();
   	   						newEvent.id = data[i].id;
   	   						newEvent.title = data[i].name;
   	   			   			newEvent.start = data[i].sessionDate;
   	   			   			newEvent.url = data[i].displayName;
   	   			   			newEvent.allDay = true;
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
   					//alert("Session Does not Exist");
   					return false;
   				}
   			});
	   	}
    </script>
    <style type="text/css">
    	.card-body .alert-callout{
    		min-height:105px;
    	}
    	
    	.fc-event{
    		font-weight:bold;
    		border-left-color: green ;
    		background-color: white;
    	}
    </style>
</head>
<c:choose>
	<c:when test="${housetype=='lowerhouse'}">
	<body class="menubar-hoverable header-fixed menubar-pin green-theme">
	</c:when>
	<c:otherwise>
	<body class="menubar-hoverable header-fixed menubar-pin red-theme">
	</c:otherwise>
</c:choose>

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
                        <form class="navbar-search " role="search">
                            <div class="form-group">
                                <input type="text" class="form-control" name="headerSearch" placeholder="Enter your keyword">
                            </div>
                            <button type="submit" class="btn btn-icon-toggle ink-reaction"><i class="fa fa-search"></i></button>
                        </form>
                    </li>
                    <li class="dropdown hidden-xs">
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
    <!-- BEGIN BASE-->
    <div id="base">

        <!-- BEGIN CONTENT-->
        <div id="content">
            <section class="full-bleed profile-section">
                <div class="section-body style-default-dark force-padding text-shadow">
                    <div class="img-backdrop" style="background-image: url('./resources/images/vidhan-sabha-banner.jpg')"></div>
                    <div class="overlay overlay-shade-top stick-top-left height-3"></div>
                    <div class="row">
                        <div class="col-md-6 col-sm-4 col-xs-12">
                            <div class="width-3 text-center profile-img-wrapper">
                                <div class="hbox-column width-3">
                                    <!-- <img class="img-circle img-responsive " src="./resources/images/Chief_Minister_of_Maharashtra_Devendra_Fadnavis.jpg" alt=""> -->
                                     <img class="img-circle img-responsive " src="./file/${memberPhoto}" alt="">
                                </div>
                            </div>
                            <h3>${memberName}<br><small>
                            	<c:if test="${memberDesignation != '' and memberDesignation != null}">
                            		${memberDesignation} <br>
                            	</c:if>
                            	 ${memberRole}
                            	</small>
                            </h3>
                        </div>
                        
                        <!--end .col -->
                        <div class="hbox-column col-md-3 col-sm-4 col-xs-12 user-more-info">
                           
                                    <h4><spring:message code="member.shortInfo" text="Short Info"/></h4>
                            
                                    <dl class="dl-horizontal dl-icon">
                                        <dt><span class="fa fa-fw fa-institution  fa-lg opacity-50"></span></dt>
                                        <dd>
                                            <span class="opacity-50"><spring:message code="constituency.displayname" text="Constituency"/> </span>
                                            <br>
                                            <span class="text-medium">${constituency}</span>
                                        </dd>
                                        <dt><span class="fa fa-fw fa-calendar fa-lg opacity-50"></span></dt>
                                        <dd>
                                            <span class="opacity-50"><spring:message code="member.personal.birthDate" text="Birth Date"/></span>
                                            <br>
                                            <span class="text-medium">${memberBirthDate}</span>
                                        </dd>
                                        <dt><span class="fa fa-fw fa-home fa-lg opacity-50"></span></dt>
                                        <dd>
                                            <span class="opacity-50"><spring:message code="member.personal.birthPlace" text="Birth Place"/></span>
                                            <br>
                                            <span class="text-medium">${memberBirthPlace}</span>
                                        </dd>
                                    </dl>
                                    <!--end .dl-horizontal -->
                               
                                
                            <!--end .row -->
                        </div>
                        <div class="col-md-3 col-sm-4 col-xs-12">
                            <div class="width-2 party-symbol text-center pull-right">
                                 <div class="hbox-column width-2">
                                 	<c:if test="${memberPartyPhoto != null && memberPartyPhoto !=''}">
                                 		<img class="img-circle img-responsive " src="./file/${memberPartyPhoto}" alt="">
                                 	</c:if>
                                    
                                </div> 
                                <h4 class="text-center">${memberParty}</h4>
                            </div>
                        </div>
                        <!--end .col -->
                    </div>
                    <!--end .row -->
                    <br>
                    <div class="overlay overlay-shade-bottom stick-bottom-left force-padding text-right">
                        <div class="row">
                            <div class="col-md-6">
                                <button type="button" class="btn ink-reaction btn-primary pull-left">View Profile</button>
                            </div>
                            <div class="col-md-6">
                                <a class="btn btn-icon-toggle" data-toggle="tooltip" data-placement="top" data-original-title="Contact me"><i class="fa fa-envelope"></i></a>
                                <a class="btn btn-icon-toggle" data-toggle="tooltip" data-placement="top" data-original-title="Follow me"><i class="fa fa-twitter"></i></a>
                                <a class="btn btn-icon-toggle" data-toggle="tooltip" data-placement="top" data-original-title="Personal info"><i class="fa fa-facebook"></i></a>
                            </div>
                        </div>
                    </div>
                </div>
                <!--end .section-body -->
            </section>
            <section class="main-content">
                <!-- BEGIN ALERT - TIME ON SITE -->
                <div class="section-body m-t-n">
                    <div class="row  row-eq-height clearfix">
                    	<div style="overflow: hidden;">
                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                       <!-- 	<h3 class="pull-right text-theme"><i class="md md-grade"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${questions_starred_count != null  and questions_starred_count != ''}">
                                        			${questions_starred_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                       </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.starred" text="Starred Question"/></span>
                                        
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                       <!--  <h3 class="pull-right text-theme"><i class="md md-question-answer"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${questions_unstarred_count != null  and questions_unstarred_count != ''}">
                                        			${questions_unstarred_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                        </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.unstarred" text="Unstarred Question"/></span>
                                       
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <!--end .col -->
                        <!-- END ALERT - TIME ON SITE -->
                        <!-- BEGIN ALERT - TIME ON SITE -->
                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                      	<!-- <h3 class="pull-right text-theme"><i class="md md-schedule"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${questions_halfhourdiscussion_from_question_count != null  and questions_halfhourdiscussion_from_question_count != ''}">
                                        			${questions_halfhourdiscussion_from_question_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                        </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.halfHourDiscussionFromQuestion" text="Half hour Discussion from Question"/></span>
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <!--end .col -->
                        <!-- END ALERT - TIME ON SITE -->
                        <!-- BEGIN ALERT - TIME ON SITE -->
                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                       <!-- <h3 class="pull-right text-theme"><i class="md md-visibility"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${motions_calling_attention_count != null  and motions_calling_attention_count != ''}">
                                        			${motions_calling_attention_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                        </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.callingAttentionMotion" text="Calling Attention Motion"/></span>
                                       
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <!-- BEGIN ALERT - TIME ON SITE -->
                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                        <!-- <h3 class="pull-right text-theme"><i class="md md-speaker-notes"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${resolutions_nonofficial_count != null  and resolutions_nonofficial_count != ''}">
                                        			${resolutions_nonofficial_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                        </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.nonOfficialResolution" text="Non official Resolution"/></span>
                                       
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <!--end .col -->
                        <!-- END ALERT - TIME ON SITE -->
                        <!-- END ALERT - BOUNCE RATES -->

                        <div class="col-md-2 col-sm-6">
                            <div class="card">
                                <div class="card-body no-padding">
                                    <div class="alert alert-callout alert-success no-margin">
                                        <!-- <h3 class="pull-right text-theme"><i class="md md-alarm"></i></h3> -->
                                        <strong class="text-xl deviceCount pull-right">
                                        	<c:choose>
                                        		<c:when test="${motions_standalonemotion_halfhourdiscussion_count != null  and motions_standalonemotion_halfhourdiscussion_count != ''}">
                                        			${motions_standalonemotion_halfhourdiscussion_count}
                                        		</c:when>
                                        		<c:otherwise>
                                        			0
                                        		</c:otherwise>
                                        	</c:choose>
                                        </strong>
                                        <br>
                                        <span class="opacity-80 deviceCount"><spring:message code="device.standaloneMotion" text="Standalone Motion"/></span>
                                        
                                    </div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                            </div>
                        </div>
                        <!--end .col -->
                        <!-- END ALERT - TIME ON SITE -->
                        <div class="col-md-12 col-sm-12 card sessionBar" style="margin-left: 10px; margin-right: 10px; width: 1060px;">
                        	<div class="col-md-4 col-sm-4">
                            	<div style="color: white; display:inline-block">
                            	   <h4><strong><spring:message code="group.houseType" text="House Type"/></strong></h4>
                            	 </div>
                          	   	<div style="margin-top:6px;display:inline-block">
                          	   		<select id="houseType"  style="width: 150px;">
		                          		<option value=" "><spring:message code="please.select"/></option>
		                          		<c:forEach items="${houseTypes}" var="i">
		                          			<c:choose>
		                          				<c:when test="${i.type==housetype}">
		                          					<option selected="selected" value="${i.type}">${i.name}</option>
		                          				</c:when>
		                          				<c:otherwise>
		                          					<option value="${i.type}">${i.name}</option>
		                          				</c:otherwise>
		                          			</c:choose>
		                          		</c:forEach>
		                          	</select>
                              	</div>
                            </div>
                        	<div class="col-md-4 col-sm-4">
                            	<div style="color: white;display:inline-block">
                            	   <h4><strong><spring:message code="sessiontype.sessionType" text="Session Type"/></strong></h4>
                            	 </div>
                          	   	<div style="margin-top:6px;display:inline-block">
                          	   		<select id="sessionType"  style="width: 150px;">
		                          		<option value=" "><spring:message code="please.select"/></option>
		                          		<c:forEach items="${sessionTypes}" var="i">
		                          			<c:choose>
		                          				<c:when test="${i.id==sessionType}">
		                          					<option selected="selected" value="${i.id}">${i.sessionType}</option>
		                          				</c:when>
		                          				<c:otherwise>
		                          					<option value="${i.id}">${i.sessionType}</option>
		                          				</c:otherwise>
		                          			</c:choose>
		                          			
		                          		</c:forEach>
		                          	</select>
                              	</div>
                            </div>
                            <div class="col-md-4 col-sm-4">
                           		<div  style="color: white;display:inline-block">
                            		 <h4><strong><spring:message code="resolution.sessionyear" text="Session Year"/></strong></h4>
                            	</div>
                              	<div  style="margin-top:6px;display:inline-block"> 
                              		<select id="sessionYear"  style="width: 150px;">
                               			<option value=" "><spring:message code="please.select"/></option>
	                              		<c:forEach items="${years}" var="i">
	                              			<c:choose>
	                              				<c:when test="${i == sessionYear}">
	                              					<option selected="selected" value="${i}">${i}</option>
	                              				</c:when>
	                              				<c:otherwise>
	                              					<option value="${i}">${i}</option>
	                              				</c:otherwise>
	                              			</c:choose>
	                              		</c:forEach>
                           			</select>
                              	</div>
                            </div>
                        </div>
                        <div class="col-md-12 col-sm-12">
                            <div class="card calender">
  <!--                              <div class="card-head style-default-light">
                                    <ul class="nav nav-tabs tabs-text-contrast tabs-accent" data-toggle="tabs">
                                        <li data-mode="month" class="active"><a href="#">Month</a></li>
                                        <li data-mode="agendaWeek"><a href="#">Week</a></li>
                                        <li data-mode="agendaDay"><a href="#">Day</a></li>
                                    </ul>
                                </div> -->
                                <!--end .card-head -->
                                <div class="card-body no-padding">
                                    <div id="calendar"></div>
                                </div>
                                <!--end .card-body -->
                            </div>
                            <!--end .card -->
                        </div>
                        <div class="col-md-12 col-sm-12">
                            <div class="card">
                                <div class="card-head style-default-light">
                                    <header>
                                        <h4>Chart</h4>
                                    </header>
                                </div>
                                <div class="card-body ">
                                    <canvas id="myChart" width="400" height="200"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
        <!--end #content-->
        <!-- END CONTENT -->
        <!-- BEGIN MENUBAR-->
        <div id="menubar" class="menubar-inverse ">
            <div class="menubar-fixed-panel">
                <div>
                    <a class="btn btn-icon-toggle btn-default menubar-toggle" data-toggle="menubar" href="javascript:void(0);">
                        <i class="fa fa-bars"></i>
                    </a>
                </div>
                <div class="expanded">
                    <a href="../../html/dashboards/medashboard.html">
                        <span class="text-lg text-bold text-primary ">MATERIAL&nbsp;ADMIN</span>
                    </a>
                </div>
            </div>
            <div class="menubar-scroll-panel">
                <!-- BEGIN MAIN MENU -->
                <ul id="main-menu" class="gui-controls">
                    <li>
                        <a href="http://webmail.mls.org.in:2095/" target="_blank">
                            <div class="gui-icon"><i class="fa fa-envelope"></i></div>
                            <span class="title"><spring:message code="user.email" text="E-Mail"/></span>
                        </a>
                    </li>
                     <li>
                        <a href="javascript:void(0);" id="loadELSApplication">
                            <div class="gui-icon"><i class="fa fa-external-link-square"></i></div>
                            <span class="title"><spring:message code="memberProfile.onlineDeviceSubmission" text="Online Device Submission"/></span>
                        </a>
                    </li>
                    <li>
                        <a href="${rotationOrderLink}" target="_blank">
                            <div class="gui-icon"><i class="fa fa-bar-chart"></i></div>
                            <span class="title"><spring:message code="group.rotationorder" text="Rotation Order"/></span>
                        </a>
                    </li>
                    <li>
                        <a href="http://mls.org.in/newpdf/Member%20manual%20ver%204.pdf" target="_blank">
                            <div class="gui-icon"><i class="fa fa-download"></i></div>
                            <span class="title"><spring:message code="group.usermanual" text="User Manual"/></span>
                        </a>
                    </li>
                    <!--end /menu-li -->
                    <!--end /menu-li -->
                    <!-- END EMAIL -->
                </ul>
                <!--end .main-menu -->
                <!-- END MAIN MENU -->
                <div class="menubar-foot-panel">
                    <small class="no-linebreak hidden-folded">
                        <span class="opacity-75">Copyright &copy; 2017</span> <strong>MKCL</strong>
                    </small>
                </div>
            </div>
            <!--end .menubar-scroll-panel-->
        </div>
        <!--end #menubar-->
    </div>
     <input type="hidden" id="authusername" name="authusername" value="${authusername}"/>
     <input type="hidden" id="authfullname" name="authfullname" value="${authtitle} ${authfirstname} ${authmiddlename} ${authlastname}"/>    
     <input type="hidden" id="authlocale" name="authlocale" value="${locale}"/>
     <input type="hidden" id="startURL" name="startURL" value="${startURL}"/>
     <input type="hidden" id="currentHouseType" value="${housetype}"/>
    <!--end #base-->
    <!-- END BASE -->
    <!-- BEGIN JAVASCRIPT -->
    
    <script src="./resources/js/libs/bootstrap/bootstrap.min.js"></script>
    <!-- <script src="js/libs/spin.js/spin.min.js"></script> -->
    <script src="./resources/js/libs/autosize/jquery.autosize.min.js"></script>
 
  
    <script src="./resources/js/libs/nanoscroller/jquery.nanoscroller.min.js"></script>

    <script src="./resources/js/core/source/App.js"></script>
    <script src="./resources/js/core/source/AppNavigation.js"></script>
    <script src="./resources/js/core/source/AppOffcanvas.js"></script>
    <script src="./resources/js/core/source/AppCard.js"></script>
    <script src="./resources/js/core/source/AppForm.js"></script>
    <script src="./resources/js/core/source/AppNavSearch.js"></script>
    <script src="./resources/js/core/source/AppVendor.js"></script>
    <script src="./resources/js/core/demo/Demo.js"></script>
    <!-- <script src="js/core/demo/DemoDashboard.js"></script> -->
 

    <script src="./resources/js/libs/chart/Chart.bundle.min.js"></script>
    <script src="./resources/js/custom.js"></script>
    <!-- END JAVASCRIPT -->
  
</body>

</html>
