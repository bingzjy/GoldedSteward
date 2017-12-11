package com.ldnet.utility;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//保存Cookie，并在需要的时候提供Cookie
public class ReadInfoIDs {
    // 数据库存储Cookie
    private SQLiteDatabase mDatabase;
    private DatabaseManager mDatabaseManager;

    //标记为已读通知或资讯的ID
    public final int TYPE_NOTIFICATION = 1;
    public final int TYPE_INFORMATION = 2;

    // 实现一个单例
    private ReadInfoIDs() {
        mDatabaseManager = DatabaseManager.getInstance();
    }

    // ？
    private static ReadInfoIDs readInfoIDs = null;

    // 静态的获取HttpCookies的实体
    public static ReadInfoIDs getInstance() {
        if (readInfoIDs == null) {
            readInfoIDs = new ReadInfoIDs();
        }
        return readInfoIDs;
    }

    // 保存Cookie
    public void setRead(String id, Integer type) {
        // 保存到数据库中
        mDatabase = mDatabaseManager.openDatabase();

        if (!isExists(id, type)) {
            // 插入新的cookie
            ContentValues cv = new ContentValues();
            cv.put("id", id);
            cv.put("type", type);
            mDatabase.insert("readids", null, cv);

            // 关闭数据库
            mDatabaseManager.closeDatabase();
        }
    }

    // 获取Cookie
    public List<String> getRead(Integer type) {
        // 从内存中读取Cookie
        List<String> ck = new ArrayList<String>();

        // 从数据库中获取Cookie
        mDatabase = mDatabaseManager.openDatabase();

        // 查询
        Cursor cur = mDatabase.rawQuery(
                "SELECT * FROM readids WHERE type = ?",
                new String[]{String.valueOf(type)});

        //循环读取已读Id
        while (cur.moveToNext()) {
            ck.add(cur.getString(cur.getColumnIndex("id")));
        }

        // 关闭数据库
        mDatabaseManager.closeDatabase();

        return ck;
    }

    // 判断Cookie是否存在
    public boolean isExists(String id, Integer type) {
        for (String s : getRead(type)) {
            if (id.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
