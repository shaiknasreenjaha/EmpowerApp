package androids.newapp;

/**
 * Created by Lenovo on 08-Feb-18.
 */

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class Utils {
    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}