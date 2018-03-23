package com.ldnet.interfaze;

import java.util.List;

/**
 * Created by lee on 2017/10/24
 */

public interface PermissionListener {
    void onGranted(int requestCode, List<String> deniedPermissions);
    void onDenied(int requestCode, List<String> deniedPermissions);
}
