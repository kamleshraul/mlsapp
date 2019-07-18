<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <link rel="apple-touch-icon" sizes="76x76" href="./../assets/img/apple-icon.png">
  <link rel="icon" type="image/png" href="./../assets/img/favicon.png">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1; charset=UTF-8" />
  <title>
    Dashboard For MLS
  </title>
  <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no' name='viewport' />
  <!--     Fonts and icons     -->
  <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
  <link rel="stylesheet" href="./../assets/css/font-awesome.min.css">
  <!-- CSS Files -->
  <link href="./../assets/css/material-dashboard.css?v=2.1.1" rel="stylesheet" />
 
</head>

<body class="">
 	<%@ include file="sidebar.jsp" %>
    <div class="main-panel">
      <!-- Navbar -->
      <nav class="navbar navbar-expand-lg navbar-transparent navbar-absolute fixed-top ">
        <div class="container-fluid">
          <div class="navbar-wrapper">
            <a class="navbar-brand"><spring:message code="dashboard" text="dashboard"></spring:message></a>
          </div>
          <button class="navbar-toggler" type="button" data-toggle="collapse" aria-controls="navigation-index" aria-expanded="false" aria-label="Toggle navigation">
            <span class="sr-only">Toggle navigation</span>
            <span class="navbar-toggler-icon icon-bar"></span>
            <span class="navbar-toggler-icon icon-bar"></span>
            <span class="navbar-toggler-icon icon-bar"></span>
          </button>
          <div class="collapse navbar-collapse justify-content-end">
           	
            <ul class="navbar-nav">
            
              <li class="nav-item dropdown">
                <a class="nav-link" href="#pablo" id="navbarDropdownProfile" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <i class="material-icons">person</i>
                  <p class="d-lg-none d-md-block">
                    Account
                  </p>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownProfile">

                  <div class="dropdown-divider"></div>
           <!--        <a class="dropdown-item" href="#">Log out</a> -->
                  <div><a id="logout" class="dropdown-item" href="<c:url value="/j_spring_security_logout" />"><spring:message code="logout" text="Logout"/></a></div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </nav>
      <!-- End Navbar -->
      <div class="content">
        <div class="container-fluid">
           <div class="row">
         
              
                 <div class="col-md-4 mb-3">
                <label for="state"><spring:message code="dashboard.house" text="HOUSE"></spring:message></label>
                <select class="custom-select d-block w-50" id="selectedHouseType" required>
                  <option value=""><spring:message code="dashboard.choose" text="Choose..."></spring:message></option>
                 <c:forEach var="i" items="${houses}">
					<option value="${i.name}"><c:out value="${i.name}"></c:out></option>
				</c:forEach>
                </select>
              </div>
              
              <div class="col-md-4 mb-3">
                <label for="country"><spring:message code="dashboard.session" text="SESSION"></spring:message></label>
                <select class="custom-select d-block w-50" id="selectedSession" required>
                  <option value=""><spring:message code="dashboard.choose" text="Choose..."></spring:message></option>
                 <c:forEach var="i" items="${sessions}">
					<option value="${i}"><c:out value="${i}"></c:out></option>
				</c:forEach>
                </select>
              </div>
              
              <div class="col-md-4 mb-3">
                <label for="state"><spring:message code="dashboard.search" text="SEARCH"></spring:message></label>
                   <button type="submit" class="btn btn-secondary d-block w-50" id="search"><spring:message code="dashboard.search" text="SEARCH"></spring:message></button>
              </div>
            </div>
          <div class="row">

          </div>
          <div class="row">
            <div class="col-md-8">
              <div class="card card-chart">
                <div class="card-header bg-light">
                  <canvas id="chart-area"></canvas>
                </div>
                <div class="card-body">
                  <h4 class="card-title"><spring:message code="dashboard.partywisestats" text="PartyWise Submission Statistics"></spring:message></h4>
                  
                  <!-- <p class="card-category">
                    <span class="text-success"><i class="fa fa-long-arrow-up"></i> 55% </span> increase in today sales.</p> -->
                </div>
                <div class="card-footer">
                 <!--  <div class="stats">
                    <i class="material-icons">access_time</i> updated 4 minutes ago
                  </div> -->
                </div>
              </div>
            </div>
             <div class="col-md-4">
             	 <div class="row">
             	 	<div>&nbsp;<br></div>
             	 </div>
	            <div class="row">
		            <div class="col-lg-6 col-md-6 col-sm-6">
		              <div class="card card-stats">
		                <div class="card-header card-header-warning">
		<!--                   <div class="card-icon">
		                    <i class="material-icons">Submitted</i>
		                  </div> -->
		                  <p class="card-category" style="color:white"><spring:message code="dashboard.submitted" text="submitted Count"/></p>
		                  <h3 class="card-title" id="submittedCount">
		                   
		                  </h3>
		                </div>
		               <!--  <div class="card-footer">
		                  <div class="stats">
		                    <i class="material-icons text-danger">warning</i>
		                  </div>
		                </div> -->
		                
		              </div>
		            </div>
		            <div class="col-lg-6 col-md-6 col-sm-6">
		              <div class="card card-stats">
		                <div class="card-header card-header-success">
		                 <!--  <div class="card-icon">
		                    <i class="material-icons">Admitted</i>
		                  </div> -->
		                  <p class="card-category" style="color:white"><spring:message code="dashboard.admitted" text="Admitted Count"/></p>
		                  <h3 class="card-title" id="admittedCount"></h3>
		                </div>
		                <!-- <div class="card-footer">
		                  <div class="stats">
		                    <i class="material-icons">date_range</i> Last 24 Hours
		                  </div>
		                </div> -->
		              </div>
		            </div>
	            </div>
	            <div class="row">
		            <div class="col-lg-6 col-md-6 col-sm-6">
		              <div class="card card-stats">
		                <div class="card-header card-header-danger">
		                  <!-- <div class="card-icon">
		                    <i class="material-icons">Rejected</i>
		                  </div> -->
		                  <p class="card-category" style="color:white"><spring:message code="dashboard.rejected" text="Rejected Count"/></p>
		                  <h3 class="card-title" id="rejectedCount"></h3>
		                </div>
		               <!--  <div class="card-footer">
		                  <div class="stats">
		                    <i class="material-icons">local_offer</i> Tracked from Github
		                  </div>
		                </div> -->
		              </div>
		            </div>
		            <div class="col-lg-6 col-md-6 col-sm-6">
		              <div class="card card-stats">
		                <div class="card-header card-header-primary">
		                  <!-- <div class="card-icon">
		                    <i class="material-icons">U</i>
		                  </div> -->
		                  <p class="card-category" style="color:white"><spring:message code="dashboard.unstarred" text="Unstarred Admit Count"/></p>
		                  <h3 class="card-title" id="unstarredCount"></h3>
		                </div>
		               <!--  <div class="card-footer">
		                  <div class="stats">
		                    <i class="material-icons">local_offer</i> Tracked from Github
		                  </div>
		                </div> -->
		              </div>
		            </div>
	            </div>
	         </div>
            </div>
            <div class="row">
             <div class="col-md-12">
              <div class="card card-chart">
                <div class="card-header" >
                   <canvas id="bar-chart-area"></canvas>
                </div>
                <div class="card-body">
                 
                  <h4 class="card-title"><spring:message code="dashboard.ministrywisestats" text="Ministry wise Count"></spring:message></h4>
                 
                  <!-- <p class="card-category">Last Campaign Performance</p> -->
                </div>
                <div class="card-footer">
                  <!-- <div class="stats">
                    <i class="material-icons">access_time</i> campaign sent 2 days ago
                  </div> -->
                </div>
              </div>
            </div> 
