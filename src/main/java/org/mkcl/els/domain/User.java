//package org.mkcl.els.domain;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.Set;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.JoinTable;
//import javax.persistence.ManyToMany;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.transaction.annotation.Transactional;
//
//@Configurable
//@Entity
//public class User extends Person implements Serializable {
//
//    // ---------------------------------Attributes------------------------------------------
//
//    private transient static final long serialVersionUID = 1L;
//
//    @Column(length = 50)
//    private String username;
//
//    @Column(length = 20)
//    private String password;
//
//    private boolean enabled = true;
//
//    @Temporal(TemporalType.DATE)
//    private Date lastLoginTime;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "role_membership", joinColumns = @JoinColumn(
//            name = "user_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id",
//                    referencedColumnName = "id"))
//    private Set<Role> roles;
//
//    // ---------------------------------Constructors----------------------------------------------
//
//    public User() {
//        super();
//    }
//
//    public User(String username, String password, boolean enabled) {
//        super();
//        this.username = username;
//        this.password = password;
//        this.enabled = enabled;
//    }
//
//    // Domain Methods
//    // --------------------------------------------------------------------------
//
//    @Transactional
//    public void changePassword(final String username, final String newpassword) {
//        final User user = User.findByFieldName(User.class, "username",
//                username, this.getLocale());
//        user.setPassword(newpassword);
//        user.merge();
//    }
//
//    // ------------------------------------------Getters/Setters-----------------------------------
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public Date getLastLoginTime() {
//        return lastLoginTime;
//    }
//
//    public void setLastLoginTime(Date lastLoginTime) {
//        this.lastLoginTime = lastLoginTime;
//    }
//
//    public Set<Role> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
//}
