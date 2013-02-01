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
			/**** primary Question's Details ****/
			$("#primary").click(function(){
				viewDetail($("#questionId").val());
			});
			/**** Back To Search Page****/
			$("#backToSearch").click(function(){
					back();
			});
			/**** Advanced Search Filters ****/
			$("#advancedSearch").click(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
				var params="id="+$("#questionId").val();
				$.get('clubentity/advancedsearch?'+params,function(data){
					$.unblockUI();					
					$.fancybox.open(data,{autoSize:false,width:400,height:500});
				});
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
			search("");
			});
			/**** On Page Load ****/
			$("#clubbingDiv").hide();
	});
		/**** On clicking search button ****/		
		function search(filters){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			/**** Constructing data to be sent in post request ****/
			var postData={param:$("#searchvalue").val(),question:$("#questionId").val(),record:record,start:start};
			if(filters!=undefined ||filters!=""){
				var filterArr=filters.split(",");
				for(var i=0;i<filterArr.length;i++){
					var item=filterArr[i].split("=");
					postData[item[0]]=item[1];
				}
			}
			var toBeSearched=$("#searchvalue").val();
			previousSearchTerm=toBeSearched;
			/**** Search Input Box is not empty ****/
			if(toBeSearched!=''){
				/**** Search takes place if its a new search or while loading more data in current search ****/
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){
				$.post('clubentity/search',postData,function(data){
					/**** previousSearchCount controls if clicking search button next time with same content
					 will call search function.It will only if this time no. of entries returned is
					 equal to max no of records in each search call=record****/
					previousSearchCount=data.length;
					if(data.length>0){
					var text="";	
						for(var i=0;i<data.length;i++){
						text+="<tr>";
						text+="<td class='expand'><span id='number"+data[i].id+"'>"+data[i].number+"</span><span id='operation"+data[i].id+"'>"+
						"<a onclick='clubbing("+data[i].id+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a></span>"
						+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
						+"</td>";
						text+="<td class='expand'>"+data[i].subject+"</td>";			
						text+="<td class='expand'>"+data[i].questionText
						+"<br/>"
						+data[i].deviceType+"   "+data[i].sessionYear+"  "
						+data[i].sessionType+"  "+data[i].status
						+"</td>";					
						text+="</tr>";	
						}	
						if(data.length==10){
							text+="<tr>"
								+"<td style='text-align:center;'><a onclick='search();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
								+"</tr>";
							start=start+10;							
						}
						$("#searchTable tbody").append(text);	
						$("#clubbingDiv").show();
						$.unblockUI();													
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html($("#noResultsMsg").val());
						$("#clubbingDiv").show();
						$.unblockUI();										
					}								
				});		
				}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val());
					$("#clubbingDiv").show();	
					$.unblockUI();									
				}
			}else{
				$.prompt($("#nothingToSearchMsg").val());
				$("#clubbingDiv").hide();	
				$.unblockUI();				
			}
		}
		/**** On Clubbing ****/
		function clubbing(clubId){
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
					}else if(data=='BEINGPROCESSED_QUESTION_ALREADY_CLUBBED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+questionNumber+" is already a clubbed question. ";
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
			return false;
		}
		/**** On unclubbing ****/
		function unclubbing(clubId){
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
<a id="advancedSearch" href="#"><spring:message code="clubbing.advancedsearch" text="Advanced Search"></spring:message></a>
<hr>
<p id="advancedSearchDiv">

</p>

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
<tbody>
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
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">

</body>
</html>