import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class MinMaxAlphaBetaPlayer extends PrintableWithSimulationAndHeuristicsPlayer {
    private final static int DEPTH_LIMIT = 10;
    private final Player[] dummyPlayers;

    public MinMaxAlphaBetaPlayer() {
        this('O');
    }

    public MinMaxAlphaBetaPlayer(char representation) {
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
            if (dummyPlayers[playerIndex] == ourPlayer) {
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

        if (dummyPlayers[nextPlayerIndex] == ourPlayer) {
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
}
