package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 2015/9/16.
 */
public class FreaMarketDetails implements Serializable {
    /**
     * Id : c7cc22fb14324dd2be7cf4555c08b45b
     * Address : 陵东街37号
     * CommunityId : 34e56c91b8bf40df9e33ed0ded477f70
     * ContractName : 15129291264
     * ContractTel : 15129291264
     * Img : ["a96aa50908f844f7a908b166026584c5"]
     * Memo : 创富志杂志合集。原价325.现价100
     * OrgPrice : 100
     * Price : 200
     * ResidentId : a74b5879dbe64be6aaf8e8f34b4a63aa
     * Title : 书籍
     * Updated : 2017-09-22T14:42:19.31
     * Url : http://r.goldwg.com/Unusedgoods/Details/c7cc22fb14324dd2be7cf4555c08b45b?residentid=
     */

    public String Id;
    public String Address;
    public String CommunityId;
    public String ContractName;
    public String ContractTel;
    public String Memo;
    public String OrgPrice;
    public int Price;
    public String ResidentId;
    public String Title;
    public String Updated;
    public String Url;
    public List<String> Img;

}
