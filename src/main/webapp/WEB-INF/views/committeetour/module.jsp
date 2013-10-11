<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committeetour.list" text="List of Committee Tours"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* =============== ACTIONS =============== */
	function showCommitteeTourList() {
		var id = 'list_tab';
		var url = 'committeetour/list';
		showTabByIdAndUrl(id, url);
	}
	
	function newCommitteeTour() {
		$('#cancelFn').val('newCommitteeTour');
		$('#key').val('');
		var id = 'details_tab';
		var url = 'committeetour/new';
		showTabByIdAndUrl(id, url);
	}

	function editCommitteeTour(rowId) {
		$('#cancelFn').val('editCommitteeTour');
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return false;
		}
		var id = 'details_tab';
		var url = 'committeetour' + '/' + rowId + '/' + 'edit';
		showTabByIdAndUrl(id, url);
	}

	function deleteCommitteeTour(rowId) {
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return;
		}
		var promptMsg = $('#confirmDeleteMessage').val() + rowId;
		var options = {buttons: {Ok:true, Cancel:false}, 
					   callback: function(v){
			   				if(v){
								$.delete_('committeetour' + '/' + rowId + '/' + 'delete', 
										null, 
										function(data, textStatus, XMLHttpRequest) { 
											reloadGrid(); 
										}
								);
			        		}
					  }};
		$.prompt(promptMsg, options);
	}

	function searchCommitteeTours() {
		searchRecord();
	}

	function viewCommitteeTour(rowId) {
		$('#cancelFn').val('viewCommitteeTour');
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return false;
		}
		var id = 'details_tab';
		var url = 'committeetour' + '/' + rowId + '/' + 'view';
		showTabByIdAndUrl(id, url);
	}

	function reloadGrid() {
		var oldURL = $("#grid").getGridParam("url");
		var baseURL = oldURL.split("?")[0];
		var newURL = baseURL + "?" + getGridURLParams();
		$("#grid").setGridParam({"url":newURL});
		$("#grid").trigger("reloadGrid");
	}

	/* =============== DOCUMENT READY =============== */
	$('document').ready(function(){
		$('#list_tab').click(function(){
			showCommitteeTourList();
		});	
		
		showCommitteeTourList();
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
	
	<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>">
	<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>">
</div>
</body>
</html>