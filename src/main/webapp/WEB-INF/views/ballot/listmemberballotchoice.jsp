<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		var lastAutoFilledValue=0;
		/**** On page load ****/
		$(".question").each(function(){
			var value=$(this).val();
			var id=$(this).attr("id").split("question")[1];
			if(value!='-'){
				$(".question option[value='"+value+"']").hide();
				$("#question"+id+" option[value='"+value+"']").show();
			}
		});
		$(".question").change(function(){
		    var id=$(this).attr("id").split("question")[1];
			var parameters="question="+$(this).val();
			var resource='ref/answeringDates';
			var options="<option value='-'>"+$("#pleaseSelect").val()+"</option>";			
			if($(this).val()!='-'){
			    	$.get(resource+'?'+parameters,function(data){
						if(data.length>0){
							for(var i=0;i<data.length;i++){
								options=options+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
							}
							$("#answeringDate"+id).empty();
							$("#answeringDate"+id).html(options);
						}else{
							$("#answeringDate"+id).empty();	
							$("#answeringDate"+id).html(options);								
						}
					}).fail(function(){
						$.unblockUI();
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.");
						}
						scrollTop();
					});	
					var value=$(this).val();					
					//remove selected option from all other question div except this.
					$(".question option[value='"+value+"']").hide();
					$("#question"+id+" option[value='"+value+"']").show();
					var previouslySelected=$("select[id!='question"+id+"'][value='"+value+"']").attr("id");					
					if(previouslySelected!=undefined){
						$("#"+previouslySelected+" option").show();		
						$("#"+previouslySelected).val("-");								
					}
			}else{
				$("#answeringDate"+id).empty();	
				$("#answeringDate"+id).html(options);	
			}
			$("#errorDiv").hide();
			$("#successDiv").hide();	
	    });		    
	    $(".all").click(function(){
		    var id=$(this).attr("id").split("all")[1];
		    $("#question"+id+" option").show();
		    $("#question"+id).val("-");
	    });	    
	    /**** making fonts bolder ****/
	    $(".question").css("font-weight","bolder");
	    $(".answeringDate").css("font-weight","bolder");
	    /**** Auto filling starts at ****/
	    $(".autofillstartsat").click(function(){
		    var initialAutoFillValue=$("#autofillingstartsat").val();
		    var id=$(this).attr("id").split("autofillstartsat")[1];
		   	var sibling=$(this).parent().parent().nextAll().children();		    
		    if(initialAutoFillValue==0||lastAutoFilledValue!=id){
			    $("#question"+id).attr("disabled","disabled");		    
				sibling.children(".question").attr("disabled","disabled");
				sibling.children(".answeringDate").attr("disabled","disabled");
				$("#answeringDate"+id).attr("disabled","disabled");
				sibling.children(".all").hide();
				$("#all"+id).hide();
				sibling.children(".autofillstartsat").hide();		   	
				$("#autofillingstartsat").val(id);
				lastAutoFilledValue=id;
		    }else if(lastAutoFilledValue==id){
			    $("#question"+id).removeAttr("disabled");			    
		    	sibling.children(".question").removeAttr("disabled");
				sibling.children(".answeringDate").removeAttr("disabled");
				$("#answeringDate"+id).removeAttr("disabled");				
				sibling.children(".all").show();
				$("#all"+id).show();
				sibling.children(".autofillstartsat").show();		   	
				$("#autofillingstartsat").val(0);
		    }
	    });	  
	    /**** Reset ****/
	    $(".reset").click(function(){
	    	var id=$(this).attr("id");
	    	var round=id.split("reset")[1];
	    	var questionsInLastRoundByDefault=parseInt($("#questionsInLastRoundByDefault").val());
	    	var noOfMemberBallotChoicesExceptLast=parseInt($("#noOfMemberBallotChoicesExceptLast").val());
	    	var totalRounds=parseInt($("#totalRounds").val());
	    	var limit=noOfMemberBallotChoicesExceptLast+questionsInLastRoundByDefault;
	    	var count=0;
	    	if(round>=1){
	    		$.prompt($("#resetParticularRound").val()+round+" ?",{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$(".round"+round).each(function(){
			        		$(this).val("-");	
			        		if(parseInt(round) < totalRounds){
			        			$(this).removeAttr("disabled");
			        		}else if(parseInt(round)==totalRounds){
			        			var currentId=$(this).attr("id");	
			        			if(currentId!=undefined && currentId.indexOf("question")!=-1){
			        				idCount=currentId.split("question")[1];
			        				if(parseInt(idCount)<= limit){
			        					$(this).removeAttr("disabled");
			        				}else{
			        					$(this).attr("disabled","disabled");
			        				}
			        			}
			        			if(currentId!=undefined && currentId.indexOf("answeringDate")!=-1){
			        				idCount=currentId.split("answeringDate")[1];
			        				if(parseInt(idCount)<= limit){
			        					$(this).removeAttr("disabled");
			        				}else{
			        					$(this).attr("disabled","disabled");
			        				}
			        			}
			        		}			        		     		
			        	});				        			    		
			        }
				}});  
	    	}else{
	    		$.prompt($("#resetAll").val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	for(var round=1;round <=totalRounds;round++){
			        		$(".round"+round).each(function(){
				        		$(this).val("-");	
				        		if(parseInt(round) < totalRounds){
				        			$(this).removeAttr("disabled");
				        			var currentId=$(this).attr("id");	
				        			if(currentId!=undefined && currentId.indexOf("question")!=-1){
				        				$("#"+currentId+" option").show();
				        			}
				        			if(currentId!=undefined && currentId.indexOf("answeringDate")!=-1){
				        				$("#"+currentId+" option").show();
				        				$("#"+currentId+" option[value!='-']").remove();
				        			}
				        		}else if(parseInt(round)==totalRounds){
				        			var currentId=$(this).attr("id");	
				        			if(currentId!=undefined && currentId.indexOf("question")!=-1){
				        				idCount=currentId.split("question")[1];
				        				if(parseInt(idCount)<= limit){
				        					$(this).removeAttr("disabled");
				        				}else{
				        					$(this).attr("disabled","disabled");
				        				}
				        				$("#"+currentId+" option").show();
				        			}
				        			if(currentId!=undefined && currentId.indexOf("answeringDate")!=-1){
				        				idCount=currentId.split("answeringDate")[1];
				        				if(parseInt(idCount)<= limit){
				        					$(this).removeAttr("disabled");
				        				}else{
				        					$(this).attr("disabled","disabled");
				        				}
				        				$("#"+currentId+" option").show();
				        				$("#"+currentId+" option[value!='-']").remove();
				        			}
				        		}			        		     		
				        	});	
			        	}
			        }
				}});    		
	    	}
	    });
	    /**** Enable/Disable ****/
	    $(".enable").click(function(){
	    	var id=$(this).attr("id");
	    	var count=id.split("enable")[1];	    	
	    	if($("#question"+count).attr("disabled")=="disabled"){
	    		$("#question"+count).removeAttr("disabled");
	    	}else{
	    		$("#question"+count).attr("disabled","disabled");
	    	}
	    	if($("#answeringDate"+count).attr("disabled")=="disabled"){
	    		$("#answeringDate"+count).removeAttr("disabled");
	    	}else{
	    		$("#answeringDate"+count).attr("disabled","disabled");
	    	}
	    });
	});
