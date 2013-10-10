<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="part.citation"	text=" Proceeding Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
$(document).ready(function(){	
	$('.wysiwyg').wysiwyg();
	
	});
</script>
</head>
<body>
<div class="fields clearfix watermark">	
	
	<p>
	<label class="small"><spring:message code="bookmark.bookmarkkey" text="Bookmark key"/></label>
	<input type="text" id="bookmarkkey" name="bookmarkkey" value="${bookmarkKey}"/>
	</p>
	<p>
	<label class="small"><spring:message code="bookmark.reporterName" text="Reporter"/>*</label>
	<input type="text" id="reporterName" name="reporterName" value="${reporter}"/>
	</p>
	<p>
	<label class="small"><spring:message code="bookmark.memberName" text="Member Name"/>*</label>
	<input type="text" id="memberName" name="memberName" value="${memberName}"/>
	</p>
	<p>
	<label class="wysiwyglabel"><spring:message code="bookmark.previousContent" text="Previous Content"/>*</label>
	<textarea id="previousContent" name="previousContent" class="wysiwyg" >${previousText}</textarea>
	</p>
	</div>
</html>