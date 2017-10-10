package com.example.tresdos.sistemaq;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.newtronlabs.easypermissions.EasyPermissions;
import com.newtronlabs.easypermissions.listener.IPermissionsListener;

import java.util.Set;

public class PermisosService extends Service implements IPermissionsListener{
    public PermisosService() {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Request as many permissions as you like.
        // Make sure that these permissions are in your Manifest as well.
        EasyPermissions.getInstance().requestPermissions(this, this,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onRequestSent(Set<String> set)
    {

    }

    @Override
    public void onFailure()
    {

    }
}
