/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.RivalMemberVO.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class RivalMemberVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class RivalMemberVO {

/** The votes received. */
private String votesReceived;

/** The party. */
private String party;

/** The name. */
private String name;

/**
 * Gets the votes received.
 *
 * @return the votes received
 */
public String getVotesReceived() {
	return votesReceived;
}

/**
 * Sets the votes received.
 *
 * @param votesReceived the new votes received
 */
public void setVotesReceived(final String votesReceived) {
	this.votesReceived = votesReceived;
}

/**
 * Gets the party.
 *
 * @return the party
 */
public String getParty() {
	return party;
}

/**
 * Sets the party.
 *
 * @param party the new party
 */
public void setParty(final String party) {
	this.party = party;
}

/**
 * Gets the name.
 *
 * @return the name
 */
public String getName() {
	return name;
}

/**
 * Sets the name.
 *
 * @param name the new name
 */
public void setName(final String name) {
	this.name = name;
}
}
