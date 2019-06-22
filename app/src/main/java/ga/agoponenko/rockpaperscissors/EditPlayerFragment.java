package ga.agoponenko.rockpaperscissors;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class EditPlayerFragment extends DialogFragment {
    private static final String PLAYER_INDEX = "playerIndex";
    private static final String PLAYER_ID = "playerId";

    private GameModel.Player mPlayer;
    private GameModel mModel;
    private EditText mNameView;
    private CheckBox mCheckbox;
    private TextView mScoreView;


    public EditPlayerFragment() {
        // Required empty public constructor
    }

    public static EditPlayerFragment newInstance( String playerId) {
        EditPlayerFragment fragment = new EditPlayerFragment();
        Bundle args = new Bundle();
       args.putString(PLAYER_ID, playerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
           String id = getArguments().getString(PLAYER_ID);
            mModel = GameModel.getInstance(getActivity());
            mPlayer = mModel.getPlayer(id);
        }
        View v = LayoutInflater.from(getActivity())
                               .inflate(R.layout.fragment_edit_player, null);
        mNameView = v.findViewById(R.id.editText);
        mNameView.setText(mPlayer.getName());
        mScoreView = v.findViewById(R.id.scoreView);
        mScoreView.setText(mPlayer.getEngineScore() + " " +
                                 getActivity().getResources().getString(R.string.slash) +
                                 " " + mPlayer.getPlayerScore());
        mCheckbox = v.findViewById(R.id.checkBox);
        return new AlertDialog.Builder(getActivity())
              .setView(v)
              //.setTitle(R.string.player_edit_title)
              .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      mModel.deletePlayer(mPlayer.getId());
                      sendResult(Activity.RESULT_CANCELED);
                  }
              })
              .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      sendResult(Activity.RESULT_CANCELED);
                  }
              })
              .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      mPlayer.setName(mNameView.getText().toString());
                      if (mCheckbox.isChecked()) {
                          mPlayer.setEngineScore(0);
                          mPlayer.setPlayerScore(0);
                      }
                      mModel.updatePlayer(mPlayer);
                      sendResult(Activity.RESULT_OK);
                  }
              })
              .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return; }
        Intent intent = new Intent();
        getTargetFragment()
              .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
