import java.util.List;
import java.util.ArrayList;

abstract class SlidingPuzzleSolver implements NPuzzleSolver {
    public List<Board> solve(Board board) throws NPuzzleBoardIsNotSolvableException {
        if (!isSolvable(board)) throw new NPuzzleBoardIsNotSolvableException();

        BoardState finalState = solvePuzzle(board);

        return finalState == null ? null : buildSolutionPath(finalState);
    }

    private List<Board> buildSolutionPath(BoardState currentState) {
        List<Board> result;

        if (currentState.getPreviousState() == null) {
            result = new ArrayList<Board>();
        } else {
            result = buildSolutionPath(currentState.getPreviousState());
        }

        result.add(currentState.getBoard());

        return result;
    }

    abstract BoardState solvePuzzle(Board board);

    /*
     * NPuzzle is solvable when
     * >>> zeroRow + numberOfInversions is even <<<
     * where zeroRow is the row number of empty tile (row index starts from 1)
     * where numberOfInversions is the amount of elements Ai and Aj such that i < j and Ai > Aj (Ai /= 0, Aj /= 0)
     */
    protected boolean isSolvable(Board board) {
        int zeroRow = 1 + zeroPosition(board) / board.getDimension();
        int numberOfInversions = 0;

        List<Integer> values = board.getValues();

        for (int i = 0, sz = values.size(); i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                if (i == j || values.get(i) == 0 || values.get(j) == 0) continue;

                if (i < j && values.get(i) > values.get(j)) numberOfInversions++;
            }
        }

        return (zeroRow + numberOfInversions) % 2 == 0;
    }

    protected int zeroPosition(Board board) {
        return board.getValues().indexOf(0);
    }

    protected BoardState initBoardState(Board board) {
        return new BoardState(
            board,
            zeroPosition(board),
            boardDistance(board),
            0,
            null
        );
    }

    protected List<BoardState> generatePossibleMoves(BoardState currentState) {
        List<BoardState> possibleMoves = new ArrayList<BoardState>();

        for (MoveDirection moveDirection : MoveDirection.values()) {
            possibleMoves.add(makeMove(currentState, moveDirection));
        }

        return possibleMoves;
    }

    private BoardState makeMove(BoardState currentState, MoveDirection moveDirection) {
        BoardState nextState = null;
        int emptyTileRow = positionToRow(currentState.getBoard().getDimension(), currentState.getEmptyTile());
        int emptyTileCol = positionToColumn(currentState.getBoard().getDimension(), currentState.getEmptyTile());

        switch (moveDirection) {
            case North:
                if (emptyTileRow > 0) {
                    nextState = updatePuzzleState(currentState, emptyTileRow - 1, emptyTileCol);
                }
                break;
            case East:
                if (emptyTileCol < currentState.getBoard().getDimension() - 1) {
                    nextState = updatePuzzleState(currentState, emptyTileRow, emptyTileCol + 1);
                }
                break;
            case South:
                if (emptyTileRow < currentState.getBoard().getDimension() - 1) {
                    nextState = updatePuzzleState(currentState, emptyTileRow + 1, emptyTileCol);
                }
                break;
            case West:
                if (emptyTileCol > 0) {
                    nextState = updatePuzzleState(currentState, emptyTileRow, emptyTileCol - 1);
                }
                break;
        }

        return nextState;
    }

    private int positionToRow(int dimension, int tilePosition) {
        return tilePosition / dimension;
    }

    private int positionToColumn(int dimension, int tilePosition) {
        return tilePosition % dimension;
    }

    private int rowColToPosition(int dimension, int row, int col) {
        return row * dimension + col;
    }

    private BoardState updatePuzzleState(BoardState currentState, int row, int col) {
        List<Integer> values = new ArrayList<Integer>(currentState.getBoard().getValues());

        int newEmptyTilePosition = rowColToPosition(currentState.getBoard().getDimension(), row, col);
        int value = values.get(newEmptyTilePosition);

        int newDistance = currentState.getDistance() - manhattan(value, currentState.getBoard().getDimension(), newEmptyTilePosition) + manhattan(value, currentState.getBoard().getDimension(), currentState.getEmptyTile());

        values.set(currentState.getEmptyTile(), value);
        values.set(newEmptyTilePosition, 0);

        return new BoardState(
            new Board(currentState.getBoard().getDimension(), values),
            newEmptyTilePosition,
            newDistance,
            currentState.getMoves() + 1,
            currentState
        );
    }

    protected boolean isSolved(Board board) {
        for (int i = 1; i <= board.getDimension() * board.getDimension() - 1; i++) {
            if (board.getValues().get(i-1) != i) return false;
        }

        return true;
    }

    protected boolean isSolved(BoardState boardState) {
        return boardState.getDistance() == 0;
    }

    protected int boardDistance(Board board) {
        int distance = 0;

        for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
            distance += manhattan(board.getValues().get(i), board.getDimension(), i);
        }

        return distance;
    }

    protected int manhattan(int tile, int dimension, int index) {
        if (tile == 0) return 0;

        int currentRow = positionToRow(dimension, index);
        int currentCol = positionToColumn(dimension, index);
        int expectedRow = positionToRow(dimension, tile - 1);
        int expectedCol = positionToColumn(dimension, tile - 1);
        int rowDistance = Math.abs(currentRow - expectedRow);
        int colDistance = Math.abs(currentCol - expectedCol);

        return rowDistance + colDistance;
    }
}
