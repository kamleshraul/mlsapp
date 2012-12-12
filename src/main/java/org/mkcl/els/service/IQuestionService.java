package org.mkcl.els.service;

import java.util.List;


public interface IQuestionService {

    public List<String> findSupportingMembers(final String strQuestionId);

    public String findEmailByUsername(final String username);

    public String findByLocaleAndCode(final String locale,final String code);


}
