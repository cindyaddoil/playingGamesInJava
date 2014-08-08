import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
