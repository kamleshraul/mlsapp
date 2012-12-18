<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript" src="./resources/js/jquery.shorten.1.0.js?v=1" ></script>
	<script type="text/javascript">
	var searchResultCount=0;
	var previousResultCount=0;
	var noOfRecords=10;	
		$(document).ready(function() {	
			$("#searchvalue").change(function(){
				searchResultCount=0;
				previousResultCount=0;
			});			
			$("#search").click(function(){
				//'ref/question/search?param='+$("#searchvalue").val()+"&question="+$("#questionId").val()+"&record="+noOfRecords+"&start="+previousResultCount
				var toBeSearched=$("#searchvalue").val();
				if(previousResultCount==0 || previousResultCount>=noOfRecords){
				$.post('ref/question/search',{param:$("#searchvalue").val(),question:$("#questionId").val(),record:noOfRecords,start:previousResultCount},function(data){
				if(data.length>0){
					var text="";	
					var clubbedQuestionsIds=$("#clubbedQuestionsIds").val();
					text+="Search returned "+data.length+" questions";				
					for(var i=0;i<data.length;i++){			
						if(data[i].revisedSubject==null||data[i].revisedSubject==""){
							var tempText=data[i].subject;
							var highlightedText=highlightText(tempText,toBeSearched);							
							text+="<h4><a id='resultsubject"+data[i].id+"'  style='font-size:16px;color:blue;text-decoration:underline;'>"+highlightedText+"</a></h4>";
						}else{
							var tempText=data[i].revisedSubject;
							var highlightedText=highlightText(tempText,toBeSearched);							
							text+="<h4><a id='resultsubject"+data[i].id+"' style='font-size:16px;color:blue;text-decoration:underline;'>"+highlightedText+"</a></h4>";
						}
						if(data[i].answeringDate==''){
							text+="<h6 style='font-size:12px;color:green;'>"+data[i].number+","+$("#groupMsg").val()+"-"+data[i].group+","+data[i].answeringDate+","+data[i].primaryMember;
						}else{
							text+="<h6 style='font-size:12px;color:green;'>"+data[i].number+","+$("#groupMsg").val()+"-"+data[i].group+","+data[i].primaryMember;							
						}
						if(clubbedQuestionsIds.indexOf(data[i].id)!=-1){
							text+="<a id='doClubbing"+data[i].id+"' href='#' onclick='clubbing("+data[i].id+");' style='margin-right:10px;margin-left:10px;display:none;'>Club</a>";
							text+="<a id='doUnclubbing"+data[i].id+"' href='#' onclick='unclubbing("+data[i].id+");' style='margin-right:10px;margin-left:10px;'>Unclub</a>";
							
						}else{
							text+="<a id='doClubbing"+data[i].id+"' href='#' onclick='clubbing("+data[i].id+");' style='margin-right:10px;margin-left:10px;'>Club</a>";
							text+="<a id='doUnclubbing"+data[i].id+"' href='#' onclick='unclubbing("+data[i].id+");' style='margin-right:10px;margin-left:10px;display:none;'>Unclub</a>";
							
						}
						text+="</h6>";
						text+="<h6 style='font-size:12px;color:green;'>"+data[i].ministry;
						if(data[i].department!=''){
							text+=","+data[i].department;
						}
						if(data[i].subDepartment!=''){
							text+=","+data[i].subDepartment;
						}
						if(data[i].status!=''){
							text+=","+data[i].status;
						}		
						text+="</h6>";						
						if(data[i].revisedQuestionText==null||data[i].revisedQuestionText==""){	
							var tempText=data[i].questionText;
							var newtext=shortenText(tempText,searchResultCount);
							var highlightedText=highlightText(newtext,toBeSearched);							
							text+="<h5 style='font-size:13px;'>"+highlightedText+"</h5>";
						}else{
							var tempText=data[i].revisedQuestionText;
							var newtext=shortenText(tempText,searchResultCount);
							var highlightedText=highlightText(newtext,toBeSearched);							
							text+="<h5 style='font-size:13px;'>"+highlightedText+"</h5>";
						}
						text+="<hr>";	
						searchResultCount++;					
					}	
					previousResultCount=searchResultCount;				
					$("#searchresult").empty();
					$("#searchresult").html(text);
				}else{
					$("#searchresult").empty();					
					$("#searchresult").html($("#noresult").val());
				}	
				});
				}
				return false;				
			});
		});
		function clubbing(clubId,clubNumber){
			var questionId=$("#questionId").val();
			$.post('question/clubbing?pId='+questionId+"&cId="+clubId,function(data){
					$("#clubbingResult").empty();
					$("#clubbingResult").html(data);
					$("#doUnclubbing"+clubId).show();
					$("#doClubbing"+clubId).hide();				
			},'html');
			return false;
		}
		function unclubbing(clubId,clubNumber){
			var questionId=$("#questionId").val();
			$.post('question/unclubbing?pId='+questionId+"&cId="+clubId,function(data){
					$("#clubbingResult").empty();
					$("#clubbingResult").html(data);
					$("#doUnclubbing"+clubId).hide();
					$("#doClubbing"+clubId).show();					
			},'html');
			return false;
		}	
		function shortenText(content,i){
			var showChars=100;
			var ellipsesText="...";
			var moreText="show";			
			showChars=findNoOfCharsToShort(content,showChars);	
		    if (content.length > showChars) {
		      var visibleContent = content.substr(0, showChars);
		      var hiddenContent = content.substr(showChars , content.length - showChars);
		      var html = visibleContent + '<span id="moreellipses'+i+'">' + ellipsesText + '&nbsp;</span><span id="morecontent'+i+'" style="display:none;">' + hiddenContent + '</span>&nbsp;&nbsp;<a href="#" id="qt'+i+'" onclick="toggleText(this.id);">' + moreText + '</a>';
		      return html;
		    }
		}

		function toggleText(id){
		var index=id.split("qt")[1];
		var moreText="show";
		var lessText="hide";			
		$("#moreellipses"+index).toggle();
		$("#morecontent"+index).toggle();
		if($("#"+id).text()==moreText){
			$("#"+id).text(lessText);
		}else{
			$("#"+id).text(moreText);			
		}
		return false;		
		}	

		function findNoOfCharsToShort(content,noOfChars){
			for(var i=noOfChars-1;i>=0;i--){
				var charAt=content.charAt(i);
				if(charAt=='\u0020'){
					return i;
				}				
			}
			return 0;				
		}

		function highlightText(newtext,toBeSearched){
			var highlightedText="";
			if(toBeSearched.indexOf("+")==-1&&toBeSearched.indexOf("-")==-1){
				highlightedText=newtext.replace(toBeSearched,"<span style='font-weight: bold;font-size:14px;'>"+toBeSearched+"</span>");
			}else if(toBeSearched.indexOf("+")!=-1&&toBeSearched.indexOf("-")==-1){
				var plusTerms=toBeSearched.split("+");
				highlightedText=newtext;	
				for(var i=0;i<plusTerms.length;i++){
					highlightedText=highlightedText.replace(plusTerms[i],"<span style='font-weight: bold;font-size:14px;'>"+plusTerms[i]+"</span>");
				}			
			}else if(toBeSearched.indexOf("+")==-1&&toBeSearched.indexOf("-")!=-1){
				var term=toBeSearched.split("-")[0];	
				highlightedText=newtext.replace(term,"<span style='font-weight: bold;font-size:14px;'>"+term+"</span>");
			}else if(toBeSearched.indexOf("\\+")!=-1&&toBeSearched.indexOf("-")!=-1){
				var plusTerms=toBeSearched.split("\\+");
				for(var i=0;i<plusTerms.length;i++){
					var term=plusTerms[i].split("-")[0];
					newtext.replace(term,"<span style='font-weight: bold;font-size:14px;'>"+term+"</span>");
				}
			}
			return highlightedText;			
		}		
	</script>
