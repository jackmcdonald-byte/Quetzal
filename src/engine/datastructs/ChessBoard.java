package engine.datastructs;

import engine.Moves;
import engine.Zobrist;

import java.util.Stack;

public class ChessBoard {
    private final Stack<Long> boardHistory = new Stack<>();
    private long WP, WN, WB, WR, WQ, WK;
    private long BP, BN, BB, BR, BQ, BK;
    private long EP;
    private boolean CWK, CWQ, CBK, CBQ, WhiteToMove;
    private long zobristKey;
    private ChessBoard lastBoard = null;
    private int halfmoveClock = 0;


    /**
     * Constructor
     */
    public ChessBoard() {
        WP = 0L;
        WN = 0L;
        WB = 0L;
        WR = 0L;
        WQ = 0L;
        WK = 0L;
        BP = 0L;
        BN = 0L;
        BB = 0L;
        BR = 0L;
        BQ = 0L;
        BK = 0L;
        EP = 0L;
        CWK = false;
        CWQ = false;
        CBK = false;
        CBQ = false;
        WhiteToMove = false;
        zobristKey = 0L;
    }

    /**
     * Constructor
     *
     * @param WP          White Pawns
     * @param WN          White Knights
     * @param WB          White Bishops
     * @param WR          White Rooks
     * @param WQ          White Queen
     * @param WK          White King
     * @param BP          Black Pawns
     * @param BN          Black Knights
     * @param BB          Black Bishops
     * @param BR          Black Rooks
     * @param BQ          Black Queen
     * @param BK          Black King
     * @param EP          En Passant
     * @param CWK         White can castle kingside
     * @param CWQ         White can castle queenside
     * @param CBK         Black can castle kingside
     * @param CBQ         Black can castle queenside
     * @param WhiteToMove True if white to move
     */
    public ChessBoard(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR,
                      long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ,
                      boolean WhiteToMove) {
        this.WP = WP;
        this.WN = WN;
        this.WB = WB;
        this.WR = WR;
        this.WQ = WQ;
        this.WK = WK;
        this.BP = BP;
        this.BN = BN;
        this.BB = BB;
        this.BR = BR;
        this.BQ = BQ;
        this.BK = BK;
        this.EP = EP;
        this.CWK = CWK;
        this.CWQ = CWQ;
        this.CBK = CBK;
        this.CBQ = CBQ;
        this.WhiteToMove = WhiteToMove;
        this.zobristKey = Zobrist.getZobristHash(this);
    }

    /**
     * Constructor
     *
     * @param WP          White Pawns
     * @param WN          White Knights
     * @param WB          White Bishops
     * @param WR          White Rooks
     * @param WQ          White Queen
     * @param WK          White King
     * @param BP          Black Pawns
     * @param BN          Black Knights
     * @param BB          Black Bishops
     * @param BR          Black Rooks
     * @param BQ          Black Queen
     * @param BK          Black King
     * @param EP          En Passant
     * @param CWK         White can castle kingside
     * @param CWQ         White can castle queenside
     * @param CBK         Black can castle kingside
     * @param CBQ         Black can castle queenside
     * @param WhiteToMove True if white to move
     * @param lastBoard   ChessBoard of the last move
     */
    public ChessBoard(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR,
                      long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ,
                      boolean WhiteToMove, ChessBoard lastBoard) {
        this.WP = WP;
        this.WN = WN;
        this.WB = WB;
        this.WR = WR;
        this.WQ = WQ;
        this.WK = WK;
        this.BP = BP;
        this.BN = BN;
        this.BB = BB;
        this.BR = BR;
        this.BQ = BQ;
        this.BK = BK;
        this.EP = EP;
        this.CWK = CWK;
        this.CWQ = CWQ;
        this.CBK = CBK;
        this.CBQ = CBQ;
        this.WhiteToMove = WhiteToMove;
        this.zobristKey = Zobrist.getZobristHash(this);
        this.lastBoard = lastBoard;
    }

