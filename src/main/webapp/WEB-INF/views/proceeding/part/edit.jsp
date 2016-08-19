<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="part.edit" text="Parts"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<style type="text/css">
		.imageLink{
			width: 14px;
			height: 14px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		}
		
		.imgLink{
			background-color: #256498;
    		border: 1px solid #FFFFFF ;
  		  	color: #FFFFFF;
  		 	font-size: 10px;
  		 	font-family:verdana;
  			text-decoration: none;
	   		text-shadow: 2px 1px 1px #000000;
	   		padding-left:2px;
	   		box-shadow:2px 1px 2px #000000;
	   		padding-right:2px;
		}
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
		
		.bookmarkKey{
			margin-left: 162px;
			margin-top: 30px;
		}
		
		.searchBy{
			width:60px;
			font-size: 12px;
		}
		
		.searchByDate{
			width:70px;
			font-size:12px;
		}
	</style>
	<script type="text/javascript">
	/***Global Variable***/
	var keycode=new Array();
	var mainContent="";
	var finalContent="";
	var j=0;
	var currentContent="";
	var contentToBeReplaced="";
	var lineCount=1;
	var maxHeight = '';	
	$(document).ready(function(){
		$('.minister').hide();
		$('.public').hide();
		$('.substitute').hide();
		$('.member').hide();
		currentRowId=$('#id').val();		
		
		/****AutoSuggest for Substitute Member including Ministers from Both houses****/
		$('#formattedSubstituteMember').autocomplete({
				minLength:3,			
				source:'ref/member/getmembers?session='+$("#session").val(),
				select:function(event,ui){		
					$("#substituteMember").val(ui.item.id);
				}	
		});
		
		/****AutoSuggest for House Specific Member****/
		$( "#formattedMember").autocomplete({
			minLength:3,			
			source:'ref/member/getmembers?session='+$("#session").val(),
			select:function(event,ui){		
				$("#primaryMember").val(ui.item.id);
			}	
		});
			
		/****Bookmark****/
		$('#addBookmark').click(function(){
			$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#cSlot').val()+'&currentPart='+$('#id').val(),function(data){
					$.fancybox.open(data, {autoSize: false, width:800, height:500});
			},'html');
			return false;
		});
		
		$('.bookmarkKey').click(function(){
			var id=this.id;
			var bookmarkId=id.split("##");
			var iframe= document.getElementById('proceedingContent-wysiwyg-iframe');
			contentToBeReplaced=getIframeSelectionText(iframe);
			$.prompt($('#addBookmarkMessage').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	$.post('proceeding/part/updatetext?bookmark='+bookmarkId[0]+'&textToBeAdded='+contentToBeReplaced+"&part="+$('#id').val()+"&currentSlot="+$('#cSlot').val(),function(data){
						if(data!=null){
							alert("The Text has Been added for the bookmark successfully");
						}
					});
		        }
				}});			
		       return false;  
		 }); 
		/****Citation****/
		$(".viewProceedingCitation").click(function(){
			$.get('proceeding/part/citations',function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			},'html');
			return false;
		});
			
		/****Hiding the Mainheading and PageHeading if they are empty****/
		if($('#mainHeading').val()==''){
			$('#mainHeadingP').hide();
		}
			
		if($('#pageHeading').val()==''){
			$('#pageHeadingP').hide();
		}
		
		/****Toggling of MainHeading and PageHeadingDivs on Link Click****/
		$('#mainHeadingLink').click(function(){
			$('#mainHeadingP').toggle();
		});
			
		$('#pageHeadingLink').click(function(){
			$('#pageHeadingP').toggle();
		});
			
		/****wysiwyg Control****/
		$('.wysiwyg').wysiwyg({
			events:{
				keydown:function(e){
					if(j==0 && e.which==17){
						keycode[j]=e.which;
						j++;
					}else if(j==1 && e.which==16){
						keycode[j]=e.which;
						j++;
					}else if(j==2 && e.which!=0){
						mainContent=$('#proceedingContent').val();
						finalContent=mainContent;					
						mainContent=replaceString(mainContent);
						prevMainContentLength=mainContent.length;
						keycode[j]=e.which;
						j++;
					
					}else{
						if(e.which==13){
							lineCount=lineCount+1;
						}
						currentContent=$('#proceedingContent').wysiwyg('getContent');
						currentContent=replaceString(currentContent);
					}
					if(keycode[0]==17 && keycode[1]==16){
						var key="";
						if(keycode.length>2){
							e.preventDefault();
							key=keycode;
						}
						if(key!=""){
							if(currentContent.length>mainContent.length){
								currentContent=currentContent.replace(/<br>/g,"");
								var lookupContent=currentContent.substring(mainContent.length);
								if(lookupContent!=""){
									$.get("ref/search?key="+key+"&term="+lookupContent,function(data){
										 $("#autosuggest_menu").empty();
										var menuText = "";
										if(data.length>0){
											for(var i=0;i<data.length;i++){
												menuText+="<option value='"+data[i].value+"'>"+data[i].name;
											}
											$("#autosuggest_menu").html(menuText);
											keycode.length=0;
											j=0;
											var offset = $("#proceedingContent-wysiwyg-iframe").offset();
											$("#shiftDiv").css("display","block");
											$("#autosuggest_menu").css("display","block");
											$("#autosuggest_menu").focus();
											$("#autosuggest_menu").get(0).selectedIndex = 0;;
											$("#shiftDiv").css("left",/* $('#'+contentNo).val().length  + */ offset.left);
									 		$("#shiftDiv").css("top",offset.top/* +(lineCount*30) */);
										}else{
											$("#shiftDiv").css("display","none");
											j=0;
											keycode.length=0;
										}
									}); 
								}
							}
							}
						}
					}
				},
				controls:{
						fullscreen: {
							/*groupIndex: 12,*/
							visible: true,
							exec: function () {
								if ($.wysiwyg.fullscreen) {
									$.wysiwyg.fullscreen.init(this);
								}
							},
							hotkey:{
								"ctrl":1|0,
								"key":122
							},
							tooltip: "Fullscreen"
						},
						bookmark:{
							visible: true, 
							 icon: './resources/images/bookmark.png',
				             tags: ['bookmark'], 
				             exec: function(){
				            	 	 $.get('proceeding/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val(),function(data){
				 				   		 $.fancybox.open(data, {autoSize: false, width:800, height:500});
				 			   		 },'html');
				 			    	return false;
				             	}
				       }
				}	  
	      });
			
		/****Menu to display the masters(ghat,fort,etc.) by shortcut****/
		$("#autosuggest_menu").click(function(e){
			var content=$(this).val();
			content=replaceString(content);
			$("#proceedingContent").wysiwyg("setContent",finalContent+content);
			mainContent=$("#proceedingContent").val();
			mainContent=replaceString(mainContent);
			$(this).css("display","none");
			$("proceedingContent-wysiwyg-iframe").focus();
		});
			
		/****Navigation Functionality(Next,Previous,GoTo)****/
		$('#previousLink').click(function(){
				previousPart();
		});
			
		$('#nextLink').click(function(){
			nextPart();
		});
			
		$('#orderNoInput').change(function(){
			var orderNoText=$(this).val();
			goToPart(orderNoText);
		});
		
		/***Hiding Divs on Link click(minister,public,substitute,private)*****/
		$('#ministerLink').click(function(){
			hideDiv();	
			$('.minister').show();
			$('.member').show();
			$('#privateLink').show();
			$('#publicLink').show();
			$('#substituteLink').show();
		});
			
		$('#substituteLink').click(function(){
			$('.substitute').toggle();
		});
			
		$('#privateLink').click(function(){
			hideDiv();
			$('.member').show();
			$('#ministerLink').show();
			$('#publicLink').show();
			$('#substituteLink').show();
		});
			
		$('#publicLink').click(function(){
			hideDiv();
			$('#ministerLink').show();
			$('#publicLink').show();
			$('#substituteLink').show();
			$('#privateLink').show();
			$('.public').show();
			
		});
			
		if($('#primaryMemberSelected').val()!=''){
				$('.member').show();
		}
		if($('#primaryMemberMinistrySelected').val()!=''){
			$("#primaryMemberMinistry").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
			$('.minister').show();
			$('#privateLink').show();
			$('#ministerLink').hide();
		}else{
			$("#primaryMemberMinistry").prepend("<option value='' selected='selected' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
			
		if($('#primaryMemberDesignationSelected').val()!=''){
			$("#primaryMemberDesignation").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#primaryMemberDesignation").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		
		if($('#primaryMemberSubDepartmentSelected').val()!=''){
			$("#primaryMemberSubDepartment").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#primaryMemberSubDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
			
		if($('#substituteMemberSelected').val()!=''){
			$('.substitute').show();
		}
		if($('#publicRepresentative').val()!=''){
			$('.public').show();
		}
		$('.deviceType').each(function(){
			if($(this).children().eq(1).attr('value')==''){
					$(this).hide();
			}
		}) ;
		
		if($('#substituteMemberMinistrySelected').val()!=''){
			$("#substituteMemberMinistry").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#substituteMemberMinistry").prepend("<option value='' selected='selected' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
			
		if($('#substituteMemberSubDepartmentSelected').val()!=''){
			$("#substituteMemberSubDepartment").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#substituteMemberSubDepartment").prepend("<option value='' selected='selected' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($('#substituteMemberDesignationSelected').val()!=''){
			$("#substituteMemberDesignation").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#substituteMemberDesignation").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
			
		/****Reseting the Content of Main Heading and Page Heading****/
		$('#resetMainHeading').click(function(){
				$('#mainHeading').wysiwyg('setContent',"");
		});
		$('#resetPageHeading').click(function(){
			$('#pageHeading').wysiwyg('setContent',"");
		});
			
		/**** Right Click Menu ****/
		$(".bookmarkKey").contextMenu({
		       menu: 'contextMenuItems'
		   	},
		    function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='viewBookmarkDetails'){
					viewBookmarkDetail(id);	
				}
		});	
			 
		/****To Display the photo of Member whose part is been prepared****/
		$('#formattedMember').change(function(){
			$('#memberImage').attr("src","ref/getphoto?memberId="+$('#primaryMember').val());
			$('#memberPhoto').css("display","inline");
		});
			
		/****To import the mainHeading and PageHeading of Interrupted Proceeding****/	
		$('#interruptedProceeding').click(function(){
				$('#searchBy').toggle();
				$('#searchByDate').toggle();
				$('#diffSpan').css('margin-right','10px');
		});
			
		$(document).click(function(){
			 $('#interruptProceedingDiv').css('display','none');
		});
			
		$('#searchBy').change(function(){
			$.get('ref/getInterruptedProceedings?selectedDate='+$('#searchByDate').val()+"&searchBy="+$(this).val()+'&language='+$("#selectedLanguage").val(),function(data){
				var text="";
				if(data.length>0){
				 for(var i=0;i<data.length;i++){
					 text=text+"<option value='"+data[i].value +"'>"+data[i].name+"</option>"; 
				 }
				 $('#iProceeding').html(text);
				 $('#iProceeding').attr("size",data.length);
				 $('#interruptProceedingDiv').css('left','469px');
				 $('#interruptProceedingDiv').css('top','245px');
				 $('#interruptProceedingDiv').css('display','block');
				}
			});
		});
			
			
		$('#iProceeding').change(function(){
			var strAction=$(this).val().split("#");
			$('#mainHeading').wysiwyg('setContent',strAction[1]);
			$('#pageHeading').wysiwyg('setContent',strAction[0]);
			$('#mainHeadingP').css('display','block');
			$('#pageHeadingP').css('display','block');
		});
		
		$("#primaryMemberMinistry").change(function(){
			loadSubDepartments($(this).val(),"primaryMemberSubDepartment");
		});
	
		$("#substituteMemberMinistry").change(function(){
			loadSubDepartments($(this).val(),"substituteMemberSubDepartment");
		});
		
		$('#party').change(function(){
		 	$.get('proceeding/part/getMemberByPartyPage?partyId='+$(this).val(),function(data){
	 			$.fancybox.open(data,{autoSize: false,width:800,height:500});
			},'html'); 
	 
	    return false;
		});
		
		$(".imgLink a[title]").qtip({
    		show: 'mouseover',
    		hide: 'mouseout'
    	});
		
		$('#mainHeading-wysiwyg-iframe').css('height','124px');
		$('#pageHeading-wysiwyg-iframe').css('height','124px');
		$('#party').prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
	
		$("#submit").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			var wysiwygVal=$('#proceedingContent').val().trim();
			if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
				$('#proceedingContent').val("");
			}
						
			if($('#proceedingContent').val()==''){
				$.prompt($('#proceedingEmptyMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	return true;
			        }else{
			        	return false;
			        }
				}});
			}else{
				return true;
			} 
			return false;
	    });
	});
	
	/****Function to get the selected text from iframe****/
	function getIframeSelectionText(iframe) {
		 var win = iframe.contentWindow;
		 var doc = iframe.contentDocument || win.document;
	     if (win.getSelection) {
		    return win.getSelection().toString();
		  } else if (doc.selection && doc.selection.createRange) {
		    return doc.selection.createRange().text;
		  }
	}
	
	/**** view Bookmark Details****/
	function viewBookmarkDetail(id){
		var params="id="+id;
		$.get('proceeding/part/viewbookmark?'+params,function(data){
		    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		},'html');					
	}
		
	
	/****Function to replace a part of a string****/
	function replaceString(toBeReplacedContent){
		toBeReplacedContent=toBeReplacedContent.replace(/<br><p><\/p>/g,"");
		toBeReplacedContent=toBeReplacedContent.replace(/<br>/g,"");
		toBeReplacedContent=toBeReplacedContent.replace(/<p>/g,"");
		toBeReplacedContent=toBeReplacedContent.replace(/<\/p>/g,"");
		return toBeReplacedContent;
	}
		
	/****Function to Hide Divs****/
	function hideDiv(){
		$('.member').hide();
		$('.minister').hide();
		$('.public').hide();
		$('.substitute').hide();
		$('#privateLink').hide();
		$('#ministerLink').hide();
		$('#substituteLink').hide();
	}
	
	
	function loadSubDepartments(ministry,id){
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data){
			$("#"+id).empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#"+id).html(subDepartmentText);
			$("#"+id).css('display','inline');
			}else{
				$("#"+id).empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#"+id).html(subDepartmentText);	
				$("#"+id).css('display','inline');
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
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="proceeding/part" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="part.edit.heading" text="Part:ID"/>(${domain.id })		
		<%-- <span style="margin-left: 200px;">
				<a href='#' id="interruptedProceeding"><img src="./resources/images/interruptedProceeding.jpg" title="Interrupted Proceeding" class="imageLink" /></a>
				<select id="searchBy" class="sSelect searchBy" style="display: none;">
					<option class="searchBy" selected="selected" value='pageHeading'><spring:message code="part.pageHeading"/></option>
					<option class="searchBy" value='mainHeading'><spring:message code="part.mainHeading"/></option>
				</select>
				<a href="#" id="mainHeadingLink"><img src="./resources/images/mainHeading.jpg" title="Main Heading" class="imageLink" /></a>
				<a href="#"  id="pageHeadingLink"><img src="./resources/images/pageHeading.jpg" title="Page Heading"class="imageLink" /></a>
				<a href="#" id="addBookmark" class="addBookmark" ><img src="./resources/images/bookmark.jpg" title="Bookmark" class="imageLink" /></a>
				<a href="#" id="viewProceedingCitation" class="viewProceedingCitation"><img src="./resources/images/citation.jpg" title="Citation" class="imageLink" /></a>
				<a href="#" id="addDevice" class="addDevice"><img src="./resources/images/device.jpg" title="Device" class="imageLink" /></a>
				<a href='#' id="privateLink"><img src="./resources/images/privateMember.jpg" title="Private" class="imageLink" /></a>
				<a href='#' id="ministerLink"><img src="./resources/images/minister.jpg" title="Minister" class="imageLink" /></a>
				<a href='#' id="publicLink"><img src="./resources/images/publicRepresentative.jpg" title="Public" class="imageLink" /></a>
				<a href='#' id="substituteLink"><img src="./resources/images/substitute.jpg" title="In place of" class="imageLink" /></a>
				<span style="min-width:100px; margin-right: 120px;">&nbsp;</span>
				<span style="text-align: right;">
				<a href="#" id="previousLink"><img src="./resources/images/IcoBack.jpg" title="Previous Part" class="imageLink" /></a>
				&nbsp;
				<input type="text" id="orderNoInput" name="orderNoInput" style="width: 30px"/>
				<!-- <input type="button" id="go" name="go" value="Go" style="width: 30px"/> --> 	
				&nbsp;
				<a href="#" id="nextLink"><img src="./resources/images/IcoNext.jpg" title="Next Part" class="imageLink" /></a>
				</span>
		</span> --%>
			<div style="margin-bottom:10px;display:inline-block; ">
			<span style="margin-left: 250px;">
			
			<a href='#' id="interruptedProceeding" class="imgLink" title="Interrupted Proceeding">IP<!-- <img src="./resources/images/interruptedProceeding.jpg" title="Interrupted Proceeding" class="imageLink" /> --></a>
			<input id="searchByDate" type="text" class=" sText datemask searchByDate" style="display:none;"/>
			<select id="searchBy" class="sSelect searchBy" style="display: none;">
				<option class="searchBy" selected="selected" value='pageHeading'><spring:message code="part.pageHeading"/></option>
				<option class="searchBy" value='mainHeading'><spring:message code="part.mainHeading"/></option>
			</select>
			<a href="#" id="mainHeadingLink" class="imgLink" title="Main Heading">MH<!-- <img src="./resources/images/mainHeading.jpg" title="Main Heading" class="imageLink" /> --></a>
			<a href="#"  id="pageHeadingLink" class="imgLink"  title="Page Heading">PH<!-- <img src="./resources/images/pageHeading.jpg" title="Page Heading" class="imageLink" /> --></a>
			<a href="#" id="addBookmark" class="addBookmark imgLink" title="Bookmark" >BK<!-- <img src="./resources/images/bookmark.jpg" class="imageLink" title="Bookmark" /> --></a>
			<a href="#" id="viewProceedingCitation" class="viewProceedingCitation imgLink" title="citation">CT<!-- <img src="./resources/images/citation.jpg" class="imageLink" title="Citation" /> --></a>
			<a href="#" id="addDevice" class="addDevice imgLink" title="device">DE<!-- <img src="./resources/images/device.jpg" title="Device" class="imageLink"/> --></a>
			<a href='#' id="privateLink" style="display:none;"  class="imgLink" title="Member">PM<!-- <img src="./resources/images/privateMember.jpg" title="Private" class="imageLink" /> --></a>
			<a href='#' id="ministerLink"  class="imgLink" title="Minister">MI<!-- <img src="./resources/images/minister.jpg" title="Minister"  class="imageLink"/> --></a>
			<a href='#' id="publicLink"  class="imgLink" title="Public">PU<!-- <img src="./resources/images/publicRepresentative.jpg" title="Public" class="imageLink" /> --></a>
			<a href='#' id="substituteLink" class="imgLink" title="In place of">SU<!-- <img src="./resources/images/substitute.jpg" title="In place of" class="imageLink"/> --></a>
			<span id="diffSpan" style="min-width:80px; margin-right:100px;">&nbsp;</span>
			<span style="text-align: right;">
			<a href="#" id="previousLink" class="imgLink" title="Previous Part">&lt;<!-- <img src="./resources/images/IcoBack.jpg" title="Previous Part" class="imageLink"  /> --></a>
			<input type="text" id="orderNoInput" name="orderNoInput" style="width: 60px"/>
			<!-- <input type="button" id="go" name="go" value="Go" style="width: 21px"/> --> 	
			<a href="#" id="nextLink" class="imgLink" title="Next Part">&gt;<!-- <img src="./resources/images/IcoNext.jpg" class="imageLink" title="Next Part"/> --></a>
			
			</span>
			
		</span>
		</div>
	</h2>
	<p>
		<c:forEach items="${bookmarks}" var="i">
			<c:choose>
				<c:when test="${i.textToBeReplaced!=null and i.textToBeReplaced!=''}">
					<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="color:green;">${i.bookmarkKey}</a>
				</c:when>
				<c:otherwise>
					<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="color: red;">${i.bookmarkKey}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</p>
	<p>
	<label class="small"><spring:message code="proceeding.slot" text="Slot"/></label>
	<input type="text" name="slotName" id="slotName" value="${slotName}"/>
	<p>
		<label class="small"><spring:message code="part.chairPersonRole" text="Chair Person Role"/></label>
		<form:select path="chairPersonRole" items="${roles}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>
	</p>
	<p>
		<label class="small"><spring:message code="part.chairPerson" text="Chair Person Name"/></label>
		<form:input path="chairPerson" cssClass="sText"/>
	</p>
	
	
	<p class="member">
		<label  class="small"><spring:message code="part.memberName" text=" Member"/></label>
		<input type="text" name="formattedMember" id="formattedMember" class="autosuggest sText" value="${formattedPrimaryMember}"/>
		<%-- <form:hidden path="primaryMember" value="${primaryMember}"/> --%>
		<input type="hidden" id="primaryMember" name="primaryMember" value="${primaryMember}">
		<label class="small"><spring:message code="part.party" text=" OR party"/></label>
		<select id="party" class="sSelect">
			<c:forEach items="${parties}" var="i">
				<option value="${i.id}">${i.name}</option>
			</c:forEach>
		</select>
		<div id="memberPhoto" style=" display: none;">
			<img src="" id="memberImage" style="width:60px; margin-right: 65px; float:right; margin-top: -70px; border-radius: 5px; box-shadow: 1px 1px 5px black;"/> 
		</div>
	</p>
	<p>
		<label class="small"><spring:message code="part.order" text="Order"/></label>
		<input name="orderNo" id="orderNo" class="sInteger" value="${domain.orderNo}"/>
	</p>
	<p class="minister">
		<label class="small"><spring:message code="part.primaryMemberDesignation" text="Primary Member Designation"/></label>
		<select id="primaryMemberDesignation" name="primaryMemberDesignation" class="sSelect">
			<c:forEach items="${designations}" var="i" >
				<c:choose>
					<c:when test="${i.id==primaryMemberDesignationSelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
				
			</c:forEach>
		</select>
		<form:errors path="primaryMemberDesignation"/>
	</p>
	<p class="minister">
		<label class="small"><spring:message code="part.primaryMemberMinistry" text="Primary Member Ministry"/></label>
		<select id="primaryMemberMinistry" name="primaryMemberMinistry" class="sSelect">
			<c:forEach items="${ministries}" var="i">
				<c:choose>
					<c:when test="${i.id==primaryMemberMinistrySelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		<form:errors path="primaryMemberMinistry"/>
	</p>
	
	<p class="minister" style="display:none;">
		<label class="small"><spring:message code="part.subDepartment" text="Primary Member SubDepartment"/></label>
		<select id="primaryMemberSubDepartment" name="primaryMemberSubDepartment" class="sSelect">
			<option value="" selected='selected'><spring:message code="please.select" text="Please Select"/></option>
			<c:forEach items="${subDepartments}" var="i">
				<c:choose>
					<c:when test="${i.id==primaryMemberSubDepartmentSelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		<form:errors path="primaryMemberSubDepartment"/>
	</p>
	
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberName" text="In Place of"/></label>
		<input type="text" name="formattedSubstituteMember" id="formattedSubstituteMember" class="autosuggest sText" value="${formattedSubstituteMember}"/>
		<input type="hidden" id="substituteMember" name="substituteMember" value="${substituteMember}">
		<%-- <form:hidden path="substituteMember"/> --%>
	</p>
	
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberDesignation" text="Designation"/></label>
		<select id="substituteMemberDesignation" name="substituteMemberDesignation" class="sSelect">
			<c:forEach items="${designations}" var="i" >
				<c:choose>
					<c:when test="${i.id==substituteMemberDesignationSelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
				
			</c:forEach>
		</select>
		
		<form:errors path="substituteMemberDesignation"/>
	</p>
	<p class="substitute">
		<label class="small"><spring:message code="part.substituteMemberMinistry" text="Ministry"/></label>
		<select id="substituteMemberMinistry" name="substituteMemberMinistry" class="sSelect">
			<c:forEach items="${ministries}" var="i">
				<c:choose>
					<c:when test="${i.id==substituteMemberMinistrySelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		<form:errors path="substituteMemberMinistry"/>
	</p>
	
	<p class="substitute" style="display:none;">
		<label class="small"><spring:message code="part.subDepartment" text="Substitute Member SubDepartment"/></label>
		<select id="substituteMemberSubDepartment" name="substituteMemberSubDepartment" class="sSelect">
			<option value="" selected='selected'><spring:message code="please.select" text="Please Select"/></option>
			<c:forEach items="${subDepartments}" var="i">
				<c:choose>
					<c:when test="${i.id==substituteMemberSubDepartmentSelected}">
						<option value="${i.id}" selected='selected'>${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		<form:errors path="substituteMemberSubDepartment"/>
	</p>
	<p class="public">
		<label class="small"><spring:message code="part.publicRepresentative" text="public Representative"/></label>
		<form:input path="publicRepresentative" cssClass="sText"/>
		<form:errors path="publicRepresentative"></form:errors>
	</p>
	<p class="public">
		<label class="wysiwyglabel"><spring:message code="part.publicRepresentativeDetail" text="Details"/></label>
		<form:textarea cssClass="wysiwyg" path="publicRepresentativeDetail"/>
		<form:errors path="publicRepresentativeDetail"></form:errors>
	</p>
	
	<p class="deviceType" >
		<label class="small"><spring:message code="part.deviceType" text="Device Type"/></label>
		<select id="deviceType" name="deviceType" class="sSelect">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${deviceTypes}" var="i">
				<c:choose>
					<c:when test="${i.id==selectedDeviceType }">
						<option value="${i.id}" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}">${i.name}</option>
					</c:otherwise>
				</c:choose>
				
			</c:forEach>
		</select>
		<form:errors path="deviceType"></form:errors>
	</p>
	<p class="deviceNo">
		<label class="small"><spring:message code="part.deviceNo" text="Device No"/></label>
		<input type="text" name="deviceNo" id="deviceNo" class="sInteger" value="${deviceNumber}"/>
	</p>
	<p>
		<label class="small"><spring:message code="part.isInterrupted" text="Is Interrupted"/></label>
		<form:checkbox path="isInterrupted" cssClass="sCheck"/>
	</p>
	<p>
		<label class="small"><spring:message code="part.isConstituencyRequired" text="Is Constituency Required?"/></label>
		<form:checkbox path="isConstituencyRequired" cssClass="sCheck"/>
	</p>
	<p id="pageHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.pageHeading" text="Page Heading"/></label>
		<form:textarea path="pageHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetPageHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
	</p>
	<p id="mainHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.mainHeading" text="Main Heading"/></label>
		<form:textarea path="mainHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetMainHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="part.proceedingContent" text="Content"/></label>
		<form:textarea path="proceedingContent"  cssClass="wysiwyg"/>
	</p>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="reporter" value="${reporter}"/>
	<form:hidden path="EntryDate"/>
	<form:hidden path="RevisedContent"/>
	<form:hidden path="deviceId"/>
	<form:hidden path="proceeding" value="${proceeding}"/>
	<input type="hidden" id="cSlot" name="cSlot" value="${currentSlot}"/>
	<input type="hidden" id="currentSlotName" name="currentSlotName" value="${slotName}"/>
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>	
	
	<div style="border: 2px solid black; position:absolute; z-index: 2000; display:none;" id="shiftDiv">
		<select size="5" id="autosuggest_menu">
		
		</select> 
	</div>	
	
	<div id="interruptProceedingDiv" style="height:auto;width:400px; display:none; border:1px solid black; padding:5px; border-radius:5px; box-shadow:2px 2px 5px; position:absolute;"> 
		<select id="iProceeding" style="width:400px;">
		</select>
	</div>	
</form:form>
<ul id="contextMenuItems" >
	<li><a href="#viewBookmarkDetails" class="edit"><spring:message code="proceeding.viewBookmarkDetails" text="viewDetails"></spring:message></a></li>
</ul>
<input type="hidden" id="session" value="${session}"/>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input id="addBookmarkMessage" value="<spring:message code='client.prompt.updateText' text='Do you want to add the Text for the Bookmark.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id= "primaryMemberSelected" name="primaryMemberSelected" value="${primaryMember}">
<input type="hidden" id="substituteMemberSelected" name="substituteMemberSelected" value="${substituteMember}">
<input type="hidden" id="primaryMemberMinistrySelected" name="primaryMemberMinistrySelected" value="${primaryMemberMinistrySelected}"/>
<input type="hidden" id="substituteMemberMinistrySelected" name="substituteMemberMinistrySelected" value="${substituteMemberMinistrySelected }">
<input type="hidden" id="primaryMemberDesignationSelected" name="primaryMemberDesignationSelected" value="${primaryMemberDesignationSelected }">
<input type="hidden" id="primaryMemberSubDeparmentSelected" name="primaryMemberSubDeparmentSelected" value="${primaryMemberSubDeparmentSelected }">
<input type="hidden" id="substituteMemberSubDeparmentSelected" name="substituteMemberSubDeparmentSelected" value="${substituteMemberSubDeparmentSelected }">
<input type="hidden" id="substituteMemberDesignationSelected" name="substituteMemberDesignationSelected" value="${substituteMemberDesignationSelected }">
</div> 
<input id="proceedingEmptyMsg" value="<spring:message code='client.prompt.emptyProceeding' text='Proceeding is Not Entered.Do you still want to continue?'></spring:message>" type="hidden">
</body>
</html>