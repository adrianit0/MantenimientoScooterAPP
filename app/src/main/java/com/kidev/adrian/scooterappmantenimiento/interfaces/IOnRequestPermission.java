package com.kidev.adrian.scooterappmantenimiento.interfaces;

public interface IOnRequestPermission {
    public void onPermissionAccepted(String permiso);
    public void onPermissionDenied(String permiso);
}
