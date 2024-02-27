package engine;

import NNUE.nnue_probe.nnue.NNUEProbeUtils;

public class PVSAlgorithm {
    public static String[] bestMove = new String[20];
    public static int nullWindowSearch(int beta, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove, int depth) {
        int score = Integer.MIN_VALUE;
        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input(); //try passing inputs if problematic
        NNUEProbeUtils.fillInput(input, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
        //alpha == beta - 1
        //this is either a cut- or all-node
        if (depth == Quetzal.searchDepth) {
            score = Evaluation.evaluate(input);
            return score;
        }

        String moves = Moves.generateMoves(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ, WhiteToMove);

        if (moves.isEmpty()) {
            return WhiteToMove ? Quetzal.MATE_SCORE : -Quetzal.MATE_SCORE;
        }

        //sortMoves();
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
            boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;

            if (Character.isDigit(moves.charAt(i + 3))) {//'regular' move
                int start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
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

            //may be unnecessary
            if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && WhiteToMove) ||
                    ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && !WhiteToMove)) {
                score = -nullWindowSearch(1 - beta, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
            }
            if (score >= beta) {
                return score;//fail-hard beta-cutoff
            }
        }
        return beta - 1;//fail-hard, return alpha
    }

    public static int getFirstLegalMove(String moves, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove) {
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
                return i;
            }
        }
        return -1;
    }

    public static int principleVariationSearch(int alpha, int beta, long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove, int depth) {
        int bestScore;
        int bestMoveIndex = -1;
        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        NNUEProbeUtils.fillInput(input, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
        if (depth == Quetzal.searchDepth) {
            bestScore = Evaluation.evaluate(input);
            return bestScore;
        }

        String moves = Moves.generateMoves(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ, WhiteToMove);

        if (moves.isEmpty()) {
            bestMove[depth] = "checkmate";
            return WhiteToMove ? Quetzal.MATE_SCORE : -Quetzal.MATE_SCORE;
        }

        bestMove[depth] = moves.substring(0, 4);

        char move1 = moves.charAt(0);
        char move2 = moves.charAt(1);
        char move3 = moves.charAt(2);
        char move4 = moves.charAt(3);

        long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'), WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'), WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'), WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'), BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'), BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'), BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
        WRt=Moves.makeMoveCastle(WRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
        BRt=Moves.makeMoveCastle(BRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
        boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;

        if (Character.isDigit(moves.charAt(3))) {//'regular' move
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

        bestScore = -principleVariationSearch(-beta, -alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
        Quetzal.moveCounter++;

        if (Math.abs(bestScore) > Quetzal.MATE_SCORE) {
            return bestScore;
        }
        if (bestScore > alpha) {
            if (bestScore >= beta) {
                //This is a refutation move
                //It is not a PV move
                //However, it will usually cause a cutoff so it can
                //be considered a best move if no other move is found
                return bestScore;
            }
            alpha = bestScore;
            int l = 0; //for debugging
        }
        bestMoveIndex = 0;
        for (int i = 4; i < moves.length(); i += 4) {
            int score;
            Quetzal.moveCounter++;

            //legal, non-castle move
            char moveA = moves.charAt(i);
            char moveB = moves.charAt(i + 1);
            char moveC = moves.charAt(i + 2);
            char moveD = moves.charAt(i + 3);

            WPt = Moves.makeMove(WP, moveA, moveB, moveC, moveD, 'P');
            WNt = Moves.makeMove(WN, moveA, moveB, moveC, moveD, 'N');
            WBt = Moves.makeMove(WB, moveA, moveB, moveC, moveD, 'B');
            WRt = Moves.makeMove(WR, moveA, moveB, moveC, moveD, 'R');
            WQt = Moves.makeMove(WQ, moveA, moveB, moveC, moveD, 'Q');
            WKt = Moves.makeMove(WK, moveA, moveB, moveC, moveD, 'K');
            BPt = Moves.makeMove(BP, moveA, moveB, moveC, moveD, 'p');
            BNt = Moves.makeMove(BN, moveA, moveB, moveC, moveD, 'n');
            BBt = Moves.makeMove(BB, moveA, moveB, moveC, moveD, 'b');
            BRt = Moves.makeMove(BR, moveA, moveB, moveC, moveD, 'r');
            BQt = Moves.makeMove(BQ, moveA, moveB, moveC, moveD, 'q');
            BKt = Moves.makeMove(BK, moveA, moveB, moveC, moveD, 'k');
            EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{moveA, moveB, moveC, moveD}));
            WRt=Moves.makeMoveCastle(WRt, WK|BK, String.valueOf(new char[]{moveA, moveB, moveC, moveD}), 'R');
            BRt=Moves.makeMoveCastle(BRt, WK|BK, String.valueOf(new char[]{moveA, moveB, moveC, moveD}), 'r');

            if (Character.isDigit(moves.charAt(i + 3))) {//'regular' move
                int start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
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

            score = -nullWindowSearch(-alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
            if ((score > alpha) && (score < beta)) {
                //research with window [alpha;beta]
                score = -principleVariationSearch(-beta, -alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
                if (score > alpha) {
                    bestMoveIndex = i;
                    bestMove[depth] = moves.substring(i, i + 4);
                    alpha = score;
                }
            }

            if ((score != Quetzal.NULL_INT) && (score > bestScore)) {
                if (score >= beta) {
                    bestMoveIndex = i;
                    bestMove[depth] = moves.substring(i, i + 4);
                    return score;
                }
                bestScore = score;
                if (Math.abs(bestScore) == Quetzal.MATE_SCORE) { //may remove later
                    return bestScore;
                }
            }
        }
        return bestScore;
    }
}
