package ga.agoponenko.rockpaperscissors;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QREncoder {

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    //public final static int WIDTH = 400;
    //public final static int HEIGHT = 400;
    //public final static String STR = "A string to be encoded as QR code";



    public static Bitmap encodeAsBitmap(String str, int size) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                                                    BarcodeFormat.QR_CODE, size, size, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private Context mContext;

    public QREncoder(Context context) {
        mContext = context;
    }

    public Bitmap encodeHint(long hint){
        try {
            return QREncoder.encodeAsBitmap("" + hint,
                                            mContext.getResources().getDimensionPixelSize(
                                                  R.dimen.cubeSize) * 2);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
