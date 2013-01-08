package org.mkcl.els.common.vo;


public class QuestionDatesVO {

    private String answeringDate;

    private String finalSubmissionDate;

    private String speakerSendingDate;

    private String lastSendingDateToDepartment;

    private String lastReceivingDateFromDepartment;

    private String yaadiPrintingDate;

    private String yaadiReceivingDate;

    private String suchhiPrintingDate;

    private String suchhiReceivingDate;

    private String suchhiDistributionDate;

    private String group;

    private Long groupId;

    private String dayOfWeek;
    
    private int rowId;


    public int getRowId() {
		return rowId;
	}


	public void setRowId(int rowId) {
		this.rowId = rowId;
	}


	public String getFinalSubmissionDate() {
        return finalSubmissionDate;
    }


    public void setFinalSubmissionDate(final String finalSubmissionDate) {
        this.finalSubmissionDate = finalSubmissionDate;
    }


    public String getAnsweringDate() {
        return answeringDate;
    }


    public void setAnsweringDate(final String answeringDate) {
        this.answeringDate = answeringDate;
    }


    public String getLastSendingDateToDepartment() {
        return lastSendingDateToDepartment;
    }


    public void setLastSendingDateToDepartment(final String lastSendingDateToDepartment) {
        this.lastSendingDateToDepartment = lastSendingDateToDepartment;
    }


    public String getLastReceivingDateFromDepartment() {
        return lastReceivingDateFromDepartment;
    }


    public void setLastReceivingDateFromDepartment(
            final String lastReceivingDateFromDepartment) {
        this.lastReceivingDateFromDepartment = lastReceivingDateFromDepartment;
    }


    public String getYaadiPrintingDate() {
        return yaadiPrintingDate;
    }


    public void setYaadiPrintingDate(final String yaadiPrintingDate) {
        this.yaadiPrintingDate = yaadiPrintingDate;
    }


    public String getYaadiReceivingDate() {
        return yaadiReceivingDate;
    }


    public void setYaadiReceivingDate(final String yaadiReceivingDate) {
        this.yaadiReceivingDate = yaadiReceivingDate;
    }


    public String getSuchhiPrintingDate() {
        return suchhiPrintingDate;
    }


    public void setSuchhiPrintingDate(final String suchhiPrintingDate) {
        this.suchhiPrintingDate = suchhiPrintingDate;
    }


    public String getSuchhiReceivingDate() {
        return suchhiReceivingDate;
    }


    public void setSuchhiReceivingDate(final String suchhiReceivingDate) {
        this.suchhiReceivingDate = suchhiReceivingDate;
    }


    public String getSuchhiDistributionDate() {
        return suchhiDistributionDate;
    }


    public void setSuchhiDistributionDate(final String suchhiDistributionDate) {
        this.suchhiDistributionDate = suchhiDistributionDate;
    }


    public String getGroup() {
        return group;
    }


    public void setGroup(final String group) {
        this.group = group;
    }


    public Long getGroupId() {
        return groupId;
    }


    public void setGroupId(final Long groupId) {
        this.groupId = groupId;
    }


    public void setDayOfWeek(final String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    public String getDayOfWeek() {
        return dayOfWeek;
    }


    public void setSpeakerSendingDate(final String speakerSendingDate) {
        this.speakerSendingDate = speakerSendingDate;
    }


    public String getSpeakerSendingDate() {
        return speakerSendingDate;
    }

}
