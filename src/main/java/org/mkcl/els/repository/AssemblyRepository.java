/*
******************************************************************
File: org.mkcl.els.repository.AssemblyRepository.java
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
package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.domain.AssemblyStructure;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class AssemblyRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class AssemblyRepository extends BaseRepository<Assembly, Long>{

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the assembly
	 */
	
	public Assembly findByAssembly(String assembly) {
		Search search = new Search();
		search.addFilterEqual("assembly", assembly);
		return 	this.searchUnique(search);

	}

	public Assembly findCurrentAssembly() {
		Date currentDate=new Date();		
		return null;
	}

	public List<Assembly> findAllSorted(String locale) {
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		search.addSort("assemblyStartDate", true);
		return 	this.search(search);		
	}
	
	
}
