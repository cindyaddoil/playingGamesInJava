import java.util.List;
import java.util.Random;

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
