<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			//on change of houseType,house,session,questionType
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val());			
			//Initially values in module must be set to the values read from model.
			$("#houseType").val($("#selectedHouseType").val());
			$("#sessionYear").val($("#selectedSessionYear").val());
			$("#sessionType").val($("#selectedSessionType").val());
			$("#questionType").val($("#selectedQuestionType").val());	
			//removing houseType options on the basis of houseTypeFromRole value.
			var houseType=$("#houseTypeFromRole").val();
			if(houseType=="lowerhouse"){
				$("#selectedHouseType option[value='upperhouse']").remove();
			}else if(houseType=="upperhouse"){
				$("#selectedHouseType option[value='lowerhouse']").remove();
			}	
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				if(value!=""){					
							$("#houseType").val(value);
							//if we are changing the house type at module level and then we try to reload grid then 
							//we need to change the housetype1 and housetype2 parameters in grid url.
							//also gridURLParams value will be passed as query string.
							$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val());			
							var oldURL=$("#grid").getGridParam("url");
							var baseURL=oldURL.split("?")[0];
							newURL=baseURL+"?"+$("#gridURLParams").val();
							$("#grid").setGridParam({"url":newURL});
							$("#grid").trigger("reloadGrid");							
				}				
			});			
			$("#selectedSessionYear").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#sessionYear").val(value);
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}			
			});
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#sessionYear").val(value);
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}					
			});
			$("#selectedQuestionType").change(function(){
				var value=$(this).val();
				if(value!=""){
					$("#questionType").val(value);
					$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val());			
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");					
				}
			});							
			$('#new_record').click(function(){
				newRecord();
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});					
		});
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}		
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">	
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
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
			<a href="#" id="select_session_year" class="butSim">
				<spring:message code="question.sessionyear" text="Year"/>
			</a>
			<input name="selectedSessionYear" id="selectedSessionYear" style="width:50px;height: 25px;" type="text" class="integer" value="${sessionYear}">				
			 |			
			<a href="#" id="select_sessionType" class="butSim">
				<spring:message code="question.sessionType" text="Session Type"/>
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
			<a href="#" id="select_questionType" class="butSim">
				<spring:message code="question.questionType" text="Question Type"/>
			</a>
			<select name="selectedQuestionType" id="selectedQuestionType" style="width:100px;height: 25px;">			
			<c:forEach items="${questionTypes}" var="i">
			<c:choose>
			<c:when test="${questionType==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			</select>				
			<p>&nbsp;</p>
		</div>
		<div class="commandbarContent">				
			<a href="#" id="new_record" class="butSim">
				<spring:message code="question.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="question.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="question.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="question.search" text="Search"/>
			</a>		
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">	
	</div>
</body>
</html>