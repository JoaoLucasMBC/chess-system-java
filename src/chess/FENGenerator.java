package chess;

import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class FENGenerator {
    private ChessMatch chessMatch;

    public FENGenerator(ChessMatch chessMatch) {
        this.chessMatch = chessMatch;
    }

    public String generateFEN() {
        StringBuilder FEN = new StringBuilder();
        ChessPiece[][] piecesMat = chessMatch.getPieces();
        int numberOfBlanks = 0;

        // iterates over the whole board
        for (int row = 0; row < piecesMat.length; row ++) {
            numberOfBlanks = 0;
            for (int column = 0; column < piecesMat[row].length; column++) {
                ChessPiece p = piecesMat[row][column];

                if (p != null) {
                    if (numberOfBlanks > 0){
                        FEN.append("" + numberOfBlanks);
                        numberOfBlanks = 0;
                    }

                    if (p.getColor() == Color.WHITE) {
                        FEN.append(p.toString().toUpperCase());
                    } else {
                        FEN.append(p.toString().toLowerCase());
                    }
                } else {
                    numberOfBlanks++;
                }
            }
            if (numberOfBlanks > 0) {
                FEN.append("" + numberOfBlanks);
            }
            if (row < 7) FEN.append("/");
        }

        // determines current player
        FEN.append(currentPlayer(chessMatch.getCurrentPlayer()));

        // checks castling rights
        FEN.append(checkCastlingRights(piecesMat));

        // checks enPassant on the board
        FEN.append(checkEnPassant());

        // ignores 50-move rule
        FEN.append(" 0");

        // checks number of the turn
        FEN.append(" " + chessMatch.getTurn());

        return FEN.toString();
    }

    private String currentPlayer(Color color) {
        if (color == Color.WHITE) return " w";
        return " b";
    }

    private String checkCastlingRights(ChessPiece[][] pieces) {
        ChessPiece whiteKing = chessMatch.king(Color.WHITE);
        ChessPiece blackKing = chessMatch.king(Color.BLACK);

        StringBuilder castle = new StringBuilder();
        castle.append(" ");

        if (whiteKing.getMoveCount() == 0) {
            Position pos = whiteKing.getChessPosition().toPosition();
            Position rookPos = new Position(pos.getRow(), pos.getColumn() + 3);
            ChessPiece rook = pieces[rookPos.getRow()][rookPos.getColumn()];
            if (rook != null && rook.getMoveCount() == 0) {
                castle.append("K");
            }

            rookPos.setValues(pos.getRow(), pos.getColumn() - 4);
            rook = pieces[rookPos.getRow()][rookPos.getColumn()];
            if (rook != null && rook.getMoveCount() == 0) {
                castle.append("Q");
            }
        }
        if (blackKing.getMoveCount() == 0) {
            Position pos = blackKing.getChessPosition().toPosition();
            Position rookPos = new Position(pos.getRow(), pos.getColumn() + 3);
            ChessPiece rook = pieces[rookPos.getRow()][rookPos.getColumn()];
            if (rook != null && rook.getMoveCount() == 0) {
                castle.append("k");
            }

            rookPos.setValues(pos.getRow(), pos.getColumn() - 4);
            rook = pieces[rookPos.getRow()][rookPos.getColumn()];
            if (rook != null && rook.getMoveCount() == 0) {
                castle.append("q");
            }
        }

        if (castle.toString().equals(" ")) return " -";
        return castle.toString();
    }

    private String checkEnPassant() {
        if (chessMatch.getEnPassantVulnerable() != null) {
            return " " + chessMatch.getEnPassantVulnerable().getChessPosition();
        }

        return " -";
    }
}
