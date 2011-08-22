/*
 ******************************************************************
File: org.mkcl.els.common.vo.Filter.java
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
package org.mkcl.els.common.vo;

import com.google.gson.Gson;

/**
 * The Class Filter.
 *
 * @author vishals
 * @version v1.0.0
 */
public class Filter {

	/** The group op. */
	private String groupOp;

	/** The rules. */
	private Rule[] rules;


	/**
	 * Gets the group op.
	 *
	 * @return the group op
	 */
	public String getGroupOp() {
		return groupOp;
	}

	/**
	 * Sets the group op.
	 *
	 * @param groupOp the new group op
	 */
	public void setGroupOp(String groupOp) {
		this.groupOp = groupOp;
	}

	/**
	 * Gets the rules.
	 *
	 * @return the rules
	 */
	public Rule[] getRules() {
		return rules;
	}

	/**
	 * Sets the rules.
	 *
	 * @param rules the new rules
	 */
	public void setRules(Rule[] rules) {
		this.rules = rules;
	}


	/**
	 * Creates the filter based on Json string.
	 *
	 * @param json the json
	 * @return the filter
	 */
	public static Filter create(String json){
		Gson gson = new Gson();
		Filter filter = gson.fromJson(json, Filter.class);
		return filter;
	}

	public String toSQl(){
		StringBuilder sql = new StringBuilder();
		String delim = "";
		for(Rule rule:this.rules){
			sql = sql.append(delim).append(resolveToSqlCondition(rule.getField(),rule.getOp(),rule.getData()));
			delim = " "+ this.groupOp + " ";
		}
		return " AND ( " + sql.toString() + " ) ";
	}
	
	/**
	 * Resolve to sql condition.
	 *
	 * @param field the field
	 * @param op the op
	 * @param data the data
	 * @return the string
	 */
	private String resolveToSqlCondition(String field, String op, String data){
		String sqlOp = "";
		if(op.equals("eq")){
			sqlOp = field + "='" + data + "'";
		}
		else if(op.equals("ne")){
			sqlOp = field + "!='" + data + "'";
		}
		else if(op.equals("bw")){
			sqlOp = " ( LIKE '" + field + data + "%') ";
		}
		else if(op.equals("bn")){
			sqlOp = " ( NOT LIKE '" + field + data + "%') ";
		}
		else if(op.equals("ew")){
			sqlOp = " ( LIKE '%" + field + data + "') ";
		}
		else if(op.equals("en")){
			sqlOp = " ( NOT LIKE '%" + field + data + "') ";
		}
		else if(op.equals("cn")){
			sqlOp = " ( LIKE '%" + field + data + "%') ";
		}
		else if(op.equals("nc")){
			sqlOp = " ( NOT LIKE '%" + field + data + "%') ";
		}
		else if(op.equals("nu")){
			sqlOp = " (" + field  + " IS NULL) ";
		}
		else if(op.equals("nn")){
			sqlOp = " (" + field  + " IS NOT NULL) ";
		}
		return sqlOp;
	}
}
