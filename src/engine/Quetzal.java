package engine;

import NNUE.nnue_probe.nnue.NNUEJNIBridge;
import engine.datastructs.ChessBoard;

public class Quetzal {
    public static long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L,
            BP = 0L, BN = 0L, BB = 0L, BR = 0L, BQ = 0L, BK = 0L, EP = 0L;
    public static boolean CWK = true, CWQ = true, CBK = true, CBQ = true, WhiteToMove = true; //True=castle is possible
    public static ChessBoard globalBoard = new ChessBoard();
    public static int MATE_SCORE = 32000, MAX_SEARCH_DEPTH = 200, NULL_INT = Integer.MIN_VALUE;
    static int searchDepth = 5, moveCounter; //Use moveCounter to test efficiency

    public static void main(String[] args) {
        NNUEJNIBridge.setup();
        Zobrist.zobristFillArray();
        UCI.uciCommunication();
    }
}
