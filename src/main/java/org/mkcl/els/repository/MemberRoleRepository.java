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
	private IAssemblyRoleService assemblyRoleService;
	
	
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
		search.addFilterEqual("role",assemblyRoleService.findById(roleId));
		return this.search(search);
	}
	public GridData getAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
				return null;
	
	}
	
	public GridData getAssignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		String count_select ="SELECT count(*) FROM member_roles WHERE role="+roleId;
		RowMapper<Map<String,Object>> mapper = new RowMapper<Map<String,Object>>() {
			@Override
			public Map<String,Object> mapRow(ResultSet rs, int arg1) throws SQLException {
				Map<String,Object> memberRole = new HashMap<String, Object>();
				memberRole.put("id", rs.getLong("id"));
				memberRole.put("locale", rs.getString("locale"));
				memberRole.put("remarks", rs.getString("remarks"));
				memberRole.put("fromDate",rs.getDate("from_date")==null?"-": new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(rs.getDate("from_date")));
				memberRole.put("toDate", rs.getDate("to_date")==null?"-":new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()).format(rs.getDate("to_date")));
				memberRole.put("version", rs.getLong("version"));
				memberRole.put("memberName", rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				memberRole.put("memberId", rs.getLong("memberId"));
				memberRole.put("assemblyId", rs.getLong("assemblyId"));
				memberRole.put("assemblyName", rs.getString("assemblyName"));
				memberRole.put("roleId", rs.getLong("roleId"));
				memberRole.put("roleName", rs.getString("roleName"));
				return memberRole;
			}
		};		
	int count =  (int) jdbcTemplate.queryForInt(count_select);
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
	int max=0;
	if(count>rows){
		max=count;
	}else{
		max=rows;
	}
	String select= "SELECT mr.id,mr.locale,mr.remarks,mr.to_date,mr.from_date,mr.version,md.first_name,md.middle_name,md.last_name,md.id as memberId,ar.id as roleId,ar.name as roleName,a.id as assemblyId,a.assembly as assemblyName FROM member_roles as mr JOIN member_details as md JOIN assemblies as a JOIN assembly_roles as ar WHERE mr.member=md.id and mr.role=ar.id and mr.assembly=a.id and mr.status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"' and mr.role="+roleId+" LIMIT "+start+","+max;
	List<Map<String,Object>> records = jdbcTemplate.query(select,mapper,new Object[]{});
	GridData gridVO = new GridData(page,total_pages,count,records);
	return gridVO;
	}
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, String sQl, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, Locale locale) {
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id not in(select member from member_roles where role="+roleId+" and status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"')";

		String count_select ="SELECT count(*) from member_details as md where md.id not in(select member from member_roles where role="+roleId+" and status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"')";
		RowMapper<Map<String,Object>> mapper = new RowMapper<Map<String,Object>>() {
			@Override
			public Map<String,Object> mapRow(ResultSet rs, int arg1) throws SQLException {
				Map<String,Object> members = new HashMap<String, Object>();
				members.put("id", rs.getLong("id"));
				members.put("memberName", rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				return members;
			}
		};		
	int count =  (int) jdbcTemplate.queryForInt(count_select);
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
	int max=0;
	if(count>rows){
		max=count;
	}else{
		max=rows;
	}
	String select= "SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id not in(select member from member_roles where role="+roleId+" and status='"+customParameterService.findByName("MEMBERROLE_ASSIGNED").getValue()+"')  LIMIT "+start+","+max;
	List<Map<String,Object>> records = jdbcTemplate.query(select,mapper,new Object[]{});
	GridData gridVO = new GridData(page,total_pages,count,records);
	return gridVO;
	}

}
