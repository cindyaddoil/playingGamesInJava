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
