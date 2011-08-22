/*
******************************************************************
File: org.mkcl.els.repository.UserRepository.java
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
package org.mkcl.els.repository;

import org.mkcl.els.domain.User;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class UserRepository.
 *
 * @author vishals
 * @version 1.0.0
 */
@Repository
public class UserRepository extends BaseRepository<User, Long> {

	/**
	 * Find by username.
	 *
	 * @param username the username
	 * @return the user
	 */
	
	public User findByUsername(String username) {
		return this.searchUnique(new Search().addFilterEqual("username",username));
	}

	/**
	 * Find by email.
	 *
	 * @param email the email
	 * @return the user
	 */
	public User findByEmail(String email) {
		return this.searchUnique(new Search().addFilterEqual("email",email));
	}
}
