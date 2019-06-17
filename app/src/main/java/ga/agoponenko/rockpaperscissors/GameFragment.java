package ga.agoponenko.rockpaperscissors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameFragment extends Fragment implements GameModel.MoveCallback, TurningCube.Listener {
    private static final int COUNT = 3;

    private TurningCube mTurningCube;
    private GameModel mGameModel = GameModel.getInstance();
    private Button mTurnButton;
    private CountDownAnimation mCountDownAnimation;
    private View mChoicesView;
    private ImageView mPlayerResultView;
    private ImageView mEngineResultView;
    private Animation mShowResultAnimation;
    private Animation mHideResultAnimation;
    private TextView mEngineScoreView;
    private TextView mPlayerScoreView;

    private int mPlayerScore = 0;
    private int mEngineScore = 0;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        mTurningCube = new TurningCube((ImageView) view.findViewById(R.id.cubeView1),
                                       (ImageView) view.findViewById(R.id.cubeView2),
                                       (ImageView) view.findViewById(R.id.cubeViewCover),
                                       getActivity(),
                                       this);

        mTurnButton = view.findViewById(R.id.bNewTurn);
        mTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTurnButton.setVisibility(View.INVISIBLE);
                mPlayerResultView.startAnimation(mHideResultAnimation);
                mEngineResultView.startAnimation(mHideResultAnimation);
                animateCube();
                mGameModel.prepareEngineMove(GameFragment.this);
            }
        });

        mChoicesView = view.findViewById(R.id.viewChoices);
        view.findViewById(R.id.bRock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePlayerMove(GameModel.Move.ROCK);
            }
        });
        view.findViewById(R.id.bPaper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePlayerMove(GameModel.Move.PAPER);
            }
        });
        view.findViewById(R.id.bScissors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePlayerMove(GameModel.Move.SCISSORS);
            }
        });

         mCountDownAnimation =
               new CountDownAnimation((TextView) view.findViewById(R.id.textViewCountDown),
                                                      COUNT);
         mCountDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
             @Override
             public void onCountDownEnd(CountDownAnimation animation) {
                 Log.d("CountDown", "Timed out");
                 mGameModel.onPlayerNotMoved();
                 mPlayerResultView.setImageDrawable(getActivity().getResources()
                                                                 .getDrawable(R.drawable.ic_loss));
                 mEngineResultView.setImageDrawable(getActivity().getResources()
                                                                 .getDrawable(R.drawable.ic_win));
                 mPlayerScore++;
                 mPlayerScoreView.setText(""+ mPlayerScore);
                 commonResultActions();
             }
         });

        mShowResultAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowResultAnimation.setFillAfter(true);
        mShowResultAnimation.setDuration(500);
        mHideResultAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideResultAnimation.setFillAfter(true);
        mHideResultAnimation.setDuration(300);

        mPlayerResultView = view.findViewById(R.id.playerResultView);
        mEngineResultView = view.findViewById(R.id.engineResultView);

        mEngineScoreView = view.findViewById(R.id.engineScoreView);
        mEngineScoreView.setText("" + mEngineScore);
        mPlayerScoreView = view.findViewById(R.id.playerScoreView);
        mPlayerScoreView.setText("" + mPlayerScore);

        return view;
    }

    private void makePlayerMove(GameModel.Move m) {
        mCountDownAnimation.cancel();
        GameModel.Result result = mGameModel.onPlayerMove(m);
        // show result
        switch (result) {
            case WIN:
                mPlayerResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_win));
                mEngineResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_loss));
                mPlayerScore++;
                mPlayerScoreView.setText(""+ mPlayerScore);
                break;
            case LOSS:
                mPlayerResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_loss));
                mEngineResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_win));
                mEngineScore++;
                mEngineScoreView.setText(""+ mEngineScore);
                break;
            case DRAW:
                mPlayerResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_draw));
                mEngineResultView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_draw));
                break;
        }
        commonResultActions();
        mTurningCube.animateShowEngineMove();
    }

    private void commonResultActions() {
        mChoicesView.setVisibility(View.GONE);
        mPlayerResultView.startAnimation(mShowResultAnimation);
        mEngineResultView.startAnimation(mShowResultAnimation);
        mTurnButton.setVisibility(View.VISIBLE);
    }

    private void animateCube() {
        mTurnButton.setVisibility(View.GONE);
        mTurningCube.startAnimation();
    }

    @Override
    public void onEngineMoveReady(GameModel.Move m) {
        mTurningCube.onEngineMoveReady(m);
    }


    @Override
    public void onEngineMoveReadyAnimationEnd() {
        mChoicesView.setVisibility(View.VISIBLE);
        mCountDownAnimation.start();
    }
}
