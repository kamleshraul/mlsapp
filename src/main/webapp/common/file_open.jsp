<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$('document').ready(function(){
			var fileid=$("#fileid").val();
			var filetag=$("#filetag").val();
			if(filetag != ''){
				$("#file_"+fileid+"_link").text($("#downloadUploadedFile").val());
				$("#file_"+fileid+"_removeUploadedFile").text($("#removeUploadedFile").val());			
				$('#image_'+fileid).attr("src","file/photo/${param.filetag}");
				$('#image_'+fileid).show();			
				$('#file_'+fileid+'_removeUploadedFile').click(function(){
					$.delete_('file/remove/'+filetag,function(data){
						if(data){
							$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
								$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
								$('#image_'+fileid).attr("src","");
								$('#image_'+fileid).hide();
							});
						}
					});
					return false;
				});		
			} else {
				showList();
			}
		});
	</script>
</head>
</html>
<span id="file_${param.fileid}_downloadUploadedFile" style="display: inline; margin: 25px; padding: 0px;">
	<a id="file_${param.fileid}_link" href="file/${param.filetag}/open" target="_blank"></a>
	<input type=hidden id="${param.fileid}" name="${param.fileid}" value="${param.filetag}"/>
	<c:if test="${param.isRemovable != false}">
		<a id="file_${param.fileid}_removeUploadedFile" href="#"></a>
	</c:if>
	<input type="hidden" id="filetag" value="${param.filetag}">
	<input type="hidden" id="fileid" value="${param.fileid}">	
</span>
