import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

interface Player {
    public PlayerMove makeMove(final ConnectFourGame game);
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

class RandomPlayer extends PrintablePlayer {
    private final Random rng;

    public RandomPlayer() {
        this('O');
    }

    public RandomPlayer(char representation) {
        super(representation);
        rng = new Random();
    }

    public PlayerMove makeMove(ConnectFourGame game) {
        List<PlayerMove> possibleMoves = game.getPossibleMoves(this);
        return possibleMoves.get(rng.nextInt(possibleMoves.size()));
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

    private final static int directions[][] = {{0, 1}, {1, 1}, {1, 0}, {1, -1}};

    private ArrayList<Player> board;

    public Board() {
        board = new ArrayList<Player>(NUMBER_OF_ROWS * NUMBER_OF_COLUMNS);
        for (int i = 0; i < NUMBER_OF_ROWS * NUMBER_OF_COLUMNS; i++) {
            board.add(null);
        }
    }

    private int boardIndex(int row, int col) {
        return row * NUMBER_OF_ROWS + col;
    }

    public Player at(int row, int col) {
        return board.get(boardIndex(row, col));
    }

    private void set(int row, int col, Player player) {
        board.set(boardIndex(row, col), player);
    }

    public void makeMove(PlayerMove playerMove) {
        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            if (this.at(row, playerMove.getColumnIndex()) != null) continue;
            
            set(row, playerMove.getColumnIndex(), playerMove.getPlayer());
        }
    }

    public List<Integer> getNonFullColumns() {
        int row = NUMBER_OF_ROWS - 1;
        List<Integer> nonFullColumns = new ArrayList<Integer>();

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (this.at(row, col) == null) {
                nonFullColumns.add(col);
            }
        }

        return nonFullColumns;
    }

    public boolean isFull() {
        int row = NUMBER_OF_ROWS - 1;

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (this.at(row, col) == null) {
                return false;
            }
        }

        return true;
    }

    public boolean hasFourInARow() {
        Player player = null;

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                player = this.at(row, col);

                if (player == null) continue;

                for (int[] direction : directions) {
                    int rowDiff = direction[0];
                    int colDiff = direction[1];

                    boolean fourInARow = true;

                    for (int i = 1; i <= 3; i++) {
                        int nextRow = row + rowDiff * i;
                        int nextCol = col + rowDiff * i;

                        if (nextRow < 0 || nextRow >= NUMBER_OF_ROWS 
                            || nextCol < 0 || nextCol >= NUMBER_OF_COLUMNS 
                            || this.at(nextRow, nextCol) == null 
                            || this.at(nextRow, nextCol) != player) {
                            fourInARow = false;
                            break;
                        }
                    }

                    if (fourInARow) {
                        return true;
                    }
                }
            }
        }

        return false;
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
    private final Board board;

    public ConnectFourGame() {
        board = new Board();
    }

    public ConnectFourGame(ConnectFourGame game) {
        this(game.getBoard());
    }

    public ConnectFourGame(Board board) {
        // TODO: deep copy
        this.board = null;
    }

    public Board getBoard() {
        return board; // TODO: return deep copy
    }

    public boolean isValidMove(PlayerMove playerMove) {
        return true; // TODO: add actual validation
    }

    public void makeMove(PlayerMove playerMove) {
        board.makeMove(playerMove);
    }

    public List<PlayerMove> getPossibleMoves(Player player) {
        List<PlayerMove> possibleMoves = new ArrayList<PlayerMove>();

        for (int columnIndex : board.getNonFullColumns()) {
            possibleMoves.add(new PlayerMove(player, columnIndex));
        }

        return possibleMoves;
    }

    public boolean isFinished() {
        return board.isFull() || board.hasFourInARow();
    }
}

class ConnectFour {
    public static void main(String[] args) {
        ConnectFourGame game = new ConnectFourGame();
        System.out.println(game.getBoard());
    }
}
