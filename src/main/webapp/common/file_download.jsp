<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$('document').ready(function(){
			var fileid='${param.fileid}';
			var filetag='${param.filetag}';
			/* filetag !='[object XMLDocument]' is added as temporary solution*/
			if(filetag != '' && filetag !='[object XMLDocument]'){
				$("#errorCode").css("display","none");
				$("#file_"+fileid+"_link").text($("#downloadUploadedFile").val());
				$("#file_"+fileid+"_removeUploadedFile").text($("#removeUploadedFile").val());			
				$('#image_'+fileid).attr("src","file/photo/${param.filetag}");
				$('#image_'+fileid).show();			
				$('#file_'+fileid+'_removeUploadedFile').click(function(){
					var deleteConfirmationPrompt = "";
					if($('#fileid_'+fileid+'_isDeletable').val()=='false') {
						deleteConfirmationPrompt = "Do you really want to change this file?";
					} else {
						deleteConfirmationPrompt = "Do you really want to remove this file?";
					}
					var deleteConfirmation = confirm(deleteConfirmationPrompt);
					if (deleteConfirmation == true) {
						if($('#fileid_'+fileid+'_isDeletable').val()=='false') {
							$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
								$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
								$('#image_'+fileid).attr("src","");
								$('#image_'+fileid).hide();
							});
						} else {
							$.delete_('file/remove/'+filetag,function(data){
								if(data){
									$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
										$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
										$('#image_'+fileid).attr("src","");
										$('#image_'+fileid).hide();
									});
								}
							});
						}						
					}
					/* $.prompt("Do you really want to remove this file?", {
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	$.delete_('file/remove/'+filetag,function(data){
								if(data){
									$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
										$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
										$('#image_'+fileid).attr("src","");
										$('#image_'+fileid).hide();
									});
								}
							});
		    	        } 
				        return false;
					}}); */					
					return false;
				});		
			} else {
				$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
					$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
					$('#image_'+fileid).attr("src","");
					$('#image_'+fileid).hide();
					$("#errorCode").css("display","inline")
				});
				//showList();
			}
		});
	</script>
</head>
</html>
<span id="file_${param.fileid}_downloadUploadedFile" style="display: inline; margin: 25px; padding: 0px;">
	<a id="file_${param.fileid}_link" href="file/${param.filetag}"></a>
	<input type=hidden id="${param.fileid}" name="${param.fileid}" value="${param.filetag}"/>
	<c:if test="${param.isRemovable != false}">
		<a id="file_${param.fileid}_removeUploadedFile" href="#"></a>
	</c:if>	
	<input type="hidden" id="filetag_${param.filetag}" value="${param.filetag}">
	<input type="hidden" id="fileid_${param.fileid}" value="${param.fileid}">	
	<input type="hidden" id="fileid_${param.fileid}_isDeletable" value="${param.isDeletable}">
</span>
