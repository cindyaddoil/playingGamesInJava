class Position {
    private final int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Position) {
            Position pos = (Position) obj;
            if (pos.getRow() == row && pos.getColumn() == column) {
                return true;
            }
        }
        
        return false;
    }
}
