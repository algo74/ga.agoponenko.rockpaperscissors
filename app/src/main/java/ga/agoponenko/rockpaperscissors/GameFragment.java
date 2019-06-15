package ga.agoponenko.rockpaperscissors;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GameFragment extends Fragment implements GameModel.MoveCallback {

    private TurningCube mTurningCube;

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        mTurningCube = new TurningCube((ImageView) v.findViewById(R.id.cubeView1),
                                       (ImageView) v.findViewById(R.id.cubeView2),
                                       getActivity());



        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCube();
            }
        });

        return v;
    }

    private void animateCube() {
        mTurningCube.startAnimation();
    }

    @Override
    public void onEngineMoveReady(GameModel.Move m) {

    }


}
