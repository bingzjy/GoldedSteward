package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/8.
 */
public class Integrals implements Serializable {
    public String Id;
    public String Created;
    public String ResidentId;
    public String Title;
    public Integer Score;
    public String Memo;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getResidentId() {
        return ResidentId;
    }

    public void setResidentId(String residentId) {
        ResidentId = residentId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Integer getScore() {
        return Score;
    }

    public void setScore(Integer score) {
        Score = score;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }
}
