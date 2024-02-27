package engine;

import java.util.Arrays;

public class Moves {
    public static final long FILE_A = 72340172838076673L;
    public static final long FILE_H = -9187201950435737472L;
    public static final long FILE_AB = 217020518514230019L;
    public static final long FILE_GH = -4557430888798830400L;
    public static final long RANK_1 = -72057594037927936L;
    public static final long RANK_4 = 1095216660480L;
    public static final long RANK_5 = 4278190080L;
    public static final long RANK_8 = 255L;
    public static final long CENTRE = 103481868288L;
    public static final long EXTENDED_CENTRE = 66229406269440L;
    public static final long KING_SIDE = -1085102592571150096L;
    public static final long QUEEN_SIDE = 1085102592571150095L;
    public static final long KING_SPAN = 460039L; //King's span on g2
    public static final long KNIGHT_SPAN = 43234889994L; //knight's span on f3
    public static final long[] CASTLE_ROOKS = {63, 56, 7, 0};
    public static final long[] RankMasks8 = {0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L,
            0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L}; //from rank1 to rank8
    public static final long[] FileMasks8 = {0x101010101010101L, 0x202020202020202L, 0x404040404040404L,
            0x808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L,
            0x8080808080808080L}; //from fileA to FileH
    public static final long[] DiagonalMasks8 = {0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L,
            0x10204081020L, 0x1020408102040L, 0x102040810204080L, 0x204081020408000L,
            0x408102040800000L, 0x810204080000000L, 0x1020408000000000L, 0x2040800000000000L,
            0x4080000000000000L, 0x8000000000000000L}; //from top left to bottom right
    public static final long[] AntiDiagonalMasks8 = {0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L,
            0x804020100804L, 0x80402010080402L, 0x8040201008040201L, 0x4020100804020100L,
            0x2010080402010000L, 0x1008040201000000L, 0x804020100000000L, 0x402010000000000L,
            0x201000000000000L, 0x100000000000000L}; //from top right to bottom left
    static long WHITE_PIECES; //Doesn't represent white king
    static long BLACK_PIECES; //Doesn't represent black king
    static long AVAILABLE_SQUARES_WHITE; //Represents where white can move
    static long AVAILABLE_SQUARES_BLACK; //Represents where black can move
    static long OCCUPIED;
    static long EMPTY;

    public static String generateMoves(long WP, long WN, long WB, long WR, long WQ, long WK,
                                       long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK,
                                       boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove) {
        String moves;
        if (WhiteToMove) {
            moves = possibleMovesWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
        } else {
            moves = possibleMovesBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
        }

        return legalMoves(moves, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ, WhiteToMove);
    }

    //general formula for moves along a mask = (o&m - 2s) ^ ((o&m)' - 2s')'
    //where o is OCCUPIED, m is mask, s is slider, and ' is reverse
    public static long horizontalAndVerticalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesHorizontal = (OCCUPIED - 2 * binarySlider) ^
                Long.reverse(Long.reverse(OCCUPIED) - 2 * Long.reverse(binarySlider));

        long possibilitiesVertical = ((OCCUPIED & FileMasks8[slider % 8]) - (2 * binarySlider)) ^
                Long.reverse(Long.reverse(OCCUPIED & FileMasks8[slider % 8]) - (2 * Long.reverse(binarySlider)));

        return (possibilitiesHorizontal & RankMasks8[slider / 8]) |
                (possibilitiesVertical & FileMasks8[slider % 8]);
    }

    public static long diagonalAndAntiDiagonalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesDiagonal = ((OCCUPIED & DiagonalMasks8[(slider / 8) + (slider % 8)]) -
                (2 * binarySlider)) ^ Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[(slider / 8) +
                (slider % 8)]) - (2 * Long.reverse(binarySlider)));

        long possibilitiesAntiDiagonal = ((OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]) -
                (2 * binarySlider)) ^ Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 -
                (slider % 8)]) - (2 * Long.reverse(binarySlider)));

        return (possibilitiesDiagonal & DiagonalMasks8[(slider / 8) + (slider % 8)]) | (possibilitiesAntiDiagonal &
                AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]);
    }


