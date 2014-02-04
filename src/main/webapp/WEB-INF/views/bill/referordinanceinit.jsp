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
				$("#referringOrdinanceResult").empty();	
				$("#searchTable tbody").empty();		
				$("#referringOrdinanceDiv").hide();
				previousSearchCount=record;				
			});			
			$("#search").click(function(){
				search();
			});
			
			$("#refYear").change(function(){								
				if($(this).val()=='-'){
					$(this).css('color','black');				
					$('#ordinanceNumberPromptDiv').hide();
				}else{
					$(this).css('color','blue');
					$('#ordinanceNumberPromptDiv').show();
				}
			});
			
			$('input:radio[name=isOrdinanceNumberKnown]').change(function() {
				if($(this).val()=='yes') {
					$('#ordinanceNumberDiv').show();
					$('#freeTextSearchTable').hide();	
					$('#referringOrdinanceDiv').hide();
				} else {
					$('#ordinanceNumberDiv').hide();
					$('#freeTextSearchTable').show();
					$('#referringOrdinanceDiv').show();
				}				
			});
			
			$('#searchByNumber').click(function() {
				if($('#ordinanceNumber').val()=='') {
					$.prompt($('#ordinanceNumberEmptyPrompt').val());					
				} else if($('#refYear').val()=='-') {
					$.prompt($('#refYearEmptyPrompt').val());
				} else {
					$.get('ref/referOrdinance/searchByNumber?ordYear='+$("#refYear").val()
							+'&ordNumber='+$('#ordinanceNumber').val(),function(data) {
						if(data.id==-1) {
							alert($('#someErrorOccuredPrompt').val());							
						} else if(data.id==0) {
							alert($('#ordinanceNotFoundPrompt').val());							
						} else {
							var text = "<a href='#' onclick='referOrdinance("+data.id+","+data.number+","+data.year+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>";
							$('#referOrdinanceThroughNumberSpan').empty();
							$('#referOrdinanceThroughNumberSpan').html(text);						
						}					
					});
				}
			});
			
			$("#resetFilters").click(function(){					
				$("#refYear").prop('selectedIndex',0);
				$("#refYear").css('color','black');
				$("#referringOrdinanceResult").empty();
				$("#referringOrdinanceResult").html("");
				$("#ordSearchTable tbody").empty();
				$("#referringOrdinanceDiv").hide();							
			});
				
			$("#referringActDiv").hide();
			$("#referringOrdinanceDiv").hide();
			
			$(".backToReferringBill").click(function(){				
				$("#referringActOrdinanceResultDiv").hide();
				
				if($("#billDiv").length>0){
					$("#billDiv").show();
				}else if($("#assistantDiv").length>0){
					$("#assistantDiv").show();
				}
				/**** Hide update success/failure message on coming back to bill ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});						
		});
		
		function search(){
			var toBeSearched=$("#searchvalue").val();
			previousSearchTerm=toBeSearched;		
			
			if(toBeSearched!=''){
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){
					$.post('bill/referOrdinance/search',{param:$("#searchvalue").val(),refYear:$("#refYear").val(),record:record,start:start},function(data){
						if(data.length>0){								
							var text="";
							for(var i=0;i<data.length;i++){									
								text+="<tr>";
								var ordinanceNumber = data[i].number;
								if(ordinanceNumber==undefined || ordinanceNumber=='') {
									ordinanceNumber = "";
								}
								text+="<td>"+ordinanceNumber+"<span id='operation"+data[i].id+"'><a onclick='referOrdinance("+data[i].id+","+ordinanceNumber+","+data[i].year+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
								+"<a onclick='viewOrdinanceDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
								+"</td>";
								if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
									text+="<td>"+data[i].title+"</td>";	
								} else {
									text+="<td>&nbsp;</td>";
								}										
								text+="<td>"+data[i].year+"</td>";								
								text+="</tr>";						
							}	
							if(data.length==10){
								text+="<tr>"
									+"<td style='text-align:center;'><a onclick='search();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
									+"</tr>";
								start=start+10;							
							}
							$("#ordSearchTable tbody").empty();
							$("#ordSearchTable tbody").append(text);	
							$("#referringOrdinanceDiv").show();										
						}else{
							$("#referringOrdinanceResult").empty();
							$("#referringOrdinanceResult").html($("#noResultsMsg").val());
							$("#referringOrdinanceDiv").show();
							//$("#searchresult").hide();
						}				
					});
				}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val()); 
					$("#clubbingDiv").show();						
				}	
			}else{
				alert($("#nothingToSearchMsg").val());
				$("#referringOrdinanceDiv").hide();
			}
		}
		
		/****For refering Ordinance****/
		function referOrdinance(ordinanceId, ordinanceNumber, ordinanceYear){
			if($('input:radio[name=isOrdinanceNumberKnown]').val()!='yes' && $('#referred_link').length) {
				$('#referred_link').text($("#referMsg").val());
			}
			$("#referredOrdinance").val(ordinanceId);
			$('#viewReferredOrdinance').text(ordinanceNumber);
			$('#viewReferredOrdinance').css('text-decoration','underline');
			var yearText = "("+$('#referredOrdinanceYearLabel').val()+": "+ordinanceYear+")";
			$('#referredOrdinanceYear').text(yearText);
			$('#referredOrdinancePara').show();
			$('#referOrdinancePara').hide();
			if($('input:radio[name=isOrdinanceNumberKnown]').val()!='yes') {
				$("#referringOrdinanceResult").empty();
				$("#operation"+ordinanceId).empty();
				$("#operation"+ordinanceId).html("<a id='referred_link' onclick='dereferOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
			} else {
				var text = "<a href='#' onclick='dereferOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>";
				text += "<a href='#' onclick='viewOrdinanceDetail("+ordinanceId+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>";
				$('#referOrdinanceThroughNumberSpan').empty();
				$('#referOrdinanceThroughNumberSpan').html(text);
			}		
		}
		
		/****To derefer Ordinance****/
		function dereferOrdinance(ordinanceId, ordinanceNumber, ordinanceYear){
			$("#referredOrdinance").val("");
			$('#viewReferredOrdinance').text("-");
			$('#referredOrdinanceYear').text("");
			$('#referredOrdinancePara').hide();
			$('#referOrdinancePara').show();
			if($('input:radio[name=isOrdinanceNumberKnown]').val()!='yes') {
				$("#referringOrdinanceResult").empty();
				$("#operation"+ordinanceId).empty();
				$("#operation"+ordinanceId).html("<a onclick='referOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+",2);' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
			} else {
				var text = "<a href='#' onclick='referOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>";
				$('#referOrdinanceThroughNumberSpan').empty();
				$('#referOrdinanceThroughNumberSpan').html(text);
			}		
		}
		
		/****To view Ordinance details****/
		function viewOrdinanceDetail(referId,refType){
			var resourceURL='ordinance/'+referId+'/edit?edit=false;';
			
			$.get(resourceURL,function(data){				
				$("#referringOrdinanceDiv").hide();
				$("#viewResult").html(data);
				$("#viewResultDiv").show();
			},'html');
			
		}
		function back(){
			if($('input:radio[name=isOrdinanceNumberKnown]').val()!='yes') {
				$("#referringOrdinanceDiv").show();
			}					
			$("#referringOrdinanceResult").empty();
			$("#viewResult").empty();
			$("#viewResultDiv").hide();
		}
	</script>
	<style type="text/css">
	#testingview:hover{
		cursor: pointer;
	}
.searchTable{
  border: 0px solid black;
  border-spacing: 0px;
}

.searchTable thead tr{
   font-size: 14px;
}

.searchTable thead tr th{
  border-bottom: 2px solid black;
  border-top: 1px solid black;
  margin: 0px;
  padding: 2px;
  background-color: #cccccc;
}

.searchTable tr {
  font-size:12px;  
}

.searchTable tr.odd {
  background-color: #AAAAAA;
}

.searchTable tr td, th{
  border-bottom: 1px solid black;
  padding: 2px; 
  text-align: center;  
}	
.bold{
font-weight: bold;
}
.expand{
}
.searchTable a{
text-decoration: underline;
color: green;
cursor: hand;
cursor:pointer;
}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${error!=''}">
	<h3 style="color: #FF0000;">${error}</h3>
</c:if>
<div>
	<p>
		<a href="#"><spring:message code="advancedsearch.ordinanceyear" text="Ordinance Year"/></a>	
		<select name="refYear" id="refYear" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${years}">
				<option value="${i.number}"><c:out value="${i.name}"></c:out></option>
			</c:forEach> 
		</select>
		<div id="ordinanceNumberPromptDiv" style="display: none;">
			<spring:message code="advancedsearch.ordinancenumberprompt" text="Do you know ordinance number?"/>
			<input class="isOrdinanceNumberKnown" type="radio" name="isOrdinanceNumberKnown" value="yes" style="width: 20px;"><spring:message code="generic.yes" text="Yes"/>
			&nbsp;&nbsp;
			<input class="isOrdinanceNumberKnown" type="radio" name="isOrdinanceNumberKnown" value="no" style="width: 20px;"><spring:message code="generic.no" text="No"/>
		</div>				
	</p>	
	<table id="freeTextSearchTable" cellpadding="0px" cellspacing="0px">
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:1px;">
				<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="resetFilters" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referringAct.resetFilters" text="Reset Filters"></spring:message></a>				
				<a href="#" class="backToReferringBill" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referringOrdinance.back" text="Back"></spring:message></a>
			</td>
		</tr>
	</table>
	<div id="ordinanceNumberDiv" style="display: none;">
		<spring:message code="advancedsearch.ordinancenumber" text="Enter Ordinance Number"/>:&nbsp;
		<input type="text" id="ordinanceNumber" name="ordinanceNumber"/>
		<input type="button" id="searchByNumber" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">		
		<span id="referOrdinanceThroughNumberSpan"></span>
		<a href="#" class="backToReferringBill" style="margin-left: 20px;margin-right: 10px;"><spring:message code="referringOrdinance.back" text="Back"></spring:message></a>
	</div>		
</div>

<div id="referringOrdinanceDiv" style="display: none;">
	<div id="referringOrdinanceResult" style="margin: 10px;">
	</div>
	
	<div id="ordSearchresult">
		<table  id="ordSearchTable" class="searchTable">
		<thead>
			<tr>
				<th><spring:message code="referringOrdinance.number" text="Ordinance Number"></spring:message></th>
				<th><spring:message code="referringOrdinance.${defaultTitleLanguage}title" text="Ordinance ${defaultTitleLanguage} Title"></spring:message></th>
				<th><spring:message code="referringOrdinance.year" text="Ordinance Year"></spring:message></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
	</div>
</div>

<div id="viewResultDiv" style="display:none;">
	<a id="backToSearch" href="#" style="display:block;"><spring:message code="referringOrdinance.back" text="Back to search page"></spring:message></a>
	<div id="viewResult">
	</div>
</div>
<br />
<br />
<input id="nothingToSearchMsg" value="<spring:message code='referringOrdinance.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='referringOrdinance.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='referringOrdinance.viewdetail' text='View Detail'></spring:message>" type="hidden">
<input id="referMsg" value="<spring:message code='referringOrdinance.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="dereferMsg" value="<spring:message code='referringOrdinance.dereferencing' text='Dereferencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='referringOrdinance.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='referringOrdinance.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input type="hidden" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select' />" />
<input id="refUsergroupType" type="hidden" value="${usergroupType}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="actionBillType" value="${action}" />
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="NoOrdinanceSelectedMsg" value="<spring:message code='client.bill.noOrdinanceSelected' text='No ordinance selected' />" />
<input type="hidden" id="refYearEmptyPrompt" value="<spring:message code='referringOrdinance.refYearEmptyPrompt' text='Please Select Year of Ordinance' />" />
<input type="hidden" id="ordinanceNumberEmptyPrompt" value="<spring:message code='referringOrdinance.ordinanceNumberEmptyPrompt' text='Please Select Number of Ordinance' />" />
<input type="hidden" id="someErrorOccuredPrompt" value="<spring:message code='referringOrdinance.someErrorOccuredPrompt' text='Some error occured. If data is correct, Please contact administrator.' />" />
<input type="hidden" id="ordinanceNotFoundPrompt" value="<spring:message code='referringOrdinance.ordinanceNotFoundPrompt' text='Ordinance Not Found.' />" />
</body>
</html>