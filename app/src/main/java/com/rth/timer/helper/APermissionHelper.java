package com.rth.timer.helper;

import android.Manifest;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class APermissionHelper {

    public APermissionHelper(){
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Klik Izinkan - 'Allow, agar dapat menggunakan Aplikasi ini!\n\nApabila belum diaktifkan maka check di [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .check();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
           // Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            // Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };
}
