<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#pre_ballot").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'ballot/preballot?' + parameters;
				
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					$.unblockUI();					
				},'html');
					
			});	
				
			$("#create_ballot").click(function() {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&questionType="+$("#selectedQuestionType").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val() 
				 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'ballot/create?' + parameters;
				$.get(resourceURL, function(data) {
					var displayMessage = data;
					if(data == "CREATED" || data == "ALREADY_EXISTS") {
						var newResourceURL = 'ballot/view?' + parameters;
						$.get(newResourceURL,function(data){
							$("#ballotResultDiv").empty();
							$("#ballotResultDiv").html(data);
							$.unblockUI();					
						},'html');
					}
					else {
						displayMessage = "Error Occurred while creating Ballot";
						$.unblockUI();
						$.fancybox.open(displayMessage);
					}
				});
				
			});		
			
			$("#view_ballot").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var parameters = "houseType="+$("#selectedHouseType").val()
								 +"&sessionYear="+$("#selectedSessionYear").val()
								 +"&sessionType="+$("#selectedSessionType").val()
								 +"&questionType="+$("#selectedQuestionType").val()
								 +"&group="+$("#selectedGroup").val()
								 +"&status="+$("#selectedStatus").val()
								 +"&role="+$("#srole").val() 
								 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				var resourceURL = 'ballot/view?' + parameters;
				$.get(resourceURL,function(data){
					$("#ballotResultDiv").empty();
					$("#ballotResultDiv").html(data);
					$.unblockUI();					
				},'html');
					
			});	
		});		
	</script>
</head>

<body>
<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv2">		
			<a href="#" id="select_answeringdate" class="butSim">
				<spring:message code="ballotinitial.answeringdate" text="Answering Date"/>
			</a>
			<c:choose>
				<c:when test="${deviceTypeType == 'questions_starred'}">
					<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
					<c:forEach items="${answeringDates}" var="i">			
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach> 
					</select>
				</c:when>
				<c:when test="${deviceTypeType == 'questions_halfhourdiscussion_from_question' or deviceTypeType == 'questions_halfhourdiscussion_standalone'}">
					<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
					<c:forEach items="${answeringDates}" var="i">			
						<option value="${i.value}"><c:out value="${i.name}"></c:out></option>	
					</c:forEach> 
					</select>
				</c:when>
			</c:choose>
			 | 
			<a href="#" id="pre_ballot" class="butSim">
				<spring:message code="ballotinitial.preballot" text="Pre Ballot"/>
			</a> |
			<a href="#" id="create_ballot" class="butSim">
				<spring:message code="ballotinitial.createballot" text="Create Ballot"/>
			</a> |
			<a href="#" id="view_ballot" class="butSim">
				<spring:message code="ballotinitial.viewballot" text="View Ballot"/>
			</a>
</div>
<div id="ballotResultDiv">
</div>
</body>
</html>