<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var start=0;
	var record=10;
	var previousSearchTerm="";
	var previousSearchCount=record;
		$(document).ready(function() {
			/**** Reset Filters ****/
			$("#reset").click(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				$("#deviceTypeStarred").val("-");
				$("#deviceTypeStarred").css("color","");
				$("#sessionYearStarred").val("-");
				$("#sessionYearStarred").css("color","");				
				$("#sessionTypeStarred").val("-");
				$("#sessionTypeStarred").css("color","");								
				$("#groupStarred").empty();
				$("#groupStarred").html(text);	
				$("#groupStarred").css("color","");								
				$("#answeringDateStarred").empty();
				$("#answeringDateStarred").html(text);
				$("#answeringDateStarred").css("color","");				
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);
				$("#ministryStarred").css("color","");				
				$("#departmentStarred").empty();
				$("#departmentStarred").html(text);
				$("#departmentStarred").css("color","");				
				$("#subDepartmentStarred").empty();
				$("#subDepartmentStarred").html(text);
				$("#subDepartmentStarred").css("color","");				
				$("#statusStarred").val("-");	
				$("#statusStarred").css("color","");				
							
			});
			/**** Filters ****/
			$(".unstarred").hide();
			/**** Session Year ****/
			$("#sessionYearStarred").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#sessionTypeStarred").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&type!='-'&&houseType!='-'){
					loadGrp(houseType,value,type);
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupStarred").empty();
					$("#groupStarred").html(text);					
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);					
				}
			});
			/**** Session Type ****/
			$("#sessionTypeStarred").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#sessionYearStarred").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&year!='-'&&houseType!='-'){
					loadGrp(houseType,year,value);
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupStarred").empty();
					$("#groupStarred").html(text);					
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}
			});
			/**** Device Type ****/
			$("#deviceTypeStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value=='-'){
					$(".unstarred").hide();
				}else{
					var type=$("#deviceTypeStarredMaster option[value='"+value+"']").text();
					if(type=='questions_starred'){
						$(".unstarred").hide();
					}else{
						$(".unstarred").show();
					}
				}
			});
			/**** Group ****/
			$("#groupStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadAD(value);
				}
			});
			/**** Ministry ****/
			$("#ministryStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadDep(value);
				}
			});
			/**** Department ****/	
			$("#departmentStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadSubDep($("#ministryStarred").val(),value);
				}
			});		
			/**** Ansering Date ****/
			$("#answeringDateStarred").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Sub Department ****/
			$("#subDepartmentStarred").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Status ****/
			$("#statusStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** primary Question's Details ****/
			$("#primary").click(function(){
				viewDetail($("#questionId").val());
			});
			/**** Back To Search Page****/
			$("#backToSearch").click(function(){
					back();
			});			
			/**** Search Content Changes ****/
			$("#searchvalue").change(function(){
				start=0;				
				$("#clubbingResult").empty();	
				$("#searchTable tbody").empty();
				$("#clubbingDiv").hide();
				previousSearchCount=record;										
			});			
			/**** On clicking search button ****/
			$("#search").click(function(){
			search("YES");
			});
			/**** On Page Load ****/
			$("#clubbingDiv").hide();
			/**** Back To Question ****/
			/**** Back To Question ****/
			$("#backToQuestion").click(function(){
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToQuestionDiv").hide();
				if($("#assistantDiv").length>0){
				$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
	});
		
		/**** Group ****/
		function loadGrp(houseType,sessionYear,sessionType){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			param="houseType="+houseType+"&year="+sessionYear+"&sessionType="+sessionType;
			$("#answeringDateStarred").empty();
			$("#answeringDateStarred").html(text);
			$("#ministryStarred").empty();
			$("#ministryStarred").html(text);
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			$.get('ref/groups?'+param,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#groupStarred").empty();
				$("#groupStarred").html(text);
			}else{
				$("#groupStarred").empty();
				$("#groupStarred").html(text);					
				}
			});
		}
		/**** Answering Date ****/
		function loadAD(group){
		var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
		$.get('ref/group/'+group+'/answeringdates',function(data){
			if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#answeringDateStarred").empty();
				$("#answeringDateStarred").html(text);
				loadMin(group);						
			}else{
				$("#answeringDateStarred").empty();
				$("#answeringDateStarred").html(text);
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);		
			}
		});	
		}
		/**** Minister ****/
		function loadMin(group){
		var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
		$("#departmentStarred").empty();
		$("#departmentStarred").html(text);
		$("#subDepartmentStarred").empty();
		$("#subDepartmentStarred").html(text);
		$.get('ref/group/'+group+'/ministries',function(data){
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#ministryStarred").empty();
			$("#ministryStarred").html(text);						
			}else{
			$("#ministryStarred").empty();
			$("#ministryStarred").html(text);		
			}
		});	
		}
		/**** Department ****/
		function loadDep(ministry){
		$.get('ref/departments/'+ministry,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);					
			}else{
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);
			}
		});
		}
		/**** Sub Department ****/
		function loadSubDep(ministry,department){
		$.get('ref/subdepartments/'+ministry+'/'+department,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);					
			}else{
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			}			
		});
		}
		/**** On clicking search button ****/		
		function search(fresh){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			/**** Constructing data to be sent in post request ****/
			if(fresh=='YES'){
				start=0;
				$("#searchTable tbody").empty();				
			}
			var postData={param:$("#searchvalue").val(),question:$("#questionId").val(),record:record,start:start};
			if($("#deviceTypeStarred").length>0){
				postData['deviceType']=$("#deviceTypeStarred").val();
			}
			if($("#houseTypeCommon").length>0){
				postData['houseType']=$("#houseTypeCommon").val();
			}
			if($("#sessionYearStarred").length>0){
				postData['sessionYear']=$("#sessionYearStarred").val();
			}
			if($("#sessionTypeStarred").length>0){
				postData['sessionType']=$("#sessionTypeStarred").val();
			}
			if($("#groupStarred").length>0){
				postData['group']=$("#groupStarred").val();
			}
			if($("#answeringDateStarred").length>0){
				postData['answeringDate']=$("#answeringDateStarred").val();
			}
			if($("#ministryStarred").length>0){
				postData['ministry']=$("#ministryStarred").val();
			}
			if($("#departmentStarred").length>0){
				postData['department']=$("#departmentStarred").val();
			}
			if($("#subDepartmentStarred").length>0){
				postData['subDepartment']=$("#subDepartmentStarred").val();
			}
			if($("#statusStarred").length>0){
				postData['status']=$("#statusStarred").val();
			}			
			var toBeSearched=$("#searchvalue").val();			
			//previousSearchTerm=toBeSearched;
			/**** Search Input Box is not empty ****/
			if(toBeSearched!=''){
				/**** Search takes place if its a new search or while loading more data in current search ****/
				//if((previousSearchCount==record)){
				$.post('clubentity/search',postData,function(data){
					/**** previousSearchCount controls if clicking search button next time with same content
					 will call search function.It will only if this time no. of entries returned is
					 equal to max no of records in each search call=record****/
					previousSearchCount=data.length;
					if(data.length>0){
					var text="";	
						for(var i=0;i<data.length;i++){
							var textTemp="";
							var textTemp=textTemp+"<tr>"+
									"<td class='expand'>"+
									"<span id='number"+data[i].id+"'>"+
									"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+									
									data[i].number+"</a></span>"
									+"<br>";
							textTemp+="<span id='operation"+data[i].id+"'>";									
							if(data[i].classification=='Clubbing'){
								textTemp+="<a onclick='clubbing("+data[i].id+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>";
							}else if(data[i].classification=='Group Change'){
								textTemp+="<a style='margin:10px;' href='#'>"+$("#groupChangeMsg").val()+"</a>";
							}else if(data[i].classification=='Ministry Change'){
								textTemp+="<a style='margin:10px;' href='#'>"+$("#ministryChangeMsg").val()+"</a>";
							}else if(data[i].classification=='Department Change'){
								textTemp+="<a style='margin:10px;' href='#'>"+$("#departmentChangeMsg").val()+"</a>";
							}else if(data[i].classification=='Sub Department Change'){
								textTemp+="<a style='margin:10px;' href='#'>"+$("#subDepartmentChangeMsg").val()+"</a>";
							}else if(data[i].classification=='Referencing'){
								textTemp+="<a style='margin:10px;' href='#'>"+$("#referencingMsg").val()+"</a>";
							}
							textTemp+="</span>";					
							+"</td>";
						
							textTemp+="<td class='expand'>"+data[i].subject+"</td>";
									
							textTemp+="<td class='expand'>"+data[i].questionText
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+"<strong>"+data[i].formattedGroup+"</span>,"+data[i].ministry+"<br>"
								+data[i].department+","+data[i].subDepartment+"<br>"
								+data[i].status
								+"</td>";
											
							textTemp+="</tr>";	
							console.log(i);						
							
							text+=textTemp;
						}	
						if(data.length==10){
							text+="<tr>"
								+"<td style='text-align:center;'><span class='clearLoadMore'><a onclick='loadMore();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></span></td>"
								+"</tr>";
							start=start+10;							
						}
						$("#clubbingResult").empty();						
						$("#searchTable > #searchresultbody:last").append(text);	
						$("#searchresult").show();							
						$("#clubbingDiv").show();
						$.unblockUI();													
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html($("#noResultsMsg").val());
						$("#searchTable tbody").empty();							
						$("#clubbingDiv").show();
						$("#searchresult").hide();						
						$.unblockUI();										
					}								
				});		
				/*}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val());
					$("#clubbingDiv").show();
					if($("#searchTable > #searchresultbody tr").length>0){
					$("#searchresult").show();							
					}else{
					$("#searchresult").show();							
					}
					$.unblockUI();									
				}*/
			}else{
				$.prompt($("#nothingToSearchMsg").val());
				$("#searchTable tbody").empty();
				$("#searchresult").hide();				
				$("#clubbingDiv").hide();
				$.unblockUI();				
			}
		}
		/**** Load More ****/
		function loadMore(){			
			search("NO");
			$(".clearLoadMore").empty();
		}		
		/**** On Clubbing ****/
		function clubbing(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });			
			var questionId=$("#questionId").val();	
			var questionNumber=$("#questionNumber").val();
			var clubbedNumber=$("#number"+clubId).text();		
			$.post('clubentity/clubbing?pId='+questionId+"&cId="+clubId,function(data){
					if(data=='SEARCHED_CLUBBED_TO_PROCESSED'){
					var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" Clubbed To "+questionNumber;
					$("#clubbingResult").empty();
					$("#clubbingResult").html(text);
					$("#operation"+clubId).empty();
					$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else if(data=='PROCESSED_CLUBBED_TO_SEARCHED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+questionNumber+" Clubbed To "+clubbedNumber;
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else if(data=='BEINGSEARCHED_QUESTION_ALREADY_CLUBBED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" is already a clubbed question.";
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html(data);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='clubbing("+clubId+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>");
					}									
			},'html');
			$.unblockUI();	
			$('html').animate({scrollTop:0}, 'slow');
			$('body').animate({scrollTop:0}, 'slow');	
			return false;
		}
		/**** On unclubbing ****/
		function unclubbing(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var questionId=$("#questionId").val();
			$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId,function(data){
				if(data=='SUCCESS'){
					$("#clubbingResult").empty();
					$("#clubbingResult").html(data);
					$("#operation"+clubId).empty();
					$("#operation"+clubId).html("<a onclick='clubbing("+clubId+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>");
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html(data);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}				
			},'html');
			$.unblockUI();	
			$('html').animate({scrollTop:0}, 'slow');
			$('body').animate({scrollTop:0}, 'slow');	
			return false;
		}		
		/**** view question details in readonly mode ****/
		function viewDetail(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });			
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#selectedQuestionType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='question/'+clubId+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$("#clubbingDiv").hide();
				$("#viewQuestion").html(data);
				$("#viewQuestionDiv").show();
				$.unblockUI();				
			},'html');
			$.unblockUI();						
		}
		/**** on clicking back ****/
		function back(){
			$("#clubbingDiv").show();		
			$("#clubbingResult").empty();
			$("#viewQuestion").empty();
			$("#viewQuestionDiv").hide();
		}
	</script>

