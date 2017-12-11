package com.ldnet.entities;

/**
 * Created by Alex on 2015/9/9.
 */
public class Repair_Complain_Status {

    //获取投诉报修的状态
    public static String getStatus(Integer statusCode)
    {
        switch (statusCode)
        {
            case 0:
                return "已提交";
            case 1:
                return "处理中";
            case 2:
                return "已处理";//已结束、已评价
            case 3:
                return "已处理";//已结束、未评价
            case 4:
                return "已处理";//物业关闭
            case 5:
                return "已处理";//业主关闭
            default:
                return "已处理";
        }
    }

    //获取报修的类型
    public static String getTypes(Integer typeCode)
    {
        switch (typeCode)
        {
            case 0:
                return "公共报修";
            case 1:
                return "个人报修";
            default:
                return "";
        }
    }
}
