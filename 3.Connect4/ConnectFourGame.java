import java.util.List;
import java.util.ArrayList;

class ConnectFourGame {
    public final static int NUMBER_OF_ROWS = 6;
    public final static int NUMBER_OF_COLUMNS = 7;

    private final static int directions[][] = {{0, 1}, {1, 1}, {1, 0}, {1, -1}};

    private Player[][] board;
    private List<PlayerMove> moves;

    public ConnectFourGame() {
        board = new Player[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        moves = new ArrayList<PlayerMove>();
    }

    public ConnectFourGame(List<PlayerMove> moves) {
        this();
        for (PlayerMove playerMove : moves) {
            makeMove(playerMove);
        }
    }

    public Player[][] getBoard() {
        return board;
    }

    public List<PlayerMove> getMoves() {
        return moves;
    }

    public boolean isFinished() {
        return isFull() || hasFourInARow();
    }

    public boolean isFull() {
        int row = NUMBER_OF_ROWS - 1;

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (board[row][col] == null) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidMove(PlayerMove playerMove) {
        return playerMove.getColumnIndex() >= 0
            && playerMove.getColumnIndex() < NUMBER_OF_COLUMNS
            && !isColumnFull(playerMove.getColumnIndex());
    }

    public boolean isWinningMove(PlayerMove playerMove) {
        makeMove(playerMove);
        boolean fourInARow = hasFourInARow();
        undoMove();

        return fourInARow;
    }

    public boolean isColumnFull(int columnIndex) {
        return board[NUMBER_OF_ROWS - 1][columnIndex] != null;
    }

    public int makeMove(PlayerMove playerMove) {
        int row;
        int col = playerMove.getColumnIndex();

        for (row = 0; row < NUMBER_OF_ROWS; row++) {
            if (board[row][col] != null) continue;

            board[row][col] = playerMove.getPlayer();
            break;
        }

        moves.add(playerMove);

        return row;
    }

    public void undoMove() {
        int lastMoveIndex = moves.size() - 1;
        PlayerMove lastMove = moves.get(lastMoveIndex);
        int col = lastMove.getColumnIndex();

        for (int row = NUMBER_OF_ROWS - 1; row >= 0; row--) {
            if (board[row][col] == null) continue;

            board[row][col] = null;
            break;
        }

        moves.remove(lastMoveIndex);
    }

    public boolean hasFourInARow() {
        Player player = null;

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                player = board[row][col];

                if (player == null) continue;

                for (int[] direction : directions) {
                    int rowDiff = direction[0]; 
                    int colDiff = direction[1];

                    boolean fourInARow = true;

                    for (int i = 1; i <= 3; i++) {
                        int nextRow = row + rowDiff * i;
                        int nextCol = col + colDiff * i;

                        if (nextRow < 0 || nextRow >= NUMBER_OF_ROWS
                            || nextCol < 0 || nextCol >= NUMBER_OF_COLUMNS
                            || board[nextRow][nextCol] != player) {
                            fourInARow = false;
                            break;
                        }
                    }

                    if (fourInARow) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<PlayerMove> getPossibleMoves(Player player) {
        List<PlayerMove> possibleMoves = new ArrayList<PlayerMove>();

        for (int columnIndex : getNonFullColumns()) {
            possibleMoves.add(new PlayerMove(player, columnIndex));
        }

        return possibleMoves;
    }

    public void printBoard() {
        for (int row = NUMBER_OF_ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                if (board[row][col] == null) System.out.print('.');
                else System.out.print(board[row][col]);
            }
            System.out.println();
        }
    }

    private List<Integer> getNonFullColumns() {
        int row = NUMBER_OF_ROWS - 1;
        List<Integer> nonFullColumns = new ArrayList<Integer>();

        for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
            if (board[row][col] == null) {
                nonFullColumns.add(col);
            }
        }

        return nonFullColumns;
    }
}
