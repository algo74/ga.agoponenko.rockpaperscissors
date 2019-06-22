package ga.agoponenko.rockpaperscissors;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;


public class GameFragment extends Fragment implements GameModel.MoveCallback, TurningCube.Listener {
    private static final String SAVED_STATE = "mState";
    private static final String SAVED_RESULT = "mResult";
    private static final String SAVED_PLAYER_MOVE = "mPlayerMove";
    private static final String SAVED_ENGINE_MOVE = "mEngineMove";
    private static final int REQUEST_CHANGE_PLAYER = 0;
    private static final int COUNT = 4;

    private TurningCube mTurningCube;
    private GameModel mGameModel;
    private Button mTurnButton;
    private StickyCountDownAnimation mCountDownAnimation;
    private View mChoicesView;
    private ImageView mPlayerResultView;
    private ImageView mEngineResultView;
    private Animation mShowResultAnimation;
    private Animation mHideResultAnimation;
    private Animation mShowPlayerMoveAnimation;
    private Animation mHidePlayerMoveAnimation;
    private Animation mPlayerMessageAnimation;
    private TextSwitcher mEngineScoreView;
    private TextSwitcher mPlayerScoreView;
    private ImageView mPlayerMoveView;
    private TextView mPlayerNameView;
    private TextView mPlayerMessageView;

    /*
     * 0 - awaiting new turn
     * 1 - spinning cube
     * 2 - engine move shown
     * 3 - showing results (and awaiting new turn)
     */
    private int mState;
    private GameModel.Move mEngineMove = null;
    private GameModel.Move mPlayerMove = null;
    private GameModel.Result mResult;
    private boolean mIsStopped;

