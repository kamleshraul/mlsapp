<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	
	$(document).ready(function(){
		
		//setInterval(function(){ save() }, 3000);
		var isCtrl = false;
		var isShift = false;
		
		
		/****Disable F5 button****/
		document.onkeydown = function(e){
		    //keycode for F5 function
			if(e.keyCode===116){
				return false;
			}
		    
			if (e.ctrlKey || e.metaKey) {
		        switch (String.fromCharCode(e.which).toLowerCase()) {
		        case 's':
		            e.preventDefault();
		            break;
		        case 'r':
		        	e.preventDefault();
		        	break;
		        case 'e':
		        	e.preventDefault();
		        	break;
		        }
		        	
		    }
		};
		
		$('#viewReport').click(function(){
			var params="proceeding="+$('#partProceeding1').val()+
			"&language="+$('#selectedLanguage').val()+
			"&outputFormat=WORD"
			$(this).attr('href','proceeding/proceedingwisereport?'+params);
		});
		
		$('.deviceNo').change(function(){
			var id=this.id;
			var mainId=id.split("deviceNo")[1];
			$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType').val(),function(data){
				if(data!=null){
					$('#dContent').html(data);
					tinyMCE.activeEditor.execCommand('mceInsertContent', false, $('#deviceContent').html());
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
		
		  $( ".formattedMember").autocomplete({
				minLength:3,			
				source:'ref/member/getmembers?session='+$("#session").val(),
				select:function(event,ui){	
					id=this.id;
					var elementCount=id.split("formattedPrimaryMember")[1];
					$("#primaryMember"+elementCount).val(ui.item.id);
					/** Setting the party of the Selected member **/
					$.get('ref/findParty?memberId='+ui.item.id,function(data){
						if(data!=null){
							$('#party option').each(function(){
								if($(this).val()==data){
									 $(this).attr('selected',true);
								 } 
							});
						}
					});
				}	
		  });
		  
		  /***Selecting Member By Party***/
		  $('#party').change(function(){
			 	$.get('proceeding/part/getMemberByPartyPage?partyId='+$(this).val()+'&partCount='+partCount+'&housetype='+$("#selectedHouseType").val(),function(data){
		 			$.fancybox.open(data,{autoSize: false,width:800,height:500});
				},'html'); 
		     return false;
		  });
		  
		  /**** Adding Bookmark ****/
		  $('.addBookmark').click(function(){
				var id=this.id;
				var count=id.split("bookmark");
				elementCount=count[count.length-1];
				$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count=1&currentPart='+$('#partId1').val(),function(data){
					    $.fancybox.open(data, {autoSize: false, width:750, height:500});
				    },'html');
				    return false;
		});
		  
		  /**** TinyMCE related Events ****/
		  $("#partCount").val(1);
		 var partCount = $("#partCount").val(); 
		 var maxPageCount = 2272;
		 var enterCount = 22;
		/*  var previousText = tinyMCE.activeEditor.getContent({format : 'text'});
		 var lineBreaks = (previousText.match(/\n/g)||[]).length;
		 var charCount = previousText.length + lineBreaks; */
		 var pageCount =  maxPageCount;
		 var headerText = "<table>"
							+"<tbody>"
							+"<tr>"
								+"<td class='left' width='200px'></td>"
								+"<td class='center' width='400px'><spring:message code='part.generalNotice' text='Un edited Copy'/></td>"
								+"<td class='right' width='200px'>${r[6]} - ${count}</td>"
							+"</tr>"
							+"<tr>"
								+"<td class='left'>${r[19]}</td>"
								+"<td class='center'><spring:message code='part.previousReporterMessage' text='Previous Reporter'/> ${r[26]}</td>"
								+"<td class='right'>${r[8]}</td>"
							+"</tr>"
						+"</thead>"
						+"</table>";
		  
		  tinyMCE.init({
			    //selector: '#proceedingContent'//,
			    	
			    	 selector: 'textarea',
			    	 elements : "proceedingContent",
			    	  height: 590,
			    	  width:840,
			    	  force_br_newlines : true,
			    	  force_p_newlines : false,
			    	  forced_root_block : "",
			    	  theme: 'modern',
			    	  nonbreaking_force_tab: true,
			    	  entity_encoding : "raw",
			    	  plugins: [
			    	    'advlist autolink lists link image charmap print preview hr anchor pagebreak',
			    	    'searchreplace wordcount visualblocks visualchars code fullscreen',
			    	    'insertdatetime media nonbreaking  table contextmenu directionality',
			    	    'emoticons template paste textcolor colorpicker textpattern imagetools lineheight'
			    	  ],
			    	  toolbar1: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image |fontselect fontsizeselect| lineheightselect',
			    	  toolbar2: 'print preview media | forecolor backcolor emoticons',
			    	  image_advtab: true,
			    	  templates: [
			    	    { title: 'Test template 1', content: 'Test 1' },
			    	    { title: 'Test template 2', content: 'Test 2' }
			    	  ],
			    	  content_css: [
			    	     './resources/css/content.css'
			    	  ],
			    	  fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
			    	  lineheight_formats: "100% 120% 150% 180% 200%",
			    	  font_formats: 'Kokila=kokila,Arial=arial,helvetica,sans-serif;Courier New=courier new,courier,monospace;AkrutiKndPadmini=Akpdmi-n',
			    	 
			    	  setup : function(proceedingContent)
			    	  {
			    		  proceedingContent.on('init',function(){
			    			  style_formats : [
			    			                   {title : 'Line height 20px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '20px'}},
			    			                   {title : 'Line height 30px', selector : 'p,div,h1,h2,h3,h4,h5,h6', styles: {lineHeight: '30px'}}
			    			           ]
			    		  })
			    		 // On key up   
			    		 proceedingContent.on('keyup', function(e) 
			    	    {
			    			 top.tinymce.activeEditor.notificationManager.close();
			    			 var enteredText = tinyMCE.activeEditor.getContent({format : 'text'});
			    			 numberOfLineBreaks = (enteredText.match(/\n/g)||[]).length;
			    			 characterCount = enteredText.length + numberOfLineBreaks;
			    			
			    			 /* if(characterCount >= pageCount){
			    				proceedingContent.execCommand('mcePageBreak',true,this,this);
			    				pageCount = pageCount + maxPageCount;
			    			 } */
			    			
							 var keyCode = e.keyCode || e.which; 
						    	/* if (keyCode == 9) { 
								e.preventDefault(); 
								tinyMCE.activeEditor.execCommand('mceInsertContent', false, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
							} */
						    	
					    	if (e.ctrlKey  || e.metaKey) {
					    		
						      /*   switch (String.fromCharCode(e.which).toLowerCase()) {
						        case 's':
						            e.preventDefault();
						            updatePart();
						            top.tinymce.activeEditor.notificationManager.close();
						            break;
						        } */
						        
						        if(e.ctrlKey && keyCode == 13){
						        	tinyMCE.activeEditor.execCommand('mcePageBreak',true,this,this);
						        	pageCount = pageCount + maxPageCount;
						        }
						    }
					    	
					    	if(e.which == 17) {
								isCtrl = false; 
							}
							if(e.which == 16) {
								isShift = false; 
							}
						
							 
					    		
			    	    });
			    		   
				    	// action on key down
				    	 proceedingContent.on('keydown',function(e) {
				    		if(e.which == 17) {
				    			isCtrl = true;
				    		}
				    		if(e.which == 16) {
				    			isShift = true;
				    		}
				    		if(e.which == 69 && isCtrl && isShift) { 
				    			tinyMCE.activeEditor.execCommand('JustifyCenter',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 89 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyLeft',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 79 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 88 && isCtrl && isShift){
								tinyMCE.activeEditor.execCommand('JustifyFull',true,this,this);
								isCtrl = false; 
								isCtrl = false; 
							}else if(e.which == 85 && isCtrl && isShift){
								updatePart()
							}
				    	}); 
			    			 
			    	  }   
		 		
		 });
	});
	
	
		
	function updatePart(){
			$("#partContent"+$("#partCount").val()).val(tinyMCE.activeEditor.getContent());
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.post($("form[action='proceeding/part/save']").attr('action'),
					$("form[action='proceeding/part/save']").serialize(),function(data){
				 if(data!=null && data!=''){		
					
					 $("#partId"+$("#partCount").val()).val(data.id);
					 $.unblockUI();
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
	<!-- <link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css" media="print" /> -->
	<style type="text/css" >
		.mce-content-body{
			   background: #FFF;
			   font-size: 18px;
			   font-family: Kokila;
			   line-height: 150%;
		}
		
		
		.imageLink{
			width: 14px;
			height: 14px;
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
</head>
<body>
	<!-- <div class="fields clearfix watermark"> -->
		<h2><spring:message code="proceeding.edit.heading" text="Slot : "/>${slotName}	</h2> (<spring:message code="proceeding.edit.heading.timing" text="Slot Time : "/>${slotStartTime} - ${slotEndTime})
		<c:if test="${committeeMeeting != null and committeeMeeting != ''}">
			<spring:message code="proceeding.edit.heading.committeeName" text="Committee : "/>${committeeName}
			<br>
		</c:if>
		<p>
		<a id="viewReport" target="_blank" class="reportLinkl">
			<spring:message code="proceeding.proceedingwiseReport" text="proceeding wise report"/>
		</a>|
		<a href='javascript:void(0)'  id='bookmark${partcount}' class=' addBookmark'><img src='./resources/images/star_full.jpg' title='Bookmark' class='imageLink'/></a>
		</p>

		<div style="border: 2px solid blue;border-radius: 15px;width: 850px;">
			<p style="padding: 10px;">
				<label class='small'><spring:message code='part.memberName' text='Member'/></label>
				<input type='text' class='autosuggest formattedMember sText' name='formattedPrimaryMember' id='formattedPrimaryMember' style="margin-left: 80px;"/>
				<label class='small' style="margin-left: 50px;width:100px;">OR party</label>
				 <select id='party' class='sSelect' style='width:150px;margin-left: 10px'>
					<c:forEach items="${parties}" var="i">
					 	<option value="${i.id}">${i.name}</option>
					</c:forEach>
				</select>
			</p>
			<p>
	
	             <label class='small' style="margin-left: 10px;"><spring:message code='part.deviceType' text='Device Type'></spring:message></label>
	             <select name='deviceType' id='deviceType'class='sSelect' style='width:170px;margin-left: 35px;'>
		     		<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
					<c:forEach items="${deviceTypes}" var="i">
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:forEach>
				</select>
		     	  <label class='small' style='width:123px;margin-left: 65px;'><spring:message code='part.deviceNo' text='Device No'/></label>
				 <input type='text' name='deviceNo' id='deviceNo' class='deviceNo sInteger sText' style='width:168px;'/>
			</p>
		</div>
	
		<br>
		<br>
		<!-- <div style="margin-left:50px"> -->
		<!-- <form id="proceedingForm" action="proceeding"> -->
			<c:choose>
				<c:when test='${!(empty parts)}'>
					<c:forEach items='${parts}' var='outer'>
						<textarea id="proceedingContent" name="procContent">${outer.revisedContent}</textarea>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<textarea id="proceedingContent" name="procContent"/>
				</c:otherwise>
			</c:choose>
		<!-- </form> -->
		<!-- </div> -->
		<form action="proceeding/part/save" method="post">
			<c:choose>
				<c:when test='${!(empty parts)}'>
					<c:forEach items='${parts}' var='outer'>
						<textarea id="partContent1" name="partContent1" style="display:none;">${outer.revisedContent}</textarea>
						<input type='hidden' id="partId1" name="partId1" value="${outer.id}">
					</c:forEach>
				</c:when>
				<c:otherwise>
					<input type="hidden" id="partContent1" name="partContent1" />	
					<input type='hidden' id="partId1" name="partId1">
				</c:otherwise>
			</c:choose>
			  <input type="hidden" id="partCount" name="partCount" value="1"/>
			  <input type="hidden" id="partProceeding1" name="partProceeding1" value="${proceeding}"/>
			  <input type="hidden" id="editingUser" name="editingUser" value="${userName}">
			  <input type="hidden" id="partReporter1" name="partReporter1" value="${reporter}">
			  <input type="hidden" id="partOrder1" name="partOrder1" value="1">
		</form>
		<input type="hidden" id="session" name="session" value="${session}"/>
		<input type="hidden" id="slot" value="${slotId}" name="slot"/>
		<div id='dContent' style="display: none"></div>
	<!-- </div> -->
</body>
</html>