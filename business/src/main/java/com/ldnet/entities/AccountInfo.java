package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/1/21.
 */
public class AccountInfo implements Serializable {
    public String ResidentID;
    public String GradeTitle;
    public Float Balance ;
    public Boolean Status;
    public Integer GradeID;

    public String getResidentID() {
        return ResidentID;
    }

    public void setResidentID(String residentID) {
        ResidentID = residentID;
    }

    public String getGradeTitle() {
        return GradeTitle;
    }

    public void setGradeTitle(String gradeTitle) {
        GradeTitle = gradeTitle;
    }

    public Float getBalance() {
        return Balance;
    }

    public void setBalance(Float balance) {
        Balance = balance;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public Integer getGradeID() {
        return GradeID;
    }

    public void setGradeID(Integer gradeID) {
        GradeID = gradeID;
    }
}