    private int mPlayerScore = 0;
    private int mEngineScore = 0;
    private Resources mResources;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mGameModel = GameModel.getInstance(getActivity());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE, mState);
        outState.putInt(SAVED_RESULT, mResult == null ? -1 : mResult.ordinal());
        outState.putInt(SAVED_ENGINE_MOVE, mEngineMove == null ? -1 : mEngineMove.ordinal());
        outState.putInt(SAVED_PLAYER_MOVE, mPlayerMove == null ? -1 : mPlayerMove.ordinal());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_game, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_player:
                Intent intent = new Intent(getActivity(), PlayersActivity.class);
                startActivityForResult(intent, REQUEST_CHANGE_PLAYER);
                return true;
            case R.id.reset_score:
                GameModel.Player player = mGameModel.getCurrentPlayer();
                player.setPlayerScore(0);
                player.setEngineScore(0);
                mGameModel.updatePlayer(player);
                resetGame();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CHANGE_PLAYER) {
            int result = data.getIntExtra(Intent.EXTRA_RETURN_RESULT, PlayersFragment.RESULT_NOT_CHANGED);
            if (result == PlayersFragment.RESULT_PLAYER_SWITCHED) {
                resetGame();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        mResources = getActivity().getResources();

        mTurningCube = new TurningCube((ImageView) view.findViewById(R.id.cubeView1),
                                       (ImageView) view.findViewById(R.id.cubeView2),
                                       (ImageView) view.findViewById(R.id.cubeViewCover),
                                       getActivity(),
                                       this);

        mTurnButton = view.findViewById(R.id.bNewTurn);
        mTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewTurn();
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
        mPlayerMessageView = view.findViewById(R.id.textViewCountDown);
        mPlayerMessageAnimation = new AlphaAnimation(0.0f, 1.0f);
        mPlayerMessageAnimation.setDuration(600);
        mPlayerMessageAnimation.setFillAfter(true);
        mPlayerMessageAnimation.setRepeatCount(Animation.INFINITE);
        mPlayerMessageAnimation.setRepeatMode(Animation.REVERSE);
        mCountDownAnimation = StickyCountDownAnimation.steal(
              mPlayerMessageView,
              COUNT,
              new StickyCountDownAnimation.CountDownListener() {
                  @Override
                  public void onCountDownEnd(StickyCountDownAnimation animation) {
                      if (!mIsStopped) {
                          onPlayerTimedOut();
                      }
                  }

                  @Override
                  public void onCountDownStolen(
                        StickyCountDownAnimation animation) {
                  }
              });

        mPlayerResultView = view.findViewById(R.id.playerResultView);
        mEngineResultView = view.findViewById(R.id.engineResultView);

        mShowResultAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowResultAnimation.setFillAfter(true);
        mShowResultAnimation.setDuration(700);
        mHideResultAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideResultAnimation.setFillAfter(true);
        mHideResultAnimation.setDuration(300);

        Animation textAnimationIn = AnimationUtils.loadAnimation(getActivity(),
                                                                 android.R.anim.slide_in_left);
        textAnimationIn.setDuration(800);

        Animation textAnimationOut = AnimationUtils.loadAnimation(getActivity(),
                                                                  android.R.anim.slide_out_right);
        textAnimationOut.setDuration(800);
        mEngineScoreView = view.findViewById(R.id.engineScoreView);

        mEngineScoreView.setInAnimation(textAnimationIn);
        mEngineScoreView.setOutAnimation(textAnimationOut);
        mPlayerScoreView = view.findViewById(R.id.playerScoreView);
        mPlayerScoreView.setInAnimation(textAnimationIn);
        mPlayerScoreView.setOutAnimation(textAnimationOut);
        mEngineScoreView.setInAnimation(textAnimationIn);
        mEngineScoreView.setOutAnimation(textAnimationOut);
        mPlayerNameView = view.findViewById(R.id.playerNameView);
        mPlayerMoveView = view.findViewById(R.id.playerMoveView);
        mShowPlayerMoveAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowPlayerMoveAnimation.setFillAfter(true);
        mShowPlayerMoveAnimation.setDuration(500);
        mShowPlayerMoveAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mTurnButton.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mHidePlayerMoveAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHidePlayerMoveAnimation.setFillAfter(true);
        mHidePlayerMoveAnimation.setDuration(300);

        // restoring state
        if (savedInstanceState != null) {
            mState = savedInstanceState.getInt(SAVED_STATE, 0);
            mEngineMove = GameModel.Move.fromInt(savedInstanceState.getInt(SAVED_ENGINE_MOVE, -1));
            mPlayerMove = GameModel.Move.fromInt(savedInstanceState.getInt(SAVED_PLAYER_MOVE, -1));
            mResult = GameModel.Result.fromInt(savedInstanceState.getInt(SAVED_RESULT, -1));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mIsStopped = false;

        loadScores();

        final View firstCubeSide = getView().findViewById(R.id.cubeView1); // needed in switch twice
        switch (mState) {
            case 0: // initial
                // nothing to do here
                break;
            case 1: // spinning cube
                if (firstCubeSide.getWidth() > 0) {
                    startNewTurn();
                } else {
                    firstCubeSide.getViewTreeObserver().addOnGlobalLayoutListener(
                          new ViewTreeObserver.OnGlobalLayoutListener() {
                              @Override
                              public void onGlobalLayout() {
                                  if (firstCubeSide.getWidth() > 0) {
                                      firstCubeSide.getViewTreeObserver()
                                                   .removeOnGlobalLayoutListener(this);
                                      startNewTurn();
                                  }
                              }
                          });
                }
                break;
            case 2:
                // engine move shown
                // restore engine move
                mTurningCube.reshowEngineMoveReady(mEngineMove);

                if (mCountDownAnimation.isRunning()) {
                    // let the count down finish
                    mChoicesView.setVisibility(View.VISIBLE);
                    mTurnButton.setVisibility(View.INVISIBLE);
                } else { // player timed out
                    // show result
                    onPlayerTimedOut();
                }
                break;
            case 3: // result shown


                mTurnButton.setVisibility(View.INVISIBLE);

                if (mPlayerMove == null) {
                    mTurningCube.reshowEngineMoveReady(mEngineMove);
                } else {
                    if (firstCubeSide.getWidth() > 0) {
                        mTurningCube.reshowEngineMove(mEngineMove);
                    } else {
                        firstCubeSide.getViewTreeObserver().addOnGlobalLayoutListener(
                              new ViewTreeObserver.OnGlobalLayoutListener() {
                                  @Override
                                  public void onGlobalLayout() {
                                      if (firstCubeSide.getWidth() > 0) {
                                          firstCubeSide.getViewTreeObserver()
                                                       .removeOnGlobalLayoutListener(this);
                                          mTurningCube.reshowEngineMove(mEngineMove);
                                      }
                                  }
                              });
                    }

                }

                showResult(mPlayerMove, mResult);
        }

    }

    @Override
    public void onStop() {
        mIsStopped = true;
        super.onStop();
    }

    private void loadScores() {
        GameModel.Player currentPlayer = mGameModel.getCurrentPlayer();
        mPlayerScore = currentPlayer.getPlayerScore();
        mEngineScore = currentPlayer.getEngineScore();
        mEngineScoreView.setCurrentText("" + mEngineScore);
        mPlayerScoreView.setCurrentText("" + mPlayerScore);
        mPlayerNameView.setText(currentPlayer.getName());

    }
    private void resetGame() {
        // reset countdown
        mCountDownAnimation.cancel();
        mState = 0;
        // reset cube
        mTurningCube.resetCube();
        // reset buttons
        mTurnButton.setVisibility(View.VISIBLE);
        mChoicesView.setVisibility(View.INVISIBLE);
        // clear results and player move
        mPlayerMoveView.setImageDrawable(null);
        mPlayerResultView.setImageDrawable(null);
        mEngineResultView.setImageDrawable(null);

        loadScores();
    }

    private void startNewTurn() {
        mState = 1;
        mEngineMove = null;
        mPlayerMove = null;
        mTurnButton.setVisibility(View.INVISIBLE);
        mPlayerResultView.startAnimation(mHideResultAnimation);
        mEngineResultView.startAnimation(mHideResultAnimation);
        mPlayerMoveView.startAnimation(mHidePlayerMoveAnimation);
        // show player side message
        mPlayerMessageView.setText("get set");
        mPlayerMessageView.startAnimation(mPlayerMessageAnimation);
        mPlayerMessageView.setVisibility(View.VISIBLE);
        // spin the cube
        mTurningCube.animateNewTurn();
        mGameModel.prepareEngineMove(GameFragment.this);
    }

    @Override
    public boolean doShowEngineMove() {
        if(mIsStopped) {
            return false;
        }
        mState = 2;
        mGameModel.onEngineMoveShown();
        mChoicesView.setVisibility(View.VISIBLE);
        // cancel player side message
        mPlayerMessageView.setText("");
        mPlayerMessageAnimation.cancel();
        // start countdown
        mCountDownAnimation.start();
        return true;
    }

    private void makePlayerMove(GameModel.Move m) {
        if(mState != 2) {
            Log.w("Player moved", "state was " + mState);
            return;
        }
        mCountDownAnimation.cancel();
        GameModel.Result result = mGameModel.onPlayerMove(m);
        commonResultActions(m, result);
        mTurningCube.animateShowEngineMove();
    }

    private void onPlayerTimedOut() {
        if(mState != 2) {
            Log.w("Player not moved", "state was " + mState);
            return;
        }
        commonResultActions(null, GameModel.Result.LOSS);
        mGameModel.onPlayerNotMoved();
    }

    private void commonResultActions(GameModel.Move m, GameModel.Result result) {
        mState = 3;
        mResult = result;
        mPlayerMove = m;
        if (result == GameModel.Result.WIN) {
            mPlayerScore++;
            mPlayerScoreView.setText(""+ mPlayerScore);
        } else if (result == GameModel.Result.LOSS) {
            mEngineScore++;
            mEngineScoreView.setText(""+ mEngineScore);
        }
        showResult(m,result);
    }

    private void showResult(GameModel.Move m, GameModel.Result result) {
        mChoicesView.setVisibility(View.GONE);
        int drawable;
        if (m == null) {
            drawable =  R.drawable.hand_x;
        } else {
            switch (m) {
                case SCISSORS:
                    drawable = R.drawable.hand_scissors;
                    break;
                case PAPER:
                    drawable = R.drawable.hand_paper;
                    break;
                default:
                    drawable = R.drawable.hand_rock;
                    break;
            }
        }
        mPlayerMoveView.setImageDrawable(mResources.getDrawable(drawable));
        mPlayerMoveView.startAnimation(mShowPlayerMoveAnimation);
                                        // ^ will call mTurnButton.setVisibility(View.VISIBLE)
                                        //   when done
        switch (result) {
            case WIN:
                mPlayerResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_win));
                mEngineResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_loss));
                break;
            case LOSS:
                mPlayerResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_loss));
                mEngineResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_win));
                break;
            case DRAW:
                mPlayerResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_draw));
                mEngineResultView.setImageDrawable(mResources.getDrawable(R.drawable.ic_draw));
                break;
        }

        mPlayerResultView.startAnimation(mShowResultAnimation);
        mEngineResultView.startAnimation(mShowResultAnimation);
    }


    @Override
    public void onEngineMoveReady(GameModel.Move m) {
        mEngineMove = m;
        mTurningCube.onEngineMoveReady(m);
    }

    @Override
    public void onEngineMoveReadyAnimationEnd() {
    }
}
