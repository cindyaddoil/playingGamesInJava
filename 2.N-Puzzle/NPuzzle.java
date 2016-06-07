import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        List<Integer> values = new ArrayList<Integer>(dimension * dimension);

        for (int i = 0, sz = dimension * dimension; i < sz; i++) {
            values.add(i, scanner.nextInt());
        }

        return new Board(dimension, values);
    }

    public void solve() throws NPuzzleBoardIsNotSolvableException {
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

    public static void main(String[] args) throws NPuzzleBoardIsNotSolvableException {
        assert(args[0] != null); // TODO: requires correct input handling
        new NPuzzle(args[0]).solve();
    }
}
