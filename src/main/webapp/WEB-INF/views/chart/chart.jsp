<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			if($('#currentDeviceType').val()=='questions_starred'){
				loadGroupChangedQuestion();
			}
			/**** On clicking a question on the chart ****/
			$(".deviceNumber").click(function(){
				if($('#currentDeviceType').val()=='questions_starred'){
					var parameters="houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&questionType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()	
					+"&edit=true";
					var resourceURL='question/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
				}else if($('#currentDeviceType').val()=='resolutions_nonofficial'){
					var parameters="houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&deviceType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()	
					+"&edit=true";
					var resourceURL='resolution/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
				}else if($('#currentDeviceType').val()=='motions_standalonemotion_halfhourdiscussion'){
					var parameters="houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+"&deviceType="+$("#selectedQuestionType").val()
					+"&ugparam="+$("#ugparam").val()
					+"&status="+$("#selectedStatus").val()
					+"&role="+$("#srole").val()
					+"&usergroup="+$("#currentusergroup").val()
					+"&usergroupType="+$("#currentusergroupType").val()	
					+"&edit=true";
					var resourceURL='standalonemotion/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
				}
				
			});
			
			$(".deviceNumber").mousedown(function(e) {
			    if (e.which === 3) {
			       console.log($(this).attr('id'));
			       if($('#processMode').val()  == 'upperhouse' && $('#srole').val() == 'QIS_CLERK' && $('#currentDeviceType').val() == 'questions_starred'){
			    	var currentDate = new Date();
			        	currentDate.setHours(0,0,0,0);
			        	var maxChartAnsweringDate = new Date($("#maxChartAnsweringDate").val());
			         if(currentDate > maxChartAnsweringDate){
			        	  $("#chart_answering_date_change").hide();
			          } else { 
			        	  if($(this).attr('name') == "1"){
			        		   $("#chart_answering_date_change").hide();
					       }else{
					    	   $("#chart_answering_date_change").show(); 
					       }  
			         }
			    	  
			       }else{
			    	   $("#chart_answering_date_change").hide();
			       }
			    }
			});
			
			 /**** Right Click Menu ****/
			$(".deviceNumber").contextMenu({
		        menu: 'contextMenuItems'
		    	},
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='clubbing'){
					clubbingInt(id);		
				}else if(action=='referencing'){
					referencingInt(id);
				}else if(action=='chart_answering_date_change'){
					chartAnsweringDateChanged(id);	
				}
		    });	

			 $(".showDetails").mousedown(function(e){
				var idx = $(this).attr('id').substring(8);
				var offset = $(this).offset();
								
				$("#detailShower").html($("#divDetail" + idx).html());
				$("#detailShower").css({'left': offset.left+'px','top':offset.top+'px'}).show();
				return false;
			 });
			 
			 $("#detailShower").click(function(){
				 $("#detailShower").hide();				 
			 });
			 			 
					 
			$(".deviceNumber a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});

			$(".rejectedCount a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});
			
			$(".legends a[title]").qtip({
	    		show: 'mouseover',
	    		hide: 'mouseout'
	    	});
			
			$("#departmentWiseCount").hide();
			
			
	    	$(".scrollable").scrollLeft();		
	    	
	    	$('#newMessageDivViewer').click(function(){
	    		if($("#groupChangedDiv").css('display')=='none'){
					$(this).empty();
					$(this).html("<b>&#9658;</b>");
				}else{
					$(this).empty();
					$(this).html("<b>&#9668;</b>");
				}
				$("#groupChangedDiv").toggle();
	    	});
	    	
	    	$("#departmentDivViewer").click(function(){
	    		$("#departmentWiseCount").toggle();
	    	});
		});	
		
		function loadGroupChangedQuestion(){
			var params="houseType="+$('#selectedHouseType').val()+
				"&sessionType="+$('#selectedSessionType').val()+
				"&sessionYear="+$('#selectedSessionYear').val()+
				"&answeringDate="+$('#selectedAnsweringDate').val()+
				"&deviceType="+$('#selectedQuestionType').val();
			$.get("ref/getGroupChangedQuestion?"+params,function(data){
				if(data.length>0){
					var groupChangeText="";
					var tableHeader = data[0][7].split(",");
					for(var i=0;i<data.length;i++){
						groupChangeText = groupChangeText +
						"<div style='border:2px solid;'>";
						var k=1;
						for(var j=0;j<tableHeader.length;j++){
							groupChangeText = groupChangeText +"<b>"+ tableHeader[j]+"</b> : "+data[i][k] +"<br>";
							k = k+1;
						}
						groupChangeText = groupChangeText +"</div>";
					}
					
					$('#groupChangedDiv').html(groupChangeText);
					//$('#groupChangedDiv').css("display","inline-block");
				}
			});
		}
	</script>
	
	<style type="text/css">
		.showDetails:hover{
			cursor: pointer;
		}
		
		#detailShower:hover{
			cursor: pointer;
		}
		.legends a:hover{
			cursor: none;
		}
		
		.legends a{
			text-decoration: none;
			
		}
		
		#newMessageDivViewer{
			background: #0A469A scroll no-repeat;
			width: 15px;
			height: 15px;
			border: 1px solid black;
			z-index: 5000;
			bottom: 25px;
			right: 5px;			
			position: fixed;
			cursor: pointer;
		}
		
		#departmentDivViewer{
			
			width: 25px;
			height: 25px;
			z-index: 5000;
			bottom: 50px;
			right: 5px;			
			position: fixed;
			cursor: pointer;
		}
		
		#groupChangedDiv{
			background: #D4F4FF scroll no-repeat;
			width: 300px;
			height: 300px;
			border: 1px solid black;
			z-index: 4000;
			bottom: 25px;
			right: 25px;			
			position: fixed;
			cursor: pointer;
			overflow: auto;
		}
	 	@media print{
			#reportDiv{
				padding-right:200px;
				margin:10px;
			}
			.uiTable{
				border: 2px solid #000000;
			}
			.uiTable td
			{
				border-bottom: 1px solid #000000;
				/* color: #669; */
				border-top: 1px solid #000000;
			}
		} 
	</style>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="scrollable" id="reportDiv">
