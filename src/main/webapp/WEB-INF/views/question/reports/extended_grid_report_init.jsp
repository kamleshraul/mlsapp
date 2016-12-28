<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		//****Disable 'default open filters if any' in Add Filter Dropdown****//
		$('#addFilter option').each(function() {
			var filterId = $(this).attr('value');
			if(filterId!="-") {			
				if(!$('#'+filterId).is(':hidden')) {
					$(this).attr('disabled', 'disabled');		
				}
			}
		});
		
		//****Expand/Collapse Report Filters Div on Toggle****//
		$('#filterSetLegend').click(function() {
			if($('#filterSetDiv').is(':hidden')) {
				$('#filterSetDiv').removeAttr('hidden');
				$('#filterSetLegendIcon').attr('src', './resources/images/arrow_collapse.jpg');
			} else {
				$('#filterSetDiv').attr('hidden', 'true');
				$('#filterSetLegendIcon').attr('src', './resources/images/arrow_expand.jpg');
			}
		});
		
		//****Add New Report Filter****//
		$('#addFilter').change(function() {			
			var filterId = $(this).val();
			if(filterId!="-") {			
				$('#addFilter option[value='+filterId+']').attr('disabled', 'disabled');
				if($('#'+filterId).is(':hidden')) {
					$('#'+filterId).removeAttr('hidden');
					$('#'+filterId+"_checkbox").attr('checked', 'checked');
					$('#'+filterId+"_applied").val('true');
				}
			}
			$('#addFilter').val("-");
		});
		
		//****Apply/Remove Filter Check Upon Checked Value****//
		$('.filter_checkbox').click(function() {
			var filterCheckBoxId = $(this).attr('id');
			var filterAppliedControlId= filterCheckBoxId.replace("checkbox", "applied");
			if($(this).is(':checked')) {
				$('#'+filterAppliedControlId).val('true');
			} else {
				$('#'+filterAppliedControlId).val('false');
			}
		});
		
		//****Toggle 'filter values dropdown view' based on operator value for the filter****//
		$('.filter_operator > select').change(function() {			
			var filterOperatorId = $(this).attr('id');
			var filterValuesId = filterOperatorId.replace('operator', 'filter_values');
			if($('#'+filterOperatorId).val()=='eq') {				
				$('#'+filterValuesId).removeAttr('hidden');
			} else {
				$('#'+filterValuesId).attr('hidden', 'true');
			}
		});
		
		//****Generate Extended Grid Report****//
		$('#extendedGridReport').click(function() {
			var reportURL = "question/report/extended_grid_report";
			if($('#outputFormat').val()=='HTML') {
				reportURL += "/html";
			} else { //for PDF and WORD.. currently available formats
				reportURL += "/doc";
			}
			$.prompt($('#generateReportPromptMsg').val(), {
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post(reportURL, $('#extendedGridReportForm').serialize(),
		    	            function(data){
		        				$('#reportConfigDiv').hide();
		        				$('#reportDataDiv').empty();
		       					$('#reportDataDiv').html(data);
		       					$('#backToReportConfigSpan').removeAttr('hidden');
		       					$('#refreshExtendedGridReportSpan').removeAttr('hidden');
		       					$('#extendedGridReport').attr('hidden', 'true');
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	   				 	   				
		    	            }).fail(function(){
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
	        return false;
		});
		
		//****Refre****//
		$('#refreshExtendedGridReport').click(function() {
			var reportURL = "question/report/extended_grid_report";
			if($('#outputFormat').val()=='HTML') {
				reportURL += "/html";
			} else { //for PDF and WORD.. currently available formats
				reportURL += "/doc";
			}
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });			
			$.post(reportURL, $('#extendedGridReportForm').serialize(),
    	            function(data){
        				$('#reportConfigDiv').hide();
        				$('#reportDataDiv').empty();
       					$('#reportDataDiv').html(data);      					
       					$('html').animate({scrollTop:0}, 'slow');
       				 	$('body').animate({scrollTop:0}, 'slow');	
    					$.unblockUI();	   				 	   				
    	            }).fail(function(){
    					$.unblockUI();
    					if($("#ErrorMsg").val()!=''){
    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
    					}else{
    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    					}
    					scrollTop();
    				});			
		});
		
		//****Back to Report Config****//
		$('#backToReportConfig').click(function() {
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });			
			$('#reportDataDiv').empty();
			$('#backToReportConfigSpan').attr('hidden', 'true');
			$('#refreshExtendedGridReportSpan').attr('hidden', 'true');
			$('#extendedGridReport').removeAttr('hidden');
			$('#reportConfigDiv').show();
			$.unblockUI();			
		});
	});	
