<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="part.memberphotos"	text=" Member photos" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	
	//$("a.memberImgA").fancybox();
	var memberArray=Array();
	$('#memberMaster option').each(function(index){
		memberArray[index]=$(this).val();
	});
	
	 
	var count=$('#count').val();
	$('#loadMore').click(function(){
		var text="";
		var i=0;
		var j=parseInt(count)+10;
		for(i=count;i<j && i<memberArray.length;i++){
			var memberValue=memberArray[i].split('#');
			text=text+"<div class='memImgA'>"+
				 "<a href='javascript:void(0)' id='"+memberArray[i]+"' rel='member' class='memberImgA' title='"+memberValue[1] +"'><img src='ref/getphoto?memberId="+memberValue[0]+"'  style='width: 113px; height:150px'/></a>"+
				  "<label>"+ memberValue[1]+"</label>"+ 
				 "</div>";
			count=i;
		}
		if(i>memberArray.length){
			$('#loadMore').css("display","none");
		}
		count=i+1;
		$('#imgDiv').append(text);
		
		$('.memberImgA').click(function(){
			var id=this.id;	
			var member=id.split('#');
			$('#primaryMember'+$('#partCounter').val()).val(member[0]);
			$('#formattedPrimaryMember'+$('#partCounter').val()).val(member[1]);
			$.fancybox.close();
		});
	});
	
	
	
	
	/* $(".memImgA a[title]").qtip({
		show: 'mouseover',
		hide: 'mouseout'
	}); */
	
	$('.memberImgA').click(function(){
		var id=this.id;	
		var member=id.split('#');
		$('#primaryMember'+$('#partCounter').val()).val(member[0]);
		$('#formattedPrimaryMember'+$('#partCounter').val()).val(member[1]);
		$.fancybox.close();
	});
	
	
	});
</script>
<style type="text/css">
	.memImgA{
		width: 113px;
		height: 150px;
		float: left;
		position: relative;
		margin-bottom: 10px;
		margin-left:2px;
		margin-top:10px;
		padding:25px;
		display: inline-block;
	}
</style>
</head>
<body>
<c:choose>	
	<c:when test="${!(empty members) }">
		<c:set var="count" value="0"></c:set>
		<div id="imgDiv" style="width: 800px; height: 500px; text-align: center; display: block; margin-left:50px;">
				<c:forEach items="${members}" var="i" begin="${count}" end="${count+10}">
					<div class="memImgA">
						<a href="javascript:void(0)" id="${i.id}#${i.getFullname()}" rel="member" class="memberImgA fancybox.ajax" title="${i.getFullname()}"><img src="ref/getphoto?memberId=${i.id}"  style="width: 113px; height:150px"/></a>
						<label>${i.getFullname()}</label>
					</div>
					<c:set var="count" value="${count+1}"></c:set>
				</c:forEach>
			<input type="hidden" id="count" value="${count}">
		</div>
	</c:when>
	<c:otherwise>
		<spring:message code="proceeding.no members" text="No Member found"></spring:message>
	</c:otherwise>
</c:choose>
<div>
	<input type="button" id="loadMore" value="Load More"/>
</div>	

<select id="memberMaster" style="display:none;">
	<c:forEach items="${members}" var="i">
		<option value="${i.id}#${i.getFullname()}"></option>
	</c:forEach>
</select>

<input type="hidden" id='partCounter' value='${partCount}'>
</body> 
</html>