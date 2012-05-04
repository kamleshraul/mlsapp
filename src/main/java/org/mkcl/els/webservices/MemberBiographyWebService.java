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

import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping(value = "/{id}/{locale}")
    public @ResponseBody MemberBiographyVO getBiography(@PathVariable("id") final long id ,
            @PathVariable("locale") final String locale){
        return Member.findBiography(id , locale);
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
}
