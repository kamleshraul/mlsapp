<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="resolution.list" text="List Of Resolutions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$("#selectionDiv1").show();					
			/**** grid params which is sent to load grid data being sent ****/	
			var deviceType = "";
			$.ajax({url: 'ref/getTypeOfSelectedDeviceType?deviceTypeId='+ $("#selectedDeviceType").val(), async: false, success : function(data){	
				deviceType = data;
			}}).done(function(){
				if(deviceType == 'resolutions_government' && $("#currentusergroupType").val()!='member') {					
					$("#gridURLParams").val("deviceType="+$("#selectedDeviceType").val()
							    +"&sessionYear="+$("#selectedSessionYear").val()
								+"&sessionType="+$('#selectedSessionType').val()								
								+"&ugparam="+$('#ugparam').val()
								+"&status="+$('#selectedStatus').val()
								+"&role="+$('#srole').val()
								+"&usergroup="+$('#currentusergroup').val()
								+"&usergroupType="+$('#currentusergroupType').val()
								);
					//console.log("govt params: " + $("#gridURLParams").val());	
					$('#karyavali_report').hide();
					$('#outputFormat').hide();
				} else {					
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
						    +"&deviceType="+$("#selectedDeviceType").val()
							+"&sessionYear="+$("#selectedSessionYear").val()
							+"&sessionType="+$("#selectedSessionType").val()								
							+"&ugparam="+$("#ugparam").val()
							+"&status="+$("#selectedStatus").val()
							+"&role="+$("#srole").val()
							+"&usergroup="+$("#currentusergroup").val()
							+"&usergroupType="+$("#currentusergroupType").val()
							);
					//console.log("nonofficial params in else: " + $("#gridURLParams").val());
					if(deviceType == 'resolutions_nonofficial') {
						$('#karyavali_report').show();
						$('#outputFormat').show();
					} else {
						$('#karyavali_report').hide();
						$('#outputFormat').hide();
					}
				}
				$('#deviceTypeSelected').val(deviceType);
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			
			/*******For Enabling the new Resolution link in the edit page********/
			$('#gridURLParams_ForNew').val($('#gridURLParams').val());	
			
			/**** new question ****/
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				newResolution();
			});
			/**** edit question ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				$("#selectionDiv1").hide();	
				editResolution();
			});
			/**** delete question ****/
			$("#delete_record").click(function() {
				deleteResolution();
			});		
			/****Searching Question****/
			$("#search").click(function() {
				searchRecord();
			});
			
			$("#karyavali_report").click(function(){
				/**** url parameters for karyavali report ****/				
				var parameters_report = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&deviceType="+$("#selectedDeviceType").val()
				 + "&outputFormat=" + $("#outputFormat").val();				
				var reportURL = 'resolution/generatekaryavalireport?' + parameters_report;
				$(this).attr('href', reportURL);		
				
				//check output format set or not
				if($("#outputFormat").val() == "") {
					$.prompt("Please Select Output Format first!!!!");
					return false;
				}				
			});	
		});
		/**** double clicking record in grid handler ****/
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			currentSelectedRow=$('#key').val();
			$("#selectionDiv1").hide();	
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'resolution/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		/**** record selection handler****/
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}	
					
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_CLERK')">			
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			</security:authorize>			
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','ROIS_CLERK')">			
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |			
			<a href="#" id="submitQuestion" class="butSim">
				<spring:message code="generic.submitquestion" text="submit"/>
			</a> |
			</security:authorize>			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |	
			<security:authorize access="hasAnyRole('ROIS_ASSISTANT')">
			<a href="#" id="karyavali_report" class="butSim" target="_blank">
				<spring:message code="resolution.karyavali_report" text="Karyavali Report"/>
			</a>
			<c:if test="${not empty outputFormats}">				
				<select id="outputFormat" name="outputFormat">
					<option value="" selected="selected">Please Select Output Format</option>
					<c:forEach items="${outputFormats}" var="i">
						<option value="${i.value}">${i.name}</option>
					</c:forEach>
				</select>				
			</c:if>			
			</security:authorize>		
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="deviceTypeSelected">.
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</div>
</body>
</html>