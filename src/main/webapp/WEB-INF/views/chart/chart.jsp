<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {
			loadGroupChangedQuestion();
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
				}else if($('#currentDeviceType').val()=='questions_halfhourdiscussion_standalone'){
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
					var resourceURL='question/'+$(this).attr("id")+'/edit?'+parameters;
					showTabByIdAndUrl('details_tab', resourceURL);
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
	</style>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="scrollable">
<c:choose>
<c:when test="${report == null}">
	<spring:message code="question.chart.notCreated" text="Chart is not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.chart.noEntries" text="There are no entries in the Chart"/>
</c:when>

<c:otherwise>
	<c:if test="${deviceType!='questions_halfhourdiscussion_standalone'}">
		<label class="small"><spring:message code="question.chart.answeringDate" text="Answering Date"/>: ${answeringDate}</label>
	</c:if>
<table class="uiTable" border="1">
	<thead>
		<tr>
		<th><spring:message code="member.name" text="Member Name"/></th>
		<c:forEach begin="1" end="${maxQns}" var="i">
				<c:if test="${deviceType == 'questions_starred'}">
					<th><spring:message code="chart.question" text="Question ${i}"/></th>
				</c:if>
				<c:if test="${deviceType == 'resolutions_nonofficial' }">
					<th style="min-width: 100px;"><spring:message code="chart.resolution" text="Resolution ${i}"/></th>
				</c:if>
				<c:if test="${deviceType == 'questions_halfhourdiscussion_standalone' }">
					<th><spring:message code="chart.question.HDS" text="HDS ${i}"/></th>
				</c:if>
			</c:forEach>
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
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
		<c:forEach items="${report}" var="r">
			<c:choose>
				<c:when test="${memberName != r[1]}">
					<c:if test="${memberName!=''}">
						<c:if test="${counter <= maxQns}">
							<c:forEach begin="${counter}" end="${maxQns}" step="1">
								<td align="center">-</td>
							</c:forEach>
						</c:if>
						
						<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'questions_halfhourdiscussion_standalone'}">
							<td align="center"><a href="#" class="rejectedCount" id="rejectedCount" title="${rejectedNotices}" style="text-decoration: none;">${rejectedCount}</a></td>
							<td align="center">${extraCount}</td>
						</c:if>
						</tr>
						<c:set var="counter" value="1" />
					</c:if>
					
					<tr>
						<td>${r[1]}</td>
						<td align="center">
							<c:choose>		
								<c:when test="${r[3] == null or r[3]==0}">
									-
								</c:when>
								<c:otherwise>
									<c:choose>										
										<c:when test="${r[7] == 'n'}">												
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[10]}"><b>${r[4]}</b></a>
											<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
												${r[18]} ${r[22]}<br>
												${r[19]} ${r[9]}<br>
												${r[20]} ${r[6]}<br>
												${r[21]} ${r[23] }
											</div>
										</c:when>
										<c:otherwise>
											<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
												<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">${r[4]}</a>
											</c:if>
											<c:if test="${deviceType == 'questions_starred'}">
												<a href="#" class="deviceNumber" id="${r[3]}" title="${r[9]}">${r[4]}</a>
												<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
													${r[18]} ${r[22]}<br>
													${r[19]} ${r[9]}<br>
													${r[20]} ${r[6]}<br>
													${r[21]} ${r[23] }
												</div>
											</c:if>
										</c:otherwise>
									</c:choose>
									
									<c:choose>
										<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'question_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
											<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
										</c:when>
										<c:when test="${r[5] == 'resolution_system_putup'}">
											<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										</c:when>
										<c:when test="${r[5] == 'question_system_clubbed' or r[7] == 'y'}">
											<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
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
										<a href="#" class="deviceNumber" id="${r[3]}" title="${r[10]}"><b>${r[4]}</b></a>
										<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
											${r[18]} ${r[22]}<br>
											${r[19]} ${r[9]}<br>
											${r[20]} ${r[6]}<br>
											${r[21]} ${r[23] }
										</div>
									</c:when>
									<c:otherwise>
										<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType=='questions_halfhourdiscussion_standalone'}">
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[6]}">${r[4]}</a>
										</c:if>
										<c:if test="${deviceType == 'questions_starred'}">
											<a href="#" class="deviceNumber" id="${r[3]}" title="${r[9]}">${r[4]}</a>
											<div style="font-weight: bold; font-size: 12px; display: none;" class="divDetail" id="divDetail${r[3]}">
												${r[18]} ${r[22]}<br>
												${r[19]} ${r[9]}<br>
												${r[20]} ${r[6]}<br>
												${r[21]} ${r[23] }
											</div>											
										</c:if>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${r[11] == 'y' && r[5] == 'resolution_system_putup'}">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'question_system_putup'}">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
										<div style="background: #004C00; border-radius: 5px;  height: 5px;  min-height: 6px;  min-width: 20px; width: 5px;" class="showDetails" id="stripDiv${r[3]}"></div>
									</c:when>
									<c:when test="${r[5] == 'resolution_system_putup' }">
										<img src="./resources/images/template/icons/red_check.jpg" class="toolTip clearfix" width="2" height="10">
									</c:when>
									<c:when test="${r[5] == 'question_system_clubbed' or r[7] == 'y' }">
										<img src="./resources/images/template/icons/blue_check.jpg" class="toolTip clearfix" width="2" height="10">
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
		<c:if test="${deviceType == 'resolutions_nonofficial' or deviceType == 'questions_halfhourdiscussion_standalone'}">
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
	<div id="newMessageDivViewer">
			<b>&#9668;</b>
	</div>
	<div id="groupChangedDiv" style="display:none"></div>
	<div style="position: fixed; z-index: 999; background: scroll; right: 45px; bottom: 50px;">
		<div style="color: #FFF; border: 1px solid black; background: #F00; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.putUpCount' text='Put Up Count'/>">${report[2][17]}</a></div>
		<div style="color: #000; border: 1px solid black; background: #0F0; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.processedCount' text='Processed Count'/>">${report[2][15]}</a></div>
		<div style="color: #FFF; border: 1px solid black; background: #00F; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.clubbedCount' text='Clubbed Count'/>">${report[2][16]}</a></div>
		<div style="color: #000; border: 1px solid black; background: #CCE57F; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.admitCount' text='Admitted Count'/>">${report[2][24]}</a></div>
		<div style="color: #000; border: 1px solid black; background: #rgb(175,175,175); width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.rejectCount' text='Rejected Count'/>">${report[2][25]}</a></div>
		<div style="color: #000; border: 1px solid black; background: #FF9980; width: 25px; height: 17px; padding: 2px; text-align: center; font-weight: bold; vertical-align: middle; display: inline-block;" class="legends"><a href="javascript.void(0)" title="<spring:message code='question.chart.UnstarredCount' text='Unstarred Count'/>" >${report[2][26]}</a></div>
	</div>
	
</c:if>
<div id="detailShower" style="font-weight: bold; font-size: 12px; padding: 4px; background: #DAEDFF; display: none; z-index: 1000; border: 2px solid #004C00; position: absolute; width: 180px;">v</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>