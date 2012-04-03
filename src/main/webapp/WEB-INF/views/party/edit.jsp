<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="${urlPattern}.edit.title" text="Edit Party"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$('document').ready(function(){	
			initControls();
			$('#key').val('');	
		});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="${urlPattern}" method="PUT" 
	modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
<form:errors path="version" cssClass="validationError"/>
	
	<p>
		<label class="small"><spring:message code="generic.id" text="Id"/></label>
		<form:input cssClass="sSelect" path="id" readonly="true" /> 
	</p>
	
	<p>
	<label class="small"><spring:message code="${urlPattern}.name" text="Party Name"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="field_error" />	
	</p>
		
	<p>
	<label class="small"><spring:message code="${urlPattern}.abbreviation" text="Abbreviation"/>&nbsp;*</label>
			<form:input cssClass="sSelect" path="abbreviation"/>
			<form:errors path="abbreviation" cssClass="field_error" />	
	</p>
	
	<!--  <p>
		 <div>
		 	 <label class="small"><spring:message code="${urlPattern}.photo.label" text="Party Symbol"/>&nbsp;*</label>	
		 	 <div class="hideDiv" id="photoDiv">
		     <img width="40" height="40" id="photoDisplay"/>
		     </div>
		     <div >
			 <input id="photo" class="sText" readonly="readonly" type="text" value="${photoOriginalName}">
			 <button id="photoRemove" class="btTxt" type="button"><spring:message code="generic.remove" text="Remove"/></button>
			 </div>
			 <form:hidden path="photo" id="photoField"></form:hidden>
			 <form:errors path="photo" cssClass="validationError" />
		 </div>	
		 </p>-->
		 <p>
		 	 <label class="small"><spring:message code="${urlPattern}.photo.label" text="Party Symbol"/></label>
			
		 
		 <c:choose>
		 	 	<c:when test="${empty party.photo}">
			 	 	<jsp:include page="/common/file_upload.jsp">
			 			<jsp:param name="fileid" value="photo" />
			 	  	</jsp:include>
		 	 	</c:when>
		 	 	<c:otherwise>
		 	 		<jsp:include page="/common/file_download.jsp">
		 	 			<jsp:param name="fileid" value="photo" />
			 			<jsp:param name="filetag" value="${party.photo}" />
			 	  	</jsp:include>
		 	 	</c:otherwise>
		 	 
		 	 </c:choose>
		 	 </p>
<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>	
	<form:hidden path="version"/>
<form:hidden path="id"/>
	<form:hidden path="locale"/>
</form:form>
<input type="hidden" id="photo_size" value="${photoSize}">	
<input type="hidden" id="photo_ext" value="${photoExt}">	
<!--  <script type="text/javascript">
$(document).ready(function(){

	/*
	*Photo Upload
	*/	
	if($('#photo').val()==''){
		uploadify('#photo',$('#photo_ext').val(),$('#photo_size').val());
		}else if(($('#photo').val()!='') && ($('#photo').val()!=undefined))
		{
		$('#photoDisplay').attr('src','/els/file/photo/'+$('#photoField').val());
   	$('#photoDiv').removeClass('hideDiv').addClass('showDiv');
		}
	$('#photoRemove').click(function(){
		$.ajax({
		    type: "DELETE",
		    url: "file/remove/"+$('#photoField').val(),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
		    success: function(json) {
		        if(json==true){
		        	$('#photo').val('');
					uploadify('#photo',$('#photo_ext').val(),$('#photo_size').val());
				        alert('File successfully deleted');
		        }
		    },
		    error: function (xhr, textStatus, errorThrown) {
		    	alert(xhr.responseText);
		    }
		});
	   $('#photoDisplay').attr('src','');
   	   $('#photoDiv').removeClass('showDiv').addClass('hideDiv');
	})
});
</script> -->
</div>
</body>
</html>