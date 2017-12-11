package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/2.
 */
public class Property implements Serializable {

    public String Explain;
    public String ID;
    public String NodesID;
    public String NodesName;
    public String OperateDay;
    public String OperateName;
    public String RID;
    public String Remark;
    public String StaffID;
    public String StaffName;
    public String StaffTel;

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNodesID() {
        return NodesID;
    }

    public void setNodesID(String nodesID) {
        NodesID = nodesID;
    }

    public String getNodesName() {
        return NodesName;
    }

    public void setNodesName(String nodesName) {
        NodesName = nodesName;
    }

    public String getOperateDay() {
        return OperateDay;
    }

    public void setOperateDay(String operateDay) {
        OperateDay = operateDay;
    }

    public String getOperateName() {
        return OperateName;
    }

    public void setOperateName(String operateName) {
        OperateName = operateName;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getStaffID() {
        return StaffID;
    }

    public void setStaffID(String staffID) {
        StaffID = staffID;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String staffName) {
        StaffName = staffName;
    }

    public String getStaffTel() {
        return StaffTel;
    }

    public void setStaffTel(String staffTel) {
        StaffTel = staffTel;
    }
}