//    long WPt=Moves.makeMove(WP, moves.substring(i,i+4), 'P'), WNt=Moves.makeMove(WN, moves.substring(i,i+4), 'N'),
//    WBt=Moves.makeMove(WB, moves.substring(i,i+4), 'B'), WRt=Moves.makeMove(WR, moves.substring(i,i+4), 'R'),
//    WQt=Moves.makeMove(WQ, moves.substring(i,i+4), 'Q'), WKt=Moves.makeMove(WK, moves.substring(i,i+4), 'K'),
//    BPt=Moves.makeMove(BP, moves.substring(i,i+4), 'p'), BNt=Moves.makeMove(BN, moves.substring(i,i+4), 'n'),
//    BBt=Moves.makeMove(BB, moves.substring(i,i+4), 'b'), BRt=Moves.makeMove(BR, moves.substring(i,i+4), 'r'),
//    BQt=Moves.makeMove(BQ, moves.substring(i,i+4), 'q'), BKt=Moves.makeMove(BK, moves.substring(i,i+4), 'k'),
//    EPt=Moves.makeMoveEP(moves.substring(i,i+4));

    public static long makeMove(long board, char move1, char move2, char move3, char move4, char type) {
        if (Character.isDigit(move4)) {//'regular' move
            int start = ((move1 - '0') * 8) + (move2 - '0');
            int end = ((move3 - '0') * 8) + (move4 - '0');

            if (((board >>> start) & 1) == 1) { //move piece on its bitboard
                board &= ~(1L << start);
                board |= (1L << end);
            } else {
                board &= ~(1L << end); //remove captured piece if it exists
            }
        } else if (move4 == 'P') {//pawn promotion
            int start, end;

            if (Character.isUpperCase(move3)) { //white promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[1]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[0]);
            } else { //black promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[6]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[7]);
            }
            if (type == move3) { //add new piece if promotion matches type and remove pawn
                board |= (1L << end);
            } else { //remove captured piece if it exists
                board &= ~(1L << start);
                board &= ~(1L << end);
            }
        } else if (move4 == 'E') {//en passant
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
        if ((((kingBoard >>> start) & 1) == 1) && (("0402".equals(move)) || ("0406".equals(move)) || ("7472".equals(move)) || ("7476".equals(move)))) {//'regular' move
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
        if (Character.isDigit(move.charAt(3))) {
            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));

            if ((Math.abs(move.charAt(0) - move.charAt(2)) == 2) && (((board >>> start) & 1) == 1)) {//pawn double push
                return FileMasks8[move.charAt(1) - '0'];
            }
        }
        return 0;
    }

    public static String possibleMovesWhite(long WP, long WN, long WB, long WR, long WQ, long WK,
                                            long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK,
                                            boolean CWQ, boolean CBK, boolean CBQ) {
        AVAILABLE_SQUARES_WHITE = ~(WP | WN | WB | WR | WQ | WK | BK); //added BK to avoid illegal capture
        BLACK_PIECES = BP | BN | BB | BR | BQ;//omitted BK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);
        OCCUPIED = ~(EMPTY);

        return possibleWP(WP, BP, EP) +
                possibleN(AVAILABLE_SQUARES_WHITE, WN) +
                possibleB(AVAILABLE_SQUARES_WHITE, WB) +
                possibleR(AVAILABLE_SQUARES_WHITE, WR) +
                possibleQ(AVAILABLE_SQUARES_WHITE, WQ) +
                possibleK(AVAILABLE_SQUARES_WHITE, WK) +
                possibleCW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, CWK, CWQ);
    }

    public static String possibleMovesBlack(long WP, long WN, long WB, long WR, long WQ, long WK,
                                            long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK,
                                            boolean CWQ, boolean CBK, boolean CBQ) {
        AVAILABLE_SQUARES_BLACK = ~(BP | BN | BB | BR | BQ | BK | WK); //added WK to avoid illegal capture
        WHITE_PIECES = WP | WN | WB | WR | WQ; //omitted WK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);
        OCCUPIED = ~(EMPTY);

        return possibleBP(BP, WP, EP) +
                possibleN(AVAILABLE_SQUARES_BLACK, BN) +
                possibleB(AVAILABLE_SQUARES_BLACK, BB) +
                possibleR(AVAILABLE_SQUARES_BLACK, BR) +
                possibleQ(AVAILABLE_SQUARES_BLACK, BQ) +
                possibleK(AVAILABLE_SQUARES_BLACK, BK) +
                possibleCB(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, CBK, CBQ);
    }

    //TODO return later and attempt to optimize by trying different loops
    public static String possibleWP(long WP, long BP, long EP) {
        StringBuilder list = new StringBuilder();

        //basic captures and pawn pushes: rank1,file1,rank2,file2
        long PAWN_MOVES = (WP >> 7) & BLACK_PIECES & ~RANK_8 & ~FILE_A;//capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & ~RANK_8 & ~FILE_H;//capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & ~RANK_8;//move 1 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 16) & EMPTY & (EMPTY >> 8) & RANK_4;//move 2 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //promoting captures and pawn pushes: file1,file2,Promotion Type,"P"
        PAWN_MOVES = (WP >> 7) & BLACK_PIECES & RANK_8 & ~FILE_A;//pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("QP").append(index % 8 - 1)
                    .append(index % 8).append("RP").append(index % 8 - 1).append(index % 8)
                    .append("BP").append(index % 8 - 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & RANK_8 & ~FILE_H;//pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("QP").append(index % 8 + 1)
                    .append(index % 8).append("RP").append(index % 8 + 1).append(index % 8)
                    .append("BP").append(index % 8 + 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & RANK_8;//pawn promotion by move 1 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("QP").append(index % 8)
                    .append(index % 8).append("RP").append(index % 8).append(index % 8)
                    .append("BP").append(index % 8).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //en passant: file1,file2,"WE"
        possibility = (WP << 1) & BP & RANK_5 & ~FILE_A & EP; //en passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("WE");
        }
        possibility = (WP >> 1) & BP & RANK_5 & ~FILE_H & EP; //en passant left
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("WE");
        }

        return list.toString();
    }

    public static String possibleBP(long BP, long WP, long EP) {
        StringBuilder list = new StringBuilder();

        //basic captures and pawn pushes: rank1,file1,rank2,file2
        long PAWN_MOVES = (BP << 7) & WHITE_PIECES & ~RANK_1 & ~FILE_H;//capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & ~RANK_1 & ~FILE_A;//capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & ~RANK_1;//move 1 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 16) & EMPTY & (EMPTY << 8) & RANK_5;//move 2 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //promoting captures and pawn pushes: file1,file2,Promotion Type,"P"
        PAWN_MOVES = (BP << 7) & WHITE_PIECES & RANK_1 & ~FILE_H;//pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("qP").append(index % 8 + 1)
                    .append(index % 8).append("rP").append(index % 8 + 1).append(index % 8)
                    .append("bP").append(index % 8 + 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & RANK_1 & ~FILE_A;//pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("qP").append(index % 8 - 1)
                    .append(index % 8).append("rP").append(index % 8 - 1).append(index % 8)
                    .append("bP").append(index % 8 - 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & RANK_1;//pawn promotion by move 1 forward
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("qP").append(index % 8)
                    .append(index % 8).append("rP").append(index % 8).append(index % 8)
                    .append("bP").append(index % 8).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //en passant: file1,file2,"BE"
        possibility = (BP >> 1) & WP & RANK_4 & ~FILE_H & EP; //en passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("BE");
        }
        possibility = (BP << 1) & WP & RANK_4 & ~FILE_A & EP; //en passant left
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
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);

            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }

            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH & AVAILABLE_SQUARES;
            } else {
                possibility &= ~FILE_AB & AVAILABLE_SQUARES;
            }

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

        while (i != 0) { //loop through each bishop
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            while (j != 0) { //loop through each possibility for given bishop
                //moves and captures: rank1,file1,rank2,file2
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

        while (i != 0) { //loop through each rook
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            while (j != 0) { //loop through each possibility for given rook
                //moves and captures: rank1,file1,rank2,file2
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

        while (i != 0) { //loop through each queen
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = (diagonalAndAntiDiagonalMoves(iLocation) | horizontalAndVerticalMoves(iLocation)) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            while (j != 0) { //loop through each possibility for given queen
                //moves and captures: rank1,file1,rank2,file2
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

        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }

        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH & AVAILABLE_SQUARES;
        } else {
            possibility &= ~FILE_AB & AVAILABLE_SQUARES;
        }

        long j = possibility & -possibility;
        while (j != 0) {
            int index = Long.numberOfTrailingZeros(j);
            list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
            possibility &= ~j;
            j = possibility & -possibility;
        }

        return list.toString();
    }

    public static String possibleCW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, boolean CWK, boolean CWQ) {
        String list = "";
        long UNSAFE = unsafeForWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);

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

    public static String possibleCB(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, boolean CBK, boolean CBQ) {
        String list = "";
        long UNSAFE = unsafeForBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);

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

        //pawn
        unsafe = ((WP >>> 7) & ~FILE_A);//pawn capture right
        unsafe |= ((WP >>> 9) & ~FILE_H);//pawn capture left

        //knight
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

        //bishop/queen
        long QB = WQ | WB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //rook/queen
        long QR = WQ | WR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //king
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

        //pawn
        unsafe = ((BP << 7) & ~FILE_H);//pawn capture right
        unsafe |= ((BP << 9) & ~FILE_A);//pawn capture left

        //knight
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

        //bishop/queen
        long QB = BQ | BB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //rook/queen
        long QR = BQ | BR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //king
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

    public static String legalMoves (String moves, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove) {
        StringBuilder legalMoves = new StringBuilder();

        for (int i = 0; i < moves.length(); i += 4) {
            char move1 = moves.charAt(i);
            char move2 = moves.charAt(i + 1);
            char move3 = moves.charAt(i + 2);
            char move4 = moves.charAt(i + 3);

            long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'), WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                    WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'), WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                    WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'), WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                    BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'), BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                    BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'), BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                    BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'), BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                    EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
            WRt=Moves.makeMoveCastle(WRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
            BRt=Moves.makeMoveCastle(BRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
            if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && WhiteToMove) ||
                    ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && !WhiteToMove)) {
                legalMoves.append(move1).append(move2).append(move3).append(move4);
            }
        }
        return legalMoves.toString();
    }

    public static void drawBitboard(long bitBoard) {
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
