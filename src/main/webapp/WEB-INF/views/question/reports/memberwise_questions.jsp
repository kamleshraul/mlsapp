<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
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
		
		$('#status').html($('#selectedStatus').html());
		$("#status").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
		
		$('#groupNumbers').focus(function() {
			if($(this).val()==$('#allSelected').val()) {				
				$('#groupNumbers').val("");
			}
		});
		
		$('#groupNumbers').focusout(function() {
			if($(this).val()=="") {				
				$('#groupNumbers').val($('#allSelected').val());				
			}			
		});
		
		$('#groupNumbers').change(function() {
			if($(this).val()=="") {
				$('#groups').val("0");
				$('#groupNumbers').val($('#allSelected').val());
				if($('#member').val()!="") {
					generateHtmlReport();
				}
			} else {
				var delimiter = ",";
				$.get('ref/parseNumbersSeparatedByGivenDelimiter?numbers='+$(this).val()
						+'&delimiter='+delimiter, function(data) {
					$('#groups').val(data);
				}) .done(function() {
					if($('#member').val()!="") {
						generateHtmlReport();
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
			}			
		});
		
		$('#status').change(function() {
			if($('#member').val()!="") {
				generateHtmlReport();
			}
		});
	});
	
	function generateHtmlReport() {
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
		var value=$('#member').val();
		var resource = '';
		var parameters = '';
		if(value!='-'){
			console.log($("#sessionYear").val());
			if($("#sYear").val()!= null & $("#sYear").val()!='-'){
				resource='question/report/membertermwisequestions/questions';
				parameters="member="+$('#member').val()
				+"&session="+$("#session").val()
				+"&questionType="+$("#questionType").val()
				+"&houseType="+$("#houseType").val()
				+"&sessionType="+$("#sessionType").val()
				+"&sessionYear="+$("#sessionYear").val()
				+"&groups="+$('#groups').val()
				+"&status="+$('#status').val()
				+"&sessionYear="+$("#sYear").val();
			}else{
				resource='question/report/memberwisequestions/questions';
				parameters="member="+$('#member').val()
				+"&session="+$("#session").val()
				+"&questionType="+$("#questionType").val()
				+"&houseType="+$("#houseType").val()
				+"&sessionType="+$("#sessionType").val()
				+"&sessionYear="+$("#sessionYear").val()
				+"&groups="+$('#groups').val()
				+"&status="+$('#status').val();
			}
		
		
		$.get(resource+'?'+parameters,function(data){
			$("#listQuestionsDiv").empty();	
			$("#listQuestionsDiv").html(data);
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
			$("#listQuestionsDiv").empty();
			$.unblockUI();			
		}
		$("#errorDiv").hide();
		$("#successDiv").hide();
	}
</script>
<style type="text/css">
    @media screen{
     a.viewQuestion{
     	text-decoration: underline !important;
     } 	        
    }
    @media print{
     a.viewQuestion{
     	text-decoration: none !important;
     }    
    }                
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="toolTip tpRed clearfix" id="errorDiv" style="display: none;">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_failed" text="Please correct following errors." /></p>
<p></p>
</div>

<div class="toolTip tpGreen clearfix" id="successDiv" style="display: none;">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_success" text="Data saved successfully." /></p>
<p></p>
</div>

<p>
	<label class="small"><spring:message code="memberwisereport.year" text="Year" /></label>
	<select id="sYear" name="sYear" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${sessionYears}" var="i">
			<option value="${i}"><c:out value="${i}"></c:out></option>
		</c:forEach>
	</select>
	<label style="margin: 10px;"><spring:message code="memberwisereport.member" text="Member" /></label>
	<input type="text" class="autosuggest sText" id="memberOption" style="width: 200px;"/>
	<select id="member" name="member" style="display: none;">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${eligibleMembers}" var="i">
			<option value="${i.id }"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	<%-- <a id="cumulativeMemberQuestionsReport" href="#" style="margin-left: 20px;">
		<spring:message code="memberwisereport.cumulativeMemberQuestionsReport" text="Cumulative Member Questions Report"/>
	</a> --%>
	<label style="margin: 10px;"><spring:message code="memberwisereport.groups" text="Groups"/></label>
	<input type="text" class="sText" id="groupNumbers" name="groupNumbers" style="width: 50px;" value="<spring:message code='generic.allSelected' text='All Selected'/>"/>
	<input type="hidden" id="groups" name="groups" value="0"/>
	<label style="margin: 10px;"><spring:message code="memberwisereport.status" text="Status" /></label>
	<select id="status" style="width: 200px;" class="sSelect"></select>
</p>
<div id="listQuestionsDiv">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
<input type="hidden" id="houseType" name="houseType" value="${houseType}">
<input type="hidden" id="sessionType" name="sessionType" value="${sessionType}">
<input type="hidden" id="sessionYear" name="sessionYear" value="${sessionYear}">
<input type="hidden" id="group" name="group" value="${group}">
<input type="hidden" id="answeringDate" name="answeringDate" value="${answeringDate}">
<input type="hidden" id="memberId" name="memberId">	

<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="allSelected" value="<spring:message code='generic.allSelected' text='All Selected'/>">
</body>
</html>