package ga.agoponenko.rockpaperscissors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;

public class PlayersFragment extends Fragment {
    public static final int RESULT_NOT_CHANGED = 0;
    public static final int RESULT_PLAYER_SWITCHED = 1111;
    private static final int REQUEST_EDIT = 0;
    private static final String DIALOG_EDIT = "DialogEdit";


    private RecyclerView mRecyclerView;
    private PlayersAdapter mAdapter;
    private GameModel mGameModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameModel = GameModel.getInstance(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_players, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_player:
                String id = mGameModel.newPlayer().getId();
                showEditPlayerDialog(id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);
        mRecyclerView = view.findViewById(R.id.players_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    private void updateUI() {
       List<Player> players = mGameModel.getPlayers();
        if (players.size() == 0) {
            // TODO
        }
        if (mAdapter == null) {
            mAdapter = new PlayersAdapter(players);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPlayers(players);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showEditPlayerDialog(String id) {
        FragmentManager manager = getFragmentManager();
        EditPlayerFragment dialog = EditPlayerFragment.newInstance(id);
        dialog.setTargetFragment(PlayersFragment.this, REQUEST_EDIT);
        dialog.show(manager, DIALOG_EDIT);
    }

    private void choosePlayer(String mId) {
        boolean res = mGameModel.choosePlayer(mId);
        int result = res ? RESULT_PLAYER_SWITCHED : RESULT_NOT_CHANGED;
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, result);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private class PlayersAdapter extends RecyclerView.Adapter<PlayerHolder> {

        private List<Player> mPlayers;

        public PlayersAdapter(List<Player> players) {
            mPlayers = players;
        }

        public void setPlayers(List<Player> players) {
            mPlayers = players;
        }

        @NonNull
        @Override
        public PlayerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PlayerHolder(LayoutInflater.from(getActivity()), viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerHolder playerHolder, int i) {
            playerHolder.bind(i, mPlayers.get(i));
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }
    }

    private class PlayerHolder extends RecyclerView.ViewHolder {

        private TextView mNameView;
        private TextView mScoreView;
        private String mId;

        public PlayerHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_player, parent, false));
            mNameView = itemView.findViewById(R.id.nameView);
            mScoreView = itemView.findViewById(R.id.scoreLabel);
            itemView.findViewById(R.id.bEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditPlayerDialog(mId);
                }
            });
            itemView.findViewById(R.id.bSelect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choosePlayer(mId);
                }
            });
        }

        public void bind(int i, Player player) {
            mId = player.getId();
            int playerScore = player.getPlayerScore();
            int percent = playerScore == 0 ? 0 :
                  playerScore * 100 / (player.getEngineScore() + playerScore);
            mNameView.setText(player.getName());
            mScoreView. setText(playerScore + " wins (" + percent + "%)");
        }
    }
}
