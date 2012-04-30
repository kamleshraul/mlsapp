<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.election" text="Member Election Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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
		$(document).ready(function(){
			$('#partyMaster').hide();
			$('#addRival').click(function(){
				addRival();
			});
			if($('#constituencySelected').val()!=""){
				$('#constituency').val($('#constituencySelected').val());
			}
		});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/election" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;
		<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	<form:errors path="version" cssClass="validationError" cssStyle="color:red;"/>	
	<p>
		<label class="small"><spring:message code="member.election.election" text="Election"/></label>
		<form:select path="election" items="${elections}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="election" cssClass="validationError"/>			
	</p>
	<p>
		<label class="small"><spring:message code="member.election.constituency" text="Constituency"/></label>
		<form:select path="constituency" items="${constituencies}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="constituency" cssClass="validationError"/>			
	</p>
	<p>
		<label class="small"><spring:message code="member.election.votingDate" text="Voting Date"/></label>
		<form:input path="votingDate" cssClass="datemask sText"/>
		<form:errors path="votingDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.election.totalValidVotes" text="Total Valid Votes"/></label>
		<form:input path="totalValidVotes" cssClass="sText"/>
		<form:errors path="totalValidVotes" cssClass="validationError"/>	
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
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>	
	<input id="constituencySelected" name="constituencySelected" value="${constituency}" type="hidden">	
	<input id="member" name="member" value="${member}" type="hidden">
</form:form>
</div>
</body>
</html>