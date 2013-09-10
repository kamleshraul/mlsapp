<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee.list" text="List of Committees"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/* =============== ACTIONS =============== */
	function showCommitteeList() {
		var id = 'list_tab';
		var url = 'committee/list' + '?' + getGridURLParams();
		showTabByIdAndUrl(id, url);
	}
	
	function newCommittee() {
		$('#cancelFn').val('newCommittee');
		$('#key').val('');
		var id = 'details_tab';
		var url = 'committee/new';
		showTabByIdAndUrl(id, url);
	}

	function editCommittee(rowId) {
		$('#cancelFn').val('editCommittee');
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return false;
		}
		var id = 'details_tab';
		var url = 'committee' + '/' + rowId + '/' + 'edit';
		showTabByIdAndUrl(id, url);
	}

	function deleteCommittee(rowId) {
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return;
		}
		var promptMsg = $('#confirmDeleteMessage').val() + rowId;
		var options = {buttons: {Ok:true, Cancel:false}, 
					   callback: function(v){
			   				if(v){
								$.delete_('committee' + '/' + rowId + '/' + 'delete', 
										null, 
										function(data, textStatus, XMLHttpRequest) { 
											reloadGrid(); 
										}
								);
			        		}
					  }};
		$.prompt(promptMsg, options);
	}

	function searchCommittees() {
		searchRecord();
	}

	function viewCommittee(rowId) {
		$('#cancelFn').val('viewCommittee');
		if(rowId == null || rowId == '') {
			var msg = $('#selectRowFirstMessage').val();
			$.prompt(msg);
			return false;
		}
		var id = 'details_tab';
		var url = 'committee' + '/' + rowId + '/' + 'view';
		showTabByIdAndUrl(id, url);
	}

	function requestToParliamentaryMinister() {
		var id = 'details_tab';
		var url = 'workflow/committee/init/requestToParliamentaryMinister';
		showTabByIdAndUrl(id, url);
	}

	function requestToLeaderOfOpposition() {
		var id = 'details_tab';
		var url = 'workflow/committee/init/requestToLeaderOfOpposition';
		showTabByIdAndUrl(id, url);
	}

	function reloadGrid() {
		var oldURL = $("#grid").getGridParam("url");
		var baseURL = oldURL.split("?")[0];
		var newURL = baseURL + "?" + getGridURLParams();
		$("#grid").setGridParam({"url":newURL});
		$("#grid").trigger("reloadGrid");
	}

	function getGridURLParams() {
		var committeeType = "committeeType=" + $('#committeeTypeFilter').val();
		var committeeName = "committeeName=" + $('#committeeNameFilter').val();
		var gridURLParamsVal = committeeType + '&' + committeeName;
		return gridURLParamsVal;
	}

	/* =============== FILTERS =============== */
	function onPageLoad() {
		prependOptionToCommitteeTypeFilter();
		prependOptionToCommitteeNameFilter();
	}

	function onCommitteeTypeFilterChange() {
		setSelectedCommitteeType();

		var committeeTypeId = $('#committeeTypeFilter').val();
		// If the "ALL" option is selected for committeeTypeFilter
		// 	1. Set committeeNameFilter as empty 
		//  2. Prepend the committeeNameFilter with the "ALL" option
		//  3. Reload the Grid
		if(committeeTypeId == 0) {
			$('#committeeNameFilter').empty();
			prependOptionToCommitteeNameFilter();
			reloadGrid();
		}
		// Else 
		// 	1. Fetch the committeeNames corresponding to the selected committeeTypeFilter
		//  2. Set committeeNameFilter as the fetched committeeNames
		// 	2. Prepend the committeeNameFilter with the "ALL" option
		//  3. Reload the Grid
		else {
			var resourceURL = "ref/committeeNames/committeeType/" + committeeTypeId;
			$.get(resourceURL, function(data){
				var dataLength = data.length;
				if(dataLength > 0) {
					var text = "";
					for(var i = 0; i < dataLength; i++) {
						text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
					}
					$('#committeeNameFilter').empty();
					$('#committeeNameFilter').html(text);
				}
				else {
					$('#committeeNameFilter').empty();
				}
				prependOptionToCommitteeNameFilter();
				reloadGrid();
			});
		}
	}

	function onCommitteeNameFilterChange() {
		setSelectedCommitteeName();
		reloadGrid();
	}

	function prependOptionToCommitteeTypeFilter() {
		var optionValue = $('#allOption').val();
		var option = "<option value='0' selected>" + optionValue + "</option>";
		$('#committeeTypeFilter').prepend(option);
	}

	function prependOptionToCommitteeNameFilter() {
		var optionValue = $('#allOption').val();
		var option = "<option value='0' selected>" + optionValue + "</option>";
		$('#committeeNameFilter').prepend(option);
	}

	function setSelectedCommitteeType() {
		var comitteeTypeId = $('#committeeTypeFilter').val();
		$('#selectedCommitteeType').val(comitteeTypeId);
	}

	function setSelectedCommitteeName() {
		var comitteeNameId = $('#committeeNameFilter').val();
		$('#selectedCommitteeName').val(comitteeNameId);
	}

	/* =============== DOCUMENT READY =============== */
	$('document').ready(function(){
		onPageLoad();

		$('#list_tab').click(function(){
			$('#selectionDiv1').show();								
			showCommitteeList();
		});	

		$('#committeeTypeFilter').change(function(){
			onCommitteeTypeFilterChange();
		});

		$('#committeeNameFilter').change(function(){
			onCommitteeNameFilterChange();
		});
		
		showCommitteeList();
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
	
	<div id="selectionDiv1" class="commandbarContent" style="margin-top: 10px;">
		<a href="#" id="select_committeeType" class="butSim">
			<spring:message code="committee.committeeType" text="Committee Type"/>
		</a>
		<select id="committeeTypeFilter" name="committeeTypeFilter" class="sSelect">
		<c:forEach items="${committeeTypes}" var="i">
			<c:choose>
				<c:when test="${selectedCommitteeType == i.id}">
					<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</select> |
		
		<a href="#" id="select_committeeName" class="butSim">
			<spring:message code="committee.committeeName" text="Committee Name"/>
		</a>
		<select id="committeeNameFilter" name="committeeNameFilter" class="sSelect">
		<c:if test="${not empty committeeNames}">
			<c:forEach items="${committeeNames}" var="i">
				<c:choose>
					<c:when test="${selectedCommitteeName == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.displayName}"></c:out></option>			
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>			
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:if>
		</select>
		<hr>
	</div>
	
	<div class="tabContent">
	</div>
	
	<input type="hidden" id="key" name="key">
	<input type="hidden" id="selectedCommitteeType" name="selectedCommitteeType">
	<input type="hidden" id="selectedCommitteeName" name="selectedCommitteeName">
	
	<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>">
	<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>">
	<input type="hidden" id="allOption" name="allOption" value="<spring:message code='generic.allOption' text='---- All ----'></spring:message>">
</div>
</body>
</html>