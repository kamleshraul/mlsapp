/*
******************************************************************
File: org.mkcl.els.repository.DocumentRepository.java
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
package org.mkcl.els.repository;

import org.mkcl.els.domain.Document;
import org.springframework.stereotype.Repository;
import com.trg.search.Search;


// TODO: Auto-generated Javadoc
/**
 * The Class DocumentRepository.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Repository
public class DocumentRepository extends BaseRepository<Document, Long>{
	
	
	/**
	 * Find by tag.
	 *
	 * @param tag the tag
	 * @return the document
	 */
	public Document findByTag(String tag) {
		return this.searchUnique(new Search().addFilterEqual("tag",tag));
	}

	/**
	 * Removes the by tag.
	 *
	 * @param tag the tag
	 */
	public void removeByTag(String tag) {
		Document document=findByTag(tag);
		remove(document);
	}
	
}
