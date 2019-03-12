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
			/*** Module Filter ****/
			$("#requiredModule").change(function(){
				if($(this).val()=='-'){
					$("#committeeCriteria").css("display","none");
					$("#sessionCriteria").css("display","inline-block");
					$("#requiredCommitteeName").empty();
					$("#requiredCommitteeName").html(text);	
					$("#requiredCommitteeName").css("color","");								
					$("#requiredCommitteeMeeting").empty();
					$("#requiredCommitteeMeeting").html(text);
					$("#requiredCommitteeMeeting").css("color","");	
				}else{
					$("#committeeCriteria").css("display","inline-block");
					$("#sessionCriteria").css("display","none");
					$("#requiredHouseType").val("-");
					$("#requiredHouseType").css("color","");	
					$("#requiredSessionYear").val("-");
					$("#requiredSessionYear").css("color","");				
					$("#requiredSessionType").val("-");
					$("#requiredSessionType").css("color","");	
				}
			});	
			/**** Remove hr from embeddd table ****/
			$("#searchTable td > table hr:last").remove();
			/**** Reset Filters ****/
			$("#reset").click(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";	
				$("#requiredHouseType").val("-");
				$("#requiredHouseType").css("color","");	
				$("#requiredSessionYear").val("-");
				$("#requiredSessionYear").css("color","");				
				$("#requiredSessionType").val("-");
				$("#requiredSessionType").css("color","");								
				$("#requiredCommitteeName").empty();
				$("#requiredCommitteeName").html(text);	
				$("#requiredCommitteeName").css("color","");								
				$("#requiredCommitteeMeeting").empty();
				$("#requiredCommitteeMeeting").html(text);
				$("#requiredCommitteeMeeting").css("color","");				
	
			});
			/**** Filters ****/
			/**** House Type ****/
			$("#requiredHouseType").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#requiredSessionType").val();
				var year=$("#requiredSessionYear").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Session Year ****/
			$("#requiredSessionYear").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#requiredSessionType").val();
				var houseType=$("#requiredHouseType").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Session Type ****/
			$("#requiredSessionType").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#requiredSessionYear").val();
				var houseType=$("#requiredHouseType").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			
			$("#requiredCommitteeName").change(function(){
				$.get('ref/committeemeeting?committeeNameId='+$(this).val(),function(data){
					if(data.length>0){
						var text="<option value=''>"+$("#pleaseSelect").val()+"</option>";
						for( var i=0;i<data.length;i++){
							text+="<option title='"+data[i].value+"' value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$("#requiredCommitteeMeeting").empty();
						$("#requiredCommitteeMeeting").html(text);
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			});
			
	
			/**** Search Content Changes ****/
			$("#searchvalue").change(function(){
				start=0;				
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
		});
		
		/**** On clicking search button ****/		
		function search(fresh){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			/**** Constructing data to be sent in post request ****/
			if(fresh=='YES'){
				start=0;
				$("#searchTable tbody").empty();				
			}
			
			var resourceURL = "";
			var postData = {param:$("#searchvalue").val(),record:record,start:start,module:$("#requiredModule").val()};
							
			if($("#requiredHouseType").length>0){
				postData['houseType']=$("#requiredHouseType").val();
			}
			if($("#requiredSessionYear").length>0){
				postData['sessionYear']=$("#requiredSessionYear").val();
			}
			if($("#requiredSessionType").length>0){
				postData['sessionType']=$("#requiredSessionType").val();
			}
			if($("#requiredCommitteeName").length>0){
				postData['committeeName']=$("#requiredCommitteeName").val();
			}
			if($("#requiredCommitteeMeeting").length>0){
				postData['committeeMeeting']=$("#requiredCommitteeMeeting").val();
			}
			
			resourceURL = "proceedingsearch/searchfacility";
			 
			var toBeSearched=$("#searchvalue").val();			
			//previousSearchTerm=toBeSearched;
			/**** Search Input Box is not empty ****/
			if(toBeSearched!=''){
				/**** Search takes place if its a new search or while loading more data in current search ****/
				//if((previousSearchCount==record)){
				$.post(resourceURL,postData,function(data){
					/**** previousSearchCount controls if clicking search button next time with same content
					 will call search function.It will only if this time no. of entries returned is
					 equal to max no of records in each search call=record****/
					previousSearchCount=data.length;
					if(data.length>0){
					var text="";	
						for(var i=0;i<data.length;i++){
							var textTemp="";
							textTemp=textTemp+"<tr>"+
									"<td class='expand' style='width: 150px; max-width: 150px;'>"+
									"<span id='part"+data[i].id+"'>"+
										data[i].formattedPrimaryMember+"</span>"
									+"<br>";
							if($("#requiredModule").val()=='-'){
								textTemp = textTemp +  data[i].subject +" " +  data[i].sessionType +" "+ data[i].sessionYear +"<br>"
							}else{
								textTemp = textTemp +  data[i].ministry +"<br>"
							}
							
							textTemp = textTemp	+ $("#rosterDateValue").val() + " : " + data[i].chartAnsweringDate + "<br>"
										+ $("#slotDateValue").val() + " : "+  data[i].subDepartment + " - " + data[i].statusType +"<br>"
										+  data[i].actor +"<br>";
							if(data[i].number!=null && data[i].number!=""){
								textTemp = textTemp + $("#registerNumberValue").val() + " :" + data[i].number;
							}
							textTemp = textTemp + "</td>"
											
																
							textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>" + data[i].noticeContent
								+"<br/>";
							textTemp+="</td></tr>";								
							text+=textTemp;
						}	
						if(data.length==10){
							text+="<tr>"
								+"<td style='text-align:center;'><span class='clearLoadMore'><a onclick='loadMore();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></span></td>"
								+"</tr>";
							start=start+10;							
						}
												
						$("#searchTable > #searchresultbody:last").append(text);	
						$("#searchresult").show();							
						$("#clubbingDiv").show();
						$.unblockUI();													
					}else{
						$("#searchTable tbody").empty();							
						$("#clubbingDiv").show();
						$("#searchresult").hide();						
						$.unblockUI();										
					}								
				}).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});		
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
				
		
</script>

<style type="text/css">

.filterSelected{
color:blue;
}
.highlightedSearchPattern{
font-weight: bold;
text-decoration: underline;
}
td>table{
	width: 350px;
}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent" id="advancedSearch">
	<div id='moduleFilter' style='display:inline;'>
		<a href="#" id="moduletypeLabel" class="butSim">
			<spring:message code="mytask.module" text="Module"/>
		</a>
		<select name="requiredModule" id="requiredModule" style="width:100px;height: 25px;">
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
		</select> |
	</div>	

	<div id="sessionCriteria" style="display:inline;">
		<a href="#" class="butSim">
			<spring:message code="advancedsearch.requiredHouseType" text="House Type"/>
		</a>		
		<select name="requiredHouseType" id="requiredHouseType" style="width:100px;height: 25px;">			
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${houseTypes}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>						
			</c:forEach>
		</select> |
		<a href="#" class="butSim">
			<spring:message code="advancedsearch.sessionyear" text="Year"/>
		</a>			
		<select name="requiredSessionYear" id="requiredSessionYear" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${years}">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach> 
		</select> |			
		<a href="#" class="butSim">
			<spring:message code="advancedsearch.sessionType" text="Session Type"/>
		</a>			
		<select name="requiredSessionType" id="requiredSessionType" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${sessionTypes}" var="i">
				<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
			</c:forEach> 
		</select> |		
	</div>
	<div id="committeeCriteria" style="display:none;">
		<a href="#" class="butSim">
			<spring:message code="advancedsearch.committee" text="Committee"/>
		</a>			
		<select name="requiredCommitteeName" id="requiredCommitteeName" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${committeeDetails}" var="i">			
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
		</select> |			
		
		<a href="#" class="butSim">
			<spring:message code="advancedsearch.committeeMeeting" text="Committee Meeting"/>
		</a>	
		<select name="requiredCommitteeMeeting" id="requiredCommitteeMeeting" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		</select> 
	</div>
</div>	
<hr>
<div id="searchBoxDiv">
	<table style="padding: 0px; margin: 0px;"> 
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:2px;">
				<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.reset" text="Reset Filters"></spring:message></a>
			</td>
		</tr>
	</table>
</div>


<div id="clubbingDiv">

	
	<div id="searchresult" style="display:none; width: 910px; border: 2px solid; margin: 5px;">
		<table  id="searchTable" style="width: 100%;" class="strippedTable">
		<thead>
		<tr>
		<th class="expand"><spring:message code="roster.rosterDetails" text="Roster Details"></spring:message></th>
		<th class="expand"><spring:message code="roster.proceeding" text="Proceeding"></spring:message></th>
		</tr>
		</thead>
		<tbody id="searchresultbody">
		</tbody>
		</table>
	</div>

</div>

</div>
</div>

<input id="nothingToSearchMsg" value="<spring:message code='clubbing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='clubbing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id="registerNumberValue" value="<spring:message code='roster.registerNumber' text='Register Number'/>" />
<input type="hidden" id="rosterDateValue" value="<spring:message code='roster.rosterDate' text='Roster Date'/>">
<input id="slotDateValue" value="<spring:message code='roster.slotTime' text='Slot Time'/>" type="hidden">
</body>
</html>