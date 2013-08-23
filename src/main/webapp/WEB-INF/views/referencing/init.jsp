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
			$("#backToSearch").click(function(){
					back();
			});
			$("#searchvalue").change(function(){
				start=0;				
				$("#referencingResult").empty();	
				$("#searchTable tbody").empty();		
				$("#referencingDiv").hide();
				previousSearchCount=record;
			});			
			$("#search").click(function(){
				search();
			});
			
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
			$("#refSsessionYear").change(function(){
				if($(this).val()=='-'){
					$(this).css('color','black');
				}else{
					$(this).css('color','blue');
				}
			});
			$("#refSessionType").change(function(){
				if($(this).val()=='-'){
					$(this).css('color','black');
				}else{
					$(this).css('color','blue');
				}
			});
			$("#reset").click(function(){					
				$("#refSessionYear").prop('selectedIndex',0);
				$("#refSessionType").prop('selectedIndex',0);
				$("#refSessionYear").css('color','black');
				$("#refSessionType").css('color','black');
				$("#referencingResult").empty();
				$("#referencingResult").html("");
				$("#searchTable tbody").empty();
				$("#referencingDiv").hide();
							
			});
				
			$("#backToResolution").click(function(){
				$("#referencingResultDiv").hide();
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
			$("#referencingDiv").hide();
			
			$("#refSessionYear").change(function(){
				if($(this).val()=='-'){
					$(this).css("color","black");
				}else{
					$(this).css("color","blue");
				}
			});
			
			$("#refSessionType").change(function(){
				if($(this).val()=='-'){
					$(this).css("color","black");
				}else{
					$(this).css("color","blue");
				}
			});
		});
		
		function search(){
			var toBeSearched=$("#searchvalue").val();
			previousSearchTerm=toBeSearched;			
			
			var currentDeviceType = $("#refDeviceType").val();
			if(toBeSearched!=''){
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){	
					if(/*/^questions_/.test(currentDeviceType)*/currentDeviceType.contains('questions')){
						$.post('refentity/search',{param:$("#searchvalue").val(),question:$("#questionId").val(), houseType:$("#houseTypeType").val(), questionSessionYear:$("#refSessionYear").val(),questionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							if(data.length>0){
								var text="";	
								for(var i=0;i<data.length;i++){
									text+="<tr>";
									text+="<td>"+data[i].number+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
									+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
									+"</td>";
									text+="<td>"+data[i].subject+"</td>";			
									text+="<td>"+data[i].questionText+"</td>";			
									text+="<td>"+data[i].deviceType+"</td>";			
									text+="<td>"+data[i].sessionYear+"</td>";			
									text+="<td>"+data[i].sessionType+"</td>";			
									text+="<td>"+data[i].status+"</td>";
									text+="</tr>";						
								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><a onclick='search();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
										+"</tr>";
									start=start+10;							
								}
								$("#searchTable tbody").empty();
								$("#searchTable tbody").append(text);	
								$("#referencingDiv").show();										
							}else{
								$("#referencingResult").empty();
								$("#referencingResult").html($("#noResultsMsg").val());
								$("#referencingDiv").show();
							}				
						}).fail(function(){
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});	
					}else if(/*/^resolutions_/.test(currentDeviceType)*/currentDeviceType.contains('resolutions')){
						$.post('refentity/searchresolution',{param:$("#searchvalue").val(),resolution:$("#resolutionId").val(), resolutionSessionYear:$("#refSessionYear").val(),resolutionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							
							if(data.length>0){
								var text="";
								for(var i=0;i<data.length;i++){
									text+="<tr>";
									text+="<td>"+data[i].number+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
									+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
									+"</td>";
									text+="<td>"+data[i].subject+"</td>";			
									text+="<td>"+data[i].noticeContent+"</td>";			
									text+="<td>"+data[i].deviceType+"</td>";	
									text+="<td>"+data[i].sessionYear+"</td>";			
									text+="<td>"+data[i].sessionType+"</td>";
									text+="<td>"+data[i].status+"</td>";
									text+="</tr>";						
								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><a onclick='search();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
										+"</tr>";
									start=start+10;							
								}
								$("#searchTable tbody").empty();
								$("#searchTable tbody").append(text);	
								$("#referencingDiv").show();										
							}else{
								$("#referencingResult").empty();
								$("#referencingResult").html($("#noResultsMsg").val());
								$("#referencingDiv").show();
								$("#searchresult").hide();
							}				
						}).fail(function(){
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
	    				});
						
					}
				}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val());
					$("#clubbingDiv").show();						
				}	
			}else{
				$.prompt($("#nothingToSearchMsg").val());
				$("#referencingDiv").hide();
			}
		}
		function referencing(referId){
			var whichDevice= $('#whichDevice').val();
			var device=$("#refDeviceType").val();
			if(whichDevice=='questions_'){
				var deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				var deviceId=$("#resolutionId").val();
			}
			$.post('refentity/referencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
					if(data=='SUCCESS'){
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}
		function dereferencing(referId){
			var whichDevice= $('#whichDevice').val();
			var device=$("#refDeviceType").val();
			if(whichDevice=='questions_'){
				var deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				var deviceId=$("#resolutionId").val();
			}
			var device=$("#refDeviceType").val();
			$.post('refentity/dereferencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
				if(data=='SUCCESS'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}				
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}		

		function viewDetail(referId){
			var resourceURL="";
			var device=$("#refDeviceType").val();
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#refDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&usergroupType="+$("#usergroupType").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			if(/^questions_/.test(device)){
				resourceURL='question/'+referId+'/edit?'+parameters;
			}else if(/^resolutions_/.test(device)){
				resourceURL='resolution/'+referId+'/edit?'+parameters;
			}
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				if($("#whichDevice").val()=='questions_'){
					$("#viewQuestion").html(data);
					$("#viewQuestionDiv").show();
				}else if($("#whichDevice").val()=='resolutions_'){
					$("#viewResolution").html(data);
					$("#viewResolutionDiv").show();
				}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});		
		}

		function back(){
			$("#referencingDiv").show();		
			$("#referencingResult").empty();
			$("#viewQuestion").empty();
			$("#viewQuestionDiv").hide();
			if($("#whichDevice").val()=='resolutions_'){
				$("#viewResolution").empty();
				$("#viewResolutionDiv").hide();
			}else if($("#whichDevice").val()=='questions_'){
				$("#viewQuestion").empty();
				$("#viewQuestionDiv").hide();
			}
		}
	</script>
	<style type="text/css">
	#testingview:hover{
		cursor: pointer;
	}
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
.bold{
font-weight: bold;
}
.expand{
}
#searchTable a{
text-decoration: underline;
color: green;
cursor: hand;
cursor:pointer;
}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="searchBoxDiv">
	<p>
		<a href="#">
			<spring:message code="advancedsearch.sessionyear" text="Session Year"/>
		</a>	
		<select name="sessionYear" id="refSessionYear" style="width:100px;height: 25px;">				
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
			
			<a href="#">
				<spring:message code="advancedsearch.sessionType" text="Session Type"/>
			</a>			
			<select name="sessionType" id="refSessionType" style="width:100px;height: 25px;">				
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
	</p>
	<table cellpadding="0px" cellspacing="0px">
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:1px;">
				<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.reset" text="Reset Filters"></spring:message></a>
				<c:if test="${whichDevice=='questions_'}">
					<a href="#" id="backToQuestion" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.back" text="Back"></spring:message></a>
				</c:if>
				<c:if test="${whichDevice=='resolutions_'}">
					<a href="#" id="backToResolution" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.back" text="Back"></spring:message></a>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<p id="referencingP">
	<c:choose>
		<c:when test="${whichDevice=='questions_'}" >
			<label style="color:blue;font-size:14px;">${number}:${subject}</label>
		</c:when>
		<c:when test="${whichDevice=='resolutions_'}" >
			<label style="color:blue;font-size:14px;">${number}:${noticeContent}</label>
		</c:when>
	</c:choose>
	
	<c:choose>
		<c:when test="${whichDevice=='questions_'}">
			<input type="hidden" id="questionId" value="${id }">
		</c:when>
		<c:when test="${whichDevice=='resolutions_'}">
			<input type="hidden" id="resolutionId" value="${id }">
		</c:when>
	</c:choose>
