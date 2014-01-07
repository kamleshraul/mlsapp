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
			$("#searchrefvalue").change(function(){				
				start=0;				
				$("#referencingResult").empty();	
				$("#searchTable tbody").empty();		
				$("#referencingDiv").hide();
				previousSearchCount=record;
			});			
			$("#searchReference").click(function(){
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
			/**** Language ****/
			$("#languageAllowed").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			$("#reset").click(function(){					
				$("#refSessionYear").prop('selectedIndex',0);
				$("#refSessionType").prop('selectedIndex',0);
				$("#refSessionYear").css('color','black');
				$("#refSessionType").css('color','black');
				if($('#whichDevice').val()=='bills_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
				}
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
			
			if($("#searchTable > tbody > tr").length==0) {
				$("#referencingDiv").hide();
			}			
			/* $("#refSessionYear").change(function(){
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
			}); */
			
			$("#backToBill").click(function(){
				/* $("#reset").click();
				$('#searchrefvalue').val(""); */
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
		
		function searchExactReference() {
			start=start+10;	
			if(previousSearchCount==record) {
				$.post('refentity/searchexactbill',{bill:$("#billId").val(),language:$("#languageAllowed").val(),billSessionYear:$("#refSessionYear").val(),billSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
					
					if(data.length>0){								
						var text="";
						for(var i=0;i<data.length;i++){									
							text+="<tr>";
							var billNumber = data[i].number;
							if(billNumber==undefined || billNumber=='') {
								billNumber = "";
							}
							if(data[i].deviceType=='act') {
								text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
								+"<br/><a onclick='viewActDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
								+"</td>";
							} else {
								text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
								+"<br/><a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
								+"</td>";
							}							
							if(data[i].revisedTitle!='null' && data[i].revisedTitle!='' && data[i].revisedTitle!=undefined) {
								text+="<td style='width: 300px; max-width: 300px;'>"+data[i].revisedTitle+"</td>";	
							} else if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
								text+="<td style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";	
							} else {
								text+="<td>&nbsp;</td>";
							}
							var content = "";
							if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
								content = data[i].revisedContent;									
							} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
								content = data[i].content;
							} 				
							text+="<td style='width: 420px; max-width: 420px;'>";
							text+="<div id='contentInShortDiv"+i+"' style='height: 200px; max-height: 200px; overflow: hidden;'>";
							text+=content;
							text+="</div>";
							text+="<div style='text-align: right;'>";
							text+="<a href='#' style='text-decoration: none;' onclick='showFullContent("+i+");'>";
							text+="<img src='./resources/images/ViewRevision.jpg' title='<spring:message code='referencing.viewContentInDetail' text='View Entire Details'></spring:message>' class='imageLink' />";
							text+="</a>";
							text+="</div>";
							text+="</td>";
							if(data[i].deviceType=='act') {
								text+="<td><spring:message code='bill.referredAct' text='Act'/></td>";
							} else {
								text+="<td>"+data[i].deviceType+"</td>";
							}								
							text+="<td>"+data[i].sessionYear+"</td>";			
							text+="<td>"+data[i].sessionType+"</td>";
							text+="<td>"+data[i].status+"</td>";
							text+="<td>"+data[i].dateOfBill+"</td>";
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
						//$("#searchresult").hide();
					}				
				});
			} else {
				$("#referencingResult").empty();
				$("#referencingResult").html($("#finishedSearchingMsg").val());
				$("#referencingDiv").show();
			}
		}
		
		function search(){
			var toBeSearched=$("#searchrefvalue").val();
			previousSearchTerm=toBeSearched;			
			
			var currentDeviceType = $("#refDeviceType").val();
			if(toBeSearched!=''){
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){	
					if(/*/^questions_/.test(currentDeviceType)*/currentDeviceType.contains('questions')){
						$.post('refentity/search',{param:$("#searchrefvalue").val(),question:$("#questionId").val(), houseType:$("#houseTypeType").val(), questionSessionYear:$("#refSessionYear").val(),questionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
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
						$.post('refentity/searchresolution',{param:$("#searchrefvalue").val(),resolution:$("#resolutionId").val(), resolutionSessionYear:$("#refSessionYear").val(),resolutionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							
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
						
					}else if(/^bills_/.test(currentDeviceType)){
						$.post('refentity/searchbill',{param:$("#searchrefvalue").val(),bill:$("#billId").val(),language:$("#languageAllowed").val(),billSessionYear:$("#refSessionYear").val(),billSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							
							if(data.length>0){								
								var text="";
								for(var i=0;i<data.length;i++){									
									text+="<tr>";
									var billNumber = data[i].number;
									if(billNumber==undefined || billNumber=='') {
										billNumber = "";
									}
									if(data[i].deviceType=='act') {
										text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<a onclick='viewActDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
									} else {
										text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<br/><a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
									}										
									if(data[i].revisedTitle!='null' && data[i].revisedTitle!='' && data[i].revisedTitle!=undefined) {
										text+="<td style='width: 300px; max-width: 300px;'>"+data[i].revisedTitle+"</td>";	
									} else if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
										text+="<td style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";	
									} else {
										text+="<td>&nbsp;</td>";
									}									
									var content = "";
									if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
										content = data[i].revisedContent;									
									} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
										content = data[i].content;
									} else {
										content = "&nbsp;";
									}				
									text+="<td style='width: 420px; max-width: 420px;'>";
									text+="<div id='contentInShortDiv"+i+"' style='height: 200px; max-height: 200px; overflow: hidden;'>";
									text+=content;
									text+="</div>";
									text+="<div style='text-align: right;'>";
									text+="<a href='#' style='text-decoration: none;' onclick='showFullContent("+i+");'>";
									text+="<img src='./resources/images/ViewRevision.jpg' title='<spring:message code='referencing.viewContentInDetail' text='View Entire Details'></spring:message>' class='imageLink' />";
									text+="</a>";
									text+="</div>";
									text+="</td>";
									if(data[i].deviceType=='act') {
										text+="<td><spring:message code='bill.referredAct' text='Act'/></td>";
									} else {
										text+="<td>"+data[i].deviceType+"</td>";
									}		
									if(data[i].sessionYear!=null) {
										text+="<td>"+data[i].sessionYear+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].sessionType!=null) {
										text+="<td>"+data[i].sessionType+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].status!=null) {
										text+="<td>"+data[i].status+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].dateOfBill!=null) {
										text+="<td>"+data[i].dateOfBill+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}								
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
								//$("#searchresult").hide();
							}				
						});
					}
				}else{
					$("#referencingResult").empty();
					$("#referencingResult").html($("#finishedSearchingMsg").val());
					$("#referencingDiv").show();						
				}	
			}else{
				alert($("#nothingToSearchMsg").val());
				$("#referencingDiv").hide();
			}
		}
		function referencing(referId,isAct){
			var whichDevice= $('#whichDevice').val();
			var device;
			if(isAct=='true') {
				device = "act";
			} else {
				device=$("#refDeviceType").val();
			}
			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
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
		function dereferencing(referId,isAct){
			var whichDevice= $('#whichDevice').val();
			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
			}
			var device;
			if(isAct=='true') {
				device = "act";
			} else {
				device=$("#refDeviceType").val();
			}
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
		
		function showFullContent(indexOfContent) {		
			$.fancybox.open($('#contentInShortDiv'+indexOfContent).html(),{autoSize:false,width:800,height:600});
		}

		function viewDetail(referId){
			var resourceURL="";
			var device=$("#refDeviceType").val();
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#refDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&usergroupType="+$("#usergroupType").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			if(/^questions_/.test(device)){
				resourceURL='question/'+referId+'/edit?'+parameters;
			}else if(/^resolutions_/.test(device)){
				resourceURL='resolution/'+referId+'/edit?'+parameters;
			}else if(/^bills_/.test(device)){
				resourceURL='bill/'+referId+'/edit?'+parameters;
			}
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				if($("#whichDevice").val()=='questions_'){
					$("#viewQuestion").html(data);
					$("#viewQuestionDiv").show();
				}else if($("#whichDevice").val()=='resolutions_'){
					$("#viewResolution").html(data);
					$("#viewResolutionDiv").show();
				}else if($("#whichDevice").val()=='bills_'){
					$("#viewBill").html(data);
					$("#viewBillDiv").show();
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
		
		/****To view Act details****/
		function viewActDetail(referId){
			var resourceURL='act/'+referId+'/edit?edit=false;';			
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				$("#viewBill").html(data);
				$("#viewBillDiv").show();
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
			if($("#whichDevice").val()=='resolutions_'){
				$("#viewResolution").empty();
				$("#viewResolutionDiv").hide();
			}else if($("#whichDevice").val()=='questions_'){
				$("#viewQuestion").empty();
				$("#viewQuestionDiv").hide();
			}else if($("#whichDevice").val()=='bills_'){
				$("#viewBill").empty();
				$("#viewBillDiv").hide();
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
	<c:choose>
	<c:when test="${whichDevice!='bills_'}">
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
	</c:when>
	<c:otherwise>
		<a href="#" class="butSim">
			<spring:message code="bill.language" text="Language"/>
		</a>		
		<select name="languageAllowed" id="languageAllowed" class="sSelect">			
			<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${languagesAllowedForBill}">
				<option value="${i.type}">${i.name}</option>			
			</c:forEach>
		</select>
	</c:otherwise>
	</c:choose>
	<table cellpadding="0px" cellspacing="0px">
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:1px;">
				<input type="text" name="zoom_query" id="searchrefvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="searchReference" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.reset" text="Reset Filters"></spring:message></a>
				<c:if test="${whichDevice=='questions_'}">
					<a href="#" id="backToQuestion" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.back" text="Back"></spring:message></a>
				</c:if>
				<c:if test="${whichDevice=='resolutions_'}">
					<a href="#" id="backToResolution" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.back" text="Back"></spring:message></a>
				</c:if>
				<c:if test="${whichDevice=='bills_'}">
					<a href="#" id="backToBill" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.back" text="Back"></spring:message></a>
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
		<c:when test="${whichDevice=='bills_'}" >
			<label style="color:blue;font-size:14px;">${number}:${title}</label>
		</c:when>
	</c:choose>
	
	<c:choose>
		<c:when test="${whichDevice=='questions_'}">
			<input type="hidden" id="questionId" value="${id }">
		</c:when>
		<c:when test="${whichDevice=='resolutions_'}">
			<input type="hidden" id="resolutionId" value="${id }">
		</c:when>
		<c:when test="${whichDevice=='bills_'}">
			<input type="hidden" id="billId" value="${id }">
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
				<c:if test="${whichDevice=='bills_'}">
					<th><spring:message code="referencing.number" text="Device Number"></spring:message></th>
				</c:if>
				<c:choose>
				<c:when test="${whichDevice!='bills_'}">
					<th><spring:message code="referencing.subject" text="Subject"></spring:message></th>
				</c:when>
				<c:otherwise>
					<th><spring:message code="referencing.title" text="Title"></spring:message></th>
				</c:otherwise>
				</c:choose>
				<c:if test="${whichDevice=='questions_'}">
					<th><spring:message code="referencing.question" text="Question"></spring:message></th>
				</c:if>
				<c:if test="${whichDevice=='resolutions_'}">
					<th><spring:message code="referencing.resolution" text="Resolution"></spring:message></th>
				</c:if>
				<c:if test="${whichDevice=='bills_'}">
					<th><spring:message code="referencing.bill" text="Content Draft"></spring:message></th>
				</c:if>
				<th><spring:message code="referencing.devicetype" text="Device Type"></spring:message></th>
				<c:choose>
				<c:when test="${whichDevice=='bills_'}">
					<th><spring:message code="referencing.year" text="Year"></spring:message></th>					
				</c:when>
				 <c:otherwise>
				 	<th><spring:message code="referencing.sessionyear" text="Session Year"></spring:message></th>
				 </c:otherwise>
				</c:choose>
				
				<th><spring:message code="referencing.sessiontype" text="Session Type"></spring:message></th>
				<th><spring:message code="referencing.status" text="Status"></spring:message></th>
				<c:if test="${whichDevice=='bills_'}">
					<th><spring:message code="referencing.dateOfBill" text="Admission/Rejection"></spring:message></th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:if test="${whichDevice=='bills_'}">
				<c:if test="${not empty exactReferences}">
					<c:forEach var="i" items="${exactReferences}" varStatus="exactReferenceEntry">
						<tr>
							<c:set var="billNumber" value="${i.number}"/>
							<c:if test="${empty i.number}">
								<c:set var="billNumber" value=""/>
							</c:if>
							<td>								
								<c:choose>
									<c:when test="${i.deviceType=='act'}">
										${billNumber}<span id='operation${i.id}'><a onclick='referencing(${i.id},"true");' style='margin:10px;'><spring:message code='referencing.referencing' text='Referencing'/></a></span>
										<a onclick='viewActDetail(${i.id});' style='margin:10px;'><spring:message code='referencing.viewdetail' text='View Detail'/></a>
									</c:when>
									<c:otherwise>
										${billNumber}<span id='operation${i.id}'><a onclick='referencing(${i.id});' style='margin:10px;'><spring:message code='referencing.referencing' text='Referencing'/></a></span>
										<a onclick='viewDetail(${i.id});' style='margin:10px;'><spring:message code='referencing.viewdetail' text='View Detail'/></a>
									</c:otherwise>
								</c:choose>								 
							</td>
							<td style='width: 300px; max-width: 300px;'>
								<c:choose>
									<c:when test="${not empty i.revisedTitle}">${i.revisedTitle}</c:when>
									<c:when test="${not empty i.title}">${i.title}</c:when>
									<c:otherwise>&nbsp;</c:otherwise>
								</c:choose>
							</td>
							<td style='width: 420px; max-width: 420px;'>
								<div id="contentInShortDiv${exactReferenceEntry.count}" style="height: 200px; max-height: 200px; overflow: hidden;">
								<c:choose>
									<c:when test="${not empty i.revisedContent}">${i.revisedContent}</c:when>
									<c:when test="${not empty i.content}">${i.content}</c:when>
									<c:otherwise>&nbsp;</c:otherwise>
								</c:choose>
								</div>
								<div style="text-align: right;">
								<a href="#" style="text-decoration: none;" onclick="showFullContent(${exactReferenceEntry.count});">
									<%-- <spring:message code="referencing.viewContentInDetail" text="<<View Entire Details"></spring:message> --%>
									<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='referencing.viewContentInDetail' text='View Entire Details'></spring:message>" class="imageLink" />
								</a>
								</div>							
							</td>
							<td>
								<c:choose>
									<c:when test="${i.deviceType=='act'}">
										<spring:message code="bill.referredAct" text="Act"/>
									</c:when>
									<c:otherwise>
										${i.deviceType}
									</c:otherwise>
								</c:choose>
							</td>
							<td>${i.sessionYear}</td>
							<td>${i.sessionType}</td>
							<td>${i.status}</td>
							<td>${i.dateOfBill}</td>
						</tr>
						<c:if test="${exactReferenceEntry.count==10}">
							<tr>
								<td style='text-align:center;'>
									<a onclick='searchExactReference();' style='margin:10px;'>
										<spring:message code='referencing.loadmore' text='Show More'/>
									</a>
								</td>
							</tr>
						</c:if>
					</c:forEach>					
				</c:if>
			</c:if>
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
			<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
			<div id="viewResolution">
			</div>
		</div>
	</c:when>
	
	<c:when test="${whichDevice=='bills_'}" >
		<div id="viewBillDiv" style="display:none;">
			<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
			<div id="viewBill">
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
<input id="loadMoreMsg" value="<spring:message code='referencing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='referencing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="refUsergroupType" type="hidden" value="${usergroupType}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>