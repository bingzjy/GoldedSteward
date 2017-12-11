package com.ldnet.entities;

/**
 * Created by zjy on 2017/9/10.
 */
public class ChargAmount {
    private String id;
    private String payment;
    private String giveFee;
    private String userName;
    private String created;
    private boolean checked;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getGiveFee() {
        return giveFee;
    }

    public void setGiveFee(String giveFee) {
        this.giveFee = giveFee;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public ChargAmount(String id, String payment, String giveFee, String userName, String created) {
        this.id = id;
        this.payment = payment;
        this.giveFee = giveFee;
        this.userName = userName;
        this.created = created;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
