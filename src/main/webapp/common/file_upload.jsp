<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$('document').ready(function(){
			/* $('#file_${param.fileid}').fileupload({
				url: 'fileupload?authusername='+$('#authusername').val()
						+'&storageType='+$('#storageType_${param.fileid}').val()
						+'&locationHierarchy='+$('#locationHierarchy_${param.fileid}').val()
						+'&locale='+$('#locale').val(),
				formData:null}); */
			
			$('#file_${param.fileid}').click(function() 
			{
				//console.log("locationHierarchy now: " + $('#locationHierarchy_${param.fileid}').val());
				$('#file_${param.fileid}').fileupload({
					maxFileSize: 1,
					url: 'fileupload?authusername='+$('#authusername').val()
							+'&storageType='+$('#storageType_${param.fileid}').val()
							+'&locationHierarchy='+$('#locationHierarchy_${param.fileid}').val()
							+'&maxFileSizeMB='+$('#maxFileSizeMB_${param.fileid}').val()
							+'&locale='+$('#locale').val(),
					formData:null});
			});
		});
	</script>
</head>
</html>
<span id="file_${param.fileid}_upload" style="display: inline; margin: 0px; padding: 0px;">
	<input id="file_${param.fileid}" type="file" class="sText" />
	<input type=hidden id="${param.fileid}" name="${param.fileid}" value=""/>
	<input type=hidden id="storageType_${param.fileid}" value="${param.storageType}"/>
	<input type=hidden id="locationHierarchy_${param.fileid}" value="${param.locationHierarchy}"/>
	<input type=hidden id="maxFileSizeMB_${param.fileid}" value="${param.maxFileSizeMB}"/>
	<span id="errorCode" style="display:none;">Invalid File... Kindly upload proper file</span>
	<span id="file_${param.fileid}_progress" style="display: none;">File uploading. Please wait...</span>
</span>