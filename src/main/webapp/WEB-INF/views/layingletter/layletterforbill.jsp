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
	
	function generateLetter() {
		if($('#selectedBillNumber').val()=='') {
			alert($("#emptyBillNumberMsg").val());
			return false;
		}
		if($('#layingDate').val()=='') {
			alert($("#emptyLayingDateMsg").val());
			return false;
		}
		$('#generateLetter').attr('href', 'bill/generateLayingLetterWhenPassedByFirstHouse?deviceId='+$("#deviceId").val()
				+'&houseRound='+$("#houseRound").val()+'&layingDate='+$("#layingDate").val());
	}
	
	$('document').ready(function(){
		initControls();
		$('#key').val('');		
		
		var text="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";		
		
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
				$.get('ref/bill/checkeligibilityforlayingletter?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&currentHouseTypeType='+$("#selectedHouseTypeType").val(), function(data) {
					if($("#houseRound option[value='']").length <= 0) {
						$('#houseRound').prepend(text);
					}				
					if(data!=undefined) {
						if(data=='-1') {
							alert($('#billNumberChangeInvalidMsg2').val());
							$('#selectedBillNumber').val("");
						} else if(data=='-2') {
							alert($('#billNumberChangeInvalidMsg3').val());
							$('#selectedBillNumber').val("");
						} else {
							$('#deviceId').val(data);
							$('#viewBillDetails').show();
						}						
					} else {
						alert($('#billNumberChangeInvalidMsg1').val());
						$('#selectedBillNumber').val("");
					}					
				});
			} else {				
				if($("#houseRound option[value='']").length <= 0) {
					$('#houseRound').prepend(text);
				}				
				$('#viewBillDetails').hide();
			}			
			$('#layingLetterFieldsDiv').empty();
			$('#layingLetterFieldsDiv').hide();
			$('#submitLayingLetterButtonsDiv').hide();
		});
		
		$('#houseRound').change(function() {
			if($(this).val()=='') {
				$('#layingLetterFieldsDiv').empty();
				$('#layingLetterFieldsDiv').hide();
				$('#submitLayingLetterButtonsDiv').hide();
			} else {
				if($('#selectedBillNumber').val()=='') {
					alert($("#emptyBillNumberMsg").val());
					return false;
				} else {					
					$.get('layingletter/bill/getlayingletter?deviceId='+$("#deviceId").val()
							+'&requisitionFor='+$("#requisitionFor").val()+'&status='+$("#status").val()
							+'&houseRound='+$("#houseRound").val(), function(data) {				
						$('#layingLetterFieldsDiv').html(data);
						$('#layingLetterFieldsDiv').show();
						if($('#isLetterLaid').val()=='true') {
							$('#submitLayingLetterButtonsDiv').hide();
						} else {
							$('#submitLayingLetterButtonsDiv').show();
						}
					});					
				}
			}
		});
		
		$("#saveLetterButton").click(function(){			
			$('#operation').val("save");
			$('#usergroupTypeForLayingLetter').val($('#currentusergroupType').val());
			$.post($("#layingLetterForm").attr('action'),
	            $("#layingLetterForm").serialize(),  
	            function(data){	
					$('#layingLetterFieldsDiv').empty();
					$('#layingLetterFieldsDiv').html(data);
					if($('#isLayingLetterReadOnly').val()=='readonly') {
						$('#submitLayingLetterButtonsDiv').hide();
					} else {
						$('#submitLayingLetterButtonsDiv').show();
					}
					/* $(".fancybox-inner").attr("tabindex",1).focus();
					$('html [id=printRequisitionHtml]').animate({scrollTop:0}, 'slow');
   				 	$('body [id=printRequisitionBody]').animate({scrollTop:0}, 'slow'); */				
	            });       
	    });
		$("#layLetterButton").click(function(){		
			$('#operation').val("send");
			$('#usergroupTypeForLayingLetter').val($('#currentusergroupType').val());
			$('#usergroupForLayingLetter').val($('#currentusergroup').val());
			$.post($("#layingLetterForm").attr('action'),
	            $("#layingLetterForm").serialize(),  
	            function(data){	
					$('#layingLetterFieldsDiv').empty();
					$('#layingLetterFieldsDiv').html(data);
					if($('#isLayingLetterReadOnly').val()=='readonly') {
						$('#submitLayingLetterButtonsDiv').hide();
					} else {
						$('#submitLayingLetterButtonsDiv').show();
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
	<form:form id="layingLetterForm" action="layingletter/bill/layLetterWhenPassedByFirstHouse" method="POST" modelAttribute="domain">
	<div id="resultOfRequisitionDiv"></div>
	<h2><spring:message code="layingletter.heading" text="Laying Letter When Passed By First House"/></h2>
	<p>
		<label class="small"><spring:message code="layingletter.houseType" text="House Type"/></label>
		<input id="formattedHouseType" name="formattedSelectedHouseType" value="${selectedHouseType.getName()}" class="sText" readonly="readonly"/>
		<form:input type="hidden" id="selectedHouseTypeType" path="houseType" value="${selectedHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.year" text="Year"/></label>
		<input id="selectedYear" name="selectedYear" value="${formattedSelectedYear}" class="sInteger"/>
		<%-- <input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/> --%>	
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"/>
		<a href="#" id="viewBillDetails"><spring:message code="layingletter.viewBillDetails" text="View Bill"/></a>
		<form:input type="hidden" id="deviceId" path="deviceId" value="${selectedBillId}"/>
	</p>
	<p>
		<label class="small"><spring:message code="layingletter.houseRound" text="House Round"/></label>
		<form:select id="houseRound" class="sSelect" path="houseRound"> 
			<c:forEach var="i" items="${houseRoundVOs}">
				<option value="${i.value}">${i.name}</option>
			</c:forEach>
		</form:select>
	</p>
	<div id="layingLetterFieldsDiv" style="display:none;">
	</div>
	<div id="submitLayingLetterButtonsDiv" class="fields expand" style="display: none;">
		<h2></h2>
		<p class="tright">
			<input id="saveLetterButton" type="button" value="<spring:message code='layingletter.saveLetterButton' text='Save Letter'/>" class="butDef">
			<input id="layLetterButton" type="button" value="<spring:message code='layingletter.layLetterButton' text='Lay Letter'/>" class="butDef">	
		</p>
	</div>	
	<input type="hidden" id="operation" name="operation"/>
	<input type="hidden" id="usergroupForLayingLetter" name="usergroupForLayingLetter"/>
	<input type="hidden" id="usergroupTypeForLayingLetter" name="usergroupTypeForLayingLetter"/>
	</form:form>
	<input id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillNumberMsg" value="<spring:message code='layingletter.emptyBillNumberMsg' text='Please Enter Bill Number'/>" type="hidden">
	<input id="emptyLayingDateMsg" value="<spring:message code='layingletter.emptyLayingDateMsg' text='Please Enter Laying Date'/>" type="hidden">
	<input id="emptyStatusMsg" value="<spring:message code='layingletter.emptyStatusMsg' text='Please Enter Status'/>" type="hidden">
	<input id="billNumberChangeInvalidMsg1" value="<spring:message code='layingletter.billNumberChangeInvalidMsg1' text='Please check if bill number is valid for the year! If it is valid, contact Administrator'/>" type="hidden">
	<input id="billNumberChangeInvalidMsg2" value="<spring:message code='layingletter.billNumberChangeInvalidMsg2' text='Selected bill is not currently passed from first house! So it is not eligible for laying letter.'/>" type="hidden">
	<input id="billNumberChangeInvalidMsg3" value="<spring:message code='layingletter.billNumberChangeInvalidMsg3' text='selected housetype is not second house of selected bill! So it is not eligible for laying letter.'/>" type="hidden">
	<input id="pressCopiesTransmittedAlreadyMsg" value="<spring:message code='layingletter.pressCopiesTransmittedAlreadyMsg' text='Press Copies are transmitted already'/>" type="hidden">
</div>	
</body>
</html>