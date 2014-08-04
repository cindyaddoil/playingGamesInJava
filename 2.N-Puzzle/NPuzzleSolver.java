import java.util.List;

interface NPuzzleSolver {
    public List<Board> solve(Board board) throws NPuzzleBoardIsNotSolvableException;
}
