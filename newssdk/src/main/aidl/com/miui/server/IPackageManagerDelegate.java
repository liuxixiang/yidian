package com.miui.server;

import android.net.Uri;

public interface IPackageManagerDelegate extends android.os.IInterface {
    abstract class Stub extends android.os.Binder implements IPackageManagerDelegate {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static IPackageManagerDelegate asInterface(android.os.IBinder obj) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public android.os.IBinder asBinder() {
            throw new RuntimeException("Stub!");
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
                throws android.os.RemoteException {
            throw new RuntimeException("Stub!");
        }
    }

    boolean installPackage(Uri fromFile, android.os.IInterface obj, int i) throws android.os.RemoteException;

}
