package engine.utils;

public class BitBoardConstants {
    public static final long FILE_A = 72340172838076673L;
    public static final long FILE_H = -9187201950435737472L;
    public static final long FILE_AB = 217020518514230019L;
    public static final long FILE_GH = -4557430888798830400L;
    public static final long RANK_1 = -72057594037927936L;
    public static final long RANK_4 = 1095216660480L;
    public static final long RANK_5 = 4278190080L;
    public static final long RANK_8 = 255L;
    public static final long CENTRE = 103481868288L; //2x2 square
    public static final long EXTENDED_CENTRE = 66229406269440L; //4x4 square
    public static final long KING_SIDE = -1085102592571150096L;
    public static final long QUEEN_SIDE = 1085102592571150095L;
    public static final long KING_SPAN = 460039L; //King's span on g2
    public static final long KNIGHT_SPAN = 43234889994L; //Knight's span on b7
    public static final long[] CASTLE_ROOKS = {63, 56, 7, 0};
    public static final long[] RankMasks8 = {0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L,
            0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L}; //From rank1 to rank8
    public static final long[] FileMasks8 = {0x101010101010101L, 0x202020202020202L, 0x404040404040404L,
            0x808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L,
            0x8080808080808080L}; //From fileA to FileH
    public static final long[] DiagonalMasks8 = {0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L,
            0x10204081020L, 0x1020408102040L, 0x102040810204080L, 0x204081020408000L,
            0x408102040800000L, 0x810204080000000L, 0x1020408000000000L, 0x2040800000000000L,
            0x4080000000000000L, 0x8000000000000000L}; //From top left to bottom right
    public static final long[] AntiDiagonalMasks8 = {0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L,
            0x804020100804L, 0x80402010080402L, 0x8040201008040201L, 0x4020100804020100L,
            0x2010080402010000L, 0x1008040201000000L, 0x804020100000000L, 0x402010000000000L,
            0x201000000000000L, 0x100000000000000L}; //From top right to bottom left
}
