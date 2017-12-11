package com.ldnet.entities;

/**
 * Created by lee on 2017/9/20
 */

public class AccountRecord {
    /**
     * content : 钱包支付
     * created : 1503904103000
     * money : 1
     * type : 0
     */

    public String content;
    public long created;
    public int money;
    public int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
