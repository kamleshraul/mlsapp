<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title></title>
		<script type="text/javascript">
			$(document).ready(function() {
				$('#linkForReport').css('font-size','20px');
				
				var startingSessionValues = [];
				var endingSessionValues = [];
				
				$('#startingSession option').each(function(){			
					startingSessionValues.push($(this).text());
				});
				
				$('#endingSession option').each(function(){			
					endingSessionValues.push($(this).text());
				});
				
				$("#startingSessionOption").autocomplete({						
					source: startingSessionValues,
					select:function(event,ui){	
						$('#startingSession').val("");
						$('#startingSession option').each(function(){				
							if($(this).text()==ui.item.value) {							
								$(this).attr('selected', 'selected');								
							}
						});			
					}	
				});
				
				$("#startingSessionOption").change(function(){
					if($(this).val()=="") {
						$('#startingSession').val("");
					}
				});
				
				$("#endingSessionOption").autocomplete({						
					source: endingSessionValues,
					select:function(event,ui){	
						$('#endingSession').val("");
						$('#endingSession option').each(function(){				
							if($(this).text()==ui.item.value) {							
								$(this).attr('selected', 'selected');								
							}
						});			
					}	
				});
				
				$("#endingSessionOption").change(function(){
					if($(this).val()=="") {
						$('#endingSession').val("");
					}
				});
				
				$('#linkForReport').click(function() {
					$('#session').val("");
					var startingSessionOrder = $('#session option[value='+$('#startingSession').val()+']').text();
					var endingSessionOrder = $('#session option[value='+$('#endingSession').val()+']').text();
					if(startingSessionOrder=="" && endingSessionOrder=="") {
						$('#session option').each(function(){
							$(this).attr('selected', 'selected');
						});
					} else if(startingSessionOrder=="" && endingSessionOrder!="") {
						$('#session option').each(function(){
							if($(this).text()<=endingSessionOrder) {
								$(this).attr('selected', 'selected');
							}
						});
					} else if(startingSessionOrder!="" && endingSessionOrder=="") {
						$('#session option').each(function(){							
							if($(this).text()>=startingSessionOrder) {
								$(this).attr('selected', 'selected');
							}
						});
					} else {
						if(startingSessionOrder>endingSessionOrder) {
							alert("selected starting session can not be after selected ending session");
							return false;
						}			
						$('#session option').each(function(){
							if($(this).text()>=startingSessionOrder && $(this).text()<=endingSessionOrder) {
								$(this).attr('selected', 'selected');
							}
						});
					}					
					if($('#session').val()!=undefined && $('#session').val()!="") {
						$('#linkForReport').attr('href', 'question/report/unstarredacrosssessiondepartmentwise/export?sessionIds='+$('#session').val()+'&outputFormat=WORD');
					} else {
						alert("Please select atleast one session");
						return false;
					}					
				});
			});
		</script>
	</head>
	<body>
		<p id="overlay_error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<p style="color: #FF0000;"><spring:message code="${error}" text="Error Occured Contact For Support."/></p>
		</c:if>
		<div class="fields clearfix watermark">
			<h3><spring:message code='question.unstarredacrosssessiondepartmentwise.header' text='Provide Sessions For The Report'/>:</h3>
			<p>
				<label class="small"><spring:message code='question.unstarredacrosssessiondepartmentwise.startingsession' text='Starting Session'/></label>
				<input type="text" class="sText" id="startingSessionOption" style="width: 200px;"/>
				<select id="startingSession" name="startingSession" style="display: none;">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
					<c:forEach items="${sessionVOs}" var="i">
						<option value="${i.id }"><c:out value="${i.description}"></c:out></option>
					</c:forEach>
				</select>
			</p>
			<p style="margin-top: 10px;">
				<label class="small"><spring:message code='question.unstarredacrosssessiondepartmentwise.endingsession' text='Ending Session'/></label>
				<input type="text" class="sText" id="endingSessionOption" style="width: 200px;"/>
				<select id="endingSession" name="endingSession" style="display: none;">
					<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
					<c:forEach items="${sessionVOs}" var="i">
						<option value="${i.id }"><c:out value="${i.description}"></c:out></option>
					</c:forEach>
				</select>
			</p>
			<select id="session" name="session" multiple="multiple" style="display: none;">
				<c:forEach items="${sessionVOs}" var="i">
					<option value="${i.id }">${i.order}</option>
				</c:forEach>
			</select>
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<a href="#" id="linkForReport"><spring:message code='question.unstarredacrosssessiondepartmentwise.generateReport' text='Generate Report'/></a>
				</p>
			</div>
		</div>
	</body>
</html>