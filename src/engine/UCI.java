package engine;

import engine.datastructs.ChessBoard;

import java.util.*;

import static engine.utils.BitBoardConstants.*;

public class UCI {
    static String ENGINENAME = "Quetzal v1.0";

    /**
     * UCI communication loop
     */
    public static void uciCommunication() {
        Scanner input = new Scanner(System.in);
        while (true) {
            String inputString = input.nextLine();
            if ("uci".equals(inputString)) {
                inputUCI();
            } else if (inputString.startsWith("setoption")) {
                inputSetOption(inputString);
            } else if ("isready".equals(inputString)) {
                inputIsReady();
            } else if ("ucinewgame".equals(inputString)) {
                inputUCINewGame();
            } else if (inputString.startsWith("position")) {
                inputPosition(inputString);
            } else if (inputString.startsWith("go")) {
                inputGo();
            } else if ("print".equals(inputString)) {
                inputPrint();
            } else if ("quit".equals(inputString)) {
                inputQuit();
            }
        }
    }

    /**
     * Print engine information for the UCI protocol
     */
    public static void inputUCI() {
        System.out.println("id name " + ENGINENAME);
        System.out.println("id author Jack McDonald");
        //TODO options go here
        System.out.println("uciok");
    }

    /**
     * Set options for the UCI protocol
     *
     * @param inputString Input string from the UCI protocol
     */
    public static void inputSetOption(String inputString) {
        //TODO set options
    }

    /**
     * Print "readyok" for the UCI protocol indicating the engine is ready
     */
    public static void inputIsReady() {
        System.out.println("readyok");
    }

    /**
     * Start a new game
     */
    public static void inputUCINewGame() {
        //TODO add code here
    }

    /**
     * Parse the position command from the UCI protocol and update the global board
     *
     * @param input Input string from the UCI protocol
     */
    public static void inputPosition(String input) {
        input = input.substring(9).concat(" ");

        //Check for startpos or fen
        if (input.contains("startpos ")) {
            input = input.substring(9);
            BoardGenerator.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            Quetzal.WhiteToMove = true;
        } else if (input.contains("fen")) {
            input = input.substring(4);
            BoardGenerator.importFEN(input);
        }

        ChessBoard board = new ChessBoard(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK,
                Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK,
                Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, null);

        //Make each move on global board
        if (input.contains("moves")) {
            input = input.substring(input.indexOf("moves") + 6);
            while (!input.isEmpty()) {
                String moves;
                if (Quetzal.WhiteToMove) {
                    moves = Moves.possibleMovesWhite(board);
                } else {
                    moves = Moves.possibleMovesBlack(board);
                }
                algebraToMove(input, moves);
                input = input.substring(input.indexOf(' ') + 1);
            }
        }
    }

    /**
     * Begin the search for the best move
     */
    public static void inputGo() {
        //Search for best move
        PVSAlgorithm.rootSearch(-10000, 10000, Quetzal.searchDepth, PVSAlgorithm.FIXED);
        System.out.println("bestmove " + moveToAlgebra(PVSAlgorithm.bestMoves.getBestMove()));
    }

