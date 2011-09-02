/*
******************************************************************
File: org.mkcl.els.service.impl.UserServiceImpl.java
Copyright (c) 2011, vishals, MKCL
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

import org.mkcl.els.common.exception.RecordNotFoundException;
import org.mkcl.els.domain.User;
import org.mkcl.els.repository.MessageResourceRepository;
import org.mkcl.els.repository.UserRepository;
import org.mkcl.els.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * The Class UserServiceImpl.
 *
 * @author vishals
 * @version v1.0.0
 */
@Service
public class UserServiceImpl extends GenericServiceImpl<User,Long> implements IUserService{

	/** The user repository. */
	private UserRepository userRepository;

	/**
	 * Sets the user repository.
	 *
	 * @param userRepository the new user repository
	 */
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.dao = userRepository;
		this.userRepository = userRepository;
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IUserService#findByUsername(java.lang.String)
	 */
	public User findByUsername(String username){
		User user = userRepository.findByUsername(username);
		if(user==null){
			throw new RecordNotFoundException("Error:Record was not found for user:"+ username);
		}
		return user;
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IUserService#changePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public void changePassword(String username, String new_password) {
		User user = this.findByUsername(username);
		user.setPassword(new_password);
		this.update(user);
	}

}