</head>
<body>

<div id="allParentDiv">
<div id="searchDiv">
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

<p id="clubbingP">
<label style="color:blue;font-size:14px;">${number}:${subject}</label>
<input type="hidden" id="questionId" value="${id }">
<label style="display:none;"><spring:message code="question.clubbed" text="Clubbed Questions"></spring:message></label>
<c:forEach items="${clubbedQuestions }" var="i">
<a href="#" style="display:none;"><c:out value="${i.name}"></c:out></a>
</c:forEach>
<input id="clubbedQuestionsIds" value="${clubbedQuestionsIds}" type="hidden">
</p>

<div id="clubbingDiv">
<div id="clubbingResult">
</div>
<div id="searchresult">
</div>
</div>

<div id="viewQuestionDiv" style="display:none;">
<a id="backToSearch" href="#" style="display:block;"><spring:message code="clubbing.back" text="Back to search page"></spring:message></a>
<div id="viewQuestion">
</div>
</div>
<input id="successClub" type="hidden" value="<spring:message code='question.clubbedQuestions' text='Clubbed Questions: '></spring:message>">
<input id="failureClub" type="hidden" value="<spring:message code='question.clubbedfailed' text='Clubbing Failed '></spring:message>">
<input id="noresults" value="<spring:message code='generic.noresults' text='Search returned no results'></spring:message>" type="hidden">
<input id="groupMsg" name="groupMsg" value="<spring:message code='question.group' text='Group'></spring:message>" type="hidden">
<input id=subDepartmentMsg" name="subDepartmentMsg" value="<spring:message code='question.subdepartment' text='Sub Department'/>" type="hidden">
</div>
</body>
</html>