</script>
<style type="text/css">
	tr.filter_row > td
	{
	  padding-bottom: 1em;
	}
	td.filter_field {
		width: 200px;
	}	
	td.filter_operator {
		width: 120px;
	}
	#filterSetLegendIcon {
		width: 12px;
		height: 12px;				
		/* box-shadow: 2px 2px 5px #000000;
		border-radius: 5px;
		padding: 2px;
		border: 1px solid #000000; */ 
	}
	.imgN:hover{
		/*border-radius: 32px;*/
		box-shadow: 2px 2px 2px #0E4269;
	}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<a id="extendedGridReport" href="#" style="font-size: 16px;font-weight: bold;">Extended Grid Report</a>		
	<span style="float: left;width: 32px; text-align: center;" id="refreshExtendedGridReportSpan" hidden="true">
		<a href="javascript:void(0);" id="refreshExtendedGridReport" style="text-decoration: none; color: #000;">
			<img class="imgN" src="./resources/images/refresh2D.png" alt="Back" height="32px" title="<spring:message code='generic.refresh' text='Refresh' />" />
		</a>
	</span>
	<span style="float: right; width: 54px; text-align: center;" id="backToReportConfigSpan" hidden="true">
		<a href="javascript:void(0);" id="backToReportConfig" style="text-decoration: none; color: #000;">
			<img class="imgN" src="./resources/images/back2D.png" alt="Back" height="32px" title="<spring:message code='generic.back' text='Back' />" />
		</a>
	</span>
