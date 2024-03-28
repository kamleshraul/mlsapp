<%@page import="org.mkcl.els.common.util.FormaterUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>Roster Word Format</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<script>
function showProceedingList() {

	showTabByIdAndUrl('list_tab', 'editing/displayRosterList?houseType=' + $('#selectedHouseType').val() 
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+'&language=1'
			+'&day='+$("#selectedDay").val()
			+ '&userGroup=' + $("#userGroup").val()
			+ '&userGroupType=' + $("#userGroupType").val()
			+'&locale=mr_IN'
			);
}

/**** double clicking record in grid handler ****/
function rowDblClickHandler(rowid, iRow, iCol, e) {
	console.log("hi")
	$("#selectionDiv1").hide();
	$("#cancelFn").val("rowDblClickHandler");
	$('#key').val(rowid);
}

$(document).ready(function(){
	showProceedingList();
	
	/**** house type changes then reload grid****/			
	$("#selectedHouseType").change(function(){
		var value=$(this).val();
		if(value!=""){	
			showProceedingList()								
		}	
	});	
	/**** session year changes then reload grid****/			
	$("#selectedSessionYear").change(function(){
		var value=$(this).val();
		if(value!=""){		
			showProceedingList()									
		}			
	});
	/**** session type changes then reload grid****/
	$("#selectedSessionType").change(function(){
		var value=$(this).val();
		if(value!=""){	
			showProceedingList()								
		}			
	});	
	
	/**** Day changes then reload grid****/
	$("#selectedDay").change(function(){
		var value=$(this).val();
		if(value!=""){			
			showProceedingList()								
		}			
	});
})
</script>
</head>
<body>

<div class="clearfix tabbar">
<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details">
				   </spring:message>
				</a>
			</li>	
				
		</ul>
	<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">
				
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="proceeding.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">			
			<c:forEach items="${houseTypes}" var="i">
				<c:choose>
					<c:when test="${houseType==i.type}">
						<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
					</c:when>
					<c:otherwise>
						<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</select> |	
			
		
			
			<div id="sessionDiv" style="display:inline;">				
				<a href="#" id="select_session_year" class="butSim">
					<spring:message code="proceeding.sessionyear" text="Year"/>
				</a>
				<select name="selectedSessionYear" id="selectedSessionYear" style="width:100px;height: 25px;">				
					<c:forEach var="i" items="${years}">
						<c:choose>
							<c:when test="${i.name==sessionYear }">
								<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>				
							</c:when>
							<c:otherwise>
								<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
							</c:otherwise>
						</c:choose>
					</c:forEach> 
				</select> |	
									
				<a href="#" id="select_sessionType" class="butSim">
					<spring:message code="proceeding.sessionType" text="Session Type"/>
				</a>
				<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">				
					<c:forEach items="${sessionTypes}" var="i">
						<c:choose>
							<c:when test="${sessionType==i.id}">
								<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
							</c:when>
							<c:otherwise>
								<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>			
							</c:otherwise>
						</c:choose>			
					</c:forEach> 
				</select> |		
				
				<%-- <a href="#" id="select_day" class="butSim">
				<spring:message code="proceeding.day" text="Day"/>
				</a>
				<select name="selectedDay" id="selectedDay" style="width:100px;height: 25px;">				
				<c:forEach items="${days}" var="i">
				<c:choose>
				<c:when test="${day==i}">
				<option value="${i.number}" selected="selected"><c:out value="${i.value}"></c:out></option>				
				</c:when>
				<c:otherwise>
				<option value="${i.number}"><c:out value="${i.value}"></c:out></option>			
				</c:otherwise>
				</c:choose>			
				</c:forEach> 
				</select> |	 --%>		
			</div>
					
			<hr>		
			
				<div class="tabContent">
				</div>	
			
			<input type="hidden" id="selectedLanguage" value=1>					
		</div>	
		</div>		
</body>
</html>