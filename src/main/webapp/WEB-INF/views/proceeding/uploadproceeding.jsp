<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />	
	<script type="text/javascript">
	$('#file_document_removeUploadedFile').click(function(){
		var deleteConfirmationPrompt = "Do you really want to remove this file?";
		var deleteConfirmation = confirm(deleteConfirmationPrompt);
		if (deleteConfirmation == true) {
			$.delete_('proceeding/remove/'+$("#document").val()+"?proceedingId="+$("#id").val()+"&driveOrDatabase=drive",function(data){
				if(data){
					$.get('proceeding/'+$("#id").val()+'/uploadproceeding',function(data){
						$(".tabContent").html(data);
						/* $('#downloadUploadedFile').replaceWith(dataupload); */
					});
				}
			});
			 					
		}
		return false;
	});
	
	
	$( '#proceedingForm' )
	  .submit( function( e ) {
	    $.ajax( {
	      url: 'proceeding/uploadproceeding?proceedingId='+$("#id").val(),
	      type: 'POST',
	      data: new FormData( this ),
	      processData: false,
	      contentType: false,
	      success: function(data){
	    	   $.get('proceeding/'+$("#id").val()+'/uploadproceeding',function(data){
					$(".tabContent").html(data);
					/* $('#downloadUploadedFile').replaceWith(dataupload); */
			  });
	      }
	    } );
	    e.preventDefault();
	  } );
	
	/* $( "input:file" ).change(function(){
		
		$.ajax({ 
		    type: "POST",
		    contentType: "multipart/form-data" ,
		    url: "proceeding/uploadproceeding",
		    success: function( data ) { 
		        alert( data );  
		    }  
		}); 
		 $.post($('#proceedingForm').attr('action'),  
	            $("#proceedingForm").serialize(),  
	            function(datauploaded){
					if(datauploaded){
						$.get('proceeding/'+$("#id").val()+'/uploadproceeding',function(data){
							$(".tabContent").html(data);
							 $('#downloadUploadedFile').replaceWith(dataupload); 
						});
					}
	            }); 
		
	}); */
	
	
	</script>
	<style type="text/css">
		
	</style>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${error!=''}">
		<h3 style="color: #FF0000;">${error}</h3>
	</c:if>
	<div class="fields clearfix watermark">
	<form:form id="proceedingForm" action="proceeding/uploadproceeding" method="POST" modelAttribute="domain" enctype="multipart/form-data"> 
		<%@ include file="/common/info.jsp" %>
		<h2><spring:message code="proceeding.edit.heading" text="Proceeding:ID"/>(${domain.id})	</h2>
		<p>
			<label class="small"><spring:message code="proceeding.document" text="Upload Document"/></label>
			<c:choose>		
				<c:when test="${empty domain.documentId}">
					<span id="file_upload" style="display: inline; margin: 0px; padding: 0px;">
						<input id="file" type="file" name="file" class="sText" />
						<span id="file_upload_progress" style="display: none;">File uploading. Please wait...</span>
					</span>
				</c:when>
				<c:otherwise>		
					<span id="downloadUploadedFile" style="display: inline; margin: 25px; padding: 0px;">
						<label>${documentName}</label>
						<a id="file_document_link" href="proceeding/${domain.documentId}?driveOrDatabase=drive"><spring:message code="proceeding.download" text="download"/></a>
						<input type=hidden id="document" name="document" value="${domain.documentId}"/>
						<a id="file_document_removeUploadedFile" href="#"><spring:message code="proceeding.remove" text="remove"/></a>
						<input id="filetag_${domain.documentId}" type="hidden" value="${domain.documentId}">
							
					</span>
				</c:otherwise>
			</c:choose>		
			<form:errors path="documentId" cssClass="validationError" />
		</p>
		<div class="fields expand">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit"
					value="<spring:message code='generic.submit' text='Submit'/>"
					class="butDef"> 
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
					
			</p>
		</div>
		<form:hidden path="locale" />
		<form:hidden path="id" />
		<form:hidden path="version" />
		<form:hidden path="slot" value="${slot}"/>
		<input type="hidden" name="driveOrDatabase" id="driveOrDatabase" value="drive"/>
	</form:form>
	
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
</body>
</html>