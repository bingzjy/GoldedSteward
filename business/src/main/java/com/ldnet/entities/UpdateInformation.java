package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/10/21.
 */
public class UpdateInformation implements Serializable {
    public String Id;
    public String VersionCode;
    public String VersionName;
    public String Memo;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(String versionCode) {
        VersionCode = versionCode;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }
}
