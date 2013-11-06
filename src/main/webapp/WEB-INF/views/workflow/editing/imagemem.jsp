<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="roster" text="Roster"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css">
		.blue{
			color:blue;
		}
		.green{
			color:green;
		}
		.red{
			color:red;
		}
		
		input[type=button]{
			width:30px;
			margin: 5px;
			font-size: 13px; 
			padding: 5px;
			/*display: inline;*/ 		
		}	
		.highlight{
			font-weight: bold;
		}
		
		/******CSS FOR PAGE *******/
		textarea {
		height: 28px;
		width: 400px;
	}

	/* #textarea {
		-moz-appearance: textfield-multiline;
		-webkit-appearance: textarea;
		border: 1px solid gray;
		font: medium -moz-fixed;
		font: -webkit-small-control;
		height: 28px;
		overflow: auto;
		padding: 2px;
		resize: both;
		width: 400px;
	}

	input {
		margin-top: 5px;
		width: 400px;
	}

	#input {
		-moz-appearance: textfield;
		-webkit-appearance: textfield;
		background-color: white;
		background-color: -moz-field;
		border: 1px solid darkgray;
		box-shadow: 1px 1px 1px 0 lightgray inset;  
		font: -moz-field;
		font: -webkit-small-control;
		margin-top: 5px;
		padding: 2px 3px;
		width: 398px;    
	} */
	
		.replaceMe :hover{
			opacity:1.0;
			/*filter:alpha(opacity=100);*/ /* For IE8 and earlier */
		}
		
		.container{
			margin-left: 50px;
			width: 540px; 
			max-width: 800px; 
			opacity: 0.5; 
			box-shadow: 2px 2px 2px #000000; 
			border: 1px solid black;
		}
		
		div.wysiwyg{
			left: 10px;
			top: 10px;
		}
	
		.textDemo{
			margin-top: 20px; 
			display: none;
			position: fixed; 
			top: 120px; 
			background: scroll repeat-x #000000; 
			width: 620px;
			height: 262px;
			z-index: 10000; 
			box-shadow: 0px 2px 5px #888; 
			opacity:0.8;
		}
		
		#reportIconsDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin-left: 50px; 
			border: 1px solid black; 
			/*background: #0E4269;*/
			
			/*background: -prefix-linear-gradient(top, #0E4269, #59A8E3);*/
			/* The new syntax needed by standard-compliant browsers (Opera 12.1, IE 10, Fx 16 onwards), without prefix */
			background: #feffff; /* Old browsers */
			background: -moz-linear-gradient(top,  #feffff 0%, #d2ebf9 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#feffff), color-stop(100%,#d2ebf9)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #feffff 0%,#d2ebf9 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#feffff', endColorstr='#d2ebf9',GradientType=0 ); /* IE6-9 */
		}
		img:hover{
			border-radius: 32px;
			box-shadow: 2px 2px 2px #0E4269;
		}
		
		#containerTable{
			width: 750px; 
			table-layout: fixed; 
			margin-left: 25px;
		}
	</style>
	<script type="text/javascript">			
	
		var whichId;
		var showIt=0;
	
		function showEditor(e){
			//console.log("Text: " +window.getSelection().toString().trim());
			var pageWidth=$(window).width();
			var pageHeight=$(window).height();
			//console.log("******************************************");
			//console.log("Page: "+ pageWidth+":"+pageHeight);
			var clickX=e.clientX;
			var clickY=e.clientY;
			//console.log("Coordinates and height: "+clickX+":"+clickY+":"+$('#textDemo').height()+":"+(clickY+$('#textDemo').height()));
			//console.log((clickY+$('#textDemo').height())>pageHeight);
			if((clickY+$('#textDemo').height()) > pageHeight){
				var diff=$('#textDemo').height() - clickY;
				//console.log("Diff: "+diff);
				if(diff < 0){
					//console.log("clickY-half of height: "+(clickY - ($('#textDemo').height() / 2)));
					$('#textDemo').css('top', (pageHeight - clickY) + 'px');
				}else{
					$('#textDemo').css('top', diff + 'px');
				}
				
				$('#textDemo').css('left', clickX + 'px');
			}else{
			
				var diff=$('#textDemo').height() - clickY;
				//console.log("Diff: "+diff);
				//console.log("PageHeight-textDemo Height: " + (pageHeight - $('#textDemo').height()));
				$('#textDemo').css('top', clickY + 'px');
				$('#textDemo').css('left', 'auto');
			}
			$("#textDemo").fadeIn();
		} 
		
		function hideEditor(){
			$("#textDemo").fadeOut();
			return false;
		}
		
		function getSelectionText() {
		    var text = "";
		    if (window.getSelection) {
		        text = window.getSelection().toString();
		    } else if (document.selection && document.selection.type != "Control") {
		        text = document.selection.createRange().text;
		    }
		    return text;
		}
		
		function getIframeSelectionText(iframe) {
		  var win = iframe.contentWindow;
		  var doc = iframe.contentDocument || win.document;
	
		  if (win.getSelection) {
			return win.getSelection().toString();
		  } else if (doc.selection && doc.selection.createRange) {
			return doc.selection.createRange().text;
		  }
		}
		
		function setCursor(nodeEx,pos){
		    var node = (typeof nodeEx == "string" ||nodeEx instanceof String) ? $("#"+nodeEx) : nodeEx;
		   
	        if(!node){
	            return false;
	        }else if(node.createTextRange){
	            var textRange = node.createTextRange();
	            textRange.collapse(true);
	            textRange.moveEnd(pos);
	            textRange.moveStart(pos);
	            textRange.select();
	            return true;
	        }else if(node.setSelectionRange){
	            node.setSelectionRange(pos,pos);
	            return true;
	        }
	        return false;
	    }
		
		function saveAndHide(){
			hideEditor();
			var content = $("#ttA").val().trim();
			var newContent=content.replace(/<span.*?>/g,"");
			/* $("#"+whichId).empty();
			//console.log($("#"+whichId)+"showIt:"+showIt);
			$("#"+whichId).html(newContent); */
			
			//console.log("Id: "+whichId+"Content: " + content + "\nNew Content: " + newContent);
			//alert(newContent);
			newContent=newContent.replace("</span>","");
			//alert(newContent);
			
            $("#data").val(newContent);
            if($("#prevcontent").val()!=newContent){
				$.post($("form").attr('action')+'/'+whichId.substring(2),
						$("form").serialize(),function(data){
					if(data=='SUCCESS'){
						$("#"+whichId).empty();
						$("#"+whichId).html(newContent);
					}
				}).fail(function(){
					
				});
            }
            
            //showEditProceeding();
		}
		$(document).ready(function(){
			$('.wysiwyg').wysiwyg({controls: {
			       save: { 
			             visible: true, 
			             tags: ['tt'],  
			             exec: function(e){
			            	 	showIt=0;
			            	 	saveAndHide(e);
			             },
			             hotkey:{"ctrl":1,"shift":1,"key":88}			             
			        	}
			    	},
					events:{keydown:function(e){
         				}
         			}
				});
			$("#data").click(function(){
				if ($(this).children('input').length == 0) { 
					
					var inputbox = "<input type='text' class='inputbox' value=\""+$(this).text()+"\">"; 
					 
					$(this).html(inputbox); 
					 
					$("input.inputbox").focus(); 
					 
					$("input.inputbox").blur(function() { 
						var value = $(this).val(); 
						$("#data").empty(); 
						$("#data").html(value);
					}); 
				} 	
			});
			
			$(".replaceMe").mouseup(function(e){
				if($("#action").val()=='edit'){
					var text=window.getSelection().toString().trim();
					
					whichId = $(this).attr('id');
					
					if(text.length>0){
						if(showIt==0){
							showIt=1;
							showEditor(e);		
							
							var content = $(this).html().trim();
							$("#prevcontent").val(content);
							var newContent=content.replace(/<span.*?>/g,"");
							$(".wysiwyg").wysiwyg('setContent', newContent.replace(text,'<span style="background: yellow;">'+text+'</span>'));
							$("#ttA-wysiwyg-iframe").focus();
						}else if(showIt==1){
							showIt=0;
							saveAndHide(e);
							
							/* var content = $("#ttA").val().trim();
							var newContent=content.replace(/<span.*?>/g,"");
							$("#"+whichId).empty();
							$("#"+whichId).html(newContent); */
							
						}
					}else{
						if(showIt==1){
							showIt=0;
							saveAndHide();
						}
					}
				}
			});
			
			$(".revision").click(function(e){
				$.get("editing/revisions/" + $(this).attr('id').substring(2),function(data){
				    $.fancybox.open(data);
			    });
			    return false;
			});
					
			/*$(".replaceMe").toggle(function(e){
				console.log("Text: " +window.getSelection().toString().trim());
				var pageWidth=$(window).width();
				var pageHeight=$(window).height();
				console.log("******************************************");
				console.log("Page: "+ pageWidth+":"+pageHeight);
				var clickX=e.clientX;
				var clickY=e.clientY;
				console.log("Coordinates and height: "+clickX+":"+clickY+":"+$('#textDemo').height()+":"+(clickY+$('#textDemo').height()));
				console.log((clickY+$('#textDemo').height())>pageHeight);
				if((clickY+$('#textDemo').height()) > pageHeight){
					var diff=$('#textDemo').height() - clickY;
					console.log("Diff: "+diff);
					if(diff < 0){
						console.log("clickY-half of height: "+(clickY - ($('#textDemo').height() / 2)));
						$('#textDemo').css('top', (pageHeight - clickY) + 'px');
					}else{
						$('#textDemo').css('top', diff + 'px');
					}
					
					$('#textDemo').css('left', clickX + 'px');
				}else{
				
					var diff=$('#textDemo').height() - clickY;
					console.log("Diff: "+diff);
					console.log("PageHeight-textDemo Height: " + (pageHeight - $('#textDemo').height()));
					$('#textDemo').css('top', clickY + 'px');
					$('#textDemo').css('left', 'auto');
				}
				$("#textDemo").fadeIn();
				
				//$('.wysiwyg').wysiwyg('focus');
				}, function(){
				//var op = window.open("http://google.com","_blank");
				$("#textDemo").fadeOut();
				return false;
			}); */
			$(".wysiwyg").keypress(function(e){
				//alert(e.keyCode);
			});		
			$('.wysiwyg').change(function(e){
				var html;
				$("#"+whichId ).empty();
				html = $(this).val();
				$("#" + whichId).html(html);
			});
			
			$("#re_edit").click(function(){
				showReEditProceeding();
			});
			
			/* $(document).mousemove(function(e){
				document.title="x: " + e.clientX + "; y: " + e.clientY;
			}); */
			
			$("#editorreport_pdf").click(function(e){
				
				var params="";
				if($("#selectedMember").val()=='-'){
					params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&day=' +$('#selectedDay').val()
					+ '&action=edit'
					+ '&reportType=other'
					+ '&reedit=false'
					+ '&format=PDF';					
				}else if($("#selectedMember").val()!='-'){
					params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&day=' +$('#selectedDay').val()
					+ '&action=edit'
					+ '&reportType=member'
					+ '&reedit=true'
					+ '&member='+ $("#selectedMember").val()
					+ '&format=PDF';
				}
				var reportURL = 'editing/editorreport?' + params;
				$("#editorreport_pdf").attr('href', reportURL);
			});
			
			$("#editorreport_word").click(function(e){
				
				var params="";
				if($("#selectedMember").val()=='-'){
					params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&day=' +$('#selectedDay').val()
					+ '&action=edit'
					+ '&reportType=other'
					+ '&reedit=false'
					+ '&format=WORD';					
				}else if($("#selectedMember").val()!='-'){
					params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&day=' +$('#selectedDay').val()
					+ '&action=edit'
					+ '&reportType=member'
					+ '&reedit=true'
					+ '&member='+ $("#selectedMember").val()
					+ '&format=WORD';
				}
				var reportURL = 'editing/editorreport?' + params;
				$("#editorreport_word").attr('href', reportURL);
			});
		});
	</script>
