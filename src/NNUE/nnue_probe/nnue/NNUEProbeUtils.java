package NNUE.nnue_probe.nnue;

import engine.*;

public class NNUEProbeUtils {
    private static int[] SQUARE_MAPPING = new int[] {
            56, 57, 58, 59, 60, 61, 62, 63,
            48, 49, 50, 51, 52, 53, 54, 55,
            40, 41, 42, 43, 44, 45, 46, 47,
            32, 33, 34, 35, 36, 37, 38, 39,
            24, 25, 26, 27, 28, 29, 30, 31,
            16, 17, 18, 19, 20, 21, 22, 23,
            8, 9, 12, 11, 12, 13, 14, 15,
            0, 1, 2, 3, 4, 5, 6, 7,
    };



    /**
     * Evaluation subroutine suitable for chess engines.
     * -------------------------------------------------
     * Piece codes are
     *     wking=1, wqueen=2, wrook=3, wbishop= 4, wknight= 5, wpawn= 6,
     *     bking=7, bqueen=8, brook=9, bbishop=10, bknight=11, bpawn=12,
     * Squares are
     *     A1=0, B1=1 ... H8=63
     * Input format:
     *     piece[0] is white king, square[0] is its location
     *     piece[1] is black king, square[1] is its location
     *     ..
     *     piece[x], square[x] can be in any order
     *     ..
     *     piece[n+1] is set to 0 to represent end of array
     * Returns
     *   Score relative to side to move in approximate centi-pawns
     */
    public static final void fillInput(Input input, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK) {

        input.color = Quetzal.WhiteToMove ? 1 : 0;

        long bb_w_king 		= WK;
        long bb_b_king 		= BK;
        long bb_w_queens 	= WQ;
        long bb_b_queens 	= BQ;
        long bb_w_rooks 	= WR;
        long bb_b_rooks 	= BR;
        long bb_w_bishops 	= WB;
        long bb_b_bishops 	= BB;
        long bb_w_knights 	= WN;
        long bb_b_knights 	= BN;
        long bb_w_pawns 	= WP;
        long bb_b_pawns 	= BP;

        int index 			= 0;
        int square_type 	= 1;

        //White king
        input.pieces[index] 	= square_type;
        input.squares[index] 	= getSquareID(bb_w_king);
        index++;

        //Black king
        input.pieces[index] 	= square_type + 6;
        input.squares[index] 	= getSquareID(bb_b_king);
        index++;

        square_type++;

        //White queens
        while (bb_w_queens != 0) {
            input.pieces[index] 	= square_type;
            input.squares[index] 	= getSquareID(bb_w_queens);
            index++;
            bb_w_queens &= bb_w_queens - 1;
        }

        //Black queens
        while (bb_b_queens != 0) {
            input.pieces[index] 	= square_type + 6;
            input.squares[index] 	= getSquareID(bb_b_queens);
            index++;
            bb_b_queens &= bb_b_queens - 1;
        }

        square_type++;

        //White rooks
        while (bb_w_rooks != 0) {
            input.pieces[index] 	= square_type;
            input.squares[index] 	= getSquareID(bb_w_rooks);
            index++;
            bb_w_rooks &= bb_w_rooks - 1;
        }

        //Black rooks
        while (bb_b_rooks != 0) {
            input.pieces[index] 	= square_type + 6;
            input.squares[index] 	= getSquareID(bb_b_rooks);
            index++;
            bb_b_rooks &= bb_b_rooks - 1;
        }

        square_type++;

        //White bishops
        while (bb_w_bishops != 0) {
            input.pieces[index] 	= square_type;
            input.squares[index] 	= getSquareID(bb_w_bishops);
            index++;
            bb_w_bishops &= bb_w_bishops - 1;
        }

        //Black bishops
        while (bb_b_bishops != 0) {
            input.pieces[index] 	= square_type + 6;
            input.squares[index] 	= getSquareID(bb_b_bishops);
            index++;
            bb_b_bishops &= bb_b_bishops - 1;
        }

        square_type++;

        //White knights
        while (bb_w_knights != 0) {
            input.pieces[index] 	= square_type;
            input.squares[index] 	= getSquareID(bb_w_knights);
            index++;
            bb_w_knights &= bb_w_knights - 1;
        }

        //Black knights
        while (bb_b_knights != 0) {
            input.pieces[index] 	= square_type + 6;
            input.squares[index] 	= getSquareID(bb_b_knights);
            index++;
            bb_b_knights &= bb_b_knights - 1;
        }

        square_type++;

        //White pawns
        while (bb_w_pawns != 0) {
            input.pieces[index] 	= square_type;
            input.squares[index] 	= getSquareID(bb_w_pawns);
            index++;
            bb_w_pawns &= bb_w_pawns - 1;
        }

        //Black pawns
        while (bb_b_pawns != 0) {
            input.pieces[index] 	= square_type + 6;
            input.squares[index] 	= getSquareID(bb_b_pawns);
            index++;
            bb_b_pawns &= bb_b_pawns - 1;
        }

        input.pieces[index] 	= 0;
        input.squares[index] 	= 0;
    }


    private static int getSquareID(long bitboard) {

        int result =  Long.numberOfTrailingZeros(bitboard);

        result = SQUARE_MAPPING[result];

        return result;
    }


    public static class Input {

        public int color;
        public int[] pieces = new int[33];
        public int[] squares = new int[33];
    }

    public static String boardToFen(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove) {
        StringBuilder fen = new StringBuilder();
        int empty = 0;
        int EPSquare = -1;
        for (int i = 0; i < 64; i++) {
            if (((1L << i) & WP) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("P");
            } else if (((1L << i) & WN) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("N");
            } else if (((1L << i) & WB) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("B");
            } else if (((1L << i) & WR) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("R");
            } else if (((1L << i) & WQ) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("Q");
            } else if (((1L << i) & WK) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("K");
            } else if (((1L << i) & BP) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("p");
            } else if (((1L << i) & BN) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("n");
            } else if (((1L << i) & BB) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("b");
            } else if (((1L << i) & BR) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("r");
            } else if (((1L << i) & BQ) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("q");
            } else if (((1L << i) & BK) != 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append("k");
            } else if (((1L << i) & EP) != 0) {
                EPSquare = i;
            } else {
                empty++;
            }
            if ((i + 1) % 8 == 0) {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                if (i != 63) {
                    fen.append("/");
                }
            }
        }
        fen.append(" ");
        if (WhiteToMove) {
            fen.append("w");
        } else {
            fen.append("b");
        }
        fen.append(" ");
        if (CWK || CWQ || CBK || CBQ) {
            if (CWK) {
                fen.append("K");
            }
            if (CWQ) {
                fen.append("Q");
            }
            if (CBK) {
                fen.append("k");
            }
            if (CBQ) {
                fen.append("q");
            }
        } else {
            fen.append("-");
        }
        fen.append(" ");
        if (EP != 0) {
            fen.append((char) ('a' + (EPSquare % 8)));
            fen.append((char) ('8' - (EPSquare / 8)));
        } else {
            fen.append("-");
        }
        fen.append(" ");
        fen.append("0");
        fen.append(" ");
        fen.append("1");

        return fen.toString();
    }
}