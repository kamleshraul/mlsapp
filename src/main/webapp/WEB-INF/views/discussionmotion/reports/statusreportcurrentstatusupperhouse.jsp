<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.admission.report" text="Department Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
			$('.actor_displayname_label').each(function() {
				if($(this).text()==$('#deputy_secretary1_label').val()) {
					$(this).text($('#deputy_secretary_label').val());
				
				} else if($(this).text()==$('#deputy_secretary2_label').val()) {
					$(this).text($('#deputy_secretary_label').val());
				}
				
			});
		});
		
		
	</script>
	 <!-- <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 800px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	margin-top: 20px !important;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 20px 0px 0px 15px !important;
	        } 
	        
	        hr{
	        	display: none !important;
	        }    
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        #loadMore{
        	background: #00FF00 scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			/*border-radius: 10px;*/
			text-align: center;
			border: 1px solid black;
			z-index: 5000;
			bottom: 5px;
			right: 50px;			
			position: fixed;
			cursor: pointer;
        }
        
        th{
        	text-align: center;
        }
    </style> -->
</head> 

<body>
	<c:choose>
		<c:when test="${data==null}">
			<b>No data.</b>
		</c:when>
		<c:when test="${empty(data)}">
			Empty data found.
		</c:when>
		<c:otherwise>
		
			<c:set var="labels" value="${fn:split(data[0][15],';')}" />	
			<div>
				<table style="width: 750px; margin-top: 0px !important;">
					<tr>
						<td style="font-size: 16px; text-decoration: underline; width:230px; text-align: center; font-weight: bold;">${labels[0]}</td>
						<td style="font-size: 22px; text-decoration: underline; width:290px; text-align: center; font-weight: bold;">
							${labels[1]}
						</td>	
						<td style="font-size: 14px; text-decoration: underline; width:230px; text-align: center; font-weight: bold;">
							${data[0][14]}
						</td>
					</tr>		
					<tr>
						<td colspan="3" style="width: 750px; text-align: right; font-size: 10px;" colspan="3">
							${labels[2]} : ${data[0][18]}
						</td>
					</tr>
				</table>
				<br>
				<table style="margin-left: 30px;">
					<tr style="font-weight: bold;">
						<td style="font-size: 17px; word-wrap: break-word; width: 250px; padding-left: 5px; padding-bottom:10px;">
							${labels[3]}
						</td>
						<%-- <td colspan="2" style="font-size: 17px; word-wrap: break-word; width: 500px; padding-left: 5px;">
							${data[0][7]}
						</td> --%>
						<td colspan="2" style="font-size: 17px; word-wrap: break-word; width: 500px; padding-left: 5px;text-align:justify; padding-bottom:10px;">
							 ${data[0][21] }
						</td>
					</tr>
					<tr style="font-weight: bold;">													
						<td style="font-size: 17px; word-wrap: break-word; width: 250px; padding-left: 5px; padding-bottom:10px;">					
							${labels[4]}
						</td>
						<td colspan="2" style="font-size: 17px; word-wrap: break-word; width: 500px; padding-left: 5px; padding-bottom:10px;">
							${data[0][6]} 
						</td>
					</tr>
					<tr style="font-weight: bold;">
						<td style="font-size: 17px; word-wrap: break-word; width: 250px; padding-left: 5px; padding-bottom:10px;">					
							${labels[5]} 
						</td>
						<td colspan="2" style="font-size: 17px; word-wrap: break-word; width: 500px; padding-left: 5px; padding-bottom:10px;">
							${data[0][9]}
						</td>	
					</tr>
					
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[6]}
						</td>
						<td colspan="2" style="font-weight: bold; text-decoration: underline; width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][10]}
						</td>
					</tr>
					
					<c:if test="${not empty data[0][20]}">
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[13]} 
						</td>
						<td colspan="2" style="font-weight: bold; text-decoration: underline; width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][20]}
						</td>
					</tr>
					</c:if>
					
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[7]}
						</td>
						<td colspan="2" style="font-weight: bold; text-decoration: underline; width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][12]} 
							
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>						
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[12]}
						</td>
						<td colspan="3" style="width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][23]}
							<hr>
						</td>
					</tr>	
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[13]}
						</td>
						<td colspan="3" style="width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][25]}
							<hr>
						</td>
					</tr>
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[14]}
						</td>
						<td colspan="3" style="width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][24]} 
							<hr>
						</td>
					</tr>
					
					<tr>
						<td style="font-weight: bold; width: 250px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${labels[15]}
						</td>
						<td colspan="3" style="width: 500px; font-size: 17px; word-wrap: break-word; padding-left: 5px;">
							${data[0][26]}
							
						</td>
					</tr>							
				</table>	
				
				<div style="width: 750px; border-top: 2px dotted black; margin-left: 30px;">&nbsp;</div>
				
				<%-- <c:forEach items="${fn:split(data[0][2],',')}" var="d" varStatus="dd">
					<c:set var="actor${dd.count}" value="${d}" />
					<c:forEach items="${data}" var="rd">
						<c:set var="actor${dd.count}Status" value="${rd[11]}" />
						<c:set var="actor${dd.count}Remark" value="${rd[17]}" />
					</c:forEach>
				</c:forEach>
				
				<c:forEach items="${fn:split(data[0][2],',')}" var="d" varStatus="dd">
					<c:out value="actor${dd.count}Status" />
					<c:out value="actor${dd.count}Status" />
					<c:out value="actor${dd.count}Remark" />					
				</c:forEach> --%>
				
				<jsp:useBean id="dataMapper" class="java.util.HashMap"/>
 				
 				<c:forEach items="${fn:split(data[0][2],',')}" var="d" varStatus="dd">
 					<c:set target="${dataMapper}" property="actor${dd.count}" value="${d}" />
					
					<c:forEach items="${data}" var="rd">
						<c:if test="${d==rd[0]}">
							<c:set target="${dataMapper}" property="actor${dd.count}Name" value="${rd[1]}" />
							<c:if test="${fn:length(rd[11])>6}">
								<c:set target="${dataMapper}" property="actor${dd.count}Status" value="${rd[11]}" />
							</c:if>
							<c:if test="${fn:length(rd[16])>6}">
								<c:set target="${dataMapper}" property="actor${dd.count}Remark" value="${rd[16]}" />
							</c:if>
							<c:if test="${d!='assistant' and d!='principal_secretary'}">
								<c:set target="${dataMapper}" property="user${dd.count}Name" value="${rd[17]}" />
							</c:if>
						</c:if>		
					</c:forEach>
				</c:forEach>
				
				<table style="width: 750px; font-weight: bold; border: 2px solid black; font-size: 14px; margin-left: 30px;" class="uiTable">
					<%-- <tr>
						${dataMapper}
					</tr> --%>
					<c:forEach items="${fn:split(data[0][2],',')}" var="d" varStatus="dd">
						<c:if test="${d!='clerk' && d!='member' && d!='typist'}">
							<tr>
								<c:set var="actor" value="actor${dd.count}Name" />
								<c:set var="propertS" value="actor${dd.count}Status" />
								<c:set var="propertR" value="actor${dd.count}Remark" />
								<c:set var="propertName" value="user${dd.count}Name" />
								<td style="width:120px;">
									<label class="actor_displayname_label"><c:out value="${dataMapper[actor]}" /></label><br>
									<c:if test="${dataMapper[propertName]!=null}">
										<span style="font-size: 10px">
											(${dataMapper[propertName]})
										</span>
									</c:if>
								<td style="width:100px;"><c:out value="${dataMapper[propertS]}" /></td>
								<td style="width:530px; text-align:center;"><c:out value="${dataMapper[propertR]}" /></td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
				
				<br><br>
				
				<table style="width: 750px; font-size: 17px;">
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>
						<td style="width: 150px; font-size: 16px; word-wrap: break-word; padding-left: 5px; text-align: center;">
							&nbsp;
						</td>
						
						<td colspan="2" style="font-weight: bold; width: 600px; font-size: 16px; word-wrap: break-word; padding-left: 5px; text-align: center;">
							<br>
							(${labels[8]} / ${labels[9]} / ${labels[10]})<br>
							<br><br>
							${labels[11]}
						</td>
					</tr>
				</table>
			</div>
			
			<span class='page-break-before-forced'>&nbsp;</span>
		</c:otherwise>
	</c:choose>
	<hr>
	<br>
	<br>
	<input type="hidden" id="deputy_secretary1_label" value="${deputy_secretary1_label}"/>
	<input type="hidden" id="deputy_secretary2_label" value="${deputy_secretary2_label}"/>
	<input type="hidden" id="deputy_secretary_label" value="${deputy_secretary_label}"/>
</body>
</html>