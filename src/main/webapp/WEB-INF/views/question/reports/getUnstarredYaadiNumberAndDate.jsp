<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				if($('#yaadiLayingDate').val()!='-') {
					$("#yaadiLayingDate option[value='-']").hide();		
					if($('#isYaadiLayingDateSet').val()=='yes') {
						$('#yaadiLayingDate').css('display', 'none');
						$('#existingYaadiLayingDate').css('display', 'inline');
						$('#existingYaadiLayingDate').val($('#yaadiLayingDate').val());
						$('#changeYaadiNumber').css('display', 'inline');
						$('#changeYaadiLayingDate').css('display', 'inline');
					}
				}
				
				$('#linkForReport').css('font-size','20px');
				
				$('#yaadiNumber').change(function() {
					$.get('ref/findYaadiLayingDateForYaadi?sessionId='+$('#sessionId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val(), function(data) {
						
						var yaadiLayingDateForYaadiNumber = data.name.toString();						
						var yaadiLayingDate = $('#yaadiLayingDate').val();						
						$('#yaadiLayingDate option').each(function() {
							if(this.value==yaadiLayingDateForYaadiNumber) {
								$(this).attr('selected', 'selected');
								if($('#changedYaadiLayingDatePara').css('display')!='none') {
									$('#imageLink_yaadiLayingDate').attr('title', $('#iconLabelOnUndo').val());
									$("#changedYaadiLayingDate option[value='"+yaadiLayingDate+"']").show();
									$("#changedYaadiLayingDate option[value='-']").attr('selected', 'selected');
									$('#changedYaadiLayingDatePara').hide();
								}								
							}
						});
						if(yaadiLayingDateForYaadiNumber!=undefined && yaadiLayingDateForYaadiNumber!='-'
								&& yaadiLayingDateForYaadiNumber!='') {
							$('#yaadiLayingDate').css('display', 'none');
							$('#existingYaadiLayingDate').css('display', 'inline');
							$('#existingYaadiLayingDate').val(yaadiLayingDateForYaadiNumber);
							$('#changeYaadiNumber').css('display', 'inline');
							$('#changeYaadiLayingDate').css('display', 'inline');
						} else {	
							$('#yaadiLayingDate').css('display', 'inline');
							$("#yaadiLayingDate option[value='-']").show();
							$("#yaadiLayingDate option[value='-']").attr('selected', 'selected');
							$('#existingYaadiLayingDate').css('display', 'none');
							$('#existingYaadiLayingDate').val("");
							$('#changeYaadiNumber').css('display', 'none');
							$('#changeYaadiLayingDate').css('display', 'none');
						}				
					}).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#overlay_error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#overlay_error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}						
					});
				});
				
				$('#changeYaadiNumber').click(function() {		
					var yaadiNumber = $('#yaadiNumber').val();
					if($('#changedYaadiNumberPara').css('display')=='none') {						
						if(yaadiNumber=="") {
							alert("Please select existing yaadi number for the yaadi first");
							return false;
						}
						$('#imageLink_yaadiNumber').attr('title', $('#iconLabelOnClick').val());
						$('#yaadiNumber').attr('readonly', 'readonly');
						$('#changedYaadiNumberPara').show();				
					} else {
						$('#imageLink_yaadiNumber').attr('title', $('#iconLabelOnUndo').val());
						if($('#changedYaadiLayingDatePara').css('display')=='none') {
							$('#yaadiNumber').removeAttr('readonly');
						}						
						$("#changedYaadiNumber").attr('value','');
						$('#changedYaadiNumberPara').hide();
					}
				});
				
				$('#changeYaadiLayingDate').click(function() {		
					var yaadiLayingDate = $('#yaadiLayingDate').val();
					if($('#changedYaadiLayingDatePara').css('display')=='none') {						
						if(yaadiLayingDate=="-") {
							alert("Please select existing yaadi laying date for the yaadi first");
							return false;
						}
						$('#yaadiNumber').attr('readonly', 'readonly');
						$('#imageLink_yaadiLayingDate').attr('title', $('#iconLabelOnClick').val());
						$("#changedYaadiLayingDate option[value='"+yaadiLayingDate+"']").hide();
						$('#yaadiLayingDate').css('display', 'none');
						$('#existingYaadiLayingDate').css('display', 'inline');
						$('#existingYaadiLayingDate').val(yaadiLayingDate);
						$('#changedYaadiLayingDatePara').show();						
					} else {
						if($('#changedYaadiNumberPara').css('display')=='none') {
							$('#yaadiNumber').removeAttr('readonly');
						}						
						$('#imageLink_yaadiLayingDate').attr('title', $('#iconLabelOnUndo').val());
						$("#changedYaadiLayingDate option[value='"+yaadiLayingDate+"']").show();
						$("#changedYaadiLayingDate option[value='-']").attr('selected', 'selected');
						$('#changedYaadiLayingDatePara').hide();
					}
				});
				
				$('#linkForReport').click(function() {
					if($('#yaadiNumber').val()=="") {
						alert("Please select yaadi number");
						return false;
					} else if($('#yaadiLayingDate').val()=="") {
						alert("Please select yaadi laying date");
						return false;
					}
					$('#linkForReport').attr('href', 'question/report/generateUnstarredYaadiReport?sessionId='+$('#sessionId').val()
							+'&yaadiNumber='+$('#yaadiNumber').val()
							+'&yaadiLayingDate='+$('#yaadiLayingDate').val()
							+'&changedYaadiLayingDate='+$('#changedYaadiLayingDate').val()
							+'&changedYaadiNumber='+$('#changedYaadiNumber').val()
							+'&outputFormat=WORD');
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
		<h3><spring:message code='question.generateUnstarredYaadiReport.getUnstarredYaadiNumberAndDate' text='Select Yaadi Number & Laying Date'/>:</h3>
		<p style="margin-top: 20px;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiNumber' text='Yaadi Number'/></label>
			<input id="yaadiNumber" class="sInteger" name="yaadiNumber" value="${yaadiNumber}"/>
			<c:set var="iconLabel">
				<spring:message code='question.unstarred_yaadi_report.changeField' text='Change'/>
			</c:set>
			<a href="#" id="changeYaadiNumber" style="margin-left: 10px;text-decoration: none;display: none;">				
				<img src="./resources/images/Revise.jpg" title="${iconLabel}" id="imageLink_yaadiNumber" width="20px" height="20px"/>
			</a>
		</p>
		<p id="changedYaadiNumberPara" style="margin-top: 10px;display: none;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.newYaadiNumber' text='New Yaadi Number'/></label>
			<input id="changedYaadiNumber" class="sInteger" name="changedYaadiNumber"/>			
		</p>
		<p style="margin-top: 10px;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.yaadiLayingDate' text='Yaadi Laying Date'/></label>
			<select id="yaadiLayingDate" name="yaadiLayingDate" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingDates}" var="i">					
					<c:choose>
						<c:when test="${i==yaadiLayingDate}">
							<option value="${i}" selected="selected">${i}</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">${i}</option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</select>	
			<input id="existingYaadiLayingDate" class="sText datemask" style="display:none;" readonly="readonly"/>
			<c:set var="iconLabel">
				<spring:message code='question.unstarred_yaadi_report.changeYaadiLayingDate' text='Change'/>
			</c:set>
			<a href="#" id="changeYaadiLayingDate" style="margin-left: 10px;text-decoration: none;display: none;">				
				<img src="./resources/images/Revise.jpg" title="${iconLabel}" id="imageLink_yaadiLayingDate" width="20px" height="20px"/>
			</a>					
		</p>
		<p id="changedYaadiLayingDatePara" style="margin-top: 10px;display: none;">
			<label class="small"><spring:message code='question.unstarred_yaadi_report.newYaadiLayingDate' text='New Yaadi Laying Date'/></label>
			<select id="changedYaadiLayingDate" name="changedYaadiLayingDate" class="sSelect">
				<option value="-"><spring:message code="please.select" text="Please Select"/></option>
				<c:forEach items="${yaadiLayingDates}" var="i">					
					<c:choose>
						<c:when test="${i==currentDate}">
							<option value="${i}" selected="selected">${i}</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">${i}</option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>
			</select>					
		</p>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<a href="#" id="linkForReport"><spring:message code='question.unstarred_yaadi_report.generateReport' text='Generate Report'/></a>
			</p>
		</div>		
		</div>
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="sessionId" name="sessionId" value="${sessionId}"/>		
		<input type="hidden" id="iconLabelOnClick" value="<spring:message code='question.unstarred_yaadi_report.undoChangeField' text='Undo'/>">
		<input type="hidden" id="iconLabelOnUndo" value="<spring:message code='question.unstarred_yaadi_report.changeField' text='Change'/>">		
		<input type="hidden" id=isYaadiLayingDateSet value="${isYaadiLayingDateSet}"/>	
	</body>
</html>