</script>
<style type="text/css">
.round1{
	color:green ;
	}
	.round2{
	color:blue ;
	}
	.round3{
	color: red;
	}
	.round4{
	color: black;
	}
	.round5{
	color:  #8B6914	;
	}
	th,td{
	font-size: 14px;
	}
	#memberChoicesDiv{
	width:700px;
	}
	.autofillstartsat:HOVER{
		color:blue;
		font-size: 16px;
	}
	.autofillstartsat{
		margin-left: 20px;
	}
	.all:HOVER{
		color:blue;
		font-size: 16px;
	}
	.all{
		margin-left: 20px;
	}
	
	
</style>
</head>
<body>	
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:if test="${type=='SUCCESS' }">
<div class="toolTip tpGreen clearfix" id="successDiv">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_success" text="Data saved successfully." /></p>
<p></p>
</div>
</c:if>
<c:if test="${type=='FAILED' }">
<div class="toolTip tpRed clearfix" id="failedDiv">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_failure" text="Data couldnot be saved.Please try after sometime" /></p>
<p></p>
</div>
</c:if>
<div id="memberChoicesDiv">
	<table class="uiTable" style="width:100%">					
		<tr>						
			<th><spring:message code="memberballotchoice.sno" text="S.No"></spring:message></th>
			<th><spring:message code="memberballotchoice.Question"
				text="Question"></spring:message></th>
			<th><spring:message code="memberballotchoice.answeringdate"
				text="Answering Date"></spring:message><a id="reset" class="reset" style="font-weight: bolder;cursor: pointer;" title="Clear All">(-)</a></th>
		</tr>
		<c:set value="1" var="count"></c:set>
		<c:set value="0" var="noOfQuestionsInLastRound"></c:set>
		<c:forEach var="i" begin="1" end="${totalRounds}">			
			<tr>
				<td colspan="3" style="text-align: center;font-weight: bold;"><span class="round${i }"><spring:message code="listmemberchoice.round" text="Round"/>${i }</span><a id="reset${i }" class="reset" style="font-weight: bolder;cursor: pointer;" title="Clear Round ${i }">(-)</a></td>
			</tr>			
			<c:forEach var="j" begin="1" end="${questionsInEachRoundMap[i]}">
			<c:choose>
					<c:when test="${noOfMemberBallotChoices==0 && noOfQuestionsInLastRound < questionsInLastRoundByDefault}">
					<tr>
						<td><span class="round${i}">${j}</span></td>
						<td>
							<select id="question${count}" name="question${count}" class="question round${i} sSelect">
								<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
								<c:forEach items="${admittedQuestions}" var="k">
									<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>
								</c:forEach>
							</select>
							<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;" title="Show All Questions">+</a>					
						</td>
						<td class="ad">
							<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round${i} sSelect">
								<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
							</select>
							<input id="round${count}" name="round${count }" value="${i}" type="hidden">
							<input id="choice${count}" name="choice${count }" value="${j}" type="hidden">	
							<a id="autofillstartsat${count}" class="autofillstartsat" style="font-weight: bolder;cursor: pointer;" title="Autofill from here">@</a>	
							<a id="enable${count}" class="enable" style="font-weight: bolder;cursor: pointer;" title="Enable/Disable this Question and Answering Date">$</a>				
						</td>						
					</tr>
					</c:when>
					<c:when test="${noOfMemberBallotChoices==0 && noOfQuestionsInLastRound >= questionsInLastRoundByDefault}">
					<tr>
						<td><span class="round${i}">${j}</span></td>
						<td>
							<select id="question${count}" name="question${count}" class="question round${i} sSelect" disabled="disabled">
								<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
								<c:forEach items="${admittedQuestions}" var="k">
									<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>
								</c:forEach>
							</select>
							<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;" title="Show All Questions">+</a>					
						</td>
						<td class="ad">
							<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round${i} sSelect" disabled="disabled">
								<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
							</select>
							<input id="round${count}" name="round${count }" value="${i}" type="hidden">
							<input id="choice${count}" name="choice${count }" value="${j}" type="hidden">	
							<a id="autofillstartsat${count}" class="autofillstartsat" style="font-weight: bolder;cursor: pointer;" title="Autofill from here">@</a>	
							<a id="enable${count}" class="enable" style="font-weight: bolder;cursor: pointer;" title="Enable/Disable this Question and Answering Date">$</a>				
						</td>						
					</tr>
					</c:when>
					<c:when test="${noOfMemberBallotChoices > 0}">						
							<tr>
								<td><span class="round${i}">${j}</span></td>
								<td>
									<c:choose>
										<c:when test="${!(empty memberBallotChoicesMap[i][j])}">
											<select id="question${count}" name="question${count}" class="question round${i} sSelect">
												<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
												<c:forEach items="${admittedQuestions}" var="k">
													<c:choose>
														<c:when test="${memberBallotChoicesMap[i][j].question.id==k.id }">
															<option value='${k.id}' selected="selected"><c:out value="${k.findFormattedNumber()}"></c:out></option>
														</c:when>
														<c:otherwise>
															<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>
														</c:otherwise>
													</c:choose>									
												</c:forEach>
											</select>
											<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;" title="Show All Questions">+</a>					
										</c:when>
										<c:otherwise>
											<select id="question${count}" name="question${count}" class="question round${i} sSelect" disabled="disabled">
												<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
												<c:forEach items="${admittedQuestions}" var="k">											
													<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>
												</c:forEach>
											</select>
											<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;" title="Show All Questions">+</a>					
										</c:otherwise>
									</c:choose>							
								</td>								
								<td class="ad">
									<c:choose>
									<c:when test="${!(empty memberBallotChoicesMap[i][j])}">
										<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round${i} sSelect">
											<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
											<c:forEach items="${memberBallotChoicesMap[i][j].question.group.questionDates}" var="l">
												<c:choose>
													<c:when test="${l.id==memberBallotChoicesMap[i][j].newAnsweringDate.id}">
														<option value="${l.id }" selected="selected">${l.findFormattedAnsweringDate()}</option>
													</c:when>
													<c:otherwise>
														<option value="${l.id }">${l.findFormattedAnsweringDate()}</option>					
													</c:otherwise>					
												</c:choose>
											</c:forEach>
										</select>
									</c:when>
									<c:otherwise>
										<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round${i} sSelect" disabled="disabled">
											<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
										</select>
									</c:otherwise>
									</c:choose>
									
									<input id="round${count}" name="round${count }" value="${i}" type="hidden">
									<input id="choice${count}" name="choice${count }" value="${j}" type="hidden">	
									<a id="autofillstartsat${count}" class="autofillstartsat" style="font-weight: bolder;cursor: pointer;" title="Autofill from here">@</a>	
									<a id="enable${count}" class="enable" style="font-weight: bolder;cursor: pointer;" title="Enable/Disable this Question and Answering Date">$</a>				
								</td>						
							</tr>
										
					</c:when>					
			</c:choose> 					
			<c:set value="${i}" var="currentRound"></c:set>
			<c:if test="${currentRound == totalRounds}">
			<c:set value="${noOfQuestionsInLastRound+1}" var="noOfQuestionsInLastRound"></c:set>	
			</c:if>
			<c:set value="${count+1}" var="count"></c:set>	
			</c:forEach>
		</c:forEach>				
	</table>
	<div class="fields">
		<p style="margin-top: 20px;"></p>
		<c:if test="${isMemberFillingQuestionChoices!='YES'}">
			<p>
				<label class="centerlabel" style="margin-top: 10px;"><spring:message code="memberballotchoice.reasonForChoicesUpdate" text="Reason for choices update"/>*</label>
				<textarea id="reasonForChoicesUpdate" name="reasonForChoicesUpdate" rows="2" cols="50"></textarea>
			</p>
			<h2></h2>			
		</c:if>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" style="text-align:center;">
		</p>
	</div>
