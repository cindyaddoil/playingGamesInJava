import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class FindWordsFromDictionarySolver extends WithDictionary implements BoggleSolver {
    public List<String> solve(Board board) {
        List<String> words = readDictionary();
        List<String> foundWords = new ArrayList<String>();

        for (String word : words) {
            if (findWord(word, board)) {
                foundWords.add(word);
            }
        }

        return foundWords;
    }

    private boolean findWord(String word, Board board) {
        List<Node> startingNodes = findStartingNodes(board, word);

        for (Node startingNode : startingNodes) {
            Set<Position> visited = new HashSet<Position>();

            visited.add(startingNode.getPosition());

            if (searchForWord(1, word, board, startingNode, visited)) {
                return true;
            }
        }

        return false;
    }

    private List<Node> findStartingNodes(Board board, String word) {
        List<Node> startingNodes = new ArrayList<Node>();

        for (Node node : board.getNodes()) {
            if (node.getValue() == word.charAt(0)) {
                startingNodes.add(node);
            }
        }

        return startingNodes;
    }

    private boolean searchForWord(int index, String word, Board board, Node currentNode, Set<Position> visited) {
        if (index == word.length()) return true;

        List<Node> neighbours = new ArrayList<Node>();

        for (Position neighbour : currentNode.getNeighbours()) {
            neighbours.add(positionToNode(neighbour, board));
        }

        List<Node> candidates = new ArrayList<Node>();

        for (Node candidate : neighbours) {
            if (visited.contains(candidate.getPosition())) continue;
            if (candidate.getValue() == word.charAt(index)) {
                candidates.add(candidate);
            }
        }

        for (Node node : candidates) {
            visited.add(node.getPosition());
            if (searchForWord(index + 1, word, board, node, visited)) return true;
            visited.remove(node.getPosition());
        }

        return false;
    }

    private Node positionToNode(Position position, Board board) {
        for (Node node : board.getNodes()) {
            if (position.equals(node.getPosition())) return node;
        }

        assert (false); // should never happen

        return null;
    }

}
