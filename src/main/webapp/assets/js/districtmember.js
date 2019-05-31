/**
 * 
 */

  function getMembersByDistrict(districtId){
    	alert("ala");
		$.get("http://localhost:9090/els/ws/biography/membersforGrav/allMembersDistrictwise/lowerhouse/mr_IN?districtId="+districtId, function(result){
			var text ='';
			if(result[0].countriesVisited!= null && result[0].countriesVisited!=''){
			text = text +
						'<div class="col-md-12 ">'+
							 '<h5 class="border-bottom pb-3">'+
								result[0].countriesVisited+'चे'+' '+'निवडून आलेले माननीय सदस्य'+
							 '</h5>'+
						'</div>';
			}
			for(var i=0;i<result.length;i++){
				if(result[i].firstName!= null && result[i].firstName!=''){
					var text = text+
								'<div class="col-md-6">'+
									'<a href="#" class="member-list-item">'+
										'<div class="img-wrapper">'+
											'<img src="http://localhost:8080/els/ws/biography/photo/'+result[i].photo+'" class="img-fluid">'+
										'</div>'+
										'<div class="col member-info">'+
											'<div class="member-name">'+
												result[i].firstName+' '+result[i].lastName+
											'</div>'+
											'<div class="member-profile">'+
												result[i].countriesVisited+//districtName	
											'</div>'+
										'</div>'+
									'</a>'+
								'</div>';
				}
			}
				$("#districtwiseMemberList").html(text);
			});
	}