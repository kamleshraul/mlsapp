<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$('document').ready(function(){
			$('#file_${param.fileid}_remove').click(function(){
				$.delete_('file/remove/${param.filetag}',function(data){
					$('#image_${param.fileid}').attr("src","");					
					if(data){
						$.get('./common/file_upload.jsp?fileid=${param.fileid}',function(data){
							$('#file_${param.fileid}_download').replaceWith(data);
						});
					}
				});
			});		
		});
	</script>
</head>
</html>
<span id="file_${param.fileid}_download" style="display: inline; margin: 0px; padding: 0px;">
	<a id="file_${param.fileid}_link" href="file/${param.filetag}">${param.filetag}</a>
	<input type=hidden id="${param.fileid}" name="${param.fileid}" value="${param.filetag}"/>
	<button id="file_${param.fileid}_remove" class="butDef" type="button">
		<spring:message code="generic.remove" text="Remove" />
	</button>
</span>
<script type="text/javascript">
$('#image_${param.fileid}').attr("src","file/photo/${param.filetag}");
</script>