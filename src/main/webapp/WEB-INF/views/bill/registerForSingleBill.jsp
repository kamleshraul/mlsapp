<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="bill.register" text="Register" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	
	<script type="text/javascript">
		$(document).ready(function() {		
			$('#registerForSingleBill_pdf').click(function() {
				var resourceURL = 'bill/registerReport?billId='+$("#key").val()+'&outputFormat=PDF';			
				$(this).attr('href', resourceURL);
			});
			
			$('#registerForSingleBill_word').click(function() {
				var resourceURL = 'bill/registerReport?billId='+$("#key").val()+'&outputFormat=WORD';			
				$(this).attr('href', resourceURL);
			});
		});
	</script>
	
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=1" />
	<style type="text/css" media="print" >
		.uiTable{
			color: black !important;
			font-family: serif;
			font-size: 16px !important;
			width: 700px !important;		
			border: 1px;
		}
		.uiTable td{
			padding: 14px;
		}
		.exportLink{			
			display: none !important;
		}		
		.centeralign{
			text-align: center;
		}
		.leftalign{
			text-align: left;
		}			
	</style>
	<style>
		td{min-width:150px; max-width:500px;min-height:30px;}
		th{min-width:150px; max-width:500px;min-height:30px;}
	</style>
</head>
<body>	
	<div id="reportDiv">
		<a id="registerForSingleBill_pdf" class="exportLink" href="#" style="text-decoration: none;">
			<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
		</a>
		&nbsp;
		<a id="registerForSingleBill_word" class="exportLink" href="#" style="text-decoration: none;">
			<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
		</a>
		
		<h2 align="center"><spring:message code="bill.register" text="Bill Register"/></h2>
		
		<table class="uiTable" style="width: 100%; border-right: 0px;">
			<c:forEach var="registerEntry" items="${registerEntries}">
				<c:if test="${registerEntry.value!=''}">
					<tr>
						<td>${registerEntry.key}</td>
						<td>${registerEntry.value}</td>										
					</tr>
				</c:if>
			</c:forEach>		
		</table>
	</div>		
</body>
</html>