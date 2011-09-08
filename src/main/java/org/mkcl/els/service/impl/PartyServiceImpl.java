/*
******************************************************************
File: org.mkcl.els.service.impl.PartyServiceImpl.java
Copyright (c) 2011, amitd, MKCL
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
package org.mkcl.els.service.impl;

import org.mkcl.els.domain.Party;
import org.mkcl.els.repository.PartyRepository;
import org.mkcl.els.service.IPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class PartyServiceImpl.
 *
 * @author amitd
 * @version v1.0.0
 */
@Service
public class PartyServiceImpl
	extends GenericServiceImpl<Party,Long>
	implements IPartyService{

	/** The party repository. */
	private PartyRepository partyRepository;
	
	/**
	 * Sets the party repository.
	 *
	 * @param partyRepository the new party repository
	 */
	@Autowired
	public void setPartyRepository(PartyRepository partyRepository) {
		this.dao = partyRepository;
		this.partyRepository = partyRepository;
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the party
	 */
	@Override
	public Party findByName(String name) {
		return this.partyRepository.findByName(name);
	}

}
