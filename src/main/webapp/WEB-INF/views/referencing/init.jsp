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
			$("#referencingDiv").hide();			
	});
		function search(){
			var toBeSearched=$("#searchvalue").val();
			previousSearchTerm=toBeSearched;			
			if(toBeSearched!=''){
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){					
				$.post('refentity/search',{param:$("#searchvalue").val(),question:$("#questionId").val(),record:record,start:start},function(data){
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
						$("#searchTable tbody").append(text);	
						$("#referencingDiv").show();										
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html($("#noResultsMsg").val());
						$("#referencingDiv").show();
					}				
				});	
				}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val());
					$("#clubbingDiv").show();						
				}	
			}else{
				alert($("#nothingToSearchMsg").val());
				$("#referencingDiv").hide();
			}
		}
		function referencing(referId){
			var questionId=$("#questionId").val();			
			$.post('refentity/referencing?pId='+questionId+"&cId="+referId,function(data){
					if(data.id=='success'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).text("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).text("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}
			},'html');
			return false;
		}
		function dereferencing(referId){
			var questionId=$("#questionId").val();
			$.post('refentity/dereferencing?pId='+questionId+"&cId="+referId,function(data){
				if(data.id=='success'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).text("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).text("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}				
			},'html');
			return false;
		}		

		function viewDetail(referId){
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&questionType="+$("#selectedQuestionType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			var resourceURL='question/'+referId+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();			
				$("#viewQuestion").html(data);
				$("#viewQuestionDiv").show();
			},'html');		
		}

		function back(){
			$("#referencingDiv").show();		
			$("#referencingResult").empty();
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

<div id="searchBoxDiv">
<table cellpadding="0px" cellspacing="0px"> 
<tr> 
<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:1px;">
<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
</td>
<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
</td>
</tr>
</table>
</div>

<p id="referencingP">
<label style="color:blue;font-size:14px;">${number}:${subject}</label>
<input type="hidden" id="questionId" value="${id }">
</p>

<div id="referencingDiv">

<div id="referencingResult" style="margin: 10px;">
</div>

<div id="searchresult">
<table  id="searchTable">
<thead>
<tr>
<th><spring:message code="referencing.number" text="Question Number"></spring:message></th>
<th><spring:message code="referencing.subject" text="Subject"></spring:message></th>
<th><spring:message code="referencing.question" text="Question"></spring:message></th>
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

<div id="viewQuestionDiv" style="display:none;">
<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
<div id="viewQuestion">
</div>
</div>



<input id="nothingToSearchMsg" value="<spring:message code='referencing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='referencing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='referencing.viewdetail' text='View Detail'></spring:message>" type="hidden">
<input id="referMsg" value="<spring:message code='referencing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="dereferMsg" value="<spring:message code='referencing.dereferencing' text='Dereferencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">

</body>
</html>