/*
******************************************************************
File: org.mkcl.els.domain.MemberPositionsDetails.java
Copyright (c) 2011, sandeeps, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberPositionsDetails.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="member_positions")
@JsonIgnoreProperties({"member"})
public class MemberPositionsDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The member. */
	@ManyToOne
	@JoinColumn(name="member_id")
	private MemberDetails member;
	
	/** The period. */
	@Column(length=30)
	private String period;
	
	/** The details. */
	@Column(length=10000)
	private String details;

	/**
	 * Gets the period.
	 *
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * Sets the period.
	 *
	 * @param period the new period
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public MemberDetails getMember() {
		return member;
	}

	/**
	 * Sets the member.
	 *
	 * @param member the new member
	 */
	public void setMember(MemberDetails member) {
		this.member = member;
	}

	/**
	 * Instantiates a new member positions details.
	 */
	public MemberPositionsDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
