<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			view_chart();
			
			$("#create_chart").click(function() {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'chart/create?' + parameters;
				$.get(resourceURL, function(data) {
					var displayMessage = data;
					if(data == "CREATED" || data == "ALREADY_EXISTS") {
						var newResourceURL = 'chart/view?' + parameters;
						$.get(newResourceURL,function(data){
							$("#chartResultDiv").empty();
							$("#chartResultDiv").html(data);
							$.unblockUI();					
						},'html');
					}
					else if(data == "PREVIOUS_CHART_IS_NOT_PROCESSED") {
						displayMessage = "Previos Chart is not Processed. Kindly process it before creating a new Chart.";
						$.unblockUI();
						$.fancybox.open(displayMessage);
					}
				});
			});
			
			$("#view_chart").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'chart/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#chartResultDiv").empty();
					$("#chartResultDiv").html(data);
					$.unblockUI();					
				},'html');
					
			});	
		});

		function view_chart() {
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters = "houseType="+$("#selectedHouseType").val()
			 	+"&sessionYear="+$("#selectedSessionYear").val()
			 	+"&sessionType="+$("#selectedSessionType").val()
			 	+"&questionType="+$("#selectedQuestionType").val()
			 	+"&group="+$("#selectedGroup").val()
			 	+"&status="+$("#selectedStatus").val()
			 	+"&role="+$("#srole").val() 
			 	+ "&answeringDate=" + $("#selectedAnsweringDate").val();
			var resourceURL = 'chart/view?' + parameters;
			$.get(resourceURL,function(data){
				$("#chartResultDiv").empty();
				$("#chartResultDiv").html(data);
				$.unblockUI();				
			}, 'html');
		}	
	</script>
</head>

<body>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">		
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="chartinitial.answeringdate" text="Answering Date"/>
			</a>
			<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
			<c:forEach items="${answeringDates}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> | 
			<security:authorize access="hasAnyRole('QIS_ASSISTANT', 'SUPER_ADMIN')">
			<a href="#" id="create_chart" class="butSim">
				<spring:message code="chartinitial.createchart" text="Create Chart"/>
			</a> |
			</security:authorize>
			<a href="#" id="view_chart" class="butSim">
				<spring:message code="chartinitial.viewchart" text="View Chart"/>
			</a>
</div>
<div id="chartResultDiv">
</div>
</body>
</html>