<!--             <div class="col-md-4">
              <div class="card card-chart">
                <div class="card-header card-header-danger">
                  <div class="ct-chart" id="completedTasksChart"></div>
                </div>
                <div class="card-body">
                  <h4 class="card-title">Completed Tasks</h4>
                  <p class="card-category">Last Campaign Performance</p>
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <i class="material-icons">access_time</i> campaign sent 2 days ago
                  </div>
                </div>
              </div>
            </div> -->
          </div>
          <div class="row">
            <div class="col-lg-12 col-md-12">
              <div class="card">
                <div class="card-header card-header-warning">
                  
                   <h4 class="card-title"><spring:message code="dashboard.memeberwisestats" text="Memberwise Report"></spring:message></h4>
                 
                  <!-- <p class="card-category">New employees on 15th September, 2016</p> -->
                </div>
                <div class="card-body table-responsive" >
                	<table class="table table-hover" id="memberTable">
	                    <thead class="text-warning">
	                     <th><spring:message code="dashboard.photo" text="Photo"/></th>
	                      <th><spring:message code="dashboard.membername" text="Member Name"/></th>
	                      <th><spring:message code="dashboard.admitted" text="Admitted Count"/></th>
	                      <th><spring:message code="dashboard.rejected" text="Rejected Count"/></th>
	                      <th><spring:message code="dashboard.submitted" text="submitted Count"/></th>
	                    </thead>
                    <tbody id="membersSessionwise">
                    </tbody>
                   </table>
                </div>
              </div>
            </div>
            
           
          </div>
        </div>
      </div>
      <footer class="footer">
        <div class="container-fluid">
          <nav class="float-left">
           <ul>
              <li>
                <a href="https://www.mkcl.org/">
                  MKCL
                </a>
              </li>
              <li>
                <a href="https://www.mkcl.org/about-mkcl">
                  About Us
                </a>
              </li>
              <li>
                <a href="https://www.mkcl.org/contact-us">
                  Contact Us
                </a>
              </li>
              
            </ul>
          </nav>
          <div class="copyright float-right">
            &copy;
            <script>
              document.write(new Date().getFullYear())
            </script>, made with <i class="material-icons">favorite</i> using
            <a href="https://www.creative-tim.com" target="_blank">Creative Tim</a>.
          </div>
        </div>
      </footer>
    </div>
  </div>
  <!--   Core JS Files   -->
  <script src="./../assets/js/core/jquery.min.js"></script>
  <script src="./../assets/js/core/popper.min.js"></script>
  <script src="./../assets/js/core/bootstrap-material-design.min.js"></script>
  <script src="./../assets/js/plugins/perfect-scrollbar.jquery.min.js"></script>
  <!-- Plugin for the momentJs  -->
  <script src="./../assets/js/plugins/moment.min.js"></script>
  <!--  Plugin for Sweet Alert -->
  <script src="./../assets/js/plugins/sweetalert2.js"></script>
  <!-- Forms Validations Plugin -->
  <script src="./../assets/js/plugins/jquery.validate.min.js"></script>
  <!-- Plugin for the Wizard, full documentation here: https://github.com/VinceG/twitter-bootstrap-wizard -->
  <script src="./../assets/js/plugins/jquery.bootstrap-wizard.js"></script>
  <!--	Plugin for Select, full documentation here: http://silviomoreto.github.io/bootstrap-select -->
  <script src="./../assets/js/plugins/bootstrap-selectpicker.js"></script>
  <!--  Plugin for the DateTimePicker, full documentation here: https://eonasdan.github.io/bootstrap-datetimepicker/ -->
  <script src="./../assets/js/plugins/bootstrap-datetimepicker.min.js"></script>
  <!--  DataTables.net Plugin, full documentation here: https://datatables.net/  -->
  <script src="./../assets/js/plugins/jquery.dataTables.min.js"></script>
  <!--	Plugin for Tags, full documentation here: https://github.com/bootstrap-tagsinput/bootstrap-tagsinputs  -->
  <script src="./../assets/js/plugins/bootstrap-tagsinput.js"></script>
  <!-- Plugin for Fileupload, full documentation here: http://www.jasny.net/bootstrap/javascript/#fileinput -->
  <script src="./../assets/js/plugins/jasny-bootstrap.min.js"></script>
  <!--  Full Calendar Plugin, full documentation here: https://github.com/fullcalendar/fullcalendar    -->
  <script src="./../assets/js/plugins/fullcalendar.min.js"></script>
  <!-- Vector Map plugin, full documentation here: http://jvectormap.com/documentation/ -->
  <script src="./../assets/js/plugins/jquery-jvectormap.js"></script>
  <!--  Plugin for the Sliders, full documentation here: http://refreshless.com/nouislider/ -->
  <script src="./../assets/js/plugins/nouislider.min.js"></script>
  <!-- Include a polyfill for ES6 Promises (optional) for IE11, UC Browser and Android browser support SweetAlert -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/core-js/2.4.1/core.js"></script>
  <!-- Library for adding dinamically elements -->
  <script src="./../assets/js/plugins/arrive.min.js"></script>
  <!--  Google Maps Plugin    -->
  <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_KEY_HERE"></script>
  <!-- Chartist JS -->
  <script src="./../assets/js/plugins/chartist.min.js"></script>
  <!--  Notifications Plugin    -->
  <script src="./../assets/js/plugins/bootstrap-notify.js"></script>
  <!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
  <script src="./../assets/js/material-dashboard.js?v=2.1.1" type="text/javascript"></script>
  <script src="./../assets/js/Chart.min.js"></script>
  <script src="./../assets/js/utils.js"></script>
	
  <script>
    $(document).ready(function() {
      $().ready(function() {
        $sidebar = $('.sidebar');

        $sidebar_img_container = $sidebar.find('.sidebar-background');

        $full_page = $('.full-page');

        $sidebar_responsive = $('body > .navbar-collapse');

        window_width = $(window).width();

        fixed_plugin_open = $('.sidebar .sidebar-wrapper .nav li.active a p').html();

        if (window_width > 767 && fixed_plugin_open == 'Dashboard') {
          if ($('.fixed-plugin .dropdown').hasClass('show-dropdown')) {
            $('.fixed-plugin .dropdown').addClass('open');
          }

        }

        $('.fixed-plugin a').click(function(event) {
          // Alex if we click on switch, stop propagation of the event, so the dropdown will not be hide, otherwise we set the  section active
          if ($(this).hasClass('switch-trigger')) {
            if (event.stopPropagation) {
              event.stopPropagation();
            } else if (window.event) {
              window.event.cancelBubble = true;
            }
          }
        });

        $('.fixed-plugin .active-color span').click(function() {
          $full_page_background = $('.full-page-background');

          $(this).siblings().removeClass('active');
          $(this).addClass('active');

          var new_color = $(this).data('color');

          if ($sidebar.length != 0) {
            $sidebar.attr('data-color', new_color);
          }

          if ($full_page.length != 0) {
            $full_page.attr('filter-color', new_color);
          }

          if ($sidebar_responsive.length != 0) {
            $sidebar_responsive.attr('data-color', new_color);
          }
        });

        $('.fixed-plugin .background-color .badge').click(function() {
          $(this).siblings().removeClass('active');
          $(this).addClass('active');

          var new_color = $(this).data('background-color');

          if ($sidebar.length != 0) {
            $sidebar.attr('data-background-color', new_color);
          }
        });

        $('.fixed-plugin .img-holder').click(function() {
          $full_page_background = $('.full-page-background');

          $(this).parent('li').siblings().removeClass('active');
          $(this).parent('li').addClass('active');


          var new_image = $(this).find("img").attr('src');

          if ($sidebar_img_container.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
            $sidebar_img_container.fadeOut('fast', function() {
              $sidebar_img_container.css('background-image', 'url("' + new_image + '")');
              $sidebar_img_container.fadeIn('fast');
            });
          }

          if ($full_page_background.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
            var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');

            $full_page_background.fadeOut('fast', function() {
              $full_page_background.css('background-image', 'url("' + new_image_full_page + '")');
              $full_page_background.fadeIn('fast');
            });
          }

          if ($('.switch-sidebar-image input:checked').length == 0) {
            var new_image = $('.fixed-plugin li.active .img-holder').find("img").attr('src');
            var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');

            $sidebar_img_container.css('background-image', 'url("' + new_image + '")');
            $full_page_background.css('background-image', 'url("' + new_image_full_page + '")');
          }

          if ($sidebar_responsive.length != 0) {
            $sidebar_responsive.css('background-image', 'url("' + new_image + '")');
          }
        });

        $('.switch-sidebar-image input').change(function() {
          $full_page_background = $('.full-page-background');

          $input = $(this);

          if ($input.is(':checked')) {
            if ($sidebar_img_container.length != 0) {
              $sidebar_img_container.fadeIn('fast');
              $sidebar.attr('data-image', '#');
            }

            if ($full_page_background.length != 0) {
              $full_page_background.fadeIn('fast');
              $full_page.attr('data-image', '#');
            }

            background_image = true;
          } else {
            if ($sidebar_img_container.length != 0) {
              $sidebar.removeAttr('data-image');
              $sidebar_img_container.fadeOut('fast');
            }

            if ($full_page_background.length != 0) {
              $full_page.removeAttr('data-image', '#');
              $full_page_background.fadeOut('fast');
            }

            background_image = false;
          }
        });

        $('.switch-sidebar-mini input').change(function() {
          $body = $('body');

          $input = $(this);

          if (md.misc.sidebar_mini_active == true) {
            $('body').removeClass('sidebar-mini');
            md.misc.sidebar_mini_active = false;

            $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar();

          } else {

            $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar('destroy');

            setTimeout(function() {
              $('body').addClass('sidebar-mini');

              md.misc.sidebar_mini_active = true;
            }, 300);
          }

          // we simulate the window Resize so the charts will get updated in realtime.
          var simulateWindowResize = setInterval(function() {
            window.dispatchEvent(new Event('resize'));
          }, 180);

          // we stop the simulation of Window Resize after the animations are completed
          setTimeout(function() {
            clearInterval(simulateWindowResize);
          }, 1000);

        });
        $("#search").click(function(){
        	 loadPartyWiseDetails($("#selectedSession").val(),$("#deviceType").val());
             loadMinistryWiseDetails($("#selectedSession").val(),$("#deviceType").val());
             loadDeviceDetails($("#selectedSession").val(),$("#deviceType").val())
    		url = "?house="+$("#selectedHouseType").val()
    				+"&session="+$("#selectedSession").val()
    		+"&deviceType="+$("#deviceType").val()
    		+"&admitStatus="+$("#admitStatus").val()
    		+"&rejectStatus="+$("#rejectStatus").val();
    		resourceURL = '../statisticaldashboard/getSortedData'+url;
    		$.get(resourceURL,function(data){
  			var text = '';
  			for(var i=0;i<data.length;i++){	
  				if(data[i].name!= null && data[i].name!=''){
					var text = text+
								'<tr>';
										if(data[i].type!=null && data[i].type!=''){
											text = text + '<td><img src="../ws/biography/photo/'+data[i].type+'" class="img-fluid rounded" style="width:80px;height:80px;"></td>'
										}else{
											text = text + '<td><img src="./resources/images/member.png" class="img-fluid rounded" style="width:80px;height:80px;"></td>';
										}
									text = text + '<td>'+data[i].name+'</td>'+
					  				'<td>'+data[i].formattedNumber+'</td>'+
					  				'<td>'+data[i].formattedOrder+'</td>'+
					  				'<td>'+data[i].value+'</td></tr>';
				}

  			}
  			$("#membersSessionwise").html(text);
  			$('#memberTable').DataTable();
  		  	$('.dataTables_length').addClass('bs-select');
    		});
        });
      });
      
      
      
      loadHouseTypes();
      $("#selectedHouseType").change(function(){
    	 if(this.value !=''){
    		 loadSessionsByHouseType(this.value);
    	 } 
      });
    });
  </script>
  <script>
    $(document).ready(function() {
    	
      // Javascript method's body can be found in assets/js/demos.js
      md.initDashboardPageCharts();
      loadHouseTypes();
     
    });
    
    function loadHouseTypes(){
    	$.get("loadhousetypes",function(data){
    		var text = '<option value="">Choose...</option>';
    		if(data!=null && data!=''){
    			for(var i=0;i<data.length;i++){
    				text = text + "<option value='"+data[i].id+"'>"+data[i].name + "</option>"
    			}
    		}
    		$("#selectedHouseType").html(text);
    	});
    }
    
    function loadSessionsByHouseType(houseTypeId){
    	 $.get("../ref/loadsessionsbyhousetype?houseTypeId="+houseTypeId,function(data){
    		var text = '<option value="">Choose...</option>';
    		if(data!=null && data!=''){
    			for(var i=0;i<data.length;i++){
    				text = text + "<option value='"+data[i].id+"'>"+data[i].name + "</option>"
    			}
    		}
    		$("#selectedSession").html(text);
    	});
    }
    
    function getRandomColor() {
    	  var letters = '0123456789ABCDEF';
    	  var color = '#';
    	  for (var i = 0; i < 6; i++) {
    	    color += letters[Math.floor(Math.random() * 16)];
    	  }
    	  return color;
    }
    
    function loadDeviceDetails(sessionId, deviceTypeId){
    	$.get("../statisticaldashboard/loadDeviceDetails?session="+sessionId+"&deviceType="+ deviceTypeId,function(data){
    		if(data!=null && data!=''){
    			/* for(var i=0;i<data.length;i++){
    				
    			} */
    			$("#submittedCount").html(data[0].value);
    			$("#admittedCount").html(data[0].name);
    			$("#rejectedCount").html(data[0].formattedNumber);
    			$("#unstarredCount").html(data[0].formattedOrder);
    		}
    		//$("#selectedSession").html(text);
    	});
    }
    
    function loadPartyWiseDetails(sessionId, deviceTypeId){
    	var pieLabels = [];
	    var pieData = [];
	    var pieBackgroundColor= [];
    	$.get("../statisticaldashboard/loadPartyDetails?session="+sessionId+"&deviceType="+ deviceTypeId,function(data){
    		if(data!=null && data!=''){
    			for(var i=0;i<data.length;i++){
    				pieLabels.push(data[i].name);
    				pieData.push(data[i].id);
    				pieBackgroundColor.push(getRandomColor());
    			}
    			var config = {
    					type: 'pie',
    					data: {
    						datasets: [{
    							data: pieData,
    							backgroundColor: pieBackgroundColor,
    							label: 'Dataset 1'
    						}],
    						labels:pieLabels,
       					},
    					options: {
    						responsive: true,
    						legend: {
    				            display: true,
    				            position: 'right',
    				            
    				        },
    				       
    				        
    					},
    					
    				};
    			
    			window.onload = function() {
    				
    			};
    			var ctx = document.getElementById('chart-area').getContext('2d');
    			window.myPie = new Chart(ctx, config);	
	   		}
       	});
    }
    
    
    function loadMinistryWiseDetails(sessionId, deviceTypeId){
    	var barLabels = [];
	    var barData = [];
	    var barBackgroundColor= [];
    	$.get("../statisticaldashboard/loadministriesdetails?session="+sessionId+"&deviceType="+ deviceTypeId,function(data){
    		if(data!=null && data!=''){
    			for(var i=0;i<data.length;i++){
    				barLabels.push(data[i].name);
    				barData.push(data[i].id);
    				//pieBackgroundColor.push(getRandomColor());
    			}
    			var config = {
    					type: 'bar',
    					data: {
    						datasets: [{
    							data: barData,
    							//backgroundColor: pieBackgroundColor,
    							label: 'Question Count'
    						}],
    						labels:barLabels,
       					},
    					options: {
    						responsive: true,
    					    scales: {
    					        yAxes: [{
    					            ticks: {
    					                max: 2500,
    					                min: 0,
    					                stepSize: 250
    					            }
    					        }]
    					    },
    						legend: {
    				            display: true,
    				            position: 'right',
    				            
    				        },
    					},
    					
    				};
    			
    			window.onload = function() {
    				
    			};
    			var ctx = document.getElementById('bar-chart-area').getContext('2d');
    			window.myPie = new Chart(ctx, config);	
	   		}
       	});
    }
  </script>
  
  <script>
	  
		/* var randomScalingFactor = function() {
			var pieData = [1,2,3,4]
			return pieData;//Math.round(Math.random() * 100);
		}; */


		document.getElementById('randomizeData').addEventListener('click', function() {
			config.data.datasets.forEach(function(dataset) {
				dataset.data = dataset.data.map(function() {
					return randomScalingFactor();
				});
			});

			window.myPie.update();
		});

		var colorNames = Object.keys(window.chartColors);
		document.getElementById('addDataset').addEventListener('click', function() {
			var newDataset = {
				backgroundColor: [],
				data: [],
				label: 'New dataset ' + config.data.datasets.length,
			};

			for (var index = 0; index < config.data.labels.length; ++index) {
				newDataset.data.push(randomScalingFactor());

				var colorName = colorNames[index % colorNames.length];
				var newColor = window.chartColors[colorName];
				newDataset.backgroundColor.push(newColor);
			}

			config.data.datasets.push(newDataset);
			window.myPie.update();
		});

		document.getElementById('removeDataset').addEventListener('click', function() {
			config.data.datasets.splice(0, 1);
			window.myPie.update();
		});
	</script>
  
	<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden"/>	
	<input id="admitStatus" name="admitStatus" value="${admitStatus}" type="hidden"/>	
	<input id="rejectStatus" name="rejectStatus" value="${rejectStatus}" type="hidden"/>	
				
</body>

</html>
