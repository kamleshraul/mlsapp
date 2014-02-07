<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.part.proceedingwisereport" text="Proceeding Wise Report"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	var controlId='';
	function hideEditor(){
		$("#textDemo").fadeOut();
		return false;
	}
	
	function showEditor(e){
		
		var pageWidth=$(window).width();
		var pageHeight=$(window).height();
		
		var clickX=e.clientX;
		var clickY=e.clientY;
		
		/****To Adjust the location of the editor when shown to the user by height****/ 
		if((clickY+$('#textDemo').height()) > pageHeight){
			var diffH=$('#textDemo').height() - clickY;
			if(diffH < 0){
				$('#textDemo').css('top', (pageHeight - clickY) + 'px');
			}else{
				$('#textDemo').css('top', diffH + 'px');
			}
		}else{
		
			var diffH=$('#textDemo').height() - clickY;
			$('#textDemo').css('top', clickY + 'px');
		}
		
		/****To Adjust the location of the editor when shown to the user by width****/
		if((clickX+$('#textDemo').width()) > pageWidth){
			var diffW=$('#textDemo').width() - clickX;
			
			if(diffW < 0){
				$('#textDemo').css('left', (pageWidth - clickX) + 'px');
			}else{
				$('#textDemo').css('left', diffW + 'px');
			}
			
		}else{
		
			var diffW=$('#textDemo').width() - clickX;
			$('#textDemo').css('left', clickX + 'px');
		}
		$("#textDemo").fadeIn();
	} 
	
	function saveAndHide(){
		hideEditor();
		var content = $("#ttA").val().trim();
		var newContent=content.replace(/<span.*?>/g,"");
		newContent=newContent.replace("</span>","");
		
		var tempId=controlId.split('-');
		$("#undoCount").val(parseInt($("#undoCount").val()) + 1);			
		if($("#undoCount").val()=='1'){
			$("#redoCount").val('0');
				$("#pprp"+tempId[1]).empty();
				$("#pprp"+tempId[1]).html('classes');
		}
		var params = "?editedBy="+$('#editingUser').val()+"&partField="+tempId[0]+"&partId="+tempId[1]+"&undoCount="+$("#undoCount").val();
		
		$("#data").val(newContent);
			
        if($("#prevcontent").val()!=newContent){
        	$.post($("form[action='proceeding/part/updatePart']").attr('action')+params,
					$("form[action='proceeding/part/updatePart']").serialize(),function(data){
				if(data!=null){		
					//var memberContent=$('#'+controlId).children()[1].id;
					$("#"+controlId).empty();
					$("#"+controlId).html(newContent);
					/* alert(data);
					showEditProceeding(); */
					var undoData=$("#ppsp"+tempId[1]).html();;
					var draftData=data.value;
					if(undoData=='classes' || undoData==''){
						$("#ppsp"+data.id).empty();
						$("#ppsp"+data.id).html(draftData);
					}else{
						$("#ppsp"+data.id).html(undoData+";"+data.value);
					}
				} else{
					$("#undoCount").val((parseInt($("#undoCount").val()) - 1));
				} 
			}).fail(function(){
			});
        }
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
	
	
	
	function replaceAll(command,reedit){
		var params='proceedingId='+$('#proceedingId').val();
		$("#undoCount").val(parseInt($("#undoCount").val()) + 1);	
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.post($("form[action='proceeding/part/replace']").attr('action')+"?"+params,
				$("form[action='proceeding/part/replace']").serialize(),function(data){
			if(data.length>0){
				var i;
				for(i = 0; i < data.length; i++){
					if(data[i][12]=='include'){
						$("#mainHeading-"+data[i][0]).empty();
						$("#mainHeading-"+data[i][0]).html(data[i][6]);
						$("#pageHeading-"+data[i][0]).empty();
						$("#pageHeading-"+data[i][0]).html(data[i][9]);
						$("#procContent-"+data[i][0]).empty();
						$("#procContent-"+data[i][0]).html(data[i][3]);
						
						var undoData=$("#ppsp"+data[i][0]).html();
						
						if(undoData=='classes' || undoData==''){
								$("#ppsp"+data[i][0]).empty();
								$("#ppsp"+data[i][0]).html(data[i][10]);
						}else{
							$("#ppsp"+data[i][0]).html(undoData+";"+data[i][10]);
						}
					}
				}
				$.unblockUI();
			}else{
				$.unblockUI();
				$("#undoCount").val((parseInt($("#undoCount").val()) - 1));	
			}
		}).fail(function(){
			$.unblockUI();
		});
	}
	
	function undoLastChange(){
		$(".ppsp").each(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var undoData=$(this).html();
			if(undoData!='' && undoData!='classes'){
				var undoDataArray = undoData.split(";");
				var undoDataToWorkWith=undoDataArray[undoDataArray.length-1].split(":");
				console.log(undoDataArray);
				$("#uniqueIdentifierForUndo").val(undoDataToWorkWith[2]);
				var redoData=$("#pprp"+$(this).attr('id').substring(4)).html();
				
				var ppId=$(this).attr('id').substring(4);
				
				$.post("proceeding/part/undolastchange/"+ppId,
						$("form[action='proceeding/part/replace']").serialize(),function(data){
					if(data){
						$("#mainHeading-"+ppId).empty();
						$("#mainHeading-"+ppId).html(data.mainHeading);
						$("#pageHeading-"+ppId).empty();
						$("#pageHeading-"+ppId).html(data.pageHeading);
						$("#procContent-"+ppId).empty();
						$("#procContent-"+ppId).html(data.content);
						
						
						var pprpData=$("#pprp"+ppId).html();
						
						var redoCountX=undoDataArray[undoDataArray.length-1].split(":")[1];
						if(redoCountX==''){
							redoCountX='0';
						}
						
						if(pprpData=='classes' || pprpData==''){
							$("#pprp"+ppId).empty();
							$("#pprp"+ppId).html(undoDataArray[undoDataArray.length-1]);
							$("#redoCount").val(parseInt($("#redoCount").val())+1);
						}else{
							$("#pprp"+ppId).html(pprpData+";"+undoDataArray[undoDataArray.length-1]);
							$("#redoCount").val(parseInt($("#redoCount").val())+1);
						}
						
						$("#undoCount").val(parseInt($("#undoCount").val())-1);
						var html="";
						if(undoDataArray.length>1){
							html=$("#ppsp"+ppId).html().replace(";"+undoDataArray[undoDataArray.length-1],"");
						}else{
							html=$("#ppsp"+ppId).html().replace(undoDataArray[undoDataArray.length-1],"");
						}
						$("#ppsp"+ppId).html(html);
						
						$.unblockUI();
					}
				}).fail(function(){
					$.unblockUI();
				});
			}
			$.unblockUI();
		});
	}
	
	function redoLastChange(){
		$(".pprp").each(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var redoData=$(this).html();
			if(redoData!='' && redoData!='classes' ){
				var redoDataArray = redoData.split(";");
				var redoDataToWorkWith=redoDataArray[redoDataArray.length-1].split(":");
				$("#uniqueIdentifierForRedo").val(redoDataToWorkWith[2]);
				var html="";
				if(redoDataArray.length>1){
					html=$(this).html().replace(";"+redoDataArray[redoDataArray.length-1],"");
				}else{
					html=$(this).html().replace(redoDataArray[redoDataArray.length-1],"");
					
				}
				var redoData=$("#pprp"+$(this).attr('id').substring(4)).html();
				
				var ppspData=$("#pprp"+ppId).html();
				if(ppspData=='classes' || ppspData==''){
					$("#ppsp"+ppId).empty();
					$("#ppsp"+ppId).html(redoDataArray[redoDataArray.length-1]);
					//$("#undoCount").val(parseInt($("#undoCount").val())+1);
				}else{
					$("#ppsp"+ppId).html(ppspData+";"+redoDataArray[redoDataArray.length-1]);
					//$("#undoCount").val(parseInt($("#undoCount").val())+1);
				}
				$("#urData").val(redoDataArray[redoDataArray.length-1]);
				
				var ppId=$(this).attr('id').substring(4);
				$(this).html(html);			
				$.post("proceeding/part/redolastchange/"+ppId,
						$("form[action='proceeding/part/replace']").serialize(),function(data){
					if(data){
						$("#mainHeading-"+ppId).empty();
						$("#mainHeading-"+ppId).html(data.mainHeading);
						$("#pageHeading-"+ppId).empty();
						$("#pageHeading-"+ppId).html(data.pageHeading);
						$("#procContent-"+ppId).empty();
						$("#procContent-"+ppId).html(data.content);
						$("#redoCount").val(parseInt($("#redoCount").val())-1);
						$.unblockUI();
					}
				}).fail(function(){
					$.unblockUI();
				});
			}
			$.unblockUI();
		});
	}
		$(document).ready(function(){
			var outputFormat="";
			
			$('.wysiwyg').wysiwyg({controls: {
			       save: { 
			             visible: true, 
			             tags: ['tt'],  
			             exec: function(e){
			            	 	showIt=0;
			            	 	saveAndHide(e);
			             },
			             hotkey:{"ctrl":1,"shift":1,"key":88}			             
			        	},
			        close:{
			        	visible:true,
			        	tags:['close'],
			        	exec:function(e){
			        		hideEditor();
			        	}
			        }
			    	},
					events:{keydown:function(e){
      				}
      			}
				});
				$('#viewReport').click(function(){
					var params="proceeding="+$('#proceedingId').val()+
					"&language="+$('#languageId').val()+
					"&outputFormat="+outputFormat;
					$(this).attr('href','proceeding/proceedingwisereport?'+params);
				});
				$('#outputFormat').change(function(){
					outputFormat=$('#outputFormat').val();
				});
				
				$('.editableContent').mouseup(function(e){
					 controlId=$(this).attr('id');
					 showEditor(e);
					$('#prevContent').val($('#'+controlId).html());
					$('#ttA').wysiwyg('setContent',$('#'+controlId).html());
				});
				
				
				$("#replaceAll").click(function(){
					replaceAll('edit','false');
				});
				
				$("#undo").click(function(){
					var undoCount = parseInt($("#undoCount").val());
					if(undoCount>0){
						undoLastChange();
					}
				});
				
				$("#redo").click(function(){
					var redoCount = parseInt($("#redoCount").val());
					if(redoCount>0){
						redoLastChange();
					}
				});
				
		});		
		
	</script>
	
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=4" />
	
	<style type="text/css" media="print">
		.doBreak{
			page-break-after: always !important;
			
		}
		#reportDiv{
			width: 800px;
			margin: 0px 0px 0px 75px;
			
		}
	</style>
	<style type="text/css" media="all">
		table{
		width:800px;
		font-size: 14px;
		}
		.content{
			text-align:justify;
		}
		.right{
			text-align: right;	
			width: 100px;
			max-width: 100px;		
		}
		.left{
			text-left:left;
			width: 100px;
			max-width: 100px;
		}
		.center{
			text-align: center;
			width: 600px;
			max-width: 600px;
		}
		
		.reportLink{
			cursor: pointer;
			
		}
		.reportLink:hover{
			text-shadow: 1px 1px gray;
		}
		
		#reportDiv table ul{
			background: white !important;
		}
		
		div.wysiwyg{
			left: 1px;
			top: 16px;
			width: 600px !important;
		}
		.textDemo{
			margin-top: 20px; 
			display: none;
			position: fixed; 
			top: 120px; 
			background: scroll repeat-x #000000; 
			width: 610px;
			height: 265px;
			z-index: 10000; 
			box-shadow: 0px 2px 5px #888; 
			/* opacity:0.8; */
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
		
		#replaceToolDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin: 10px 0px 0px 50px; 
			border: 1px solid black; 
			min-height: 25px;
			position: relative;
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
		
		.imgI:hover{
			border-radius: 32px;
			box-shadow: 2px 2px 2px #0E4269;
		}
		.imgN:hover{
			/*border-radius: 32px;*/
			box-shadow: 2px 2px 2px #0E4269;
		}
		
	</style>
</head>

<body>
<a id="viewReport" target="_blank" class="reportLinkl">report</a>
<c:if test="${not empty outputFormats}">				
	<select id="outputFormat" name="outputFormat">
		<option value="" selected="selected">Please Select Output Format</option>
		<c:forEach items="${outputFormats}" var="i">
			<option value="${i.value}">${i.name}</option>
		</c:forEach>
	</select>				
</c:if>
<div id="replaceToolDiv">
			<form action="proceeding/part/replace" method="post">
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.searchTerm" text="Find" /></label><input type="text" id="searchTerm" name="searchTerm" value="${searchTerm}" style="border-radius: 3px; border: 1px solid #000080;" />
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.replaceTerm" text="Replace With" /></label><input type="text" id="replaceTerm" name="replaceTerm" value="${replaceTerm}"  style="border-radius: 3px; border: 1px solid #000080;"/>
				<a href="javascript:void(0);" id="replaceAll" style="width: 70px; border: 1px solid #000080; text-decoration: none; text-align: center; color: #000080; padding: 1px; border-radius: 5px;"><spring:message code='editing.replace.replaceAll' text='Replace'></spring:message></a>
				<a href="javascript:void(0);" id="undo"><img src="./resources/images/undo.png" width="16px" /></a>&nbsp;&nbsp;<a href="javascript:void(0);" id="redo"><img src="./resources/images/redo.png" alt="Redo" width="16px"/></a>
				<input type="hidden" name="undoCount" id="undoCount" value="${undoCount}" />
				<input type="hidden" name="uniqueIdentifierForUndo" id="uniqueIdentifierForUndo" value="" />
				<input type="hidden" name="redoCount" id="redoCount" value="${redoCount}" />
				<input type="hidden" name="uniqueIdentifierForRedo" id="uniqueIdentifierForRedo" value="" />
				<input type="hidden" name="urData" id="urData" value="" />
			</form> 
</div>
<div class="fields clearfix watermark" style="margin-top: 30px">
		
<form action="proceeding/part/updatePart" method="post">
	<%@ include file="/common/info.jsp" %>
	
	<div id="reportDiv" align="center">
		<c:set var="slot" value="" />	
		<c:set var="mheading" value="" />
		<c:set var="pheading" value="" />
		<c:set var="member" value=""/>
		<c:set var="count" value="1" />
		<c:set var="chairPerson" value=""/>
		<%--${report[0][3]} --%>		
		<c:forEach items="${report}" var="r" varStatus="i">
			<c:choose>
				<c:when test="${slot==r[6]}">	
					<c:choose>
						<c:when test="${mheading!=r[2] and pheading!=r[1] }">
						</table>
						<table class="doBreak">
							<thead>
								<tr>
									<th class="left">${r[7]}</th>
									<th class="center">${generalNotice}</th>
									<th class="right">${r[6]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r[19]}</th>
									<th class="center">${r[18]}</th>
									<th class="right">${r[8]}</th>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</thead>
							<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
							</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings-${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading-${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading-${r[20]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading${r[20]}" style="display: inline;">${r[2]}</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
						
					<tr>
					<td id="pContent${r[20]}" colspan="3" style="text-align: justify;"  >
						<div id="procMember-${r[20]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
												    <c:otherwise>
												    	<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
												    </c:otherwise>	
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r[16]!=null }">
												<c:choose>
													<c:when test="${r[12] !=null}">
														<c:choose>
															<c:when test="${r[13]!=null }">
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
					</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:when>
					<c:otherwise>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<tr>
						<td id="pContent${r[20]}" colspan="3" class="content"  >
						<div id="procMember-${r[20]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null and r[14]!=member}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
												    <c:otherwise>
												    	<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
												    </c:otherwise>	
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r[16]!=null }">
												<c:choose>
													<c:when test="${r[12] !=null}">
														<c:choose>
															<c:when test="${r[13]!=null }">
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:otherwise>
					</c:choose>
					
				</c:when>
				<c:otherwise>
					</table>
					
					<table class="doBreak">
						<thead>
							<tr>
								<th class="left">${r[7]}</th>
								<th class="center">${generalNotice}</th>
								<th class="right">${r[6]} - ${count}</th>
							</tr>
							<tr>
								<th class="left">${r[19]}</th>
								<th class="center">${r[18]}</th>
								<th class="right">${r[8]}</th>
							</tr>
							<tr><td colspan="3" height="30px"></td></tr>
						</thead>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chaiPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading${r[20]}" colspan="3" style="text-align: center;"  >
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading${r[20]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
					<tr>
					<td id="pContent${r[20]}" colspan="3" style="text-align: justify;">
						<div id="procMember-${r[20]}" style="display: inline-block;">
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
												    <c:otherwise>
												    	<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
												    </c:otherwise>	
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r[16]!=null }">
												<c:choose>
													<c:when test="${r[12] !=null}">
														<c:choose>
															<c:when test="${r[13]!=null }">
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent" >
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
					</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
				</c:otherwise>						
			</c:choose>
				
			<p id="page-number"></p>
			<c:set var="count" value="${count + 1}" />
			<c:set var="slot" value="${r[6]}" />
			<c:set var="mheading" value="${r[2]}" />
			<c:set var="pheading" value="${r[1]}" />
			<c:set var="member" value="${r[14]}"/>
			<c:set var="chairPerson" value="${r[9]}"/>
		</c:forEach>
		
	</div>	
	
	<input type="hidden" id="proceedingId" name="proceedingId" value="${proceeding}">
	<input type="hidden" id="languageId" name="languageId" value="${language}">
	<input type="hidden" id="editingUser" name="editingUser" value="${userName}">
	<input id="prevcontent" type="hidden" name ="prevContent" value="" />
</div>
<input type="hidden" name="editedContent" id="data" value="demo" />
</form>
<div id="textDemo" class="textDemo">
	<textarea id="ttA" class="wysiwyg">
		Proceedings
	</textarea>
</div>
</body>
</html>