</p>
<div id="reportConfigDiv">
	<form id="extendedGridReportForm">
		<fieldset id="filterSet" style="border: 1px solid;padding: 1em;display: block;">
			<legend id="filterSetLegend" style="font-size: 14px;font-weight: bold;background-color: lightblue;">
				<a href="#"><img src="./resources/images/arrow_collapse.jpg" id="filterSetLegendIcon" class="imageLink"/></a>
				Report Filters
			</legend>
			<div id="filterSetDiv">
			<table style="width:100%">
				<tr>
					<td>
						<table id="filterTable">
							<tr class="filter_row" id="clubbing_status_filter">
								<td class="filter_field" id="clubbing_status_filter_field">
									<input type="checkbox" id="clubbing_status_filter_checkbox" class="sCheck filter_checkbox" checked="checked">
									<label class="small" for="clubbing_status"><spring:message code="extended_grid_report.clubbing_status_filter.filter_field" text="Clubbing Status"/></label>
									<input type="hidden" id="clubbing_status_filter_applied" name="clubbing_status_filter_applied" value="true"/>
								</td>
								<td class="filter_operator" id="clubbing_status_filter_operator">
									<select id="clubbing_status_operator" name="clubbing_status_operator" style="width: 100px; height: 25px;">
										<option value="-" selected="selected"><spring:message code="extended_grid_report.clubbing_status_filter.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.clubbing_status_filter.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="clubbing_status_filter_values" hidden="true">
									<select id="clubbing_status" name="clubbing_status" style="width: 100px; height: 25px;">									
										<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
										<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
									</select>
								</td>
							</tr>
							
							<tr class="filter_row" id="effective_status_filter">
								<td class="filter_field" id="effective_status_filter_field">
									<input type="checkbox" id="effective_status_filter_checkbox" class="sCheck filter_checkbox" checked="checked">
									<label class="small" for="effective_status"><spring:message code="extended_grid_report.effective_status_filter.filter_field" text="Status"/></label>
									<input type="hidden" id="effective_status_filter_applied" name="effective_status_filter_applied" value="true"/>
								</td>
								<td class="filter_operator" id="effective_status_filter_operator">
									<select id="effective_status_operator" name="effective_status_operator" style="width: 100px; height: 25px;">
										<option value="submitted" selected="selected"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.submitted" text="Submitted"/></option>
										<option value="eq"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.eq" text="is"/></option>
										<option value="clarificationNeededFromDepartment"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.clarificationNeededFromDepartment" text="Clarification From Department"/></option>
										<option value="clarificationNeededFromMember"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.clarificationNeededFromMember" text="Clarification From Member"/></option>
										<option value="lapsed"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.lapsed" text="Lapsed"/></option>
									</select>
								</td>
								<td class="filter_values" id="effective_status_filter_values" hidden="true">
									<select id="effective_status" name="effective_status" style="width: 250px; height: 25px;">
										<option value="question_submit"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_submit" text="Submit"/></option>
										<option value="question_final_admission"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_final_admission" text="Admit"/></option>
										<option value="question_final_rejection"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_final_rejection" text="Reject"/></option>
									</select>
								</td>
							</tr>
							
							<tr class="filter_row" id="group_filter" hidden="true">
								<td class="filter_field" id="group_filter_field">
									<input type="checkbox" id="group_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="group"><spring:message code="extended_grid_report.group_filter.filter_field" text="Group"/></label>
									<input type="hidden" id="group_filter_applied" name="group_filter_applied" value="false"/>
								</td>
								<td class="filter_operator" id="group_filter_operator">
									<select id="group_operator" name="group_operator" style="width: 100px; height: 25px;">
										<option value="0" selected="selected"><spring:message code="extended_grid_report.group_filter.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.group_filter.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="group_filter_values" hidden="true">
									<select id="group" name="group" style="width: 100px; height: 25px;">
										<c:forEach items="${groupList}" var="grp">
											<option value="${grp.id}">${grp.number}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
							
							<tr class="filter_row" id="subdepartment_filter" hidden="true">
								<td class="filter_field" id="subdepartment_filter_field">
									<input type="checkbox" id="subdepartment_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="subdepartment"><spring:message code="extended_grid_report.subdepartment_filter.filter_field" text="Sub-Department"/></label>
									<input type="hidden" id="subdepartment_filter_applied" name="subdepartment_filter_applied" value="false"/>
								</td>
								<td class="filter_operator" id="subdepartment_filter_operator">
									<select id="subdepartment_operator" name="subdepartment_operator" style="width: 100px; height: 25px;">
										<option value="0" selected="selected"><spring:message code="extended_grid_report.subdepartment_filter.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.subdepartment_filter.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="subdepartment_filter_values" hidden="true">
									<select id="subdepartment" name="subdepartment" style="width: 250px; height: 25px;">
										<c:forEach items="${subdepartmentList}" var="sd">
											<option value="${sd.id}">${sd.name}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
					</td>
					<td align="right" valign="top">
						<p>
							<label class="small"><spring:message code="extended_grid_report.addFilter" text="Add Filter" /></label>
							<select id="addFilter" style="width: 100px; height: 25px;">
								<option value="-" selected="selected"><spring:message code='please.select' text='Please Select'/></option>							
								<option value="clubbing_status_filter"><spring:message code='extended_grid_report.filter.clubbing_status_filter' text='Clubbing Status'/></option>
								<option value="effective_status_filter"><spring:message code='extended_grid_report.filter.effective_status_filter' text='Status'/></option>
								<option value="group_filter"><spring:message code='extended_grid_report.filter.group_filter' text='Group'/></option>
								<option value="subdepartment_filter"><spring:message code='extended_grid_report.filter.subdepartment_filter' text='Sub-Department'/></option>
							</select>
						</p>
					</td>
				</tr>			
			</table>
			</div>
		</fieldset>	
		<input type="hidden" id="session" name="session" value="${session}">
		<input type="hidden" id="questionType" name="questionType" value="${questionType}">
		<input type="hidden" id="questionTypeType" name="questionTypeType" value="${questionTypeType}">
		<input type="hidden" id="locale" name="locale" value="${locale}">	
		<input type="hidden" id="reportHeader" name="reportHeader" value="">
		<input type="hidden" id="reportQuery" name="reportQuery" value="${fn:toUpperCase(questionTypeType)}_EXTENDED_GRID_QUERY">
		<input type="hidden" id="xsltFileName" name="xsltFileName" value="template_extended_grid_report">
		<input type="hidden" id="outputFormat" name="outputFormat" value="HTML">
		<input type="hidden" id="reportFileName" name="reportFileName" value="extended_grid_report">
	</form>
</div>
<div id="reportDataDiv">
</div>
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="allSelected" value="<spring:message code='generic.allSelected' text='All Selected'/>">
<input type="hidden" id="generateReportPromptMsg" value="<spring:message code='extended_grid_report.generateReportPromptMsg' text='Do you want to generate extended grid report now?'/>">
</body>
</html>