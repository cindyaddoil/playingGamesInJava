import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class MinMaxPlayer extends PrintableWithSimulationAndHeuristicsPlayer {
    private final static int DEPTH_LIMIT = 8;
    private final Player[] dummyPlayers;

    public MinMaxPlayer() {
        this('O');
    }

    public MinMaxPlayer(char representation) {
        super(representation, new DummyPlayer(), new DummyPlayer());
        dummyPlayers = new Player[] {ourPlayer, opponentPlayer};
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        List<PlayerMove> initialBoardMoves = prepareMovesForSimulation(game.getMoves());

        ConnectFourGame simulatedGame = new ConnectFourGame(initialBoardMoves);
        List<PlayerMove> possibleMoves = simulatedGame.getPossibleMoves(ourPlayer);

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
}
