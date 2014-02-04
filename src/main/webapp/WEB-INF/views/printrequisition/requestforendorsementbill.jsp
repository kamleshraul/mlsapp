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
					$.get('ref/findIdOfBillWithGivenNumberYearAndHouseType?billNumber='+$("#selectedBillNumber").val()
							+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val(), function(data) {
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
				$.get('ref/bill/sendGreenCopyForEndorsement_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
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
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#endorsementCopiesDiv').empty();
			$('#endorsementCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#houseType').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/sendGreenCopyForEndorsement_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
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
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#endorsementCopiesDiv').empty();
			$('#endorsementCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#selectedYear').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/sendGreenCopyForEndorsement_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
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
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#endorsementCopiesDiv').empty();
			$('#endorsementCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#status').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
			}
		});
		
		$('#status').change(function() {
			$('#endorsementCopiesDiv').empty();
			$('#endorsementCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#houseRound').change(function() {
			$('#endorsementCopiesDiv').empty();
			$('#endorsementCopiesDiv').hide();
			$('#submitRequisitionButtonsDiv').hide();
		});
		
		$('#checkFoEndorsementCopies').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
				return false;
			} else if($('#status').val()=='') {
				alert($("#emptyStatusMsg").val());
				return false;
			}
			if($('#endorsementCopiesDiv').is(':hidden')) {
				$.get('printrequisition/bill/getEndorsementCopies?deviceId='+$("#deviceId").val()
						+'&status='+$("#status").val()+'&houseRound='+$("#houseRound").val(), function(data) {				
					$('#endorsementCopiesDiv').html(data);
					$('#endorsementCopiesDiv').show();			
					if($('#isAlreadySentForEndorsement').val()=='true') {
						$('#submitRequisitionButtonsDiv').hide();
					} else {
						$('#submitRequisitionButtonsDiv').show();
					}
				});
			} else {
				return false;
			}			
		});
		
		$("#sendForEndorsement").click(function(){		
			$('#operation').val("send");
			$('#usergroupForSendingRequisition').val($('#currentusergroup').val());
			$.post($("#sendGreenCopyForEndorsementForm").attr('action'),
	            $("#sendGreenCopyForEndorsementForm").serialize(),  
	            function(data){	
					$('#endorsementCopiesDiv').empty();
					$('#endorsementCopiesDiv').html(data);
					alert($('#isAlreadySentForEndorsement').val());
					if($('#isAlreadySentForEndorsement').val()=='true') {
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
	<form:form id="sendGreenCopyForEndorsementForm" action="printrequisition/sendForEndorsement" method="POST" modelAttribute="domain">
	<div id="resultOfRequisitionDiv"></div>
	<h2><spring:message code="printrequisition.heading" text="Send for Endorsement Details"/></h2>
	<p>
		<label class="small"><spring:message code="printrequisition.houseType" text="House Type"/></label>
		<select id="houseType" class="sSelect">
			<c:forEach items="${houseTypes}" var="houseType">
				<c:choose>
					<c:when test="${houseType.getId()==selectedHouseType.getId()}">
						<option value="${houseType.getId()}" selected="selected">${houseType.getName()}</option>
					</c:when>
					<c:otherwise>
						<option value="${houseType.getId()}">${houseType.getName()}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		<form:input type="hidden" id="currentHouseTypeType" path="houseType" value="${currentHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="printrequisition.year" text="Year"/></label>
		<input id="selectedYear" name="selectedYear" value="${formattedSelectedYear}" class="sInteger"/>
		<%-- <input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/> --%>	
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
			<c:forEach var="i" items="${sendGreenCopyForEndorsementStatuses}">
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
		<input id="checkFoEndorsementCopies" type="button" value="<spring:message code='printrequisition.checkFoEndorsementCopies' text='Check For Endorsement Copies'/>" class="butDef">
	</p>
	<div id="endorsementCopiesDiv" style="display:none;">
	</div>
	<div id="submitRequisitionButtonsDiv" class="fields expand" style="display: none;">
		<h2></h2>
		<p class="tright">
			<input id="sendForEndorsement" type="button" value="<spring:message code='printrequisition.sendForEndorsement' text='Send For Endorsement'/>" class="butDef">		
		</p>
	</div>	
	<input type="hidden" id="operation" name="operation"/>
	<input type="hidden" id="usergroupForSendingRequisition" name="usergroup"/>
	</form:form>
	<input id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillNumberMsg" value="<spring:message code='printrequisition.emptyBillNumberMsg' text='Please Enter Bill Number'/>" type="hidden">
	<input id="emptyStatusMsg" value="<spring:message code='printrequisition.emptyStatusMsg' text='Please Enter Status'/>" type="hidden">
	<input id="billNotFoundMsg" value="<spring:message code='printrequisition.billNotFoundMsg' text='Bill not found.'/>" type="hidden">
</div>	
</body>
</html>