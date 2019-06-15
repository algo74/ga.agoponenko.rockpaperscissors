package ga.agoponenko.rockpaperscissors;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GameFragment extends Fragment implements GameModel.MoveCallback {

    private ValueAnimator mCubeAnimator;
    private ImageView mSideTL;
    private ImageView mSideBL;
    private ImageView mSideTR;
    private ImageView mSideBR;

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        mSideBL = v.findViewById(R.id.textViewBL);
        mSideBR = v.findViewById(R.id.textViewBR);
        mSideTL = v.findViewById(R.id.textViewTL);
        mSideTR = v.findViewById(R.id.textViewTR);

        mCubeAnimator = ValueAnimator.ofFloat(0f, 4f);
        CubeTurner cubeTurner = new CubeTurner();
        mCubeAnimator.addUpdateListener(cubeTurner);
        mCubeAnimator.addListener(cubeTurner);
        mCubeAnimator.setDuration(4000);

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCube();
            }
        });

        return v;
    }

    private void animateCube() {
        mCubeAnimator.start();
    }

    @Override
    public void onEngineMoveReady(GameModel.Move m) {

    }

    private class CubeTurner implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {

        int mPhase = 0;

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            final float value = animation.getAnimatedFraction();
            float phaseProg;
            if (value <= 1) {
                if (mPhase != 0) {
                    Log.d("animation", "Phase 0");
                    setPhase(0);
                    mSideBL.setVisibility(View.VISIBLE);
                    mSideBL.setImageDrawable(getResources().getDrawable( R.drawable.natural_rock));
                    mSideBL.setBackgroundColor(getResources().getColor(R.color.colorRockCubeBg));
                    mSideBL.setScaleX(1);
                    mSideBR.setVisibility(View.GONE);
                    mSideTR.setVisibility(View.GONE);
                    mSideTL.setVisibility(View.VISIBLE);
                    mSideTL.setImageDrawable(getResources().getDrawable( R.drawable.natural_scissors));
                    mSideTL.setBackgroundColor(getResources().getColor(R.color.colorScissorsCubeBg));
                    mSideTL.setScaleX(1);
                }
                phaseProg = value;
                mSideBL.setRotationX(-90 * phaseProg);
                mSideTL.setRotationX(90 * (1 - phaseProg));

            } else if (value <= 2) {
                if (mPhase != 1) {
                    setPhase(1);
                }
                phaseProg = value - 1;
            } else if (value <= 3) {
                if (mPhase != 2) {
                    setPhase(2);
                }
                phaseProg = value - 2;
            } else {
                if (mPhase != 3) {
                    setPhase(3);
                }
                phaseProg = value - 3;
            }


        }


        @Override
        public void onAnimationStart(Animator animation) {
            mPhase = -1;
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
        }
    }
}
