package com.example.android.sairamedicalstore.models;

import java.util.HashMap;

/**
 * Created by chandan on 15-12-2017.
 */

public class Faq {
    String faqId,faqQuestion;
    int faqPriority;
    private HashMap<String, Object> faqAnswers;

    public Faq() {
    }

    public Faq(String faqId, String faqQuestion, int faqPriority, HashMap<String, Object> faqAnswers) {
        this.faqId = faqId;
        this.faqQuestion = faqQuestion;
        this.faqPriority = faqPriority;
        this.faqAnswers = faqAnswers;
    }

    public String getFaqId() {
        return faqId;
    }

    public void setFaqId(String faqId) {
        this.faqId = faqId;
    }

    public String getFaqQuestion() {
        return faqQuestion;
    }

    public void setFaqQuestion(String faqQuestion) {
        this.faqQuestion = faqQuestion;
    }

    public int getFaqPriority() {
        return faqPriority;
    }

    public void setFaqPriority(int faqPriority) {
        this.faqPriority = faqPriority;
    }

    public HashMap<String, Object> getFaqAnswers() {
        return faqAnswers;
    }

    public void setFaqAnswers(HashMap<String, Object> faqAnswers) {
        this.faqAnswers = faqAnswers;
    }
}
