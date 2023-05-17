package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.repository.ApiTokenRepositry;
import org.mkcl.els.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * The Class Election.
 *
 * @author kamlesh r
 * @author shubham a
 * @since v1.0.0
 */


@Configurable
@Entity
@Table(name = "api_tokens")
public class ApiToken extends BaseDomain implements Serializable {
	
	   // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;
    
    
    
    /** The name. */
    @Column(length = 600)
    private String subUrl;
    
    
    /**  From date. */
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    /**  To date. */
    @Temporal(TemporalType.DATE)
    private Date toDate;
    
    
    @Column(columnDefinition="varchar(1000)")
    private String token;


    
    
    
     //-----------------------------------------------------------------------------------------------

    
    /**
     * Instantiates a new JwtToken.
     */
    

	public ApiToken() {
		super();
		// TODO Auto-generated constructor stub
	}




	public ApiToken(String subUrl, Date fromDate, Date toDate, String token) {
		super();
		this.subUrl = subUrl;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.token = token;
	}


	   public static ApiTokenRepositry getApiTokenRepository() {
			
		   ApiTokenRepositry apiRepository = new ApiToken().getApiTokenRepository();
	        if (apiRepository == null) {
	            throw new IllegalStateException(
	                    "SessionRepository has not been injected in Session Domain");
	        }
	        return apiRepository;
			 
	    }
	    
	
	


	 // ------------------------------------------Getters/Setters-----------------------------------
	
	   
	   public String getSubUrl() {
			return subUrl;
		}




		public void setSubUrl(String subUrl) {
			this.subUrl = subUrl;
		}

	




	public Date getFromDate() {
		return fromDate;
	}


	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}




	public Date getToDate() {
		return toDate;
	}




	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}




	public String getToken() {
		return token;
	}




	public void setToken(String token) {
		this.token = token;
	}
	
	


	

	
    
}
