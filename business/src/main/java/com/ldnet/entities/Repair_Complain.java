package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/9.
 */
public class Repair_Complain implements Serializable {
    public String Id;
    public Integer Status;
    public String Content;
    public String DateTime;
    public Float Score;
    public Integer Type;
    public Integer NewCount;

    public String StatusName() {
        return Repair_Complain_Status.getStatus(Status);
    }

    public String TypeName() {
        return Repair_Complain_Status.getTypes(Type);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public Float getScore() {
        return Score;
    }

    public void setScore(Float score) {
        Score = score;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public Integer getNewCount() {
        return NewCount;
    }

    public void setNewCount(Integer newCount) {
        NewCount = newCount;
    }
}
