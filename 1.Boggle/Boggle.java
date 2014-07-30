import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

class Boggle {
    private BoggleSolver solver;
    private Board board;

    public Boggle(String input) {
        board = buildBoard(input);
        //solver = new GeneratePossibleWordsSolver();
        solver = new GeneratePossibleWordsWithPruningSolver();
        //solver = new FindWordsFromDictionarySolver();
    }

    private Board buildBoard(String input) {
        List<Node> nodes = new ArrayList<Node>(input.length());

        int dimension = (int) Math.sqrt(input.length());
        assert (dimension * dimension == input.length()); // TODO: requires correct input handling

        for (int i = 0, sz = input.length(); i < sz; i++) {
            nodes.add(buildNode(input.charAt(i), new Position(i / dimension, i % dimension), dimension));
        }

        return new Board(nodes);
    }

    private Node buildNode(char value, Position position, int dimension) {
        return new Node(value, position, getNeighbours(position, dimension));
    }

    private List<Position> getNeighbours(Position position, int dimension) {
        int[] directions = {-1, 0, 1};
        List<Position> neighbours = new ArrayList<Position>();

        for (int rowDiff : directions) {
            for (int colDiff : directions) {
                Position neighbour = new Position(position.getRow() + rowDiff, position.getColumn() + colDiff);

                if (!position.equals(neighbour) && validPosition(neighbour, dimension)) {
                    neighbours.add(neighbour);
                }
            }
        }

        return neighbours;
    }

    private boolean validPosition(Position position, int dimension) {
        return inRange(position.getRow(), dimension) && inRange(position.getColumn(), dimension);
    }

    private boolean inRange(int x, int dimension) {
        return x >= 0 && x < dimension;
    }

    private Position positionFromPair(int row, int column) {
        return new Position(row, column);
    }

    public void solve() {
        List<String> foundWords = solver.solve(board);
        Collections.sort(foundWords);

        for (String foundWord : foundWords) {
            if (foundWord.length() < 3) continue;
            System.out.print(foundWord + " ");
        }

        System.out.println();
    }

    public static void main(String[] args) {
        assert (args.length > 0); // TODO: requires correct input handling
        new Boggle(args[0]).solve();
    }
}