<c:choose>
<c:when test="${report == null}">
	<spring:message code="question.chart.notCreated" text="Chart is not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.chart.noEntries" text="There are no entries in the Chart"/>
</c:when>

<c:otherwise>
	<c:if test="${deviceType!='motions_standalonemotion_halfhourdiscussion'}">
		<label class="small"><spring:message code="question.chart.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
	</c:if>
<table class="uiTable" border="1" style="width:900px;">
	<thead>
		<tr>
		<th><spring:message code="chart.serialNumber" text="Sr No"/></th>
		<th><spring:message code="member.name" text="Member Name"/></th>
		<c:forEach begin="1" end="${maxQns}" var="i">
				<c:if test="${deviceType == 'questions_starred'}">
					<th><spring:message code="chart.question" text="Question ${i}"/></th>
				</c:if>
				<c:if test="${deviceType == 'resolutions_nonofficial' }">
					<th style="min-width: 100px;"><spring:message code="chart.resolution" text="Resolution ${i}"/></th>
				</c:if>
				<c:if test="${deviceType == 'motions_standalonemotion_halfhourdiscussion' }">
					<th><spring:message code="chart.question.HDS" text="HDS ${i}"/></th>
				</c:if>
			</c:forEach>
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='motions_standalonemotion_halfhourdiscussion'}">
		<th><spring:message code="chart.rejectedCount" text="Reject Count"/></th>
		<th><spring:message code="chart.extraCount" text="Extra Count"/></th>
		</c:if>
		</tr>
	</thead>
	<tbody>
		<c:set var="memberName" value=""></c:set>
		<c:set var="counter" value="1"></c:set>
		<c:set var="rejectedNotices" value="" />
		<c:set var="rejectedCount" value="" />
		<c:set var="extraCount" value="" />
		<c:set var="indexNo" value="1"/>
		<c:forEach items="${report}" var="r">
			<c:choose>
				<c:when test="${memberName != r[1]}">
					<c:if test="${memberName!=''}">
						<c:if test="${counter <= maxQns}">
							<c:forEach begin="${counter}" end="${maxQns}" step="1">
								<td align="center">-</td>
							</c:forEach>
						</c:if>
						
						<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'motions_standalonemotion_halfhourdiscussion'}">
							<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${rejectedNotices}" style="text-decoration: none;">${rejectedCount}</a></td>
							<td align="center">${extraCount}</td>
						</c:if>
						</tr>
						<c:set var="counter" value="1" />
					</c:if>
					
					<tr>
						<td>${indexNo} <c:set var="indexNo" value="${indexNo + 1}"/></td>
						<td>${r[1]}</td>
						<td align="center">
							
							<c:choose>		
								<c:when test="${r[3] == null or r[3]==0}">
									-
								</c:when>
								<c:otherwise>
									<c:choose>										
										<c:when test="${r[7] == 'n'}">												
											<a href="#" class="deviceNumber" id="${r[3]}" name="${r[29]}" title="${r[10]}">
												<b>${r[4]}</b>
												<c:if test="${deviceType == 'questions_starred'}">
													<c:if test="${not fn:contains(r[27], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>
												<c:if test="${fn:startsWith(deviceType, 'resolutions_')}">
													<c:if test="${not fn:contains(r[15], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>
												<c:if test="${deviceType=='motions_standalonemotion_halfhourdiscussion'}">
													<c:if test="${not fn:contains(r[15], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>
											</a>
											
											<br>
											<c:if test="${deviceType == 'questions_starred'}">
												<c:choose>
													<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7]!='y'}">
														<spring:message code="question.shortUnstarredAdmission" text="US"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7] == 'y'}">
														<spring:message code="question.shortUnstarredClubbedAdmission" text="US"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] == 'y'}">
														<spring:message code="question.shortStarredClubbedAdmission" text="SA"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] != 'y'}">
														<spring:message code="question.shortStarredAdmission" text="SA"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection'}">
														<spring:message code="question.shortStarredRejection" text="R"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection') and r[7] != 'y'}">
														<spring:message code="question.shortStarredClubbedRejection" text="RC"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromMember' or r[5]=='question_final_clarificationNeededFromMember'}">
														<spring:message code="question.shortStarredClarificationFromMember" text="CM"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromDepartment' or r[5]=='question_final_clarificationNeededFromDepartment'}">
														<spring:message code="question.shortStarredClarificationFromDepartment" text="CD"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromMemberAndDepartment' or r[5]=='question_final_clarificationNeededFromMemberAndDepartment'}">
														<spring:message code="question.shortStarredClarificationFromMemberAndDepartment" text="CMD"/>
													</c:when>
												</c:choose>
												
										</c:if>
											<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
												<c:if test="${deviceType == 'resolutions_nonofficial'}">
														${r[16]} ${r[19]}<br>
														${r[17]} ${r[6]}<br>
														${r[18]} ${r[20]}
													
												</c:if>
												<c:if test="${deviceType == 'questions_starred'}">
													${r[18]} ${r[22]}<br>
													${r[19]} ${r[9]}<br>
													${r[20]} ${r[6]}<br>
													${r[21]} ${r[23] }
												</c:if>
											</div>
										</c:when>
										<c:otherwise>
											<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='motions_standalonemotion_halfhourdiscussion'}">
												<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">
													${r[4]}
													<c:if test="${fn:startsWith(deviceType, 'resolutions_')}">
														<c:if test="${not fn:contains(r[15], 'typist')}">
															<sup style="font-size: 8pt;">*</sup>
														</c:if>
													</c:if>
													<c:if test="${deviceType=='motions_standalonemotion_halfhourdiscussion'}">
														<c:if test="${not fn:contains(r[15], 'typist')}">
															<sup style="font-size: 8pt;">*</sup>
														</c:if>
													</c:if>
												</a>
												<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
													<c:if test="${deviceType == 'resolutions_nonofficial'}">
														${r[16]} ${r[19]}<br>
														${r[17]} ${r[6]}<br>
														${r[18]} ${r[20]}<br>
													</c:if>
												</div>
											</c:if>
											<c:if test="${deviceType == 'questions_starred'}">
												<a href="#" class="deviceNumber" id="${r[3]}" name="${r[29]}" title="${r[9]}">
													${r[4]} 
													<c:if test="${deviceType == 'questions_starred'}">
														<c:if test="${not fn:contains(r[27], 'typist')}">
															<sup style="font-size: 8pt;">*</sup>
														</c:if>
													</c:if>
												</a>
												<br>
												<c:if test="${deviceType == 'questions_starred'}">
													<c:choose>
														<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7]!='y'}">
															<spring:message code="question.shortUnstarredAdmission" text="US"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7] == 'y'}">
															<spring:message code="question.shortUnstarredClubbedAdmission" text="US"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] == 'y'}">
															<spring:message code="question.shortStarredClubbedAdmission" text="SA"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] != 'y'}">
															<spring:message code="question.shortStarredAdmission" text="SA"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection'}">
															<spring:message code="question.shortStarredRejection" text="R"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection') and r[7] != 'y'}">
															<spring:message code="question.shortStarredClubbedRejection" text="RC"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromMember' or r[5]=='question_final_clarificationNeededFromMember'}">
															<spring:message code="question.shortStarredClarificationFromMember" text="CM"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromDepartment' or r[5]=='question_final_clarificationNeededFromDepartment'}">
															<spring:message code="question.shortStarredClarificationFromDepartment" text="CD"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromMemberAndDepartment' or r[5]=='question_final_clarificationNeededFromMemberAndDepartment'}">
															<spring:message code="question.shortStarredClarificationFromMemberAndDepartment" text="CMD"/>
														</c:when>
													</c:choose>
												</c:if>
												<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
													<c:if test="${deviceType == 'questions_starred'}">
														${r[18]} ${r[22]}<br>
														${r[19]} ${r[9]}<br>
														${r[20]} ${r[6]}<br>
														${r[21]} ${r[23] }
													</c:if>
												</div>
											</c:if>
										</c:otherwise>
									</c:choose>
									
									<c:choose>
										<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'question_final_rejection'}">
											<img src="./resources/images/template/icons/black_check.png" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:when>
										<c:when test="${r[5] == 'question_system_putup' or r[5] == 'standalonemotion_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:when>
										<c:when test="${r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${(r[5] == 'question_system_clubbed' or r[5] == 'standalonemotion_system_clubbed') or r[7] == 'y' }">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:when>
										<c:when test="${r[5] == 'question_unstarred_final_admission'}">
											<img src="./resources/images/template/icons/yellow_check.jpg" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:when>
										<c:otherwise>
											<c:if test="${r[5]!='resolution_final_rejection' and r[5]!='0'}">
												<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
												<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
											</c:if>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
							<br>
							<div>
								<span style="font-size: 10px;">${r[22]}</span>
								<c:if test="${deviceType == 'questions_starred'}">
									<c:if test="${r[28]==true}">
										<img width="20px" height="20px" src="./resources/images/VerifyIcon.png" align="right" style="display:inline-block" title="<spring:message code='question.processedByClerk' text='Processed By Clerk'/>">
									</c:if>
								</c:if>
								
							</div>
						</td>
						<c:set var="counter" value="${counter + 1}"/>
						<c:set var="rejectedNotices" value="${r[12]}" />
						<c:set var="rejectedCount" value="${r[13]}" />
						<c:set var="extraCount" value="${r[14]}" />
						
						<%-- <td>
							${counter}:${rejectedNotices}:${rejectedCount}:${extraCount}
						</td> --%>
				</c:when>
				<c:otherwise>
					<td align="center">
						<c:choose>
							<c:when test="${r[3] == null}" >
								-
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[7] == 'n'}">
										<a href="#" class="deviceNumber" id="${r[3]}" name="${r[29]}" title="${r[10]}">
											<b>${r[4]}</b>
											<c:if test="${deviceType == 'questions_starred'}">
												<c:if test="${not fn:contains(r[27], 'typist')}">
													<sup style="font-size: 8pt;">*</sup>
												</c:if>
											</c:if>
											<c:if test="${fn:startsWith(deviceType, 'resolutions_')}">
												<c:if test="${not fn:contains(r[15], 'typist')}">
													<sup style="font-size: 8pt;">*</sup>
												</c:if>
											</c:if>
											<c:if test="${deviceType=='motions_standalonemotion_halfhourdiscussion'}">
												<c:if test="${not fn:contains(r[15], 'typist')}">
													<sup style="font-size: 8pt;">*</sup>
												</c:if>
											</c:if>
										</a>
										<br>
										<c:if test="${deviceType == 'questions_starred'}">
											<div style="font-size: 10px;">
												<c:choose>
													<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7]!='y'}">
														<spring:message code="question.shortUnstarredAdmission" text="US"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7] == 'y'}">
														<spring:message code="question.shortUnstarredClubbedAdmission" text="US"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] == 'y'}">
														<spring:message code="question.shortStarredClubbedAdmission" text="SA"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] != 'y'}">
														<spring:message code="question.shortStarredAdmission" text="SA"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection'}">
														<spring:message code="question.shortStarredRejection" text="R"/>
													</c:when>
													<c:when test="${(r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection') and r[7] != 'y'}">
														<spring:message code="question.shortStarredClubbedRejection" text="RC"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromMember' or r[5]=='question_final_clarificationNeededFromMember'}">
														<spring:message code="question.shortStarredClarificationFromMember" text="CM"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromDepartment' or r[5]=='question_final_clarificationNeededFromDepartment'}">
														<spring:message code="question.shortStarredClarificationFromDepartment" text="CD"/>
													</c:when>
													<c:when test="${r[5]=='question_recommend_clarificationNeededFromMemberAndDepartment' or r[5]=='question_final_clarificationNeededFromMemberAndDepartment'}">
														<spring:message code="question.shortStarredClarificationFromMemberAndDepartment" text="CMD"/>
													</c:when>
												</c:choose>
											</div>
										</c:if>
										<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
											<c:if test="${deviceType == 'resolutions_nonofficial'}">
														${r[16]} ${r[19]}<br>
														${r[17]} ${r[6]}<br>
														${r[18]} ${r[20]}<br>
													
											</c:if>
											<c:if test="${deviceType == 'questions_starred'}">
												${r[18]} ${r[22]}<br>
												${r[19]} ${r[9]}<br>
												${r[20]} ${r[6]}<br>
												${r[21]} ${r[23] }
											</c:if>
										</div>
									</c:when>
									<c:otherwise>
										<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='motions_standalonemotion_halfhourdiscussion'}">
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">
												${r[4]}
												<c:if test="${fn:startsWith(deviceType, 'resolutions_')}">
													<c:if test="${not fn:contains(r[15], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>												
												<c:if test="${deviceType=='motions_standalonemotion_halfhourdiscussion'}">
													<c:if test="${not fn:contains(r[15], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>
											</a>	
											<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
												<c:if test="${deviceType == 'resolutions_nonofficial'}">
														${r[16]} ${r[19]}<br>
														${r[17]} ${r[6]}<br>
														${r[18]} ${r[20]}<br>
													
												</c:if>
											</div>											
										</c:if>
										<c:if test="${deviceType == 'questions_starred'}">
											<a href="#" class="deviceNumber" id="${r[3]}" name="${r[29]}" title="${r[9]}">
												${r[4]}
												<c:if test="${deviceType == 'questions_starred'}">
													<c:if test="${not fn:contains(r[27], 'typist')}">
														<sup style="font-size: 8pt;">*</sup>
													</c:if>
												</c:if>
											</a>
											<br>
											<c:if test="${deviceType == 'questions_starred'}">
												<div style="font-size: 10px;">
													<c:choose>
														<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7]!='y'}">
															<spring:message code="question.shortUnstarredAdmission" text="US"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_convertToUnstarredAndAdmit' or r[5]=='question_unstarred_final_admission') and r[7] == 'y'}">
															<spring:message code="question.shortUnstarredClubbedAdmission" text="US"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] == 'y'}">
															<spring:message code="question.shortStarredClubbedAdmission" text="SA"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_admission' or r[5]=='question_final_admission') and r[7] != 'y'}">
															<spring:message code="question.shortStarredAdmission" text="SA"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection'}">
															<spring:message code="question.shortStarredRejection" text="R"/>
														</c:when>
														<c:when test="${(r[5]=='question_recommend_rejection' or  r[5]=='question_final_rejection') and r[7] != 'y'}">
															<spring:message code="question.shortStarredClubbedRejection" text="RC"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromMember' or r[5]=='question_final_clarificationNeededFromMember'}">
															<spring:message code="question.shortStarredClarificationFromMember" text="CM"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromDepartment' or r[5]=='question_final_clarificationNeededFromDepartment'}">
															<spring:message code="question.shortStarredClarificationFromDepartment" text="CD"/>
														</c:when>
														<c:when test="${r[5]=='question_recommend_clarificationNeededFromMemberAndDepartment' or r[5]=='question_final_clarificationNeededFromMemberAndDepartment'}">
															<spring:message code="question.shortStarredClarificationFromMemberAndDepartment" text="CMD"/>
														</c:when>
													</c:choose>
												</div>
											</c:if>
											<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
												<c:if test="${deviceType == 'questions_starred'}">
													${r[18]} ${r[22]}<br>
													${r[19]} ${r[9]}<br>
													${r[20]} ${r[6]}<br>
													${r[21]} ${r[23] }
												</c:if>
											</div>											
										</c:if>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'question_final_rejection'}">
										<img src="./resources/images/template/icons/black_check.png" class="toolTip clearfix" width="2" height="10">
										<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
									</c:when>
									<c:when test="${r[5] == 'question_system_putup' or r[5] == 'standalonemotion_system_putup'}">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
									</c:when>
									<c:when test="${r[5] == 'resolution_system_putup' }">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${(r[5] == 'question_system_clubbed' or r[5] == 'standalonemotion_system_clubbed') or r[7] == 'y' }">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
										<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
									</c:when>
									<c:when test="${r[5] == 'question_unstarred_final_admission'}">
										<img src="./resources/images/template/icons/yellow_check.jpg" class="toolTip clearfix" width="2" height="10">
										<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
									</c:when>
									<c:otherwise>
										<c:if test="${r[5]!='resolution_final_rejection' and r[5]!='0'}">
											<img src="./resources/images/template/icons/green_check.jpg" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:if>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						<br>
						<div>
							<span style="font-size: 10px;">${r[22]}</span>
							<c:if test="${deviceType == 'questions_starred'}">
								<c:if test="${r[28]==true}">
									<img width="20px" height="20px" src="./resources/images/VerifyIcon.png" align="right" style="display:inline-block" title="<spring:message code='question.processedByClerk' text='Processed By Clerk'/>">
								</c:if>
							</c:if>
						</div>
					</td>
					<c:set var="counter" value="${counter + 1}"/>
					
					<c:set var="rejectedNotices" value="${r[12]}" />
					<c:set var="rejectedCount" value="${r[13]}" />
					<c:set var="extraCount" value="${r[14]}" />
					<%-- <td>
						${counter}:${rejectedNotices}:${rejectedCount}:${extraCount}
					</td> --%>
				</c:otherwise>
			</c:choose>
			<c:set var="memberName" value="${r[1]}" />
			
		</c:forEach>
				
		<c:if test="${counter < maxQns}">
			<c:forEach begin="${counter}" end="${maxQns}" step="1">
				<td align="center">-</td>
			</c:forEach>
		</c:if>
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'motions_standalonemotion_halfhourdiscussion'}">
			<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${rejectedNotices}" style="text-decoration: none;">${rejectedCount}</a></td>
			<td align="center">${extraCount}</td>
		</c:if>
		
		</tr>
	</tbody>
