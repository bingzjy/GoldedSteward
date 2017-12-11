package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zxs on 2015/12/9.
 */
public class APPHomePage_Area implements Serializable {


    public List<com.ldnet.entities.APPHomePage_Row> APPHomePage_Row;

    public List<com.ldnet.entities.APPHomePage_Row> getAPPHomePage_Row() {
        return APPHomePage_Row;
    }

    public void setAPPHomePage_Row(List<com.ldnet.entities.APPHomePage_Row> APPHomePage_Row) {
        this.APPHomePage_Row = APPHomePage_Row;
    }

    public Float getRowHeightBI() {
        Float bi = 0.0f;
        for (com.ldnet.entities.APPHomePage_Row row : APPHomePage_Row) {
            bi += Float.parseFloat(row.HEIGHTBI);
        }
        return bi;
    }

}
