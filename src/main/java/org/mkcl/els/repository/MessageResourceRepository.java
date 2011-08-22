/*
******************************************************************
File: org.mkcl.els.repository.MessageResourceRepository.java
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

import java.util.Locale;

import org.mkcl.els.domain.MessageResource;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GenericDAO;
import com.trg.search.Search;

/**
 * The Class MessageResourceRepository.
 *
 * @author vishals
 * @version v1.0.0
 */
@Repository
public class MessageResourceRepository extends BaseRepository<MessageResource, Long> {
	
	/**
	 * Find by code and locale.
	 *
	 * @param code the code
	 * @param locale the locale
	 * @return the message resource
	 */
	public MessageResource findByLocale(Locale locale){
		Search search = new Search();
		search.addFilterEqual("locale", locale.getCountry());
		MessageResource message = this.searchUnique(search);
		return message;
	}
	
	/**
	 * Find by locale and code.
	 *
	 * @param locale the locale
	 * @param code the code
	 * @return the message resource
	 */
	public MessageResource findByLocaleAndCode(String locale, String code){
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		search.addFilterEqual("code", code);
		return this.searchUnique(search);
	}
}
