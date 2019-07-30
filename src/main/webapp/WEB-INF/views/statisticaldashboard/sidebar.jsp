 <div class="wrapper " style="overflow:auto;">
    <div class="sidebar" data-color="purple" data-background-color="white" data-image="./assets/img/sidebar-1.jpg">
      <!--
        Tip 1: You can change the color of the sidebar using: data-color="purple | azure | green | orange | danger"

        Tip 2: you can also add an image using data-image tag
    -->
      <div class="logo">
        <a href="http://mls.org.in" target="_blank" class="simple-text logo-normal">
         <spring:message code="dashboard.vidhanmandalsachivalay" text=""></spring:message>
        </a>
      </div>	
            <div class="sidebar-wrapper">
        <ul class="nav">
          <li class="nav-item active  ">
            <a class="nav-link" href="#">
              <i class="material-icons">dashboard</i>
               <div><spring:message code="dashboard" text="dashboard"></spring:message></div>
            </a>
          </li>
		  
<li class="nav-item " data-toggle="collapse" href="#collapseQuestion"  aria-expanded="false" aria-controls="collapseExample">
            <a class="nav-link">
              <i class="material-icons" href="#">label_important</i>
              <p><spring:message code="dashboard.question" text="QUESTION"></spring:message></p>
            </a>
     </li>

    <div class="collapse" id="collapseQuestion">
		   <li class="nav-item ">
            <a class="nav-link" href="statisticaldashboard/device?deviceType=4&admitStatus=33&rejectStatus=34">
              <i class="material-icons">label_important</i>
                 <p><spring:message code="dashboard.starred" text="STARRED QUESTION"></spring:message></p>
            </a>
          </li>
		     
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=5&admitStatus=637&rejectStatus=638">
              <i class="material-icons">label_important</i>
                 <p><spring:message code="dashboard.unstarred" text="UNSTARRED QUESTION"></spring:message></p>
            </a>
          </li>
		     <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=49&admitStatus=750&rejectStatus=751">
              <i class="material-icons">label_important</i>
             <p><spring:message code="dashboard.questionhalfhour" text="QUESTION HALF HOUR"></spring:message></p>
            </a>
          </li>
		
		     <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=7&admitStatus=682&rejectStatus=683">
              <i class="material-icons">label_important</i>
              <p><spring:message code="dashboard.questionshortnotice" text="SHORT NOTICE QUESTIONS"></spring:message></p>
            </a>
          </li>
	</div>

  
    <li class="nav-item " data-toggle="collapse" href="#collapseResolution"  aria-expanded="false" aria-controls="collapseExample">
            <a class="nav-link">
              <i class="material-icons">label_important</i>
              <p><spring:message code="dashboard.resolution" text="RESOLUTIONS"></spring:message></p>
            </a>
     </li>

    <div class="collapse" id="collapseResolution">
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=100&admitStatus=100&rejectStatus=101">
              <i class="material-icons">label_important</i>
                <p><spring:message code="dashboard.resolutionsgovernment" text="GOVEREMENT RESOLUTION"></spring:message></p>
            </a>
          </li>
		    
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=50&admitStatus=100&rejectStatus=101">
              <i class="material-icons">label_important</i>
            
              <p><spring:message code="dashboard.resolutionsnonofficial" text="NON OFFICIAL RESOLUTION"></spring:message></p>
            </a>
          </li>
	</div>
         
             <li class="nav-item " data-toggle="collapse" href="#collapseMotion"  aria-expanded="false" aria-controls="collapseExample">
            <a class="nav-link">
              <i class="material-icons">label_important</i>
              <p><spring:message code="dashboard.motion" text="MOTIONS"></spring:message></p>
            </a>
     </li>

    <div class="collapse" id="collapseMotion">
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=101&admitStatus=942&rejectStatus=943">
              <i class="material-icons">label_important</i>
                <p><spring:message code="dashboard.motionscallingattention" text="MOTION CALLING ATTENTION"></spring:message></p>
            </a>
          </li>
		    
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=2550&admitStatus=585&rejectStatus=586">
              <i class="material-icons">label_important</i>
            
              <p><spring:message code="dashboard.motionstandalone" text="MOTION STANDALONE"></spring:message></p>
            </a>
          </li>
          
           <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=2552&admitStatus=853&rejectStatus=854">
              <i class="material-icons">label_important</i>
            
              <p><spring:message code="dashboard.motionadjournment" text="MOTION ADJOURNMENT"></spring:message></p>
            </a>
          </li>
          
	</div>

		      <li class="nav-item " data-toggle="collapse" href="#collapseCutmotion"  aria-expanded="false" aria-controls="collapseExample">
            <a class="nav-link">
              <i class="material-icons">label_important</i>
             <p><spring:message code="dashboard.cutmotion" text="CUT MOTION"></spring:message></p>
            </a>
    		 </li>

    <div class="collapse" id="collapseCutmotion">
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=104&admitStatus=508&rejectStatus=509">
              <i class="material-icons">label_important</i>
                <p><spring:message code="dashboard.cutmotionbudgetary" text="BUDGETARY CUT MOTION"></spring:message></p>
            </a>
          </li>
	    
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=105&admitStatus=508&rejectStatus=509">
              <i class="material-icons">label_important</i>
              <p><spring:message code="dashboard.cutmotionsupplementary" text="SUPPLEMENTARY CUT MOTION"></spring:message></p>
            </a>
          </li>
	</div>
	 <li class="nav-item " data-toggle="collapse" href="#collapseDiscussionmotion"  aria-expanded="false" aria-controls="collapseExample">
            <a class="nav-link">
              <i class="material-icons">label_important</i>
              <p><spring:message code="dashboard.discussionmotion" text="DISCUSSION MOTION"></spring:message></p>
            </a>
     </li>

    <div class="collapse" id="collapseDiscussionmotion">
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=2601&admitStatus=989&rejectStatus=990">
              <i class="material-icons">label_important</i>
               <p><spring:message code="dashboard.discussionmotionlastweek" text="LAST WEEK MOTION"></spring:message></p>
            </a>
          </li>
		    
		   <li class="nav-item ">
            <a class="nav-link" href="device?deviceType=2602&admitStatus=989&rejectStatus=990">
              <i class="material-icons">label_important</i>
               <p><spring:message code="dashboard.discussionmotionpublicimportance" text="PUBLIC IMPORTANCE MOTION"></spring:message></p>
            </a>
         </li>
	</div>
	       
        </ul>
      </div>
    </div>