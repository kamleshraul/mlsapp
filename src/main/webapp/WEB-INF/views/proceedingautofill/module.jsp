<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="proceedingautofill.list" text="List of Proceeding Autofill"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* =============== ACTIONS =============== */
	function showProceedingAutoFillList() {
		var id = 'list_tab';
		var url = 'proceedingautofill/list?' + getGridURLParams();
		showTabByIdAndUrl(id, url);
	}
	
	function newProceedingAutofill() {
		$('#cancelFn').val('newProceedingAutofill');
		$('#key').val('');
		var id = 'details_tab';
		var url = 'proceedingautofill/new?' + getGridURLParams();
		showTabByIdAndUrl(id, url);
	}

	function editProceedingAutofill(rowId) {
		$('#cancelFn').val('editProceedingAutofill');
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return false;
		}
		var id = 'details_tab';
		var url = 'proceedingautofill' + '/' + rowId + '/' + 'edit';
		showTabByIdAndUrl(id, url);
	}

	function deleteProceedingAutofill(rowId) {
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return;
		}
		var promptMsg = $('#confirmDeleteMessage').val() + rowId;
		var options = {buttons: {Ok:true, Cancel:false}, 
					   callback: function(v){
			   				if(v){
								$.delete_('proceedingautofill' + '/' + rowId + '/' + 'delete', 
										null, 
										function(data, textStatus, XMLHttpRequest) { 
											reloadGrid(); 
										}
								);
			        		}
					  }};
		$.prompt(promptMsg, options);
	}

	function searchProceedingAutofill() {
		searchRecord();
	}

	function reloadGrid() {
		var oldURL = $("#grid").getGridParam("url");
		var baseURL = oldURL.split("?")[0];
		var newURL = baseURL + "?" + getGridURLParams();
		$("#grid").setGridParam({"url":newURL});
		$("#grid").trigger("reloadGrid");
	}
	
	function getGridURLParams() {
		var username = "username=" + $('#currentUsername').val();
		var gridURLParamsVal = username;
		return gridURLParamsVal;
	}
	
	
	/* =============== DOCUMENT READY =============== */
	$('document').ready(function(){
		$('#list_tab').click(function(){
			showProceedingAutoFillList();
		});	
		
		
		
		showProceedingAutoFillList();
	});
	</script>
</head>
<body>
<div class="clearfix tabbar">
	<ul class="tabs">
		<li>
			<a href="#" id="list_tab" class="selected tab">
				<spring:message code="generic.list" text="List"></spring:message>
			</a>
		</li>
		<li>
			<a href="#" id="details_tab" class="tab">
			   <spring:message code="generic.details" text="Details"></spring:message>
			</a>
		</li>
	</ul>
		
	<div class="tabContent">
	</div>
	
	<input type="hidden" id="key" name="key">
	<input type="hidden" id="currentUsername" name="currentUsername" value="${username}">
	<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>">
	<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>">
</div>
</body>
</html>