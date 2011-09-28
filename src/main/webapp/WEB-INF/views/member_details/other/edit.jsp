<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form:form cssClass="wufoo" action="member_other_details" method="PUT" modelAttribute="memberOtherDetails">
	<div class="info">
		 <h2><spring:message code="member_other_details.edit.heading"/></h2>		
		<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label"/></div>
	</div>
	
	<ul>
	
		<li>
		<label class="desc"><spring:message code="generic.id"/>&nbsp;*</label>
			<div>
				<form:input cssClass="field text medium" path="id" readonly="true" /> 
			</div>
		</li>
							
		<li>
		<label class="desc"><spring:message code="generic.locale"/>&nbsp;*</label>
			<div>
				<form:select cssClass="field select medium" path="locale"> 
					<form:option value="en">English</form:option>
					<form:option value="hi_IN">Hindi</form:option>
					<form:option value="mr_IN">Marathi</form:option>
				</form:select>
			</div>
		</li>
				
		<li>
		<label class="desc"><spring:message code="member_other_details.noofTerms"/>&nbsp;*</label>
			<div>
			<form:input cssClass="integer field text medium" path="noOfTerms"/><form:errors path="noOfTerms" cssClass="field_error" />	
		   </div>
		</li>		
		
		<li>
		<input id="addBt"  type="button" value="<spring:message code="generic.add"/>" onclick="addPosition()">
		</li>
				
		<c:choose>		
		<c:when test="${!(empty positions)}">		
		<c:set  var="count" value="1"></c:set>
		<c:forEach items="${positions}" var="i">
		<li class="name" id="li${count}">
		<span>
		<label class="desc"><spring:message code="member_other_details.positionHeldPeriod"/>&nbsp;*</label>	
		<input type="hidden" id="positionId${count}" name="positionId${count}" value="${i.id}">
		<input type="text" id="positionPeriod${count}" name="positionPeriod${count}" value="${i.period}" class="field text">	
		</span>	
		<span>
		<label class="desc"><spring:message code="member_other_details.positionHeldDetails"/>&nbsp;*</label>		
		<textarea id="positionDetail${count}" name="positionDetail${count}" class="field textarea">${i.details}</textarea>
		</span>
		<span>
		<input id="deleteBt${count}"  type="button"  value="<spring:message code="generic.delete"/>" onclick="deletePosition(${count})">
		</span>
		</li>	
		<c:set value="${count+1}" var="count"/>
		</c:forEach>
		<div id="newPositionsDiv"></div>
		</c:when>
		<c:otherwise>
		<div id="newPositionsDiv"></div>
		</c:otherwise>
		</c:choose>	
					
		<li>
		<label class="desc"><spring:message code="member_other_details.socioCulturalActivities"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="socioCulturalActivities"/><form:errors path="socioCulturalActivities" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.literaryArtisticScAccomplishment"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="literaryArtisticScAccomplishment"/><form:errors path="literaryArtisticScAccomplishment" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.specialInterests"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="specialInterests"/><form:errors path="specialInterests" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.pastimeRecreation"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="pastimeRecreation"/><form:errors path="pastimeRecreation" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.sportsClubs"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="sportsClubs"/><form:errors path="sportsClubs" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.countriesVisited"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="countriesVisited"/><form:errors path="countriesVisited" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.experience"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="experience"/><form:errors path="experience" cssClass="field_error" />	
			</div>
		</li>
		
		<li>
		<label class="desc"><spring:message code="member_other_details.otherInfo"/>&nbsp;*</label>
			<div>
				<form:textarea cssClass="field textarea small" path="otherInfo"/><form:errors path="otherInfo" cssClass="field_error" />	
			</div>
		</li>	
			
		<li class="buttons">
			<input id="saveForm" class="btTxt" type="submit" value="<spring:message code="generic.submit"/>" ></input>
		</li>
		
	</ul>	
	<form:hidden path="version"></form:hidden>		
	<input type="hidden" id="noOfPositions" name="noOfPositions" value="${noOfPositions}"></input>
	</form:form>
</body>
<head>
	<title>
	<spring:message code="member_information_system"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	var initialNoOfPositions=$('#noOfPositions').val();
	if(initialNoOfPositions==""){
		$('#noOfPositions').val(0);
	}
	function deletePosition(position){
		var id=$('#positionId'+position).val();
		if(id!=undefined){
			if(id!=""){
				$.ajax({
   				    type: "DELETE",
   				    url: 'member_position_details/'+id+'/delete',
   				    success: function(json) {
   				        alert("Record deleted successfully");
   				    }
   				});
			}
		}
		$('#li'+position).detach();
		
	}
	function addPosition(){
			var totalPositions=parseInt($('#noOfPositions').val());
			var newPosition=totalPositions+1;
			var text="<li class='name' id='li"+newPosition+"'>"+
			"<span>"+
			"<label class='desc'><spring:message code='member_other_details.positionHeldPeriod'/>&nbsp;*</label>"+	
			"<input type='text' id='positionPeriod"+newPosition+"' name='positionPeriod"+newPosition+"' class='field text'>"+	
			"</span>"+	
			"<span>"+
			"<label class='desc'><spring:message code='member_other_details.positionHeldDetails'/>&nbsp;*</label>"+		
			"<textarea id='positionDetail"+newPosition+"' name='positionDetail"+newPosition+"' class='field textarea'>\</textarea>"+
			"</span>"+
			"<span>"+
			"<input id='deleteBt"+newPosition+"' type='button' onclick='deletePosition("+newPosition+")' value='<spring:message code='generic.delete'/>'/> "+
			"</span>"+
			"</li>";
			$('#newPositionsDiv').append(text);
			$('#noOfPositions').val(newPosition);		
	}		

	//function for sorting fields according to their positions
	//important thing is all fields to be sorted must be placed inside a li 
	
	$('li').sortElements(function(a, b){
		if($(a).attr("id")!=undefined&&$(b).attr("id")!=undefined){
			 return parseInt($(a).attr("id")) > parseInt($(b).attr("id")) ? 1 : -1;
		}
	});
	</script>
</head>
</html>