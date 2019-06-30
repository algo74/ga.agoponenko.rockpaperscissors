package ga.agoponenko.rockpaperscissors;

import android.os.Looper;

public class GameModelBackgroundThread_PrepareMoveDouble extends GameModelBackgroundThread {

    GameModelBackgroundThread_PrepareMoveDouble() {
    }

    @Override
    public Looper getLooper() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void queueTask(Runnable task) {
        task.run();
    }


}
