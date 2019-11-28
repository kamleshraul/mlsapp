<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.print_credentials" text="Member Credentials Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//load instructions for accessing system
			$.get('member/printCredentials/instructions_vm',function(data){					 								
				$('#instructionsWindow').empty();
				$('#instructionsWindow').html(data);
			}).fail(function(){				 
			});
			//print pdf
			$('#Generate_PDF').click(function () { 
				
				alert($("#house").val())
				alert($("#houseType").val())
				alert($("#key").val())
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
		
				resourceURL = "member/printCredentials?house="+$('#house').val()+'&houseType='+$("#houseType").val()+ '&member=' + row;
				
				form_submit( "member/printCredentials?house="+$('#house').val()+'&houseType='+$("#houseType").val()+ '&member=' + row, parameters, 'GET');
				$.unblockUI();
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
	        }
	        table{
	        	border: 1px solid black;
	        	width: 100%;
	        }
	        
	        @page {
			  size: auto;
			  margin: 20px 20px 20px 30px;
			}
			
			div#statsReportDiv{
				margin-left: 120px;
			}
			
			div#statHeaderDiv{
				margin-left: 180px;
			}
			
			div#typistDiv{
				margin: 50px 10px 10px 100px;
				width: 850px;				
			}
			
			div#typistDiv h2{
				width: 100%;
				text-align: center;
			}
			
			div#typistDiv table thead {display: table-header-group; margin-top: 50px;}
			
			div#rep8NoData{
				display: none;
			}       
	        div#instructionsWindow{
	        	width: 950px !important;
	        	font-size: 19px !important;
	        }
	        span#websiteURL {
	        	font-size: 21px !important;
	        	font-weight: bold;
	        }
	        div#footerDiv{
	        	margin-top: 350px;
	        }
	        hr {
	        	align: left;
	        	width: 100%;
	        }
	        p#footer_paragraph{
	        	font-size: 14pt !important;
	        }
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        table#reportTable th{
        	text-align: center;
        	font-weight: bold;
        	font-size: 14px;
        }
        
        table#reportTable td{
        	text-align: center;
        	font-weight: bold;
        	font-size: 20px;
        	height: 50px;
        }
        
        table#reportTypistTable th{
        	text-align: center;
        }
        
        table#reportTypistTable td{
        	font-size: 13px;
        }
        
        table#reportTypistTable .center{
        	text-align: center;
        }
        
        table#reportTable .finalTotal{
        	height: 20px !important;
        }        
        
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="statHeaderDiv">
		<h2 style="color: black !important; margin-left: 150px;">
			<spring:message code="login.vidhanmandal" text="Member Info"/>
		</h2>
		<h2 style="color: black !important; margin-left: 200px;">
			<spring:message code="Member.printCredential" text="Member Info"/>
		</h2>
		
		<br />	
		<br />	
		<br />	
	
	</div>
	<!-- <div style="text-align: right">
		<a href="#" id="Generate_PDF">
			<img src="./resources/images/pdf_icon.png" style="width:25px;height:25px;">
		</a>
	</div> -->
	<div id="statsReportDiv">
		<table border="1" id="reportTable" class="strippedTable">
			<thead>
			<th>
			</th>
			<th>
			</th>
			</thead>
			<tbody>
				<c:forEach items="${report}" var="r" varStatus="counter">
				<tr>
					<td class="finalTotal">
						<spring:message code="mis.report.cred.name" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty r[1]}">
								-
							</c:when>
							<c:otherwise>
								${r[1]} ${r[3]} ${r[4]} ${r[5]}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td class="finalTotal">
						<spring:message code="mis.report.cred.nameeng" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty r[2]}">
								-
							</c:when>
							<c:otherwise>
								${r[2]} ${r[6]} ${r[7]} ${r[8]}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
					<tr>
					<td class="finalTotal">
						<spring:message code="mis.report.cred.namealias" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty r[13]}">
								-
							</c:when>
							<c:otherwise>
								${r[13]} 
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td class="finalTotal">
						<spring:message code="mis.report.cred.username" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty r[11]}">
								-
							</c:when>
							<c:otherwise>
								${r[11]} 
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
					<tr>
					<td class="finalTotal">
						<spring:message code="mis.report.cred.password" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty r[12]}">
								-
							</c:when>
							<c:otherwise>
								${r[12]} 
							</c:otherwise>
						</c:choose>
					</td>
				</tr>	
				
				</c:forEach>
			
			</tbody>
		</table>	
		<div id="instructionsWindowDiv" style="margin-top: 125px;">
			<div id="instructionsWindow" style="word-wrap: break-word;">
				<!-- instructions for accessing system to be loaded -->
			</div>
		</div>
		<!-- <div style="font-size: 15px;">This is computer generated letter no signature required.</div> -->	
	</div>
	
	
</div>

<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>