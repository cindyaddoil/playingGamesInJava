import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
