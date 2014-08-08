abstract class PrintableWithSimulationAndHeuristicsPlayer extends PrintableWithSimulationPlayer {
    public PrintableWithSimulationAndHeuristicsPlayer(char representation, Player ourPlayer, Player opponentPlayer) {
        super(representation, ourPlayer, opponentPlayer);
    }

    protected int heuristic(ConnectFourGame game, PlayerMove playerMove) {
        int value = 0;
        int count = 0;
        Player[][] board = game.getBoard();
        
        int moveRow = game.makeMove(playerMove);
        int moveCol = playerMove.getColumnIndex();

        // row heuristic
        for (int col = moveCol - 3; col <= moveCol; col++) {
            if (col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[moveRow][col + i] != playerMove.getPlayer() && board[moveRow][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[moveRow][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // column heuristic
        count = 0;
        for (int row = moveRow; row >= 0; row--) {
            if (board[row][moveCol] != playerMove.getPlayer()) break;
            count++;
        }

        // only if we can actually get 4 in this column
        if (moveRow + (4 - count) < ConnectFourGame.NUMBER_OF_ROWS) {
            value += countToValue(count);
        }

        // left diagonal heuristic
        for (int row = moveRow - 3, col = moveCol - 3; row <= moveRow && col <= moveCol; row++, col++) {
            if (row < 0 || row + 3 >= ConnectFourGame.NUMBER_OF_ROWS || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row + i][col + i] != playerMove.getPlayer() && board[row + i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row + i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        // right diagonal heuristic
        for (int row = moveRow + 3, col = moveCol - 3; row >= moveRow && col <= moveCol; row--, col++) {
            if (row >= ConnectFourGame.NUMBER_OF_ROWS || row - 3 < 0 || col < 0 || col + 3 >= ConnectFourGame.NUMBER_OF_COLUMNS) continue;

            boolean ok = true;
            count = 0;

            for (int i = 0; i < 4; i++) {
                if (board[row - i][col + i] != playerMove.getPlayer() && board[row - i][col + i] != null) {
                    ok = false;
                    break;
                }

                if (board[row - i][col + i] == playerMove.getPlayer()) {
                    count++;
                }
            }

            if (!ok) continue;

            value += countToValue(count);
        }

        game.undoMove();

        return value;
    }

    private int countToValue(int count) {
        int value = 0;

        if (count == 1) {
            value = 1;
        } else if (count == 2) {
            value = 4;
        } else if (count == 3) {
            value = 32;
        }

        return value;
    }
}
