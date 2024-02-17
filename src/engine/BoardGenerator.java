package engine;

/*
 * Colour = LIGHT/dark
 * Pawn = P/p
 * Knight = N/n
 * Bishop = B/b
 * Rook = R/r
 * Queen = Q/q
 * King = K/k
 */

import java.util.Arrays;

public class BoardGenerator {
    public static void initiateStandardBoard() {
        //Long integers store 64 bits, which is enough to represent a piece on each
        //square of the board. This is known as a bitboard which is a significantly
        //more efficient way to represent the board than a 2D array of strings
        //
        // We need 12 bitboards to represent each type of piece
        long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BN = 0L, BB = 0L, BR = 0L, BQ = 0L, BK = 0L;

        //For simplicity, we can initialize the board with a 2D array of strings
        //Note: Fischer Random and FEN have unique initialization methods
        String[][] board = new String[0][];
        if (UserInterface.humanIsWhite == 1) {
            board = new String[][]{
                    {"r", "n", "b", "q", "k", "b", "n", "r"},
                    {"p", "p", "p", "p", "p", "p", "p", "p"},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {"P", "P", "P", "P", "P", "P", "P", "P"},
                    {"R", "N", "B", "Q", "K", "B", "N", "R"}
            };
        } else {
            board = new String[][]{
                    {"R", "N", "B", "K", "Q", "B", "N", "R"},
                    {"P", "P", "P", "P", "P", "P", "P", "P"},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " "},
                    {"p", "p", "p", "p", "p", "p", "p", "p"},
                    {"r", "n", "b", "k", "q", "b", "n", "r"},
            };
        }

        arrayToBitboards(board, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
    }

    //TODO add chess 960 functionality
    public static void initiateFischerRandom() {
        //
    }

    //TODO add FEN functionality

    public static void importFEN(String input) {
        //
    }

    public static void arrayToBitboards(String[][] board, long WP, long WN, long WB, long WR, long WQ,
                                        long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        //To avoid interpreting decimal numbers we can use a string of 64 0s and 1s
        //to represent a binary number
        String emptyBinary = "0".repeat(64);

        //Iterate through the 2D array of strings to initialize the bitboards
        for (int i = 0; i < 64; i++) {
            String binary = emptyBinary;
            binary = binary.substring(i + 1) + "1" + binary.substring(0, i);
            switch (board[i / 8][i % 8]) {
                case "P" -> WP += stringToBitboard(binary);
                case "N" -> WN += stringToBitboard(binary);
                case "B" -> WB += stringToBitboard(binary);
                case "R" -> WR += stringToBitboard(binary);
                case "Q" -> WQ += stringToBitboard(binary);
                case "K" -> WK += stringToBitboard(binary);
                case "p" -> BP += stringToBitboard(binary);
                case "n" -> BN += stringToBitboard(binary);
                case "b" -> BB += stringToBitboard(binary);
                case "r" -> BR += stringToBitboard(binary);
                case "q" -> BQ += stringToBitboard(binary);
                case "k" -> BK += stringToBitboard(binary);
            }
        }
        drawArray(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
        UserInterface.WP = WP;
        UserInterface.WN = WN;
        UserInterface.WB = WB;
        UserInterface.WR = WR;
        UserInterface.WQ = WQ;
        UserInterface.WK = WK;
        UserInterface.BP = BP;
        UserInterface.BN = BN;
        UserInterface.BB = BB;
        UserInterface.BR = BR;
        UserInterface.BQ = BQ;
        UserInterface.BK = BK;
    }

    public static long stringToBitboard(String binary) {
        //Java represents signed integers using two's complement, so we
        //account for this by checking if the first bit is 0 to indicate a
        //positive number, or 1 to indicate a negative number
        //
        //I.e. 1111 = -1 in two's complement but 1111 = 15 in unsigned binary
        if (binary.charAt(0) == '0') {
            return Long.parseLong(binary, 2);
        } else {
            return Long.parseLong("1" + binary.substring(2), 2) * 2;
        }
    }

    public static void drawArray(long WP, long WN, long WB, long WR, long WQ, long WK,
                                 long BP, long BN, long BB, long BR, long BQ, long BK) {
        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = " ";
        }
        for (int i = 0; i < 64; i++) {
            if (((WP >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";
            }
            if (((WN >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "N";
            }
            if (((WB >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "B";
            }
            if (((WR >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "R";
            }
            if (((WQ >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "Q";
            }
            if (((WK >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "K";
            }
            if (((BP >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "p";
            }
            if (((BN >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "n";
            }
            if (((BB >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "b";
            }
            if (((BR >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "r";
            }
            if (((BQ >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "q";
            }
            if (((BK >> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "k";
            }
        }
        for (String[] row : chessBoard) {
            System.out.println(Arrays.toString(row));
        }
    }
}
