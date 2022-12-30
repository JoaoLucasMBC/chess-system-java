package application;

import chess.*;
import chess.FENGenerator;
import stockfish.Stockfish;

import java.util.*;

public class Program {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to Johnny's Chess!");
        System.out.println();
        System.out.print("How many human players are playing the game (0/1/2)? ");
        String answer = sc.nextLine();
        while (!answer.equals("1") && !answer.equals("2") && !answer.equals("0")) {
            System.out.print("The answer must be 0, 1 or 2 human players: ");
            answer = sc.nextLine();
        }

        ChessMatch chessMatch = new ChessMatch(Integer.parseInt(answer));
        List<ChessPiece> captured = new ArrayList<>();

        FENGenerator fenGenerator = null;
        Stockfish client = null;
        Color playerColor = null;
        if (chessMatch.getNumberOfPlayers() < 2) {
            client = new Stockfish();
            client.startEngine();
            // send commands manually
            client.sendCommand("uci");

            // creates FENGenerator
            fenGenerator = new FENGenerator(chessMatch);

            if (chessMatch.getNumberOfPlayers() == 1) {
                System.out.println();
                System.out.print("You are playing as White, Black, or Random (W/B/R)? ");
                String color = sc.nextLine().toUpperCase();

                while (!color.equals("B") && !color.equals("W") && !color.equals("R")) {
                    System.out.print("Possible answers are (W/B/R): ");
                    color = sc.nextLine().toUpperCase();
                }

                playerColor = getPlayerColor(color);
                System.out.println();
                System.out.println("You are playing as " + playerColor);
                System.out.println("Press ENTER to continue");
                sc.nextLine();
            } else {
                playerColor = (chessMatch.getCurrentPlayer() == Color.WHITE) ? Color.BLACK : Color.WHITE;

                System.out.println();
                System.out.println("You're about to watch Stockfish playing itself!");
                System.out.println("Press ENTER when you're ready to start");
                sc.nextLine();
            }
        }

        while (!chessMatch.getCheckMate()) {
            try {
                UI.clearScreen();
                UI.printMatch(chessMatch, captured);
                System.out.println();

                ChessPiece capturedPiece;

                //send this part to the UI? Overload sending string instead of sc
                if (chessMatch.getNumberOfPlayers() < 2 && chessMatch.getCurrentPlayer() != playerColor) {
                    String FEN = fenGenerator.generateFEN();
                    client.getOutput(0);

                    String bestMove = client.getBestMove(FEN, 100);

                    String sourceString = bestMove.substring(0, bestMove.length()/2);
                    ChessPosition source = new ChessPosition(sourceString.charAt(0), Integer.parseInt(sourceString.substring(1)));

                    String targetString = bestMove.substring(bestMove.length()/2);
                    ChessPosition target = new ChessPosition(targetString.charAt(0), Integer.parseInt(targetString.substring(1)));

                    capturedPiece = chessMatch.performChessMove(source, target);

                    if (chessMatch.getNumberOfPlayers() == 0) {
                        playerColor = (chessMatch.getCurrentPlayer() == Color.WHITE) ? Color.BLACK : Color.WHITE;
                        System.out.println();
                        System.out.println("Press ENTER to continue");
                        sc.nextLine();
                    }
                } else {
                    System.out.print("Source: ");
                    ChessPosition source = UI.readChessPosition(sc);

                    boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                    UI.clearScreen();
                    UI.printBoard(chessMatch.getPieces(), possibleMoves);

                    System.out.println();
                    System.out.print("Target: ");
                    ChessPosition target = UI.readChessPosition(sc);

                    capturedPiece = chessMatch.performChessMove(source, target);
                }

                if (capturedPiece != null) {
                    captured.add(capturedPiece);
                }

                if (chessMatch.getPromoted() != null) {
                    System.out.print("Enter piece for promotion (B/N/R/Q): ");
                    String type = sc.nextLine().toUpperCase();

                    while (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
                        System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                        type = sc.nextLine().toUpperCase();
                    }

                    chessMatch.replacePromotedPiece(type);
                }

            } catch (ChessException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        client.stopEngine();
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
    }

    private static Color getPlayerColor(String color) {
        if (color.equals("W")) return Color.WHITE;
        if (color.equals("B")) return Color.BLACK;

        Color[] options = new Color[]{Color.WHITE, Color.BLACK};
        return options[new Random().nextInt(options.length)];
    }
}