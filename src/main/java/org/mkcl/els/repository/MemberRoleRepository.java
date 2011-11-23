package org.mkcl.els.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Query;
import javax.sql.DataSource;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.service.IAssemblyRoleService;
import org.mkcl.els.service.IAssemblyService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberRoleRepository extends BaseRepository<MemberRole, Long>{

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IMemberDetailsService memberDetailsService;
	
	@Autowired
	private IAssemblyService assemblyService;
	
	@Autowired
	private ICustomParameterService customParameterService;
	
	@Autowired
	private AssemblyRoleRepository assemblyRoleRepository;
	
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}	
	public void createMemberRole(long role, Long memberId, MemberRole memberRole) {
		String insertQuery="insert into member_roles(locale,remarks,to_date,from_date,version,assembly,member,role) values("+
		"'"+memberRole.getLocale()+"',"+
		"'"+memberRole.getRemarks()+"',"+
		"'"+memberRole.getToDate()+"',"+
		"'"+memberRole.getFromDate()+"',"+
		memberRole.getAssembly().getId()+","+
		memberId+","+
		role+")";
		jdbcTemplate.update(insertQuery);
	}
	public List<MemberRole> findByMemberId(Long memberId) {
		Search search=new Search();
		search.addFilterEqual("member",memberDetailsService.findById(memberId));
		return this.search(search);
	}
	public List<MemberInRoleVO> findUnassignedMembers(Long roleId) {
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id not in(select member from member_roles where role="+roleId+" and status='Unassigned')";
		RowMapper<MemberInRoleVO> mapper = new RowMapper<MemberInRoleVO>() {
			@Override
			public MemberInRoleVO mapRow(ResultSet rs, int arg1) throws SQLException {
				MemberInRoleVO memberInRoleVO = new MemberInRoleVO();
				memberInRoleVO.setId(rs.getLong("id"));
				memberInRoleVO.setName(rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				return memberInRoleVO;
			}
		};		
		return jdbcTemplate.query(query,mapper,new Object[]{});
	}
	public List<MemberInRoleVO> findAssignedMembers(Long roleId) {
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id in(select member from member_roles where role="+roleId+" and status='Assigned')";
		RowMapper<MemberInRoleVO> mapper = new RowMapper<MemberInRoleVO>() {
			@Override
			public MemberInRoleVO mapRow(ResultSet rs, int arg1) throws SQLException {
				MemberInRoleVO memberInRoleVO = new MemberInRoleVO();
				memberInRoleVO.setId(rs.getLong("id"));
				memberInRoleVO.setName(rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				return memberInRoleVO;
			}
		};		
		return jdbcTemplate.query(query,mapper,new Object[]{});
	}
	public List<MemberRole> findByRoleId(Long roleId) {
		Search search=new Search();
		search.addFilterEqual("role",assemblyRoleRepository.find(roleId));
		return this.search(search);
	}
	
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, String sQl, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, Locale locale) {
		String select="SELECT m FROM MemberDetails m WHERE m.id not in(SELECT mr.member.id FROM MemberRole mr WHERE mr.role.id="+roleId+" and status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and m.locale='"+locale+"' ORDER BY " + sidx + " " + order;
		String count_select ="SELECT count(m) FROM MemberDetails m WHERE m.id not in(SELECT mr.member.id FROM MemberRole mr WHERE mr.role.id="+roleId+" and status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and m.locale='"+locale+"'";
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}	
	
	public GridData getAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.role.id="+ roleId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"'"+ sQl + " ORDER BY " + sidx + " " + order;
		String select= "SELECT m,role,assembly,member FROM MemberRole m JOIN m.member member JOIN m.role role JOIN m.assembly assembly WHERE m.role.id=" + roleId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"'" + sQl +" ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}	
	public GridData getAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.role.id="+roleId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		String select= "SELECT m FROM MemberRole m  WHERE m.role.id="+roleId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}
	
	public GridData getAssignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.member.id="+ memberId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"'"+ sQl + " ORDER BY " + sidx + " " + order;
		String select= "SELECT m,role,assembly,member FROM MemberRole m JOIN m.member member JOIN m.role role JOIN m.assembly assembly WHERE m.member.id=" + memberId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"'" + sQl +" ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
		}
	
	@SuppressWarnings("unchecked")
	public GridData getAssignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.member.id="+memberId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		String select= "SELECT m FROM MemberRole m  WHERE m.member.id="+memberId+" and m.status='"+ customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}
	
	public MemberRole checkForDuplicateMemberRole(MemberRole memberRole) {
		Search search=new Search();
		search.addFilterEqual("fromDate",memberRole.getFromDate());
		search.addFilterEqual("toDate",memberRole.getToDate());
		search.addFilterEqual("member",memberRole.getMember());
		search.addFilterEqual("assembly",memberRole.getAssembly());
		search.addFilterEqual("role",memberRole.getRole());
		search.addFilterEqual("locale",memberRole.getLocale());
		return this.searchUnique(search);
		
	}
	public boolean isMember(MemberDetails memberDetails, Assembly assembly, String fromdate, String todate) {
		Search search=new Search();
		search.addFilterEqual("member",memberDetails);
		search.addFilterEqual("assembly",assembly);
		search.addFilterEqual("role",assemblyRoleRepository.findByName("Member"));
		search.addFilterEqual("fromDate", fromdate);
		search.addFilterEqual("toDate",todate);
		search.addFilterEqual("status",customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue());
		int count=this.count(search);
		if(count>0){
			return true;
		}else{
			return false;
		}		
	}

}
