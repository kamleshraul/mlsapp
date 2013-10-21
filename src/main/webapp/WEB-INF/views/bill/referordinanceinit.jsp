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
				$("#referringActResult").empty();	
				$("#searchTable tbody").empty();		
				$("#referringActDiv").hide();
				previousSearchCount=record;
			});			
			$("#search").click(function(){
				search();
			});
			
			$("#refYear").change(function(){
				var billTypeAction = $("#actionBillType").val();
				
				if($(this).val()=='-'){
					$(this).css('color','black');
				
					if(billTypeAction!=''){
						if(billTypeAction=='ordinance'){
							/****Reset Ordinance Number ****/
							$("#refOrdNumber").css('color','black');
							$("#refOrdNumber").empty();						
							$("#refOrdNumber").html("<option value='-'>" + $("#pleaseSelect").val() + "</option>");
							$("#referToOrdinance").hide();
						}
					}
				}else{
					$(this).css('color','blue');
					if(billTypeAction!=''){
						if(billTypeAction=='ordinance'){
							loadOrdinance($(this).val());
						}
					}
				}
			});
			
			$("#resetFilters").click(function(){					
				
				/****For Act reference***/
				$("#refYear").prop('selectedIndex',0);
				$("#refYear").css('color','black');
				$("#referringActResult").empty();
				$("#referringActResult").html("");
				$("#searchTable tbody").empty();
				$("#referringActDiv").hide();
				
				/****For ordinance reference****/
				$("#refYear").prop('selectedIndex',0);
				$("#refYear").css('color','black');
				$("#refOrdNumber").prop('selectedIndex',0);
				$("#refOrdNumber").css('color','black');
				$("#referringOrdinanceResult").empty();
				$("#referringOrdinanceResult").html("");
				$("#ordSearchTable tbody").empty();
				$("#referringOrdinanceDiv").hide();
							
			});
				
			$("#referringActDiv").hide();
			$("#referringOrdinanceDiv").hide();
			$("#referToOrdinance").hide();
			
			$("#backToReferringBill").click(function(){				
				$("#referringActResultDiv").hide();
				$("#referringOrdinanceResultDiv").hide();
				
				if($("#billDiv").length>0){
					$("#billDiv").show();
				}else if($("#assistantDiv").length>0){
					$("#assistantDiv").show();
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			$("#refOrdNumber").change(function(){
				if($(this).val()!=''){
					if($(this).val() !='-'){
						if($("#viewq").is(":checked")){
							var resourceURL = "ordinance/"+$(this).val()+"/edit?edit=false";
							$.get(resourceURL,function(data){
								$.unblockUI();
								$.fancybox.open(data,{autoSize:false,width:800,height:700});
							},'html');
						}
					}
				}
			});
			
			$("#referToOrdinance").click(function(){
				//alert($("#refOrdNumber option:selected").val() + $("#refOrdNumber option:selected").text());
				
				var ordId = $("#refOrdNumber option:selected").val();
				var ordNumber = $("#refOrdNumber option:selected").text();
				var ordYear = $("#refYear option:selected").val();
								
				if(ordId !='-'){
					referOrdinance(ordId, ordNumber, ordYear, 1);	
				}else{
					$.prompt($("#NoOrdinanceSelectedMsg").val());
				}
				
			});
		});
		
		function loadOrdinance(year){
			resourceURL='ref/ordinance/'+year+'/getOrdinancesForYear';
			$.get(resourceURL,function(data){
				if(data){
					if(data.length > 0){
						var i;
						var text="<option value='-'>" + $("#pleaseSelect").val() + "</option>";						
						for(i = 0; i < data.length; i++){
							text += "<option value='" + data[i].id + "'>" + data[i].value + "</option>";
						}
						
						$("#refOrdNumber").empty();						
						$("#refOrdNumber").html(text);
						$("#refOrdNumber").css('color','#669900');
						
						$("#referToOrdinance").show();
						
					}else{
						$("#refOrdNumber").empty();						
						$("#refOrdNumber").html("<option value='-'>" + $("#pleaseSelect").val() + "</option>");
						$("#refOrdNumber").css('color','black');
						$("#referToOrdinance").hide();
					}
				}else{				
					$("#refOrdNumber").empty();						
					$("#refOrdNumber").html("<option value='-'>" + $("#pleaseSelect").val() + "</option>");
					$("#refOrdNumber").css('color','#000000');
					$("#referToOrdinance").hide();
				}
			},'json').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		function search(){
			var refType = $("#actionBillType").val();
			var toBeSearched=$("#searchvalue").val();
			previousSearchTerm=toBeSearched;		
			
			if(toBeSearched!=''){
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){
					if(refType=='act'){
						$.post('bill/referAct/search',{param:$("#searchvalue").val(),refYear:$("#refYear").val(),record:record,start:start},function(data){
							if(data.length>0){								
								var text="";
								for(var i=0;i<data.length;i++){									
									text+="<tr>";
									var actNumber = data[i].number;
									if(actNumber==undefined || actNumber=='') {
										actNumber = "";
									}
									text+="<td>"+actNumber+"<span id='operation"+data[i].id+"'><a onclick='referAct("+data[i].id+","+actNumber+","+data[i].year+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
									+"<a onclick='viewActDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
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
								$("#searchTable tbody").empty();
								$("#searchTable tbody").append(text);	
								$("#referringActDiv").show();										
							}else{
								$("#referringActResult").empty();
								$("#referringActResult").html($("#noResultsMsg").val());
								$("#referringActDiv").show();
								//$("#searchresult").hide();
							}				
						});
					}else if(refType=='ordinance'){
						$.post('bill/referOrdinance/search',{param:$("#searchvalue").val(),refYear:$("#refYear").val(),record:record,start:start},function(data){
							if(data.length>0){								
								var text="";
								for(var i=0;i<data.length;i++){									
									text+="<tr>";
									var ordinanceNumber = data[i].number;
									if(ordinanceNumber==undefined || ordinanceNumber=='') {
										ordinanceNumber = "";
									}
									text+="<td>"+ordinanceNumber+"<span id='operation"+data[i].id+"'><a onclick='referOrdinance("+data[i].id+","+ordinanceNumber+","+data[i].year+", 2);' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
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
					}
				}else{
					$("#clubbingResult").empty();
					$("#clubbingResult").html($("#finishedSearchingMsg").val());
					$("#clubbingDiv").show();						
				}	
			}else{
				alert($("#nothingToSearchMsg").val());
				$("#referringActDiv").hide();
			}
		}
		
		/****For refering Act****/
		function referAct(actId, actNumber, actYear){
			if($('#referred_link').length) {
				$('#referred_link').text($("#referMsg").val());
			}	
			$("#referredAct").val(actId);
			$('#viewReferredAct').text(actNumber);
			$('#viewReferredAct').css('text-decoration','underline');
			var yearText = "("+$('#referredActYearLabel').val()+": "+actYear+")";
			$('#referredActYear').text(yearText);			
			$("#referringActResult").empty();
			$("#referringActResult").html("SUCCESS");
			$("#operation"+actId).empty();
			$("#operation"+actId).html("<a id='referred_link' onclick='dereferAct("+actId+","+actNumber+","+actYear+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
		}
		
		/****For refering Ordinance****/
		function referOrdinance(ordinanceId, ordinanceNumber, ordinanceYear,whichLabel){
			if($('#referred_link').length) {
				$('#referred_link').text($("#referMsg").val());
			}
			$("#referredOrdinance").val(ordinanceId);
			$('#viewReferredOrdinance').text(ordinanceNumber);
			$('#viewReferredOrdinance').css('text-decoration','underline');
			var yearText = "("+$('#referredOrdinanceYearLabel').val()+": "+ordinanceYear+")";
			$('#referredOrdinanceYear').text(yearText);			
			$("#referringOrdinanceResult").empty();
			if(whichLabel == 1){
				$("#ordinanceRefered").html("SUCCESS");
			}else if(whichLabel==2){
				$("#referringOrdinanceResult").html("SUCCESS");
			}
			$("#operation"+ordinanceId).empty();
			$("#operation"+ordinanceId).html("<a id='referred_link' onclick='dereferOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
		}
		
		/****To derefer Act****/
		function dereferAct(actId, actNumber, actYear){
			$("#referredAct").val("");
			$('#viewReferredAct').text("-");
			$('#referredActYear').text("");
			$("#referringActResult").empty();
			$("#referringActResult").html("SUCCESS");
			$("#operation"+actId).empty();
			$("#operation"+actId).html("<a onclick='referAct("+actId+","+actNumber+","+actYear+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
		}
		
		/****To derefer Ordinance****/
		function dereferOrdinance(ordinanceId, ordinanceNumber, ordinanceYear,whichLabel){
			$("#referredOrdinance").val("");
			$('#viewReferredOrdinance').text("-");
			$('#referredOrdinanceYear').text("");
			$("#referringOrdinanceResult").empty();
			if(whichLabel == 1){
				$("#ordinanceRefered").html("SUCCESS");
			}else if(whichLabel==2){
				$("#referringOrdinanceResult").html("SUCCESS");
			}
			$("#operation"+ordinanceId).empty();
			$("#operation"+ordinanceId).html("<a onclick='referOrdinance("+ordinanceId+","+ordinanceNumber+","+ordinanceYear+",2);' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
		}
		
		/****To view Act details****/
		function viewActDetail(referId,refType){
			var resourceURL='act/'+referId+'/edit?edit=false;';		
			
			$.get(resourceURL,function(data){
				$("#referringActDiv").hide();		
				$("#viewResult").html(data);
				$("#viewResultDiv").show();
			},'html');
			
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
			var actionBillType = $("#actionBillType").val();
			if(actionBillType=='act'){
				$("#referringActDiv").show();		
				$("#referringActResult").empty();
				$("#viewResult").empty();
				$("#viewResultDiv").hide();
			}else if(actionBillType=='ordinance'){
				$("#referringOrdinanceDiv").show();		
				$("#referringOrdinanceResult").empty();
				$("#viewResult").empty();
				$("#viewResultDiv").hide();
			}
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
<div id="searchBoxDiv">
	<p>
		<a href="#">
			<c:if test="${action=='act'}" >
				<spring:message code="advancedsearch.actyear" text="Act Year"/>
			</c:if>
			<c:if test="${action=='ordinance'}" >
				<spring:message code="advancedsearch.ordinanceyear" text="Ordinance Year"/>
			</c:if>
		</a>	
		<select name="actYear" id="refYear" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${years}">
				<option value="${i.number}"><c:out value="${i.name}"></c:out></option>
			</c:forEach> 
		</select>
		<c:if test="${action=='ordinance'}">
			| 
			<a href="#">
				<spring:message code="advancedsearch.actordinancenumber" text="Ordinance"/>
			</a>	
			<select name="ordNumber" id="refOrdNumber" style="width:100px;height: 25px;">				
				<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
				<c:forEach var="i" items="${ordinances}">
					<option value="${i.id}"><c:out value="${i.value}"></c:out></option>
				</c:forEach> 
			</select>
			<input type="checkbox" id="viewq" class="sCheck">&nbsp;<label for="viewq"><spring:message code="client.viewdevice" text="View Ordinance"/></label>
			|
			<a href="#" id="referToOrdinance">
				<spring:message code="advancedsearch.refer" text="Refer Ordinance"/>
			</a>
			&nbsp;
			<span id="ordinanceRefered" style="color: #669900; font-weight: bold;"></span>
		</c:if>		
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
				<a href="#" id="resetFilters" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referringAct.resetFilters" text="Reset Filters"></spring:message></a>
				<a href="#" id="backToReferringBill" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referringAct.back" text="Back"></spring:message></a>
			</td>
		</tr>
	</table>
</div>

<div id="referringActDiv">
	<div id="refActResult"></div>
	<div id="referringActResult" style="margin: 10px;">
	</div>
	
	<div id="searchresult">
		<table  id="searchTable" class="searchTable">
		<thead>
			<tr>
				<th><spring:message code="referringAct.number" text="Act Number"></spring:message></th>
				<th><spring:message code="referringAct.${actDefaultLanguage}title" text="Act ${actDefaultLanguage} Title"></spring:message></th>
				<th><spring:message code="referringAct.year" text="Act Year"></spring:message></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
	</div>
</div>

<div id="referringOrdinanceDiv">
	<div id="refOrdinanceResult"></div>
	<div id="referringOrdinanceResult" style="margin: 10px;">
	</div>
	
	<div id="ordSearchresult">
		<table  id="ordSearchTable" class="searchTable">
		<thead>
			<tr>
				<th><spring:message code="referringOrdinance.number" text="Ordinance Number"></spring:message></th>
				<th><spring:message code="referringOrdinance.${actDefaultLanguage}title" text="Ordinance ${ordinanceDefaultLanguage} Title"></spring:message></th>
				<th><spring:message code="referringOrdinance.year" text="Ordinance Year"></spring:message></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
	</div>
</div>

<div id="viewResultDiv" style="display:none;">
	<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
	<div id="viewResult">
	</div>
</div>
<br />
<br />
<input id="nothingToSearchMsg" value="<spring:message code='referencing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='referencing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='referencing.viewdetail' text='View Detail'></spring:message>" type="hidden">
<input id="referMsg" value="<spring:message code='referencing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="dereferMsg" value="<spring:message code='referencing.dereferencing' text='Dereferencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input type="hidden" id="pleaseSelect" value="<spring:message code='please.select' text='Please Select' />" />
<input id="refUsergroupType" type="hidden" value="${usergroupType}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="actionBillType" value="${action}" />
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input type="hidden" id="actDefaultLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="NoOrdinanceSelectedMsg" value="<spring:message code='client.bill.noOrdinanceSelected' text='No ordinance selected' />" />
</body>
</html>