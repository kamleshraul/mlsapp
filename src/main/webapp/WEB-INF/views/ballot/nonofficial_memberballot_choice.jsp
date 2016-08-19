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
	<script type="text/javascript">
		var srcId="";
		var destDiv="";
		
		function allowDrop(ev){
			if(srcId==""){
				var id = $(ev.target).attr('id');
				if(/^memResos/.test(id)){
					srcId=$(ev.target).attr('id').substring(8);	
				}else if(/^memChoice/.test(id)){
					srcId=$(ev.target).attr('id').substring(9);
				}
			}			
			ev.preventDefault();
		}
	
		function drag(ev){
			ev.dataTransfer.setData("Text",ev.target.id);
		}
	
		function drop(ev){
			ev.preventDefault();			
			var data=ev.dataTransfer.getData("Text");
			var id=$(ev.target).attr('id');
			var destId="";
			if((/^memResos/.test(id)) || $("#"+id).children(".memRes").length==0){
				if(/^memResos/.test(id)){
					destId=$(ev.target).attr('id').substring(8);	
				}else if(/^memChoice/.test(id)){
					destId=$(ev.target).attr('id').substring(9);
				}
				
				if(srcId==destId){
					var element=$(ev.target).children(".validity");
					console.log($(element).val());
					if($(element).val()=='1'){
						ev.target.appendChild(document.getElementById(data));
						
						if(/^memResos/.test(id)){
							$("#memChoiceResos"+destId).val("");	
						}else if(/^memChoice/.test(id)){
							$("#memChoiceResos"+destId).val(data.substring(6));
						}	
					}
				}
			}
			srcId="";
		}
	</script>
	<style type="text/css">
		.member_choice_list{
			width: 180px;
		}
		#memberChoiceDiv{
			margin-left: 100px;
		}
		
		.memRes{
			border: 1px solid black;
			margin: 2px; 
			width:14px; 
			height:14px; 
			float: left; 
			position: relative; 
			padding: 5px; 
			text-align: center; 
			border-radius: 5px;
		}
		.memberResolutions > .memRes{ 
			background-color: #FFDBA8;
		}
		
		.memberChoice > .memRes{
			background-color: #AFCCA1;
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
	<c:choose>
		<c:when test="${choicedone=='yes'}">
			<div class="toolTip tpGreen clearfix">
					<p style="font-size: 14px;">
						<img src="./resources/images/template/icons/light-bulb-on.png" width="16px" height="16px">
						<spring:message code="ballot.info.choicedone" text="Choices have been submitted."/>
					</p>
			</div>
		</c:when>
		<c:when test="${choicedone=='no'}">
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
									<%-- <select id="members" name="member" class="sSelect">
										<option value="-" selected="selected">---<spring:message code='please.select' text='Please Select'/>---</option>
										<c:forEach items="${members}" var="m">
											<option value="${m.id}">${m.value}</option>
										</c:forEach>
									</select><br /><br />
									
									<select id="memberChoice" name="choice" size="5" class="member_choice_list" style="display: none;">									
									</select><br /><br /> --%>
									<table style="border: 1px solid black; width: 750px;" border="1">
										<thead>
											<tr>
												<td style="text-align: center; width: 250px;">
													<spring:message code="resolution.ballot.choice.membername" text="Member Name"></spring:message>
												</td>
												<td style="text-align: center; width: 250px;">
													<spring:message code="resolution.ballot.resolution.admitted" text="Admitted Resolutions"></spring:message>
												</td>
												<td style="text-align: center; width: 250px;">
													<spring:message code="resolution.ballot.choice" text="Chosen Resolution"></spring:message>
												</td>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${members}" var="m">
												<tr>
													<td>
														<span id="mem${m.id}">${m.name}</span>
													</td>
													<td>
														<div id="memResos${m.id}" class="memberResolutions" ondrop="drop(event)" ondragover="allowDrop(event)" style="width: 250px; height: 30px;">
															<c:forEach items="${memberRes[m.id]}" var="mr" varStatus="counter">
																<div id="memRes${mr.id}" class="memRes" draggable="true" ondragstart="drag(event)">${mr.name}</div>
															</c:forEach>
															<input type="hidden" id="validityResos${m.id}" class="validity"  value="${m.value}" />
														</div>
													</td>
													<td>
														<div id="memChoice${m.id}" class="memberChoice" ondrop="drop(event)" ondragover="allowDrop(event)" style="width: 250px; height: 30px;" >
															<c:forEach items="${memberChosenRes[m.id]}" var="mcr">
																<div id="memRes${mcr.id}" class="memRes" draggable="true" ondragstart="drag(event)">${mcr.name}</div>
															</c:forEach>
															<input type="hidden" id="validityChoice${m.id}" class="validity" value="${m.value}" />
														</div>
														<div id="memChoiceResosDiv${m.id}" class="memChoiceResosDiv">
															<input type="hidden" id="memChoiceResos${m.id}" value="" name="choice" />
														</div>												
													</td>											
												</tr>
											</c:forEach>
											<tr>
												<td colspan="3" style="height: 20px; padding: 5px">
													<div style="float: right;">
														<input type="button" id="submitChoice" value="Submit Choice" class="butDef" />
													</div>
												</td>
											</tr>
										</tbody>
									</table>
								</form>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</c:when>
	</c:choose>
</div>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>