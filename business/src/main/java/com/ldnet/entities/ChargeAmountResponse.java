package com.ldnet.entities;

/**
 * Created by zjy on 2017/9/12.
 */
public class ChargeAmountResponse {

//      "userId":"",  //用户ID
//              "money":"10",
//              "orderNo":"1233444", //订单编号，支付宝使用
//              "prepayId":"wxyuyuiyi", //微信充值返回预支付订单
//              "nonce_str":"fdsfafdsafsd",//微信充值返回随机字符串
//              "sign":"xerttewq3454521243",//微信充值返回签名
//              "tn":"f3tr534fdsczxcsdf",//银联支付返回

    private String userId;
    private String money;
    private String orderNo;
    private String prepayId;
    private String nonce_str;
    private String sign;
    private String tn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public ChargeAmountResponse(String userId, String money, String orderNo, String prepayId, String nonce_str, String sign, String tn) {
        this.userId = userId;
        this.money = money;
        this.orderNo = orderNo;
        this.prepayId = prepayId;
        this.nonce_str = nonce_str;
        this.sign = sign;
        this.tn = tn;
    }
}