    /**
     * Convert a move from the move format used in the engine to algebraic notation
     *
     * @param move Move in internal notation
     * @return Move in algebraic notation
     */
    public static String moveToAlgebra(String move) {
        String append = "";
        int start = 0, end = 0;

        if (Character.isDigit(move.charAt(3))) { //'Regular' move
            start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
            end = (Character.getNumericValue(move.charAt(2)) * 8) + (Character.getNumericValue(move.charAt(3)));
        } else if (move.charAt(3) == 'P') { //Pawn promotion
            if (Character.isUpperCase(move.charAt(2))) {
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[1]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[0]);
            } else {
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[6]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[7]);
            }
            append = "" + Character.toLowerCase(move.charAt(2));
        } else if (move.charAt(3) == 'E') { //En passant
            if (move.charAt(2) == 'W') {
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[3]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[2]);
            } else {
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[4]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[5]);
            }
        }

        String returnMove = "";
        returnMove += (char) ('a' + (start % 8));
        returnMove += (char) ('8' - (start / 8));
        returnMove += (char) ('a' + (end % 8));
        returnMove += (char) ('8' - (end / 8));
        returnMove += append;

        return returnMove;
    }

    /**
     * Updates the global board given a series of algebraic moves
     *
     * @param input algebraic move
     * @param moves string of possible moves
     */
    public static void algebraToMove(String input, String moves) {
        Quetzal.WhiteToMove = !Quetzal.WhiteToMove;
        int start = 0, end = 0;
        int from = (input.charAt(0) - 'a') + (8 * ('8' - input.charAt(1)));
        int to = (input.charAt(2) - 'a') + (8 * ('8' - input.charAt(3)));

        for (int i = 0; i < moves.length(); i += 4) {
            if (Character.isDigit(moves.charAt(i + 3))) { //'Regular' move
                start = (Character.getNumericValue(moves.charAt(i)) * 8)
                        + (Character.getNumericValue(moves.charAt(i + 1)));
                end = (Character.getNumericValue(moves.charAt(i + 2)) * 8)
                        + (Character.getNumericValue(moves.charAt(i + 3)));
            } else if (moves.charAt(i + 3) == 'P') { //Pawn promotion
                if (Character.isUpperCase(moves.charAt(i + 2))) {
                    start = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i) - '0'] & RankMasks8[1]);
                    end = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i + 1) - '0'] & RankMasks8[0]);
                } else {
                    start = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i) - '0'] & RankMasks8[6]);
                    end = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i + 1) - '0'] & RankMasks8[7]);
                }
            } else if (moves.charAt(i + 3) == 'E') { //En passant
                if (moves.charAt(i + 2) == 'W') {
                    start = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i) - '0'] & RankMasks8[3]);
                    end = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i + 1) - '0'] & RankMasks8[2]);
                } else {
                    start = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i) - '0'] & RankMasks8[4]);
                    end = Long.numberOfTrailingZeros(FileMasks8[moves.charAt(i + 1) - '0'] & RankMasks8[5]);
                }
            }

            if ((start == from) && (end == to)) {
                if ((input.charAt(4) == ' ')
                        || (Character.toUpperCase(input.charAt(4)) == Character.toUpperCase(moves.charAt(i + 2)))) {
                    if (Character.isDigit(moves.charAt(i + 3))) { //'Regular' move
                        start = (Character.getNumericValue(moves.charAt(i)) * 8)
                                + (Character.getNumericValue(moves.charAt(i + 1)));
                        //Update castling rights before moving
                        if (((1L << start) & Quetzal.WK) != 0) {
                            Quetzal.CWK = false;
                            Quetzal.CWQ = false;
                        } else if (((1L << start) & Quetzal.BK) != 0) {
                            Quetzal.CBK = false;
                            Quetzal.CBQ = false;
                        } else if (((1L << start) & Quetzal.WR & (1L << 63)) != 0) {
                            Quetzal.CWK = false;
                        } else if (((1L << start) & Quetzal.WR & (1L << 56)) != 0) {
                            Quetzal.CWQ = false;
                        } else if (((1L << start) & Quetzal.BR & (1L << 7)) != 0) {
                            Quetzal.CBK = false;
                        } else if (((1L << start) & Quetzal.BR & 1L) != 0) {
                            Quetzal.CBQ = false;
                        }
                    }

                    char move1 = moves.charAt(i);
                    char move2 = moves.charAt(i + 1);
                    char move3 = moves.charAt(i + 2);
                    char move4 = moves.charAt(i + 3);

                    Quetzal.EP = Moves.makeMoveEP(Quetzal.WP | Quetzal.BP, moves.substring(i, i + 4));
                    Quetzal.WR = Moves.makeMoveCastle(Quetzal.WR,
                            Quetzal.WK | Quetzal.BK, moves.substring(i, i + 4), 'R');
                    Quetzal.BR = Moves.makeMoveCastle(Quetzal.BR,
                            Quetzal.WK | Quetzal.BK, moves.substring(i, i + 4), 'r');
                    Quetzal.WP = Moves.makeMove(Quetzal.WP, move1, move2, move3, move4, 'P');
                    Quetzal.WN = Moves.makeMove(Quetzal.WN, move1, move2, move3, move4, 'N');
                    Quetzal.WB = Moves.makeMove(Quetzal.WB, move1, move2, move3, move4, 'B');
                    Quetzal.WR = Moves.makeMove(Quetzal.WR, move1, move2, move3, move4, 'R');
                    Quetzal.WQ = Moves.makeMove(Quetzal.WQ, move1, move2, move3, move4, 'Q');
                    Quetzal.WK = Moves.makeMove(Quetzal.WK, move1, move2, move3, move4, 'K');
                    Quetzal.BP = Moves.makeMove(Quetzal.BP, move1, move2, move3, move4, 'p');
                    Quetzal.BN = Moves.makeMove(Quetzal.BN, move1, move2, move3, move4, 'n');
                    Quetzal.BB = Moves.makeMove(Quetzal.BB, move1, move2, move3, move4, 'b');
                    Quetzal.BR = Moves.makeMove(Quetzal.BR, move1, move2, move3, move4, 'r');
                    Quetzal.BQ = Moves.makeMove(Quetzal.BQ, move1, move2, move3, move4, 'q');
                    Quetzal.BK = Moves.makeMove(Quetzal.BK, move1, move2, move3, move4, 'k');
                    break;
                }
            }
        }
    }

    /**
     * Quit the engine
     */
    public static void inputQuit() {
        System.exit(0);
    }

    /**
     * Debugging method to print the board and Zobrist hash
     */
    public static void inputPrint() {
        //Debugging method
        BoardGenerator.drawArray(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP,
                Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK);
        System.out.print("Zobrist Hash: ");
        System.out.println(Zobrist.getZobristHash(Quetzal.globalBoard));
    }
}