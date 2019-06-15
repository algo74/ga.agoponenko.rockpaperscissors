package ga.agoponenko.rockpaperscissors;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class TurningCube {
    private ImageView mViewNew;
    private ImageView mViewOld;
    private CubeSide mSideNew;
    private CubeSide mSideOld;
    private Context mContext;
    private ValueAnimator mCubeAnimator;

    TurningCube(ImageView view1, ImageView view2, Context context) {
        mViewNew = view1;
        mViewOld = view2;
        mContext = context;
        mSideNew = new CubeSide(view1);
        mSideOld = new CubeSide(view2);
        mSideNew.setValue(GameModel.Move.ROCK);

        mCubeAnimator = ValueAnimator.ofFloat(0f, 4f);
        CubeTurner cubeTurner = new CubeTurner();
        mCubeAnimator.addUpdateListener(cubeTurner);
        mCubeAnimator.addListener(cubeTurner);
        mCubeAnimator.setInterpolator(new LinearInterpolator());
        mCubeAnimator.setDuration(6000);
        mCubeAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    void startAnimation() {
        mCubeAnimator.start();
    }

    private class CubeSide {
        ImageView mView;
        GameModel.Move mValue;

        CubeSide(ImageView v) {
            mView = v;
        }

        void setValue(GameModel.Move value) {
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

        GameModel.Move getValue() {
            return mValue;
        }
    }

    private class CubeTurner implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {

        private int mPhase;
        private float mSize;
        private float reverseDistance;


        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            final float value = (float) animation.getAnimatedValue();
            float phaseProg;
            if (value <= 1) {
                if (mPhase != 0) {
                    Log.d("animation", "Phase 0");
                    setPhase(0);
                    mViewOld.setPivotY(0);
                    mViewOld.setPivotX(mSize);
                    mViewNew.setPivotY(mSize * 2);
                    mViewNew.setPivotX(mSize);
                }
                phaseProg = value;

            } else if (value <= 2) {
                if (mPhase != 1) {
                    Log.d("animation", "Phase 1");
                    setPhase(1);
                    mViewOld.setPivotY(mSize);
                    mViewOld.setPivotX(mSize * 2);
                    mViewNew.setPivotY(mSize);
                    mViewNew.setPivotX(0);
                }
                phaseProg = value - 1;
            } else if (value <= 3) {
                if (mPhase != 2) {
                    Log.d("animation", "Phase 2");
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
            scale = (1f - scale * scale);
            float scaleEquator = 1f + .5f *  mSize * reverseDistance * scale * scale;
            float scaleMedian = 1f + .2f * mSize * reverseDistance * scale * scale;
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
            mPhase = -1;
            mViewNew.setVisibility(View.VISIBLE);
            mViewOld.setVisibility(View.VISIBLE);
            mSize = mViewNew.getWidth() / 2f;

            int distance = 4000;
            float scale = mContext.getResources().getDisplayMetrics().density;
            mViewOld.setCameraDistance(distance * scale);
            mViewNew.setCameraDistance(distance * scale);
            reverseDistance =  6f / (distance * scale);
            Log.d("Animation", "Scale coeff: " + reverseDistance);
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

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
            CubeSide tmpS = mSideNew;
            mSideNew = mSideOld;
            mSideOld = tmpS;
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
}
