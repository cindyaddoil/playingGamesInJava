import java.util.List;

class Board {
    private final int dimension;
    private final List<Integer> values;

    public Board(int dimension, List<Integer> values) {
        this.dimension = dimension;
        this.values = values;
    }

    public int getDimension() {
        return dimension;
    }

    public List<Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                sb.append(values.get(row * dimension + col));
                sb.append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
