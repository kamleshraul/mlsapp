<%@ include file="/common/taglibs.jsp" %>
<html id="printRequisitionHtml">
<head>
	<title>
	<spring:message code="title" text="Voting Detail"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function viewBillDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&deviceType="+$("#typeOfSelectedDeviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='bill/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:800,height:700});
		},'html');	
	}
	
	$('document').ready(function(){
		initControls();
		$('#key').val('');		
		
		var text="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";		
		
		if($('#status').val()==undefined || $('#status').val()=='') {			
			$('#status').html(text);			
		} else {
			$('#status').prepend(text);	
		}
		
		$('#houseRound').prepend(text);	
		
		if($('#selectedBillNumber').val()!=undefined && $('#selectedBillNumber').val()!='') {
			$('#viewBillDetails').show();			
		} else {
			$('#viewBillDetails').hide();			
		}
		
		$('#viewBillDetails').click(function() {			
			if($('#selectedBillNumber').val()!='') {
				if($('#deviceId').val()!='' && $('#deviceId').val()!=undefined) {
					viewBillDetail($('#deviceId').val());
				} else {
					$.get('ref/findIdOfBillWithGivenNumberAndYear?billNumber='+$("#selectedBillNumber").val()
							+'&billYear='+$("#selectedYear").val(), function(data) {
						if(data!='' && data!=undefined) {
							viewBillDetail(data);
						}
					});
				}			
			} else {
				alert($("#emptyBillNumberMsg").val());
				return false;
			}
		});		
		
		$('#selectedBillNumber').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/transmitPressCopies_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&currentHouseTypeType='+$("#selectedHouseTypeType").val(), function(data) {
					$("#status").empty();
					var statusText=text;
					if(data.length!=undefined) {
						for(var i=0;i<data.length;i++){
							statusText+="<option value='"+data[i].value+"'>"+data[i].name;
						}
						$("#status").html(statusText);
						$('#deviceId').val(data[0].id);
						$('#viewBillDetails').show();
					} else {
						alert($('#billNumberChangeInvalidMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#pressCopiesDiv').empty();
			$('#pressCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#status').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
			}
		});
		
		$('#status').change(function() {
			$('#pressCopiesDiv').empty();
			$('#pressCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#houseRound').change(function() {
			$('#pressCopiesDiv').empty();
			$('#pressCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#checkFoPressCopies').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
				return false;
			} else if($('#status').val()=='') {
				alert($("#emptyStatusMsg").val());
				return false;
			}
			if($('#pressCopiesDiv').is(':hidden')) {
				$.get('printrequisition/bill/getPressCopies?deviceId='+$("#deviceId").val()
						+'&status='+$("#status").val()+'&houseRound='+$("#houseRound").val(), function(data) {				
					$('#pressCopiesDiv').html(data);
					$('#pressCopiesDiv').show();			
					if($('#isAlreadyTransmitted').val()=='true') {
						$('#submitRequisitionButtonsDiv').hide();
						alert($('#pressCopiesTransmittedAlreadyMsg').val());
					} else {
						$('#submitRequisitionButtonsDiv').show();
					}
				});
			} else {
				return false;
			}			
		});		
		$("#transmitPressCopiesButton").click(function(){		
			$('#operation').val("send");
			$('#usergroupForSendingRequisition').val($('#currentusergroup').val());
			$.post($("#transmitPressCopiesForm").attr('action'),
	            $("#transmitPressCopiesForm").serialize(),  
	            function(data){	
					$('#pressCopiesDiv').empty();
					$('#pressCopiesDiv').html(data);
					alert($('#isAlreadyTransmitted').val());
					if($('#isAlreadyTransmitted').val()=='true') {
						$('#submitRequisitionButtonsDiv').hide();
					} else {
						$('#submitRequisitionButtonsDiv').show();
					}
					/* $(".fancybox-inner").attr("tabindex",1).focus();
					$('html [id=printRequisitionHtml]').animate({scrollTop:0}, 'slow');
   				 	$('body [id=printRequisitionBody]').animate({scrollTop:0}, 'slow'); */				
	            });       
	    });
	});		
</script>
</head>
<body id="printRequisitionBody">
<div class="fields clearfix vidhanmandalImg">
	<form:form id="transmitPressCopiesForm" action="printrequisition/transmitPressCopies" method="POST" modelAttribute="domain">
	<div id="resultOfRequisitionDiv"></div>
	<h2><spring:message code="printrequisition.heading" text="Transmit Press Copies Details"/></h2>
	<p>
		<label class="small"><spring:message code="printrequisition.houseType" text="House Type"/></label>
		<input id="formattedHouseType" name="formattedSelectedHouseType" value="${selectedHouseType.getName()}" class="sText" readonly="readonly"/>
		<form:input type="hidden" id="selectedHouseTypeType" path="houseType" value="${selectedHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="printrequisition.year" text="Year"/></label>
		<input id="formattedSelectedYear" name="formattedSelectedYear" value="${formattedSelectedYear}" class="sInteger" readonly="readonly"/>
		<input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/>	
	</p>
	<p>
		<label class="small"><spring:message code="printrequisition.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"/>
		<a href="#" id="viewBillDetails"><spring:message code="printrequisition.viewBillDetails" text="View Bill"/></a>
		<form:input type="hidden" id="deviceId" path="deviceId" value="${selectedBillId}"/>
	</p>
	<p>
		<label class="small"><spring:message code="printrequisition.status" text="Status"/></label>
		<form:select id="status" class="sSelect" path="status"> 
			<c:forEach var="i" items="${transmitPressCopiesStatuses}">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</form:select>
	</p>
	<p>
		<label class="small"><spring:message code="printrequisition.houseRound" text="House Round"/></label>
		<form:select id="houseRound" class="sSelect" path="houseRound"> 
			<c:forEach var="i" items="${houseRoundVOs}">
				<option value="${i.value}">${i.name}</option>
			</c:forEach>
		</form:select>
	</p>
	<p align="right">
		<input id="checkFoPressCopies" type="button" value="<spring:message code='printrequisition.checkFoPressCopies' text='Check For Press Copies'/>" class="butDef">
	</p>
	<div id="pressCopiesDiv" style="display:none;">
	</div>
	<div id="submitRequisitionButtonsDiv" class="fields expand" style="display: none;">
		<h2></h2>
		<p class="tright">
			<input id="transmitPressCopiesButton" type="button" value="<spring:message code='printrequisition.transmitPressCopiesButton' text='Transmit Press Copies'/>" class="butDef">		
		</p>
	</div>	
	<input type="hidden" id="operation" name="operation"/>
	<input type="hidden" id="usergroupForSendingRequisition" name="usergroup"/>
	</form:form>
	<input id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillNumberMsg" value="<spring:message code='printrequisition.emptyBillNumberMsg' text='Please Enter Bill Number'/>" type="hidden">
	<input id="emptyStatusMsg" value="<spring:message code='printrequisition.emptyStatusMsg' text='Please Enter Status'/>" type="hidden">
	<input id="billNumberChangeInvalidMsg" value="<spring:message code='printrequisition.billNumberChangeInvalidMsg' text='Please check if bill number is valid for the year! If it is valid, contact Administrator'/>" type="hidden">
	<input id="pressCopiesTransmittedAlreadyMsg" value="<spring:message code='printrequisition.pressCopiesTransmittedAlreadyMsg' text='Press Copies are transmitted already'/>" type="hidden">
</div>	
</body>
</html>