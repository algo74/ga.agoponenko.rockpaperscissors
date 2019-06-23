package ga.agoponenko.rockpaperscissors;

import android.os.Handler;
import android.os.HandlerThread;

public class GameModelBackgroundThread extends HandlerThread {
    private static final String TAG = "GameModelBackgroundThread";
    private static final int MESSAGE = 0;

    private Handler mRequestHandler;


    public GameModelBackgroundThread() {
        super(TAG);
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler();
    }

    public void queueTask(Runnable task) {
        mRequestHandler.post(task);
    }
}