</head>

<body>
<div>
	<%-- <div id="reportIconsDiv">
		<a id="editorreport_pdf" class="exportLink" href="javascript:void(0);" style="text-decoration: none; margin-left: 40px;" target="_blank">
			<img src="./resources/images/pdf_icon.png" alt="Export to PDF" width="24px" height="32px" title="<spring:message code='editing.editorreport.pdf' text='Editor Report In PDF' />">
		</a>
		&nbsp;&nbsp;
		<a id="editorreport_word" class="exportLink" href="javascript:void(0);" style="text-decoration: none;" target="_blank">
			<img src="./resources/images/word_icon.png" alt="Export to WORD" width="33px" height="32px" title="<spring:message code='editing.editorreport.doc' text='Editor Report In Word' />">
		</a>
		<c:if test="${action=='edit'}">
			<div id="reeditDiv" style="display: inline-block; float: right; margin-right: 5px;"><a href="javascript:void(0);" id="re_edit"><img src="./resources/images/refresh.png" alt="Export to PDF" width="32px" height="32px" title="<spring:message code='editing.reedit' text='Re-edit' />" /></a></div>
		</c:if>
	</div>
</div>	
<div class="fields clearfix watermark">
<form:form action="editing/savepart" modelAttribute="part" method="post">
	<div id="container" class="container">
		<table id="containerTable">
			<c:set var="ph" value="-" />
			<c:set var="mh" value="-" />
			<c:forEach items="${report}" var="r">
				<tr>
					<td>
						<div style="text-align: center; font-size: 16px;">
							<c:if test="${ph!=r[1] && mh!=r[2]}">
								<c:choose>
									<c:when test="${(fn:length(r[1])>0) && (fn:length(r[2])>0)}">
										<b><spring:message code="editing.pageheading" text="Page Heading" /></b>${r[1]}<br />
										<b><spring:message code="editing.mainheading" text="Main Heading" /></b>${r[2]}
									</c:when>
									<c:when test="${(fn:length(r[1])>0) || (fn:length(r[2])>0)}">
										<b><spring:message code="editing.pageheading" text="Page Heading" /></b> ${r[1]} / <b><spring:message code="editing.mainheading" text="Main Heading" /></b> ${r[2]}
									</c:when>
								</c:choose>
							</c:if>
						</div>
						<c:choose>						
							<c:when test="${(action=='edited' or action=='edit') and (reedit=='false')}">
								<c:choose>
									<c:when test="${not (empty r[21])}">
										<b class="member" style="display: inline-block;">
											${r[15]}/  
											<c:if test="${not(empty r[17]) and (not (r[17]==null))}">
												$r[17]
											</c:if>:
										</b>
										<div id="pp${r[20]}" style="width: 750px; max-width: 750px; word-wrap:break-word; display: inline;" class="replaceMe">
											${r[21]}
										</div>
										<div class="revision" id="pd${r[20]}" style="position: relative; border-radius: 10px; margin-left: 750px; top: -20px; text-align: center; display: inline-block; min-width: 16px; min-height: 16px; border: 1px solid blue; background: #00ff00; cursor: pointer;">
											<spring:message code="editing.more" text="S" />
										</div>
									</c:when>
									<c:otherwise>
										<c:if test="${not (empty r[0])}">
											<b class="member" style="display: inline-block;">
												${r[15]}  
												<c:if test="${not(empty r[17]) and (not (r[17]==null))}">
													/${r[17]}
												</c:if>:
											</b>
											<div id="pp${r[20]}" style="width: 750px; max-width: 750px; word-wrap:break-word; display: inline;" class="replaceMe">
												${r[0]}
											</div>
											<div class="revision" id="pd${r[20]}" style="position: relative; border-radius: 10px; margin-left: 750px; top: -20px; text-align: center; display: inline-block; min-width: 16px; min-height: 16px; border: 1px solid blue; background: #013094; cursor: pointer;">
												<spring:message code="editing.more" text="S" />
											</div>
										</c:if>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:if test="${not (empty r[0])}">
									<b class="member" style="display: inline-block;">
										${r[15]}  
										<c:if test="${not(empty r[17]) and (not (r[17]==null))}">
											/${r[17]}
										</c:if>:
									</b>
									<div id="pp${r[20]}" style="width: 750px; max-width: 750px; word-wrap:break-word; display: inline;" class="replaceMe">
										${r[0]}
									</div>
									<div style="position: relative; border-radius: 10px; margin-left: 750px; top: -20px; text-align: center; display: inline-block; min-width: 16px; min-height: 16px; border: 1px solid blue; background: #013094; cursor: pointer;">
										<spring:message code="editing.more" text="S" />
									</div>
								</c:if>
							</c:otherwise>	
						</c:choose>
					</td>
				<tr>
				<tr>
					<td>
						&nbsp;
					</td>
				<tr>
				<c:set var="ph" value="${r[1]}"/>
				<c:set var="mh" value="${r[2]}"/>
			</c:forEach>
		</table>
	</div>
		

	<div id="textDemo" class="textDemo">
		<textarea id="ttA" class="wysiwyg">
			Proceedings
		</textarea>
	</div>
	<input type="hidden" name="editedContent" id="data" value="demo" />
</form:form>
	<input id="reportType" type="hidden" value="${reportType}" />
	<input id="prevcontent" type="hidden" value="" />
	<input id="action" type="hidden" value="${action}" /> 
	<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
	<input id="recreate_slots" value="<spring:message code='ris.recreate_slots' text='Recreate Slots'/>" type="hidden">
	<input id="turnoff_slots" value="<spring:message code='ris.turnoff_slots' text='Turn Off Slots'/>" type="hidden">
	<input id="delete_slots" value="<spring:message code='ris.delete_slots' text='Delete Slots'/>" type="hidden">
	<input id="create_new_slots" value="<spring:message code='ris.create_new_slots' text='Create New Slots'/>" type="hidden">
	<input id="recreate_slots_from_slot_duration_changed_time" value="<spring:message code='ris.recreate_slots_from_slot_duration_changed_time' text='Recreate Slots From Slot Duration Changed Time'/>" type="hidden">
	<input type="hidden" name="pleaseSelectMsg" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'></spring:message>"> --%>	
</div>
</body>
</html>