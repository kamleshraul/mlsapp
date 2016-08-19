<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	function generateHtmlReport() {
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
		var value=$('#member').val();
		if(value!='-'){
		var parameters="member="+$('#member').val()+"&session="
		+$("#session").val()+"&questionType="+$("#questionType").val();
		var resource='ballot/memberballot/member/questions';
		$.get(resource+'?'+parameters,function(data){
			$("#listchoices").empty();	
			$("#listchoices").html(data);
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
		}else{
			$("#listchoices").empty();
			$.unblockUI();			
		}
		$("#errorDiv").hide();
		$("#successDiv").hide();
		$('#cumulativeMembersDiv').hide();
	}
	
	function getAllSelectedMembersInOrder() {
		var items=new Array();
		var validationSucceeded = true;
		//check if order is mentioned for selected members
		$(".membercheck").each(function(){
			if($(this).is(":checked")){					
				var memberId = $(this).attr("id").split("chk")[1];	
				var memberName = $('#membername'+memberId).text();
				var memberOrder = $('#memberorder'+memberId).val();							
				items.push(memberId + "_" + memberName + "_" + memberOrder);					
			};
		});		
		if(items.length==0) {
			validationSucceeded = false;
			$.prompt("Please select at least one member");
			$('#selectedMembersInOrder').val("");
			return false;
		}
		if(validationSucceeded==true) {			
			var parameters = "items="+items;
			$.ajax({url: 'ref/cumulativememberwisequestionsreport/memberorder', data: parameters, 
				type: 'POST',
				async: false,
				success: function(data) {					
					if(data.length==1 && (data[0].id==undefined || data[0].id=="")) {
						$.prompt("Please select at least one member");
						$('#selectedMembersInOrder').val("");
						return false;
					} else {
						var selectedMembersInOrder = "";
						for(var i=0; i<data.length; i++) {
							selectedMembersInOrder += data[i].id;
							if(i!=data.length-1) {
								selectedMembersInOrder += ",";
							}
						}						
					}						
					$('#selectedMembersInOrder').val(selectedMembersInOrder);				
				}				
			});			
		}
	}

	$(document).ready(function() {
		var myArray = [];
		
		$('#member option').each(function(){			
			myArray.push($(this).text());
		});
		
		$( ".autosuggest").autocomplete({						
				source: myArray,
				select:function(event,ui){	
					$('#member').val("");
					$('#member option').each(function(){						
						if($(this).text()==ui.item.value) {							
							$(this).attr('selected', 'selected');
							generateHtmlReport();
						}
					});			
				}	
		});	
		
		$('#memberwise_cumulative_report').click(function() {
			$('#memberOption').attr("value", "");
			$("#listchoices").empty();
			$('#cumulativeMembersDiv').show();			
		});
		
		$('#preview_member_order').click(function() {
			var items=new Array();
			$(".membercheck").each(function(){
				var memberId = $(this).attr("id").split("chk")[1];	
				var memberName = $('#membername'+memberId).text();
				var memberOrder = $('#memberorder'+memberId).val();							
				items.push(memberId + "_" + memberName + "_" + memberOrder);	
			});					
			var parameters = "items="+items;
			$.ajax({url: 'ref/cumulativememberwisequestionsreport/memberorder', data: parameters, 
				type: 'POST',
				async: false,
				success: function(data) {					
					if(data.length>=1) {
						var membersOrderHtml = "<table class='uiTable'>";
						membersOrderHtml += "<tr>";
						membersOrderHtml += "<th>";
						membersOrderHtml += "<label>"+$('#memberName').val()+"</label>";
						membersOrderHtml += "</th>";
						membersOrderHtml += "<th>";
						membersOrderHtml += "<label>"+$('#memberOrder').val()+"</label>";
						membersOrderHtml += "</th>";
						membersOrderHtml += "</tr>";
						for(var i=0; i<data.length; i++) {
							membersOrderHtml += "<tr>";
							membersOrderHtml += "<td>";
							membersOrderHtml += "<label>"+data[i].name+"</label>";
							membersOrderHtml += "</td>";
							membersOrderHtml += "<td>";
							membersOrderHtml += "<label>"+data[i].formattedOrder+"</label>";
							membersOrderHtml += "</td>";
							membersOrderHtml += "</tr>";
						}
						membersOrderHtml += "</table>";
						$.fancybox.open(membersOrderHtml, {autoSize: false, width: 510, height:600});
					}										
				}				
			});
		});
		
		$("#chkall").change(function(){
			if($(this).is(":checked")){
				$(".membercheck").attr("checked","checked");	
			}else{
				$(".membercheck").removeAttr("checked");
			}
		});
		
		$('#cumulative_memberwisequestions_pdf').click(function(data) {
			getAllSelectedMembersInOrder();
			if($('#selectedMembersInOrder').val()!=undefined && $('#selectedMembersInOrder').val()!="") {
				var parameters = "session="+$("#session").val()
				 +"&questionType="+$("#questionType").val()
				 +"&allMembers="+$('#selectedMembersInOrder').val()			 
				 +"&outputFormat=PDF";
				var resourceURL = 'ballot/memberballot/member/cumulative/questionsreport?'+ parameters;	
				$(this).attr('href', resourceURL);
			}
		});
		
		$('#cumulative_memberwisequestions_word').click(function() {			
			getAllSelectedMembersInOrder();
			if($('#selectedMembersInOrder').val()!=undefined && $('#selectedMembersInOrder').val()!="") {
				var parameters = "session="+$("#session").val()
				 +"&questionType="+$("#questionType").val()
				 +"&allMembers="+$('#selectedMembersInOrder').val()			 
				 +"&outputFormat=WORD";
				var resourceURL = 'ballot/memberballot/member/cumulative/questionsreport?'+ parameters;	
				$(this).attr('href', resourceURL);
			}			
		});
		
		var currentMemberOrderValueForGivenMember = "";
		
		$('.memberorder').click(function() {
			currentMemberOrderValueForGivenMember = $(this).val();
		});
		
		$('.memberorder').change(function() {
			var memberorderIdForGivenMember = $(this).attr("id");
			var changedMemberOrderValueForGivenMember = $(this).val();	
			//add previous order for other members
			$('.memberorder').each(function() {
				if($(this).attr("id")!=memberorderIdForGivenMember) {
					var memberorderIdForThisMember = $(this).attr("id");
					$('#'+memberorderIdForThisMember+' option[value='+currentMemberOrderValueForGivenMember+']').show();
				}
			});
			//remove updated order for other members if not empty(please select option)
			if(changedMemberOrderValueForGivenMember!="") {
				$('.memberorder').each(function() {
					if($(this).attr("id")!=memberorderIdForGivenMember) {
						var memberorderIdForThisMember = $(this).attr("id");
						$('#'+memberorderIdForThisMember+' option[value='+changedMemberOrderValueForGivenMember+']').hide();
					}
				});
			}			
		});
		
		/* $("#member").change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			var value=$(this).val();
			if(value!='-'){
			var parameters="member="+$(this).val()+"&session="
			+$("#session").val()+"&questionType="+$("#questionType").val();
			var resource='ballot/memberballot/member/questions';
			$.get(resource+'?'+parameters,function(data){
				$("#listchoices").empty();	
				$("#listchoices").html(data);
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
			}else{
				$("#listchoices").empty();
				$.unblockUI();			
			}
			$("#errorDiv").hide();
			$("#successDiv").hide();
		}); */		
	});
</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="toolTip tpRed clearfix" id="errorDiv" style="display: none;">
	<p style="font-size: 14px;">
		<img src="./resources/images/template/icons/light-bulb-off.png"> 
		<spring:message	code="update_failed" text="Please correct following errors." />
	</p>
	<p></p>
	</div>
	
	<div class="toolTip tpGreen clearfix" id="successDiv" style="display: none;">
	<p style="font-size: 14px;">
		<img src="./resources/images/template/icons/light-bulb-off.png"> 
		<spring:message	code="update_success" text="Data saved successfully." />
	</p>
	<p></p>
	</div>

	<p>
	<label style="margin: 10px;"><spring:message code="memberballotmemberwise.member" text="Member" /></label>
	<input type="text" class="autosuggest sText" id="memberOption" style="width: 200px;"/>
	<select id="member" name="member" style="display: none;">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${eligibleMembers}" var="i">
			<option value="${i.id }"><c:out value="${i.getFullname()}"></c:out></option>
		</c:forEach>
	</select>
	<a href="#" id="memberwise_cumulative_report" class="butSim">
		<spring:message code="memberballot.memberwisecumulativereport" text="Cumulative Member's Questions Report"/>
	</a>
	</p>
	<div id="listchoices">
	</div>
	<div id="cumulativeMembersDiv" style="display: none;">
	<c:choose>
		<c:when test="${!(empty eligibleMembers) }">
			<a id="cumulative_memberwisequestions_pdf" href="#" style="text-decoration: none;">
				<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
			</a>
			&nbsp;
			<a id="cumulative_memberwisequestions_word" href="#" style="text-decoration: none;">
				<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
			</a>				
			<table class="uiTable">
				<tr>
					<th style="text-align:center;min-width: 100px;">
						<label><spring:message code="memberballotmemberwise.selectAll" text="Select All"></spring:message></label>
						<input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true">
					</th>
					<th><spring:message code="memberballotmemberwise.membername" text="Member Name"></spring:message></th>
					<th>
						<label style="float: left;margin-top:3px;">
							<spring:message code="memberballotmemberwise.memberorder" text="Member Order">
						</spring:message></label>
						<a style="text-decoration: none;float: right;" href="#" id="preview_member_order">
							<img width="25" height="18" alt="Preview Order" src="./resources/images/preview.png">
						</a>	
					</th>
				</tr>			
				<c:forEach items="${eligibleMembers}" var="i">
					<tr>
						<td class="chk" style="text-align:center;max-width:50px !important;">
							<input type="checkbox" id="chk${i.id}" name="chk${i.id}" class="sCheck membercheck" value="true">
						</td>
						<td style="min-width:240px !important;">
							<label id="membername${i.id}">${i.getFullname()}</label>
						</td>
						<td class="memberOrder">
							<select class="sSelect memberorder" id="memberorder${i.id}" name="memberOrder${i.id}">
							<option value=""><spring:message code="please.select" text="Please Select"/></option>
							<c:forEach items="${eligibleMemberCounts}" var="j">
								<option value="${j.number}">${j.formattedNumber}</option>
							</c:forEach>
							</select>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="memberballotmemberwise.nomembers" text="No Eligible Member Found"></spring:message>
		</c:otherwise>
	</c:choose>	
	<input type="hidden" id="selectedMembersInOrder" value="">	
	</div>
	<input type="hidden" id="session" name="session" value="${session }">
	<input type="hidden" id="questionType" name="questionType" value="${questionType}">	
	
	<input type="hidden" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="memberName" value="<spring:message code='memberballotmemberwise.membername' text='Member Name'/>">
	<input type="hidden" id="memberOrder" value="<spring:message code='memberballotmemberwise.memberorder' text='Member Order'/>">
</body>
</html>