public class Trie {
    private TrieNode root = new TrieNode();

    public void addWord(String word) {
        root.addWord(word.toLowerCase());
    }

    public boolean hasPrefix(String word) {
        TrieNode node = descend(word);

        return node == null ? false : true;
    }

    public boolean isWord(String word) {
        TrieNode node = descend(word);

        return node == null ? false : node.isWord();
    }

    private TrieNode descend(String word) {
        TrieNode lastNode = root;

        for (int i = 0, sz = word.length(); i < sz; i++) {
            lastNode = lastNode.getNode(word.charAt(i));

            if (lastNode == null) break;
        }

        return lastNode;
    }
}

class TrieNode {
    private TrieNode parent;
    private TrieNode[] children;
    private boolean isLeaf;
    private boolean isWord;
    private char character;

    protected TrieNode() {
        children = new TrieNode[26];
        isLeaf = true;
        isWord = false;
    }

    protected TrieNode(char character) {
        this();
        this.character = character;
    }

    protected void addWord(String word) {
        isLeaf = false;

        if (!(word.charAt(0) >= 'a' && word.charAt(0) <= 'z')) {
            System.out.println(word);
        }

        int charPos = word.charAt(0) - 'a';

        if (children[charPos] == null) {
            children[charPos] = new TrieNode(word.charAt(0));
            children[charPos].parent = this;
        }

        if (word.length() > 1) {
            children[charPos].addWord(word.substring(1));
        } else {
            children[charPos].isWord = true;
        }
    }

    protected TrieNode getNode(char c) {
        return children[c - 'a'];
    }

    protected boolean isWord() {
        return isWord;
    }
}
