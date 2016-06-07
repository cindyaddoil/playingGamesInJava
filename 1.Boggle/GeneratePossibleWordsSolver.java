import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class GeneratePossibleWordsSolver extends WithDictionary implements BoggleSolver {
    public List<String> solve(Board board) {
        Set<String> words = new HashSet<String>(readDictionary());
        Set<String> foundWords = new HashSet<String>();

        for (Node startingNode : board.getNodes()) {
            Set<Position> visited = new HashSet<Position>();
            visited.add(startingNode.getPosition());
            findAll("" + startingNode.getValue(), startingNode, board, words, foundWords, visited);
        }

        return new ArrayList<String>(foundWords);
    }

    private void findAll(String currentWord, Node currentNode, Board board, Set<String> words, Set<String> foundWords, Set<Position> visited) {
        if (currentWord.length() > 20) return; // TODO: the limit should actually be the length of the longest word in the dictionary

        if (words.contains(currentWord)) {
            foundWords.add(currentWord);
        }

        List<Node> neighbours = new ArrayList<Node>();

        for (Position neighbour : currentNode.getNeighbours()) {
            neighbours.add(positionToNode(neighbour, board));
        }

        for (Node neighbour : neighbours) {
            if (visited.contains(neighbour.getPosition())) continue;
            visited.add(neighbour.getPosition());
            findAll(currentWord + neighbour.getValue(), neighbour, board, words, foundWords, visited);
            visited.remove(neighbour.getPosition());
        }
    }

    private Node positionToNode(Position position, Board board) {
        for (Node node : board.getNodes()) {
            if (position.equals(node.getPosition())) return node;
        }

        assert (false); // should never happen

        return null;
    }
}
