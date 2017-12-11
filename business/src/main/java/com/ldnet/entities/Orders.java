package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 2015/9/28.
 */
public class Orders implements Serializable {
    /**
     * OID : d953f102aab84cf9a54dbe3116160a3d
     * OVID : 3
     * OVN : 已发货
     * AM : 92
     * AC : 0
     * BID : e1b568533e1849a9b068359a6de23b9f
     * BN : GMY
     * BM : 18706747232
     * CM : null
     * OT : 1
     * OD : null
     * ODS : [{"SN":"保洁服务","SI":"718ca696b0804716ab04b92b0a6f4d34","P":23,"T":4,"SM":"2017-6-29 16:00-20:00"}]
     * ONB : null
     * KM : 0
     * JYN : null
     * PD : 0001-01-01T00:00:00
     * AN : null
     * AMP : null
     * AR : null
     * ACT : null
     * AA : null
     * AAD : null
     * ISVO : false
     * VOM : 0
     * EName : null
     * ECode : null
     * ENumber : null
     */

    public String OID; //订单ID
    public int OVID; //订单子状态ID 1:待付款，3:已发货，4:已签收，5:待发货，6:已关闭，7：取消
    public String OVN;  //订单子状态名称
    public Float AM;  //总金额
    public int AC; //总件数
    public String BID;  //商家ID
    public String BN; //商家名称
    public String BM; //商家电话
    public List<com.ldnet.entities.OD> OD;//订单明细表
    public String CM;
    public int OT; //订单类型
    public String ONB;  //订单号
    public Float KM;  //配送费
    public String JYN; //交易号
    public String PD; //下单日期
    public String AN;  //收货姓名
    public String AMP; //收货电话
    public String AR; //收货省
    public String ACT; //收货市
    public String AA; //收货区
    public String AAD;  //收货详细
    public boolean ISVO;
    public Float VOM;
    public String EName; //快递名称
    public String ECode;  //快递编码
    public String ENumber; //快递单号
    public List<ODSBean> ODS; //服务类订单明细表

    //是否选中
    public Boolean IsChecked = true;


    public static class ODSBean {
        /**
         * SN : 保洁服务
         * SI : 718ca696b0804716ab04b92b0a6f4d34
         * P : 23  单价
         * T : 4   服务时长
         * SM : 2017-6-29 16:00-20:00
         */
        public String SN;
        public String SI;
        public int P;
        public int T;
        public String SM;
    }


//    public String OID;//订单ID
//    public Integer OVID;//订单子状态ID 1:待付款，3:已发货，4:已签收，5:待发货，6:已关闭，7：取消
//    public String OVN;//订单子状态名称
//    public Float AM;//总金额
//    public Integer AC;//总件数
//    public String BID;//商家ID
//    public String BN;//商家名称
//    public String BM;//商家电话
//    public List<com.ldnet.entities.OD> OD;//订单明细表
//    //----------以下在订单详细接口中才会有的数据---------------
//    public String ONB;//订单号
//    public Float KM;//配送费
//    public String JYN;//交易号
//    public String PD;//下单日期
//    public String AN;//收货姓名
//    public String AMP;//收货电话
//    public String AR;//收货省
//    public String ACT;//收货市
//    public String AA;//收货区
//    public String AAD;//收货详细
//    public String CM;//取消订单原因
//    public String ECode;//快递编码
//    public String EName;//快递名称
//    public String ENumber;//快递单号
//
//    //是否选中
//    public Boolean IsChecked = true;
//
//    public String getOID() {
//        return OID;
//    }
//
//    public void setOID(String OID) {
//        this.OID = OID;
//    }
//
//    public Integer getOVID() {
//        return OVID;
//    }
//
//    public void setOVID(Integer OVID) {
//        this.OVID = OVID;
//    }
//
//    public String getOVN() {
//        return OVN;
//    }
//
//    public void setOVN(String OVN) {
//        this.OVN = OVN;
//    }
//
//    public Float getAM() {
//        return AM;
//    }
//
//    public void setAM(Float AM) {
//        this.AM = AM;
//    }
//
//    public Integer getAC() {
//        return AC;
//    }
//
//    public void setAC(Integer AC) {
//        this.AC = AC;
//    }
//
//    public String getBID() {
//        return BID;
//    }
//
//    public void setBID(String BID) {
//        this.BID = BID;
//    }
//
//    public String getBN() {
//        return BN;
//    }
//
//    public void setBN(String BN) {
//        this.BN = BN;
//    }
//
//    public String getBM() {
//        return BM;
//    }
//
//    public void setBM(String BM) {
//        this.BM = BM;
//    }
//
//    public List<com.ldnet.entities.OD> getOD() {
//        return OD;
//    }
//
//    public void setOD(List<com.ldnet.entities.OD> OD) {
//        this.OD = OD;
//    }
//
//    public String getONB() {
//        return ONB;
//    }
//
//    public void setONB(String ONB) {
//        this.ONB = ONB;
//    }
//
//    public Float getKM() {
//        return KM;
//    }
//
//    public void setKM(Float KM) {
//        this.KM = KM;
//    }
//
//    public String getJYN() {
//        return JYN;
//    }
//
//    public void setJYN(String JYN) {
//        this.JYN = JYN;
//    }
//
//    public String getPD() {
//        return PD;
//    }
//
//    public void setPD(String PD) {
//        this.PD = PD;
//    }
//
//    public String getAN() {
//        return AN;
//    }
//
//    public void setAN(String AN) {
//        this.AN = AN;
//    }
//
//    public String getAMP() {
//        return AMP;
//    }
//
//    public void setAMP(String AMP) {
//        this.AMP = AMP;
//    }
//
//    public String getAR() {
//        return AR;
//    }
//
//    public void setAR(String AR) {
//        this.AR = AR;
//    }
//
//    public String getACT() {
//        return ACT;
//    }
//
//    public void setACT(String ACT) {
//        this.ACT = ACT;
//    }
//
//    public String getAA() {
//        return AA;
//    }
//
//    public void setAA(String AA) {
//        this.AA = AA;
//    }
//
//    public String getAAD() {
//        return AAD;
//    }
//
//    public void setAAD(String AAD) {
//        this.AAD = AAD;
//    }
//
//    public String getCM() {
//        return CM;
//    }
//
//    public void setCM(String CM) {
//        this.CM = CM;
//    }
//
//    public String getECode() {
//        return ECode;
//    }
//
//    public void setECode(String ECode) {
//        this.ECode = ECode;
//    }
//
//    public String getEName() {
//        return EName;
//    }
//
//    public void setEName(String EName) {
//        this.EName = EName;
//    }
//
//    public String getENumber() {
//        return ENumber;
//    }
//
//    public void setENumber(String ENumber) {
//        this.ENumber = ENumber;
//    }
//
//    public Boolean getIsChecked() {
//        return IsChecked;
//    }
//
//    public void setIsChecked(Boolean isChecked) {
//        IsChecked = isChecked;
//    }



}
