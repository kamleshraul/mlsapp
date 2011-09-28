/*
******************************************************************
File: org.mkcl.els.repository.ConstituencyRepository.java
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class ConstituencyRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class ConstituencyRepository 
	extends BaseRepository<Constituency, Long>{

	@Autowired
	DistrictRepository districtRepository;
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the constituency
	 */
	public Constituency findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		Constituency constituency = this.searchUnique(search);
		return constituency;
	}

	@SuppressWarnings("unchecked")
	public List<Constituency> findConstituenciesByDistrictName(String name) {
		District district=districtRepository.findByName(name);
		String constituencyQuery="SELECT c FROM Constituency c WHERE :district MEMBER OF c.districts";
		Query query=this.em().createQuery(constituencyQuery);
		query.setParameter("district",district);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Constituency> findConstituenciesByDistrictId(Long districtId) {
		District district=districtRepository.find(districtId);
		String constituencyQuery="SELECT c FROM Constituency c WHERE :district MEMBER OF c.districts";
		Query query=this.em().createQuery(constituencyQuery);
		query.setParameter("district",district);
		return query.getResultList();
	}

	public List<Reference> findConstituenciesStartingWith(String param) {
		List<Reference> constituencies = new ArrayList<Reference>();
		Search search =  new Search().addField("name").addField("name","id").addFilterILike("name", param+"%");
		search.setResultMode(Search.RESULT_MAP);
		constituencies=this.search(search);
		return constituencies;
	}

	public List<Constituency> findAllSorted() {
		Search search=new Search();
		search.addSort("name",false);
		return this.search(search);
	}

	
}
