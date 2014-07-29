import java.util.List;
import java.util.ArrayList;

interface BoggleSolver {
    String[] solve(Board board);
}

class GeneratePossibleWordsSolver implements BoggleSolver {
    public String[] solve(Board board) {
        return null;
    }
}

class FindWordsFromDictionarySolver implements BoggleSolver {
    public String[] solve(Board board) {
        return null;
    }
}

class Position {
    private final int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Position) {
            Position pos = (Position) obj;
            if (pos.getRow() == row && pos.getColumn() == column) {
                return true;
            }
        }
        
        return false;
    }
}

class Node {
    private final char value;
    private final Position position;
    private final List<Position> neighbours;

    public Node(char value, Position position, List<Position> neighbours) {
        this.value = value;
        this.position = position;
        this.neighbours = neighbours;
    }
}

class Board {
    private final List<Node> nodes;

    public Board(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}

class Boggle {
    private BoggleSolver solver;
    private Board board;

    public Boggle(String input) {
        board = buildBoard(input);
        solver = new FindWordsFromDictionarySolver();
    }

    private Board buildBoard(String input) {
        List<Node> nodes = new ArrayList<Node>(input.length());

        int dimension = (int) Math.sqrt(input.length());
        assert (dimension * dimension == input.length()); // TODO: requires correct input handling

        for (int i = 0, sz = input.length(); i < sz; i++) {
            nodes.set(i, buildNode(input.charAt(i), new Position(i / dimension, i % dimension), dimension));
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
        solver.solve(board);
    }

    public static void main(String[] args) {
        assert (args.length > 0); // TODO: requires correct input handling
        new Boggle(args[0]).solve();
    }
}
