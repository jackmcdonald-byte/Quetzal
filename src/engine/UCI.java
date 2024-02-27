package engine;

import java.util.*;

public class UCI {
    static String ENGINENAME = "Quetzal v1.0";

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
            }
        }
    }

    public static void inputUCI() {
        System.out.println("id name " + ENGINENAME);
        System.out.println("id author Jack McDonald");
        //options go here
        System.out.println("uciok");
    }

    public static void inputSetOption(String inputString) {
        //set options
    }

    public static void inputIsReady() {
        System.out.println("readyok");
    }

    public static void inputUCINewGame() {
        //add code here
    }

    public static void inputPosition(String input) {
        input = input.substring(9).concat(" ");
        if (input.contains("startpos ")) {
            input = input.substring(9);
            BoardGenerator.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            Quetzal.WhiteToMove = true;
        } else if (input.contains("fen")) {
            input = input.substring(4);
            BoardGenerator.importFEN(input);
        }
        if (input.contains("moves")) {
            input = input.substring(input.indexOf("moves") + 6);
            while (!input.isEmpty()) {
                String moves;
                if (Quetzal.WhiteToMove) {
                    moves = Moves.possibleMovesWhite(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ);
                } else {
                    moves = Moves.possibleMovesBlack(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ);
                }
                algebraToMove(input, moves);
                input = input.substring(input.indexOf(' ') + 1);
            }
        }
    }

    public static void inputGo() {
        PVSAlgorithm.principleVariationSearch(-1000, 1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        System.out.println("bestmove " + moveToAlgebra(PVSAlgorithm.bestMove[0]));
    }

    public static String moveToAlgebra(String move) {
        String append = "";
        int start = 0, end = 0;
        if (Character.isDigit(move.charAt(3))) {//'regular' move
            start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
            end = (Character.getNumericValue(move.charAt(2)) * 8) + (Character.getNumericValue(move.charAt(3)));
        } else if (move.charAt(3) == 'P') {//pawn promotion
            if (Character.isUpperCase(move.charAt(2))) {
                start = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(0) - '0'] & Moves.RankMasks8[1]);
                end = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(1) - '0'] & Moves.RankMasks8[0]);
            } else {
                start = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(0) - '0'] & Moves.RankMasks8[6]);
                end = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(1) - '0'] & Moves.RankMasks8[7]);
            }
            append = "" + Character.toLowerCase(move.charAt(2));
        } else if (move.charAt(3) == 'E') {//en passant
            if (move.charAt(2) == 'W') {
                start = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(0) - '0'] & Moves.RankMasks8[3]);
                end = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(1) - '0'] & Moves.RankMasks8[2]);
            } else {
                start = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(0) - '0'] & Moves.RankMasks8[4]);
                end = Long.numberOfTrailingZeros(Moves.FileMasks8[move.charAt(1) - '0'] & Moves.RankMasks8[5]);
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

    public static void algebraToMove(String input, String moves) {
        Quetzal.WhiteToMove = !Quetzal.WhiteToMove;
        int start = 0, end = 0;
        int from = (input.charAt(0) - 'a') + (8 * ('8' - input.charAt(1)));
        int to = (input.charAt(2) - 'a') + (8 * ('8' - input.charAt(3)));
        for (int i = 0; i < moves.length(); i += 4) {
            if (Character.isDigit(moves.charAt(i + 3))) {//'regular' move
                start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
                end = (Character.getNumericValue(moves.charAt(i + 2)) * 8) + (Character.getNumericValue(moves.charAt(i + 3)));
            } else if (moves.charAt(i + 3) == 'P') {//pawn promotion
                if (Character.isUpperCase(moves.charAt(i + 2))) {
                    start = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i) - '0'] & Moves.RankMasks8[1]);
                    end = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i + 1) - '0'] & Moves.RankMasks8[0]);
                } else {
                    start = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i) - '0'] & Moves.RankMasks8[6]);
                    end = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i + 1) - '0'] & Moves.RankMasks8[7]);
                }
            } else if (moves.charAt(i + 3) == 'E') {//en passant
                if (moves.charAt(i + 2) == 'W') {
                    start = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i) - '0'] & Moves.RankMasks8[3]);
                    end = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i + 1) - '0'] & Moves.RankMasks8[2]);
                } else {
                    start = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i) - '0'] & Moves.RankMasks8[4]);
                    end = Long.numberOfTrailingZeros(Moves.FileMasks8[moves.charAt(i + 1) - '0'] & Moves.RankMasks8[5]);
                }
            }
            if ((start == from) && (end == to)) {
                if ((input.charAt(4) == ' ') || (Character.toUpperCase(input.charAt(4)) == Character.toUpperCase(moves.charAt(i + 2)))) {
                    if (Character.isDigit(moves.charAt(i + 3))) {//'regular' move
                        start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
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
                    Quetzal.WR=Moves.makeMoveCastle(Quetzal.WR, Quetzal.WK|Quetzal.BK, moves.substring(i,i+4), 'R');
                    Quetzal.BR=Moves.makeMoveCastle(Quetzal.BR, Quetzal.WK|Quetzal.BK, moves.substring(i,i+4), 'r');
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

    public static void inputQuit() {
        System.exit(0);
    }

    public static void inputPrint() {
        BoardGenerator.drawArray(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK);
        System.out.print("Zobrist Hash: ");
        System.out.println(Zobrist.getZobristHash(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove));
    }
}