import java.util.List;
import java.util.ArrayList;

abstract class PrintableWithSimulationPlayer extends PrintablePlayer {
    protected final Player ourPlayer;
    protected final Player opponentPlayer;

    public PrintableWithSimulationPlayer(char representation, Player ourPlayer, Player opponentPlayer) {
        super(representation);
        this.ourPlayer = ourPlayer;
        this.opponentPlayer = opponentPlayer;
    }

    protected List<PlayerMove> prepareMovesForSimulation(List<PlayerMove> moves) {
        List<PlayerMove> simulatedMoves = new ArrayList<PlayerMove>(moves.size());

        for (PlayerMove playerMove : moves) {
            if (playerMove.getPlayer() == this) {
                simulatedMoves.add(new PlayerMove(ourPlayer, playerMove.getColumnIndex()));
            } else {
                simulatedMoves.add(new PlayerMove(opponentPlayer, playerMove.getColumnIndex()));
            }
        }

        return simulatedMoves;
    }
}
