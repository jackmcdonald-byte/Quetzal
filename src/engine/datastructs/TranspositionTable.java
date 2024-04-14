package engine.datastructs;

import engine.datastructs.ChessBoard;

public class TranspositionTable {

    /*
     Because of alpha-beta pruning, any position we store may be
     the exact evaluation or the current alpha/beta values at the time.
     To get around this issue, we use a flag to determine
     whether the position was an upper or lower bound.
     */
    public static final int EXACT = 0;
    public static final int LOWER = 1;
    public static final int UPPER = 2;

    private final int size;
    private final HashData[] hashTable;
    private final ChessBoard board;

    /**
     * Constructor
     *
     * @param board ChessBoard class containing all important values to the chess game (e.g. check, castling rights, piece position)
     * @param size  Size of the TranspositionTable
     */
    public TranspositionTable(ChessBoard board, int size) {
        this.board = board;
        this.hashTable = new HashData[size];
        this.size = size;
    }

    /**
     * Stores an evaluation into the table
     *
     * @param depth Depth of the search
     * @param flag  Indicator of bounds (see above for more details)
     * @param eval  Evaluation of position
     * @param move  Move made before storing
     */
    public void storeEvaluation(int depth, int flag, int eval, String move) {
        HashData data = new HashData(board.getZobristKey(), depth, flag, eval, move);
        hashTable[getIndex()] = data;
    }

    /**
     * Looks up a value from the transposition table
     *
     * @param depth Current search depth
     * @param alpha Current alpha in search
     * @param beta  Current beta in search
     * @return Eval of position or Integer.MIN_VALUE
     */
    public int lookupEvaluation(int depth, int alpha, int beta) {
        HashData data = hashTable[getIndex()];

        // Ignore if the depth of the position is greater than the one already searched to
        if (data == null || data.depth <= depth || data.hash != board.getZobristKey()) return Integer.MIN_VALUE;

        if (data.flag == EXACT)
            return data.bestEval;
        if (data.flag == UPPER && data.bestEval <= alpha)
            return data.bestEval;
        if (data.flag == LOWER && data.bestEval >= beta)
            return data.bestEval;

        return Integer.MIN_VALUE;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            hashTable[i] = null;
        }
    }

    /**
     * Hashing function for table
     *
     * @return The index of the current board position in the table
     */
    private int getIndex() {
        return (int) (board.getZobristKey() % size);
    }

    /**
     * Get the best move from the current position
     *
     * @return Best move from the current position
     */
    public String getCurrentMove() {
        HashData data = hashTable[getIndex()];
        return data != null ? data.bestMove : null;
    }

    /**
     * Get the best evaluation from the current position
     *
     * @return Best evaluation from the current position
     */
    public int getCurrentEval() {
        return hashTable[getIndex()].bestEval;
    }

    /**
     * HashData - Used to store a zobrist key and its relevant information in the transposition table
     */
    class HashData {

        public long hash;
        public int depth;
        public int flag;
        public int bestEval;
        public String bestMove;

        /**
         * Constructor
         *
         * @param hash     Zobrist key of the position
         * @param depth    Depth of the search
         * @param flag     Indicator of bounds (see above for more details)
         * @param bestEval Best evaluation of the position
         * @param bestMove Best move from the position
         */
        HashData(long hash, int depth, int flag, int bestEval, String bestMove) {
            this.hash = hash;
            this.depth = depth;
            this.flag = flag;
            this.bestEval = bestEval;
            this.bestMove = bestMove;
        }
    }

}