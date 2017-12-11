package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/6.
 */
public class Score implements Serializable {

    private String ID;
    private String OrtherContent;
    private String RID;
    private String ScoreDay;
    private String SocreCnt;
    private String SocreCntName;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrtherContent() {
        return OrtherContent;
    }

    public void setOrtherContent(String ortherContent) {
        OrtherContent = ortherContent;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getScoreDay() {
        return ScoreDay;
    }

    public void setScoreDay(String scoreDay) {
        ScoreDay = scoreDay;
    }

    public String getSocreCnt() {
        return SocreCnt;
    }

    public void setSocreCnt(String socreCnt) {
        SocreCnt = socreCnt;
    }

    public String getSocreCntName() {
        return SocreCntName;
    }

    public void setSocreCntName(String socreCntName) {
        SocreCntName = socreCntName;
    }
}