    /**
     * Constructor
     *
     * @param WP           White Pawns
     * @param WN           White Knights
     * @param WB           White Bishops
     * @param WR           White Rooks
     * @param WQ           White Queen
     * @param WK           White King
     * @param BP           Black Pawns
     * @param BN           Black Knights
     * @param BB           Black Bishops
     * @param BR           Black Rooks
     * @param BQ           Black Queen
     * @param BK           Black King
     * @param EP           En Passant
     * @param CWK          White can castle kingside
     * @param CWQ          White can castle queenside
     * @param CBK          Black can castle kingside
     * @param CBQ          Black can castle queenside
     * @param WhiteToMove  True if white to move
     * @param lastBoard    ChessBoard of the last move
     * @param boardHistory Stack of Zobrist keys
     */
    public ChessBoard(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR,
                      long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ,
                      boolean WhiteToMove, ChessBoard lastBoard, Stack<Long> boardHistory) {
        this.WP = WP;
        this.WN = WN;
        this.WB = WB;
        this.WR = WR;
        this.WQ = WQ;
        this.WK = WK;
        this.BP = BP;
        this.BN = BN;
        this.BB = BB;
        this.BR = BR;
        this.BQ = BQ;
        this.BK = BK;
        this.EP = EP;
        this.CWK = CWK;
        this.CWQ = CWQ;
        this.CBK = CBK;
        this.CBQ = CBQ;
        this.WhiteToMove = WhiteToMove;
        this.zobristKey = Zobrist.getZobristHash(this);
        this.lastBoard = lastBoard;
        this.boardHistory.addAll(boardHistory);
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWP() {
        return WP;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWN() {
        return WN;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWB() {
        return WB;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWR() {
        return WR;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWQ() {
        return WQ;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getWK() {
        return WK;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBP() {
        return BP;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBN() {
        return BN;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBB() {
        return BB;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBR() {
        return BR;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBQ() {
        return BQ;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the pieces
     */
    public long getBK() {
        return BK;
    }

    /**
     * Getter
     *
     * @return Bitboard representation of the en passant square
     */
    public long getEP() {
        return EP;
    }

    /**
     * Getter
     *
     * @return True if white can castle kingside, false otherwise
     */
    public boolean getCWK() {
        return CWK;
    }

    /**
     * Getter
     *
     * @return True if white can castle queenside, false otherwise
     */
    public boolean getCWQ() {
        return CWQ;
    }

    /**
     * Getter
     *
     * @return True if black can castle kingside, false otherwise
     */
    public boolean getCBK() {
        return CBK;
    }

    /**
     * Getter
     *
     * @return True if black can castle queenside, false otherwise
     */
    public boolean getCBQ() {
        return CBQ;
    }

    /**
     * Getter
     *
     * @return True if white to move, false otherwise
     */
    public boolean getWhiteToMove() {
        return WhiteToMove;
    }

    /**
     * Getter
     *
     * @return Zobrist key of the position
     */
    public long getZobristKey() {
        return zobristKey;
    }

    /**
     * Getter
     *
     * @return ChessBoard of the last move
     */
    public ChessBoard getLastBoard() {
        return lastBoard;
    }

    /**
     * Getter
     *
     * @return Stack of Zobrist keys for previous positions
     */
    public Stack<Long> getBoardHistory() {
        return boardHistory;
    }

    /**
     * Make a move on the board
     *
     * @param moves    String of moves
     * @param index    Index of the move
     * @param inSearch True if in search, false otherwise
     */
    public void makeMove(String moves, int index, boolean inSearch) {
        char move1 = moves.charAt(index);
        char move2 = moves.charAt(index + 1);
        char move3 = moves.charAt(index + 2);
        char move4 = moves.charAt(index + 3);

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
                BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
        WRt = Moves.makeMoveCastle(WRt, WK | BK,
                String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
        BRt = Moves.makeMoveCastle(BRt, WK | BK,
                String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
        boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;

        if (Character.isDigit(moves.charAt(3))) { //'Regular' move
            int start = (Character.getNumericValue(moves.charAt(0)) * 8) + (Character.getNumericValue(moves.charAt(1)));
            if (((1L << start) & WK) != 0) {
                CWKt = false;
                CWQt = false;
            } else if (((1L << start) & BK) != 0) {
                CBKt = false;
                CBQt = false;
            } else if (((1L << start) & WR & (1L << 63)) != 0) {
                CWKt = false;
            } else if (((1L << start) & WR & (1L << 56)) != 0) {
                CWQt = false;
            } else if (((1L << start) & BR & (1L << 7)) != 0) {
                CBKt = false;
            } else if (((1L << start) & BR & 1L) != 0) {
                CBQt = false;
            }
        }

        lastBoard = new ChessBoard(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK,
                EP, CWK, CWQ, CBK, CBQ, WhiteToMove, lastBoard, boardHistory);

        WP = WPt;
        WN = WNt;
        WB = WBt;
        WR = WRt;
        WQ = WQt;
        WK = WKt;
        BP = BPt;
        BN = BNt;
        BB = BBt;
        BR = BRt;
        BQ = BQt;
        BK = BKt;
        EP = EPt;
        CWK = CWKt;
        CWQ = CWQt;
        CBK = CBKt;
        CBQ = CBQt;
        WhiteToMove = !WhiteToMove;
        zobristKey = Zobrist.getZobristHash(this);

        //draw functionality
        //TODO add 3 fold repetition recognition
        if (!inSearch) {
            boardHistory.push(zobristKey);
        }
    }

    /**
     * Undo a move on the board
     *
     * @param inSearch True if in search, false otherwise
     */
    public void undoMove(boolean inSearch) {
        this.cloneBoard(lastBoard);

        if (!inSearch && !boardHistory.isEmpty()) {
            boardHistory.pop();
        }
    }

    /**
     * Clone the board
     *
     * @param board ChessBoard to clone
     */
    public void cloneBoard(ChessBoard board) {
        WP = board.getWP();
        WN = board.getWN();
        WB = board.getWB();
        WR = board.getWR();
        WQ = board.getWQ();
        WK = board.getWK();
        BP = board.getBP();
        BN = board.getBN();
        BB = board.getBB();
        BR = board.getBR();
        BQ = board.getBQ();
        BK = board.getBK();
        EP = board.getEP();
        CWK = board.getCWK();
        CWQ = board.getCWQ();
        CBK = board.getCBK();
        CBQ = board.getCBQ();
        WhiteToMove = board.getWhiteToMove();
        zobristKey = board.getZobristKey();
        lastBoard = board.getLastBoard();
    }

    /**
     * Check if the position is a repeat
     *
     * @param zobristKey Zobrist key of the position
     * @return True if the position is a repeat, false otherwise
     */
    public boolean isRepeatPosition(long zobristKey) {
        return boardHistory.contains(zobristKey);
    }
}
