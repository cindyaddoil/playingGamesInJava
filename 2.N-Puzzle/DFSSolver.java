import java.util.List;
import java.util.Set;
import java.util.HashSet;

class DFSSolver extends SlidingPuzzleSolver {
    private Set<List<Integer>> visited;

    public BoardState solvePuzzle(Board board) {
        visited = new HashSet<List<Integer>>();
        BoardState currentState = initBoardState(board);

        return findSolution(currentState);
    }
    
    private BoardState findSolution(BoardState currentState) {
        if (isSolved(currentState)) {
            return currentState;
        }

        BoardState finalState = null;

        visited.add(currentState.getBoard().getValues());

        for (BoardState nextState : generatePossibleMoves(currentState)) {
            if (nextState == null || visited.contains(nextState.getBoard().getValues())) continue;

            finalState = findSolution(nextState);

            if (finalState != null) {
                return finalState;
            }
        }

        visited.remove(currentState.getBoard().getValues());

        return null;
    }
}
