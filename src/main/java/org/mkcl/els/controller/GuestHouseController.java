package org.mkcl.els.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.GuestHouse;
import org.mkcl.els.domain.GuestHouseReservation;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guesthouse")
public class GuestHouseController extends GenericController<GuestHouse> {
	
	
	@RequestMapping(value = "/booking", method = RequestMethod.GET)
    public String guestHouseBooking(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) throws ELSException, ParseException {
		   final String servletPath = request.getServletPath().replaceFirst("\\/","");
		   
		   Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
     
		
        
 
        		List<GuestHouse> guesthouses = new ArrayList<GuestHouse>();
        		guesthouses = GuestHouse.findAll(GuestHouse.class, "location", ApplicationConstants.ASC, locale.toString());
        		
        	       		
        		String format = ApplicationConstants.SERVER_DATEFORMAT;
        		Date fromDate = FormaterUtil.getCurrentDate();

        		Date toDate = FormaterUtil.getCurrentDate();
        		
        		
        		String strfromDate = FormaterUtil.formatDateToString(fromDate, format);

          		String strtoDate = FormaterUtil.formatDateToString(toDate, format);
        		
        		List<GuestHouseReservation> guesthousereservations = new ArrayList<GuestHouseReservation>();
        		guesthousereservations=GuestHouseReservation.getGuestHouseReservationRepository().findBookedRoomsByGuestHouse(guesthouses.get(0), fromDate,toDate ,locale.toString());
        			List<MasterVO> availablerooms = new ArrayList<MasterVO>();
        		/*	for(int i = 1;i <= 10;i++){
        				
        				String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i);
        				availablerooms.add(new MasterVO(i,localizedPriority));
        				
    					}*/
        			       			
        			if (guesthousereservations.size()!=0)
        			{
        				HashMap<Integer, String> tRooms = new HashMap<Integer, String>();
        				for(int i = 1;i <= guesthouses.get(0).getTotalRooms();i++){
        					 
        					 String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i);
        		        		
        					 tRooms.put(i, localizedPriority);
        				}
        				
        				for (GuestHouseReservation ghr : guesthousereservations) {
        					tRooms.remove(ghr.getRoomNumber());
        				}
        				 Set set = tRooms.entrySet();
        			      Iterator iterator = set.iterator();
        			      while(iterator.hasNext()) {
        			         Map.Entry mentry = (Map.Entry)iterator.next();
        			         availablerooms.add(new MasterVO(Integer.parseInt(mentry.getKey().toString()),mentry.getValue().toString()));
        			      }
        			}else
        			{
        				for(int i = 1;i <= guesthouses.get(0).getTotalRooms();i++){
        				
        				
        						String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i);
        		        		availablerooms.add(new MasterVO(i,localizedPriority));
        		        		

        				}
        			}
        			
        			model.addAttribute("availablerooms",availablerooms);
        			model.addAttribute("fromDate", strfromDate);
            		model.addAttribute("toDate", strtoDate);
            		model.addAttribute("guesthouses", guesthouses);
            		if( fromDate.compareTo(guesthouses.get(0).getFromDate()) > 0 )
            			
            		{
            			   Calendar c = Calendar.getInstance();
            		        c.setTime(fromDate);
          		      
            		        c.add(Calendar.DATE, 3); //same with c.add(Calendar.DAY_OF_MONTH, 1);
            		      

            		        // convert calendar to date
            		        Date currentDatePlusThree = c.getTime();
            			model.addAttribute("mindate", FormaterUtil.formatDateToString(currentDatePlusThree, ApplicationConstants.SERVER_DATEFORMAT));
            			model.addAttribute("fromDate", FormaterUtil.formatDateToString(currentDatePlusThree, ApplicationConstants.SERVER_DATEFORMAT));
                		model.addAttribute("toDate", FormaterUtil.formatDateToString(currentDatePlusThree, ApplicationConstants.SERVER_DATEFORMAT));
            		
            		}else
            		{
            			model.addAttribute("mindate", FormaterUtil.formatDateToString(guesthouses.get(0).getFromDate(), ApplicationConstants.SERVER_DATEFORMAT));
            			model.addAttribute("fromDate", FormaterUtil.formatDateToString(guesthouses.get(0).getFromDate(), ApplicationConstants.SERVER_DATEFORMAT));
                		model.addAttribute("toDate", FormaterUtil.formatDateToString(guesthouses.get(0).getFromDate(), ApplicationConstants.SERVER_DATEFORMAT));

            		}
            
            		model.addAttribute("maxdate", FormaterUtil.formatDateToString(guesthouses.get(0).getToDate(), ApplicationConstants.SERVER_DATEFORMAT));
            		
            		 /** username **/
                    model.addAttribute("username", this.getCurrentUser().getActualUsername());  
                    if (member != null) {
                    model.addAttribute("member", this.getCurrentUser().getActualUsername());
                    }
            		
        		
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("booking","error");
        }else{
            return servletPath;
        }
    }
	
	
	@Transactional
	@RequestMapping(value="/booking",method=RequestMethod.POST)
	public @ResponseBody String guestHouseBooking(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) throws ELSException {
		
		AuthUser authUser = this.getCurrentUser();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
	
		String strfromDate = request.getParameter("fromDate");
		String strtoDate = request.getParameter("toDate");
		String guesthouserooms = request.getParameter("guesthouserooms");
		String guesthouse = request.getParameter("guesthouse");
		Date currentDate = new Date();
		
		GuestHouse guestHouse=GuestHouse.findById(GuestHouse.class, Long.parseLong(guesthouse));
		String format = ApplicationConstants.SERVER_DATEFORMAT;
		Date fromDate = FormaterUtil.formatStringToDate(strfromDate, format);

		
		Date toDate = FormaterUtil.formatStringToDate(strtoDate, format);
		
	/*	Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
    
        c.add(Calendar.DATE, 3); //same with c.add(Calendar.DAY_OF_MONTH, 1);
      

        // convert calendar to date
        Date currentDatePlusThree = c.getTime();
        
		if( currentDatePlusThree.compareTo(guestHouse.getFromDate()) < 0 )
			
		{
			return "failed";
		}
*/
	
		List<GuestHouseReservation> guesthousereservations = new ArrayList<GuestHouseReservation>();
		guesthousereservations=GuestHouseReservation.getGuestHouseReservationRepository().findBookedRoomsByGuestHouseMember(guestHouse,member,fromDate,toDate ,locale.toString());
		if (guesthousereservations.size()==0)
		{
			
		GuestHouseReservation guestHouseReservation=new GuestHouseReservation();
		guestHouseReservation.setLocale(locale.toString());
		guestHouseReservation.setCreatedBy(authUser.getActualUsername());
		guestHouseReservation.setCreationDate(currentDate);
		guestHouseReservation.setMember(member);
		guestHouseReservation.setGuestHouse(guestHouse);
		guestHouseReservation.setRoomNumber(Integer.parseInt(guesthouserooms));
		guestHouseReservation.setFromDate(fromDate);
		guestHouseReservation.setToDate(toDate);
		
		
		guestHouseReservation.merge();

        //NotificationController.sendGuestHouseBookingNotification(guestHouseReservation, guestHouseReservation.getLocale());
        return "success";
		}
		else if ( authUser.getActualUsername().equals("honblespeaker") )
		{
			GuestHouseReservation guestHouseReservation=new GuestHouseReservation();
			guestHouseReservation.setLocale(locale.toString());
			guestHouseReservation.setCreatedBy(authUser.getActualUsername());
			guestHouseReservation.setCreationDate(currentDate);
			guestHouseReservation.setMember(member);
			guestHouseReservation.setGuestHouse(guestHouse);
			guestHouseReservation.setRoomNumber(Integer.parseInt(guesthouserooms));
			guestHouseReservation.setFromDate(fromDate);
			guestHouseReservation.setToDate(toDate);
			
			
			guestHouseReservation.merge();

	        //NotificationController.sendGuestHouseBookingNotification(guestHouseReservation, guestHouseReservation.getLocale());
	        return "success";
		}
		else
		{
	        return "failed";
		}
		
	
   
        
    }
	
	
	@RequestMapping(value="/bookingDetails", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> bookingDetails(final HttpServletRequest request, final Locale locale){
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		try{
			String guesthouse = request.getParameter("guesthouse");
			GuestHouse guestHouse=GuestHouse.findById(GuestHouse.class, Long.parseLong(guesthouse));
	
				if(guestHouse != null){
					List<GuestHouseReservation> guestHouseReservations = GuestHouseReservation.findAllByFieldName(GuestHouseReservation.class, "guestHouse", guestHouse, "fromDate", ApplicationConstants.ASC, locale.toString());
					for(GuestHouseReservation ghr : guestHouseReservations){
			
					
							MasterVO masterVO = new MasterVO();
							masterVO.setId(ghr.getId());
							masterVO.setName(ghr.getMember().getFullname());
							masterVO.setNumber(ghr.getRoomNumber());
							masterVO.setFormattedNumber((ghr.getFromDate()).toString());
							masterVO.setFormattedOrder(ghr.getToDate().toString());
							masterVO.setType(FormaterUtil.formatDateToString(guestHouse.getFromDate(), ApplicationConstants.SERVER_DATEFORMAT));
							masterVO.setDisplayName(FormaterUtil.formatDateToString(guestHouse.getToDate(), ApplicationConstants.SERVER_DATEFORMAT));
							masterVOs.add(masterVO);
						
					
				
				}
    			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		return masterVOs;
	}
	
	@RequestMapping(value="/getAvailableRooms", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getAvavilableRooms(
			final HttpServletRequest request,
			final Locale locale) {
		String strguesthouse = request.getParameter("guesthouse");
		String strfromDate = request.getParameter("fromDate");
		String strtoDate = request.getParameter("toDate");
		
		//Populate Priorities
		GuestHouse guesthouse = new GuestHouse();
		guesthouse = GuestHouse.findById(GuestHouse.class, Long.parseLong(strguesthouse));
	
		String format = ApplicationConstants.SERVER_DATEFORMAT;

		Date fromDate = FormaterUtil.formatStringToDate(strfromDate, format);
		Date toDate = FormaterUtil.formatStringToDate(strtoDate, format);
		

		List<MasterVO> availablerooms = new ArrayList<MasterVO>();
		try{
		List<GuestHouseReservation> guesthousereservations = new ArrayList<GuestHouseReservation>();
		
		guesthousereservations=GuestHouseReservation.getGuestHouseReservationRepository().findBookedRoomsByGuestHouse(guesthouse, fromDate,toDate ,locale.toString());
	
			 availablerooms = new ArrayList<MasterVO>();
			
			if (guesthousereservations.size()!=0)
			{
				HashMap<Integer, String> tRooms = new HashMap<Integer, String>();
				for(int i = 1;i <= guesthouse.getTotalRooms();i++){
					 
					 String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i);
		        		
					 tRooms.put(i, localizedPriority);
				}
				
				for (GuestHouseReservation ghr : guesthousereservations) {
					tRooms.remove(ghr.getRoomNumber());
				}
				 Set set = tRooms.entrySet();
			      Iterator iterator = set.iterator();
			      while(iterator.hasNext()) {
			         Map.Entry mentry = (Map.Entry)iterator.next();
			         availablerooms.add(new MasterVO(Integer.parseInt(mentry.getKey().toString()),mentry.getValue().toString()));
			      }
			      
				
			
			}
			else
			{
				for(int i = 1;i <= guesthouse.getTotalRooms();i++){
				
				
						String localizedPriority = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i);
		        		availablerooms.add(new MasterVO(i,localizedPriority));
		        		

				}
				
		
			}
		
			
	
		return availablerooms;
		}catch(Exception e){
			logger.error(e.toString());
			return availablerooms;
		}
	}
	
	@Override
	protected void customValidateCreate(final GuestHouse domain, final BindingResult result,
			final HttpServletRequest request) {	
		AuthUser authUser = this.getCurrentUser();
		try {
			
		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
