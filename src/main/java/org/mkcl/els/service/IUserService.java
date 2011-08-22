/*
******************************************************************
File: org.mkcl.els.service.IUserService.java
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
package org.mkcl.els.service;

import org.mkcl.els.common.exception.RecordNotFoundException;
import org.mkcl.els.domain.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Interface IUserService.
 *
 * @author vishals
 * @version v1.0.0
 */
public interface IUserService extends IGenericService<User,Long>{
	
	
	/**
	 * Find by username.
	 *
	 * @param username the username
	 * @return the user
	 * @throws RecordNotFoundException 
	 */
	@Transactional(readOnly=true)
	public User findByUsername(String username) throws RecordNotFoundException;

}
