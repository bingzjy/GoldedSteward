package com.ldnet.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.entities
 * @class describe
 * @time 2018/1/3 17:30
 * @change
 * @chang time
 * @class describe
 */

public class OwnerRoomRelation {

    /**
     * Abbreviation : 202文明家属院 88888栋1单元702室
     * RoomID : null
     * CommunityID : 34e56c91b8bf40df9e33ed0ded477f70
     * Resident : [{"Name":"18629656320","Id":"c76dd7abba774037a72aa6aa3c40065a","Image":"","bindTime":"2017-06-17","ResidentTypeName":"家属","ResidentType":1,"Leasedates":"2017-06-17","Leasedatee":"2017-06-17"},{"Name":"18629649892","Id":"c9b56faf72d74cdc8d51b18821e4edc7","Image":"","bindTime":"2017-06-12","ResidentTypeName":"家属","ResidentType":1,"Leasedates":"2017-06-12","Leasedatee":"2017-06-12"}]
     */

    public String Abbreviation;
    public String RoomID;
    public String CommunityID;
    public List<ResidentBean> Resident;


    public static class ResidentBean {
        /**
         * Name : 18629656320
         * Id : c76dd7abba774037a72aa6aa3c40065a
         * Image :
         * bindTime : 2017-06-17
         * ResidentTypeName : 家属
         * ResidentType : 1
         * Leasedates : 2017-06-17
         * Leasedatee : 2017-06-17
         */

        public String Name;
        public String Id;
        public String Image;
        public String Tel;
        public String bindTime;
        public String ResidentTypeName;
        public int ResidentType;
        public String Leasedates;
        public String Leasedatee;


        //租户是否有效 0有效 1预警 2已失效
        public int residentState() {
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date endDate = mFormat.parse(Leasedatee);
                Date currentDate = mFormat.parse(mFormat.format(new Date()));
                if (endDate.getTime() > currentDate.getTime()) {
                    return 0;
                } else if (endDate.getTime() == currentDate.getTime()) {
                    return 1;
                } else {
                    return 2;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return 0;
        }

    }


}
