<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#yaadiNumbersForBulkUpdate').change(function() {				
				var yaadiNumbers = $(this).val();
				console.log("yaadiNumbers: "+yaadiNumbers);
				if(yaadiNumbers!=undefined && yaadiNumbers!="") {
					$("#yaadiSelectionDiv").empty();
					//fetch devicetype from appropriate request parameter name
					var selectedDeviceType = $("#selectedDeviceType").val();
					if($("#selectedDeviceType").val()==undefined || $("#selectedDeviceType").val()==''){
						if($("#category").val()=='question') {
							selectedDeviceType = $("#selectedQuestionType").val();
						} else if($("#category").val()=='motion') {
							selectedDeviceType = $("#selectedMotionType").val();
						} else if($("#category").val()=='resolution') {
							selectedDeviceType = $("#selectedResolutionType").val();
						}
					}
					//fetch requested yaadis by user & display their details with selection
					var parameters = "houseType=" + $("#selectedHouseType").val()
					   + "&sessionYear=" + $("#selectedSessionYear").val()
					   + "&sessionType=" + $("#selectedSessionType").val()
					   + "&deviceType=" + selectedDeviceType
					   + "&ugparam=" + $("#ugparam").val()
					   + "&role=" + $("#srole").val()
				 	   + "&usergroup=" + $("#currentusergroup").val()
					   + "&usergroupType=" + $("#currentusergroupType").val()
					   + "&yaadiNumbers=" + yaadiNumbers;
					
					$.get('yaadi_details/bulk_yaadi_selection?'+parameters,function(data){
						$("#yaadiSelectionDiv").empty();
						$("#yaadiSelectionDiv").html(data);
						$("#yaadiLayingStatusAndDatePara").show();
						$.unblockUI();					
					},'html').fail(function(data){
						$("#yaadiLayingStatusAndDatePara").hide();
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.");
						}
						scrollTop();
					});
				} else {
					$.prompt("Please enter atleast 1 yaadi number!");
					return false;
				}
			});
			$("#bulksubmit").click(function(){
				bulkUpdate();			
			});
		});
		function bulkUpdate() {
			var items=new Array();
			$(".action").each(function(){
				if($(this).is(":checked")){
				items.push($(this).attr("id").split("chk")[1]);
				}
			});			
			if(items.length<=0){
				$.prompt($("#selectItemsMsg").val());
				return false;	
			} else if(items.length>$('#yaadiBulkUpdateCountLimit').val()){
				$("#itemsLimitReachedMsg").val($("#itemsLimitReachedMsg").val().replace("#yaadiBulkUpdateCountLimit#", $('#yaadiBulkUpdateCountLimit').val()));
				$.prompt($("#itemsLimitReachedMsg").val());
				return false;	
			}
			//fetch devicetype from appropriate request parameter name
			var selectedDeviceType = $("#selectedDeviceType").val();
			if($("#selectedDeviceType").val()==undefined || $("#selectedDeviceType").val()==''){
				if($("#category").val()=='question') {
					selectedDeviceType = $("#selectedQuestionType").val();
				} else if($("#category").val()=='motion') {
					selectedDeviceType = $("#selectedMotionType").val();
				} else if($("#category").val()=='resolution') {
					selectedDeviceType = $("#selectedResolutionType").val();
				}
			}
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post('yaadi_details/bulk_yaadi_update?loadIt=yes',
			        	{
							items:items,
				        	houseType:$("#selectedHouseType").val(),
				   		 	sessionYear:$("#selectedSessionYear").val(),
						 	sessionType:$("#selectedSessionType").val(),
						 	deviceType:selectedDeviceType,
						 	ugparam:$("#ugparam").val(),
						 	role:$("#srole").val(),
						 	usergroup:$("#currentusergroup").val(),
						 	usergroupType:$("#currentusergroupType").val(),
						 	yaadiNumbers:$("#yaadiNumbersForBulkUpdate").val(),
						 	yaadiLayingStatus:$("#yaadiLayingStatus").val(),
						 	yaadiLayingDate:$("#yaadiLayingDate").val()
					 	},
	    	            function(data){
	       					$('html').animate({scrollTop:0}, 'slow');
	       				 	$('body').animate({scrollTop:0}, 'slow');
	    					$("#yaadiSelectionDiv").empty();
	    					$("#yaadiSelectionDiv").html(data);
	    					$.unblockUI();
	    	            }
	    	            ,'html').fail(function(){
	    					$.unblockUI();
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});
		    	}
			}});
		}
	</script>
	<!-- <style type="text/css">
		.o{
			vertical-align: middle;
		}
	</style> -->
</head>

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;" id="error">${error}</h4>
	</c:if>
	<div class="fields">
		<p>
			<label class="small"><spring:message code="yaadidetails.bulkYaadiUpdate.yaadiNumber" text="Yaadi Numbers"/></label>
			<input class="sText" type="text" id="yaadiNumbersForBulkUpdate" name="yaadiNumbersForBulkUpdate" placeholder="e.g.1,2,5-9,12">
		</p>
		<div id="yaadiLayingStatusAndDatePara" style="display: none;">
		<p>
			<label class="small"><spring:message code='yaadidetails.bulkYaadiUpdate.yaadiLayingStatus' text='Yaadi Laying Status'/></label>
			<select id="yaadiLayingStatus" name="yaadiLayingStatus" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingStatuses}" var="i">					
					<option value="${i.id}">${i.name}</option>	
				</c:forEach>
			</select>
		</p>
		<p>
			<label class="small"><spring:message code='yaadidetails.bulkYaadiUpdate.yaadiLayingDate' text='Yaadi Laying Date'/></label>
			<select id="yaadiLayingDate" name="yaadiLayingDate" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingDates}" var="i">					
					<option value="${i.value}">${i.name}</option>	
				</c:forEach>
			</select>
		</p>
		<h2></h2>
		<p class="tright">
			<input id="bulksubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
		</div>	
		<br/>
		<div id="yaadiSelectionDiv">
		</div>
	</div>
	<input type="hidden" id="yaadiBulkUpdateCountLimit" value="${yaadiBulkUpdateCountLimit}" />
	<input id="selectItemsMsg" value="<spring:message code='yaadidetails.bulkYaadiUpdate.prompt.selectitems' text='Please select atleast 1 yaadi to continue..'></spring:message>" type="hidden">
	<input id="itemsLimitReachedMsg" value="<spring:message code='yaadidetails.bulkYaadiUpdate.prompt.itemsLimitReached' text='You can select only #yaadiBulkUpdateCountLimit# yaadis at one go..'></spring:message>" type="hidden">
	<input id="submissionMsg" value="<spring:message code='yaadidetails.bulkYaadiUpdate.prompt.submit' text='Do you want to update the selected yaadis?'></spring:message>" type="hidden">
</body>
</html>