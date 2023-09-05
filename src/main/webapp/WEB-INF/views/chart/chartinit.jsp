<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			view_chart();
			$("#create_chart").click(function() {
				$.prompt($('#chartCreationConfirmationMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
				   		if(v){
				   			if($('#currentDeviceType').val()=='resolutions_nonofficial'){
								$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
								var parameters = "houseType="+$("#selectedHouseType").val()
												 +"&sessionYear="+$("#selectedSessionYear").val()
												 +"&sessionType="+$("#selectedSessionType").val()
												 +"&deviceType="+$("#selectedDeviceType").val()
												 +"&status="+$("#selectedStatus").val()
												 +"&role="+$("#srole").val(); 
								var resourceURL = 'chart/create?' + parameters;
								$.get(resourceURL, function(data) {
									var displayMessage = data;
									if(data == "CREATED" || data == "ALREADY_EXISTS") {
										var newResourceURL = 'chart/view?' + parameters;
										$.get(newResourceURL,function(data){
											$("#chartResultDiv").empty();
											$("#chartResultDiv").html(data);
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
									}else {
										displayMessage = "Error Occurred while creating Chart";
										$.unblockUI();
										$.fancybox.open(displayMessage);
									}
								}).fail(function(){
									$.unblockUI();
									if($("#ErrorMsg").val()!=''){
										$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
									}else{
										$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
									}
									scrollTop();
								});
							}else if($('#currentDeviceType').val()=="questions_starred"){
								var parameters = "houseType="+$("#selectedHouseType").val()
												 +"&sessionYear="+$("#selectedSessionYear").val()
												 +"&sessionType="+$("#selectedSessionType").val()
												 +"&deviceType="+$("#selectedQuestionType").val()
												 +"&group="+$("#selectedGroup").val()
												 +"&status="+$("#selectedStatus").val()
												 +"&role="+$("#srole").val() 
												 + "&answeringDate=" + $("#selectedAnsweringDate").val();
								if($("#selectedHouseType").val()=='lowerhouse' && $("#chartCreatePermissionLowerhouse").val()=='yes'){
									$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
									var resourceURL = 'chart/create?' + parameters;
									$.get(resourceURL, function(data) {
										var displayMessage = data;
										if(data == "CREATED" || data == "ALREADY_EXISTS") {
											var newResourceURL = 'chart/view?' + parameters;
											$.get(newResourceURL,function(data){
												$("#chartResultDiv").empty();
												$("#chartResultDiv").html(data);
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
										}
										else if(data == "PREVIOUS_CHART_IS_NOT_PROCESSED") {
											displayMessage = "Previos Chart is not Processed. Kindly process it before creating a new Chart.";
											$.unblockUI();
											$.fancybox.open(displayMessage);
										}
										else {
											displayMessage = "Error Occurred while creating Chart";
											$.unblockUI();
											$.fancybox.open(displayMessage);
										}
									}).fail(function(){
										$.unblockUI();
										if($("#ErrorMsg").val()!=''){
											$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
										}else{
											$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
										}
										scrollTop();
									});
								}else if($("#selectedHouseType").val()=='upperhouse' && $("#chartCreatePermissionUpperhouse").val()=='yes'){
									$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
									var resourceURL = 'chart/create?' + parameters;
									$.get(resourceURL, function(data) {
										var displayMessage = data;
										if(data == "CREATED" || data == "ALREADY_EXISTS") {
											var newResourceURL = 'chart/view?' + parameters;
											$.get(newResourceURL,function(data){
												$("#chartResultDiv").empty();
												$("#chartResultDiv").html(data);
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
										}
										else if(data == "PREVIOUS_CHART_IS_NOT_PROCESSED") {
											displayMessage = "Previos Chart is not Processed. Kindly process it before creating a new Chart.";
											$.unblockUI();
											$.fancybox.open(displayMessage);
										}
										else {
											displayMessage = "Error Occurred while creating Chart";
											$.unblockUI();
											$.fancybox.open(displayMessage);
										}
									}).fail(function(){
										$.unblockUI();
										if($("#ErrorMsg").val()!=''){
											$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
										}else{
											$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
										}
										scrollTop();
									});
								}

							}else if($('#currentDeviceType').val()=="motions_standalonemotion_halfhourdiscussion"){
								$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
								var parameters = "houseType="+$("#selectedHouseType").val()
												 +"&sessionYear="+$("#selectedSessionYear").val()
												 +"&sessionType="+$("#selectedSessionType").val()
												 +"&deviceType="+$("#selectedQuestionType").val()
												 +"&status="+$("#selectedStatus").val()
												 +"&role="+$("#srole").val();
								var resourceURL = 'chart/create?' + parameters;
								$.get(resourceURL, function(data) {
									var displayMessage = data;
									if(data == "CREATED" || data == "ALREADY_EXISTS") {
										var newResourceURL = 'chart/view?' + parameters;
										$.get(newResourceURL,function(data){
											$("#chartResultDiv").empty();
											$("#chartResultDiv").html(data);
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
									}
									else if(data == "PREVIOUS_CHART_IS_NOT_PROCESSED") {
										displayMessage = "Previos Chart is not Processed. Kindly process it before creating a new Chart.";
										$.unblockUI();
										$.fancybox.open(displayMessage);
									}
									else {
										displayMessage = "Error Occurred while creating Chart";
										$.unblockUI();
										$.fancybox.open(displayMessage);
									}
								}).fail(function(){
									$.unblockUI();
									if($("#ErrorMsg").val()!=''){
										$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
									}else{
										$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
									}
									scrollTop();
								});
							}
				   		}
					}
				});
				return false;								
			});
			
			$("#view_chart").click(function(){
				if($('#currentDeviceType').val()=='resolutions_nonofficial'){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&deviceType="+$("#selectedDeviceType").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val();
					var resourceURL = 'chart/view?' + parameters;
					$.get(resourceURL,function(data){
						$("#chartResultDiv").empty();
						$("#chartResultDiv").html(data);
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
				}else if($('#currentDeviceType').val()=="questions_starred"){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var parameters = "houseType="+$("#selectedHouseType").val()
									 +"&sessionYear="+$("#selectedSessionYear").val()
									 +"&sessionType="+$("#selectedSessionType").val()
									 +"&deviceType="+$("#selectedQuestionType").val()
									 +"&group="+$("#selectedGroup").val()
									 +"&status="+$("#selectedStatus").val()
									 +"&role="+$("#srole").val() 
									 + "&answeringDate=" + $("#selectedAnsweringDate").val();
					var resourceURL = 'chart/view?' + parameters;
					$.get(resourceURL,function(data){
						$("#chartResultDiv").empty();
						$("#chartResultDiv").html(data);
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
				}else if($('#currentDeviceType').val()=="motions_standalonemotion_halfhourdiscussion"){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					var parameters = "houseType="+$("#selectedHouseType").val()
					 	+"&sessionYear="+$("#selectedSessionYear").val()
					 	+"&sessionType="+$("#selectedSessionType").val()
					 	+"&deviceType="+$("#selectedQuestionType").val()
					 	+"&status="+$("#selectedStatus").val()
					 	+"&role="+$("#srole").val();
					var resourceURL = 'chart/view?' + parameters;
					$.get(resourceURL,function(data){
						$("#chartResultDiv").empty();
						$("#chartResultDiv").html(data);
						$.unblockUI();				
					}, 'html').fail(function(){
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});
				}	
			});
			
			$("#refresh_chart").click(function(){
				var parameters = $("#gridURLParams").val();
				if($('#currentDeviceType').val()=='resolutions_nonofficial'){
					if(parameters==undefined){
						parameters = "houseType="+$("#selectedHouseType").val()
						 +"&sessionYear="+$("#selectedSessionYear").val()
						 +"&sessionType="+$("#selectedSessionType").val()
						 +"&deviceType="+$("#selectedDeviceType").val()
						 +"&ugparam="+$("#ugparam").val()
						 +"&status="+$("#selectedStatus").val()
						 +"&role="+$("#srole").val()
						 +"&usergroup="+$("#currentusergroup").val()
						 +"&usergroupType="+$("#currentusergroupType").val();
					}
				}else if($('#currentDeviceType').val()=="questions_starred"){
					if(parameters==undefined){
						parameters = "houseType="+$("#selectedHouseType").val()
						 +"&sessionYear="+$("#selectedSessionYear").val()
						 +"&sessionType="+$("#selectedSessionType").val()
						 +"&questionType="+$("#selectedQuestionType").val()
						 +"&ugparam="+$("#ugparam").val()
						 +"&status="+$("#selectedStatus").val()
						 +"&role="+$("#srole").val()
						 +"&usergroup="+$("#currentusergroup").val()
						 +"&usergroupType="+$("#currentusergroupType").val();
					}
					 parameters = parameters + "&group=" + $("#selectedGroup").val();
				}else if($('#currentDeviceType').val()=="motions_standalonemotion_halfhourdiscussion"){
					if(parameters==undefined){
						parameters = "houseType="+$("#selectedHouseType").val()
					 	+"&sessionYear="+$("#selectedSessionYear").val()
					 	+"&sessionType="+$("#selectedSessionType").val()
					 	+"&deviceType="+$("#selectedQuestionType").val()
					 	+"&status="+$("#selectedStatus").val()
					 	+"&role="+$("#srole").val();
					}
				}
				var resourceURL = 'chart/init?' + parameters;
				showTabByIdAndUrl('chart_tab', resourceURL);
			});	
			$("#selectedAnsweringDate").change(function(){
				$("#chartAnsweringDate").val($(this).val());
			});	
		});

		function view_chart() {
			if($('#currentDeviceType').val()=='resolutions_nonofficial'){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
				 	+"&sessionYear="+$("#selectedSessionYear").val()
				 	+"&sessionType="+$("#selectedSessionType").val()
				 	+"&deviceType="+$("#selectedDeviceType").val()
				 	+"&status="+$("#selectedStatus").val()
				 	+"&role="+$("#srole").val(); 
				var resourceURL = 'chart/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#chartResultDiv").empty();
					$("#chartResultDiv").html(data);
					$.unblockUI();				
				}, 'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else if($('#currentDeviceType').val()=="questions_starred"){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var chartansweringDate="";
				if($("#chartAnsweringDate").val()=='-'){
					chartansweringDate=$("#selectedAnsweringDate").val();
				}else{
					chartansweringDate=$("#chartAnsweringDate").val();
					$("#selectedAnsweringDate").val(chartansweringDate);
				}
				var parameters = "houseType="+$("#selectedHouseType").val()
				 	+"&sessionYear="+$("#selectedSessionYear").val()
				 	+"&sessionType="+$("#selectedSessionType").val()
				 	+"&deviceType="+$("#selectedQuestionType").val()
				 	+"&group="+$("#selectedGroup").val()
				 	+"&status="+$("#selectedStatus").val()
				 	+"&role="+$("#srole").val() 
				 	+ "&answeringDate=" + chartansweringDate;
				var resourceURL = 'chart/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#chartResultDiv").empty();
					$("#chartResultDiv").html(data);
					$.unblockUI();				
				}, 'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else if($('#currentDeviceType').val()=="motions_standalonemotion_halfhourdiscussion"){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
				 	+"&sessionYear="+$("#selectedSessionYear").val()
				 	+"&sessionType="+$("#selectedSessionType").val()
				 	+"&deviceType="+$("#selectedQuestionType").val()
				 	+"&status="+$("#selectedStatus").val()
				 	+"&role="+$("#srole").val();
				var resourceURL = 'chart/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#chartResultDiv").empty();
					$("#chartResultDiv").html(data);
					$.unblockUI();				
				}, 'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}			
		}	
		/**** detail of clubbed and refernced questions ****/			
		function viewQuestionDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#selectedQuestionType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='question/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}	
		/**** Clubbing ****/
		function clubbingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
						+"&usergroup="+$("#currentusergroup").val()
				        +"&usergroupType="+$("#currentusergroupType").val();		
			$.get('clubentity/init?'+params,function(data){
				$.unblockUI();	
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#clubbingResultDiv").html(data);
				$("#clubbingResultDiv").show();
				$("#referencingResultDiv").hide();
				$("#selectionDiv2").hide();				
				$("#chartResultDiv").hide();
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		/**** Referencing ****/
		function referencingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val()
			+"&deviceType="+$("#deviceType_Chart").val();
			$.get('refentity/init?'+params,function(data){
				$.unblockUI();			
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#referencingResultDiv").html(data);
				$("#referencingResultDiv").show();
				$("#clubbingResultDiv").hide();
				$("#selectionDiv2").hide();
				$("#chartResultDiv").hide();
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}	
		
		function chartAnsweringDateChanged(id){
			$.get('question/questionId/'+id,{strusergroupType: $("#currentusergroupType").val()},function(data){
			    $.fancybox.open(data);
		    }).fail(function(){
    			if($("#ErrorMsg").val()!=''){
    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
    			}else{
    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    			}
    			scrollTop();
    		});
		}
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">	
	<c:if test="${answeringDates!=null}">
		<c:if test="${not empty answeringDates}" >
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="chartinitial.answeringdate" text="Answering Date"/>
			</a>
			<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
				<c:forEach items="${answeringDates}" var="i">			
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
				</c:forEach> 
			</select> |
		</c:if>
	</c:if>
	<security:authorize access="hasAnyRole('QIS_ASSISTANT', 'QIS_CLERK', 'SUPER_ADMIN','ROIS_CLERK','ROIS_ASSISTANT', 'SMOIS_ASSISTANT', 'SMOIS_CLERK')">
	<a href="#" id="create_chart" class="butSim">
		<spring:message code="chartinitial.createchart" text="Create Chart"/>
	</a> |
	</security:authorize>
	<a href="#" id="view_chart" class="butSim">
		<spring:message code="chartinitial.viewchart" text="View Chart"/>
	</a> |
	<a href="#" id="refresh_chart" class="butSim">
		<spring:message code="chartinitial.refreshchart" text="Refresh Chart"/>
	</a>
</div>
	
<br />
<br />
<div id="chartResultDiv">
</div>

<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
<input id="usergroupType" name="usergroupType" type="hidden" value="${usergroupType}">
<ul id="contextMenuItems" >
	<c:if test="${deviceType=='questions_starred'}" >
		<li><a href="#clubbing" class="edit"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a></li>
	</c:if>
	<li><a href="#referencing" class="edit"><spring:message code="generic.referencing" text="Referencing"></spring:message></a></li>
	<li><a href="#chart_answering_date_change" id="chart_answering_date_change" class="edit"><spring:message code="question.chart_answering_date" text="Shift Chart"/></a></li>
</ul>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingResultDiv" style="display:none;">
</div>
<input type="hidden" id="deviceType_Chart" value="${deviceType}" />
<input type="hidden" id="maxChartAnsweringDate" value="${maxChartAnsweringDate}" />
<%-- <input id="chartAnsweringChangeDate" name="chartAnsweringChangeDate" type="hidden" value="${showChartingAnsweringChange}"> --%>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="chartCreatePermissionLowerhouse" value="<spring:message code='chart.createpermission.lowerhouse'/>"/>
<input type="hidden" id="chartCreatePermissionUpperhouse" value="<spring:message code='chart.createpermission.upperhouse'/>"/>
<input type="hidden" id="chartCreationConfirmationMsg" value="<spring:message code='chart.creation.confirmationMessage' text='Are you sure you want to create this chart now?'/>"/>
</body>
</html>