package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/7/5.
 */
public class Cookies implements Serializable {

    public String domain;
    public String cookieinfo;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCookieinfo() {
        return cookieinfo;
    }

    public void setCookieinfo(String cookieinfo) {
        this.cookieinfo = cookieinfo;
    }
}
