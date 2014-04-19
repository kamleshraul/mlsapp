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
					$('#reportWindow').html(data);				
				}
			}).fail(function(){
				
			});
		});
	</script>
	 <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 840px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 840px;
	        	padding: 10px;
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
		<%@ include file="/common/info.jsp" %>
				<c:choose>
					<c:when test="${size==null}">
						<spring:message code="question.statusreport" text="Status Report"/>
					</c:when>
					<c:when test="${size==0}">
						<spring:message code="question.statusreport" text="Status Report"/>
					</c:when>
					<c:otherwise>
						<div id="reportWindow" style="size: 800px;">s
							v
						</div>
						<div>
						</div>
					</c:otherwise>
				</c:choose>
	</div>
</div>
<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>