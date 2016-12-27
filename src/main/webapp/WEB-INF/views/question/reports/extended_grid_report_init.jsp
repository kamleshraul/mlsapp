<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {			
		//****Expand/Collapse Report Filters Div on Toggle****//
		$('#filterSetLegend').click(function() {
			if($('#filterSetDiv').is(':hidden')) {
				$('#filterSetDiv').show();
				$('#filterSetLegendIcon').attr('src', './resources/images/arrow_collapse.jpg');
			} else {
				$('#filterSetDiv').hide();
				$('#filterSetLegendIcon').attr('src', './resources/images/arrow_expand.jpg');
			}
		});
		
		//****Add New Report Filter****//
		$('#addFilter').change(function() {			
			var filterId = $(this).val();
			if(filterId!="-") {			
				$('#addFilter option[value='+filterId+']').attr('disabled', 'disabled');
				if($('#'+filterId).is(':hidden')) {
					$('#'+filterId).show();					
				}
			}
			$('#addFilter').val("-");
		});
		
		$('.filter_operator > select').change(function() {
			var filterOperatorId = $(this).attr('id');
			var filterValuesId = filterOperatorId.replace('operator', 'filter_values');
			if($('#'+filterOperatorId).val()=='eq') {				
				$('#'+filterValuesId).show();
			} else {
				$('#'+filterValuesId).hide();
			}
		});
	});	
</script>
<style type="text/css">
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
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<a href="#" style="font-size: 16px;font-weight: bold;">Extended Grid Report</a>
</p>
<div id="reportConfigDiv">
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
							</td>
							<td class="filter_operator" id="clubbing_status_filter_operator">
								<select id="clubbing_status_operator" style="width: 100px; height: 25px;">
									<option value="-" selected="selected"><spring:message code="extended_grid_report.clubbing_status_filter.filter_operator.any" text="Any"/></option>
									<option value="eq"><spring:message code="extended_grid_report.clubbing_status_filter.filter_operator.eq" text="is"/></option>
								</select>
							</td>
							<td class="filter_values" id="clubbing_status_filter_values" style="display: none;">
								<select id="clubbing_status" style="width: 100px; height: 25px;">									
									<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
									<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
								</select>
							</td>
						</tr>
						<tr><td colspan="3">&nbsp;</td></tr>
						<tr class="filter_row" id="main_status_filter">
							<td class="filter_field" id="main_status_filter_field">
								<input type="checkbox" id="main_status_filter_checkbox" class="sCheck filter_checkbox" checked="checked">
								<label class="small" for="main_status"><spring:message code="extended_grid_report.main_status_filter.filter_field" text="Status"/></label>
							</td>
							<td class="filter_operator" id="main_status_filter_operator">
								<select id="main_status_operator" style="width: 100px; height: 25px;">
									<option value="submitted" selected="selected"><spring:message code="extended_grid_report.main_status_filter.filter_operator.submitted" text="Submitted"/></option>
									<option value="eq"><spring:message code="extended_grid_report.main_status_filter.filter_operator.eq" text="is"/></option>
									<option value="clarificationNeededFromDepartment"><spring:message code="extended_grid_report.main_status_filter.filter_operator.clarificationNeededFromDepartment" text="Clarification From Department"/></option>
									<option value="clarificationNeededFromMember"><spring:message code="extended_grid_report.main_status_filter.filter_operator.clarificationNeededFromMember" text="Clarification From Member"/></option>
									<option value="lapsed"><spring:message code="extended_grid_report.main_status_filter.filter_operator.lapsed" text="Lapsed"/></option>
								</select>
							</td>
							<td class="filter_values" id="main_status_filter_values" style="display: none;">
								<select id="main_status" style="width: 100px; height: 25px;">
									<option value="question_submit"><spring:message code="extended_grid_report.main_status_filter.main_status_filter_values.question_submit" text="Submit"/></option>
									<option value="question_final_admission"><spring:message code="extended_grid_report.main_status_filter.main_status_filter_values.question_final_admission" text="Admit"/></option>
									<option value="question_final_rejection"><spring:message code="extended_grid_report.main_status_filter.main_status_filter_values.question_final_rejection" text="Reject"/></option>
								</select>
							</td>
						</tr>
						<tr><td colspan="3">&nbsp;</td></tr>
						<tr class="filter_row" id="group_filter" hidden="true">
							<td class="filter_field" id="group_filter_field">
								<input type="checkbox" id="group_filter_checkbox" class="sCheck filter_checkbox" checked="checked">
								<label class="small" for="group"><spring:message code="extended_grid_report.group_filter.filter_field" text="Group"/></label>
							</td>
							<td class="filter_operator" id="group_filter_operator">
								<select id="group_operator" style="width: 100px; height: 25px;">
									<option value="0" selected="selected"><spring:message code="extended_grid_report.group_filter.filter_operator.any" text="Any"/></option>
									<option value="eq"><spring:message code="extended_grid_report.group_filter.filter_operator.eq" text="is"/></option>
								</select>
							</td>
							<td class="filter_values" id="group_filter_values" style="display: none;">
								<select id="group" style="width: 100px; height: 25px;">
									<c:forEach items="${groupList}" var="grp">
										<option value="${grp.id}">${grp.number}</option>
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
							<option value="main_status_filter"><spring:message code='extended_grid_report.filter.main_status_filter' text='Status'/></option>
							<option value="group_filter"><spring:message code='extended_grid_report.filter.group_filter' text='Group'/></option>
							<option value="subdepartment_filter"><spring:message code='extended_grid_report.filter.subdepartment_filter' text='Sub-Department'/></option>
						</select>
					</p>
				</td>
			</tr>			
		</table>
		</div>
	</fieldset>
</div>
<div id="reportDataDiv">
</div>
<input type="hidden" id="session" value="${session }">
<input type="hidden" id="questionType" value="${questionType}">
<input type="hidden" id="questionTypeType" value="${questionTypeType}">
<input type="hidden" id="locale" value="${locale}">
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="allSelected" value="<spring:message code='generic.allSelected' text='All Selected'/>">
</body>
</html>