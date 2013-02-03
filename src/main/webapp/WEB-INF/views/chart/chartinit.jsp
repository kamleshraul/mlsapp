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
			$("#refresh_chart").click(function(){
					var parameters = $("#gridURLParams").val();
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
					var parameters = parameters + "&group=" + $("#selectedGroup").val();
					var resourceURL = 'chart/init?' + parameters;
					showTabByIdAndUrl('chart_tab', resourceURL);
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
			},'html');	
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
			},'html');
		}
		/**** Referencing ****/
		function referencingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val();
			$.get('refentity/init?'+params,function(data){
				$.unblockUI();			
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#referencingResultDiv").html(data);
				$("#referencingResultDiv").show();
				$("#clubbingResultDiv").hide();
				$("#selectionDiv2").hide();
				$("#chartResultDiv").hide();
			},'html');
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
			</a> |
			<a href="#" id="refresh_chart" class="butSim">
				<spring:message code="chartinitial.refreshchart" text="Refresh Chart"/>
			</a>
</div>
<div id="chartResultDiv">
</div>

<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
<input id="usergroupType" name="usergroupType" type="hidden" value="${usergroupType}">
<ul id="contextMenuItems" >
<li><a href="#clubbing" class="edit"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a></li>
<li><a href="#referencing" class="edit"><spring:message code="generic.referencing" text="Referencing"></spring:message></a></li>
</ul>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingresultDiv" style="display:none;">
</div>
</body>
</html>