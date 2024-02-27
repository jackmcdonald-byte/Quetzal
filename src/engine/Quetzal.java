package engine;

import NNUE.nnue_probe.nnue.NNUEJNIBridge;

public class Quetzal {
    public static long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BN = 0L, BB = 0L, BR = 0L, BQ = 0L, BK = 0L, EP = 0L;
    public static boolean CWK = true, CWQ = true, CBK = true, CBQ = true, WhiteToMove = true;//true=castle is possible
    static long UniversalWP = 0L, UniversalWN = 0L, UniversalWB = 0L, UniversalWR = 0L,
            UniversalWQ = 0L, UniversalWK = 0L, UniversalBP = 0L, UniversalBN = 0L,
            UniversalBB = 0L, UniversalBR = 0L, UniversalBQ = 0L, UniversalBK = 0L,
            UniversalEP = 0L;
    static int searchDepth = 5, moveCounter; //use moveCounter to test efficiency
    static int MATE_SCORE = 5000, NULL_INT = Integer.MIN_VALUE;

    public static void main(String[] args) {
//        Zobrist.random64();
//        Zobrist.testDistribution();
        NNUEJNIBridge.setup();
        Zobrist.zobristFillArray();
        UCI.uciCommunication();
    }
}
