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
		
		//****Expand/Collapse Report Options Div on Toggle****//
		$('#resultSetLegend').click(function() {
			if($('#resultSetDiv').is(':hidden')) {
				$('#resultSetDiv').removeAttr('hidden');
				$('#resultSetLegendIcon').attr('src', './resources/images/arrow_collapse.jpg');
			} else {
				$('#resultSetDiv').attr('hidden', 'true');
				$('#resultSetLegendIcon').attr('src', './resources/images/arrow_expand.jpg');
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
		
		//****Report Fields Selection Related Script (for Custom Multiselect)****//
		/** Remove Default Selected Items From All Items Box **/
		$("#selectedItems option").each(function() {
			$("#allItems option[value='"+$(this).val()+"']").remove();
		});
		/** Move Selected Items To Select Box **/
		$("#to2").click(function(){
			$("#allItems option:selected").each(function(){
				$("#selectedItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
				$("#allItems option[value='"+$(this).val()+"']").remove();				
			});
		});
		/** Move Selected Items To All Items Box **/
		$("#to1").click(function(){
			$("#selectedItems option:selected").each(function(){
				$("#allItems").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
				$("#selectedItems option[value='"+$(this).val()+"']").remove();				
			});
		});
		/** for moving items up **/
		$(".up").click(function(){
			//get the currently slected item and its index
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
				//if index is not 0 then proceed
				if(index!=0){
					//swap current with previous
					var prev=$("#selectedItems option:eq("+(index-1)+")");
					var prevVal=prev.val();
					var prevText=prev.text();
					prev.val(current.val());
					prev.text(current.text());
					current.val(prevVal);
					current.text(prevText);	
					//set previous as selected and remove selection from current
					prev.attr("selected","selected");
					current.removeAttr("selected");								
				}
			
		});
		/** for moving items down **/	
		$(".down").click(function(){
			//get the currently slected item and its index			
			var current=$("#selectedItems option:selected");
			var index=parseInt(current.index());
			var length=$("#selectedItems option").length;
			//if end of items is not reached then proceed
				if(index!=length-1){
					//swap current with next				
					var next=$("#selectedItems option:eq("+(index+1)+")");
					var nextVal=next.val();
					var nextText=next.text();
					next.val(current.val());
					next.text(current.text());
					current.val(nextVal);
					current.text(nextText);	
					//set next as selected and remove selection from current
					next.attr("selected","selected");
					current.removeAttr("selected");					
				}			
		});
		
		//****Generate Extended Grid Report****//
		$('#generateExtendedGridReport').click(function() {
			//set report fields as per selected order by user
			var fieldCount = 1;
			$("#selectedItems option").each(function(){
				$('#reportField_'+fieldCount).attr('value', $(this).val());
	            fieldCount++;
			});
			$('#reportFieldsCount').attr('value', fieldCount-1);
			for(var i=fieldCount; i<=$('#itemMaster > option').length; i++) {
				$('#reportField_'+i).attr('value', '');
			}
			var reportURL = "question/report/extended_grid_report";
			if($('#outputFormat').val()=='HTML') {
				reportURL += "/html";
				generateExtendedGridReportInHTML(reportURL);
			} else { //for PDF and WORD.. currently available formats
				reportURL += "/doc";
				generateExtendedGridReportInFOP(reportURL);
			}			
		});
		
		//****Refresh Extended Grid Report in HTML Format****//
		$('#refreshExtendedGridReport').click(function() {
			var reportURL = "question/report/extended_grid_report/html";
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.get(reportURL, $('#extendedGridReportForm').serialize(),
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
	
	//****Function to Generate Extended Grid Report in HTML Format****//
	function generateExtendedGridReportInHTML(reportURL) {
		$.prompt($('#generateReportPromptMsg').val(), {
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.ajax({
					url: 'ref/field_select_query_for_report',
					data: $('#extendedGridReportForm').serialize(),
					type: 'GET',
			        async: false,
					success: function(data) {
						$("#field_select_query").attr('value', data);
						$.ajax({
							url: 'ref/field_header_select_query_for_report',
							data: $('#extendedGridReportForm').serialize(),
							type: 'GET',
					        async: false,
							success: function(data) {
								$("#field_header_select_query").attr('value', data);							
								$.get(reportURL, $('#extendedGridReportForm').serialize(),
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
						}).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}					
						});
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}					
				});
	        }
		}});			
        return false;
	}	
	
	//****Function to Generate Extended Grid Report in DOC (PDF/WORD) Format****//
	function generateExtendedGridReportInFOP(reportURL) {
		$.prompt($('#generateReportPromptMsg').val(), {
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
	        	form_submit(reportURL, $('#extendedGridReportForm').serialize(), 'GET');		
	        }
		}});			
        return false;
	}
</script>
<style type="text/css">
	tr.filter_row > td {
	  padding-bottom: 1em;
	}
	td.filter_field {
		width: 200px;
	}	
	td.filter_operator {
		width: 120px;
	}
	#filterSetLegendIcon, #resultSetLegendIcon {
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
	.btnMS{
		width:30px;
		margin: 5px;
		font-size: 13px; 
		padding: 5px;
		/*display: inline;*/
	}
	tr.filter_row > td {
	  padding-bottom: 1em;
	}
	td.filter_field {
		width: 200px;
	}	
	td.filter_operator {
		width: 120px;
	}
	.butRep{
		width: auto;
		background:-webkit-gradient(linear, left top, left bottom, color-stop(0.05, #77b55a), color-stop(1, #72b352));
		background:-moz-linear-gradient(top, #77b55a 5%, #72b352 100%);
		background:-webkit-linear-gradient(top, #77b55a 5%, #72b352 100%);
		background:-o-linear-gradient(top, #77b55a 5%, #72b352 100%);
		background:-ms-linear-gradient(top, #77b55a 5%, #72b352 100%);
		background:linear-gradient(to bottom, #77b55a 5%, #72b352 100%);
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#77b55a', endColorstr='#72b352',GradientType=0);
		background-color:#77b55a;
		-moz-border-radius:4px;
		-webkit-border-radius:4px;
		border-radius:4px;
		border:1px solid #4b8f29;
		display:inline-block;
		cursor:pointer;
		color:#ffffff;
		font-family:Arial;
		font-size:13px;
		font-weight:bold;
		padding:6px 12px;
		text-decoration:none;
		text-shadow:0px 1px 0px #5b8a3c;
	}
	.butRep:hover {
		background:-webkit-gradient(linear, left top, left bottom, color-stop(0.05, #72b352), color-stop(1, #77b55a));
		background:-moz-linear-gradient(top, #72b352 5%, #77b55a 100%);
		background:-webkit-linear-gradient(top, #72b352 5%, #77b55a 100%);
		background:-o-linear-gradient(top, #72b352 5%, #77b55a 100%);
		background:-ms-linear-gradient(top, #72b352 5%, #77b55a 100%);
		background:linear-gradient(to bottom, #72b352 5%, #77b55a 100%);
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#72b352', endColorstr='#77b55a',GradientType=0);
		background-color:#72b352;
	}
	.butRep:active {
		position:relative;
		top:1px;
	}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<a id="extendedGridReport" href="#" style="font-size: 16px;font-weight: bold;">
		<spring:message code="extended_grid_report.report_link" text="Extended Grid Report"/>
	</a>
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
				<spring:message code="extended_grid_report.report_filters" text="Report Filters"/>
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
										<option value="-" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
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
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
										<option value="clarificationNeededFromDepartment"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.clarification_needed_from_department" text="Clarification From Department"/></option>
										<option value="clarificationNeededFromMember"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.clarification_needed_from_member" text="Clarification From Member"/></option>
										<%-- <option value="lapsed"><spring:message code="extended_grid_report.effective_status_filter.filter_operator.lapsed" text="Lapsed"/></option> --%>
									</select>
								</td>
								<td class="filter_values" id="effective_status_filter_values" hidden="true">
									<select id="effective_status" name="effective_status" style="width: 250px; height: 25px;">
										<option value="question_submit"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_submit" text="Just Submitted"/></option>
										<option value="question_final_admission"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_final_admission" text="Admitted"/></option>
										<option value="question_final_rejection"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_final_rejection" text="Rejected"/></option>
										<option value="question_unstarred_final_admission"><spring:message code="extended_grid_report.effective_status_filter.effective_status_filter_values.question_unstarred_final_admission" text="Unstarred Admitted"/></option>
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
										<option value="0" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
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
							
							<tr class="filter_row" id="group_changed_status_filter" hidden="true">
								<td class="filter_field" id="group_changed_status_filter_field">
									<input type="checkbox" id="group_changed_status_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="group_changed_status"><spring:message code="extended_grid_report.group_changed_status_filter.filter_field" text="Group Changed Status"/></label>
									<input type="hidden" id="group_changed_status_filter_applied" name="group_changed_status_filter_applied" value="true"/>
								</td>
								<td class="filter_operator" id="group_changed_status_filter_operator">
									<select id="group_changed_status_operator" name="group_changed_status_operator" style="width: 100px; height: 25px;">
										<option value="-" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="group_changed_status_filter_values" hidden="true">
									<select id="group_changed_status" name="group_changed_status" style="width: 250px; height: 25px;">
										<option value="group_unchanged"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_unchanged" text="Group Not Changed"/></option>
										<option value="group_changed"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed" text="Group Changed"/></option>
										<option value="group_changed_sent"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed_sent" text="Group Changed and Sent to Other Groups"/></option>
										<option value="group_changed_preballot"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed_preballot" text="Group Changed Pre-Ballot"/></option>
										<option value="group_changed_preballot_sent"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed_preballot_sent" text="Group Changed Pre-Ballot and Sent to Other Groups"/></option>
										<option value="group_changed_postballot"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed_postballot" text="Group Changed Post-Ballot"/></option>
										<option value="group_changed_postballot_sent"><spring:message code="extended_grid_report.group_changed_status_filter.group_changed_status_filter_values.group_changed_postballot_sent" text="Group Changed Post-Ballot and Sent to Other Groups"/></option>
									</select>
								</td>
							</tr>
							
							<tr class="filter_row" id="subdepartment_filter" hidden="true">
								<td class="filter_field" id="subdepartment_filter_field">
									<input type="checkbox" id="subdepartment_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="subdepartment"><spring:message code="extended_grid_report.subdepartment_filter.filter_field" text="Department"/></label>
									<input type="hidden" id="subdepartment_filter_applied" name="subdepartment_filter_applied" value="false"/>
								</td>
								<td class="filter_operator" id="subdepartment_filter_operator">
									<select id="subdepartment_operator" name="subdepartment_operator" style="width: 100px; height: 25px;">
										<option value="0" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
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
							
							<tr class="filter_row" id="subdepartment_changed_status_filter" hidden="true">
								<td class="filter_field" id="subdepartment_changed_status_filter_field">
									<input type="checkbox" id="subdepartment_changed_status_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="subdepartment_changed_status"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.filter_field" text="Department Changed Status"/></label>
									<input type="hidden" id="subdepartment_changed_status_filter_applied" name="subdepartment_changed_status_filter_applied" value="true"/>
								</td>
								<td class="filter_operator" id="subdepartment_changed_status_filter_operator">
									<select id="subdepartment_changed_status_operator" name="subdepartment_changed_status_operator" style="width: 100px; height: 25px;">
										<option value="-" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="subdepartment_changed_status_filter_values" hidden="true">
									<select id="subdepartment_changed_status" name="subdepartment_changed_status" style="width: 250px; height: 25px;">
										<option value="subdepartment_unchanged"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_unchanged" text="Department Not Changed"/></option>
										<option value="subdepartment_changed"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed" text="Department Changed"/></option>
										<option value="subdepartment_changed_sent"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed_sent" text="Department Changed and Sent to Other Departments"/></option>
										<option value="subdepartment_changed_preballot"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed_preballot" text="Department Changed Pre-Ballot"/></option>
										<option value="subdepartment_changed_preballot_sent"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed_preballot_sent" text="Department Changed Pre-Ballot and Sent to Other Departments"/></option>
										<option value="subdepartment_changed_postballot"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed_postballot" text="Department Changed Post-Ballot"/></option>
										<option value="subdepartment_changed_postballot_sent"><spring:message code="extended_grid_report.subdepartment_changed_status_filter.subdepartment_changed_status_filter_values.subdepartment_changed_postballot_sent" text="Department Changed Post-Ballot and Sent to Other Departments"/></option>
									</select>
								</td>
							</tr>
							
							<tr class="filter_row" id="lapsed_status_filter" hidden="true">
								<td class="filter_field" id="lapsed_status_filter_field">
									<input type="checkbox" id="lapsed_status_filter_checkbox" class="sCheck filter_checkbox">
									<label class="small" for="lapsed_status"><spring:message code="extended_grid_report.lapsed_status_filter.filter_field" text="Lapsed Status"/></label>
									<input type="hidden" id="lapsed_status_filter_applied" name="lapsed_status_filter_applied" value="true"/>
								</td>
								<td class="filter_operator" id="lapsed_status_filter_operator">
									<select id="lapsed_status_operator" name="lapsed_status_operator" style="width: 100px; height: 25px;">
										<option value="-" selected="selected"><spring:message code="extended_grid_report.filter_operator.any" text="Any"/></option>
										<option value="eq"><spring:message code="extended_grid_report.filter_operator.eq" text="is"/></option>
									</select>
								</td>
								<td class="filter_values" id="lapsed_status_filter_values" hidden="true">
									<select id="lapsed_status" name="lapsed_status" style="width: 100px; height: 25px;">									
										<option value="lapsed"><spring:message code="extended_grid_report.lapsed_status_filter.lapsed_status_filter_values.lapsed" text="Lapsed"/></option>
										<option value="unlapsed"><spring:message code="extended_grid_report.lapsed_status_filter.lapsed_status_filter_values.unlapsed" text="Not Lapsed"/></option>
									</select>
								</td>
							</tr>
						</table>
					</td>
					<td align="right" valign="top">
						<p>
							<label class="small"><spring:message code="extended_grid_report.add_filter" text="Add Filter" /></label>
							<select id="addFilter" style="width: 100px; height: 25px;">
								<option value="-" selected="selected"><spring:message code='please.select' text='Please Select'/></option>				
								<option value="clubbing_status_filter"><spring:message code='extended_grid_report.clubbing_status_filter.filter_field' text='Clubbing Status'/></option>
								<option value="effective_status_filter"><spring:message code='extended_grid_report.effective_status_filter.filter_field' text='Status'/></option>
								<option value="group_filter"><spring:message code='extended_grid_report.group_filter.filter_field' text='Group'/></option>
								<option value="group_changed_status_filter"><spring:message code='extended_grid_report.group_changed_status_filter.filter_field' text='Group Changed Status'/></option>
								<option value="subdepartment_filter"><spring:message code='extended_grid_report.subdepartment_filter.filter_field' text='Department'/></option>
								<option value="subdepartment_changed_status_filter"><spring:message code='extended_grid_report.subdepartment_changed_status_filter.filter_field' text='Department Changed Status'/></option>
								<option value="lapsed_status_filter"><spring:message code='extended_grid_report.lapsed_status_filter.filter_field' text='Lapsed Status'/></option>
							</select>
						</p>
					</td>
				</tr>			
			</table>
			</div>
		</fieldset>
		<p></p>
		<fieldset id="resultSet" style="border: 1px solid;padding: 1em;display: block;">
			<legend id="resultSetLegend" style="font-size: 14px;font-weight: bold;background-color: lightblue;">
				<a href="#"><img src="./resources/images/arrow_collapse.jpg" id="resultSetLegendIcon" class="imageLink"/></a>
				<spring:message code="extended_grid_report.report_options" text="Report Options"/>
			</legend>
			<div id="resultSetDiv">
			<table style="width:100%">
				<tr>
					<td valign="top" width="200px;">
						<label><spring:message code="extended_grid_report.report_header" text="Report Header"/></label>
					</td>
					<td>
						<textarea id="reportHeader" name="reportHeader" rows="2" cols="50"></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td valign="center" width="200px;">
						<label><spring:message code="extended_grid_report.report_fields" text="Report Fields"/></label>
					</td>
					<td>
						<table id="fieldsTable">
							<tr>
								<th>
									<b><spring:message code="extended_grid_report.available_fields" text="Available Fields"/></b>
								</th>
								<th></th>
								<th>
									<b><spring:message code="extended_grid_report.selected_fields" text="Selected Fields"/></b>
								</th>
								<th></th>
							</tr>
							<tr>
								<td>
									<select id="allItems" multiple="multiple" style="height:200px;width:150px;">
										<c:forEach items="${availableFields}" var="i">
											<option value="${fn:trim(i)}">
												<spring:message code="extended_grid_report.available_fields.${fn:trim(i)}" text="${fn:trim(i)}"/>
											</option>
										</c:forEach>
									</select>
								</td>
								<td>
									<input type="button" id="to2" value="&gt;" class="btnMS" />
									<!-- <input type="button" id="allTo2" value="&gt;&gt;" class="btnMS" /> -->
									<br>
									<!-- <input type="button" id="allTo1" value="&lt;&lt;" class="btnMS" /> -->
									<input type="button" id="to1" value="&lt;" class="btnMS"  />
								</td>
								<td>
									<select id="selectedItems" multiple="multiple" style="height:200px;width:150px;">
										<c:forEach items="${defaultFields}" var="i">
											<option value="${fn:trim(i)}">
												<spring:message code="extended_grid_report.available_fields.${fn:trim(i)}" text="${fn:trim(i)}"/>
											</option>
										</c:forEach>					
									</select>
								</td>
								<td>
									<input id="up" type="button" value="&#x2191;" class="up btnMS"/>
									<br>
									<input id="down" type="button" value="&#x2193;" class="down btnMS"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>	
			<select id="itemMaster" style="display:none;">
				<c:forEach items="${availableFields}" var="i">
					<c:forEach items="${availableFields}" var="i">
						<option value="${fn:trim(i)}">
							<spring:message code="extended_grid_report.available_fields.${fn:trim(i)}" text="${fn:trim(i)}"></spring:message>
						</option>
					</c:forEach>
				</c:forEach>
			</select>
			<c:forEach begin="1" end="${fn:length(availableFields)}" varStatus="fieldCount">
				<input type="hidden" id="reportField_${fieldCount.count}" name="reportField_${fieldCount.count}" value="" />
			</c:forEach>
			</div>
		</fieldset>
		<div class="fields" style="margin-top: 10px;">
			<!-- <h2></h2> -->
			<p class="tright">
				<input id="generateExtendedGridReport" type="button" value="<spring:message code='extended_grid_report.generateExtendedGridReport' text='Generate Report'/>" class="butRep">
			</p>
		</div>
		<input type="hidden" id="session" name="session" value="${session}">
		<input type="hidden" id="questionType" name="questionType" value="${questionType}">
		<input type="hidden" id="questionTypeType" name="questionTypeType" value="${questionTypeType}">
		<input type="hidden" id="locale" name="locale" value="${locale}">	
		<input type="hidden" id="reportQuery" name="reportQuery" value="${fn:toUpperCase(questionTypeType)}_EXTENDED_GRID_QUERY">
		<input type="hidden" id="reportFieldsCount" name="reportFieldsCount" value="">
		<input type="hidden" id="reportSelectQuery" name="reportSelectQuery" value="${fn:toUpperCase(questionTypeType)}_EXTENDED_GRID_QUERY_SELECTED_FIELDS">
		<input type="hidden" id="reportHeaderSelectQuery" name="reportHeaderSelectQuery" value="${fn:toUpperCase(questionTypeType)}_EXTENDED_GRID_QUERY_HEADERS_SELECTED_FIELDS">
		<input type="hidden" id="field_select_query" name="field_select_query" value="">
		<input type="hidden" id="field_header_select_query" name="field_header_select_query" value="">
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
<input type="hidden" id="selectItemFirstMessage" value="<spring:message code='generic.selectitem' text='Select an item first'/>">
<input type="hidden" id="generateReportPromptMsg" value="<spring:message code='extended_grid_report.generateReportPromptMsg' text='Do you want to generate extended grid report now?'/>">
</body>
</html>