<style type="text/css">
#searchTable{
  border: 0px solid black;
  border-spacing: 0px;
}

#searchTable thead tr{
   font-size: 14px;
}

#searchTable thead tr th{
  border-bottom: 2px solid black;
  border-top: 1px solid black;
  margin: 0px;
  padding: 2px;
  background-color: #cccccc;
}

#searchTable tr {
  font-size:12px;  
}

#searchTable tr.odd {
  background-color: #AAAAAA;
}

#searchTable tr td, th{
  border-bottom: 1px solid black;
  padding: 2px; 
  text-align: center;  
}	
.highlightedSearchPattern{
font-weight: bold;
text-decoration: underline;
}
.expand{

}
#searchTable a{
text-decoration: underline;
color: green;
cursor: hand;
cursor:pointer;
}
.filterSelected{
color:blue;
}
</style>
</head>
<body>

<div class="commandbarContent" id="advancedSearch">

			<c:choose>
			<c:when test="${deviceType=='questions_starred'}">
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.deviceType" text="Device Type"/>
			</a>		
			<select name="deviceTypeStarred" id="deviceTypeStarred" style="width:100px;height: 25px;">			
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:forEach>
			</select> 
			<select id="deviceTypeStarredMaster" style="display:none;">
			<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id}"><c:out value="${i.type}"></c:out></option>			
			</c:forEach>
			</select> |			
			<a href="#" class="butSim unstarred">
				<spring:message code="advancedsearch.sessionyear" text="Year"/>
			</a>			
			<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;" class="unstarred">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${years}">
			<c:choose>
			<c:when test="${sessionYear==i.id}">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach> 
			</select> |			
			<a href="#" class="butSim unstarred">
				<spring:message code="advancedsearch.sessionType" text="Session Type"/>
			</a>			
			<select name="sessionTypeStarred" id="sessionTypeStarred" style="width:100px;height: 25px;" class="unstarred">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${sessionTypes}" var="i">
			<c:choose>
			<c:when test="${sessionType==i.id}">
			<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
			</c:when>
			<c:otherwise>
			<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>	
			</c:otherwise>
			</c:choose>
			</c:forEach> 
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="question.group" text="Group"/>
			</a>			
			<select name="groupStarred" id="groupStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="question.answeringDate" text="Answering Date"/>
			</a>			
			<select name="answeringDateStarred" id="answeringDateStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |
			<hr>			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.ministry" text="Ministry"/>
			</a>			
			<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.department" text="Department"/>
			</a>			
			<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
			</a>			
			<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>			
			<select name="statusStarred" id="statusStarred" class="sSelect">			
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
			<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
			<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
			</select> |
			</c:when>
			
			
			<c:when test="${deviceType=='questions_unstarred'}">
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.sessionyear" text="Year"/>
			</a>			
			<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${years}">
			<c:choose>
			<c:when test="${sessionYear==i.id}">
			<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
			</c:when>
			<c:otherwise>
			<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
			</c:otherwise>
			</c:choose>
			</c:forEach> 
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.sessionType" text="Session Type"/>
			</a>			
			<select name="sessionTypeStarred" id="sessionTypeStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
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
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.ministry" text="Ministry"/>
			</a>			
			<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.department" text="Department"/>
			</a>			
			<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |	
			<hr>		
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
			</a>			
			<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>			
			<select name="statusStarred" id="statusStarred" class="sSelect">			
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
			<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
			<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
			</select> |
			</c:when>			
			
			<c:otherwise>					
			<a href="#" class="butSim">
				<spring:message code="question.group" text="Group"/>
			</a>			
			<select name="groupStarred" id="groupStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
			</select> |						
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.ministry" text="Ministry"/>
			</a>			
			<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.department" text="Department"/>
			</a>			
			<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |			
			<a href="#" class="butSim">
				<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
			</a>			
			<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			</select> |	
			<hr>		
			<a href="#" class="butSim">
				<spring:message code="question.status" text="Status"/>
			</a>			
			<select name="statusStarred" id="statusStarred" class="sSelect">			
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
			<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
			<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
			</select> |
			</c:otherwise>
			</c:choose>						
