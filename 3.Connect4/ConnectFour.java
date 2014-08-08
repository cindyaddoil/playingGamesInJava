import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

interface Player {
    public PlayerMove suggestMove(final ConnectFourGame game);
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

    public PlayerMove suggestMove(ConnectFourGame game) {
        PlayerMove playerMove = null;

        while (true) {
            System.out.print("Select column [1-" + game.NUMBER_OF_COLUMNS + "]: ");

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

    public PlayerMove suggestMove(ConnectFourGame game) {
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
    private final RandomPlayer ourRandomPlayer;
    private final RandomPlayer opponentRandomPlayer;
    private final static int NUMBER_OF_SIMULATIONS = 2000;

    public MonteCarloPlayer() {
        this('O');
    }

    public MonteCarloPlayer(char representation) {
        super(representation);
        ourRandomPlayer = new RandomPlayer('1');
        opponentRandomPlayer = new RandomPlayer('2');
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        int bestNumberOfWins = 0;
        PlayerMove bestMove = null;
        List<PlayerMove> initialBoardMoves = prepareMovesForSimulation(game.getMoves());

        ConnectFourGame simulatedGame = new ConnectFourGame(initialBoardMoves);
        List<PlayerMove> possibleMoves = simulatedGame.getPossibleMoves(ourRandomPlayer);

        for (PlayerMove possibleMove : possibleMoves) {
            int numberOfWins = 0;

            for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
                Player winner = simulateGame(simulatedGame, possibleMove);

                if (winner == ourRandomPlayer) {
                    numberOfWins++;
                }

                simulatedGame = new ConnectFourGame(initialBoardMoves);
            }

            if (numberOfWins > bestNumberOfWins) {
                bestNumberOfWins = numberOfWins;
                bestMove = possibleMove;
            }
        }

        // anywhere we go will result in a loss in the end
        if (bestMove == null) {
            Collections.shuffle(possibleMoves);
            bestMove = possibleMoves.get(0);
        }

        return new PlayerMove(this, bestMove.getColumnIndex());
    }

    private Player simulateGame(ConnectFourGame simulatedGame, PlayerMove initialMove) {
        Player[] players = {ourRandomPlayer, opponentRandomPlayer};

        int currentPlayer = 0;
        simulatedGame.makeMove(initialMove);

        while (!simulatedGame.isFinished()) {
            currentPlayer = (currentPlayer + 1) % 2;
            PlayerMove nextMove = players[currentPlayer].suggestMove(simulatedGame);
            simulatedGame.makeMove(nextMove);
        }

        Player winner = null;

        if (simulatedGame.hasFourInARow()) {
            winner = players[currentPlayer];
        }

        return winner;
    }

    private List<PlayerMove> prepareMovesForSimulation(List<PlayerMove> moves) {
        List<PlayerMove> simulatedMoves = new ArrayList<PlayerMove>(moves.size());

        for (PlayerMove playerMove : moves) {
            if (playerMove.getPlayer() == this) {
                simulatedMoves.add(new PlayerMove(ourRandomPlayer, playerMove.getColumnIndex()));
            } else {
                simulatedMoves.add(new PlayerMove(opponentRandomPlayer, playerMove.getColumnIndex()));
            }
        }

        return simulatedMoves;
    }
}

class MinMaxPlayer extends PrintablePlayer {
    private final static int DEPTH_LIMIT = 8;
    private final DummyPlayer ourDummyPlayer;
    private final DummyPlayer opponentDummyPlayer;
    private final Player[] dummyPlayers;

    private class DummyPlayer extends PrintablePlayer {
        public DummyPlayer(char representation) {
            super(representation);
        }

        public PlayerMove suggestMove(ConnectFourGame game) {
            return null;
        }
    }

    public MinMaxPlayer() {
        this('O');
    }

    public MinMaxPlayer(char representation) {
        super(representation);
        ourDummyPlayer = new DummyPlayer('1');
        opponentDummyPlayer = new DummyPlayer('2');
        dummyPlayers = new Player[] {ourDummyPlayer, opponentDummyPlayer};
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        List<PlayerMove> initialBoardMoves = prepareMovesForSimulation(game.getMoves());

        ConnectFourGame simulatedGame = new ConnectFourGame(initialBoardMoves);
        List<PlayerMove> possibleMoves = simulatedGame.getPossibleMoves(ourDummyPlayer);

        int bestResult = Integer.MIN_VALUE;
        List<PlayerMove> candidateMoves = new ArrayList<PlayerMove>();

        for (PlayerMove possibleMove : possibleMoves) {
            int result = minimax(simulatedGame, possibleMove, 0, 0);

            System.out.println("move to column " + possibleMove.getColumnIndex() + " is worth " + result);

            if (result > bestResult) {
                candidateMoves.clear();
                bestResult = result;
                candidateMoves.add(possibleMove);
            } else if (result == bestResult) {
                candidateMoves.add(possibleMove);
            }
        }

        if (candidateMoves.size() > 1) {
            Collections.shuffle(candidateMoves);
        }

        return new PlayerMove(this, candidateMoves.get(0).getColumnIndex());
    }

    /*
     Pseudocode:

     minimax (move, depth) {
         if the move is a winning move
             return INFINITY

         if depth = DEPTH_LIMIT
             return heuristic value of the current move
         
         moveValue = INFINITY

         for all possible opponent moves
             moveValue = min (moveValue, -minimax(opponent move, depth + 1))

         return moveValue
     }

     */

    private int minimax(ConnectFourGame simulatedGame, PlayerMove playerMove, int playerIndex, int depth) {
        if (simulatedGame.isWinningMove(playerMove)) {
            return Integer.MAX_VALUE;
        }

        if (depth == DEPTH_LIMIT) {
            return heuristic(simulatedGame, playerMove);
        }

        simulatedGame.makeMove(playerMove);

        int moveValue = Integer.MAX_VALUE;
        int nextPlayerIndex = (playerIndex + 1) % 2;

        for (PlayerMove possibleMove : simulatedGame.getPossibleMoves(dummyPlayers[nextPlayerIndex])) {
            moveValue = Math.min(moveValue, -minimax(simulatedGame, possibleMove, nextPlayerIndex, depth + 1));
        }

        simulatedGame.undoMove();

        return moveValue;
    }

    private int heuristic(ConnectFourGame game, PlayerMove playerMove) {
        int value = 0;
        int count = 0;
        Player[][] board = game.getBoard();
        
        int moveRow = game.makeMove(playerMove);
        int moveCol = playerMove.getColumnIndex();

        // row heuristic
        for (int col = moveCol - 3; col <= moveCol; col++) {
            if (col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[moveRow][col + i] != playerMove.getPlayer() && board[moveRow][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[moveRow][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // column heuristic
        count = 0;
        for (int row = moveRow; row >= 0; row--) {
            if (board[row][moveCol] != playerMove.getPlayer()) break;
            count++;
        }

        // only if we can actually get 4 in this column
        if (moveRow + (4 - count) < ConnectFourGame.NUMBER_OF_ROWS) {
            value += countToValue(count);
        }

        // left diagonal heuristic
        for (int row = moveRow - 3, col = moveCol - 3; row <= moveRow && col <= moveCol; row++, col++) {
            if (row < 0 || row + 3 >= ConnectFourGame.NUMBER_OF_ROWS || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row + i][col + i] != playerMove.getPlayer() && board[row + i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row + i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // right diagonal heuristic
        for (int row = moveRow + 3, col = moveCol - 3; row >= moveRow && col <= moveCol; row--, col++) {
            if (row >= ConnectFourGame.NUMBER_OF_ROWS || row - 3 < 0 || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row - i][col + i] != playerMove.getPlayer() && board[row - i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row - i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        game.undoMove();

        return value;
    }

    private int countToValue(int count) {
        int value = 0;

        if (count == 1) {
            value = 1;
        } else if (count == 2) {
            value = 4;
        } else if (count == 3) {
            value = 32;
        }

        return value;
    }

    private List<PlayerMove> prepareMovesForSimulation(List<PlayerMove> moves) {
        List<PlayerMove> simulatedMoves = new ArrayList<PlayerMove>(moves.size());

        for (PlayerMove playerMove : moves) {
            if (playerMove.getPlayer() == this) {
                simulatedMoves.add(new PlayerMove(ourDummyPlayer, playerMove.getColumnIndex()));
            } else {
                simulatedMoves.add(new PlayerMove(opponentDummyPlayer, playerMove.getColumnIndex()));
            }
        }

        return simulatedMoves;
    }
}

class MinMaxAlphaBetaPlayer extends PrintablePlayer {
    private final static int DEPTH_LIMIT = 10;
    private final DummyPlayer ourDummyPlayer;
    private final DummyPlayer opponentDummyPlayer;
    private final Player[] dummyPlayers;

    private class DummyPlayer extends PrintablePlayer {
        public DummyPlayer(char representation) {
            super(representation);
        }

        public PlayerMove suggestMove(ConnectFourGame game) {
            return null;
        }
    }

    public MinMaxAlphaBetaPlayer() {
        this('O');
    }

    public MinMaxAlphaBetaPlayer(char representation) {
        super(representation);
        ourDummyPlayer = new DummyPlayer('1');
        opponentDummyPlayer = new DummyPlayer('2');
        dummyPlayers = new Player[] {ourDummyPlayer, opponentDummyPlayer};
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        List<PlayerMove> initialBoardMoves = prepareMovesForSimulation(game.getMoves());

        ConnectFourGame simulatedGame = new ConnectFourGame(initialBoardMoves);
        List<PlayerMove> possibleMoves = simulatedGame.getPossibleMoves(ourDummyPlayer);

        int bestResult = Integer.MIN_VALUE;
        List<PlayerMove> candidateMoves = new ArrayList<PlayerMove>();

        for (PlayerMove possibleMove : possibleMoves) {
            int result = alphaBeta(simulatedGame, possibleMove, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            System.out.println("move to column " + possibleMove.getColumnIndex() + " is worth " + result);

            if (result > bestResult) {
                candidateMoves.clear();
                bestResult = result;
                candidateMoves.add(possibleMove);
            } else if (result == bestResult) {
                candidateMoves.add(possibleMove);
            }
        }

        if (candidateMoves.size() > 1) {
            Collections.shuffle(candidateMoves);
        }

        return new PlayerMove(this, candidateMoves.get(0).getColumnIndex());
    }

    private int alphaBeta(ConnectFourGame simulatedGame, PlayerMove playerMove, int playerIndex, int depth, int alpha, int beta) {
        if (simulatedGame.isWinningMove(playerMove)) {
            if (dummyPlayers[playerIndex] == ourDummyPlayer) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        if (depth == DEPTH_LIMIT) {
            return heuristic(simulatedGame, playerMove);
        }

        simulatedGame.makeMove(playerMove);

        int nextPlayerIndex = (playerIndex + 1) % 2;
        List<PlayerMove> possibleMoves = simulatedGame.getPossibleMoves(dummyPlayers[nextPlayerIndex]);
        Collections.shuffle(possibleMoves);

        if (dummyPlayers[nextPlayerIndex] == ourDummyPlayer) {
            for (PlayerMove possibleMove : possibleMoves) {
                alpha = Math.max(alpha, alphaBeta(simulatedGame, possibleMove, nextPlayerIndex, depth + 1, alpha, beta));

                if (beta <= alpha) {
                    break;
                }
            }

            simulatedGame.undoMove();

            return alpha;
        } else {
            for (PlayerMove possibleMove : possibleMoves) {
                beta = Math.min(beta, alphaBeta(simulatedGame, possibleMove, nextPlayerIndex, depth + 1, alpha, beta));

                if (beta <= alpha) {
                    break;
                }
            }

            simulatedGame.undoMove();

            return beta;
        }
    }

    private int heuristic(ConnectFourGame game, PlayerMove playerMove) {
        int value = 0;
        int count = 0;
        Player[][] board = game.getBoard();
        
        int moveRow = game.makeMove(playerMove);
        int moveCol = playerMove.getColumnIndex();

        // row heuristic
        for (int col = moveCol - 3; col <= moveCol; col++) {
            if (col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[moveRow][col + i] != playerMove.getPlayer() && board[moveRow][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[moveRow][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // column heuristic
        count = 0;
        for (int row = moveRow; row >= 0; row--) {
            if (board[row][moveCol] != playerMove.getPlayer()) break;
            count++;
        }

        // only if we can actually get 4 in this column
        if (moveRow + (4 - count) < ConnectFourGame.NUMBER_OF_ROWS) {
            value += countToValue(count);
        }

        // left diagonal heuristic
        for (int row = moveRow - 3, col = moveCol - 3; row <= moveRow && col <= moveCol; row++, col++) {
            if (row < 0 || row + 3 >= ConnectFourGame.NUMBER_OF_ROWS || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row + i][col + i] != playerMove.getPlayer() && board[row + i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row + i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // right diagonal heuristic
        for (int row = moveRow + 3, col = moveCol - 3; row >= moveRow && col <= moveCol; row--, col++) {
            if (row >= ConnectFourGame.NUMBER_OF_ROWS || row - 3 < 0 || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row - i][col + i] != playerMove.getPlayer() && board[row - i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row - i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        game.undoMove();

        return value;
    }

    private int countToValue(int count) {
        int value = 0;

        if (count == 1) {
            value = 1;
        } else if (count == 2) {
            value = 4;
        } else if (count == 3) {
            value = 32;
        }

        return value;
    }

    private List<PlayerMove> prepareMovesForSimulation(List<PlayerMove> moves) {
        List<PlayerMove> simulatedMoves = new ArrayList<PlayerMove>(moves.size());

        for (PlayerMove playerMove : moves) {
            if (playerMove.getPlayer() == this) {
                simulatedMoves.add(new PlayerMove(ourDummyPlayer, playerMove.getColumnIndex()));
            } else {
                simulatedMoves.add(new PlayerMove(opponentDummyPlayer, playerMove.getColumnIndex()));
            }
        }

        return simulatedMoves;
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

class ConnectFourGame {
    public final static int NUMBER_OF_ROWS = 6;
    public final static int NUMBER_OF_COLUMNS = 7;

    private final static int directions[][] = {{0, 1}, {1, 1}, {1, 0}, {1, -1}};

    private Player[][] board;
    private List<PlayerMove> moves;

    public ConnectFourGame() {
        board = new Player[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        moves = new ArrayList<PlayerMove>();
    }

    public ConnectFourGame(List<PlayerMove> moves) {
        this();
        for (PlayerMove playerMove : moves) {
            makeMove(playerMove);
        }
    }

    public Player[][] getBoard() {
        return board;
    }

    public List<PlayerMove> getMoves() {
        return moves;
    }

    public boolean isFinished() {
        return isFull() || hasFourInARow();
    }

    public boolean isFull() {
        int row = NUMBER_OF_ROWS - 1;

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (board[row][col] == null) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidMove(PlayerMove playerMove) {
        return playerMove.getColumnIndex() >= 0
            && playerMove.getColumnIndex() < NUMBER_OF_COLUMNS
            && !isColumnFull(playerMove.getColumnIndex());
    }

    public boolean isWinningMove(PlayerMove playerMove) {
        makeMove(playerMove);
        boolean fourInARow = hasFourInARow();
        undoMove();

        return fourInARow;
    }

    public boolean isColumnFull(int columnIndex) {
        return board[NUMBER_OF_ROWS - 1][columnIndex] != null;
    }

    public int makeMove(PlayerMove playerMove) {
        int row;
        int col = playerMove.getColumnIndex();

        for (row = 0; row < NUMBER_OF_ROWS; row++) {
            if (board[row][col] != null) continue;

            board[row][col] = playerMove.getPlayer();
            break;
        }

        moves.add(playerMove);

        return row;
    }

    public void undoMove() {
        int lastMoveIndex = moves.size() - 1;
        PlayerMove lastMove = moves.get(lastMoveIndex);
        int col = lastMove.getColumnIndex();

        for (int row = NUMBER_OF_ROWS - 1; row >= 0; row--) {
            if (board[row][col] == null) continue;

            board[row][col] = null;
            break;
        }

        moves.remove(lastMoveIndex);
    }

    public boolean hasFourInARow() {
        Player player = null;

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                player = board[row][col];

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
                            || board[nextRow][nextCol] != player) {
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

    public List<PlayerMove> getPossibleMoves(Player player) {
        List<PlayerMove> possibleMoves = new ArrayList<PlayerMove>();

        for (int columnIndex : getNonFullColumns()) {
            possibleMoves.add(new PlayerMove(player, columnIndex));
        }

        return possibleMoves;
    }

    public void printBoard() {
        for (int row = NUMBER_OF_ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                if (board[row][col] == null) System.out.print('.');
                else System.out.print(board[row][col]);
            }
            System.out.println();
        }
    }

    private List<Integer> getNonFullColumns() {
        int row = NUMBER_OF_ROWS - 1;
        List<Integer> nonFullColumns = new ArrayList<Integer>();

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (board[row][col] == null) {
                nonFullColumns.add(col);
            }
        }

        return nonFullColumns;
    }
}

class ConnectFour {
    public static void main(String[] args) {
        //Player[] players = { new HumanPlayer('O'), new RandomPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MonteCarloPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MinMaxPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MinMaxAlphaBetaPlayer('X') };
        //Player[] players = { new MonteCarloPlayer('X'), new MinMaxPlayer('O') };
        Player[] players = { new MonteCarloPlayer('X'), new MinMaxAlphaBetaPlayer('O') };

        ConnectFourGame game = new ConnectFourGame();
        int index = 0;
        Player nextPlayer = players[index];

        game.printBoard();
        System.out.println();

        while (!game.isFinished()) {
            PlayerMove playerMove = nextPlayer.suggestMove(game);
            game.makeMove(playerMove);

            index = (index + 1) % 2;
            nextPlayer = players[index];

            game.printBoard();
            System.out.println();
        }
    }
}
