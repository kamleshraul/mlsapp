<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$('document').ready(function(){
			var fileid='${param.fileid}';
			var filetag='${param.filetag}';
			
			var storageType = $('#storageType_'+fileid).val();
			var locationHierarchy = $('#locationHierarchy_'+fileid).val();
			var maxFileSizeMB = $('#maxFileSizeMB_'+fileid).val();
			
			if($('#image_'+fileid).length>0) {
				$('#file_'+fileid+'_downloadUploadedFile').css("margin-left", '25px');
			} 
			else {
				$('#file_'+fileid+'_downloadUploadedFile').css("margin-left", '-5px');
			}
			
			/* filetag !='[object XMLDocument]' is added as temporary solution*/
			if(filetag != '' && filetag !='[object XMLDocument]'){
				var strFileTag = JSON.stringify(filetag);
				if(strFileTag.indexOf("size_exceeded") != -1) {
					$.get('./common/file_upload.jsp?fileid='+fileid+'&storageType='+storageType+'&locationHierarchy='+locationHierarchy+'&maxFileSizeMB='+maxFileSizeMB,function(dataupload){
						$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
						$('#image_'+fileid).attr("src","");
						$('#image_'+fileid).hide();
						$("#errorCode").html("Please limit uploading file with size <= " + filetag.split("_")[0]);
						$("#errorCode").css("display","inline");
					});
				}
				else {
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
								$.get('./common/file_upload.jsp?fileid='+fileid+'&storageType='+storageType+'&locationHierarchy='+locationHierarchy+'&maxFileSizeMB='+maxFileSizeMB,function(dataupload){
									$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
									$('#image_'+fileid).attr("src","");
									$('#image_'+fileid).hide();
								});
							} else {
								$.delete_('file/remove/'+filetag,function(data){
									if(data){
										$.get('./common/file_upload.jsp?fileid='+fileid+'&storageType='+storageType+'&locationHierarchy='+locationHierarchy+'&maxFileSizeMB='+maxFileSizeMB,function(dataupload){
											$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
											$('#image_'+fileid).attr("src","");
											$('#image_'+fileid).hide();
										});
									}
								});
							}						
						}			
						return false;
					});
				}						
			} else {
				$.get('./common/file_upload.jsp?fileid='+fileid+'&storageType='+storageType+'&locationHierarchy='+locationHierarchy+'&maxFileSizeMB='+maxFileSizeMB,function(dataupload){
					$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
					$('#image_'+fileid).attr("src","");
					$('#image_'+fileid).hide();
					$("#errorCode").css("display","inline");
				});
			}
		});
	</script>
</head>
</html>
<span id="file_${param.fileid}_downloadUploadedFile" style="display: inline; margin: 25px; padding: 0px;">
	<a id="file_${param.fileid}_link" href="file/${param.filetag}"></a>
	<input type=hidden id="${param.fileid}" name="${param.fileid}" value="${param.filetag}"/>
	<c:if test="${param.isRemovable != false}">
		<a id="file_${param.fileid}_removeUploadedFile" href="#" style="margin-left: 10px;"></a>
	</c:if>	
	<input type="hidden" id="filetag_${param.filetag}" value="${param.filetag}">
	<input type="hidden" id="fileid_${param.fileid}" value="${param.fileid}">	
	<input type="hidden" id="fileid_${param.fileid}_isDeletable" value="${param.isDeletable}">
	<input type=hidden id="storageType_${param.fileid}" value="${param.storageType}"/>
	<input type=hidden id="locationHierarchy_${param.fileid}" value="${param.locationHierarchy}"/>
	<input type=hidden id="maxFileSizeMB_${param.fileid}" value="${param.maxFileSizeMB}"/>
</span>
