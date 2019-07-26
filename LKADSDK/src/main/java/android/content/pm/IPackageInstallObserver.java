package android.content.pm;

public interface IPackageInstallObserver extends android.os.IInterface {

    abstract class Stub extends android.os.Binder implements android.content.pm.IPackageInstallObserver {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static android.content.pm.IPackageInstallObserver asInterface(android.os.IBinder obj) {
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

    void packageInstalled(java.lang.String packageName, int returnCode)
            throws android.os.RemoteException;
}