</div>

<input id="noOfAdmittedQuestions" name="noOfAdmittedQuestions" value="${noOfAdmittedQuestions }" type="hidden">
<input id="totalRounds" name="totalRounds" value="${totalRounds }" type="hidden">
<input id="flag" name="flag" value="${flag }" type="hidden">
<input id="autofillingstartsat" name="autofillingstartsat" type="hidden" value="0">
<input id="blankForm" name="blankForm" type="hidden" value="no">
<input id="blankFormAutoFillingStartsFromLast" name="blankFormAutoFillingStartsFromLast" type="hidden" value="no">
<input id="blankFormAutoFillingStartsAt" name="blankFormAutoFillingStartsAt" type="hidden" value="${blankFormAutoFillingStartsAt}">

<input id="questionsInLastRoundByDefault" name="questionsInLastRoundByDefault" type="hidden" value="${questionsInLastRoundByDefault}">
<input id="noOfMemberBallotChoicesExceptLast" name="noOfMemberBallotChoicesExceptLast" type="hidden" value="${noOfMemberBallotChoicesExceptLast}">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="resetAll" value="<spring:message code='generic.resetAll' text='Do you want to clear choices for all rounds ?'/>"/>
<input type="hidden" id="resetParticularRound" value="<spring:message code='generic.resetParticularRound' text='Do you want to clear choices for round : '/>"/>
</body>
</html>