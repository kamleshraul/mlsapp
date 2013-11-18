<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="bill.citationReport" text="Citation Report"/>
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
				$.get('ref/bill/citation_statuses?billNumber='+$("#selectedBillNumber").val()
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
			$('#citationDiv').empty();
			$('#citationDiv').hide();			
		});
		
		$('#status').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
			}
		});
		
		$('#status').change(function() {
			$('#citationDiv').empty();
			$('#citationDiv').hide();			
		});
		
		$('#generateCitationReportButton').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
				return false;
			} else if($('#status').val()=='') {
				alert($("#emptyStatusMsg").val());
				return false;
			}
			if($('#citationDiv').is(':hidden')) {
				$.get('bill/generateCitationReport?deviceId='+$("#deviceId").val()
						+'&status='+$("#status").val()+'&statusDate='+$("#statusDate").val(), function(data) {				
					$('#citationDiv').html(data);					
					$('#citationDiv').show();					
				},'html');
			} else {
				return false;
			}			
		});	
	});		
</script>
</head>
<body>
<div class="fields clearfix vidhanmandalImg">
	<form id="sendGreenCopyForEndorsementForm" action="printrequisition/sendForEndorsement" method="POST">
	<h2><spring:message code="bill.citationReport" text="Citation Report"/></h2>
	<p>
		<label class="small"><spring:message code="bill.houseType" text="House Type"/></label>
		<input id="formattedHouseType" name="formattedSelectedHouseType" value="${selectedHouseType.getName()}" class="sText" readonly="readonly"/>
		<input type="hidden" id="selectedHouseTypeType" name="houseType" value="${selectedHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="bill.year" text="Year"/></label>
		<input id="formattedSelectedYear" name="formattedSelectedYear" value="${formattedSelectedYear}" class="sInteger" readonly="readonly"/>
		<input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/>	
	</p>
	<p>
		<label class="small"><spring:message code="bill.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"/>
		<a href="#" id="viewBillDetails"><spring:message code="bill.viewBillDetails" text="View Bill"/></a>
		<input type="hidden" id="deviceId" name="deviceId" value="${selectedBillId}"/>
	</p>
	<p>
		<label class="small"><spring:message code="bill.status" text="Status"/></label>
		<select id="status" class="sSelect" name="status"> 
			<c:forEach var="i" items="${citationStatuses}">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="bill.statusDate" text="Status Date"/></label>
		<input type="text" id="statusDate" name="statusDate" value="${currentDate}" class="datemask sText"/>
	</p>
	<p align="right">
		<input id="generateCitationReportButton" type="button" value="<spring:message code='bill.generateCitationReportButton' text='Generate Citation Report'/>" class="butDef">
	</p>
	<div id="citationDiv" style="display:none; width: 650px; height: 300px; margin: 10px 60px 0px 60px; padding: 10px; overflow: auto; border: 1px solid black; box-shadow: 2px 2px 2px grey;"">
	</div>	
	<input type="hidden" id="operation" name="operation"/>
	<input type="hidden" id="usergroupForCitation" name="usergroup"/>
	</form>
	<input id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillNumberMsg" value="<spring:message code='bill.emptyBillNumberMsg' text='Please Enter Bill Number'/>" type="hidden">
	<input id="emptyStatusMsg" value="<spring:message code='bill.emptyStatusMsg' text='Please Enter Status'/>" type="hidden">
	<input id="billNumberChangeInvalidMsg" value="<spring:message code='bill.billNumberChangeInvalidMsg' text='Please check if bill number is valid for the year! If it is valid, contact Administrator'/>" type="hidden">
</div>	
</body>
</html>