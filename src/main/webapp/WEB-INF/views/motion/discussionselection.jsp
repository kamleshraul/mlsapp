<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
		//$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		//$.unblockUI();
		//$.fancybox.open(data,{autoSize:false,width:750,height:700});
		//$.unblockUI();
		
		function loadMemberByParty(party){
			
			var targetURL = "ref/party/memberbyparty?partyId=" + party;
			$.get(targetURL, function(data){
				var text = "<option value='-'>" + $("#pleaseSelect").val() + "</option>";
				if(data){
					
					for(var i = 0; i < data.length; i++){
						text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";						
					}
					
					$("#ds_selectedMember").empty();
					$("#ds_selectedMember").html(text);
				}
			});
		}
	
		$(document).ready(function(){
			$("#ds_selectedParty").change(function(e){
				var party = $(this).val();
				if(party != '' && party != '-'){
					loadMemberByParty(party);
				}else{
					var pleaseSelect = "<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					$("#ds_selectedMember").empty();
					$("#ds_selectedMember").html(pleaseSelect);
					
					$("#deviceSelector").empty();
					$.get('ref/alladmitted/motions?' + 
							'houseType=' + $("#ds_selectedHouseType").val() + 
							'&sessionYear=' + $("#ds_selectedSessionYear").val() + 
							'&sessionType=' + $("#ds_selectedSessionType").val() +
							'&deviceType=' + $("#deviceType").val(), function(data){
								var text = "<option value='-'>-- " + $("#pleaseSelect").val() + " --</option>";
								
								for(var i = 0; i < data.length; i++){
									text += "<option value='" + data[i].id + ":" + data[i].name + "'>" +data[i].name + "</option>";
								}
								
								$("#deviceSelector").empty();
								$("#deviceSelector").html(text);	
					});					
				}
			});
			
			$("#deviceSelector").change(function(e){
				if($(this).val() != '-'){
					var id = $(this).val();
					var text = $("option[value='" + id + "']").text();
					
					var node = "<div class='actualDevices' id='acD" + id + "' style='background: #99CCFF; border: 1px solid black; border-radius: 5px; padding: 4px; margin-left: 4px; display: inline; text-align: center;'>" + text + "</div>";
					if($(".actualDevices").length > 0){
						
						if($(".actualDevices").length <= 3){
							
							if($(".actualDevices[id='acD" + id + "']").length==0){
								$("#actualDiscussionDateDevices").append(node);
								
								if($("#actualDeviceData").val()!=''){
									$("#actualDeviceData").val($("#actualDeviceData").val()+"~"+id);
								}else if($("#actualDeviceData").val()==''){
									$("#actualDeviceData").val(id);
								}
								$("#deviceSelector option[value='" + id + "']").remove();
							}else{
								$("#deviceSelector option[value='" + id + "']").remove();
							}
						}else{
							
							if($(".actualDevices").length > 4){
								if($(".actualDevices[id='acD" + id + "']").length==0){
									$("#actualDiscussionDateDevices").append(node);
									
									if($("#actualDeviceData").val()!=''){
										$("#actualDeviceData").val($("#actualDeviceData").val()+"~"+id);
									}else if($("#actualDeviceData").val()==''){
										$("#actualDeviceData").val(id);
									}
									$("#deviceSelector option[value='" + id + "']").remove();
								}else{
									$("#deviceSelector option[value='" + id + "']").remove();
								}
							}else{
								$.prompt($('#addMoreMessage').val(),{
									buttons: {Ok:true, Cancel:false}, callback: function(v){
							        if(v){
							        	if($(".actualDevices[id='acD" + id + "']").length==0){
											$("#actualDiscussionDateDevices").append(node);
											
											if($("#actualDeviceData").val()!=''){
												$("#actualDeviceData").val($("#actualDeviceData").val()+"~"+id);
											}else if($("#actualDeviceData").val()==''){
												$("#actualDeviceData").val(id);
											}
											$("#deviceSelector option[value='" + id + "']").remove();
										}else{
											$("#deviceSelector option[value='" + id + "']").remove();
										}
					    	        }
								}});
							}
						}
					}else{
						
						$("#actualDiscussionDateDevices").append(node);
						if($("#actualDeviceData").val()!=''){
							$("#actualDeviceData").val($("#actualDeviceData").val()+"~"+id);
						}else if($("#actualDeviceData").val()==''){
							$("#actualDeviceData").val(id);
						}
						$("#deviceSelector option[value='" + id + "']").remove();
											
					}
				}
			});
			
			$("#actualDiscussionDateDevices").click('.actualDevices', function(e){
				if($(e.target)!=undefined){
					var divId = $(e.target).attr('id');
					var actualId = divId.substring(3);
					
					if($(this).attr('id')!=divId){
						if($("option[value='" + actualId + "']").length==0){
							var text = "<option value='" + actualId + "'>" + divId.split(":")[1] + "</option>";
							//console.log(divId+"\n"+text);
							$("#deviceSelector").append(text);
						}
						$("#actualDiscussionDateDevices .actualDevices[id='" + divId +"']").remove();
					}
					
					var retainText = $("#actualDeviceData").val();
					var data = retainText.split("~");
					var retainedText = "";
					for(var i = 0; i < data.length; i++){
						if(data[i] != actualId){
							retainedText += data[i]+"~";
						}
					}
					//console.log("Pre: " + retainedText);
					retainedText = retainedText.substring(0,retainedText.length - 1);
					//console.log("Post: " + retainedText);
					$("#actualDeviceData").val(retainedText);
				}
			});
			
			$("#ds_selectedMember").change(function(e){
				var value = $(this).val();
				if(value !='' && value !='-'){
					$.get('ref/member/motions?member=' + value +
							'&houseType=' + $("#ds_selectedHouseType").val() + 
							'&sessionYear=' + $("#ds_selectedSessionYear").val() + 
							'&sessionType=' + $("#ds_selectedSessionType").val() +
							'&deviceType=' + $("#selectedMotionType").val(), 
							function(data){
								if(data.length > 0){
									var text = "<option value='-'>-- " + $("#pleaseSelect").val() + " --</option>";
									
									for(var i = 0; i < data.length; i++){
										text += "<option value='" + data[i].id + ":" + data[i].name + "'>" +data[i].name + "</option>";
									}
									
									$("#deviceSelector").empty();
									$("#deviceSelector").html(text);
								}else{
									var pleaseSelect = "<option value='-'>"+$("#pleaseSelect").val()+"</option>";
									$("#deviceSelector").empty();
									$("#deviceSelector").html(pleaseSelect);
								}
					});
				}else{
					var pleaseSelect = "<option value='-'>"+$("#pleaseSelect").val()+"</option>";
					$("#deviceSelector").empty();
					$("#deviceSelector").html(pleaseSelect);
				}
			});
			
			$("#submitDiscussionDate").click(function(e){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				//var formEle = $("form[action='motion/discussionselection']");
				$("#discussDate").val($(".formDiv").attr('id').substring(6));
				//alert($(".formDiv").attr('id').substring(6));
				$.post( $("form").attr('action')+"?houseType="+$("#ds_selectedHouseType").val()+
						"&sessionYear="+$("#ds_selectedSessionYear").val()+
						"&sessionType="+$("#ds_selectedSessionType").val()+
						"&deviceType="+$("#deviceType").val(), $("form").serialize(), function(){
					$.unblockUI();
				}).fail(function(){
					$.unblockUI();
				});
			});
		});
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
        .styleSelect select {
		   background: transparent;
		   width: 100px;
		   padding: 2px;
		   font-size: 12px;
		   line-height: 1;
		   border: 0;
		   border-radius: 10px;
		   height: 25px;
		   -webkit-appearance: none;
		  }
	
		.styleSelect{
		   width: 100px;
		   height: 25px;
		   overflow: hidden;
		   background: url(./resources/images/down_arrow_select_2.jpg) no-repeat right #ddd;
		   border: 1px solid #ccc;
		   box-shadow: 2px 2px 2px #000;
		   border_radius: 10px;
	   }
    </style>
