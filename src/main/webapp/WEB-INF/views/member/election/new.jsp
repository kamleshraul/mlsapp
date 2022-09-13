<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.election" text="Member Election Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style>
	#submit{
	cursor: pointer;
	}
	#cancel{
	cursor: pointer;
	}
	</style>
	<script type="text/javascript">
	var rivalCount=parseInt($('#rivalCount').val());
	var totalRivalCount=0;
	totalRivalCount=totalRivalCount+rivalCount;
	function addRival(){
		rivalCount=rivalCount+1;
		totalRivalCount=totalRivalCount+1;
		var text="<div id='rival"+rivalCount+"'>"+
				  "<p>"+
	    		  "<label class='small'>"+$('#rivalNameMessage').val()+"</label>"+
	    		  "<input name='rivalName"+rivalCount+"' id='rivalName"+rivalCount+"' class='sText'>"+
	    		  "</p>"+
	    		  "<p>"+
	    		  "<label class='small'>"+$('#rivalVotesMessage').val()+"</label>"+
	    		  "<input name='rivalVotesReceived"+rivalCount+"' id='rivalVotesReceived"+rivalCount+"' class='sText'>"+
	    		  "</p>"+
				  "<p>"+
		              "<label class='small'>"+$('#rivalPartyMessage').val()+"</label>"+
		              "<select name='rivalParty"+rivalCount+"' id='rivalParty"+rivalCount+"' class='sSelect'>"+
				      $('#partyMaster').html()+
				      "</select>"+
				      "</p>"+
				      "<input type='button' class='button' id='"+rivalCount+"' value='"+$('#deleteRivalMessage').val()+"' onclick='deleteRival("+rivalCount+");'>"+
					  "<input type='hidden' id='rivalId"+rivalCount+"' name='rivalId"+rivalCount+"'>"+
					  "<input type='hidden' id='rivalLocale"+rivalCount+"' name='rivalLocale"+rivalCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='rivalVersion"+rivalCount+"' name='rivalVersion"+rivalCount+"'>"+
					  "</div>"; 
				      var prevCount=rivalCount-1;
				      if(totalRivalCount==1){
					   $('#addRival').after(text);
					    }else{
				      $('#rival'+prevCount).after(text);
				      }
				      $('#rivalCount').val(rivalCount); 				
	}
	function deleteRival(id){
		$('#rival'+id).remove();
		totalRivalCount=totalRivalCount-1;
		if(id==rivalCount){
			rivalCount=rivalCount-1;
		}		
	}	

	function loadDivisionDistricts(constituency){
	    $.get('ref/divdis/'+constituency,function(data){
		    $("#division").val(data.division);
		    var text="";
		    if(data.districts.length>0){
		    for(var i=0;i<data.districts.length;i++){
			    text=text+"<option value='"+data.districts[i].id+"' selected='selected'>"+data.districts[i].name+"</option>";
		    }
		    $("#districts").empty();
			$("#districts").html(text);
		    }else{
			$("#districts").empty();			    
		    }			
	    },'json').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
		});
	}

	function loadConstituencies(houseType){
		$.get('ref/constituencies/'+houseType,function(data){
			var text="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			    text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$("#constituency").empty();
			$("#constituency").html(text);
			loadDivisionDistricts(data[0].id);
			}else{
				$("#constituency").empty();
				$("#division").val("");
				$("#districts").empty();
			}
		},'json').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
		});
	}

	function loadElections(houseType){
		$.get('ref/elections/'+houseType,function(data){
			var text="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			    text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$("#election").empty();
			$("#election").html(text);
			}else{
				$("#election").empty();
			}
			loadConstituencies(houseType);			
		},'json').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val());
			}else{
				$("#error_p").html("Error occured contact for support.");
			}
		});
	}

	function getElectionType(electionId) {
		$.get('ref/election/' + electionId + '/electionType',
			function(data){
				$("#electionType").val(data.name);
			},
			'json').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
	}
	
		$(document).ready(function(){
			$('#partyMaster').hide();
			$('#addRival').click(function(){
				addRival();
			});
			if($('#currentConstituency').val()!=""){
				$('#constituency').val($('#currentConstituency').val());
			}
			$("#houseTypes").val($("#selectedhouseType").val());					
			$("#houseTypes").change(function(){
				loadElections($(this).val());
				$("#selectedhouseType").val($(this).val());
				$("span[id$='errors']").remove();
				$("div[id^='rival']").remove();
			});
			$("#constituency").change(function(){
				loadDivisionDistricts($(this).val());
			});

			$('.election').change(function(){
				getElectionType($(this).val());
			});
		});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/election" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}
	</h2>
	<h2>
		<spring:message code="election.result" text="Election Result"/>
	</h2>
	<form:errors path="version" cssClass="validationError" cssStyle="color:red;"/>	
	<p>
	<label class="small"><spring:message code="member.house.houseType"	text="House Types" /></label>
	<select id="houseTypes" name="houseTypes" class="sSelect">
	<c:forEach items="${houseTypes }" var="i">
	<option value="${i.type}">${i.name}</option>
	</c:forEach>
	</select>											
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.election" text="Election"/></label>
		<form:select path="election" items="${elections}" itemLabel="name" itemValue="id" cssClass="sSelect election"/>
		<form:errors path="election" cssClass="validationError"/>			
	</p>	
	
	<p>
		<label class="small"><spring:message code="member.election.electionType" text="Election Type"/></label>
		<input type="text" class="sText" id="electionType" name="electionType" value="${electionType}" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.constituencies" text="Constituency"/></label>
		<form:select path="constituency" items="${constituencies}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="constituency" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.division" text="Division"/></label>
		<input name="division" id="division" type="text" value="${division}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.district" text="District"/></label>
		<c:choose>
		<c:when test="${!(empty districts) }">
		<select name="districts" id="districts" class="sSelect" size="2" multiple="multiple">
		<c:forEach items="${districts}" var="i">
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
		</select>
		</c:when>
		<c:otherwise>
		<select name="districts" id="districts" class="sSelect" size="2" multiple="multiple">
		</select>
		</c:otherwise>
		</c:choose>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.votingDate" text="Voting Date"/></label>
		<form:input path="votingDate" cssClass="datemask sText"/>
		<form:errors path="votingDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.electionResultDate" text="Election Result Date"/></label>
		<form:input path="electionResultDate" cssClass="datemask sText"/>
		<form:errors path="electionResultDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.noOfVoters" text="No of Voters"/></label>
		<form:input path="noOfVoters" cssClass="sText"/>
		<form:errors path="noOfVoters" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.totalValidVotes" text="Total Valid Votes"/></label>
		<form:input path="totalValidVotes" cssClass="sText"/>
		<form:errors path="totalValidVotes" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.electedMemberName" text="Elected Member's Name"/></label>
		<input type="text" class="sText" id="electedMemberName" name="electedMemberName" value="${fullname}" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.election.votesReceived" text="Votes Received"/></label>
		<form:input path="votesReceived" cssClass="sText"/>
		<form:errors path="votesReceived" cssClass="validationError"/>	
	</p>
		
	<div>
	<input type="button" class="button" id="addRival" value="<spring:message code='member.election.addRival' text='Add Rival Members'></spring:message>">
	<input type="hidden" id="rivalCount" name="rivalCount" value="${rivalCount}"/>
	
	<input type="hidden" id="deleteRivalMessage" name="deleteRivalMessage" value="<spring:message code='member.election.deleteRival' text='Delete Rival Member'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="rivalNameMessage" name="rivalNameMessage" value="<spring:message code='member.election.rivalName' text='Name'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="rivalVotesMessage" name="rivalVotesMessage" value="<spring:message code='member.election.rivalVotesReceived' text='Votes Received'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="rivalPartyMessage" name="rivalPartyMessage" value="<spring:message code='member.election.rivalParty' text='Rival Party'></spring:message>" disabled="disabled"/>
	
	<select name="partyMaster" id="partyMaster" disabled="disabled">
	<c:forEach items="${parties}" var="i">
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="rivalMembers" cssClass="validationError"></form:errors>
	<c:if test="${!(empty rivalMembers)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${rivalMembers}" var="outer">
	<div id="rival${count}">
	<p>
	    <label class="small"><spring:message code="member.election.rivalName" text="Name"/></label>
		<input name="rivalName${count}" id="rivalName${count}" class="sText" value="${outer.name}">
	</p>
	<p>
	    <label class="small"><spring:message code="member.election.rivalVotesReceived" text="Votes Received"/></label>
		<input name="rivalVotesReceived${count}" id="rivalVotesReceived${count}" class="sText" value="${outer.votesReceived}">
	</p>
	<p>
	    <label class="small"><spring:message code="member.election.rivalParty" text="Rival Party"/></label>
		<select name="rivalParty${count}" id="rivalParty${count}" class="sSelect">
		<c:forEach items="${parties}" var="i">
		<c:choose>
		<c:when test="${outer.party.id==i.id}">
	    <option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
		</c:when>
		<c:otherwise>
		<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
		</c:otherwise>
		</c:choose>		
		</c:forEach>
		</select>
	</p>
	
	<input type='button' class='button' id='${count}' value='<spring:message code="member.election.deleteRival" text="Delete Rival Member"></spring:message>' onclick='deleteRival(${count});'/>
	<input type='hidden' id='rivalId${count}' name='rivalId${count}' value="${outer.id}">
	<input type='hidden' id='rivalLocale${count}' name='rivalLocale${count}' value="${domain.locale}">
	<input type='hidden' id='rivalVersion${count}' name='rivalVersion${count}' value="${outer.version}">
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>
	</div>
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
			
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>	
	<input id="currentConstituency" name="currentConstituency" value="${currentConstituency}" type="hidden">	
	<input id="member" name="member" value="${member}" type="hidden">
	<input id="selectedhouseType" name="selectedhouseType" value="${houseType}" type="hidden">
	
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>