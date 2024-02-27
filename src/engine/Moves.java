package engine;

import java.util.Arrays;

import static engine.internal.BitBoardConstants.*;

public class Moves {
    static long WHITE_PIECES; //Doesn't represent white king
    static long BLACK_PIECES; //Doesn't represent black king
    static long AVAILABLE_SQUARES_WHITE; //Represents where white can move
    static long AVAILABLE_SQUARES_BLACK; //Represents where black can move
    static long OCCUPIED;
    static long EMPTY;

    public static String generateMoves(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN,
                                       long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ,
                                       boolean CBK, boolean CBQ, boolean WhiteToMove) {
        String moves;
        if (WhiteToMove) {
            moves = possibleMovesWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ);
        } else {
            moves = possibleMovesBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CBK, CBQ);
        }

        return legalMoves(moves, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, WhiteToMove);
    }

    //General formula for moves along a mask = (o&m - 2s) ^ ((o&m)' - 2s')'
    //where o is OCCUPIED, m is mask, s is slider, and ' is the reverse operation
    public static long horizontalAndVerticalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesHorizontal = (OCCUPIED - (binarySlider << 1)
                ^ Long.reverse(Long.reverse(OCCUPIED) - (Long.reverse(binarySlider) << 1)));

        long possibilitiesVertical = ((OCCUPIED & FileMasks8[slider % 8]) - (binarySlider << 1))
                ^ Long.reverse(Long.reverse(OCCUPIED & FileMasks8[slider % 8])
                - (Long.reverse(binarySlider) << 1));

        return (possibilitiesHorizontal & RankMasks8[slider / 8])
                | (possibilitiesVertical & FileMasks8[slider % 8]);
    }

    public static long diagonalAndAntiDiagonalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesDiagonal =
                ((OCCUPIED & DiagonalMasks8[(slider / 8) + (slider % 8)]) - (binarySlider << 1))
                        ^ Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[(slider / 8)
                        + (slider % 8)]) - (Long.reverse(binarySlider) << 1));

        long possibilitiesAntiDiagonal =
                ((OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]) - (binarySlider << 1))
                        ^ Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)])
                        - (Long.reverse(binarySlider) << 1));

        return (possibilitiesDiagonal & DiagonalMasks8[(slider / 8) + (slider % 8)])
                | (possibilitiesAntiDiagonal & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]);
    }

    public static long makeMove(long board, char move1, char move2, char move3, char move4, char type) {
        if (Character.isDigit(move4)) { //'Regular' move
            int start = ((move1 - '0') * 8) + (move2 - '0');
            int end = ((move3 - '0') * 8) + (move4 - '0');

            if (((board >>> start) & 1) == 1) { //Move piece on its bitboard
                board &= ~(1L << start);
                board |= (1L << end);
            } else {
                board &= ~(1L << end); //Remove captured piece if it exists
            }
        } else if (move4 == 'P') { //Pawn promotion
            int start, end;

            if (Character.isUpperCase(move3)) { //White promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[1]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[0]);
            } else { //Black promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[6]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[7]);
            }
            if (type == move3) { //Add new piece if promotion matches type and remove pawn
                board |= (1L << end);
            } else { //Remove captured piece if it exists
                board &= ~(1L << start);
                board &= ~(1L << end);
            }
        } else if (move4 == 'E') { //En passant
            int start, end;

            if (move3 == 'W') {
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[3]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[2]);
                board &= ~(FileMasks8[move2 - '0'] & RankMasks8[3]);
            } else {
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[4]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[5]);
                board &= ~(FileMasks8[move2 - '0'] & RankMasks8[4]);
            }
            if (((board >>> start) & 1) == 1) {
                board &= ~(1L << start);
                board |= (1L << end);
            }
        }

        return board;
    }

    public static long makeMoveCastle(long rookBoard, long kingBoard, String move, char type) {
        int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
        //Check if king castled and updated rook board accordingly (kingBoard is updated in makeMove method)
        if ((((kingBoard >>> start) & 1) == 1) && (("0402".equals(move)) || ("0406".equals(move))
                || ("7472".equals(move)) || ("7476".equals(move)))) {//'regular' move
            if (type == 'R') {
                switch (move) {
                    case "7472":
                        rookBoard &= ~(1L << CASTLE_ROOKS[1]);
                        rookBoard |= (1L << (CASTLE_ROOKS[1] + 3));
                        break;
                    case "7476":
                        rookBoard &= ~(1L << CASTLE_ROOKS[0]);
                        rookBoard |= (1L << (CASTLE_ROOKS[0] - 2));
                        break;
                }
            } else {
                switch (move) {
                    case "0402":
                        rookBoard &= ~(1L << CASTLE_ROOKS[3]);
                        rookBoard |= (1L << (CASTLE_ROOKS[3] + 3));
                        break;
                    case "0406":
                        rookBoard &= ~(1L << CASTLE_ROOKS[2]);
                        rookBoard |= (1L << (CASTLE_ROOKS[2] - 2));
                        break;
                }
            }
        }
        return rookBoard;
    }

    public static long makeMoveEP(long board, String move) {
        //Masks EP file if double pawn push
        if (Character.isDigit(move.charAt(3))) {
            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0) - move.charAt(2)) == 2) && (((board >>> start) & 1) == 1)) { //Double pawn push
                return FileMasks8[move.charAt(1) - '0'];
            }
        }
        return 0;
    }

    public static String possibleMovesWhite(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN,
                                            long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ) {
        AVAILABLE_SQUARES_WHITE = ~(WP | WN | WB | WR | WQ | WK | BK); //Added BK to avoid illegal capture
        BLACK_PIECES = BP | BN | BB | BR | BQ; //Omitted BK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);
        OCCUPIED = ~(EMPTY);

        return possibleWP(WP, BP, EP)
                + possibleN(AVAILABLE_SQUARES_WHITE, WN)
                + possibleB(AVAILABLE_SQUARES_WHITE, WB)
                + possibleR(AVAILABLE_SQUARES_WHITE, WR)
                + possibleQ(AVAILABLE_SQUARES_WHITE, WQ)
                + possibleK(AVAILABLE_SQUARES_WHITE, WK)
                + possibleCW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, CWK, CWQ);
    }

    public static String possibleMovesBlack(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN,
                                            long BB, long BR, long BQ, long BK, long EP, boolean CBK, boolean CBQ) {
        AVAILABLE_SQUARES_BLACK = ~(BP | BN | BB | BR | BQ | BK | WK); //Added WK to avoid illegal capture
        WHITE_PIECES = WP | WN | WB | WR | WQ; //Omitted WK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);
        OCCUPIED = ~(EMPTY);

        return possibleBP(BP, WP, EP)
                + possibleN(AVAILABLE_SQUARES_BLACK, BN)
                + possibleB(AVAILABLE_SQUARES_BLACK, BB)
                + possibleR(AVAILABLE_SQUARES_BLACK, BR)
                + possibleQ(AVAILABLE_SQUARES_BLACK, BQ)
                + possibleK(AVAILABLE_SQUARES_BLACK, BK)
                + possibleCB(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, CBK, CBQ);
    }

    public static String possibleWP(long WP, long BP, long EP) {
        StringBuilder list = new StringBuilder();

        //Basic captures and pawn pushes: y1,x1,y2,x2
        long PAWN_MOVES = (WP >> 7) & BLACK_PIECES & ~RANK_8 & ~FILE_A; //Capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & ~RANK_8 & ~FILE_H; //Capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & ~RANK_8; //Single push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 16) & EMPTY & (EMPTY >> 8) & RANK_4; //Double push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //Promoting captures and pawn pushes: x1,x2,Promotion Type,"P"
        PAWN_MOVES = (WP >> 7) & BLACK_PIECES & RANK_8 & ~FILE_A; //Pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("QP").append(index % 8 - 1)
                    .append(index % 8).append("RP").append(index % 8 - 1).append(index % 8)
                    .append("BP").append(index % 8 - 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & RANK_8 & ~FILE_H; //Pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("QP").append(index % 8 + 1)
                    .append(index % 8).append("RP").append(index % 8 + 1).append(index % 8)
                    .append("BP").append(index % 8 + 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & RANK_8; //Pawn promotion by push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("QP").append(index % 8)
                    .append(index % 8).append("RP").append(index % 8).append(index % 8)
                    .append("BP").append(index % 8).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //En passant: x1,x2,"WE"
        possibility = (WP << 1) & BP & RANK_5 & ~FILE_A & EP; //En passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("WE");
        }
        possibility = (WP >> 1) & BP & RANK_5 & ~FILE_H & EP; //En passant left
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("WE");
        }

        return list.toString();
    }

    public static String possibleBP(long BP, long WP, long EP) {
        StringBuilder list = new StringBuilder();

        //Basic captures and pawn pushes: y1,x1,y2,x2
        long PAWN_MOVES = (BP << 7) & WHITE_PIECES & ~RANK_1 & ~FILE_H; //Capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & ~RANK_1 & ~FILE_A; //Capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & ~RANK_1; //Single push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 16) & EMPTY & (EMPTY << 8) & RANK_5; //Double push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //Promoting captures and pawn pushes: x1,x2,Promotion Type,"P"
        PAWN_MOVES = (BP << 7) & WHITE_PIECES & RANK_1 & ~FILE_H; //Pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("qP").append(index % 8 + 1)
                    .append(index % 8).append("rP").append(index % 8 + 1).append(index % 8)
                    .append("bP").append(index % 8 + 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & RANK_1 & ~FILE_A; //Pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("qP").append(index % 8 - 1)
                    .append(index % 8).append("rP").append(index % 8 - 1).append(index % 8)
                    .append("bP").append(index % 8 - 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & RANK_1; //Pawn promotion by push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("qP").append(index % 8)
                    .append(index % 8).append("rP").append(index % 8).append(index % 8)
                    .append("bP").append(index % 8).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //En passant: x1,x2,"BE"
        possibility = (BP >> 1) & WP & RANK_4 & ~FILE_H & EP; //En passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("BE");
        }
        possibility = (BP << 1) & WP & RANK_4 & ~FILE_A & EP; //En passant left
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("BE");
        }

        return list.toString();
    }

    public static String possibleN(long AVAILABLE_SQUARES, long N) {
        StringBuilder list = new StringBuilder();
        long i = N & -N;
        long possibility;

        //Loop through each knight
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);

            //KNIGHT_SPAN is centered at 18th bit, so we must shift it accordingly
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }

            //Remove moves that wrap around the board and self-captures
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH & AVAILABLE_SQUARES;
            } else {
                possibility &= ~FILE_AB & AVAILABLE_SQUARES;
            }

            //Loop through legal moves and captures: y1,x1,y2,x2
            long j = possibility & -possibility;
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            N &= ~i;
            i = N & -N;
        }
        return list.toString();
    }

    public static String possibleB(long AVAILABLE_SQUARES, long B) {
        StringBuilder list = new StringBuilder();
        long i = B & -B;
        long possibility;

        //Loop through each bishop
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given bishop: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            B &= ~i;
            i = B & -B;
        }
        return list.toString();
    }

    public static String possibleR(long AVAILABLE_SQUARES, long R) {
        StringBuilder list = new StringBuilder();
        long i = R & -R;
        long possibility;

        //Loop through each rook
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given rook: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            R &= ~i;
            i = R & -R;
        }
        return list.toString();
    }

    public static String possibleQ(long AVAILABLE_SQUARES, long Q) {
        StringBuilder list = new StringBuilder();
        long i = Q & -Q;
        long possibility;

        //Loop through each queen
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = (diagonalAndAntiDiagonalMoves(iLocation) | horizontalAndVerticalMoves(iLocation))
                    & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given queen: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            Q &= ~i;
            i = Q & -Q;
        }
        return list.toString();
    }

    public static String possibleK(long AVAILABLE_SQUARES, long K) {
        StringBuilder list = new StringBuilder();
        long possibility;
        int iLocation = Long.numberOfTrailingZeros(K);

        //KING_SPAN is centered at 9th bit, so we must shift it accordingly
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }

        //Remove moves that wrap around the board and self-captures
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH & AVAILABLE_SQUARES;
        } else {
            possibility &= ~FILE_AB & AVAILABLE_SQUARES;
        }

        //Loop through legal moves and captures for given king: y1,x1,y2,x2
        long j = possibility & -possibility;
        while (j != 0) {
            int index = Long.numberOfTrailingZeros(j);
            list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
            possibility &= ~j;
            j = possibility & -possibility;
        }

        return list.toString();
    }

    public static String possibleCW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN,
                                    long BB, long BR, long BQ, long BK, boolean CWK, boolean CWQ) {
        String list = "";
        long UNSAFE = unsafeForWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);

        //Check if castling path is clear and king is not in check
        if ((UNSAFE & WK) == 0) {
            if (CWK && (((1L << CASTLE_ROOKS[0]) & WR) != 0)) {
                if (((OCCUPIED | UNSAFE) & ((1L << 61) | (1L << 62))) == 0) {
                    list += "7476";
                }
            }
            if (CWQ && (((1L << CASTLE_ROOKS[1]) & WR) != 0)) {
                if (((OCCUPIED | (UNSAFE & ~(1L << 57))) & ((1L << 57) | (1L << 58) | (1L << 59))) == 0) {
                    list += "7472";
                }
            }
        }
        return list;
    }

    public static String possibleCB(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN,
                                    long BB, long BR, long BQ, long BK, boolean CBK, boolean CBQ) {
        String list = "";
        long UNSAFE = unsafeForBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);

        //Check if castling path is clear and king is not in check
        if ((UNSAFE & BK) == 0) {
            if (CBK && (((1L << CASTLE_ROOKS[2]) & BR) != 0)) {
                if (((OCCUPIED | UNSAFE) & ((1L << 5) | (1L << 6))) == 0) {
                    list += "0406";
                }
            }
            if (CBQ && (((1L << CASTLE_ROOKS[3]) & BR) != 0)) {
                if (((OCCUPIED | (UNSAFE & ~(1L << 1))) & ((1L << 1) | (1L << 2) | (1L << 3))) == 0) {
                    list += "0402";
                }
            }
        }
        return list;
    }

    public static long unsafeForBlack(long WP, long WN, long WB, long WR, long WQ, long WK,
                                      long BP, long BN, long BB, long BR, long BQ, long BK) {
        long unsafe;
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        long possibility;

        //Pawn
        unsafe = ((WP >>> 7) & ~FILE_A); //Pawn capture right
        unsafe |= ((WP >>> 9) & ~FILE_H); //Pawn capture left

        //Knight
        long i = WN & -WN;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH;
            } else {
                possibility &= ~FILE_AB;
            }
            unsafe |= possibility;
            WN &= ~i;
            i = WN & -WN;
        }

        //Bishop/queen
        long QB = WQ | WB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //Rook/queen
        long QR = WQ | WR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //King
        int iLocation = Long.numberOfTrailingZeros(WK);
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH;
        } else {
            possibility &= ~FILE_AB;
        }
        unsafe |= possibility;

        return unsafe;
    }

    public static long unsafeForWhite(long WP, long WN, long WB, long WR, long WQ, long WK,
                                      long BP, long BN, long BB, long BR, long BQ, long BK) {
        long unsafe;
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        long possibility;

        //Pawn
        unsafe = ((BP << 7) & ~FILE_H); //Pawn capture right
        unsafe |= ((BP << 9) & ~FILE_A); //Pawn capture left

        //Knight
        long i = BN & -BN;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH;
            } else {
                possibility &= ~FILE_AB;
            }
            unsafe |= possibility;
            BN &= ~i;
            i = BN & -BN;
        }

        //Bishop/queen
        long QB = BQ | BB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //Rook/queen
        long QR = BQ | BR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //King
        int iLocation = Long.numberOfTrailingZeros(BK);
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH;
        } else {
            possibility &= ~FILE_AB;
        }
        unsafe |= possibility;

        return unsafe;
    }

    public static String legalMoves(String moves, long WP, long WN, long WB, long WR, long WQ, long WK,
                                    long BP, long BN, long BB, long BR, long BQ, long BK, boolean WhiteToMove) {
        StringBuilder legalMoves = new StringBuilder();

        //Primitives instead of strings for performance
        for (int i = 0; i < moves.length(); i += 4) {
            char move1 = moves.charAt(i);
            char move2 = moves.charAt(i + 1);
            char move3 = moves.charAt(i + 2);
            char move4 = moves.charAt(i + 3);

            long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'),
                    WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                    WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'),
                    WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                    WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'),
                    WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                    BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'),
                    BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                    BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'),
                    BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                    BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'),
                    BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k');
            WRt = Moves.makeMoveCastle(WRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
            BRt = Moves.makeMoveCastle(BRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
            if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0
                    && WhiteToMove)
                    || ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0
                    && !WhiteToMove)) {
                legalMoves.append(move1).append(move2).append(move3).append(move4);
            }
        }
        return legalMoves.toString();
    }

    public static void drawBitboard(long bitBoard) {
        //Debug method to print bitboards
        //TODO remove later

        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = "";
        }
        for (int i = 0; i < 64; i++) {
            if (((bitBoard >>> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";
            }
            if ("".equals(chessBoard[i / 8][i % 8])) {
                chessBoard[i / 8][i % 8] = " ";
            }
        }
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }
}
