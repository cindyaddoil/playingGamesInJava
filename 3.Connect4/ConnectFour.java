import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
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
                playerMove = new PlayerMove(this, columnIndex - 1);
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

        for (PlayerMove possibleMove : possibleMoves) {
            if (game.isWinningMove(possibleMove)) {
                return possibleMove;
            }
        }

        return possibleMoves.get(rng.nextInt(possibleMoves.size()));
    }
}

class MonteCarloPlayer extends PrintablePlayer {
    private final Random rng;
    private final static int NUMBER_OF_SIMULATIONS = 5000;

    public MonteCarloPlayer() {
        this('O');
    }

    public MonteCarloPlayer(char representation) {
        super(representation);
        rng = new Random();
    }

    public PlayerMove makeMove(ConnectFourGame game) {
        int bestNumberOfWins = 0;
        PlayerMove bestMove = null;
        List<PlayerMove> possibleMoves = game.getPossibleMoves(this);

        for (PlayerMove possibleMove : possibleMoves) {
            int numberOfWins = 0;

            for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
                Player winner = simulateGame(game, possibleMove);
                if (winner == this) {
                    numberOfWins++;
                }
            }

            if (numberOfWins > bestNumberOfWins) {
                bestNumberOfWins = numberOfWins;
                bestMove = possibleMove;
            }
        }

        return bestMove;
    }

    private Player simulateGame(ConnectFourGame game, PlayerMove playerMove) {
        ConnectFourGame simulatedGame = new ConnectFourGame(game);
        simulatedGame.makeMove(playerMove);
        return null;
    }

    private PlayerMove generateRandomMove(ConnectFourGame game) {
        List<PlayerMove> possibleMoves = game.getPossibleMoves(this);

        for (PlayerMove possibleMove : possibleMoves) {
            if (game.isWinningMove(possibleMove)) {
                return possibleMove;
            }
        }

        return possibleMoves.get(rng.nextInt(possibleMoves.size()));
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

    public Board(Board otherBoard) {
        this();
        Collections.copy(this.board, otherBoard.board);
    }

    private int boardIndex(int row, int col) {
        return row * NUMBER_OF_COLUMNS + col;
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
            break;
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

    public boolean isColumnFull(int columnIndex) {
        int row = NUMBER_OF_ROWS - 1;

        return this.at(row, columnIndex) != null;
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
                        int nextCol = col + colDiff * i;

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

        for (int row = NUMBER_OF_ROWS - 1; row >= 0; row--) {
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
    private final List<PlayerMove> moves;

    public ConnectFourGame() {
        board = new Board();
        moves = new ArrayList<PlayerMove>();
    }

    public ConnectFourGame(ConnectFourGame game) {
        this(game.board, game.moves);
    }

    public ConnectFourGame(List<PlayerMove> moves) {
        this.board = boardFromMoves(moves);
        this.moves = new ArrayList<PlayerMove>(moves.size());
        Collections.copy(this.moves, moves);
    }

    public ConnectFourGame(Board board, List<PlayerMove> moves) {
        this.board = new Board(board);
        this.moves = new ArrayList<PlayerMove>(moves.size());
        Collections.copy(this.moves, moves);
    }

    public Board getBoard() {
        return new Board(board);
    }

    public List<PlayerMove> getMoves() {
        List<PlayerMove> movesCopy = new ArrayList<PlayerMove>(moves.size());
        Collections.copy(movesCopy, moves);
        return movesCopy;
    }

    public boolean isValidMove(PlayerMove playerMove) {
        return playerMove.getColumnIndex() >= 0 
            && playerMove.getColumnIndex() < Board.NUMBER_OF_COLUMNS
            && !board.isColumnFull(playerMove.getColumnIndex());
    }

    public void makeMove(PlayerMove playerMove) {
        moves.add(playerMove);
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

    public boolean isWinningMove(PlayerMove playerMove) {
        Board boardCopy = getBoard();
        boardCopy.makeMove(playerMove);
        return boardCopy.hasFourInARow();
    }

    private Board boardFromMoves(List<PlayerMove> moves) {
        Board board = new Board();

        for (PlayerMove playerMove : moves) {
            board.makeMove(playerMove);
        }

        return board;
    }
}

class ConnectFour {
    public static void main(String[] args) {
        ConnectFourGame game = new ConnectFourGame();

        int index = 0;
        Player players[] = { new HumanPlayer(), new RandomPlayer() };
        Player nextPlayer = players[index];

        System.out.println(game.getBoard());

        while (!game.isFinished()) {
            PlayerMove playerMove = nextPlayer.makeMove(game);
            game.makeMove(playerMove);

            index = (index + 1) % 2;
            nextPlayer = players[index];

            System.out.println(game.getBoard());
        }
    }
}
