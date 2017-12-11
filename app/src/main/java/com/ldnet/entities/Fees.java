package com.ldnet.entities;

import net.tsz.afinal.annotation.sqlite.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 2015/9/22.
 */
public class Fees implements Serializable {

    @Id(column="id")//自定义主键名称
    public String id;
    public String FeeDate;
    public Float Sum;
    public List<com.ldnet.entities.lstAPPFees> lstAPPFees;
    public Boolean IsChecked;

    //获取费用的日期
    public String DateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy年MM月");
        try {
            Date datetime = dateFormat.parse(FeeDate);
            return dateFormat1.format(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return FeeDate;
    }


    //为缴费用和值
    public Float UnpaidSum() {
        BigDecimal unpaidSum = new BigDecimal("0.00");
        unpaidSum.setScale(2,BigDecimal.ROUND_HALF_UP);
        for (com.ldnet.entities.lstAPPFees fee : lstAPPFees) {
            if (!fee.Status) {
                unpaidSum = unpaidSum.add(new BigDecimal(fee.Payable.toString()));
            }
        }
        return unpaidSum.floatValue();
    }

    //是否已缴完所有费用
    public Boolean isPaid() {
        if (UnpaidSum().equals(0.00f)) {
            return true;
        }
        return false;
    }

    public String getFeeDate() {
        return FeeDate;
    }

    public void setFeeDate(String feeDate) {
        FeeDate = feeDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getSum() {
        return Sum;
    }

    public void setSum(Float sum) {
        Sum = sum;
    }

    public List<com.ldnet.entities.lstAPPFees> getLstAPPFees() {
        return lstAPPFees;
    }

    public void setLstAPPFees(List<com.ldnet.entities.lstAPPFees> lstAPPFees) {
        this.lstAPPFees = lstAPPFees;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }
}
