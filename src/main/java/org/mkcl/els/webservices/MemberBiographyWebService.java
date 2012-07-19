/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberBiographyWebService.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.webservices;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberBiographyWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ws/biography")
public class MemberBiographyWebService {

    /**
     * Gets the biography.
     *
     * @param id the id
     * @param locale the locale
     * @return the biography
     */
    @RequestMapping(value = "/{id}/{locale}",method=RequestMethod.GET)
    public @ResponseBody MemberBiographyVO getBiography(@PathVariable("id") final long id ,
            @PathVariable("locale") final String locale,
            final HttpServletRequest request){
    	String constituency = null;
    	String party=null;
    	String gender=null;
    	String maritalstatus=null;
    	//for tomcat
    	CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
		if(customParameter.getValue().equals("TOMCAT")){
    	try {
			constituency = new String(request.getParameter("constituency").getBytes("ISO-8859-1"),"UTF-8");
			party = new String(request.getParameter("party").getBytes("ISO-8859-1"),"UTF-8");
			gender = new String(request.getParameter("gender").getBytes("ISO-8859-1"),"UTF-8");
			maritalstatus = new String(request.getParameter("maritalstatus").getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		}else{
		//for glassfish
		constituency = request.getParameter("constituency");
		party = request.getParameter("party");
		gender = request.getParameter("gender");
		maritalstatus = request.getParameter("maritalstatus");
		}
		}
    	String[] data={constituency,party,gender,maritalstatus};
        return Member.findBiography(id , locale,data);
    }

    /**
     * Gets the photo.
     *
     * @param tag the tag
     * @param response the response
     * @return the photo
     */
    @RequestMapping(value="/photo/{tag}")
    public @ResponseBody byte[] getPhoto(@PathVariable("tag")
            final String tag ,
            final HttpServletResponse response){
        Document document = Document.findByTag(tag);
        return document.getFileData();
    }

    /**
     * This method is added to facilitate Kartik's requirement of
     * being able to print all the Member's Bioprofile in one go.
     * Since this utility is temporary, no effort is made to
     * optimize the query by adding methods to the domain/vo package.
     */
//    @RequestMapping(value="/allMembers/{locale}")
//    public @ResponseBody MemberBiographiesVO getAllMemberBiographies(
//            @PathVariable final String locale){
//        List<Member> members =
//            Member.findAll(Member.class, "lastName", ApplicationConstants.ASC, locale);
//        MemberBiographiesVO biographiesVOs = new MemberBiographiesVO();
//        for(Member member : members){
//            Long memberId = member.getId();
//            try{
//                MemberBiographyVO biographyVO = Member.findBiography(memberId.longValue(), locale);
//                biographiesVOs.getMemberBiographyVOs().add(biographyVO);
//            } catch(IllegalArgumentException iae){
//                System.out.println(memberId);
//                iae.printStackTrace();
//            }
//        }
//        return biographiesVOs;
//    }
}
