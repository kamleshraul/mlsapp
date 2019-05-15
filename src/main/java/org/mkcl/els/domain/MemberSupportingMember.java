package org.mkcl.els.domain;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.MemberRepository;
import org.mkcl.els.repository.MemberSupportingMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="member_supportingmembers")
public class MemberSupportingMember extends BaseDomain implements Serializable {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;

    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    
       /** The SupportingMember. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supportingmember_id")
    private Member supportingMember; 
    
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Member getSupportingMember() {
		return supportingMember;
	}

	public void setSupportingMember(Member supportingMember) {
		this.supportingMember = supportingMember;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	/** The member repository. */
	@Autowired
	private transient MemberSupportingMemberRepository memberSupportingMemberRepository;
	
	/**
	 * Gets the member repository.
	 *
	 * @return the member repository
	 */
	public static MemberSupportingMemberRepository getMemberSupportingMemberRepository() {
		MemberSupportingMemberRepository memberSupportingMemberRepository = new MemberSupportingMember().memberSupportingMemberRepository;
		if (memberSupportingMemberRepository == null) {
			throw new IllegalStateException(
					"MemberRepository has not been injected in Member Domain");
		}
		return memberSupportingMemberRepository;
	}
	public static List<MemberSupportingMember> findMemberSupportingMember(final DeviceType deviceType,
			final Member member,
			final Session session,
			final String locale) {
		List<MemberSupportingMember> supportingmembers = getMemberSupportingMemberRepository().findMemberSupportingMember(deviceType, member, session, locale);
		
		// A member can be assigned multiple ministries, hence members may contain
		// duplicate names. Remove the duplicates.
		Map<Long, MemberSupportingMember> map = new HashMap<Long, MemberSupportingMember>();
		for(MemberSupportingMember m : supportingmembers) {
			Long id = m.getId();
			if(map.get(id) == null) {
				map.put(id, m);
			}
		}
		
		List<MemberSupportingMember> uniqueMembers = new ArrayList<MemberSupportingMember>();
		Set<Long> keys = map.keySet();
		for(Long k : keys) {
			MemberSupportingMember m = map.get(k);
			uniqueMembers.add(m);
		}
		
		return uniqueMembers;
	}
	public static String deleteMemberSupportingMember(final DeviceType deviceType,
			final Member member,
			final Session session,
			final String locale) {
		String msg = getMemberSupportingMemberRepository().deleteMemberSupportingMember(deviceType, member, session, locale);
		return msg;
	}
    
}
