package chess;

import boardgame.Position;

public class ChessPosition {
    private Character column;
    private Integer row;

    public ChessPosition(Character column, Integer row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8) {
            throw new ChessException("Error instantiating ChessPosition. Valid values range from a1 to h8.");
        }
        this.column = column;
        this.row = row;
    }

    public Character getColumn() {
        return column;
    }

    public Integer getRow() {
        return row;
    }

    protected Position toPosition() {
        int matrix_row = 8 - getRow();
        int matrix_column = getColumn() - 'a';

        return new Position(matrix_row , matrix_column);
    }

    protected static ChessPosition fromPosition(Position position) {
        int chess_row = 8 - position.getRow();
        char chess_column = (char) ('a' + position.getColumn());

        return new ChessPosition(chess_column, chess_row);
    }


    @Override
    public String toString() {
        return "" + getColumn() + getRow();
    }
}
