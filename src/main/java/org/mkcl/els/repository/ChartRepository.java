package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class ChartRepository extends BaseRepository<Chart, Long> {

	public Chart find(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("session", session);
		search.addFilterEqual("group", group);
		search.addFilterEqual("answeringDate", answeringDate);
		search.addFilterEqual("locale", locale);
		Chart chart = this.searchUnique(search);
		return chart;
	}

	public List<Member> findMembers(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter =
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());

		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strQuery = "SELECT m" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.member m" +
				" WHERE c.session.id = " + session.getId() +
				" AND c.group.id = " + group.getId() +
				" AND c.answeringDate = '" + date + "'" +
				" AND c.locale = '" + locale + "'";

		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}

	public List<Question> findQuestions(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");

		String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
		String strQuery = "SELECT q" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
				" WHERE c.session.id = " + session.getId() +
				" AND c.group.id = " + group.getId() +
				" AND c.answeringDate = '" + date + "'" +
				" AND c.locale = '" + locale + "'";

		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		List<Question> questions = query.getResultList();
		return questions;
	}

	public List<Question> findQuestions(final Session session,
			final Group group,
			final Date answeringDate,
			final String sortOrder,
			final String locale) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");

		String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
		String strQuery = "SELECT q" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
				" WHERE c.session.id = " + session.getId() +
				" AND c.group.id = " + group.getId() +
				" AND c.answeringDate = '" + date + "'" +
				" AND c.locale = '" + locale + "'" +
				" ORDER BY q.number " + sortOrder;

		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		List<Question> questions = query.getResultList();
		return questions;
	}

	public List<Question> findQuestions(final Member member,
			final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter =
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());

		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strQuery = "SELECT q" +
				" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
				" WHERE c.session.id = " + session.getId() +
				" AND c.group.id = " + group.getId() +
				" AND c.answeringDate = '" + date + "'" +
				" AND c.locale = '" + locale + "'" +
				" AND ce.member.id = " + member.getId();

		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		List<Question> questions = query.getResultList();
		return questions;
	}

	public Chart find(final Question question) {
        String query = "SELECT c " +
        		" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
        		" WHERE q.id = " + question.getId();
        try {
            return (Chart) this.em().createQuery(query).getSingleResult();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return new Chart();
        }
    }

	public Boolean isProcessed(final Session session,
            final Group group,
            final Date answeringDate,
            final String excludeInternalStatus,
            final String locale) {
        CustomParameter parameter =
                CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());

        String strQuery = "SELECT COUNT(q)" +
        " FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
        " WHERE c.session.id = " + session.getId() +
        " AND c.group.id = " + group.getId() +
        " AND c.answeringDate = '" + date + "'" +
        " AND c.locale = '" + locale + "'" +
        " AND q.internalStatus.type = '" + excludeInternalStatus + "'";

        TypedQuery<Long> query = this.em().createQuery(strQuery, Long.class);
        long count = query.getSingleResult();
        if(count == 0) {
            return true;
        }
        return false;
    }

	/**
     * Update chartAnsweringdate, internalStatus, and recommendationStatus
     * of all the Questions on @param chart to @param chartAnsweringDate,
     * @param internalStatus and @param recommendationStatus respectively.
     */
    public void updateChartQuestions(final Chart chart,
    		final QuestionDates chartAnsweringDate,
    		final Status internalStatus,
    		final Status recommendationStatus) {
        Session session = chart.getSession();
        Group group = chart.getGroup();
        String locale = chart.getLocale();
        Date answeringDate = chart.getAnsweringDate();

        CustomParameter parameter =
            CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());

        String query = "UPDATE questions" +
            " SET chart_answering_date = " + chartAnsweringDate.getId() + "," +
            " internalstatus_id = " + internalStatus.getId() + "," +
            " recommendationstatus_id = " + recommendationStatus.getId() +
            " WHERE id IN (" +
                " SELECT qid FROM (" +
                    " SELECT q.id AS qid" +
                    " FROM charts AS c JOIN charts_chart_entries AS cce" +
                    " JOIN chart_entries_questions AS ceq JOIN questions AS q" +
                    " WHERE c.id = cce.chart_id" +
                    " AND cce.chart_entry_id = ceq.chart_entry_id" +
                    " AND ceq.question_id = q.id" +
                    " AND c.session_id = " + session.getId() +
                    " AND c.group_id = " + group.getId() +
                    " AND c.answering_date = '" + date + "'" +
                    " AND c.locale = '" + locale + "'" +
                    " ) AS rs" +
                " )";
        this.em().createNativeQuery(query).executeUpdate();
    }
}
