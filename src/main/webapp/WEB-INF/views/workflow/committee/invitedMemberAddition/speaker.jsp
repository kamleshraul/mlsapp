<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="committee" text="Committee"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function onPageLoad() {
		conditionalReadOnlyRendering();
	}

	function conditionalReadOnlyRendering() {
		var isRenderAsReadOnly = $('#renderAsReadOnly').val();
		if(isRenderAsReadOnly == 'true') {
			// Render all the visible attributes on the page as readOnly
			$('#remarks').attr('readOnly', true);
			$('.autosuggestmultiple').attr('readOnly', true);
		}
	}

	function getHouseTypeId() {
		var id = $('#houseTypeId').val();
		return id;
	}

	function extractLast(term) {
		return split(term).pop();
	}

	function split(val) {
		return val.split( /,\s*/ );
	}

	function getErrorMsg() {
		var errorMsg = $("#ErrorMsg").val();
		if(errorMsg == '') {
			errorMsg = "Error occured contact for support.";
		}
		return errorMsg;
	}
	
	$('document').ready(function(){
		onPageLoad();

		$('.autosuggestmultiple').change(function(){
			// If a value is removed from the AutoComplete box, then that value needs to be removed 
			// from the attached Select Box.
			// Iterate through the Select Box selected value. If a value is present in AutoComplete
			// but absent in Select Box then remove that that value from the Select Box.
			var controlId = $(this).attr("id");
			var value = $(this).val();
			$("select[name='" + controlId + "'] option:selected").each(function(){
				var optionClass = $(this).attr("class");
				if(value.indexOf(optionClass) == -1) {
					$("select[name='" + controlId + "'] option[class='" + optionClass + "']").remove();
				}		
			});	
			$("select[name='" + controlId + "']").hide();				
		});

		// http://api.jqueryui.com/autocomplete/#event-select
		$('.autosuggestmultiple').autocomplete({
			minLength:3,
			source: function(request, response){
				var url = 'ref/houseType/' + getHouseTypeId() + '/members';
				var data = {term: extractLast(request.term)}; 
				$.get(url, data, response).fail(function(){
					var errorMsg = getErrorMsg();
					$("#error_p").html(errorMsg).css({'color':'red', 'display':'block'});
					scrollTop();
				});
			},
			search: function(){
				var term = extractLast(this.value);
				if (term.length < 2) {
					return false;
				}
			},
			focus: function(){
				return false;
			},
			select: function(event, ui){
				var itemId = ui.item.id;
				var itemValue = ui.item.value;
				
				var terms = $(this).val().split(",");
				var controlId = $(this).attr("id");
				
				// If atleast one option is already added
				if($("select[name='" + controlId + "']").length > 0) {
					// If option being selected is not already present
					if($("select[name='" +controlId + "'] option[value='" + itemId + "']").length == 0) {
						if(itemId != undefined && itemValue != undefined) {
							var text = "<option value='" + itemId + "' selected='selected' class='" + itemValue + "'></option>";
							$("select[name='" + controlId + "']").append(text);
							terms.pop();
							terms.push(itemValue);
							terms.push("");
							$(this).val(terms.join(","));
						}
					}
				}
				else {
					if(itemId != undefined && itemValue != undefined) {
						var selectOpen = "<select name='" + controlId + "' multiple='multiple'>";
						var option = "<option value='" + itemId + "' selected='selected' class='" + itemValue + "'></option>";
						var selectClose = "</select>";

						var text = selectOpen + option + selectClose;
						$(this).after(text);

						terms.pop();
						terms.push(itemValue);
						terms.push("");
						$(this).val(terms.join(","));
					}		
				}
				$("select[name='" + controlId + "']").hide();
				return false;
			}
		});
		
	});
	</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error != '') && (error != null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix">
<form:form action="workflow/committee/invitedMemberAddition" method="PUT" modelAttribute="committeeCompositeVO">
<%@ include file="/common/info.jsp" %>

<h2>
	<spring:message code="committee.invitedMemberAddition" text="Invited Member Addition"/>
</h2>

<c:choose>
<c:when test="${empty committeeCompositeVO.committeeVOs}">
	<spring:message code="committee.noCommitteesToBeProcessed" text="There are no Committees to be processed"/>
</c:when>
<c:otherwise>
	<div class="scrollable">
	<table class="uiTable" border="1">
		<tr>
			<th><spring:message code="committee.committees" text="Committees"/></th>
			<th><spring:message code="committee.maximumMembers" text="Maximum Members"/></th>
			
			<th><spring:message code="committee.chairman" text="Chairman"/></th>
			<th><spring:message code="committee.members" text="Members"/></th>
			
			<th><spring:message code="committee.invitedMembers" text="Invited Members"/></th>
		</tr>
	
		<c:set var="committeeCounter" value="1"></c:set>
		<c:forEach items="${committeeCompositeVO.committeeVOs}" var="committeeVO">
			<tr>
				<td>${committeeVO.committeeDisplayName}</td>
				<td>${committeeVO.maxCommitteeMembers}</td>
								
				<td>
					<textarea id="chairman" readonly="readonly" rows="5" cols="30">${committeeVO.committeeChairman.memberName}</textarea>
				</td>
				
				<td>
					<textarea id="members" readonly="readonly" rows="5" cols="30">${committeeVO.committeeMembersName}</textarea>
				</td>
				
				<td>
					<textarea id="invitedMembers_${committeeVO.committeeId}" class="autosuggestmultiple" rows="5" cols="30">${committeeVO.invitedCommitteeMembersName}</textarea>
					<c:if test="${not empty invitedCommitteeMembers}">		
						<select name="invitedMembers_${committeeVO.committeeId}" multiple="multiple">
							<c:forEach items="${invitedCommitteeMembers}" var="i">
								<option value="${i.memberId}" class="${i.memberName}"></option>
							</c:forEach>		
						</select>
					</c:if>
				</td>
			</tr>
			
			<!-- Hidden input field that stores the id of the committee. This will be used in the
				 Controller to determine the committees being processed -->
			<input type="hidden" id="committeeId${committeeCounter}" name="committeeId${committeeCounter}" value="${committeeVO.committeeId}"/>
			<c:set var="committeeCounter" value="${committeeCounter + 1}"></c:set>
		</c:forEach>
	</table>
	</div>
	<p></p>
	
	<p>
	<label class="small"><spring:message code="committee.putUpFor" text="Put Up For" /></label>
	<select id="status" name="status" class="sSelect">
	<c:choose>
		<c:when test="${not empty statuses}">
			<c:forEach items="${statuses}" var="i">
				<c:choose>
					<c:when test="${status.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<option value="${status.id}" selected="selected"><c:out value="${status.name}"></c:out></option>
		</c:otherwise>
	</c:choose>		
	</select>
	</p>
	
	<c:if test="${hideNextActors ne true}">
		<c:if test="${not empty actor}">
			<p>
			<label class="small"><spring:message code="committee.nextactor" text="Next Actor"/></label>
			<select id="actor" name="actor" class="sSelect">
			<c:choose>
				<c:when test="${not empty actors}">
					<c:forEach items="${actors}" var="i">
						<c:choose>
							<c:when test="${actor.id == i.id}">
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<option value="${actor.id}" selected="selected"><c:out value="${actor.name}"></c:out></option>
				</c:otherwise>
			</c:choose>
			</select>
			</p>
		</c:if>
	</c:if>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="committee.remarks" text="Remarks"/></label>
	<textarea id="remarks" name="remarks"  class="wysiwyg" rows="2" cols="50">${remarks}</textarea>	
	</p>
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
		<c:if test="${renderAsReadOnly ne true}">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
		</p>
	</div>
</c:otherwise>
</c:choose>

<!-- Hidden fields  -->
<input type="hidden" id="workflowInit" name="workflowInit" value="${workflowInit}"/>
<input type="hidden" id="workflowName" name="workflowName" value="${workflowName}"/>
<input type="hidden" id="houseTypeId" name="houseTypeId" value="${houseType.id}"/>
<input type="hidden" id="renderAsReadOnly" name="renderAsReadOnly" value="${renderAsReadOnly}"/>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</form:form>
</div>
</body>