</head> 

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix watermark">
	
		<div id="discussionSelectionMainDiv">
			<div id="discussionFilterDiv">
				<a href="#" id="ds_select_houseType" class="butSim">
					<spring:message code="motion.houseType" text="House Type"/>
				</a>
				<select name="ds_selectedHouseType" id="ds_selectedHouseType" class="styleSelect">			
					<c:forEach items="${houseTypes}" var="i">
						<c:choose>
							<c:when test="${houseType==i.type}">
								<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
							</c:when>
							<c:otherwise>
								<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select> |				
				<a href="#" id="ds_select_session_year" class="butSim">
					<spring:message code="motion.sessionyear" text="Year"/>
				</a>
				<select name="ds_selectedSessionYear" id="ds_selectedSessionYear" class="styleSelect">
					<c:forEach var="i" items="${years}">
						<c:choose>
							<c:when test="${i.number==sessionYear}">
								<option value="${i.number}" selected="selected"><c:out value="${i.value}"></c:out></option>				
							</c:when>
							<c:otherwise>
								<option value="${i.number}" ><c:out value="${i.value}"></c:out></option>			
							</c:otherwise>
						</c:choose>
					</c:forEach> 
				</select> |	
								
				<a href="#" id="ds_select_sessionType" class="butSim">
					<spring:message code="motion.sessionType" text="Session Type"/>
				</a>
				<select name="ds_selectedSessionType" id="ds_selectedSessionType" class="styleSelect">
					<c:forEach items="${sessionTypes}" var="i">
						<c:choose>
							<c:when test="${sessionType==i.id}">
								<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>			
							</c:otherwise>
						</c:choose>			
					</c:forEach> 
				</select> |	
				<a href="#" id="ds_select_party" class="butSim">
					<spring:message code="motion.party" text="Party"/>
				</a>
				<select name="ds_selectedParty" id="ds_selectedParty" class="styleSelect">		
					<option value="-" >-- <spring:message code="please.select" text="Please Select" /> --</option>
					<c:forEach var="i" items="${parties}">
						<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>
					</c:forEach> 
				</select> |				
				<a href="#" id="ds_select_member" class="butSim">
					<spring:message code="motion.member" text="Member"/>
				</a>
				<select name="ds_selectedMember" id="ds_selectedMember" class="styleSelect">		
					<option value="0" >-- <spring:message code="please.select" text="Please Select" /> --</option>
					<c:forEach var="i" items="${members}">
						<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>
					</c:forEach> 
				</select> |		
				
				<%-- <a href="#" id="select_motionType" class="butSim">
					<spring:message code="motion.questionType" text="Motion Type"/>
				</a>
				<select name="selectedMotionType" id="selectedMotionType" style="width:100px;height: 25px;">			
					<c:forEach items="${motionTypes}" var="i">
						<c:choose>
							<c:when test="${motionType==i.id}">
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select> 
				<select id="deviceTypeMaster" style="display:none;">
					<c:forEach items="${motionTypes }" var="i">
						<option value="${i.id }">${i.type }</option>
					</c:forEach>			
				</select>|	 --%>
				
			</div>
			<br />
			<br />
			<div id="formDiv">
				<form:form id="formToSubmit" action="motion/discussionselection" method="POST">
					<%@ include file="/common/info.jsp" %>
					<c:forEach items="${sessionDates}" var="s">
						<c:if test="${s.id==currDate}">
							<div id="ddddiv${s.id}" class="formDiv" style="width: 800px; height: 60px; border: 1px solid black; border-radius: 5px; box-shadow: 5px 5px #D8D8DF; padding: 5px;">
								<div style="width: 300px; font-size: 14px; font-weight: bold; float: left;">${s.name}</div>
								<div style="width: 500px; font-size: 12px; font-weight: bold; float: right;">
									<div style="display: inline;">
										<select id="deviceSelector" class="styleSelect">
											<option value="-">-- <spring:message code='please.select' text='Please Select'/> --</option>
											<c:if test="${motions != null && not(empty motions)}">
												<c:forEach items="${motions}" var="m">
													<option value="${m.id}:${m.name}">${m.name}</option>
												</c:forEach>												
											</c:if>
										</select>
									</div>
									<c:set var="deviceCount" value="0" />
									<div id="actualDiscussionDateDevices" style="display: inline; margin-left: 20px;">
										<c:if test="${not (empty discussionDateMap[s.id])}">
											<c:forEach items="${fn:split(discussionDateMap[s.id],'~')}" var="dd">
												<div id="acD${dd}" class="actualDevices" style="background: #99CCFF; border: 1px solid black; border-radius: 5px; padding: 4px; margin-left: 4px; display: inline; text-align: center;">${fn:split(dd,':')[1]}</div>
												<c:set var="deviceCount" value="${deviceCount + 1}" />
											</c:forEach>
										</c:if>
									</div>
									<input type="hidden" id="locale" name="locale" value="${discussionDateDomain.locale}" />
									<input type="hidden" id="actualDeviceData" name="devices" value="" />
									<input type="hidden" id="deviceType" name="deviceType" value="${deviceType}" /> 
									<input type="hidden" id="session" name="session" value="${session}" />
									<input type="hidden" id="discussDate" name="discussDate" value="" />
									<c:if test="${deviceCount >= 0}">
										<div id="submitDiv" style="display: block; float: right; right: 10px; width: 100px;">
											<input type="button" id="submitDiscussionDate" value="<spring:message code='generic.submit' text='Submit' />" class="butDef" />
										</div>
									</c:if>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</form:form>
			</div>
			<br />
			<div id="presetDateDiv">
				<c:forEach items="${sessionDates}" var="ss">
					<c:if test="${ss.id!=currDate}">
						<div style="background: #D9E5E5; width: 800px; height: 60px; border: 1px solid black; border-radius: 5px; box-shadow: 5px 5px #D8D8DF; padding: 5px; margin-top: 20px;">
							<div style="width: 300px; font-size: 14px; font-weight: bold; float: left;">
								${ss.name}
							</div>
							<div style="width: 500px; font-size: 12px; font-weight: bold; float: right;">
								<c:if test="${not (empty discussionDateMap[ss.id])}">
									<c:forEach items="${fn:split(discussionDateMap[ss.id],'~')}" var="dd">
										<div style="background: #99CCFF; border: 1px solid black; border-radius: 5px; padding: 4px; margin-left: 4px; display: inline; text-align: center;">${fn:split(dd,':')[1]}</div>
									</c:forEach>			
								</c:if>
							</div>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</div>
	
	</div>
	<input type="hidden" id="addMoreMessage" value="<spring:message code='generic.addmore' text='Add more' />" />
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="pleaseSelect" value="-- <spring:message code='please.select' text='Please Select'/> --" />
</body>
</html>