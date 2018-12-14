package bigfix.apt.imec.be.shared;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableUtils {
    private ParcelableUtils() {

    }

    public static byte[] marshall(Parcelable parcelable) {
        final Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        final byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }
}
