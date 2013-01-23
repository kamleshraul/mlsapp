<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {		
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
					if(data == "CREATED") {
						displayMessage = "Ballot is successfully created.";
					}
					else if(data == "ALREADY_EXISTS") {
						displayMessage = "Ballot already exists.";
					}
					$.unblockUI();
					$.fancybox.open(displayMessage);
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
			<select name="selectedAnsweringDate" id="selectedAnsweringDate" style="width:100px;height: 25px;">				
			<c:forEach items="${answeringDates}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> | 
			<a href="#" id="create_ballot" class="butSim">
				<spring:message code="ballotinitial.createballot" text="Create Ballot"/>
			</a> |
			<a href="#" id="view_ballot" class="butSim">
				<spring:message code="chartinitial.viewchart" text="View Ballot"/>
			</a>
</div>
<div id="ballotResultDiv">
</div>
</body>
</html>