</p>

<div id="referencingDiv">
	<div id="refResult"></div>
	<div id="referencingResult" style="margin: 10px;">
	</div>
	
	<div id="searchresult">
		<table  id="searchTable">
		<thead>
			<tr>
				<c:if test="${whichDevice=='questions_'}">
					<th><spring:message code="referencing.number" text="Question Number"></spring:message></th>
				</c:if>
				<c:if test="${whichDevice=='resolutions_'}">
					<th><spring:message code="referencing.number" text="Resolution Number"></spring:message></th>
				</c:if>
				<th><spring:message code="referencing.subject" text="Subject"></spring:message></th>
				<c:if test="${whichDevice=='questions_'}">
					<th><spring:message code="referencing.question" text="Question"></spring:message></th>
				</c:if>
				<c:if test="${whichDevice=='resolutions_'}">
					<th><spring:message code="referencing.resolution" text="Resolution"></spring:message></th>
				</c:if>
				<th><spring:message code="referencing.devicetype" text="Device Type"></spring:message></th>
				<th><spring:message code="referencing.sessionyear" text="Session Year"></spring:message></th>
				<th><spring:message code="referencing.sessiontype" text="Session Type"></spring:message></th>
				<th><spring:message code="referencing.status" text="Status"></spring:message></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
	</div>
</div>

<c:choose>
	<c:when test="${whichDevice=='questions_'}" >
		<div id="viewQuestionDiv" style="display:none;">
			<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
			<div id="viewQuestion">
			</div>
		</div>
	</c:when>
	
	<c:when test="${whichDevice=='resolutions_'}" >
		<div id="viewResolutionDiv" style="display:none;">
			<a id="backToSearch" href="#" style="display:block;"><spring:message code="clubbing.back" text="Back to search page"></spring:message></a>
			<div id="viewResolution">
			</div>
		</div>
	</c:when>
</c:choose>
<br />
<br />
<input id="nothingToSearchMsg" value="<spring:message code='referencing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='referencing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='referencing.viewdetail' text='View Detail'></spring:message>" type="hidden">
<input id="referMsg" value="<spring:message code='referencing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="dereferMsg" value="<spring:message code='referencing.dereferencing' text='Dereferencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="refUsergroupType" type="hidden" value="${usergroupType}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>