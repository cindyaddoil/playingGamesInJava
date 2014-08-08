class ConnectFour {
    public static void main(String[] args) {
        Player humanPlayer = new HumanPlayer('O');
        Player randomPlayer = new RandomPlayer('X');
        Player monteCarloPlayer = new MonteCarloPlayer('X');
        Player minMaxPlayer = new MinMaxPlayer('X');
        Player minMaxAlphaBetaPlayer = new MinMaxAlphaBetaPlayer('X');

        //Player[] players = { humanPlayer, randomPlayer };
        //Player[] players = { humanPlayer, monteCarloPlayer };
        //Player[] players = { humanPlayer, minMaxPlayer };
        //Player[] players = { humanPlayer, minMaxAlphaBetaPlayer };
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
