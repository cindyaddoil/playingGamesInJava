import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

interface NPuzzleSolver {
    public void solve(Board board);
}

abstract class SlidingPuzzleSolver implements NPuzzleSolver {
    public void solve(Board board) {
        if (!isSolvable(board)) return;

        solvePuzzle(board);
    }

    abstract void solvePuzzle(Board board);

    /*
     * NPuzzle is solvable when
     * >>> zeroRow + numberOfInversions is even <<<
     * where zeroRow is the row number of empty tile (row index starts from 1)
     * where numberOfInversions is the amount of elements Ai and Aj such that i < j and Ai > Aj (Ai /= 0, Aj /= 0)
     */
    protected boolean isSolvable(Board board) {
        int zeroRow = 1 + zeroPosition(board);
        int numberOfInversions = 0;

        int[] values = board.getValues();

        for (int i = 0, sz = values.length; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                if (values[i] == 0 || values[j] == 0) continue;

                if (i < j && values[i] > values[j]) numberOfInversions++;
            }
        }

        return (zeroRow + numberOfInversions) % 2 == 0;
    }

    protected int zeroPosition(Board board) {
        return Arrays.asList(board.getValues()).indexOf(0) / board.getDimension();
    }

    protected BoardState initBoardState(Board board) {
        return new BoardState(
            board,
            zeroPosition(board),
            0, // TODO: calculate manhattan distance
            0,
            null
        );
    }
}

class DFSSolver extends SlidingPuzzleSolver {
    final static int MAX_DEPTH = 50;

    public void solvePuzzle(Board board) {
    }
}

class BFSSolver extends SlidingPuzzleSolver {
    private Queue<BoardState> queue;

    public void solvePuzzle(Board board) {
        BoardState currentState = initBoardState(board);
        queue = new LinkedList<BoardState>();
        queue.add(currentState);

        while (!queue.isEmpty()) {
            currentState = queue.remove();
        }
    }
}

class AStarSolver extends SlidingPuzzleSolver {
    public void solvePuzzle(Board board) {
    }
}

class IDAStarSolver extends SlidingPuzzleSolver {
    public void solvePuzzle(Board board) {
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
}

enum MoveDirection {
    North,
    East,
    South,
    West
}

class Board {
    private final int dimension;
    private final int[] values;

    public Board(int dimension, int[] values) {
        this.dimension = dimension;
        this.values = values;
    }

    public int getDimension() {
        return dimension;
    }

    public int[] getValues() {
        return values;
    }
}

class NPuzzle {
    private Board board;
    private NPuzzleSolver solver;

    public NPuzzle(String fileName) {
        board = buildBoard(fileName);
        solver = new DFSSolver();
        //solver = new BFSSolver();
        //solver = new AStarSolver();
        //solver = new IDAStarSolver();
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
        int[] values = new int[dimension * dimension];

        for (int i = 0, sz = dimension * dimension; i < sz; i++) {
            values[i] = scanner.nextInt();
        }

        return new Board(dimension, values);
    }

    public void solve() {
        solver.solve(board);
    }

    public static void main(String[] args) {
        assert(args[0] != null); // TODO: requires correct input handling
        new NPuzzle(args[0]).solve();
    }
}