</div>	


<hr>


<div id="searchBoxDiv">
<table cellpadding="0px" cellspacing="0px"> 
<tr> 
<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:2px;">
<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
</td>
<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
</td>
<td>
<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.reset" text="Reset Filters"></spring:message></a>
<a href="#" id="backToQuestion" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.back" text="Back"></spring:message></a>
</td>
</tr>
</table>
</div>

<p id="clubbingP">
<a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${subject}
<input type="hidden" id="questionId" value="${id }">
<input type="hidden" id="questionNumber" value="${number}">
</p>

<div id="clubbingDiv">

<div id="clubbingResult" style="margin: 10px;">
</div>

<div id="searchresult" style="display:none;">
<table  id="searchTable">
<thead>
<tr>
<th class="expand"><spring:message code="clubbing.number" text="Question Number"></spring:message></th>
<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
<th class="expand"><spring:message code="clubbing.question" text="Question"></spring:message></th>
</tr>
</thead>
<tbody id="searchresultbody">
</tbody>
</table>
</div>

</div>

<div id="viewQuestionDiv" style="display:none;">
<a id="backToSearch" href="#" style="display:block;"><spring:message code="clubbing.back" text="Back to search page"></spring:message></a>
<div id="viewQuestion">
</div>
</div>

<input id="nothingToSearchMsg" value="<spring:message code='clubbing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='clubbing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='clubbing.viewdetail' text='Detail'></spring:message>" type="hidden">
<input id="clubMsg" value="<spring:message code='clubbing.club' text='Club'></spring:message>" type="hidden">
<input id="unclubMsg" value="<spring:message code='clubbing.unclub' text='Unclub'></spring:message>" type="hidden">
<input id="groupChangeMsg" value="<spring:message code='clubbing.groupchange' text='Change Group'></spring:message>" type="hidden">
<input id="ministryChangeMsg" value="<spring:message code='clubbing.ministrychange' text='Change Ministry'></spring:message>" type="hidden">
<input id="departmentChangeMsg" value="<spring:message code='clubbing.departmentchange' text='Change Department'></spring:message>" type="hidden">
<input id="subDepartmentChangeMsg" value="<spring:message code='clubbing.subDepartmentchange' text='Change Sub Department'></spring:message>" type="hidden">
<input id="referencingMsg" value="<spring:message code='clubbing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="houseTypeCommon" value="${houseType}" type="hidden">
<input id="houseTypeYearSessionTypeEmptyMsg" value="<spring:message code='client.error.advancedsearch.yeartypeempty' text='Session Year and Session Type must be selected to continue'/>" type="hidden">
<input id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
</body>
</html>