package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 2015/9/16.
 */
public class WeekendDetails implements Serializable {
    /**
     * Id : 6c5a955ccf9248f9a33c7998a42c190c
     * Title : 咯哦哦
     * ResidentId : 29abb785b4ad4635bf8ccf17be5896e9
     * Status : 1
     * IsRecord : false
     * StartDatetime : 2017-11-22T11:27:00
     * EndDatetime : 2017-11-23T11:27:00
     * Memo : 金叶
     * Img : ["3b1f4cf7b47948ae9dc2c55a13288cdb"]
     * Cost : 12535.22
     * MemberCount : 0
     * ContractTel : 18603419370
     * ContractName : 金叶
     * ActiveAddress : 嗯嗯啦
     * CityName : 西安市
     * CityId : 610100
     * Url : http://r.goldwg.com/Weekend/Details/6c5a955ccf9248f9a33c7998a42c190c?residentid=
     * Lat : 
     * Lng : 
     */

    public String Id;
    public String Title;
    public String ResidentId;
    public int Status;
    public boolean IsRecord;
    public String StartDatetime;
    public String EndDatetime;
    public String Memo;
    public double Cost;
    public int MemberCount;
    public String ContractTel;
    public String ContractName;
    public String ActiveAddress;
    public String CityName;
    public int CityId;
    public String Url;
    public String Lat;
    public String Lng;
    public List<String> Img;

    public String getCover(){
        if (Img!=null&&Img.size()>0){
            return Img.get(0);
        }else{
            return null;
        }
    }
}
