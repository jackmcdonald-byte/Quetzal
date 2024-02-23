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
        String[][] board;

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

        arrayToBitboards(board, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
    }

    //TODO add chess 960 functionality
    public static void initiateFischerRandom() {
        //
    }

    //TODO add FEN functionality

    public static void importFEN(String fenString) {
        //not chess960 compatible
        Quetzal.WP = 0;
        Quetzal.WN = 0;
        Quetzal.WB = 0;
        Quetzal.WR = 0;
        Quetzal.WQ = 0;
        Quetzal.WK = 0;
        Quetzal.BP = 0;
        Quetzal.BN = 0;
        Quetzal.BB = 0;
        Quetzal.BR = 0;
        Quetzal.BQ = 0;
        Quetzal.BK = 0;
        Quetzal.CWK = false;
        Quetzal.CWQ = false;
        Quetzal.CBK = false;
        Quetzal.CBQ = false;

        int charIndex = 0;
        int boardIndex = 0;

        while (fenString.charAt(charIndex) != ' ') {
            switch (fenString.charAt(charIndex++)) {
                case 'P':
                    Quetzal.WP |= (1L << boardIndex++);
                    break;
                case 'p':
                    Quetzal.BP |= (1L << boardIndex++);
                    break;
                case 'N':
                    Quetzal.WN |= (1L << boardIndex++);
                    break;
                case 'n':
                    Quetzal.BN |= (1L << boardIndex++);
                    break;
                case 'B':
                    Quetzal.WB |= (1L << boardIndex++);
                    break;
                case 'b':
                    Quetzal.BB |= (1L << boardIndex++);
                    break;
                case 'R':
                    Quetzal.WR |= (1L << boardIndex++);
                    break;
                case 'r':
                    Quetzal.BR |= (1L << boardIndex++);
                    break;
                case 'Q':
                    Quetzal.WQ |= (1L << boardIndex++);
                    break;
                case 'q':
                    Quetzal.BQ |= (1L << boardIndex++);
                    break;
                case 'K':
                    Quetzal.WK |= (1L << boardIndex++);
                    break;
                case 'k':
                    Quetzal.BK |= (1L << boardIndex++);
                    break;
                case '/':
                    break;
                case '1':
                    boardIndex++;
                    break;
                case '2':
                    boardIndex += 2;
                    break;
                case '3':
                    boardIndex += 3;
                    break;
                case '4':
                    boardIndex += 4;
                    break;
                case '5':
                    boardIndex += 5;
                    break;
                case '6':
                    boardIndex += 6;
                    break;
                case '7':
                    boardIndex += 7;
                    break;
                case '8':
                    boardIndex += 8;
                    break;
                default:
                    break;
            }
        }
        Quetzal.WhiteToMove = (fenString.charAt(++charIndex) == 'w');
        charIndex += 2;
        while (fenString.charAt(charIndex) != ' ') {
            switch (fenString.charAt(charIndex++)) {
                case '-':
                    break;
                case 'K':
                    Quetzal.CWK = true;
                    break;
                case 'Q':
                    Quetzal.CWQ = true;
                    break;
                case 'k':
                    Quetzal.CBK = true;
                    break;
                case 'q':
                    Quetzal.CBQ = true;
                    break;
                default:
                    break;
            }
        }
        if (fenString.charAt(++charIndex) != '-') {
            Quetzal.EP = Moves.FileMasks8[fenString.charAt(charIndex++) - 'a'];
        }
        //the rest of the fenString is not yet utilized
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
//        drawArray(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
        Quetzal.WP = WP;
        Quetzal.WN = WN;
        Quetzal.WB = WB;
        Quetzal.WR = WR;
        Quetzal.WQ = WQ;
        Quetzal.WK = WK;
        Quetzal.BP = BP;
        Quetzal.BN = BN;
        Quetzal.BB = BB;
        Quetzal.BR = BR;
        Quetzal.BQ = BQ;
        Quetzal.BK = BK;
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
