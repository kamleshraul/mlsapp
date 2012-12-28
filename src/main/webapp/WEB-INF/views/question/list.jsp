<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="question.list" text="List Of Questions"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectionDiv1").show();	
			$("#selectionDiv2").show();	
			$("#selectionDiv3").show();					
			//setting grid url param to initial selected values added to model in /list request.
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()+"&sessionYear="+$("#selectedSessionYear").val()+"&sessionType="+$("#selectedSessionType").val()+"&questionType="+$("#selectedQuestionType").val()+"&ugparam="+$("#ugparam").val()+'&usergroup='+$("#usergroup").val()+'&userrole='+$("#userrole").val());										
			$('#new_record').click(function(){
				$("#selectionDiv1").hide();	
				$("#selectionDiv2").hide();	
				$("#selectionDiv3").hide();						
				newQuestion();
			});
			$('#edit_record').click(function(){
				$("#selectionDiv1").hide();	
				$("#selectionDiv2").hide();	
				$("#selectionDiv3").hide();						
				editQuestion();
			});
			$("#delete_record").click(function() {
				deleteQuestion();
			});					
			$("#submitQuestion").click(function() {			
				var selectedRows = $('#grid').getGridParam('selarrrow');
				if(selectedRows == null || selectedRows.length==0){
					$.prompt("Please select atleast one record to submit");
					return false;
				}
				else {
				var completed = true; //flag for checking whether all selected questions are completed & ready for submission
				var incompleteQuestions = new Array();
				var j=0; //count for incompleted questions
				//check for any incomplete questions
				for(var i=0; i<selectedRows.length; i++){
					var status = $('#grid').getCell(selectedRows[i], 'status.type');					
					if(status != 'questions_complete') {
						completed = false;
						incompleteQuestions[j] = $('#grid').getCell(selectedRows[i], 'number');
						j++;
					};				
				}
				//if(completed == true) {
					$.get('question/'+selectedRows+'/submit', function(data) {
						$.fancybox.open(data);
					});
				//} //else {
					//$.prompt("Question No. " + incompleteQuestions + " are either incomplete or submitted already");
				//};	
				};
			});		 
		});	
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();	
			$("#selectionDiv2").hide();		
			$("#selectionDiv3").hide();							
			$('#key').val(rowid);
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('details_tab', 'question/'+rowid+'/edit?'+$("#gridURLParams").val());
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}
		
		$('.multipleSelect').multiselect({
			dividerLocation : 0.5
		});		
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
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
				<spring:message code="generic.submitquestion" text="submit"/>
			</a> |					
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/info.jsp" %>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	<input type="hidden" id="ugparam" name="ugparam" value="${ugparam}">	
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
</body>
</html>