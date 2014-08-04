import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;

class AStarSolver extends SlidingPuzzleSolver {
    private PriorityQueue<BoardState> queue;
    private Set<List<Integer>> visited;

    public BoardState solvePuzzle(Board board) {
        queue = new PriorityQueue<BoardState>(100, new BoardStateComparator());
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

    private class BoardStateComparator implements Comparator<BoardState> {
        @Override
        public int compare(BoardState firstState, BoardState secondState) {
            int result = 0;

            if (firstState.getDistance() + firstState.getMoves() < secondState.getDistance() + secondState.getMoves()) {
                result = -1;
            } else if (firstState.getDistance() + firstState.getMoves() > secondState.getDistance() + secondState.getMoves()) {
                result = 1;
            }

            return result;
        }
    }
}
