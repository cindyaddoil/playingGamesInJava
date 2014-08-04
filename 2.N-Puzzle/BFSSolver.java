import java.util.List;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;

class BFSSolver extends SlidingPuzzleSolver {
    private Queue<BoardState> queue;
    private Set<List<Integer>> visited;

    public BoardState solvePuzzle(Board board) {
        queue = new LinkedList<BoardState>();
        visited = new HashSet<List<Integer>>();

        BoardState currentState = initBoardState(board);
        queue.add(currentState);
        visited.add(currentState.getBoard().getValues());

        while (!queue.isEmpty()) {
            currentState = queue.remove();

            if (isSolved(currentState.getBoard())) {
                return currentState;
            }

            for (BoardState nextState : generatePossibleMoves(currentState)) {
                if (nextState == null || visited.contains(nextState.getBoard().getValues())) continue;

                visited.add(nextState.getBoard().getValues());
                queue.add(nextState);
            }
        }

        return null;
    }
}
