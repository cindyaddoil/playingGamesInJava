import java.util.List;

interface Player {
    public PlayerMove makeMove(Board board);
}

class HumanPlayer implements Player {
    public PlayerMove makeMove(Board board) {
        return null;
    }
}

class MonteCarloPlayer implements Player {
    public PlayerMove makeMove(Board board) {
        return null;
    }
}

class MinMaxPlayer implements Player {
    public PlayerMove makeMove(Board board) {
        return null;
    }
}

class MinMaxAlphaBetaPlayer implements Player {
    public PlayerMove makeMove(Board board) {
        return null;
    }
}

class PlayerMove {
    private final Player player;
    private final int columnIndex;

    public PlayerMove(Player player, int columnIndex) {
        this.player = player;
        this.columnIndex = columnIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public Player getPlayer() {
        return player;
    }
}

class Board {
    private final static int NUMBER_OF_ROWS = 6;
    private final static int NUMBER_OF_COLUMNS = 7;
}

class ConnectFourState {
    private Board board;
    private List<PlayerMove> moves;
}

class ConnectFour {
    public static void main(String[] args) {
        System.out.println("HI");
    }
}
