/*
******************************************************************
File: org.mkcl.els.repository.StateRepository.java
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

import java.util.List;

import org.mkcl.els.domain.State;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class StateRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class StateRepository 
	extends BaseRepository<State, Long>{

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the state
	 */
	public State findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		State state = this.searchUnique(search);
		return state;
	}
	
	public List<State> findAllSorted(){		
		return findAllSorted("name",false);
	}
	
	public List<State> findAllSorted(String property,boolean descOrder){
		Search search=new Search();
		search.addSort(property,descOrder);
		return this.search(search);
	}
}