</table>
</c:otherwise>
</c:choose>
</div>
<c:if test="${deviceType=='questions_starred'}">
	<div id="departmentDivViewer" title="View Departmentwise Count">
			<b><img src="./resources/images/information.png"  width="25" height="25"></b>
	</div>
	<div id="newMessageDivViewer">
			<b>&#9668;</b>
	</div>
	<div id="groupChangedDiv" style="display:none"></div>
	<div style="position: fixed; z-index: 999; background: scroll; right: 45px; bottom: 30px;">
		<div style="color: #FFF; border: 1px solid black; background: #F00; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.putUpCount' text='Put Up Count'/>">${putupCount}</a></div>
		<div style="color: #000; border: 1px solid black; background: #0F0; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.processedCount' text='Processed Count'/>">${processedCount}</a></div>
		<div style="color: #FFF; border: 1px solid black; background: #00F; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.clubbedCount' text='Clubbed Count'/>">${clubbedCount}</a></div>
		<div style="color: #000; border: 1px solid black; background: #CCE57F; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.admitCount' text='Admitted Count'/>">${admitCount}</a></div>
		<div style="color: #000; border: 1px solid black; background: #rgb(175,175,175); width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.rejectCount' text='Rejected Count'/>">${rejectCount}</a></div>
		<div style="color: #000; border: 1px solid black; background: #FF9980; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.UnstarredCount' text='Unstarred Count'/>" >${unstarredCount}</a></div>
	</div>
	<div id="departmentWiseCount" style="position: fixed; z-index: 999; background: scroll; right: 25px; bottom: 70px;width: 300px;overflow: auto; height:480px;">
		<c:forEach items="${departmentwiseCounts}" var="i">
			<div style="color: black; border: 1px solid black; background:#80dfff; width: 200px; height: 30px; padding: 2px; font-weight: bold; vertical-align: middle; display: inline-block;font-size:12px;">
				${i.name} 
			</div>
			<div style="color: black; border: 1px solid black; background:#80dfff; width: 50px; height: 30px; padding: 2px; font-weight: bold; vertical-align: middle; display: inline-block;text-align: center;font-size:12px;">
				${i.formattedNumber}
			</div>
			<div style="height:1px;"></div>
		</c:forEach>
	</div>
</c:if>
<div id="detailShower" style="font-weight: bold; font-size: 12px; padding: 4px; background: #DAEDFF; display: none; z-index: 1000; border: 2px solid #004C00; position: absolute; width: 180px;">v</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>