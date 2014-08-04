import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

interface NPuzzleSolver {
    public List<Board> solve(Board board) throws PuzzleBoardIsNotSolvableException;
}

class PuzzleBoardIsNotSolvableException extends Exception {
    public PuzzleBoardIsNotSolvableException() {}

    public PuzzleBoardIsNotSolvableException(String message) {
        super(message);
    }
}

abstract class SlidingPuzzleSolver implements NPuzzleSolver {
    public List<Board> solve(Board board) throws PuzzleBoardIsNotSolvableException {
        if (!isSolvable(board)) throw new PuzzleBoardIsNotSolvableException();

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

class DFSSolver extends SlidingPuzzleSolver {
    final static int MAX_DEPTH = 50;

    public BoardState solvePuzzle(Board board) {
        return null;
    }
}

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

class BoardState {
    private final Board board;
    private final int emptyTile;
    private final int distance;
    private final int moves;
    private final BoardState previousState;

    public BoardState(Board board, int emptyTile, int distance, int moves, BoardState previousState) {
        this.board = board;
        this.emptyTile = emptyTile;
        this.distance = distance;
        this.moves = moves;
        this.previousState = previousState;
    }

    public Board getBoard() {
        return board;
    }

    public int getEmptyTile() {
        return emptyTile;
    }

    public int getDistance() {
        return distance;
    }

    public int getMoves() {
        return moves;
    }

    public BoardState getPreviousState() {
        return previousState;
    }
}

enum MoveDirection {
    North,
    East,
    South,
    West
}

class Board {
    private final int dimension;
    private final List<Integer> values;

    public Board(int dimension, List<Integer> values) {
        this.dimension = dimension;
        this.values = values;
    }

    public int getDimension() {
        return dimension;
    }

    public List<Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                sb.append(values.get(row * dimension + col));
                sb.append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}

class NPuzzle {
    private Board board;
    private NPuzzleSolver solver;

    public NPuzzle(String fileName) {
        board = buildBoard(fileName);
        //solver = new DFSSolver();
        //solver = new BFSSolver();
        //solver = new AStarSolver();
        solver = new IDAStarSolver();
    }

    private Board buildBoard(String fileName) {
        String input = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(" ");
            }

            input = stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Integer dimension = 0;
        Scanner scanner = new Scanner(input);

        // TODO: error handling for incorrect input file
        dimension = scanner.nextInt();
        List<Integer> values = new ArrayList<Integer>(dimension * dimension);

        for (int i = 0, sz = dimension * dimension; i < sz; i++) {
            values.add(i, scanner.nextInt());
        }

        return new Board(dimension, values);
    }

    public void solve() throws PuzzleBoardIsNotSolvableException {
        List<Board> solutionPath = solver.solve(board);

        if (solutionPath == null) {
            System.out.println("Unable to find solution :(");
        } else {
            System.out.println("Solution found :)");
        
            for (Board board : solutionPath) {
                System.out.println(board);
            }

            System.out.println("Number of steps to solve: " + solutionPath.size());
        }
    }

    public static void main(String[] args) throws PuzzleBoardIsNotSolvableException {
        assert(args[0] != null); // TODO: requires correct input handling
        new NPuzzle(args[0]).solve();
    }
}
