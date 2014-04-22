<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.statusreport" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$.get('question/report/'+ $("#key").val() + '/currentstatusreportvm?device=question',function(data){
				if(data!=null){
					$('#reportWindow1').html(data);				
				}
			}).fail(function(){
				
			});
			
			$.get('question/report/'+ $("#key").val() + '/currentstatusreportstarredvm?device=question',function(data){
				if(data!=null){
					$('#reportWindow2').html(data);				
				}
			}).fail(function(){
				
			});
		});
	</script>
	 <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 800px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 800px;
	        	padding: 10px;
	        	margin-top: 10px !important;
	        }
	        
	        .page-breakx{
	        	page-break-after: always;
	        }           
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="statusReportDiv">		
		<c:choose>
			<c:when test="${size==null}">
				<spring:message code="question.statusreport" text="Status Report"/>
			</c:when>
			<c:when test="${size==0}">
				<spring:message code="question.statusreport" text="Status Report"/>
			</c:when>
			<c:otherwise>
				<div id="reportWindow" style="size: 600px;">
					<div id="reportWindow1" style="word-wrap: break-word;">
						v
					</div>
					<span class="page-break-before-forced">&nbsp;</span>
					<div id="reportWindow2" style="word-wrap: break-word;">
						v
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>