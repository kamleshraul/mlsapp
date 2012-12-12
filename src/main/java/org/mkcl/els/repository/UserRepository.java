/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.UserRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.trg.search.Search;

/**
 * The Class UserRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class UserRepository extends BaseRepository<User,Long>{

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Find by user name.
     *
     * @param username the username
     * @param locale the locale
     * @return the user
     */
    public User findByUserName(final String username,final String locale){
        String query="SELECT u FROM User u JOIN FETCH u.credential c  where u.locale='"+locale+"' AND c.username='"+username+"'";
        try {
            User user=(User) this.em().createQuery(query).getSingleResult();
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new User();
        }
    }
    @Transactional(readOnly=false)
    public void assignMemberId(final Long memberId,final Long userId){
        String query="UPDATE User u SET u.id="+ memberId +"WHERE u.id="+userId;
        this.em().createQuery(query).executeUpdate();
    }
    public User find(final Member member) {
        Search search=new Search();
        if(!member.getFirstName().isEmpty()){
            search.addFilterEqual("firstName",member.getFirstName());
        }
        if(!member.getMiddleName().isEmpty()){
            search.addFilterEqual("middleName",member.getMiddleName());
        }
        if(!member.getLastName().isEmpty()){
            search.addFilterEqual("lastName",member.getLastName());
        }
        if(member.getBirthDate()!=null){
            search.addFilterEqual("birthDate", member.getBirthDate());
        }
        search.addSort("lastName",false);
        List<User> users=new ArrayList<User>();
        users=this.search(search);
        if(!users.isEmpty()){
            return users.get(0);
        }else{
            return new User();
        }
    }
}
