class ConnectFour {
    public static void main(String[] args) {
        //Player[] players = { new HumanPlayer('O'), new RandomPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MonteCarloPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MinMaxPlayer('X') };
        //Player[] players = { new HumanPlayer('O'), new MinMaxAlphaBetaPlayer('X') };
        //Player[] players = { new MonteCarloPlayer('X'), new MinMaxPlayer('O') };
        Player[] players = { new MonteCarloPlayer('X'), new MinMaxAlphaBetaPlayer('O') };

        ConnectFourGame game = new ConnectFourGame();
        int index = 0;
        Player nextPlayer = players[index];

        game.printBoard();
        System.out.println();

        while (!game.isFinished()) {
            PlayerMove playerMove = nextPlayer.suggestMove(game);
            game.makeMove(playerMove);

            index = (index + 1) % 2;
            nextPlayer = players[index];

            game.printBoard();
            System.out.println();
        }
    }
}
