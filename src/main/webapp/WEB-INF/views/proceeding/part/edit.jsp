<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="part.edit" text="Parts"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<style type="text/css">
		.imageLink{
			width: 16px;
			height: 16px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		}
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
		
	</style>
	<script type="text/javascript">

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
		$('#formattedSubstituteMember').autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers?session='+$("#session").val(),
				select:function(event,ui){		
					$("#substituteMember").val(ui.item.id);
				}	
			});
			$( "#formattedMember").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers?session='+$("#session").val(),
				select:function(event,ui){		
					$("#primaryMember").val(ui.item.id);
				}	
			});
			
			$('#addBookmark').click(function(){
				//var id=this.id;
				$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#cSlot').val()+'&currentPart='+$('#id').val(),function(data){
					    $.fancybox.open(data, {autoSize: false, width:800, height:500});
				 },'html');
				    return false;
			});
			
			$(".viewProceedingCitation").click(function(){
				$.get('proceeding/part/citations',function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			    },'html');
			    return false;
			});
			
			if($('#mainHeading').val()==''){
				$('#mainHeadingP').hide();
			}
			
			if($('#pageHeading').val()==''){
				$('#pageHeadingP').hide();
			}
			
			$('#mainHeadingLink').click(function(){
				$('#mainHeadingP').toggle();
			});
			
			$('#pageHeadingLink').click(function(){
				$('#pageHeadingP').toggle();
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
							mainContent=mainContent.replace(/<br><p><\/p>/g,"");
							mainContent=mainContent.replace(/<br>/g,"");
							mainContent=mainContent.replace(/<p>/g,"");
							mainContent=mainContent.replace(/<\/p>/g,"");
							prevMainContentLength=mainContent.length;
							console.log("MainContent="+mainContent);
							keycode[j]=e.which;
							j++;
						
						}else{
							if(e.which==13){
								
								lineCount=lineCount+1;
								console.log(lineCount);
							}
							currentContent=$('#proceedingContent').wysiwyg('getContent');
							currentContent=currentContent.replace(/<br><p><\/p>/g,"");
							currentContent=currentContent.replace(/<br>/g,"");
							currentContent=currentContent.replace(/<p>/g,"");
							currentContent=currentContent.replace(/<\/p>/g,"");
							console.log("current Content:"+currentContent);
							
							
						}
						
						if(keycode[0]==17 && keycode[1]==16){
							
							var key="";
							if(keycode.length>2){
								console.log("keycode[2]"+keycode[2]);
								e.preventDefault();
								key=keycode;
							}
													
							if(key!=""){
							console.log("current Content Length:"+currentContent.length);
							console.log("Main Content Length:"+mainContent.length);
							
							if(currentContent.length>mainContent.length){
								
								currentContent=currentContent.replace(/<br>/g,"");
								var lookupContent=currentContent.substring(mainContent.length);
								console.log("lookupContent:"+lookupContent);
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
			
			$("#autosuggest_menu").click(function(e){
	
					var content=$(this).val();
					content==content.replace(/<br><p><\/p>/g,"");
					content==content.replace(/<br>/g,"");
					$("#proceedingContent").wysiwyg("setContent",finalContent+content);
					mainContent=$("#proceedingContent").val();
					mainContent=mainContent.replace(/<br><p><\/p>/g,"");
					mainContent=mainContent.replace(/<br>/g,"");
					mainContent=mainContent.replace(/<p>/g,"");
					mainContent=mainContent.replace(/<\/p>/g,"");
					$(this).css("display","none");
					$("proceedingContent-wysiwyg-iframe").focus();
				});
			
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
						
			$('#ministerLink').click(function(){
				$('.minister').show();
				$('.member').show();
				$('.substitute').hide();
				$('.private').hide();
				$('.public').hide();
				$('#privateLink').show();
				$('#ministerLink').hide();
			});
			
			$('#substituteLink').click(function(){
				$('.substitute').toggle();
			});
			
			$('#privateLink').click(function(){
				$('.member').show();
				$('.minister').hide();
				$('.public').hide();
				$('.substitute').hide();
				$('#privateLink').hide();
				$('#ministerLink').show();
			});
			
			$('#publicLink').click(function(){
				$('.public').show();
				$('.member').hide();
				$('.substitute').hide();
				$('.minister').hide();
				
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
			
			if($('#substituteMemberDesignationSelected').val()!=''){
				$("#substituteMemberDesignation").prepend("<option value='' >----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
				$("#substituteMemberDesignation").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}
			
			$('#resetMainHeading').click(function(){
				$('#mainHeading').wysiwyg('setContent',"");
			});
			$('#resetPageHeading').click(function(){
				$('#pageHeading').wysiwyg('setContent',"");
			});
			
		});
		function getIframeSelectionText(iframe) {
			  var win = iframe.contentWindow;
			  var doc = iframe.contentDocument || win.document;

			  if (win.getSelection) {
			    return win.getSelection().toString();
			  } else if (doc.selection && doc.selection.createRange) {
			    return doc.selection.createRange().text;
			  }
			}
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="proceeding/part" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="part.edit.heading" text="Part:ID"/>(${domain.id })		
		<span style="margin-left: 300px;">
				<a href="#" id="mainHeadingLink"><img src="./resources/images/IcoMainHeading.jpg" title="Main Heading" class="imageLink" /></a>
				<a href="#"  id="pageHeadingLink"><img src="./resources/images/IcoPageHeading.jpg" title="Page Heading"class="imageLink" /></a>
				<a href="#" id="addBookmark" class="addBookmark" ><img src="./resources/images/IcoBookMark.jpg" title="Bookmark" class="imageLink" /></a>
				<a href="#" id="viewProceedingCitation" class="viewProceedingCitation"><img src="./resources/images/IcoCitation.jpg" title="Citation" class="imageLink" /></a>
				<a href="#" id="addDevice" class="addDevice"><img src="./resources/images/IcoDeviceType.jpg" title="Device" class="imageLink" /></a>
				<a href='#' id="privateLink"><img src="./resources/images/IcoPrivateMember.jpg" title="Private" class="imageLink" /></a>
				<a href='#' id="ministerLink"><img src="./resources/images/IcoMinister.jpg" title="Minister" class="imageLink" /></a>
				<a href='#' id="publicLink"><img src="./resources/images/IcoPublicRepresentative.jpg" title="Public" class="imageLink" /></a>
				<a href='#' id="substituteLink"><img src="./resources/images/IcoSubstitute.jpg" title="In place of" class="imageLink" /></a>
				<span style="min-width:100px; margin-right: 120px;">&nbsp;</span>
				<span style="text-align: right;">
				<a href="#" id="previousLink"><img src="./resources/images/IcoBack.jpg" title="Previous Part" class="imageLink" /></a>
				&nbsp;
				<input type="text" id="orderNoInput" name="orderNoInput" style="width: 30px"/>
				<!-- <input type="button" id="go" name="go" value="Go" style="width: 30px"/> --> 	
				&nbsp;
				<a href="#" id="nextLink"><img src="./resources/images/IcoNext.jpg" title="Next Part" class="imageLink" /></a>
				</span>
		</span>
	</h2>
	<p>
		<c:forEach items="${bookmarks}" var="i">
			<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="margin-left: 162px;margin-top: 30px;">${i.bookmarkKey}</a> 
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
	<%-- <p class="deviceNo">
		<label class="small"><spring:message code="part.deviceNo" text="Device No"/></label>
		<input type="text" name="deviceNo" id="deviceNo" class="sInteger"/>
	</p> --%>
	<p>
		<label class="small"><spring:message code="part.isInterrupted" text="Is Interrupted"/></label>
		<form:checkbox path="isInterrupted" cssClass="sCheck"/>
	</p>
	<p id="mainHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.mainHeading" text="Main Heading"/></label>
		<form:textarea path="mainHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetMainHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
	</p>
	<p id="pageHeadingP">
		<label class="wysiwyglabel"><spring:message code="part.pageHeading" text="Page Heading"/></label>
		<form:textarea path="pageHeading" cssClass="wysiwyg"/>
		<a href="javascript:void(0)" id="resetPageHeading" style="margin-right: 100px; float: right; margin-top: -150px;">reset</a>
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
</form:form>
<input type="hidden" id="session" value="${session}"/>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input id="addBookmarkMessage" value="<spring:message code='client.prompt.updateText' text='Do you want to add the Text for the Bookmark.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id= "primaryMemberSelected" name="primaryMemberSelected" value="${primaryMember}">
<input type="hidden" id="substituteMemberSelected" name="substituteMemberSelected" value="${substituteMember}">
<input type="hidden" id="primaryMemberMinistrySelected" name="primaryMemberMinistrySelected" value="${primaryMemberMinistrySelected}"/>
<input type="hidden" id="substituteMemberMinistrySelected" name="substituteMemberMinistrySelected" value="${substituteMemberMinistrySelected }">
<input type="hidden" id="primaryMemberDesignationSelected" name="primaryMemberDesignationSelected" value="${primaryMemberDesignationSelected }">
<input type="hidden" id="substituteMemberDesignationSelected" name="substituteMemberDesignationSelected" value="${substituteMemberDesignationSelected }">
</div> 
</body>
</html>