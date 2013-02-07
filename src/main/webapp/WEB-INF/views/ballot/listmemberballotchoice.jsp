<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {
		var lastAutoFilledValue=0;
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
<c:if test="${type=='SUCCESS' }">
<div class="toolTip tpGreen clearfix" id="successDiv">
<p style="font-size: 14px;"><img
	src="./resources/images/template/icons/light-bulb-off.png"> <spring:message
	code="update_success" text="Data saved successfully." /></p>
<p></p>
</div>
</c:if>
<div id="memberChoicesDiv">
<c:choose>
	<c:when test="${!(empty memberBallots)&&flag=='edit' }">
	<table class="uiTable" style="width:100%">					
					<tr>						
						<th><spring:message code="memberballotchoice.sno" text="S.No"></spring:message></th>
						<th><spring:message code="memberballotchoice.Question"
							text="Question"></spring:message></th>
						<th><spring:message code="memberballotchoice.answeringdate"
							text="Answering Date"></spring:message></th>
					</tr>
					<c:set value="1" var="count"></c:set>	
					<c:set value="1" var="roundcount"></c:set>					
									
					<c:forEach items="${memberBallots }" var="i">
					<c:if test="${!(empty i.questionChoices)}">					
					<tr>
					<td colspan="3" style="text-align: center;font-weight: bold;"><span class="round${roundcount }"><spring:message code="listmemberchoice.round" text="Round"/>${i.round}</span></td>
					</tr>
					<c:forEach items="${i.questionChoices}" var="j">
					<tr>
					<td><span class="round${roundcount}">${j.choice}</span></td>
					<td>
					<select id="question${count}" name="question${count}" class="question round${roundcount } sSelect">
					<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
					<c:forEach items="${admittedQuestions}" var="k">
					<c:choose>
					<c:when test="${k.id==j.question.id}">
					<option value='${k.id}' selected="selected"><c:out value="${k.findFormattedNumber()}"></c:out></option>					
					</c:when>
					<c:otherwise>
					<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>					
					</c:otherwise>
					</c:choose>
					</c:forEach>
					</select>
					<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;">+</a>
					</td>
					<td class="ad">
					<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round${roundcount } sSelect">
					<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
					<c:forEach items="${j.question.group.questionDates}" var="l">
					<c:choose>
					<c:when test="${l.id==j.newAnsweringDate.id}">
					<option value="${l.id }" selected="selected">${l.findFormattedAnsweringDate()}</option>
					</c:when>
					<c:otherwise>
					<option value="${l.id }">${l.findFormattedAnsweringDate()}</option>					
					</c:otherwise>					
					</c:choose>
					</c:forEach>
					</select>
					<input id="round${count}" name="round${count }" value="${i.round }" type="hidden">
					<input id="choice${count}" name="choice${count }" value="${j.choice}" type="hidden">
					<input id="memberBallotchoiceId${count}" name="memberBallotchoiceId${count}" value="${j.id}" type="hidden">		
					<a id="autofillstartsat${count}" class="autofillstartsat" style="font-weight: bolder;cursor: pointer;">@</a>					
							
					</td>
					<c:set value="${count+1}" var="count"></c:set>		
					</tr>
					</c:forEach>
					</c:if>	
					<c:set value="${roundcount+1}" var="roundcount"></c:set>							
					</c:forEach>
	</table>
	<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" style="text-align:center;">
	
	</c:when>
	<c:when test="${!(empty admittedQuestions)&&flag=='new'}">	
				<table class="uiTable" style="width:100%">					
					<tr>						
						<th><spring:message code="memberballotchoice.sno" text="S.No"></spring:message></th>
						<th><spring:message code="memberballotchoice.Question"
							text="Question"></spring:message></th>
						<th><spring:message code="memberballotchoice.answeringdate"
							text="Answering Date"></spring:message></th>
					</tr>
					<c:set value="1" var="count"></c:set>
					<%
					    for (int i = 1; i <= (Integer) request.getAttribute("totalRounds"); i++) {
					%>	
					<c:if test="${count<=noOfAdmittedQuestions}">						
					<tr>
					<td colspan="3" style="text-align: center;font-weight: bold;"><span class="round<%=i%>"><spring:message code="listmemberchoice.round" text="Round"/><%=i%></span></td>
					</tr>
					</c:if>
					<% 
						for (int j = 1; j <= (Integer) request.getAttribute("round" + i); j++) {
 					%>	
 					<c:if test="${count<=noOfAdmittedQuestions}">
 					<tr>
					<td><span class="round<%=i%>"><%=j%></span></td>
					<td>
					<select id="question${count}" name="question${count}" class="question round<%=i%> sSelect">
					<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
					<c:forEach items="${admittedQuestions}" var="k">
					<option value='${k.id}'><c:out value="${k.findFormattedNumber()}"></c:out></option>
					</c:forEach>
					</select>
					<a id="all${count}" class="all" style="font-weight: bolder;cursor: pointer;">+</a>					
					</td>
					<td class="ad">
					<select id="answeringDate${count}" name="answeringDate${count}" class="answeringDate round<%=i%> sSelect">
					<option value='-'><spring:message code='please.select'	text='Please Select' /></option>
					</select>
					<input id="round${count}" name="round${count }" value="<%=i%>" type="hidden">
					<input id="choice${count}" name="choice${count }" value="<%=j%>" type="hidden">	
					<a id="autofillstartsat${count}" class="autofillstartsat" style="font-weight: bolder;cursor: pointer;">@</a>					
					</td>						
					</tr>
					<c:set value="${count+1}" var="count"></c:set>					
 					</c:if>					
					<%
					    }}
					%>
			</table>
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" style="text-align:center;">
				
	</c:when>					
</c:choose>	
</div>

<input id="noOfAdmittedQuestions" name="noOfAdmittedQuestions" value="${noOfAdmittedQuestions }" type="hidden">
<input id="totalRounds" name="totalRounds" value="${totalRounds }" type="hidden">
<input id="flag" name="flag" value="${flag }" type="hidden">
<input id="autofillingstartsat" name="autofillingstartsat" type="hidden" value="0">

</body>
</html>