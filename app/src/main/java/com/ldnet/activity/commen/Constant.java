package com.ldnet.activity.commen;

import com.ldnet.entities.KeyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 2017/5/3.
 */
public class Constant {
    public static final int SetCurrentInforamtionOK=111;
    public static final int GetCurrentInforamtionOK=112;
    public static final int GetScanResult=113;
    public static final int GetKeyChainOK=113;
    public static final int GetKeyChainNull=114;


    public static final int GetLocationSuss=115;
    public static final int GetLocationFail=116;


    public static List<KeyChain> keyChainPublic=new ArrayList<>();
    public  static Map<String,List<String>> sendSmsCount=new HashMap<String,List<String>>();
    public static List<String> timeCount=new ArrayList<String>();
}
