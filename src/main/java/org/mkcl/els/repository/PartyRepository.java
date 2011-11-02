/*
******************************************************************
File: org.mkcl.els.repository.PartyRepository.java
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.domain.Party;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class PartyRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class PartyRepository 
	extends BaseRepository<Party, Long>{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Party findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		Party party = this.searchUnique(search);
		return party;
	}

	public List<Party> findAllSorted() {
		Search search=new Search();
		search.addSort("name",false);
		return this.search(search);		
	}

	public List<MasterVO> findAllSortedVO(String locale) {
		String query="SELECT name FROM parties WHERE locale = '"+ locale+ "' ORDER BY name asc";	
		RowMapper<MasterVO> mapper = new RowMapper<MasterVO>() {
			@Override
			public MasterVO mapRow(ResultSet rs, int rowNo) throws SQLException {
				MasterVO masterVO = new MasterVO();
				masterVO.setName(rs.getString("name"));		
				return masterVO;
			}
		};	
		return jdbcTemplate.query(query, mapper,new Object[]{});
	}
}
