import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class HumanPlayer extends PrintablePlayer {
    private String input;
    private final BufferedReader inputStream;

    public HumanPlayer() {
        this('X');
    }

    public HumanPlayer(char representation) {
        super(representation);
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        PlayerMove playerMove = null;

        while (true) {
            System.out.print("Select column [1-" + game.NUMBER_OF_COLUMNS + "]: ");

            try {
                input = inputStream.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Failed to read your move! Please try again.");
                continue;
            }

            try {
                int columnIndex = Integer.parseInt(input);
                playerMove = new PlayerMove(this, columnIndex - 1);
            } catch (NumberFormatException e) {
                System.out.println("Invalid column! Please try again.");
                continue;
            }

            if (!game.isValidMove(playerMove)) {
                System.out.println("Invalid move! Please try again.");
                continue;
            }

            break;
        }

        return playerMove;
    }
}
