class DummyPlayer extends PrintablePlayer {
    public DummyPlayer() {
        this('O');
    }

    public DummyPlayer(char representation) {
        super(representation);
    }

    public PlayerMove suggestMove(ConnectFourGame game) {
        return null;
    }
}
