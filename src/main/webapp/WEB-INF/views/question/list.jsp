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
				newQuestion();
			});
			$('#edit_record').click(function(){
				editQuestion($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteQuestion($('#key').val());
			});	
			$("#submitQuestion").click(function() {			
				var selectedRows = $('#grid').getGridParam('selarrrow');				
				var qssd = ${questionSubmissionStartDate};				
				var questionSubmissionStartDate = new Date(qssd);				
				if(selectedRows == null || selectedRows.length==0){
					$.prompt($('#selectRowFirstMessage').val());
					return false;
				} else if(questionSubmissionStartDate > new Date()) {					
					$.prompt($('#dateBeforeSubmissionStartedMessage').val());
					return false;
				}
				else {					
				var completed = true; //flag for checking whether all selected questions are completed & ready for submission
				var incompleteQuestions = new Array();
				var j=0; //count for incompleted questions
				//check for any incomplete questions
				for(var i=0; i<selectedRows.length; i++){						
					var status = $('#grid').getCell(selectedRows[i], 'status.type');					
					if(status != 'question_init_complete') {
						completed = false;
						incompleteQuestions[j] = $('#grid').getCell(selectedRows[i], 'number');
						j++;
					};				
				}
				if(completed == true) {
					$.get('question/'+selectedRows+'/submit', function(data) {
						$.fancybox.open(data);
					});
				} else {
					$.prompt($('#questionNumberMessage').val() + " " + incompleteQuestions + " " + $('#questionIncompleteOrSubmittedMessage').val());
				};	
				};
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
			<select name="selectedSessionYear" id="selectedSessionYear" style="width:100px;height: 25px;">				
			<c:forEach var="i" items="${years}">
			<option value="${i}" ><c:out value="${i}"></c:out></option>				
			</c:forEach> 
			</select> |			
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
			</a> |
			<a href="#" id="submitQuestion" class="butSim">
				<spring:message code="question.submit" text="submit"/>
			</a>		
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/info.jsp" %>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="selectRowFirstMessage" value='<spring:message code="generic.selectRowFirstMessage" text='Please select the desired row first'></spring:message>'>
	<input type="hidden" id="dateBeforeSubmissionStartedMessage" value='<spring:message code="question.dateBeforeSubmissionStartDateMessage" text='You cannot submit before question submission start date'></spring:message>'>
	<input type="hidden" id="questionNumberMessage" value='<spring:message code="question.questionNumberMessage" text='Question No.'></spring:message>'>
	<input type="hidden" id="questionIncompleteOrSubmittedMessage" value='<spring:message code="question.questionIncompleteOrSubmittedMessage" text='are either incomplete or submitted already'></spring:message>'>
	</div>
</body>
</html>