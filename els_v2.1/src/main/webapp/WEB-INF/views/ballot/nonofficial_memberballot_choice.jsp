<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		function getMemberResolutions(value){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			if(value!='' && value!='-'){
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val() 
				 +"&answeringDate=" + $("#selectedAnsweringDate").val()
				 +"&isQuestion=no";
				
				var resourceURL = 'ref/member_choice_resolutions?' + parameters+"&member="+value;
				
				$.get(resourceURL,function(data){
					if(data.length > 0){
						var text = "";
						for(var i = 0; i < data.length; i++){
							text += "<option value='"+data[i].id+"'>"+data[i].number+"</option>";
						}
						$("#memberChoice").empty();
						$("#memberChoice").html(text);
						$("#memberChoice").show();
						$.unblockUI();	
					}
				}).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
			}else{
				$("#memberChoice").empty();
				$("#memberChoice").hide();
				$.unblockUI();
			}
		}
		$(document).ready(function(){
			$("#members").change(function(){
				getMemberResolutions($(this).val());		
			});
			
			$("#submitChoice").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var resourceURL;
				var parameters;
				
				parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 +"&group="+$("#selectedGroup").val()
				 +"&status="+$("#selectedStatus").val()
				 +"&role="+$("#srole").val() 
				 + "&answeringDate=" + $("#selectedAnsweringDate").val();
				resourceURL = 'ballot/fillresolutionchoice?' + parameters;
				$.post(resourceURL,  
			            $("form").serialize(),  
			            function(data){
							$("#memberChoice").empty();	
		   					//$("#memberChoice").html(data);	
		   					$("#memberChoice").hide();	   					
							
							var resourceURL_L = 'ballot/fillresolutionchoices?' + parameters;
							$.get(resourceURL_L,function(data){
								$("#ballotResultDiv").empty();
								$("#ballotResultDiv").html(data);
								$.unblockUI();					
							},'html');
						
		   					$('html').animate({scrollTop:0}, 'slow');
		   				 	$('body').animate({scrollTop:0}, 'slow');	
							$.unblockUI();	   				 	   				
			            },'html').fail(function(){
							$.unblockUI();
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.");
							}
							scrollTop();
						});
			  	return false; 
			});
		});
	</script>
	<style type="text/css">
		.member_choice_list{
			width: 180px;
		}
		#memberChoiceDiv{
			margin-left: 100px;
		}
	</style>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=3" media="print" />
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="reportDiv">
	<div id="choiceParentDiv" style="margin: 10px;">
		<div id="membersDiv">
			<c:choose>
				<c:when test="${members == null}">
					<spring:message code="resolution.ballot.notchosen" text="Ballot Choice is not Available"/>
				</c:when>
				
				<c:when test="${empty members}">
					<spring:message code="resolution.ballot.nomemberleft" text="There are no members left to submit choice"/>
				</c:when>
				
				<c:otherwise>
					<div id="memberChoiceDiv">
						<form action="ballot/fillresolutionchoice" method="POST" >
							<select id="members" name="member" class="sSelect">
								<option value="-" selected="selected">---<spring:message code='please.select' text='Please Select'/>---</option>
								<c:forEach items="${members}" var="m">
									<option value="${m.id}">${m.value}</option>
								</c:forEach>
							</select><br /><br />
							
							<select id="memberChoice" name="choice" size="5" class="member_choice_list" style="display: none;">									
							</select><br /><br />
							
							<input type="button" id="submitChoice" value="Submit Choice" class="butDef" />
						</form>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>