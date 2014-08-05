import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

interface Player {
    public PlayerMove makeMove(ConnectFourGame game);
}

abstract class PrintablePlayer implements Player {
    protected String representation;

    public PrintablePlayer(char representation) {
        this.representation = String.valueOf(representation);
    }

    @Override
    public String toString() {
        return representation;
    }
}

class HumanPlayer extends PrintablePlayer {
    private String input;
    private final BufferedReader inputStream;

    public HumanPlayer() {
        this('X');
    }

    public HumanPlayer(char representation) {
        super(representation);
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    public PlayerMove makeMove(ConnectFourGame game) {
        PlayerMove playerMove = null;

        while (true) {
            System.out.print("Select column [1-" + game.getBoard().NUMBER_OF_COLUMNS + "]: ");

            try {
                input = inputStream.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Failed to read your move! Please try again.");
                continue;
            }

            try {
                int columnIndex = Integer.parseInt(input);
                playerMove = new PlayerMove(this, columnIndex);
            } catch (NumberFormatException e) {
                System.out.println("Invalid column! Please try again.");
                continue;
            }

            if (!game.isValidMove(playerMove)) {
                System.out.println("Invalid move! Please try again.");
                continue;
            }

            break;
        }

        return playerMove;
    }
}

class MonteCarloPlayer implements Player {
    public PlayerMove makeMove(ConnectFourGame game) {
        return null;
    }
}

class MinMaxPlayer implements Player {
    public PlayerMove makeMove(ConnectFourGame game) {
        return null;
    }
}

class MinMaxAlphaBetaPlayer implements Player {
    public PlayerMove makeMove(ConnectFourGame game) {
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
    public final static int NUMBER_OF_ROWS = 6;
    public final static int NUMBER_OF_COLUMNS = 7;

    private ArrayList<Player> board;

    public Board() {
        board = new ArrayList<Player>(NUMBER_OF_ROWS * NUMBER_OF_COLUMNS);
        for (int i = 0; i < NUMBER_OF_ROWS * NUMBER_OF_COLUMNS; i++) {
            board.add(null);
        }
    }

    public Player at(int row, int col) {
        return board.get(row * NUMBER_OF_ROWS + col);
    }

    @Override
    public String toString() {
        Player player = null;
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                player = this.at(row, col);
                
                if (player == null) {
                    sb.append('.');
                } else {
                    sb.append(player);
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}

class ConnectFourGame {
    private Board board;
    private List<PlayerMove> moves;

    public ConnectFourGame() {
        board = new Board();
        moves = new ArrayList<PlayerMove>();
    }

    public ConnectFourGame(Board board, List<PlayerMove> moves) {
        // TODO: deep copy
    }

    public Board getBoard() {
        return board;
    }

    public boolean isValidMove(PlayerMove playerMove) {
        return true; // TODO: add actual validation
    }
}

class ConnectFour {
    public static void main(String[] args) {
        ConnectFourGame game = new ConnectFourGame();
        System.out.println(game.getBoard());
    }
}
