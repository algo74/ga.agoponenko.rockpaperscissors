package ga.agoponenko.rockpaperscissors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import ga.agoponenko.rockpaperscissors.gamemodel.Move;

class TurningCube {
    public static final int DURATION_COVER = 1000;
    public static final int DURATION_CUBE = 2000;
    public static final int DURATION_INFO = 500;

    private final ImageView mViewCover;
    private final Listener mListener;
    private ImageView mViewNew;
    private ImageView mViewOld;
    private CubeSide mSideNew;
    private CubeSide mSideOld;
    private Context mContext;
    private ValueAnimator mCubeAnimator;
    private boolean mEngineMoveReady;
    private Move mEngineMove;
    private ValueAnimator mEngineMoveReadyAnimator;
    private ValueAnimator mShowEngineMoveAnimator;
    private final ObjectAnimator mHideCoverAnimator;
    private boolean mDelayedShowEngineMove = false;
    private Bitmap mBitmap;

    TurningCube(ImageView view1, ImageView view2, ImageView viewCover, Context context,
                @NonNull Listener listener) {
        mViewNew = view1;
        mViewOld = view2;
        mViewCover = viewCover;
        mContext = context;
        mListener = listener;
        mSideNew = new CubeSide(view1);
        mSideOld = new CubeSide(view2);
        mSideNew.setValue(Move.ROCK);

        mCubeAnimator = ValueAnimator.ofFloat(0f, 4f);
        CubeTurner cubeTurner = new CubeTurner();
        mCubeAnimator.addUpdateListener(cubeTurner);
        mCubeAnimator.addListener(cubeTurner);
        mCubeAnimator.setInterpolator(new LinearInterpolator());
        mCubeAnimator.setDuration(DURATION_CUBE);
        //mCubeAnimator.setRepeatCount(ValueAnimator.INFINITE);

        mEngineMoveReadyAnimator = ValueAnimator.ofFloat(0f, 1f);
        mEngineMoveReadyAnimator.setInterpolator(new LinearInterpolator());
        mEngineMoveReadyAnimator.setDuration(DURATION_COVER);
        EngineMoveTurner engineMoveTurner = new EngineMoveTurner();
        mEngineMoveReadyAnimator.addListener(engineMoveTurner);
        mEngineMoveReadyAnimator.addUpdateListener(engineMoveTurner);

        mShowEngineMoveAnimator = ValueAnimator.ofFloat(0f, 1f);
        mShowEngineMoveAnimator.setDuration(DURATION_COVER);
        EngineMoveShower engineMoveShower = new EngineMoveShower();
        mShowEngineMoveAnimator.addUpdateListener(engineMoveShower);
        mShowEngineMoveAnimator.addListener(engineMoveShower);

        mHideCoverAnimator = ObjectAnimator.ofFloat(mViewCover,"alpha", 1f, 0f);
        mHideCoverAnimator.setDuration(DURATION_INFO);
        mHideCoverAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mViewNew.setVisibility(View.VISIBLE);
                mViewNew.setScaleX(1);
                mViewNew.setScaleY(1);
                mViewNew.setRotationY(0);
                mViewNew.setRotationX(0);
                mViewNew.setTranslationY(0);
                mViewNew.setTranslationX(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewCover.setVisibility(View.GONE);
                mCubeAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void resetCube() {
        mEngineMoveReadyAnimator.cancel();
        mShowEngineMoveAnimator.cancel();
        mCubeAnimator.cancel();
        mHideCoverAnimator.cancel();
        mViewNew.setVisibility(View.VISIBLE);
        mViewNew.setScaleX(1);
        mViewNew.setScaleY(1);
        mViewNew.setRotationY(0);
        mViewNew.setRotationX(0);
        mViewNew.setTranslationY(0);
        mViewNew.setTranslationX(0);

        mViewOld.setVisibility(View.GONE);

        mViewCover.setVisibility(View.GONE);
    }

    void animateNewTurn() {
        mEngineMoveReady = false;
        mDelayedShowEngineMove = false;
        if (mViewCover.getVisibility() == View.VISIBLE) {
            mHideCoverAnimator.start();
        }else {
            mCubeAnimator.start();
        }
    }

    void onEngineMoveReady(Move m, Bitmap bitmap) {
        mEngineMove = m;
        mEngineMoveReady = true;
        mBitmap = bitmap;
    }

    private void animateEngineMoveReady() {
        if(mListener.doShowEngineMove()) {
            mEngineMoveReadyAnimator.start();
        }
    }

    void reshowEngineMoveReady(Move m, Bitmap bitmap) {
        mEngineMove = m;
        mEngineMoveReady = true;
        mBitmap = bitmap;

        mViewCover.setImageBitmap(bitmap);

        mViewCover.setVisibility(View.VISIBLE);
        mViewCover.setAlpha(1f);
        mViewCover.setScaleX(1);
        mViewCover.setScaleY(1);
        mViewCover.setRotationY(0);
        mViewCover.setRotationX(0);
        mViewCover.setTranslationY(0);
        mViewCover.setTranslationX(0);

        mViewNew.setVisibility(View.VISIBLE);
        mViewNew.setScaleX(1);
        mViewNew.setScaleY(1);
        mViewNew.setRotationY(0);
        mViewNew.setRotationX(0);
        mViewNew.setTranslationY(0);
        mViewNew.setTranslationX(0);

        mViewOld.setVisibility(View.GONE);
    }

    void animateShowEngineMove() {
        //mEngineMoveReadyAnimator.cancel();
        if (mEngineMoveReadyAnimator.isStarted()) {
            mDelayedShowEngineMove = true;
        } else {
            mShowEngineMoveAnimator.start();
        }
    }

    void reshowEngineMove(Move m, Bitmap bitmap) {
        mEngineMove = m;
        mEngineMoveReady = true;
        mBitmap = bitmap;

        mViewCover.setImageBitmap(bitmap);

        float size = mViewNew.getWidth() * 0.9f;
        // make sure we won't go away from the screen
        View v = (View) mViewCover.getParent();
        float sizeX = Math.min(size, mViewNew.getLeft());
        float sizeY = Math.min(size, v.getHeight() - mViewCover.getBottom());

        //Log.d("reshowEngineMove", "sizeY: " + sizeY);
        //Log.d("reshowEngineMove", "parent height: " + v.getHeight());
        //Log.d("reshowEngineMove", "bottom: " + mViewCover.getBottom());

        mViewNew.setVisibility(View.VISIBLE);
        mViewNew.setScaleX(1);
        mViewNew.setScaleY(1);
        mViewNew.setRotationY(0);
        mViewNew.setRotationX(0);
        mViewNew.setTranslationY(0);
        mViewNew.setTranslationX(0);

        mViewOld.setVisibility(View.GONE);

        mViewCover.setVisibility(View.VISIBLE);
        mViewCover.setAlpha(1f);
        mViewCover.setScaleX(1);
        mViewCover.setScaleY(1);
        mViewCover.setRotationY(0);
        mViewCover.setRotationX(0);
        mViewCover.setTranslationY(sizeY);
        mViewCover.setTranslationX(-sizeX);

        mSideNew.setValue(mEngineMove);

        mListener.onEngineMoveCoverOpen(mViewCover);
    }

    private class CubeSide {
        ImageView mView;
        Move mValue;

        CubeSide(ImageView v) {
            mView = v;
        }

        void setValue(Move value) {
            mValue = value;
            switch (value) {
                case ROCK:
                    mView.setImageDrawable(mContext.getResources().getDrawable( R.drawable.natural_rock));
                    mView.setBackground(mContext.getResources().getDrawable(R.drawable.background_rock));
                    break;
                case PAPER:
                    mView.setImageDrawable(mContext.getResources().getDrawable( R.drawable.natural_paper));
                    mView.setBackground(mContext.getResources().getDrawable(R.drawable.background_paper));
                    break;
                case SCISSORS:
                    mView.setImageDrawable(mContext.getResources().getDrawable( R.drawable.natural_scissors));
                    mView.setBackground(mContext.getResources().getDrawable(R.drawable.background_scissors));
            }
        }

        Move getValue() {
            return mValue;
        }
    }

    private class EngineMoveTurner implements ValueAnimator.AnimatorUpdateListener,
          ValueAnimator.AnimatorListener {
        private float mSize;
        private float reverseDistance;

        @Override
        public void onAnimationStart(Animator animation) {
            mSize = mViewNew.getWidth() / 2f;

            mViewNew.setVisibility(View.VISIBLE);
            mViewNew.setScaleX(1);
            mViewNew.setScaleY(1);
            mViewNew.setRotationY(0);
            mViewNew.setRotationX(0);
            mViewNew.setTranslationY(0);
            mViewNew.setTranslationX(0);

            mViewOld.setVisibility(View.GONE);

            mViewCover.setVisibility(View.VISIBLE);
            mViewCover.setAlpha(1f);
            mViewCover.setScaleX(1);
            mViewCover.setScaleY(1);
            mViewCover.setRotationY(0);
            mViewCover.setRotationX(0);
            mViewCover.setTranslationY(0);
            mViewCover.setTranslationX(0);

            mViewCover.setImageBitmap(mBitmap);

            int distance = 4000;
            float scale = mContext.getResources().getDisplayMetrics().density;
            mViewCover.setCameraDistance(distance * scale);
            mViewNew.setCameraDistance(distance * scale);
            reverseDistance =  6f / (distance * scale);

            mViewNew.setPivotY(mSize);
            mViewNew.setPivotX(0);
            mViewCover.setPivotY(mSize);
            mViewCover.setPivotX(mSize * 2);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mListener.onEngineMoveReadyAnimationEnd();
            if (mDelayedShowEngineMove) {
                mDelayedShowEngineMove = false;
                mShowEngineMoveAnimator.start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float phaseProg = (float) animation.getAnimatedValue();
            float angle = 90 * phaseProg;
            float scale = 2 * (phaseProg - 0.5f);
            scale = 1f - scale * scale;
            scale = scale * scale;
            final float scaleEquator = 1f + .5f *  mSize * reverseDistance * scale;
            final float scaleMedian = 1f + .2f * mSize * reverseDistance * scale;

            mViewNew.setRotationY(angle);
            mViewNew.setTranslationX(mSize * 2 * phaseProg);
            mViewNew.setScaleY(scaleEquator);
            mViewNew.setScaleX(scaleMedian);
            mViewCover.setRotationY(angle - 90);
            mViewCover.setTranslationX(mSize * 2 * (phaseProg - 1));
            mViewCover.setScaleY(scaleEquator);
            mViewCover.setScaleX(scaleMedian);
        }
    }

    private class EngineMoveShower implements ValueAnimator.AnimatorUpdateListener,
          ValueAnimator.AnimatorListener {
        private float mSizeX;
        private float mSizeY;

        @Override
        public void onAnimationStart(Animator animation) {
            float size = mViewNew.getWidth() * 0.9f;
            // make sure we won't go away from the screen
            View v = (View) mViewCover.getParent();
            mSizeX = Math.min(size, mViewCover.getLeft());
            mSizeY = Math.min(size, v.getHeight() - mViewCover.getBottom());

            mViewNew.setVisibility(View.VISIBLE);
            mViewNew.setScaleX(1);
            mViewNew.setScaleY(1);
            mViewNew.setRotationY(0);
            mViewNew.setRotationX(0);
            mViewNew.setTranslationY(0);
            mViewNew.setTranslationX(0);

            mViewOld.setVisibility(View.GONE);

            mViewCover.setVisibility(View.VISIBLE);
            mViewCover.setAlpha(1f);
            mViewCover.setScaleX(1);
            mViewCover.setScaleY(1);
            mViewCover.setRotationY(0);
            mViewCover.setRotationX(0);
            mViewCover.setTranslationY(0);
            mViewCover.setTranslationX(0);

            mSideNew.setValue(mEngineMove);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mListener.onEngineMoveCoverOpen(mViewCover);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float phaseProg = (float) animation.getAnimatedValue();
            mViewCover.setTranslationX(-mSizeX * phaseProg);
            mViewCover.setTranslationY(mSizeY * phaseProg);
        }
    }

    private class CubeTurner implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {
        private int mPhase;
        private float mSize;
        private float reverseDistance;
        private boolean mCancelled = false;

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            final float value = (float) animation.getAnimatedValue();
            float phaseProg;
            if (value <= 1) {
                if (mPhase != 0) {
                    setPhase(0);
                    mViewOld.setPivotY(0);
                    mViewOld.setPivotX(mSize);
                    mViewNew.setPivotY(mSize * 2);
                    mViewNew.setPivotX(mSize);
                }
                phaseProg = value;
            } else if (value <= 2) {
                if (mPhase != 1) {
                    setPhase(1);
                    mViewOld.setPivotY(mSize);
                    mViewOld.setPivotX(mSize * 2);
                    mViewNew.setPivotY(mSize);
                    mViewNew.setPivotX(0);
                }
                phaseProg = value - 1;
            } else if (value <= 3) {
                if (mPhase != 2) {
                    setPhase(2);
                    mViewOld.setPivotY(mSize * 2);
                    mViewOld.setPivotX(mSize);
                    mViewNew.setPivotY(0);
                    mViewNew.setPivotX(mSize);
                }
                phaseProg = value - 2;
            } else {
                if (mPhase != 3) {
                    setPhase(3);
                    mViewOld.setPivotY(mSize);
                    mViewOld.setPivotX(0);
                    mViewNew.setPivotY(mSize);
                    mViewNew.setPivotX(mSize * 2);
                }
                phaseProg = value - 3;
            }
            float angle = 90 * phaseProg;
            float scale = 2 * (phaseProg - 0.5f);
            scale = 1f - scale * scale;
            scale = scale * scale;
            final float scaleEquator = 1f + .5f *  mSize * reverseDistance * scale;
            final float scaleMedian = 1f + .2f * mSize * reverseDistance * scale;
            switch(mPhase) {
                case 0:
                    mViewOld.setRotationX(-angle);
                    mViewOld.setTranslationY(mSize * 2 * phaseProg);
                    mViewOld.setScaleX(scaleEquator);
                    mViewOld.setScaleY(scaleMedian);
                    mViewNew.setRotationX(90 - angle);
                    mViewNew.setTranslationY(mSize * 2 * (phaseProg - 1));
                    mViewNew.setScaleX(scaleEquator);
                    mViewNew.setScaleY(scaleMedian);
                    break;
                case 3:
                    mViewOld.setRotationY(angle);
                    mViewOld.setTranslationX(mSize * 2 * phaseProg);
                    mViewOld.setScaleY(scaleEquator);
                    mViewOld.setScaleX(scaleMedian);
                    mViewNew.setRotationY(angle - 90);
                    mViewNew.setTranslationX(mSize * 2 * (phaseProg - 1));
                    mViewNew.setScaleY(scaleEquator);
                    mViewNew.setScaleX(scaleMedian);
                    break;
                case 2:
                    mViewOld.setRotationX(angle);
                    mViewOld.setTranslationY(mSize * 2 * -phaseProg);
                    mViewOld.setScaleX(scaleEquator);
                    mViewOld.setScaleY(scaleMedian);
                    mViewNew.setRotationX(angle - 90);
                    mViewNew.setTranslationY(mSize * 2 * (1 - phaseProg));
                    mViewNew.setScaleX(scaleEquator);
                    mViewNew.setScaleY(scaleMedian);
                    break;
                case 1:
                    mViewOld.setRotationY(-angle);
                    mViewOld.setTranslationX(mSize * 2 * -phaseProg);
                    mViewOld.setScaleY(scaleEquator);
                    mViewOld.setScaleX(scaleMedian);
                    mViewNew.setRotationY(90 - angle);
                    mViewNew.setTranslationX(mSize * 2 * (1 - phaseProg));
                    mViewNew.setScaleY(scaleEquator);
                    mViewNew.setScaleX(scaleMedian);
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mCancelled = false;
            mPhase = -1;
            mViewNew.setVisibility(View.VISIBLE);
            mViewOld.setVisibility(View.VISIBLE);
            mSize = mViewNew.getWidth() / 2f;

            int distance = 4000;
            float scale = mContext.getResources().getDisplayMetrics().density;
            mViewOld.setCameraDistance(distance * scale);
            mViewNew.setCameraDistance(distance * scale);
            reverseDistance =  6f / (distance * scale);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(mCancelled) {
                return;
            }

            if (mEngineMoveReady) {
                animateEngineMoveReady();
            } else {
                mCubeAnimator.start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCancelled = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        private void setPhase(int phase) {
            mPhase = phase;
            // swapping new and old
            ImageView tmpV = mViewNew;
            mViewNew = mViewOld;
            mViewOld = tmpV;
            CubeSide tmpS = mSideOld;
            mSideOld = mSideNew;
            mSideNew = tmpS;
            mSideNew.setValue(mSideOld.getValue().next());
            mViewOld.setScaleX(1);
            mViewOld.setScaleY(1);
            mViewOld.setRotationY(0);
            mViewOld.setRotationX(0);
            mViewOld.setTranslationY(0);
            mViewOld.setTranslationX(0);
            mViewNew.setScaleX(1);
            mViewNew.setScaleY(1);
            mViewNew.setRotationY(0);
            mViewNew.setRotationX(0);
            mViewNew.setTranslationY(0);
            mViewNew.setTranslationX(0);
        }
    }

    interface Listener {
        void onEngineMoveReadyAnimationEnd();
        boolean doShowEngineMove();
        void onEngineMoveCoverOpen(View coverView);
    }
}
