<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		
		$(document).ready(function(){
			$("#members").change(function(){
				getMemberResolutions($(this).val());		
			});
						
			$(".dev").click(function(e){
				
				var divId = $(this).attr('id');
				var qId = divId.substring(3);
				var member = $("#opt"+qId).parent().attr('id').substring(2);
				//(member+"\n"+$("#addedMembers").val().trim().indexOf(member));
				if($("#finalSelection").val().trim()==''){
					
					if($("#addedMembers").val().trim().indexOf(member)<0){
						$("#finalSelection").val(qId);
						$(this).css({'display':'none'});						
					}
				}else{
					var lengthOfIds = $("#finalSelection").val().split(":").length;
					
					if($("#addedMembers").val().trim().indexOf(member)<0){
						if(lengthOfIds < 2){
							var finalVar = $("#finalSelection").val() + ":"+qId;
							$("#finalSelection").val(finalVar);
							$(this).css({'display':'none'});							
						}
					}
				}
				
				if($("#finalSelDiv").html().trim()==''){
					if($("#addedMembers").val().trim().indexOf(member)<0){
						$("#finalSelDiv").html($("#opt"+qId).text().trim());
						$(this).css({'display':'none'});
					}
				}else{
					var lengthOfIds = $("#finalSelDiv").html().trim().split(",").length;
					
					if($("#addedMembers").val().trim().indexOf(member)<0){
						if(lengthOfIds < 2){
							var html = $("#finalSelDiv").html() + ", " + $("#opt"+qId).text().trim();
							$("#finalSelDiv").html(html);
							$(this).css({'display':'none'});
						}
					}
				}
				
				
				if($("#addedMembers").val().trim()==''){
					$("#addedMembers").val(member);
				}else{
					if($("#addedMembers").val().trim().indexOf(member)<0){
						$("#addedMembers").val($("#addedMembers").val()+"," + member);
					}
				}
				
			});
			
			$("#submitQues").click(function(e){
				var ids = $("#finalSelection").val();
				/* var session = 0;
				
				var url = "ref/sessionbyhousetype/" + $("#selectedHouseType").val()
				+ "/" + $("#selectedSessionYear").val()
				+ "/" + $("#selectedSessionType").val();
				
				$.get('ref/session', function(data){
					if(data){
						session = data.id;
					}
				}).done(function(){
					$.post("ballot/updateHDQ?ids=" + ids + "&sessionId=" + session
							+ "&deviceTypeId=" + $("#selectedDeviceType").val()
							+ "&deviceTypeId=" + $("#selectedDeviceType").val()
							+"&answeringDate=" + $("#selectedAnsweringDate").val(), function(data){					
					});
				}); */	
				
				$.post("ballot/updateHDQ?ids=" + ids+"&answeringDate=" + $("#selectedAnsweringDate").val(), function(data){
					$("#balUpdateDiv").empty();
					$("#balUpdateDiv").html(data);
				},"html");
			});
			
			$("#resetQues").click(function(e){
				$(".dev").css({'display':'inline'});
				$("#finalSelDiv").empty();
				$("#finalSelection").val('');
				$("#addedMembers").val('');
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
			width:12px; 
			height:12px; 
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
	<div style="padding: 20px; border: 5px solid black; border-radius: 10px; height: 400px; width: 800px;" id="balUpdateDiv">
		<table border="1">
			<c:forEach items="${data}" var="m">
				<tr>
					<td>
						<div style="min-height: 25px; padding: 10px; border: 2px solid black; border-radius: 5px; background-color: #E0E0EB; font-weight: bold; font-size: 12pt;">
							${m.value[0].name}
						</div>
					</td>
					<td>
						<div style="min-height: 25px; padding: 10px; border: 2px solid black; border-radius: 5px; background-color: #E0E0EB;">
							<select id="sel${m.key}" style="display: none;">
								<c:forEach items="${m.value}" var="q">
									<option value="${q.id}" id="opt${q.id}">${q.formattedNumber}</option>				
								</c:forEach>
							</select>
							
							<c:forEach items="${m.value}" var="q">
								<div class="dev" id="dev${q.id}" style="display: inline; padding: 5px;  margin: 5px; border: 1px solid black; border-radius: 2px; width: 20px; height: 20px; max-width: 20px; max-height: 20px; background-color: #4D944D; cursor: pointer;">
									${q.formattedNumber}
								</div>	
							</c:forEach>
						</div>
					</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="2">
						<div id="finalSelDiv">
							
						</div>
						<input type="hidden" id="addedMembers" value="" />
						<input id="finalSelection" name="finalSelection" type="hidden" value="" />
						<br>
						<input type="button" class="butDef" style="float: right;" id="submitQues" value="Submit" />
						<input type="button" class="butDef" style="float: right;" id="resetQues" value="Reset" />
				</td>
			</tr>			
		</table>		
	</div>
	<%-- <c:choose>
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
									<select id="members" name="member" class="sSelect">
										<option value="-" selected="selected">---<spring:message code='please.select' text='Please Select'/>---</option>
										<c:forEach items="${members}" var="m">
											<option value="${m.id}">${m.value}</option>
										</c:forEach>
									</select><br /><br />
									
									<select id="memberChoice" name="choice" size="5" class="member_choice_list" style="display: none;">									
									</select><br /><br />
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
	</c:choose> --%>
</div>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>