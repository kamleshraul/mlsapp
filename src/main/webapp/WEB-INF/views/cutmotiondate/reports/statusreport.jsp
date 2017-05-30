<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotiondate.statusreport" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){			
			addRemarkReport($('#cutMotionDateId').val());
		});
		
		function addRemarkReport(cutMotionDateId){
			$.get('cutmotiondate/report/'+ $('#cutMotionDateId').val() + '/currentstatusreportvm?device='+$("#device").val(),
				function(data){						
					$('#reportWindow1').empty();
					$('#reportWindow1').html(data);
			    }
			).fail(function(){
					
			});
		}
	</script>
	 <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 760px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 760px;
	        	padding: 5px;
	        	margin-top: 0px !important;
	        }
	        
	        .tableHeaderColumnLabel{
	        	font-size: 16px !important;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 0px 0px 0px 15px !important;
	        }  
	        
	        hr{
	        	display: none !important;
	        }   
	        
	        span{
	        	display: block !important;
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
	<div id="reportDiv">
		<div id="statusReportDiv">				
			<div id="reportWindow" style="size: 760px;">
				<div id="reportWindow1" style="word-wrap: break-word;">
					v
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="device" value="${device}" /> 
	<input type="hidden" id="reportType" value="${reportType}" />
	<input type="hidden" id="cutMotionDateId" value="${cutMotionDateId}" />
</body>
</html>