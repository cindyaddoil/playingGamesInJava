import java.util.List;
import java.util.Set;
import java.util.HashSet;

class IDAStarSolver extends SlidingPuzzleSolver {
    private Set<List<Integer>> visited;

    public BoardState solvePuzzle(Board board) {
        visited = new HashSet<List<Integer>>();
        BoardState currentState = initBoardState(board);
        BoardState finalState = null;
        int depthLimit = currentState.getDistance();
        
        while ((finalState = findSolution(currentState, depthLimit)) == null) {
            depthLimit += 5;
        }

        return finalState;
    }

    private BoardState findSolution(BoardState currentState, int depthLimit) {
        if (isSolved(currentState)) {
            return currentState;
        }

        BoardState finalState = null;

        visited.add(currentState.getBoard().getValues());

        for (BoardState nextState : generatePossibleMoves(currentState)) {
            if (nextState == null || visited.contains(nextState.getBoard().getValues()) || nextState.getMoves() + nextState.getDistance() > depthLimit) continue;

            finalState = findSolution(nextState, depthLimit);

            if (finalState != null) {
                return finalState;
            }
        }

        visited.remove(currentState.getBoard().getValues());

        return null;
    }
}
