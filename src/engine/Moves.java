package engine;

public class Moves {
    static final long FILE_A = 72340172838076673L;
    static final long FILE_H = -9187201950435737472L;
    static final long FILE_AB = 217020518514230019L;
    static final long FILE_GH = -4557430888798830400L;
    static final long RANK_1 = -72057594037927936L;
    static final long RANK_4 = 1095216660480L;
    static final long RANK_5 = 4278190080L;
    static final long RANK_8 = 255L;
    static final long CENTRE = 103481868288L;
    static final long EXTENDED_CENTRE = 66229406269440L;
    static final long KING_SIDE = -1085102592571150096L;
    static final long QUEEN_SIDE = 1085102592571150095L;
    static final long KING_B7 = 460039L;
    static final long KNIGHT_C6 = 43234889994L;
    static long UNCAPTURABLE_SQUARES; //Represents where white cannot move
    static long BLACK_PIECES; //Doesn't represent black king
    static long EMPTY;
    static long[] RankMasks8 = {0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L,
            0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L}; //from rank1 to rank8
    static long[] FileMasks8 ={0x101010101010101L, 0x202020202020202L, 0x404040404040404L,
            0x808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L,
            0x8080808080808080L}; //from fileA to FileH

    public static String possibleMovesWhite(String history, long WP, long WN, long WB, long WR, long WQ, long WK,
                                            long BP, long BN, long BB, long BR, long BQ, long BK) {
        UNCAPTURABLE_SQUARES = ~(WP | WN | WB | WR | WQ | WK | BK);//added BK to avoid illegal capture
        BLACK_PIECES = BP | BN | BB | BR | BQ;//omitted BK to avoid illegal capture
        EMPTY = ~(WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK);

        return possiblePW(history, WP, BP) + 
                possibleNW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK) +
                possibleBW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK) +
                possibleRW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK) +
                possibleQW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK) +
                possibleKW(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
    }

    //TODO return later and attempt to optimize by trying different loops
    //TODO fix board generation to avoid rewriting this i.e, only flip graphical representation
    private static String possiblePW(String history, long WP, long BP) {
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

        //en passant: file1,file2," ","E"
        if (history.length() >= 4)
        {
            //check if en passant is possible; i.e, 1636 is a double pawn move and 56 E is en passant
            if ((history.charAt(history.length() - 1) == history.charAt(history.length() - 3)) &&
                    Math.abs(history.charAt(history.length() - 2) - history.charAt(history.length() - 4)) == 2) {
                int eFile = history.charAt(history.length() - 1) - '0';

                //en passant right
                possibility = (WP << 1) & BP & RANK_5 & ~FILE_A & FileMasks8[eFile];//shows piece to remove, not the destination
                if (possibility != 0) {
                    int index = Long.numberOfTrailingZeros(possibility);
                    list.append(index % 8 - 1).append(index % 8).append(" E");
                }

                //en passant left
                possibility = (WP >> 1) & BP & RANK_5 & ~FILE_H & FileMasks8[eFile];//shows piece to remove, not the destination
                if (possibility != 0) {
                    int index = Long.numberOfTrailingZeros(possibility);
                    list.append(index % 8 + 1).append(index % 8).append(" E");
                }
            }
        }
        return list.toString();
    }

    private static String possibleNW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        return null;
    }

    private static String possibleBW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        return null;
    }

    private static String possibleRW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        return null;
    }

    private static String possibleQW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        return null;
    }

    private static String possibleKW(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {
        return null;
    }
}
