package boardgame;

public abstract class Piece {
    protected Position position;
    private Board board;

    public Piece(Board board) {
        this.board = board;
        position = null;
    }

    protected Board getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves();

    public boolean possibleMove(Position position) {
        if (possibleMoves()[position.getRow()][position.getRow()] == true) {
            return true;
        }
        return false;
    }

    public boolean isThereAnyPossibleMove() {
        for (boolean move : possibleMoves()[]) {

        }
    }
}
