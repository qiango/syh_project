package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class IllDes {


    /**
     *主诉
     */
    @JSONField(name = "complaint_content")
    private String complaintContent;

    /**
     *现病史
     */
    @JSONField(name = "present_illness")
    private String presentIllness;

    /**
     *既往史
     */
    @JSONField(name = "past_history")
    private String pastHistory;

    /**
     *过敏史
     */
    @JSONField(name = "allergic_history")
    private String allergicHistory;


    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getPresentIllness() {
        return presentIllness;
    }

    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }

    public String getAllergicHistory() {
        return allergicHistory;
    }

    public void setAllergicHistory(String allergicHistory) {
        this.allergicHistory = allergicHistory;
    }
}
