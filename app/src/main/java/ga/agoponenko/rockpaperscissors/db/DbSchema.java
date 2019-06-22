package ga.agoponenko.rockpaperscissors.db;


public class DbSchema {
    public static final class PlayerTable {
        public static final String NAME = "players";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String NAME = "name";
            public static final String PLAYER_SCORE = "player_score";
            public static final String ENGINE_SCORE = "engine_score";
        }

        public static final class Indexes {
            public static final String NAME_INDEX = "players_name_index";
        }
    }

    public static final class PlayerHistoryTable {
        public static final String NAME = "player_history";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String PLAYER_ID = "player_id";
            public static final String LAST_MOVE = "last_move";
            public static final String UP_DOWN_HISTORY = "up_down";
            public static final String WIN_LOSS_HISTORY = "win_loss";
        }

        public static final class Indexes {
            public static final String PLAYER_INDEX = "ph_player_index";
        }
    }

    public static final class HistoryTable {
        public static final String NAME = "history";

        public static final class Cols {
            public static final String PLAYER_ID = "player_id";
            public static final String KEY = "history_key";
            public static final String UP = "up";
            public static final String DOWN = "down";
            public static final String SAME = "same";
        }

        public static final class Indexes {
            public static final String PLAYER_INDEX = "history_player_index";
            public static final String KEY_INDEX ="history_key_index";
        }
    }
}
